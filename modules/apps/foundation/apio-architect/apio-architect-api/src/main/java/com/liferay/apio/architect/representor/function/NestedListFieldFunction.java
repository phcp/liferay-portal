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

package com.liferay.apio.architect.representor.function;

import com.liferay.apio.architect.representor.NestedRepresentor;

import java.util.List;
import java.util.function.Function;

/**
 * Instances of this class represent a {@code Representor} nested list field.
 *
 * @author Alejandro Hernández
 * @review
 */
public class NestedListFieldFunction<T, S> {

	public NestedListFieldFunction(
		String key, Function<T, List<S>> function,
		NestedRepresentor<S> nestedRepresentor) {

		this.nestedRepresentor = nestedRepresentor;
		this.key = key;
		this.function = function;
	}

	/**
	 * The function that transforms the model into the list whose values are
	 * used in the {@code NestedRepresentor#Builder}.
	 *
	 * @review
	 */
	public final Function<T, List<S>> function;

	/**
	 * The field's key
	 *
	 * @review
	 */
	public final String key;

	/**
	 * The field's {@code NestedRepresentor}
	 */
	public final NestedRepresentor<S> nestedRepresentor;

}