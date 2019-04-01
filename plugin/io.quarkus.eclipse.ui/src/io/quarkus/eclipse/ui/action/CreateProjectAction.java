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

package io.quarkus.eclipse.ui.action;

import java.util.HashMap;

import org.eclipse.jface.action.Action;

import io.quarkus.eclipse.core.ProjectCreator;

public class CreateProjectAction extends Action {

	private String groupId, artefactId, version, name = null;
	private HashMap<String, Object> context = new HashMap<String, Object>();
	
	public CreateProjectAction(
			String groupId, 
			String artefactId, 
			String version, 
			String name,
			HashMap<String, Object> context) {
		this.groupId = groupId;
		this.artefactId = artefactId;
		this.version = version;
		this.name = name;
		if (context != null) {
			this.context = context;
		}
	}
	
	@Override
	public void run() {
		ProjectCreator.create(name, groupId, artefactId, version, context);
	}	

}
