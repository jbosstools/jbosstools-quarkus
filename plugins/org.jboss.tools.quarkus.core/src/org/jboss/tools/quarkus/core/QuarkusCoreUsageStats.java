/*******************************************************************************
 * Copyright (c) 2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.quarkus.core;

import org.jboss.tools.quarkus.tool.MavenToolSupport;
import org.jboss.tools.quarkus.tool.ToolSupport;
import org.jboss.tools.usage.event.UsageEventType;
import org.jboss.tools.usage.event.UsageReporter;

/**
 * @author Red Hat Developers
 *
 */
public class QuarkusCoreUsageStats {
	private static final String QUARKUS_COMPONENT = "quarkus";

	private static QuarkusCoreUsageStats INSTANCE = null;

	private UsageEventType startApplication;
	
	private UsageEventType startLS;

	private UsageEventType startQuteLS;

	public static QuarkusCoreUsageStats getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new QuarkusCoreUsageStats();
		}
		return INSTANCE;
	}

	private QuarkusCoreUsageStats() {
		this.startApplication = createEventType("startApplication", // actionName
				"mode: run/debug", // labelDescription
				"type: 0=maven/1=gradle");
		this.startLS = createEventType("startLS", // actionName
				"Number of microprofile-ls start", // labelDescription
				UsageEventType.HOW_MANY_TIMES_VALUE_DESCRIPTION);
		this.startQuteLS = createEventType("startQuteLS", // actionName
				"Number of qute-ls start", // labelDescription
				UsageEventType.HOW_MANY_TIMES_VALUE_DESCRIPTION);
	}

	private UsageEventType createEventType(String actionName, String labelDescription, String valueDescription) {
		UsageEventType eventType = new UsageEventType(QUARKUS_COMPONENT,
				UsageEventType.getVersion(QuarkusCorePlugin.getDefault()), null, actionName,
				labelDescription, valueDescription);
		UsageReporter.getInstance().registerEvent(eventType);
		return eventType;
	}

	public void startApplication(String mode, ToolSupport tool) {
		UsageReporter.getInstance().trackEvent(startApplication.event(mode, getTool(tool)));
	}
	
	public void startLS() {
		UsageReporter.getInstance().countEvent(startLS.event());
	}
	
	public void startQuteLS() {
		UsageReporter.getInstance().countEvent(startQuteLS.event());
	}

	private static int getTool(ToolSupport tool) {
		return tool instanceof MavenToolSupport?0:1;
	}
}
