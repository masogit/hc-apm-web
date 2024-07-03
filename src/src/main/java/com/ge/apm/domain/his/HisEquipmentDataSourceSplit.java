package com.ge.apm.domain.his;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 数据拆分的表
 * @author 503209435
 */
@Entity
@Table(name = "his_equipment_data_source_split")
public class HisEquipmentDataSourceSplit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;

    @Column(name = "source_id")//元数据ID
    private String sourceId;

    @Column(name = "org_code")//机构代码
    private String orgCode;

    @Column(name = "org_name")
    private String orgName;

    @Column(name = "device_code")//设备唯一编号
    private String deviceCode;

    @Column(name = "patient_registration_num")//病人住院号或门诊号
    private String patientRegistrationNum;

    @Column(name = "prescription_num")//医嘱号、处方号、申请单号
    private String prescriptionNum;

    @Column(name = "device_dept_code")//收费项目代码
    private String deviceDeptCode;

    @Column(name = "receipt_num") //收费数量(退费传负数)
    private Integer receiptNum;

    @Column(name = "unit") //单位
    private String unit;

    @Column(name = "unit_price",scale = 4) // 单价
    private BigDecimal unitPrice;

    @Column(name = "total_amount",scale = 4) //总金额
    private BigDecimal totalAmount;

    @Column(name = "exec_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    Date execTime; //执行时间

    @Column(name = "exec_dept")
    private String execDept;//执行科室

    @Column(name = "exam_body_part")
    private String examBodyPart;//检查部位(LIS为检验项目或收费名称

    @Column(name = "patient_type")
    private String patientType;//来源

    @Column(name = "source_code")
    private String sourceCode;//系统来源

    @Column(name = "asset_uid")
    private String assetUid;//APM设备编码 存 AssetInfo uid

    @Column(name = "asset_name")
    private String assetName; //apm设备资产名称

    @Column(name = "depart_num")
    private String departNum;//apm设备编号

    @Column(name = "ksbm")
    private String ksbm;//APM科室编码

    @Column(name = "ks_name")
    private String ksName;//APM科室名称

    @Column(name = "serial_num")
    private String serialNum;// 流水号 --新加

    @Column(name = "category")
    private String category; //类别

    @Column(name = "project_name")
    private String projectName; //项目名称

    @Column(name = "standards")
    private String standards; //规格

    @Column(name = "pay_amount") //应付金额
    private BigDecimal payAmount;

    @Column(name = "billing_department")
    private String billingDepartment; //开单科室

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "asset_counts",scale = 4)
    private BigDecimal assetCounts;//拆分次数

    @Column(name = "is_sum")
    private Boolean isSum;//是否被统计 默认为false

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUid;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUid;

    @Column(name = "data_source_count")
    private int dataSourceCount;//对应

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getPatientRegistrationNum() {
        return patientRegistrationNum;
    }

    public void setPatientRegistrationNum(String patientRegistrationNum) {
        this.patientRegistrationNum = patientRegistrationNum;
    }

    public String getPrescriptionNum() {
        return prescriptionNum;
    }

    public void setPrescriptionNum(String prescriptionNum) {
        this.prescriptionNum = prescriptionNum;
    }

    public String getDeviceDeptCode() {
        return deviceDeptCode;
    }

    public void setDeviceDeptCode(String deviceDeptCode) {
        this.deviceDeptCode = deviceDeptCode;
    }

    public Integer getReceiptNum() {
        return receiptNum;
    }

    public void setReceiptNum(Integer receiptNum) {
        this.receiptNum = receiptNum;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getExecTime() {
        return execTime;
    }

    public void setExecTime(Date execTime) {
        this.execTime = execTime;
    }

    public String getExecDept() {
        return execDept;
    }

    public void setExecDept(String execDept) {
        this.execDept = execDept;
    }

    public String getExamBodyPart() {
        return examBodyPart;
    }

    public void setExamBodyPart(String examBodyPart) {
        this.examBodyPart = examBodyPart;
    }

    public String getPatientType() {
        return patientType;
    }

    public void setPatientType(String patientType) {
        this.patientType = patientType;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getDepartNum() {
        return departNum;
    }

    public void setDepartNum(String departNum) {
        this.departNum = departNum;
    }

    public String getKsbm() {
        return ksbm;
    }

    public void setKsbm(String ksbm) {
        this.ksbm = ksbm;
    }

    public String getKsName() {
        return ksName;
    }

    public void setKsName(String ksName) {
        this.ksName = ksName;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getStandards() {
        return standards;
    }

    public void setStandards(String standards) {
        this.standards = standards;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public String getBillingDepartment() {
        return billingDepartment;
    }

    public void setBillingDepartment(String billingDepartment) {
        this.billingDepartment = billingDepartment;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public BigDecimal getAssetCounts() {
        return assetCounts;
    }

    public void setAssetCounts(BigDecimal assetCounts) {
        this.assetCounts = assetCounts;
    }

    public Boolean getSum() {
        return isSum;
    }

    public void setSum(Boolean sum) {
        isSum = sum;
    }

    public int getDataSourceCount() {
        return dataSourceCount;
    }

    public void setDataSourceCount(int dataSourceCount) {
        this.dataSourceCount = dataSourceCount;
    }
}
