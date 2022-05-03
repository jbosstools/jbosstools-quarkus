/*******************************************************************************
 * Copyright (c) 2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.quarkus.ui.wizard;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.databinding.conversion.Converter;
import org.jboss.tools.quarkus.core.code.model.QuarkusExtension;

/**
 * @author Red Hat Developers
 *
 */
public class QuarkusExtension2StringConverter extends Converter<Object, String> {

	public QuarkusExtension2StringConverter() {
		super(QuarkusExtension.class, String.class);
	}

	@Override
	public String convert(Object fromObject) {
		return fromObject!=null?toString(fromObject):"";
	}

	private String toString(Object fromObject) {
		if (fromObject instanceof QuarkusExtension) {
			StringBuilder builder = new StringBuilder(((QuarkusExtension) fromObject).getDescription());
			String guide = ((QuarkusExtension) fromObject).getGuide();
			if (StringUtils.isNotBlank(guide)) {
				builder.append(". <a>Click to open guide</a>");
			}
			return builder.toString();
		}
		return "";
	}

}
