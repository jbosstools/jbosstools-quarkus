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

import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels.TextLabels;
import org.jboss.tools.quarkus.reddeer.perspective.QuarkusPerspective;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * https://issues.redhat.com/browse/JBIDE-27065 New quarkus launch configuration
 * integration test for Gradle
 * 
 * @author olkornii@redhat.com
 */
@OpenPerspective(QuarkusPerspective.class)
@RunWith(RedDeerSuite.class)
public class CreateNewQuarkusConfigurationGradleTest extends AbstractLaunchConfigurationTest {

	private static String GRADLE_PROJECT_NAME = "code-with-quarkus"; // w8ing for fix
																		// https://issues.redhat.com/browse/JBIDE-27073
	private static ConsoleView consoleView;

	@BeforeClass
	public static void testNewNewQuarkusGradleProject() {
		createNewQuarkusProject(GRADLE_PROJECT_NAME, TextLabels.GRADLE_TYPE);
		consoleView = new ConsoleView();
	}

	@Test
	public void testNewQuarkusGradleConfiguration() {
		testNewQuarkusConfiguration(GRADLE_PROJECT_NAME, consoleView);
	}
}
