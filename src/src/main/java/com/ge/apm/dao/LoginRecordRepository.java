package com.ge.apm.dao;

import com.ge.apm.domain.LoginRecord;
import com.ge.apm.domain.simpleparts.SimplePartsRequestLineItem;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface LoginRecordRepository extends GenericRepository<LoginRecord> {

    @Select(value = "select item.* from login_record item where login_name = #{loginName} order by login_time desc")
    List<LoginRecord> findListByLoginName(@Param("loginName") String loginName);
}
