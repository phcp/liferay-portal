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

package com.liferay.forms.apio.internal.architect.resource;

import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.resource.CollectionResource;
import com.liferay.apio.architect.routes.CollectionRoutes;
import com.liferay.apio.architect.routes.ItemRoutes;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.forms.apio.architect.identifier.StructureIdentifier;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;

import javax.ws.rs.ServerErrorException;

/**
 * Provides the information necessary to expose Structure resources through
 * a web API. The resources are mapped from the internal model {@code
 * DDMStructure}.
 * @author Paulo Cruz
 * @review
 */
@Component(immediate = true)
public class StructureCollectionResource implements
	CollectionResource<DDMStructure, Long, StructureIdentifier> {

	@Override
	public CollectionRoutes<DDMStructure> collectionRoutes(
		CollectionRoutes.Builder<DDMStructure> builder) {

		return builder.addGetter(
			this::_getPageItems, Company.class
		).build();
	}

	@Override
	public String getName() {
		return "structures";
	}

	@Override
	public ItemRoutes<DDMStructure, Long> itemRoutes(
		ItemRoutes.Builder<DDMStructure, Long> builder) {

		return builder.addGetter(
			this::_getStructure
		).build();
	}

	@Override
	public Representor<DDMStructure, Long> representor(
		Representor.Builder<DDMStructure, Long> builder) {

		return builder.types(
			"Structure"
		).identifier(
			DDMStructure::getStructureId
		).addLocalizedString(
			"description",
			(ddmStructure, language) -> ddmStructure.getDescription(
				language.getPreferredLocale())
		).addLocalizedString(
			"name",
			(ddmStructure, language) -> ddmStructure.getName(
				language.getPreferredLocale())
		).addString(
			"definition", DDMStructure::getDefinition
		).build();
	}

	private DDMStructure _getStructure(Long structureId) {
		try {
			return _ddmStructureLocalService.getStructure(structureId);
		}
		catch(PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	private PageItems<DDMStructure> _getPageItems(
		Pagination pagination, Company company) {

		try {
			List<Group> groups = _groupService.getGroups(company.getCompanyId(),
				GroupConstants.ANY_PARENT_GROUP_ID, false);

			long[] groupIds =
				groups.stream().mapToLong(Group::getGroupId).toArray();

			long classNameId =
				_classNameLocalService.getClassNameId(DDMFormInstance.class);

			List<DDMStructure> ddmStructures =
				_ddmStructureLocalService.getStructures(
					groupIds, classNameId, pagination.getStartPosition(),
					pagination.getEndPosition());

			int count = _ddmStructureLocalService.getStructuresCount(
				company.getGroupId());

			return new PageItems<>(ddmStructures, count);
		}
		catch(PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private GroupService _groupService;
}
