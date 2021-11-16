/*******************************************************************************
 * Copyright (c) 2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.quarkus.tool;

import java.util.Collections;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.m2e.actions.MavenLaunchConstants;
import org.jboss.tools.quarkus.core.project.ProjectUtils;

/**
 * @author Red Hat Developers
 *
 */
public class MavenToolSupport extends AbstractToolSupport {

	public MavenToolSupport(IProject project) {
		super(project);
	}

	@Override
	public String getScript() {
		if (Platform.OS_WIN32.contentEquals(Platform.getOS())) {
			return "mvnw.cmd";
		} else {
			return "mvnw";
		}
	}
	
	private ILaunchConfigurationWorkingCopy getConfiguration(ToolContext context) throws CoreException {
		ILaunchConfigurationType launchConfigurationType = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(MavenLaunchConstants.LAUNCH_CONFIGURATION_TYPE_ID);
		ILaunchConfigurationWorkingCopy launchConfiguration = launchConfigurationType.newInstance(null, context.getName() + "__Maven__");
		launchConfiguration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, "${workspace_loc:/" + project.getName() + "}");
		launchConfiguration.setAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, context.getEnvironment());
		launchConfiguration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_JRE_CONTAINER_PATH, ProjectUtils.getJREEntry(project));
		launchConfiguration.setAttribute(MavenLaunchConstants.ATTR_WORKSPACE_RESOLUTION, true);
		return launchConfiguration;
	}

	@Override
	public void addExtension(ToolContext context) throws CoreException {
		ILaunchConfigurationWorkingCopy launchConfiguration = getConfiguration(context);
		launchConfiguration.setAttribute(MavenLaunchConstants.ATTR_GOALS, "quarkus:add-extension");
		launchConfiguration.setAttribute(MavenLaunchConstants.ATTR_PROPERTIES, Collections.singletonList("extension=" + context.getExtraArguments().iterator().next()));
		launchConfiguration.launch(ILaunchManager.RUN_MODE, new NullProgressMonitor());
	}

	@Override
	public ILaunch run(ToolExecutionContext context, IProgressMonitor monitor) throws CoreException {
		ILaunchConfigurationWorkingCopy launchConfiguration = getConfiguration(context);
		launchConfiguration.setAttribute(MavenLaunchConstants.ATTR_GOALS, "compile quarkus:dev");
		launchConfiguration.setAttribute(MavenLaunchConstants.ATTR_PROPERTIES, Collections.singletonList("debug=" + context.getDebugPort()));
		return launchConfiguration.launch(ILaunchManager.RUN_MODE, monitor);
	}
}
