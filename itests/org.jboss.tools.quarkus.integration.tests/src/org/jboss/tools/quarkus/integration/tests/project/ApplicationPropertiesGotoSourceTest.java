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

import static org.junit.Assert.assertEquals;

import org.eclipse.reddeer.common.wait.AbstractWait;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.swt.impl.menu.ShellMenuItem;
import org.eclipse.reddeer.swt.impl.table.DefaultTable;
import org.eclipse.reddeer.swt.impl.text.DefaultText;
import org.eclipse.reddeer.swt.keyboard.Keyboard;
import org.eclipse.reddeer.swt.keyboard.KeyboardFactory;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.impl.editor.TextEditor;
import org.eclipse.swt.SWT;
import org.jboss.tools.quarkus.integration.tests.project.universal.methods.AbstractQuarkusTest;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels.TextLabels;
import org.jboss.tools.quarkus.reddeer.perspective.QuarkusPerspective;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * https://issues.redhat.com/browse/JBIDE-28195
 * @author jmaury@redhat.com
 */
@OpenPerspective(QuarkusPerspective.class)
@RunWith(RedDeerSuite.class)
public class ApplicationPropertiesGotoSourceTest extends AbstractQuarkusTest {

	private static String PROJECT_NAME = "testgotosourceproject";

	private static String[] PACKAGE_PATH = {"src/main/java", "org.acme"};
	private static String RESOURCE_JAVA_FILE = "ExampleResource.java";
	private static String RESOURCE_PATH = "src/main/resources";
	private static String APPLICATION_PROPERTIES = "application.properties";

	@BeforeClass
	public static void createNewQuarkusProject() {
		testCreateNewProject(PROJECT_NAME, TextLabels.MAVEN_TYPE);
		checkProblemsView();
	}

	@Test
	public void checkThatGotoGoesToSourceFile() {
		TextEditor javaEditor = openFileWithTextEditor(PROJECT_NAME, TextLabels.JAVA_EDITOR, PACKAGE_PATH, RESOURCE_JAVA_FILE);
		javaEditor.insertLine(6, "import org.eclipse.microprofile.config.inject.ConfigProperty;");
		javaEditor.insertLine(11, "\t@ConfigProperty(name=\"myprefix.name\")");
		javaEditor.insertLine(12, "\tpublic String myname;");
		javaEditor.save();
		javaEditor.close();
		TextEditor textEditor = openFileWithTextEditor(PROJECT_NAME, TextLabels.GENERIC_TEXT_EDITOR, RESOURCE_PATH, APPLICATION_PROPERTIES);
		textEditor.insertLine(0, "myprefix.name=app");
		textEditor.save();
		textEditor.selectText("name");
		/*
		 * let the LSP process the file changes and compute information
		 */
		new WaitWhile(new JobIsRunning());
		AbstractWait.sleep(TimePeriod.DEFAULT);

		if ("Mac OS X".equals(System.getProperty("os.name"))) {
			new ShellMenuItem("Window", "Navigation", "Find Actions").select();
			new DefaultText().setText("Open Hyperlink (F3)");
			new DefaultTable().getItem(0).click();
		} else {
			Keyboard keyboard = KeyboardFactory.getKeyboard();
			keyboard.invokeKeyCombination(SWT.F3);
			javaEditor = new TextEditor();
		}
		assertEquals("/" + PROJECT_NAME + "/src/main/java/org/acme/ExampleResource.java", javaEditor.getAssociatedFile().getRelativePath());
	}
}
