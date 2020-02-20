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

import static org.jboss.tools.quarkus.ui.wizard.CodeProjectModel.CATEGORIES_PROPERTY;
import static org.jboss.tools.quarkus.ui.wizard.CodeProjectModel.EXTENSIONS_PROPERTY;
import static org.jboss.tools.quarkus.ui.wizard.CodeProjectModel.SELECTED_CATEGORY_PROPERTY;
import static org.jboss.tools.quarkus.ui.wizard.CodeProjectModel.SELECTED_EXTENSIONS_PROPERTY;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.typed.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableSetContentProvider;
import org.eclipse.jface.databinding.viewers.typed.ViewerProperties;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.common.ui.databinding.ValueBindingBuilder;
import org.jboss.tools.common.ui.wizard.AbstractDataBindingWizardPage;
import org.jboss.tools.quarkus.core.code.model.QuarkusCategory;
import org.jboss.tools.quarkus.core.code.model.QuarkusExtension;

public class CodeProjectExtensionsWizardPage extends AbstractDataBindingWizardPage {

	private final CodeProjectModel model;

	protected CodeProjectExtensionsWizardPage(IWizard wizard, CodeProjectModel model) {
		super("Quarkus extensions", "Select the Quarkus extensions for your project", "third", wizard, null);
		this.model = model;
		setImageDescriptor(CodeProjectWizard.QUARKUS_LOGO);
	}

	@Override
	protected void doCreateControls(Composite parent, DataBindingContext dbc) {
		GridLayoutFactory.fillDefaults().margins(6, 6).numColumns(3).equalWidth(true).applyTo(parent);

		// explanation
		Label explanation = new Label(parent, SWT.WRAP);
		explanation.setText("Clicking on a category will display the extensions in the middle column."
				+ " Double clicking on an extension will add/remove the extension from the selected extensions list."
				+ " The current selected extensions are displayed in the third column.");
		GridDataFactory.fillDefaults().span(3, 1).align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(explanation);

		// categories
		Composite categoriesContainer = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(categoriesContainer);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).applyTo(categoriesContainer);

		Label lblCategories = new Label(categoriesContainer, SWT.WRAP);
		lblCategories.setText("Categories");
		GridDataFactory.fillDefaults().span(2, 1).align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(lblCategories);

		List listCategories = new List(categoriesContainer, SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.READ_ONLY);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(listCategories);
		ListViewer listCategoriesViewer = new ListViewer(listCategories);
		listCategoriesViewer.setContentProvider(new ObservableListContentProvider());
		listCategoriesViewer.setInput(BeanProperties.list(CATEGORIES_PROPERTY).observe(model));
		listCategoriesViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				QuarkusCategory category = (QuarkusCategory) element;
				return category.getName();
			}
		});
		IObservableValue selectedCategoryObservable = BeanProperties.value(SELECTED_CATEGORY_PROPERTY).observe(model);
		ValueBindingBuilder.bind(ViewerProperties.singleSelection().observe(listCategoriesViewer))
				.to(selectedCategoryObservable).in(dbc);

		// extensions
		Composite extensionsContainer = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(extensionsContainer);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).applyTo(extensionsContainer);

		Label lblExtensions = new Label(extensionsContainer, SWT.WRAP);
		lblExtensions.setText("Extensions");
		GridDataFactory.fillDefaults().span(2, 1).align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(lblExtensions);

		List listExtensions = new List(extensionsContainer, SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.READ_ONLY);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(listExtensions);
		ListViewer listExtensionsViewer = new ListViewer(listExtensions);
		listExtensionsViewer.setContentProvider(new ObservableListContentProvider());
		listExtensionsViewer.setInput(BeanProperties.list(EXTENSIONS_PROPERTY).observe(model));
		listExtensionsViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				QuarkusExtension extension = (QuarkusExtension) element;
				return extension.asLabel();
			}
		});
		listExtensionsViewer.addDoubleClickListener(e -> {
			QuarkusExtension extension = model.getExtensions()
					.get(((ListViewer) e.getSource()).getList().getSelectionIndex());
			model.toggleSelectedExtension(extension);
		});

		// selected extensions
		Composite selectedExtensionsContainer = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(selectedExtensionsContainer);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).applyTo(selectedExtensionsContainer);

		Label lblselectedExtensions = new Label(selectedExtensionsContainer, SWT.WRAP);
		lblselectedExtensions.setText("Selected");
		GridDataFactory.fillDefaults().span(2, 1).align(SWT.FILL, SWT.FILL).grab(true, false)
				.applyTo(lblselectedExtensions);

		List listSelectedExtensions = new List(selectedExtensionsContainer,
				SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.READ_ONLY);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(listSelectedExtensions);
		ListViewer listSelectedExtensionsViewer = new ListViewer(listSelectedExtensions);
		listSelectedExtensionsViewer.setContentProvider(new ObservableSetContentProvider());
		listSelectedExtensionsViewer.setInput(BeanProperties.set(SELECTED_EXTENSIONS_PROPERTY).observe(model));
		listSelectedExtensionsViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				QuarkusExtension extension = (QuarkusExtension) element;
				return extension.asLabel();
			}
		});
		listSelectedExtensionsViewer.addDoubleClickListener(e -> {
			ListViewer viewer = (ListViewer) e.getSource();
			QuarkusExtension extension = (QuarkusExtension) viewer.getElementAt(viewer.getList().getSelectionIndex());
			model.toggleSelectedExtension(extension);
		});
	}

	@Override
	protected void onPageActivated(DataBindingContext dbc) {
		setToPreferredVerticalSize(getShell());
	}

	private void setToPreferredVerticalSize(Shell shell) {
		Point size = shell.computeSize(900, SWT.DEFAULT);
		// windows doesn't take progress bar area into account, maven section gets cut off.
		// size.y += 20;
		shell.setSize(size);
	}

}
