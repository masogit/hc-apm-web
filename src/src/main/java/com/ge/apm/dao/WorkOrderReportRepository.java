package com.ge.apm.dao;

import com.ge.apm.domain.V2_WorkOrder;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * Created by 212605082 on 2017/12/8.
 */
public interface WorkOrderReportRepository extends GenericRepositoryUUID<V2_WorkOrder> {
    List<V2_WorkOrder> findByAssetId(Integer assetId);
}
