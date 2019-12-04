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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4e.LanguageClientImpl;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;
import org.eclipse.lsp4j.jsonrpc.CompletableFutures;
import org.jboss.tools.quarkus.lsp4e.internal.JDTUtils;
import org.jboss.tools.quarkus.lsp4e.internal.JDTUtilsImpl;

import com.redhat.quarkus.commons.QuarkusJavaCodeLensParams;
import com.redhat.quarkus.commons.QuarkusProjectInfo;
import com.redhat.quarkus.commons.QuarkusProjectInfoParams;
import com.redhat.quarkus.commons.QuarkusPropertiesScope;
import com.redhat.quarkus.commons.QuarkusPropertyDefinitionParams;
import com.redhat.quarkus.jdt.core.DocumentationConverter;
import com.redhat.quarkus.jdt.core.IQuarkusPropertiesChangedListener;
import com.redhat.quarkus.jdt.core.JDTQuarkusManager;
import com.redhat.quarkus.jdt.core.JDTQuarkusManagerForJava;
import com.redhat.quarkus.jdt.core.QuarkusActivator;
import com.redhat.quarkus.ls.api.QuarkusLanguageClientAPI;
import com.redhat.quarkus.ls.api.QuarkusLanguageServerAPI;

/**
 * LSP4E Quarkus language client.
 * 
 * @author Angelo ZERR
 *
 */
public class QuarkusLanguageClient extends LanguageClientImpl implements QuarkusLanguageClientAPI {

	private static IQuarkusPropertiesChangedListener SINGLETON_LISTENER;

	private IQuarkusPropertiesChangedListener listener = event -> {
		((QuarkusLanguageServerAPI) getLanguageServer()).quarkusPropertiesChanged(event);
	};

	public QuarkusLanguageClient() {
		// FIXME : how to remove the listener????
		// The listener should be removed when language server is shutdown, how to
		// manage that????
		if (SINGLETON_LISTENER != null) {
			QuarkusActivator.getDefault().removeQuarkusPropertiesChangedListener(SINGLETON_LISTENER);
		}
		SINGLETON_LISTENER = listener;
		QuarkusActivator.getDefault().addQuarkusPropertiesChangedListener(listener);
	}

	@Override
	public CompletableFuture<QuarkusProjectInfo> getQuarkusProjectInfo(QuarkusProjectInfoParams params) {
		return CompletableFutures.computeAsync((cancelChecker) -> {
			IProgressMonitor monitor = getProgressMonitor(cancelChecker);
			try {
				String applicationPropertiesUri = params.getUri();
				IFile file = JDTUtils.findFile(applicationPropertiesUri);
				if (file == null) {
					throw new UnsupportedOperationException(
							String.format("Cannot find IFile for '%s'", applicationPropertiesUri));
				}
				QuarkusPropertiesScope scope = params.getScope();
				return JDTQuarkusManager.getInstance().getQuarkusProjectInfo(file, scope,
						DocumentationConverter.DEFAULT_CONVERTER, monitor);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	private IProgressMonitor getProgressMonitor(CancelChecker cancelChecker) {
		IProgressMonitor monitor = new NullProgressMonitor() {
			public boolean isCanceled() {
				cancelChecker.checkCanceled();
				return false;
			};
		};
		return monitor;
	}

	@Override
	public CompletableFuture<Location> getPropertyDefinition(QuarkusPropertyDefinitionParams params) {
		// TODO : implements the Quarkus definition when LSP4E will support JDT LS
		// syntax uri -> see // https://bugs.eclipse.org/bugs/show_bug.cgi?id=551625
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public CompletableFuture<List<? extends CodeLens>> quarkusJavaCodelens(QuarkusJavaCodeLensParams javaParams) {
		return CompletableFutures.computeAsync((cancelChecker) -> {
			IProgressMonitor monitor = getProgressMonitor(cancelChecker);
			try {
				return JDTQuarkusManagerForJava.getInstance().codeLens(javaParams, JDTUtilsImpl.getInstance(), monitor);
			} catch (JavaModelException e) {
				QuarkusLSPPlugin.logException(e.getLocalizedMessage(), e);
				return Collections.emptyList();
			}	
		});
	}

}
