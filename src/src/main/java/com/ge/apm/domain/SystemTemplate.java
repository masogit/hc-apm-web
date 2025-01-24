package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 212688574
 */
@Entity
@Table(name = "system_template")
@Data
public class SystemTemplate implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;

    @Column(name = "file_name", columnDefinition = "CHAR(64)")
    private String fileName;


    @Column(name = "file_type")
    private String fileType;

    @Column(name = "template_type")
    private String templateType;  //模板类型
    @Column(name = "template_type_name")
    private String templateTypeName;  //模板类型名称
    @Column(name = "business_type")
    private String businessType;  //业务类型

    @Column(name = "storage_id")
    private String storageId;



    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Column(name = "create_by")
    private String createBy;
    @Column(name = "create_name")
    private String createName;

    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;

    @Column(name = "last_modified_name", length = 50)
    private String lastModifiedName;


    @Column(name = "last_modified_date")
    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastModifiedDate = new Date();

    @Transient
    private  String valueId;


    @Transient
    private  String onlyId;


    @Transient
    private  String templateTypeNameValue;

    @Transient
    private  String fileNameValue;

    @Transient
    private  String valueStorageId;
    @Transient
    private String siteUid;

    @Transient
    private boolean isEdit;
    public enum SystemTemplateBusinessEnum{
        ARRIVAL_INSTALLATION("ARRIVAL_INSTALLATION","到货安装模块"),
        ASSET("ASSET","设备档案模块"),
        CONTRACT("CONTRACT","合同模块"),
        SUPPLIER("SUPPLIER","供应商模块"),
        HIS_PERFORMANCE("HIS_PERFORMANCE","HIS绩效分析模块"),
        USER("USER","用户模块"),
        MVS("MVS","MVS模块"),
        NURSE_SITE_MAINTAIN("NURSE_SITE_MAINTAIN","日常检查模块"),
        MEASURING_RESULT("MEASURING_RESULT","计量模块"),
        PM("PM","保养"),

        BUDGET("BUDGET","预算模块"),
        ORG_INFO("ORG_INFO","组织机构模块"),
        PARTS("PARTS","备件模块"),
        WORK("WORK","维修模块"),
        INVENTORY("INVENTORY","盘点"),

        ASSET_PERFORMANCE("ASSET_PERFORMANCE","设备绩效模块"),
        ;
        final String code;
        final String name;


        SystemTemplateBusinessEnum(String code,String name) {
            this.code = code;

            this.name = name;

        }

        public static String getName(String code) {
            for (SystemTemplateBusinessEnum template : SystemTemplateBusinessEnum.values()) {
                if (template.getCode().equals(code)) {
                    return template.name;
                }
            }
            return null;
        }
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }

    }

    public enum SystemTemplateEnum{
        //出库入库
        OUT_STOCKED("OUT_STOCKED","出库模板","word",SystemTemplateBusinessEnum.ARRIVAL_INSTALLATION.code,"out_stocked_template.docx"),
        IN_STOCKED("IN_STOCKED","入库模板","word",SystemTemplateBusinessEnum.ARRIVAL_INSTALLATION.code,"in_stocked_template.docx"),

        //到货安装
        ARRIVAL_ITEM("ARRIVAL_ITEM","导出安装单模板","excel",SystemTemplateBusinessEnum.ARRIVAL_INSTALLATION.code,"APM_AssetArrivalItem_Template.xlsx"),
        ARRIVAL_SUPPLIER_REPORT("ARRIVAL_SUPPLIER_REPORT","到货安装单模板","word",SystemTemplateBusinessEnum.ARRIVAL_INSTALLATION.code,"ArrivalSupplierReportTemplate.docx"),

        //到货安装验收模板调整为一个模板 需要用户自行添加子模板使用
        ARRIVAL_ACCEPTANCE_REPORT("ARRIVAL_ACCEPTANCE_REPORT","到货安装验收报告","word",SystemTemplateBusinessEnum.ARRIVAL_INSTALLATION.code,"TechnologyReportTemplate.docx"),
//        TECHNOLOGY("TECHNOLOGY","技术验收模板","word",SystemTemplateBusinessEnum.ARRIVAL_INSTALLATION.code,"TechnologyReportTemplate.docx"),
//        BUSINESS("BUSINESS","商务验收模板","word",SystemTemplateBusinessEnum.ARRIVAL_INSTALLATION.code,"BusinessReportTemplate.docx"),
//        CLINICAL("CLINICAL","临床验收模板","word",SystemTemplateBusinessEnum.ARRIVAL_INSTALLATION.code,"ClinicalReportTemplate.docx"),
//        DETAIL("DETAIL","验收资料详单","word",SystemTemplateBusinessEnum.ARRIVAL_INSTALLATION.code,"DetailReportTemplate.docx"),

//        FIXASSET("FIXASSET","固定资产验收单","word",SystemTemplateBusinessEnum.ARRIVAL_INSTALLATION.code,"AssetReportTemplate.docx"),//这模板应该是废弃的类型是有但是传参没找到

//        FIXASSET_BATCH("FIXASSET_BATCH","固定资产验收单(面单)","word",SystemTemplateBusinessEnum.ARRIVAL_INSTALLATION.code,"AssetReportTemplateBatch.docx"),
//        FIXASSET_BATCH_BOTTOM("FIXASSET_BATCH_BOTTOM","固定资产验收单(底单)","word",SystemTemplateBusinessEnum.ARRIVAL_INSTALLATION.code,"AssetReportTemplateBatchBottom.docx"),

        //调拨
        DEPARTMENT_CONVERT("DEPARTMENT_CONVERT","设备转科模板","word",SystemTemplateBusinessEnum.ASSET.code,"transferorder_template.docx"),
        // 设备
        ASSET_COUNTRYAREA_TEMPLATE("ASSET_COUNTRYAREA_TEMPLATE","资产导入模板-县域租户","excel",SystemTemplateBusinessEnum.ASSET.code,"APM_Asset_Dynamic_CountryArea_Template.xlsx"),
        ASSET_COUNTRYAREA_TEMPLATE_EN("ASSET_COUNTRYAREA_TEMPLATE_EN","资产导入模板-县域租户_en","excel",SystemTemplateBusinessEnum.ASSET.code,"APM_Asset_Dynamic_CountryArea_Template_en.xlsx"),
        ASSET_TEMPLATE("ASSET_TEMPLATE","资产导入模板","excel",SystemTemplateBusinessEnum.ASSET.code,"APM_Asset_Dynamic_Template.xlsx"),
        ASSET_TEMPLATE_EN("ASSET_TEMPLATE_EN","资产导入模板_en","excel",SystemTemplateBusinessEnum.ASSET.code,"APM_Asset_Dynamic_Template_en.xlsx"),

        //合同
        CONTRACT("CONTRACT","合同导入模板","excel",SystemTemplateBusinessEnum.CONTRACT.getCode(),"APM_Contract_Service_Template.xlsx"),
        CONTRACT_EN("CONTRACT_EN","合同导入模板_en","excel",SystemTemplateBusinessEnum.CONTRACT.code,"APM_Contract_Service_Template_en.xlsx"),
        CONTRACT_ITEM("CONTRACT_ITEM","合同相关设备导入模板","excel",SystemTemplateBusinessEnum.CONTRACT.getCode(),"APM_ContractItem_Service_Template.xlsx"),
        CONTRACT_EXPORT("CONTRACT_EXPORT","合同信息导出模板","excel",SystemTemplateBusinessEnum.CONTRACT.getCode(),"APM_Contract_Export_Template.xlsx"),

        //供应商
        SUPPLIER("SUPPLIER","供应商导入模板","excel",SystemTemplateBusinessEnum.SUPPLIER.getCode(),"APM_Supplier.xlsx"),
        //用户
        USER_ACCOUNT("USER_ACCOUNT","用户导入模板","excel",SystemTemplateBusinessEnum.USER.getCode(),"UserAccountImport_Template.xlsx"),
        USER_ACCOUNT_EXPORT("USER_ACCOUNT_EXPORT","用户导出模板","excel",SystemTemplateBusinessEnum.USER.getCode(),"APM_UserAccount_Template.xlsx"),
        //计量
        MEASURING_RESULT("MEASURING_RESULT","计量数据导入模板","excel",SystemTemplateBusinessEnum.MEASURING_RESULT.getCode(),"APM_MEASURING_Template.xls"),
        // HIS模板
        BENEFIT_ANALYSIS("BENEFIT_ANALYSIS","设备效益分析导出模板","excel",SystemTemplateBusinessEnum.HIS_PERFORMANCE.getCode(),"APM_EfficiencyEquipment_export_Template.xlsx"),
        HIS_CLINICAL("HIS_CLINICAL","HIS科室导入模板","excel",SystemTemplateBusinessEnum.HIS_PERFORMANCE.getCode(),"His_clinical.xlsx"),
        HIS_EQUIPMENT_SFXM("HIS_EQUIPMENT_SFXM","HIS收费项目导入模板","excel",SystemTemplateBusinessEnum.HIS_PERFORMANCE.getCode(),"His_sfxm.xlsx"),
        HIS_EQUIPMENT_CONNECT("HIS_EQUIPMENT_CONNECT","HIS联机设备导入模板","excel",SystemTemplateBusinessEnum.HIS_PERFORMANCE.getCode(),"His_equipment_connect.xlsx"),
        HIS_EQUIPMENT_DATA_SOURCE("HIS_EQUIPMENT_DATA_SOURCE","HIS收费明细源数据导入模板","excel",SystemTemplateBusinessEnum.HIS_PERFORMANCE.getCode(),"His_data_source_Template.xlsx"),


        //MVS
        MVS_CONTRACT("MVS_CONTRACT","MVS合同信息模板","excel",SystemTemplateBusinessEnum.MVS.getCode(),"APM_mvs_contract.xlsx"),
        MVS_SITE("MVS_SITE","MVS院区导入模板","excel",SystemTemplateBusinessEnum.MVS.getCode(),"APM_mvs_site.xlsx"),


        //保养
        PM_ORDER_EXPORT("PM_ORDER_EXPORT","保养导出模板","excel",SystemTemplateBusinessEnum.PM.getCode(),"PmOrder_export.xlsx"),
        PM_ORDER_IMPORT("PM_ORDER_IMPORT","保养工单导入模板","excel",SystemTemplateBusinessEnum.PM.getCode(),"PmOrderImport.xls"),
        MEASURE_ELECTRIC_SAFETY("MEASURE_ELECTRIC_SAFETY","电气安全数据导入模板","ps",SystemTemplateBusinessEnum.PM.getCode(),"APM_Electric_Safety_Template.ps"),
        PM_ORDER_REPORT("PM_ORDER_REPORT","保养工单报告模板","word",SystemTemplateBusinessEnum.PM.getCode(),"maintenance-template_zh.docx"),

        //预算
        BUDGET_IMPORT("BUDGET_IMPORT","预算导入模板","excel",SystemTemplateBusinessEnum.BUDGET.getCode(),"Budget_Import_Template.xlsx"),
        OVER_FIFTY_THOUSAND("OVER_FIFTY_THOUSAND","医疗仪器设备年度采购申报表模板","word",SystemTemplateBusinessEnum.BUDGET.getCode(),"over_fifty_thousand.docx"),
        BUDGET_BAP_EXP("BUDGET_BAP_EXP","申购导出模板","excel",SystemTemplateBusinessEnum.BUDGET.getCode(),"APM_Budget_BAP_Exp_Template.xlsx"),
        ASSET_ARGUMENT_REPORT("ASSET_ARGUMENT_REPORT","预算设备论证报告","excel",SystemTemplateBusinessEnum.BUDGET.getCode(),"单价50万元以上（含50万）设备论证报告.docx"),

        //组织结构
        ORG_INFO("ORG_INFO","组织机构导入模板","excel",SystemTemplateBusinessEnum.ORG_INFO.getCode(),"APM_Org_Template.xlsx"),
        BUILD_MANAGEMENT("BUILD_MANAGEMENT","楼宇管理导出模板","excel",SystemTemplateBusinessEnum.ORG_INFO.getCode(),"APM_Building_Template.xlsx"),




        //设备绩效报表模板
        SMALL_ASSET_PERFORMANCE("SMALL_ASSET_PERFORMANCE","设备绩效报表模板","excel",SystemTemplateBusinessEnum.ASSET_PERFORMANCE.getCode(),"APM_SmallAssetPerformance_Template.xlsx"),
        SMALL_ASSET_PERFORMANCE_CONFIG("SMALL_ASSET_PERFORMANCE_CONFIG","设备绩效报表配置模板","excel",SystemTemplateBusinessEnum.ASSET_PERFORMANCE.getCode(),"APM_SmallAssetPerformanceConfig_Template.xlsx"),

        //报废
        BATCH_ASSET_TERMINATION("BATCH_ASSET_TERMINATION","批量报废文件模板","excel",SystemTemplateBusinessEnum.ASSET.getCode(),"APM_Asset_Termination_Template.xlsx"),

        //
        ASSET_RISK_ASSESSMENT("ASSET_RISK_ASSESSMENT","医疗设备风险评估模板","word",SystemTemplateBusinessEnum.ASSET.getCode(),"assetRiskAssessment.docx"),

        //日常检查
        NURSE_SITE_MAINTAIN("NURSE_SITE_MAINTAIN","日常检查报表模板","excel",SystemTemplateBusinessEnum.NURSE_SITE_MAINTAIN.getCode(),"APM_NurseSiteMaintain_Template.xlsx"),
        NURSE_SITE_MAINTAIN_DETAIL("NURSE_SITE_MAINTAIN_DETAIL","日常检查报表详情模板","excel",SystemTemplateBusinessEnum.NURSE_SITE_MAINTAIN.getCode(),"APM_NurseSiteMaintainDetail_Template.xlsx"),

        //备件
        PARTS_OUT_BOUND_ORDER("PARTS_OUT_BOUND_ORDER","备件出库单模板","word",SystemTemplateBusinessEnum.PARTS.getCode(),"outBoundOrderReportTemplate.docx"),


        //资产分类
        ASSET_TYPE_GROUP("ASSET_TYPE_GROUP","资产分类模板","excel",SystemTemplateBusinessEnum.ASSET.getCode(),"APM_Auth_Template.xlsx"),
        ASSET_PURCHASE("ASSET_PURCHASE","采购清单模板","excel",SystemTemplateBusinessEnum.ASSET.getCode(),"APM_PURCHASE_Template.xls"),

        WORK_ORDER_REPORT("WORK_ORDER_REPORT","维修工单报告模板","word",SystemTemplateBusinessEnum.WORK.getCode(),"workOrderTemplate.docx"),

        INVENTORY_ORDER_LIST("INVENTORY_ORDER_LIST","盘点工单列表模板","excel",SystemTemplateBusinessEnum.INVENTORY.getCode(),"inventoryOrderList.xlsx"),

        ;
        final String code;
        final String name;
        final String fileType;


        final String businessType;

        final String fileName;

        SystemTemplateEnum(String code,String name,String fileType,String businessType,String fileName) {
            this.code = code;

            this.name = name;

            this.fileType = fileType;

            this.fileName = fileName;
            this.businessType = businessType;
        }

        public static String getName(String code) {
            for (SystemTemplateEnum template : SystemTemplateEnum.values()) {
                if (template.getCode().equals(code)) {
                    return template.name;
                }
            }
            return null;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public String getFileType() {
            return fileType;
        }


        public String getFileName() {
            return fileName;
        }

        public String getBusinessType() {
            return businessType;
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getStorageId() {
        return storageId;
    }

    public void setStorageId(String storageId) {
        this.storageId = storageId;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getLastModifiedName() {
        return lastModifiedName;
    }

    public String getFileType() {
        return fileType;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setLastModifiedName(String lastModifiedName) {
        this.lastModifiedName = lastModifiedName;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getTemplateTypeName() {
        return templateTypeName;
    }

    public void setTemplateTypeName(String templateTypeName) {
        this.templateTypeName = templateTypeName;
    }

    public String getValueId() {
        return valueId;
    }

    public void setValueId(String valueId) {
        this.valueId = valueId;
    }

    public String getTemplateTypeNameValue() {
        return templateTypeNameValue;
    }

    public void setTemplateTypeNameValue(String templateTypeNameValue) {
        this.templateTypeNameValue = templateTypeNameValue;
    }

    public String getOnlyId() {
        return onlyId;
    }

    public void setOnlyId(String onlyId) {
        this.onlyId = onlyId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (onlyId != null ? onlyId.hashCode() : 0);
        return hash;
    }

    /**
     * Warning: this method won't work in the case the id fields are not set
     *
     * @param object compare target
     * @return true if the object has the same id
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SystemTemplate)) {
            return false;
        }
        SystemTemplate other = (SystemTemplate) object;
        return (this.onlyId != null || other.onlyId == null) && (this.onlyId == null || this.onlyId.equals(other.onlyId));
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.SystemTemplate[ id=" + id + " ]";
    }

}
