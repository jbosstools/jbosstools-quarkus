/*******************************************************************************
* Copyright (c) 2020 Red Hat Inc. and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package org.jboss.tools.quarkus.lsp4e.core.restclient.java;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4mp.commons.MicroProfileJavaCodeLensParams;
import org.eclipse.lsp4mp.jdt.core.BasePropertiesManagerTest;
import org.eclipse.lsp4mp.jdt.core.PropertiesManagerForJava;
import org.eclipse.lsp4mp.jdt.core.utils.IJDTUtils;
import org.eclipse.lsp4mp.jdt.internal.core.providers.MicroProfileConfigSourceProvider;
import org.junit.Assert;
import org.junit.Test;

/**
 * MicroProfile RestClient URL Codelens test for Java file.
 *
 * @author Angelo ZERR
 *
 */
public class MicroProfileRestClientJavaCodeLensTest extends BasePropertiesManagerTest {

	@Test
	public void urlCodeLensProperties() throws Exception {
		IJavaProject javaProject = loadMavenProject(MicroProfileMavenProjectName.rest_client_quickstart);
		IJDTUtils utils = JDT_UTILS;

		// Initialize file
		initConfigFile(javaProject);

		MicroProfileJavaCodeLensParams params = new MicroProfileJavaCodeLensParams();
		IFile javaFile = javaProject.getProject()
				.getFile(new Path("src/main/java/org/acme/restclient/CountriesService.java"));
		params.setUri(javaFile.getLocation().toFile().toURI().toString());
		params.setUrlCodeLensEnabled(true);

		// No configuration of base url
		List<? extends CodeLens> lenses = PropertiesManagerForJava.getInstance().codeLens(params, utils,
				new NullProgressMonitor());
		Assert.assertEquals(0, lenses.size());

		// /mp-rest/url
		saveFile(MicroProfileConfigSourceProvider.MICROPROFILE_CONFIG_PROPERTIES_FILE,
				"org.acme.restclient.CountriesService/mp-rest/url = https://restcountries.url/rest", javaProject);
		assertCodeLenses("https://restcountries.url/rest", params, utils);

		// /mp-rest/uri
		saveFile(MicroProfileConfigSourceProvider.MICROPROFILE_CONFIG_PROPERTIES_FILE, //
				"org.acme.restclient.CountriesService/mp-rest/uri = https://restcountries.uri/rest" + //
						System.lineSeparator() + //
						"org.acme.restclient.CountriesService/mp-rest/url = https://restcountries.url/rest", //
				javaProject);
		assertCodeLenses("https://restcountries.uri/rest", params, utils);
	}

