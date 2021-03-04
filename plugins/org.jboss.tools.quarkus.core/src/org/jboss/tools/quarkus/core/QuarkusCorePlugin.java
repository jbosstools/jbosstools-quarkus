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

import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Quarkus plugin
 * 
 */
public class QuarkusCorePlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.jboss.tools.quarkus.core"; //$NON-NLS-1$

    // The shared instance
    private static QuarkusCorePlugin plugin;
    
    private ServiceTracker<IProxyService, IProxyService> proxyServiceTracker;

    private BundleContext context;

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
        if (proxyServiceTracker != null) {
          proxyServiceTracker.close();
        }
        super.stop(context);
    }
    
    public IProxyService getProxyService() {
      try {
        if (proxyServiceTracker == null) {
          proxyServiceTracker = new ServiceTracker<>(getBundle().getBundleContext(), IProxyService.class.getName(), null);
          proxyServiceTracker.open();
        }
        return proxyServiceTracker.getService();
      } catch (Exception e) {
        logException(e.getLocalizedMessage(), e);
      } catch (NoClassDefFoundError e) {
        logException(e.getLocalizedMessage(), e);
      }
      return null;
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

	public static void logInfo(String message) {
		getDefault().getLog().log(new Status(IStatus.INFO, getPluginId(), message));
	}
}
