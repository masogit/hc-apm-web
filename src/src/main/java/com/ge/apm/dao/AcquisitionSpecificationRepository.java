package com.ge.apm.dao;

import com.ge.apm.domain.acquisition.AcquisitionSpecification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface AcquisitionSpecificationRepository extends GenericRepositoryUUID<AcquisitionSpecification> {

    @Query("SELECT s FROM AcquisitionSpecification s WHERE " +
           "LOWER(s.firstLevelCategory) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.secondLevelCategory) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<AcquisitionSpecification> findByCategoryKeyword(@Param("keyword") String keyword);

}
