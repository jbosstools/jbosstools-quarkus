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

import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_ENDPOINT_URL_PROPERTY_NAME;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_ENDPOINT_URL_TEST;
import static org.junit.Assert.assertNotNull;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.BeforeClass;
import org.junit.Test;

public class QuarkusModelRegistryTest {
	private static QuarkusModelRegistry registry;
	
	@BeforeClass
	public static void setup() {
		System.setProperty(CODE_ENDPOINT_URL_PROPERTY_NAME, CODE_ENDPOINT_URL_TEST);
		registry = QuarkusModelRegistry.getDefault();
	}
	
	@Test
	public void testThatRegistryIsAvailable() {
		assertNotNull(registry);
	}
	
	@Test
	public void testThatRegistryReturnsDefaultModel() throws CoreException  {
		assertNotNull(registry.getModel(new NullProgressMonitor()));
	}
	
}
