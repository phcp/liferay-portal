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

import com.liferay.apio.architect.customactions.PostRoute;
import com.liferay.apio.architect.file.BinaryFile;
import com.liferay.apio.architect.functional.Try;
import com.liferay.apio.architect.language.Language;
import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.representor.NestedRepresentor;
import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.resource.NestedCollectionResource;
import com.liferay.apio.architect.routes.ItemRoutes;
import com.liferay.apio.architect.routes.NestedCollectionRoutes;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormTemplateContextFactory;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceSettings;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceVersion;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceService;
import com.liferay.forms.apio.architect.identifier.FormInstanceIdentifier;
import com.liferay.forms.apio.architect.identifier.StructureIdentifier;
import com.liferay.forms.apio.internal.form.FormContextForm;
import com.liferay.forms.apio.internal.form.MediaObjectCreatorForm;
import com.liferay.forms.apio.internal.representable.EvaluateContextRoute;
import com.liferay.forms.apio.internal.representable.FormContextIdentifier;
import com.liferay.forms.apio.internal.representable.FormContextWrapper;
import com.liferay.forms.apio.internal.util.FormInstanceRepresentorUtil;
import com.liferay.forms.apio.internal.util.FormValuesUtil;
import com.liferay.media.object.apio.identifier.FileEntryIdentifier;
import com.liferay.person.apio.identifier.PersonIdentifier;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.site.apio.identifier.WebSiteIdentifier;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

/**
 * Provides the information necessary to expose FormInstance resources through a
 * web API. The resources are mapped from the internal model {@code
 * DDMFormInstance}.
 *
 * @author Victor Oliveira
 */
