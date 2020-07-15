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
package org.jboss.tools.quarkus.lsp4e;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.core.filebuffers.IDocumentSetupParticipantExtension;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.IDocument;
import org.jboss.tools.quarkus.core.project.ProjectUtils;
import org.jboss.tools.quarkus.lsp4e.internal.yaml.SchemaRegistry;

/**
 * @author Red Hat Developers
 *
 */
public class QuarkusYAMLDocumentSetupParticipant implements IDocumentSetupParticipant, IDocumentSetupParticipantExtension {

  @Override
  public void setup(IDocument document) {
    ITextFileBuffer buffer = ITextFileBufferManager.DEFAULT.getTextFileBuffer(document);
    if (buffer != null && buffer.getLocation() != null) {
      setup(document, buffer.getLocation(), LocationKind.IFILE);
    }
  }

  @Override
  public void setup(IDocument document, IPath location, LocationKind locationKind) {
    IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(location);
    if (file != null && ProjectUtils.isApplicationYAML(file)) {
      Job job = new Job("Compute Quarkus schemas") {
        @Override
        protected IStatus run(IProgressMonitor monitor) {
          SchemaRegistry.getInstance().updateYAMLLanguageServerConfigIfRequired(file.getProject());
          return Status.OK_STATUS;
        }
      };
      job.setUser(true);
      job.setPriority(Job.DECORATE);
      job.schedule(200);
    }
  }
}
