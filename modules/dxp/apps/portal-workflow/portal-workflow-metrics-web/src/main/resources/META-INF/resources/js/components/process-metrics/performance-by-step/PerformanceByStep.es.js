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
/* eslint-disable react-hooks/exhaustive-deps */

import React, {useContext, useMemo, useEffect} from 'react';

import EmptyState from '../../../shared/components/list/EmptyState.es';
import ListHeadItem from '../../../shared/components/list/ListHeadItem.es';
import ReloadButton from '../../../shared/components/list/ReloadButton.es';
import LoadingState from '../../../shared/components/loading/LoadingState.es';
import PaginationBar from '../../../shared/components/pagination/PaginationBar.es';
import Search from '../../../shared/components/pagination/Search.es';
import PromisesResolver from '../../../shared/components/request/PromisesResolver.es';
import Request from '../../../shared/components/request/Request.es';
import ResultBar from '../../../shared/components/results-bar/ResultsBar.es';
import {useRouter} from '../../../shared/components/router/useRouter.es';
import {formatDuration} from '../../../shared/util/duration.es';
import {getFormattedPercentage} from '../../../shared/util/util.es';
import {AppContext} from '../../AppContext.es';
import {TimeRangeFilter} from '../filter/TimeRangeFilter.es';
import {
	TimeRangeContext,
	TimeRangeProvider
} from '../filter/store/TimeRangeStore.es';
import {
	PerformanceDataContext,
	PerformanceDataProvider
} from './store/PerformanceByStepStore.es';

function PerformanceByStep({page, pageSize, processId, search, sort}) {
	const {client, setTitle} = useContext(AppContext);

	useEffect(() => {
		client.get(`/processes/${processId}/title`).then(({data}) => {
			setTitle(`${data}: ${Liferay.Language.get('performance-by-step')}`);
			return data;
		});
	}, []);

	return (
		<Request>
			<TimeRangeProvider
				previousKeys={['30']}
				search={search}
				timeRangeKeys={[]}
			>
				<PerformanceDataProvider>
					<PerformanceByStep.Body
						page={page}
						pageSize={pageSize}
						processId={processId}
						sort={sort}
						term={search}
					/>
				</PerformanceDataProvider>
			</TimeRangeProvider>
		</Request>
	);
}

const Body = ({page, pageSize, processId, sort, term}) => {
	const {fetchData, items = [], totalCount} = useContext(
		PerformanceDataContext
	);

	const {getSelectedTimeRange} = useContext(TimeRangeContext);

	const timeRange = getSelectedTimeRange() || {};

	const {
		location: {search},
		match: {path}
	} = useRouter();

	const params = [page, pageSize, processId, term, sort, timeRange];

	const promises = useMemo(() => [fetchData(...params)], [
		page,
		pageSize,
		sort,
		timeRange.key
	]);

	const errorMessageText = Liferay.Language.get(
		'there-was-a-problem-retrieving-data-please-try-reloading-the-page'
	);

	const emptyMessageText = term
		? Liferay.Language.get('no-results-were-found')
		: Liferay.Language.get('there-is-no-data-at-the-moment');

	return (
		<div>
			<nav className="management-bar management-bar-light navbar navbar-expand-md">
				<div className="container-fluid container-fluid-max-xl">
					<div className="navbar-form navbar-form-autofit">
						<Search
							disabled={false}
							placeholder={Liferay.Language.get(
								'search-for-step-name'
							)}
						/>
					</div>

					<TimeRangeFilter
						filterKey="timeRange"
						hideControl={true}
						position="right"
						showFilterName={false}
					/>
				</div>
			</nav>

			{term && (
				<ResultBar>
					<>
						<ResultBar.TotalCount
							term={term}
							totalCount={totalCount}
						/>
						<ResultBar.Clear
							pagination={{
								page,
								pageSize,
								processId,
								sort
							}}
							path={path}
							search={search}
						/>
					</>
				</ResultBar>
			)}

			<div
				className="container-fluid-1280 mt-4"
				data-testid="performance-test"
			>
				<PromisesResolver promises={promises}>
					<PromisesResolver.Pending>
						<div className={`border-1 pb-6 pt-6 sheet`}>
							<LoadingState />
						</div>
					</PromisesResolver.Pending>

					<PromisesResolver.Resolved>
						{items && items.length ? (
							<>
								<PerformanceByStep.Table items={items} />

								<PaginationBar
									page={page}
									pageCount={items.length}
									pageSize={pageSize}
									totalCount={totalCount}
								/>
							</>
						) : (
							<EmptyState
								className="border-1"
								hideAnimation={false}
								message={emptyMessageText}
								type="not-found"
							/>
						)}
					</PromisesResolver.Resolved>

					<PromisesResolver.Rejected>
						<EmptyState
							actionButton={<ReloadButton />}
							className="border-1"
							hideAnimation={true}
							message={errorMessageText}
							messageClassName="small"
							type="error"
						/>
					</PromisesResolver.Rejected>
				</PromisesResolver>
			</div>
		</div>
	);
};

const Table = ({items}) => {
	return (
		<div className="mb-3 table-responsive table-scrollable">
			<table className="table table-autofit table-heading-nowrap table-hover table-list">
				<thead>
					<tr>
						<th style={{width: '50%'}}>
							{Liferay.Language.get('step-name')}
						</th>

						<th className="text-right" style={{width: '25%'}}>
							<ListHeadItem
								name="overdueInstanceCount"
								title={Liferay.Language.get(
									'sla-breached-percent'
								)}
							/>
						</th>

						<th
							className="pr-4 table-head-title text-right"
							style={{width: '25%'}}
						>
							<ListHeadItem
								name="durationAvg"
								title={Liferay.Language.get(
									'average-completion-time'
								)}
							/>
						</th>
					</tr>
				</thead>

				<tbody>
					{items &&
						items.map((step, index) => (
							<PerformanceByStep.Item {...step} key={index} />
						))}
				</tbody>
			</table>
		</div>
	);
};

const Item = ({durationAvg, instanceCount, name, overdueInstanceCount}) => {
	const formattedDuration = formatDuration(durationAvg);
	const formattedPercentage = getFormattedPercentage(
		overdueInstanceCount,
		instanceCount
	);

	return (
		<tr>
			<td className="lfr-title-column table-cell-expand table-title">
				{name}
			</td>

			<td className="text-right">
				{overdueInstanceCount} {`(${formattedPercentage})`}
			</td>

			<td className="pr-4 text-right">{formattedDuration}</td>
		</tr>
	);
};

PerformanceByStep.Table = Table;

PerformanceByStep.Item = Item;

PerformanceByStep.Body = Body;

export default PerformanceByStep;
