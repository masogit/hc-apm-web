package com.ge.apm.service.wo;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by 212605082 on 2017/7/25.
 */
@Component
@Scope("prototype")
public class WorkOrderExportService {

    private static final String SHEET_WORKORDER = "WorkOrder List";
    private static final String I18N_INT_EXT_TYPE="intExtType";
    private static final String I18N_REPAIR_TYPE="repairType";
    private static final String I18N_CURRENT_STEP_="woSteps";

    @Autowired
    ServiceRequestRepository serviceRequestDao;
    @Autowired
    private V2_WorkOrderRepository v2WorkOrderDao;
    @Autowired
    private V2_WorkOrderStepRepository v2WorkOrderStepDao;
    @Autowired
    WorkOrderDetailRepository workOrderDetailDao;
    @Autowired
    AssetInfoRepository assetInfoDao;
    @Autowired
    AssetFaultTypeRepository assetFaultTypeDao;
    @Autowired
    I18nMessageRepository i18nMessageDao;


    //public List<Map<String,Object>> createData(String institutionUID, String hospitalUID, String siteUID, Date startTime, Date endTime, List<Integer> statusList){
    public List<Map<String,Object>> createData(OrgInfo selectedOrg, Date startTime, Date endTime, List<Integer> statusList){

        List<Map<String,Object>> resultList = new ArrayList<>();
        Map<String,Object> workOrderMap;

        List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();

        if(selectedOrg != null && selectedOrg.getOrgLevel() == 0){
            SearchFilter tenantFilter = new SearchFilter("tenant_uid", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(tenantFilter);
        }
        if(selectedOrg != null && selectedOrg.getOrgLevel() == 1){
            SearchFilter institutionFilter = new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(institutionFilter);
        }
        if(selectedOrg != null && selectedOrg.getOrgLevel() == 2){
            SearchFilter hospitalFilter = new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(hospitalFilter);
        }
        if(selectedOrg != null && selectedOrg.getOrgLevel() == 3){
            SearchFilter siteFilter = new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(siteFilter);
        }

        ZoneId zone = ZoneId.systemDefault();
        LocalDate startDate = LocalDateTime.ofInstant(startTime.toInstant(), zone).toLocalDate();
        LocalDate endDate = LocalDateTime.ofInstant(endTime.toInstant(), zone).toLocalDate();

        if(startTime != null){
            searchFilters.add(new SearchFilter("createdDate", SearchFilter.Operator.GTE, startTime));
        }
        if(endTime != null){
            searchFilters.add(new SearchFilter("createdDate", SearchFilter.Operator.LT, Date.from(endDate.plus(1, ChronoUnit.DAYS).atStartOfDay().atZone(zone).toInstant())));
        }
        if(statusList != null && !statusList.isEmpty()){
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.IN, statusList));
        }

        PageRequest pageRequest = this.buildPageRequest(0, 100000);

        Page<V2_WorkOrder> pageDate = v2WorkOrderDao.findBySearchFilter(searchFilters, pageRequest);

        //List<V2_WorkOrder> tempWorkOrderList = v2WorkOrderDao.findBySearchFilter(searchFilters);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();

