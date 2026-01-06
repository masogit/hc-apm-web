package com.ge.apm.dao;

import com.ge.apm.domain.ZkbTrainAttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

@Repository
public interface ZkbTrainAttendanceRecordRepository extends GenericRepositoryUUID<ZkbTrainAttendanceRecord> {

    List<ZkbTrainAttendanceRecord> findAllByEngineerId(String engineerId);
}