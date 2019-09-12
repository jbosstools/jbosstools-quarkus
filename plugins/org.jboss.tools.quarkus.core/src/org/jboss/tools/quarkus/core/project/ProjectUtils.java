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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.MavenModelManager;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.LocalProjectScanner;
import org.eclipse.m2e.core.project.MavenProjectInfo;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;

import io.quarkus.cli.commands.AddExtensions;
import io.quarkus.cli.commands.CreateProject;
import io.quarkus.cli.commands.ListExtensions;
import io.quarkus.cli.commands.writer.FileProjectWriter;
import io.quarkus.cli.commands.writer.ProjectWriter;
import io.quarkus.dependencies.Extension;
import io.quarkus.maven.utilities.MojoUtils;

public class ProjectUtils {
	
	public static Set<?> findInstalledExtensions(Object currentProject) {
		try {
			if (currentProject != null && currentProject instanceof IProject) {
				IResource resource = ((IProject)currentProject).findMember("pom.xml");
				if (resource != null) {
					File pomFile = new File(resource.getRawLocation().toOSString());
					Model model = MojoUtils.readPom(pomFile);
					ListExtensions listExtensions = new ListExtensions(model);
					Map<?,?> extensions = (Map<?,?>)getFindInstalledMethod().invoke(listExtensions);
					return extensions.keySet(); 
				}
			}
		} catch (IOException | InvocationTargetException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return new HashSet<Object>();
	}
	
	public static void createProject(			
			String name, 
			String location,
			String groupId, 
			String artefactId, 
			String version, 
			String className,
			HashMap<String, Object> context) {
		try {
			if (context == null) {
				context = new HashMap<String, Object>();
			}
			File workspaceFolder = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
			File projectFolder = new File(location);
			ProjectWriter projectWriter = new FileProjectWriter(projectFolder);
			new CreateProject(projectWriter)
					.groupId(groupId)
					.artifactId(artefactId)
					.version(version)
					.className(className)
					.doCreateProject(context);
			Set<MavenProjectInfo> projectSet = null;
			IProjectConfigurationManager projectConfigurationManager = MavenPlugin.getProjectConfigurationManager();
			MavenModelManager mavenModelManager = MavenPlugin.getMavenModelManager();
			LocalProjectScanner scanner = new LocalProjectScanner(
					workspaceFolder, //
					projectFolder.getCanonicalPath(), 
					false, 
					mavenModelManager);
			scanner.run(new NullProgressMonitor());
			projectSet = projectConfigurationManager.collectProjects(scanner.getProjects());
			ProjectImportConfiguration configuration = new ProjectImportConfiguration();
			projectConfigurationManager.importProjects(projectSet,
					configuration, new NullProgressMonitor());
		} catch (IOException | InterruptedException | CoreException e) {
			e.printStackTrace();
		}
	}

	private static Method getFindInstalledMethod() {	
		Method result = null;
		try {
			result = ListExtensions.class.getDeclaredMethod("findInstalled");
			result.setAccessible(true);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	public static void installExtension(Object currentProject, Extension extension) {
		try {
			if (currentProject != null && currentProject instanceof IProject) {
				IResource resource = ((IProject)currentProject).findMember("pom.xml");
				IPath path = resource.getRawLocation().removeLastSegments(1);
				File file = new File(path.toOSString());
				ProjectWriter projectWriter = new FileProjectWriter(file);
				AddExtensions project = new AddExtensions(projectWriter, "pom.xml");
				project.addExtensions(Collections.singleton(extension.getArtifactId()));
				resource.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			}		
		} catch (IOException | CoreException e) {
			throw new RuntimeException(e);
		}
	}

	public static Object getSelectedProject(Object selectedElement) {
		Object result = null;
		if (selectedElement instanceof IResource) {
			result = ((IResource) selectedElement).getProject();
		}
		return result;
	}
	
	public static boolean projectExists(String name) {
		return (name != null) 
				&& !"".equals(name) 
				&& ResourcesPlugin.getWorkspace().getRoot().getProject(name).exists();
	}
	
	public static String getProjectLocationDefault() {
		return ResourcesPlugin.getWorkspace().getRoot().getRawLocation().toOSString();
	}
	
}
