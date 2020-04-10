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
package org.jboss.tools.quarkus.tool;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Platform;

/**
 * @author Red Hat Developers
 *
 */
public class MavenToolSupport extends AbstractToolSupport {

	public MavenToolSupport(IProject project) {
		super(project);
	}

	@Override
	public String getScript() {
		if (Platform.OS_WIN32.contentEquals(Platform.getOS())) {
			return "mvnw.cmd";
		} else {
			return "mvnw";
		}
	}

	@Override
	public List<String> getAddExtensionParameters(String extension) {
		return Arrays.asList("quarkus:add-extension", "-Dextension=" + extension);
	}
}
