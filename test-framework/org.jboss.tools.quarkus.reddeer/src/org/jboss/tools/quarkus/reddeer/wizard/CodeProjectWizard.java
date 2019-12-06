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
package org.jboss.tools.quarkus.reddeer.wizard;

import org.eclipse.reddeer.eclipse.ui.dialogs.NewWizard;
import org.eclipse.reddeer.jface.wizard.WizardDialog;
import org.eclipse.reddeer.swt.impl.button.NextButton;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels;

/**
 * 
 * @author jkopriva@redhat.com
 */
public class CodeProjectWizard extends WizardDialog {

	/**
	 * Opens wizard from File menu
	 * (does not need opened perspective)
	 */
	public void open() {
		new WorkbenchShell().setFocus();		
		new NewWizard().open();
		new DefaultShell("New").setFocus(); 
		new DefaultTreeItem("Quarkus", "Quarkus Project (Code)").select(); 
		new NextButton().click();
		new DefaultShell(QuarkusLabels.Shell.NEW_QUARKUS_PROJECT);
	}
	
	/**
	 * Opens wizard from File menu
	 * (need to have opened perspective)
	 */

	public void openFromPerspective() {
		QuarkusWizard qw = new QuarkusWizard();
		qw.open();
		new DefaultShell(QuarkusLabels.Shell.NEW_QUARKUS_PROJECT);
	}
}
