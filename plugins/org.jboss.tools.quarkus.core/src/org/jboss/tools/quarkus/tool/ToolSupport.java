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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.output.NullOutputStream;
import org.eclipse.core.runtime.IPath;

/**
 * @author Red Hat Developers
 *
 */
public interface ToolSupport {
	
	/**
	 * Return the tool wrapper script name.
	 * 
	 * @return the wrapper script name
	 */
	String getScript();
	
	IPath getScriptPath();
	
	/**
	 * Return the list of arguments for adding a new extension.
	 * 
	 * @return
	 */
	List<String> getAddExtensionParameters(String extension);
	
	default void execute(File baseDir, List<String> args) throws IOException {
		CommandLine command = new CommandLine(getScriptPath().toOSString());
		command.addArguments(args.toArray(new String[args.size()]));
		PumpStreamHandler handler = new PumpStreamHandler(new NullOutputStream());
		Executor executor = new DefaultExecutor();
		executor.setWorkingDirectory(baseDir);
		executor.setStreamHandler(handler);
		executor.execute(command);
	}
}
