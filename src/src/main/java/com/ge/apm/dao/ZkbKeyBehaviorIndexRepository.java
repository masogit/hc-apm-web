package com.ge.apm.dao;

import com.ge.apm.domain.ZkbKeyBehaviorIndex;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author @author 503202755 2020/9/23
 */
public interface ZkbKeyBehaviorIndexRepository extends GenericRepositoryUUID<ZkbKeyBehaviorIndex> {

    @Query(value = "SELECT  * from zkb_key_behavior_index where tenant_uid= ?1 and to_char(kbi_date, ?2)=?3 ", nativeQuery = true)
    List<ZkbKeyBehaviorIndex> findByTenantUIDAndKbiDate(String tenantUID,String dateFormat,String kpiDate);
}
