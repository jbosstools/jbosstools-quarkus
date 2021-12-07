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

import java.io.File;

import org.eclipse.core.resources.ResourcesPlugin;

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.eclipse.condition.ConsoleHasText;
import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.quarkus.integration.tests.project.universal.methods.AbstractQuarkusTest;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels.TextLabels;
import org.jboss.tools.quarkus.reddeer.perspective.QuarkusPerspective;
import org.jboss.tools.quarkus.reddeer.view.ExtensionsView;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author jkopriva@redhat.com
 */
@OpenPerspective(QuarkusPerspective.class)
@RunWith(RedDeerSuite.class)
public class InstallQuarkusExtensionTest extends AbstractQuarkusTest {

	protected static final String WORKSPACE = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();

	@BeforeClass
	public static void prepareWorkspace() {
		testCreateNewProject("test", TextLabels.MAVEN_TYPE);
		checkProblemsView();
	}

	@Test
	public void testAddQuarkusExtensionInProject() {
		new WorkbenchShell().setFocus();
		new ProjectExplorer().selectProjects("test");

		ExtensionsView ev = new ExtensionsView();
		ev.open();
		ev.getExtension("RESTEasy").select();
		new ContextMenuItem("Install extension").select();
		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);
		new WaitUntil(new ConsoleHasText(new ConsoleView(),"Extension io.quarkus:quarkus-resteasy has been installed"), TimePeriod.LONG);

		checkExtensionInPom(new File(WORKSPACE + "/test/pom.xml"), "quarkus-resteasy");
		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);
	}
}
