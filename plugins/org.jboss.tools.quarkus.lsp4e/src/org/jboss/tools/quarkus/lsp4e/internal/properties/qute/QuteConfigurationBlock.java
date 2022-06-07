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

import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.internal.ui.preferences.OptionsConfigurationBlock;
import org.eclipse.jdt.internal.ui.wizards.IStatusChangeListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IListAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ListDialogField;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.jboss.tools.quarkus.lsp4e.QuarkusLSPPlugin;

/**
 * @author Red Hat Developers
 *
 */
public class QuteConfigurationBlock extends OptionsConfigurationBlock {
	
	private static final int NEW_BUTTON_INDEX = 0;
	
	private static final int EDIT_BUTTON_IDEX = 1;
	
	private static final int REMOVE_BUTTON_IDX = 2;
	
	public class ExcludeAdapter implements IListAdapter<String>, IDialogFieldListener {

		@Override
		public void dialogFieldChanged(DialogField field) {
			if (excludeList.getElements().isEmpty()) {
				setValue(QuarkusLSPPlugin.EXCLUDE_KEY, null);
				
			} else {
				setValue(QuarkusLSPPlugin.EXCLUDE_KEY, String.join(",", excludeList.getElements()));
			}
		}

		@Override
		public void customButtonPressed(ListDialogField<String> field, int index) {
			processButton(field, index);
		}

		@Override
		public void selectionChanged(ListDialogField<String> field) {
				field.enableButton(EDIT_BUTTON_IDEX, !field.getSelectedElements().isEmpty());
		}

		@Override
		public void doubleClicked(ListDialogField<String> field) {
			processButton(field, EDIT_BUTTON_IDEX);
		}
	}

	private static final Key[] ALL_KEYS = new Key[] { QuarkusLSPPlugin.ENABLE_KEY, QuarkusLSPPlugin.EXCLUDE_KEY };
	
	private static final String[] enableDisableValues = new String[] {QuarkusLSPPlugin.ENABLED, QuarkusLSPPlugin.DISABLED};
	
	private Button enableCheckBox;

	private ListDialogField<String> excludeList;

	/**
	 * @param context
	 * @param project
	 * @param allKeys
	 * @param container
	 */
	public QuteConfigurationBlock(IStatusChangeListener context, IProject project,
			IWorkbenchPreferenceContainer container) {
		super(context, project, ALL_KEYS, container);
	}
	
	/**
	 * @param field
	 * @param index
	 */
	public void processButton(ListDialogField<String> field, int index) {
		if (index == NEW_BUTTON_INDEX) {
			excludeList.addElement("src/main/resources/templates/*.html");
		} else {
			String element = excludeList.getSelectedElements().get(0);
			if (index == EDIT_BUTTON_IDEX) {
				InputDialog dialog = new InputDialog(getShell(), "Edit exclude pattern", "", element, null);
				if (dialog.open() == Window.OK) {
					excludeList.replaceElement(element, dialog.getValue());
				}
		} else {
			excludeList.removeElement(element);
		}
		}
	}

	protected Button addEnableField(Composite composite) {
		Button checkBox = addCheckBox(composite, "Enable validation", QuarkusLSPPlugin.ENABLE_KEY, enableDisableValues, 0);
		checkBox.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				updateMainPreferenceContent();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		return checkBox;
	}
	
	protected void updateMainPreferenceContent() {
	}


	@Override
	protected Control createContents(Composite parent) {
		setShell(parent.getShell());

		enableCheckBox = addEnableField(parent);
		addExcludeList(parent);
		return parent;
	}

	/**
	 * @param mainComp
	 */
	private void addExcludeList(Composite mainComp) {
		String[] buttons = new String[] { "New", "Edit", "Remove" };
		
		ExcludeAdapter adapter = new ExcludeAdapter();

		excludeList = new ListDialogField<>(adapter, buttons, new LabelProvider());
		excludeList.setDialogFieldListener(adapter);
		excludeList.setRemoveButtonIndex(2);
		excludeList.setLabelText("Exclude from validation:");
		createExcludeContent(mainComp);
		String excludes = getStoredValue(QuarkusLSPPlugin.EXCLUDE_KEY);
		if (excludes != null && excludes.length() > 0) {
			excludeList.setElements(Arrays.asList(excludes.split(",")));
		}
	}
	
	private Composite createExcludeContent(Composite folder) {
		GridLayout layout= new GridLayout();
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		layout.numColumns= 2;
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;

		PixelConverter conv= new PixelConverter(folder);

		Composite excludeComposite = new Composite(folder, SWT.NULL);
		excludeComposite.setLayout(layout);
		excludeComposite.setLayoutData(gd);
		excludeComposite.setFont(folder.getFont());

		GridData data= new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		Control labelControl = excludeList.getLabelControl(excludeComposite);
		labelControl.setLayoutData(data);
		
		data= new GridData(GridData.FILL_BOTH);
		data.widthHint= conv.convertWidthInCharsToPixels(50);
		Control listControl= excludeList.getListControl(excludeComposite);
		listControl.setLayoutData(data);

		Control buttonsControl= excludeList.getButtonBox(excludeComposite);
		buttonsControl.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));

		return excludeComposite;
	}


	@Override
	protected void validateSettings(Key changedKey, String oldValue, String newValue) {
	}

	@Override
	protected String[] getFullBuildDialogStrings(boolean workspaceSettings) {
		return null;
	}
}
