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
package org.jboss.tools.quarkus.lsp4e.internal.qute;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.jboss.tools.quarkus.lsp4e.QuarkusLSPPlugin;

/**
 * @author Red Hat Developers
 *
 */
public class QutePropertyTester extends PropertyTester {
	
	private static final String IS_QUTE = "isQute";

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		try {
			if (IS_QUTE.equals(property) && receiver instanceof IEditorPart) {
				IEditorInput input = ((IEditorPart)receiver).getEditorInput();
				if (input instanceof IFileEditorInput) {
					return QuteUtils.isOwnContenType(((IFileEditorInput) input).getFile().getContentDescription().getContentType());
				}
			}
		} catch (CoreException e) {
			QuarkusLSPPlugin.logException(e.getLocalizedMessage(), e);
		}
		return false;
	}

}
