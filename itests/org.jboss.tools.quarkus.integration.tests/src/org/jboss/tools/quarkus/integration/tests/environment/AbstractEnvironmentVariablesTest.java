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

import org.eclipse.reddeer.eclipse.debug.ui.launchConfigurations.EnvironmentTab;
import org.eclipse.reddeer.eclipse.debug.ui.launchConfigurations.RunConfigurationsDialog;
import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.workbench.impl.editor.TextEditor;
import org.jboss.tools.quarkus.integration.tests.launch.configuration.AbstractLaunchConfigurationTest;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels.Shell;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels.TextLabels;
import org.jboss.tools.quarkus.reddeer.ui.launch.QuarkusLaunchConfigurationTabGroup;

/**
 * 
 * @author olkornii@redhat.com Oleksii Korniienko
 *
 */
public abstract class AbstractEnvironmentVariablesTest extends AbstractLaunchConfigurationTest {

	private static String ENVIRONMENT_NAME = "MYENV";
	private static String ENVIRONMENT_VALUE = "environment_works_!";
	private static String RESOURCE_PATH = "src/main/java";
	private static String ORG_ACME = "org.acme";
	private static String EXAMPLE_RESOURCE = "ExampleResource.java";

	public void testEnvironmentWorks(String projectName) {
		RunConfigurationsDialog runDialog = new RunConfigurationsDialog();
		QuarkusLaunchConfigurationTabGroup launchConfiguration = createNewQuarkusConfiguration(projectName, runDialog);
		checkNewQuarkusConfiguration(projectName, runDialog, launchConfiguration);
		addEnvironment(projectName, runDialog, launchConfiguration);
		changeReturn(projectName);
		runNewQuarkusConfiguration(projectName, runDialog, launchConfiguration, "8080", TextLabels.MAVEN_TYPE, false);
		checkUrlContent("environment_works_!", "8080");
		new ConsoleView().terminateConsole();
		deleteNewQuarkusConfiguration(projectName, runDialog, launchConfiguration);
	}

	private void addEnvironment(String projectName, RunConfigurationsDialog runDialog,
			QuarkusLaunchConfigurationTabGroup launchConfiguration) {
		runDialog.open();
		runDialog.select(launchConfiguration, projectName);
		EnvironmentTab envTab = new EnvironmentTab();
		envTab.activate();
		envTab.add(ENVIRONMENT_NAME, ENVIRONMENT_VALUE);
		new DefaultShell(Shell.RUN_CONFIGURATION).setFocus();
		runDialog.close(true);
	}
	
	private void changeReturn(String projectName) {
		new ProjectExplorer().getProject(projectName).getProjectItem(RESOURCE_PATH)
				.getProjectItem(ORG_ACME).getProjectItem(EXAMPLE_RESOURCE).open();
		TextEditor ed = new TextEditor(EXAMPLE_RESOURCE);
		int line = ed.getLineOfText("return");
		ed.selectLine(line);
		new ContextMenuItem("Cut").select();
		ed.insertLine(line, "return System.getenv(\"MYENV\");");
		ed.save();
	}
}
