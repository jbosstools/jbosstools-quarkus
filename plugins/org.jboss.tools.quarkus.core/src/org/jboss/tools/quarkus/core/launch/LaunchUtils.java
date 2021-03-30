/*
 * Copyright 2019-2020 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.tools.quarkus.core.launch;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.externaltools.internal.IExternalToolConstants;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.jboss.tools.quarkus.core.QuarkusCoreConstants;
import org.jboss.tools.quarkus.core.project.ProjectUtils;

public class LaunchUtils {
	
	public static void initializeQuarkusLaunchConfiguration(
			ILaunchConfigurationWorkingCopy workingCopy) throws CoreException {
		String projectName = workingCopy.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String)null);
		String profileName = workingCopy.getAttribute(QuarkusCoreConstants.ATTR_PROFILE_NAME, "");
		String suffix = profileName.length() > 0 ? " -Dquarkus.profile=" + profileName:"";
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (project.exists() && project.isAccessible()) {
			workingCopy.setAttribute(IExternalToolConstants.ATTR_LOCATION, ProjectUtils.getToolSupport(project).getScriptPath().toOSString());
			workingCopy.setAttribute(IExternalToolConstants.ATTR_WORKING_DIRECTORY, project.getLocation().toOSString());
			workingCopy.setAttribute(IExternalToolConstants.ATTR_BUILD_SCOPE, "${projects:" + project.getName() + "}");
			if (ProjectUtils.isMavenProject(project)) {
				workingCopy.setAttribute(IExternalToolConstants.ATTR_TOOL_ARGUMENTS, "compile quarkus:dev" + suffix);
			} else {
				workingCopy.setAttribute(IExternalToolConstants.ATTR_TOOL_ARGUMENTS, "quarkusDev" + suffix);
			}
		}
	}
}
