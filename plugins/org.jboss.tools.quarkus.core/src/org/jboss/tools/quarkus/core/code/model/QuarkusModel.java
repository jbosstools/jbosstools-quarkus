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

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class QuarkusModel {
    private String baseURL;
    
    private List<QuarkusStream> streams;
    
    private Map<String, QuarkusExtensionsModel> extensionsModelMap = new HashMap<>();

    public QuarkusModel(String baseURL, List<QuarkusStream> streams) {
    	this.baseURL = baseURL;
    	this.streams = streams;
    }
    
    public List<QuarkusStream> getStreams() {
        return streams;
    }
    
    public QuarkusExtensionsModel getExtensionsModel(String key, IProgressMonitor monitor) throws CoreException {
    	QuarkusExtensionsModel extensionsModel = extensionsModelMap.get(key);
    	if (extensionsModel == null) {
    		extensionsModel = QuarkusModelRegistry.loadExtensionsModel(baseURL, key, monitor);
    		extensionsModelMap.put(key, extensionsModel);
    	}
    	return extensionsModel;
    }
    
    public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
    }
}
