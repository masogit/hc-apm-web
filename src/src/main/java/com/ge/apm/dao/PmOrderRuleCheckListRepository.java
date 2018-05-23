package com.ge.apm.dao;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import com.ge.apm.domain.PmOrderRuleCheckList;
import webapp.framework.dao.GenericRepository;

public interface PmOrderRuleCheckListRepository extends GenericRepository<PmOrderRuleCheckList> {

	 @Query(value="delete from PmOrderRuleCheckList wc where wc.ruleUID = ?1")
	 @Modifying
	 @Transactional
	public void deleteExistsRecord(String ruleUID);
	
	@Query("select o.itemId from PmOrderRuleCheckList o where o.ruleUID = ?1 ")
	public List<Integer> findByRuleUID(String ruleUID);

	@Query("select o from PmOrderRuleCheckList o where o.ruleUID = ?1 ")
	public List<PmOrderRuleCheckList> findAllByRuleUID(String ruleUID);

}
