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

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.condition.ConsoleHasText;
import org.eclipse.reddeer.eclipse.core.resources.ProjectItem;
import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.group.DefaultGroup;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.swt.impl.text.DefaultText;
import org.eclipse.reddeer.swt.impl.toolbar.DefaultToolItem;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;
import org.eclipse.reddeer.workbench.handler.WorkbenchShellHandler;
import org.eclipse.reddeer.workbench.impl.editor.TextEditor;
import org.jboss.tools.quarkus.integration.tests.project.universal.methods.AbstractQuarkusTest;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels.TextLabels;
import org.jboss.tools.quarkus.reddeer.perspective.QuarkusPerspective;
import org.jboss.tools.quarkus.reddeer.ui.launch.QuarkusLaunchConfigurationTabGroup;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author olkornii@redhat.com Oleksii Korniienko
 * 
 */
@OpenPerspective(QuarkusPerspective.class)
@RunWith(RedDeerSuite.class)
public class RunProjectWithProfileTest extends AbstractQuarkusTest {

	private static String PROJECT_NAME = "testRunWithProfile";
	private static String JAVA_SOURCE_PATH = "src/main/java";
	private static String RESOURCES_SOURCE_PATH = "src/main/resources";
	private static String ORG_ACME = "org.acme.commandmode";
	private static String EXAMPLE_RESOURCE = "HelloCommando.java";
  private static String APPLICATION_PROPERTIES = "application.properties";
	private static String FIRST_LINE = "    	@javax.inject.Inject";
	private static String SECOND_LINE = "    	@org.eclipse.microprofile.config.inject.ConfigProperty(name=\"greeting\", defaultValue = \"commando\")";
	private static String THIRD_LINE = "    	String greeting;";
	private static String GREETING_PROPERTY_NAME = "greeting";
	private static String GREETING_PROPERTY_VALUE = "mygreeting";

	@BeforeClass
	public static void testNewNewQuarkusMavenProject() {
		testCreateNewProject(PROJECT_NAME, TextLabels.MAVEN_TYPE);
		checkProblemsView();
	}

	@Test
	public void testRunWithProfile() {

		ProjectItem exampleResource = new ProjectExplorer().getProject(PROJECT_NAME).getProjectItem(JAVA_SOURCE_PATH)
				.getProjectItem(ORG_ACME).getProjectItem(EXAMPLE_RESOURCE);

		modifyJavaResource(exampleResource, FIRST_LINE, SECOND_LINE, THIRD_LINE);
		
		ProjectItem applicationProperties = new ProjectExplorer().getProject(PROJECT_NAME).getProjectItem(RESOURCES_SOURCE_PATH)
        .getProjectItem(APPLICATION_PROPERTIES);

    modifyApplicationProperties(applicationProperties, GREETING_PROPERTY_NAME + " " + GREETING_PROPERTY_VALUE);


		new QuarkusLaunchConfigurationTabGroup().selectProject(PROJECT_NAME);
		new QuarkusLaunchConfigurationTabGroup().openDebugConfiguration();

		new DefaultTreeItem(TextLabels.QUARKUS_APPLICATION_TREE_ITEM).select();
		new ContextMenuItem("New Configuration").select();
		
		DefaultGroup group = new DefaultGroup("Profile");
		new DefaultText(group).setText("myprofile");

		new PushButton(TextLabels.RUN).click();

		ConsoleView consoleView = new ConsoleView();
		new WaitUntil(new ConsoleHasText(consoleView, GREETING_PROPERTY_VALUE), TimePeriod.getCustom(600));
		WorkbenchShellHandler.getInstance().closeAllNonWorbenchShells();

		ConsoleView cv = new ConsoleView();
		cv.open();
		new DefaultToolItem("Terminate").click();

		checkProblemsView();
	}

	private void modifyJavaResource(ProjectItem exampleResource, String firstValue, String secondValue, String thirdValue) {
		exampleResource.open();
		TextEditor ed = new TextEditor(EXAMPLE_RESOURCE);

		int line = ed.getLineOfText("public class HelloCommando");
		ed.insertLine(line + 1, firstValue);
		ed.insertLine(line + 2, secondValue);
		ed.insertLine(line + 3, thirdValue);
		
		line = ed.getLineOfText("final String name");
		ed.insertLine(line + 1, "name = greeting;");

		ed.save();
	}
	
	 private void modifyApplicationProperties(ProjectItem exampleResource, String line) {
	    exampleResource.open();
	    TextEditor ed = new TextEditor(EXAMPLE_RESOURCE);
	    ed.setText(line);

	    ed.save();
	  }
}