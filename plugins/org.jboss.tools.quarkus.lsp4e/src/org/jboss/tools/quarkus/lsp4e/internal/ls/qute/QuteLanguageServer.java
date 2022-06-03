/******************************************************************************* 
 * Copyright (c) 2022 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.quarkus.lsp4e.internal.ls.qute;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.lsp4e.server.ProcessStreamConnectionProvider;
import org.jboss.tools.quarkus.core.QuarkusCoreUsageStats;
import org.jboss.tools.quarkus.lsp4e.QuarkusLSPPlugin;
import org.jboss.tools.quarkus.lsp4e.internal.qute.QuteUtils;

/**
 * Quarkus language server.
 * 
 * @author Angelo ZERR
 *
 */
public class QuteLanguageServer extends ProcessStreamConnectionProvider {

	public QuteLanguageServer() {
		List<String> commands = new ArrayList<>();
		commands.add(computeJavaPath());
		commands.add("-classpath");
		try {
			commands.add(computeClasspath());
			commands.add("com.redhat.qute.ls.QuteServerLauncher");
			setCommands(commands);
			setWorkingDirectory(System.getProperty("user.dir"));
		} catch (IOException e) {
			QuarkusLSPPlugin.getDefault().getLog().log(new Status(IStatus.ERROR,
					QuarkusLSPPlugin.getDefault().getBundle().getSymbolicName(), e.getMessage(), e));
		}
	}
	
	private String computeClasspath() throws IOException {
		StringBuilder builder = new StringBuilder();
		URL url = FileLocator.toFileURL(getClass().getResource("/server/com.redhat.qute.ls-uber.jar"));
		builder.append(new java.io.File(url.getPath()).getAbsolutePath());
		return builder.toString();
	}
	
	private String computeJavaPath() {
		String javaPath = "java";
		boolean existsInPath = Stream.of(System.getenv("PATH").split(Pattern.quote(File.pathSeparator))).map(Paths::get)
				.anyMatch(path -> Files.exists(path.resolve("java")));
		if (!existsInPath) {
			File f = new File(System.getProperty("java.home"),
					"bin/java" + (Platform.getOS().equals(Platform.OS_WIN32) ? ".exe" : ""));
			javaPath = f.getAbsolutePath();
		}
		return javaPath;
	}

	@Override
	public void start() throws IOException {
		super.start();
		QuarkusCoreUsageStats.getInstance().startQuteLS();
	}

	@Override
	public Object getInitializationOptions(URI rootUri) {
		Map<String, Object> root = new HashMap<>();
		Map<String, Object> settings = QuteUtils.getQuteSettings();
		Map<String, Object> extendedClientCapabilities = new HashMap<>();
		Map<String, Object> commands = new HashMap<>();
		Map<String, Object> commandsKind = new HashMap<>();
		commandsKind.put("valueSet", Arrays.asList("qute.command.java.definition", "qute.command.configuration.update", "qute.command.open.uri"));
		commands.put("commandsKind", commandsKind);
		extendedClientCapabilities.put("commands", commands);
	    extendedClientCapabilities.put("shouldLanguageServerExitOnShutdown", Boolean.TRUE);
		root.put("extendedClientCapabilities", extendedClientCapabilities);
		root.put("settings", settings);
		return root;
	}

	@Override
	public String toString() {
		return "Qute Language Server: " + super.toString();
	}
}