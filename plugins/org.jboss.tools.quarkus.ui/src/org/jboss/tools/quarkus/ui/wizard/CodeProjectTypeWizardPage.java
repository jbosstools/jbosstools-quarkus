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

import static org.jboss.tools.quarkus.ui.wizard.CodeProjectModel.LOCATION_PROPERTY;
import static org.jboss.tools.quarkus.ui.wizard.CodeProjectModel.MODEL_PROPERTY;
import static org.jboss.tools.quarkus.ui.wizard.CodeProjectModel.PROJECT_NAME_PROPERTY;
import static org.jboss.tools.quarkus.ui.wizard.CodeProjectModel.TOOL_PROPERTY;
import static org.jboss.tools.quarkus.ui.wizard.CodeProjectModel.USE_DEFAULT_LOCATION_PROPERTY;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.typed.BeanProperties;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.databinding.viewers.IViewerObservableValue;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.typed.ViewerProperties;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.foundation.ui.databinding.EclipseProjectValidator;
import org.jboss.tools.foundation.ui.databinding.InvertingBooleanConverter;
import org.jboss.tools.foundation.ui.databinding.MandatoryStringValidator;
import org.jboss.tools.foundation.ui.databinding.RequiredControlDecorationUpdater;
import org.jboss.tools.foundation.ui.databinding.ValueBindingBuilder;
import org.jboss.tools.foundation.ui.databinding.wizard.AbstractDataBindingWizardPage;
import org.jboss.tools.foundation.ui.databinding.wizard.WizardUtils;
import org.jboss.tools.quarkus.core.code.model.QuarkusModelRegistry;
import org.jboss.tools.quarkus.core.code.model.QuarkusStream;
import org.jboss.tools.quarkus.core.code.model.Tool;

public class CodeProjectTypeWizardPage extends AbstractDataBindingWizardPage {

    private static final int PREFERRED_WIDTH = 200;

    private final CodeProjectModel model;

    protected CodeProjectTypeWizardPage(IWizard wizard,
            CodeProjectModel model) {
        super("Project type", "Select the code.quarkus.io endpoint and project type", "first", wizard, null);
        this.model = model;
        setImageDescriptor(CodeProjectWizard.QUARKUS_LOGO);
    }

