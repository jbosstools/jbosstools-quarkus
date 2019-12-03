package org.jboss.tools.quarkus.lsp4e.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;

/**
 * This class is a copy/paste of JDT LS
 * https://github.com/eclipse/eclipse.jdt.ls/blob/master/org.eclipse.jdt.ls.core/src/org/eclipse/jdt/ls/core/internal/ProjectUtils.java
 * with only required method for Quarkus LSP4E.
 *
 */
public class ProjectUtils {
	public static boolean hasNature(IProject project, String natureId) {
		try {
			return project != null && project.hasNature(natureId);
		} catch (CoreException e) {
			return false;
		}
	}

	public static boolean isJavaProject(IProject project) {
		return hasNature(project, JavaCore.NATURE_ID);
	}
}
