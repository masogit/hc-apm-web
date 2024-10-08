package com.ge.apm.dao;

import com.ge.apm.domain.HisGroupDataEntryHistory;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface HisGroupDataEntryHistoryRepository extends GenericRepositoryUUID<HisGroupDataEntryHistory> {
    void deleteByDataEntryUid(String uid);

    List<HisGroupDataEntryHistory> findByDataEntryUid(String uid);
}
