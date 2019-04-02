package io.quarkus.eclipse.core;

import org.eclipse.core.resources.IResource;

public class SelectionChangeHandler {
	
	public static Object getSelectedProject(Object selectedElement) {
		Object result = null;
		if (selectedElement instanceof IResource) {
			result = ((IResource) selectedElement).getProject();
		}
		return result;
	}

}
