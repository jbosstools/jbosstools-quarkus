/*************************************************************************************
 * Copyright (c) 2008-2011 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.jboss.tools.quarkus.core.project;

import org.eclipse.jdt.core.IClasspathContainer;

/**
 * @since 2.0
 */
public class ClasspathContainersHelper {

	public static final String MAVEN_CONTAINER_ID = "org.eclipse.m2e.MAVEN2_CLASSPATH_CONTAINER"; 
			
	public static final String GRADLE_CONTAINER_ID = "com.springsource.sts.gradle.classpathcontainer";
	
	public static final String JRE_CONTAINER_ID = "org.eclipse.jdt.launching.JRE_CONTAINER";
	
	
	private ClasspathContainersHelper() {
		//We don't want to instanciate that class
	}
	
	public static boolean applies(IClasspathContainer classpathLibrary, String libPrefix) {
		return classpathLibrary != null
				&& classpathLibrary.getPath() != null
				&& classpathLibrary.getPath().toPortableString().startsWith(libPrefix);
	}
	
}
