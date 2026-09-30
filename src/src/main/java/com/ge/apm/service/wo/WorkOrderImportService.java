package com.ge.apm.service.wo;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;
import webapp.framework.web.service.UserContext;

import javax.faces.context.FacesContext;
import java.io.*;
import java.util.*;

@Service
public class WorkOrderImportService {

    @Autowired
    private AssetInfoRepository assetInfoRepository;
    @Autowired
    private WorkOrderReportRepository workOrderReportRepository;
    @Autowired
    private ServiceRequestApiService serviceRequestApiService;
    @Autowired
    private ServiceRequestRepository serviceRequestRepository;
    @Autowired
    private V2_WorkOrder_Step_Repository v2_workOrder_step_repository;
    @Autowired
    private AssetLifecycleEventRepository assetLifecycleEventRepository;
    @Autowired
    private I18nMessageRepository i18nMessageRepository;

    private Logger logger = LoggerFactory.getLogger(getClass());


    public StreamedContent getWorkOrderTemplate() throws IOException {
        DefaultStreamedContent ret= new DefaultStreamedContent(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/wo/woExport/" + "WorkOrderImport.xls"));
        ret.setName("WorkOrderImport.xls");
        ret.setContentType("application/vnd.ms-excel");
        return ret;
    }

    public List<ImportWorkOrderForm> importWorkOrder(UploadedFile file) throws IOException {
        List<ImportWorkOrderForm> result = new ArrayList<>();
        Workbook b = new HSSFWorkbook(file.getInputstream());
        Sheet sheet = b.getSheetAt(0);
        List<ImportWorkOrderForm> importWorkOrderForms = new ArrayList<>();
        for(Row row:sheet) {
            if(row.getRowNum()!=0 && row.getRowNum()!=1) {
                if(row==null) continue;
                ImportWorkOrderForm importWorkOrderForm = new ImportWorkOrderForm();
                try {
                    boolean departNumNull = false;
                    boolean financingNumNull = false;
                    for (int i = 0; i < 14; i++) {   //字段数14
                        Cell cell = row.getCell(i);
                        if (cell == null) {
                            if (i == 1) financingNumNull = true;
                            if (i == 2) departNumNull = true;
                            if(financingNumNull && departNumNull) {
                                result.add(importWorkOrderForm);
                                continue;
                            }
                        }
                        switch (cell.getColumnIndex()) {
                            case 0: importWorkOrderForm.setAssetName(cell.getStringCellValue());break;
                            case 1: importWorkOrderForm.setFinancingNum(cell.getStringCellValue());break;
                            case 2: importWorkOrderForm.setDepartNum(cell.getStringCellValue());break;
                            case 3: importWorkOrderForm.setSrNum(cell.getStringCellValue());break;
                            case 4: importWorkOrderForm.setCasePriority(cell.getStringCellValue().equals("紧急") ? "1" : cell.getStringCellValue().equals("重要") ? "2" : cell.getStringCellValue().equals("一般") ? "3" : "4");break;
                            case 5: importWorkOrderForm.setRequestReason(cell.getStringCellValue());break;
                            case 6: importWorkOrderForm.setRequestTime(cell.getDateCellValue());break;
                            case 7: importWorkOrderForm.setRepairType(cell.getStringCellValue().equals("上门维修") ? "1" : cell.getStringCellValue().equals("送到维修中心") ? "2" : cell.getStringCellValue().equals("返厂维修") ? "3" : "4");break;
                            case 8: importWorkOrderForm.setConfirmedUpDate(cell.getDateCellValue());break;
                            case 9: importWorkOrderForm.setAssetStatus(cell.getStringCellValue().equals("正常") ? "1" : cell.getStringCellValue().equals("停机") ? "2" : cell.getStringCellValue().equals("限制功能使用") ? "3" : "4");break;
                            case 10: importWorkOrderForm.setRepairResult(cell.getStringCellValue().equals("完全修复") ? "1" : cell.getStringCellValue().equals("部分修复") ? "2" : cell.getStringCellValue().equals("无法修复") ? "3" : cell.getStringCellValue().equals("需外送修复") ? "4" : cell.getStringCellValue().equals("误报") ? "5" : "6");break;
                            case 11: importWorkOrderForm.setCaseType(cell.getStringCellValue().equals("自然故障") ? "1" : cell.getStringCellValue().equals("人为故障") ? "2" : cell.getStringCellValue().equals("环境故障") ? "3" : cell.getStringCellValue().equals("设计故障") ? "4" : cell.getStringCellValue().equals("其它") ? "5" : "6");break;
                            case 12: importWorkOrderForm.setManhours(cell.getStringCellValue());break;
                            case 13: importWorkOrderForm.setCloseTime(cell.getDateCellValue());break;
                        }

                    }
                    importWorkOrderForms.add(importWorkOrderForm);
                } catch (Exception exception) {
                    result.add(importWorkOrderForm);
                    continue; //可能是时间格式不一样导致的报错
                }
            }

        }
        return importWorkOrderByForm(importWorkOrderForms,result);
    }

    @Transactional
    public List<ImportWorkOrderForm> importWorkOrderByForm(List<ImportWorkOrderForm> importWorkOrderForms,List<ImportWorkOrderForm> result) {
        UserAccount userAccount = UserContext.getCurrentLoginUser();
        for(ImportWorkOrderForm importWorkOrderForm : importWorkOrderForms) {
            AssetInfo assetInfo = null;
            List<AssetInfo> assetInfos = null;
            if(importWorkOrderForm.getDepartNum()!=null && importWorkOrderForm.getFinancingNum()!=null) {
                assetInfos = assetInfoRepository.findByDepartNumAndFinancingNum(importWorkOrderForm.getDepartNum(),importWorkOrderForm.getFinancingNum());
            } else if(importWorkOrderForm.getFinancingNum()!=null) {
                assetInfos = assetInfoRepository.findByFinancingNum(importWorkOrderForm.getFinancingNum(),userAccount.getSiteUID());
            } else if(importWorkOrderForm.getDepartNum()!=null) {
                assetInfos = assetInfoRepository.findByDepartNum(importWorkOrderForm.getDepartNum());
            } else {
                result.add(importWorkOrderForm);continue;
            }
            if(assetInfos.size()!=1) { result.add(importWorkOrderForm); continue;}
            assetInfo = assetInfos.get(0);
            boolean isContinue = false;
            List<V2_WorkOrder> findByIds = workOrderReportRepository.findByAssetId(assetInfo.getId());
            for(V2_WorkOrder workOrder:findByIds) {
                if(importWorkOrderForm.getSrNum().equals(workOrder.getServiceRequest().getOriginSrNum())) isContinue = true;   //申请编号重复，说明已经导入过了该工单
            };
            if(isContinue) {result.add(importWorkOrderForm); continue;}
            V2_ServiceRequest serviceRequest = new V2_ServiceRequest();
            serviceRequest.setCasePriority(Integer.parseInt(importWorkOrderForm.getCasePriority()));
            serviceRequest.setRequestReason(importWorkOrderForm.getRequestReason());
            serviceRequest.setStatus(2);     //为什么关单时候设置成2 ？
            serviceRequest.setRequestorId(userAccount.getId());
            serviceRequest.setRequestorName(userAccount.getName());
            serviceRequest.setRequestTime(importWorkOrderForm.getRequestTime());
            serviceRequest.setConfirmedDownTime(importWorkOrderForm.getRequestTime());
            serviceRequest.setConfirmedUpTime(importWorkOrderForm.getCloseTime());  //恢复时间
/*            serviceRequest.setCreatedBy(userAccount.getLoginName());
            serviceRequest.setLastModifiedBy(userAccount.getName());
            serviceRequest.setLastModifiedDate(ZonedDateTime.ofInstant(importWorkOrderForm.getCloseTime().toInstant(), ZoneId.systemDefault()));*/
            serviceRequest.setAssetLocation(assetInfo.getClinicalDeptName());  //当前位置
            serviceRequest.setSiteId(assetInfo.getSiteId());
            serviceRequest.setSiteUID(assetInfo.getSiteUID());
            serviceRequest.setInstitutionUID(assetInfo.getInstitutionUID());
            serviceRequest.setTenantUID(assetInfo.getTenantUID());
            serviceRequest.setHospitalId(assetInfo.getHospitalId());
            serviceRequest.setHospitalUID(assetInfo.getHospitalUID());
            serviceRequest.setHospitalName(assetInfo.getHospital().getName());
            serviceRequest.setAssetId(assetInfo.getId());
            serviceRequest.setAssetName(assetInfo.getName());
            serviceRequest.setFromDeptId(assetInfo.getClinicalDeptId());
            serviceRequest.setFromDeptUID(assetInfo.getClinicalDeptUID());
            serviceRequest.setFromDeptName(assetInfo.getClinicalDeptName());
            serviceRequest.setSrRepairType("2");  //暂时不知道用处
            serviceRequest.setReportSource("1");
            serviceRequest.setOriginSrNum(importWorkOrderForm.getSrNum());
            String srNUm = serviceRequestApiService.getSrNum("work");
            serviceRequest.setSrNum(srNUm);
            serviceRequest.setEstimatedCloseTime(importWorkOrderForm.getCloseTime());
            serviceRequest.setCloseTime(importWorkOrderForm.getCloseTime());
            serviceRequestRepository.save(serviceRequest);

            V2_WorkOrder workOrder = new V2_WorkOrder();
            workOrder.setSiteId(assetInfo.getSiteId());
            workOrder.setSiteUID(assetInfo.getSiteUID());
            workOrder.setTenantUID(assetInfo.getTenantUID());
            workOrder.setInstitutionUID(assetInfo.getInstitutionUID());
            workOrder.setHospitalId(assetInfo.getHospitalId());
            workOrder.setHospitalUID(assetInfo.getHospitalUID());
            workOrder.setAssetId(assetInfo.getId());
            workOrder.setTotalManHour(Double.parseDouble(importWorkOrderForm.getManhours())>0.01 ? Double.parseDouble(importWorkOrderForm.getManhours()) : 0.01);
            workOrder.setTotalPrice(0.0);
            workOrder.setManHours(0.0);
            workOrder.setOtherExpense(0.0);
            workOrder.setExternalExpense(0.0);
            workOrder.setStatus(2);
            workOrder.setFeedbackRating(0);
            workOrder.setIntExtType(1);
            workOrder.setRepairType(1);
            workOrder.setApprovalStatus("0");
            workOrder.setSrId(serviceRequest.getId());
            workOrder.setComment(importWorkOrderForm.getRequestReason());
            workOrder.setCurrentStepId(9);
            workOrder.setCurrentStepName("已关单");
            workOrder.setCurrentPersonId(userAccount.getId());
            workOrder.setCurrentPersonName(userAccount.getName());
            workOrder.setDispatchMode(1);
            workOrder.setRepairType(Integer.parseInt(importWorkOrderForm.getRepairType()));
            workOrder.setAcceptStepOwnerId(userAccount.getId());
            workOrder.setAcceptStepOwnerName(userAccount.getName());
            workOrder.setAcceptStepStartTime(importWorkOrderForm.getCloseTime());  //接单时间，是否让输入？
            workOrder.setAckId(userAccount.getId());
            workOrder.setAckName(userAccount.getName());
/*            workOrder.setPatActions("解决方案");
            workOrder.setPatProblems("原因描述");
            workOrder.setPatTests("交付前检测");*/
            workOrder.setCaseType(Integer.parseInt(importWorkOrderForm.getCaseType()));
            workOrder.setRepairResult(importWorkOrderForm.getRepairResult());
            workOrder.setCloseTime(importWorkOrderForm.getCloseTime());
            workOrder.setCreatedDate(importWorkOrderForm.getRequestTime());// 计算响应时间，暂时省略
            workOrder.setImportBy(userAccount.getUid());
            workOrderReportRepository.save(workOrder);
            V2_WorkOrder_Step workOrder_step = new V2_WorkOrder_Step();
            workOrder_step.setOwnerId(userAccount.getId());
            workOrder_step.setOwnerName(userAccount.getName());
            workOrder_step.setStartTime(importWorkOrderForm.getCloseTime());
            workOrder_step.setEndTime(importWorkOrderForm.getCloseTime());
            workOrder_step.setWoId(workOrder.getId());
            workOrder_step.setStepId(6);
            workOrder_step.setStepName("待关单");
            workOrder_step.setTenantUID(assetInfo.getTenantUID());
            workOrder_step.setInstitutionUID(assetInfo.getInstitutionUID());
            workOrder_step.setHospitalUID(assetInfo.getHospitalUID());
            workOrder_step.setSiteUID(assetInfo.getSiteUID());
            workOrder_step.setEndTime(importWorkOrderForm.getCloseTime());
            v2_workOrder_step_repository.save(workOrder_step);
            AssetLifecycleEvent assetLifecycleEvent = new AssetLifecycleEvent();
            assetLifecycleEvent.setAssetId(assetInfo.getId());
            assetLifecycleEvent.setAssetUid(assetInfo.getUid());
            assetLifecycleEvent.setAssetName(assetInfo.getName());
            assetLifecycleEvent.setEventType(1);  //维修1 保养2
            assetLifecycleEvent.setEventUid(workOrder.getId());
            assetLifecycleEvent.setStatus(workOrder.getStatus());
            assetLifecycleEvent.setCreateTime(importWorkOrderForm.getRequestTime());
            assetLifecycleEvent.setScheduleDate(importWorkOrderForm.getCloseTime());
            assetLifecycleEvent.setDescription(getDescriptionByWorkOrder(workOrder,serviceRequest));
            assetLifecycleEventRepository.save(assetLifecycleEvent);
        }
        return result;
    }

    public String getDescriptionByWorkOrder(V2_WorkOrder workOrder,V2_ServiceRequest serviceRequest) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", workOrder.getCurrentStepId());
        if (Objects.equals(workOrder.getStatus(), 1)) {
            map.put("title", workOrder.getCurrentStepName());
            map.put("messages", Arrays.asList("报修编号：" + serviceRequest.getSrNum()));
        } else if (Objects.equals(workOrder.getStatus(), 3)) {
            map.put("title", "已取消");
            map.put("messages", Arrays.asList("报修编号：" + serviceRequest.getSrNum()));
        } else {
            I18nMessage i18n = workOrder.getCaseType()==null ? null: i18nMessageRepository.getByMsgTypeAndMsgKeyAndSiteId("faultType", workOrder.getCaseType().toString(), -1);
            map.put("title", "已完成");
            map.put(
                    "messages",
                    Arrays.asList(
                            "报修编号：" + serviceRequest.getSrNum(),
                            "故障类别：" + (i18n == null ? "" : i18n.getValueZh()),
                            "解决方案：" + (workOrder.getPatActions() == null ? "" : workOrder.getPatActions())));
        }
        return JSON.toJSONString(map);
    }

}
