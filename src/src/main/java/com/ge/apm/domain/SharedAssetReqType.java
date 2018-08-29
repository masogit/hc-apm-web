/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author caoyong
 */
@Entity
@Table(name = "shared_asset_req_type")//借还申请设备类型
public class SharedAssetReqType implements Serializable{
    
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id",columnDefinition = "CHAR(32)")
    private String id;
    
    @Column(name = "shared_id")
    private String sharedId;//主表id
    
    @Column(name = "swap_name")
    private String swapName;//调剂设备名称
    
    @Column(name = "shared_asset_type")
    private String sharedAssetTypeId;//调剂设备id

    @Column(name = "apply_qty")
    private int applyQty;//申请数量
    
    @Column(name = "except_qty")
    private int exceptQty;//预计借用数
    
    @Column(name = "actual_qty")
    private int actualQty;//实际数量
    
    @Column(name = "return_qty")
    private int returnQty;//归还数量
    
    @Column(name = "expect_date")
    @Temporal(TemporalType.DATE)
    private Date expectDate;//预计归还日期
    
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

    public String getSwapName() {
        return swapName;
    }

    public void setSwapName(String swapName) {
        this.swapName = swapName;
    }

    public String getSharedAssetTypeId() {
        return sharedAssetTypeId;
    }

    public void setSharedAssetTypeId(String sharedAssetTypeId) {
        this.sharedAssetTypeId = sharedAssetTypeId;
    }

    public int getApplyQty() {
        return applyQty;
    }

    public void setApplyQty(int applyQty) {
        this.applyQty = applyQty;
    }

    public int getActualQty() {
        return actualQty;
    }

    public void setActualQty(int actualQty) {
        this.actualQty = actualQty;
    }

    public int getReturnQty() {
        return returnQty;
    }

    public void setReturnQty(int returnQty) {
        this.returnQty = returnQty;
    }

    public Date getExpectDate() {
        return expectDate;
    }

    public void setExpectDate(Date expectDate) {
        this.expectDate = expectDate;
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
    
    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof SharedAssetReqType)) {
            return false;
        }
        SharedAssetReqType other = (SharedAssetReqType) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.Iou.IouApplyDetail[ id=" + getId() + " ]";
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
    
    public int getExceptQty() {
        return exceptQty;
    }

    public void setExceptQty(int exceptQty) {
        this.exceptQty = exceptQty;
    }
}
