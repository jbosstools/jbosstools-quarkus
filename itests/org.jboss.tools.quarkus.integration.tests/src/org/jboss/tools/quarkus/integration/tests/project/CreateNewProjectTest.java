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

import static org.junit.Assert.assertTrue;

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.quarkus.reddeer.perspective.QuarkusPerspective;
import org.jboss.tools.quarkus.reddeer.wizard.CodeProjectTypeWizardPage;
import org.jboss.tools.quarkus.reddeer.wizard.QuarkusWizard;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author jkopriva@redhat.com
 */
@OpenPerspective(QuarkusPerspective.class)
@RunWith(RedDeerSuite.class)
public class CreateNewProjectTest {

	private static String MAVEN_PROJECT_NAME = "testProjectMaven";

	@Test
	public void testNewNewQuarkusMavenProject() {
		new WorkbenchShell().setFocus();

		QuarkusWizard qw = new QuarkusWizard();
		qw.open();
		assertTrue(qw.isOpen());

		CodeProjectTypeWizardPage wp = new CodeProjectTypeWizardPage(qw);
		wp.setProjectName(MAVEN_PROJECT_NAME);
		wp.setMavenProjectType();

		qw.next();
		qw.finish(TimePeriod.VERY_LONG);

		assertTrue(new ProjectExplorer().containsProject(MAVEN_PROJECT_NAME));
	}

	@After
	public void deleteProject() {
		ProjectExplorer pe = new ProjectExplorer();
		pe.open();
		pe.deleteAllProjects(true);
	}

}
