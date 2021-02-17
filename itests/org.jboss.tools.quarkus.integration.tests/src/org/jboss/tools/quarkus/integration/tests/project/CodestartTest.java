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
package org.jboss.tools.quarkus.integration.tests.project;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.reddeer.eclipse.core.resources.ProjectItem;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.tools.quarkus.integration.tests.project.universal.methods.AbstractQuarkusTest;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels.TextLabels;
import org.jboss.tools.quarkus.reddeer.perspective.QuarkusPerspective;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author olkornii@redhat.com Oleksii Korniienko
 * 
 */
@OpenPerspective(QuarkusPerspective.class)
@RunWith(RedDeerSuite.class)
public class CodestartTest extends AbstractQuarkusTest {

	private static String JAVA_FILES_PATH = "src/main/java";
	private static String PROJECT_NAME = "codestarttest";

	@Test
	public void checkEnabledCodestart() {
		testCreateNewProject(PROJECT_NAME, TextLabels.MAVEN_TYPE, true);
		checkProblemsView();
		assertTrue(checkFileExist());
	}

	@Test
	public void checkDisabledCodestart() {
		testCreateNewProject(PROJECT_NAME, TextLabels.MAVEN_TYPE, false);
		checkProblemsView();
		assertFalse(checkFileExist());
	}

	private boolean checkFileExist() {
		ProjectItem java_dir = new ProjectExplorer().getProject(PROJECT_NAME).getProjectItem(JAVA_FILES_PATH);
		if (java_dir.containsResource("org.acme")) {
			return java_dir.getProjectItem("org.acme").containsResource("ExampleResource.java");
		}
		return false;
	}
}
