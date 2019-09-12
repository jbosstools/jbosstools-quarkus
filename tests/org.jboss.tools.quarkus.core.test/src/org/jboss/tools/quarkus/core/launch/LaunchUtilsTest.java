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

package org.jboss.tools.quarkus.core.launch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.jboss.tools.quarkus.core.launch.LaunchUtils;
import org.junit.jupiter.api.Test;

import io.quarkus.dev.DevModeMain;

public class LaunchUtilsTest {
	
	private Object[] arguments = null;
	
	@Test
	public void testInitializeQuarkusLaunchConfiguration() {
		ILaunchConfigurationWorkingCopy lcwc = (ILaunchConfigurationWorkingCopy)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { ILaunchConfigurationWorkingCopy.class }, 
				new TestLaunchConfigurationWorkingCopyInvocationHandler());
		assertNull(arguments);
		LaunchUtils.initializeQuarkusLaunchConfiguration(lcwc);
		assertEquals(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, arguments[0]);
		assertEquals(DevModeMain.class.getName(), arguments[1]);
	}
	
	private class TestLaunchConfigurationWorkingCopyInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			arguments = args;
			return null;
		}		
	}
	
}

