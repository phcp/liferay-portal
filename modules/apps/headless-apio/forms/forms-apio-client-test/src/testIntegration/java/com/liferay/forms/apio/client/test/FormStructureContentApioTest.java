/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.forms.apio.client.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.liferay.forms.apio.client.test.activator.FormStructureApioTestBundleActivator;
import com.liferay.oauth2.provider.test.util.OAuth2ProviderTestUtil;
import com.liferay.portal.apio.test.util.ApioClientBuilder;

import java.net.MalformedURLException;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Paulo Cruz
 */
@RunAsClient
@RunWith(Arquillian.class)
public class FormStructureContentApioTest {

	@Deployment
	public static Archive<?> getArchive() throws Exception {
		return OAuth2ProviderTestUtil.getArchive(
			FormStructureApioTestBundleActivator.class);
	}

	@Before
	public void setUp() throws MalformedURLException {
		_rootEndpointURL = new URL(_url, "/o/api");
	}

	@Test
	public void testTextFieldDataTypeIsDisplayed() {
		String dataType = _getFieldProperty("MyTextField", "dataType");

		assertThat(dataType, equalTo("string"));
	}

	@Test
	public void testTextFieldInputControlIsDisplayed() {
		String inputControl = _getFieldProperty("MyTextField", "inputControl");

		assertThat(inputControl, equalTo("text"));
	}

	private <T> T _getFieldProperty(
		String fieldName, String fieldPropertyName) {

		return ApioClientBuilder.given(
		).basicAuth(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/hal+json"
		).header(
			"Accept-Language", "en-US"
		).when(
		).get(
			_getFormStructuresLink()
		).then(
		).log(
		).ifError(
		).statusCode(
			200
		).extract(
		).path(
			"_embedded.Structure[0]._embedded.formPages._embedded[0]." +
				"_embedded.fields._embedded.find {it.name == '%s'}.%s",
			fieldName, fieldPropertyName
		);
	}

	private String _getFormStructuresLink() {
		return ApioClientBuilder.given(
		).basicAuth(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/hal+json"
		).header(
			"Accept-Language", "en-US"
		).when(
		).get(
			_rootEndpointURL.toExternalForm()
		).follow(
			"_links.content-space.href"
		).then(
		).extract(
		).path(
			"_embedded.ContentSpace.find {it.name == '%s'}._links." +
			"formStructures.href",
			FormStructureApioTestBundleActivator.SITE_NAME
		);
	}

	private URL _rootEndpointURL;

	@ArquillianResource
	private URL _url;

}