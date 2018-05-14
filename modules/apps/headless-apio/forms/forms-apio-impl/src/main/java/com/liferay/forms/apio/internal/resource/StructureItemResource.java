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

import com.liferay.apio.architect.representor.NestedRepresentor;
import com.liferay.apio.architect.representor.NestedRepresentor.Builder;
import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.resource.ItemResource;
import com.liferay.apio.architect.routes.ItemRoutes;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidation;
import com.liferay.dynamic.data.mapping.model.DDMFormSuccessPageSettings;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.forms.apio.architect.identifier.StructureIdentifier;
import com.liferay.forms.apio.internal.FormLayoutPage;
import com.liferay.forms.apio.internal.util.LocalizedValueUtil;
import com.liferay.forms.apio.internal.util.StructureRepresentorUtil;
import com.liferay.person.apio.identifier.PersonIdentifier;

import java.util.Map.Entry;

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
			"successPage", StructureRepresentorUtil::getSuccessPage,
			StructureItemResource::_buildSuccessPageSettings
		).addNested(
			"version", StructureRepresentorUtil::getVersion,
			StructureItemResource::_buildVersion
		).addNestedList(
			"pages", StructureRepresentorUtil::getPages,
			StructureItemResource::_buildFormPages
		).build();
	}

	private static NestedRepresentor<Entry<String, LocalizedValue>>
		_buildFieldOptions(Builder<Entry<String, LocalizedValue>> builder) {

		return builder.types(
			"FormFieldOptions"
		).addLocalizedStringByLocale(
			"label", LocalizedValueUtil.getLocalizedValue(Entry::getValue)
		).addString(
			"value", Entry::getKey
		).build();
	}

	private static NestedRepresentor<DDMFormField> _buildFormField(
		Builder<DDMFormField> builder) {

		return builder.types(
			"FormField"
		).addBoolean(
			"isAutocomplete", DDMFormField::isLocalizable
		).addBoolean(
			"isInline",
			StructureRepresentorUtil.getFieldProperty(
				Boolean.class::cast, "inline")
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
			StructureRepresentorUtil.getFieldProperty(
				Boolean.class::cast, "showAsSwitcher")
		).addBoolean(
			"isShowLabel", DDMFormField::isShowLabel
		).addBoolean(
			"isTransient", DDMFormField::isTransient
		).addLocalizedStringByLocale(
			"label",
			LocalizedValueUtil.getLocalizedValue(DDMFormField::getLabel)
		).addLocalizedStringByLocale(
			"predefinedValue",
			LocalizedValueUtil.getLocalizedValue(
				DDMFormField::getPredefinedValue)
		).addLocalizedStringByLocale(
			"style",
			LocalizedValueUtil.getLocalizedValue(DDMFormField::getStyle)
		).addLocalizedStringByLocale(
			"tip", LocalizedValueUtil.getLocalizedValue(DDMFormField::getTip)
		).addNested(
			"grid", ddmFormField -> ddmFormField,
			StructureItemResource::_buildGridProperties
		).addNested(
			"validation", DDMFormField::getDDMFormFieldValidation,
			StructureItemResource::_buildValidationProperties
		).addNestedList(
			"options",
			StructureRepresentorUtil.getFieldOptions(
				DDMFormField::getDDMFormFieldOptions),
			StructureItemResource::_buildFieldOptions
		).addString(
			"additionalType", DDMFormField::getType
		).addString(
			"dataSourceType",
			StructureRepresentorUtil.getFieldProperty(
				String.class::cast, "dataSourceType")
		).addString(
			"dataType", DDMFormField::getDataType
		).addString(
			"displayStyle",
			StructureRepresentorUtil.getFieldProperty(
				String.class::cast, "displayStyle")
		).addString(
			"indexType", DDMFormField::getIndexType
		).addString(
			"name", DDMFormField::getName
		).addLocalizedStringByLocale(
			"placeholder",
			StructureRepresentorUtil.getLocalizedValue("placeholder")
		).addLocalizedStringByLocale(
			"text", StructureRepresentorUtil.getLocalizedValue("text")
		).build();
	}

	private static NestedRepresentor<FormLayoutPage> _buildFormPages(
		Builder<FormLayoutPage> pagesBuilder) {

		return pagesBuilder.types(
			"FormLayoutPage"
		).addLocalizedStringByLocale(
			"headline", FormLayoutPage::getTitle
		).addLocalizedStringByLocale(
			"text", FormLayoutPage::getDescription
		).addNestedList(
			"fields", FormLayoutPage::getFields,
			StructureItemResource::_buildFormField
		).build();
	}

	private static NestedRepresentor<DDMFormField> _buildGridProperties(
		Builder<DDMFormField> builder) {

		return builder.types(
			"FormFieldProperties"
		).addNestedList(
			"columns", StructureRepresentorUtil.getFieldOptions("columns"),
			StructureItemResource::_buildFieldOptions
		).addNestedList(
			"rows", StructureRepresentorUtil.getFieldOptions("rows"),
			StructureItemResource::_buildFieldOptions
		).build();
	}

	private static NestedRepresentor<DDMFormSuccessPageSettings>
		_buildSuccessPageSettings(Builder<DDMFormSuccessPageSettings> builder) {

		return builder.types(
			"FormSuccessPageSettings"
		).addBoolean(
			"isEnabled", DDMFormSuccessPageSettings::isEnabled
		).addLocalizedStringByLocale(
			"headline",
			LocalizedValueUtil.getLocalizedValue(
				DDMFormSuccessPageSettings::getTitle)
		).addLocalizedStringByLocale(
			"text",
			LocalizedValueUtil.getLocalizedValue(
				DDMFormSuccessPageSettings::getBody)
		).build();
	}

	private static NestedRepresentor<DDMFormFieldValidation>
		_buildValidationProperties(Builder<DDMFormFieldValidation> builder) {

		return builder.types(
			"FormFieldProperties"
		).addString(
			"error", DDMFormFieldValidation::getErrorMessage
		).addString(
			"expression", DDMFormFieldValidation::getExpression
		).build();
	}

	private static NestedRepresentor<DDMStructureVersion> _buildVersion(
		Builder<DDMStructureVersion> nestedBuilder) {

		return nestedBuilder.types(
			"StructureVersion"
		).addLinkedModel(
			"author", PersonIdentifier.class, DDMStructureVersion::getUserId
		).addString(
			"name", DDMStructureVersion::getVersion
		).build();
	}

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

}