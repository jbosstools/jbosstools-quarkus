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
