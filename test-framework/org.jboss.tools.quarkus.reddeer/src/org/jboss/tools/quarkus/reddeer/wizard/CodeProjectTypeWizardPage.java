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
import org.eclipse.reddeer.swt.impl.combo.LabeledCombo;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels;

/**
 * 
 * @author jkopriva@redhat.com
 */
public class CodeProjectTypeWizardPage extends WizardPage {

	public CodeProjectTypeWizardPage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}

	/**
	 * Sets the "Project name:" value
	 * 
	 * @param newName
	 */
	public void setProjectName(String newName) {
		new LabeledText(QuarkusLabels.TextLabels.PROJECT_NAME).setText(newName);
	}

	/**
	 * Returns project name
	 * 
	 * @return project name
	 */
	public String getProjectName() {
		return new LabeledText(QuarkusLabels.TextLabels.PROJECT_NAME).getText();
	}

	/**
	 * Sets the Maven project type
	 */
	public void setMavenProjectType() {
		new LabeledCombo(QuarkusLabels.TextLabels.PROJECT_TYPE).setSelection(QuarkusLabels.TextLabels.MAVEN_TYPE);
		
	}
	
	/**
	 * Sets the Gradle project type
	 */
	public void setGradleProjectType() {
		new LabeledCombo(QuarkusLabels.TextLabels.PROJECT_TYPE).setSelection(QuarkusLabels.TextLabels.GRADLE_TYPE);
		
	}
	
	/**
	 * Returns project type
	 * 
	 * @return project type
	 */
	public String getProjectType() {
		return new LabeledCombo(QuarkusLabels.TextLabels.PROJECT_TYPE).getSelection();
		
	}

}
