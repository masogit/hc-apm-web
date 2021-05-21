package com.ge.apm.dao;

import com.ge.apm.domain.UserAccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 * @author lyh
 * @version 1.0
 * @date 2021/5/19
 * @descript SystemConfigHistory操作记录
 * @package com.ge.apm.dao
 */
public interface UserAccountHistoryRepository extends GenericRepositoryUUID<UserAccountHistory>, JpaRepository<UserAccountHistory, String> {

}
