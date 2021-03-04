/*******************************************************************************
 * Copyright (c) 2019 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.quarkus.core.code.model;

import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_ENDPOINT_URL;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_ENDPOINT_URL_PROPERTY_NAME;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_ENDPOINT_URL_TEST;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class QuarkusModelRegistryTest {
	private static QuarkusModelRegistry registry;
	
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	@BeforeClass
	public static void setup() {
		System.setProperty(CODE_ENDPOINT_URL_PROPERTY_NAME, CODE_ENDPOINT_URL_TEST);
		registry = QuarkusModelRegistry.getDefault();
	}
	
	@Test
	public void testThatRegistryIsAvailable() {
		assertNotNull(registry);
	}
	
	@Test
	public void testThatRegistryReturnsDefaultModel() throws CoreException  {
		assertNotNull(registry.getModel(new NullProgressMonitor()));
	}
	
  @Test
  public void checkThatModelCanLoadWithCodeQuarkusIO() throws CoreException {
      assertNotNull(registry.getModel(CODE_ENDPOINT_URL, new NullProgressMonitor()));
  }

  @Test
  public void checkThatModelCanLoadWithCodeQuarkusIOAndSpaceInHost() throws CoreException {
      assertNotNull(registry.getModel(CODE_ENDPOINT_URL + ' ', new NullProgressMonitor()));
  }

  @Test
  public void checkThatModelCanLoadWithCodeQuarkusIOAndSpaceInPath() throws CoreException {
      assertNotNull(registry.getModel(CODE_ENDPOINT_URL + "/ ", new NullProgressMonitor()));
  }

  @Test(expected = CoreException.class)
  public void checkThatIOExceptionIsReturnedWithInvalidURL() throws CoreException {
      registry.getModel("https://invalid.org", new NullProgressMonitor());
  }

  private Set<QuarkusExtension> enableExtension(QuarkusModel model, String name) {
    Set<QuarkusExtension> extensions = new HashSet<>();
    model.getCategories().stream().forEach(category -> category.getExtensions().stream()
        .filter(extension -> extension.getName().equals(name)).forEach(extension -> extensions.add(extension)));
    return extensions;
  }

  private File checkBaseMavenProject(boolean examples) throws CoreException, IOException {
      File folder = temporaryFolder.newFolder();
      QuarkusModel model = registry.getModel(CODE_ENDPOINT_URL, new NullProgressMonitor());
      registry.zip(CODE_ENDPOINT_URL, Tool.MAVEN, "org.acme", "code-with-quarkus",
              "0.0.1-SNAPSHOT", "org.acme.ExampleResource", "/example",
              enableExtension(model, "RESTEasy JAX-RS"), examples, new Path(folder.getAbsolutePath()), new NullProgressMonitor());
      assertTrue(new File(folder, "pom.xml").exists());
      return folder;
  }

  @Test
  public void checkBaseMavenProjectWithExamples() throws CoreException, IOException {
      File folder = checkBaseMavenProject(true);
      assertTrue(new File(folder, "src/main/java/org/acme/ExampleResource.java").exists());
  }

  @Test
  public void checkBaseMavenProjectWithoutExamples() throws CoreException, IOException {
      File folder = checkBaseMavenProject(false);
      assertFalse(new File(folder, "src/main/java/org/acme/ExampleResource.java").exists());
  }

  private Set<QuarkusExtension> enableAllExtensions(QuarkusModel model) {
    Set<QuarkusExtension> extensions = new HashSet<>();
    model.getCategories().stream().filter(category -> !category.getName().equals("Alternative languages"))
        .forEach(category -> extensions.addAll(category.getExtensions()));
    return extensions;
  }

  @Test
  public void checkAllExtensionsMavenProject() throws CoreException, IOException {
      File folder = temporaryFolder.newFolder();
      QuarkusModel model = registry.getModel(CODE_ENDPOINT_URL, new NullProgressMonitor());
      registry.zip(CODE_ENDPOINT_URL, Tool.MAVEN, "org.acme", "code-with-quarkus",
              "0.0.1-SNAPSHOT", "org.acme.ExampleResource", "/example", enableAllExtensions(model),
              false, new Path(folder.getAbsolutePath()), new NullProgressMonitor());
      assertTrue(new File(folder, "pom.xml").exists());
  }

  private File checkBaseGradleProject(boolean examples) throws CoreException, IOException {
      File folder = temporaryFolder.newFolder();
      QuarkusModel model = registry.getModel(CODE_ENDPOINT_URL, new NullProgressMonitor());
      registry.zip(CODE_ENDPOINT_URL, Tool.GRADLE, "org.acme", "code-with-quarkus",
              "0.0.1-SNAPSHOT", "org.acme.ExampleResource", "/example",
              enableExtension(model, "RESTEasy JAX-RS"), examples, new Path(folder.getAbsolutePath()), new NullProgressMonitor());
      assertTrue(new File(folder, "build.gradle").exists());
      return folder;
  }

  @Test
  public void checkBaseGradleProjectWithExamples() throws CoreException, IOException {
      File folder  = checkBaseGradleProject(true);
      assertTrue(new File(folder, "src/main/java/org/acme/ExampleResource.java").exists());
  }

  @Test
  public void checkBaseGradleProjectWithoutExamples() throws CoreException, IOException {
      File folder  = checkBaseGradleProject(false);
      assertFalse(new File(folder, "src/main/java/org/acme/ExampleResource.java").exists());
  }

  @Test
  public void checkAllExtensionsGradleProject() throws CoreException, IOException {
      File folder = temporaryFolder.newFolder();
      QuarkusModel model = registry.getModel(CODE_ENDPOINT_URL, new NullProgressMonitor());
      ;
      registry.zip(CODE_ENDPOINT_URL, Tool.GRADLE, "org.acme", "code-with-quarkus",
              "0.0.1-SNAPSHOT", "org.acme.ExampleResource", "/example", enableAllExtensions(model),
              false, new Path(folder.getAbsolutePath()), new NullProgressMonitor());
      assertTrue(new File(folder, "build.gradle").exists());
  }
}
