/******************************************************************************* 
 * Copyright (c) 2020 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.quarkus.reddeer.view;

import java.util.List;

import org.eclipse.reddeer.swt.api.TableItem;
import org.eclipse.reddeer.swt.impl.table.DefaultTable;
import org.eclipse.reddeer.workbench.impl.view.WorkbenchView;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels;

/**
 * 
 * @author jkopriva@redhat.com
 */
public class ExtensionsView extends WorkbenchView  {

	public ExtensionsView() {
		super(QuarkusLabels.Shell.QUARKUS_CATEGORY, QuarkusLabels.Shell.EXTENSION_VIEW);
	}

	/**
	 * Returns list of Quarkus extensions
	 * 
	 * @return list of extensions
	 */
	public List<TableItem> getExtentions() {
		return new DefaultTable(this).getItems();
	}
	
	/**
	 * Returns specific Quarkus extension TableItem
	 * 
	 * @param extension name
	 * @return extension TableItem
	 */
	public TableItem getExtension(String name) {
		return getExtentions().stream().filter(e -> e.getText(0).equals(name)).findFirst().get();
	}
}
