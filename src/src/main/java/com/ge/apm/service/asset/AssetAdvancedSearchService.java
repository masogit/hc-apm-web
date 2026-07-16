package com.ge.apm.service.asset;


import com.ge.apm.domain.SearchCondition;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.domain.I18nMessage;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;

import java.util.*;

@Component
public class AssetAdvancedSearchService {

    private static final String CONDITION_PATH = "adv*";

    private static final List<FieldOption> FIELD_OPTIONS = new ArrayList<>();
    private static final Map<String, String> FIELD_TYPE_MAP = new HashMap<>();

    static {
        registerField("name", "设备名称", "string");
        registerField("aliasName", "设备别名", "string");
        registerField("functionType", "设备型号", "string");
        registerField("serialNum", "制造商序列号", "string");
        registerField("departNum", "设备编号（院方）", "string");
        registerField("financingNum", "资产编号", "string");
        registerField("qrCode", "二维码", "string");
        registerField("rfId", "RFID", "string");
        registerField("barcode", "条码", "string");
        registerField("nationalStandardCode", "设备国标码", "string");
        registerField("modalityType", "设备类型", "string");
        registerField("modalityId", "院内IT系统编号", "string");
        registerField("rfGroup", "射频码分组", "string");
        registerField("systemId", "SystemID/厂商报修ID", "string");
        registerField("systemNum1", "系统编号1", "string");
        registerField("systemNum2", "系统编号2", "string");
        registerField("systemNum3", "系统编号3", "string");
        registerField("systemNum4", "系统编号4", "string");
        registerField("systemNum5", "系统编号5", "string");
        registerField("registrationNo", "注册证号", "string");
        registerField("locationCode", "安装位置编码", "string");
        registerField("locationName", "安装位置", "string");
        registerField("assetLocation", "当前位置", "string");
        registerField("locationNotes", "位置备注1(科室)", "string");
        registerField("manufacture", "制造商", "string");
        registerField("manufactureTel", "制造商电话", "string");
        registerField("vendor", "供应商", "string");
        registerField("vendorTel", "制造商联系方式", "string");
        registerField("makerShortName", "品牌/制造商简称", "string");
        registerField("deviceName", "设备标准名称", "string");
        registerField("maitanance", "维修商", "string");
        registerField("maitananceTel", "维修商电话", "string");
        registerField("subjectProject", "所属课题项目", "string");
        registerField("comments", "备注", "string");
        registerField("pmOwnerName", "保养负责人", "string");
        registerField("pmOwnerTel", "保养负责人电话", "string");
        registerField("patrolInspectionOwnerName", "巡检负责人", "string");
        registerField("patrolInspectionOwnerTel", "巡检负责人电话", "string");
        registerField("assetOwnerName", "维修负责人", "string");
        registerField("assetOwnerTel", "维修负责人电话", "string");
        registerField("assetOwnerName2", "维修负责人2", "string");
        registerField("assetOwnerTel2", "维修负责人2电话", "string");
        registerField("assetOwnerName3", "维修负责人3", "string");
        registerField("assetOwnerTel3", "维修负责人3电话", "string");
        registerField("clinicalOwnerName", "使用责任人", "string");
        registerField("clinicalOwnerTel", "使用责任人电话", "string");
        registerField("measuringOwnerName", "计量负责人", "string");
        registerField("createdBy", "创建者", "string");
        registerField("UCMID", "UCMID", "string");
        registerField("epc", "EPC码", "string");
        registerField("productFamily", "产品系列", "string");
        registerField("spare06", "备用字段06", "string");
        registerField("spare07", "备用字段07", "string");
        registerField("spare08", "备用字段08", "string");
        registerField("spare09", "备用字段09", "string");
        registerField("spare10", "备用字段10", "string");
        registerField("currentId", "电流ID", "string");
        registerField("powerOnThreshold", "开机电流阈值", "string");
        registerField("clinicalDeptName", "所属科室", "string");
        registerField("warrantyStatus", "维保状态", "string");
        registerField("medicalApparatusClassification", "医疗器械分类", "string");

        registerField("assetGroup", "设备分类", "enum");
        registerField("assetClass", "管理分类", "enum");
        registerField("status", "当前状态", "enum");
        registerField("deviceStatus", "资产状态", "enum");
        registerField("networkingStatus", "联网情况", "enum");
        registerField("assetUsage", "用途分类", "enum");
        registerField("capitalSource", "资金来源", "enum");
        registerField("capitalSource2", "资金来源2", "enum");
        registerField("measureGrade", "计量等级", "enum");
        registerField("riskGrade", "风险等级", "enum");
        registerField("emergencyEquipment", "是否急救设备", "enum");
        registerField("importEquipment", "是否进口设备", "enum");
        registerField("repairForm", "维修类型", "enum");
        registerField("proprietaryClass", "专有分类", "enum");
        registerField("productSegment", "产品细分", "enum");
        registerField("assetQuality", "设备性质", "enum");
        registerField("assetDisposalResults", "资产处置结果", "enum");
        registerField("measuringItemCategory", "计量项目类别", "enum");
        registerField("accountingCategory", "核算类别", "enum");
        registerField("assetValueCategory", "资产价值类别", "enum");
        registerField("depreciationMethod", "折旧方法", "enum");
        registerField("functionGrade", "功能等级", "enum");

        registerField("purchasePrice", "采购价格", "number");
        registerField("salvageValue", "最终残值(元)", "number");
        registerField("lifecycle", "使用年限", "number");
        registerField("warrantyMonth", "质保时间（月）", "number");
        registerField("assetQuantity", "数量", "number");
        registerField("assetUsedLifecycle", "设备已使用年限", "number");
        registerField("usageCount", "已使用次数", "number");
        registerField("ratingUsageCount", "额定使用次数", "number");
        registerField("monitorRate", "主机功率", "number");
        registerField("unitPriceElectricity", "电价单价", "number");
        registerField("otherPower", "其他功率", "number");

        registerField("warrantyDate", "维保截止日期", "date");
        registerField("createdDate", "创建日期", "date");
        registerField("manufactDate", "出厂日期", "date");
        registerField("purchaseDate", "采购日期", "date");
        registerField("acceptDate", "验收日期", "date");
        registerField("arriveDate", "到货日期", "date");
        registerField("installDate", "启用日期", "date");
        registerField("terminateDate", "报废日期", "date");
        registerField("factoryWarrantyDate", "质保截止日期", "date");
        registerField("lastInventoryDate", "上次盘点日期", "date");
        registerField("assetLifecycleDeadline", "设备使用年限截止日期", "date");
        registerField("lastPmDate", "上次保养日期", "date");
        registerField("lastQaDate", "上次巡检日期", "date");
        registerField("lastMeteringDate", "上次计量日期", "date");
        registerField("nextMeteringDate", "下次计量日期", "date");
        registerField("mvsContractStartDate", "MVS合同起始日", "date");
        registerField("mvsContractEndDate", "MVS合同截止日", "date");

        registerField("isPm", "是否是保养", "boolean");
        registerField("isFixed", "是否固定资产", "boolean");
        registerField("isChecked", "是否盘点", "boolean");
        registerField("isUseRegistration", "是否使用登记", "boolean");
        registerField("warranty", "是否保修提醒", "boolean");
        registerField("isValid", "是否在用", "boolean");
    }

