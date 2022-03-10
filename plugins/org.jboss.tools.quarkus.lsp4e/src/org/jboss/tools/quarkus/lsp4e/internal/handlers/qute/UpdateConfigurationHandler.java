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
package org.jboss.tools.quarkus.lsp4e.internal.handlers.qute;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.lsp4e.command.LSPCommandHandler;
import org.eclipse.lsp4j.Command;
import org.jboss.tools.quarkus.lsp4e.internal.qute.QuteUtils;

/**
 * @author Red Hat Developers
 *
 */
public class UpdateConfigurationHandler extends LSPCommandHandler {
	
	private static final String QUTE_VALIDATION_ENABLED_SECTION = "qute.validation.enabled";

	private static final String QUTE_VALIDATION_EXCLUDED_SECTION = "qute.validation.excluded";

	@Override
	public Object execute(ExecutionEvent event, Command command, IPath path) throws ExecutionException {
		if (!command.getArguments().isEmpty() && command.getArguments().get(0) instanceof Map<?, ?>) {
			String section = (String) ((Map<String,Object>) command.getArguments().get(0)).get("section");
			Object sectionValue = ((Map<String,Object>) command.getArguments().get(0)).get("value");
			IProject project = getProject((Map<String, Object>) command.getArguments().get(0));
			if (project != null) {
				if (QUTE_VALIDATION_ENABLED_SECTION.equals(section)) {
					QuteUtils.setQuteValidation(project, (boolean) sectionValue);
				} else if (QUTE_VALIDATION_EXCLUDED_SECTION.equals(section)) {
					IFile template = getFile((String) sectionValue);
					if (template != null) {
						QuteUtils.addExclude(project, template.getProjectRelativePath().toPortableString());
					}
				}
			}
		}
		return null;
	}

	/**
	 * @param sectionValue
	 * @return
	 */
	private IFile getFile(String uri) {
		try {
			IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(new URI(uri));
			if (files.length > 0) {
				return files[0];
			}
		} catch (URISyntaxException e) {}
		return null;
	}

	/**
	 * @param object
	 * @return
	 */
	private IProject getProject(Map<String, Object> arguments) {
		try {
			String uri = (String) arguments.get("scopeUri");
			IContainer[] containers = ResourcesPlugin.getWorkspace().getRoot().findContainersForLocationURI(new URI(uri));
			if (containers.length > 0) {
				return (IProject) containers[0].getProject();
			}
		} catch (URISyntaxException e) {}
		return null;
	}

}
