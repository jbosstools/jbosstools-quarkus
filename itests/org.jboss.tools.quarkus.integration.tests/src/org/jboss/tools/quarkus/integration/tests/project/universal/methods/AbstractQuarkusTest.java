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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.List;

import org.eclipse.reddeer.common.exception.WaitTimeoutExpiredException;
import org.eclipse.reddeer.common.wait.AbstractWait;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.eclipse.ui.problems.Problem;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView.ProblemType;
import org.eclipse.reddeer.eclipse.ui.wizards.newresource.BasicNewFileResourceWizard;
import org.eclipse.reddeer.swt.impl.button.LabeledCheckBox;
import org.eclipse.reddeer.swt.impl.button.OkButton;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.handler.WorkbenchShellHandler;
import org.eclipse.reddeer.workbench.impl.editor.TextEditor;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.quarkus.core.QuarkusCorePlugin;
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

	/**
	 * Overriding method for create new quarkus project without/with codestart
	 */
	public static void testCreateNewProject(String projectName, String projectType, boolean withCodestart) {
		QuarkusWizard qw = openQuarkusWizard();
		insertProjectInfo(projectName, projectType, qw);
		if (!withCodestart) {
			new LabeledCheckBox("Example code:").click();
		}
		finishCreatingProject(projectName, projectType, qw);
	}

	/**
	 * Default method for create new quarkus project
	 */
	public static void testCreateNewProject(String projectName, String projectType) {
		QuarkusWizard qw = openQuarkusWizard();
		insertProjectInfo(projectName, projectType, qw);
		finishCreatingProject(projectName, projectType, qw);
	}

	private static QuarkusWizard openQuarkusWizard() {
		new WorkbenchShell().setFocus();
		QuarkusWizard qw = new QuarkusWizard();
		qw.open();
		assertTrue(qw.isOpen());
		return qw;
	}

	private static void insertProjectInfo(String projectName, String projectType, QuarkusWizard qw) {
		CodeProjectTypeWizardPage wp = new CodeProjectTypeWizardPage(qw);
		wp.setProjectName(projectName);
		if (projectType.equals(TextLabels.MAVEN_TYPE)) {
			wp.setMavenProjectType();
		} else if (projectType.equals(TextLabels.GRADLE_TYPE)) {
			wp.setGradleProjectType();
		}
		qw.next();
		new LabeledText(TextLabels.ARTIFACT_ID).setText(projectName);
	}

	private static void finishCreatingProject(String projectName, String projectType, QuarkusWizard qw) {
		qw.finish(TimePeriod.getCustom(900));
		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);
		assertTrue(new ProjectExplorer().containsProject(projectName));
		refreshProject(projectName, projectType);
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
		try {
			newFileDialog.finish();
		} catch (org.eclipse.reddeer.common.exception.WaitTimeoutExpiredException e) { // sometimes node.js warning
			// blocks test and need to close
			// warning shell
			WorkbenchShellHandler.getInstance().closeAllNonWorbenchShells();
		}
		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);
	}

	public static void checkUrlContent(String should_be, String localhostPort) {
		URL localhost = null;
		try {
			localhost = new URL("http://localhost:" + localhostPort + "/hello");
		} catch (MalformedURLException e) {
			QuarkusCorePlugin.logException("Wrong URL! ", e);
		}
		assertNotEquals("Should not be <NULL> , but is <" + localhost + ">", null, localhost);

		String readedLine = "default";
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(localhost.openStream()))) {
			readedLine = reader.readLine();
			reader.close();
		} catch (IOException e) {
			QuarkusCorePlugin.logException("Can`t read from url!", e);
		}
		assertEquals("Should be <" + should_be + "> , but is <" + readedLine + ">", should_be, readedLine);
	}

	public static void refreshProject(String projectName, String projectType) {
		new ProjectExplorer().selectProjects(projectName);
		if (projectType.equals(TextLabels.MAVEN_TYPE)) {
			new ContextMenuItem(TextLabels.MAVEN_CONTEXT_MENU_ITEM, TextLabels.UPDATE_MAVEN_PROJECT).select();
			new OkButton().click();
		} else {
			new ContextMenuItem(TextLabels.GRADLE_CONTEXT_MENU_ITEM, TextLabels.REFRESH_GRADLE_PROJECT).select();
		}
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		if (projectType.equals(TextLabels.MAVEN_TYPE)) {
			refreshProjectWorkaround(projectName);
		}
	}

	private static void refreshProjectWorkaround(String projectName) {

		AbstractWait.sleep(TimePeriod.DEFAULT);

		ProblemsView problemsView = new ProblemsView();
		problemsView.open();
		List<Problem> problems = problemsView.getProblems(ProblemType.ERROR);
		Problem problem = null;
		if (problems.size() > 0) {
			problem = problems.get(0);
			String description = problem.getDescription();
			assertTrue("There should be Quarkus code generation error. But instead of it: " + description, 
					description.contains("Quarkus code generation phase has failed"));
		}

		TextEditor ed = openFileWithTextEditor(projectName, "Text Editor", "", "pom.xml");
		ed.insertLine(3,"");
		ed.save();
		ed.close();

		AbstractWait.sleep(TimePeriod.DEFAULT);
	}

	public static void checkExtensionInPom(File file, String extension) {
		try {
			String pomContent = readFile(file);
			assertTrue(pomContent.contains(extension));
		} catch (IOException e) {
			QuarkusCorePlugin.logException("Interrupted!", e);
			fail("Attempt to read the 'pom.xml' failed!");
		}
	}

	public static String readFile(File file) throws IOException {
		FileInputStream stream = new FileInputStream(file);
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			/* Instead of using default, pass in a decoder. */
			return Charset.defaultCharset().decode(bb).toString();
		} finally {
			stream.close();
		}
	}

	public static TextEditor openFileWithTextEditor(String projectName, String textEditorType, String resourcePath,
			String fileName) {
		return openFileWithTextEditor(projectName, textEditorType, new String[] { resourcePath }, fileName);
	}

	public static TextEditor openFileWithTextEditor(String projectName, String textEditorType, String resourcePath[],
			String fileName) {

		try {
			if (resourcePath.length == 0 || "".equals(resourcePath[0])) {
				new ProjectExplorer().getProject(projectName).getProjectItem(fileName).openWith(textEditorType);
			} else {
				new ProjectExplorer().getProject(projectName).getProjectItem(resourcePath).getProjectItem(fileName)
				.openWith(textEditorType);	
			}
		} catch (WaitTimeoutExpiredException e) { // CRS need some time for
			// download microprofile...
			// when
			// application_properties
			// opens, sometimes it need
			// more then default 10
			// seconds
		}
		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);
		return new TextEditor(fileName);
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
