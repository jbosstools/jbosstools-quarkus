/*******************************************************************************
 * Copyright (c) 2019-2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.quarkus.core;

/**
 * General Constants
 *
 */
public class QuarkusCoreConstants {
    /*
     * Parameter names for the download request. See
     * http://editor.swagger.io/?url=https://code.quarkus.io/openapi for reference.
     */
    public static final String CODE_TOOL_PARAMETER_NAME = "buildTool";

    public static final String CODE_GROUP_ID_PARAMETER_NAME = "groupId";

    public static final String CODE_ARTIFACT_ID_PARAMETER_NAME = "artifactId";

    public static final String CODE_VERSION_PARAMETER_NAME = "version";

    public static final String CODE_CLASSNAME_PARAMETER_NAME = "className";

    public static final String CODE_PATH_PARAMETER_NAME = "path";

    public static final String CODE_EXTENSIONS_PARAMETER_NAME = "extensions";
    public static final String CODE_EXTENSIONS_SHORT_PARAMETER_NAME = "s";
    
    public static final String CODE_STREAM_PARAMETER_NAME = "streamKey";

    public static final String CODE_CLIENT_NAME_PARAMETER_NAME = "cn";

    public static final String CODE_CLIENT_NAME_PARAMETER_VALUE = "JBoss%20Tools%20Quarkus";

    public static final String CODE_CLIENT_CONTACT_EMAIL_PARAMETER_NAME = "cne";

    public static final String CODE_CLIENT_CONTACT_EMAIL_PARAMETER_VALUE = "tools%40jboss.org";
    
    public static final String CODE_CLIENT_NAME_HEADER_NAME = "Client-Name";
    public static final String CODE_CLIENT_NAME_HEADER_VALUE = "JBoss Tools Quarkus";
    public static final String CODE_CLIENT_CONTACT_EMAIL_HEADER_NAME = "Client-Contact-Email";
    public static final String CODE_CLIENT_CONTACT_EMAIL_HEADER_VALUE = "tools@jboss.org";

    
    public static final String CODE_NO_EXAMPLE_CODE_PARAMETER_NAME = "noExamples";

    public static final String CODE_ENDPOINT_URL_PROPERTY_NAME = "jbosstools.quarkus.code.url";
    
    public static final String CODE_ENDPOINT_URL_PRODUCTION = "https://code.quarkus.io";
    
    public static final String CODE_ENDPOINT_URL_TEST = "https://stage.code.quarkus.io";
    		
    
    public static final String CODE_ENDPOINT_URL = System.getProperty(CODE_ENDPOINT_URL_PROPERTY_NAME, CODE_ENDPOINT_URL_PRODUCTION);
    
    public static final String CLIENT_ID_PARAMETERS = CODE_CLIENT_NAME_PARAMETER_NAME + "="
            + CODE_CLIENT_NAME_PARAMETER_VALUE + "&" + CODE_CLIENT_CONTACT_EMAIL_PARAMETER_NAME + "="
            + CODE_CLIENT_CONTACT_EMAIL_PARAMETER_VALUE;
    
    public static final String PLATFORM_ONLY_PARAMETER = "platformOnly";

    public static final String EXTENSIONS_SUFFIX = "/api/extensions/stream/";

    public static final String STREAMS_SUFFIX = "/api/streams?" + CLIENT_ID_PARAMETERS;

    public static final String DOWNLOAD_SUFFIX = "/api/download";

	public static final String QUARKUS_RUNTIME_CLASS_NAME = "io.quarkus.runtime.LaunchMode";
	
	public static final String ATTR_PROFILE_NAME = QuarkusCorePlugin.PLUGIN_ID + ".profileName";

}
