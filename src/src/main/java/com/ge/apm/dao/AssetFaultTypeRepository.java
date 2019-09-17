package com.ge.apm.dao;

import com.ge.apm.domain.AssetFaultType;
import webapp.framework.dao.GenericRepository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AssetFaultTypeRepository extends GenericRepository<AssetFaultType> {

    public List<AssetFaultType> getByAssetGroupId(Integer assetGroupId);

    public List<AssetFaultType> getByAssetGroupIdOrderById(Integer assetGroupId);

    @Query(value = "select fault_name from asset_fault_type where id = :id",nativeQuery = true)
    public String getCaseTypeName(@Param("id") Integer id);

}
