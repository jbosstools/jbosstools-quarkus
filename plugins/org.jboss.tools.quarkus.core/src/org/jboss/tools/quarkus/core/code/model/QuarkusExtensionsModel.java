/*******************************************************************************
 * Copyright (c) 2019 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.quarkus.core.code.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuarkusExtensionsModel {
	private final String key;
    private List<QuarkusCategory> categories = new ArrayList<>();

    public QuarkusExtensionsModel(String key, List<QuarkusExtension> extensions) {
    	this.key = key;
        Collections.sort(extensions, (e1, e2) -> e1.getOrder() - e2.getOrder());
        final QuarkusCategory[] currentCategory = {null};
        extensions.forEach(e -> {
            if (currentCategory[0] == null || !e.getCategory().equals(currentCategory[0].getName())) {
                currentCategory[0] = new QuarkusCategory(e.getCategory());
                categories.add(currentCategory[0]);
            }
            currentCategory[0].getExtensions().add(e);
        });
    }
    
    public List<QuarkusCategory> getCategories() {
        return categories;
    }

    public String getKey() {
    	return key;
    }
}
