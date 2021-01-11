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
package org.jboss.tools.quarkus.integration.tests.project;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.reddeer.common.condition.AbstractWaitCondition;
import org.eclipse.reddeer.common.exception.RedDeerException;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.eclipse.condition.ConsoleHasText;
import org.eclipse.reddeer.eclipse.core.resources.ProjectItem;
import org.eclipse.reddeer.eclipse.debug.ui.launchConfigurations.DebugConfigurationsDialog;
import org.eclipse.reddeer.eclipse.debug.ui.views.variables.VariablesView;
import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.eclipse.ui.perspectives.AbstractPerspective;
import org.eclipse.reddeer.eclipse.ui.perspectives.DebugPerspective;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.swt.api.TreeItem;
import org.eclipse.reddeer.swt.impl.button.OkButton;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.swt.impl.menu.ShellMenuItem;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.styledtext.DefaultStyledText;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.handler.WorkbenchShellHandler;
import org.eclipse.reddeer.workbench.impl.editor.TextEditor;
import org.jboss.tools.quarkus.core.QuarkusCorePlugin;
import org.jboss.tools.quarkus.integration.tests.project.universal.methods.AbstractQuarkusTest;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels.TextLabels;
import org.jboss.tools.quarkus.reddeer.perspective.QuarkusPerspective;
import org.jboss.tools.quarkus.reddeer.ui.launch.QuarkusLaunchConfigurationTabGroup;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author olkornii@redhat.com Oleksii Korniienko
 * 
 */
@OpenPerspective(QuarkusPerspective.class)
@RunWith(RedDeerSuite.class)
public class RunProjectWithDebugTest extends AbstractQuarkusTest {

	private static String PROJECT_NAME = "testrunwithdebug";
	private static String RESOURCE_PATH = "src/main/java";
	private static String ORG_ACME = "org.acme";
	private static String EXAMPLE_RESOURCE = "ExampleResource.java";
	private static String VARIABLE = "test_var";
	private static String FIRST_LINE = "    	String " + VARIABLE + " = \"w/o changes\";";
	private static String SECOND_LINE = "    	System.out.println(\"Printed first \" + test_var);";
	private static String THIRD_LINE = "    	System.out.println(\"Printed second \" + test_var);";
	private static String FILE_PATH = "resources/helloCommandoProject.txt";

	@BeforeClass
	public static void testNewNewQuarkusMavenProject() {
		testCreateNewProject(PROJECT_NAME, TextLabels.MAVEN_TYPE);
		changeProject();
		refreshProject(PROJECT_NAME, TextLabels.MAVEN_TYPE);
		checkProblemsView();
	}

	@Test
	public void testRunWithDebug() {

		ProjectItem exampleResource = new ProjectExplorer().getProject(PROJECT_NAME).getProjectItem(RESOURCE_PATH)
				.getProjectItem(ORG_ACME).getProjectItem(EXAMPLE_RESOURCE);

		insertLines(exampleResource, FIRST_LINE, SECOND_LINE, THIRD_LINE);
		addBreakpointToLine(exampleResource, SECOND_LINE);

		DebugConfigurationsDialog debugDialog = new DebugConfigurationsDialog();
		debugDialog.open();

		QuarkusLaunchConfigurationTabGroup launchConfiguration = new QuarkusLaunchConfigurationTabGroup();
		debugDialog.create(launchConfiguration, PROJECT_NAME);
		launchConfiguration.debug();

		ConsoleView consoleView = new ConsoleView();
		new WaitUntil(new ConsoleHasText(consoleView, "[io.quarkus]"), TimePeriod.getCustom(600));
		WorkbenchShellHandler.getInstance().closeAllNonWorbenchShells();

		openDebugPerspective();
		checkReturn("with changes");

		ConsoleView cv = new ConsoleView();
		cv.open();
		cv.terminateConsole();

		checkProblemsView();

	}

	private void insertLines(ProjectItem exampleResource, String firstValue, String secondValue, String thirdValue) {
		exampleResource.open();
		TextEditor ed = new TextEditor(EXAMPLE_RESOURCE);

		int line = ed.getLineOfText("final String name");
		ed.insertLine(line + 1, firstValue);
		ed.insertLine(line + 2, secondValue);
		ed.insertLine(line + 3, thirdValue);
		ed.save();
	}

	private void addBreakpointToLine(ProjectItem exampleResource, String text) {
		exampleResource.open();
		TextEditor textEditor = new TextEditor(EXAMPLE_RESOURCE);
		textEditor.setCursorPosition(textEditor.getPositionOfText(text));
		openDebugPerspective();
		new ShellMenuItem("Run", "Toggle Breakpoint").select();
		openQuarkusPerspective();
	}

	private void openDebugPerspective() {
		AbstractPerspective dp = new DebugPerspective();
		dp.open();
		assertTrue(dp.isOpened());
	}

	private void openQuarkusPerspective() {
		AbstractPerspective qp = new QuarkusPerspective();
		qp.open();
		assertTrue(qp.isOpened());
	}

	private void checkReturn(String shouldBe) {

		new ShellMenuItem("Run", "Step Over").select();
		ConsoleView consoleView = new ConsoleView();
		new WaitUntil(new ConsoleHasText(consoleView, "Printed first w/o changes"), TimePeriod.DEFAULT);

		VariablesView variablesView = new VariablesView();
		variablesView.open();

		new WaitUntil(new AbstractWaitCondition() {

			@Override
			public boolean test() {
				try {
					TreeItem variable = new DefaultTreeItem(VARIABLE);
					variable.select();
					return variable.isSelected();
				} catch (RedDeerException e) {
					return false;
				}
			}

			@Override
			public String description() {
				return "Variable is not selected";
			}
		}, TimePeriod.LONG);

		new ContextMenuItem("Change Value...").select();
		new DefaultShell("Change Object Value");
		new DefaultStyledText().setText(shouldBe);
		new OkButton().click();

		new WaitWhile(new JobIsRunning());

		new ShellMenuItem("Run", "Step Over").select();
		new WaitUntil(new ConsoleHasText(consoleView, "Printed second with changes"), TimePeriod.DEFAULT);
		new ShellMenuItem("Run", "Resume").select();
		new WaitUntil(new ConsoleHasText(consoleView, "hello commando"), TimePeriod.LONG);
	}

	private static void changeProject() {
		File file = new File(FILE_PATH).getAbsoluteFile();

		String st = "";
		StringBuilder bld = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			while ((st = reader.readLine()) != null)
				bld.append(st + "\n");
		} catch (FileNotFoundException e) {
			QuarkusCorePlugin.logException("Can`t open stream for read from file!", e);
		} catch (IOException e1) {
			QuarkusCorePlugin.logException("IOException while onpening file <" + FILE_PATH + ">", e1);
		}

		String newProject = bld.toString();

		if (!newProject.equals("")) {
			ProjectItem exampleResource = new ProjectExplorer().getProject(PROJECT_NAME).getProjectItem(RESOURCE_PATH)
					.getProjectItem(ORG_ACME).getProjectItem(EXAMPLE_RESOURCE);

			exampleResource.open();
			TextEditor ed = new TextEditor(EXAMPLE_RESOURCE);

			new WaitUntil(new AbstractWaitCondition() {

				@Override
				public boolean test() {
					try {
						return ed.isActive();
					} catch (RedDeerException e) {
						return false;
					}
				}

				@Override
				public String description() {
					return "Opening TextEditor for ExampleResource";
				}
			}, TimePeriod.LONG);

			ed.setText(newProject);
			ed.save();
			ed.close();
		}
	}
}