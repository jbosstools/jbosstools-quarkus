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

package io.quarkus.eclipse.ui.view;

import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import io.quarkus.dependencies.Extension;
import io.quarkus.eclipse.core.project.ProjectUtils;
import io.quarkus.maven.utilities.MojoUtils;

public class ExtensionsView extends ViewPart {
	
	public static final String ID = "io.quarkus.eclipse.ui.view.extensionsView";

	private Table table = null;
	private Object currentProject = null;

	public void createPartControl(Composite parent) {
		table = new Table(parent, SWT.CHECK | SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
        table.setHeaderVisible(true);
        String[] titles = { "Name", "Group Id", "Artifact Id", "Version" };
        for (int i = 0; i < titles.length; i++) {
            TableColumn column = new TableColumn(table, SWT.NONE);
            column.setText(titles[i]);
        }
        createContextMenu();
        addSelectionListener();
        List<Extension> extensions = MojoUtils.loadExtensions();
        for (Extension extension : extensions) {
        	TableItem item = new TableItem(table, SWT.NONE);
        	item.setData(extension);
        	item.setText(0, extension.getName());
        	item.setText(1, extension.getGroupId());
        	item.setText(2, extension.getArtifactId());
        	item.setText(3, extension.getVersion());
        }
        for (int i = 0; i < titles.length; i++) {
        	table.getColumn(i).pack();
        }
    }

	@Override
	public void setFocus() {
		table.setFocus();
	}
	
	private void createContextMenu() {
		Menu contextMenu = new Menu(table);
		table.setMenu(contextMenu);
		MenuItem menuItem = new MenuItem(contextMenu, SWT.None);
		menuItem.setText("Install extension");
		menuItem.addSelectionListener(new SelectionListener() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (currentProject != null) {
					ProjectUtils.installExtension(
							currentProject, 
							(Extension)table.getSelection()[0].getData());
					refreshView();
				}
			}		
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		table.addListener(SWT.MouseDown, new Listener() {		
		    @Override
		    public void handleEvent(Event event) {
		        TableItem[] selection = table.getSelection();
		        if(selection.length!=0 && (event.button == 3)){
		            contextMenu.setVisible(true);
		        }
		    }
		});
	}
	
	private void addSelectionListener() {
		getSite()
			.getWorkbenchWindow()
			.getSelectionService()
			.addSelectionListener(
					new ISelectionListener() {						
						@Override
						public void selectionChanged(IWorkbenchPart part, ISelection selection) {
							if (!selection.isEmpty() && selection instanceof StructuredSelection) {
								Object newProject = ProjectUtils.getSelectedProject(
										((StructuredSelection)selection).getFirstElement());
								if (currentProject != newProject) {
									currentProject = newProject;
									refreshView();
								}
							}
							else if (selection.isEmpty() && (currentProject != null)) {
								currentProject = null;
								refreshView();
							}
						}
					});
	}
	
	private void refreshView() {
		Set<?> installed = ProjectUtils.findInstalledExtensions(currentProject);
		for (TableItem tableItem : table.getItems()) {
			String credentials = 
					MojoUtils.credentials(
							((Extension)tableItem.getData()).toDependency(true));
			tableItem.setChecked(installed.contains(credentials));
		}
	}
	
}
