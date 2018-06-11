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

import static com.liferay.forms.apio.internal.util.FormInstanceRecordResourceUtil.setServiceContextAttributes;
import static com.liferay.forms.apio.internal.util.FormValuesUtil.getDDMFormValues;
import static com.liferay.forms.apio.internal.util.LocalizedValueUtil.getLocalizedString;

import com.google.gson.Gson;

import com.liferay.apio.architect.functional.Try;
import com.liferay.apio.architect.language.Language;
import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.resource.NestedCollectionResource;
import com.liferay.apio.architect.routes.ItemRoutes;
import com.liferay.apio.architect.routes.NestedCollectionRoutes;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersionModel;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.forms.apio.architect.identifier.FormInstanceIdentifier;
import com.liferay.forms.apio.architect.identifier.FormInstanceRecordIdentifier;
import com.liferay.forms.apio.internal.FileEntryValue;
import com.liferay.forms.apio.internal.form.FormInstanceRecordForm;
import com.liferay.forms.apio.internal.provider.ServiceContextWrapper;
import com.liferay.forms.apio.internal.util.FormInstanceRecordResourceUtil;
import com.liferay.person.apio.identifier.PersonIdentifier;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides the information necessary to expose FormInstanceRecord resources
 * through a web API. The resources are mapped from the internal model {@code
 * DDMFormInstanceRecord}.
 *
 * @author Paulo Cruz
 */
