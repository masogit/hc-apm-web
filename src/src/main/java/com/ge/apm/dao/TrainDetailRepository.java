package com.ge.apm.dao;

import com.ge.apm.domain.TrainDetail;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface TrainDetailRepository extends GenericRepositoryUUID<TrainDetail> {

    List<TrainDetail> findByTrainId(String trainId);
}
