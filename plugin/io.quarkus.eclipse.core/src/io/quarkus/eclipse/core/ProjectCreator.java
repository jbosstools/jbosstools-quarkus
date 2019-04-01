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

package io.quarkus.eclipse.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.MavenModelManager;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.LocalProjectScanner;
import org.eclipse.m2e.core.project.MavenProjectInfo;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;

import io.quarkus.cli.commands.CreateProject;

public class ProjectCreator {
	
	public static void create(			
			String name, 
			String groupId, 
			String artefactId, 
			String version, 
			HashMap<String, Object> context) {
		try {
			if (context == null) {
				context = new HashMap<String, Object>();
			}
			File workspaceFolder = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
			File projectFolder = new File(workspaceFolder, name);
			new CreateProject(projectFolder)
					.groupId(groupId)
					.artifactId(artefactId)
					.version(version)
					.doCreateProject(context);;
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

}
