/*
 * Copyright 2019 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.tools.quarkus.ui.wizard;

import java.io.File;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import io.quarkus.eclipse.core.project.ProjectUtils;

public class CreateProjectWizardPage extends WizardPage {
	
	private Button browseButton = null;
	private Text locationText, groupIdText, artefactIdText, versionText, nameText, classNameText, pathText = null;

	public CreateProjectWizardPage() {
		super("Create New Quarkus Project");
        setTitle("Quarkus Project");
        setDescription("Create a new Quarkus project resource.");
	}
	
	public String getGroupId() {
		return groupIdText.getText();
	}
	
	public String getArtefactId() {
		return artefactIdText.getText();
	}
	
	public String getVersion() {
		return versionText.getText();
	}
	
	public String getName() {
		return nameText.getText();
	}
	
	public String getLocation() {
		return locationText.getText();
	}
	
	public String getClassName() {
		return classNameText.getText();
	}
	
	public String getPath() {
		return pathText.getText();
	}

	@Override
	public void createControl(Composite parent) {
	    Composite container = createContainer(parent);
	    createNameField(container);
	    createFillerField(container, true, false);
	    createMavenGroup(container);
	    createFillerField(container, true, false);
	    createUseDefaultWorkspaceLocationButton(container);
	    createLocationField(container);
	    createFillerField(container, true, false);
	    createRestInfoGroup(container);
	    createFillerField(container, true, true);
	    setControl(container);
	    setPageComplete(checkPageComplete());
	}
	
	private Composite createContainer(Composite parent) {
	    Composite container = new Composite(parent, SWT.NONE);
	    GridLayout layout = new GridLayout();
	    container.setLayout(layout);
	    layout.numColumns = 3;
	    return container;
	}
	
	private void createFillerField(
			Composite parent, 
			boolean horizontalFill, 
			boolean verticalFill) {
		Label fillerLabel = new Label(parent, SWT.NONE);
		fillerLabel.setLayoutData(new GridData(
				SWT.FILL, 
				SWT.TOP, 
				horizontalFill, 
				verticalFill, 
				3, 
				1));
	}
	
	private void createNameField(Composite parent) {
		Label nameLabel = new Label(parent, SWT.NONE);
		nameLabel.setText("Name:");
		nameText = new Text(parent, SWT.BORDER | SWT.SINGLE);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		nameText.setText("com.acme.quarkus");
		nameText.addModifyListener(new ModifyListener() {		
			@Override
			public void modifyText(ModifyEvent e) {
				if (!locationText.isEnabled()) {
					locationText.setText(getDefaultLocation());
				}
				checkPageComplete();
			}
		});
	}
	
	private void createMavenGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_OUT);
		group.setText("Maven Info");
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 3, 1));
		group.setLayout(new GridLayout(2, false));
		createGroupIdField(group);
		createArtefactIdField(group);
		createVersionField(group);
	}
	
	private void createGroupIdField(Composite parent) {
		Label groupIdLabel = new Label(parent, SWT.NONE);
		groupIdLabel.setText("Group ID:");
		groupIdText = new Text(parent, SWT.BORDER | SWT.SINGLE);
		groupIdText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		groupIdText.setText("com.acme");
	}
	
	private void createArtefactIdField(Composite parent) {
		Label artefactIdLabel = new Label(parent, SWT.NONE);
		artefactIdLabel.setText("Artifact ID:");
		artefactIdText = new Text(parent, SWT.BORDER | SWT.SINGLE);
		artefactIdText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		artefactIdText.setText("quarkus");
	}
	
	private void createVersionField(Composite parent) {
		Label versionLabel = new Label(parent, SWT.NONE);
		versionLabel.setText("Version:");
		versionText = new Text(parent, SWT.BORDER | SWT.SINGLE);
		versionText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		versionText.setText("1.0.0-SHAPSHOT");
	}
	
	private Button createUseDefaultWorkspaceLocationButton(Composite container) {
		Button button = new Button(container, SWT.CHECK);
	    GridData gridData = new GridData(SWT.FILL, SWT.TOP, false, false, 3, 1);
	    button.setLayoutData(gridData);
	    button.setText("Use default Workspace location");
	    button.setSelection(true);
	    button.addListener(SWT.Selection, new Listener() {			
			@Override
			public void handleEvent(Event event) {
				locationText.setEnabled(!button.getSelection());
				locationText.setText(button.getSelection() ? getDefaultLocation() : "");
				browseButton.setEnabled(!button.getSelection());
			}
		});
		return button;
	}
	
	private void createLocationField(Composite parent) {
		Label locationLabel = new Label(parent, SWT.NONE);
		locationLabel.setText("Location:");
		locationText = new Text(parent, SWT.BORDER | SWT.SINGLE);
		locationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		locationText.setText(getDefaultLocation());
		locationText.setEnabled(false);
		browseButton = new Button(parent, SWT.NONE);
		browseButton.setText("Browse...");
		browseButton.setEnabled(false);
		browseButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				String newLocation = new DirectoryDialog(getShell()).open();
				if (newLocation != null) {
					locationText.setText(newLocation);
				}
			}		
		});
	}
	
	private void createRestInfoGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_OUT);
		group.setText("REST Info");
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 3, 1));
		group.setLayout(new GridLayout(2, false));
		createClassNameField(group);
		createPathField(group);
	}
	
	private void createClassNameField(Composite parent) {
		Label classNameLabel = new Label(parent, SWT.NONE);
		classNameLabel.setText("Class Name:");
		classNameText = new Text(parent, SWT.BORDER | SWT.SINGLE);
		classNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		classNameText.setText("org.acme.quarkus.GreetingResource");
	}
	
	private void createPathField(Composite parent) {
		Label pathLabel = new Label(parent, SWT.NONE);
		pathLabel.setText("Path:");
		pathText = new Text(parent, SWT.BORDER | SWT.SINGLE);
		pathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		pathText.setText("/hello");
	}	
	
	private boolean checkPageComplete() {
		String name = nameText.getText();
		boolean projectExists = ProjectUtils.projectExists(name);		
		if (projectExists) {
			setMessage("A project with name '" + name + "' already exists.", ERROR);
		} else {
			setMessage("");
		}
		return !projectExists;
	}
	
	private String getDefaultLocation() {
		return ProjectUtils.getProjectLocationDefault() + File.separator + getName();
	}

}
