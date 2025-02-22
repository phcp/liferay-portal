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

'use strict';

Liferay.XMLFormatter = function() {};

Liferay.XMLFormatter.prototype.format = function(content) {
	return content;
};

var METADATA = {
	name: 'definition',
	version: 1
};

var XML_NAMESPACE = {
	xmlns: 'urn:liferay.com:liferay-workflow_7.1.0',
	'xmlns:xsi': 'http://www.w3.org/2001/XMLSchema-instance',
	'xsi:schemaLocation':
		'urn:liferay.com:liferay-workflow_7.1.0 http://www.liferay.com/dtd/liferay-workflow-definition_7_1_0.xsd'
};

var serializeDefinition;

describe('Liferay.KaleoDesignerXMLDefinitionSerializer', () => {
	before(done => {
		AUI().use('liferay-kaleo-designer-xml-definition-serializer', () => {
			serializeDefinition = Liferay.KaleoDesignerXMLDefinitionSerializer;

			done();
		});
	});

	describe('regression', () => {
		it('test should serialize "receptionType" attribute.', done => {
			var jsonDefinition = {
				nodes: [
					{
						name: 'task1',
						notifications: {
							name: ['notification1'],
							recipients: [
								{
									receptionType: 'bcc'
								}
							]
						},
						xmlType: 'task'
					}
				]
			};

			var definition = serializeDefinition(
				XML_NAMESPACE,
				METADATA,
				jsonDefinition
			);

			assert(
				definition.indexOf('receptionType="bcc"') > 0,
				'receptionType attribute not serialized.'
			);

			done();
		});

		it('test should not serialize "receptionType" attribute if it has no value.', done => {
			var jsonDefinition = {
				nodes: [
					{
						name: 'task1',
						notifications: {
							name: ['notification1'],
							recipients: [
								{
									receptionType: [null]
								}
							]
						},
						xmlType: 'task'
					}
				]
			};

			var definition = serializeDefinition(
				XML_NAMESPACE,
				METADATA,
				jsonDefinition
			);

			assert(
				definition.indexOf('receptionType="') < 0,
				'Empty receptionType attribute is serialized.'
			);

			done();
		});

		it('test should not serialize "receptionType" attribute if it has an empty string value.', done => {
			var jsonDefinition = {
				nodes: [
					{
						name: 'task1',
						notifications: {
							name: ['notification1'],
							recipients: [
								{
									receptionType: ['']
								}
							]
						},
						xmlType: 'task'
					}
				]
			};

			var definition = serializeDefinition(
				XML_NAMESPACE,
				METADATA,
				jsonDefinition
			);

			assert(
				definition.indexOf('receptionType="') < 0,
				'Empty receptionType attribute is serialized.'
			);

			done();
		});

		it('test should serialize <user> element if given.', done => {
			var jsonDefinition = {
				nodes: [
					{
						name: 'task1',
						notifications: {
							name: ['notification1'],
							recipients: [
								{
									assignmentType: ['user'],
									emailAddress: [null],
									screenName: [null],
									userId: [null]
								}
							]
						},
						xmlType: 'task'
					}
				]
			};

			var definition = serializeDefinition(
				XML_NAMESPACE,
				METADATA,
				jsonDefinition
			);

			assert(
				definition.indexOf('<user') > 0,
				'<users/> element not serialized.'
			);

			done();
		});

		it('test should serialize <user> element even if empty.', done => {
			var jsonDefinition = {
				nodes: [
					{
						name: 'task1',
						notifications: {
							name: ['notification1'],
							recipients: [
								{
									assignmentType: ['user']
								}
							]
						},
						xmlType: 'task'
					}
				]
			};

			var definition = serializeDefinition(
				XML_NAMESPACE,
				METADATA,
				jsonDefinition
			);

			assert(
				definition.indexOf('<user') > 0,
				'<users/> element not serialized.'
			);

			done();
		});

		it('test should serialize <assignment> even when assignment object is empty.', done => {
			var jsonDefinition = {
				nodes: [
					{
						assignments: {},
						name: 'task1',
						xmlType: 'task'
					}
				]
			};

			var definition = serializeDefinition(
				XML_NAMESPACE,
				METADATA,
				jsonDefinition
			);

			assert(
				definition.indexOf('<assignments') >= 0,
				'<assignments/> element not serialized from empty object.'
			);

			done();
		});
	});
});
