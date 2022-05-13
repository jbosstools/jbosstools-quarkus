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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.lsp4e.LSPEclipseUtils;
import org.jboss.tools.quarkus.lsp4e.QuarkusLSPPlugin;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Red Hat Developers
 *
 */
public class QuteUtils {

	public static boolean isOwnContenType(IContentType contentType) {
		return contentType != null && (QuarkusLSPPlugin.QUTE_CONTENT_TYPE_HTML.equals(contentType.getId())
				|| QuarkusLSPPlugin.QUTE_CONTENT_TYPE_YAML.equals(contentType.getId())
				|| QuarkusLSPPlugin.QUTE_CONTENT_TYPE_JSON.equals(contentType.getId())
				|| QuarkusLSPPlugin.QUTE_CONTENT_TYPE_TXT.equals(contentType.getId())
				|| QuarkusLSPPlugin.QUTE_CONTENT_TYPE.equals(contentType.getId()));
	}
	
	public static boolean hasQuteSettings(IProject project) {
		try {
			ProjectScope scope = new ProjectScope(project);
			IEclipsePreferences node = scope.getNode(QuarkusLSPPlugin.PREFERENCES_QUALIFIER);
			return Arrays.stream(node.keys()).filter(name -> QuarkusLSPPlugin.QUTE_VALIDATION.equals(name) || QuarkusLSPPlugin.QUTE_EXCLUDES.equals(name)).findFirst().isPresent();
		} catch (BackingStoreException e) {
			return false;
		}
	}

	protected static IScopeContext[] getScopes(IProject project) {
		return hasQuteSettings(project)?new IScopeContext[] {new ProjectScope(project), InstanceScope.INSTANCE, DefaultScope.INSTANCE}:new IScopeContext[] {InstanceScope.INSTANCE, DefaultScope.INSTANCE};
	}
	
	
	public static boolean isQuteValidationEnabled(IProject project) {
		IScopeContext[] scopes = getScopes(project);
		return QuarkusLSPPlugin.ENABLED.equals(QuarkusLSPPlugin.ENABLE_KEY.getStoredValue(scopes, false, null));
	}

	/**
	 * @param project
	 * @param parseBoolean
	 */
	public static void setQuteValidation(IProject project, boolean enabled) {
		try {
			IEclipsePreferences node = new ProjectScope(project).getNode(QuarkusLSPPlugin.PREFERENCES_QUALIFIER);
			node.putBoolean(QuarkusLSPPlugin.QUTE_VALIDATION, enabled);
			node.flush();
		} catch (BackingStoreException e) {
			QuarkusLSPPlugin.logException(e.getLocalizedMessage(), e);
		}
	}

	public static String[] getQuteExcludes(IProject project) {
		IScopeContext[] scopes = getScopes(project);
		String excludes = QuarkusLSPPlugin.EXCLUDE_KEY.getStoredValue(scopes, false, null);
		if (excludes.length() > 0) {
			return excludes.split(",");
		} else {
			return new String[0];
		}
	}

	/**
	 * @param project
	 * @param pattern
	 */
	public static void addExclude(IProject project, String pattern) {
		try {
			IEclipsePreferences node = new ProjectScope(project).getNode(QuarkusLSPPlugin.PREFERENCES_QUALIFIER);
			String excludes = QuarkusLSPPlugin.EXCLUDE_KEY.getStoredValue(getScopes(project), false, null);
			if (excludes.length() > 0) {
				node.put(QuarkusLSPPlugin.QUTE_EXCLUDES, String.join(",", excludes, pattern));
			} else {
				node.put(QuarkusLSPPlugin.QUTE_EXCLUDES, pattern);
			}
			node.flush();
		} catch (BackingStoreException e) {
			QuarkusLSPPlugin.logException(e.getLocalizedMessage(), e);
		}
		
	}

	public static Map<String, Object> getQuteSettings() {
		Map<String, Object> settings = new HashMap<>();
		Map<String, Object> qute = new HashMap<>();
		Map<String, Object> workspaceFolders = new HashMap<>();
		for(IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			if (project.isAccessible()) {
				Map<String, Object> projectInfo = new HashMap<>();
				Map<String, Object> validation = new HashMap<>();
				validation.put("enabled", isQuteValidationEnabled(project));
				String[] excludes = getQuteExcludes(project);
				if (excludes.length > 0) {
					validation.put("excluded", excludes);
				}
				projectInfo.put("validation", validation);
				workspaceFolders.put(LSPEclipseUtils.toUri(project).toString(), projectInfo);
			}
		}
		qute.put("workspaceFolders", workspaceFolders);
		settings.put("qute", qute);
		return settings;
	}
}
