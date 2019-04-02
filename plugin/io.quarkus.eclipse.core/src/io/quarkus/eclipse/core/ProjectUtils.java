package io.quarkus.eclipse.core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import io.quarkus.cli.commands.AddExtensions;
import io.quarkus.cli.commands.ListExtensions;
import io.quarkus.dependencies.Extension;
import io.quarkus.maven.utilities.MojoUtils;

public class ProjectUtils {
	
	public static Set<?> findInstalledExtensions(Object currentProject) {
		try {
			if (currentProject != null && currentProject instanceof IProject) {
				IResource resource = ((IProject)currentProject).findMember("pom.xml");
				if (resource != null) {
					File pomFile = new File(resource.getRawLocation().toOSString());
					Model model = MojoUtils.readPom(pomFile);
					ListExtensions listExtensions = new ListExtensions(model);
					Map<?,?> extensions = (Map<?,?>)getFindInstalledMethod().invoke(listExtensions);
					return extensions.keySet(); 
				}
			}
		} catch (IOException | InvocationTargetException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return new HashSet<Object>();
	}
	
	private static Method getFindInstalledMethod() {	
		Method result = null;
		try {
			result = ListExtensions.class.getDeclaredMethod("findInstalled");
			result.setAccessible(true);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
		return result;
	}
	
	public static void installExtension(Object currentProject, Extension extension) {
		try {
			if (currentProject != null && currentProject instanceof IProject) {
				IResource resource = ((IProject)currentProject).findMember("pom.xml");
				AddExtensions project = new AddExtensions(new File(resource.getRawLocation().toOSString()));
				project.addExtensions(Collections.singleton(extension.getName()));
				resource.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			}		
		} catch (IOException | CoreException e) {
			throw new RuntimeException(e);
		}
	}

}
