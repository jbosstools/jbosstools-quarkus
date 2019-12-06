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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.eclipse.core.resources.ResourcesPlugin;


import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.quarkus.reddeer.perspective.QuarkusPerspective;
import org.jboss.tools.quarkus.reddeer.view.ExtensionsView;
import org.jboss.tools.quarkus.reddeer.wizard.CodeProjectTypeWizardPage;
import org.jboss.tools.quarkus.reddeer.wizard.QuarkusWizard;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author jkopriva@redhat.com
 */
@OpenPerspective(QuarkusPerspective.class)
@RunWith(RedDeerSuite.class)
public class InstallQuarkusExtensionTest {
	
	protected static final String WORKSPACE = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();

	@BeforeClass
	public static void prepareWorkspace() {
		QuarkusWizard qw = new QuarkusWizard();
		qw.open();
		assertTrue(qw.isOpen());

		CodeProjectTypeWizardPage wp = new CodeProjectTypeWizardPage(qw);
		wp.setProjectName("test");
		wp.setMavenProjectType();

		qw.next();
		qw.finish(TimePeriod.VERY_LONG);

		assertTrue(new ProjectExplorer().containsProject("test"));
	}

	@Test
	public void testAddQuarkusExtensionInProject() {
		new WorkbenchShell().setFocus();

		new ProjectExplorer().selectProjects("test");
		ExtensionsView ev = new ExtensionsView();
		ev.open();
		ev.getExtension("RESTEasy");
		new ContextMenuItem("Install extension").select();
		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);
		
		try {
			String pomContent = readFile(WORKSPACE + "/" + "test" + "/pom.xml");
			assertTrue(pomContent.contains("quarkus-resteasy"));
		} catch (IOException e) {
			e.printStackTrace();
			fail("Attempt to read the 'pom.xml' failed!");
		}
	}
	
	public static String readFile(String path) throws IOException {
		  FileInputStream stream = new FileInputStream(new File(path));
		  try {
		    FileChannel fc = stream.getChannel();
		    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		    /* Instead of using default, pass in a decoder. */
		    return Charset.defaultCharset().decode(bb).toString();
		  }
		  finally {
		    stream.close();
		  }
	}
}