    @Override
    protected void doCreateControls(Composite parent, DataBindingContext dbc) {
        // explanation
		GridDataFactory.fillDefaults()
			.align(SWT.FILL, SWT.FILL)
			.hint(PREFERRED_WIDTH, SWT.DEFAULT)
			.grab(true, true)
			.applyTo(parent);
    	GridLayoutFactory.fillDefaults()
    		.numColumns(2)
    		.applyTo(parent);
    	Label explanation = new Label(parent, SWT.WRAP);
	    explanation.setText("code.quarkus.io will generate an application for you."
	            + " Select the project type according to your favorite build tool."
	            + " Then select the Quarkus dependencies you plan to use in your application.");
	    GridDataFactory.fillDefaults()
	        .span(2, 1)
	        .align(SWT.FILL, SWT.FILL)
	        .grab(true, false)
	        .applyTo(explanation);

	    //streams
	    Label lblStreams = new Label(parent, SWT.NONE);
	    lblStreams.setText("Quarkus stream:");
	    lblStreams.setToolTipText("The Quarkus stream the generated project will be based on.");
	    GridDataFactory.fillDefaults()
	        .align(SWT.LEFT, SWT.CENTER)
	        .applyTo(lblStreams);
	    Combo comboStreams = new Combo(parent, SWT.SINGLE | SWT.DROP_DOWN | SWT.READ_ONLY);
	    GridDataFactory.fillDefaults()
	        .align(SWT.LEFT, SWT.CENTER)
	        .hint(300, SWT.DEFAULT)
	    	.align(SWT.LEFT, SWT.CENTER)
	        .applyTo(comboStreams);
	    ComboViewer comboStreamsViewer = new ComboViewer(comboStreams);
	    comboStreamsViewer.setContentProvider(new ObservableListContentProvider<>());
	    comboStreamsViewer.setInput(BeanProperties.value(MODEL_PROPERTY).list("streams").observe(model));
	    comboStreamsViewer.setLabelProvider(new LabelProvider() {
	        @Override
	        public String getText(Object element) {
	            QuarkusStream stream = (QuarkusStream) element;
	            return stream.getPlatformVersion() + " " + stream.getStatus();
	        }
	    });
	    model.addPropertyChangeListener(MODEL_PROPERTY, e -> {
	    	comboStreamsViewer.getControl().getDisplay().asyncExec(() -> comboStreamsViewer.setSelection(new StructuredSelection(model.getModel().getStreams().stream().filter(QuarkusStream::isRecommended).findFirst().orElse(model.getModel().getStreams().get(0)))));
	    });
	    comboStreamsViewer.addSelectionChangedListener(e -> {
	    	loadExtensionsModel((QuarkusStream)e.getStructuredSelection().getFirstElement());
	    });

	    //project type
	    Label lblProjectType = new Label(parent, SWT.NONE);
	    lblProjectType.setText("Project type:");
	    lblProjectType.setToolTipText("The build tool used for the generated project.");
	    GridDataFactory.fillDefaults()
	        .align(SWT.LEFT, SWT.CENTER)
	        .applyTo(lblProjectType);
	    Combo comboProjectType = new Combo(parent, SWT.SINGLE | SWT.DROP_DOWN | SWT.READ_ONLY);
	    GridDataFactory.fillDefaults()
	        .align(SWT.LEFT, SWT.CENTER)
	        .hint(300, SWT.DEFAULT)
	    	.align(SWT.LEFT, SWT.CENTER)
	        .applyTo(comboProjectType);
	    ComboViewer comboProjectTypeViewer = new ComboViewer(comboProjectType);
	    comboProjectTypeViewer.setContentProvider(ArrayContentProvider.getInstance());
	    comboProjectTypeViewer.setInput(Tool.values());
	    comboProjectTypeViewer.setLabelProvider(new LabelProvider() {
	        @Override
	        public String getText(Object element) {
	            Tool tool = (Tool) element;
	            return tool.getLabel();
	        }
	    });
	    IViewerObservableValue projectTypeObservable = ViewerProperties
	            .singleSelection().observe(comboProjectTypeViewer);
	    ValueBindingBuilder
	    .bind(projectTypeObservable)
	    .to(BeanProperties.value(TOOL_PROPERTY).observe(model))
	    .in(dbc).init(dbc);
	    
	    // separator
	    Label mavenSeparator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
	    GridDataFactory.fillDefaults()
	        .span(2,1)
	        .align(SWT.FILL, SWT.CENTER)
	        .applyTo(mavenSeparator);
	
	    // project name
	    ISWTObservableValue<String> projectNameObservable = createTextWidget(parent, model, dbc, "Project name:", PROJECT_NAME_PROPERTY,
	            new EclipseProjectValidator("Please specify an Eclipse project", "Project already exists"));
	    //use default location
	    Button buttonUseDefaultLocation = new Button(parent, SWT.CHECK);
	    buttonUseDefaultLocation.setText("Use default location");
	    GridDataFactory.fillDefaults()
	        .span(2, 1)
	        .align(SWT.LEFT, SWT.CENTER)
	        .applyTo(buttonUseDefaultLocation);
	    IObservableValue<Boolean> useDefaultLocationButtonObservable = WidgetProperties.buttonSelection().observe(buttonUseDefaultLocation);
	    ValueBindingBuilder.bind(useDefaultLocationButtonObservable)
	            .to(BeanProperties.value(USE_DEFAULT_LOCATION_PROPERTY).observe(model))
	            .in(dbc);
	
	    // location
	    Label lblLocation = new Label(parent, SWT.NONE);
	    lblLocation.setText("Location:");
	    GridDataFactory.fillDefaults()
	        .align(SWT.LEFT, SWT.CENTER)
	        .applyTo(lblLocation);
	
	    Text txtLocation = new Text(parent, SWT.BORDER);
	    GridDataFactory.fillDefaults()
	        .align(SWT.FILL, SWT.FILL)
	        .grab(true, false)
	    	.applyTo(txtLocation);
	    ISWTObservableValue<String> txtLocationObservable = WidgetProperties.text(SWT.Modify).observe(txtLocation);
	    Binding locationBinding = ValueBindingBuilder.bind(txtLocationObservable)
	            .validatingAfterGet(new MandatoryStringValidator("Please specify a location for you project"))
	            .converting(IConverter.create(String.class, IPath.class, CodeProjectTypeWizardPage::string2IPath))
	            .to(BeanProperties.value(LOCATION_PROPERTY).observe(model)).in(dbc);
	    ValueBindingBuilder.bind(WidgetProperties.enabled().observe(txtLocation))
	            .notUpdatingParticipant()
	            .to(BeanProperties.value(USE_DEFAULT_LOCATION_PROPERTY).observe(model))
	            .converting(new InvertingBooleanConverter()).in(dbc);
	    ControlDecorationSupport.create(locationBinding, SWT.LEFT | SWT.TOP, null, new RequiredControlDecorationUpdater());
	    
	    dbc.addValidationStatusProvider(new MultiValidator() {
			
			@Override
			protected IStatus validate() {
			    Tool tool = (Tool) projectTypeObservable.getValue();
			    String projectName = projectNameObservable.getValue();
			    String location = txtLocationObservable.getValue();
			    File   locationFile = new File(location);
			    
			    if (Tool.GRADLE.equals(tool)) {
			    	if (isDirectChildOfWorkspaceRootFolder(location) && !projectName.equals(locationFile.getName())) {
				    	return ValidationStatus.error("Gradle projects at the root of the workspace must be using a folder with the same name");
				    }
			    }
				return ValidationStatus.ok();
			}
		});
    
    	loadModel();
    }
    