@Component(immediate = true)
public class FormInstanceRecordNestedCollectionResource
	implements NestedCollectionResource<DDMFormInstanceRecord, Long,
		FormInstanceRecordIdentifier, Long, FormInstanceIdentifier> {

	@Override
	public NestedCollectionRoutes<DDMFormInstanceRecord, Long> collectionRoutes(
		NestedCollectionRoutes.Builder<DDMFormInstanceRecord, Long> builder) {

		return builder.addGetter(
			this::_getPageItems
		).addCreator(
			this::_addFormInstanceRecord, Language.class,
			ServiceContextWrapper.class, (credentials, aLong) -> true,
			FormInstanceRecordForm::buildForm
		).build();
	}

	@Override
	public String getName() {
		return "form-instance-records";
	}

	@Override
	public ItemRoutes<DDMFormInstanceRecord, Long> itemRoutes(
		ItemRoutes.Builder<DDMFormInstanceRecord, Long> builder) {

		return builder.addGetter(
			_ddmFormInstanceRecordService::getFormInstanceRecord
		).addUpdater(
			this::_updateFormInstanceRecord, Language.class,
			ServiceContextWrapper.class,
			(credentials, aLong) -> true, FormInstanceRecordForm::buildForm
		).build();
	}

	@Override
	public Representor<DDMFormInstanceRecord> representor(
		Representor.Builder<DDMFormInstanceRecord, Long> builder) {

		return builder.types(
			"FormInstanceRecord"
		).identifier(
			DDMFormInstanceRecord::getFormInstanceRecordId
		).addBidirectionalModel(
			"formInstance", "formInstanceRecords", FormInstanceIdentifier.class,
			DDMFormInstanceRecord::getFormInstanceId
		).addDate(
			"dateCreated", DDMFormInstanceRecord::getCreateDate
		).addDate(
			"dateModified", DDMFormInstanceRecord::getModifiedDate
		).addDate(
			"datePublished", DDMFormInstanceRecord::getLastPublishDate
		).addLinkedModel(
			"author", PersonIdentifier.class, DDMFormInstanceRecord::getUserId
		).addNested(
			"version", FormInstanceRecordResourceUtil::getVersion,
			versionBuilder -> versionBuilder.types(
				"FormInstanceRecordVersion"
			).addLinkedModel(
				"author", PersonIdentifier.class,
				DDMFormInstanceRecordVersion::getUserId
			).addString(
				"name", DDMFormInstanceRecordVersionModel::getVersion
			).build()
		).addNestedList(
			"fieldValues", this::_getFieldValues,
			fieldValuesBuilder -> fieldValuesBuilder.types(
				"FormFieldValue"
			).addLocalizedStringByLocale(
				"value", getLocalizedString(DDMFormFieldValue::getValue)
			).addString(
				"name", DDMFormFieldValue::getName
			).build()
		).build();
	}

	private DDMFormInstanceRecord _addFormInstanceRecord(
			Long ddmFormInstanceId,
			FormInstanceRecordForm formInstanceRecordForm, Language language,
			ServiceContextWrapper serviceContextWrapper)
		throws PortalException {

		DDMFormInstance ddmFormInstance =
			_ddmFormInstanceService.getFormInstance(ddmFormInstanceId);

		DDMStructure ddmStructure = ddmFormInstance.getStructure();

		DDMFormValues ddmFormValues = getDDMFormValues(
			formInstanceRecordForm.getFieldValues(), ddmStructure.getDDMForm(),
			language.getPreferredLocale());

		setServiceContextAttributes(
			serviceContextWrapper, formInstanceRecordForm.isDraft());

		long groupId = ddmFormInstance.getGroupId();
		long formInstanceId = ddmFormInstance.getFormInstanceId();

		ServiceContext serviceContext =
			serviceContextWrapper.getServiceContext();

		DDMForm ddmForm = ddmStructure.getDDMForm();

		List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();

		_linkFiles(ddmFormFields, ddmFormValues.getDDMFormFieldValues());

		return _ddmFormInstanceRecordService.addFormInstanceRecord(
			groupId, formInstanceId, ddmFormValues, serviceContext);
	}

	private Long _extractFileEntryId(DDMFormFieldValue ddmFormFieldValue) {
		return Try.fromFallible(
			() -> ddmFormFieldValue.getValue()
		).map(
			Value::getValues
		).map(
			Map::values
		).map(
			Collection::stream
		).mapOptional(
			Stream::findFirst
		).map(
			fileEntryUrl -> fileEntryUrl.substring(
				fileEntryUrl.lastIndexOf("/") + 1)
		).map(
			Long::valueOf
		).orElse(
			null
		);
	}

	private Optional<DDMFormFieldValue> _findField(
		DDMFormField formField, List<DDMFormFieldValue> ddmFormFieldValues) {

		Stream<DDMFormFieldValue> ddmFormFieldValuesStream =
			ddmFormFieldValues.stream();

		return ddmFormFieldValuesStream.filter(
			value -> value.getName().equals(formField.getName())
		).findFirst();
	}

	private List<DDMFormFieldValue> _getFieldValues(
		DDMFormInstanceRecord ddmFormInstanceRecord) {

		return Try.fromFallible(
			ddmFormInstanceRecord::getDDMFormValues
		).map(
			DDMFormValues::getDDMFormFieldValues
		).orElse(
			null
		);
	}

	private PageItems<DDMFormInstanceRecord> _getPageItems(
			Pagination pagination, Long formInstanceId)
		throws PortalException {

		List<DDMFormInstanceRecord> ddmFormInstanceRecords =
			_ddmFormInstanceRecordService.getFormInstanceRecords(
				formInstanceId, WorkflowConstants.STATUS_ANY,
				pagination.getStartPosition(), pagination.getEndPosition(),
				null);

		int count = _ddmFormInstanceRecordService.getFormInstanceRecordsCount(
			formInstanceId);

		return new PageItems<>(ddmFormInstanceRecords, count);
	}

	private void _linkFiles(
		List<DDMFormField> ddmFormFields,
		List<DDMFormFieldValue> ddmFormFieldValues) {

		Stream<DDMFormField> ddmFormFieldsStream = ddmFormFields.stream();

		ddmFormFieldsStream.filter(
			formField -> formField.getType().equals("document_library")
		).map(
			field -> _findField(field, ddmFormFieldValues)
		).forEach(
			optional -> optional.ifPresent(this::_setFileEntryAsFormFieldValue)
		);
	}

	private void _setFileEntryAsFormFieldValue(
		DDMFormFieldValue ddmFormFieldValue) {

		Gson gson = new Gson();

		Try.fromFallible(
			() -> _extractFileEntryId(ddmFormFieldValue)
		).map(
			_dlAppService::getFileEntry
		).map(
			fileEntry -> new FileEntryValue(
				fileEntry.getGroupId(), fileEntry.getUuid())
		).map(
			gson::toJson
		).map(
			UnlocalizedValue::new
		).ifSuccess(
			ddmFormFieldValue::setValue
		);
	}

	private DDMFormInstanceRecord _updateFormInstanceRecord(
			Long formInstanceRecordId,
			FormInstanceRecordForm formInstanceRecordForm, Language language,
			ServiceContextWrapper serviceContextWrapper)
		throws PortalException {

		DDMFormInstanceRecord ddmFormInstanceRecord =
			_ddmFormInstanceRecordService.getFormInstanceRecord(
				formInstanceRecordId);

		DDMFormInstance ddmFormInstance =
			ddmFormInstanceRecord.getFormInstance();

		DDMStructure ddmStructure = ddmFormInstance.getStructure();

		DDMFormValues ddmFormValues = getDDMFormValues(
			formInstanceRecordForm.getFieldValues(), ddmStructure.getDDMForm(),
			language.getPreferredLocale());

		ServiceContext serviceContext =
			serviceContextWrapper.getServiceContext();

		setServiceContextAttributes(
			serviceContextWrapper, formInstanceRecordForm.isDraft());

		return _ddmFormInstanceRecordService.updateFormInstanceRecord(
			formInstanceRecordId, false, ddmFormValues, serviceContext);
	}

	@Reference
	private DDMFormInstanceRecordService _ddmFormInstanceRecordService;

	@Reference
	private DDMFormInstanceService _ddmFormInstanceService;

	@Reference
	private DLAppService _dlAppService;

}