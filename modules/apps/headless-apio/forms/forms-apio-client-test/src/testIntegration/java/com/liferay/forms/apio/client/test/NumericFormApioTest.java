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

import com.liferay.forms.apio.client.test.internal.activator.NumericFormApioTestBundleActivator;
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
public class NumericFormApioTest {

	@Deployment
	public static Archive<?> getArchive() throws Exception {
		return OAuth2ProviderTestUtil.getArchive(
			NumericFormApioTestBundleActivator.class);
	}

	@Before
	public void setUp() throws MalformedURLException {
		_rootEndpointURL = new URL(_url, "/o/api");
	}

	@Test
	public void testGetNumericFieldsFromFormStructure() {
		_assertNumericFieldProperties(_DOUBLE_FIELD_NAME);
		_assertNumericFieldProperties(_INTEGER_FIELD_NAME);
	}

	@Test
	public void testNumericFieldsDataTypeIsDisplayed() {
		String doubleDataType = FormApioTestUtil.getFieldProperty(
			_rootEndpointURL, _DOUBLE_FIELD_NAME, "dataType");

		String integerDataType = FormApioTestUtil.getFieldProperty(
			_rootEndpointURL, _INTEGER_FIELD_NAME, "dataType");

		assertThat(doubleDataType, equalTo("double"));
		assertThat(integerDataType, equalTo("integer"));
	}

	@Test
	public void testNumericFieldsLabelIsDisplayed() {
		String doubleLabel = FormApioTestUtil.getFieldProperty(
			_rootEndpointURL, _DOUBLE_FIELD_NAME, "label");

		String integerLabel = FormApioTestUtil.getFieldProperty(
			_rootEndpointURL, _INTEGER_FIELD_NAME, "label");

		assertThat(doubleLabel, equalTo("My Double Field"));
		assertThat(integerLabel, equalTo("My Integer Field"));
	}

	private void _assertNumericFieldProperties(String fieldName) {
		Map<String, Object> fieldProperties =
			FormApioTestUtil.getFieldProperties(_rootEndpointURL, fieldName);

		assertThat(fieldProperties.get("hasFormRules"), isBoolean());
		assertThat(fieldProperties.get("showLabel"), isBoolean());
		assertThat(fieldProperties.get("repeatable"), isBoolean());
		assertThat(fieldProperties.get("required"), isBoolean());
	}

	private static final String _DOUBLE_FIELD_NAME = "MyDoubleField";

	private static final String _INTEGER_FIELD_NAME = "MyIntegerField";

	private URL _rootEndpointURL;

	@ArquillianResource
	private URL _url;

}