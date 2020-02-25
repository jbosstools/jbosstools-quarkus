/*
 * Copyright 2020 Red Hat, Inc.
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
package org.jboss.tools.quarkus.core.code.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;

public class ProjectHelpers {
	/*
	 * Utility class should not be instantiated.
	 */
	private ProjectHelpers() {
		throw new IllegalStateException("Utility class");
	}
	
	public static IProject loadProject(File projectDir) throws CoreException, IOException, InterruptedException {
		// Move project to working directory
		File projectFolder = copyProjectToWorkingDirectory(projectDir);

		IPath path = new Path(new File(projectFolder, "/.project").getAbsolutePath());
		IProjectDescription description = ResourcesPlugin.getWorkspace().loadProjectDescription(path);
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
		if (!project.exists()) {
			project.create(description, null);
			project.open(null);

			// We need to call waitForBackgroundJobs with a Job which does nothing to have a
			// resolved classpath (IJavaProject#getResolvedClasspath) when search is done.
			IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
				@Override
				public void run(IProgressMonitor monitor) throws CoreException {
					monitor.done();

				}
			};
			IProgressMonitor monitor = new NullProgressMonitor();
			JavaCore.run(runnable, null, monitor);
			waitForBackgroundJobs(monitor);
		}
		// Collect Quarkus properties from the "hibernate-orm-resteasy" project. It
		// should collect Quarkus properties from given JAR:

		// 1) quarkus-hibernate-orm.jar which is declared in the dependencies of the
		// pom.xml
		// <dependency>
		// <groupId>io.quarkus</groupId>
		// <artifactId>quarkus-hibernate-orm</artifactId>
		// </dependency>

		// 2) quarkus-hibernate-orm-deployment.jar which is declared in
		// META-INF/quarkus-extension.properties of quarkus-hibernate-orm.jar as
		// property:
		// deployment-artifact=io.quarkus\:quarkus-hibernate-orm-deployment\:0.21.1

		return project;
	}

	private static File copyProjectToWorkingDirectory(File from) throws IOException {
		File to = new File(getWorkingProjectDirectory(), from.getName());

		if (to.exists()) {
			FileUtils.forceDelete(to);
		}

		if (from.isDirectory()) {
			FileUtils.copyDirectory(from, to);
		} else {
			FileUtils.copyFile(from, to);
		}

		return to;
	}

	private static void waitForBackgroundJobs(IProgressMonitor monitor) throws InterruptedException, CoreException {
		JobHelpers.waitForJobsToComplete(monitor);
	}
	
	public static File getWorkingProjectDirectory() throws IOException {
		File dir = new File("target", "workingProjects");
		FileUtils.forceMkdir(dir);
		return dir;
	}


}
