package org.jboss.tools.quarkus.lsp4e.core;

import org.jboss.tools.quarkus.lsp4e.internal.JDTUtilsImpl;
import org.junit.BeforeClass;

import com.redhat.microprofile.jdt.quarkus.ConfigItemIntBoolDefaultValueTest;

public class JDTConfigItemIntBoolDefaultValueTest extends ConfigItemIntBoolDefaultValueTest {
	@BeforeClass
	public static void init() {
		setJDTUtils(JDTUtilsImpl.getInstance());
	}
}
