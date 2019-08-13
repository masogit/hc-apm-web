package com.ge.apm.dao;

import com.ge.apm.domain.AssetLifecycle;
import com.ge.apm.domain.AssetLifecycleEvent;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;


public interface AssetLifecycleRepository extends GenericRepositoryUUID<AssetLifecycle> {


}