package com.ge.apm.dao;

import com.ge.apm.domain.UserAccountHistory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepository;

/**
 * @author lyh
 * @version 1.0
 * @date 2021/5/19
 * @descript SystemConfigHistory操作记录
 * @package com.ge.apm.dao
 */
public interface UserAccountHistoryRepository extends GenericRepository<UserAccountHistory> {

    @Transactional
    @Modifying
    @Query(value = "update user_account_history set user_uid = ?1 where login_name = ?2",nativeQuery =true)
    void updateNewUser(String userUid, String loginName);
}
