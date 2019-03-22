package io.quarkus.eclipse.ui.action;

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
							"com.acme", 
							"quarkus", 
							"1.0.0-SHAPSHOT", 
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
