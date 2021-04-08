package com.ge.apm.dao;

import com.ge.apm.domain.IBUInfoImage;
import webapp.framework.dao.GenericRepository;

public interface IBUInfoImageRepository extends GenericRepository<IBUInfoImage> {

    IBUInfoImage findByIbuOffer(String ibuOffer);

}