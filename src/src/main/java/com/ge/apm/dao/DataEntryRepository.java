package com.ge.apm.dao;

import com.ge.apm.domain.DataEntry;
import com.ge.apm.domain.his.HisAssetGroupAssetMatch;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Date;
import java.util.List;

public interface DataEntryRepository  extends GenericRepositoryUUID<DataEntry> {


    List<DataEntry> findByAssetGroupUidAndAssetGroupOrgInfoUid(String assetGroupUid, String clinicalDeptUid);



    @Query(value = "select * from data_entry   where asset_group_uid=?1 and asset_group_org_info_uid=?2 and      ", nativeQuery = true)
    List<DataEntry> findByAssetGroupUidAndAssetGroupOrgInfoUidAndDateQuery(String assetGroupUid, String clinicalDeptUid, Date startDate, Date endDate);


}
