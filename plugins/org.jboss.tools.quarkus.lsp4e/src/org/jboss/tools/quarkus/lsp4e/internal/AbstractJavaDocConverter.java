/*******************************************************************************
 * Copyright (c) 2017 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Red Hat Inc. - initial API and implementation
 *******************************************************************************/
package org.jboss.tools.quarkus.lsp4e.internal;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.eclipse.jdt.internal.ui.text.javadoc.JavaDoc2HTMLTextReader;

/**
 * Converts JavaDoc tags into an output format.
 *
 * @author Fred Bricon
 */
abstract class AbstractJavaDocConverter {

	private JavaDoc2HTMLTextReader reader;

	private boolean read;

	private String result;

	public AbstractJavaDocConverter(Reader reader) {
		setJavaDoc2HTMLTextReader(reader);
	}

	public AbstractJavaDocConverter(String javadoc) {
		setJavaDoc2HTMLTextReader(javadoc == null ? null : new StringReader(javadoc));
	}

	private void setJavaDoc2HTMLTextReader(Reader reader) {
		if (reader == null || reader instanceof JavaDoc2HTMLTextReader) {
			this.reader = (JavaDoc2HTMLTextReader) reader;
		} else {
			this.reader = new JavaDoc2HTMLTextReader(reader);
		}
	}

	public String getAsString() throws IOException {
		if (!read && reader != null) {
			String rawHtml = reader.getString();
			result = convert(rawHtml);
		}
		return result;
	}

	public Reader getAsReader() throws IOException {
		String m = getAsString();
		return m == null ? null : new StringReader(m);
	}

	abstract String convert(String rawHtml);
}
