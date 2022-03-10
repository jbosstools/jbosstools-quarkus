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
package org.jboss.tools.quarkus.lsp4e.internal.handlers.qute;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.lsp4e.command.LSPCommandHandler;
import org.eclipse.lsp4j.Command;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.jboss.tools.quarkus.lsp4e.QuarkusLSPPlugin;

/**
 * @author Red Hat Developers
 *
 */
public class OpenUriHandler extends LSPCommandHandler {

	@Override
	public Object execute(ExecutionEvent event, Command command, IPath path) throws ExecutionException {
		try {
			if (!command.getArguments().isEmpty() && command.getArguments().get(0) instanceof String) {
				IWorkbenchWindow window= PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window != null && window.getActivePage() != null) {
					IFile file = getFile((String) command.getArguments().get(0));
					if (file != null) {
						IDE.openEditor(window.getActivePage(), file);
					}
				}
			}
		} catch (PartInitException | URISyntaxException e) {
			QuarkusLSPPlugin.logException(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * @param uri the URI of the file
	 * @return the IFile object or null
	 * @throws URISyntaxException 
	 */
	private IFile getFile(String uri) throws URISyntaxException {
		IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(new URI(uri));
		return files.length > 0 ? files[0] : null;
	}

}
