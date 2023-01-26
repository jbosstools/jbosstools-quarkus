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
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.condition.ConsoleHasText;
import org.eclipse.reddeer.eclipse.debug.ui.launchConfigurations.DebugConfigurationsDialog;
import org.eclipse.reddeer.eclipse.debug.ui.launchConfigurations.LaunchConfigurationsDialog;
import org.eclipse.reddeer.eclipse.debug.ui.launchConfigurations.RunConfigurationsDialog;
import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.toolbar.DefaultToolItem;
import org.eclipse.m2e.tests.common.JobHelpers;
import org.jboss.tools.quarkus.integration.tests.project.universal.methods.AbstractQuarkusTest;
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
		testNewQuarkusConfiguration(projectName, projectType, debug, "Hello from RESTEasy Reactive");
	}

	public void testNewQuarkusConfiguration(String projectName, String projectType, boolean debug, String url) {
		LaunchConfigurationsDialog launchDialog = debug?new DebugConfigurationsDialog(): new RunConfigurationsDialog();
		QuarkusLaunchConfigurationTabGroup launchConfiguration = createNewQuarkusConfiguration(projectName, launchDialog);
		checkNewQuarkusConfiguration(projectName, launchDialog, launchConfiguration);
		runNewQuarkusConfiguration(projectName, launchDialog, launchConfiguration, "8080", projectType, debug);
		checkUrlContent(url, "8080");
		stopProject(projectType);
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
			QuarkusLaunchConfigurationTabGroup launchConfiguration, String localhostPort, String projectType, boolean debug) {
		launchDialog.open();
		launchDialog.select(launchConfiguration, projectName);
		new DefaultShell(launchDialog.getTitle()).setFocus();
		if (debug) {
			new PushButton("Debug").click();;
		} else {
			if (TextLabels.GRADLE_TYPE == projectType) { 
				new PushButton("Run").click(); // Gradle project has an infinite process, 
											   // "launchDialog.run()" fails because of timeout in "JobIsRunning".
			} else {
				launchDialog.run();
			}
		}
		ConsoleView consoleView = new ConsoleView();
		new WaitUntil(new ConsoleHasText(consoleView, "started in"), TimePeriod.getCustom(600));
	}

	public void deleteNewQuarkusConfiguration(String projectName, LaunchConfigurationsDialog launchDialog,
			QuarkusLaunchConfigurationTabGroup launchConfiguration) {
		launchDialog.open();
		launchDialog.select(launchConfiguration, projectName);
		launchDialog.delete(launchConfiguration, projectName);
		new DefaultShell(launchDialog.getTitle()).setFocus();
		launchDialog.close();
	}
	
	private void stopProject(String projectType) {
		if (TextLabels.GRADLE_TYPE == projectType) {
			new ConsoleView().activate();
			new DefaultToolItem("Cancel Execution").click(); // There is another button for Gradle project
		} else {
			new ConsoleView().terminateConsole();
		}
	}
}
