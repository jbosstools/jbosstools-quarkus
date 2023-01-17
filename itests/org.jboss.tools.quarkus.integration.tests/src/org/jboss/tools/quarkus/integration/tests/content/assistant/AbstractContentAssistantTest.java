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

import static org.junit.Assert.assertFalse;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.core.exception.CoreLayerException;
import org.eclipse.reddeer.core.lookup.ShellLookup;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.jface.text.contentassist.ContentAssistant;
import org.eclipse.reddeer.swt.impl.menu.ShellMenuItem;
import org.eclipse.reddeer.swt.impl.table.DefaultTable;
import org.eclipse.reddeer.swt.impl.text.DefaultText;
import org.eclipse.reddeer.workbench.condition.ContentAssistantShellIsOpened;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.impl.editor.TextEditor;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels.TextLabels;
import org.jboss.tools.quarkus.core.QuarkusCorePlugin;
import org.jboss.tools.quarkus.integration.tests.project.universal.methods.AbstractQuarkusTest;

/**
 * 
 * @author olkornii@redhat.com
 *
 */
public abstract class AbstractContentAssistantTest extends AbstractQuarkusTest {

	protected static final String WORKSPACE = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();

	private static String RESOURCE_PATH = "src/main/resources";
	private static String APPLICATION_PROPERTIES = "application.properties";

	public static void createProjectAndCheckJDK(String projectName) {
		testCreateNewProject(projectName, TextLabels.MAVEN_TYPE);
		checkProblemsView();
	}

	public static ContentAssistant testContentAssistant(String projectName, String textForContentAssist) {
		new WorkbenchShell().setFocus();
		new ProjectExplorer().selectProjects(projectName);
		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);

		TextEditor editor = openFileWithTextEditor(projectName, TextLabels.GENERIC_TEXT_EDITOR);
		insertAndSave(editor, textForContentAssist);
//		System.out.println("Try to open content assist");
//		AbstractWait.sleep(TimePeriod.VERY_LONG);
//		String os_name = System.getProperty("os.name");
//		System.out.println(System.getProperty("os.name"));
//		AbstractWait.sleep(TimePeriod.DEFAULT);
		return openContentAssist(editor);
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
	
	public static TextEditor openFileWithTextEditor(String projectName, String textEditor) {
		return openFileWithTextEditor(projectName, textEditor, RESOURCE_PATH, APPLICATION_PROPERTIES);
	}

	public static ContentAssistant openContentAssist(TextEditor editor) {
		ContentAssistant contentAssist = null;
		Shell[] shells = ShellLookup.getInstance().getShells();
		ContentAssistantShellIsOpened caw = new ContentAssistantShellIsOpened(shells);
		if ("Mac OS X".equals(System.getProperty("os.name"))) {
			new ShellMenuItem("Window", "Navigation", "Find Actions").select();
			new DefaultText().setText("Content Assist - Content Assist (Space)");
			new DefaultTable().getItem(0).click();

			new WaitUntil(caw, TimePeriod.LONG);
			contentAssist = new ContentAssistant(caw.getContentAssistTable());
		} else {
			try {
				contentAssist = editor.openContentAssistant();
			} catch (org.eclipse.reddeer.common.exception.WaitTimeoutExpiredException e) { // CRS need some time for
																							// download microprofile...
																							// when
																							// content assistent
																							// opens, sometimes it need
																							// more then default 10
																							// seconds
				new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
				new WaitUntil(caw, TimePeriod.LONG);
				contentAssist = new ContentAssistant(caw.getContentAssistTable());
			}
		}
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);

		assertFalse(contentAssist == null);
		return contentAssist;
	}

	public static void insertAndSave(TextEditor editor, String textForContentAssist) {
		editor.setText(textForContentAssist);
		editor.selectText(textForContentAssist);
		editor.save();
	}
}