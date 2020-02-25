/*
 * Copyright 2019 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.tools.quarkus.core.project;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ProjectUtilsTest {
	
	private static final String PROJECT_NAME = "com.acme.quarkus";
	
	// TODO Reenable this test
	@Disabled
	@Test
	public void testCreate() {
		IProject project = ResourcesPlugin
				.getWorkspace()
				.getRoot()
				.getProject(PROJECT_NAME);
		assertFalse(project.exists());
		ProjectUtils.createProject(
				"quarkus", 
				project.getLocation().toOSString(),
				PROJECT_NAME, 
				"com.acme", 
				"1.0.0-SHAPSHOT", 
				null,
				null);
		assertTrue(project.exists());
	}
	
	@Test 
	public void testProjectExists() {
		try {
			assertFalse(ProjectUtils.projectExists(PROJECT_NAME));
			ResourcesPlugin
					.getWorkspace()
					.getRoot()
					.getProject(PROJECT_NAME)
					.create(new NullProgressMonitor());
			assertTrue(ProjectUtils.projectExists(PROJECT_NAME));
			assertFalse(ProjectUtils.projectExists(null));
			assertFalse(ProjectUtils.projectExists(""));
		}
		catch (CoreException e) {
			fail();
		}
	}
	
	@Test
	public void testGetProjectLocationDefault() {
		assertNotNull(ProjectUtils.getProjectLocationDefault());
	}
	
}

