/******************************************************************************* 
 * Copyright (c) 2020 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.quarkus.reddeer.common;

/**
 * 
 * @author jkopriva@redhat.com
 */
public class QuarkusLabels {
	/*
	 * This class should not be instantiated.
	 */
	private QuarkusLabels() {
		throw new IllegalStateException("Labels class");
	}

	/**
	 * Shell title labels.
	 */
	public static class Shell {
		/*
		 * This class should not be instantiated.
		 */
		private Shell() {
			throw new IllegalStateException("Shell labels class");
		}
		
		public static final String PERSPECTIVE_NAME = "Quarkus";
		public static final String QUARKUS_CATEGORY = "Quarkus";
		public static final String EXTENSION_VIEW = "Quarkus Extensions";
		public static final String NEW_QUARKUS_PROJECT_WIZARD = "New Quarkus project";
		public static final String QUARKUS_PROJECT = "Quarkus Project";
		public static final String NEW_QUARKUS_PROJECT = "New Quarkus Project";
	}
	
	
	public static class TextLabels {
		/*
		 * This class should not be instantiated.
		 */
		private TextLabels() {
			throw new IllegalStateException("Text labels class");
		}
		
		public static final String FILE_CONTEXT_MENU = "File";
		public static final String NEW_CONTEXT_ITEM = "New";
		
		//Project type labels
		public static final String PROJECT_NAME = "Project name:";
		public static final String NAME = "Name:";
		public static final String PROJECT_TYPE = "Project type:";
		public static final String MAVEN_TYPE = "Maven";
		public static final String GRADLE_TYPE = "Gradle";
		
		//Maven labels
		public static final String ARTIFACT_ID = "Artifact id:";
		public static final String GROUP_ID = "Group id:";
		public static final String VERSION = "Version:";
		public static final String REST_CLASS_NAME = "Class name:";
		public static final String REST_PATH = "Path:";
		
		//Extension selection labels
		public static final String EXTENSIONS_CATEGORIES = "Categories";
		public static final String EXTENSIONS_NAMES = "Extensions";
	}
}
