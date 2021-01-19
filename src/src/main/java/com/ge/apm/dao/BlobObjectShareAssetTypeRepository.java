package com.ge.apm.dao;

import com.ge.apm.domain.V2_BlobObjectSharedAssetType;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;


public interface BlobObjectShareAssetTypeRepository extends GenericRepositoryUUID<V2_BlobObjectSharedAssetType> {

    public List<V2_BlobObjectSharedAssetType> findByBoUuid(String boUid);

    public List<V2_BlobObjectSharedAssetType> findAllByObjectStorageId(String objectId);
}
