package com.ge.apm.dao;

import com.ge.apm.domain.AuditGroupConfigurationDetail;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface AuditGroupConfigurationDetailRepository extends GenericRepositoryUUID<AuditGroupConfigurationDetail> {

    public List<AuditGroupConfigurationDetail> findByBusinessUidAndAuditStatus(String businessUid, Boolean auditStatus);

    public List<AuditGroupConfigurationDetail> findByBusinessUidOrderByApproveNumDesc(String businessUid);

    public List<AuditGroupConfigurationDetail> findByBusinessUidInOrderByApproveNumDesc(List<String> businessUidList);

    public List<AuditGroupConfigurationDetail> findByBusinessUidAndActualAuditUidAndDeleteFlag(String businessUid, String actualAuditUid, Integer deleteFlag);

    @Query(value = "select o from AuditGroupConfigurationDetail o where o.businessUid = ?1  order by o.createdDate desc")
    List<AuditGroupConfigurationDetail> listAllByBusinessUid(String businessUid);


}
