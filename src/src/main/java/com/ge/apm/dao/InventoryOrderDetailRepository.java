package com.ge.apm.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.InventoryOrderDetail;
import com.ge.apm.domain.InventoryOrderDetail;

import webapp.framework.dao.GenericRepositoryUUID;
/**
 * Created by 212605082 on 2018/3/7.
 */
public interface InventoryOrderDetailRepository extends GenericRepositoryUUID<InventoryOrderDetail> {
	@Query(value="select o.* from inventory_order_detail o limit 10", nativeQuery = true)
	public List<InventoryOrderDetail> findSomeTestRecords();
}
