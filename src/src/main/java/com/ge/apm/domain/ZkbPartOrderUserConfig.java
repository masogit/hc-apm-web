package com.ge.apm.domain;

import com.ge.apm.service.zkb.ZkbPartOrderUserConfigService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : 503189362
 * @date : 2020/6/23
 * @description :
 */
@Entity
@Table(name = "zkb_part_order_user_config")
public class ZkbPartOrderUserConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum ConfigTypeEnum {
        Tech,Manage,Order,Online
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
     * 区域
     */
    @Column(name = "region")
    private String region;

    /**
     * 类型
     */
    @Column(name = "config_type")
    private String configType;

    //DI CT x-ray
    /**
     * DI 是手动选择的在线工程师
     * CT X-RAY是不同的公用邮箱
     */
    @Column(name = "asset_type")
    private String assetType;

    @Column(name = "sub_config_type")
    private String subConfigType;

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

    @Column(name = "order_type")
    private String orderType;

    @Column(name = "is_vacation")
    private String isVacation = "f";// 是否休假 t f

    @Column(name = "us_back_up_id")
    private String usBackUpId;

    @Column(name = "us_back_up_name")
    private String usBackUpName;

    @Transient
    private String delFlag;

    public String getUsBackUpId() {
        return usBackUpId;
    }

    public void setUsBackUpId(String usBackUpId) {
        this.usBackUpId = usBackUpId;
    }

    public String getUsBackUpName() {
        return usBackUpName;
    }

    public void setUsBackUpName(String usBackUpName) {
        this.usBackUpName = usBackUpName;
    }

    public String getIsVacation() {
        return isVacation;
    }

    public void setIsVacation(String isVacation) {
        this.isVacation = isVacation;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getSubConfigType() {
        return subConfigType;
    }

    public void setSubConfigType(String subConfigType) {
        this.subConfigType = subConfigType;
    }

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

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getConfigTypeName()
    {
        Map<String, String> listI18nMessageMap =
                ZkbPartOrderUserConfigService.listI18nMessage.stream().collect(Collectors.toMap(I18nMessage::getMsgKey,
                        I18nMessage::getValueZh));
        return listI18nMessageMap.get(this.configType);
    }

    public String getVacationText()
    {
        if("t".equals(this.isVacation))
        {
            return "是";
        }else
                return "否";
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
    public boolean isNull() {
        boolean flag = false;
        if(StringUtils.isBlank(this.getLoginName())
                &&StringUtils.isBlank(this.getName())
                &&StringUtils.isBlank(this.getEmail())
                &&StringUtils.isBlank(this.getPhone())
                &&StringUtils.isBlank(this.getSso())
                &&StringUtils.isBlank(this.getRegion())
                &&StringUtils.isBlank(this.getConfigType())
                &&StringUtils.isBlank(this.getAssetType())
                &&StringUtils.isBlank(this.getRemark())
                &&StringUtils.isBlank(this.getOrderType())
                &&StringUtils.isBlank(this.getId())){
            flag = true;
        }
        return flag;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }
}
