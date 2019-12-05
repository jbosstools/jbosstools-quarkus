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
package org.jboss.tools.quarkus.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Quarkus plugin
 * 
 */
public class QuarkusCorePlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.boss.tools.quarkus.core"; //$NON-NLS-1$

    // The shared instance
    private static QuarkusCorePlugin plugin;

    /**
     * The constructor
     */
    public QuarkusCorePlugin() {
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static QuarkusCorePlugin getDefault() {
        return plugin;
    }

    public static String getPluginId() {
        return QuarkusCorePlugin.PLUGIN_ID;
    }

    public static void log(IStatus status) {
        getDefault().getLog().log(status);
    }

    public static void logException(String errMsg, Throwable ex) {
        getDefault().getLog().log(new Status(IStatus.ERROR, getPluginId(), errMsg, ex));
        
    }
}