	@Test
	public void urlCodeLensPropertiesWithPropertyExpression() throws Exception {
		IJavaProject javaProject = loadMavenProject(MicroProfileMavenProjectName.rest_client_quickstart);
		IJDTUtils utils = JDT_UTILS;

		// Initialize file
		initConfigFile(javaProject);

		MicroProfileJavaCodeLensParams params = new MicroProfileJavaCodeLensParams();
		IFile javaFile = javaProject.getProject()
				.getFile(new Path("src/main/java/org/acme/restclient/CountriesService.java"));
		params.setUri(javaFile.getLocation().toFile().toURI().toString());
		params.setUrlCodeLensEnabled(true);

		// No configuration of base url
		List<? extends CodeLens> lenses = PropertiesManagerForJava.getInstance().codeLens(params, utils,
				new NullProgressMonitor());
		Assert.assertEquals(0, lenses.size());

		// /mp-rest/url
		// get final value by following expressions transitively
		saveFile(MicroProfileConfigSourceProvider.MICROPROFILE_CONFIG_PROPERTIES_FILE,
				"org.acme.restclient.CountriesService/mp-rest/url = https://restcountries.url/${asdf}\nasdf=${hjkl}\nhjkl=rest",
				javaProject);
		assertCodeLenses("https://restcountries.url/rest", params, utils);

		// /mp-rest/uri
		// get final value though using default
		saveFile(MicroProfileConfigSourceProvider.MICROPROFILE_CONFIG_PROPERTIES_FILE,
				"org.acme.restclient.CountriesService/mp-rest/url = https://restcountries.url/${ASDF:rest}",
				javaProject);
		assertCodeLenses("https://restcountries.url/rest", params, utils);

		// /mp-rest/uri
		// cyclic dependencies between properties
		// this will likely prevent the micro profile application from starting,
		// but we also want to prevent the language server from freezing
		saveFile(MicroProfileConfigSourceProvider.MICROPROFILE_CONFIG_PROPERTIES_FILE,
				"org.acme.restclient.CountriesService/mp-rest/url = https://restcountries.url/${asdf}\nasdf=${hjkl}\nhjkl=${org.acme.restclient.CountriesService/mp-rest/url}",
				javaProject);
		assertCodeLenses("https://restcountries.url/${asdf}", params, utils);

		// /mp-rest/uri
		// self loop
		// this will also prevent the micro profile application from starting
		saveFile(MicroProfileConfigSourceProvider.MICROPROFILE_CONFIG_PROPERTIES_FILE,
				"org.acme.restclient.CountriesService/mp-rest/url = https://restcountries.url/${org.acme.restclient.CountriesService/mp-rest/url}",
				javaProject);
		assertCodeLenses("https://restcountries.url/${org.acme.restclient.CountriesService/mp-rest/url}", params,
				utils);
		
		// /mp-rest/uri
		// double reference
		saveFile(MicroProfileConfigSourceProvider.MICROPROFILE_CONFIG_PROPERTIES_FILE,
				"org.acme.restclient.CountriesService/mp-rest/url = https://restcountries.url/${asdf}${asdf}\nasdf=rest",
				javaProject);
		assertCodeLenses("https://restcountries.url/restrest", params,
				utils);

		// /mp-rest/uri
		// billion laughs
		saveFile(MicroProfileConfigSourceProvider.MICROPROFILE_CONFIG_PROPERTIES_FILE,
				"org.acme.restclient.CountriesService/mp-rest/url = https://restcountries.url/${lulz}\n" //
				+ "lulz=${lol9}${lol9}${lol9}${lol9}${lol9}${lol9}${lol9}${lol9}${lol9}\n" //
				+ "lol9=${lol8}${lol8}${lol8}${lol8}${lol8}${lol8}${lol8}${lol8}${lol8}\n" //
				+ "lol8=${lol7}${lol7}${lol7}${lol7}${lol7}${lol7}${lol7}${lol7}${lol7}\n" //
				+ "lol7=${lol6}${lol6}${lol6}${lol6}${lol6}${lol6}${lol6}${lol6}${lol6}\n" //
				+ "lol6=${lol5}${lol5}${lol5}${lol5}${lol5}${lol5}${lol5}${lol5}${lol5}\n" //
				+ "lol5=${lol4}${lol4}${lol4}${lol4}${lol4}${lol4}${lol4}${lol4}${lol4}\n" //
				+ "lol4=${lol3}${lol3}${lol3}${lol3}${lol3}${lol3}${lol3}${lol3}${lol3}\n" //
				+ "lol3=${lol2}${lol2}${lol2}${lol2}${lol2}${lol2}${lol2}${lol2}${lol2}\n" //
				+ "lol2=${lol1}${lol1}${lol1}${lol1}${lol1}${lol1}${lol1}${lol1}${lol1}\n" //
				+ "lol1=${lol}${lol}${lol}${lol}${lol}${lol}${lol}${lol}${lol}${lol}\n" //
				+ "lol=lol",
				javaProject);
		assertCodeLenses("https://restcountries.url/${lulz}", params,
				utils);

	}

	private static void initConfigFile(IJavaProject javaProject) throws Exception {
		saveFile(MicroProfileConfigSourceProvider.MICROPROFILE_CONFIG_PROPERTIES_FILE, "", javaProject);
	}

