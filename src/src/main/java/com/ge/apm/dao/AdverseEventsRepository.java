package com.ge.apm.dao;


import com.ge.apm.domain.AdverseEvents;
import com.ge.apm.domain.UserAccount;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webapp.framework.dao.GenericRepository;

import java.util.List;
import java.util.Set;

public interface AdverseEventsRepository extends GenericRepository<AdverseEvents> {

    @Modifying
    @Query(value = "update adverse_events set status = :#{#events.status}, last_modified_date = CURRENT_TIMESTAMP, last_modified_by = :#{#events.lastModifiedBy} where uid = :#{#events.uid}", nativeQuery = true)
    void updateStatus(@Param("events") AdverseEvents events);

    @Query(value = "select count(1) from adverse_events t1 left join multi_level_approval t2 on t1.uid = t2.business_uid " +
        " where t2.approval_node_status = 0 and t1.status = 2 and t2.approval_uid like concat('%',:#{#user.uid},'%') " +
        " and t1.tenant_uid = :#{#user.tenantUID} and t1.institution_uid = :#{#user.institutionUID}" +
        " and t1.hospital_uid = :#{#user.hospitalUID} and t1.site_uid = :#{#user.siteUID}", nativeQuery = true)
    long countApproveWaitApprove(@Param("user") UserAccount user);

    @Query(value = "select count(distinct t1.*) from multi_level_approval t1 " +
                    " left join adverse_events t2 " +
                    "on t1.business_uid = t2.uid " +
                    "and t1.tenant_uid = t2.tenant_uid " +
                    "where t1.business_type = 13 and t1.approval_uid like concat('%',:#{#user.uid},'%') " +
                    "and t1.tenant_uid = :#{#user.tenantUID} and t2.status =2"
        , nativeQuery = true)
    long countApproveWaitApproveV2(@Param("user") UserAccount user);

    List<AdverseEvents> findByIdIn(Set<Integer> keySet);

    AdverseEvents findByUid(String uid);

    @Query(value = "select DISTINCT(hospital_uid) FROM adverse_events", nativeQuery = true)
    List<String> findByAllUseHospital();
}
