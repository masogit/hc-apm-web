package com.ge.apm.dao;

import com.ge.apm.domain.IdleEquipmentApplication;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface IdleEquipmentApplicationRepository extends GenericRepositoryUUID<IdleEquipmentApplication> {

    public IdleEquipmentApplication findByUid(String uid);



    public List<IdleEquipmentApplication> findByAssetUidAndStatusIn(String uid,List<Integer> status);

}
