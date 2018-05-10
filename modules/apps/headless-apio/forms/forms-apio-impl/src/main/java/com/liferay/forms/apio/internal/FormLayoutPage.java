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

package com.liferay.forms.apio.internal;

import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;

import java.util.List;
import java.util.Locale;

/**
 * @author Paulo Cruz
 */
public class FormLayoutPage {

	public FormLayoutPage(
		LocalizedValue description, List<DDMFormField> fields,
		LocalizedValue title) {

		_description = description;
		_fields = fields;
		_title = title;
	}

	public LocalizedValue getDescription() {
		return _description;
	}

	public String getDescription(Locale locale) {
		return _description.getString(locale);
	}

	public List<DDMFormField> getFields() {
		return _fields;
	}

	public LocalizedValue getTitle() {
		return _title;
	}

	public String getTitle(Locale locale) {
		return _title.getString(locale);
	}

	public void setDescription(LocalizedValue description) {
		_description = description;
	}

	public void setFields(List<DDMFormField> fields) {
		_fields = fields;
	}

	public void setTitle(LocalizedValue title) {
		_title = title;
	}

	private LocalizedValue _description;
	private List<DDMFormField> _fields;
	private LocalizedValue _title;

}