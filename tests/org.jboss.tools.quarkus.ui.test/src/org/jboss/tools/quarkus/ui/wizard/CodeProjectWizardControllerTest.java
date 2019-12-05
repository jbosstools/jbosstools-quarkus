/*******************************************************************************
 * Copyright (c) 2019 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.quarkus.ui.wizard;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.quarkus.core.code.model.QuarkusModel;
import org.jboss.tools.quarkus.core.code.model.QuarkusModelRegistry;
import org.jboss.tools.quarkus.core.code.model.Tool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CodeProjectWizardControllerTest {
	private int counter = 0;
	
	private static QuarkusModel catalog;
	
	@BeforeAll
	public static void setup() throws CoreException {
		catalog = QuarkusModelRegistry.getDefault().getModel(new NullProgressMonitor());
	}
	
	@Test
	public void testMaven()  {
		CodeProjectModel model = new CodeProjectModel();
		model.setProjectName("quarkus-maven" + counter++);
		model.setModel(catalog);
		IStatus status = new CodeProjectWizardController(model).run(new NullProgressMonitor());
		assertTrue(status.isOK());
	}
	
	@Test
	public void testGradle()  {
		CodeProjectModel model = new CodeProjectModel();
		model.setProjectName("quarkus-gradle" + counter++);
		model.setModel(catalog);
		model.setTool(Tool.GRADLE);
		IStatus status = new CodeProjectWizardController(model).run(new NullProgressMonitor());
		assertTrue(status.isOK(), status::getMessage);
	}
}
