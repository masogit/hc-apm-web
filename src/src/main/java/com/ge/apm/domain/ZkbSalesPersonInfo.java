package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.*;

/**
 * @autor by lyj 503277717
 * @date 2022/8/24
*/

@Entity
@Table(name = "zkb_sales_person_info")
public class ZkbSalesPersonInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    /**
     * 姓名
     */
    @Column(name = "name")
    private String name;

    /**
     * 电话
     */
    @Column(name = "phone")
    private String phone;

    /**
     * 邮箱
     */
    @Pattern(regexp = "^$|^[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?", message = "{javax.validation.constraints.Pattern.email.message}")
    @Column(name = "email")
    private String email;

    /**
     * 备件选择
     */
    @Column(name = "spare_parts_selection")
    private Boolean sparePartsSelection;

    /**
     * 探头选择
     */
    @Column(name = "probe_selection")
    private Boolean probeSelection;



    //创建人
    @Column(name = "created_by")
    private String createdBy;

    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "created_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;

    //修改人
    @Column(name = "modified_by")
    private String modifiedBy;

    //修改时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "modified_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedTime;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "zkbSalesPersonInfo", fetch = FetchType.EAGER)
    private List<ZkbSalesTenantAssociate> zkbSalesTenantAssociateList = new ArrayList<ZkbSalesTenantAssociate>();

    /**
     * 订单类型
     */
    @Transient
    private String orderType;

    public List<ZkbSalesTenantAssociate> getZkbSalesTenantAssociateList() {
        return zkbSalesTenantAssociateList;
    }

    public void setZkbSalesTenantAssociateList(List<ZkbSalesTenantAssociate> zkbSalesTenantAssociateList) {
        this.zkbSalesTenantAssociateList = zkbSalesTenantAssociateList;
    }

    public String getOrderType() {
        StringBuilder str = new StringBuilder();
        if(this.probeSelection){
            str.append("探头").append(" ");
        };
        if(this.sparePartsSelection){
            str.append("备件");
        };
        orderType = str.toString();
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getSparePartsSelection() {
        return sparePartsSelection;
    }

    public void setSparePartsSelection(Boolean sparePartsSelection) {
        this.sparePartsSelection = sparePartsSelection;
    }

    public Boolean getProbeSelection() {
        return probeSelection;
    }

    public void setProbeSelection(Boolean probeSelection) {
        this.probeSelection = probeSelection;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
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
        if (!(object instanceof ZkbSalesPersonInfo)) {
            return false;
        }
        ZkbSalesPersonInfo other = (ZkbSalesPersonInfo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.ZkbSalesPersonInfo[ id=" + id + " ]";
    }

}
