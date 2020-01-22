/*
 * Copyright 2019-2020 Red Hat, Inc.
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
package org.jboss.tools.quarkus.core.launch;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import java.io.File;
import org.eclipse.core.externaltools.internal.IExternalToolConstants;
import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.jboss.tools.quarkus.core.code.utils.ProjectHelpers;
import org.jboss.tools.quarkus.core.launch.LaunchUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class LaunchUtilsTest {
	
	private ILaunchConfigurationWorkingCopy configuration;
	
	@BeforeEach
	public void setUp() {
		configuration = Mockito.mock(ILaunchConfigurationWorkingCopy.class);
	}
	
	@Test
	public void testInitializeQuarkusLaunchConfigurationWithMaven() throws Exception {
		IProject project = ProjectHelpers.loadProject(new File("projects/maven/code-with-quarkus-maven"));
		when(configuration.getAttribute(eq(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME), isNull(String.class))).thenReturn(project.getName());
		LaunchUtils.initializeQuarkusLaunchConfiguration(configuration);
		Mockito.verify(configuration).setAttribute(eq(IExternalToolConstants.ATTR_LOCATION), contains("mvnw"));
		Mockito.verify(configuration).setAttribute(eq(IExternalToolConstants.ATTR_WORKING_DIRECTORY), eq(project.getLocation().toOSString()));
		Mockito.verify(configuration).setAttribute(eq(IExternalToolConstants.ATTR_TOOL_ARGUMENTS), eq("compile quarkus:dev"));
	}


	@Test
	public void testInitializeQuarkusLaunchConfigurationWithGradle() throws Exception {
		IProject project = ProjectHelpers.loadProject(new File("projects/gradle/code-with-quarkus-gradle"));
		when(configuration.getAttribute(eq(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME), isNull(String.class))).thenReturn(project.getName());
		LaunchUtils.initializeQuarkusLaunchConfiguration(configuration);
		Mockito.verify(configuration).setAttribute(eq(IExternalToolConstants.ATTR_LOCATION), contains("gradlew"));
		Mockito.verify(configuration).setAttribute(eq(IExternalToolConstants.ATTR_WORKING_DIRECTORY), eq(project.getLocation().toOSString()));
		Mockito.verify(configuration).setAttribute(eq(IExternalToolConstants.ATTR_TOOL_ARGUMENTS), eq("quarkusDev"));
	}
}

