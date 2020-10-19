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

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.eclipse.debug.ui.launchConfigurations.LaunchConfiguration;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.table.DefaultTableItem;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels.Shell;

/**
 * 
 * @author olkornii@redhat.com
 *
 */

public class QuarkusLaunchConfigurationTabGroup extends LaunchConfiguration {

	public QuarkusLaunchConfigurationTabGroup() {
		super("Quarkus Application");
	}
	
	/**
	 * Select project in Quarkus Launch Configuration Dialog -> Browse...
	 * 
	 * @param composite is a shell
	 * @param projectName
	 */
	public void selectProject(ReferencedComposite composite, String projectName) {
		new PushButton(composite, "Browse...").click();
		new DefaultTableItem(projectName).select();
		new PushButton("OK").click();
	}
	
	/**
	 * Click "Debuge" button in Quarkus Launch Configuration Dialog
	 * 
	 * @param composite is a shell
	 */
	public void debug() {
		new DefaultShell(Shell.DEBUG_CONFIGURATION).setFocus();
		String shellText = new DefaultShell().getText();
		new PushButton("Debug").click();

		new WaitWhile(new ShellIsAvailable(shellText), TimePeriod.VERY_LONG);
		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);
	}
}
