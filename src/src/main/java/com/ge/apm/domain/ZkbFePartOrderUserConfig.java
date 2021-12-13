package com.ge.apm.domain;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author : 503202755
 * @date : 2020/12/11
 * @description :
 */
@Entity
@Table(name = "zkb_fe_part_order_user_config")
public class ZkbFePartOrderUserConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * FourHundredsTeam 400团队备件
     * Supplier 供应商
     * Commercial Commercial
     * GEAdmin GEAdmin
     * FourHundredsTeam_Probe 400团队探头
     * PublicEmail 公共邮箱
     */
    public enum ConfigTypeEnum {
        FourHundredsTeam,Supplier,Commercial,FourHundredsTeam_Probe,GEAdmin,PublicEmail
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    /**
     * 账号
     */
    @Column(name = "login_name")
    private String loginName;

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
     * 租户UID
     */
    @Column(name = "tenant_uid")
    private String tenantUid;

    @Column(name = "is_vacation")
    private String isVacation = "f";// 是否休假 t f

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
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

    public String getTenantUid() {
        return tenantUid;
    }

    public void setTenantUid(String tenantUid) {
        this.tenantUid = tenantUid;
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
        ZkbFePartOrderUserConfig other = (ZkbFePartOrderUserConfig) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public String getIsVacation() {
        if(StringUtils.isBlank(this.isVacation))
        {
            return  "f";
        }
        return isVacation;
    }

    public void setIsVacation(String isVacation) {
        this.isVacation = isVacation;
    }
}
