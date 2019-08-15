package com.ge.apm.edgeserver.dataupload.dao;

import com.ge.apm.edgeserver.dataupload.entity.AssetPerformanceRecordDelta;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 212688574
 */
@Repository
public interface AssetPerformanceRecordDeltaRepository extends CrudRepository<AssetPerformanceRecordDelta, String> {
}