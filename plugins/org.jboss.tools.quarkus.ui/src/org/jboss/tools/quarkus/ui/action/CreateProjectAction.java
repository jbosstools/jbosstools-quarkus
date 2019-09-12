/*
 * Copyright 2019 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.tools.quarkus.ui.action;

import java.util.HashMap;

import org.eclipse.jface.action.Action;

import io.quarkus.eclipse.core.project.ProjectUtils;

public class CreateProjectAction extends Action implements Runnable {

	private String name, location, groupId, artefactId, version, className = null;
	private HashMap<String, Object> context = new HashMap<String, Object>();
	
	public CreateProjectAction(
			String name,
			String location,
			String groupId, 
			String artefactId, 
			String version, 
			String className,
			HashMap<String, Object> context) {
		this.groupId = groupId;
		this.artefactId = artefactId;
		this.version = version;
		this.name = name;
		this.location = location;
		this.className = className;
		if (context != null) {
			this.context = context;
		}
	}
	
	@Override
	public void run() {
		ProjectUtils.createProject(name, location, groupId, artefactId, version, className, context);
	}	

}