    private static void registerField(String value, String label, String fieldType) {
        FIELD_OPTIONS.add(new FieldOption(value, label, fieldType));
        FIELD_TYPE_MAP.put(value, fieldType);
    }

    public List<FieldOption> getFieldOptions() {
        return FIELD_OPTIONS;
    }

    public String getFieldType(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) return "string";
        return FIELD_TYPE_MAP.getOrDefault(fieldName, "string");
    }

    public List<OperatorOption> getOperatorsForField(String fieldName) {
        String fieldType = getFieldType(fieldName);
        List<OperatorOption> operators = new ArrayList<>();
        switch (fieldType) {
            case "string":
                operators.add(new OperatorOption("LIKE", "包含"));
                operators.add(new OperatorOption("EQ", "等于"));
                operators.add(new OperatorOption("NE", "不等于"));
                operators.add(new OperatorOption("IsNull", "为空"));
                operators.add(new OperatorOption("IsNotNull", "不为空"));
                break;
            case "enum":
                operators.add(new OperatorOption("EQ", "等于"));
                operators.add(new OperatorOption("NE", "不等于"));
                operators.add(new OperatorOption("IsNull", "为空"));
                operators.add(new OperatorOption("IsNotNull", "不为空"));
                break;
            case "number":
                operators.add(new OperatorOption("EQ", "等于"));
                operators.add(new OperatorOption("GT", "大于"));
                operators.add(new OperatorOption("LT", "小于"));
                operators.add(new OperatorOption("GTE", "大于等于"));
                operators.add(new OperatorOption("LTE", "小于等于"));
                operators.add(new OperatorOption("NE", "不等于"));
                operators.add(new OperatorOption("BETWEEN", "区间"));
                operators.add(new OperatorOption("IsNull", "为空"));
                operators.add(new OperatorOption("IsNotNull", "不为空"));
                break;
            case "date":
                operators.add(new OperatorOption("EQ", "等于"));
                operators.add(new OperatorOption("GT", "晚于"));
                operators.add(new OperatorOption("LT", "早于"));
                operators.add(new OperatorOption("GTE", "不早于"));
                operators.add(new OperatorOption("LTE", "不晚于"));
                operators.add(new OperatorOption("BETWEEN", "区间"));
                operators.add(new OperatorOption("IsNull", "为空"));
                operators.add(new OperatorOption("IsNotNull", "不为空"));
                break;
            case "boolean":
                operators.add(new OperatorOption("EQ", "等于"));
                operators.add(new OperatorOption("NE", "不等于"));
                break;
            default:
                operators.add(new OperatorOption("LIKE", "包含"));
                operators.add(new OperatorOption("EQ", "等于"));
                operators.add(new OperatorOption("IsNull", "为空"));
                operators.add(new OperatorOption("IsNotNull", "不为空"));
                break;
        }
        return operators;
    }

    public void clearAdvancedFilters(List<SearchFilter> searchFilters) {
        if (searchFilters == null) return;
        Iterator<SearchFilter> iterator = searchFilters.iterator();
        while (iterator.hasNext()) {
            SearchFilter filter = iterator.next();
            if (CONDITION_PATH.equals(filter.conditionPath)) {
                iterator.remove();
            }
        }
    }

    public void applyConditions(List<SearchFilter> searchFilters, List<SearchCondition> conditions) {
        if (searchFilters == null || conditions == null || conditions.isEmpty()) {
            return;
        }

        clearAdvancedFilters(searchFilters);

        for (SearchCondition condition : conditions) {
            if (condition.getFieldName() == null || condition.getFieldName().trim().isEmpty()) {
                continue;
            }
            String operatorStr = condition.getOperator();
            if (operatorStr == null || operatorStr.trim().isEmpty()) {
                continue;
            }

            SearchFilter.Operator operator;
            try {
                operator = SearchFilter.Operator.valueOf(operatorStr);
            } catch (IllegalArgumentException e) {
                continue;
            }

            String fieldType = getFieldType(condition.getFieldName());

            if (operator == SearchFilter.Operator.IsNull || operator == SearchFilter.Operator.IsNotNull) {
                searchFilters.add(new SearchFilter(condition.getFieldName(), operator, null, CONDITION_PATH));
            } else if (operator == SearchFilter.Operator.BETWEEN) {
                applyBetween(searchFilters, condition, fieldType);
            } else {
                Object value = resolveValue(condition, fieldType);
                if (value != null) {
                    searchFilters.add(new SearchFilter(condition.getFieldName(), operator, value, CONDITION_PATH));
                }
            }
        }
    }

    private void applyBetween(List<SearchFilter> searchFilters, SearchCondition condition, String fieldType) {
        if ("date".equals(fieldType)) {
            if (condition.getDateValue() != null) {
                searchFilters.add(new SearchFilter(condition.getFieldName(), SearchFilter.Operator.GTE, condition.getDateValue(), CONDITION_PATH));
            }
            if (condition.getDateValue2() != null) {
                searchFilters.add(new SearchFilter(condition.getFieldName(), SearchFilter.Operator.LTE, condition.getDateValue2(), CONDITION_PATH));
            }
        } else {
            if (isNotBlank(condition.getValue())) {
                Object val = "number".equals(fieldType) ? parseNumber(condition.getValue()) : condition.getValue();
                if (val != null) {
                    searchFilters.add(new SearchFilter(condition.getFieldName(), SearchFilter.Operator.GTE, val, CONDITION_PATH));
                }
            }
            if (isNotBlank(condition.getValue2())) {
                Object val2 = "number".equals(fieldType) ? parseNumber(condition.getValue2()) : condition.getValue2();
                if (val2 != null) {
                    searchFilters.add(new SearchFilter(condition.getFieldName(), SearchFilter.Operator.LTE, val2, CONDITION_PATH));
                }
            }
        }
    }

    public List<EnumOption> getEnumOptions(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) return Collections.emptyList();
        String msgType = fieldName;
        // status field uses "assetStatus" as i18n message type
        if ("status".equals(fieldName)) {
            msgType = "assetStatus";
        }
        try {
            List<I18nMessage> msgList = FieldValueMessageController.getFieldValueList(msgType);
            List<EnumOption> options = new ArrayList<>();
            for (I18nMessage msg : msgList) {
                options.add(new EnumOption(msg.getMsgKey(), msg.getValue()));
            }
            return options;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private Object resolveValue(SearchCondition condition, String fieldType) {
        switch (fieldType) {
            case "date":
                return condition.getDateValue();
            case "number":
                return parseNumber(condition.getValue());
            case "enum":
                return parseNumber(condition.getValue());
            case "boolean":
                return "true".equals(condition.getValue());
            default:
                return condition.getValue();
        }
    }

    private Object parseNumber(String str) {
        if (str == null || str.trim().isEmpty()) return null;
        try {
            if (str.contains(".")) {
                return Double.parseDouble(str);
            } else {
                return Integer.parseInt(str);
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static class FieldOption {
        private String value;
        private String label;
        private String fieldType;

        public FieldOption(String value, String label, String fieldType) {
            this.value = value;
            this.label = label;
            this.fieldType = fieldType;
        }

        public String getValue() { return value; }
        public String getLabel() { return label; }
        public String getFieldType() { return fieldType; }
    }

    public static class OperatorOption {
        private String value;
        private String label;

        public OperatorOption(String value, String label) {
            this.value = value;
            this.label = label;
        }

        public String getValue() { return value; }
        public String getLabel() { return label; }
    }

    public static class EnumOption {
        private String value;
        private String label;

        public EnumOption(String value, String label) {
            this.value = value;
            this.label = label;
        }

        public String getValue() { return value; }
        public String getLabel() { return label; }
    }
}
