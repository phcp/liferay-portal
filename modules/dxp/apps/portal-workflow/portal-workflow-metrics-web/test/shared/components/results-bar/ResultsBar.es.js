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
import renderer from 'react-test-renderer';

import ResultsBar from '../../../../src/main/resources/META-INF/resources/js/shared/components/results-bar/ResultsBar.es';
import {MockRouter as Router} from '../../../mock/MockRouter.es';

test('Should render component with one item', () => {
	const component = renderer.create(
		<Router>
			<ResultsBar>
				<>
					<ResultsBar.TotalCount search={'test'} totalCount={1} />

					<ResultsBar.Clear
						page={1}
						pageSize={5}
						sort={encodeURIComponent('title:asc')}
					/>
				</>
			</ResultsBar>
		</Router>
	);

	const tree = component.toJSON();

	expect(tree).toMatchSnapshot();
});

test('Should render component with one list item', () => {
	const component = renderer.create(
		<Router>
			<ResultsBar>
				<>
					<ResultsBar.TotalCount search={'test'} totalCount={5} />

					<ResultsBar.Clear
						page={1}
						pageSize={5}
						sort={encodeURIComponent('title:asc')}
					/>
				</>
			</ResultsBar>
		</Router>
	);

	const tree = component.toJSON();

	expect(tree).toMatchSnapshot();
});
