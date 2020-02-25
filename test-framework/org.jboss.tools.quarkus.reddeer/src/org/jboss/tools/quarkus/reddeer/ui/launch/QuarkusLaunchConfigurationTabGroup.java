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
package org.jboss.tools.quarkus.reddeer.ui.launch;

import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.debug.ui.launchConfigurations.LaunchConfiguration;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels.Shell;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels.TextLabels;

/**
 * 
 * @author olkornii@redhat.com
 *
 */

public class QuarkusLaunchConfigurationTabGroup extends LaunchConfiguration {

	public QuarkusLaunchConfigurationTabGroup() {
		super(TextLabels.QUARKUS_LAUNCH_CONFIGURATION);
	}

	/**
	 * Select project in Project Explorer
	 */
	public void selectProject(String projectName) {
		new WorkbenchShell().setFocus();
		new ProjectExplorer().selectProjects(projectName);
	}

	/**
	 * Opens Run configuration dialog
	 */
	public void openRunConfiguration() {
		new ContextMenuItem(TextLabels.RUN_AS_CONTEXT_MENU_ITEM, TextLabels.RUN_CONFIGURATION_CONTEXT_MENU_ITEM)
				.select();
		new WaitUntil(new ShellIsAvailable(Shell.RUN_CONFIGURATION));
	}

}
