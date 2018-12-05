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

package com.liferay.forms.apio.internal.helper;

import com.liferay.apio.architect.file.BinaryFile;
import com.liferay.apio.architect.functional.Try;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.forms.apio.internal.architect.form.MediaObjectCreatorForm;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;

import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Javier Gamarra
 * @author Paulo Cruz
 */
@Component(immediate = true, service = UploadFileHelper.class)
public class UploadFileHelper {

	public FileEntry uploadFile(
		DDMFormInstance ddmFormInstance,
		MediaObjectCreatorForm mediaObjectCreatorForm) {

		Optional<Long> folderIdOptional =
			mediaObjectCreatorForm.getFolderIdOptional();

		long folderId = folderIdOptional.orElse(0L);

		BinaryFile binaryFile = mediaObjectCreatorForm.getBinaryFile();

		return Try.fromFallible(
			ddmFormInstance::getGroupId
		).map(
			repositoryId -> _dlAppService.addFileEntry(
				repositoryId, folderId, mediaObjectCreatorForm.getName(),
				binaryFile.getMimeType(), mediaObjectCreatorForm.getTitle(),
				mediaObjectCreatorForm.getDescription(), null,
				binaryFile.getInputStream(), binaryFile.getSize(),
				new ServiceContext())
		).orElse(
			null
		);
	}

	@Reference
	private DLAppService _dlAppService;

}