package io.quarkus.eclipse.core;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.junit.jupiter.api.Test;

public class ProjectCreatorTest {
	
	@Test
	public void testCreate() {
		IProject project = ResourcesPlugin
				.getWorkspace()
				.getRoot()
				.getProject("com.acme.quarkus");
		assertFalse(project.exists());
		ProjectCreator.create(
				"com.acme.quarkus", 
				"com.acme", 
				"quarkus", 
				"1.0.0-SHAPSHOT", 
				null);
		assertTrue(project.exists());
	}
	
}

