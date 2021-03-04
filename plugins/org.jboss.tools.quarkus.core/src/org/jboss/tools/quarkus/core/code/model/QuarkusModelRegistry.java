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

import static org.eclipse.core.runtime.IStatus.ERROR;
import static org.jboss.tools.foundation.core.ecf.URLTransportUtility.CACHE_FOREVER;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_ARTIFACT_ID_PARAMETER_NAME;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_CLASSNAME_PARAMETER_NAME;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_ENDPOINT_URL;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_EXTENSIONS_PARAMETER_NAME;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_GROUP_ID_PARAMETER_NAME;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_NO_EXAMPLE_CODE_PARAMETER_NAME;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_PATH_PARAMETER_NAME;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_TOOL_PARAMETER_NAME;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.CODE_VERSION_PARAMETER_NAME;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.DOWNLOAD_SUFFIX;
import static org.jboss.tools.quarkus.core.QuarkusCoreConstants.EXTENSIONS_SUFFIX;
import static org.jboss.tools.quarkus.core.QuarkusCorePlugin.PLUGIN_ID;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jboss.tools.foundation.core.ecf.URLTransportUtility;
import org.jboss.tools.quarkus.core.QuarkusCoreConstants;
import org.jboss.tools.quarkus.core.QuarkusCorePlugin;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class QuarkusModelRegistry {
    private static final QuarkusModelRegistry INSTANCE = new QuarkusModelRegistry();
    
    private static final ObjectMapper mapper = new ObjectMapper();
    
    private static final URLTransportUtility TRANSPORT_UTILITY = new URLTransportUtility();
    
    public static QuarkusModelRegistry getDefault() {
        return INSTANCE;
    }
    
    private final Map<String, QuarkusModel> models = new HashMap<>();
    
    private QuarkusModelRegistry() {}
    
    public QuarkusModel getModel(IProgressMonitor monitor) throws CoreException {
        return getModel(CODE_ENDPOINT_URL, monitor);
    }
    
    private static String normalizeURL(String endPointURL) {
      endPointURL = endPointURL.trim();
      while (endPointURL.endsWith("/")) {
        endPointURL = endPointURL.substring(0, endPointURL.length() - 1);
      }
      return endPointURL;
    }
    
    public QuarkusModel getModel(String endpointURL, IProgressMonitor monitor) throws CoreException {
        endpointURL = normalizeURL(endpointURL);
        QuarkusModel model = models.get(endpointURL);
        if (model == null) {
            model = loadModel(endpointURL, monitor);
            models.put(endpointURL, model);
        }
        return model;
    }

    private QuarkusModel loadModel(String endpointURL, IProgressMonitor monitor) throws CoreException {
        try {
            endpointURL += EXTENSIONS_SUFFIX;
            File file = TRANSPORT_UTILITY.getCachedFileForURL(endpointURL, endpointURL, CACHE_FOREVER, monitor);
            if (file == null) {
              throw new CoreException(new Status(ERROR, PLUGIN_ID, "Invalid URL"));
            }
            return readModel(file);
        } catch (IOException ioe) {
            throw new CoreException(new Status(ERROR, PLUGIN_ID, ioe.getLocalizedMessage(), ioe));
        }
    }

	public static QuarkusModel readModel(File file) throws IOException, JsonParseException, JsonMappingException {
		List<QuarkusExtension> extensions = mapper.readValue(file, new TypeReference<List<QuarkusExtension>>() {
		});
		return new QuarkusModel(extensions);
	}
	
  private static String buildParameters(String tool, String groupId, String artifactId, String version,
      String className, String path, Set<QuarkusExtension> selected, boolean codeStarts) {
    ObjectNode json = JsonNodeFactory.instance.objectNode();

    json.put(CODE_TOOL_PARAMETER_NAME, tool);
    json.put(CODE_GROUP_ID_PARAMETER_NAME, groupId);
    json.put(CODE_ARTIFACT_ID_PARAMETER_NAME, artifactId);
    json.put(CODE_VERSION_PARAMETER_NAME, version);
    json.put(CODE_CLASSNAME_PARAMETER_NAME, className);
    json.put(CODE_PATH_PARAMETER_NAME, path);
    json.put(CODE_NO_EXAMPLE_CODE_PARAMETER_NAME, !codeStarts);
    ArrayNode extensions = JsonNodeFactory.instance.arrayNode();
    selected.forEach(extension -> extensions.add(extension.getId()));
    json.set(CODE_EXTENSIONS_PARAMETER_NAME, extensions);
    return json.toString();
  }
    
  public IStatus zip(String endpointURL, Tool tool, String groupId, String artifactId, String version, String className,
      String path, Set<QuarkusExtension> selected, boolean useCodeStarters, OutputStream output,
      IProgressMonitor monitor) {
    StringBuilder builder = new StringBuilder(normalizeURL(endpointURL));
    builder.append(DOWNLOAD_SUFFIX);
    try (CloseableHttpClient client = HttpClients.createDefault()) {

      HttpPost request = new HttpPost(builder.toString());
      request.setEntity(new StringEntity(
          buildParameters(tool.name(), groupId, artifactId, version, className, path, selected, useCodeStarters), ContentType.APPLICATION_JSON));
      request.addHeader(QuarkusCoreConstants.CODE_CLIENT_NAME_HEADER_NAME,
          QuarkusCoreConstants.CODE_CLIENT_NAME_HEADER_VALUE);
      request.addHeader(QuarkusCoreConstants.CODE_CLIENT_CONTACT_EMAIL_HEADER_NAME,
          QuarkusCoreConstants.CODE_CLIENT_CONTACT_EMAIL_HEADER_VALUE);
      IProxyService proxyService = QuarkusCorePlugin.getDefault().getProxyService();
      if (proxyService != null) {
        IProxyData[] proxyData = proxyService.select(new URI(builder.toString()));
        if (proxyData != null && proxyData.length > 0) {
          HttpHost proxy = new HttpHost(proxyData[0].getHost(), proxyData[0].getPort(), proxyData[0].getType()==IProxyData.HTTPS_PROXY_TYPE?"https":"http");
          request.setConfig(RequestConfig.custom().setProxy(proxy).build());
        }
      }
      try (CloseableHttpResponse response = client.execute(request)) {
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
          response.getEntity().writeTo(output);
          return Status.OK_STATUS;
        } else {
          return new Status(IStatus.ERROR, QuarkusCorePlugin.PLUGIN_ID, "Server returned status code " + response.getStatusLine().getStatusCode() + " message:" + response.getStatusLine().getReasonPhrase());
        }
      }
    } catch (IOException | URISyntaxException e) {
      return new Status(IStatus.ERROR, QuarkusCorePlugin.PLUGIN_ID, e.getLocalizedMessage(), e);
    }
  }

  public IStatus zip(String endpointURL, Tool tool, String groupId, String artifactId, String version, String className,
      String path, Set<QuarkusExtension> selected, boolean useCodeStarters, IPath output,
      IProgressMonitor monitor) {
    ByteArrayOutputStream content = new ByteArrayOutputStream();
    IStatus status = zip(endpointURL, tool, groupId, artifactId, version, className, path, selected, useCodeStarters, content, monitor);
    if (status.isOK()) {
      status = unzip(content.toByteArray(), output);
    }
    return status;
  }

    /**
   * Skip first level in path because Launcher backend returns a zip where the first folder is
   * the artifact id.
   * 
   * @param path the path of the zip entry
   * @return the computed path
   */
  public static String skipOneLevel(String path) {
      int index = path.indexOf('/');
      if (index != (-1)) {
          path = path.substring(index);
      }
      return path;
  }

  public static IStatus unzip(byte[] byteArray, IPath location) {
      ZipInputStream stream = new ZipInputStream(new ByteArrayInputStream(byteArray));
      ZipEntry entry;
      IStatus status = Status.OK_STATUS;
      try {
          while ((entry = stream.getNextEntry()) != null) {
              unzipEntry(location, stream, entry);
          }
      } catch (IOException e) {
          status = new Status(IStatus.ERROR, PLUGIN_ID, e.getLocalizedMessage(), e);
      }
      return status;
  }

  public static void unzipEntry(IPath location, ZipInputStream stream, ZipEntry entry)
          throws IOException {
      try {
          String name = skipOneLevel(entry.getName());
          if (!name.isEmpty()) {
              Path path = Paths.get(location.toOSString(), name);
              if (entry.isDirectory()) {
                  Files.createDirectories(path);
              } else {
                  try (OutputStream output = new FileOutputStream(path.toFile())) {
                      IOUtils.copy(stream, output);
                  }
              }
          }
      } catch (InvalidPathException e) {
          QuarkusCorePlugin.getDefault().getLog().log(new Status(IStatus.WARNING, PLUGIN_ID, e.getLocalizedMessage(), e));
      }
  }
}
