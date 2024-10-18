package com.ge.apm.dao;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.EfficiencyEquipmentInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.GenericRepositoryUUID;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public interface EfficiencyEquipmentInfoRepository extends GenericRepositoryUUID<EfficiencyEquipmentInfo> {
    @Transactional
    @Modifying
    @Query(value="update  efficiency_equipment_info set positive_rate=?2 where asset_uid in ?1",nativeQuery = true)
    int updateBatchPositiveRate(Set<String> assetUids, BigDecimal positiveRate);


//    @Query(value = "select  ai.* from asset_info ai  left join  efficiency_equipment_info eei on ai.uid=eei.asset_uid   where ai.tenant_uid=?1 and ai.is_valid =true and ai.is_deleted=false   and (ai.institution_uid in (?2) or ai.hospital_uid in (?2) or ai.site_uid in (?2))  and ai.clinical_dept_uid=?3 and eei.uid is null limit 5000 ",nativeQuery = true)
//    List<AssetInfo> getAssetInfoIsNotEfficiencyEquipment(String tenantUID, List<String> siteOrgUids, String clinicalDeptUid );

}
