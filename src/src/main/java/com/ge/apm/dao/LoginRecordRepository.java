package com.ge.apm.dao;

import com.ge.apm.domain.LoginRecord;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface LoginRecordRepository extends GenericRepository<LoginRecord> {

    @Query(nativeQuery = true, value = "select o.* from login_record o where o.login_name = ?1 order by login_time desc")
    List<LoginRecord> findByLoginName(String loginName);

}
