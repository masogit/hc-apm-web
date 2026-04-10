package com.ge.apm.dao;

import com.ge.apm.domain.AdjustmentTransferApplication;
import com.ge.apm.domain.IdleEquipmentStock;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface AdjustmentTransferApplicationRepository extends GenericRepositoryUUID<AdjustmentTransferApplication> {

    public AdjustmentTransferApplication findByUid(String uid);




}
