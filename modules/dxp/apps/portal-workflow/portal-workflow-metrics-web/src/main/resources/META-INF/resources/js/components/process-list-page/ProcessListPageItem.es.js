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

import {ChildLink} from '../../shared/components/router/routerWrapper.es';

const Item = ({
	id,
	instanceCount,
	onTimeInstanceCount,
	overdueInstanceCount,
	title
}) => {
	return (
		<tr>
			<td className="lfr-title-column table-cell-expand table-cell-minw-200 table-title">
				<ChildLink to={`/metrics/${id}`}>
					<span>{title}</span>
				</ChildLink>
			</td>

			<td className="text-right">{overdueInstanceCount}</td>

			<td className="text-right">{onTimeInstanceCount}</td>

			<td className="text-right">{instanceCount}</td>
		</tr>
	);
};
export {Item};
