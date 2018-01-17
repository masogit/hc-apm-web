package com.ge.apm.dao;

import com.ge.apm.domain.ExternalWorkOrder;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Date;
import java.util.List;

public interface ExWorkOrderRepository extends GenericRepositoryUUID<ExternalWorkOrder> {


	@Query(value="select ewo.* from external_work_order ewo where ewo.asset_uid = ?1 limit 4", nativeQuery = true)
	public List<ExternalWorkOrder> getExWorkOrderList(String assetUid);


	/*public List<ExternalWorkOrder> getExWorkOrderList();*/



}