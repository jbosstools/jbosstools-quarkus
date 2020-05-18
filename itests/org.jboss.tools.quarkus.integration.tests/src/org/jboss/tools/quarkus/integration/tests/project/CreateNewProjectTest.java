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
package org.jboss.tools.quarkus.integration.tests.project;

import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.tools.quarkus.integration.tests.project.universal.methods.AbstractQuarkusTest;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels.TextLabels;
import org.jboss.tools.quarkus.reddeer.perspective.QuarkusPerspective;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author jkopriva@redhat.com
 * @editor olkornii@redhat.com
 */
@OpenPerspective(QuarkusPerspective.class)
@RunWith(RedDeerSuite.class)
public class CreateNewProjectTest extends AbstractQuarkusTest {

	private static String MAVEN_PROJECT_NAME = "testmvn";
	private static String GRADLE_PROJECT_NAME = "testgrdl";

	@Test
	public void testNewNewQuarkusMavenProject() {
		testCreateNewProject(MAVEN_PROJECT_NAME, TextLabels.MAVEN_TYPE);
		checkJdkVersion(MAVEN_PROJECT_NAME, TextLabels.MAVEN_TYPE);
		checkProblemsView();
	}

	@Test
	public void testNewNewQuarkusGradleProject() {

		testCreateNewProject(GRADLE_PROJECT_NAME, TextLabels.GRADLE_TYPE);
		checkJdkVersion(GRADLE_PROJECT_NAME, TextLabels.GRADLE_TYPE);

		checkProblemsView();

	}

}
