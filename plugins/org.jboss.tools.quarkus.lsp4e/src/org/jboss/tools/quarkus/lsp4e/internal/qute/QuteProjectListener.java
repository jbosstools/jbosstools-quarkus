/*******************************************************************************
 * Copyright (c) 2022 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.quarkus.lsp4e.internal.qute;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.internal.events.ILifecycleListener;
import org.eclipse.core.internal.events.LifecycleEvent;
import org.eclipse.core.internal.events.ResourceChangeEvent;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.jboss.tools.quarkus.lsp4e.QuarkusLSPPlugin;

/**
 * @author Red Hat Developers
 *
 */
public class QuteProjectListener implements IResourceChangeListener, ILifecycleListener {
	private static final String QUTE_ENGINE_CLASS_NAME = "io.quarkus.qute.Engine";
	
	public QuteProjectListener() {
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			if (project.isAccessible()) {
				configureProject(project);
			}
		}
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.PRE_CLOSE
				| IResourceChangeEvent.PRE_DELETE | IResourceChangeEvent.POST_CHANGE | IResourceChangeEvent.POST_BUILD);
		((Workspace) ResourcesPlugin.getWorkspace()).addLifecycleListener(this);
	}
	
	private void enableNature(IProject project) throws CoreException {
		new WorkspaceJob("Enabling Qute for project " + project.getName()) {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				IProjectDescription description = project.getDescription();
				String[] natures = description.getNatureIds();
				String[] newNatures = new String[natures.length + 1];
				System.arraycopy(natures, 0, newNatures, 0, natures.length);
				newNatures[natures.length] = QuarkusLSPPlugin.QUTE_NATURE_ID;
				description.setNatureIds(newNatures);
				project.setDescription(description, monitor);
				return Status.OK_STATUS;
			}
		}.schedule();
	}
	
	private void disableNature(IProject project) throws CoreException {
		new WorkspaceJob("Disabling Qute for project " + project.getName()) {
			
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				IProjectDescription description = project.getDescription();
				String[] natures = description.getNatureIds();
				String[] newNatures = new String[natures.length - 1];
				int newIndex = 0;
				for(int oldIndex = 0; oldIndex < natures.length; ++oldIndex) {
					if (!QuarkusLSPPlugin.QUTE_NATURE_ID.equals(natures[oldIndex])) {
						newNatures[newIndex++] = natures[oldIndex];
					}
				}
				description.setNatureIds(newNatures);
				project.setDescription(description, null);
				return Status.OK_STATUS;
			}
		}.schedule();
	}

	/**
	 * @param project
	 */
	public void configureProject(IProject project) {
		try {
			IJavaProject javaProject = project.isOpen() && project.hasNature(JavaCore.NATURE_ID)
					? (IJavaProject) project.getNature(JavaCore.NATURE_ID)
					: null;
			if (javaProject != null) {
				IType engineType = javaProject.findType(QUTE_ENGINE_CLASS_NAME);
				IProjectNature nature = project.getNature(QuarkusLSPPlugin.QUTE_NATURE_ID);
				if (engineType != null && nature == null) {
					enableNature(project);
				} else if (engineType == null && nature != null) {
					disableNature(project);
				}
			}
		} catch (CoreException e) {
			QuarkusLSPPlugin.logException(e.getLocalizedMessage(), e);
		}
	}
	
	private static class Visitor implements IResourceDeltaVisitor {
		
		private Set<IProject> projects = new HashSet<>();

		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			if ((".project".equals(delta.getResource().getName()) || ".classpath".equals(delta.getResource().getName())) && delta.getResource() instanceof IFile && delta.getResource().getParent() instanceof IProject) {
				projects.add(delta.getResource().getProject());
				return false;
			}
			return true;
		}
		
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.POST_CHANGE || event.getType() == IResourceChangeEvent.POST_BUILD) {
			Visitor visitor = new Visitor();
			for(IResourceDelta d : event.getDelta().getAffectedChildren()) {
				try {
					d.accept(visitor, false);
				} catch (CoreException e) {
					QuarkusLSPPlugin.logException(e.getLocalizedMessage(), e);
				}
			}
			for(IProject project : visitor.projects) {
				configureProject(project);
			}
		}
	}
	
	@Override
	public void handleEvent(LifecycleEvent event) throws CoreException {
		if ((event.kind & LifecycleEvent.PRE_REFRESH) == LifecycleEvent.PRE_REFRESH && event.resource instanceof IProject) {
			configureProject((IProject) event.resource);
		}
	}
}
