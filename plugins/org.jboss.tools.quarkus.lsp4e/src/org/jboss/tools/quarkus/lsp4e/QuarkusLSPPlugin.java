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
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.preferences.OptionsConfigurationBlock.Key;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Quarkus plugin
 * 
 * @author Angelo ZERR
 */
public class QuarkusLSPPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.jboss.tools.quarkus.lsp4e"; //$NON-NLS-1$
	
	/**
	 * The properties/preferences scope for Qute properties/preferences
	 */
	public static final String PREFERENCES_QUALIFIER = PLUGIN_ID;

	/**
	 * Property/preference name for enabling/disabling Qute validation
	 */
	public static final String QUTE_VALIDATION = "quteValidation";

	/**
	 * 
	 */
	public static final String ENABLED = "enabled";

	/**
	 * 
	 */
	public static final String DISABLED = "disabled";

	/**
	 * Property/preference name for excluding Qute validation for files
	 */
	public static final String QUTE_EXCLUDES = "quteExclude";

	// The shared instance
	private static QuarkusLSPPlugin plugin;

  /**
   * 
   */
  public static final String QUARKUS_APPLICATION_PROPERTIES_CONTENT_TYPE = "org.jboss.tools.quarkus.lsp4e.properties";
  
  public static final String QUTE_CONTENT_TYPE_HTML = "org.jboss.tools.quarkus.lsp4e.qute.html";

  public static final String QUTE_CONTENT_TYPE_YAML = "org.jboss.tools.quarkus.lsp4e.qute.yaml";

  public static final String QUTE_CONTENT_TYPE_JSON = "org.jboss.tools.quarkus.lsp4e.qute.json";

  public static final String QUTE_CONTENT_TYPE_TXT = "org.jboss.tools.quarkus.lsp4e.qute.txt";

  public static final String QUTE_CONTENT_TYPE = "org.jboss.tools.quarkus.lsp4e.qute";

  public static final String QUTE_NATURE_ID = "org.jboss.tools.quarkus.lsp4e.qutenature";

/**
 * 
 */
public static final Key ENABLE_KEY = new Key(PREFERENCES_QUALIFIER, QUTE_VALIDATION);

public static final Key EXCLUDE_KEY = new Key(PREFERENCES_QUALIFIER, QUTE_EXCLUDES);

	/**
	 * The constructor
	 */
	public QuarkusLSPPlugin() {
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
	public static QuarkusLSPPlugin getDefault() {
		return plugin;
	}

	public static String getPluginId() {
		return QuarkusLSPPlugin.PLUGIN_ID;
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	public static void logException(String errMsg, Throwable ex) {
		getDefault().getLog().log(new Status(IStatus.ERROR, getPluginId(), errMsg, ex));
		
	}

//	public static void log(Throwable e) {
//		log(new Status(IStatus.ERROR, getPluginId(), FreemarkerMessages.FreemarkerPlugin_internal_error, e));
//	}
}