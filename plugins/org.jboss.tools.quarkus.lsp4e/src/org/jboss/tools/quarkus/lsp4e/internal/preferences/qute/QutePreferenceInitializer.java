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
package org.jboss.tools.quarkus.lsp4e.internal.preferences.qute;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.jboss.tools.quarkus.lsp4e.QuarkusLSPPlugin;

/**
 * @author Red Hat Developers
 *
 */
public class QutePreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences defaultPreferences = DefaultScope.INSTANCE
				.getNode(QuarkusLSPPlugin.PREFERENCES_QUALIFIER);
		defaultPreferences.put(QuarkusLSPPlugin.QUTE_VALIDATION, "enabled");
		defaultPreferences.put(QuarkusLSPPlugin.QUTE_EXCLUDES, "");
	}
}
