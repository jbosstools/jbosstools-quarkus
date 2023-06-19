/*******************************************************************************
 * Copyright (c) 2022 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.quarkus.lsp4e.internal.ls.qute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.INodeChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.NodeChangeEvent;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.lsp4e.LanguageClientImpl;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DocumentLink;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;
import org.eclipse.lsp4j.jsonrpc.CompletableFutures;
import org.jboss.tools.quarkus.lsp4e.QuarkusLSPPlugin;
import org.jboss.tools.quarkus.lsp4e.internal.ls.JDTUtilsImpl;
import org.jboss.tools.quarkus.lsp4e.internal.qute.QuteUtils;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.redhat.qute.commons.GenerateMissingJavaMemberParams;
import com.redhat.qute.commons.JavaTypeInfo;
import com.redhat.qute.commons.ProjectInfo;
import com.redhat.qute.commons.QuteJavaCodeLensParams;
import com.redhat.qute.commons.QuteJavaDefinitionParams;
import com.redhat.qute.commons.QuteJavaDiagnosticsParams;
import com.redhat.qute.commons.QuteJavaDocumentLinkParams;
import com.redhat.qute.commons.QuteJavaTypesParams;
import com.redhat.qute.commons.QuteJavadocParams;
import com.redhat.qute.commons.QuteProjectParams;
import com.redhat.qute.commons.QuteResolvedJavaTypeParams;
import com.redhat.qute.commons.ResolvedJavaTypeInfo;
import com.redhat.qute.commons.datamodel.DataModelParameter;
import com.redhat.qute.commons.datamodel.DataModelProject;
import com.redhat.qute.commons.datamodel.DataModelTemplate;
import com.redhat.qute.commons.datamodel.QuteDataModelProjectParams;
import com.redhat.qute.commons.usertags.QuteUserTagParams;
import com.redhat.qute.commons.usertags.UserTagInfo;
import com.redhat.qute.jdt.IJavaDataModelChangedListener;
import com.redhat.qute.jdt.QutePlugin;
import com.redhat.qute.jdt.QuteSupportForJava;
import com.redhat.qute.jdt.QuteSupportForTemplate;
import com.redhat.qute.ls.api.QuteLanguageClientAPI;
import com.redhat.qute.ls.api.QuteLanguageServerAPI;

/**
 * @author Red Hat Developers
 *
 */
public class QuteLanguageClient extends LanguageClientImpl implements QuteLanguageClientAPI, IPreferenceChangeListener, INodeChangeListener {

	private static IJavaDataModelChangedListener SINGLETON_LISTENER;
	
	private static QuteLanguageClient SINGLETON_CLIENT;
	
	private IJavaDataModelChangedListener listener = event -> {
		((QuteLanguageServerAPI) getLanguageServer()).dataModelChanged(event);
	};

	private List<IEclipsePreferences> preferencesNodes = new ArrayList<>();
	
	/**
	 * 
	 */
	public QuteLanguageClient() {
		// FIXME : how to remove the listener????
		// The listener should be removed when language server is shutdown, how to
		// manage that????
		if (SINGLETON_LISTENER != null) {
			QutePlugin.getDefault().removeJavaDataModelChangedListener(SINGLETON_LISTENER);
		}
		if (SINGLETON_CLIENT != null) {
			SINGLETON_CLIENT.dispose();
		}
		SINGLETON_LISTENER = listener;
		SINGLETON_CLIENT = this;
		QutePlugin.getDefault().addJavaDataModelChangedListener(listener);
		listenForPreferences();
	}

	/**
	 * 
	 */
	private void dispose() {
		InstanceScope.INSTANCE.getNode(QuarkusLSPPlugin.PREFERENCES_QUALIFIER).removePreferenceChangeListener(this);
		((IEclipsePreferences) Platform.getPreferencesService().getRootNode().node(ProjectScope.SCOPE)).removeNodeChangeListener(this);
		preferencesNodes.forEach(node -> node.removePreferenceChangeListener(this));
	}

