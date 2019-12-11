/*******************************************************************************
 * Copyright (c) 2019 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.quarkus.ui.wizard;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.jboss.tools.common.ui.wizard.AbstractModelWizard;

public class CodeProjectWizard extends AbstractModelWizard<CodeProjectModel> implements INewWizard {

	static final ImageDescriptor QUARKUS_LOGO = 
			ImageDescriptor.createFromFile(CodeProjectWizard.class, "/icon/quarkus-64.png");
	
    public CodeProjectWizard() {
        super("New Quarkus project", new CodeProjectModel());
    }

    @Override
    public void addPages() {
        addPage(new CodeProjectTypeWizardPage(this, getModel()));
        addPage(new CodeProjectInfoWizardPage(this, getModel()));
        addPage(new CodeProjectExtensionsWizardPage(this, getModel()));
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
    }

    @Override
    public boolean performFinish() {
        Job.create("Creating Quarkus project...", 
                new CodeProjectWizardController(getModel())::run).schedule();
        return true;
    }

}
