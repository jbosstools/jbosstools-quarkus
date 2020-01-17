/*
 * Copyright 2020 Red Hat, Inc.
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

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDisconnect;
import org.eclipse.debug.core.model.RuntimeProcess;
import org.jboss.tools.quarkus.core.QuarkusCorePlugin;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;

public class RuntimeProcessWithJVMAttach extends RuntimeProcess {
	private ILaunch jvmLaunch;

	public RuntimeProcessWithJVMAttach(ILaunch launch, Process process, String name, Map<String, String> attributes) {
		super(launch, process, name, attributes);
	}

	/**
	 * @return the jvmLaunch
	 */
	public ILaunch getJvmLaunch() {
		return jvmLaunch;
	}

	/**
	 * @param jvmLaunch the jvmLaunch to set
	 */
	public void setJvmLaunch(ILaunch jvmLaunch) {
		this.jvmLaunch = jvmLaunch;
	}

	@Override
	public void terminate() throws DebugException {
		preTerminate();
		super.terminate();
		if (jvmLaunch != null && jvmLaunch instanceof IDisconnect && ((IDisconnect)jvmLaunch).canDisconnect()) {
			((IDisconnect)jvmLaunch).disconnect();
		}
	}

	/*
	 * On Windows, kill the root process will not get child process killed thus we need a special
	 * cleanup procedure.
	 */
	private void preTerminate() {
		if (Platform.OS_WIN32.equals(Platform.getOS())) {

			try {
				Process p = (Process) getSystemProcess();
				Field field = p.getClass().getDeclaredField("handle");
				field.setAccessible(true);
				long handl = field.getLong(p);
				Kernel32 kernel = Kernel32.INSTANCE;
				WinNT.HANDLE handle = new WinNT.HANDLE();
				handle.setPointer(Pointer.createConstant(handl));
				int pid = kernel.GetProcessId(handle);
				Runtime.getRuntime().exec(new String[] { "taskkill", "/T", "/F", "/PID", String.valueOf(pid) }).waitFor();
			} catch (SecurityException | IllegalAccessException | IllegalArgumentException
			        | IOException | NoSuchFieldException | InterruptedException e) {
				QuarkusCorePlugin.logException(e.getLocalizedMessage(), e);
			}
		}
	}
}