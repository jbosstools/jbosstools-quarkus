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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.eclipse.core.resources.IProject;

public class DefaultToolContext implements ToolContext {
	private final String name;
	private final IProject project;
	private final Map<String, String> environment;
	private final Collection<String> extraArguments;

	public DefaultToolContext(String name, IProject project, Map<String, String> environment, Collection<String> extraArguments) {
		this.name = name;
		this.project = project;
		this.environment = environment;
		this.extraArguments = extraArguments;
	}

	public DefaultToolContext(String name, IProject project) {
		this(name, project, Collections.emptyMap(), Collections.emptyList());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public Map<String, String> getEnvironment() {
		return environment;
	}

	@Override
	public Collection<String> getExtraArguments() {
		return extraArguments;
	}
}
