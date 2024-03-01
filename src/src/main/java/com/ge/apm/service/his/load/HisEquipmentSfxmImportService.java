package com.ge.apm.service.his.load;

import com.ge.apm.dao.HisEquipmentSfxmRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.his.HisEquipmentConnect;
import com.ge.apm.domain.his.HisEquipmentSfxm;
import com.ge.apm.service.asset.ContractRow;
import com.ge.apm.service.his.HisEquipmentRow;
import com.ge.apm.service.his.HisEquipmentSfxmRow;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HisEquipmentSfxmImportService {

    private static final Logger logger = LoggerFactory.getLogger(HisEquipmentSfxmImportService.class);

    public static final String SHEET_SERVICE_CONTRACT = "HisEquipmentSfxm List";
    public static final int EXCEL_CONTRACT_HEADER_ROW = 1;
    public static final int EXCEL_CONTRACT_DATA_ROW = 2;
    @Autowired
    private HisEquipmentSfxmRepository hisEquipmentSfxmRepository;

    @Autowired
    private OrgInfoRepository orgInfoDao;

    public List<HisEquipmentSfxm> parse(ExcelDocument doc){

        List<HisEquipmentSfxm> resultList = new ArrayList<>();

        int keyRow = 1;
        int dataRow = 2;
        for (Map< String, Object> rowMap = doc.getDataRowMap(keyRow, dataRow); rowMap != null; rowMap = doc.getDataRowMap(keyRow, ++dataRow)) {
            String id = ExcelDocument.getCellStringValue(rowMap.get("id"));
            String sfxmId = ExcelDocument.getCellStringValue(rowMap.get("sfxm_id"));
            String sfxmName = ExcelDocument.getCellStringValue(rowMap.get("sfxm_name"));
            String sfxmLb = ExcelDocument.getCellStringValue(rowMap.get("sfxm_lb"));
            String hosId = ExcelDocument.getCellStringValue(rowMap.get("hos_id"));
//            String isMatched = ExcelDocument.getCellStringValue(rowMap.get("is_matched"));

            if (null == id) {
                break;
            }

            HisEquipmentSfxm data = new HisEquipmentSfxm();
            data.setId(id);
            data.setSfxmId(sfxmId);
            data.setSfxmName(sfxmName);
            data.setSfxmLb(sfxmLb);
            data.setHosId(hosId);
            data.setIsMatched(false);

            resultList.add(data);
        }

        return resultList;
    }

    public List<HisEquipmentSfxm> saveAll(List<HisEquipmentSfxm> data){
        Iterable<HisEquipmentSfxm> iterable = hisEquipmentSfxmRepository.save(data);

        return Lists.newArrayList(iterable);
    }

    public List<Map<String, Object>> getMapFromTemplate(ExcelDocument doc) {

        List<Map<String, Object>> result = new ArrayList<>();

        //for (Map< String, Object> rowMap = doc.getDataRowMap(SHEET_SERVICE_CONTRACT, keyRow, dataRow); rowMap.size() > 0 ; rowMap = doc.getDataRowMap(SHEET_SERVICE_CONTRACT, keyRow, ++dataRow)) {
        for (int i = EXCEL_CONTRACT_DATA_ROW ; i <= doc.getWorkBook().getSheet(SHEET_SERVICE_CONTRACT).getLastRowNum() + 1 ; i++) {

            Map< String, Object> rowMap = doc.getDataRowMap(SHEET_SERVICE_CONTRACT, EXCEL_CONTRACT_HEADER_ROW, i);

            Map<String, Object> map = new LinkedHashMap();
            HisEquipmentSfxm hisEquipmentSfxm = new HisEquipmentSfxm();


            hisEquipmentSfxm.setLineNum(i);

            HisEquipmentSfxmRow hisEquipmentRow = new HisEquipmentSfxmRow(hisEquipmentSfxm, rowMap);
            hisEquipmentRow.setFieldStringValue("His收费项目代码", "setSfxmId");
            hisEquipmentRow.setFieldStringValue("His收费项目名称", "setSfxmName");
            hisEquipmentRow.setFieldStringValue("收费项目分类", "setSfxmLb");
            hisEquipmentRow.setFieldStringValue("医院机构代码", "setHosId");
//            hisEquipmentRow.setFieldStringValue(WebUtil.getMessage("EqId"), "setEqId");
//            hisEquipmentRow.setFieldStringValue(WebUtil.getMessage("EquipmentName"), "setEqName");
//            hisEquipmentRow.setFieldStringValue(WebUtil.getMessage("PresetValue"), "setEqLb");
//            hisEquipmentRow.setFieldStringValue(WebUtil.getMessage("HospitalCode"), "setEqHosid");
            map.put("hisEquipmentSfxm", hisEquipmentSfxm);
            map.put("rowData", rowMap);
            map.put("status",  HisEquipmentImportService.ImportStatus.New.name());

            result.add(map);
        }

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for getContractMapFromTemplate: {} seconds", duration.getSeconds());

        return result;
    }

    public void importData(List<Map<String, Object>> importMapList) {

        List<SearchFilter> orgInfoFilters = new ArrayList<>();
        orgInfoFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID()));
        orgInfoFilters.add(new SearchFilter("orgCode", SearchFilter.Operator.IsNotNull,null));
        List<OrgInfo> orgInfoList = orgInfoDao.findBySearchFilter(orgInfoFilters);
        List<String> orgCodeList = orgInfoList.stream().map(OrgInfo::getOrgCode).collect(Collectors.toList());
        Map<String,OrgInfo> map =  orgInfoList.stream().collect(Collectors.toMap(OrgInfo::getOrgCode,each->each,(value1, value2) -> value1));

        int row = 0;
        try {
            for (Map<String, Object> item : importMapList) {
                ++row;
                if(checkData(item, importMapList,orgCodeList,map)) {
                    importOneHisEquipmentSfxm(item);
                }

            }
        } catch (Exception e) {
            logger.info("error occurred when importing the {} row", row);
            throw e;
        }

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for importData: {} seconds", duration.getSeconds());
    }

    private void importOneHisEquipmentSfxm(Map<String, Object> item) {


        HisEquipmentSfxm hisEquipmentSfxm = (HisEquipmentSfxm)item.get("hisEquipmentSfxm");
        //ContractRow.clearErrMessage(item);
        //item.put("status", StringUtils.isEmpty(contractInfoVO.getId()) ? ImportStatus.Created.name() : ImportStatus.Exist.name());
        hisEquipmentSfxm.setIsMatched(Boolean.FALSE);
        hisEquipmentSfxmRepository.save(hisEquipmentSfxm);
    }

    private boolean checkData(Map<String, Object> item, List<Map<String, Object>> importMapList, List<String> orgCodeList,  Map<String,OrgInfo> map) {

        boolean isValidateData = true;
        HisEquipmentSfxm hisEquipmentSfxm = (HisEquipmentSfxm)item.get("hisEquipmentSfxm");
        ContractRow.clearErrMessage(item);

        if(hisEquipmentSfxm.getHosId() == null){
            item.put("status", HisEquipmentImportService.ImportStatus.Failure);
            ContractRow.addErrMessage(item, "医院机构代码不能为空");
            isValidateData = false;
        }
        if(hisEquipmentSfxm.getSfxmId() == null){
            item.put("status", HisEquipmentImportService.ImportStatus.Failure);
            ContractRow.addErrMessage(item, "收费项目不能为空");
            isValidateData = false;
        }
        if(hisEquipmentSfxm.getSfxmName() == null){
            item.put("status", HisEquipmentImportService.ImportStatus.Failure);
            ContractRow.addErrMessage(item, "收费项目名称不能为空");
            isValidateData = false;
        }
        if(hisEquipmentSfxm.getSfxmLb() == null){
            item.put("status", HisEquipmentImportService.ImportStatus.Failure);
            ContractRow.addErrMessage(item, "收费项目分类不能为空");
            isValidateData = false;
        }

        if(StringUtils.isNotBlank(hisEquipmentSfxm.getHosId())){
            if(!orgCodeList.contains(hisEquipmentSfxm.getHosId())){
                item.put("status", HisEquipmentImportService.ImportStatus.Failure);
                ContractRow.addErrMessage(item, "租户下面不存在该医院机构代码");
                isValidateData = false;
            }
        }

        if(isValidateData && StringUtils.isNotBlank(hisEquipmentSfxm.getSfxmId()) && StringUtils.isNotBlank(hisEquipmentSfxm.getHosId())){
            OrgInfo orgInfo = map.get(hisEquipmentSfxm.getHosId());
            hisEquipmentSfxm.setSiteUid(orgInfo.getSiteUID());
            hisEquipmentSfxm.setTenantUid(orgInfo.getTenantUID());
            HisEquipmentSfxm hisEquipmentSfxm1 = hisEquipmentSfxmRepository.findBySfxmIdAndHosIdAndSiteUid(hisEquipmentSfxm.getSfxmId(),hisEquipmentSfxm.getHosId(),orgInfo.getSiteUID());
            if(hisEquipmentSfxm1 != null){
                item.put("status", HisEquipmentImportService.ImportStatus.Failure);
                ContractRow.addErrMessage(item, "收费项目代码已存在,不允许导入");
                isValidateData = false;
            }
        }
        return isValidateData;
    }

    public List<Map<String, Object>> filterErrorRecords(List<Map<String, Object>> importMapList) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> map: importMapList) {

            String importStatus = map.get("status").toString();
            if (importStatus == null) {
                continue;
            }

            if (HisEquipmentImportService.ImportStatus.CheckFail.name().equals(importStatus) || HisEquipmentImportService.ImportStatus.Failure.name().equals(importStatus)) {
                result.add(map);
            }

        }

        return result;
    }
}
