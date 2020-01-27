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
package org.jboss.tools.quarkus.lsp4e.internal;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.lsp4e.command.LSPCommandHandler;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.jboss.tools.foundation.ui.util.BrowserUtility;
import org.jboss.tools.quarkus.lsp4e.QuarkusLSPPlugin;

/**
 * @author Red Hat Developers
 *
 */
public class OpenBrowserHandler extends LSPCommandHandler {

	@Override
	public Object execute(ExecutionEvent event, @NonNull Command command, IPath path) throws ExecutionException {
		new BrowserUtility().checkedCreateInternalBrowser((String) command.getArguments().get(0), path.toOSString(), QuarkusLSPPlugin.PLUGIN_ID, QuarkusLSPPlugin.getDefault().getLog());
		return null;
	}

}
