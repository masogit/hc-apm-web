package com.ge.apm.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.ge.apm.service.adverseEvent.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
public class AdverseEventsExportDto {

    @ExcelProperty("报告编码")
    private String workOrderNum;

    @ExcelProperty(value = "报告日期", converter = AdverseEventDateConverter.class)
    private Date reportDate;

    @ExcelProperty("报告人")
    private String reporterName;

    @ExcelProperty("发生地")
    private String occurredPlace;

    @ExcelProperty("产品名称")
    private String assetName;

    @ExcelProperty("注册证编号")
    private String registrationNum;

    @ExcelProperty("曾用注册证编号")
    private String previouslyRegistrationNum;

  /*  @ExcelProperty("曾用注册证编号是否上报")
    private Boolean previouslyRegistrationReport;*/

    @ExcelProperty(value = "器械分类", converter = AdverseEventDevicesClassConverter.class)
    private Integer devicesClass;

    @ExcelProperty("型号")
    private String model;

    @ExcelProperty("规格")
    private String standard;

    @ExcelProperty("产品类别")
    private String productCategory;

    @ExcelProperty("产地")
    private String originPlace;

    @ExcelProperty("产品批号")
    private String productLotNum;

    @ExcelProperty("产品编号")
    private String productNum;

    @ExcelProperty("udi")
    private String udi;

    @ExcelProperty(value = "生产日期", converter = AdverseEventDateConverter.class)
    private Date productionDate;

    @ExcelProperty(value = "有效期至", converter = AdverseEventDateConverter.class)
    private Date validUntil;

    @ExcelProperty("上市许可证持有人")
    private String marketingAuthHolder;

    @ExcelProperty(value = "事件发生日期", converter = AdverseEventDateConverter.class)
    private Date eventOccurredDate;

    @ExcelProperty(value = "事件发现或获知日期", converter = AdverseEventDateConverter.class)
    private Date eventDiscoverDate;

    @ExcelProperty(value = "伤害程度", converter = AdverseEventHarmDegreeConverter.class)
    private Integer harmDegree;

    @ExcelProperty("伤害程度描述")
    private String harmDegreeDesc;

    @ExcelProperty("伤害表现")
    private String harmPerform;

    @ExcelProperty("器械故障表现")
    private String deviceFaultPerform;

    @ExcelProperty("姓名")
    private String patientName;

    @ExcelProperty(value = "性别", converter = AdverseEventSexConverter.class)
    private Integer patientSex;

    @ExcelProperty(value = "出生日期", converter = AdverseEventDateConverter.class)
    private Date patientBornDate;

    @ExcelProperty(value = "年龄类型", converter = AdverseEventAgeTypeConverter.class)
    private Integer patientAgeType;

    @ExcelProperty("病历号")
    private String medicalCaseNum;

    @ExcelProperty("既往病史")
    private String medicalHistory;

    @ExcelProperty("预期治疗疾病或者作用")
    private String exceptAffect;

    @ExcelProperty(value = "器械使用日期", converter = AdverseEventDateConverter.class)
    private Date deviceUsedDate;

    @ExcelProperty(value = "使用场所", converter = AdverseEventUsePlaceConverter.class)
    private Integer usePlace;

    @ExcelProperty("场所名称")
    private String placeName;

    @ExcelProperty("使用过程")
    private String useProcess;

    @ExcelProperty("合并用药/器械情况说明")
    private String deviceDesc;

    @ExcelProperty(value = "事件原因分析", converter = AdverseEventEventCauseAnalyzeConverter.class)
    private String eventCauseAnalyze;

    @ExcelProperty("事件原因分析描述")
    private String eventCauseDesc;

    @ExcelProperty("初步处置情况")
    private String initDisposeSitu;

    @ExcelProperty(value = "是否开展了调查", converter = AdverseEventBooleanConverter.class)
    private Boolean hasSurvey;

    @ExcelProperty("调查情况")
    private String surveySitu;

    @ExcelProperty("关联性评价")
    private String relevanceEval;

    @ExcelProperty("评价结果")
    private String evalCauseAnalyze;

    @ExcelProperty(value = "是否已采取控制措施", converter = AdverseEventBooleanConverter.class)
    private Boolean hasControlMeasure;

    @ExcelProperty("具体控制措施")
    private String controlMeasureDesc;

    @ExcelProperty("未采取控制措施原因")
    private String notControlCause;

    @ExcelProperty(value = "是否错报误报", converter = AdverseEventBooleanConverter.class)
    private Boolean hasFalseReport;

    @ExcelProperty("错报误报原因")
    private String falseReportCause;

    @ExcelProperty(value = "是否合并报告", converter = AdverseEventBooleanConverter.class)
    private Boolean reportMerge;

    @ExcelProperty("报告合并编码")
    private String reportMergeCode;

    @ExcelProperty(value = "状态", converter = AdverseEventStatusConverter.class)
    private Integer status;

    @ExcelProperty(value = "审核结果", converter = AdverseEventApprovalResultConverter.class)
    private Integer approvalResult;

    @ExcelProperty("审批意见")
    private String approvalOpinion;

    @ExcelProperty("审批人")
    private String actualApproverName;
    @ExcelProperty(value = "审批时间", converter = AdverseEventDateConverter.class)
    private Date actualApproverDate;
}
