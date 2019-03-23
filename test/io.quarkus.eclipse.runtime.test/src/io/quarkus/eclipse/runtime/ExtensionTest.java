package io.quarkus.eclipse.runtime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import io.quarkus.dependencies.Extension;

public class ExtensionTest {
	
	@Test
	public void testExistence() {
		assertNotNull(Extension.class);
	}

}
