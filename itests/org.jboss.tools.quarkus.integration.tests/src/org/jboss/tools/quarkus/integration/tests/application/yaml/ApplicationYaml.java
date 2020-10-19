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
package org.jboss.tools.quarkus.integration.tests.application.yaml;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.eclipse.condition.ConsoleHasText;
import org.eclipse.reddeer.eclipse.debug.ui.launchConfigurations.RunConfigurationsDialog;
import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.handler.WorkbenchShellHandler;
import org.eclipse.reddeer.workbench.impl.editor.TextEditor;
import org.jboss.tools.quarkus.integration.tests.project.universal.methods.AbstractQuarkusTest;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels.Shell;
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
public class ApplicationYaml extends AbstractQuarkusTest {

	private static String projectName = "testApplicationYaml";
	private static String applicationYamlFile = "application.yaml";
	private static String applicationYamlPath = "src/main/resources";
	private static String pomName = "pom.xml";
	private static List<String> extension = Arrays.asList("\u0020</dependency>",
			"\u0020\u0020<artifactId>quarkus-config-yaml</artifactId>", "\u0020\u0020<groupId>io.quarkus</groupId>",
			"\u0020<dependency>");
	private static List<String> applicationYamlLines = Arrays.asList(
			"\u0020\u0020\u0020\u0020name: new_test_quarkus_project_name", "\u0020\u0020application:", "quarkus:");

	@BeforeClass
	public static void createNewQuarkusProject() {
		testCreateNewProject(projectName, TextLabels.MAVEN_TYPE);
		checkProblemsView();
	}

	@Test
	public void runWithApplicationYaml() {
		new ProjectExplorer().getProject(projectName).getProjectItem(pomName).open();
		addExtensionInPomFile();
		createNewFile(projectName, applicationYamlFile, applicationYamlPath);
		addLinesInApplicationYaml();
		WorkbenchShellHandler.getInstance().closeAllNonWorbenchShells();

		RunConfigurationsDialog runDialog = new RunConfigurationsDialog();
		runDialog.open();
		QuarkusLaunchConfigurationTabGroup launchConfiguration = new QuarkusLaunchConfigurationTabGroup();
		runDialog.create(launchConfiguration, projectName);
		launchConfiguration.selectProject(new DefaultShell(Shell.RUN_CONFIGURATION), projectName);
		new DefaultShell(Shell.RUN_CONFIGURATION).setFocus();
		runDialog.run();

		ConsoleView consoleView = new ConsoleView();
		new WaitUntil(new ConsoleHasText(consoleView, "new_test_quarkus_project_name"), TimePeriod.getCustom(600));

		consoleView.terminateConsole();
	}

	private void addExtensionInPomFile() {
		TextEditor editor = new TextEditor(pomName);
		int endLineNumber = editor.getLineOfText("</dependencyManagement>");
		String startDependenciesLine = editor.getTextAtLine(endLineNumber + 1);
		assertEquals("Expected <dependencies> but was " + startDependenciesLine, "  <dependencies>",
				startDependenciesLine);
		int lineForInsert = endLineNumber + 2;

		for (String str : extension) {
			editor.insertLine(lineForInsert, str);
		}

		editor.close(true);
		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);
	}

	private void addLinesInApplicationYaml() {
		TextEditor editor = new TextEditor(applicationYamlFile);

		int lineForInsert = 0;
		for (String str : applicationYamlLines) {
			editor.insertLine(lineForInsert, str);
		}
		editor.close(true);
		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);
	}
}