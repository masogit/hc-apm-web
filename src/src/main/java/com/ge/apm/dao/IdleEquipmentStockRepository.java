package com.ge.apm.dao;

import com.ge.apm.domain.IdleEquipmentApplication;
import com.ge.apm.domain.IdleEquipmentStock;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface IdleEquipmentStockRepository extends GenericRepositoryUUID<IdleEquipmentStock> {

    public IdleEquipmentStock findByUid(String uid);



    public List<IdleEquipmentStock> findByAssetUidAndStatusIn(String uid,List<Integer> status);

}
