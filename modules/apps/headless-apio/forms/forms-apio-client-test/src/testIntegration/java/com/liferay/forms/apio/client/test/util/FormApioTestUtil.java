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

package com.liferay.forms.apio.client.test.util;

import com.liferay.forms.apio.client.test.internal.activator.BaseFormApioTestBundleActivator;
import com.liferay.portal.apio.test.util.ApioClientBuilder;

import io.restassured.response.ValidatableResponse;

import java.net.URL;

/**
 * @author Paulo Cruz
 */
public final class FormApioTestUtil {

	public static <T> T getFieldProperties(
		URL rootEndpointURL, String fieldName) {

		ValidatableResponse formStructureResponse = _getFormWithDefaultLanguage(
			rootEndpointURL);

		return formStructureResponse.extract(
		).path(
			"_embedded.Form[0]._embedded.formPages._embedded[0]._embedded." +
				"fields._embedded.find {it.name == '%s'}",
			fieldName
		);
	}

	public static <T> T getFieldProperty(
		URL rootEndpointURL, String fieldName, String fieldPropertyName) {

		ValidatableResponse formStructureResponse = _getFormWithDefaultLanguage(
			rootEndpointURL);

		return formStructureResponse.extract(
		).path(
			"_embedded.Form[0]._embedded.formPages._embedded[0]._embedded." +
				"fields._embedded.find {it.name == '%s'}.%s",
			fieldName, fieldPropertyName
		);
	}

	private static ValidatableResponse _getForm(
		URL rootEndpointURL, String acceptLanguage) {

		String formsEndpointURL =
			_getFormsEndpointURL(rootEndpointURL) + "?embedded=structure";

		return ApioClientBuilder.given(
		).basicAuth(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/hal+json"
		).header(
			"Accept-Language", acceptLanguage
		).when(
		).get(
			formsEndpointURL
		).then(
		).log(
		).ifError(
		).statusCode(
			200
		);
	}

	private static String _getFormsEndpointURL(URL rootEndpointURL) {
		return ApioClientBuilder.given(
		).basicAuth(
			"test@liferay.com", "test"
		).header(
			"Accept", "application/hal+json"
		).header(
			"Accept-Language", "en-US"
		).when(
		).get(
			rootEndpointURL.toExternalForm()
		).follow(
			"_links.content-space.href"
		).then(
		).extract(
		).path(
			"_embedded.ContentSpace.find {it.name == '%s'}._links.forms.href",
			BaseFormApioTestBundleActivator.SITE_NAME
		);
	}

	private static ValidatableResponse _getFormWithDefaultLanguage(
		URL rootEndpointURL) {

		return _getForm(
			rootEndpointURL,
			BaseFormApioTestBundleActivator.FORM_DEFAULT_LANGUAGE);
	}

}