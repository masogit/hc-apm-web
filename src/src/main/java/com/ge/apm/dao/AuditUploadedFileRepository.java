package com.ge.apm.dao;

import com.ge.apm.domain.AuditUploadedFile;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 * Created by 503266963
 */
public interface AuditUploadedFileRepository extends GenericRepositoryUUID<AuditUploadedFile> {

    void deleteByEntryUid(String entryUid);

}
