package com.ge.apm.service.his.load;

import com.ge.apm.dao.HisEquipmentConnectRepository;

import com.ge.apm.domain.his.HisEquipmentConnect;
import com.ge.apm.service.HisEquipmentRow;
import com.ge.apm.service.asset.ContractImportService;
import com.ge.apm.service.asset.ContractRow;
import com.ge.apm.service.utils.ExcelDocument;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.web.WebUtil;

import java.time.*;
import java.util.*;

@Service
public class HisEquipmentImportService {
    private static final Logger logger = LoggerFactory.getLogger(HisEquipmentImportService.class);


    public static final String SHEET_SERVICE_CONTRACT = "HisEquipment List";
    public static final int EXCEL_CONTRACT_HEADER_ROW = 1;
    public static final int EXCEL_CONTRACT_DATA_ROW = 2;


    @Autowired
    HisEquipmentConnectRepository hisEquipmentConnectRepository;

    public List<HisEquipmentConnect> parse(ExcelDocument doc){

        List<HisEquipmentConnect> resultList = new ArrayList<>();

        int keyRow = 1;
        int dataRow = 2;
        for (Map< String, Object> rowMap = doc.getDataRowMap(keyRow, dataRow); rowMap != null; rowMap = doc.getDataRowMap(keyRow, ++dataRow)) {
            String id = ExcelDocument.getCellStringValue(rowMap.get("id"));
            String eqId = ExcelDocument.getCellStringValue(rowMap.get("eq_id"));
            String eqName = ExcelDocument.getCellStringValue(rowMap.get("eq_name"));
            String eqLb = ExcelDocument.getCellStringValue(rowMap.get("eq_lb"));
            String eqHosid = ExcelDocument.getCellStringValue(rowMap.get("eq_hosid"));
//            String createdDate = ExcelDocument.getCellStringValue(rowMap.get("create_date"));
//            String isMatched = ExcelDocument.getCellStringValue(rowMap.get("is_matched"));

            if (null == id) {
                break;
            }

            HisEquipmentConnect data = new HisEquipmentConnect();
            data.setId(id);
            data.setEqId(eqId);
            data.setEqName(eqName);
            data.setEqLb(eqLb);
            data.setEqHosid(eqHosid);
            data.setIsMatched(false);
            data.setCreatedDate(new Date());

            resultList.add(data);
        }

        return resultList;
    }

    public List<HisEquipmentConnect> saveAll(List<HisEquipmentConnect> data){
        Iterable<HisEquipmentConnect> iterable = hisEquipmentConnectRepository.save(data);

        return Lists.newArrayList(iterable);
    }

    public List<Map<String, Object>> getMapFromTemplate(ExcelDocument doc) {

        List<Map<String, Object>> result = new ArrayList<>();

        //for (Map< String, Object> rowMap = doc.getDataRowMap(SHEET_SERVICE_CONTRACT, keyRow, dataRow); rowMap.size() > 0 ; rowMap = doc.getDataRowMap(SHEET_SERVICE_CONTRACT, keyRow, ++dataRow)) {
        for (int i = EXCEL_CONTRACT_DATA_ROW ; i <= doc.getWorkBook().getSheet(SHEET_SERVICE_CONTRACT).getLastRowNum() + 1 ; i++) {

            Map< String, Object> rowMap = doc.getDataRowMap(SHEET_SERVICE_CONTRACT, EXCEL_CONTRACT_HEADER_ROW, i);

            Map<String, Object> map = new LinkedHashMap();
            HisEquipmentConnect hisEquipmentConnect = new HisEquipmentConnect();


            hisEquipmentConnect.setLineNum(i);

            HisEquipmentRow hisEquipmentRow = new HisEquipmentRow(hisEquipmentConnect, rowMap);
//            hisEquipmentRow.setFieldStringValue("设备唯一识别编号", "setEqId");
//            hisEquipmentRow.setFieldStringValue("设备名称", "setEqName");
//            hisEquipmentRow.setFieldStringValue("预置值", "setEqLb");
//            hisEquipmentRow.setFieldStringValue("医院机构代码", "setEqHosid");
            hisEquipmentRow.setFieldStringValue(WebUtil.getMessage("EqId"), "setEqId");
            hisEquipmentRow.setFieldStringValue(WebUtil.getMessage("EquipmentName"), "setEqName");
            hisEquipmentRow.setFieldStringValue(WebUtil.getMessage("PresetValue"), "setEqLb");
            hisEquipmentRow.setFieldStringValue(WebUtil.getMessage("HospitalCode"), "setEqHosid");
            map.put("hisEquipmentConnect", hisEquipmentConnect);
            map.put("rowData", rowMap);
            map.put("status",  ImportStatus.New.name());

            result.add(map);
        }

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for getContractMapFromTemplate: {} seconds", duration.getSeconds());

        return result;
    }

