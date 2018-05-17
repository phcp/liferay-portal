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

import com.liferay.apio.architect.function.throwable.ThrowableFunction;
import com.liferay.apio.architect.functional.Try;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormTemplateContextFactory;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.forms.apio.internal.representable.FormContextWrapper;
import com.liferay.portal.kernel.util.LocaleThreadLocal;

import java.util.Locale;

/**
 * @author Paulo Cruz
 */
public final class EvaluateContextUtil {

	public EvaluateContextUtil(
		DDMFormTemplateContextFactory ddmFormTemplateContextFactory) {

		_ddmFormTemplateContextFactory = ddmFormTemplateContextFactory;
	}

	public ThrowableFunction<DDMForm, FormContextWrapper> evaluateContext(
		String fieldValues, DDMFormRenderingContext ddmFormRenderingContext,
		Locale locale) {

		_setEvaluatorLocale(ddmFormRenderingContext, locale);

		return ddmForm -> Try.fromFallible(
			() -> FormValuesUtil.getDDMFormValues(fieldValues, ddmForm, locale)
		).map(
			ddmFormValues -> _getEvaluationResult(
				ddmForm, ddmFormValues, ddmFormRenderingContext)
		).orElse(
			null
		);
	}

	private FormContextWrapper _getEvaluationResult(
		DDMForm ddmForm, DDMFormValues ddmFormValues,
		DDMFormRenderingContext ddmFormRenderingContext) {

		ddmFormRenderingContext.setDDMFormValues(ddmFormValues);

		return Try.fromFallible(
			() -> _ddmFormTemplateContextFactory.create(
				ddmForm, ddmFormRenderingContext)
		).map(
			FormContextWrapper::new
		).orElse(
			null
		);
	}

	private void _setEvaluatorLocale(
		DDMFormRenderingContext ddmFormRenderingContext, Locale locale) {

		LocaleThreadLocal.setThemeDisplayLocale(locale);
		ddmFormRenderingContext.setLocale(locale);
	}

	private final DDMFormTemplateContextFactory _ddmFormTemplateContextFactory;

}