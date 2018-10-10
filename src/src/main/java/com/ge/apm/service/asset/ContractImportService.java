package com.ge.apm.service.asset;

import com.ge.apm.dao.ContractInfoRepository;
import com.ge.apm.dao.SupplierRepository;
import com.ge.apm.domain.ContractInfo;
import com.ge.apm.domain.ContractInfoVO;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.Supplier;
import com.ge.apm.service.utils.ExcelDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import webapp.framework.web.WebUtil;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Created by 212605082 on 2018/8/20.
 */
@Service
public class ContractImportService {
    private static final Logger logger = LoggerFactory.getLogger(ContractImportService.class);

    public static final String EXCEL_CONTRACT_TEMPLATE_VERSION = "v1.0";

    public static final String SHEET_SERVICE_CONTRACT = "Contract List";
    public static final int EXCEL_CONTRACT_HEADER_ROW = 1;
    public static final int EXCEL_CONTRACT_DATA_ROW = 2;

    public static final int EXCEL_EXPORT_CONTRACT_HEADER_ROW = 1;
    public static final int EXCEL_EXPORT_CONTRACT_DATA_ROW = 2;

    @Autowired
    private ContractInfoRepository contractInfoDao;
    @Autowired
    private SupplierRepository supplierDao;

    public enum ImportStatus {
        New, Exist, Created, Failure, CheckFail
    }

    public List<Map<String, Object>> getContractMapFromTemplate(ExcelDocument doc, String contractType, OrgInfo selectedOrg) throws Exception {

        List<Map<String, Object>> result = new ArrayList<>();

        //for (Map< String, Object> rowMap = doc.getDataRowMap(SHEET_SERVICE_CONTRACT, keyRow, dataRow); rowMap.size() > 0 ; rowMap = doc.getDataRowMap(SHEET_SERVICE_CONTRACT, keyRow, ++dataRow)) {
        for (int i = EXCEL_CONTRACT_DATA_ROW ; i <= doc.getWorkBook().getSheet(SHEET_SERVICE_CONTRACT).getLastRowNum() + 1 ; i++) {

            Map< String, Object> rowMap = doc.getDataRowMap(SHEET_SERVICE_CONTRACT, EXCEL_CONTRACT_HEADER_ROW, i);

            Map<String, Object> map = new LinkedHashMap();
            ContractInfoVO contractInfoVO = new ContractInfoVO();

            contractInfoVO.setTenantUID(selectedOrg.getTenantUID());
            contractInfoVO.setInstitutionUID(selectedOrg.getInstitutionUID());
            contractInfoVO.setHospitalUID(selectedOrg.getHospitalUID());
            contractInfoVO.setSiteUID(selectedOrg.getSiteUID());
            contractInfoVO.setContractType(contractType);

            contractInfoVO.setLineNum(i);

            ContractRow contractRow = new ContractRow(contractInfoVO, rowMap);
            contractRow.setFieldStringValue("合同UID", "setId");
            contractRow.setFieldStringValue("名称", "setName");
            contractRow.setFieldStringValue("合同编号", "setContractNum");
            contractRow.setFieldStringValue("供应商", "setSupplierName");
            contractRow.setFieldDoubleValue("合同总额", "setAmount");
            contractRow.setFieldDateValue("开始时间", "setStartDate");
            contractRow.setFieldDateValue("结束时间", "setEndDate");
            contractRow.setFieldIntegerValue("合同时间(月)", "setEndMonths");

            /*contractRow.setFieldStringValue("清单序号", "setListNum");
            contractRow.setFieldStringValue("报告号", "setReportNum");
            contractRow.setFieldStringValue("项目申请名称", "setApplyProjectName");
            contractRow.setFieldStringValue("申请科室", "setApplyDept");
            contractRow.setFieldStringValue("参数撰写人", "setParameterPerson");
            contractRow.setFieldStringValue("中标单位", "setBidWinner");
            contractRow.setFieldStringValue("中标单位联系人", "setBidWinnerPerson");
            contractRow.setFieldStringValue("中标单位联系电话", "setBidWinnerTelphone");
            contractRow.setFieldStringValue("购入方式", "setTenderType");
            contractRow.setFieldDateValue("论证会结果时间", "setArgumentEndDate");
            contractRow.setFieldDateValue("技术参数送出时间", "setParameterEndDate");
            contractRow.setFieldDateValue("开标时间", "setTenderOpenDate");
            contractRow.setFieldDateValue("中标通知书时间", "setTenderNoticeDate");
            contractRow.setFieldDateValue("接招标文件材料", "setReceiveFileDate");
            contractRow.setFieldDateValue("拷样本", "setCopyFileDate");
            contractRow.setFieldDateValue("接电子版合同", "setFirstCheckDate");
            contractRow.setFieldDateValue("送审", "setChangeCommentDate");
            contractRow.setFieldDateValue("律师", "setLawerDignDate");
            contractRow.setFieldDateValue("返回供货商修改意见", "setReturnSupplierDate");
            contractRow.setFieldDateValue("收合同定稿", "setFileContractDate");
            contractRow.setFieldDateValue("签字并盖合同专用章", "setSignContractDate");
            contractRow.setFieldDateValue("备注", "setComments");*/

            if(contractInfoVO.isNull()){
                break;
            }

            map.put("contractInfoVO", contractInfoVO);
            map.put("rowData", rowMap);
            map.put("status", StringUtils.isEmpty(contractInfoVO.getId()) ? ImportStatus.New.name() : ImportStatus.Exist.name());
            map.put("hashCode", contractInfoVO.hashCode());

            result.add(map);
        }

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for getContractMapFromTemplate: {} seconds", duration.getSeconds());

        return result;
    }

