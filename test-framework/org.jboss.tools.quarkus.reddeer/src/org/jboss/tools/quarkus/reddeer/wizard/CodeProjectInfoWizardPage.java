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
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels;

/**
 * 
 * @author jkopriva@redhat.com
 */
public class CodeProjectInfoWizardPage extends WizardPage {

	public CodeProjectInfoWizardPage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}

	/**
	 * Sets the "Artifact id" value
	 * 
	 * @param newArtifactId
	 */
	public void setMavenArtifactId(String newArtifactId) {
		new LabeledText(QuarkusLabels.TextLabels.ARTIFACT_ID).setText(newArtifactId);
	}

	/**
	 * Returns the "Artifact id" value
	 * 
	 * @return
	 */
	public String getMavenArtifactId() {
		return new LabeledText(QuarkusLabels.TextLabels.ARTIFACT_ID).getText();
	}

	/**
	 * Sets the "Group id" value
	 * 
	 * @param newGroupId
	 */
	public void setMavenGroupId(String newGroupId) {
		new LabeledText(QuarkusLabels.TextLabels.GROUP_ID).setText(newGroupId);
	}

	/**
	 * Returns the "Group id" value
	 * 
	 * @return
	 */
	public String getMavenGroupId() {
		return new LabeledText(QuarkusLabels.TextLabels.GROUP_ID).getText();
	}

	/**
	 * Sets the version.
	 * 
	 * @param newVersion
	 */
	public void setMavenVersion(String newVersion) {
		new LabeledText(QuarkusLabels.TextLabels.VERSION).setText(newVersion);
	}

	/**
	 * Returns the version.
	 * 
	 * @return
	 */
	public String getMavenVersion() {
		return new LabeledText(QuarkusLabels.TextLabels.VERSION).getText();
	}
	
	/**
	 * Sets the rest class name.
	 * 
	 * @param className
	 */
	public void setRESTClassName(String className) {
		new LabeledText(QuarkusLabels.TextLabels.REST_CLASS_NAME).setText(className);
	}
	
	/**
	 * Returns the rest class name.
	 * 
	 * @return rest class
	 */
	public String getRESTClassName() {
		return new LabeledText(QuarkusLabels.TextLabels.REST_CLASS_NAME).getText();
	}
	
	/**
	 * Sets the rest path.
	 * 
	 * @param path
	 */
	public void setRESTpath(String path) {
		new LabeledText(QuarkusLabels.TextLabels.REST_PATH).setText(path);
	}
	
	/**
	 * Returns the rest path.
	 * 
	 * @return rest path
	 */
	public String getRESTPath() {
		return new LabeledText(QuarkusLabels.TextLabels.REST_PATH).getText();
	}


}
