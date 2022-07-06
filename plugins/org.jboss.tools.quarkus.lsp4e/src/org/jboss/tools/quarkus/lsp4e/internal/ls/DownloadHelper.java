/*******************************************************************************
 * Copyright (c) 2022 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.quarkus.lsp4e.internal.ls;

import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.m2e.core.embedder.ArtifactKey;
import org.eclipse.m2e.jdt.MavenJdtPlugin;
import org.eclipse.m2e.jdt.internal.BuildPathManager;
import org.jboss.tools.maven.core.internal.identification.MavenCentralIdentifier;
import org.jboss.tools.maven.core.internal.identification.MavenPropertiesIdentifier;
import org.jboss.tools.quarkus.core.project.ProjectUtils;
import org.jboss.tools.quarkus.core.utils.JobHelpers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * @author Red Hat Developers
 *
 */
public class DownloadHelper {
	public static final DownloadHelper INSTANCE = new DownloadHelper();
	
	private DownloadHelper() {}
	
	private Cache<String, Boolean> downloadRequestsCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(1, TimeUnit.HOURS).build();
	private final int MAX_TIME_MILLIS = 3000;
	
	public void discoverSource(IClassFile classFile, IProgressMonitor monitor) throws CoreException {
		if (ProjectUtils.isMavenProject(classFile.getJavaProject().getProject())) {
			discoverSourceMaven(classFile, monitor);
		}
	}

	public void discoverSourceMaven(IClassFile classFile, IProgressMonitor monitor) throws CoreException {
		if (classFile == null) {
			return;
		}
		IJavaElement element = classFile;
		while (element.getParent() != null) {
			element = element.getParent();
			if (element instanceof IPackageFragmentRoot) {
				final IPackageFragmentRoot fragment = (IPackageFragmentRoot) element;
				IPath attachmentPath = fragment.getSourceAttachmentPath();
				if (attachmentPath != null && !attachmentPath.isEmpty() && attachmentPath.toFile().exists()) {
					break;
				}
				if (fragment.isArchive()) {
					IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(fragment.getPath());
					IPath path = file.getFullPath();
					if (path == null || !path.toFile().exists()) {
						path = file.getLocation();
						if (path == null) {
							return;
						}
					}
					Boolean downloaded = downloadRequestsCache.getIfPresent(path.toString());
					if (downloaded == null) {
						downloadRequestsCache.put(path.toString(), true);
						ArtifactKey artifact = new MavenPropertiesIdentifier().identify(path.toFile(), monitor);
						if (artifact == null) {
							artifact = new MavenCentralIdentifier().identify(path.toFile(), monitor);
						}
						if (artifact != null) {
							BuildPathManager buildpathManager = (BuildPathManager) MavenJdtPlugin.getDefault().getBuildpathManager();
							buildpathManager.scheduleDownload(fragment, artifact, true, true);
							JobHelpers.waitForDownloadSourcesJobs(MAX_TIME_MILLIS);
						}
					}
					break;
				}
			}
		}
	}
}
