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
package org.jboss.tools.quarkus.integration.tests.common;

import static org.junit.Assert.assertTrue;

import org.eclipse.reddeer.eclipse.ui.perspectives.AbstractPerspective;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.jboss.tools.quarkus.reddeer.perspective.QuarkusPerspective;
import org.jboss.tools.quarkus.reddeer.view.ExtensionsView;
import org.jboss.tools.quarkus.reddeer.wizard.QuarkusWizard;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

/**
 * 
 * @author jkopriva@redhat.com
 */
@RunWith(RedDeerSuite.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PerspectiveTest {

	@Test
	public void openAPerspectiveTest() {
		AbstractPerspective ap = new QuarkusPerspective();
		ap.open();
		assertTrue(ap.isOpened());
	}

	@Test
	public void openBExtensionsViewTest() {
		ExtensionsView ev = new ExtensionsView();
		ev.open();
		assertTrue(ev.isOpen());
	}

	@Test
	public void openCFileMenuIsModifiedTest() {
		QuarkusWizard qw = new QuarkusWizard();
		qw.open();
		assertTrue(qw.isOpen());
		qw.cancel();
	}
}
