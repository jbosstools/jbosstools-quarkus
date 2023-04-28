package org.jboss.tools.quarkus.lsp4e.internal.handlers.qute;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.lsp4e.LanguageServiceAccessor;
import org.eclipse.lsp4e.command.LSPCommandHandler;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.ExecuteCommandOptions;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.quarkus.lsp4e.QuarkusLSPPlugin;

public class GenerateTemplateFileHandler extends LSPCommandHandler {

	private static final String TEMPLATE_FILE_URI = "templateFileUri"; //$NON-NLS-1$
	private static final String QUTE_COMMAND_GENERATE_TEMPLATE_CONTENT = "qute.command.generate.template.content"; //$NON-NLS-1$

	@Override
	public Object execute(ExecutionEvent event, @NonNull Command command, IPath sourcePath) throws ExecutionException {
		URI uri;
		try {
			uri = getURI(command.getArguments());
			ExecuteCommandParams params = new ExecuteCommandParams();
			params.setCommand(QUTE_COMMAND_GENERATE_TEMPLATE_CONTENT);
			params.setArguments(command.getArguments());
			LanguageServer server = getServer();
			if (server != null) {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				server.getWorkspaceService().executeCommand(params).thenApply(content -> {
					try {
						Path path = Path.of(uri);
						if (!Files.exists(path)) {
							Files.createDirectories(path.getParent());
							Files.createFile(path);
							Files.writeString(path, content.toString());
						}
					} catch (IOException e) {
						QuarkusLSPPlugin.logException(e.getLocalizedMessage(), e);
					}
					return content;
				}).thenRunAsync(() -> {
					try {
						IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(uri);
						if (files != null && files.length > 0) {
							IFile file = files[0];
							file.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
						}
					} catch (CoreException e) {
						QuarkusLSPPlugin.logException(e.getLocalizedMessage(), e);
					}
				});
			} else {
				QuarkusLSPPlugin.log(new Status(IStatus.INFO, QuarkusLSPPlugin.PLUGIN_ID,
						"Can't get active LSP server for getting qute default content."));
			}
		} catch (URISyntaxException e) {
			QuarkusLSPPlugin.logException(e.getLocalizedMessage(), e);
		}
		return null;
	}

	private LanguageServer getServer() {
		List<LanguageServer> servers = LanguageServiceAccessor.getActiveLanguageServers(cap -> {
			ExecuteCommandOptions provider = cap.getExecuteCommandProvider();
			return provider != null && provider.getCommands().contains(QUTE_COMMAND_GENERATE_TEMPLATE_CONTENT);
		});
		return servers.isEmpty() ? null : servers.get(0);
	}

	private URI getURI(List<Object> arguments) throws URISyntaxException {
		URI uri = null;
		if (!arguments.isEmpty() && arguments.get(0) instanceof Map) {
			Map<String, String> obj = ((Map<String, String>) arguments.get(0));
			uri = new URI(obj.get(TEMPLATE_FILE_URI));
		}
		return uri;
	}
}
