package org.jboss.tools.quarkus.lsp4e.internal;
/*******************************************************************************
 * Copyright (c) 2016-2017 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 *******************************************************************************/

import java.io.Reader;
import java.lang.reflect.Field;

import org.jboss.tools.quarkus.lsp4e.QuarkusLSPPlugin;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;

import com.overzealous.remark.Options;
import com.overzealous.remark.Options.Tables;
import com.overzealous.remark.Remark;

/**
 * Converts JavaDoc tags into Markdown equivalent.
 *
 * @author Fred Bricon
 */
public class JavaDoc2MarkdownConverter extends AbstractJavaDocConverter {

	private static Remark remark;

	static {
		Options options = new Options();
		options.tables = Tables.MULTI_MARKDOWN;
		options.hardwraps = true;
		options.inlineLinks = true;
		options.autoLinks = true;
		options.reverseHtmlSmartPunctuation = true;
		remark = new Remark(options);
		//Stop remark from stripping file and jdt protocols in an href
		try {
			Field cleanerField = Remark.class.getDeclaredField("cleaner");
			cleanerField.setAccessible(true);

			Cleaner c = (Cleaner) cleanerField.get(remark);

			Field whitelistField = Cleaner.class.getDeclaredField("whitelist");
			whitelistField.setAccessible(true);

			Whitelist w = (Whitelist) whitelistField.get(c);

			w.addProtocols("a", "href", "file", "jdt");
			w.addProtocols("img", "src", "file");
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			QuarkusLSPPlugin.logException("Unable to modify jsoup to include file and jdt protocols", e);
		}
	}

	public JavaDoc2MarkdownConverter(Reader reader) {
		super(reader);
	}

	public JavaDoc2MarkdownConverter(String javadoc) {
		super(javadoc);
	}

	@Override
	String convert(String rawHtml) {
		return remark.convert(rawHtml);
	}
}
