package com.ge.apm.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.Size;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.baomidou.mybatisplus.annotations.TableField;
import com.ge.apm.service.adverseEvent.AdverseEventDateConverter;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import webapp.framework.web.WebUtil;
import com.alibaba.excel.annotation.ExcelProperty;

/**
 * Created by 212605082 on 2017/6/19.
 */
@Entity
@Table(name = "inventory_order_detail")
public class InventoryOrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;
    @ExcelIgnore
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;
    //    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "inventory_order_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
//    private InventoryOrder inventoryOrder;
    @ExcelIgnore
    @Column(name = "inventory_order_id")
    private String inventoryOrderId;
    @ExcelIgnore
    @Column(name = "site_id")
    private Integer siteId;
    @ExcelIgnore
    @Column(name = "hospital_id")
    private Integer hospitalId;

    @ExcelIgnore
    @Column(name = "qr_code")
    private String qrCode;

    @ExcelProperty(value = "设备编号（院方）",index = 0)
    @Column(name = "depart_num")
    private String departNum;
    @ExcelIgnore
    @Column(name = "asset_group")
    private Integer assetGroup;
    @ExcelIgnore
    @Column(name = "asset_group_name")
    private String assetGroupName;
    @ExcelIgnore
    @Column(name = "org_id")
    private Integer orgId;
    @ExcelIgnore
    @Column(name = "org_name")
    private String orgName;

    @ExcelProperty(value = "问题描述",index = 10)
    @Column(name = "comment")
    private String comment;
    @ExcelIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "bo_uuid", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Fetch(FetchMode.SUBSELECT)
    private List<V2_BlobObject> attachments;
    @ExcelIgnore
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "clinical_dept_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OrgInfo clinicalDept;
    @ExcelIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private AssetInfo assetInfo;
    //0:未盘点，1：已盘点，2：无台账, 3:有问题 ,4:无工单
    @ExcelIgnore
    @Column(name = "status")
    private Integer status;

    @ExcelIgnore
    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    @Size(max = 32)
    private String tenantUID;

    @ExcelIgnore
    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @ExcelIgnore
    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @ExcelIgnore
    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @ExcelIgnore
    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;

    @ExcelIgnore
    @Column(name = "last_modified_date")
    private Date lastModifiedDate ;

    @ExcelIgnore
    @Column(name = "clinical_dept_uid", columnDefinition = "CHAR(32)")
    private String clinicalDeptUID;//设备使用科室


    @ExcelProperty(value = "所属科室",index = 3)
    @Size(max = 64)
    @Column(name = "clinical_dept_name")
    private String clinicalDeptName;

    @ExcelIgnore
    @Column(name = "asset_type", length = 50)
    private String assetType;

    @ExcelIgnore
    @Column(name = "is_last")
    private Boolean isLast;

    @Column(name = "inventory_method")
    private String inventoryMethod;

    @ExcelProperty(value = "未通过项",index = 11)
    @Transient
    private String inventoryCheckListItems;

    @ExcelProperty(value = "使用科室",index = 11)
    @Transient
    private String assetDeptName;

    @ExcelProperty(value = "已解决",index = 12)
    @Transient
    private String isSolved;

    @ExcelIgnore
    @Transient
    private Date planEndTime;

    @ExcelIgnore
    @Transient
    private Date planStartTime;

    @ExcelIgnore
    @Transient
    @Getter @Setter
    private Integer assetId;


    @ExcelProperty(value = "设备型号",index = 2)
    @Transient
    @Getter @Setter
    private String functionType;

    @ExcelIgnore
    @Transient
    @Getter @Setter
    private String financingNum;

    @ExcelIgnore
    @Transient
    @Getter @Setter
    private String assetStatus;

    @ExcelIgnore
    @Transient
    @Getter @Setter
    private String assetOwnerName;


    @ExcelProperty(value = "采购价格",index = 5)
    @Transient
    @Getter @Setter
    private Double purchasePrice;//采购价格(元)

    @ExcelProperty(value = "账面数量",index = 6)
    @Transient
    @Getter @Setter
    private int   quantity=1;//账面数量


    @ExcelProperty(value = "入账时间",index = 7,converter = AdverseEventDateConverter.class)
    @Transient
    @Getter @Setter
    @Temporal(TemporalType.DATE)
    private Date purchaseDate;//采购日期

    @ExcelProperty(value = "盘点数量",index = 8)
    @Transient
    private int inventoryCount;//盘点数量

    @ExcelProperty(value = "当前状态",index = 9)
    @Transient
    private String assetStatusName;





    @ExcelProperty(value = "盘亏",index = 13)
    @Transient
    private String  inventoryLoss;

    @ExcelProperty(value = "盘盈",index = 14)
    @Transient
    private String  inventoryProfit;

    @ExcelProperty(value = "盘点状态",index = 15)
    @Transient
    private String statusName;

    @ExcelProperty(value = "备注",index = 16)
    @Transient
    @Getter @Setter
    private String assetComments;


    @ExcelProperty(value = "制造商",index = 17)
    @Transient
    @Getter @Setter
    private String makerShortName;

    @ExcelProperty(value = "盘点时间",index = 18)
    @Transient
    private String  inventoryDate;

    @ExcelProperty(value = "盘点人",index = 19)
    @Transient
    private String  inventoryName;


    @ExcelProperty(value = "按安装位置",index = 4)
    @Transient
    @Getter @Setter
    private String locationName;


    @ExcelProperty(value = "设备名称",index = 1)
    @Transient
    @Getter @Setter
    private String name;

    @ExcelIgnore
    @Transient
    @Getter @Setter
    private String assetUid;

    @ExcelIgnore
    @Transient
    @TableField(exist = false)
    private String encodeAssetDetailStr;


    public String getClinicalDeptUID() {
        return clinicalDeptUID;
    }

    public void setClinicalDeptUID(String clinicalDeptUID) {
        this.clinicalDeptUID = clinicalDeptUID;
    }

    public String getClinicalDeptName() {
        return clinicalDeptName;
    }

    public void setClinicalDeptName(String clinicalDeptName) {
        this.clinicalDeptName = clinicalDeptName;
    }

    public Date getPlanStartTime() {
        return planStartTime;
    }

    public void setPlanStartTime(Date planStartTime) {
        this.planStartTime = planStartTime;
    }

    public Date getPlanEndTime() {
        return planEndTime;
    }

    public void setPlanEndTime(Date planEndTime) {
        this.planEndTime = planEndTime;
    }

    public String getInventoryCheckListItems() {
        return inventoryCheckListItems;
    }

    public void setInventoryCheckListItems(String inventoryCheckListItems) {
        this.inventoryCheckListItems = inventoryCheckListItems;
    }

    public String getAssetDeptName() {
        return assetDeptName;
    }

    public void setAssetDeptName(String assetDeptName) {
        this.assetDeptName = assetDeptName;
    }

    public String getIsSolved() {
        return isSolved;
    }

    public void setIsSolved(String isSolved) {
        this.isSolved = isSolved;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInventoryOrderId() {
        return inventoryOrderId;
    }

    public void setInventoryOrderId(String inventoryOrderId) {
        this.inventoryOrderId = inventoryOrderId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Integer getAssetGroup() {
        return assetGroup;
    }

    public void setAssetGroup(Integer assetGroup) {
        this.assetGroup = assetGroup;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public String getAssetGroupName() {
        return assetGroupName;
    }

    public void setAssetGroupName(String assetGroupName) {
        this.assetGroupName = assetGroupName;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<V2_BlobObject> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<V2_BlobObject> attachments) {
        this.attachments = attachments;
    }

    public OrgInfo getClinicalDept() {
        return clinicalDept;
    }

    public void setClinicalDept(OrgInfo clinicalDept) {
        this.clinicalDept = clinicalDept;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public String getDepartNum() {
        return departNum;
    }

    public void setDepartNum(String departNum) {
        this.departNum = departNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
    }

    public String getInstitutionUID() {
        return institutionUID;
    }

    public void setInstitutionUID(String institutionUID) {
        this.institutionUID = institutionUID;
    }

    public String getHospitalUID() {
        return hospitalUID;
    }

    public void setHospitalUID(String hospitalUID) {
        this.hospitalUID = hospitalUID;
    }

    public String getSiteUID() {
        return siteUID;
    }

    public void setSiteUID(String siteUID) {
        this.siteUID = siteUID;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public Boolean getIsLast() {
        return isLast;
    }

    public void setIsLast(Boolean isLast) {
        this.isLast = isLast;
    }

    public String getInventoryMethod() {
        return inventoryMethod;
    }

    public void setInventoryMethod(String inventoryMethod) {
        this.inventoryMethod = inventoryMethod;
    }

    public String getEncodeAssetDetailStr() {
        String url = "actionName=View&selectedid="
                + getAssetUid()
                + "&asset_name=" + getName()
                + "&terminate=" + Boolean.parseBoolean(WebUtil.getRequestParameter("terminate"));
        url = UrlParamUtil.encodeUrlQueryString(url);
        return url;
    }

    public void setEncodeAssetDetailStr(String encodeAssetDetailStr) {
        this.encodeAssetDetailStr = encodeAssetDetailStr;
    }

    public String  getAssetStatusName(){
        // 1:正常,2:停机,3:限制功能使用,4:待报废,5:停用
        switch (getAssetStatus()){
            case "1":
                return "正常";
            case "2":
                return "停机";
            case "3":
                return "限制功能使用";
            case "4":
                return "待报废";
            case "5":
                return "停用";

        }
        return null;
    }

    public String  getStatusName(){
        //0:未盘点，1：已盘点，2：无台账, 3:有问题 ,4:无工单
        switch (getStatus()){
            case 0:
                return "未盘点";
            case 1:
                return "已盘点";
            case 2:
                return "无台账";
            case 3:
                return "有问题";
            case 4:
                return "无工单";

        }
        return null;
    }

    public String  getInventoryName( ) {
        if(status==0){
            return "";
        }
        return getLastModifiedBy();
    }

    public String getInventoryDate(){
        if(status==0){
            return "";
        }
        return DateUtil.formatDate(getLastModifiedDate());
    }

    public int getInventoryCount(){
        if(status==1){
            return 1;
        }
        return 0;
    }
    public String getInventoryLoss(){
        if(status!=1){
            return "盘亏";
        }
        return "";
    }


    public String getInventoryProfit(){
        if(status==1){
            return "盘盈";
        }
        return "";
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof InventoryOrderDetail)) {
            return false;
        }
        InventoryOrderDetail other = (InventoryOrderDetail) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}