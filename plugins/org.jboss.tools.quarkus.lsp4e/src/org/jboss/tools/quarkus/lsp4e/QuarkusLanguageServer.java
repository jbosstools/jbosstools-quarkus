/******************************************************************************* 
 * Copyright (c) 2019 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.quarkus.lsp4e;

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

/**
 * Quarkus language server.
 * 
 * @author Angelo ZERR
 *
 */
public class QuarkusLanguageServer extends ProcessStreamConnectionProvider {

	public QuarkusLanguageServer() {
		List<String> commands = new ArrayList<>();
		commands.add(computeJavaPath());
		commands.add("-classpath");
		try {
			commands.add(computeClasspath());
			commands.add("com.redhat.microprofile.ls.MicroProfileServerLauncher");
			setCommands(commands);
			setWorkingDirectory(System.getProperty("user.dir"));
		} catch (IOException e) {
			QuarkusLSPPlugin.getDefault().getLog().log(new Status(IStatus.ERROR,
					QuarkusLSPPlugin.getDefault().getBundle().getSymbolicName(), e.getMessage(), e));
		}
	}
	
	private String computeClasspath() throws IOException {
	  StringBuilder builder = new StringBuilder();
    URL url = FileLocator.toFileURL(getClass().getResource("/server/com.redhat.microprofile.ls-uber.jar"));
    builder.append(new java.io.File(url.getPath()).getAbsolutePath());
    builder.append(File.pathSeparatorChar);
    url = FileLocator.toFileURL(getClass().getResource("/server/com.redhat.quarkus.ls.jar"));
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
		QuarkusCoreUsageStats.getInstance().startLS();
	}

	@Override
	public Object getInitializationOptions(URI rootUri) {
		/*QuarkusGeneralClientSettings settings = new QuarkusGeneralClientSettings();
		QuarkusCodeLensSettings codeLensSettings = new QuarkusCodeLensSettings();
		codeLensSettings.setUrlCodeLensEnabled(true);
		settings.setCodeLens(codeLensSettings);
		return settings;*/
		Map<String, Object> root = new HashMap<>();
		Map<String, Object> settings = new HashMap<>();
		Map<String, Object> quarkus = new HashMap<>();
		Map<String, Object> tools = new HashMap<>();
		Map<String, Object> trace = new HashMap<>();
		trace.put("server", "verbose");
		tools.put("trace", trace);
		Map<String, Object> codeLens = new HashMap<>();
		codeLens.put("urlCodeLensEnabled", "true");
		tools.put("codeLens", codeLens);
		quarkus.put("tools", tools);
		settings.put("quarkus", quarkus);
		root.put("settings", settings);
		Map<String, Object> extendedClientCapabilities = new HashMap<>();
		Map<String, Object> commands = new HashMap<>();
		Map<String, Object> commandsKind = new HashMap<>();
		commandsKind.put("valueSet", Arrays.asList("quarkus.command.configuration.update", "quarkus.command.open.uri"));
		commands.put("commandsKind", commandsKind);
		extendedClientCapabilities.put("commands", commands);
		root.put("extendedClientCapabilities", extendedClientCapabilities);
		return root;
	}

	@Override
	public String toString() {
		return "Quarkus Language Server: " + super.toString();
	}

}