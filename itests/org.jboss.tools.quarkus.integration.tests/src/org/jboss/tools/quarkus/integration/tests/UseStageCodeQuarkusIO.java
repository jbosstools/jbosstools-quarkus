/*******************************************************************************
 * Copyright (c) 2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.quarkus.integration.tests;

import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_ENDPOINT_URL_PROPERTY_NAME;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_ENDPOINT_URL_TEST;

import org.eclipse.reddeer.junit.extensionpoint.IBeforeTest;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

/**
 * @author Red Hat Developers
 *
 */
public class UseStageCodeQuarkusIO implements IBeforeTest {

	@Override
	public long getPriority() {
		return Long.MAX_VALUE;
	}

	@Override
	public void runBeforeTestClass(String config, TestClass testClass) {
		System.setProperty(CODE_ENDPOINT_URL_PROPERTY_NAME, CODE_ENDPOINT_URL_TEST);
	}

	@Override
	public void runBeforeTest(String config, Object target, FrameworkMethod method) {
	}

	@Override
	public boolean hasToRun() {
		return true;
	}
}