@Component(immediate = true)
public class FormInstanceNestedCollectionResource
	implements NestedCollectionResource<DDMFormInstance, Long,
	FormInstanceIdentifier, Long, WebSiteIdentifier> {

	@Override
	public NestedCollectionRoutes<DDMFormInstance, Long> collectionRoutes(
		NestedCollectionRoutes.Builder<DDMFormInstance, Long> builder) {

		return builder.addGetter(
			this::_getPageItems, Company.class
		).build();
	}

	@Override
	public String getName() {
		return "form-instances";
	}

	@Override
	public ItemRoutes<DDMFormInstance, Long> itemRoutes(
		ItemRoutes.Builder<DDMFormInstance, Long> builder) {

		PostRoute evaluateContextRoute = new EvaluateContextRoute();

		PostRoute uploadFileRoute = new UploadFileRoute();

		return builder.addGetter(
			_ddmFormInstanceService::getFormInstance
		).addCustomRoute(evaluateContextRoute,
			this::_evaluateContext, FormContextIdentifier.class,
			(credentials, aLong) -> true, FormContextForm::buildForm,
			DDMFormRenderingContext.class, Language.class
		).addCustomRoute(uploadFileRoute,
			this::uploadFile, FileEntryIdentifier.class,
			(credentials, aLong) -> true,
			MediaObjectCreatorForm::buildForm
		).build();
	}

	private FileEntry uploadFile(Long ddmFormInstanceId, MediaObjectCreatorForm mediaObjectCreatorForm)
		throws PortalException {

		Long folderId = 0L;

		ServiceContext serviceContext = new ServiceContext();
		BinaryFile binaryFile = mediaObjectCreatorForm.getBinaryFile();

		String sourceFileName = mediaObjectCreatorForm.getName();

		String title = mediaObjectCreatorForm.getTitle();

		String mimeType = binaryFile.getMimeType();

		String description = mediaObjectCreatorForm.getDescription();

		String changelog = mediaObjectCreatorForm.getChangelog();

		InputStream inputStream = binaryFile.getInputStream();

		long size = binaryFile.getSize();

		Folder folder = _dlAppService.getFolder(folderId);

		long repositoryId = folder.getRepositoryId();

		return _dlAppService.addFileEntry(
			repositoryId, folderId, sourceFileName, mimeType, title,
			description, changelog, inputStream, size, serviceContext);
	}

	@Override
	public Representor<DDMFormInstance> representor(
		Representor.Builder<DDMFormInstance, Long> builder) {

		return builder.types(
			"FormInstance"
		).identifier(
			DDMFormInstance::getFormInstanceId
		).addBidirectionalModel(
			"interactionService", "formInstances", WebSiteIdentifier.class,
			DDMFormInstance::getGroupId
		).addDate(
			"dateCreated", DDMFormInstance::getCreateDate
		).addDate(
			"dateModified", DDMFormInstance::getModifiedDate
		).addDate(
			"datePublished", DDMFormInstance::getLastPublishDate
		).addLinkedModel(
			"author", PersonIdentifier.class, DDMFormInstance::getUserId
		).addLinkedModel(
			"structure", StructureIdentifier.class,
			DDMFormInstance::getStructureId
		).addNested(
			"settings", FormInstanceRepresentorUtil::getSettings,
			FormInstanceNestedCollectionResource::_buildSettings
		).addNested(
			"version", FormInstanceRepresentorUtil::getVersion,
			nestedBuilder -> nestedBuilder.types(
				"FormInstanceVersion"
			).addLinkedModel(
				"author", PersonIdentifier.class,
				DDMFormInstanceVersion::getUserId
			).addString(
				"name", DDMFormInstanceVersion::getVersion
			).build()
		).addLocalizedStringByLocale(
			"description", DDMFormInstance::getDescription
		).addLocalizedStringByLocale(
			"name", DDMFormInstance::getName
		).addString(
			"defaultLanguage", DDMFormInstance::getDefaultLanguageId
		).addStringList(
			"availableLanguages",
			FormInstanceRepresentorUtil::getAvailableLanguages
		).build();
	}

	private static NestedRepresentor<DDMFormInstanceSettings> _buildSettings(
		NestedRepresentor.Builder<DDMFormInstanceSettings> builder) {

		return builder.types(
			"FormInstanceSettings"
		).addBoolean(
			"isPublished", DDMFormInstanceSettings::published
		).addBoolean(
			"isRequireAuthentication",
			DDMFormInstanceSettings::requireAuthentication
		).addBoolean(
			"isRequireCaptcha", DDMFormInstanceSettings::requireCaptcha
		).addNested(
			"emailNotification", settings -> settings,
			emailSettingsBuilder -> emailSettingsBuilder.types(
				"EmailMessage"
			).addBoolean(
				"isEnabled", DDMFormInstanceSettings::sendEmailNotification
			).addNested(
				"sender", settings -> settings,
				senderBuilder -> senderBuilder.types(
					"ContactPoint"
				).addString(
					"email", DDMFormInstanceSettings::emailFromAddress
				).addString(
					"name", DDMFormInstanceSettings::emailFromName
				).build()
			).addNested(
				"toRecipient", settings -> settings,
				toRecipientBuilder -> toRecipientBuilder.types(
					"ContactPoint"
				).addString(
					"email", DDMFormInstanceSettings::emailToAddress
				).build()
			).addString(
				"about", DDMFormInstanceSettings::emailSubject
			).build()
		).addString(
			"redirectURL", DDMFormInstanceSettings::redirectURL
		).addString(
			"storageType", DDMFormInstanceSettings::storageType
		).addString(
			"workflowDefinition", DDMFormInstanceSettings::workflowDefinition
		).build();
	}

	private FormContextWrapper _evaluateContext(
		Long ddmFormInstanceId, FormContextForm formContextForm,
		DDMFormRenderingContext ddmFormRenderingContext, Language language)
		throws PortalException {

		Locale locale = language.getPreferredLocale();

		LocaleThreadLocal.setThemeDisplayLocale(locale);

		DDMFormInstance ddmFormInstance =
			_ddmFormInstanceService.getFormInstance(ddmFormInstanceId);

		String fieldValues = formContextForm.getFieldValues();
		ddmFormRenderingContext.setLocale(locale);

		DDMForm ddmForm = Try.fromFallible(
			() -> ddmFormInstance.getStructure()
		).map(
			DDMStructure::getDDMForm
		).orElse(
			null
		);

		Try.fromFallible(
			() -> FormValuesUtil.getDDMFormValues(
				fieldValues, ddmForm, locale)
		).ifSuccess(
			ddmFormRenderingContext::setDDMFormValues
		);

		return _getEvaluationResult(ddmForm, ddmFormRenderingContext);
	}

	private FormContextWrapper _getEvaluationResult(
		DDMForm ddmForm, DDMFormRenderingContext ddmFormRenderingContext) {

		return Try.fromFallible(
			() -> _ddmFormTemplateContextFactory.create(
				ddmForm, ddmFormRenderingContext)
		).map(
			FormContextWrapper::new
		).orElse(
			null
		);
	}

	private PageItems<DDMFormInstance> _getPageItems(
		Pagination pagination, Long groupId, Company company) {

		List<DDMFormInstance> ddmFormInstances =
			_ddmFormInstanceService.getFormInstances(
				company.getCompanyId(), groupId, pagination.getStartPosition(),
				pagination.getEndPosition());

		int count = _ddmFormInstanceService.getFormInstancesCount(
			company.getCompanyId(), groupId);

		return new PageItems<>(ddmFormInstances, count);
	}

	@Reference
	private DDMFormInstanceService _ddmFormInstanceService;

	@Reference
	private DDMFormTemplateContextFactory _ddmFormTemplateContextFactory;

	@Reference
	private DLAppService _dlAppService;

}