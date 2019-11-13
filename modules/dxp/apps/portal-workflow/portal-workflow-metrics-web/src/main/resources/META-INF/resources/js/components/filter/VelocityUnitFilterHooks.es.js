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

import React, {useMemo} from 'react';

import Filter from '../../shared/components/filter/Filter.es';
import {useFilterName} from '../../shared/components/filter/hooks/useFilterName.es';
import {useFilterStatic} from '../../shared/components/filter/hooks/useFilterStatic.es';
import {getVelocityUnits} from './util/velocityUnitUtil.es';

const VelocityUnitFilter = ({
	className,
	dispatch,
	filterKey = 'velocityUnit',
	options: {
		hideControl = true,
		multiple = false,
		position = 'right',
		withSelectionTitle = true
	} = {},
	timeRange
}) => {
	const velocityUnits = useMemo(() => getVelocityUnits(timeRange), [
		timeRange
	]);

	const {items, selectedItems} = useFilterStatic(
		dispatch,
		filterKey,
		velocityUnits
	);

	const defaultItem = useMemo(
		() => items.find(item => item.defaultVelocityUnit),
		[items]
	);

	const filterName = useFilterName(
		multiple,
		selectedItems,
		Liferay.Language.get('velocity-unit'),
		withSelectionTitle
	);

	return (
		<Filter
			defaultItem={defaultItem}
			elementClasses={className}
			filterKey={filterKey}
			hideControl={hideControl}
			items={items}
			multiple={multiple}
			name={filterName}
			position={position}
		/>
	);
};

export default VelocityUnitFilter;
