package io.quarkus.eclipse.core;

import io.quarkus.dependencies.Extension;

public class ExtensionWrapper {
	
	private Extension extension = null;
	
	public ExtensionWrapper(Extension extension) {
		this.extension = extension;
	}
	
	public String getName() {
		return extension.getName();
	}

	public String getGroupId() {
		return extension.getGroupId();
	}

	public String getArtifactId() {
		return extension.getArtifactId();
	}

	public String getVersion() {
		return extension.getVersion();
	}

}
