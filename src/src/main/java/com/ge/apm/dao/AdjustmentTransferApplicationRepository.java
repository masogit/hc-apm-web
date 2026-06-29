package com.ge.apm.dao;

import com.ge.apm.domain.AdjustmentTransferApplication;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface AdjustmentTransferApplicationRepository extends GenericRepositoryUUID<AdjustmentTransferApplication> {

    public AdjustmentTransferApplication findByUid(String uid);


    @Query(value = "select * from  adjustment_transfer_application where uid in ?1 ",nativeQuery = true)
    List<AdjustmentTransferApplication>  findByUids(List<String> uids);

}
