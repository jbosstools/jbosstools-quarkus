/*
 * Copyright 2020 Red Hat, Inc.
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
package org.jboss.tools.quarkus.ui.launch;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.debug.ui.launcher.AbstractJavaMainTab;
import org.eclipse.jdt.internal.debug.ui.launcher.LauncherMessages;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.quarkus.core.QuarkusCoreConstants;
import org.jboss.tools.quarkus.core.launch.LaunchUtils;
import org.jboss.tools.quarkus.core.project.ProjectUtils;

public class QuarkusProjectTab extends AbstractJavaMainTab {
  private Text profileText;

	@Override
	public void createControl(Composite parent) {
		Composite comp = SWTFactory.createComposite(parent, parent.getFont(), 1, 1, GridData.FILL_BOTH);
		((GridLayout)comp.getLayout()).verticalSpacing = 0;
		createProjectEditor(comp);
		createProfileEditor(comp);
		setControl(comp);
	}

	/**
   * @param comp
   */
  private void createProfileEditor(Composite comp) {
    Group group = SWTFactory.createGroup(comp, "Profile", 1, 1, GridData.FILL_HORIZONTAL);
    profileText = SWTFactory.createSingleText(group, 1);
    profileText.addModifyListener(e -> updateLaunchConfigurationDialog());
  }

  /* (non-Javadoc)
	 * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#isValid(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	@Override
	public boolean isValid(ILaunchConfiguration config) {
		setErrorMessage(null);
		setMessage(null);
		String name = fProjText.getText().trim();
		if (name.length() > 0) {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IStatus status = workspace.validateName(name, IResource.PROJECT);
			if (status.isOK()) {
				IProject project= ResourcesPlugin.getWorkspace().getRoot().getProject(name);
				if (!project.exists()) {
					setErrorMessage(NLS.bind(LauncherMessages.JavaMainTab_20, new String[] {name}));
					return false;
				}
				if (!project.isOpen()) {
					setErrorMessage(NLS.bind(LauncherMessages.JavaMainTab_21, new String[] {name}));
					return false;
				}
				if (!ProjectUtils.isJavaProject(project)) {
					setErrorMessage("Not a Java project");
					return false;
				}
				if (!ProjectUtils.isQuarkusProject(project)) {
					setErrorMessage("Not a Quarkus project");
					return false;
				}
			}
			else {
				setErrorMessage(NLS.bind(LauncherMessages.JavaMainTab_19, new String[]{status.getMessage()}));
				return false;
			}
		} else {
			setErrorMessage(LauncherMessages.JavaMainTab_missing_project);
			return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		IJavaElement javaElement = getContext();
		if (javaElement != null && ProjectUtils.isQuarkusProject(javaElement.getJavaProject())) {
			initializeJavaProject(javaElement, config);
		}
		else {
			config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, EMPTY_STRING);
		}
		config.setAttribute(DebugPlugin.ATTR_PROCESS_FACTORY_ID, QuarkusCoreConstants.QUARKUS_PROCESS_FACTORY);
		config.setAttribute(QuarkusCoreConstants.ATTR_PROFILE_NAME, "");
	}

	@Override
  public void initializeFrom(ILaunchConfiguration config) {
    super.initializeFrom(config);
    String profileName = "";
    try {
      profileName = config.getAttribute(QuarkusCoreConstants.ATTR_PROFILE_NAME, "");
    }
    catch (CoreException ce) {
      setErrorMessage(ce.getStatus().getMessage());
    }
    profileText.setText(profileName);
  }

  @Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		String projectName = fProjText.getText().trim();
		configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName);
		String profileName = profileText.getText().trim();
		configuration.setAttribute(QuarkusCoreConstants.ATTR_PROFILE_NAME, profileName);
		try {
			if (!projectName.isEmpty()) {
				LaunchUtils.initializeQuarkusLaunchConfiguration(configuration);
			}
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getName() {
		return "Project";
	}

}
