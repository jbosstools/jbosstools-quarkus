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
package org.jboss.tools.quarkus.ui.wizard;

import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_ENDPOINT_URL;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.jboss.tools.foundation.core.databinding.ObservablePojo;
import org.jboss.tools.quarkus.core.code.model.QuarkusCategory;
import org.jboss.tools.quarkus.core.code.model.QuarkusExtension;
import org.jboss.tools.quarkus.core.code.model.QuarkusExtensionsModel;
import org.jboss.tools.quarkus.core.code.model.QuarkusModel;
import org.jboss.tools.quarkus.core.code.model.Tool;

public class CodeProjectModel extends ObservablePojo {
    public static final String PROJECT_NAME_PROPERTY = "projectName";

    public static final String USE_DEFAULT_LOCATION_PROPERTY = "useDefaultLocation";

    public static final String LOCATION_PROPERTY = "location";

    public static final String GROUPID_PROPERTY = "groupId";

    public static final String ARTIFACTID_PROPERTY = "artifactId";

    public static final String VERSION_PROPERTY = "version";
    
    public static final String CLASSNAME_PROPERTY = "className";
    
    public static final String PATH_PROPERTY = "path";

    public static final String TOOL_PROPERTY = "tool";
    public static final String ENDPOINT_PROPERTY = "endpoint";
    public static final String MODEL_PROPERTY = "model";
    public static final String EXTENSIONS_MODEL_PROPERTY = "extensionsModel";
    
    public static final String SELECTED_EXTENSIONS_PROPERTY = "selectedExtensions";
    
    public static final String USE_CODE_STARTERS_PROPERTY = "useCodeStarters";
    
    private static final IPath ROOT = ResourcesPlugin.getWorkspace().getRoot().getLocation();
    
    private String projectName;
    
    private boolean useDefaultLocation = true;
    
    private IPath location = ROOT;
    
    private String groupId = "org.acme";
    
    private String artifactId = "code-with-quarkus";
    
    private String version = "1.0.0-SNAPSHOT";
    
    private String className = "org.acme.ExampleResource";
    
    private String path = "/hello";

    private Tool tool = Tool.MAVEN;
    
    private String endpoint = CODE_ENDPOINT_URL;
    
    private QuarkusModel model;
    
    private QuarkusExtensionsModel extensionsModel;
    
    private List<QuarkusCategory> categories = new ArrayList<>();
    
    private QuarkusCategory selectedCategory;
    
    private List<QuarkusExtension> extensions = new ArrayList<>();
    
    private Set<QuarkusExtension> selectedExtensions = new HashSet<>();
    
    private boolean useCodeStarters = true;

    /**
     * @return the projectName
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * @param projectName the projectName to set
     */
    public void setProjectName(String projectName) {
        firePropertyChange(PROJECT_NAME_PROPERTY, this.projectName, this.projectName = projectName);
        updateLocation();
    }
    
    /**
     * updates the location for the existing project name.
     * 
     * @see #getProjectName()
     */
    private void updateLocation() {
        if (isUseDefaultLocation()) {
            setLocation(ROOT.append(getProjectName() == null ? "" : getProjectName()));
        }
    }


    /**
     * @return the useDefaultLocation
     */
    public boolean isUseDefaultLocation() {
        return useDefaultLocation;
    }

    /**
     * @param useDefaultLocation the useDefaultLocation to set
     */
    public void setUseDefaultLocation(boolean useDefaultLocation) {
        firePropertyChange(USE_DEFAULT_LOCATION_PROPERTY, this.useDefaultLocation, this.useDefaultLocation = useDefaultLocation);
        updateLocation();
    }

    /**
     * @return the location
     */
    public IPath getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(IPath location) {
        firePropertyChange(LOCATION_PROPERTY, this.location, this.location = location);
    }

    /**
     * @return the groupId
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * @param groupId the groupId to set
     */
    public void setGroupId(String groupId) {
        firePropertyChange(GROUPID_PROPERTY, this.groupId, this.groupId = groupId);
    }

    /**
     * @return the artifactId
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * @param artifactId the artifactId to set
     */
    public void setArtifactId(String artifactId) {
        firePropertyChange(ARTIFACTID_PROPERTY, this.artifactId, this.artifactId = artifactId);
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        firePropertyChange(VERSION_PROPERTY, this.version, this.version = version);
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className) {
        firePropertyChange(CLASSNAME_PROPERTY, this.className, this.className = className);
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        firePropertyChange(PATH_PROPERTY, this.path, this.path = path);
    }

    /**
     * @return the tool
     */
    public Tool getTool() {
        return tool;
    }

    /**
     * @param tool the tool to set
     */
    public void setTool(Tool tool) {
        firePropertyChange(TOOL_PROPERTY, this.tool, this.tool = tool);
    }

    /**
     * @return the endpoint
     */
    public String getEndpoint() {
        return endpoint;
    }

    /**
     * @param endpoint the endpoint to set
     */
    public void setEndpoint(String endpoint) {
        firePropertyChange(ENDPOINT_PROPERTY, this.endpoint, this.endpoint = endpoint);
    }

    public void setModel(QuarkusModel model) {
        firePropertyChange(MODEL_PROPERTY, this.model, this.model = model);
    }
    
    public QuarkusModel getModel() {
    	return model;
    }
    
    public QuarkusExtensionsModel getExtensionsModel() {
    	return extensionsModel;
    }
    
    public void setExtensionsModel(QuarkusExtensionsModel extensionsModel) {
        firePropertyChange(EXTENSIONS_MODEL_PROPERTY, this.extensionsModel, this.extensionsModel = extensionsModel);
        extensionsModel.getCategories().forEach(category -> category.getExtensions().forEach(extension -> {
        	if (extension.isDefaultExtension()) {
        		selectedExtensions.add(extension);
        	}
        }));
    }
    
    /**
     * @return the selectedExtensions
     */
    public Set<QuarkusExtension> getSelectedExtensions() {
        return selectedExtensions;
    }

    /**
     * @param selectedExtensions the selectedExtensions to set
     */
    public void setSelectedExtensions(Set<QuarkusExtension> selectedExtensions) {
        firePropertyChange(SELECTED_EXTENSIONS_PROPERTY, this.selectedExtensions, this.selectedExtensions = selectedExtensions);
    }
    
    public void toggleSelectedExtension(QuarkusExtension extension) {
    	if (!extension.isDefaultExtension()) {
            Set<QuarkusExtension> newSelectedExtensions = new HashSet<QuarkusExtension>(selectedExtensions);
            if (newSelectedExtensions.contains(extension)) {
                newSelectedExtensions.remove(extension);
            } else {
                newSelectedExtensions.add(extension);
            }
            setSelectedExtensions(newSelectedExtensions);
    	}
    }

    /**
     * @return the useCodeStarters
     */
    public boolean isUseCodeStarters() {
      return useCodeStarters;
    }

    /**
     * @param useCodeStarters the useCodeStarters to set
     */
    public void setUseCodeStarters(boolean useCodeStarters) {
      firePropertyChange(USE_CODE_STARTERS_PROPERTY, this.useCodeStarters, this.useCodeStarters = useCodeStarters);
    }
}
