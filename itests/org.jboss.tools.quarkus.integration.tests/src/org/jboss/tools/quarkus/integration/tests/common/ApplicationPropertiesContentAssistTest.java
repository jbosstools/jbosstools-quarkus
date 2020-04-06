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
package org.jboss.tools.quarkus.integration.tests.common;

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.jface.text.contentassist.ContentAssistant;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.impl.editor.TextEditor;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.quarkus.integration.tests.project.AbstractCreateNewProjectTest;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels.TextLabels;
import org.jboss.tools.quarkus.reddeer.perspective.QuarkusPerspective;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jboss.tools.quarkus.core.QuarkusCorePlugin;

/**
 * 
 * @author olkornii@redhat.com
 */
@OpenPerspective(QuarkusPerspective.class)
@RunWith(RedDeerSuite.class)
public class ApplicationPropertiesContentAssistTest {

	private static String PROJECT_NAME = "testContentAssistProject";
	private static String RESOURCE_PATH = "src/main/resources";
	private static String APPLICATION_PROPERTIES = "application.properties";

	private static String TEXT_FOR_TEST_CONTENT_ASSIST = "quarkus";
	private static String PORPOSAL_FOR_SELECT = "quarkus.application.name";
	
	public static final String PLUGIN_ID = "org.jboss.tools.quarkus.core"; //$NON-NLS-1$

	@BeforeClass
	public static void testNewNewQuarkusGradleProject() {
		AbstractCreateNewProjectTest.testCreateNewProject(PROJECT_NAME, TextLabels.MAVEN_TYPE);
		AbstractCreateNewProjectTest.checkJdkVersion(PROJECT_NAME, TextLabels.MAVEN_TYPE);
		AbstractCreateNewProjectTest.checkProblemsView();
	}

	@Test
	public void testContentAssist() {
		new WorkbenchShell().setFocus();
		new ProjectExplorer().selectProjects(PROJECT_NAME);

		new ProjectExplorer().getProject(PROJECT_NAME).getProjectItem(RESOURCE_PATH)
				.getProjectItem(APPLICATION_PROPERTIES).select();
		new ContextMenuItem(TextLabels.OPEN_WITH, TextLabels.GENERIC_TEXT_EDITOR).select();

		TextEditor ed = new TextEditor(APPLICATION_PROPERTIES);
		ed.insertLine(0, TEXT_FOR_TEST_CONTENT_ASSIST);
		ed.selectText(TEXT_FOR_TEST_CONTENT_ASSIST);

		ContentAssistant ca = ed.openContentAssistant();
		
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		try {
			Thread.sleep(1000); // 1 second sleep for sure, that Content Assistant will open
		} catch (InterruptedException e) {
			QuarkusCorePlugin.logException("Interrupted!", e);
		    Thread.currentThread().interrupt();
		}
		ca.chooseProposal(PORPOSAL_FOR_SELECT);
	}

	@After
	public void deleteProject() {
		ProjectExplorer pe = new ProjectExplorer();
		pe.open();
		pe.deleteAllProjects(true);
	}

}
