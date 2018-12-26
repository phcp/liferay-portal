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
import static org.hamcrest.Matchers.notNullValue;

import com.liferay.forms.apio.client.test.activator.FormStructureApioTestBundleActivator;
import com.liferay.forms.apio.client.test.util.FormStructureApioTestUtil;
import com.liferay.oauth2.provider.test.util.OAuth2ProviderTestUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

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
public class TextFieldFormStructureApioTest {

	private static final String TEXT_FIELD_NAME = "MyTextField";

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
	public void testGetTextFieldFromFormStructure() {
		Map<String, Object> fieldProperties =
			FormStructureApioTestUtil.getFieldProperties(
				_rootEndpointURL, TEXT_FIELD_NAME);

		assertThat(fieldProperties.get("autocomplete"), notNullValue());
		assertThat(fieldProperties.get("displayStyle"), notNullValue());
		assertThat(fieldProperties.get("hasFormRules"), notNullValue());
		assertThat(fieldProperties.get("showLabel"), notNullValue());
		assertThat(fieldProperties.get("repeatable"), notNullValue());
		assertThat(fieldProperties.get("required"), notNullValue());
	}

	@Test
	public void testTextFieldDataTypeIsDisplayed() {
		String dataType = FormStructureApioTestUtil.getFieldProperty(
			_rootEndpointURL, TEXT_FIELD_NAME, "dataType");

		assertThat(dataType, equalTo("string"));
	}

	@Test
	public void testTextFieldInputControlIsDisplayed() {
		String inputControl = FormStructureApioTestUtil.getFieldProperty(
			_rootEndpointURL, TEXT_FIELD_NAME, "inputControl");

		assertThat(inputControl, equalTo("text"));
	}

	@Test
	public void testTextFieldLabelIsDisplayed() {
		String label = FormStructureApioTestUtil.getFieldProperty(
			_rootEndpointURL, TEXT_FIELD_NAME, "label");

		assertThat(label, equalTo("My Text Field"));
	}

	private URL _rootEndpointURL;

	@ArquillianResource
	private URL _url;

}