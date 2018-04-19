package com.ge.apm.dao;
import com.ge.apm.domain.SharedAssetType;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface SharedAssetTypeRepository extends GenericRepositoryUUID<SharedAssetType> {
    //public SharedAssetType findById(String id);
    @Query(value = "select * from asset_info where shared_asset_type =?1",nativeQuery = true)
    public List<String> findAssetIsExist(String UID);
    
    @Query(value = "select distinct s.* from shared_asset_type s inner join asset_info a on s.id = a.shared_asset_type and s.site_uid =?1 and a.clinical_dept_uid =?2",nativeQuery = true)
    public List<SharedAssetType> findbyClinicalDeptUid(String SiteUid, String ClinicalDeptUid);
    
    @Query(value = "select s.* from shared_asset_type s inner join asset_info a on s.id = a.shared_asset_type and s.site_uid =?1 and a.clinical_dept_uid =?2 and s.id =?3",nativeQuery = true)
    public List<SharedAssetType> findbyBorrowQty(String SiteUid, String ClinicalDeptUid, String SharedTypeUid);
    
    @Query(value = "select id from SharedAssetType ")
    public List<String> findAllUid();

    @Query(value = "select distinct s.* from shared_asset_type s where s.site_uid =?1",nativeQuery = true)
    public List<SharedAssetType> findbySiteUid(String SiteUid);

}
