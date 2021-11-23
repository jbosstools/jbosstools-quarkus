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

/**
 * Represents the run/debug execution context.
 */
public interface ToolExecutionContext extends ToolContext {
	/**
	 * The Quarkus profile or null.
	 * 
	 * @return the Quarkus profile or null
	 */
	String getProfile();

	/**
	 * true if debug is required, false otherwise
	 * @return true if debug is required, false otherwise
	 */
	boolean isDebug();

	/**
	 * If debug is on the debug port to use
	 * @return the debug port to use if debug is on 
	 */
	int getDebugPort();
}
