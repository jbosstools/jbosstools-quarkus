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
package org.jboss.tools.quarkus.core.code.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class QuarkusModelTest {
	private static QuarkusModelRegistry registry;
	
	@BeforeClass
	public static void setup() {
		registry = QuarkusModelRegistry.getDefault();
	}
	
	@Test
	public void testCategories() throws CoreException, JsonParseException, JsonMappingException, IOException  {
		QuarkusModel model = registry.readModel(new File("resources/extensions.json"));
		assertNotNull(model);
		assertNotNull(model.getCategories());
		assertEquals(13, model.getCategories().size());
		QuarkusCategory category = model.getCategories().iterator().next();
		assertNotNull(category);
		assertEquals("Web", category.getName());
	}
	
}
