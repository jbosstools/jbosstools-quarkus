/*******************************************************************************
* Copyright (c) 2023 Red Hat Inc. and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package org.jboss.tools.quarkus.lsp4e.core.openapi.properties;

import org.eclipse.lsp4mp.jdt.core.openapi.properties.MicroProfileOpenAPITest;
import org.jboss.tools.quarkus.lsp4e.internal.ls.JDTUtilsImpl;
import org.junit.BeforeClass;

/**
 * @author Red Hat Developers
 *
 */
public class JDTMicroProfileOpenAPITest extends MicroProfileOpenAPITest {
	@BeforeClass
	public static void init() {
		setJDTUtils(JDTUtilsImpl.getInstance());
	}
}
