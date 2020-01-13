/******************************************************************************* 
 * Copyright (c) 2019 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 * IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.quarkus.lsp4e.internal;

import java.io.IOException;
import java.io.Reader;

import org.eclipse.jdt.core.IJavaModelStatusConstants;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavadocContentAccess;

/**
 * @see <a href="https://github.com/eclipse/eclipse.jdt.ls/blob/master/org.eclipse.jdt.ls.core/src/org/eclipse/jdt/ls/core/internal/javadoc/JavadocContentAccess.java">https://github.com/eclipse/eclipse.jdt.ls/blob/master/org.eclipse.jdt.ls.core/src/org/eclipse/jdt/ls/core/internal/javadoc/JavadocContentAccess.java</a>
 *
 */
public class JDTJavadocContentAccess {
	private JDTJavadocContentAccess() {
		// do not instantiate
	}
	
	/**
	 * Gets a reader for an IMember's Javadoc comment content from the source
	 * attachment. and renders the tags in Markdown. Returns <code>null</code>
	 * if the member does not contain a Javadoc comment or if no source is
	 * available.
	 *
	 * @param member
	 *            the member to get the Javadoc of.
	 * @return a reader for the Javadoc comment content in Markdown or
	 *         <code>null</code> if the member does not contain a Javadoc
	 *         comment or if no source is available
	 * @throws JavaModelException
	 *             is thrown when the elements Javadoc can not be accessed
	 */
	public static Reader getMarkdownContentReader(IMember member) throws JavaModelException {
		Reader contentReader = JavadocContentAccess.getHTMLContentReader(member, true, true);
		if (contentReader != null) {
			try {
				return new JavaDoc2MarkdownConverter(contentReader).getAsReader();
			} catch (IOException e) {
				throw new JavaModelException(e, IJavaModelStatusConstants.UNKNOWN_JAVADOC_FORMAT);
			}
		}
		return null;
	}
	
	/**
	 * Gets a reader for an IMember's Javadoc comment content from the source
	 * attachment. and renders the tags in plain text. Returns <code>null</code> if
	 * the member does not contain a Javadoc comment or if no source is available.
	 *
	 * @param member
	 *            the member to get the Javadoc of.
	 * @return a reader for the Javadoc comment content in plain text or
	 *         <code>null</code> if the member does not contain a Javadoc comment or
	 *         if no source is available
	 * @throws JavaModelException
	 *             is thrown when the elements Javadoc can not be accessed
	 */
	public static Reader getPlainTextContentReader(IMember member) throws JavaModelException {
		Reader contentReader = JavadocContentAccess.getHTMLContentReader(member, true, true);
		if (contentReader != null) {
			try {
				return new JavaDoc2PlainTextConverter(contentReader).getAsReader();
			} catch (IOException e) {
				throw new JavaModelException(e, IJavaModelStatusConstants.UNKNOWN_JAVADOC_FORMAT);
			}
		}
		return null;
	}
}
