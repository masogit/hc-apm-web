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
@Table(name = "shared_asset_type")
public class SharedAssetType implements Serializable{
    
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id",columnDefinition = "CHAR(32)")
    private String id;//主键
    
    @Column(name = "swap_name")
    private String swapName;//调剂设备名称
    
    @Column(name = "charging_type")
    private String chargingType;//计价方式  1.次  2.小时（整点半点） 3.天  4. 自然时间
    
    @Column(name = "unit_price")
    private Double unitPrice;//单价
    
    @Column(name = "unit")
    private String unit;//计量单位
    
    @Column(name = "remark")
    private String remark;//备注
    
    @Column(name = "create_by")
    private String createBy;//创建人
    
    @Column(name = "modify_by")
    private String modifyBy;//修改人
    
    @Column(name = "create_date")
    @Temporal(TemporalType.DATE)
    private Date createDate;//创建日期
    
    @Column(name = "modify_date")
    @Temporal(TemporalType.DATE)
    private Date modifyDate;//修改日期
    
    
    //新增字段（数据权限四个字段）
    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;
    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;
    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;
    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSwapName() {
        return swapName;
    }

    public void setSwapName(String swapName) {
        this.swapName = swapName;
    }

    public String getChargingType() {
        return chargingType;
    }

    public void setChargingType(String chargingType) {
        this.chargingType = chargingType;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
    
    
    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof SharedAssetType)) {
            return false;
        }
        SharedAssetType other = (SharedAssetType) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.SwapType.SharedAssetType[ id=" + getId() + " ]";
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
    
    @Column(name = "accessories")
    private String accessories;//附件信息
    
//    public List <SharedAssetTypeAccesssory>  getAccessoriesAsList(){ // checkbox 调用
//        try{
//            List <SharedAssetTypeAccesssory> accessoriesList = JSON.parseObject(accessories, List.class);
//            return accessoriesList;
//        }catch(Exception e){
//            return new ArrayList <SharedAssetTypeAccesssory>();
//        }
//    }
    
//    public void setAccessoriesAsList (List <SwapTypeAttach> accessoriesList){
//        accessories = JSON.toJSONString(accessoriesList);
//
//    }
    
    @Column(name = "accessories_long")
    private String accessoriesLong;//附件信息

    public String getAccessoriesLong() {
        return accessoriesLong;
    }

    public void setAccessoriesLong(String accessoriesLong) {
        this.accessoriesLong = accessoriesLong;
    }
    
    public String getAccessories() {
        return accessories;
    }

    public void setAccessories(String accessories) {
        this.accessories = accessories;
    }
    
    @Transient
    private List<SharedAssetTypeAccesssory> accessoryList;
//
    public void setAccessoryList(List<SharedAssetTypeAccesssory> accessoryList){
        this.accessoryList = accessoryList;
        accessories = JSON.toJSONString(accessoryList);
//        System.out.println(accessories);
    }
    
    public List<SharedAssetTypeAccesssory> getAccessoryList(){
        if(accessoryList!=null) return accessoryList;

        if (accessories==null)
            accessoryList = new ArrayList<>();
        else
            accessoryList = JSON.parseArray(accessories, SharedAssetTypeAccesssory.class);

        return accessoryList;
    }
//
}
