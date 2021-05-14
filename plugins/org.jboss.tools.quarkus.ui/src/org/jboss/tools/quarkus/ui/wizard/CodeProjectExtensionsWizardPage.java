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

import static org.jboss.tools.quarkus.ui.wizard.CodeProjectModel.CATEGORIES_PROPERTY;
import static org.jboss.tools.quarkus.ui.wizard.CodeProjectModel.EXTENSIONS_PROPERTY;
import static org.jboss.tools.quarkus.ui.wizard.CodeProjectModel.SELECTED_CATEGORY_PROPERTY;
import static org.jboss.tools.quarkus.ui.wizard.CodeProjectModel.SELECTED_EXTENSIONS_PROPERTY;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.typed.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableSetContentProvider;
import org.eclipse.jface.databinding.viewers.typed.ViewerProperties;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;
import org.jboss.tools.common.ui.databinding.ValueBindingBuilder;
import org.jboss.tools.common.ui.wizard.AbstractDataBindingWizardPage;
import org.jboss.tools.foundation.ui.util.BrowserUtility;
import org.jboss.tools.quarkus.core.code.model.QuarkusCategory;
import org.jboss.tools.quarkus.core.code.model.QuarkusExtension;
import org.jboss.tools.quarkus.ui.QuarkusUIPlugin;

public class CodeProjectExtensionsWizardPage extends AbstractDataBindingWizardPage {

	private final CodeProjectModel model;
	
	private static final class ExtensionStyledCellLabelProvider extends StyledCellLabelProvider {

    @Override
    public void update(ViewerCell cell) {
      QuarkusExtension extension = (QuarkusExtension) cell.getElement();
      cell.setText(extension.asLabel());
      if (extension.isProvidesExampleCode()) {
        cell.setImage(CodeProjectWizard.CODESTARTS_EXTENSION_ICON.createImage());
      }
      super.update(cell);
    }
	}

    protected CodeProjectExtensionsWizardPage(IWizard wizard, CodeProjectModel model) {
        super("Quarkus Extensions", "Select the Quarkus extensions for your project", "third", wizard, null);
        this.model = model;
        setImageDescriptor(CodeProjectWizard.QUARKUS_LOGO);
    }

