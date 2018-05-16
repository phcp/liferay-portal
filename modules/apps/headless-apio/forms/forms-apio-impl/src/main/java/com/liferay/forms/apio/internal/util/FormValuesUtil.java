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

package com.liferay.forms.apio.internal.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.forms.apio.internal.FormFieldValue;

import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Paulo Cruz
 */
public class FormValuesUtil {

	public static DDMFormValues getDDMFormValues(
		String fieldValues, DDMForm ddmForm, Locale locale) {

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		FormValuesUtil.FormFieldValueListToken formFieldValueListToken =
			new FormValuesUtil.FormFieldValueListToken();

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		Type listType = formFieldValueListToken.getType();

		Gson gson = new Gson();

		List<FormFieldValue> formFieldValues = gson.fromJson(
			fieldValues, listType);

		for (FormFieldValue formFieldValue : formFieldValues) {
			DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

			ddmFormFieldValue.setInstanceId(formFieldValue.identifier);
			ddmFormFieldValue.setName(formFieldValue.name);

			DDMFormField ddmFormField = ddmFormFieldsMap.get(
				formFieldValue.name);

			Value value;

			if (ddmFormField.isLocalizable()) {
				value = new LocalizedValue();

				value.addString(locale, formFieldValue.value);
			}
			else {
				value = new UnlocalizedValue(formFieldValue.value);
			}

			ddmFormFieldValue.setValue(value);

			ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);
		}

		return ddmFormValues;
	}

	private static class FormFieldValueListToken
		extends TypeToken<ArrayList<FormFieldValue>> {
	}

}