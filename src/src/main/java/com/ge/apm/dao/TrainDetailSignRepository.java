package com.ge.apm.dao;

import com.ge.apm.domain.TrainDetailSign;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface TrainDetailSignRepository extends GenericRepositoryUUID<TrainDetailSign> {

    List<TrainDetailSign> findByDetailId(String detailId);

    @Query("select max(signOrder) from TrainDetailSign where detailId = ?1 order by signOrder desc")
    Object queryMaxOrderByDetailId(String detailId);
}
