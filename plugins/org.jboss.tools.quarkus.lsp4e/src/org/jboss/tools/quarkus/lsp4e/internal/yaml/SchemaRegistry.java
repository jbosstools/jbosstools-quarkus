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
package org.jboss.tools.quarkus.lsp4e.internal.yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.lsp4e.LSPEclipseUtils;
import org.eclipse.lsp4e.LanguageServers;
import org.eclipse.lsp4e.LanguageServersRegistry;
import org.eclipse.lsp4e.LanguageServersRegistry.LanguageServerDefinition;
import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4mp.commons.ClasspathKind;
import org.eclipse.lsp4mp.commons.DocumentFormat;
import org.eclipse.lsp4mp.commons.MicroProfileProjectInfo;
import org.eclipse.lsp4mp.commons.MicroProfilePropertiesChangeEvent;
import org.eclipse.lsp4mp.commons.MicroProfilePropertiesScope;
import org.eclipse.lsp4mp.jdt.core.IMicroProfilePropertiesChangedListener;
import org.eclipse.lsp4mp.jdt.core.PropertiesManager;
import org.eclipse.lsp4mp.jdt.internal.core.MicroProfilePropertiesListenerManager;
import org.eclipse.lsp4mp.utils.JSONSchemaUtils;
import org.jboss.tools.quarkus.lsp4e.QuarkusLSPPlugin;
import org.jboss.tools.quarkus.lsp4e.internal.ls.JDTUtilsImpl;

/**
 * @author Red Hat Developers
 *
 */
public class SchemaRegistry implements IMicroProfilePropertiesChangedListener, IResourceChangeListener {

	private static SchemaRegistry INSTANCE = new SchemaRegistry();

	public static SchemaRegistry getInstance() {
		return INSTANCE;
	}

	private ConcurrentHashMap<IProject, MutablePair<File, Boolean>> schemas = new ConcurrentHashMap<>();

	/**
	 * 
	 */
	private SchemaRegistry() {
		MicroProfilePropertiesListenerManager.getInstance().addMicroProfilePropertiesChangedListener(this);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.PRE_CLOSE);
	}

	public void updateYAMLLanguageServerConfigIfRequired(IProject project) {
		try {
			MutablePair<File, Boolean> schemaEntry = schemas.get(project);
			if (schemaEntry == null || !schemaEntry.getRight()) {
				File schemaFile = computeSchema(project, schemaEntry != null ? schemaEntry.getLeft() : null);
				if (schemaEntry != null) {
					schemaEntry.setRight(Boolean.TRUE);
				} else {
					schemaEntry = new MutablePair<>(schemaFile, Boolean.TRUE);
					schemas.put(project, schemaEntry);
				}
			}
			sendInitialize(project);
		} catch (CoreException | IOException e) {
			QuarkusLSPPlugin.logException(e.getLocalizedMessage(), e);
		}
	}

	private void sendInitialize(IProject project) throws IOException {
		LanguageServerDefinition def = LanguageServersRegistry.getInstance()
				.getDefinition("org.eclipse.wildwebdeveloper.yaml");
		Map<String, Object> settings = new HashMap<>();
		Map<String, Object> yaml = new HashMap<>();
		yaml.put("schemas", schemas2YAMLLSMap());
		settings.put("yaml", yaml);

		DidChangeConfigurationParams params = new DidChangeConfigurationParams(settings);
		LanguageServers.forProject(project).withPreferredServer(def).excludeInactive()
				.collectAll((w, ls) -> CompletableFuture.completedFuture(ls))
				.thenAccept(lss -> lss.stream().forEach(ls -> ls.getWorkspaceService().didChangeConfiguration(params)));
	}

	private Map<String, Object> schemas2YAMLLSMap() {
		Map<String, Object> config = new HashMap<>();
		schemas.forEach(
				(project, entry) -> config.put(LSPEclipseUtils.toUri(entry.getLeft()).toString(), "application.y*ml"));
		return config;
	}

	/**
	 * @param project
	 * @return
	 * @throws IOException
	 */
	private File computeSchema(IProject project, File f) throws CoreException, IOException {
		File tmpFile;
		if (f == null) {
			tmpFile = File.createTempFile(project.getName() + "-schema", ".json");
		} else {
			tmpFile = f;
		}
		MicroProfileProjectInfo projectInfo = PropertiesManager.getInstance().getMicroProfileProjectInfo(
				JavaCore.create(project), MicroProfilePropertiesScope.SOURCES_AND_DEPENDENCIES, ClasspathKind.SRC,
				JDTUtilsImpl.getInstance(), DocumentFormat.Markdown, new NullProgressMonitor());

		String schemaStr = JSONSchemaUtils.toJSONSchema(projectInfo, false);
		try (Writer w = new FileWriter(tmpFile)) {
			IOUtils.write(schemaStr, w);
		}
		return tmpFile;
	}

	@Override
	public void propertiesChanged(MicroProfilePropertiesChangeEvent event) {
		List<IProject> projects = new ArrayList<>();
		for (String projectURI : event.getProjectURIs()) {
			IContainer[] containers = ResourcesPlugin.getWorkspace().getRoot()
					.findContainersForLocationURI(new Path(projectURI).toFile().toURI());
			for (IContainer container : containers) {
				if (container instanceof IProject) {
					schemas.computeIfPresent((IProject) container, (p, e) -> {
						e.setRight(Boolean.FALSE);
						projects.add((IProject) container);
						return e;
					});
				}
			}
		}
		if (!projects.isEmpty()) {
			for (IProject project : projects) {
				updateYAMLLanguageServerConfigIfRequired(project);
			}
		}
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		if (event.getResource() instanceof IProject) {
			schemas.remove(event.getResource());
		}
	}

	private class YamlConfig extends HashMap<String, Object> {

		Map<String, List<String>> schemas;

	}
}
