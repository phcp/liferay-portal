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

import com.liferay.apio.architect.functional.Try;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
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
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Paulo Cruz
 */
public final class StructureRepresentorUtil {

	public static Function<DDMFormField,
		List<Map.Entry<String, LocalizedValue>>> getFieldOptions(
			Function<DDMFormField, DDMFormFieldOptions> function) {

		return ddmFormField -> Try.fromFallible(
			() -> function.apply(ddmFormField)
		).map(
			DDMFormFieldOptions::getOptions
		).map(
			Map::entrySet
		).map(
			ArrayList::new
		).orElse(
			null
		);
	}

	public static Function<DDMFormField,
		List<Map.Entry<String, LocalizedValue>>> getFieldOptions(String key) {

		return getFieldOptions(
			ddmFormField -> (DDMFormFieldOptions)ddmFormField.getProperty(key));
	}

	public static <T> Function<DDMFormField, T> getFieldProperty(
		Function<Object, T> parseFunction, String key) {

		return ddmFormField -> Try.fromFallible(
			() -> ddmFormField.getProperty(key)
		).map(
			parseFunction::apply
		).orElse(
			null
		);
	}

	public static BiFunction<DDMFormField, Locale, String> getLocalizedValue(
		String key) {

		return LocalizedValueUtil.getLocalizedString(
			ddmFormField -> (LocalizedValue)ddmFormField.getProperty(key));
	}

	public static List<FormLayoutPage> getPages(DDMStructure ddmStructure) {
		return Try.fromFallible(
			ddmStructure::getDDMFormLayout
		).map(
			DDMFormLayout::getDDMFormLayoutPages
		).map(
			List::stream
		).orElseGet(
			Stream::empty
		).map(
			ddmFormLayoutPage ->
				_getFormLayoutPage(ddmStructure, ddmFormLayoutPage)
		).collect(
			Collectors.toList()
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

	private static List<DDMFormField> _getFieldsPerPage(
		DDMStructure ddmStructure, List<String> fieldNamesPerPage) {

		return Try.fromFallible(
			() -> ddmStructure.getDDMFormFields(true)
		).map(
			List::stream
		).orElseGet(
			Stream::empty
		).filter(
			ddmFormField -> fieldNamesPerPage.contains(ddmFormField.getName())
		).collect(
			Collectors.toList()
		);
	}

	private static FormLayoutPage _getFormLayoutPage(
		DDMStructure ddmStructure, DDMFormLayoutPage ddmFormLayoutPage) {

		List<String> fieldNamesPerPage = _getFieldNames(ddmFormLayoutPage);

		List<DDMFormField> ddmFormFields = _getFieldsPerPage(
			ddmStructure, fieldNamesPerPage);

		return new FormLayoutPage(
			ddmFormLayoutPage.getDescription(), ddmFormFields,
			ddmFormLayoutPage.getTitle());
	}

}