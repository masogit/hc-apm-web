package com.ge.apm.dao;

import com.ge.apm.domain.USProUp;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepository;

public interface USProUpRepository extends GenericRepository<USProUp> {

    @Transactional
    @Modifying
    @Query(value = "delete from us_pro_up  where name = ?1 ",nativeQuery = true)
    void deleteByName(String name);
}