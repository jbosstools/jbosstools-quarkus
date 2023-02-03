/*******************************************************************************
 * Copyright (c) 2023 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.quarkus.lsp4e.core;

import org.eclipse.lsp4mp.jdt.core.PropertiesManagerClassPathKindTest;
import org.jboss.tools.quarkus.lsp4e.internal.ls.JDTUtilsImpl;
import org.junit.BeforeClass;

/**
 * @author Red Hat Developers
 *
 */
public class JDTPropertiesManagerClassPathKindTest extends PropertiesManagerClassPathKindTest {
	@BeforeClass
	public static void init() {
		setJDTUtils(JDTUtilsImpl.getInstance());
	}

}
