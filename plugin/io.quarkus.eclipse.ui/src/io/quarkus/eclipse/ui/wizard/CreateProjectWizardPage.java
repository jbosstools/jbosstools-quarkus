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

package io.quarkus.eclipse.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import io.quarkus.eclipse.core.ProjectUtils;

public class CreateProjectWizardPage extends WizardPage {
	
	private Text groupText, artefactText, versionText, nameText = null;

	public CreateProjectWizardPage() {
		super("Create New Quarkus Project");
        setTitle("Quarkus Project");
        setDescription("Create a new Quarkus project resource.");
	}
	
	public String getGroupId() {
		return groupText.getText();
	}
	
	public String getArtefactId() {
		return artefactText.getText();
	}
	
	public String getVersion() {
		return versionText.getText();
	}
	
	public String getName() {
		return nameText.getText();
	}

	@Override
	public void createControl(Composite parent) {
	    Composite container = createContainer(parent);
	    createLabel(container, "Group ID : ");
	    groupText = createText(container, "com.acme");
	    createLabel(container, "Artifact ID : ");
	    artefactText = createText(container, "quarkus");
	    createLabel(container, "Version");
	    versionText = createText(container, "1.0.0-SHAPSHOT");
	    createLabel(container, "Name");
	    nameText = createText(container, "com.acme.quarkus");
	    nameText.addModifyListener(new ModifyListener() {		
			@Override
			public void modifyText(ModifyEvent e) {
				setPageComplete(checkPageComplete());
			}
		});
	    setControl(container);
	    setPageComplete(checkPageComplete());
	}
	
	private GridData createGridData() {
	    GridData gridData = new GridData();
	    gridData.horizontalAlignment = GridData.FILL;
	    gridData.grabExcessHorizontalSpace = true;		
	    return gridData;
	}
	
	private Composite createContainer(Composite parent) {
	    Composite container = new Composite(parent, SWT.NONE);
	    GridLayout layout = new GridLayout();
	    container.setLayout(layout);
	    layout.numColumns = 2;
	    return container;
	}
	
	private Label createLabel(Composite container, String text) {
	    Label label = new Label(container, SWT.NONE);
	    label.setText(text);
	    return label;
	}
	
	private Text createText(Composite container, String initialContents) {
		Text text = new Text(container, SWT.BORDER | SWT.SINGLE);
		text.setLayoutData(createGridData());
		text.setText(initialContents);
		return text;
	}
	
	private boolean checkPageComplete() {
		return !ProjectUtils.projectExists(nameText.getText());
	}

}