    public void importData(List<Map<String, Object>> importContractMapList) {

        int row = 0;
        try {
            for (Map<String, Object> item : importContractMapList) {
                ++row;
                if(checkData(item, importContractMapList)) {
                    importOneContract(item);
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

    @Transactional
    protected void importOneContract(Map<String, Object> item) {

        ContractInfoVO contractInfoVO = (ContractInfoVO)item.get("contractInfoVO");

        //ContractRow.clearErrMessage(item);
        //item.put("status", StringUtils.isEmpty(contractInfoVO.getId()) ? ImportStatus.Created.name() : ImportStatus.Exist.name());

        if(contractInfoVO.getEndMonths() != null){
            Instant startDateInstant = contractInfoVO.getStartDate().toInstant();
            ZoneId zone = ZoneId.systemDefault();
            LocalDateTime startLocalDateTime = LocalDateTime.ofInstant(startDateInstant, zone);
            LocalDate startLocalDate = startLocalDateTime.toLocalDate();
            Instant scheduleDateInstant = startLocalDate.plus(contractInfoVO.getEndMonths(), ChronoUnit.MONTHS).plus(-1, ChronoUnit.DAYS).atStartOfDay().atZone(zone).toInstant();

            contractInfoVO.setEndDate(Date.from(scheduleDateInstant));
        }

        ContractInfo newContractInfo = new ContractInfo();

        BeanUtils.copyProperties(contractInfoVO, newContractInfo);

        contractInfoDao.save(newContractInfo);

    }

    public List<Map<String, Object>> filterErrorRecords(List<Map<String, Object>> importContractMapList) {

        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> map: importContractMapList) {

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

    public boolean checkData(Map<String, Object> item, List<Map<String, Object>> importContractMapList) {

        boolean isValidateData = true;

        ContractInfoVO contractInfoVO = (ContractInfoVO)item.get("contractInfoVO");
        Integer hashCode = Integer.valueOf(item.get("hashCode").toString());

        ContractRow.clearErrMessage(item);

        if(contractInfoVO.getName() == null){
            item.put("status", ImportStatus.Failure);
            ContractRow.addErrMessage(item, "错误: 合同名称为空");
            isValidateData = false;
        }

        if(contractInfoVO.getStartDate() == null){
            item.put("status", ImportStatus.Failure);
            ContractRow.addErrMessage(item, "错误: 开始时间为空");
            isValidateData = false;
        }

        if(contractInfoVO.getEndDate() == null && contractInfoVO.getEndMonths() == null){
            item.put("status", ImportStatus.Failure);
            ContractRow.addErrMessage(item, "错误: 结束时间为空和合同时间(月)不能同时为空");
            isValidateData = false;
        }

        if(contractInfoVO.getId() != null){
            ContractInfo tempContractInfo = contractInfoDao.findOne(contractInfoVO.getId());
            if(tempContractInfo == null){
                item.put("status", ImportStatus.Failure);
                ContractRow.addErrMessage(item, "错误: 无效的合同UID");
                isValidateData = false;
            }else if(!tempContractInfo.getContractType().equals(contractInfoVO.getContractType())){
                item.put("status", ImportStatus.Failure);
                ContractRow.addErrMessage(item, "错误: 不允许更改合同类型");
                isValidateData = false;
            }
        }

        if(contractInfoVO.getSupplierName() != null){
            Supplier supplier = supplierDao.findFirstByTenantUIDAndName(contractInfoVO.getTenantUID(), contractInfoVO.getSupplierName());
            if(supplier == null){
                item.put("status", ImportStatus.Failure);
                ContractRow.addErrMessage(item, "错误: 不存在的供应商");
                isValidateData = false;
            }else {
                contractInfoVO.setSupplierUid(supplier.getUid());
            }
        }

        for (Map<String, Object> tempContractMap: importContractMapList) {
            ContractInfoVO tempContractInfoVO = (ContractInfoVO)tempContractMap.get("contractInfoVO");
            Integer tempHashCode = Integer.valueOf(tempContractMap.get("hashCode").toString());
            if(hashCode.equals(tempHashCode) && contractInfoVO.getLineNum() != tempContractInfoVO.getLineNum()){
                item.put("status", ImportStatus.Failure);
                ContractRow.addErrMessage(item, "错误: 重复的数据");
                isValidateData = false;
                break;
            }
        }

        return isValidateData;
    }

    public Boolean exportFailData(ExcelDocument doc, List<Map<String, Object>> errorContractList) {

        int keyRowExport = EXCEL_EXPORT_CONTRACT_HEADER_ROW;
        int dataRowExport = EXCEL_EXPORT_CONTRACT_DATA_ROW;
        Boolean hasFailData = false;

        //doc.clearRowsData(SHEET_SERVICE_CONTRACT, dataRowExport, keyRowExport + errorContractList.size());
        for (Map<String, Object> item : errorContractList) {
            if (item.get("status").equals(ImportStatus.Failure)) {
                doc.writeRowData(SHEET_SERVICE_CONTRACT, keyRowExport, dataRowExport++, (Map<String, Object>) item.get("rowData"));
                hasFailData = true;
            }
        }

        return hasFailData;
    }


    public boolean checkDocVersion(ExcelDocument doc){
        return true;
    }

}
