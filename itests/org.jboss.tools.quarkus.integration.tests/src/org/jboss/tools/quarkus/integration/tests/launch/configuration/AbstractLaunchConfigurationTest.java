/******************************************************************************* 
 * Copyright (c) 2020 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.quarkus.integration.tests.launch.configuration;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.tests.common.JobHelpers;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.condition.ConsoleHasText;
import org.eclipse.reddeer.eclipse.debug.ui.launchConfigurations.DebugConfigurationsDialog;
import org.eclipse.reddeer.eclipse.debug.ui.launchConfigurations.LaunchConfigurationsDialog;
import org.eclipse.reddeer.eclipse.debug.ui.launchConfigurations.RunConfigurationsDialog;
import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.tools.quarkus.integration.tests.project.universal.methods.AbstractQuarkusTest;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels.Shell;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels.TextLabels;
import org.jboss.tools.quarkus.reddeer.ui.launch.QuarkusLaunchConfigurationTabGroup;

/**
 * 
 * @author olkornii@redhat.com Oleksii Korniienko
 *
 */

public abstract class AbstractLaunchConfigurationTest extends AbstractQuarkusTest {
	public static void createNewQuarkusProject(String projectName, String projectType) {
		testCreateNewProject(projectName, projectType);
		checkProblemsView();
	}
	
	public static void importProject(String projectName, String location) throws IOException, CoreException, InterruptedException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		File dir = new File(workspace.getRoot().getLocation().toFile(), projectName);
	    if(dir.isFile()) {
	      dir = dir.getParentFile();
	    }
	    FileUtils.copyDirectory(new File(location), dir);

	    final IProject project = workspace.getRoot().getProject(projectName);

	      if(!project.exists()) {
	        IProjectDescription projectDescription = workspace.loadProjectDescription(workspace.getRoot().getLocation().append(projectName).append(".project"));
	        project.create(projectDescription, null);
	        project.open(IResource.NONE, null);
	        JavaCore.run(m -> m.done(), null, new NullProgressMonitor());
	      } else {
	        project.refreshLocal(IResource.DEPTH_INFINITE, null);
	      }
	    try {
	    	JobHelpers.waitForJobsToComplete();
	    } catch (AssertionError e) {}
	}

	public void testNewQuarkusConfiguration(String projectName, String projectType, boolean debug) {
		LaunchConfigurationsDialog launchDialog = debug?new DebugConfigurationsDialog(): new RunConfigurationsDialog();
		QuarkusLaunchConfigurationTabGroup launchConfiguration = createNewQuarkusConfiguration(projectName, launchDialog);
		checkNewQuarkusConfiguration(projectName, launchDialog, launchConfiguration);
		runNewQuarkusConfiguration(projectName, launchDialog, launchConfiguration, "8080", projectType);
		new ConsoleView().terminateConsole();
		deleteNewQuarkusConfiguration(projectName, launchDialog, launchConfiguration);
	}

	public QuarkusLaunchConfigurationTabGroup createNewQuarkusConfiguration(String projectName,
			LaunchConfigurationsDialog launchDialog) {
		launchDialog.open();
		QuarkusLaunchConfigurationTabGroup launchConfiguration = new QuarkusLaunchConfigurationTabGroup();
		launchDialog.create(launchConfiguration, projectName);
		launchConfiguration.selectProject(new DefaultShell(launchDialog.getTitle()), projectName);
		new DefaultShell(launchDialog.getTitle()).setFocus();
		launchDialog.close(true);

		return launchConfiguration;
	}

	public void checkNewQuarkusConfiguration(String projectName, LaunchConfigurationsDialog launchDialog,
			QuarkusLaunchConfigurationTabGroup launchConfiguration) {
		launchDialog.open();
		launchDialog.select(launchConfiguration, projectName);
		new DefaultShell(launchDialog.getTitle()).setFocus();
		launchDialog.close();
	}

	public void runNewQuarkusConfiguration(String projectName, LaunchConfigurationsDialog launchDialog,
			QuarkusLaunchConfigurationTabGroup launchConfiguration, String localhostPort, String projectType) {
		launchDialog.open();
		launchDialog.select(launchConfiguration, projectName);
		new DefaultShell(launchDialog.getTitle()).setFocus();
		launchDialog.run();

		ConsoleView consoleView = new ConsoleView();
		if (TextLabels.GRADLE_TYPE == projectType) {
			new WaitUntil(new ConsoleHasText(consoleView,
					"[[39m[38;5;69mio.quarkus[39m[38;5;188m] ([39m[38;5;71mQuarkus Main Thread[39m[38;5;188m) [39m[38;5;151m[39m[38;5;188m"
							+ projectName),
					TimePeriod.getCustom(600));

		} else {
			new WaitUntil(new ConsoleHasText(consoleView, "[io.quarkus] (Quarkus Main Thread) " + projectName),
					TimePeriod.getCustom(600));
		}
		checkUrlContent("Hello RESTEasy", localhostPort);
	}

	public void deleteNewQuarkusConfiguration(String projectName, LaunchConfigurationsDialog launchDialog,
			QuarkusLaunchConfigurationTabGroup launchConfiguration) {
		launchDialog.open();
		launchDialog.select(launchConfiguration, projectName);
		launchDialog.delete(launchConfiguration, projectName);
		new DefaultShell(launchDialog.getTitle()).setFocus();
		launchDialog.close();
	}
}
