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
package org.jboss.tools.quarkus.lsp4e.internal.nature.qute;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.ProjectScope;
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
public class QuteNature implements IProjectNature {

	private IProject project;

	private Preferences getProjectPreferences(IProject project) {
		IPreferencesService preferencesService = Platform.getPreferencesService();
		Preferences scopeNode = preferencesService.getRootNode().node(ProjectScope.SCOPE);
		return scopeNode.node(project.getName());
	}
	
	private void configureContentTypes() {
		Preferences prefs = getProjectPreferences(getProject());
		Preferences node = prefs.node("org.eclipse.core.runtime").node("content-types");
		node.putBoolean("enabled", true);
		node.node(QuarkusLSPPlugin.QUTE_CONTENT_TYPE_HTML).put("file-extensions", "htm,html");
		node.node(QuarkusLSPPlugin.QUTE_CONTENT_TYPE_YAML).put("file-extensions", "yml,yaml");
		node.node(QuarkusLSPPlugin.QUTE_CONTENT_TYPE_JSON).put("file-extensions", "json");
		node.node(QuarkusLSPPlugin.QUTE_CONTENT_TYPE_TXT).put("file-extensions", "txt,text");
	}

	@Override
	public void configure() throws CoreException {
	}

	@Override
	public void deconfigure() throws CoreException {
	}

	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;
		configureContentTypes();
	}

}
