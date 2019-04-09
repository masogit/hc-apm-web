package com.ge.apm.dao;

import com.ge.apm.domain.MeasureOrderRuleCheckList;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface MeasureOrderRuleCheckListRepository extends GenericRepository<MeasureOrderRuleCheckList> {

	 @Query(value="delete from MeasureOrderRuleCheckList wc where wc.ruleUID = ?1 and status = 1")
	 @Modifying
	 @Transactional
	public void deleteExistsRecord(String ruleUID);

	@Query("select o.itemId from MeasureOrderRuleCheckList o where o.ruleUID = ?1 ")
	public List<Integer> findByRuleUID(String ruleUID);

	@Query("select o from MeasureOrderRuleCheckList o where o.ruleUID = ?1 and o.status = 1")
	public List<MeasureOrderRuleCheckList> findAllByRuleUID(String ruleUID);

	@Query("select o from MeasureOrderRuleCheckList o where o.ruleUID in ?1 and o.status = 1")
	public List<MeasureOrderRuleCheckList> findAllByRuleUIDBatch(List<String> ruleList);

	@Query("select o from MeasureOrderRuleCheckList o where o.ruleUID = ?1")
	public List<MeasureOrderRuleCheckList> findAllByRuleUID_full(String ruleUID);

	@Query(value="update MeasureOrderRuleCheckList rcl set rcl.status = 0 where rcl.uid in ?1")
	@Modifying
	@Transactional
	void updateStatus(List<String> deletedCheckListIds);

	@Query(value="select o from MeasureOrderRuleCheckList o where uid in ?1 and ruleUID =?2")
	@Transactional
	public List<MeasureOrderRuleCheckList> findPMCL(List<String> stringList, String ruleUid);


}