        for (V2_WorkOrder workOrder: pageDate.getContent()) {
            AssetInfo assetInfo = assetInfoDao.findById(workOrder.getAssetId());
            V2_ServiceRequest serviceRequest = serviceRequestDao.findOne(workOrder.getSrId());
            List<V2_WorkOrder_Detail> workOrderDetailList = workOrderDetailDao.findByWoId(workOrder.getId());
            V2_WorkOrder_Step sendStep = v2WorkOrderStepDao.findFirstByWoIdAndStepIdOrderByStartTimeDesc(workOrder.getId(), 2);
            V2_WorkOrder_Step acceptStep = v2WorkOrderStepDao.findFirstByWoIdAndStepIdOrderByStartTimeAsc(workOrder.getId(), 4);
            V2_WorkOrder_Step evaluateStep = v2WorkOrderStepDao.findFirstByWoIdAndStepIdOrderByStartTimeDesc(workOrder.getId(), 5);
            V2_WorkOrder_Step closeStep = v2WorkOrderStepDao.findFirstByWoIdAndStepIdOrderByStartTimeDesc(workOrder.getId(), 6);
            workOrderMap = new HashedMap();
            workOrderMap.put("woId", workOrder.getId());
            workOrderMap.put("报修单号", serviceRequest.getSrNum() != null ? serviceRequest.getSrNum() : "");
            workOrderMap.put("设备编号", assetInfo.getDepartNum());
            workOrderMap.put("工单状态", workOrder.getStatus() != null ? FieldValueMessageController.fieldValue("status",workOrder.getStatus().toString()) : "");
            workOrderMap.put("资产编号", assetInfo.getFinancingNum());
            workOrderMap.put("设备名称", serviceRequest.getAssetName());
            workOrderMap.put("院区", serviceRequest.getHospitalName());
            workOrderMap.put("科室", assetInfo.getClinicalDeptName());
            workOrderMap.put("当前责任人", workOrder.getCurrentPersonName());
            workOrderMap.put("当前步骤", workOrder.getCurrentStepId() != null ? FieldValueMessageController.fieldValue(I18N_CURRENT_STEP_, workOrder.getCurrentStepId().toString()) : "");
            workOrderMap.put("报修人", serviceRequest.getRequestorName());
            workOrderMap.put("报修时间", serviceRequest.getRequestTime() != null ? sdf.format(serviceRequest.getRequestTime()) : "");
            workOrderMap.put("故障描述", serviceRequest.getRequestReason());
            if(sendStep != null){
                workOrderMap.put("派工人", sendStep.getOwnerName());
                workOrderMap.put("派工时间", sendStep.getEndTime() != null ? sdf.format(sendStep.getEndTime()) : "");
            }
            workOrderMap.put("工单类型", workOrder.getIntExtType() != null ? FieldValueMessageController.fieldValue(I18N_INT_EXT_TYPE, workOrder.getIntExtType().toString()) : "");
            workOrderMap.put("上门维修/送修", workOrder.getRepairType() != null ? FieldValueMessageController.fieldValue(I18N_REPAIR_TYPE, workOrder.getRepairType().toString()) : "");
            if(acceptStep != null){
                workOrderMap.put("接单人", acceptStep.getOwnerName());
                workOrderMap.put("接单时间", acceptStep.getStartTime() != null ? sdf.format(acceptStep.getStartTime()) : "");
            }
            workOrderMap.put("签到时间", workOrder.getCheckinTime() != null ? sdf.format(workOrder.getCheckinTime()) : "");
            workOrderMap.put("维修工时",workOrder.getTotalManHour());
            String info = "";
            double infoTotalPrice = 0;
            for (V2_WorkOrder_Detail workOrderDetail:workOrderDetailList) {
                Integer tempQuantity = workOrderDetail.getApartsQuantity() != null ? workOrderDetail.getApartsQuantity() : 0;
                Double tempPrice = workOrderDetail.getPartsPrice() != null ? workOrderDetail.getPartsPrice() : 0;
                info += workOrderDetail.getParts()+",数量："+ tempQuantity +",单价:"+ tempPrice +";";
                infoTotalPrice += tempQuantity * tempPrice;
            }

            workOrderMap.put("维修备件", info);
            workOrderMap.put("备件总费用", infoTotalPrice);
            workOrderMap.put("其他费用", workOrder.getOtherExpense());
            workOrderMap.put("外修费用", workOrder.getExternalExpense());
            workOrderMap.put("维修总费用", workOrder.getTotalPrice());
            if(evaluateStep != null && workOrder.getCurrentStepId() >= 6){
                workOrderMap.put("验收人", evaluateStep.getOwnerName());
                workOrderMap.put("验收时间", evaluateStep.getEndTime() != null ? sdf.format(evaluateStep.getEndTime()) : "");
            }
            if(closeStep != null){
                workOrderMap.put("关单人", closeStep.getOwnerName());
                workOrderMap.put("关单时间", closeStep.getEndTime() != null ? sdf.format(closeStep.getEndTime()) : "");
            }
            workOrderMap.put("最终评价", workOrder.getFeedbackRating());

            if(workOrder.getCaseType() != null){
                workOrderMap.put("故障类型", assetFaultTypeDao.findById(workOrder.getCaseType()) != null ? assetFaultTypeDao.findById(workOrder.getCaseType()).getFaultName() : "");
            }

            workOrderMap.put("问题描述", workOrder.getPatProblems());
            workOrderMap.put("解决方案", workOrder.getPatActions());
            workOrderMap.put("测试方法", workOrder.getPatTests());
            workOrderMap.put("备注", workOrder.getComment());

            resultList.add(workOrderMap);
        }

        return resultList;
    }

    public Boolean exportData(ExcelDocument doc, List<Map<String,Object>> data){

        Integer keyRow = 1;
        Integer dataRow = 2;

        for (Map<String, Object> item : data) {
            doc.writeRowData(SHEET_WORKORDER, keyRow, dataRow++, item);
        }

        return Boolean.TRUE;
    }

    //构建PageRequest
    private PageRequest buildPageRequest(Integer pageNumber, int pagzSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
        return new PageRequest(pageNumber, pagzSize, sort);
    }

}
