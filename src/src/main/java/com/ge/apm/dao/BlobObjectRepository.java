package com.ge.apm.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.V2_BlobObject;

import webapp.framework.dao.GenericRepositoryUUID;


public interface BlobObjectRepository extends GenericRepositoryUUID<V2_BlobObject> {
    
//    public V2_BlobObject findByObjectStorageId(String objectId);
    
    public List<V2_BlobObject> findByBoUuid(String boUid);

    public List<V2_BlobObject> findAllByObjectStorageId(String objectId);
    
    public List<V2_BlobObject> findAllByObjectStorageId(Set<String> objectIds);

    @Query(value="select o from V2_BlobObject o,AssetInfo a where o.boUuid=a.uid and o.boType='AssetInfo' and  (a.siteUID in ?1 or a.clinicalDeptUID in ?1"
            + " or a.institutionUID in ?1 or a.hospitalUID in ?1 or a.tenantUID in (?1) ) and o.boSubType like ?2 and o.objectName like ?3 and a.name like ?4 and a.functionType like ?5")
    public Page<V2_BlobObject> loadDataForPage(List<String> siteUidList, String boSubType, String boName,String assetName,String functionType,Pageable pageable);
    
    @Query(value="select o.* from v2_blob_object o limit 10", nativeQuery = true)
	public List<V2_BlobObject> findSomeTestRecords();

    public List<V2_BlobObject> findByBoUuidAndBoSubType(String boUuid, String boSubType);
}
