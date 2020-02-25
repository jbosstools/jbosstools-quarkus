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
	 * This class should not be instantiated.
	 */
	private QuarkusCoreConstants() {
		throw new IllegalStateException("Constants class");
	}
    /*
     * Parameter names for the download request. See
     * http://editor.swagger.io/?url=https://code.quarkus.io/openapi for reference.
     */
    public static final String CODE_TOOL_PARAMETER_NAME = "b";

    public static final String CODE_GROUP_ID_PARAMETER_NAME = "g";

    public static final String CODE_ARTIFACT_ID_PARAMETER_NAME = "a";

    public static final String CODE_VERSION_PARAMETER_NAME = "v";

    public static final String CODE_CLASSNAME_PARAMETER_NAME = "c";

    public static final String CODE_PATH_PARAMETER_NAME = "p";

    public static final String CODE_EXTENSIONS_PARAMETER_NAME = "e";

    public static final String CODE_CLIENT_NAME_PARAMETER_NAME = "cn";

    public static final String CODE_CLIENT_NAME_PARAMETER_VALUE = "JBoss%20Tools%20Quarkus";

    public static final String CODE_CLIENT_CONTACT_EMAIL_PARAMETER_NAME = "cne";

    public static final String CODE_CLIENT_CONTACT_EMAIL_PARAMETER_VALUE = "tools%40jboss.org";

    public static final String CODE_ENDPOINT_URL = "https://code.quarkus.io";

    public static final String EXTENSIONS_SUFFIX = "api/extensions?" + CODE_CLIENT_NAME_PARAMETER_NAME + "="
            + CODE_CLIENT_NAME_PARAMETER_VALUE + "&" + CODE_CLIENT_CONTACT_EMAIL_PARAMETER_NAME + "="
            + CODE_CLIENT_CONTACT_EMAIL_PARAMETER_VALUE;

    public static final String DOWNLOAD_SUFFIX = "api/download";

	public static final String QUARKUS_RUNTIME_CLASS_NAME = "io.quarkus.runtime.LaunchMode";
	
	public static final String QUARKUS_PROCESS_FACTORY = QuarkusCorePlugin.PLUGIN_ID + ".processFactory";

}
