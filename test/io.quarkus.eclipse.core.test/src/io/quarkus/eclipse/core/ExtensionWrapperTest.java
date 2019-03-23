package io.quarkus.eclipse.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.quarkus.dependencies.Extension;

public class ExtensionWrapperTest {
	
	@Test
	public void testGetters() {
		Extension extension = new Extension("com.acme", "quarkus", "1.0.0-SNAPSHOT");
		ExtensionWrapper extensionWrapper = new ExtensionWrapper(extension);
		assertEquals("com.acme", extensionWrapper.getGroupId());
		assertEquals("quarkus", extensionWrapper.getArtifactId());
		assertEquals("1.0.0-SNAPSHOT", extensionWrapper.getVersion());
	}

}
