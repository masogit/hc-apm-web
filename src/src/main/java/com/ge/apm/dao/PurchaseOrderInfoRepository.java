package com.ge.apm.dao;

import com.ge.apm.domain.budget.PurchaseOrderInfo;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;


/**
 * @author lyj
 */
public interface PurchaseOrderInfoRepository extends GenericRepositoryUUID<PurchaseOrderInfo> {


    /**
     * 获取索引
     * @return
     */
    @Query(value = "select max(t.purchase_num_index) from purchase_order t where  to_char(t.created_date,'yyyyMMdd') = to_char(now(),'yyyyMMdd') ", nativeQuery = true)
    Object getMaxIndex();
}
