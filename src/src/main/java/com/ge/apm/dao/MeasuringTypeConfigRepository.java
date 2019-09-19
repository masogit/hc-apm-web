package com.ge.apm.dao;

import com.ge.apm.domain.MeasuringTypeConfig;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * Created by 212605082 on 2017/10/11.
 */
public interface MeasuringTypeConfigRepository extends GenericRepositoryUUID<MeasuringTypeConfig> {

    public MeasuringTypeConfig findByMeasuringItemCategoryAndTenantUID(String measuringItemCategory, String tenantUID);

    public List<MeasuringTypeConfig> findBySiteUID(String siteUID);

}
