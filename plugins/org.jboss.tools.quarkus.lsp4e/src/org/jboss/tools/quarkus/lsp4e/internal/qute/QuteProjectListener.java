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

import org.eclipse.core.internal.events.ILifecycleListener;
import org.eclipse.core.internal.events.LifecycleEvent;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.quarkus.lsp4e.QuarkusLSPPlugin;
import org.osgi.service.prefs.Preferences;

/**
 * @author Red Hat Developers
 *
 */
public class QuteProjectListener implements ILifecycleListener {
	private static final String QUTE_ENGINE_CLASS_NAME = "io.quarkus.qute.Engine";

	public QuteProjectListener() {
		for(IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			if (project.isAccessible()) {
				configureProject(project);
			}
		}
		((Workspace) ResourcesPlugin.getWorkspace()).addLifecycleListener(this);
	}
	
	private void enableNature(IProject project) throws CoreException {
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = QuarkusLSPPlugin.QUTE_NATURE_ID;
		description.setNatureIds(newNatures);
		project.setDescription(description, null);
	}
	

	/**
	 * @param project
	 */
	private void configureProject(IProject project) {
		try {
			IJavaProject javaProject = JavaCore.create(project);
			if (javaProject != null && javaProject.findType(QUTE_ENGINE_CLASS_NAME) != null) {
				if (project.getNature(QuarkusLSPPlugin.QUTE_NATURE_ID) == null) {
					enableNature(project);
				}
				
			}
		} catch (CoreException e) {
			QuarkusLSPPlugin.logException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void handleEvent(LifecycleEvent event) throws CoreException {
		if ((event.kind & LifecycleEvent.PRE_PROJECT_OPEN) == LifecycleEvent.PRE_PROJECT_OPEN && event.resource instanceof IProject) {
			configureProject((IProject) event.resource);
		}
	}

}
