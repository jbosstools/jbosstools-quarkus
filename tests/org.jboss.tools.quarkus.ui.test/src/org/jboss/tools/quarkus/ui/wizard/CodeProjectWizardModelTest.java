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

import java.io.File;
import java.io.IOException;

import org.jboss.tools.quarkus.core.code.model.QuarkusModel;
import org.jboss.tools.quarkus.core.code.model.QuarkusModelRegistry;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;


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
	
	@Test
	public void testModelWithCatalog() throws JsonParseException, JsonMappingException, IOException   {
		CodeProjectModel model = new CodeProjectModel();
		QuarkusModel model1 = QuarkusModelRegistry.readModel(new File("resources/extensions.json"));
		model.setModel(model1);
		assertNotNull(model.getSelectedCategory());
	}
}
