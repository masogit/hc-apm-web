package com.ge.apm.dao;

import com.ge.apm.domain.MvsContractDateInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * Created by 503266962
 */
public interface MvsContractDateInfoRepository extends GenericRepositoryUUID<MvsContractDateInfo> {


    @Query(value = "select * from mvs_contract_date_info t1 where order_change_date = (select max(order_change_date) from mvs_contract_date_info t2 where t2.oc_status = ?1 and t2.contract_no = t1.contract_no) and t1.contract_no = ?2", nativeQuery = true)
    List<MvsContractDateInfo> findNewestByStatusAndContractNo(String status, String contractNo);

    @Query(value = "select * from mvs_contract_date_info t1 where oc_status = ?1 and t1.contract_no = ?2", nativeQuery = true)
    List<MvsContractDateInfo> findByStatusAndContractNo(String status, String contractNo);
}
