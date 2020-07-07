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

import org.eclipse.reddeer.jface.text.contentassist.ContentAssistant;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.tools.quarkus.reddeer.perspective.QuarkusPerspective;
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

	private static String NEW_EXTENSION_PROJECT_NAME = "testNewExtensionContentAssistProject";

	private static String TEXT_FOR_TEST_CONTENT_ASSIST = "mp";
	private static String PORPOSAL_FOR_SELECT = "mp.openapi.filter";

	@BeforeClass
	public static void createNewQuarkusProject() {
		createProjectAndCheckJDK(NEW_EXTENSION_PROJECT_NAME);
	}

	@Test
	public void testContentAssistOldNew() {
		ContentAssistant ca = testContentAssistant(NEW_EXTENSION_PROJECT_NAME, TEXT_FOR_TEST_CONTENT_ASSIST);
		assertFalse(checkProposal(ca, PORPOSAL_FOR_SELECT));

		addExtension(NEW_EXTENSION_PROJECT_NAME);
		ca = testContentAssistant(NEW_EXTENSION_PROJECT_NAME, TEXT_FOR_TEST_CONTENT_ASSIST);
		assertTrue(checkProposal(ca, PORPOSAL_FOR_SELECT));
	}
}
