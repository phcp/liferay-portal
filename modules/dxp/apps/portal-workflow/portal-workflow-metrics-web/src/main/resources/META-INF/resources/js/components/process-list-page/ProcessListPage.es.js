/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

import React from 'react';

import Search from '../../shared/components/pagination/Search.es';
import PromisesResolver from '../../shared/components/request/PromisesResolver.es';
import ResultsBar from '../../shared/components/results-bar/ResultsBar.es';
import {usePageTitle} from '../../shared/hooks/usePageTitle.es';
import {useResource} from '../../shared/hooks/useResource.es';
import {
	Body,
	EmptyView,
	ErrorView,
	LoadingView
} from './ProcessListPageBody.es';
import {Item} from './ProcessListPageItem.es';
import {Table} from './ProcessListPageTable.es';

const ProcessListPage = ({page, pageSize, search, sort}) => {
	usePageTitle(Liferay.Language.get('metrics'));

	const {data, promises} = useResource('/processes', {
		page,
		pageSize,
		sort: decodeURIComponent(sort),
		title: search ? decodeURIComponent(search) : null
	});

	return (
		<PromisesResolver promises={promises}>
			<ProcessListPage.Filters
				page={page}
				pageSize={pageSize}
				search={search}
				sort={sort}
				totalCount={data.totalCount}
			/>

			<div className="container-fluid-1280">
				<ProcessListPage.Body data={data} search={search} />
			</div>
		</PromisesResolver>
	);
};

const Filters = ({page, pageSize, search, sort, totalCount}) => {
	return (
		<>
			<nav className="management-bar management-bar-light navbar navbar-expand-md">
				<div className="container-fluid container-fluid-max-xl">
					<div className="navbar-form navbar-form-autofit">
						<Search disabled={!search && totalCount === 0} />
					</div>
				</div>
			</nav>

			{search && (
				<ResultsBar>
					<ResultsBar.TotalCount
						search={search}
						totalCount={totalCount}
					/>

					<ResultsBar.Clear
						page={page}
						pageSize={pageSize}
						sort={sort}
					/>
				</ResultsBar>
			)}
		</>
	);
};

ProcessListPage.Body = Body;
ProcessListPage.Empty = EmptyView;
ProcessListPage.Error = ErrorView;
ProcessListPage.Filters = Filters;
ProcessListPage.Item = Item;
ProcessListPage.Loading = LoadingView;
ProcessListPage.Table = Table;

export default ProcessListPage;
