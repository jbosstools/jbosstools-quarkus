/******************************************************************************* 
 * Copyright (c) 2019 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.quarkus.lsp4e.internal.ls;

import java.io.Reader;
import java.util.Scanner;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4mp.commons.DocumentFormat;
import org.eclipse.lsp4mp.jdt.core.utils.IJDTUtils;

public class JDTUtilsImpl implements IJDTUtils, com.redhat.qute.jdt.utils.IJDTUtils {
	private static final IJDTUtils INSTANCE = new JDTUtilsImpl();

	public static <T> T getInstance() {
		return (T) INSTANCE;
	}

	private JDTUtilsImpl() {
	}
	
	@Override
	public ICompilationUnit resolveCompilationUnit(String uriString) {
		return JDTUtils.resolveCompilationUnit(uriString);
	}
	public IClassFile resolveClassFile(String uriString) {
		return JDTUtils.resolveClassFile(uriString);
	}

	@Override
	public boolean isHiddenGeneratedElement(IJavaElement element) {
		return JDTUtils.isHiddenGeneratedElement(element);
	}

	@Override
	public Range toRange(IOpenable openable, int offset, int length) throws JavaModelException {
		return JDTUtils.toRange(openable, offset, length);
	}

	@Override
	public String toClientUri(String uri) {
		return ResourceUtils.toClientUri(uri);
	}

	@Override
	public String toUri(ITypeRoot typeRoot) {
		return JDTUtils.toUri(typeRoot);
	}

	@Override
	public void waitForLifecycleJobs(IProgressMonitor monitor) {
		/*try {
			Job.getJobManager().join(DocumentLifeCycleHandler.DOCUMENT_LIFE_CYCLE_JOBS, monitor);
		} catch (OperationCanceledException ignorable) {
			// No need to pollute logs when query is cancelled
		} catch (Exception e) {
			QuarkusLSPPlugin.logException(e.getMessage(), e);
		}*/
		//TODO: verify we need to synchronise on jobs
	}
	
	@Override
	public int toOffset(IBuffer buffer, int line, int column) {
		return JsonRpcHelpers.toOffset(buffer, line, column);
	}

	@Override
	public IFile findFile(String uriString) {
		return JDTUtils.findFile(uriString);
	}

	@Override
	public void discoverSource(IClassFile classFile, IProgressMonitor monitor) throws CoreException {
		DownloadHelper.INSTANCE.discoverSource(classFile, monitor);
	}

	@Override
	public Location toLocation(IJavaElement element) throws JavaModelException {
		return JDTUtils.toLocation(element);
	}

	@Override
	public String getJavadoc(IMember member, DocumentFormat documentFormat) throws JavaModelException {
		boolean markdown = DocumentFormat.Markdown.equals(documentFormat);
		Reader reader = markdown ? JDTJavadocContentAccess.getMarkdownContentReader(member)
				: JDTJavadocContentAccess.getPlainTextContentReader(member);
		return reader != null ? toString(reader) : null;
	}

	private static String toString(Reader reader) {
		try (Scanner s = new java.util.Scanner(reader)) {
			s.useDelimiter("\\A");
			return s.hasNext() ? s.next() : "";
		}
	}

	@Override
	public String getJavadoc(IMember member, com.redhat.qute.commons.DocumentFormat documentFormat)
			throws JavaModelException {
		boolean markdown = DocumentFormat.Markdown.equals(documentFormat);
		Reader reader = markdown ? JDTJavadocContentAccess.getMarkdownContentReader(member)
				: JDTJavadocContentAccess.getPlainTextContentReader(member);
		return reader != null ? toString(reader) : null;
	}
}
