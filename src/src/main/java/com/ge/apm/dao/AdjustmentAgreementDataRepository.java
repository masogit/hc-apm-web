package com.ge.apm.dao;

import com.ge.apm.domain.AdjustmentAgreementData;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 * 调剂协议数据 Repository
 */
public interface AdjustmentAgreementDataRepository extends GenericRepositoryUUID<AdjustmentAgreementData> {

    /**
     * 按医院UID和协议类型查询（用于模板复用）
     */
    @Query("SELECT a FROM AdjustmentAgreementData a WHERE a.hospitalUid = ?1 AND a.agreementType = ?2")
    AdjustmentAgreementData findByHospitalUidAndAgreementType(String hospitalUid, String agreementType);
}
