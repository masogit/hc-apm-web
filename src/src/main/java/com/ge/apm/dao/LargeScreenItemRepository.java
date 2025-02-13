package com.ge.apm.dao;

import com.ge.apm.domain.LargeScreenItem;
import webapp.framework.dao.GenericRepositoryUUID;

public interface LargeScreenItemRepository extends GenericRepositoryUUID<LargeScreenItem> {


    void deleteByLargeScreenConfigId(String id);
}
