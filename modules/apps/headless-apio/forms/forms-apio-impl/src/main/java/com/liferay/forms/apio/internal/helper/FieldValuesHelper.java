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

package com.liferay.forms.apio.internal.helper;

import com.google.gson.Gson;
import com.liferay.apio.architect.functional.Try;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.forms.apio.internal.architect.form.FieldValueForm;
import com.liferay.forms.apio.internal.model.FileEntryValue;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * @author Paulo Cruz
 */
@Component(immediate = true, service = FieldValuesHelper.class)
public final class FieldValuesHelper {

	public DDMFormValues getDDMFormValues(
		List<FieldValueForm> fieldValueForms, DDMForm ddmForm, Locale locale) {

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		ddmFormValues.addAvailableLocale(locale);
		ddmFormValues.setDefaultLocale(locale);

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		for (FieldValueForm fieldValueForm : fieldValueForms) {
			DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

			ddmFormFieldValue.setName(fieldValueForm.getName());

			DDMFormField ddmFormField = ddmFormFieldsMap.get(
				fieldValueForm.getName());

			Value value = _EMPTY_VALUE;

			if (ddmFormField != null) {
				value = Optional.ofNullable(
					fieldValueForm.getValue()
				).map(
					stringValue -> _getValue(stringValue, ddmFormField, locale)
				).orElseGet(
					() -> _getDocumentValue(
						fieldValueForm, ddmFormField, locale)
				);
			}

			_setFieldValue(value, ddmFormValues, ddmFormFieldValue);
		}

		return ddmFormValues;
	}

	private Value _getDocumentValue(
		FieldValueForm fieldValueForm, DDMFormField ddmFormField,
		Locale locale) {

		return Optional.ofNullable(
			fieldValueForm.getDocument()
		).map(
			document -> _getFileData(fieldValueForm)
		).map(
			stringValue -> _getValue(stringValue, ddmFormField, locale)
		).orElse(
			_EMPTY_VALUE
		);
	}

	private String _getFileData(FieldValueForm fieldValueForm) {
		return Try.fromFallible(
			fieldValueForm::getDocument
		).map(
			_dlAppService::getFileEntry
		).map(
			fileEntry -> new FileEntryValue(
				fileEntry.getFileEntryId(), fileEntry.getGroupId(),
				fileEntry.getTitle(), fileEntry.getMimeType(),
				fileEntry.getUuid(), fileEntry.getVersion())
		).map(
			fileEntryValue -> gson.toJson(fileEntryValue)
		).orElse(
			null
		);
	}

	private Value _getValue(
		String stringValue, DDMFormField ddmFormField, Locale locale) {

		if (ddmFormField.isLocalizable()) {
			LocalizedValue localizedValue = new LocalizedValue();

			localizedValue.addString(locale, stringValue);

			return localizedValue;
		}

		return new UnlocalizedValue(stringValue);
	}

	private void _setFieldValue(
		Value value, DDMFormValues ddmFormValues,
		DDMFormFieldValue ddmFormFieldValue) {

		ddmFormFieldValue.setValue(value);

		ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);
	}

	private static final Value _EMPTY_VALUE = new UnlocalizedValue(
		(String)null);

	private Gson gson = new Gson();

	@Reference
	private DLAppService _dlAppService;

}