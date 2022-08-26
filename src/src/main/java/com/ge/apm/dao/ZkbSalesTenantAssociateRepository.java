package com.ge.apm.dao;

import com.ge.apm.domain.ZkbSalesTenantAssociate;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author by lyj 503277717
 * @date 2022/8/25.
 */
public interface ZkbSalesTenantAssociateRepository extends GenericRepositoryUUID<ZkbSalesTenantAssociate>{

    @Modifying
    @Transactional
    @Query(value="delete from zkb_sales_Tenant_Associate where sales_person_id = ?1",nativeQuery = true)
    public void deleteBySalesPersonId(String salesPersonId);

    List<ZkbSalesTenantAssociate> findBySalesPersonId(String salesPersonId);
}
