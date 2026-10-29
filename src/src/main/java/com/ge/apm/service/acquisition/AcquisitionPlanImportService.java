package com.ge.apm.service.acquisition;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.acquisition.AcquisitionPlan;
import com.ge.apm.service.utils.ExcelParser;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
        import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service
public class AcquisitionPlanImportService {

    @Autowired
    private AcquisitionPlanService acquisitionPlanService;

    @Autowired
    private OrgInfoRepository orgInfoRepository;

    /**
     * 导入购置计划Excel
     * @param file 上传的Excel文件
     * @return 导入结果列表
     */
    @Transactional(rollbackFor = Exception.class)
    public List<AcquisitionPlanImportResult> importPlan(UploadedFile file) throws Exception {
        List<AcquisitionPlanImportResult> results = new ArrayList<>();
        Workbook workbook = null;

        try {
            String fileName = file.getFileName().toLowerCase();
            if (fileName.endsWith(".xls")) {
                workbook = new org.apache.poi.hssf.usermodel.HSSFWorkbook(file.getInputstream());
            } else if (fileName.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(file.getInputstream());
            } else {
                throw new RuntimeException("不支持的文件格式，请上传 .xls 或 .xlsx 文件");
            }

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null || sheet.getLastRowNum() < 1) {
                throw new RuntimeException("Excel文件为空或没有数据");
            }

            Map<String, Integer> header = ExcelParser.findFirstRowHeader(sheet);
            if (header == null || header.isEmpty()) {
                throw new RuntimeException("无法读取Excel表头");
            }

            UserAccount currentUser = UserContextService.getCurrentUserAccount();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                AcquisitionPlanImportResult result = new AcquisitionPlanImportResult();
                result.setRowNum(i + 1);

                try {
                    AcquisitionPlan plan = parseRowData(row, header, currentUser);
                    String validateError = validatePlan(plan);
                    if (StringUtils.isNotEmpty(validateError)) {
                        result.setSuccess(false);
                        result.setErrorMsg(validateError);
                        result.setAssetName(plan.getAssetName());
                    } else {
                        acquisitionPlanService.saveApplication(plan, currentUser, null, null);
                        result.setSuccess(true);
                        result.setAssetName(plan.getAssetName());
                    }
                } catch (Exception e) {
                    result.setSuccess(false);
                    result.setErrorMsg(e.getMessage());
                }
                results.add(result);
            }

        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        return results;
    }

    private AcquisitionPlan parseRowData(Row row, Map<String, Integer> header, UserAccount currentUser) throws Exception {
        AcquisitionPlan plan = new AcquisitionPlan();

        // TODO: 根据实际模板列名调整以下映射
        plan.setApplicationHospitalName(getStringValue(row, header, "申报单位"));
        plan.setApplicationDeptName(getStringValue(row, header, "申报临床科室（部门）"));
        plan.setAssetName(getStringValue(row, header, "医疗设备名称"));
        plan.setSpecifications(getStringValue(row, header, "规格型号"));
        plan.setApplicationType(getStringValue(row, header, "申请类别"));
        plan.setApplicationPlanCount(getIntegerValue(row, header, "计划采购数量（台/套）"));
        plan.setBudgetUnitPrice(getBigDecimalValue(row, header, "预算单价（万元）"));
        plan.setBudgetTotalAmount(getBigDecimalValue(row, header, "预算总价（万元）"));
        plan.setEstimatedServiceYears(getIntegerValue(row, header, "预计使用年限（年）"));
        plan.setSimilarEquipmentCount(getIntegerValue(row, header, "同类设备数量（台/套）"));
        plan.setFundingSource(getStringValue(row, header, "资金来源"));
        plan.setFundingImplemented(getStringValue(row, header, "资金落实（是/否）"));
        plan.setMedicalEquipmentLicense(getStringValue(row, header, "甲乙类大型医疗设备及配置许可证"));
        plan.setSiteRenovationCost(getBigDecimalValue(row, header, "场地改造成本（万元）"));
        plan.setPurchaseReason(getStringValue(row, header, "采购原因"));
        plan.setNewStaffAllocation(getIntegerValue(row, header, "新增人员配备（人）"));
        plan.setUpdatedAssetName(getStringValue(row, header, "被更新设备"));
        plan.setCurrentEquipmentServiceYears(getBigDecimalValue(row, header, "拟更新设备已使用年数（年）"));

        // 折旧成本（万元）(自动计算, 最多保留两位小数)
        plan.setDepreciationCost(getBigDecimalValue(row, header, "折旧成本（万元）"));
        // 维保费用（万元）(填报数字, 最多保留两位小数)
        plan.setMaintenanceCost(getBigDecimalValue(row, header, "维保费用（万元）"));
        // 配套不可单独收费耗材收费占比及采购成本（万元）
        plan.setConsumablesCost(getBigDecimalValue(row, header, "配套不可单独收费耗材收费占比及采购成本（万元）"));
        // 年均服务量（人/次）(填报整数)
        plan.setAnnualServiceVolume(getIntegerValue(row, header, "年均服务量（人/次）"));
        // 年服务收入总金额（万元）(填报数字, 最多保留两位小数)
        plan.setAnnualServiceIncome(getBigDecimalValue(row, header, "年服务收入总金额（万元）"));
        // 年度运营效益权重（%）(自动计算, 最多保留两位小数)
        plan.setAnnualOperationEfficiencyWeight(getBigDecimalValue(row, header, "年度运营效益权重%"));
        plan.setRequiredAssetFlag(getStringValue(row, header, "临床重点专科建设标准配备或政策文件要求配备"));

        /*if (plan.getBudgetUnitPrice() != null && plan.getApplicationPlanCount() != null) {
            plan.setBudgetTotalAmount(plan.getBudgetUnitPrice().multiply(new BigDecimal(plan.getApplicationPlanCount())));
        }*/

        plan.setApplicationUserUid(currentUser.getUid());
        plan.setApplicationUserName(currentUser.getName());
        plan.setApplicationDate(new Date());
        plan.setApplicationHospitalUid(currentUser.getHospitalUID());
        plan.setApplicationStatus(AcquisitionPlan.ApplicationStatusEnum.TempSave.name());
        plan.setApplicationStep(0);
        plan.setTenantUID(currentUser.getTenantUID());

        int year = Calendar.getInstance().get(Calendar.YEAR);
        plan.setApplicationYear(String.valueOf(year));
        acquisitionPlanService.genApplicationNum(plan,"GZ");

        return plan;
    }

    private String validatePlan(AcquisitionPlan plan) {
        StringBuilder errors = new StringBuilder();
        if(StringUtils.isEmpty(plan.getApplicationHospitalName())){
            errors.append("申报单位不能为空；");
        }else{
            OrgInfo orgInfo = orgInfoRepository.getHospitalByNameAndTenantUID(plan.getApplicationHospitalName(), plan.getTenantUID());
            if(orgInfo == null){
                errors.append("申报单位填写错误；");
            }else{
                plan.setApplicationHospitalName(orgInfo.getName());
                plan.setApplicationHospitalUid(orgInfo.getUid());
                plan.setInstitutionUID(orgInfo.getInstitutionUID());
                plan.setHospitalUID(orgInfo.getHospitalUID());
                plan.setSiteUID(orgInfo.getSiteUID());
            }
        }
        if(StringUtils.isEmpty(plan.getApplicationDeptName())){
            errors.append("申报临床科室（部门）不能为空；");
        }else{
            if(StringUtils.isNotEmpty(plan.getApplicationHospitalUid())){
                OrgInfo orgInfo = orgInfoRepository.getDepartmentByNameAndHospital(plan.getApplicationDeptName(), plan.getApplicationHospitalUid());
                if(orgInfo == null){
                    errors.append("申报临床科室（部门）填写错误；");
                }else{
                    plan.setApplicationDeptName(orgInfo.getName());
                    plan.setApplicationDeptUid(orgInfo.getUid()); 
                }
            }
        }
        if (StringUtils.isEmpty(plan.getAssetName())) {
            errors.append("设备名称不能为空；");
        }
       /* if (StringUtils.isEmpty(plan.getSpecifications())) {
            errors.append("规格型号不能为空；");
        }*/
        if (StringUtils.isEmpty(plan.getApplicationType())) {
            errors.append("申请类别不能为空；");
        }else {
            if("必需类".equals(plan.getApplicationType())){
                plan.setApplicationType("necessities");
            }else if("更新类".equals(plan.getApplicationType())){
                plan.setApplicationType("updates");
            }else if("常规类".equals(plan.getApplicationType())){
                plan.setApplicationType("general");
            }else {
                plan.setApplicationType(null);
            }
            if(StringUtils.isEmpty(plan.getApplicationType())){
                errors.append("申请类别填写错误；");
            }
        }
        if (plan.getApplicationPlanCount() == null) {
            errors.append("计划采购数量不能为空；");
        }
        if (plan.getBudgetUnitPrice() == null) {
            errors.append("预算单价不能为空；");
        }
        if (plan.getBudgetTotalAmount() == null) {
            errors.append("预算单价不能为空；");
        }
        if (plan.getEstimatedServiceYears() == null) {
            errors.append("预计使用年限不能为空；");
        }
        if (plan.getSimilarEquipmentCount() == null) {
            errors.append("同类设备数量不能为空；");
        }
        if (StringUtils.isEmpty(plan.getFundingSource())) {
            errors.append("资金来源不能为空；");
        }else {
            if("财政".equals(plan.getFundingSource())){
                plan.setFundingSource("fiscal");
            }else if("自有".equals(plan.getFundingSource())){
                plan.setFundingSource("self-owned");
            }else if("其他".equals(plan.getFundingSource())){
                plan.setFundingSource("others");
            }else{
                plan.setFundingSource(null);
            }
            if(StringUtils.isEmpty(plan.getFundingSource())){
                errors.append("资金来源填写错误；");
            }
        }
        if(StringUtils.isEmpty(plan.getFundingImplemented())){
            errors.append("资金落实不能为空；");
        }else {
            if("是".equals(plan.getFundingImplemented())){
                plan.setFundingImplemented("Y");
            }else if("否".equals(plan.getFundingImplemented())){
                plan.setFundingImplemented("N");
            }else {
                plan.setFundingImplemented(null);
            }
            if(StringUtils.isEmpty(plan.getFundingImplemented())){
                errors.append("资金落实填写错误；");
            }
        }
        if(StringUtils.isEmpty(plan.getMedicalEquipmentLicense())){
            errors.append("甲乙类大型医疗设备及配置许可证不能为空；");
        }else {
            if("甲类".equals(plan.getMedicalEquipmentLicense())){
                plan.setMedicalEquipmentLicense("CategoryA");
            }else if ("乙类".equals(plan.getMedicalEquipmentLicense())){
                plan.setMedicalEquipmentLicense("CategoryB");
            }else if("无".equals(plan.getMedicalEquipmentLicense())){
                plan.setMedicalEquipmentLicense("None");
            }else {
                plan.setMedicalEquipmentLicense(null);
            }
            if(StringUtils.isEmpty(plan.getMedicalEquipmentLicense())){
                errors.append("甲乙类大型医疗设备及配置许可证填写错误；");
            }
        }
        if(StringUtils.isEmpty(plan.getPurchaseReason())){
            errors.append("采购原因不能为空；");
        }
        if(StringUtils.isEmpty(plan.getRequiredAssetFlag())){
            errors.append("临床重点专科建设标准配备或政策文件要求配备不能为空；");
        }else {
            if("是".equals(plan.getRequiredAssetFlag())){
                plan.setRequiredAssetFlag("Y");
            }else if("否".equals(plan.getRequiredAssetFlag())){
                plan.setRequiredAssetFlag("N");
            }else {
                plan.setRequiredAssetFlag(null);
            }
            if(StringUtils.isEmpty(plan.getRequiredAssetFlag())){
                errors.append("临床重点专科建设标准配备或政策文件要求配备填写错误；");
            }
        }
        if(plan.getSiteRenovationCost() == null){
            errors.append("场地改造成本不能为空；");
        }
        if(plan.getNewStaffAllocation() == null){
            errors.append("新增人员配备不能为空；");
        }
        if(plan.getDepreciationCost() == null){
            errors.append("折旧成本不能为空；");
        }
        if(plan.getMaintenanceCost() == null){
            errors.append("维保费用不能为空；");
        }
        if(plan.getConsumablesCost() == null){
            errors.append("配套不可单独收费耗材收费占比及采购成本不能为空；");
        }
        if(plan.getAnnualServiceVolume() == null){
            errors.append("年均服务量不能为空；");
        }
        if(plan.getAnnualServiceIncome() == null){
            errors.append("年服务收入总金额不能为空；");
        }
        if(plan.getAnnualOperationEfficiencyWeight() == null){
            errors.append("年度运营效益权重不能为空；");
        }

        return errors.length() > 0 ? errors.toString() : null;
    }

    private String getStringValue(Row row, Map<String, Integer> header, String columnName) {
        Integer index = header.get(columnName);
        if (index == null) {
            return null;
        }
        try {
            String value = ExcelParser.getCellStringValue(row, index);
            return StringUtils.isEmpty(value) ? null : value.trim();
        } catch (Exception e) {
            return null;
        }
    }

    private Integer getIntegerValue(Row row, Map<String, Integer> header, String columnName) {
        String value = getStringValue(row, header, columnName);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        try {
            value = value.trim().replaceAll("\\.0$", "");
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal getBigDecimalValue(Row row, Map<String, Integer> header, String columnName) {
        String value = getStringValue(row, header, columnName);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static class AcquisitionPlanImportResult {
        private boolean success;
        private int rowNum;
        private String assetName;
        private String errorMsg;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public int getRowNum() {
            return rowNum;
        }

        public void setRowNum(int rowNum) {
            this.rowNum = rowNum;
        }

        public String getAssetName() {
            return assetName;
        }

        public void setAssetName(String assetName) {
            this.assetName = assetName;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }

        /**
         * 兼容前端 Import.xhtml 中的 #{item.excelIdex}
         */
        public int getExcelIdex() {
            return rowNum;
        }

        /**
         * 兼容前端 Import.xhtml 中的 #{item.errorInfo}
         */
        public String getErrorInfo() {
            return errorMsg;
        }
    }
}
