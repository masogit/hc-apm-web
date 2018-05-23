package com.ge.apm.dao;


import com.ge.apm.domain.JsonTemplate;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

public interface JsonTemplateRepository extends GenericRepository<JsonTemplate> {
    @Query(value = "select content from json_template where type = ?1",nativeQuery = true)
    public String findByType(int type);
}

