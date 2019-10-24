/******************************************************************************* 
 * Copyright (c) 2019 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.quarkus.lsp4e;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Quarkus plugin
 * 
 * @author Angelo ZERR
 */
public class QuarkusPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.boss.tools.quarkus.lsp4e"; //$NON-NLS-1$

	// The shared instance
	private static QuarkusPlugin plugin;

	/**
	 * The constructor
	 */
	public QuarkusPlugin() {
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
	public static QuarkusPlugin getDefault() {
		return plugin;
	}

	public static String getPluginId() {
		return QuarkusPlugin.PLUGIN_ID;
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

//	public static void log(Throwable e) {
//		log(new Status(IStatus.ERROR, getPluginId(), FreemarkerMessages.FreemarkerPlugin_internal_error, e));
//	}
}