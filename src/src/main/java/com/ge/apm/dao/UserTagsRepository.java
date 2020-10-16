package com.ge.apm.dao;

import com.ge.apm.domain.UserTags;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepository;

/**
 * Created by 212605082 on 2017/5/12.
 */
public interface UserTagsRepository extends GenericRepository<UserTags> {

    @Transactional
    @Modifying
    @Query(value = "delete from user_tags where user_uid = ?1", nativeQuery = true)
    void deleteByUserUid(String userUid);
}
