package com.ge.apm.dao;

import com.ge.apm.domain.USProject;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

public interface USProjectRepository extends GenericRepository<USProject> {

    USProject findByName(String name);

    int countByPdf(String pdf);


}