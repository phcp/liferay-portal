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

import static com.liferay.forms.apio.client.test.matcher.FormApioTestMatchers.isBoolean;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.liferay.forms.apio.client.test.internal.activator.TextFormApioTestBundleActivator;
import com.liferay.forms.apio.client.test.util.FormApioTestUtil;
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
public class TextFormApioTest {

	@Deployment
	public static Archive<?> getArchive() throws Exception {
		return OAuth2ProviderTestUtil.getArchive(
			TextFormApioTestBundleActivator.class);
	}

	@Before
	public void setUp() throws MalformedURLException {
		_rootEndpointURL = new URL(_url, "/o/api");
	}

	@Test
	public void testGetTextFieldFromForm() {
		Map<String, Object> fieldProperties =
			FormApioTestUtil.getFieldProperties(
				_rootEndpointURL, _TEXT_FIELD_NAME);

		assertThat(fieldProperties.get("displayStyle"), notNullValue());
		assertThat(fieldProperties.get("hasFormRules"), isBoolean());
		assertThat(fieldProperties.get("showLabel"), isBoolean());
		assertThat(fieldProperties.get("repeatable"), isBoolean());
		assertThat(fieldProperties.get("required"), isBoolean());
	}

	@Test
	public void testTextFieldDataTypeIsDisplayed() {
		String dataType = FormApioTestUtil.getFieldProperty(
			_rootEndpointURL, _TEXT_FIELD_NAME, "dataType");

		assertThat(dataType, equalTo("string"));
	}

	@Test
	public void testTextFieldDisplayStyle() {
		String multilineDisplayStyle = FormApioTestUtil.getFieldProperty(
			_rootEndpointURL, _MULTILINE_TEXT_FIELD_NAME, "displayStyle");

		String singlelineDisplayStyle = FormApioTestUtil.getFieldProperty(
			_rootEndpointURL, _TEXT_FIELD_NAME, "displayStyle");

		assertThat(multilineDisplayStyle, equalTo("multiline"));
		assertThat(singlelineDisplayStyle, equalTo("singleline"));
	}

	@Test
	public void testTextFieldInputControlIsDisplayed() {
		String inputControl = FormApioTestUtil.getFieldProperty(
			_rootEndpointURL, _TEXT_FIELD_NAME, "inputControl");

		assertThat(inputControl, equalTo("text"));
	}

	@Test
	public void testTextFieldLabelIsDisplayed() {
		String label = FormApioTestUtil.getFieldProperty(
			_rootEndpointURL, _TEXT_FIELD_NAME, "label");

		assertThat(label, equalTo("My Text Field"));
	}

	private static final String _MULTILINE_TEXT_FIELD_NAME =
		"MyMultilineTextField";

	private static final String _TEXT_FIELD_NAME = "MyTextField";

	private URL _rootEndpointURL;

	@ArquillianResource
	private URL _url;

}