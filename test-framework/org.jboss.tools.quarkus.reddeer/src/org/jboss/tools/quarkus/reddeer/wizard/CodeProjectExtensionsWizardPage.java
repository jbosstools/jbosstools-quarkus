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
package org.jboss.tools.quarkus.reddeer.wizard;

import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.jface.wizard.WizardPage;
import org.eclipse.reddeer.swt.impl.list.DefaultList;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels;

/**
 * 
 * @author jkopriva@redhat.com
 */
public class CodeProjectExtensionsWizardPage extends WizardPage {

	public CodeProjectExtensionsWizardPage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}

	/**
	 * Returns Quarkus extension categories
	 * 
	 * @return 
	 */
	public String[] getCategories() {
		return new DefaultList(QuarkusLabels.TextLabels.EXTENSIONS_CATEGORIES).getListItems();
	}

	/**
	 * Select Quarkus extension category
	 * 
	 * @param category
	 */
	public void selectCategory(String category) {
		new DefaultList(QuarkusLabels.TextLabels.EXTENSIONS_CATEGORIES).select(category);
	}
	
	/**
	 * Returns Quarkus extensions
	 * 
	 * @return list of extensions
	 */
	public String[] getExtensions() {
		return new DefaultList(QuarkusLabels.TextLabels.EXTENSIONS_NAMES).getListItems();
	}
	
	/**
	 * Select Quarkus extension
	 * 
	 * @param extension
	 */
	public void selectExtension(String extension) {
		new DefaultList(QuarkusLabels.TextLabels.EXTENSIONS_NAMES).select(extension);
	}
}
