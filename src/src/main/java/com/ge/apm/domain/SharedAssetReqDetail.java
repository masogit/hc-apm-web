/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.domain;

import com.alibaba.fastjson.JSON;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author caoyong
 */
@Entity
@Table(name = "shared_asset_req_detail")//借还申请设备详情
public class SharedAssetReqDetail implements Serializable{
    
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id",columnDefinition = "CHAR(32)")
    private String id;
    
    @Column(name = "shared_id")
    private String sharedId;//主表id
    
    @Column(name = "borrow_id")
    private String borrowId;
    
    @Column(name = "return_id")
    private String returnId;
    
    @Column(name = "asset_info_id")
    private String assetInfoId;//资产设备id
    
    @Column(name = "shared_asset_type")
    private String sharedAssetTypeId;//调剂设备id 
    
    @Column(name = "asset_number")
    private String assetNumber;//资产设备编号
    
    @Column(name = "asset_name")
    private String assetName;//资产设备名称
    
    @Column(name = "unit")
    private String unit;//计价单位
    
    @Column(name = "unit_price")
    private Double unitPrice;//单价
    
     @Column(name = "charging_type")
    private String chargingType;//计价方式
//    @Column(name = "return_attach_info")
//    private String returnAttachInfo;//归还附件情况
//    
//    @Column(name = "borrow_attach_info")
//    private String borrowAttachInfo;//借用附件情况
//    
    @Column(name = "state")
    private String state;//状态 0待归还 1已归还
    
    @Column(name = "charging_begin_time")
    @Temporal(TemporalType.TIMESTAMP) 
    private Date chargingBeginTime;//计费开始时间
    
    @Column(name = "charging_end_time")
    @Temporal(TemporalType.TIMESTAMP) 
    private Date chargingEndTime;//计费结束时间
    
    @Column(name = "count_qty")
    private Integer countQty;//计费数量
    
    @Column(name = "cost",columnDefinition = "decimal(11,2)")
    private Double cost;//费用
    
    @Column(name = "accepter")
    private String accepter;//设备接收人：默认等于申请人或上一条
    
    @Column(name = "accessories", columnDefinition = "text")
    private String accessories;//附件信息
    
    @Column(name = "financing_num")
    private String financingNum;
    
    @Column(name = "remark")
    private String remark;//备注
    
    //新增字段（数据权限四个字段）
    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;
    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;
    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;
    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;
    
    @Column(name = "clinical_dept_uid")//借用申请科室id
    private String clinicalDeptUid;
    
    @Column(name = "clinical_dept_for_return_uid")//归还申请科室id
    private String clinicalDeptForReturnUid;
    
    @Column(name = "clinical_dept_for_return_name")//归还申请科室名称
    private String clinicalDeptForReturnName;
    
    @Column(name = "clinical_dept_name")//借用申请科室名称
    private String clinicalDeptName;
    
    @Column(name = "borrow_deal_uid")
    private String borrowDealUid;//借用受理人
    
    @Column(name = "return_deal_uid")
    private String returnDealUid;//归还受理人
    
    @Column(name = "borrow_deal_date")
    @Temporal(TemporalType.DATE)
    private Date borrowDealDate;//借用受理日期
    
    @Column(name = "return_deal_date")
    @Temporal(TemporalType.DATE)
    private Date returnDealDate;//归还受理日期
    
    @Column(name = "create_by")
    private String createBy;//创建人
    
    @Column(name = "modify_by")
    private String modifyBy;//修改人
    
