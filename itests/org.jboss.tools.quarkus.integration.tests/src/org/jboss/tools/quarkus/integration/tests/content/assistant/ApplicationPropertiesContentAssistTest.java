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
public class ApplicationPropertiesContentAssistTest extends AbstractContentAssistantTest {

	private static String CONTENT_ASSIST_PROJECT_NAME = "testcontentassistproject";

	private static String TEXT_FOR_TEST_CONTENT_ASSIST = "mp";
	private static String PORPOSAL_FOR_SELECT = "mp.context.ManagedExecutor.maxAsync";

	@BeforeClass
	public static void createNewQuarkusProject() {
		createProjectAndCheckJDK(CONTENT_ASSIST_PROJECT_NAME);
	}

	@Test
	public void testContentAssist() {
		ContentAssistant ca = testContentAssistant(CONTENT_ASSIST_PROJECT_NAME, TEXT_FOR_TEST_CONTENT_ASSIST);
		assertTrue(checkProposal(ca, PORPOSAL_FOR_SELECT));
	}
}
