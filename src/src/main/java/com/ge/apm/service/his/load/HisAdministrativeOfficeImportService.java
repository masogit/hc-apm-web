package com.ge.apm.service.his.load;

import com.ge.apm.dao.HisAdministrativeOfficesRepository;
import com.ge.apm.domain.his.HisAdministrativeOffices;
import com.ge.apm.domain.his.HisEquipmentConnect;
import com.ge.apm.service.asset.ContractImportService;
import com.ge.apm.service.asset.ContractRow;
import com.ge.apm.service.his.HisClinicalRow;
import com.ge.apm.service.utils.ExcelDocument;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class HisAdministrativeOfficeImportService {
    private static final Logger logger = LoggerFactory.getLogger(HisAdministrativeOfficeImportService.class);

    @Autowired
    HisAdministrativeOfficesRepository hisAdministrativeOfficesRepository;


    public static final String SHEET_SERVICE_CONTRACT = "HisClinical List";
    public static final int EXCEL_CONTRACT_HEADER_ROW = 1;
    public static final int EXCEL_CONTRACT_DATA_ROW = 2;

    public List<HisAdministrativeOffices> parse(ExcelDocument doc){

        List<HisAdministrativeOffices> resultList = new ArrayList<>();

        int keyRow = 1;
        int dataRow = 2;
        for (Map< String, Object> rowMap = doc.getDataRowMap(keyRow, dataRow); rowMap != null; rowMap = doc.getDataRowMap(keyRow, ++dataRow)) {
            String id = ExcelDocument.getCellStringValue(rowMap.get("id"));
            String hisKsId = ExcelDocument.getCellStringValue(rowMap.get("his_ksid"));
            String hisKsName = ExcelDocument.getCellStringValue(rowMap.get("his_ksname"));
            String hosId = ExcelDocument.getCellStringValue(rowMap.get("hos_id"));
            String tyBz = ExcelDocument.getCellStringValue(rowMap.get("ty_bz"));
//            String isMatched = ExcelDocument.getCellStringValue(rowMap.get("is_matched"));

            if (null == id) {
                break;
            }

            HisAdministrativeOffices hisAdministrativeOffices = new HisAdministrativeOffices();
            hisAdministrativeOffices.setId(id);
            hisAdministrativeOffices.setHisKsid(hisKsId);
            hisAdministrativeOffices.setHisKsname(hisKsName);
            hisAdministrativeOffices.setHosId(hosId);
            hisAdministrativeOffices.setTyBz(tyBz);
            hisAdministrativeOffices.setIsMatched(false);

            resultList.add(hisAdministrativeOffices);
        }

        return resultList;
    }

    public List<HisAdministrativeOffices> saveAll(List<HisAdministrativeOffices> data){
        Iterable<HisAdministrativeOffices> iterable = hisAdministrativeOfficesRepository.save(data);

        return Lists.newArrayList(iterable);
    }

    public List<Map<String, Object>> getMapFromTemplate(ExcelDocument doc) {
        List<Map<String, Object>> result = new ArrayList<>();

        //for (Map< String, Object> rowMap = doc.getDataRowMap(SHEET_SERVICE_CONTRACT, keyRow, dataRow); rowMap.size() > 0 ; rowMap = doc.getDataRowMap(SHEET_SERVICE_CONTRACT, keyRow, ++dataRow)) {
        for (int i = EXCEL_CONTRACT_DATA_ROW ; i <= doc.getWorkBook().getSheet(SHEET_SERVICE_CONTRACT).getLastRowNum() + 1 ; i++) {

            Map< String, Object> rowMap = doc.getDataRowMap(SHEET_SERVICE_CONTRACT, EXCEL_CONTRACT_HEADER_ROW, i);

            Map<String, Object> map = new LinkedHashMap();
            HisAdministrativeOffices hisAdministrativeOffices = new HisAdministrativeOffices();
            hisAdministrativeOffices.setLineNum(i);
            HisClinicalRow hisClinicalRow = new HisClinicalRow(hisAdministrativeOffices, rowMap);
            hisClinicalRow.setFieldStringValue("His科室代码", "setHisKsid");
            hisClinicalRow.setFieldStringValue("His科室名称", "setHisKsname");
            hisClinicalRow.setFieldStringValue("是否停用", "setTyBz");
            hisClinicalRow.setFieldStringValue("医院机构代码", "setHosId");
//            hisEquipmentRow.setFieldStringValue(WebUtil.getMessage("EqId"), "setEqId");
//            hisEquipmentRow.setFieldStringValue(WebUtil.getMessage("EquipmentName"), "setEqName");
//            hisEquipmentRow.setFieldStringValue(WebUtil.getMessage("PresetValue"), "setEqLb");
//            hisEquipmentRow.setFieldStringValue(WebUtil.getMessage("HospitalCode"), "setEqHosid");
            map.put("hisClinical", hisAdministrativeOffices);
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
        int row = 0;
        try {
            for (Map<String, Object> item : importMapList) {
                ++row;
                if(checkData(item, importMapList)) {
                    importOneHisClinical(item);
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

    private void importOneHisClinical(Map<String, Object> item) {

        HisAdministrativeOffices hisAdministrativeOffices = (HisAdministrativeOffices)item.get("hisClinical");
        //ContractRow.clearErrMessage(item);
        //item.put("status", StringUtils.isEmpty(contractInfoVO.getId()) ? ImportStatus.Created.name() : ImportStatus.Exist.name());
        hisAdministrativeOffices.setIsMatched(Boolean.FALSE);
        hisAdministrativeOfficesRepository.save(hisAdministrativeOffices);
    }

    private boolean checkData(Map<String, Object> item, List<Map<String, Object>> importMapList) {

        boolean isValidateData = true;
        HisAdministrativeOffices hisAdministrativeOffices = (HisAdministrativeOffices)item.get("hisClinical");
        ContractRow.clearErrMessage(item);

        if(hisAdministrativeOffices.getHosId() == null){
            item.put("status", ContractImportService.ImportStatus.Failure);
            ContractRow.addErrMessage(item, "医院机构代码不能为空");
            isValidateData = false;
        }
        if(hisAdministrativeOffices.getHisKsid() == null){
            item.put("status", ContractImportService.ImportStatus.Failure);
            ContractRow.addErrMessage(item, "科室编号不能为空");
            isValidateData = false;
        }
        if(hisAdministrativeOffices.getHisKsname() == null){
            item.put("status", ContractImportService.ImportStatus.Failure);
            ContractRow.addErrMessage(item, "科室名称不能为空");
            isValidateData = false;
        }
        if(hisAdministrativeOffices.getTyBz() == null){
            item.put("status", ContractImportService.ImportStatus.Failure);
            ContractRow.addErrMessage(item, "是否停用不能为空");
            isValidateData = false;
        }
        if(hisAdministrativeOffices.getTyBz() != null && HisAdministrativeOffices.TyBz.getTyBz(hisAdministrativeOffices.getTyBz())==null){
            item.put("status", ContractImportService.ImportStatus.Failure);
            ContractRow.addErrMessage(item, "是否停用输入有误");
            isValidateData = false;
        }
        HisAdministrativeOffices hisKsid = hisAdministrativeOfficesRepository.findByHisKsid(hisAdministrativeOffices.getHisKsid());
        if(hisKsid != null){
            item.put("status", ContractImportService.ImportStatus.Failure);
            ContractRow.addErrMessage(item, "科室编号已存在,不允许导入");
            isValidateData = false;
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
