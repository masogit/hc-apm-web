package com.ge.apm.dao;

import com.ge.apm.domain.ExternalWorkOrder;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.Date;
import java.util.List;

public interface ExWorkOrderRepository extends GenericRepository<ExternalWorkOrder> {


	@Query(value="select ewo.* from external_work_order ewo where ewo.asset_uid = ?1", nativeQuery = true)
	public List<ExternalWorkOrder> getExWorkOrderList(String assetUid);



}