    @Column(name = "create_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;//创建日期
    
    @Column(name = "modify_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifyDate;//修改日期
    
    @Transient
    private String rentalTime;
    
    @Transient
    private Double totalAmount;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "clinical_dept_uid", referencedColumnName = "uid", unique = true, insertable = false, updatable = false)
    private OrgInfo clinicalDept;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "borrow_id", referencedColumnName = "id", unique = true, insertable = false, updatable = false)
    private SharedAssetRequest leaseRequest;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "return_id", referencedColumnName = "id", unique = true, insertable = false, updatable = false)
    private SharedAssetRequest returnRequest;

    @Column(name = "is_intact")
    private boolean isIntact;

    @Column(name = "compensation_amount", columnDefinition = "decimal(11,2)")
    private Double compensationAmount;

    @Column(name = "req_comment", columnDefinition = "VARCHAR(256)")
    private String reqComment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSharedId() {
        return sharedId;
    }

    public void setSharedId(String sharedId) {
        this.sharedId = sharedId;
    }

    public String getAssetInfoId() {
        return assetInfoId;
    }

    public void setAssetInfoId(String assetInfoId) {
        this.assetInfoId = assetInfoId;
    }

    public String getAssetNumber() {
        return assetNumber;
    }

    public void setAssetNumber(String assetNumber) {
        this.assetNumber = assetNumber;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

//    public String getReturnAttachInfo() {
//        return returnAttachInfo;
//    }
//
//    public void setReturnAttachInfo(String returnAttachInfo) {
//        this.returnAttachInfo = returnAttachInfo;
//    }
//
//    public String getBorrowAttachInfo() {
//        return borrowAttachInfo;
//    }
//
//    public void setBorrowAttachInfo(String borrowAttachInfo) {
//        this.borrowAttachInfo = borrowAttachInfo;
//    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getChargingBeginTime() {
        return chargingBeginTime;
    }

    public void setChargingBeginTime(Date chargingBeginTime) {
        this.chargingBeginTime = chargingBeginTime;
    }

    public Date getChargingEndTime() {
        return chargingEndTime;
    }

    public void setChargingEndTime(Date chargingEndTime) {
        this.chargingEndTime = chargingEndTime;
    }

    public Integer getCountQty() {
        return countQty == null ? 0 : countQty;
    }

    public void setCountQty(Integer countQty) {
        this.countQty = countQty;
    }

    public Double getCost() {

        return cost == null ? 0 : cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public String getAccepter() {
        return accepter;
    }

    public void setAccepter(String accepter) {
        this.accepter = accepter;
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

    public String getBorrowDealUid() {
        return borrowDealUid;
    }

    public void setBorrowDealUid(String borrowDealUid) {
        this.borrowDealUid = borrowDealUid;
    }

    public String getReturnDealUid() {
        return returnDealUid;
    }

    public void setReturnDealUid(String returnDealUid) {
        this.returnDealUid = returnDealUid;
    }

    public Date getBorrowDealDate() {
        return borrowDealDate;
    }

    public void setBorrowDealDate(Date borrowDealDate) {
        this.borrowDealDate = borrowDealDate;
    }

    public Date getReturnDealDate() {
        return returnDealDate;
    }

    public void setReturnDealDate(Date returnDealDate) {
        this.returnDealDate = returnDealDate;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getModifyBy() {
        return modifyBy;
    }

    public void setModifyBy(String modifyBy) {
        this.modifyBy = modifyBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }
    
    public String getAccessories() {
        return accessories;
    }

    public void setAccessories(String accessories) {
        this.accessories = accessories;
    }
    
    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof SharedAssetReqDetail)) {
            return false;
        }
        SharedAssetReqDetail other = (SharedAssetReqDetail) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "com.ge.apm.domain.Iou.IouAssetDetail[ id=" + getId() + " ]";
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }
    
    @PrePersist
    public void initializeUid() {
        if(getId()==null)
            setId(UUID.randomUUID().toString().replace("-", ""));
    }

    /**
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * @return the unitPrice
     */
    public Double getUnitPrice() {
        return unitPrice;
    }

    /**
     * @param unitPrice the unitPrice to set
     */
    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * @return the chargingType
     */
    public String getChargingType() {
        return chargingType;
    }

    /**
     * @param chargingType the chargingType to set
     */
    public void setChargingType(String chargingType) {
        this.chargingType = chargingType;
    }

    /**
     * @return the sharedAssetTypeId
     */
    public String getSharedAssetTypeId() {
        return sharedAssetTypeId;
    }

    /**
     * @param sharedAssetTypeId the sharedAssetTypeId to set
     */
    public void setSharedAssetTypeId(String sharedAssetTypeId) {
        this.sharedAssetTypeId = sharedAssetTypeId;
    }

    /**
     * @return the financingNum
     */
    public String getFinancingNum() {
        return financingNum;
    }

    /**
     * @param financingNum the financingNum to set
     */
    public void setFinancingNum(String financingNum) {
        this.financingNum = financingNum;
    }
    public String getClinicalDeptUid() {
        return clinicalDeptUid;
    }

    public void setClinicalDeptUid(String clinicalDeptUid) {
        this.clinicalDeptUid = clinicalDeptUid;
    }

    public String getClinicalDeptName() {
        return clinicalDeptName;
    }

    public void setClinicalDeptName(String clinicalDeptName) {
        this.clinicalDeptName = clinicalDeptName;
    }
    
    @Transient
    private List<SharedAssetTypeAccesssory> accessoryList;
    
