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
package org.jboss.tools.quarkus.lsp4e.internal.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IEditorAssociationOverride;
import org.jboss.tools.quarkus.lsp4e.QuarkusLSPPlugin;
import org.jboss.tools.quarkus.lsp4e.internal.qute.QuteUtils;

/**
 * @author Red Hat Developers
 * 
 * As the JBoss Tools Properties editor is the default for properties file, this extension
 * will make sure that for application.properties files, the generic editor is choosen.
 */
public class QuteDefaultEditorAssociationOverride implements IEditorAssociationOverride {

  private IEditorDescriptor genericEditorDescriptor;
  
  private IEditorDescriptor genericEditorDescriptor() {
    if (genericEditorDescriptor == null) {
      IEditorRegistry editorReg = PlatformUI.getWorkbench()
          .getEditorRegistry();
      IEditorDescriptor parent = editorReg.findEditor("org.eclipse.ui.genericeditor.GenericEditor");
      genericEditorDescriptor = new IEditorDescriptor() {
        
        @Override
        public boolean isOpenInPlace() {
          return parent.isOpenInPlace();
        }
        
        @Override
        public boolean isOpenExternal() {
          return parent.isOpenExternal();
        }
        
        @Override
        public boolean isInternal() {
          return parent.isInternal();
        }
        
        @Override
        public String getLabel() {
          return parent.getLabel();
        }
        
        @Override
        public ImageDescriptor getImageDescriptor() {
          try {
            return ImageDescriptor.createFromURL(new URL(QuarkusLSPPlugin.getDefault().getBundle().getEntry("/"), "icons/quarkus_icon_rgb_16px_default.png"));
          } catch (MalformedURLException e) {
            return parent.getImageDescriptor();
          }
        }
        
        @Override
        public String getId() {
          return parent.getId();
        }
        
        @Override
        public IEditorMatchingStrategy getEditorMatchingStrategy() {
          return parent.getEditorMatchingStrategy();
        }
      };
      
    }
    return genericEditorDescriptor;
  }
  
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
		if (QuteUtils.isOwnContenType(contentType)) {
			return genericEditorDescriptor();
		}
		return editorDescriptor;
	}
}
