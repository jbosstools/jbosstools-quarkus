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
package org.jboss.tools.quarkus.lsp4e.core.health;

import org.jboss.tools.quarkus.lsp4e.internal.JDTUtilsImpl;
import org.junit.BeforeClass;
import org.junit.Ignore;

import com.redhat.microprofile.jdt.core.health.JavaDiagnosticsMicroProfileHealthTest;

/**
 * @author Red Hat Developers
 *
 */
@Ignore("JDT and JDT-LS JavaDoc2HTML generators differs")
public class JDTJavaDiagnosticsMicroProfileHealthTest extends JavaDiagnosticsMicroProfileHealthTest {
	@BeforeClass
	public static void init() {
		setJDTUtils(JDTUtilsImpl.getInstance());
	}
}
