package com.ge.apm.dao;

import com.ge.apm.domain.ReminderRule;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 * @author 503310146
 */
public interface ReminderRuleRepository extends GenericRepositoryUUID<ReminderRule> {
    @Transactional
    @Modifying
    @Query(value = "delete from reminder_rule where type = ?1", nativeQuery = true)
    void deleteByType(String type);
}
