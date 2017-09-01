package com.ge.apm.dao;

import com.ge.apm.domain.Brand;
import webapp.framework.dao.GenericRepository;

public interface BrandRepository extends GenericRepository<Brand> {
    public Brand findByName(String name);
}
