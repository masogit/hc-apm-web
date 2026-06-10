package com.ge.apm.service.wo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.WorkOrderReportMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.V2WorkOrderReportDto;
import com.ge.apm.domain.pure.WorkOrderExportPojo;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.common.I18nMessageService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.service.wo.doctemplate.PartsPictureTableRenderPolicy;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.date.DatePattern;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.google.common.base.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.internal.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.fop.fonts.type1.AdobeStandardEncoding.a;
import static org.apache.fop.fonts.type1.AdobeStandardEncoding.i;

/**
 * Created by 212605082 on 2017/7/25.
 */
@Component
@Scope("prototype")
public class WorkOrderExportService {

    private static final String SHEET_WORKORDER = "WorkOrder List";
    private static final String SHEET_PARTS = "Parts List";
    private static final String I18N_INT_EXT_TYPE = "intExtType";
    private static final String I18N_REPAIR_TYPE = "repairType";
    private static final String I18N_CURRENT_STEP_ = "woSteps";
    private static final String I18N_REPAIR_RESULT = "repairResult";
    private static final String I18N_HOUR_TYPE = "hoursEntryType";

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final Logger log = LoggerFactory.getLogger(WorkOrderExportService.class);
    @Autowired
    ServiceRequestRepository serviceRequestDao;
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
    @Autowired
    private TenantInfoRepository tenantInfoRepository;
    @Autowired
    private AuditGroupConfigurationDetailRepository auditGroupConfigurationDetailRepository;
    @Autowired
    private OrgInfoTreeService orgInfoTreeService;
    @Autowired
    private WorkOrderReportMapper worDao;
    @Autowired
    private ServiceRequestApiService serviceRequestApiService;
    @Autowired
    private MultiLevelApprovalRepository multiLevelApprovalRepository;
    @Autowired
    private V2_WorkOrderService woService;
    @Autowired
    private BlobObjectRepository blobObjectRepository;
    @Autowired
    private ExternalTempUserRepository externalTempUserDao;
    @Autowired
    private MicroServiceUtil microServiceUtil;

    @Autowired
    private MultiLevelApprovalConfigRepository multiLevelApprovalConfigRepository;

    @Autowired
    private I18nMessageService i18nMessageService;

    public List<Integer> getVisableMvAssetIds(OrgInfo siteOrg, OrgInfo clinicalDepte) {
        String token = UserContextService.getAccessToken();
        if (clinicalDepte != null) {
            return serviceRequestApiService.getVisableMvsAssetSystemIdByClinaicalDept(token, clinicalDepte.getUid());
        } else if (siteOrg != null) {
            return serviceRequestApiService.getVisableMvsAssetSystemIdBySite(token, siteOrg.getUid());
        } else {
            return serviceRequestApiService.getVisableMvsAssetSystemId(token);
        }
    }

    public List<Map<String, Object>> createPreviewWorkOrderData(V2WorkOrderReportDto v2WorkOrderReportDto) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        List<SearchFilter> searchFilters = new ArrayList<>();

        if (v2WorkOrderReportDto.getTenantUid() != null) {
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, v2WorkOrderReportDto.getTenantUid()));
        }
        if (v2WorkOrderReportDto.getSelectedOrg() != null && v2WorkOrderReportDto.getSelectedOrg().getOrgLevel() == 0) {
            SearchFilter tenantFilter = new SearchFilter("tenantUID", SearchFilter.Operator.EQ, v2WorkOrderReportDto.getSelectedOrg().getUid());
            searchFilters.add(tenantFilter);
        }
        if (v2WorkOrderReportDto.getSelectedOrg() != null && v2WorkOrderReportDto.getSelectedOrg().getOrgLevel() == 1) {
            SearchFilter institutionFilter = new SearchFilter("institutionUID", SearchFilter.Operator.EQ, v2WorkOrderReportDto.getSelectedOrg().getUid());
            searchFilters.add(institutionFilter);
        }
        if (v2WorkOrderReportDto.getSelectedOrg() != null && v2WorkOrderReportDto.getSelectedOrg().getOrgLevel() == 2) {
            SearchFilter hospitalFilter = new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, v2WorkOrderReportDto.getSelectedOrg().getUid());
            searchFilters.add(hospitalFilter);
        }
        if (v2WorkOrderReportDto.getSelectedOrg() != null && v2WorkOrderReportDto.getSelectedOrg().getOrgLevel() == 3) {
            SearchFilter siteFilter = new SearchFilter("siteUID", SearchFilter.Operator.EQ, v2WorkOrderReportDto.getSelectedOrg().getUid());
            searchFilters.add(siteFilter);
        }

        if (v2WorkOrderReportDto.getSelectedOrg() != null) {
            SearchFilter siteFilter = new SearchFilter("orgUID", SearchFilter.Operator.IN, v2WorkOrderReportDto.getSelectedOrg().getUid());
            searchFilters.add(siteFilter);
        } else if (v2WorkOrderReportDto.getSiteUid() != null) {
            searchFilters.add(new SearchFilter("orgUID", SearchFilter.Operator.IN, v2WorkOrderReportDto.getSiteUid()));
        }

        if (v2WorkOrderReportDto.getClinicalDept() != null) {
            List<OrgInfo> orgSearchList = new ArrayList<>();
            orgSearchList.add(v2WorkOrderReportDto.getClinicalDept());
            Set<OrgInfo> tempOrgSearchSet = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgSearchList, v2WorkOrderReportDto.getClinicalDeptList());
            List<String> clinicalDeptUIDList = tempOrgSearchSet.stream().map(OrgInfo::getUid).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, clinicalDeptUIDList));
        }
        if (v2WorkOrderReportDto.getAssetDept() != null) {
            List<OrgInfo> orgSearchList = new ArrayList<>();
            orgSearchList.add(v2WorkOrderReportDto.getAssetDept());
            Set<OrgInfo> tempOrgSearchSet = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgSearchList, v2WorkOrderReportDto.getClinicalDeptList());
            List<String> clinicalDeptUIDList = tempOrgSearchSet.stream().map(OrgInfo::getUid).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("assetInfo.assetDeptUID", SearchFilter.Operator.IN, clinicalDeptUIDList));
        }
        if (v2WorkOrderReportDto.getCurrentOwner() != null) {
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, v2WorkOrderReportDto.getCurrentOwner().getId()));
        }

        ZoneId zone = ZoneId.systemDefault();

        if (v2WorkOrderReportDto.getStartTime() != null) {
            searchFilters.add(new SearchFilter("requestTime", SearchFilter.Operator.GTE, v2WorkOrderReportDto.getStartTime()));
        }
        if (v2WorkOrderReportDto.getEndTime() != null) {
            LocalDate endDate = LocalDateTime.ofInstant(v2WorkOrderReportDto.getEndTime().toInstant(), zone).toLocalDate();
            searchFilters.add(new SearchFilter("requestTime", SearchFilter.Operator.LT, Date.from(endDate.plus(1, ChronoUnit.DAYS).atStartOfDay().atZone(zone).toInstant())));
        }
        if (v2WorkOrderReportDto.getClosedStartTime() != null) {
            searchFilters.add(new SearchFilter("closeTime", SearchFilter.Operator.GTE, v2WorkOrderReportDto.getClosedStartTime()));
        }
        if (v2WorkOrderReportDto.getClosedEndTime() != null) {
            LocalDate closeEndDate = LocalDateTime.ofInstant(v2WorkOrderReportDto.getClosedEndTime().toInstant(), zone).toLocalDate();
            searchFilters.add(new SearchFilter("closeTime", SearchFilter.Operator.LT, Date.from(closeEndDate.plus(1, ChronoUnit.DAYS).atStartOfDay().atZone(zone).toInstant())));
        }
        if (ObjectUtil.isNotEmpty(v2WorkOrderReportDto.getStatusList())) {
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.IN, v2WorkOrderReportDto.getStatusList()));
        }
        if (v2WorkOrderReportDto.getFilterParts() != null) {
            searchFilters.add(new SearchFilter("filterParts", SearchFilter.Operator.EQ, v2WorkOrderReportDto.getFilterParts()));
        }
        if (v2WorkOrderReportDto.getFilterTotalPrice() != null) {
            searchFilters.add(new SearchFilter("filterTotalPrice", SearchFilter.Operator.EQ, v2WorkOrderReportDto.getFilterTotalPrice()));
        }
        if (v2WorkOrderReportDto.getAssetInfoId() != null) {
            searchFilters.add(new SearchFilter("assetInfoId", SearchFilter.Operator.EQ, v2WorkOrderReportDto.getAssetInfoId()));
        }
        if (ObjectUtil.isNotEmpty(v2WorkOrderReportDto.getAssetTypeGroupEncodedSet())) {
            searchFilters.add(new SearchFilter("assetInfo.assetType", SearchFilter.Operator.IN, v2WorkOrderReportDto.getAssetTypeGroupEncodedSet()));
        }
        if (StringUtils.isNotEmpty(v2WorkOrderReportDto.getSrNum())) {
            searchFilters.add(new SearchFilter("srNum", SearchFilter.Operator.EQ, v2WorkOrderReportDto.getSrNum()));
        }
        List<WorkOrderExportPojo> workOrderList = worDao.workOrderExport(searchFilters);
        searchFilters.add(new SearchFilter("userUid", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid()));
        List<WorkOrderExportPojo> mvsWorkOrderList = worDao.mvsWorkOrderExport(searchFilters);
        workOrderList.addAll(mvsWorkOrderList);
