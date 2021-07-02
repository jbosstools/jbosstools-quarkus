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

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.condition.ConsoleHasText;
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

	public void testNewQuarkusConfiguration(String projectName, String projectType) {
		RunConfigurationsDialog runDialog = new RunConfigurationsDialog();
		QuarkusLaunchConfigurationTabGroup launchConfiguration = createNewQuarkusConfiguration(projectName, runDialog);
		checkNewQuarkusConfiguration(projectName, runDialog, launchConfiguration);
		runNewQuarkusConfiguration(projectName, runDialog, launchConfiguration, "8080", projectType);
		new ConsoleView().terminateConsole();
		deleteNewQuarkusConfiguration(projectName, runDialog, launchConfiguration);
	}

	public QuarkusLaunchConfigurationTabGroup createNewQuarkusConfiguration(String projectName,
			RunConfigurationsDialog runDialog) {
		runDialog.open();
		QuarkusLaunchConfigurationTabGroup launchConfiguration = new QuarkusLaunchConfigurationTabGroup();
		runDialog.create(launchConfiguration, projectName);
		launchConfiguration.selectProject(new DefaultShell(Shell.RUN_CONFIGURATION), projectName);
		new DefaultShell(Shell.RUN_CONFIGURATION).setFocus();
		runDialog.close(true);

		return launchConfiguration;
	}

	public void checkNewQuarkusConfiguration(String projectName, RunConfigurationsDialog runDialog,
			QuarkusLaunchConfigurationTabGroup launchConfiguration) {
		runDialog.open();
		runDialog.select(launchConfiguration, projectName);
		new DefaultShell(Shell.RUN_CONFIGURATION).setFocus();
		runDialog.close();
	}

	public void runNewQuarkusConfiguration(String projectName, RunConfigurationsDialog runDialog,
			QuarkusLaunchConfigurationTabGroup launchConfiguration, String localhostPort, String projectType) {
		runDialog.open();
		runDialog.select(launchConfiguration, projectName);
		new DefaultShell(Shell.RUN_CONFIGURATION).setFocus();
		runDialog.run();

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

	public void deleteNewQuarkusConfiguration(String projectName, RunConfigurationsDialog runDialog,
			QuarkusLaunchConfigurationTabGroup launchConfiguration) {
		runDialog.open();
		runDialog.select(launchConfiguration, projectName);
		runDialog.delete(launchConfiguration, projectName);
		new DefaultShell(Shell.RUN_CONFIGURATION).setFocus();
		runDialog.close();
	}
}
