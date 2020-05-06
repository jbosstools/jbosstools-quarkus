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
package org.jboss.tools.quarkus.lsp4e.internal.ui;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IEditorAssociationOverride;

/**
 * @author Red Hat Developers
 * 
 * As the JBoss Tools Properties editor is the default for properties file, this extension
 * will make sure that for application.properties files, the generic editor is choosen.
 */
public class QuarkusDefaultEditorAssociationOverride implements IEditorAssociationOverride {

	@Override
	public IEditorDescriptor[] overrideEditors(IEditorInput editorInput, IContentType contentType,
	        IEditorDescriptor[] editorDescriptors) {
		return editorDescriptors;
	}

	@Override
	public IEditorDescriptor[] overrideEditors(String fileName, IContentType contentType,
	        IEditorDescriptor[] editorDescriptors) {
		return editorDescriptors;
	}

	@Override
	public IEditorDescriptor overrideDefaultEditor(IEditorInput editorInput, IContentType contentType,
	        IEditorDescriptor editorDescriptor) {
		return overrideDefaultEditor(editorInput.getName(), contentType, editorDescriptor);
	}

	@Override
	public IEditorDescriptor overrideDefaultEditor(String fileName, IContentType contentType,
	        IEditorDescriptor editorDescriptor) {
		if (isOwnContenType(contentType)) {
			return genericEditorDescriptor();
		}
		return editorDescriptor;
	}

	private IEditorDescriptor genericEditorDescriptor() {
		IEditorRegistry editorReg = PlatformUI.getWorkbench()
				.getEditorRegistry();
		return editorReg.findEditor("org.eclipse.ui.genericeditor.GenericEditor");
	}

	private boolean isOwnContenType(IContentType contentType) {
		return contentType != null && "org.jboss.tools.quarkus.lsp4e".equals(contentType.getId());
	}
}
