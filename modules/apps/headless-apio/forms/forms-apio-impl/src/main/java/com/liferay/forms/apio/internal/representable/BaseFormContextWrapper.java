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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Paulo Cruz
 */
public class BaseFormContextWrapper {

	public BaseFormContextWrapper(Object wrappedMap) {
		_wrappedMap = (Map<String, Object>) wrappedMap;
	}

	public List<BaseFormContextWrapper> getListFromValue(String key) {
		return getListFromMap(_wrappedMap, key);
	}

	public List<BaseFormContextWrapper> getListFromMap(
		Map<String, Object> map, String key) {

		return Try.fromFallible(
			() -> (List<Object>) map.get(key)
		).map(
			List::stream
		).orElseGet(
			Stream::empty
		).map(
			object -> (Map<String, Object>) object
		).map(
			BaseFormContextWrapper::new
		).collect(Collectors.toList());
	}

	public List<FormPageContextWrapper> getPagesList(String key) {
		return Try.fromFallible(
			() -> (List<Object>) _wrappedMap.get(key)
		).map(
			List::stream
		).orElseGet(
			Stream::empty
		).map(
			object -> (Map<String, Object>) object
		).map(
			FormPageContextWrapper::new
		).collect(Collectors.toList());
	}

	public List<FormFieldContextWrapper> getFieldsList(
		Map<String, Object> map, String key) {

		return Try.fromFallible(
			() -> (List<Object>) map.get(key)
		).map(
			List::stream
		).orElseGet(
			Stream::empty
		).map(
			object -> (Map<String, Object>) object
		).map(
			FormFieldContextWrapper::new
		).collect(Collectors.toList());
	}

	public <T> T getValue(String key, Function<Object, T> parseFunction) {
		return Optional.ofNullable(
			_wrappedMap.get(key)
		).map(
			parseFunction::apply
		).orElse(
			null
		);
	}

	public <T> T getValue(String key, Class<T> type) {
		return Optional.ofNullable(
			_wrappedMap.get(key)
		).map(
			type::cast
		).orElse(
			null
		);
	}

	public <T> T getValue(String key, Class<T> type, T defaultValue) {
		return Optional.ofNullable(
			_wrappedMap.get(key)
		).map(
			type::cast
		).orElse(
			defaultValue
		);
	}

	public Map<String, Object> getWrappedMap() {
		return _wrappedMap;
	}

	private Map<String, Object> _wrappedMap;

}