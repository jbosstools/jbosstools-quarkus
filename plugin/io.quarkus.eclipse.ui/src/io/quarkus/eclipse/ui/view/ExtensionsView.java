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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import io.quarkus.dependencies.Extension;
import io.quarkus.maven.utilities.MojoUtils;

public class ExtensionsView extends ViewPart {
	
	public static final String ID = "io.quarkus.eclipse.ui.view.extensionsView";

	private Table table;

	public void createPartControl(Composite parent) {
		table = new Table(parent, SWT.CHECK | SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
        table.setHeaderVisible(true);
        String[] titles = { "Name", "Group Id", "Artifact Id", "Version" };
        for (int i = 0; i < titles.length; i++) {
            TableColumn column = new TableColumn(table, SWT.NONE);
            column.setText(titles[i]);
        }
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
	
}
