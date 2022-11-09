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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.buildship.core.internal.CorePlugin;
import org.eclipse.buildship.core.internal.configuration.ProjectConfiguration;
import org.eclipse.buildship.core.internal.launch.GradleRunConfigurationAttributes;
import org.eclipse.buildship.core.internal.launch.GradleRunConfigurationDelegate;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.jboss.tools.quarkus.core.QuarkusCorePlugin;

/**
 * @author Red Hat Developers
 *
 */
public class GradleToolSupport extends AbstractToolSupport {

	public GradleToolSupport(IProject project) {
		super(project);
	}

	@Override
	public String getScript() {
		if (Platform.OS_WIN32.equals(Platform.getOS())) {
			return "gradlew.bat";
		} else {
			return "gradlew";
		}
	}
	
	private String emptyOrValue(Object src) {
		return src!=null?src.toString():"";
	}

	private ILaunchConfigurationWorkingCopy getConfiguration(ToolContext context) throws CoreException {
		ILaunchConfigurationType launchConfigurationType = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(GradleRunConfigurationDelegate.ID);
		ILaunchConfigurationWorkingCopy launchConfiguration = launchConfigurationType.newInstance(null, context.getName() + "__Gradle__");
		ProjectConfiguration configuration = CorePlugin.configurationManager().loadProjectConfiguration(context.getProject());
		/*
		 * arguments are part of the project configuration so we need to load it and
		 * create a run that does override them.
		 */
		GradleRunConfigurationAttributes attributes = new GradleRunConfigurationAttributes(Collections.emptyList(),
				"${workspace_loc:/" + project.getName() + "}",
				configuration.getBuildConfiguration().getGradleDistribution().toString(),
				emptyOrValue(configuration.getBuildConfiguration().getGradleUserHome()),
				emptyOrValue(configuration.getBuildConfiguration().getJavaHome()),
				configuration.getBuildConfiguration().getJvmArguments(),
				configuration.getBuildConfiguration().getArguments(),
				false,
				configuration.getBuildConfiguration().isShowConsoleView(),
				true,
				configuration.getBuildConfiguration().isOfflineMode(),
				configuration.getBuildConfiguration().isBuildScansEnabled());
		attributes.apply(launchConfiguration);
		return launchConfiguration;
	}
	
	private List<String> add(List<String> list, String element) {
		List<String> result = new ArrayList<>(list);
		result.add(element);
		return result;
	}

	@Override
	public void addExtension(ToolContext context) throws CoreException {
		ILaunchConfigurationWorkingCopy launchConfiguration = getConfiguration(context);
		launchConfiguration.setAttribute("tasks", Collections.singletonList("quarkus:add-extension"));
		launchConfiguration.setAttribute("arguments", add(launchConfiguration.getAttribute("arguments", Collections.emptyList()), "--extensions=" + context.getExtraArguments().iterator().next()));
		launchConfiguration.launch(ILaunchManager.RUN_MODE, new NullProgressMonitor());
	}

	@Override
	public ILaunch run(ToolExecutionContext context, IProgressMonitor monitor) throws CoreException {
		ILaunch launch = new Launch(null, ILaunchManager.RUN_MODE, null);
		Job job = Job.create("Starting " + context.getName(), monitor2 -> {
			ILaunchConfigurationWorkingCopy launchConfiguration = getConfiguration(context);
			launchConfiguration.setAttribute("tasks", Collections.singletonList("quarkusDev"));
			List<String> arguments = add(launchConfiguration.getAttribute("arguments", Collections.emptyList()), "-Ddebug=" + getDebugArgument(context));
			if (StringUtils.isNotBlank(context.getProfile())) {
				arguments = add(arguments, "-Dquarkus.profile=" + context.getProfile());
			}
			launchConfiguration.setAttribute("arguments", arguments);
			launchConfiguration.launch(ILaunchManager.RUN_MODE, monitor);
		});
		job.addJobChangeListener(new JobChangeAdapter() {

			@Override
			public void done(IJobChangeEvent event) {
				try {
					launch.terminate();
				} catch (DebugException e) {
					QuarkusCorePlugin.logException(e.getLocalizedMessage(), e);
				}
			}
		});
		job.schedule();
		return launch;
	}

	protected String getDebugArgument(ToolExecutionContext context) {
		return context.isDebug()?Integer.toString(context.getDebugPort()):"false";
	}
}
