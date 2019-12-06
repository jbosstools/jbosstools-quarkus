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

import static org.jboss.tools.quarkus.ui.wizard.CodeProjectModel.ARTIFACTID_PROPERTY;
import static org.jboss.tools.quarkus.ui.wizard.CodeProjectModel.CLASSNAME_PROPERTY;
import static org.jboss.tools.quarkus.ui.wizard.CodeProjectModel.GROUPID_PROPERTY;
import static org.jboss.tools.quarkus.ui.wizard.CodeProjectModel.PATH_PROPERTY;
import static org.jboss.tools.quarkus.ui.wizard.CodeProjectModel.VERSION_PROPERTY;
import static org.jboss.tools.quarkus.ui.wizard.CodeProjectTypeWizardPage.createTextWidget;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.common.ui.databinding.MandatoryStringValidator;
import org.jboss.tools.common.ui.wizard.AbstractDataBindingWizardPage;

public class CodeProjectInfoWizardPage extends AbstractDataBindingWizardPage {

    private final CodeProjectModel model;

    protected CodeProjectInfoWizardPage(IWizard wizard,
            CodeProjectModel model) {
        super("Project type", "Select the code.quarkus.io endpoint and project type", "second", wizard, null);
        this.model = model;
        setImageDescriptor(CodeProjectWizard.QUARKUS_LOGO);
    }

    @Override
    protected void doCreateControls(Composite parent, DataBindingContext dbc) {
        GridLayoutFactory.fillDefaults()
        .margins(6, 6).numColumns(2)
        .applyTo(parent);

    // maven artifact
    Label mavenArtifactExplanation = new Label(parent, SWT.None);
    mavenArtifactExplanation.setText("Maven Artifact:");
    GridDataFactory.fillDefaults()
        .span(2,1).align(SWT.FILL, SWT.CENTER)
        .applyTo(mavenArtifactExplanation);
    createTextWidget(parent, model, dbc, "Artifact id:", 
            ARTIFACTID_PROPERTY, new MandatoryStringValidator("Please specify an artifact id"));
    createTextWidget(parent, model, dbc, "Group id:", 
            GROUPID_PROPERTY, new MandatoryStringValidator("Please specify a group id"));
    createTextWidget(parent, model, dbc, "Version:", 
            VERSION_PROPERTY, new MandatoryStringValidator("Please specify a version"));
    
    Label restExplanation = new Label(parent, SWT.None);
    restExplanation.setText("REST:");
    GridDataFactory.fillDefaults()
        .span(2,1).align(SWT.FILL, SWT.CENTER)
        .applyTo(restExplanation);
    createTextWidget(parent, model, dbc, "Class name:", 
            CLASSNAME_PROPERTY, new MandatoryStringValidator("Please specify a Java class name"));
    createTextWidget(parent, model, dbc, "Path:", 
            PATH_PROPERTY, new MandatoryStringValidator("Please specify a path"));
    
    }

    
    @Override
    protected void onPageActivated(DataBindingContext dbc) {
        setToPreferredVerticalSize(getShell());
    }

    private void setToPreferredVerticalSize(Shell shell) {
        Point size = shell.computeSize(600, SWT.DEFAULT);
        // windows doesn't take progress bar area into account, maven section gets cut off.
        //size.y += 20;
        shell.setSize(size );
    }
    
}
