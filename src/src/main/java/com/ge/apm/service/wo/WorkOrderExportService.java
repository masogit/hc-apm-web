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
    private static final String I18N_INT_EXT_TYPE = "intExtType";
    private static final String I18N_REPAIR_TYPE = "repairType";
    private static final String I18N_CURRENT_STEP_ = "woSteps";

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    ServiceRequestRepository serviceRequestDao;
    @Autowired
    ServiceRequestMsgRepository serviceRequestMsgRepository;
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
    @Autowired
    V2_WorkOrderCoworkerRepository workOrderCoworkerDao;
    @Autowired
    UserAccountRepository userAccountDao;

    //public List<Map<String,Object>> createData(String institutionUID, String hospitalUID, String siteUID, Date startTime, Date endTime, List<Integer> statusList){
    public List<Map<String, Object>> createData(OrgInfo selectedOrg, Date startTime, Date endTime, Date closedStartTime, Date closedEndTime, List<Integer> statusList) {

        List<Map<String, Object>> resultList = new ArrayList<>();

        List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();

        if (selectedOrg != null && selectedOrg.getOrgLevel() == 0) {
            SearchFilter tenantFilter = new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(tenantFilter);
        }
        if (selectedOrg != null && selectedOrg.getOrgLevel() == 1) {
            SearchFilter institutionFilter = new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(institutionFilter);
        }
        if (selectedOrg != null && selectedOrg.getOrgLevel() == 2) {
            SearchFilter hospitalFilter = new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(hospitalFilter);
        }
        if (selectedOrg != null && selectedOrg.getOrgLevel() == 3) {
            SearchFilter siteFilter = new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(siteFilter);
        }

        ZoneId zone = ZoneId.systemDefault();

        if (startTime != null) {
            searchFilters.add(new SearchFilter("createdDate", SearchFilter.Operator.GTE, startTime));
        }
        if (endTime != null) {
            LocalDate endDate = LocalDateTime.ofInstant(endTime.toInstant(), zone).toLocalDate();
            searchFilters.add(new SearchFilter("createdDate", SearchFilter.Operator.LT, Date.from(endDate.plus(1, ChronoUnit.DAYS).atStartOfDay().atZone(zone).toInstant())));
        }
        if (closedStartTime != null) {
            searchFilters.add(new SearchFilter("closeTime", SearchFilter.Operator.GTE, closedStartTime));
        }
        if (closedEndTime != null) {
            LocalDate closeEndDate = LocalDateTime.ofInstant(closedEndTime.toInstant(), zone).toLocalDate();
            searchFilters.add(new SearchFilter("closeTime", SearchFilter.Operator.LT, Date.from(closeEndDate.plus(1, ChronoUnit.DAYS).atStartOfDay().atZone(zone).toInstant())));
        }
        if (statusList != null && !statusList.isEmpty()) {
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.IN, statusList));
        }

        PageRequest pageRequest = this.buildPageRequest(0, 100000);

        Page<V2_WorkOrder> pageDate = v2WorkOrderDao.findBySearchFilter(searchFilters, pageRequest);

        Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();

        for (V2_WorkOrder workOrder : pageDate.getContent()) {
            AssetInfo assetInfo = assetInfoDao.findById(workOrder.getAssetId());
            V2_ServiceRequest serviceRequest = serviceRequestDao.findOne(workOrder.getSrId());
            List<V2_WorkOrder_Detail> workOrderDetailList = workOrderDetailDao.findByWoId(workOrder.getId());
            V2_WorkOrder_Step sendStep = v2WorkOrderStepDao.findFirstByWoIdAndStepIdOrderByStartTimeDesc(workOrder.getId(), 2);
            V2_WorkOrder_Step acceptStep = v2WorkOrderStepDao.findFirstByWoIdAndStepIdOrderByStartTimeAsc(workOrder.getId(), 4);
            V2_WorkOrder_Step workStep = v2WorkOrderStepDao.findFirstByWoIdAndStepIdOrderByStartTimeDesc(workOrder.getId(), 4);
            V2_WorkOrder_Step evaluateStep = v2WorkOrderStepDao.findFirstByWoIdAndStepIdOrderByStartTimeDesc(workOrder.getId(), 5);
            V2_WorkOrder_Step closeStep = v2WorkOrderStepDao.findFirstByWoIdAndStepIdOrderByStartTimeDesc(workOrder.getId(), 6);

            List<V2_WorkOrderCoworker> workOrderList = workOrderCoworkerDao.findByWoId(workOrder.getId());

            Map<String, Object> workOrderMap = new HashedMap();

            workOrderMap.put("woId", workOrder.getId());
            workOrderMap.put("报修单号", serviceRequest.getSrNum() != null ? serviceRequest.getSrNum() : "");
            workOrderMap.put("设备编号", assetInfo.getDepartNum());
            workOrderMap.put("工单状态", workOrder.getStatus() != null ? FieldValueMessageController.fieldValue("status", workOrder.getStatus().toString()) : "");
            workOrderMap.put("资产编号", assetInfo.getFinancingNum());
            workOrderMap.put("设备序列号", assetInfo.getSerialNum());
            workOrderMap.put("设备名称", serviceRequest.getAssetName());
            workOrderMap.put("院区", serviceRequest.getHospitalName());
            workOrderMap.put("科室", assetInfo.getClinicalDeptName());
            workOrderMap.put("制造商", assetInfo.getManufacture());
            workOrderMap.put("品牌", assetInfo.getMakerShortName());
            workOrderMap.put("当前责任人", workOrder.getCurrentPersonName());
            workOrderMap.put("当前步骤", workOrder.getCurrentStepId() != null ? FieldValueMessageController.fieldValue(I18N_CURRENT_STEP_, workOrder.getCurrentStepId().toString()) : "");
            workOrderMap.put("报修人", serviceRequest.getRequestorName());
            workOrderMap.put("报修时间", formatDate(serviceRequest.getRequestTime()));
            workOrderMap.put("故障描述", serviceRequest.getRequestReason());
            if (sendStep != null) {
                workOrderMap.put("派工人", sendStep.getOwnerName());
                workOrderMap.put("派工时间", formatDate(sendStep.getEndTime()));
            }
            workOrderMap.put("工单类型", workOrder.getIntExtType() != null ? FieldValueMessageController.fieldValue(I18N_INT_EXT_TYPE, workOrder.getIntExtType().toString()) : "");
            workOrderMap.put("上门维修/送修", workOrder.getRepairType() != null ? FieldValueMessageController.fieldValue(I18N_REPAIR_TYPE, workOrder.getRepairType().toString()) : "");
            if (acceptStep != null) {
                workOrderMap.put("接单人", acceptStep.getOwnerName());
                workOrderMap.put("接单时间", formatDate(acceptStep.getStartTime()));
            }
            if (workStep != null) {
                workOrderMap.put("维修完成时间", formatDate(workStep.getEndTime()));
            }
            workOrderMap.put("签到时间", formatDate(workOrder.getCheckinTime()));
            workOrderMap.put("维修工时", workOrder.getTotalManHour());
            String info = "";
            double infoTotalPrice = 0;
            List<Map<String, Object>> parts = new ArrayList<>();
            for (V2_WorkOrder_Detail workOrderDetail : workOrderDetailList) {
                if (workOrderDetail.getApproved() == null || workOrderDetail.getApproved()) {
                    Integer tempQuantity = workOrderDetail.getApartsQuantity() != null ? workOrderDetail.getApartsQuantity() : 0;
                    Double tempPrice = workOrderDetail.getPartsPrice() != null ? workOrderDetail.getPartsPrice() : 0;
                    info += workOrderDetail.getParts() + ",数量：" + tempQuantity + ",单价:" + tempPrice + ";";
                    infoTotalPrice += tempQuantity * tempPrice;
                }

                Map<String, Object> part = new HashedMap();
                part.put("备件名", workOrderDetail.getParts());
                part.put("单价", workOrderDetail.getPartsPrice());
                part.put("数量", workOrderDetail.getApartsQuantity());
                part.put("类型", getPartType(workOrderDetail.getPartsType()));
                part.put("序列号", workOrderDetail.getSerialNum());
                part.put("质保期", workOrderDetail.getWarrantyDate());
                if (workOrderDetail.getArrived() == null) {
                    part.put("到货", null);
                } else {
                    String arrived = workOrderDetail.getArrived().equals(Boolean.TRUE) ? "到货" : "未到货";
                    part.put("到货", arrived);
                }
                parts.add(part);
            }

            putServiceRequestMessage(workOrder, workOrderMap);

            workOrderMap.put("维修备件", info);
            workOrderMap.put("备件总费用", infoTotalPrice);
            workOrderMap.put("其他费用", workOrder.getOtherExpense());
            workOrderMap.put("外修费用", workOrder.getExternalExpense());
            workOrderMap.put("维修总费用", workOrder.getTotalPrice());
            if (evaluateStep != null && workOrder.getCurrentStepId() >= 6) {
                workOrderMap.put("验收人", evaluateStep.getOwnerName());
                workOrderMap.put("验收时间", formatDate(evaluateStep.getEndTime()));
            }
            if (closeStep != null) {
                workOrderMap.put("关单人", closeStep.getOwnerName());
                workOrderMap.put("关单时间", formatDate(closeStep.getEndTime()));
            }
            String coworkerStr = "";
            String coworkManHourStr = "";
            int i = 0;
            for (V2_WorkOrderCoworker woc : workOrderList) {
                coworkerStr += woc.getCowokerUserName() + ",";
                coworkManHourStr += woc.getManHours() + ",";

                i++;
                workOrderMap.put("工时" + String.valueOf(i), woc.getManHours());
                workOrderMap.put("协作者" + String.valueOf(i), woc.getCowokerUserName());
            }
            workOrderMap.put("协助者", coworkerStr);
            workOrderMap.put("协助者工时", coworkManHourStr);
            workOrderMap.put("最终评价", workOrder.getFeedbackRating());

            if (workOrder.getCaseType() != null) {
                workOrderMap.put("故障类型", assetFaultTypeDao.findById(workOrder.getCaseType()) != null ? assetFaultTypeDao.findById(workOrder.getCaseType()).getFaultName() : "");
            }

            workOrderMap.put("原因描述", workOrder.getPatProblems());
            workOrderMap.put("解决方案", workOrder.getPatActions());
            workOrderMap.put("交付前检测", workOrder.getPatTests());
            workOrderMap.put("备注", workOrder.getComment());
            workOrderMap.put("二维码", assetInfo.getQrCode());
            workOrderMap.put("设备分类", assetInfo.getAssetGroup() != null ? FieldValueMessageController.fieldValue("assetGroup", assetInfo.getAssetGroup().toString()) : "");
            if (assetInfo.getFunctionGroup() != null) {
                workOrderMap.put("医疗器械分类", assetInfo.getFunctionGroup() != null ? FieldValueMessageController.fieldValue("assetFunctionType", assetInfo.getFunctionGroup().toString()) : "");
                workOrderMap.put("医疗器械子分类", assetInfo.getAssetCode68() != null ? FieldValueMessageController.fieldValue(assetInfo.getFunctionGroup().toString(), assetInfo.getAssetCode68().toString()) : "");
            } else {
                workOrderMap.put("医疗器械分类", "");
                workOrderMap.put("医疗器械子分类", "");
            }
            workOrderMap.put("设备型号", assetInfo.getFunctionType());
            workOrderMap.put("距上次报修天数", serviceRequest.getNearestSrDays());
            workOrderMap.put("停机时间", formatDate(serviceRequest.getConfirmedDownTime()));
            workOrderMap.put("恢复时间", formatDate(serviceRequest.getConfirmedUpTime()));

            //added columns for pdf export
            workOrderMap.put("联系电话", (userAccountDao.getById(serviceRequest.getRequestorId()) != null) ? userAccountDao.getById(serviceRequest.getRequestorId()).getTelephone() : null);
            workOrderMap.put("设备编号", assetInfo.getDepartNum());
            workOrderMap.put("当前位置", serviceRequest.getAssetLocation() == null ? assetInfo.getLocationName()
                    : serviceRequest.getAssetLocation());
            workOrderMap.put("报修状态", getPdfStatus(assetInfo.getStatus()));
			/*if(serviceRequest.getStatus() == 2)
			{
				workOrderMap.put("停机时间", (serviceRequest.getConfirmedDownTime() != null ? serviceRequest.getConfirmedDownTime():null));
			}
			else
			{
				workOrderMap.put("停机时间", "未停机");
			}*/

            workOrderMap.put("备件信息", parts);
            workOrderMap.put("维修流程", null);
            workOrderMap.put("工时", workOrder.getManHours());
            workOrderMap.put("维修类型", workOrder.getRepairType() != null ? FieldValueMessageController.fieldValue(I18N_REPAIR_TYPE, workOrder.getRepairType().toString()) : "");

            List<V2_WorkOrder_Step> steps = v2WorkOrderStepDao.findByWorkOrder(workOrder.getId());
            List<Map<String, Object>> woSteps = new ArrayList<Map<String, Object>>();
            Map<String, Object> woStep;
            for (V2_WorkOrder_Step step : steps) {
                woStep = new HashedMap();
                woStep.put("步骤名称", step.getStepName());
                woStep.put("责任人", step.getOwnerName());
                woStep.put("开始时间", formatDate(step.getStartTime()));
                woStep.put("结束时间", formatDate(step.getEndTime()));
                woStep.put("处理描述", step.getComments());
                woSteps.add(woStep);
            }

            workOrderMap.put("维修流程", woSteps);

            resultList.add(workOrderMap);
        }

        return resultList;
    }

    private void putServiceRequestMessage(V2_WorkOrder workOrder, Map<String, Object> workOrderMap) {
        List<ServiceRequestMsg> serviceRequestMsgs = serviceRequestMsgRepository.findBySrIdOrderBySendTime(workOrder.getId());
        List<Map<String, Object>> srMsgs = new ArrayList<>();
        for (ServiceRequestMsg srMessage : serviceRequestMsgs) {
            Map<String, Object> map = new HashedMap();
            map.put("sendTime", formatDate(srMessage.getSendTime()));
            map.put("senderUserName", srMessage.getSenderUserName());
            map.put("message", srMessage.getMessage());
            srMsgs.add(map);
        }
        workOrderMap.put("serviceRequestMessages", srMsgs);
    }

    private String formatDate(Date date) {
        return date != null ? sdf.format(date) : "";
    }

    private String getPartType(String type) {
        String partType = null;

        if (type != null) {
            switch (type) {
                case "1":
                    partType = "库房领用";
                    break;
                case "2":
                    partType = "医工采购";
                    break;
                case "3":
                    partType = "外修采购";
                    break;
                case "4":
                    partType = "报废设备拆卸";
                    break;
                default:
                    break;
            }
        }
        return partType;
    }

    private String getPdfStatus(Integer status) {
        String assetStatus = null;

        if (status != null) {
            switch (status) {
                case 1:
                    assetStatus = "正常";
                    break;
                case 2:
                    assetStatus = "停机";
                    break;
                case 3:
                    assetStatus = "限制功能使用";
                    break;
                case 4:
                    assetStatus = "待报废";
                    break;
                case 5:
                    assetStatus = "停用";
                    break;
                default:
                    break;
            }
        }

        return assetStatus;
    }

    public Boolean exportData(ExcelDocument doc, List<Map<String, Object>> data) {

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
