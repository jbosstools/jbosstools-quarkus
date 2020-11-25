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
package org.jboss.tools.quarkus.integration.tests.project.universal.methods;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.eclipse.ui.problems.Problem;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView.ProblemType;
import org.eclipse.reddeer.eclipse.ui.wizards.newresource.BasicNewFileResourceWizard;
import org.eclipse.reddeer.swt.impl.button.OkButton;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.handler.WorkbenchShellHandler;
import org.eclipse.reddeer.workbench.impl.editor.TextEditor;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels.TextLabels;
import org.jboss.tools.quarkus.reddeer.wizard.CodeProjectTypeWizardPage;
import org.jboss.tools.quarkus.reddeer.wizard.QuarkusWizard;
import org.junit.After;

/**
 * 
 * @author olkornii@redhat.com
 *
 */
public abstract class AbstractQuarkusTest {

	private static String pomFile = "pom.xml";

	public static void testCreateNewProject(String projectName, String projectType) {
		new WorkbenchShell().setFocus();

		QuarkusWizard qw = new QuarkusWizard();
		qw.open();
		assertTrue(qw.isOpen());

		CodeProjectTypeWizardPage wp = new CodeProjectTypeWizardPage(qw);
		wp.setProjectName(projectName);
		if (projectType.equals(TextLabels.MAVEN_TYPE)) {
			wp.setMavenProjectType();
		} else if (projectType.equals(TextLabels.GRADLE_TYPE)) {
			wp.setGradleProjectType();
		}

		qw.next();
		if (projectType.equals(TextLabels.GRADLE_TYPE)) {
			new LabeledText(TextLabels.ARTIFACT_ID).setText(projectName);
		}
		qw.finish(TimePeriod.VERY_LONG);

		assertTrue(new ProjectExplorer().containsProject(projectName));

		if (projectType.equals(TextLabels.MAVEN_TYPE)) {
			changePom(projectName, pomFile);
		}
	}

	private static void changePom(String projectName, String openFile) {
		new ProjectExplorer().getProject(projectName).getProjectItem(openFile).select();
		new ContextMenuItem(TextLabels.OPEN_WITH, TextLabels.TEXT_EDITOR).select();

		TextEditor ed = new TextEditor(openFile);

		deleteLine(ed, "<goal>generate-code</goal>");
		deleteLine(ed, "<goal>generate-code-tests</goal>");

		ed.save();
		ed.close();

		new ProjectExplorer().selectProjects(projectName);

		new ContextMenuItem(TextLabels.MAVEN_CONTEXT_MENU_ITEM, TextLabels.UPDATE_MAVEN_PROJECT).select();
		new OkButton().click();

		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);
	}

	private static void deleteLine(TextEditor ed, String strToDelete) {
		int lineToDelete = ed.getLineOfText(strToDelete);
		ed.selectLine(lineToDelete);
		new ContextMenuItem(TextLabels.CUT_CONTEXT_MENU_ITEM).select();

	}

	public static void checkProblemsView() {
		ProblemsView problemsView = new ProblemsView();
		problemsView.open();
		List<Problem> problems = problemsView.getProblems(ProblemType.ERROR);
		assertEquals("There should be no errors in imported project", 0, problems.size());

	}

	public static void createNewFile(String projectName, String fileName, String filePath) {
		BasicNewFileResourceWizard newFileDialog = new BasicNewFileResourceWizard();
		newFileDialog.open();
		WizardNewFileCreationPage page = new WizardNewFileCreationPage(newFileDialog);
		page.setFileName(fileName);
		page.setFolderPath(projectName + "/" + filePath);
		newFileDialog.finish();
		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);
	}

	@After
	public void deleteProject() {
		WorkbenchShellHandler.getInstance().closeAllNonWorbenchShells();
		ProjectExplorer pe = new ProjectExplorer();
		pe.open();
		pe.deleteAllProjects(true);
		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);
	}
}
