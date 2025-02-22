/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.bookmarks.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link BookmarksFolderService}.
 *
 * @author Brian Wing Shun Chan
 * @see BookmarksFolderService
 * @generated
 */
public class BookmarksFolderServiceWrapper
	implements BookmarksFolderService, ServiceWrapper<BookmarksFolderService> {

	public BookmarksFolderServiceWrapper(
		BookmarksFolderService bookmarksFolderService) {

		_bookmarksFolderService = bookmarksFolderService;
	}

	/**
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link BookmarksFolderServiceUtil} to access the bookmarks folder remote service. Add custom service methods to <code>com.liferay.bookmarks.service.impl.BookmarksFolderServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	@Override
	public com.liferay.bookmarks.model.BookmarksFolder addFolder(
			long parentFolderId, String name, String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksFolderService.addFolder(
			parentFolderId, name, description, serviceContext);
	}

	@Override
	public void deleteFolder(long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_bookmarksFolderService.deleteFolder(folderId);
	}

	@Override
	public void deleteFolder(long folderId, boolean includeTrashedEntries)
		throws com.liferay.portal.kernel.exception.PortalException {

		_bookmarksFolderService.deleteFolder(folderId, includeTrashedEntries);
	}

	@Override
	public com.liferay.bookmarks.model.BookmarksFolder getFolder(long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksFolderService.getFolder(folderId);
	}

	@Override
	public java.util.List<Long> getFolderIds(long groupId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksFolderService.getFolderIds(groupId, folderId);
	}

	@Override
	public java.util.List<com.liferay.bookmarks.model.BookmarksFolder>
		getFolders(long groupId) {

		return _bookmarksFolderService.getFolders(groupId);
	}

	@Override
	public java.util.List<com.liferay.bookmarks.model.BookmarksFolder>
		getFolders(long groupId, long parentFolderId) {

		return _bookmarksFolderService.getFolders(groupId, parentFolderId);
	}

	@Override
	public java.util.List<com.liferay.bookmarks.model.BookmarksFolder>
		getFolders(long groupId, long parentFolderId, int start, int end) {

		return _bookmarksFolderService.getFolders(
			groupId, parentFolderId, start, end);
	}

	@Override
	public java.util.List<com.liferay.bookmarks.model.BookmarksFolder>
		getFolders(
			long groupId, long parentFolderId, int status, int start, int end) {

		return _bookmarksFolderService.getFolders(
			groupId, parentFolderId, status, start, end);
	}

	@Override
	public java.util.List<Object> getFoldersAndEntries(
		long groupId, long folderId) {

		return _bookmarksFolderService.getFoldersAndEntries(groupId, folderId);
	}

	@Override
	public java.util.List<Object> getFoldersAndEntries(
		long groupId, long folderId, int status) {

		return _bookmarksFolderService.getFoldersAndEntries(
			groupId, folderId, status);
	}

	@Override
	public java.util.List<Object> getFoldersAndEntries(
		long groupId, long folderId, int status, int start, int end) {

		return _bookmarksFolderService.getFoldersAndEntries(
			groupId, folderId, status, start, end);
	}

	@Override
	public int getFoldersAndEntriesCount(long groupId, long folderId) {
		return _bookmarksFolderService.getFoldersAndEntriesCount(
			groupId, folderId);
	}

	@Override
	public int getFoldersAndEntriesCount(
		long groupId, long folderId, int status) {

		return _bookmarksFolderService.getFoldersAndEntriesCount(
			groupId, folderId, status);
	}

	@Override
	public int getFoldersCount(long groupId, long parentFolderId) {
		return _bookmarksFolderService.getFoldersCount(groupId, parentFolderId);
	}

	@Override
	public int getFoldersCount(long groupId, long parentFolderId, int status) {
		return _bookmarksFolderService.getFoldersCount(
			groupId, parentFolderId, status);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _bookmarksFolderService.getOSGiServiceIdentifier();
	}

	@Override
	public void getSubfolderIds(
		java.util.List<Long> folderIds, long groupId, long folderId,
		boolean recurse) {

		_bookmarksFolderService.getSubfolderIds(
			folderIds, groupId, folderId, recurse);
	}

	@Override
	public java.util.List<Long> getSubfolderIds(
		long groupId, long folderId, boolean recurse) {

		return _bookmarksFolderService.getSubfolderIds(
			groupId, folderId, recurse);
	}

	@Override
	public void mergeFolders(long folderId, long parentFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_bookmarksFolderService.mergeFolders(folderId, parentFolderId);
	}

	@Override
	public com.liferay.bookmarks.model.BookmarksFolder moveFolder(
			long folderId, long parentFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksFolderService.moveFolder(folderId, parentFolderId);
	}

	@Override
	public com.liferay.bookmarks.model.BookmarksFolder moveFolderFromTrash(
			long folderId, long parentFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksFolderService.moveFolderFromTrash(
			folderId, parentFolderId);
	}

	@Override
	public com.liferay.bookmarks.model.BookmarksFolder moveFolderToTrash(
			long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksFolderService.moveFolderToTrash(folderId);
	}

	@Override
	public void restoreFolderFromTrash(long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_bookmarksFolderService.restoreFolderFromTrash(folderId);
	}

	@Override
	public void subscribeFolder(long groupId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_bookmarksFolderService.subscribeFolder(groupId, folderId);
	}

	@Override
	public void unsubscribeFolder(long groupId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_bookmarksFolderService.unsubscribeFolder(groupId, folderId);
	}

	@Override
	public com.liferay.bookmarks.model.BookmarksFolder updateFolder(
			long folderId, long parentFolderId, String name, String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _bookmarksFolderService.updateFolder(
			folderId, parentFolderId, name, description, serviceContext);
	}

	@Override
	public BookmarksFolderService getWrappedService() {
		return _bookmarksFolderService;
	}

	@Override
	public void setWrappedService(
		BookmarksFolderService bookmarksFolderService) {

		_bookmarksFolderService = bookmarksFolderService;
	}

	private BookmarksFolderService _bookmarksFolderService;

}