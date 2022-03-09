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

import java.util.Arrays;
import java.util.Map;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.lsp4e.command.LSPCommandHandler;
import org.eclipse.lsp4j.Command;
import org.eclipse.ui.PartInitException;
import org.jboss.tools.quarkus.lsp4e.QuarkusLSPPlugin;

/**
 * @author Red Hat Developers
 *
 */
public class OpenJavaDefinitionHandler extends LSPCommandHandler {
	
	private static final String PROJECT_PARAMETER = "projectUri";
	
	private static final String TYPE_PARAMETER = "sourceType";
	
	private static final String FIELD_PARAMETER = "sourceField";
	
	private static final String METHOD_PARAMETER = "sourceMethod";

	@Override
	public Object execute(ExecutionEvent event, Command command, IPath path) throws ExecutionException {
		try {
			if (!command.getArguments().isEmpty() && command.getArguments().get(0) instanceof Map<?, ?>) {
				IJavaElement element = getJavaElement((Map<String, String>) command.getArguments().get(0));
				if (element != null) {
					JavaUI.openInEditor(element);
				}
			}
		} catch (PartInitException | JavaModelException e) {
			QuarkusLSPPlugin.logException(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * @param parameters
	 * @return
	 */
	private IJavaElement getJavaElement(Map<String, String> parameters) {
		try {
			String projectName = parameters.get(PROJECT_PARAMETER);
			if (projectName != null) {
				IProject project =ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
				if (project != null && project.isAccessible()) {
					IJavaProject javaProject = JavaCore.create(project);
					String typeName = parameters.get(TYPE_PARAMETER);
					if (javaProject != null && typeName != null) {
						IType type = javaProject.findType(typeName);
						if (type != null) {
							IJavaElement element = null;
							String name = parameters.get(METHOD_PARAMETER);
							if (name == null) {
								name = parameters.get(FIELD_PARAMETER);
								if (name != null) {
									element = type.getField(name);
								}
							} else {
								final String fname = name;
								element = Arrays.stream(type.getMethods()).filter(m -> fname.equals(m.getElementName())).findFirst().orElse(null);
							}
							return element;
						}
					}
				}
			}
		} catch (JavaModelException e) {
			QuarkusLSPPlugin.logException(e.getLocalizedMessage(), e);
		}
		return null;
	}

}
