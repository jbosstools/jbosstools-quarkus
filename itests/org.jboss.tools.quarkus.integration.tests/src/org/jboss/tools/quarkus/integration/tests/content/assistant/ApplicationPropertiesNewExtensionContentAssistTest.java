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
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.eclipse.condition.ConsoleHasText;
import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.jface.text.contentassist.ContentAssistant;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.handler.WorkbenchShellHandler;
import org.eclipse.reddeer.workbench.impl.editor.TextEditor;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels.TextLabels;
import org.jboss.tools.quarkus.reddeer.perspective.QuarkusPerspective;
import org.jboss.tools.quarkus.reddeer.view.ExtensionsView;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author olkornii@redhat.com
 */
@OpenPerspective(QuarkusPerspective.class)
@RunWith(RedDeerSuite.class)
public class ApplicationPropertiesNewExtensionContentAssistTest extends AbstractContentAssistantTest {

	private static String NEW_EXTENSION_PROJECT_NAME = "testnewextensionca";
	private static String POM_FILE_PATH = "/testnewextensionca/pom.xml";
	
	private static String TEXT_FOR_TEST_CONTENT_ASSIST = "mp";
	private static String PORPOSAL_FOR_SELECT = "mp.openapi.filter";
	

	@BeforeClass
	public static void createNewQuarkusProject() {
		createProjectAndCheckJDK(NEW_EXTENSION_PROJECT_NAME);
	}

	@Test
	public void testContentAssistOldNew() {
		ContentAssistant contentAssistOld = testContentAssistant(NEW_EXTENSION_PROJECT_NAME,
				TEXT_FOR_TEST_CONTENT_ASSIST);
		assertFalse(checkProposal(contentAssistOld, PORPOSAL_FOR_SELECT));

		TextEditor editor = openFileWithTextEditor(NEW_EXTENSION_PROJECT_NAME, TextLabels.GENERIC_TEXT_EDITOR);
		editor.setText("");
		editor.close(true);

		new ProjectExplorer().selectProjects(NEW_EXTENSION_PROJECT_NAME);
		ExtensionsView ev = new ExtensionsView();
		ev.open();
		ev.getExtension("SmallRye OpenAPI").select();
		new ContextMenuItem("Install extension").select();
		
		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);
		ConsoleView consoleView = new ConsoleView();
		new WaitUntil(new ConsoleHasText(consoleView, "Finished at:"), TimePeriod.LONG);

		checkExtensionInPom(new File(WORKSPACE + POM_FILE_PATH),
				"quarkus-smallrye-openapi");
		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);
		
		WorkbenchShellHandler.getInstance().closeAllNonWorbenchShells();
		refreshProject(NEW_EXTENSION_PROJECT_NAME, TextLabels.MAVEN_TYPE);
		
		ContentAssistant contentAssistNew = testContentAssistant(NEW_EXTENSION_PROJECT_NAME,
				TEXT_FOR_TEST_CONTENT_ASSIST);
		assertTrue(checkProposal(contentAssistNew, PORPOSAL_FOR_SELECT));
	}
}