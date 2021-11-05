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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


public class CodeProjectWizardModelTest {
	@Test
	public void testDefaultModel()  {
		CodeProjectModel model = new CodeProjectModel();
		assertNotNull(model.getArtifactId());
		assertNotNull(model.getGroupId());
		assertNotNull(model.getVersion());
		assertNotNull(model.getLocation());
		assertTrue(model.isUseDefaultLocation());
	}
}
