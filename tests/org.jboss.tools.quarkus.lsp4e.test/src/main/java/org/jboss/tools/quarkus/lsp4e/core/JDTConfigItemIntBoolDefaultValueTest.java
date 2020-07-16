package org.jboss.tools.quarkus.lsp4e.core;

import org.eclipse.lsp4mp.jdt.core.ConfigItemIntBoolDefaultValueTest;
import org.jboss.tools.quarkus.lsp4e.internal.JDTUtilsImpl;
import org.junit.BeforeClass;

public class JDTConfigItemIntBoolDefaultValueTest extends ConfigItemIntBoolDefaultValueTest {
	@BeforeClass
	public static void init() {
		setJDTUtils(JDTUtilsImpl.getInstance());
	}
}
