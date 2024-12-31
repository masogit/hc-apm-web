package com.ge.apm.service.wo;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.AssetLifecycleEventRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.V2_PmOrderRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.constant.PmConstant;
import com.ge.apm.view.sysutil.FileDownloadService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;
import webapp.framework.web.service.UserContext;

import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PmOrderImportService {
    @Autowired
    private AssetInfoRepository assetInfoRepository;
    @Autowired
    private AssetLifecycleEventRepository assetLifecycleEventRepository;
    @Autowired
    private V2_PmOrderRepository pmOrderRepository;
    @Autowired
    private ServiceRequestApiService serviceRequestApiService;
    @Autowired
    private I18nMessageRepository i18nMessageRepository;

    public StreamedContent getWorkOrderTemplate() throws IOException {
//        FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/pm/pmOrderReportTemplate/" + "PmOrderImport.xls")
        FileDownloadService fileDownloadService = WebUtil.getBean(FileDownloadService.class);
        InputStream inputStream = fileDownloadService.getTemplateFileInputStream(SystemTemplate.SystemTemplateEnum.PM_ORDER_IMPORT.getCode(), SystemTemplate.SystemTemplateEnum.PM_ORDER_IMPORT.getFileType());
        DefaultStreamedContent ret= new DefaultStreamedContent(inputStream);
        ret.setName("PmOrderImport.xls");
        ret.setContentType("application/vnd.ms-excel");
        return ret;
    }

    public List<ImportPmOrderForm> importWorkOrder(UploadedFile file) throws IOException {
        List<ImportPmOrderForm> result = new ArrayList<>();
        Workbook b = new HSSFWorkbook(file.getInputstream());
        Sheet sheet = b.getSheetAt(0);
        List<ImportPmOrderForm> importPmOrderForms = new ArrayList<>();
        for(Row row:sheet) {
            if(row==null) continue;
            if(row.getRowNum()!=0 && row.getRowNum()!=1) {
                ImportPmOrderForm importPmOrderForm = new ImportPmOrderForm();
                boolean departNumNull = false;
                boolean financingNumNull = false;
                try {
                    for (int i = 0;i<9;i++) {   //字段数14
                        Cell cell = row.getCell(i);
                        if(cell==null) {
                            if(i==1) financingNumNull = true;
                            if(i==2) departNumNull = true;
                            if(financingNumNull && departNumNull) {
                                result.add(importPmOrderForm);
                                continue;
                            }
                        }
                        switch (cell.getColumnIndex()) {
                            case 0: importPmOrderForm.setAssetName(cell.getStringCellValue());break;
                            case 1: importPmOrderForm.setFinancingNum(cell.getStringCellValue());break;
                            case 2: importPmOrderForm.setDepartNum(cell.getStringCellValue());break;
                            case 3: importPmOrderForm.setPmNum(cell.getStringCellValue());break;
                            case 4: importPmOrderForm.setPmType(cell.getStringCellValue().equals("周期性")?"1":cell.getStringCellValue().equals("一次性")? "2":"3");break;
                            case 5: importPmOrderForm.setRuleLevel(cell.getStringCellValue().equals("2级规则")?"2":"3");break;
                            case 6: importPmOrderForm.setManhours(cell.getStringCellValue());break;
                            case 7: importPmOrderForm.setRequestTime(cell.getDateCellValue());break;
                            case 8: importPmOrderForm.setCloseTime(cell.getDateCellValue());break;
                        }

                    }
                    importPmOrderForms.add(importPmOrderForm);
                } catch (Exception exception) {
                    result.add(importPmOrderForm);
                    continue; //可能是时间格式不一样导致的报错
                }

            }
        }
        return importPmOrderForms(importPmOrderForms,result);
    }

    @Transactional
    public List<ImportPmOrderForm> importPmOrderForms(List<ImportPmOrderForm> importPmOrderForms,List<ImportPmOrderForm> result) {
        UserAccount userAccount = UserContext.getCurrentLoginUser();
        for(ImportPmOrderForm importPmOrderForm : importPmOrderForms) {
            AssetInfo assetInfo = null;
            List<AssetInfo> assetInfos = null;
            if(importPmOrderForm.getDepartNum()!=null && importPmOrderForm.getFinancingNum()!=null) {
                assetInfos = assetInfoRepository.findByDepartNumAndFinancingNum(importPmOrderForm.getDepartNum(),importPmOrderForm.getFinancingNum());
            } else if(importPmOrderForm.getFinancingNum()!=null) {
                assetInfos = assetInfoRepository.findByFinancingNum(importPmOrderForm.getFinancingNum(),userAccount.getSiteUID());
            } else if(importPmOrderForm.getDepartNum()!=null) {
                assetInfos = assetInfoRepository.findByDepartNum(importPmOrderForm.getDepartNum());
            } else {
                result.add(importPmOrderForm);continue;
            }
            if(assetInfos.size()!=1) {
                result.add(importPmOrderForm); continue;
            }
            assetInfo = assetInfos.get(0);
            if(pmOrderRepository.findByOriginPmNumAndSiteUID(importPmOrderForm.getPmNum(),assetInfo.getSiteUID()).size()>0) {
                result.add(importPmOrderForm); continue;
            }
            V2_PmOrder pmOrder = new V2_PmOrder();
            pmOrder.setTenantUID(assetInfo.getTenantUID());
            pmOrder.setInstitutionUID(assetInfo.getInstitutionUID());
            pmOrder.setHospitalUID(assetInfo.getHospitalUID());
            pmOrder.setSiteUID(assetInfo.getSiteUID());
            pmOrder.setAssetId(assetInfo.getId());
            pmOrder.setAssetUid(assetInfo.getUid());
            pmOrder.setAssetName(assetInfo.getName());
            pmOrder.setCreatorId(userAccount.getUid());
            pmOrder.setCreatorName(userAccount.getName());
            pmOrder.setOwnerId(userAccount.getUid());
            pmOrder.setOwnerName(userAccount.getName());
            pmOrder.setCurrentPersonId(userAccount.getUid());
            pmOrder.setCurrentPersonName(userAccount.getName());
            pmOrder.setImportBy(userAccount.getUid());
            pmOrder.setCreateTime(importPmOrderForm.getRequestTime());
            pmOrder.setFinished(true);
            pmOrder.setStartTime(importPmOrderForm.getRequestTime());
            pmOrder.setEndTime(importPmOrderForm.getCloseTime());
            pmOrder.setPlanTime(importPmOrderForm.getCloseTime());  //计划时间，暂时设置成关单时间
            pmOrder.setOriginPlanTime(importPmOrderForm.getCloseTime());  //暂时设为关单时间
            pmOrder.setTotalPrice(0.0);
            pmOrder.setTotalManHour(0.0);
            pmOrder.setClinicalDeptId(assetInfo.getClinicalDeptUID());
            pmOrder.setIntExtType(1); // 1:内修 2:外修 3:内转外
            pmOrder.setPmType(Integer.parseInt(importPmOrderForm.getPmType())); //保养类型
            pmOrder.setCurrentStepId("Closed");
            pmOrder.setCurrentStepName("已关单");
            pmOrder.setManHours(Double.parseDouble(importPmOrderForm.getManhours()));
            pmOrder.setTotalManHour(Double.parseDouble(importPmOrderForm.getManhours()));
            pmOrder.setSimplify(false);  //一下几个需要从workflowConfig 里面获取
            pmOrder.setAutoAckMaintenance(true);
            String pmNUm = serviceRequestApiService.getSrNum("pm");
            pmOrder.setPmNum(pmNUm);
            pmOrder.setOriginPmNum(importPmOrderForm.getPmNum());
            pmOrderRepository.save(pmOrder);
            AssetLifecycleEvent assetLifecycleEvent = new AssetLifecycleEvent();
            assetLifecycleEvent.setAssetId(assetInfo.getId());
            assetLifecycleEvent.setAssetUid(assetInfo.getUid());
            assetLifecycleEvent.setAssetName(assetInfo.getName());
            assetLifecycleEvent.setEventType(2);  //维修1 保养2
            assetLifecycleEvent.setEventUid(pmOrder.getId());
            assetLifecycleEvent.setStatus(assetInfo.getStatus());
            assetLifecycleEvent.setCreateTime(importPmOrderForm.getRequestTime());
            assetLifecycleEvent.setScheduleDate(importPmOrderForm.getCloseTime());
            pmOrder.setRuleLevel(Integer.parseInt(importPmOrderForm.getRuleLevel()));
            assetLifecycleEvent.setDescription(getDescriptionByWorkOrder(pmOrder));
            assetLifecycleEventRepository.save(assetLifecycleEvent);
        }
        return result;
    }

    public String getDescriptionByWorkOrder(V2_PmOrder pmOrder) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Map<String,Object> map = new HashMap<>();
        map.put("title", pmOrder.getCurrentStepName());
        map.put("status", pmOrder.getCurrentStepId());
        map.put(
                "messages",
                Arrays.asList(
                        "保养编号：" + pmOrder.getPmNum(),
                        "保养等级：" + pmOrder.getRuleLevel() + "级保养",
                        "保养类型："
                                + i18nMessageRepository
                                .findByMsgTypeAndMsgKey(
                                        "pmType", pmOrder.getPmType() + "", pmOrder.getTenantUID())
                                .getValueZh(),
                        "计划时间:" + format.format(pmOrder.getPlanTime()))
        );
        return JSON.toJSONString(map);
    }
}
