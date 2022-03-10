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
package org.jboss.tools.quarkus.lsp4e.internal.menus.qute;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart3;
import org.eclipse.ui.PlatformUI;

/**
 * @author Red Hat Developers
 *
 */
public class QuteValidationContributionItem extends ContributionItem {
	
	private static final String VALIDATION_PROPERTY = QuteValidationContributionItem.class.getName() + ".validation";

	private static IEditorPart getActiveEditor() {
		var window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			var page = window.getActivePage();
			if (page != null) {
				return page.getActiveEditor();
			}
		}
		return null;
	}
	
	@Override
	public void fill(Menu menu, int index) {
		IEditorPart editor = getActiveEditor();
		if (editor instanceof IWorkbenchPart3) {
			String validation = ((IWorkbenchPart3) editor).getPartProperty(VALIDATION_PROPERTY);
			MenuItem item = new MenuItem(menu, SWT.PUSH, index);
			item.setText("false".equals(validation)?"Disable Qute validation":"Enable Qute validation");
		}
	
	}
}
