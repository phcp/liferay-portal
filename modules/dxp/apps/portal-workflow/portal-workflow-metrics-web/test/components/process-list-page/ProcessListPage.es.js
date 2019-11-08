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

import ProcessListPage from '../../../src/main/resources/META-INF/resources/js/components/process-list-page/ProcessListPage.es';
import {MockRouter} from '../../mock/MockRouter.es';

const MockContext = ({children, clientMock}) => (
	<MockRouter client={clientMock}>{children}</MockRouter>
);

test('Should render component', () => {
	const clientMock = {
		get: jest.fn().mockResolvedValue({data: {items: [], totalCount: 0}})
	};

	const component = renderer.create(
		<MockContext clientMock={clientMock}>
			<ProcessListPage
				page="1"
				pageSize="20"
				sort="overdueInstanceCount:desc"
			/>
		</MockContext>
	);
	const tree = component.toJSON();

	expect(tree).toMatchSnapshot();
});

test('Should render component with 10 records', () => {
	const data = {
		items: [
			{
				instancesCount: 0,
				title: 'Single Approver 1'
			},
			{
				instancesCount: 0,
				title: 'Single Approver 2'
			},
			{
				instancesCount: 0,
				title: 'Single Approver 3'
			},
			{
				instancesCount: 1,
				title: 'Single Approver 4'
			},
			{
				instancesCount: 0,
				title: 'Single Approver 5'
			},
			{
				instancesCount: 0,
				title: 'Single Approver 6'
			},
			{
				instancesCount: 0,
				title: 'Single Approver 7'
			},
			{
				instancesCount: 0,
				title: 'Single Approver 8'
			},
			{
				instancesCount: 0,
				title: 'Single Approver 9'
			},
			{
				instancesCount: 0,
				title: 'Single Approver 10'
			}
		],
		totalCount: 10
	};

	const clientMock = {
		get: jest.fn().mockResolvedValue({data: {data}})
	};

	const component = renderer.create(
		<MockContext clientMock={clientMock}>
			<ProcessListPage
				page="1"
				pageSize="20"
				sort="overdueInstanceCount:desc"
			/>
		</MockContext>
	);

	const tree = component.toJSON();

	expect(tree).toMatchSnapshot();
});

test('Should render component with 4 records', () => {
	const data = {
		items: [
			{
				instancesCount: 0,
				title: 'Single Approver 1'
			},
			{
				instancesCount: 0,
				title: 'Single Approver 2'
			},
			{
				instancesCount: 0,
				title: 'Single Approver 3'
			},
			{
				instancesCount: 1,
				title: 'Single Approver 4'
			}
		],
		totalCount: 4
	};

	const clientMock = {
		get: jest.fn().mockResolvedValue({data: {data}})
	};

	const component = renderer.create(
		<MockContext clientMock={clientMock}>
			<ProcessListPage
				page="1"
				pageSize="20"
				sort="overdueInstanceCount:desc"
			/>
		</MockContext>
	);

	const tree = component.toJSON();

	expect(tree).toMatchSnapshot();
});
