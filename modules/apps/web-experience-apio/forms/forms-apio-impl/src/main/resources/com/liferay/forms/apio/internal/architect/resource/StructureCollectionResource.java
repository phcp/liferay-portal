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
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.forms.apio.architect.identifier.DDMStructureIdentifier;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;

import javax.ws.rs.ServerErrorException;

/**
 * @author Paulo Cruz
 */
@Component(immediate = true)
public class StructureCollectionResource implements
	CollectionResource<DDMStructure, Long, DDMStructureIdentifier> {

	@Override
	public CollectionRoutes<DDMStructure> collectionRoutes(
		CollectionRoutes.Builder<DDMStructure> builder) {

		return builder.addGetter(
			this::_getStructureItems, Company.class
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
			return _ddmStructureService.getStructure(structureId);
		}
		catch(PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	private PageItems<DDMStructure> _getStructureItems(
		Pagination pagination, Company company) {

		List<DDMStructure> ddmStructures = _ddmStructureService.getStructures(
			company.getCompanyId(), null, 0, 0,
			pagination.getStartPosition(), pagination.getEndPosition(), null);

		// TODO expose getStructuresCount method on DDMStructureService interface
		return new PageItems<>(ddmStructures, ddmStructures.size());
	}

	@Reference
	DDMStructureService _ddmStructureService;
}
