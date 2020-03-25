/******************************************************************************* 
 * Copyright (c) 2020 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.quarkus.integration.tests.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.eclipse.ui.problems.Problem;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView.ProblemType;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.impl.editor.TextEditor;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels.TextLabels;
import org.jboss.tools.quarkus.reddeer.wizard.CodeProjectTypeWizardPage;
import org.jboss.tools.quarkus.reddeer.wizard.QuarkusWizard;
import org.junit.After;

/**
 * 
 * @author olkornii@redhat.com
 *
 */
public abstract class AbstractCreateNewProjectTest {

	private static String jdkVersion11 = "11";
	private static String propertyName = "java.specification.version";
	
	private static String pomFile = "pom.xml";
	private static String gradleBuildFile = "build.gradle";

	private static String mavenCompilerSourceStart = "<maven.compiler.source>";
	private static String mavenCompilerSourceEnd = "</maven.compiler.source>";
	private static String mavenCompilerTargetStart = "<maven.compiler.target>";
	private static String mavenCompilerTargetEnd = "</maven.compiler.target>";

	private static String gradleCompilerSource = "sourceCompatibility = JavaVersion.VERSION_";
	private static String gradleCompilerTarget = "targetCompatibility = JavaVersion.VERSION_";

	public static void testCreateNewProject(String projectName, String projectType) {
		new WorkbenchShell().setFocus();

		QuarkusWizard qw = new QuarkusWizard();
		qw.open();
		assertTrue(qw.isOpen());

		CodeProjectTypeWizardPage wp = new CodeProjectTypeWizardPage(qw);
		wp.setProjectName(projectName);
		if (projectType.equals(TextLabels.MAVEN_TYPE)) {
			wp.setMavenProjectType();
		} else if (projectType.equals(TextLabels.GRADLE_TYPE)) {
			wp.setGradleProjectType();
		}

		qw.next();
		if (projectType.equals(TextLabels.GRADLE_TYPE)) {
			new LabeledText(TextLabels.ARTIFACT_ID).setText(projectName);
		}
		qw.finish(TimePeriod.VERY_LONG);

		assertTrue(new ProjectExplorer().containsProject(projectName));
	}

	public static void checkJdkVersion(String projectName, String projectType) {
		String javaVersion = System.getProperty(propertyName);
		boolean isFound = javaVersion.contains(jdkVersion11);

		String newSource;
		String newTarget;
		String actualSource;
		String actualTarget;
		String openFile;
		
		if (!isFound) {
			if (projectType.equals(TextLabels.GRADLE_TYPE)) {
				javaVersion = javaVersion.replaceAll("\\.", "_");
				newSource = gradleCompilerSource + javaVersion;
				newTarget = gradleCompilerTarget + javaVersion;
				actualSource = gradleCompilerSource + jdkVersion11;
				actualTarget = gradleCompilerTarget + jdkVersion11;
				openFile = gradleBuildFile;
			} else {
				newSource = mavenCompilerSourceStart + javaVersion + mavenCompilerSourceEnd;
				newTarget = mavenCompilerTargetStart + javaVersion + mavenCompilerTargetEnd;
				actualSource = mavenCompilerSourceStart + jdkVersion11 + mavenCompilerSourceEnd;
				actualTarget = mavenCompilerTargetStart + jdkVersion11 + mavenCompilerTargetEnd;
				openFile = pomFile;
			}
			changeVersion(projectName, projectType, openFile, actualSource, actualTarget, newSource, newTarget);
		}
	}

	private static void changeVersion(String projectName, String projectType, String openFile, String actualSource,
			String actualTarget, String newSource, String newTarget) {
		new ProjectExplorer().getProject(projectName).getProjectItem(openFile).select();
		new ContextMenuItem(TextLabels.OPEN_WITH, TextLabels.TEXT_EDITOR).select();

		TextEditor ed = new TextEditor(openFile);

		changeLine(ed, actualSource, newSource);
		changeLine(ed, actualTarget, newTarget);

		ed.save();
		new ProjectExplorer().selectProjects(projectName);

		if (projectType.equals(TextLabels.GRADLE_TYPE)) {
			new ContextMenuItem(TextLabels.GRADLE_CONTEXT_MENU_ITEM, TextLabels.REFRESH_GRADLE_PROJECT).select();
		} else {
			new ContextMenuItem(TextLabels.MAVEN_CONTEXT_MENU_ITEM, TextLabels.UPDATE_MAVEN_PROJECT).select();
			new PushButton("OK").click();
		}

		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);
	}

	private static void changeLine(TextEditor ed, String myActual, String myNew) {
		ed.selectText(myActual);
		int textPos = ed.getPositionOfText(myActual);
		new ContextMenuItem(TextLabels.CUT_CONTEXT_MENU_ITEM).select();
		ed.insertText(textPos, myNew);
	}

	public static void checkProblemsView() {
		ProblemsView problemsView = new ProblemsView();
		problemsView.open();
		List<Problem> problems = problemsView.getProblems(ProblemType.ERROR);
		assertEquals("There should be no errors in imported project", 0, problems.size());

	}

	@After
	public void deleteProject() {
		ProjectExplorer pe = new ProjectExplorer();
		pe.open();
		pe.deleteAllProjects(true);
	}
}
