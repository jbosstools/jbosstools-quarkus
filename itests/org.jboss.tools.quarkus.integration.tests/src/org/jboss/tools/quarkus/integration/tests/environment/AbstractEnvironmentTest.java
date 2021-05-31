/******************************************************************************* 
 * Copyright (c) 2021 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.quarkus.integration.tests.environment;

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.condition.ConsoleHasText;
import org.eclipse.reddeer.eclipse.debug.ui.launchConfigurations.EnvironmentTab;
import org.eclipse.reddeer.eclipse.debug.ui.launchConfigurations.RunConfigurationsDialog;
import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.tools.quarkus.integration.tests.launch.configuration.AbstractLaunchConfigurationTest;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels.Shell;
import org.jboss.tools.quarkus.reddeer.ui.launch.QuarkusLaunchConfigurationTabGroup;

/**
 * 
 * @author olkornii@redhat.com Oleksii Korniienko
 *
 */
public abstract class AbstractEnvironmentTest extends AbstractLaunchConfigurationTest {

	private static String ENVIRONMENT_NAME = "MVNW_VERBOSE";

	public void testEnvironmentWorks(String projectName) {
		RunConfigurationsDialog runDialog = new RunConfigurationsDialog();
		QuarkusLaunchConfigurationTabGroup launchConfiguration = createNewQuarkusConfiguration(projectName, runDialog);
		checkNewQuarkusConfiguration(projectName, runDialog, launchConfiguration);
		addEnvironment(projectName, runDialog, launchConfiguration);
		runNewQuarkusConfiguration(projectName, runDialog, launchConfiguration, "8080");
		checkEnvironmentWorks(projectName, runDialog, launchConfiguration);
		new ConsoleView().terminateConsole();
		deleteNewQuarkusConfiguration(projectName, runDialog, launchConfiguration);
	}

	public void addEnvironment(String projectName, RunConfigurationsDialog runDialog,
			QuarkusLaunchConfigurationTabGroup launchConfiguration) {
		runDialog.open();
		runDialog.select(launchConfiguration, projectName);
		EnvironmentTab envTab = new EnvironmentTab();
		envTab.activate();
		envTab.add(ENVIRONMENT_NAME, "true");
		new DefaultShell(Shell.RUN_CONFIGURATION).setFocus();
		runDialog.close(true);
	}

	public void checkEnvironmentWorks(String projectName, RunConfigurationsDialog runDialog,
			QuarkusLaunchConfigurationTabGroup launchConfiguration) {
		ConsoleView consoleView = new ConsoleView();
		new WaitUntil(new ConsoleHasText(consoleView, "Takari Maven Wrapper"), TimePeriod.DEFAULT);
	}
}
