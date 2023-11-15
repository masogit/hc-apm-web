package com.ge.apm.dao;


import com.ge.apm.domain.VenderInfo;
import com.ge.apm.domain.ZkbPartOrderUserConfig;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author lyj
 */
public interface VenderInfoRepository extends GenericRepositoryUUID<VenderInfo> {


    VenderInfo findByOpenId(String openId);


    @Query(value = " select vi.* from vender_info vi inner join purchase_vender pv on vi.id = pv.vender_id inner join purchase_order po on po.id=pv.purchase_id where po.id= ?1 ",nativeQuery = true)
    List<VenderInfo> queryByPurchaseId(String purchaseId);
}
