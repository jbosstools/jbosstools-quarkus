/*******************************************************************************
 * Copyright (c) 2021 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.quarkus.tool;

import java.util.Collections;
import java.util.Map;

import org.eclipse.core.resources.IProject;

public class DefaultToolExecutionContext extends DefaultToolContext implements ToolExecutionContext {
	private final String profile;
	private final boolean debug;
	private final int debugPort;

	/* (non-Javadoc)
	 * @see org.jboss.tools.quarkus.tool.ToolContext#getProject()
	 */

	public DefaultToolExecutionContext(String name, IProject project, Map<String, String> environment, 
			String profile, boolean debug, int debugPort) {
		super(name, project, environment, Collections.emptyList());
		this.profile = profile;
		this.debug = debug;
		this.debugPort = debugPort;
	}

	@Override
	public String getProfile() {
		return profile;
	}

	@Override
	public boolean isDebug() {
		return debug;
	}

	@Override
	public int getDebugPort() {
		return debugPort;
	}
}
