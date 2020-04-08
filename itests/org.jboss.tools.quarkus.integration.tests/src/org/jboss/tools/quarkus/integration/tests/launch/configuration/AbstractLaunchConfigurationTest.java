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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.eclipse.reddeer.common.exception.RedDeerException;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.condition.ConsoleHasText;
import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.swt.impl.table.DefaultTableItem;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;
import org.junit.After;
import org.jboss.tools.quarkus.integration.tests.project.AbstractCreateNewProjectTest;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels.TextLabels;
import org.jboss.tools.quarkus.reddeer.ui.launch.QuarkusLaunchConfigurationTabGroup;
import org.eclipse.reddeer.swt.impl.toolbar.DefaultToolItem;

/**
 * 
 * @author olkornii@redhat.com
 *
 */

public abstract class AbstractLaunchConfigurationTest {
	public static void createNewQuarkusProject(String projectName, String projectType) {
		AbstractCreateNewProjectTest.testCreateNewProject(projectName, projectType);
		AbstractCreateNewProjectTest.checkJdkVersion(projectName, projectType);

		AbstractCreateNewProjectTest.checkProblemsView();
	}

	public void testNewQuarkusConfiguration(String projectName, ConsoleView consoleView) {
		createNewQuarkusConfiguration(projectName);
		checkNewQuarkusConfiguration(projectName);
		runNewQuarkusConfiguration(projectName, consoleView);
		deleteNewQuarkusConfiguration(projectName);
	}

	public void createNewQuarkusConfiguration(String projectName) {
		new QuarkusLaunchConfigurationTabGroup().selectProject(projectName);
		new QuarkusLaunchConfigurationTabGroup().openRunConfiguration();

		new DefaultTreeItem(TextLabels.QUARKUS_APPLICATION_TREE_ITEM).select();
		new ContextMenuItem("New Configuration").select();

		new QuarkusLaunchConfigurationTabGroup().setName(projectName + TextLabels.CONFIGURATION);

		new PushButton("Browse...").click();
		new DefaultTableItem(projectName).select();
		new PushButton("OK").click();

		new PushButton("Apply").click();
		new PushButton("Close").click();
	}

	public void checkNewQuarkusConfiguration(String projectName) {
		new QuarkusLaunchConfigurationTabGroup().selectProject(projectName);
		new QuarkusLaunchConfigurationTabGroup().openRunConfiguration();

		new DefaultTreeItem(TextLabels.QUARKUS_APPLICATION_TREE_ITEM, projectName + TextLabels.CONFIGURATION).select();

		new PushButton("Close").click();
	}

	public void runNewQuarkusConfiguration(String projectName, ConsoleView consoleView) {
		new QuarkusLaunchConfigurationTabGroup().selectProject(projectName);
		new QuarkusLaunchConfigurationTabGroup().openRunConfiguration();

		new DefaultTreeItem(TextLabels.QUARKUS_APPLICATION_TREE_ITEM, projectName + TextLabels.CONFIGURATION).select();

		new PushButton("Run").click();
		new WaitUntil(new ConsoleHasText(consoleView, "[io.quarkus] (main) Installed features: [cdi, resteasy]"),
				TimePeriod.getCustom(600));

		String urlContent = "";

		try {
			urlContent = getUrlContent("http://localhost:8080/hello");
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!(urlContent.equals("hello"))) {
			throw new RedDeerException("Should be 'hello' !");
		}
		new DefaultToolItem("Terminate").click();
	}

	public void deleteNewQuarkusConfiguration(String projectName) {
		new QuarkusLaunchConfigurationTabGroup().selectProject(projectName);
		new QuarkusLaunchConfigurationTabGroup().openRunConfiguration();

		new DefaultTreeItem(TextLabels.QUARKUS_APPLICATION_TREE_ITEM, projectName + TextLabels.CONFIGURATION).select();

		new DefaultToolItem("Delete selected launch configuration(s)").click();
		new PushButton("Delete").click();
		new PushButton("Close").click();

	}

	@After
	public void deleteAllProjects() {
		ProjectExplorer pe = new ProjectExplorer();
		pe.open();
		pe.deleteAllProjects(true);
	}

	public String getUrlContent(String readedURL) throws IOException {

		URL localhost = new URL(readedURL);
		String inputLine = "";
		try (BufferedReader in = new BufferedReader(new InputStreamReader(localhost.openStream()));) {
			inputLine = in.readLine();
			in.close();
		}
		return inputLine;
	}

}
