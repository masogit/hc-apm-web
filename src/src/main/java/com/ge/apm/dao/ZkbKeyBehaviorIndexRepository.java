package com.ge.apm.dao;

import com.ge.apm.domain.ZkbKeyBehaviorIndex;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author @author 503202755 2020/9/23
 */
public interface ZkbKeyBehaviorIndexRepository extends GenericRepositoryUUID<ZkbKeyBehaviorIndex> {

    @Query(value = "SELECT  * from zkb_key_behavior_index where tenant_uid= ?1 and to_char(kbi_date, ?2)=?3 ", nativeQuery = true)
    List<ZkbKeyBehaviorIndex> findByTenantUIDAndKbiDate(String tenantUID,String dateFormat,String kpiDate);


    /**
     * @return
     */
    @Query(value = "SELECT * from (SELECT TO_CHAR(kbi_date,'yyyy-MM') as kbi_date from zkb_key_behavior_index ) T GROUP BY kbi_date", nativeQuery = true)
    List<String> findKbiDate();

    @Transactional
    @Modifying
    @Query(value = "update zkb_key_behavior_index set remark_ccsb=?1 where id =?2",nativeQuery = true)
    void updateRemarkCcsb(String remarkCcsb, String id);
}
