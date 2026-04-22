package com.ge.apm.dao;

import com.ge.apm.domain.EfficiencyEquipmentInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.math.BigDecimal;
import java.util.Set;

public interface EfficiencyEquipmentInfoRepository extends GenericRepositoryUUID<EfficiencyEquipmentInfo> {

    @Transactional
    @Modifying
    @Query(value = "update efficiency_equipment_info set positive_rate=?2, display_name=?3 where asset_uid in ?1", nativeQuery = true)
    int updateBatchBase(Set<String> assetUids, BigDecimal positiveRate, String displayName);

    @Transactional
    @Modifying
    @Query(value = "update efficiency_equipment_info set positive_rate=?2, display_name=?3, expected_annual_income=?4 where asset_uid in ?1", nativeQuery = true)
    int updateBatchWithIncome(Set<String> assetUids, BigDecimal positiveRate, String displayName, BigDecimal expectedAnnualIncome);

    @Transactional
    @Modifying
    @Query(value = "update efficiency_equipment_info set positive_rate=?2, display_name=?3, expected_check_number_people=?4 where asset_uid in ?1", nativeQuery = true)
    int updateBatchWithPeople(Set<String> assetUids, BigDecimal positiveRate, String displayName, Integer expectedCheckNumberPeople);

    @Transactional
    @Modifying
    @Query(value = "update efficiency_equipment_info set positive_rate=?2, display_name=?3, expected_annual_income=?4, expected_check_number_people=?5 where asset_uid in ?1", nativeQuery = true)
    int updateBatchWithIncomeAndPeople(Set<String> assetUids,
                                       BigDecimal positiveRate,
                                       String displayName,
                                       BigDecimal expectedAnnualIncome,
                                       Integer expectedCheckNumberPeople);

    @Transactional
    default int updateBatchSmart(Set<String> assetUids,
                                 BigDecimal positiveRate,
                                 String displayName,
                                 BigDecimal expectedAnnualIncome,
                                 Integer expectedCheckNumberPeople) {
        if (expectedAnnualIncome != null && expectedCheckNumberPeople != null) {
            return updateBatchWithIncomeAndPeople(assetUids, positiveRate, displayName, expectedAnnualIncome, expectedCheckNumberPeople);
        } else if (expectedAnnualIncome != null) {
            return updateBatchWithIncome(assetUids, positiveRate, displayName, expectedAnnualIncome);
        } else if (expectedCheckNumberPeople != null) {
            return updateBatchWithPeople(assetUids, positiveRate, displayName, expectedCheckNumberPeople);
        } else {
            return updateBatchBase(assetUids, positiveRate, displayName);
        }
    }

    @Transactional
    @Modifying
    @Query(value="update  efficiency_equipment_info set  display_name=?2 where asset_uid in ?1",nativeQuery = true)
    int updateBatchDisplayName(Set<String> assetUids, String displayName);


//    @Query(value = "select  ai.* from asset_info ai  left join  efficiency_equipment_info eei on ai.uid=eei.asset_uid   where ai.tenant_uid=?1 and ai.is_valid =true and ai.is_deleted=false   and (ai.institution_uid in (?2) or ai.hospital_uid in (?2) or ai.site_uid in (?2))  and ai.clinical_dept_uid=?3 and eei.uid is null limit 5000 ",nativeQuery = true)
//    List<AssetInfo> getAssetInfoIsNotEfficiencyEquipment(String tenantUID, List<String> siteOrgUids, String clinicalDeptUid );

}
