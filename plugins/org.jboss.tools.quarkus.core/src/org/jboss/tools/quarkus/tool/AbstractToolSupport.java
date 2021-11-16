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
package org.jboss.tools.quarkus.tool;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

/**
 * @author Red Hat Developers
 *
 */
public abstract class AbstractToolSupport implements ToolSupport {
	protected final IProject project;
	
	protected AbstractToolSupport(IProject project) {
		this.project = project;
	}
	
	abstract String getScript();

	@Override
	public IPath getScriptPath() {
		String script = getScript();
		IPath path = project.getLocation();
		boolean changed = true;
		while (changed) {
			if (path.append(script).toFile().exists()) {
				return path.append(script);
			}
			IPath newPath = path.removeLastSegments(1);
			changed = !newPath.equals(path);
			path = newPath;
		}
		return null;
	}
}