	@Test
	public void urlCodeLensPropertiesWithAnnotationBaseUri() throws Exception {
		IJavaProject javaProject = loadMavenProject(MicroProfileMavenProjectName.rest_client_quickstart);
		IJDTUtils utils = JDT_UTILS;

		// Initialize file
		initConfigFile(javaProject);

		MicroProfileJavaCodeLensParams params = new MicroProfileJavaCodeLensParams();
		IFile javaFile = javaProject.getProject()
				.getFile(new Path("src/main/java/org/acme/restclient/CountriesServiceWithBaseUri.java"));
		params.setUri(javaFile.getLocation().toFile().toURI().toString());
		params.setUrlCodeLensEnabled(true);

		// @RegisterRestClient(baseUri = "https://rescountries.ann/rest")
		// public class CountriesServiceWithBaseUri
		assertCodeLenses("https://restcountries.ann/rest", params, utils);

		// /mp-rest/url overrides @RegisterRestClient/baseUri
		saveFile(MicroProfileConfigSourceProvider.MICROPROFILE_CONFIG_PROPERTIES_FILE,
				"org.acme.restclient.CountriesServiceWithBaseUri/mp-rest/url = https://restcountries.url/rest",
				javaProject);
		assertCodeLenses("https://restcountries.url/rest", params, utils);

		// /mp-rest/uri overrides @RegisterRestClient/baseUri
		saveFile(MicroProfileConfigSourceProvider.MICROPROFILE_CONFIG_PROPERTIES_FILE, //
				"org.acme.restclient.CountriesServiceWithBaseUri/mp-rest/uri = https://restcountries.uri/rest" + //
						System.lineSeparator() + //
						"org.acme.restclient.CountriesServiceWithBaseUri/mp-rest/url = https://restcountries.url/rest", //
				javaProject);
		assertCodeLenses("https://restcountries.uri/rest", params, utils);
	}

	private static void assertCodeLenses(String baseURL, MicroProfileJavaCodeLensParams params, IJDTUtils utils)
			throws JavaModelException {
		List<? extends CodeLens> lenses = PropertiesManagerForJava.getInstance().codeLens(params, utils,
				new NullProgressMonitor());
		Assert.assertEquals(8, lenses.size());

		// @GET
		// @Path("/name/{name}")
		// Set<Country> getByName(@PathParam String name);
		CodeLens lensForGet = lenses.get(0);
		Assert.assertNotNull(lensForGet.getCommand());
		Assert.assertEquals(baseURL + "/v2/name/{name}", lensForGet.getCommand().getTitle());

		// @GET
		// @Path("/name/{name}")
		// CompletionStage<Set<Country>> getByNameAsync(@PathParam String name);
		CodeLens lensForGetSingle = lenses.get(1);
		Assert.assertNotNull(lensForGetSingle.getCommand());
		Assert.assertEquals(baseURL + "/v2/name/{name}", lensForGetSingle.getCommand().getTitle());
		
		// @POST
		// @Path("/post")
		// String post();
		CodeLens lensForPost = lenses.get(2);
		Assert.assertNotNull(lensForPost.getCommand());
		Assert.assertEquals(baseURL + "/v2/post", lensForPost.getCommand().getTitle());
		
		// @PUT
		// @Path("/put")
		// String put();
		CodeLens lensForPut = lenses.get(3);
		Assert.assertNotNull(lensForPut.getCommand());
		Assert.assertEquals(baseURL + "/v2/put", lensForPut.getCommand().getTitle());
		
		// @DELETE
		// @Path("/delete")
		// String delete();
		CodeLens lensForDelete = lenses.get(4);
		Assert.assertNotNull(lensForDelete.getCommand());
		Assert.assertEquals(baseURL + "/v2/delete", lensForDelete.getCommand().getTitle());

		// @HEAD
		// @Path("/head")
		// String head();
		CodeLens lensForHead = lenses.get(5);
		Assert.assertNotNull(lensForHead.getCommand());
		Assert.assertEquals(baseURL + "/v2/head", lensForHead.getCommand().getTitle());

		// @OPTIONS
		// @Path("/options")
		// String options();
		CodeLens lensForOptions = lenses.get(6);
		Assert.assertNotNull(lensForOptions.getCommand());
		Assert.assertEquals(baseURL + "/v2/options", lensForOptions.getCommand().getTitle());

		// @PATCH
		// @Path("/patch")
		// String patch();
		CodeLens lensForPatch = lenses.get(7);
		Assert.assertNotNull(lensForPatch.getCommand());
		Assert.assertEquals(baseURL + "/v2/patch", lensForPatch.getCommand().getTitle());
	}

}
