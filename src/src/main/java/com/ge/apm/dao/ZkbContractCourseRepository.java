package com.ge.apm.dao;

import com.ge.apm.domain.ZkbContractCourse;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author 212605082 on 2019/12/16.
 */
public interface ZkbContractCourseRepository extends GenericRepositoryUUID<ZkbContractCourse> {

    ZkbContractCourse findFirstByContractIdAndCourseId(String contractId, String courseId);


    List<ZkbContractCourse> findByCourseId(String id);


    List<ZkbContractCourse> findByContractId(String contractId);

    /**
     * 根据时间和tenantUID查询数据
     * @param date 当前时间
     * @param tenantUid 当前用户tenant UID
     * @return
     */
    @Query(value = "SELECT zkbcc.* from zkb_contract zkbc INNER JOIN zkb_contract_course zkbcc on zkbc.id = zkbcc.contract_id where zkbc.start_date<=to_date(?1,'yyyy-MM-dd')  and zkbc.end_date>=to_date(?1,'yyyy-MM-dd') and tenant_uid = ?2 ", nativeQuery = true)
    List<ZkbContractCourse> findByDateAndTenantUid(String date,String tenantUid);
}
