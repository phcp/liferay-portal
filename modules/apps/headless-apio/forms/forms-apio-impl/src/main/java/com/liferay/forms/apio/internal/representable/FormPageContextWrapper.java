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

package com.liferay.forms.apio.internal.representable;

import com.liferay.apio.architect.functional.Try;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Paulo Cruz
 */
public class FormPageContextWrapper extends BaseFormContextWrapper {

	public FormPageContextWrapper(Object wrappedMap) {
		super(wrappedMap);
	}

	public LocalizedValue getDescription() {
		return getValue("localizedDescription", LocalizedValue.class);
	}

	public List<FormFieldContextWrapper> getFormFieldContexts() {
		List<List<BaseFormContextWrapper>> columnsListOfList = Try.fromFallible(
			() -> getListFromValue("rows")
		).map(
			List::stream
		).orElseGet(
			Stream::empty
		).map(
			row -> getListFromMap(row.getWrappedMap(), "columns")
		).collect(Collectors.toList());

		List<BaseFormContextWrapper> columns =
			columnsListOfList.stream().reduce(
				(columns1, columns2) -> {
					columns1.addAll(columns2);
					return columns1;
				}
			).get();


		List<FormFieldContextWrapper> fields = columns.stream().map(
			column -> getFieldsList(column.getWrappedMap(), "fields")
		).reduce(
			(fields1, fields2) -> {
				fields1.addAll(fields2);
				return fields1;
			}
		).get();

		return fields;
	}

	public LocalizedValue getTitle() {
		return getValue("localizedTitle", LocalizedValue.class);
	}

	public boolean isEnabled() {
		return getValue("enabled", Boolean.class);
	}

	public boolean isShowRequiredFieldsWarning() {
		return getValue("showRequiredFieldsWarning", Boolean.class);
	}

}