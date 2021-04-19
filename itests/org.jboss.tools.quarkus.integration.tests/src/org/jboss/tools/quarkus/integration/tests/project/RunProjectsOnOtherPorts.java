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

package org.jboss.tools.quarkus.integration.tests.project;

import static org.junit.Assert.fail;

import org.eclipse.reddeer.common.matcher.RegexMatcher;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.core.matcher.WithTextMatcher;
import org.eclipse.reddeer.eclipse.debug.ui.launchConfigurations.RunConfigurationsDialog;
import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.impl.editor.TextEditor;
import org.jboss.tools.quarkus.integration.tests.launch.configuration.AbstractLaunchConfigurationTest;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels.TextLabels;
import org.jboss.tools.quarkus.reddeer.perspective.QuarkusPerspective;
import org.jboss.tools.quarkus.reddeer.ui.launch.QuarkusLaunchConfigurationTabGroup;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author olkornii@redhat.com
 */
@OpenPerspective(QuarkusPerspective.class)
@RunWith(RedDeerSuite.class)
public class RunProjectsOnOtherPorts extends AbstractLaunchConfigurationTest {

	private static String PROJECT_NAME_1 = "testport1";
	private static String PROJECT_NAME_2 = "testport2";
	private static String DEBUG_PORT = "quarkus.http.port=8085";

	private static String RESOURCE_PATH = "src/main/resources";
	private static String APPLICATION_PROPERTIES = "application.properties";

	@BeforeClass
	public static void createNewQuarkusProject() {
		testCreateNewProject(PROJECT_NAME_1, TextLabels.MAVEN_TYPE);
		checkProblemsView();
		testCreateNewProject(PROJECT_NAME_2, TextLabels.MAVEN_TYPE);
		checkProblemsView();
	}

	@Test
	public void runBothProjects() {
		changeDebugPort();

		RunConfigurationsDialog firstRunDialog = new RunConfigurationsDialog();
		QuarkusLaunchConfigurationTabGroup firstLaunchConfiguration = createNewQuarkusConfiguration(PROJECT_NAME_1,
				firstRunDialog);
		ConsoleView consoleView = new ConsoleView();
		runNewQuarkusConfiguration(PROJECT_NAME_1, firstRunDialog, firstLaunchConfiguration, "8080");
		if (!consoleView.getConsoleText().contains("Listening for transport dt_socket at address:")) {
			consoleView.terminateConsole();
			deleteNewQuarkusConfiguration(PROJECT_NAME_1, firstRunDialog, firstLaunchConfiguration);
			fail("First application: debug was not started!");
		}

		RunConfigurationsDialog secondRunDialog = new RunConfigurationsDialog();
		QuarkusLaunchConfigurationTabGroup secondLaunchConfiguration = createNewQuarkusConfiguration(PROJECT_NAME_2,
				secondRunDialog);
		runNewQuarkusConfiguration(PROJECT_NAME_2, secondRunDialog, secondLaunchConfiguration, "8085");

		terminateBothApplications(consoleView);
		deleteNewQuarkusConfiguration(PROJECT_NAME_1, firstRunDialog, firstLaunchConfiguration);
		deleteNewQuarkusConfiguration(PROJECT_NAME_2, secondRunDialog, secondLaunchConfiguration);

		WithTextMatcher applicationMatcher = new WithTextMatcher(new RegexMatcher(".*" + PROJECT_NAME_2 + ".*"));
		consoleView.switchConsole(applicationMatcher);
		if (!consoleView.getConsoleText().contains("Listening for transport dt_socket at address:")) {
			fail("Second application: debug was not started!");
		}
	}

	private void terminateBothApplications(ConsoleView consoleView) {
		consoleView.terminateConsole();
		WithTextMatcher applicationMatcher = new WithTextMatcher(new RegexMatcher(".*" + PROJECT_NAME_1 + ".*"));
		consoleView.switchConsole(applicationMatcher);
		consoleView.terminateConsole();
	}

	private void changeDebugPort() {
		TextEditor editor = openFileWithTextEditor(PROJECT_NAME_2, TextLabels.GENERIC_TEXT_EDITOR, RESOURCE_PATH,
				APPLICATION_PROPERTIES);
		editor.setText(DEBUG_PORT);
		editor.save();
		editor.close();
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
	}
}