    @Override
    protected void doCreateControls(Composite parent, DataBindingContext dbc) {
    	GridLayoutFactory.fillDefaults()
    		.margins(6, 6)
        	.applyTo(parent);

    	//  explanation
    	Label explanation = new Label(parent, SWT.WRAP);
    	explanation.setText("Clicking on a category will display the extensions in the middle column."
    			+ " Double clicking on an extension will add/remove the extension from the selected extensions list."
    			+ " The current selected extensions are displayed in the third column.");
    	GridDataFactory.fillDefaults()
    		.align(SWT.FILL, SWT.FILL)
    		.grab(true, false)
    		.applyTo(explanation);

    	SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
    	GridDataFactory.fillDefaults()
			.align(SWT.FILL, SWT.FILL)
			.grab(true, true)
			.applyTo(sashForm);
    	
    	// categories
    	List categoriesList = (List) createColumn("Extension Categories:", sashForm, true);
        ListViewer listCategoriesViewer = new ListViewer(categoriesList);
        listCategoriesViewer.setContentProvider(new ObservableListContentProvider<QuarkusCategory>());
        listCategoriesViewer.setInput(BeanProperties.list(CATEGORIES_PROPERTY).observe(model));
        listCategoriesViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                QuarkusCategory category = (QuarkusCategory) element;
                return category.getName();
            }
        });
		IObservableValue<QuarkusCategory> selectedCategoryObservable = BeanProperties
				.value(null, SELECTED_CATEGORY_PROPERTY, QuarkusCategory.class)
				.observe(model);
        ValueBindingBuilder.bind(ViewerProperties.singleSelection().observe(listCategoriesViewer))
                .to(selectedCategoryObservable)
                .in(dbc);

        // extensions
    	Table extensionsTable = (Table) createColumn("Available Extensions:", sashForm, false);
    	TableViewer tableExtensionsViewer = getExtensionViewer(extensionsTable);
        tableExtensionsViewer.addDoubleClickListener(e -> {
        	QuarkusExtension extension = model.getExtensions().get(((TableViewer)e.getSource()).getTable().getSelectionIndex());
			model.toggleSelectedExtension(extension);
        });
        tableExtensionsViewer.setContentProvider(new ObservableListContentProvider<QuarkusExtension>());
        tableExtensionsViewer.setInput(BeanProperties.list(EXTENSIONS_PROPERTY).observe(model));

        // selected extensions
    	Table selectedExtensionsTable = (Table) createColumn("Selected Extensions:", sashForm, false);
        TableViewer tableSelectedExtensionsViewer = getExtensionViewer(selectedExtensionsTable);
        tableSelectedExtensionsViewer.setContentProvider(new ObservableSetContentProvider<>());
        tableSelectedExtensionsViewer.setInput(BeanProperties.set(SELECTED_EXTENSIONS_PROPERTY).observe(model));
        tableSelectedExtensionsViewer.addDoubleClickListener(e -> {
        	TableViewer viewer = (TableViewer) e.getSource();
        	QuarkusExtension extension = (QuarkusExtension) viewer.getElementAt(viewer.getTable().getSelectionIndex());
			model.toggleSelectedExtension(extension);
        });
        
    	sashForm.setWeights(20, 40, 40);

        // selected extension detail
    	Label detailsSeparator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
    	GridDataFactory.fillDefaults()
			.align(SWT.FILL, SWT.FILL)
			.grab(true, false)
			.applyTo(detailsSeparator);
    	Composite container = new Composite(parent, SWT.NONE);
    	GridDataFactory.fillDefaults()
			.align(SWT.FILL, SWT.FILL)
			.grab(true, false)
			.applyTo(container);
    	GridLayoutFactory.fillDefaults()
    		.numColumns(2)
    		.applyTo(container);
    	Label detailLabel = new Label(container, SWT.NONE);
    	detailLabel.setText("Extension Details:");
    	GridDataFactory.fillDefaults()
			.align(SWT.LEFT, SWT.FILL)
			.applyTo(detailLabel);
    	Link detail = new Link(container, SWT.WRAP);
    	GridDataFactory.fillDefaults()
    		.align(SWT.FILL, SWT.FILL)
    		.grab(true, true)
    		.applyTo(detail);
    	
        ISWTObservableValue<String> extensionDetailObservable = WidgetProperties.text().observe(detail);
        ValueBindingBuilder
        	.bind(ViewerProperties.singleSelection().observe(tableExtensionsViewer))
        	.converting(new QuarkusExtension2StringConverter())
            .to(extensionDetailObservable)
            .in(dbc);
        ValueBindingBuilder
        	.bind(ViewerProperties.singleSelection().observe(tableExtensionsViewer))
        	.to(BeanProperties.value("data").observe(detail))
        	.notUpdatingParticipant()
        	.in(dbc);
        detail.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> openBrowser((QuarkusExtension) detail.getData())));
    }

    public TableViewer getExtensionViewer(Table table) {
      TableColumnLayout layout = new TableColumnLayout();
      table.getParent().setLayout(layout);
      TableViewer viewer = new TableViewer(table);
      TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
      col.setLabelProvider(new ExtensionStyledCellLabelProvider());
      layout.setColumnData(col.getColumn(), new ColumnWeightData(100));
      return viewer;
    }

	private Control createColumn(String title, Composite parent, boolean list) {
		Composite container = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults()
			.align(SWT.FILL, SWT.FILL)
			.grab(true, true)
			.applyTo(container);
		GridLayoutFactory.fillDefaults()
			.margins(4, 0)
			.applyTo(container);

        Label label = new Label(container, SWT.WRAP);
        label.setText(title);
        GridDataFactory.fillDefaults()
        	.grab(true, false)
        	.align(SWT.FILL, SWT.FILL)
        	.applyTo(label);

        // TableLayout requires a single parent
        Control control;
        if (list) {
          control = new List(container, SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.READ_ONLY);
        } else {
          Composite parentControl = new Composite(container, SWT.NONE);
          GridDataFactory.fillDefaults()
          .grab(true, true)
          .align(SWT.FILL, SWT.FILL)
          .applyTo(parentControl);
          control = new Table(parentControl, SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.READ_ONLY);
          ((Table)control).setHeaderVisible(false);
          ((Table)control).setLinesVisible(false);
        }

        GridDataFactory.fillDefaults()
	    	.grab(true, true)
	    	.applyTo(control);
		return control;
	}

	private void openBrowser(QuarkusExtension extension) {
		if (extension != null) {
			new BrowserUtility().checkedCreateExternalBrowser(extension.getGuide(), QuarkusUIPlugin.PLUGIN_ID, QuarkusUIPlugin.getDefault().getLog());
		}
	}
}
