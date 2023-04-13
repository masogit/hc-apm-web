package com.ge.apm.dao;

import com.ge.apm.domain.ReminderRuleOrg;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 * @author 503310146
 */
public interface ReminderRuleOrgRepository extends GenericRepositoryUUID<ReminderRuleOrg> {
    @Transactional
    @Modifying
    @Query(value = "delete from reminder_rule_org where rule_uid = ?1", nativeQuery = true)
    void deleteByReminderRuleUid(String ruleUid);
}