//        List<Integer> assetIds = getVisableMvAssetIds(v2WorkOrderReportDto.getSelectedOrg(), v2WorkOrderReportDto.getClinicalDept());
//        if (!assetIds.isEmpty()) {
//            List<Integer> orderAssetIds = workOrderList.stream().map(WorkOrderExportPojo::getAssetId).collect(Collectors.toList());
//            List<Integer> mvsAssetIds = assetIds.stream().filter(assetId -> !orderAssetIds.contains(assetId)).collect(Collectors.toList());
//            searchFilters.add(new SearchFilter("assetIds", SearchFilter.Operator.IN, mvsAssetIds));
//            workOrderList.addAll(worDao.workOrderExportForMVS(searchFilters));
//        }
        List<I18nMessage> spare01 = i18nMessageService.getByMsgType("spare01");
        List<I18nMessage> spare02 = i18nMessageService.getByMsgType("spare02");
        List<I18nMessage> spare03 = i18nMessageService.getByMsgType("spare03");
        List<I18nMessage> spare04 = i18nMessageService.getByMsgType("spare04");
        List<I18nMessage> spare05 = i18nMessageService.getByMsgType("spare05");
        List<Integer> assetIds = mvsWorkOrderList.stream().map(WorkOrderExportPojo::getAssetId).collect(Collectors.toList());
        workOrderList = workOrderList.stream().filter(workOrderExportPojo -> {
            workOrderExportPojo.setMVSWorkOrder(assetIds.contains(workOrderExportPojo.getAssetId()));
            boolean isReturn = true;
            if (v2WorkOrderReportDto.getFilterMap().get("srNum") != null && v2WorkOrderReportDto.getFilterMap().get("srNum").length() > 0) {   //报修单号
                if (workOrderExportPojo.getSrNum() != null) {
                    isReturn = workOrderExportPojo.getSrNum().contains(v2WorkOrderReportDto.getFilterMap().get("srNum").trim());
                } else {
                    isReturn = false;
                }
            }
            if (isReturn && v2WorkOrderReportDto.getFilterMap().get("assetDepartNum") != null && v2WorkOrderReportDto.getFilterMap().get("assetDepartNum").length() > 0) {  //设备编号
                if (workOrderExportPojo.getAssetDepartNum() != null) {
                    isReturn = workOrderExportPojo.getAssetDepartNum().contains(v2WorkOrderReportDto.getFilterMap().get("assetDepartNum").trim());
                } else {
                    isReturn = false;
                }
            }
            if (isReturn && v2WorkOrderReportDto.getFilterMap().get("assetName") != null && v2WorkOrderReportDto.getFilterMap().get("assetName").length() > 0) {    //设备名称
                if (workOrderExportPojo.getAssetName() != null) {
                    isReturn = workOrderExportPojo.getAssetName().contains(v2WorkOrderReportDto.getFilterMap().get("assetName").trim());
                } else {
                    isReturn = false;
                }
            }
            if (isReturn && v2WorkOrderReportDto.getFilterMap().get("assetFunctionType") != null && v2WorkOrderReportDto.getFilterMap().get("assetFunctionType").length() > 0) {  //设备型号
                if (workOrderExportPojo.getAssetFunctionType() != null) {
                    isReturn = workOrderExportPojo.getAssetFunctionType().contains(v2WorkOrderReportDto.getFilterMap().get("assetFunctionType").trim());
                } else {
                    isReturn = false;
                }
            }
            if (isReturn && v2WorkOrderReportDto.getFilterMap().get("requestorName") != null && v2WorkOrderReportDto.getFilterMap().get("requestorName").length() > 0) {  //报修人
                if (workOrderExportPojo.getRequestorName() != null && v2WorkOrderReportDto.getFilterMap().get("requestorName") != null) {
                    isReturn = workOrderExportPojo.getRequestorName().contains(v2WorkOrderReportDto.getFilterMap().get("requestorName").trim());
                } else {
                    isReturn = false;
                }
            }
            if (isReturn && v2WorkOrderReportDto.getFilterMap().get("requestReason") != null && v2WorkOrderReportDto.getFilterMap().get("requestReason").length() > 0) {  //故障描述
                if (workOrderExportPojo.getRequestReason() != null) {
                    isReturn = workOrderExportPojo.getRequestReason().contains(v2WorkOrderReportDto.getFilterMap().get("requestReason").trim());
                } else {
                    isReturn = false;
                }
            }
            if (isReturn && v2WorkOrderReportDto.getCurrentStepId() != null) {  //当前步骤
                if (workOrderExportPojo.getCurrentStepId() != null) {
                    isReturn = Integer.parseInt(v2WorkOrderReportDto.getCurrentStepId()) == workOrderExportPojo.getCurrentStepId();
                } else {
                    isReturn = false;
                }
            }
            if (v2WorkOrderReportDto.getFilterMap().get("hospitalName") != null && v2WorkOrderReportDto.getFilterMap().get("hospitalName").length() > 0) {   //所属院区
                if (workOrderExportPojo.getSrNum() != null) {
                    isReturn = workOrderExportPojo.getHospitalName().contains(v2WorkOrderReportDto.getFilterMap().get("hospitalName").trim());
                } else {
                    isReturn = false;
                }
            }
            return isReturn;
        }).collect(Collectors.toList());

        Map<String, List<WorkOrderExportPojo>> srMap = workOrderList.stream().collect(Collectors.groupingBy(WorkOrderExportPojo::getSrId));
        for (WorkOrderExportPojo workOrderExportPojo : workOrderList) {
            Map<String, Object> workOrderMap = new HashedMap();

            workOrderMap.put("woId", workOrderExportPojo.getId());
            workOrderMap.put(WebUtil.getMessage("srNum_xlsx"), workOrderExportPojo.getSrNum() != null ? workOrderExportPojo.getSrNum() : "");
            workOrderMap.put(WebUtil.getMessage("EquipmentID_xlsx"), workOrderExportPojo.getAssetDepartNum());
            workOrderMap.put(WebUtil.getMessage("WorkorderStatus_xlsx"), workOrderExportPojo.getStatus() != null ? FieldValueMessageController.fieldValue("status", workOrderExportPojo.getStatus().toString()) : "");
            workOrderMap.put(WebUtil.getMessage("AssetID_xlsx"), workOrderExportPojo.getAssetFinancingNum());
            workOrderMap.put(WebUtil.getMessage("EquipmentSerialNumber_xlsx"), workOrderExportPojo.getAssetSerialNum());
            workOrderMap.put(WebUtil.getMessage("EquipmentName_xlsx"), workOrderExportPojo.getAssetName());
            workOrderMap.put(WebUtil.getMessage("Site"), workOrderExportPojo.getHospitalName());
            workOrderMap.put(WebUtil.getMessage("Department"), workOrderExportPojo.getAssetClinicalDeptName());
            workOrderMap.put(WebUtil.getMessage("Site"), workOrderExportPojo.getHospitalName());
            workOrderMap.put(WebUtil.getMessage("manufacture"), workOrderExportPojo.getAssetManufacture());
            workOrderMap.put(WebUtil.getMessage("brand"), workOrderExportPojo.getAssetMakerShortName());
            workOrderMap.put(WebUtil.getMessage("MaintenanceSubStatus"), getSubStatus(workOrderExportPojo.getSubStatus()));
            workOrderMap.put(WebUtil.getMessage("CurrentOwner_xlsx"), workOrderExportPojo.getCurrentPersonName());
            workOrderMap.put(WebUtil.getMessage("registrationNo"), workOrderExportPojo.getRegistrationNo());
            workOrderMap.put(WebUtil.getMessage("LatestDevelopments"), workOrderExportPojo.getComment());
            workOrderMap.put(WebUtil.getMessage("warrantyDate"), formatDate2(workOrderExportPojo.getWarrantyDate()));
            workOrderMap.put(WebUtil.getMessage("factoryWarrantyDate"), formatDate2(workOrderExportPojo.getFactoryWarrantyDate()));
            workOrderMap.put(WebUtil.getMessage("CloseTime_xlsx"), formatDate(workOrderExportPojo.getCloseTime()));
            workOrderMap.put(WebUtil.getMessage("feedbackRating"), workOrderExportPojo.getFeedbackRating()!=null&& workOrderExportPojo.getFeedbackRating()!=0?workOrderExportPojo.getFeedbackRating():null);
            workOrderMap.put("feedbackComment", workOrderExportPojo.getFeedbackComment());
            workOrderMap.put(WebUtil.getMessage("travelTime"), workOrderExportPojo.getTravelTime());
            Integer currentStepId = workOrderExportPojo.getCurrentStepId();
            if (UserContextService.hasCurrentTenantName("")) {
                if (currentStepId == null) {
                    workOrderMap.put(WebUtil.getMessage("CurrentStep_xlsx"), "");
                } else if (currentStepId == 7 || currentStepId == 9) {//云阜用户 已关单
                    currentStepId = 9;
                    workOrderMap.put(WebUtil.getMessage("CurrentStep_xlsx"), FieldValueMessageController.fieldValue(I18N_CURRENT_STEP_, currentStepId.toString()));
                } else if (currentStepId == 2 || currentStepId == 3) {//云阜用户 待接单
                    currentStepId = 3;
                    workOrderMap.put(WebUtil.getMessage("CurrentStep_xlsx"), FieldValueMessageController.fieldValue(I18N_CURRENT_STEP_, currentStepId.toString()));
                } else {
                    workOrderMap.put(WebUtil.getMessage("CurrentStep_xlsx"), FieldValueMessageController.fieldValue(I18N_CURRENT_STEP_, currentStepId.toString()));
                }
            } else {
                workOrderMap.put(WebUtil.getMessage("CurrentStep_xlsx"), currentStepId != null ? FieldValueMessageController.fieldValue(I18N_CURRENT_STEP_, currentStepId.toString()) : "");
            }

            workOrderMap.put("是否MVS工单", workOrderExportPojo.getMVSWorkOrder() ? "是" : "否");
            workOrderMap.put(WebUtil.getMessage("requestorId"), workOrderExportPojo.getRequestorName());
            workOrderMap.put(WebUtil.getMessage("RequestTime_xlsx"), formatDate(workOrderExportPojo.getRequestTime()));
            workOrderMap.put(WebUtil.getMessage("FaultDescription_xlsx"), workOrderExportPojo.getRequestReason());
            workOrderMap.put(WebUtil.getMessage("dispatchUserName"), workOrderExportPojo.getSendStepOwnerName());
            workOrderMap.put(WebUtil.getMessage("AssignTime_xlsx"), formatDate(workOrderExportPojo.getSendStepStartTime()));
            //workOrderMap.put("工单类型", workOrderExportPojo.getIntExtTypeName());
            workOrderMap.put(WebUtil.getMessage("WorkorderType_xlsx"), workOrderExportPojo.getIntExtType() != null ? FieldValueMessageController.fieldValue(I18N_INT_EXT_TYPE, workOrderExportPojo.getIntExtType().toString()) : "");
            //workOrderMap.put("维修类型", workOrderExportPojo.getRepairTypeName());
            workOrderMap.put(WebUtil.getMessage("RepairType_xlsx"), workOrderExportPojo.getRepairType() != null ? FieldValueMessageController.fieldValue(I18N_REPAIR_TYPE, workOrderExportPojo.getRepairType().toString()) : "");
            workOrderMap.put(WebUtil.getMessage("RepairResult"), workOrderExportPojo.getRepairResult() != null ? FieldValueMessageController.fieldValue(I18N_REPAIR_RESULT, workOrderExportPojo.getRepairResult().toString()) : "");
            workOrderMap.put(WebUtil.getMessage("Acceptedby_xlsx"), workOrderExportPojo.getAcceptStepOwnerName());
            workOrderMap.put(WebUtil.getMessage("AcceptTime_xlsx"), formatDate(workOrderExportPojo.getAcceptStepStartTime()));
            workOrderMap.put(WebUtil.getMessage("RepairCompleteTime_xlsx"), formatDate(workOrderExportPojo.getWorkStepEndTime()));
            workOrderMap.put(WebUtil.getMessage("CheckInTime_xlsx"), formatDate(workOrderExportPojo.getCheckinTime()));
            workOrderMap.put(WebUtil.getMessage("MaintenanceEngineerManhours_xlsx"), workOrderExportPojo.getManHours());
            workOrderMap.put(WebUtil.getMessage("TotalManhours_xlsx"), workOrderExportPojo.getTotalManHour());
            workOrderMap.put(WebUtil.getMessage("RepairPart_xlsx"), workOrderExportPojo.getParts());
            workOrderMap.put(WebUtil.getMessage("TotalPartPrice_xlsx"), workOrderExportPojo.getPartsTotalPrice());
            workOrderMap.put(WebUtil.getMessage("OtherExpenses_xlsx"), workOrderExportPojo.getOtherExpense());
            workOrderMap.put(WebUtil.getMessage("ExternalExpenses_xlsx"), workOrderExportPojo.getExternalExpense());
            workOrderMap.put("外部报价", workOrderExportPojo.getExternalQuotation());
            workOrderMap.put(WebUtil.getMessage("TotalRepairPrice_xlsx"), workOrderExportPojo.getTotalPrice());
            workOrderMap.put(WebUtil.getMessage("AcknowledgedBy_xlsx"), workOrderExportPojo.getEvaluateStepOwnerName());
            workOrderMap.put(WebUtil.getMessage("maintanenceValue_xlsx"), workOrderExportPojo.getMaintenanceValue());
            workOrderMap.put(WebUtil.getMessage("AcceptTime_xlsx2"), formatDate(workOrderExportPojo.getEvaluateStepEndTime()));
            workOrderMap.put(WebUtil.getMessage("Closedby_xlsx"), workOrderExportPojo.getCloseStepOwnerName());
            workOrderMap.put(WebUtil.getMessage("CloseTime_xlsx"), formatDate(workOrderExportPojo.getCloseStepEndTime()));
            workOrderMap.put(WebUtil.getMessage("Coworker"), workOrderExportPojo.getCoworker());
            workOrderMap.put(WebUtil.getMessage("CoworkerManhours_xlsx"), workOrderExportPojo.getCoworkManHour());
            workOrderMap.put(WebUtil.getMessage("FinalRating_xlsx"), workOrderExportPojo.getFeedbackRating());
            workOrderMap.put(WebUtil.getMessage("FaultType_xlsx"), workOrderExportPojo.getCaseTypeName());
            workOrderMap.put(WebUtil.getMessage("DiagnosticProcess_xlsx"), workOrderExportPojo.getDiagnosticProcess());
            workOrderMap.put(WebUtil.getMessage("ReasonDescription_xlsx"), workOrderExportPojo.getPatProblems());
            workOrderMap.put(WebUtil.getMessage("patActions"), workOrderExportPojo.getPatActions());
            workOrderMap.put(WebUtil.getMessage("patTests"), workOrderExportPojo.getPatTests());
            workOrderMap.put(WebUtil.getMessage("Remark"), workOrderExportPojo.getComment());
            workOrderMap.put(WebUtil.getMessage("QRCode_xlsx"), workOrderExportPojo.getAssetInfoQrCode());
            //workOrderMap.put("设备分类", workOrderExportPojo.getAssetGroupName());
            workOrderMap.put(WebUtil.getMessage("EquipmentClassification_xlsx"), workOrderExportPojo.getAssetGroup() != null ? FieldValueMessageController.fieldValue("assetGroup", workOrderExportPojo.getAssetGroup().toString()) : "");
            /*workOrderMap.put("医疗器械分类", workOrderExportPojo.getAssetFunctionGroupName());
            workOrderMap.put("医疗器械子分类", workOrderExportPojo.getAssetCode68Name());*/
            workOrderMap.put(WebUtil.getMessage("EquipmentModel_xlsx"), workOrderExportPojo.getAssetFunctionType());
            workOrderMap.put(WebUtil.getMessage("NearestWorkDays_xlsx"), (workOrderExportPojo.getNearestWorkDay() == null || workOrderExportPojo.getNearestWorkDay() < 0) ? "" : workOrderExportPojo.getNearestWorkDay());
            workOrderMap.put(WebUtil.getMessage("DownTime_xlsx"), formatDate(workOrderExportPojo.getConfirmedDownTime()));
            workOrderMap.put(WebUtil.getMessage("RecoveryTime_xlsx"), formatDate(workOrderExportPojo.getConfirmedUpTime()));
            workOrderMap.put(WebUtil.getMessage("RequestorTelephone_xlsx"), workOrderExportPojo.getTelephone());
            workOrderMap.put(WebUtil.getMessage("CurrentLocation_xlsx"), workOrderExportPojo.getLocationName());
            //workOrderMap.put("报修状态", workOrderExportPojo.getAssetStatusName());
            workOrderMap.put(WebUtil.getMessage("RequestStatus_xlsx"), workOrderExportPojo.getIbStatus() != null ? FieldValueMessageController.fieldValue("assetStatus", workOrderExportPojo.getIbStatus()) : "");
            workOrderMap.put(WebUtil.getMessage("WorkOrderTypeNew_xlsx"), workOrderExportPojo.getWorkOrderType() != null ? FieldValueMessageController.fieldValue("workOrderType", workOrderExportPojo.getWorkOrderType().toString()) : "");
            workOrderMap.put(WebUtil.getMessage("CoworkerEntryType_xlsx"), FieldValueMessageController.fieldValue(I18N_HOUR_TYPE, workOrderExportPojo.getCoworkerEntryType() == null ? "0" : workOrderExportPojo.getCoworkerEntryType().toString()));
            workOrderMap.put(WebUtil.getMessage("CoworkerEntryDetail_xlsx"), workOrderExportPojo.getCoworkerEntryDetail());

            workOrderMap.put("assetId", workOrderExportPojo.getAssetId());
            workOrderMap.put("assetUid", workOrderExportPojo.getAssetUid());

            if (workOrderExportPojo.getAssetFunctionGroup() != null) {
                workOrderMap.put(WebUtil.getMessage("MedicalDeviceClassification_xlsx"), workOrderExportPojo.getAssetFunctionGroup() != null ? FieldValueMessageController.fieldValue("assetFunctionType", workOrderExportPojo.getAssetFunctionGroup().toString()) : "");
                workOrderMap.put(WebUtil.getMessage("MedicalDeviceSubclassification_xlsx"), workOrderExportPojo.getAssetCode68() != null ? FieldValueMessageController.fieldValue(workOrderExportPojo.getAssetFunctionGroup().toString(), workOrderExportPojo.getAssetCode68().toString()) : "");
            } else {
                workOrderMap.put(WebUtil.getMessage("MedicalDeviceClassification_xlsx"), "");
                workOrderMap.put(WebUtil.getMessage("MedicalDeviceSubclassification_xlsx"), "");
            }

            workOrderMap.put("siebleNumber", workOrderExportPojo.getSiebleNumber());
            workOrderMap.put("callCenterNumber", workOrderExportPojo.getCallCenterNumber());

            workOrderMap.put(WebUtil.getMessage("entryType"), FieldValueMessageController.fieldValue(I18N_HOUR_TYPE, workOrderExportPojo.getEntryType() == null ? "0" : workOrderExportPojo.getEntryType().toString()));
            workOrderMap.put(WebUtil.getMessage("workHourDetail"), workOrderExportPojo.getWorkHourDetail());

            String attributesStr = "";
            if (!StringUtils.isEmpty(workOrderExportPojo.getAttributes())) {
                JSONArray attributesJSONArray = JSONObject.parseArray(workOrderExportPojo.getAttributes());
                for (int i = 0; i < attributesJSONArray.size(); i++) {
                    JSONObject attributesObject = attributesJSONArray.getJSONObject(i);
                    Object label = attributesObject.get("label").toString();
                    Object value = attributesObject.get("value").toString();
                    attributesStr += label + ":" + value + ";";
                }
            }
            workOrderMap.put(WebUtil.getMessage("Attribute"), attributesStr);

            if (workOrderExportPojo.getSignature() != null) {
                workOrderMap.put("signature", workOrderExportPojo.getSignature());
            }
            if (workOrderExportPojo.getAckSignature() != null) {
                workOrderMap.put("ack_signature", workOrderExportPojo.getAckSignature());
            }
            if (workOrderExportPojo.getSrId() != null) {
                workOrderMap.put("srId", workOrderExportPojo.getSrId());
            }
            if(workOrderExportPojo.getRiskGrade() != null){
                workOrderMap.put(WebUtil.getMessage("riskGrade"), FieldValueMessageController.fieldValue("riskGrade", workOrderExportPojo.getRiskGrade().toString()));
            }

            workOrderMap.put("spare01", findI18nValue(spare01, workOrderExportPojo.getSpare01()));
            workOrderMap.put("spare02", findI18nValue(spare02, workOrderExportPojo.getSpare02()));
            workOrderMap.put("spare03", findI18nValue(spare03, workOrderExportPojo.getSpare03()));
            workOrderMap.put("spare04", findI18nValue(spare04, workOrderExportPojo.getSpare04()));
            workOrderMap.put("spare05", findI18nValue(spare05, workOrderExportPojo.getSpare05()));
            workOrderMap.put("spare06", workOrderExportPojo.getSpare06());
            workOrderMap.put("spare07", workOrderExportPojo.getSpare07());
            workOrderMap.put("spare08", workOrderExportPojo.getSpare08());
            workOrderMap.put("spare09", workOrderExportPojo.getSpare09());
            workOrderMap.put("spare10", workOrderExportPojo.getSpare10());
            workOrderMap.put(WebUtil.getMessage("casePriority"), FieldValueMessageController.fieldValue("casePriority", workOrderExportPojo.getCasePriority().toString()));
            workOrderMap.put("main_sub", "sub".equals(workOrderExportPojo.getMainSub())?"子工单":"主工单");
            workOrderMap.put("related_order_number", Optional.of(srMap.get(workOrderExportPojo.getSrId()).size()-1).orElse(null));
            workOrderMap.put(WebUtil.getMessage("UseDeptName_xlsx"), workOrderExportPojo.getUseDeptName());

            resultList.add(workOrderMap);
        }
        return resultList;
    }
    private String findI18nValue(List<I18nMessage> messages, String key) {
        return messages == null || key == null ? "" :
                messages.stream()
                        .filter(ite -> key.equals(ite.getMsgKey()))
                        .findFirst()
                        .map(I18nMessage::getValueZh)
                        .orElse("");
    }
    public List<Map<String, Object>> supplementWorkOrder(List<Map<String, Object>> workOrderdata) {

        final List<String> woId = workOrderdata.stream()
                .map(i -> String.valueOf(i.get("woId"))).collect(Collectors.toList());

        final List<Integer> assetId = workOrderdata.stream()
                .map(i -> (Integer) i.get("assetId")).distinct().collect(Collectors.toList());

        final Map<Integer, AssetInfo> assetMap = assetInfoDao.findByIdIn(assetId).stream().collect(Collectors.toMap(AssetInfo::getId, i -> i));

        final List<MultiLevelApproval> multiLevelApprovals = multiLevelApprovalRepository.findByBusinessUidIn(woId);

        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        UserAccount userAccount = userContextService.getCurrentUserAccount();
        String tenantUID = userAccount.getTenantUID();
        TenantInfo tenantInfo = tenantInfoRepository.findByuid(tenantUID);
        String enableAuditGroupConfiguration = userContextService.getConfigItemValue("enableAuditGroupConfiguration");
        Map<String, List<V2_WorkOrder_Detail>> workOrderDetails = new HashMap<>();

        Map<String, String> partsSignatureMap = new HashMap<>();
        Map<String, String> medicalWorkerApproverSignatureMap = new HashMap<>();
//        if(WorkOrderConstant.DESIGNATED_USER.equals(tenantInfo.getName().trim())) {
        List<V2_WorkOrder_Detail> v2_workOrder_details = workOrderDetailDao.findByWoIdIn(woId);
        v2_workOrder_details.stream().forEach(i -> {
            workOrderDetails.putIfAbsent(i.getWoId(), new ArrayList<>());
            workOrderDetails.get(i.getWoId()).add(i);

            if (!Strings.isNullOrEmpty(i.getPartsSignature())) {
                partsSignatureMap.putIfAbsent(i.getWoId(), i.getPartsSignature());
            }
            if (!Strings.isNullOrEmpty(i.getMedicalWorkerApproverSignature())) {
                medicalWorkerApproverSignatureMap.putIfAbsent(i.getWoId(), i.getMedicalWorkerApproverSignature());
            }
        });
//        }

        final List<AuditGroupConfigurationDetail> approverSignatureList = auditGroupConfigurationDetailRepository.findByBusinessUidInOrderByApproveNumDesc(woId);
        Map<String, List<AuditGroupConfigurationDetail>> approverSignatureObjMap = new HashMap<>();
        approverSignatureList.stream().forEach(i -> {
            i.setApproveNum(Optional.ofNullable(i.getApproveNum()).orElse(0));
            approverSignatureObjMap.putIfAbsent(i.getBusinessUid(), new ArrayList<>());
            if (CollectionUtils.isEmpty(approverSignatureObjMap.get(i.getBusinessUid())) ||
                    approverSignatureObjMap.get(i.getBusinessUid()).stream().anyMatch(sign -> Objects.equals(sign.getApproveNum(), i.getApproveNum()))) {
                approverSignatureObjMap.get(i.getBusinessUid()).add(i);
            } else if (approverSignatureObjMap.get(i.getBusinessUid()).stream().anyMatch(sign -> sign.getApproveNum() < i.getApproveNum())) {
                approverSignatureObjMap.get(i.getBusinessUid()).clear();
                approverSignatureObjMap.get(i.getBusinessUid()).add(i);
            }
        });

        Map<String, List<MultiLevelApproval>> approvalsMap = new HashMap<>();
        multiLevelApprovals.stream().forEach(i -> {
            approvalsMap.putIfAbsent(i.getBusinessUid(), new ArrayList<>());
            approvalsMap.get(i.getBusinessUid()).add(i);
        });

        Map<String, List<MultiLevelApproval>> actualApprovalsMap = new HashMap<>();
        HashSet<String> configApproveUids = new HashSet<>();

        for (Map.Entry<String, List<MultiLevelApproval>> listEntry : approvalsMap.entrySet()) {
            List<MultiLevelApproval> value = listEntry.getValue();
            List<MultiLevelApproval> collect = value.stream().filter(item -> item.getApprovalStatus() == 2 || item.getApprovalStatus() == 1).collect(Collectors.toList());
            Optional<MultiLevelApproval> maxMultiLevelApproval = collect.stream().max(Comparator.comparing(MultiLevelApproval::getCreatedDate));
            if(maxMultiLevelApproval.isPresent()){
                MultiLevelApproval multiLevelApproval = maxMultiLevelApproval.get();
                Integer approvalLevelNow = multiLevelApproval.getApprovalLevelNow()==0?multiLevelApproval.getApprovalLevel():multiLevelApproval.getApprovalLevelNow();
                List<MultiLevelApproval> collect1 = collect.stream().filter(item -> item.getApprovalNumber().equals(multiLevelApproval.getApprovalNumber())  &&  item.getApprovalLevel()<approvalLevelNow   ).collect(Collectors.toList());
                List<MultiLevelApproval> collect2 = new ArrayList<>(collect1.stream().collect(Collectors.toMap(MultiLevelApproval::getApprovalLevel, Function.identity(), (o1, o2) -> o1.getCreatedDate().after(o2.getCreatedDate()) ? o1 : o2)).values());
                actualApprovalsMap.putIfAbsent(multiLevelApproval.getBusinessUid(), new ArrayList<>());
                collect2.add(multiLevelApproval);
                actualApprovalsMap.get(multiLevelApproval.getBusinessUid()).addAll(collect2);
                configApproveUids.add(multiLevelApproval.getWorkflowConfigApproveUid());
            }
        }
//        approvalsMap= actualApprovalsMap;
//        List<MultiLevelApprovalConfig> approvalConfigs = multiLevelApprovalConfigRepository.getByIds(new ArrayList<>(configApproveUids));
//        Map<String, MultiLevelApprovalConfig> configMap = approvalConfigs.stream().collect(Collectors.toMap(MultiLevelApprovalConfig::getId, i -> i));
//



        workOrderdata.stream().forEach(m -> {
            String woid = (String) m.get("woId");
            m.put(WebUtil.getMessage("OuterRepairOrgName"), Optional.ofNullable(externalTempUserDao.findByWoId(woid)).map(ExternalTempUser::getComName).orElse(null));
            m.put("拟更换配件", Optional.ofNullable(workOrderDetails.get(woid)).map(t -> t.stream().map(V2_WorkOrder_Detail::getParts).collect(Collectors.joining("、"))).orElse(null));
            m.put("设备价格", Optional.ofNullable(assetMap.get((Integer) m.get("assetId"))).map(a -> a.getPurchasePrice() == null ? a.getPurchasePrice() : new BigDecimal(a.getPurchasePrice())).orElse(null));
            m.put("采购日期", Optional.ofNullable(assetMap.get((Integer) m.get("assetId")))
                    .map(a -> a.getPurchaseDate()).orElse(null));
            m.put("auditSignatureList", Optional.ofNullable(actualApprovalsMap.get(woid))
                    .map(o -> o.stream().map(MultiLevelApproval::getAuditSignature).filter(ObjectUtil::isNotEmpty).collect(Collectors.toList()))
                    .orElse(Collections.emptyList()));
            m.put("auditOpinionList", Optional.ofNullable(actualApprovalsMap.get(woid))
                    .map(o -> o.stream().map(MultiLevelApproval::getApprovalOpinion).filter(ObjectUtil::isNotEmpty).collect(Collectors.toList()))
                    .orElse(Collections.emptyList()));
            m.put("auditDateList", Optional.ofNullable(actualApprovalsMap.get(woid))
                    .map(o -> o.stream().map(MultiLevelApproval::getActualApproverDate).filter(ObjectUtil::isNotEmpty).map(date -> DateUtil.format(date, DatePattern.NORM_DATETIME_PATTERN)).collect(Collectors.toList()))
                    .orElse(Collections.emptyList()));

            if (Boolean.TRUE.toString().equalsIgnoreCase(enableAuditGroupConfiguration)){
                m.put("partsSignature", partsSignatureMap.get(woid));
                m.put("medicalWorkerApproverSignature", medicalWorkerApproverSignatureMap.get(woid));
                m.put("approverSignatureList", Optional.ofNullable(approverSignatureObjMap.get(woid))
                        .map(o -> o.stream().map(AuditGroupConfigurationDetail::getApproverSignature).collect(Collectors.toList()))
                        .orElse(Collections.emptyList()));
            } else if (tenantInfo.getType() != null && tenantInfo.getType() == 6) {//县域新增
                m.put(WebUtil.getMessage("siebleNumber"), m.get("siebleNumber"));
                m.put(WebUtil.getMessage("callCenterNumber"), m.get("callCenterNumber"));
            }
            m.put("PartInformation", getPartsDetailMap(Optional.ofNullable(workOrderDetails.get(woid)).orElse(Collections.emptyList()),
                    (String) m.get(WebUtil.getMessage("srNum_xlsx")),
                    (String) m.get(WebUtil.getMessage("EquipmentName_xlsx")),
                    (String) m.get(WebUtil.getMessage("EquipmentID_xlsx"))));

            List<V2_BlobObject> pictureList = woService.getAttachByBoSubType((String) m.get("srId"), null); //微信端之前上传的图片没有 boSubType区分
            if (pictureList != null) {
                List<V2_BlobObject> serviceRequestPicture = new ArrayList<>();  //报修图片
                List<V2_BlobObject> workOrderPicture = new ArrayList<>();    //维修图片
                List<V2_BlobObject> partsPicture = new ArrayList<>();        //备件图片，目前只有mobile端可以上传图片
                serviceRequestPicture = pictureList.stream().filter(item -> "ServiceRequest".equals(item.getBoType())).collect(Collectors.toList());
                pictureList = woService.getAttachByBoSubType((String) m.get("srId"), V2_BlobObject.BO_SUB_TYPE_IMG);
                workOrderPicture = pictureList.stream().filter(item -> "WorkOrder".equals(item.getBoType())).collect(Collectors.toList());
                List<V2_WorkOrder_Detail> byWoId = workOrderDetailDao.findByWoId(woid);
                Set<String> ids = byWoId.stream().map(p -> p.getId()).collect(Collectors.toSet());
                List<V2_BlobObject> partPictures = null;
                if (!ids.isEmpty()) {
                    partPictures = blobObjectRepository.getPartPictures(ids);
                    partsPicture.addAll(partPictures);
                };
                m.put("serviceRequestPicture", serviceRequestPicture.stream().map(v2_blobObject -> v2_blobObject.getObjectStorageId()).collect(Collectors.toList()));
                m.put("workOrderPicture", workOrderPicture.stream().map(v2_blobObject -> v2_blobObject.getObjectStorageId()).collect(Collectors.toList()));
                m.put("partsPicture", partsPicture.stream().map(v2_blobObject -> v2_blobObject.getObjectStorageId()).collect(Collectors.toList()));
                m.put("parts_picture_table", getPartsPictureTable(byWoId,partPictures));
            }

            HashMap<String, String> approvalMap= new HashMap<>();
            List<MultiLevelApproval> multiLevelApprovals1 = actualApprovalsMap.get(woid);
            if(CollectionUtils.isNotEmpty(multiLevelApprovals1)){
                ArrayList<String> approvalMapKey = new ArrayList<>();
                for (MultiLevelApproval multiLevelApproval : multiLevelApprovals1) {
//                    MultiLevelApprovalConfig multiLevelApprovalConfig = configMap.get(multiLevelApproval.getWorkflowConfigApproveUid());
//                    String approvalItem = multiLevelApprovalConfig.getApprovalItem();
//                    List<ApprovalNode> approvalNodes = JSON.parseObject(approvalItem, new TypeReference<List<ApprovalNode>>(){});
//                    for (int i = 0; i < approvalNodes.size(); i++) {
//                        ApprovalNode node = approvalNodes.get(i);
//                        node.setNodeId(i + 1);
//                    }
//                    List<ApprovalNode> collect = approvalNodes.stream().filter(item -> Objects.equals(item.getNodeId(), multiLevelApproval.getApprovalLevel())).collect(Collectors.toList());
                    if(multiLevelApproval.getApprovalNodeStatus()==2){
                        int approvalLevel = multiLevelApproval.getApprovalLevel() - 1;
                        approvalMap.put("audit_signature_title"+approvalLevel,"审核人签名:");
                        approvalMap.put("audit_opinion_title"+approvalLevel,"备件审核意见:");
                        approvalMap.put("audit_date_title"+approvalLevel,"备件审核时间:");
                        approvalMap.put("auditSignatureList"+approvalLevel,multiLevelApproval.getAuditSignature());
                        approvalMap.put("auditOpinionList"+approvalLevel,multiLevelApproval.getApprovalOpinion());
                        approvalMap.put("auditDateList"+approvalLevel,DateUtil.format(multiLevelApproval.getActualApproverDate(), DatePattern.NORM_DATETIME_PATTERN));
                        approvalMapKey.add("auditSignatureList"+approvalLevel);
                    }



                }
                m.put("approvalMap",approvalMap);
                m.put("approvalMapKey",approvalMapKey);
            }


        });

        return workOrderdata;
    }
    private List<PartsPictureTableRenderPolicy.PartsPictureTable> getPartsPictureTable(List<V2_WorkOrder_Detail> workOrderDetails,
                                                                                       List<V2_BlobObject> blobObjects){
        if(CollectionUtils.isEmpty(workOrderDetails) || CollectionUtils.isEmpty(blobObjects)){
            return null;
        }
       return workOrderDetails.stream().map(item->{
            List<String> collect = blobObjects.stream().filter(blob -> blob.getBoUuid().equals(item.getId())).map(V2_BlobObject::getObjectStorageId).collect(Collectors.toList());
            PartsPictureTableRenderPolicy.PartsPictureTable partsPictureTable = new PartsPictureTableRenderPolicy.PartsPictureTable();
            partsPictureTable.setPartsName(item.getParts());
            partsPictureTable.setObjectId(collect);
            return partsPictureTable;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> createPartsData(List<Map<String, Object>> workOrderList) {

        List<Map<String, Object>> partList = new ArrayList<>();
        for (Map<String, Object> workOrderMap : workOrderList) {
            if (workOrderMap.containsKey("PartInformation")) {
                partList.addAll((List<Map<String, Object>>) workOrderMap.get("PartInformation"));
            }
        }
        return partList;
    }

    private List<Map<String, Object>> getPartsDetailMap(List<V2_WorkOrder_Detail> partsDetails, String srnum, String AssetName, String AssetNum) {
        List<Map<String, Object>> parts = new ArrayList<>();
        for (V2_WorkOrder_Detail workOrderDetail : partsDetails) {
            Map<String, Object> part = new HashedMap();
            part.put(WebUtil.getMessage("srNum_xlsx"), srnum);
            part.put(WebUtil.getMessage("AssetName_xlsx"), AssetName);
            part.put(WebUtil.getMessage("EquipmentID_xlsx"), AssetNum);
            part.put(WebUtil.getMessage("PartName_xlsx"), workOrderDetail.getParts());
            part.put(WebUtil.getMessage("ContractPrice_xlsx"), workOrderDetail.getClosedPrice());
            part.put(WebUtil.getMessage("Quantity"), workOrderDetail.getApartsQuantity());
            part.put(WebUtil.getMessage("PartType"), workOrderDetail.getPartsType() != null ? FieldValueMessageController.fieldValue("partsType", workOrderDetail.getPartsType()) : "");
            part.put(WebUtil.getMessage("SerialNumber_xlsx"), workOrderDetail.getSerialNum());
            part.put(WebUtil.getMessage("WarrantyDate_xlsx"), workOrderDetail.getWarrantyDate());
            part.put(WebUtil.getMessage("ContractNumber_xlsx"), workOrderDetail.getContractNum());
            part.put(WebUtil.getMessage("InvoiceNumber_xlsx"), workOrderDetail.getInvoiceNum());
            part.put(WebUtil.getMessage("InvoiceTime_xlsx"), formatDate(workOrderDetail.getInvoiceTime()));
            part.put(WebUtil.getMessage("EstimatedPrice_xlsx"), workOrderDetail.getPartsPrice());
            part.put(WebUtil.getMessage("Vendor"), workOrderDetail.getSupplier());
            part.put(WebUtil.getMessage("Remark"), workOrderDetail.getComment());
            part.put(WebUtil.getMessage("PartsPrice_xlsx"), workOrderDetail.getPartsPrice());
            if (workOrderDetail.getArrived() == null) {
                part.put(WebUtil.getMessage("Arrival"), null);
            } else {
                String arrived = workOrderDetail.getArrived().equals(Boolean.TRUE) ? WebUtil.getMessage("Arrival") : WebUtil.getMessage("NonArrived");
                part.put(WebUtil.getMessage("Arrival"), arrived);
            }
            parts.add(part);
        }
        return parts;
    }

    private String formatDate(Date date) {
        return date != null ? sdf.format(date) : "";
    }

    public String formatDate2(Date date) {
        return date != null ? new SimpleDateFormat("yyyy-MM-dd").format(date) : "";

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

    private String getSubStatus(Integer status) {
        String subStatus = null;

        if (status != null) {
            switch (status) {
                case 1:
                    subStatus = "等待备件";
                    break;
                case 2:
                    subStatus = "返厂维修-有备机";
                    break;
                case 3:
                    subStatus = "返厂维修-无备机";
                    break;
                default:
                    break;
            }
        }
        return subStatus;
    }
/*
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
    }*/

    public Boolean exportData(ExcelDocument doc, List<Map<String, Object>> workOrderData, List<Map<String, Object>> partsData) {

        Integer keyRow_workOrder = 1;
        Integer dataRow_workOrder = 2;

        for (Map<String, Object> item : workOrderData) {
            doc.writeRowData(SHEET_WORKORDER, keyRow_workOrder, dataRow_workOrder++, item);
        }

        Integer keyRow_parts = 1;
        Integer dataRow_parts = 2;
        for (Map<String, Object> item : partsData) {
            doc.writeRowData(SHEET_PARTS, keyRow_parts, dataRow_parts++, item);
        }

        return Boolean.TRUE;
    }

    public String getFileSearchUrl() {
        return microServiceUtil.getWebContextUrl() + "/gateway" + GatewayURL.SEARCH_FILE;
    }
/*

    //构建PageRequest
    private PageRequest buildPageRequest(Integer pageNumber, int pagzSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
        return new PageRequest(pageNumber, pagzSize, sort);
    }
*/


}
