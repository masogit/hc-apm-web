package com.ge.apm.dao;

import com.ge.apm.domain.USOptionImage;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

public interface USOptionImageRepository extends GenericRepository<USOptionImage> {

    @Query(value = "select * from us_option_image  where US_Segment = ?1 and product_Name = ?2 ",nativeQuery = true)
    USOptionImage findUS(String USSegment,String productName);

}