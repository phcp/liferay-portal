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
import com.google.gson.reflect.TypeToken;

import com.liferay.apio.architect.functional.Try;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.forms.apio.internal.FormFieldValue;
import com.liferay.forms.apio.internal.FormInstanceRecordServiceContext;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Paulo Cruz
 */
public class FormInstanceRecordResourceHelper {

	public static DDMFormValues getDDMFormValues(
		String fieldValues, DDMForm ddmForm, Locale locale) {

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		Gson gson = new Gson();

		FormFieldValueListToken formFieldValueListToken =
			new FormFieldValueListToken();

		Type listType = formFieldValueListToken.getType();

		List<FormFieldValue> formFieldValues = gson.fromJson(
			fieldValues, listType);

		for (FormFieldValue formFieldValue : formFieldValues) {
			DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

			ddmFormFieldValue.setInstanceId(formFieldValue.identifier);
			ddmFormFieldValue.setName(formFieldValue.name);

			LocalizedValue localizedValue = new LocalizedValue();

			localizedValue.addString(locale, formFieldValue.value);

			ddmFormFieldValue.setValue(localizedValue);

			ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);
		}

		return ddmFormValues;
	}

	public static DDMFormInstanceRecordVersion getVersion(
		DDMFormInstanceRecord ddmFormInstanceRecord) {

		return Try.fromFallible(
			ddmFormInstanceRecord::getVersion
		).map(
			ddmFormInstanceRecord::getFormInstanceRecordVersion
		).orElse(
			null
		);
	}

	public static void setServiceContextAttributes(
		FormInstanceRecordServiceContext formInstanceRecordServiceContext,
		boolean draft) {

		ServiceContext serviceContext =
			formInstanceRecordServiceContext.getServiceContext();

		if (draft) {
			serviceContext.setAttribute(
				"status", WorkflowConstants.STATUS_DRAFT);
			serviceContext.setAttribute("validateDDMFormValues", Boolean.FALSE);
			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);
		}
		else {
			serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);
		}
	}

	private static class FormFieldValueListToken
		extends TypeToken<ArrayList<FormFieldValue>> {
	}

}