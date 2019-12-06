/******************************************************************************* 
 * Copyright (c) 2020 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.quarkus.reddeer.perspective;

import org.eclipse.reddeer.eclipse.ui.perspectives.AbstractPerspective;
import org.jboss.tools.quarkus.reddeer.common.QuarkusLabels;

/**
 * 
 * @author jkopriva@redhat.com
 */
public class QuarkusPerspective extends AbstractPerspective {

	public QuarkusPerspective() {
		super(QuarkusLabels.Shell.PERSPECTIVE_NAME);
	}

}
