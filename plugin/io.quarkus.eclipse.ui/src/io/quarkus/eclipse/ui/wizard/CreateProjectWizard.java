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

import java.net.URL;
import java.util.HashMap;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import io.quarkus.eclipse.ui.action.CreateProjectAction;

public class CreateProjectWizard extends Wizard implements INewWizard {
	
	private CreateProjectWizardPage createProjectWizardPage = null;
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("New Quarkus Project");
		setDefaultPageImageDescriptor(createImageDescriptor());
	}

	@Override
	public boolean performFinish() {		
		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put("path", createProjectWizardPage.getPath());
		Runnable createProjectAction = new CreateProjectAction(
					createProjectWizardPage.getName(),
					createProjectWizardPage.getLocation(),
					createProjectWizardPage.getGroupId(), 
					createProjectWizardPage.getArtefactId(), 
					createProjectWizardPage.getVersion(), 
					createProjectWizardPage.getClassName(),
					context);
		Display.getDefault().asyncExec(createProjectAction);	
		return true;
	}

	@Override
	public void addPages() {
		createProjectWizardPage = new CreateProjectWizardPage();
	    addPage(createProjectWizardPage);
	}
	
	@Override
	public boolean canFinish() {
		return createProjectWizardPage.isPageComplete();
	}
	
	private ImageDescriptor createImageDescriptor() {
		Bundle bundle = FrameworkUtil.getBundle(getClass());
		URL url = FileLocator.find(bundle, new Path("icon/quarkus-32.png"));
		return ImageDescriptor.createFromURL(url);
	}
	
}