    private boolean isDirectChildOfWorkspaceRootFolder(String location) {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IPath rootLocationPath = workspace.getRoot().getLocation();
        IPath locationPath = Path.fromOSString(location);
        return rootLocationPath.equals(locationPath) || rootLocationPath.equals(locationPath.removeLastSegments(1));
    }
    
    private void loadModel() {
        try {
            WizardUtils.runInWizard(Job.create("Loading Quarkus code catalog", 
                    (ICoreRunnable) monitor -> model.setModel(QuarkusModelRegistry.getDefault().getModel(monitor))), getContainer());
        } catch (InvocationTargetException | InterruptedException e) {
            // ignore
        }
    }
    
    private void loadExtensionsModel(QuarkusStream stream) {
    	try {
			WizardUtils.runInWizard(Job.create("Loading extensions for stream " + stream.getPlatformVersion(),
					(ICoreRunnable) monitor -> model.setExtensionsModel(model.getModel().getExtensionsModel(stream.getKey(), monitor))), getContainer());
		} catch (InvocationTargetException | InterruptedException e) {
		}
    }

    
    static ISWTObservableValue<String> createTextWidget(Composite parent, CodeProjectModel model, DataBindingContext dbc, String label, String property, IValidator<String> validator) {
        Label lbl = new Label(parent, SWT.NONE);
        lbl.setText(label);
        GridDataFactory.fillDefaults()
            .align(SWT.LEFT, SWT.CENTER)
            .applyTo(lbl);

        Text text = new Text(parent, SWT.BORDER);
        GridDataFactory.fillDefaults()
            .align(SWT.FILL, SWT.CENTER)
            .grab(true, false)
            .applyTo(text);
        ISWTObservableValue<String> textObservable = WidgetProperties.text(SWT.Modify).observe(text);
        Binding binding = ValueBindingBuilder.bind(textObservable)
                .validatingAfterConvert(validator)
                .to(BeanProperties.value(property).observe(model)).in(dbc);
        ControlDecorationSupport.create(binding, SWT.LEFT | SWT.TOP, null, new RequiredControlDecorationUpdater());
        return textObservable;
    }
    
    private static IPath string2IPath(String str) {
        return Path.fromOSString(str);
    }
}
