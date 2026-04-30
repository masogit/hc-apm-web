package com.ge.apm.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.V2_BlobObject;

import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;


public interface BlobObjectRepository extends GenericRepositoryUUID<V2_BlobObject> {
    
//    public V2_BlobObject findByObjectStorageId(String objectId);
    
    public List<V2_BlobObject> findByBoUuid(String boUid);

    public List<V2_BlobObject> findByBoUuidAndObjectName(String boUid, String objectName);

    public List<V2_BlobObject> findAllByObjectStorageId(String objectId);
    
    @Query(value="select o.* from v2_blob_object o where o.bo_uuid=?1 and o.bo_type=?2 and o.bo_sub_type=?3",nativeQuery=true)
    public List<V2_BlobObject> fetchBlobObjectByUuidAndType(String boUuid,String boType,String boSubType);

    @Query(value="select o.* from v2_blob_object o where o.bo_uuid=?1 and o.bo_type != ?2",nativeQuery=true)
    public List<V2_BlobObject> fetchBlobObjectNotType(String boUuid, String boType);
    
    @Query(value="select o.* from v2_blob_object o where o.bo_uuid=?1 and o.bo_type=?2",nativeQuery=true)
    public List<V2_BlobObject> fetchBlobObjectByUuidAndType(String boUuid,String boType);
    
    public List<V2_BlobObject> findAllByObjectStorageId(Set<String> objectIds);

    @Query(value="select o from V2_BlobObject o,AssetInfo a where o.boUuid=a.uid and o.boType='AssetInfo' and  (a.siteUID in ?1 or a.clinicalDeptUID in ?1"
            + " or a.institutionUID in ?1 or a.hospitalUID in ?1 or a.tenantUID in (?1) ) and o.boSubType like ?2 and o.objectName like ?3 and a.name like ?4 and ((?5=?8 and 1=1) or (?5!=?8 and a.serialNum like ?5)) and ((?6=?8 and 1=1) or (?6!=?8 and a.qrCode like ?6)) and ((?7=?8 and 1=1) or (?7!=?8 and a.functionType like ?7))")
    public Page<V2_BlobObject> loadDataForPage(List<String> siteUidList, String boSubType, String boName,String assetName,String serialNum ,String qrCode,String functionType,String queryFlag,Pageable pageable);

    @Query(value="select o.* from v2_blob_object o limit 10", nativeQuery = true)
	public List<V2_BlobObject> findSomeTestRecords();

    public List<V2_BlobObject> findByBoUuidAndBoSubType(String boUuid, String boSubType);

    public List<V2_BlobObject> findByBoUuidAndBoType(String boUuid, String boSubType);


    @Query(value="select o.* from v2_blob_object o where o.id in ?1",nativeQuery=true)
	public List<V2_BlobObject> fetchListByIds(Set<String> ids);
	
	@Query(value="select o.* from v2_blob_object o where o.bo_id in ?1",nativeQuery=true)
	public List<V2_BlobObject> fetchListByBoIds(Set<Integer> ids);
	
	@Query(value="select o.* from v2_blob_object o where o.bo_uuid in ?1",nativeQuery=true)
	public List<V2_BlobObject> fetchListByBoUUIds(Set<String> ids);

	@Query(value="select o.* from v2_blob_object o where o.bo_uuid in (select t1.id from nurse_site_maintain_item t1 where t1.parent_id = ?1)",nativeQuery=true)
	public List<V2_BlobObject> fetchListByMaintainId(String maintainId);

    @Query(value="select vb from V2_BlobObject vb where vb.boUuid in (?1)")
    List<V2_BlobObject> getPartPictures(Set<String> ids);

    @Query(value="select o from V2_BlobObject o,AssetInfo a where o.boUuid=a.uid and o.boType='AssetInfo' and  (a.siteUID in (?1) or a.clinicalDeptUID in (?1)"
            + " or a.institutionUID in (?1) or a.hospitalUID in (?1) or a.tenantUID in (?1) ) and o.boSubType like ?2 and o.boSubType not in('1') and o.objectName like ?3 and a.name like ?4 and ((?5=?8 and 1=1) or (?5!=?8 and a.serialNum like ?5)) and ((?6=?8 and 1=1) or (?6!=?8 and a.qrCode like ?6)) and ((?7=?8 and 1=1) or (?7!=?8 and a.functionType like ?7))")
    public Page<V2_BlobObject> loadDataForPageYunNan(List<String> siteUidList, String boSubType, String boName,String assetName,String serialNum,String qrCode ,String functionType,String queryFlag,Pageable pageable);

    @Query(value="select o.object_storage_id from v2_blob_object o where o.bo_uuid in ?1",nativeQuery=true)
    public List<String> fetchObjectIdsByBoUUIds(List<String> ids);

    public V2_BlobObject findByBoIdAndBoType(Integer boId, String boType);

    @Query(value="select o.* from v2_blob_object o where o.bo_type = ?1 and o.bo_uuid in ?2",nativeQuery=true)
    public List<V2_BlobObject> fetchListByBoTypeAndBoUuIds(String boType, List<String> ids);

    @Modifying
    @Transactional
    void deleteByBoUuid(String boUuid);

    List<V2_BlobObject> findByObjectStorageIdIn(Set<String> objectIds);

    @Modifying
    @Transactional
    @Query(value = "UPDATE v2_blob_object SET approve_status = ?2 WHERE id= ?1", nativeQuery = true)
    public void updateBlobApprove(String id, String approveStatus);
}
