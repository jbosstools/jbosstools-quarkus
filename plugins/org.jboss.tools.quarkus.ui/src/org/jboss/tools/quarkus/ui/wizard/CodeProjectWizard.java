/*******************************************************************************
 * Copyright (c) 2019-2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.quarkus.ui.wizard;

import java.util.Objects;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.jboss.tools.common.ui.wizard.AbstractModelWizard;
import org.jboss.tools.quarkus.core.code.model.Tool;

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
    	boolean valid = isValid();
        if (valid) {
			Job.create("Creating Quarkus project...", new CodeProjectWizardController(getModel())::run).schedule();
		}
		return valid;
    }

	private boolean isValid() {
		CodeProjectModel model = getModel();
		boolean valid = true;
		if (Tool.GRADLE == model.getTool() && !Objects.equals(model.getArtifactId(), model.getProjectName())) {
		  ((WizardPage)getContainer().getCurrentPage()).setErrorMessage("Gradle project name and artifact id must be identical");
		  valid = false;
		}
		return valid;
	}

}
