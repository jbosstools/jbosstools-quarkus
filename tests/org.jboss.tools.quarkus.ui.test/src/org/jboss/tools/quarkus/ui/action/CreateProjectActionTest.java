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

package org.jboss.tools.quarkus.ui.action;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.junit.jupiter.api.Test;

public class CreateProjectActionTest {

	@Test
	public void testRun() {
		IProject project = ResourcesPlugin
				.getWorkspace()
				.getRoot()
				.getProject("com.acme.quarkus");
		assertFalse(project.exists());
		Job job = Job.create("Run CreateProjectAction", ICoreRunnable -> {
			CreateProjectAction createProjectAction = 
					new CreateProjectAction(
							"com.acme.quarkus", 
							project.getLocation().toOSString(),
							"com.acme", 
							"quarkus", 
							"1.0.0-SHAPSHOT", 
							null,
							null);
			createProjectAction.run();
		});
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				assertTrue(project.exists());
			}
		});
		job.schedule();
	}
	
}
