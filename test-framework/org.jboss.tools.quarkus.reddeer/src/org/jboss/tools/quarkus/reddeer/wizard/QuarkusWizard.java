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

import org.eclipse.reddeer.workbench.workbenchmenu.WorkbenchMenuWizardDialog;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels;

/**
 * 
 * @author jkopriva@redhat.com
 */
public class QuarkusWizard extends WorkbenchMenuWizardDialog {
	
	/**
	 * Constructor
	 * opens wizard
	 */
	public QuarkusWizard() {
		super(QuarkusLabels.Shell.NEW_QUARKUS_PROJECT_WIZARD, QuarkusLabels.TextLabels.FILE_CONTEXT_MENU , QuarkusLabels.TextLabels.NEW_CONTEXT_ITEM, QuarkusLabels.Shell.QUARKUS_PROJECT);
	}
}
