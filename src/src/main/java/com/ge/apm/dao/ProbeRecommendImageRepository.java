package com.ge.apm.dao;

import com.ge.apm.domain.ProbeRecommendImage;
import webapp.framework.dao.GenericRepository;

public interface ProbeRecommendImageRepository extends GenericRepository<ProbeRecommendImage> {
    ProbeRecommendImage findByCatNo (String catNo);

}