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
import static org.junit.Assert.fail;

import java.io.IOException;

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.jface.text.contentassist.ContentAssistant;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.handler.WorkbenchShellHandler;
import org.eclipse.reddeer.workbench.impl.editor.TextEditor;
import org.jboss.tools.quarkus.core.QuarkusCorePlugin;
import org.jboss.tools.quarkus.integration.tests.project.InstallQuarkusExtensionTest;
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

	private static String NEW_EXTENSION_PROJECT_NAME = "testNewExtensionCA";

	private static String TEXT_FOR_TEST_CONTENT_ASSIST = "resteasy";
	private static String PORPOSAL_FOR_SELECT = "quarkus.resteasy.path";

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
		editor.close(false);

		new ProjectExplorer().selectProjects(NEW_EXTENSION_PROJECT_NAME);
		ExtensionsView ev = new ExtensionsView();
		ev.open();
		ev.getExtension("RESTEasy").select();
		new ContextMenuItem("Install extension").select();
		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);

		try {
			String pomContent = InstallQuarkusExtensionTest
					.readFile(WORKSPACE + "/" + NEW_EXTENSION_PROJECT_NAME + "/pom.xml");
			assertTrue(pomContent.contains("quarkus-resteasy"));
		} catch (IOException e) {
			QuarkusCorePlugin.logException("Interrupted!", e);
			fail("Attempt to read the 'pom.xml' failed!");
		}
		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);
		WorkbenchShellHandler.getInstance().closeAllNonWorbenchShells();

		ContentAssistant contentAssistNew = testContentAssistant(NEW_EXTENSION_PROJECT_NAME,
				TEXT_FOR_TEST_CONTENT_ASSIST);
		assertTrue(checkProposal(contentAssistNew, PORPOSAL_FOR_SELECT));
	}
}