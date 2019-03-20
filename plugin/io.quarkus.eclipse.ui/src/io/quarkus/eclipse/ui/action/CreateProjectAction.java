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
