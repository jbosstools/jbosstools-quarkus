/*******************************************************************************
 * Copyright (c) 2019 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.quarkus.core.code.model;

import static org.eclipse.core.runtime.IStatus.ERROR;
import static org.jboss.tools.foundation.core.ecf.URLTransportUtility.CACHE_FOREVER;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_ARTIFACT_ID_PARAMETER_NAME;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_CLASSNAME_PARAMETER_NAME;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_CLIENT_CONTACT_EMAIL_PARAMETER_NAME;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_CLIENT_CONTACT_EMAIL_PARAMETER_VALUE;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_CLIENT_NAME_PARAMETER_NAME;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_CLIENT_NAME_PARAMETER_VALUE;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_ENDPOINT_URL;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_EXTENSIONS_SHORT_PARAMETER_NAME;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_GROUP_ID_PARAMETER_NAME;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_PATH_PARAMETER_NAME;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_TOOL_PARAMETER_NAME;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_VERSION_PARAMETER_NAME;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.DOWNLOAD_SUFFIX;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.EXTENSIONS_SUFFIX;
import static org.jboss.tools.quarkus.core.QuarkusCorePlugin.PLUGIN_ID;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jboss.tools.foundation.core.ecf.URLTransportUtility;
import org.jboss.tools.quarkus.core.QuarkusCorePlugin;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class QuarkusModelRegistry {
    private static final QuarkusModelRegistry INSTANCE = new QuarkusModelRegistry();
    
    private static final ObjectMapper mapper = new ObjectMapper();
    
    private static final URLTransportUtility TRANSPORT_UTILITY = new URLTransportUtility();
    
    public static QuarkusModelRegistry getDefault() {
        return INSTANCE;
    }
    
    private final Map<String, QuarkusModel> models = new HashMap<>();
    
    private QuarkusModelRegistry() {}
    
    public QuarkusModel getModel(IProgressMonitor monitor) throws CoreException {
        return getModel(CODE_ENDPOINT_URL, monitor);
    }
    
    public QuarkusModel getModel(String endpointURL, IProgressMonitor monitor) throws CoreException {
        QuarkusModel model = models.get(endpointURL);
        if (model == null) {
            model = loadModel(endpointURL, monitor);
            models.put(endpointURL, model);
        }
        return model;
    }

    private QuarkusModel loadModel(String endpointURL, IProgressMonitor monitor) throws CoreException {
        try {
            if (!endpointURL.endsWith("/")) {
                endpointURL += "/";
            }
            endpointURL += EXTENSIONS_SUFFIX;
            File file = TRANSPORT_UTILITY.getCachedFileForURL(endpointURL, endpointURL, CACHE_FOREVER, monitor);
            return readModel(file);
        } catch (IOException ioe) {
            throw new CoreException(new Status(ERROR, PLUGIN_ID, ioe.getLocalizedMessage(), ioe));
        }
    }

	public static QuarkusModel readModel(File file) throws IOException, JsonParseException, JsonMappingException {
		List<QuarkusExtension> extensions = mapper.readValue(file, new TypeReference<List<QuarkusExtension>>() {
		});
		return new QuarkusModel(extensions);
	}
    
    public IStatus zip(String endpointURL, Tool tool, 
            String groupId, String artifactId, String version, String className, String path, Set<QuarkusExtension> selected, OutputStream output, IProgressMonitor monitor) {
        StringBuilder builder = new StringBuilder(endpointURL);
        if (!endpointURL.endsWith("/")) {
            builder.append('/');
        }
        builder.append(DOWNLOAD_SUFFIX);
        builder.append('?');
        builder.append(CODE_TOOL_PARAMETER_NAME).append('=').append(tool.name()).append('&');
        builder.append(CODE_GROUP_ID_PARAMETER_NAME).append('=').append(groupId).append('&');
        builder.append(CODE_ARTIFACT_ID_PARAMETER_NAME).append('=').append(artifactId).append('&');
        builder.append(CODE_VERSION_PARAMETER_NAME).append('=').append(version).append('&');
        builder.append(CODE_CLASSNAME_PARAMETER_NAME).append('=').append(className).append('&');
        builder.append(CODE_PATH_PARAMETER_NAME).append('=').append(path).append('&');
        builder.append(CODE_EXTENSIONS_SHORT_PARAMETER_NAME).append('=').append(selected.stream().map(e -> e.getShortId()).collect(Collectors.joining("."))).append('&');
        builder.append(CODE_CLIENT_NAME_PARAMETER_NAME).append('=').append(CODE_CLIENT_NAME_PARAMETER_VALUE).append('&');
        builder.append(CODE_CLIENT_CONTACT_EMAIL_PARAMETER_NAME).append('=').append(CODE_CLIENT_CONTACT_EMAIL_PARAMETER_VALUE);
        try {
            String url = builder.toString();
            return TRANSPORT_UTILITY.download(url, url, output, monitor);
        } catch (RuntimeException e) {
            return new Status(IStatus.ERROR, QuarkusCorePlugin.PLUGIN_ID, e.getLocalizedMessage(), e);
        }
    }

}
