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
import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.swt.impl.button.OkButton;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.swt.impl.table.DefaultTableItem;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.tools.quarkus.integration.tests.project.universal.methods.AbstractQuarkusTest;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels.TextLabels;
import org.jboss.tools.quarkus.reddeer.ui.launch.QuarkusLaunchConfigurationTabGroup;
import org.eclipse.reddeer.swt.impl.toolbar.DefaultToolItem;

/**
 * 
 * @author olkornii@redhat.com
 *
 */

public abstract class AbstractLaunchConfigurationTest extends AbstractQuarkusTest {
	public static void createNewQuarkusProject(String projectName, String projectType) {
		testCreateNewProject(projectName, projectType);
		checkProblemsView();
	}

	public void testNewQuarkusConfiguration(String projectName) {
		createNewQuarkusConfiguration(projectName);
		checkNewQuarkusConfiguration(projectName);
		runNewQuarkusConfiguration(projectName);
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
		new OkButton().click();

		new QuarkusLaunchConfigurationTabGroup().apply();
		new PushButton(TextLabels.CLOSE).click();
	}

	public void checkNewQuarkusConfiguration(String projectName) {
		new QuarkusLaunchConfigurationTabGroup().selectProject(projectName);
		new QuarkusLaunchConfigurationTabGroup().openRunConfiguration();

		new DefaultTreeItem(TextLabels.QUARKUS_APPLICATION_TREE_ITEM, projectName + TextLabels.CONFIGURATION).select();

		new PushButton(TextLabels.CLOSE).click();
	}

	public void runNewQuarkusConfiguration(String projectName) {
		new QuarkusLaunchConfigurationTabGroup().selectProject(projectName);
		new QuarkusLaunchConfigurationTabGroup().openRunConfiguration();

		new DefaultTreeItem(TextLabels.QUARKUS_APPLICATION_TREE_ITEM, projectName + TextLabels.CONFIGURATION).select();

		new PushButton("Run").click();

		ConsoleView consoleView = new ConsoleView();
		new WaitUntil(new ConsoleHasText(consoleView, "[io.quarkus]"), TimePeriod.getCustom(600));

		checkUrlContent("hello");

		new DefaultToolItem("Terminate").click();
	}

	public void deleteNewQuarkusConfiguration(String projectName) {
		new QuarkusLaunchConfigurationTabGroup().selectProject(projectName);
		new QuarkusLaunchConfigurationTabGroup().openRunConfiguration();

		new DefaultTreeItem(TextLabels.QUARKUS_APPLICATION_TREE_ITEM, projectName + TextLabels.CONFIGURATION).select();

		new DefaultToolItem("Delete selected launch configuration(s)").click();
		new PushButton("Delete").click();
		new PushButton(TextLabels.CLOSE).click();

	}
}