//    public void setAccessoryList(List<SharedAssetTypeAccesssory> accessoryList){
//        this.accessoryList = accessoryList;
////         System.out.println(accessoryList);
////         String arr[]={};
////         for(i=0;i<=accessoryList.size();i++){
////             
////         }
//         String str;
//         str=accessoryList.toString();
//         String str_sub = "";
//            if (str.length()>2){
//              str_sub = str.substring(1, str.length()-1);
//            }
//            String str_arr[] ;
//            
//            str_arr = str_sub.split(",");
//            String json = "";
//            for (int i = 0; i < str_arr.length; i++) {
//               if(json.length() > 0){
//                json = json + ",";
//               }
//               json = json + "{" + "\"name\":\"" + str_arr[i] + "\"}";
//            }
//            System.out.println("11111[" + json + "]");
//        accessories ="[" + json + "]";
//        System.out.println(accessories);
//    }
    public void setAccessoryList(List<SharedAssetTypeAccesssory> accessoryList){
        this.accessoryList = accessoryList;
        accessories = JSON.toJSONString(accessoryList);
        System.out.println(accessories);
    }
//
    public List<SharedAssetTypeAccesssory> getAccessoryList(){
        if(accessoryList!=null) return accessoryList;

        if (accessories==null){
            accessoryList = new ArrayList<>();
        }
        else{
            accessoryList = JSON.parseArray(accessories, SharedAssetTypeAccesssory.class);
        }
//        System.out.println(111+accessories);
//        System.out.println(accessoryList.get(0).getName());
        return accessoryList;
    }
    
//     public String[] getAccessoryList(){
//         String[] array = new String[100];
//          accessoryList = JSON.parseArray(accessories, SharedAssetTypeAccesssory.class);
//          if(accessoryList!=null){
//          for(int i=0;i<accessoryList.size();i++){
//              array[i] = accessoryList.get(i).getName();
//          }
//          }
//        return array;
//    }

    /**
     * @return the borrowId
     */
    public String getBorrowId() {
        return borrowId;
    }

    /**
     * @param borrowId the borrowId to set
     */
    public void setBorrowId(String borrowId) {
        this.borrowId = borrowId;
    }

    /**
     * @return the returnId
     */
    public String getReturnId() {
        return returnId;
    }

    /**
     * @param returnId the returnId to set
     */
    public void setReturnId(String returnId) {
        this.returnId = returnId;
    }

    /**
     * @return the clinicalDeptForReturnUid
     */
    public String getClinicalDeptForReturnUid() {
        return clinicalDeptForReturnUid;
    }

    /**
     * @param clinicalDeptForReturnUid the clinicalDeptForReturnUid to set
     */
    public void setClinicalDeptForReturnUid(String clinicalDeptForReturnUid) {
        this.clinicalDeptForReturnUid = clinicalDeptForReturnUid;
    }

    /**
     * @return the clinicalDeptForReturnName
     */
    public String getClinicalDeptForReturnName() {
        return clinicalDeptForReturnName;
    }

    /**
     * @param clinicalDeptForReturnName the clinicalDeptForReturnName to set
     */
    public void setClinicalDeptForReturnName(String clinicalDeptForReturnName) {
        this.clinicalDeptForReturnName = clinicalDeptForReturnName;
    }

	public String getRentalTime() {
		return rentalTime;
	}

	public void setRentalTime(String rentalTime) {
		this.rentalTime = rentalTime;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public OrgInfo getClinicalDept() {
		return clinicalDept;
	}

	public void setClinicalDept(OrgInfo clinicalDept) {
		this.clinicalDept = clinicalDept;
	}

	public SharedAssetRequest getLeaseRequest() {
		return leaseRequest;
	}

	public void setLeaseRequest(SharedAssetRequest leaseRequest) {
		this.leaseRequest = leaseRequest;
	}

	public SharedAssetRequest getReturnRequest() {
		return returnRequest;
	}

	public void setReturnRequest(SharedAssetRequest returnRequest) {
		this.returnRequest = returnRequest;
	}

    public boolean isIntact() {
        return isIntact;
    }

    public void setIntact(boolean intact) {
        isIntact = intact;
    }

    public Double getCompensationAmount() {
        return compensationAmount;
    }

    public void setCompensationAmount(Double compensationAmount) {
        this.compensationAmount = compensationAmount;
    }

    public String getReqComment() {
        return reqComment;
    }

    public void setReqComment(String reqComment) {
        this.reqComment = reqComment;
    }
}
