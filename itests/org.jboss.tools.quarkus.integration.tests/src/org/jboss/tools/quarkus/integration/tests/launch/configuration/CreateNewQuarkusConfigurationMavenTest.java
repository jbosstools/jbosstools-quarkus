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
package org.jboss.tools.quarkus.integration.tests.launch.configuration;

import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.tools.quarkus.reddeer.perspective.QuarkusPerspective;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels.TextLabels;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * https://issues.redhat.com/browse/JBIDE-27065 New quarkus launch configuration
 * integration test for Maven
 * 
 * @author olkornii@redhat.com
 */
@OpenPerspective(QuarkusPerspective.class)
@RunWith(RedDeerSuite.class)
public class CreateNewQuarkusConfigurationMavenTest extends AbstractLaunchConfigurationTest {

	private static String MAVEN_PROJECT_NAME = "testmvnconf";

	@BeforeClass
	public static void testNewNewQuarkusMavenProject() {
		createNewQuarkusProject(MAVEN_PROJECT_NAME, TextLabels.MAVEN_TYPE);
	}

	@Test
	public void testNewQuarkusMavenConfiguration() {
		testNewQuarkusConfiguration(MAVEN_PROJECT_NAME);

	}
}
