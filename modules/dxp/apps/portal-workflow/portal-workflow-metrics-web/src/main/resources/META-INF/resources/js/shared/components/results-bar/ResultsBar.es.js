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

import pathToRegexp from 'path-to-regexp';
import React from 'react';
import {Link} from 'react-router-dom';

import {sub} from '../../util/lang.es';

/**
 * Results bar component.
 * @class
 * @memberof shared/components
 */
function ResultsBar({children}) {
	return (
		<nav className="subnav-tbar subnav-tbar-primary tbar tbar-inline-xs-down">
			<div className="container-fluid container-fluid-max-xl">
				<ul className="tbar-nav tbar-nav-wrap">{children}</ul>
			</div>
		</nav>
	);
}

const TotalCount = ({term, totalCount}) => {
	let resultText = Liferay.Language.get('x-results-for-x');

	if (totalCount === 1) {
		resultText = Liferay.Language.get('x-result-for-x');
	}

	return (
		<li className="tbar-item tbar-item-expand">
			<div className="tbar-section">
				<span className="component-text text-truncate-inline">
					<span className="text-truncate">
						{sub(resultText, [totalCount, term])}
					</span>
				</span>
			</div>
		</li>
	);
};

const Clear = ({pagination, path, search}) => {
	const pathname = pathToRegexp.compile(path)(pagination);

	return (
		<li className="tbar-item">
			<div className="tbar-section">
				<Link
					className="component-link tbar-link"
					to={{
						pathname,
						search
					}}
				>
					<span>{Liferay.Language.get('clear-all')}</span>
				</Link>
			</div>
		</li>
	);
};

ResultsBar.TotalCount = TotalCount;

ResultsBar.Clear = Clear;

export default ResultsBar;
