package com.ge.apm.service.wo;


import com.ge.apm.dao.MvsAssetInfoRepository;
import com.ge.apm.dao.V2_WorkOrderRepository;
import com.ge.apm.dao.mapper.V2_WorkOrderMapper;
import com.ge.apm.dao.mapper.WorkOrderMapper;
import com.ge.apm.domain.*;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 统一工单服务 - 合并内部工单和MVS工单
 * @author System Generated
 */
@Service
@Transactional
public class WorkOrderUnifiedService {
    protected static final Logger LOGGER = LoggerFactory.getLogger(WorkOrderUnifiedService.class);

    @Autowired
    private V2_WorkOrderMapper v2WorkOrderMapper;

    /**
     * 查询统一的工单列表（合并内部工单和MVS工单）
     * @param pageRequest 分页参数
     * @param filters 过滤条件
     * @return 分页的工单列表
     */
    public Page<V2_WorkOrder> findUnifiedWorkOrders(PageRequest pageRequest, Map<String, Object> filters) {
        try {

            List<V2_WorkOrder> pagedList =v2WorkOrderMapper.queryWorkOrderList( filters);
            long total = v2WorkOrderMapper.queryWorkOrderCount(filters);

            return new PageImpl<>(pagedList, pageRequest, total);

        } catch (Exception e) {
            LOGGER.error("findUnifiedWorkOrders error", e);
            return new PageImpl<>(Collections.emptyList(), pageRequest, 0);
        }
    }


    /**
     * 应用过滤条件
     */
    private List<V2_WorkOrder> applyFilters(List<V2_WorkOrder> orders, Map<String, Object> filters) {
        if (orders == null || orders.isEmpty()) {
            return orders;
        }

        List<V2_WorkOrder> filtered = orders;

        String assetName = (String) filters.get("assetInfo.name");
        if (StrUtil.isNotEmpty(assetName)) {
            filtered = filtered.stream()
                .filter(o -> o.getAssetInfo() != null &&
                    o.getAssetInfo().getName() != null &&
                    o.getAssetInfo().getName().contains(assetName))
                .collect(Collectors.toList());
        }

        String srNum = (String) filters.get("serviceRequest.srNum");
        if (StrUtil.isNotEmpty(srNum)) {
            filtered = filtered.stream()
                .filter(o -> o.getServiceRequest() != null &&
                    o.getServiceRequest().getSrNum() != null &&
                    o.getServiceRequest().getSrNum().contains(srNum))
                .collect(Collectors.toList());
        }

        String casePriority = (String) filters.get("serviceRequest.casePriority");
        if (StrUtil.isNotEmpty(casePriority)) {
            filtered = filtered.stream()
                .filter(o -> o.getServiceRequest() != null &&
                    casePriority.equals(o.getServiceRequest().getCasePriority()))
                .collect(Collectors.toList());
        }

        String currentStepName = (String) filters.get("currentStepName");
        if (StrUtil.isNotEmpty(currentStepName)) {
            filtered = filtered.stream()
                .filter(o -> currentStepName.equals(o.getCurrentStepName()))
                .collect(Collectors.toList());
        }

        return filtered;
    }


}