package com.ge.apm.dao;


import com.ge.apm.domain.AssetOperationRecord;
import com.ge.apm.domain.AssetOptRecord;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface AssetOperationRecordRepository extends GenericRepositoryUUID<AssetOperationRecord> {


    @Query(value="select o.* from asset_operation_record o where o.asset_info_uid=?1",nativeQuery=true)
    public List<AssetOperationRecord> fetchRecordByAssetUid(String assetUid);
    
    
}
