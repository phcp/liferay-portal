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

package com.liferay.forms.apio.client.test.internal.activator;

import com.liferay.dynamic.data.mapping.annotations.DDMForm;
import com.liferay.dynamic.data.mapping.annotations.DDMFormField;

/**
 * @author Paulo Cruz
 */
public class TextFormApioTestBundleActivator
	extends BaseFormApioTestBundleActivator {

	@Override
	protected Class<?> getFormDefinitionClass() {
		return FormWithTextFields.class;
	}

	@DDMForm
	private interface FormWithTextFields {

		@DDMFormField(
			label = "My Text Field", name = "MyTextField",
			properties = "displayStyle=singleline", type = "text"
		)
		public String textValue();

	}

}