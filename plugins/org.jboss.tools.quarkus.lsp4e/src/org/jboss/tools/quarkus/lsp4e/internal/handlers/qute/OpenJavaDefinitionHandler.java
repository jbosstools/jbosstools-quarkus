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

import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.lsp4e.LSPEclipseUtils;
import org.eclipse.lsp4e.command.LSPCommandHandler;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.Location;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.quarkus.lsp4e.QuarkusLSPPlugin;
import org.jboss.tools.quarkus.lsp4e.internal.ls.JDTUtilsImpl;

import com.redhat.qute.commons.QuteJavaDefinitionParams;
import com.redhat.qute.jdt.QuteSupportForTemplate;

/**
 * @author Red Hat Developers
 *
 */
public class OpenJavaDefinitionHandler extends LSPCommandHandler {
	
    private static final String PROJECT_URI_ATTR = "projectUri";
    private static final String SOURCE_TYPE_ATTR = "sourceType";
    private static final String SOURCE_FIELD_ATTR = "sourceField";
    private static final String SOURCE_METHOD_ATTR = "sourceMethod";
    private static final String SOURCE_PARAMETER_ATTR = "sourceParameter";
    private static final String DATA_METHOD_INVOCATION_ATTR = "dataMethodInvocation";

	@Override
	public Object execute(ExecutionEvent event, Command command, IPath path) throws ExecutionException {
		try {
			QuteJavaDefinitionParams params = getQuteJavaDefinitionParams(command.getArguments());
			if (params != null) {
				Location location = QuteSupportForTemplate.getInstance().getJavaDefinition(params, JDTUtilsImpl.getInstance(), new NullProgressMonitor());
				LSPEclipseUtils.openInEditor(location, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage());
			}
		} catch (CoreException e) {
			QuarkusLSPPlugin.logException(e.getLocalizedMessage(), e);
		}
		return null;
	}

    protected String getString(String name, Map<String, Object> obj) {
        return obj.containsKey(name)?obj.get(name).toString():null;
    }

    protected boolean getBoolean(String name, Map<String, Object> obj) {
        return obj.containsKey(name)?(boolean) obj.get(name):false;
    }

    private QuteJavaDefinitionParams getQuteJavaDefinitionParams(List<Object> arguments) {
        if (!arguments.isEmpty() && arguments.get(0) instanceof Map) {
            Map<String, Object> obj = ((Map<String, Object>) arguments.get(0));
            String templateFileUri = getString(PROJECT_URI_ATTR, obj);
            String sourceType = getString(SOURCE_TYPE_ATTR, obj);
            QuteJavaDefinitionParams params = new QuteJavaDefinitionParams(sourceType, templateFileUri);
            String sourceField = getString(SOURCE_FIELD_ATTR, obj);
            params.setSourceField(sourceField);
            String sourceMethod = getString(SOURCE_METHOD_ATTR, obj);
            params.setSourceMethod(sourceMethod);
            String methodParameter = getString(SOURCE_PARAMETER_ATTR, obj);
            params.setSourceParameter(methodParameter);
            boolean dataMethodInvocation = getBoolean(DATA_METHOD_INVOCATION_ATTR, obj);
            params.setDataMethodInvocation(dataMethodInvocation);
            return params;
        }
        return null;
    }
}
