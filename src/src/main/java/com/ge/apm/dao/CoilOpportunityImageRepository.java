package com.ge.apm.dao;

import com.ge.apm.domain.CoilOpportunityImage;
import webapp.framework.dao.GenericRepository;

public interface CoilOpportunityImageRepository extends GenericRepository<CoilOpportunityImage> {

    CoilOpportunityImage findByCataLog(String cataLog);

    CoilOpportunityImage findBySort(Integer sort);

}