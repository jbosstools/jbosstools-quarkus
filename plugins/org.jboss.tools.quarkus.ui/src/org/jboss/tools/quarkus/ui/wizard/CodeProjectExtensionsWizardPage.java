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

import static org.jboss.tools.quarkus.ui.wizard.CodeProjectModel.SELECTED_EXTENSIONS_PROPERTY;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.typed.BeanProperties;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ObservableSetContentProvider;
import org.eclipse.jface.databinding.viewers.typed.ViewerProperties;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.jboss.tools.common.ui.databinding.ValueBindingBuilder;
import org.jboss.tools.common.ui.wizard.AbstractDataBindingWizardPage;
import org.jboss.tools.foundation.ui.util.BrowserUtility;
import org.jboss.tools.quarkus.core.code.model.QuarkusCategory;
import org.jboss.tools.quarkus.core.code.model.QuarkusExtension;
import org.jboss.tools.quarkus.core.code.model.QuarkusExtensionsModel;
import org.jboss.tools.quarkus.ui.QuarkusUIPlugin;

public class CodeProjectExtensionsWizardPage extends AbstractDataBindingWizardPage {

	private class TreeFilter extends ViewerFilter {
		
		private final Text text;
		private final Button button;

		private TreeFilter(Text text, Button button) {
			this.text = text;
			this.button = button;
		}

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof QuarkusCategory) {
				return true;
			}
			if (button.getSelection() && !((QuarkusExtension) element).isPlatform()) {
				return false;
			}
			if (StringUtils.isEmpty(text.getText())) {
				return true;
			}
			Pattern pattern = Pattern.compile(".*" + text.getText() + ".*", Pattern.CASE_INSENSITIVE);
			return pattern.matcher(((QuarkusExtension) element).getName()).matches();
		}

	}

	public class ExtensionsModelContentProvider implements ITreeContentProvider {

		@Override
		public Object[] getElements(Object inputElement) {
			java.util.List<Object> results = new ArrayList<>();
			if (inputElement instanceof QuarkusExtensionsModel) {
				results.addAll(((QuarkusExtensionsModel) inputElement).getCategories());
			} else if (inputElement instanceof QuarkusCategory) {
				results.addAll(((QuarkusCategory) inputElement).getExtensions());
			}
			return results.toArray(new Object[results.size()]);
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			return getElements(parentElement);
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return element instanceof QuarkusExtensionsModel || element instanceof QuarkusCategory;
		}
	}

	private static final int PREFERRED_WIDTH = 800;
	private static final int PREFFERRED_HEIGHT_EXTENSIONS = 400;

	private final CodeProjectModel model;

	private static final class ExtensionStyledCellLabelProvider extends StyledCellLabelProvider {

		@Override
		public void update(ViewerCell cell) {
			Object element = cell.getElement();
			if (element instanceof QuarkusExtension) {
				QuarkusExtension extension = (QuarkusExtension) element;
				cell.setText(extension.asLabel());
				if (extension.isPlatform()) {
					cell.setImage(CodeProjectWizard.PLATFORM_EXTENSION_ICON);
				}
			} else if (element instanceof QuarkusCategory) {
				cell.setText(((QuarkusCategory) element).getName());
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
		GridDataFactory.fillDefaults()
			.align(SWT.FILL, SWT.FILL)
			.grab(true, true)
			.hint(PREFERRED_WIDTH, SWT.DEFAULT)
			.applyTo(parent);
		GridLayoutFactory.fillDefaults().margins(6, 6).applyTo(parent);

		// explanation
		Label explanation = new Label(parent, SWT.WRAP);
		explanation.setText("Double clicking on an extension will add/remove the extension from the selected extensions list."
				+ " The current selected extensions are displayed in the second column."
				+ " Use the filter to narrow the extensions list.");
		GridDataFactory.fillDefaults()
			.align(SWT.FILL, SWT.FILL)
			.grab(true, false)
			.applyTo(explanation);
		
		Composite filterComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults()
			.numColumns(2)
			.applyTo(filterComposite);
		GridDataFactory.fillDefaults()
			.align(SWT.FILL, SWT.FILL)
			.grab(true, false)
			.applyTo(filterComposite);
		
		Label filterExplanation = new Label(filterComposite, SWT.NONE);
		filterExplanation.setText("Filter:");
		GridDataFactory.fillDefaults()
			.align(SWT.LEFT, SWT.FILL)
			.grab(false, false)
			.applyTo(filterExplanation);
		
		Text filterText = new Text(filterComposite,SWT.BORDER);
		GridDataFactory.fillDefaults()
			.align(SWT.FILL, SWT.FILL)
			.grab(true, false)
			.applyTo(filterText);
		
		Button platformButton = new Button(parent, SWT.CHECK);
		platformButton.setSelection(true);
		platformButton.setText("Platform only extensions");
		GridDataFactory.fillDefaults()
		.align(SWT.FILL, SWT.FILL)
		.grab(true, false)
		.applyTo(platformButton);

		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
		GridDataFactory.fillDefaults()
			.align(SWT.FILL, SWT.FILL)
			.grab(true, true)
			.applyTo(sashForm);

		// extensions
		Tree extensionsTree = (Tree) createColumn("Available Extensions:", sashForm, this::createTree);
		TreeViewer treeExtensionsViewer = new TreeViewer(extensionsTree);
		GridDataFactory.fillDefaults()
			.grab(true, true)
			.align(SWT.FILL, SWT.FILL)
			.hint(SWT.DEFAULT, PREFFERRED_HEIGHT_EXTENSIONS)
			.applyTo(extensionsTree);

		treeExtensionsViewer.addDoubleClickListener(e -> {
			if (e.getSelection() instanceof IStructuredSelection) {
				Object selected = ((IStructuredSelection) e.getSelection()).getFirstElement();
				if (selected instanceof QuarkusExtension) {
					model.toggleSelectedExtension((QuarkusExtension) selected);
				}
			}
		});
		treeExtensionsViewer.setContentProvider(new ExtensionsModelContentProvider());
		treeExtensionsViewer.setLabelProvider(new ExtensionStyledCellLabelProvider());
		treeExtensionsViewer.setFilters(new TreeFilter(filterText, platformButton));
		treeExtensionsViewer.setInput(model.getExtensionsModel());
		treeExtensionsViewer.expandAll();
		
		filterText.addModifyListener(e -> treeExtensionsViewer.refresh());
		platformButton.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> treeExtensionsViewer.refresh()));

		// selected extensions
		Table selectedExtensionsTable = (Table) createColumn("Selected Extensions:", sashForm, this::createTable);
		GridDataFactory.fillDefaults()
			.grab(true, true)
			.align(SWT.FILL, SWT.FILL)
			.hint(SWT.DEFAULT, PREFFERRED_HEIGHT_EXTENSIONS)
			.applyTo(selectedExtensionsTable);
		int width = getColumnWidth(model.getExtensionsModel().getCategories(), selectedExtensionsTable);
		TableViewer tableSelectedExtensionsViewer = createExtensionViewer(width, selectedExtensionsTable);
		tableSelectedExtensionsViewer.setContentProvider(new ObservableSetContentProvider<>());
		IObservableSet<Object> selectedExtensionsObservable = BeanProperties.set(SELECTED_EXTENSIONS_PROPERTY).observe(model);
		tableSelectedExtensionsViewer.setInput(selectedExtensionsObservable);
		tableSelectedExtensionsViewer.addDoubleClickListener(e -> {
			TableViewer viewer = (TableViewer) e.getSource();
			QuarkusExtension extension = (QuarkusExtension) viewer.getElementAt(viewer.getTable().getSelectionIndex());
			model.toggleSelectedExtension(extension);
		});
		sashForm.setWeights(50, 50);

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
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.FILL).applyTo(detailLabel);
		Link detail = new Link(container, SWT.WRAP);
		GridDataFactory.fillDefaults()
			.align(SWT.FILL, SWT.FILL)
			.grab(true, true)
			.applyTo(detail);

		ISWTObservableValue<String> extensionDetailObservable = WidgetProperties.text().observe(detail);
		ValueBindingBuilder
			.bind(ViewerProperties.singleSelection().observe(treeExtensionsViewer))
			.converting(new QuarkusExtension2StringConverter())
			.to(extensionDetailObservable).in(dbc);
		ValueBindingBuilder
			.bind(ViewerProperties.singleSelection().observe(treeExtensionsViewer))
			.to(BeanProperties.value("data").observe(detail))
			.notUpdatingParticipant().in(dbc);
		detail.addSelectionListener(
				SelectionListener.widgetSelectedAdapter(e -> openBrowser((QuarkusExtension) detail.getData())));

	}

	private int getColumnWidth(List<QuarkusCategory> categories, Table table) {
		int width = 0;
		if (categories == null) {
			return width;
		}
		List<QuarkusExtension> allExtensions = categories.stream()
				.flatMap((QuarkusCategory category) -> category.getExtensions().stream())
				.collect(Collectors.toList());
		String longestLabel = getLongestLabel(allExtensions);
		if (longestLabel != null) {
			GC gc = new GC(table);
			Point extent = gc.textExtent(longestLabel);
			gc.dispose();
			if (extent != null) {
				// text extent + icon
				width = extent.x + 30;
			}
		}
		return width;
	}

	private String getLongestLabel(List<QuarkusExtension> extensions) {
		return extensions.stream()
				.map(extension -> extension.asLabel())
				.reduce((label1, label2)
                        -> label1.length() > label2.length()
                                      ? label1 : label2)
				.orElse(null);
	}
	
	public TableViewer createExtensionViewer(int width, Table table) {
		TableColumnLayout layout = new TableColumnLayout();
		table.getParent().setLayout(layout);
		TableViewer viewer = new TableViewer(table);
		TableViewerColumn col = new TableViewerColumn(viewer, SWT.LEFT);		
		col.setLabelProvider(new ExtensionStyledCellLabelProvider());
		layout.setColumnData(col.getColumn(), new ColumnWeightData(100, width, false));
		return viewer;
	}

	private Control createColumn(String title, Composite parent, Function<Composite, Control> factory) {
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

		return factory.apply(container);
	}

	private Table createTable(Composite parent) {
		Composite tableContainer = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults()
			.grab(true, true)
			.align(SWT.FILL, SWT.FILL)
			.hint(SWT.DEFAULT, PREFFERRED_HEIGHT_EXTENSIONS)
			.applyTo(tableContainer);
		// TableColumnLayout requires a parent composite for the table
		Table table = new Table(tableContainer, SWT.SINGLE | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
		table.setHeaderVisible(false);
		table.setLinesVisible(false);
		return table;
	}

	private Tree createTree(Composite parent) {
		Tree tree = new Tree(parent, SWT.SINGLE | SWT.NONE | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
		tree.setHeaderVisible(false);
		tree.setLinesVisible(false);
		return tree;
	}

	private void openBrowser(QuarkusExtension extension) {
		if (extension != null) {
			new BrowserUtility().checkedCreateExternalBrowser(extension.getGuide(), QuarkusUIPlugin.PLUGIN_ID, QuarkusUIPlugin.getDefault().getLog());
		}
	}
}
