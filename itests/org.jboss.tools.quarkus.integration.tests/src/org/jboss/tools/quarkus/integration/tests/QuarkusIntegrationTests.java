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
package org.jboss.tools.quarkus.integration.tests;

import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_ENDPOINT_URL_PROPERTY_NAME;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_ENDPOINT_URL_TEST;

import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.jboss.tools.quarkus.integration.tests.common.PerspectiveTest;
import org.jboss.tools.quarkus.integration.tests.content.assistant.ApplicationPropertiesContentAssistTest;
import org.jboss.tools.quarkus.integration.tests.content.assistant.ApplicationPropertiesNewExtensionContentAssistTest;
import org.jboss.tools.quarkus.integration.tests.launch.configuration.CreateNewQuarkusConfigurationGradleTest;
import org.jboss.tools.quarkus.integration.tests.launch.configuration.CreateNewQuarkusConfigurationMavenTest;
import org.jboss.tools.quarkus.integration.tests.project.CreateNewProjectTest;
import org.jboss.tools.quarkus.integration.tests.project.InstallQuarkusExtensionTest;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * <b>Quarkus All Tests suite</b>
 * 
 * @author jkopriva@redhat.com
 */
@RunWith(RedDeerSuite.class)
@SuiteClasses({
	PerspectiveTest.class,
	ApplicationPropertiesContentAssistTest.class,
	ApplicationPropertiesNewExtensionContentAssistTest.class,
	CreateNewProjectTest.class,
	CreateNewQuarkusConfigurationMavenTest.class,
	CreateNewQuarkusConfigurationGradleTest.class,
	InstallQuarkusExtensionTest.class,
	
})
public class QuarkusIntegrationTests {
	@BeforeClass
	public static void setup() {
		System.setProperty(CODE_ENDPOINT_URL_PROPERTY_NAME, CODE_ENDPOINT_URL_TEST);
	}
	
}