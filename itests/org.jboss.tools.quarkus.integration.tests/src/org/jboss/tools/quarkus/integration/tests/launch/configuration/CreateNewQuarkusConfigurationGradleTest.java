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

	private static String GRADLE_PROJECT_NAME = "testgrdlconf";

	@BeforeClass
	public static void testNewNewQuarkusGradleProject() {
//		String os_name = System.getProperty("os.name");
//		System.out.println(os_name);
//		System.out.println(OS.LINUX);
//		System.out.println(OS.MACOSX);
//		System.out.println(OS.WINDOWS);
//		AbstractWait.sleep(TimePeriod.LONG);
		createNewQuarkusProject(GRADLE_PROJECT_NAME, TextLabels.GRADLE_TYPE);
	}

	@Test
	public void testNewQuarkusGradleConfiguration() {
		testRunConfiguration();
		testDebugConfiguration();
	}
	
	private void testRunConfiguration() {
		testNewQuarkusConfiguration(GRADLE_PROJECT_NAME, TextLabels.GRADLE_TYPE, false);
	}
	
	private void testDebugConfiguration() {
		testNewQuarkusConfiguration(GRADLE_PROJECT_NAME, TextLabels.GRADLE_TYPE, true);
	}
}
