/**
 *
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
 *
 */

package com.liferay.forms.apio.internal.architect.resource;

import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.resource.CollectionResource;
import com.liferay.apio.architect.routes.CollectionRoutes;
import com.liferay.apio.architect.routes.ItemRoutes;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceService;
import com.liferay.forms.apio.architect.identifier.StructureIdentifier;
import com.liferay.forms.apio.architect.identifier.FormInstanceId;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;

import javax.ws.rs.ServerErrorException;

/**
 * Provides the information necessary to expose FormInstance resources through
 * a web API. The resources are mapped from the internal model {@code
 * DDMFormInstance}.
 * @author Victor Oliveira
 */
@Component(immediate = true)
public class FormInstanceCollectionResource
	implements CollectionResource<DDMFormInstance, Long, FormInstanceId> {

	@Override
	public CollectionRoutes<DDMFormInstance> collectionRoutes(
		CollectionRoutes.Builder<DDMFormInstance> builder) {

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

		return builder.addGetter(
			this::_getFormInstance
		).build();
	}

	@Override
	public Representor<DDMFormInstance, Long> representor(
		Representor.Builder<DDMFormInstance, Long> builder) {

		return builder.types(
			"FormInstance"
		).identifier(
			DDMFormInstance::getFormInstanceId
		).addLocalizedString(
			"description",
			(ddmFormInstance, language) -> ddmFormInstance.getDescription(
				language.getPreferredLocale())
		).addNumber(
			"companyId", DDMFormInstance::getCompanyId
		).addLinkedModel(
			"structure",
			StructureIdentifier.class,
			DDMFormInstance::getStructureId
		).build();
	}

	private DDMFormInstance _getFormInstance(Long formInstanceId) {
		try {
			return _ddmFormInstanceService.getFormInstance(formInstanceId);
		} catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	private PageItems<DDMFormInstance> _getPageItems(
		Pagination pagination, Company company) {

		try {
			List<Group> groups = _groupService.getGroups(company.getCompanyId(),
				GroupConstants.ANY_PARENT_GROUP_ID, false);

			long[] groupIds =
				groups.stream().mapToLong(Group::getGroupId).toArray();

			List<DDMFormInstance> ddmFormInstances =
				_ddmFormInstanceService.getFormInstances(
					groupIds, pagination.getStartPosition(),
					pagination.getEndPosition());

			int count = _ddmFormInstanceService.countByGroupId(groupIds);

			return new PageItems<>(ddmFormInstances, count);
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	@Reference
	private DDMFormInstanceService _ddmFormInstanceService;

	@Reference
	private GroupService _groupService;
}