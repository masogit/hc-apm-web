package com.ge.apm.dao;

import com.ge.apm.domain.ZkbZone;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface ZkbZoneRepository extends GenericRepositoryUUID<ZkbZone> {

    List<ZkbZone> findByRegionAndZone(String region, String zone);

    List<ZkbZone> findByRegionAndZoneAndIdNot(String region, String zone,String id);


    List<ZkbZone> findByRegion(String region);
    List<ZkbZone> findByZone(String zone);
}
