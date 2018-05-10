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

package com.liferay.forms.apio.internal.resource;

import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.resource.ItemResource;
import com.liferay.apio.architect.routes.ItemRoutes;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidation;
import com.liferay.dynamic.data.mapping.model.DDMFormSuccessPageSettings;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.forms.apio.architect.identifier.StructureIdentifier;
import com.liferay.forms.apio.internal.FormLayoutPage;
import com.liferay.forms.apio.internal.helper.LocalizedValueHelper;
import com.liferay.forms.apio.internal.helper.StructureResourceHelper;
import com.liferay.person.apio.identifier.PersonIdentifier;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides the information necessary to expose Structure resources through a
 * web API. The resources are mapped from the internal model {@code
 * DDMStructure}.
 *
 * @author Paulo Cruz
 */
@Component(immediate = true)
public class StructureItemResource
	implements ItemResource<DDMStructure, Long, StructureIdentifier> {

	@Override
	public String getName() {
		return "structures";
	}

	@Override
	public ItemRoutes<DDMStructure, Long> itemRoutes(
		ItemRoutes.Builder<DDMStructure, Long> builder) {

		return builder.addGetter(
			_ddmStructureLocalService::getStructure
		).build();
	}

	@Override
	public Representor<DDMStructure> representor(
		Representor.Builder<DDMStructure, Long> builder) {

		return builder.types(
			"Structure"
		).identifier(
			DDMStructure::getStructureId
		).addDate(
			"dateCreated", DDMStructure::getCreateDate
		).addDate(
			"dateModified", DDMStructure::getCreateDate
		).addDate(
			"datePublished", DDMStructure::getLastPublishDate
		).addLinkedModel(
			"author", PersonIdentifier.class, DDMStructure::getUserId
		).addLocalizedStringByLocale(
			"description", DDMStructure::getDescription
		).addLocalizedStringByLocale(
			"name", DDMStructure::getName
		).addNested(
			"successPage", StructureResourceHelper::getSuccessPage,
			nestedBuilder -> nestedBuilder.types(
				"FormSuccessPageSettings"
			).addBoolean(
				"isEnabled", DDMFormSuccessPageSettings::isEnabled
			).addLocalizedStringByLocale(
				"headline",
				(settings, locale) -> LocalizedValueHelper.getLocalizedString(
					settings.getTitle(), locale)
			).addLocalizedStringByLocale(
				"text",
				(settings, locale) -> LocalizedValueHelper.getLocalizedString(
					settings.getBody(), locale)
			).build()
		).addNested(
			"version", StructureResourceHelper::getVersion,
			nestedBuilder -> nestedBuilder.types(
				"StructureVersion"
			).addLinkedModel(
				"author", PersonIdentifier.class, DDMStructureVersion::getUserId
			).addString(
				"name", DDMStructureVersion::getVersion
			).build()
		).addNestedList(
			"pages", StructureResourceHelper::getPages,
			pagesBuilder -> pagesBuilder.types(
				"FormLayoutPage"
			).addLocalizedStringByLocale(
				"headline", FormLayoutPage::getTitle
			).addLocalizedStringByLocale(
				"text", FormLayoutPage::getDescription
			).addNestedList(
				"fields", FormLayoutPage::getFields,
				fieldsBuilder -> fieldsBuilder.types(
					"FormField"
				).addBoolean(
					"isAutocomplete", DDMFormField::isLocalizable
				).addBoolean(
					"isInline",
					ddmFormField -> StructureResourceHelper.getFieldProperty(
						Boolean::getBoolean, ddmFormField.getProperty("inline"))
				).addBoolean(
					"isLocalizable", DDMFormField::isLocalizable
				).addBoolean(
					"isMultiple", DDMFormField::isMultiple
				).addBoolean(
					"isReadOnly", DDMFormField::isReadOnly
				).addBoolean(
					"isRepeatable", DDMFormField::isRepeatable
				).addBoolean(
					"isRequired", DDMFormField::isRequired
				).addBoolean(
					"isShowAsSwitcher",
					ddmFormField -> StructureResourceHelper.getFieldProperty(
						Boolean::getBoolean,
						ddmFormField.getProperty("showAsSwitcher"))
				).addBoolean(
					"isShowLabel", DDMFormField::isShowLabel
				).addBoolean(
					"isTransient", DDMFormField::isTransient
				).addLocalizedStringByLocale(
					"label",
					(ddmFormField, locale) ->
						LocalizedValueHelper.getLocalizedString(
							ddmFormField.getLabel(), locale)
				).addLocalizedStringByLocale(
					"predefinedValue",
					(ddmFormField, locale) ->
						LocalizedValueHelper.getLocalizedString(
							ddmFormField.getPredefinedValue(), locale)
				).addLocalizedStringByLocale(
					"style",
					(ddmFormField, locale) ->
						LocalizedValueHelper.getLocalizedString(
							ddmFormField.getStyle(), locale)
				).addLocalizedStringByLocale(
					"tip",
					(ddmFormField, locale) ->
						LocalizedValueHelper.getLocalizedString(
							ddmFormField.getTip(), locale)
				).addNested(
					"grid", DDMFormField::getProperties,
					gridBuilder -> gridBuilder.types(
						"FormFieldProperties"
					).addNestedList(
						"columns",
						properties -> StructureResourceHelper.getFieldOptions(
							properties, "columns"),
						optionsBuilder -> optionsBuilder.types(
							"FormFieldOptions"
						).addLocalizedStringByLocale(
							"label",
							(options, locale) ->
								LocalizedValueHelper.getLocalizedString(
									options.getValue(), locale)
						).addString(
							"value", Map.Entry::getKey
						).build()
					).addNestedList(
						"rows",
						properties -> StructureResourceHelper.getFieldOptions(
							properties, "rows"),
						optionsBuilder -> optionsBuilder.types(
							"FormFieldOptions"
						).addLocalizedStringByLocale(
							"label",
							(options, locale) ->
								LocalizedValueHelper.getLocalizedString(
									options.getValue(), locale)
						).addString(
							"value", Map.Entry::getKey
						).build()
					).build()
				).addNested(
					"validation", StructureResourceHelper::getFieldValidation,
					mapBuilder -> mapBuilder.types(
						"FormFieldProperties"
					).addString(
						"error", DDMFormFieldValidation::getErrorMessage
					).addString(
						"expression", DDMFormFieldValidation::getExpression
					).build()
				).addNestedList(
					"options", StructureResourceHelper::getFieldOptions,
					optionsBuilder -> optionsBuilder.types(
						"FormFieldOptions"
					).addLocalizedStringByLocale(
						"label",
						(options, locale) ->
							LocalizedValueHelper.getLocalizedString(
								options.getValue(), locale)
					).addString(
						"value", Map.Entry::getKey
					).build()
				).addString(
					"additionalType", DDMFormField::getType
				).addString(
					"dataSourceType",
					ddmFormField -> StructureResourceHelper.getFieldProperty(
						Object::toString,
						ddmFormField.getProperty("dataSourceType"))
				).addString(
					"dataType", DDMFormField::getDataType
				).addString(
					"displayStyle",
					ddmFormField -> StructureResourceHelper.getFieldProperty(
						Object::toString,
						ddmFormField.getProperty("displayStyle"))
				).addString(
					"indexType", DDMFormField::getIndexType
				).addString(
					"name", DDMFormField::getName
				).addString(
					"placeholder",
					ddmFormField -> StructureResourceHelper.getFieldProperty(
						Object::toString,
						ddmFormField.getProperty("placeholder"))
				).addString(
					"text",
					ddmFormField -> StructureResourceHelper.getFieldProperty(
						Object::toString, ddmFormField.getProperty("text"))
				).build()
			).build()
		).addNumber(
			"additionalType", DDMStructure::getType
		).addString(
			"definition", DDMStructure::getDefinition
		).build();
	}

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

}