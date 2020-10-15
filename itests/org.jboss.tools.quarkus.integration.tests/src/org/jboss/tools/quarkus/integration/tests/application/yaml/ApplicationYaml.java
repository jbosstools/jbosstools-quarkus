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
import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.swt.impl.toolbar.DefaultToolItem;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.handler.WorkbenchShellHandler;
import org.eclipse.reddeer.workbench.impl.editor.TextEditor;
import org.jboss.tools.quarkus.integration.tests.project.universal.methods.AbstractQuarkusTest;
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

	private static String PROJECT_NAME = "testApplicationYaml";
	private static String APPLICATION_YAML_NAME = "application.yaml";
	private static String APPLICATION_YAML_PATH = "src/main/resources";
	private static String POM_NAME = "pom.xml";
	private static List<String> extension = Arrays.asList("	</dependency>",
			"		<artifactId>quarkus-config-yaml</artifactId>", "		<groupId>io.quarkus</groupId>",
			"	<dependency>");
	private static List<String> application_yaml = Arrays.asList("    name: new_test_quarkus_project_name",
			"  application:", "quarkus:");

	@BeforeClass
	public static void createNewQuarkusProject() {
		testCreateNewProject(PROJECT_NAME, TextLabels.MAVEN_TYPE);
		checkProblemsView();
	}

	@Test
	public void runWithApplicationYaml() {
		new ProjectExplorer().getProject(PROJECT_NAME).getProjectItem(POM_NAME).open();
		TextEditor editor = new TextEditor(POM_NAME);

		addExtensionInPomFile(editor);

		createNewFile(PROJECT_NAME, APPLICATION_YAML_NAME, APPLICATION_YAML_PATH);

		addLinesInApplicationYaml(editor);

		WorkbenchShellHandler.getInstance().closeAllNonWorbenchShells();

		new QuarkusLaunchConfigurationTabGroup().selectProject(PROJECT_NAME);
		new QuarkusLaunchConfigurationTabGroup().openRunConfiguration();

		new DefaultTreeItem(TextLabels.QUARKUS_APPLICATION_TREE_ITEM).select();
		new ContextMenuItem("New Configuration").select();
		new PushButton("Run").click();

		ConsoleView consoleView = new ConsoleView();
		new WaitUntil(new ConsoleHasText(consoleView, "new_test_quarkus_project_name"), TimePeriod.getCustom(600));

		new DefaultToolItem("Terminate").click();
	}

	private void addExtensionInPomFile(TextEditor editor) {
		int end_line_number = editor.getLineOfText("</dependencyManagement>");
		String start_dependencies_line = editor.getTextAtLine(end_line_number + 1);
		assertEquals("Expected <dependencies> but was " + start_dependencies_line, "  <dependencies>",
				start_dependencies_line);
		int line_for_insert = end_line_number + 2;

		for (String str : extension) {
			editor.insertLine(line_for_insert, str);
		}

		editor.close(true);
		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);
	}

	private void addLinesInApplicationYaml(TextEditor editor) {
		editor = new TextEditor(APPLICATION_YAML_NAME);

		int line_for_insert = 0;
		for (String str : application_yaml) {
			editor.insertLine(line_for_insert, str);
		}
		editor.close(true);
		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);
	}
}