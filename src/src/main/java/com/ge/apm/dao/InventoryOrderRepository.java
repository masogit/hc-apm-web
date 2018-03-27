package com.ge.apm.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.InventoryOrder;
import com.ge.apm.domain.InventoryOrder;

import webapp.framework.dao.GenericRepositoryUUID;
/**
 * Created by 212605082 on 2018/3/7.
 */
public interface InventoryOrderRepository extends GenericRepositoryUUID<InventoryOrder> {
	@Query(value="select o.* from inventory_order o limit 10", nativeQuery = true)
	public List<InventoryOrder> findSomeTestRecords();
}
