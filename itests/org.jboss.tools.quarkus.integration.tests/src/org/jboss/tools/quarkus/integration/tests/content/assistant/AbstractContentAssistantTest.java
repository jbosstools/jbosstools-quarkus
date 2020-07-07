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
package org.jboss.tools.quarkus.integration.tests.content.assistant;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.core.exception.CoreLayerException;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.jface.text.contentassist.ContentAssistant;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.handler.WorkbenchShellHandler;
import org.eclipse.reddeer.workbench.impl.editor.TextEditor;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels.TextLabels;
import org.jboss.tools.quarkus.reddeer.view.ExtensionsView;
import org.junit.After;
import org.jboss.tools.quarkus.core.QuarkusCorePlugin;
import org.jboss.tools.quarkus.integration.tests.project.universal.methods.AbstractQuarkusTest;
import org.jboss.tools.quarkus.integration.tests.project.InstallQuarkusExtensionTest;

/**
 * 
 * @author olkornii@redhat.com
 *
 */
public abstract class AbstractContentAssistantTest {

	protected static final String WORKSPACE = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();

	private static String RESOURCE_PATH = "src/main/resources";
	private static String APPLICATION_PROPERTIES = "application.properties";

	public static void createProjectAndCheckJDK(String projectName) {
		AbstractQuarkusTest.testCreateNewProject(projectName, TextLabels.MAVEN_TYPE);
		AbstractQuarkusTest.checkJdkVersion(projectName, TextLabels.MAVEN_TYPE);
		AbstractQuarkusTest.checkProblemsView();
	}

	public ContentAssistant testContentAssistant(String projectName, String textForContentAssist) {
		new WorkbenchShell().setFocus();
		new ProjectExplorer().selectProjects(projectName);

		TextEditor editor = openFileWithTextEditor(projectName, TextLabels.GENERIC_TEXT_EDITOR);

		insertAndCheckProposal(editor, textForContentAssist);

		ContentAssistant ca = openContentAssist(editor);

		return ca;
	}

	/**
	 * Selecting an exist proposal for check, that this proposal is in proposals
	 * 
	 * @return true if proposal was selected, false if not
	 */
	public static boolean checkProposal(ContentAssistant ca, String proposal) {
		try {
			ca.chooseProposal(proposal);
		} catch (CoreLayerException e) {
			QuarkusCorePlugin.logException("No proposal in list!", e);
			return false;
		}
		return true;
	}

	public void addExtension(String projectName) {
		new WorkbenchShell().setFocus();

		new ProjectExplorer().selectProjects(projectName);
		ExtensionsView ev = new ExtensionsView();
		ev.open();
		ev.getExtension("SmallRye OpenAPI").select();
		new ContextMenuItem("Install extension").select();
		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);

		try {
			String pomContent = InstallQuarkusExtensionTest.readFile(WORKSPACE + "/" + projectName + "/pom.xml");
			assertTrue(pomContent.contains("quarkus-smallrye-openapi"));
		} catch (IOException e) {
			QuarkusCorePlugin.logException("Attempt to read the 'pom.xml' failed!", e);
		}

		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		WorkbenchShellHandler.getInstance().closeAllNonWorbenchShells();
	}

	@After
	public void deleteProject() {
		ProjectExplorer pe = new ProjectExplorer();
		pe.open();
		pe.deleteAllProjects(true);
	}

	public static TextEditor openFileWithTextEditor(String projectName, String textEditor) {

		if (textEditor.equals(TextLabels.GENERIC_TEXT_EDITOR)) {
			new ProjectExplorer().getProject(projectName).getProjectItem(RESOURCE_PATH)
					.getProjectItem(APPLICATION_PROPERTIES).open();
		} else {
			new ProjectExplorer().getProject(projectName).getProjectItem(RESOURCE_PATH)
					.getProjectItem(APPLICATION_PROPERTIES).select();
			new ContextMenuItem(TextLabels.OPEN_WITH, textEditor).select();
		}
		
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);

		TextEditor ed = new TextEditor(APPLICATION_PROPERTIES);

		return ed;
	}

	public static ContentAssistant openContentAssist(TextEditor editor) {

		ContentAssistant contentAssist = editor.openContentAssistant();

		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		try {
			Thread.sleep(1000); // 1 second sleep for sure, that Content Assistant will open
		} catch (InterruptedException e) {
			QuarkusCorePlugin.logException("Interrupted!", e);
			Thread.currentThread().interrupt();
		}

		return contentAssist;
	}

	public static void insertAndCheckProposal(TextEditor editor, String textForContentAssist) {
		editor.insertLine(0, textForContentAssist);
		editor.selectText(textForContentAssist);
	}

}