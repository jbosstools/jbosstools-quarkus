/*******************************************************************************
 * Copyright (c) 2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.quarkus.lsp4e.core.restclient;

import org.eclipse.lsp4mp.jdt.core.restclient.JavaDiagnosticsMicroProfileRestClientTest;
import org.jboss.tools.quarkus.lsp4e.internal.JDTUtilsImpl;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * @author Red Hat Developers
 *
 */
@Ignore(value = "Diff between JDT and JDT-LS")
public class JDTJavaDiagnosticsMicroProfileRestClientTest extends JavaDiagnosticsMicroProfileRestClientTest {
	@BeforeClass
	public static void init() {
		setJDTUtils(JDTUtilsImpl.getInstance());
	}
}
