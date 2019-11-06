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

import {useCallback, useContext, useMemo} from 'react';

import {AppContext} from '../../../../components/AppContext.es';
import {useRouterParams} from '../../../hooks/useRouterParams.es';
import {useFilterState} from './useFilterState.es';

const useFilterResource = (
	dispatch,
	filterKey,
	requestUrl,
	callback = null
) => {
	const {client} = useContext(AppContext);
	const {filters} = useRouterParams();
	const {items, selectedItems, setItems} = useFilterState(
		dispatch,
		filterKey
	);

	const fetchData = useCallback(
		() =>
			client.get(requestUrl).then(({data}) => {
				const mappedItems = handleFilterItems(
					data,
					filters[filterKey],
					callback
				);

				setItems(mappedItems);
			}),
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[requestUrl]
	);

	// eslint-disable-next-line react-hooks/exhaustive-deps
	const promises = useMemo(() => [fetchData()], [fetchData]);

	return {
		items,
		promises,
		selectedItems
	};
};

const handleFilterItems = (data, selectedKeys, callback = null) => {
	const items =
		callback && data.items ? callback(data.items) : data.items || [];

	return items.map(item => {
		const key = item.key || String(item.id);

		return {
			...item,
			active: selectedKeys && selectedKeys.includes(key),
			key
		};
	});
};

export {useFilterResource};
