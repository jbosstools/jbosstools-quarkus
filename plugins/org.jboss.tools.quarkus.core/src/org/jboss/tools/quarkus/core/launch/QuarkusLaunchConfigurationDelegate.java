/*
 * Copyright 2019-2020 Red Hat, Inc.
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

package org.jboss.tools.quarkus.core.launch;

import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ID_REMOTE_JAVA_APPLICATION;

import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.jboss.tools.quarkus.core.QuarkusCoreConstants;
import org.jboss.tools.quarkus.core.QuarkusCorePlugin;
import org.jboss.tools.quarkus.core.QuarkusCoreUsageStats;
import org.jboss.tools.quarkus.core.project.ProjectUtils;
import org.jboss.tools.quarkus.tool.DefaultToolExecutionContext;
import org.jboss.tools.quarkus.tool.ToolSupport;

public class QuarkusLaunchConfigurationDelegate extends LaunchConfigurationDelegate {
	private static final String JWDP_HANDSHAKE = "JDWP-Handshake";
	
	 private int allocateLocalPort() throws CoreException {
	    try (ServerSocket socket = new ServerSocket(0)) {
	      return socket.getLocalPort();
	    } catch (IOException e) {
	      throw new CoreException(new Status(IStatus.ERROR, QuarkusCorePlugin.PLUGIN_ID, e.getLocalizedMessage()));
	    }
	  }
	 
	 protected void copy(ILaunch source, ILaunch target) {
		 for(IDebugTarget t : source.getDebugTargets()) {
			 target.addDebugTarget(t);
		 }
		 for(IProcess p : source.getProcesses()) {
			 target.addProcess(p);
		 }
	 }
	 
	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		IProject project = getProject(configuration);
		String profile = configuration.getAttribute(QuarkusCoreConstants.ATTR_PROFILE_NAME, (String) null);
		int debugPort = allocateLocalPort();
		DefaultToolExecutionContext context = new DefaultToolExecutionContext(configuration.getName(),
				project,
				configuration.getAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, new HashMap<>()),
				profile,
				"debug".equals(mode),
				debugPort);
		ToolSupport toolSupport = ProjectUtils.getToolSupport(project);
		copy(toolSupport.run(context, monitor), launch);
		if ("debug".equals(mode)) {
			createRemoteJavaDebugConfiguration(configuration, debugPort, monitor);
		}
		QuarkusCoreUsageStats.getInstance().startApplication(mode, toolSupport);
	}

	private ILaunch createRemoteJavaDebugConfiguration(ILaunchConfiguration configuration, int port, IProgressMonitor monitor) throws CoreException {
		waitForPortAvailable(port, monitor);
		IProject project = getProject(configuration);
		String name = "Quarkus remote " + project.getName();
		ILaunchConfigurationType launchConfigurationType = DebugPlugin.getDefault().getLaunchManager()
				.getLaunchConfigurationType(ID_REMOTE_JAVA_APPLICATION);
		ILaunchConfigurationWorkingCopy launchConfiguration = launchConfigurationType.newInstance(null, name);
		launchConfiguration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_ALLOW_TERMINATE, false);
		launchConfiguration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_CONNECTOR,
				IJavaLaunchConfigurationConstants.ID_SOCKET_ATTACH_VM_CONNECTOR);
		Map<String, String> connectMap = new HashMap<>(2);
		connectMap.put("port", String.valueOf(port)); //$NON-NLS-1$
		connectMap.put("hostname", "localhost"); //$NON-NLS-1$ //$NON-NLS-2$
		launchConfiguration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, connectMap);
			launchConfiguration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, project.getName());
		return launchConfiguration.launch("debug", monitor);
	}

	private IProject getProject(ILaunchConfiguration configuration) throws CoreException {
		String projectName = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String)null);
		return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
	}

	private void waitForPortAvailable(int port, IProgressMonitor monitor) throws CoreException{
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < 60_000 && !monitor.isCanceled()) {
			try (Socket socket = new Socket("localhost", port)) {
				socket.getOutputStream().write(JWDP_HANDSHAKE.getBytes(StandardCharsets.US_ASCII));
				return;
			} catch (ConnectException e) {
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e1) {
					throw new CoreException(new Status(IStatus.ERROR, QuarkusCorePlugin.PLUGIN_ID, e.getLocalizedMessage()));
				}
			} catch (IOException e) {
				throw new CoreException(new Status(IStatus.ERROR, QuarkusCorePlugin.PLUGIN_ID, e.getLocalizedMessage()));
			}
		}
		throw new CoreException(new Status(IStatus.ERROR, QuarkusCorePlugin.PLUGIN_ID, "Can't connect to JVM"));
	}
}