	/**
	 * In order to send configuration change notification, we should detect changes on Qute configuration. To do so,
	 * we listen on:
	 * - general Qute configuration
	 * - new nodes on project node (new projects being added)
	 * - for each project listen on the Qute prefences
	 * 
	 *  #see {@link #dispose()}
	 */
	private void listenForPreferences() {
		InstanceScope.INSTANCE.getNode(QuarkusLSPPlugin.PREFERENCES_QUALIFIER).addPreferenceChangeListener(this);
		((IEclipsePreferences) Platform.getPreferencesService().getRootNode().node(ProjectScope.SCOPE)).addNodeChangeListener(this);
		try {
			Platform.getPreferencesService().getRootNode().accept(node -> {
				if (node.parent() != null && node.parent().absolutePath().equals("/"+ ProjectScope.SCOPE)) {
					Preferences child = node.node(QuarkusLSPPlugin.PREFERENCES_QUALIFIER);
					((IEclipsePreferences) child).addPreferenceChangeListener(this);
					preferencesNodes.add((IEclipsePreferences) child);
					return false;
				}
				return true;
			});
		} catch (BackingStoreException e) {
			QuarkusLSPPlugin.logException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public CompletableFuture<ProjectInfo> getProjectInfo(QuteProjectParams params) {
		return CompletableFutures.computeAsync((cancelChecker) -> {
			IProgressMonitor monitor = getProgressMonitor(cancelChecker);
			return QuteSupportForTemplate.getInstance().getProjectInfo(params, JDTUtilsImpl.getInstance(), monitor);
		});		
	}
	
	@Override
	public CompletableFuture<DataModelProject<DataModelTemplate<DataModelParameter>>> getDataModelProject(
			QuteDataModelProjectParams params) {
		return CompletableFutures.computeAsync((cancelChecker) -> {
			try {
				IProgressMonitor monitor = getProgressMonitor(cancelChecker);
				return QuteSupportForTemplate.getInstance().getDataModelProject(params, JDTUtilsImpl.getInstance(),
						monitor);
			} catch (CoreException e) {
				QuarkusLSPPlugin.logException(e.getLocalizedMessage(), e);
				return null;
			}
		});
	}

	@Override
	public CompletableFuture<List<JavaTypeInfo>> getJavaTypes(QuteJavaTypesParams params) {
		return CompletableFutures.computeAsync((cancelChecker) -> {
			try {
				IProgressMonitor monitor = getProgressMonitor(cancelChecker);
				return QuteSupportForTemplate.getInstance().getJavaTypes(params, JDTUtilsImpl.getInstance(), monitor);
			} catch (CoreException e) {
				QuarkusLSPPlugin.logException(e.getLocalizedMessage(), e);
				return Collections.emptyList();
			}
		});
	}

	@Override
	public CompletableFuture<ResolvedJavaTypeInfo> getResolvedJavaType(QuteResolvedJavaTypeParams params) {
		return CompletableFutures.computeAsync((cancelChecker) -> {
			try {
				IProgressMonitor monitor = getProgressMonitor(cancelChecker);
				return QuteSupportForTemplate.getInstance().getResolvedJavaType(params, JDTUtilsImpl.getInstance(),
						monitor);
			} catch (CoreException e) {
				QuarkusLSPPlugin.logException(e.getLocalizedMessage(), e);
				return null;
			}
		});		
	}

	@Override
	public CompletableFuture<Location> getJavaDefinition(QuteJavaDefinitionParams params) {
		return CompletableFutures.computeAsync((cancelChecker) -> {
			try {
				IProgressMonitor monitor = getProgressMonitor(cancelChecker);
				return QuteSupportForTemplate.getInstance().getJavaDefinition(params, JDTUtilsImpl.getInstance(),
						monitor);
			} catch (CoreException e) {
				QuarkusLSPPlugin.logException(e.getLocalizedMessage(), e);
				return null;
			}
		});
	}
	
	@Override
	public CompletableFuture<List<? extends CodeLens>> getJavaCodelens(QuteJavaCodeLensParams javaParams) {
		return CompletableFutures.computeAsync((cancelChecker) -> {
			IProgressMonitor monitor = getProgressMonitor(cancelChecker);
			return QuteSupportForJava.getInstance().codeLens(javaParams, JDTUtilsImpl.getInstance(), monitor);
		});
	}

	@Override
	public CompletableFuture<List<PublishDiagnosticsParams>> getJavaDiagnostics(QuteJavaDiagnosticsParams javaParams) {
		return CompletableFutures.computeAsync((cancelChecker) -> {
			IProgressMonitor monitor = getProgressMonitor(cancelChecker);
			return QuteSupportForJava.getInstance().diagnostics(javaParams, JDTUtilsImpl.getInstance(), monitor);
		});
	}

	@Override
	public CompletableFuture<List<DocumentLink>> getJavaDocumentLink(QuteJavaDocumentLinkParams javaParams) {
		return CompletableFutures.computeAsync((cancelChecker) -> {
			IProgressMonitor monitor = getProgressMonitor(cancelChecker);
			return QuteSupportForJava.getInstance().documentLink(javaParams, JDTUtilsImpl.getInstance(), monitor);
		});
	}

	@Override
	public CompletableFuture<List<UserTagInfo>> getUserTags(QuteUserTagParams params) {
		return CompletableFutures.computeAsync((cancelChecker) -> {
			try {
				IProgressMonitor monitor = getProgressMonitor(cancelChecker);
				return QuteSupportForTemplate.getInstance().getUserTags(params, JDTUtilsImpl.getInstance(), monitor);
			} catch (CoreException e) {
				QuarkusLSPPlugin.logException(e.getLocalizedMessage(), e);
				return Collections.emptyList();
			}
		});
	}
	
	@Override
	public CompletableFuture<String> getJavadoc(QuteJavadocParams arg0) {
		return CompletableFuture.completedFuture(null);
	}
	
	@Override
	public CompletableFuture<WorkspaceEdit> generateMissingJavaMember(GenerateMissingJavaMemberParams arg0) {
		return CompletableFuture.completedFuture(null);
	}

	private static IProgressMonitor getProgressMonitor(CancelChecker cancelChecker) {
		IProgressMonitor monitor = new NullProgressMonitor() {
			public boolean isCanceled() {
				cancelChecker.checkCanceled();
				return false;
			};
		};
		return monitor;
	}

	@Override
	public void added(NodeChangeEvent event) {
		Preferences child = event.getChild().node(QuarkusLSPPlugin.PREFERENCES_QUALIFIER);
		((IEclipsePreferences) child).addPreferenceChangeListener(this);
		preferencesNodes.add((IEclipsePreferences) child);
	}

	@Override
	public void removed(NodeChangeEvent event) {
		Preferences child = event.getChild().node(QuarkusLSPPlugin.PREFERENCES_QUALIFIER);
		((IEclipsePreferences) child).removePreferenceChangeListener(this);
		preferencesNodes.remove((IEclipsePreferences) child);
	}

	@Override
	public void preferenceChange(PreferenceChangeEvent event) {
		getLanguageServer().getWorkspaceService().didChangeConfiguration(new DidChangeConfigurationParams(QuteUtils.getQuteSettings()));
	}
}
