package com.ge.apm.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.domain.PmOrderRuleCheckList;
import com.ge.apm.domain.PmOrderRuleItem;

import webapp.framework.dao.GenericRepository;

public interface PmOrderRuleCheckListRepository extends GenericRepository<PmOrderRuleCheckList> {

	 @Query(value="delete from PmOrderRuleCheckList wc where wc.pmOrderRuleId = ?1")
	 @Modifying
	 @Transactional
	public void deleteExistsRecord(Integer pmOrderRuleId);

	public List<PmOrderRuleCheckList> findByTenantUID(String tenantUID);

	@Query("select o.itemId from PmOrderRuleCheckList o where o.tenantUID = ?1 and o.pmOrderRuleId = ?2 and o.status = 1")
	public List<Integer> findItemIds(String tenantUID, Integer integer);

}
