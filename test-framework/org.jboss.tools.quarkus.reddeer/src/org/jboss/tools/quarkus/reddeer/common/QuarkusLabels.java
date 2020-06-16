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

	/**
	 * Shell title labels.
	 */
	public static class Shell {
		public static final String PERSPECTIVE_NAME = "Quarkus";
		public static final String QUARKUS_CATEGORY = "Quarkus";
		public static final String EXTENSION_VIEW = "Quarkus Extensions";
		public static final String NEW_QUARKUS_PROJECT_WIZARD = "New Quarkus project";
		public static final String QUARKUS_PROJECT = "Quarkus Project";
		public static final String NEW_QUARKUS_PROJECT = "New Quarkus Project";
		public static final String RUN_CONFIGURATION = "Run Configurations";
		public static final String DEBUG_CONFIGURATION = "Debug Configurations";
	}
	
	
	public static class TextLabels {
		public static final String FILE_CONTEXT_MENU = "File";
		public static final String NEW_CONTEXT_ITEM = "New";
		public static final String RUN_AS_CONTEXT_MENU_ITEM = "Run As";
		public static final String RUN_CONFIGURATION_CONTEXT_MENU_ITEM = "Run Configurations...";
		public static final String DEBUG_AS_CONTEXT_MENU_ITEM = "Debug As";
		public static final String DEBUG_CONFIGURATION_CONTEXT_MENU_ITEM = "Debug Configurations...";
		public static final String OPEN_WITH = "Open With";
		public static final String MAVEN_CONTEXT_MENU_ITEM = "Maven";
		public static final String GRADLE_CONTEXT_MENU_ITEM = "Gradle";
		public static final String CUT_CONTEXT_MENU_ITEM = "Cut";
		
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
		public static final String UPDATE_MAVEN_PROJECT = "Update Project...";
		
		//Gradle labels
		public static final String REFRESH_GRADLE_PROJECT = "Refresh Gradle Project";
		
		//Extension selection labels
		public static final String EXTENSIONS_CATEGORIES = "Categories";
		public static final String EXTENSIONS_NAMES = "Extensions";
		
		//Launch configuration labels
		public static final String QUARKUS_LAUNCH_CONFIGURATION = "Quarkus Launch Configuration";
		
		//Run Configuration labels
		public static final String QUARKUS_APPLICATION_TREE_ITEM = "Quarkus Application";
		public static final String CONFIGURATION = "Configuration";
		
		//Text editors labels
		public static final String GENERIC_TEXT_EDITOR = "Generic Text Editor";
		public static final String TEXT_EDITOR = "Text Editor";
		public static final String JAVA_EDITOR = "Java Editor";
		
		//Button names
		public static final String CLOSE = "Close";
		public static final String DEBUG = "Debug";
	}
}
