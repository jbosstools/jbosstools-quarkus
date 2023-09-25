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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

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
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.lsp4e.LSPEclipseUtils;
import org.eclipse.lsp4e.LanguageServerWrapper;
import org.eclipse.lsp4e.LanguageServiceAccessor;
import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4mp.commons.ClasspathKind;
import org.eclipse.lsp4mp.commons.DocumentFormat;
import org.eclipse.lsp4mp.commons.MicroProfileProjectInfo;
import org.eclipse.lsp4mp.commons.MicroProfilePropertiesChangeEvent;
import org.eclipse.lsp4mp.commons.MicroProfilePropertiesScope;
import org.eclipse.lsp4mp.jdt.core.IMicroProfilePropertiesChangedListener;
import org.eclipse.lsp4mp.jdt.core.PropertiesManager;
import org.eclipse.lsp4mp.jdt.internal.core.MicroProfilePropertiesListenerManager;
import org.eclipse.lsp4mp.utils.JSONSchemaUtils;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.jboss.tools.quarkus.lsp4e.QuarkusLSPPlugin;
import org.jboss.tools.quarkus.lsp4e.internal.ls.JDTUtilsImpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

	private void updateYAMLLanguageServerConfigIfRequired(IProject project, boolean sendToServer) {
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
			if (sendToServer) {
				sendInitialize();
			}
		} catch (CoreException | IOException e) {
			QuarkusLSPPlugin.logException(e.getLocalizedMessage(), e);
		}
	}

	public void updateYAMLLanguageServerConfigIfRequired(IProject project) {
		updateYAMLLanguageServerConfigIfRequired(project, true);
	}

	private void sendInitialize() throws IOException {
		List<LanguageServerWrapper> servers = LanguageServiceAccessor.getLSWrappers(null, null);
		for (LanguageServerWrapper server : servers) {
			if (server.serverDefinition != null) {
				if ("org.eclipse.wildwebdeveloper.yaml".equals(server.serverDefinition.id)) {
					Map<String, Object> prefs = new HashMap<>();
					prefs.put("schemas", schemas2YAMLLSMap());
					prefs.put("completion", true);
					prefs.put("hover", true);
					prefs.put("validate", true);
					DidChangeConfigurationParams params = new DidChangeConfigurationParams(
							Collections.singletonMap("yaml", prefs));
					Function<LanguageServer, CompletableFuture<Void>> fn = new Function<>() {

						@Override
						public CompletableFuture<Void> apply(LanguageServer ls) {
							ls.getWorkspaceService().didChangeConfiguration(params);
							return CompletableFuture.completedFuture(null);
						}
					};
					server.execute(fn);
				}
			}
		}
	}

	private Map<String, Object> schemas2YAMLLSMap() {
		Map<String, Object> config = loadFromYAMLLS();
		schemas.forEach(
				(project, entry) -> config.put(LSPEclipseUtils.toUri(entry.getLeft()).toString(), getPattern(project)));
		return config;
	}

	private String[] getPattern(IProject project) {
		String[] patterns = new String[2];
		String projectRoot = LSPEclipseUtils.toUri(project).toString();
		patterns[0] = projectRoot + "*/application.yaml";
		patterns[1] = projectRoot + "*/application.yml";
		return patterns;
	}

	/**
	 * Get the YAML LS default schemas config.
	 * 
	 * @see <a href=
	 *      "https://github.com/eclipse/wildwebdeveloper/blob/master/org.eclipse.wildwebdeveloper/src/org/eclipse/wildwebdeveloper/yaml/YAMLLanguageServer.java"/>https://github.com/eclipse/wildwebdeveloper/blob/master/org.eclipse.wildwebdeveloper/src/org/eclipse/wildwebdeveloper/yaml/YAMLLanguageServer.java
	 * @return
	 */
	private Map<String, Object> loadFromYAMLLS() {
		IPreferenceStore preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE,
				"org.eclipse.wildwebdeveloper");
		String schemaStr = preferenceStore.getString("wildwebdeveloper.yaml.schema");
		if (schemaStr != null) {
			Map<String, Object> result = new Gson().fromJson(schemaStr, new TypeToken<HashMap<String, Object>>() {
			}.getType());
			return result == null ? Collections.emptyMap() : result;
		}
		return Collections.emptyMap();
	}

	/**
	 * @param project
	 * @return
	 * @throws IOException
	 */
	private File computeSchema(IProject project, File f) throws CoreException, IOException {
		if (f == null) {
			f = File.createTempFile(project.getName() + "-schema", ".json");
		}
		MicroProfileProjectInfo projectInfo = PropertiesManager.getInstance().getMicroProfileProjectInfo(
				JavaCore.create(project), MicroProfilePropertiesScope.SOURCES_AND_DEPENDENCIES, ClasspathKind.SRC,
				JDTUtilsImpl.getInstance(), DocumentFormat.Markdown, new NullProgressMonitor());

		String schemaStr = JSONSchemaUtils.toJSONSchema(projectInfo, false);
		try (Writer w = new FileWriter(f)) {
			IOUtils.write(schemaStr, w);
		}
		return f;
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
		for (IProject project : projects) {
			updateYAMLLanguageServerConfigIfRequired(project, false);
		}
		if (!projects.isEmpty()) {
			try {
				sendInitialize();
			} catch (IOException e) {
				QuarkusLSPPlugin.logException(e.getLocalizedMessage(), e);
			}
		}
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		if (event.getResource() instanceof IProject) {
			schemas.remove(event.getResource());
		}
	}
}
