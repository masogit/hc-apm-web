package com.ge.apm.dao;

import com.ge.apm.domain.IdleEquipmentApplication;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface IdleEquipmentApplicationRepository extends GenericRepositoryUUID<IdleEquipmentApplication> {

    public IdleEquipmentApplication findByUid(String uid);

    @Query(value = "select * from  idle_equipment_application where uid in ?1 ",nativeQuery = true)
    List<IdleEquipmentApplication>  findByUids(List<String> uids);



    public List<IdleEquipmentApplication> findByAssetUidAndStatusIn(String uid,List<Integer> status);

}
