package io.quarkus.eclipse.cheatsheet;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class Activator extends AbstractUIPlugin {
	
	public final static String ID = "io.quarkus.eclipse.cheatsheet";
	public final static Activator DEFAULT = new Activator();

	private Activator() {}
	
	public void logError(Throwable t) {
		IStatus status = new Status(
				IStatus.ERROR, 
				ID, 
				t.getLocalizedMessage(),
				t);
		getLog().log(status);
	}

}
