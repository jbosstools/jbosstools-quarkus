/*******************************************************************************
 * Copyright (c) 2022 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.quarkus.lsp4e.qute.java;

import org.jboss.tools.quarkus.lsp4e.internal.ls.JDTUtilsImpl;
import org.junit.BeforeClass;
import com.redhat.qute.jdt.QuteProjectTest;
import com.redhat.qute.jdt.java.JavaDiagnosticsTest;

/**
 * @author Red Hat Developers
 *
 */
public class JDTJavaDiagnosticsTest extends JavaDiagnosticsTest {
	@BeforeClass
	public static void init() {
		QuteProjectTest.setJDTUtils(JDTUtilsImpl.getInstance());
	}
}
