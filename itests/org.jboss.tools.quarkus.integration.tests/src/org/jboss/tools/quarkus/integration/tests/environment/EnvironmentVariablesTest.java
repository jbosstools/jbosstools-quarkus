/******************************************************************************* 
 * Copyright (c) 2021 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.quarkus.integration.tests.environment;

import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.tools.quarkus.reddeer.perspective.QuarkusPerspective;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels.TextLabels;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author olkornii@redhat.com Oleksii Korniienko
 */
@OpenPerspective(QuarkusPerspective.class)
@RunWith(RedDeerSuite.class)
public class EnvironmentVariablesTest extends AbstractEnvironmentVariablesTest {

	private static String PROJECT_NAME = "environment_test";

	@BeforeClass
	public static void createQuarkusProject() {
		createNewQuarkusProject(PROJECT_NAME, TextLabels.MAVEN_TYPE);
	}

	@Test
	public void testEnvironment() {
		testEnvironmentWorks(PROJECT_NAME);
	}
}
