package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author : 503189362
 * @date : 2020/6/23
 * @description :
 */
@Entity
@Table(name = "zkb_part_order_user_config")
public class ZkbPartOrderUserConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    /**
     * 账号
     */
    @Column(name = "user_account_uid")
    private String userAccountId;

    /**
     * 名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 邮箱
     */
    @Column(name = "email")
    private String email;

    /**
     * 电话
     */
    @Column(name = "phone")
    private String phone;

    /**
     * 用户sso
     */
    @Column(name = "sso")
    private String sso;

    /**
     * 区域
     */
    @Column(name = "region")
    private String region;

    /**
     * 类型
     */
    @Column(name = "config_type")
    private String configType;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 租户
     */
    @Column(name = "tenant_uid")
    private String tenantUID;

    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(String userAccountId) {
        this.userAccountId = userAccountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSso() {
        return sso;
    }

    public void setSso(String sso) {
        this.sso = sso;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
        if (!(object instanceof ZkbPartOrderUserConfig)) {
            return false;
        }
        ZkbPartOrderUserConfig other = (ZkbPartOrderUserConfig) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.ZkbPartOrderUserConfig[ id=" + id + " ]";
    }
}
