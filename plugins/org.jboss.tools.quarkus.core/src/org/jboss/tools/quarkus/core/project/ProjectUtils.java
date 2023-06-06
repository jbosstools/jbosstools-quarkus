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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.MavenModelManager;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.LocalProjectScanner;
import org.eclipse.m2e.core.project.MavenProjectInfo;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.jboss.tools.quarkus.core.project.ClasspathContainersHelper;
import org.jboss.tools.quarkus.core.QuarkusCoreConstants;
import org.jboss.tools.quarkus.tool.DefaultToolContext;
import org.jboss.tools.quarkus.tool.GradleToolSupport;
import org.jboss.tools.quarkus.tool.MavenToolSupport;
import org.jboss.tools.quarkus.tool.ToolContext;
import org.jboss.tools.quarkus.tool.ToolSupport;

import io.quarkus.cli.commands.CreateProject;
import io.quarkus.cli.commands.ListExtensions;
import io.quarkus.cli.commands.file.BuildFile;
import io.quarkus.cli.commands.file.MavenBuildFile;
import io.quarkus.cli.commands.writer.FileProjectWriter;
import io.quarkus.cli.commands.writer.ProjectWriter;
import io.quarkus.dependencies.Extension;

public class ProjectUtils {
	
	public static Set<?> findInstalledExtensions(Object currentProject) {
		try {
			if (currentProject != null && currentProject instanceof IProject) {
				IResource resource = ((IProject)currentProject).findMember("pom.xml");
				if (resource != null) {
				    IPath path = resource.getRawLocation().removeLastSegments(1);
				    File file = new File(path.toOSString());
				    ProjectWriter projectWriter = new FileProjectWriter(file);
				    BuildFile buildFile = new MavenBuildFile(projectWriter);
				    ListExtensions listExtensions = new ListExtensions(buildFile);
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
					Collections.singletonList(projectFolder.getCanonicalPath()), 
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
			if (currentProject instanceof IProject) {
				IProject project = (IProject) currentProject;
				ToolSupport support = getToolSupport(project);
				ToolContext context = new DefaultToolContext(project.getName() + "__installExtension", project, Collections.emptyMap(), Collections.singletonList(extension.getArtifactId()));
				support.addExtension(context);
				project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			}		
		} catch (CoreException e) {
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
	
	public static boolean isJavaProject(IProject project) {
		try {
			return project.hasNature(JavaCore.NATURE_ID);
		} catch (CoreException e) {
			return false;
		}
	}
	
	public static boolean isMavenProject(IProject project) {
		try {
			return project.hasNature("org.eclipse.m2e.core.maven2Nature");
		} catch (CoreException e) {
			return false;
		}
	}
	
	public static boolean isQuarkusProject(IProject project) {
		return isJavaProject(project) && isQuarkusProject(JavaCore.create(project));
	}

	public static boolean isQuarkusProject(IJavaProject javaProject) {
		try {
			return javaProject.findType(QuarkusCoreConstants.QUARKUS_RUNTIME_CLASS_NAME) != null;
		} catch (JavaModelException e) {
			return false;
		}
	}
	
	public static boolean isApplicationYAML(IFile file) {
		return isQuarkusProject(file.getProject()) &&
				("application.yaml".equals(file.getName()) || "application.yml".equals(file.getName()));
	}
	
	public static ToolSupport getToolSupport(IProject project) {
		if (isMavenProject(project)) {
			return new MavenToolSupport(project);
		} else return new GradleToolSupport(project);
	}


	/**
	 * @param project the Eclipse project
	 * @return the path to the JRE/JDK attached to the project
	 * @throws CoreException 
	 */
	public static String getJREEntry(IProject project) throws CoreException {
		IJavaProject javaProject = JavaCore.create(project);
		for(IClasspathEntry cpe : javaProject.getRawClasspath()) {
			if (cpe.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
				IClasspathContainer container = JavaCore.getClasspathContainer(cpe.getPath(), javaProject);
				if (ClasspathContainersHelper.applies(container, ClasspathContainersHelper.JRE_CONTAINER_ID)) {
					return cpe.getPath().toPortableString();
					
				}
			}
		}
		return null;
	}
}
