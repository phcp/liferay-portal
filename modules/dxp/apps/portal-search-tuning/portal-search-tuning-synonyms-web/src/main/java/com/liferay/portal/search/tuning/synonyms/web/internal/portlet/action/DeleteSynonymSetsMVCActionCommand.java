/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.tuning.synonyms.web.internal.constants.SynonymsPortletKeys;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSet;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetIndexReader;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetIndexWriter;
import com.liferay.portal.search.tuning.synonyms.web.internal.synonym.SynonymIndexer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Filipe Oshiro
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + SynonymsPortletKeys.SYNONYMS,
		"mvc.command.name=deleteSynonymSet"
	},
	service = MVCActionCommand.class
)
public class DeleteSynonymSetsMVCActionCommand extends BaseMVCActionCommand {

	protected void deleteSynonymSetsFromIndex(
		List<SynonymSet> deletedSynonymSets, String indexName) {

		for (SynonymSet synonymSet : deletedSynonymSets) {
			_synonymSetIndexWriter.remove(synonymSet.getId());
		}
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long companyId = portal.getCompanyId(actionRequest);

		String[] synonymSetIds = ParamUtil.getStringValues(
			actionRequest, "rowIds");

		List<SynonymSet> deletedSynonymSets = getDeletedSynonymSets(
			synonymSetIds);

		List<String> deletedSynonyms = getDeletedSynonymsArray(
			deletedSynonymSets);

		for (String filterName : _FILTER_NAMES) {
			String[] synonymSets = _synonymIndexer.getSynonymSets(
				companyId, filterName);

			for (String synonymToBeDeleted : deletedSynonyms) {
				synonymSets = _removeSynonym(synonymSets, synonymToBeDeleted);
			}

			_synonymIndexer.updateSynonymSets(
				companyId, filterName, synonymSets);
		}

		deleteSynonymSetsFromIndex(
			deletedSynonymSets, _indexNameBuilder.getIndexName(companyId));

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		sendRedirect(actionRequest, actionResponse, redirect);
	}

	protected List<String> getDeletedSynonymsArray(
		List<SynonymSet> deletedSynonymSets) {

		Stream<SynonymSet> stream = deletedSynonymSets.stream();

		return stream.map(
			SynonymSet::getSynonyms
		).collect(
			Collectors.toList()
		);
	}

	protected List<SynonymSet> getDeletedSynonymSets(String[] synonymSetIds) {
		Stream<String> stream = Arrays.stream(synonymSetIds);

		return stream.map(
			_synonymSetIndexReader::fetchOptional
		).filter(
			Optional::isPresent
		).map(
			Optional::get
		).collect(
			Collectors.toList()
		);
	}

	@Reference
	protected Portal portal;

	private String[] _removeSynonym(
		String[] synonymSets, String synonymToBeDeleted) {

		if (ArrayUtil.contains(synonymSets, synonymToBeDeleted, true)) {
			synonymSets = ArrayUtil.remove(synonymSets, synonymToBeDeleted);
		}

		return synonymSets;
	}

	private static final String[] _FILTER_NAMES = {
		"liferay_filter_synonym_en", "liferay_filter_synonym_es"
	};

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private SynonymIndexer _synonymIndexer;

	@Reference
	private SynonymSetIndexReader _synonymSetIndexReader;

	@Reference
	private SynonymSetIndexWriter _synonymSetIndexWriter;

}