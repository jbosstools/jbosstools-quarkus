package io.quarkus.eclipse.core.launch;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

import io.quarkus.dev.DevModeMain;

public class LaunchUtils {
	
	public static void initializeQuarkusLaunchConfiguration(
			ILaunchConfigurationWorkingCopy workingCopy) {
		workingCopy.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, 
				DevModeMain.class.getName());
	}

}
