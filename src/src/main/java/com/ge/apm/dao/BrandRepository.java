package com.ge.apm.dao;

import com.ge.apm.domain.Brand;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface BrandRepository extends GenericRepository<Brand> {
    public Brand findByName(String name);

    @Query(value = "select id from brand where name like ?1",nativeQuery = true)
    public List<Integer> findIdsNameLike(String name);
}