    public List<Map<String, Object>> filterErrorRecords(List<Map<String, Object>> importMapList) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> map: importMapList) {

            String importStatus = map.get("status").toString();
            if (importStatus == null) {
                continue;
            }

            if (ImportStatus.CheckFail.name().equals(importStatus) || ImportStatus.Failure.name().equals(importStatus)) {
                result.add(map);
            }

        }

        return result;
    }

    public void importData(List<Map<String, Object>> importMapList) {


        int row = 0;
        try {
            for (Map<String, Object> item : importMapList) {
                ++row;
                if(checkData(item, importMapList)) {
                    importOneHisEquipment(item);
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

    private boolean checkData(Map<String, Object> item, List<Map<String, Object>> importMapList) {
        boolean isValidateData = true;
        HisEquipmentConnect hisEquipmentConnect = (HisEquipmentConnect)item.get("hisEquipmentConnect");
        ContractRow.clearErrMessage(item);

        if(hisEquipmentConnect.getEqHosid() == null){
            item.put("status", ContractImportService.ImportStatus.Failure);
            ContractRow.addErrMessage(item, "医院机构代码不能为空");
            isValidateData = false;
        }
        if(hisEquipmentConnect.getEqId() == null){
            item.put("status", ContractImportService.ImportStatus.Failure);
            ContractRow.addErrMessage(item, "设备唯一编号不能为空");
            isValidateData = false;
        }
        if(hisEquipmentConnect.getEqName() == null){
            item.put("status", ContractImportService.ImportStatus.Failure);
            ContractRow.addErrMessage(item, "设备名称不能为空");
            isValidateData = false;
        }
        if(hisEquipmentConnect.getEqLb() == null){
            item.put("status", ContractImportService.ImportStatus.Failure);
            ContractRow.addErrMessage(item, "预置值不能为空");
            isValidateData = false;
        }
        if(hisEquipmentConnect.getEqLb() != null && HisEquipmentConnect.EqLb.getLis(hisEquipmentConnect.getEqLb())==null){
            item.put("status", ContractImportService.ImportStatus.Failure);
            ContractRow.addErrMessage(item, "预置值输入值有误");
            isValidateData = false;
        }
        HisEquipmentConnect byEqId = hisEquipmentConnectRepository.findByEqId(hisEquipmentConnect.getEqId());
        if(byEqId != null){
            item.put("status", ContractImportService.ImportStatus.Failure);
            ContractRow.addErrMessage(item, "设备编号已存在,不允许导入");
            isValidateData = false;
        }
        return isValidateData;

    }

    private void importOneHisEquipment(Map<String, Object> item) {

        HisEquipmentConnect hisEquipmentConnect = (HisEquipmentConnect)item.get("hisEquipmentConnect");
        //ContractRow.clearErrMessage(item);
        //item.put("status", StringUtils.isEmpty(contractInfoVO.getId()) ? ImportStatus.Created.name() : ImportStatus.Exist.name());
        hisEquipmentConnect.setCreatedDate(new Date());
        hisEquipmentConnect.setIsMatched(Boolean.FALSE);
        hisEquipmentConnectRepository.save(hisEquipmentConnect);
    }

    public enum ImportStatus {
        New, Exist, Created, Failure, CheckFail
    }


}
