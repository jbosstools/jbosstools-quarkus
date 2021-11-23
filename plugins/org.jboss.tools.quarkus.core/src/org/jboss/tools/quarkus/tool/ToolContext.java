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
import java.util.Map;

import org.eclipse.core.resources.IProject;

/**
 * Represents the tool context.
 */
public interface ToolContext {
	/**
	 * The name of the execution
	 * 
	 * @return the name of the execution
	 */
	String getName();

	/**
	 * The Eclipse workspace project
	 */
	IProject getProject();

	/**
	 * The environment variables.
	 * 
	 * @return the environment variables
	 */
	Map<String, String> getEnvironment();
	
	/**
	 * Command specific extra arguments.
	 * 
	 * @return the command specific extra arguments
	 */
	Collection<String> getExtraArguments();
}
