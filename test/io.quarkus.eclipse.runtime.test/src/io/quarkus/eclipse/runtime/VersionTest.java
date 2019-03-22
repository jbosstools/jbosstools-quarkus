package io.quarkus.eclipse.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class VersionTest {
	
	@Test
	public void testVersion() {
		assertEquals("0.11.0", io.quarkus.maven.utilities.MojoUtils.getPluginVersion());
	}

}
