package com.ge.apm.dao;

import com.ge.apm.domain.FileAttachmentOrgInfo;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author 212605082 on 2019/12/5.
 */
public interface FileAttachmentOrgInfoRepository extends GenericRepositoryUUID<FileAttachmentOrgInfo> {

    void deleteByBlobObjectUid(String blobObjectUid);

    List<FileAttachmentOrgInfo> findByBlobObjectUid(String blobObjectUid);

}
