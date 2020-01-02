package com.ge.apm.dao;

import com.ge.apm.domain.ZkbEngineerTrackRecord;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author 212605082 on 2019/12/18.
 */
public interface ZkbEngineerTrackRecordRepository extends GenericRepositoryUUID<ZkbEngineerTrackRecord> {
    List<ZkbEngineerTrackRecord> findByZkbEngineerId(String zkbEngineerId);
}
