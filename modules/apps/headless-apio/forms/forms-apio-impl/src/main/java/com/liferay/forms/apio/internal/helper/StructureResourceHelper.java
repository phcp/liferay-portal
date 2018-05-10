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

import com.liferay.apio.architect.functional.Try;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidation;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutPage;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutRow;
import com.liferay.dynamic.data.mapping.model.DDMFormSuccessPageSettings;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.forms.apio.internal.FormLayoutPage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Paulo Cruz
 */
public class StructureResourceHelper {

	public static List<Map.Entry<String, LocalizedValue>> getFieldOptions(
		DDMFormField ddmFormField) {

		DDMFormFieldOptions ddmFormFieldOptions =
			ddmFormField.getDDMFormFieldOptions();

		return _getFieldOptionsList(ddmFormFieldOptions);
	}

	public static List<Map.Entry<String, LocalizedValue>> getFieldOptions(
		Map<String, Object> properties, String propertyKey) {

		DDMFormFieldOptions ddmFormFieldOptions =
			(DDMFormFieldOptions)properties.get(propertyKey);

		return _getFieldOptionsList(ddmFormFieldOptions);
	}

	public static <T> T getFieldProperty(
		Function<String, T> converterFunction, Object property) {

		String propertyString = property.toString();

		return converterFunction.apply(propertyString);
	}

	public static DDMFormFieldValidation getFieldValidation(
		DDMFormField ddmFormField) {

		Map<String, Object> properties = ddmFormField.getProperties();

		return (DDMFormFieldValidation)properties.get("validation");
	}

	public static List<FormLayoutPage> getPages(DDMStructure ddmStructure) {
		return Try.fromFallible(
			() -> {
				DDMFormLayout ddmFormLayout = ddmStructure.getDDMFormLayout();
				List<DDMFormField> ddmFormFields =
					ddmStructure.getDDMFormFields(true);

				List<DDMFormLayoutPage> ddmFormLayoutPages =
					ddmFormLayout.getDDMFormLayoutPages();

				Stream<DDMFormLayoutPage> ddmFormLayoutPageStream =
					ddmFormLayoutPages.stream();

				return ddmFormLayoutPageStream.map(
					ddmFormLayoutPage -> {
						List<String> fieldNamesPerPage = _getFieldNames(
							ddmFormLayoutPage);

						Stream<DDMFormField> ddmFormFieldStream =
							ddmFormFields.stream();

						List<DDMFormField> ddmFormFieldsPerPage =
							ddmFormFieldStream.filter(
								ddmFormField -> fieldNamesPerPage.contains(
									ddmFormField.getName())
							).collect(
								Collectors.toList()
							);

						return new FormLayoutPage(
							ddmFormLayoutPage.getDescription(),
							ddmFormFieldsPerPage, ddmFormLayoutPage.getTitle());

					}).collect(Collectors.toList());
			}
		).orElse(
			null
		);
	}

	public static DDMFormSuccessPageSettings getSuccessPage(
		DDMStructure ddmStructure) {

		DDMForm ddmForm = ddmStructure.getDDMForm();

		return ddmForm.getDDMFormSuccessPageSettings();
	}

	public static DDMStructureVersion getVersion(DDMStructure ddmStructure) {
		return Try.fromFallible(
			ddmStructure::getStructureVersion
		).orElse(
			null
		);
	}

	private static List<String> _getFieldNames(
		DDMFormLayoutPage ddmFormLayoutPage) {

		List<DDMFormLayoutRow> ddmFormLayoutRows =
			ddmFormLayoutPage.getDDMFormLayoutRows();

		Stream<DDMFormLayoutRow> ddmFormLayoutRowStream =
			ddmFormLayoutRows.stream();

		return ddmFormLayoutRowStream.map(
			DDMFormLayoutRow::getDDMFormLayoutColumns
		).flatMap(
			List::stream
		).map(
			ddmFormLayoutColumn -> ddmFormLayoutColumn.getDDMFormFieldNames()
		).flatMap(
			List::stream
		).collect(
			Collectors.toList()
		);
	}

	private static List<Map.Entry<String, LocalizedValue>> _getFieldOptionsList(
		DDMFormFieldOptions ddmFormFieldOptions) {

		Map<String, LocalizedValue> options = ddmFormFieldOptions.getOptions();

		return new ArrayList<>(options.entrySet());
	}

}