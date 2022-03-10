/*******************************************************************************
 * Copyright (c) 2022 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.quarkus.lsp4e.internal.properties.qute;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.internal.ui.preferences.PropertyAndPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.jboss.tools.quarkus.lsp4e.QuarkusLSPPlugin;

public class QutePropertyPage extends PropertyAndPreferencePage {

	/**
	 * 
	 */
	private static final String PROPERTY_ID = "org.jboss.tools.quarkus.lsp4e.properties.qute.QutePropertyPage";

	/**
	 * 
	 */
	private static final String PREFERENCE_ID = "org.jboss.tools.quarkus.lsp4e.preferences.qute.QutePropertyPage";
	
	private QuteConfigurationBlock configurationBlock;

	/**
	 * Constructor for SamplePropertyPage.
	 */
	public QutePropertyPage() {
		setPreferenceStore(QuarkusLSPPlugin.getDefault().getPreferenceStore());
		setTitle("Qute");
	}


	@Override
	public void createControl(Composite parent) {
		IWorkbenchPreferenceContainer container = (IWorkbenchPreferenceContainer) getContainer();
		configurationBlock = new QuteConfigurationBlock(getNewStatusChangedListener(), getProject(), container);

		super.createControl(parent);
	}

	/*
	 * @see org.eclipse.jface.preference.IPreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		super.performDefaults();
		if (configurationBlock != null) {
			configurationBlock.performDefaults();
		}
	}

	/*
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		if (configurationBlock != null && !configurationBlock.performOk()) {
			return false;
		}
		return super.performOk();
	}

	/*
	 * @see org.eclipse.jface.preference.IPreferencePage#performApply()
	 */
	@Override
	public void performApply() {
		if (configurationBlock != null) {
			configurationBlock.performApply();
		}
	}

	@Override
	protected Control createPreferenceContent(Composite composite) {
		return configurationBlock.createContents(composite);
	}

	@Override
	protected boolean hasProjectSpecificOptions(IProject project) {
		return configurationBlock.hasProjectSpecificOptions(project);
	}

	@Override
	protected void enableProjectSpecificSettings(boolean useProjectSpecificSettings) {
		super.enableProjectSpecificSettings(useProjectSpecificSettings);
		if (configurationBlock != null) {
			configurationBlock.useProjectSpecificSettings(useProjectSpecificSettings);
		}
	}


	@Override
	protected String getPreferencePageID() {
		return PREFERENCE_ID;
	}

	@Override
	protected String getPropertyPageID() {
		return PROPERTY_ID;
	}

}