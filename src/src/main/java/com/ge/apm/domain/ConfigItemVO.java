package com.ge.apm.domain;

import java.io.Serializable;

/**
 * @author 503266962
 */

public class ConfigItemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uid;

    private String name;

    /**
     * 类别 配置归属于哪个业务模块
     * 1-设备档案 2-设备调剂 3-到货安装 4-维修流程 5-预防维护 6-预算管理 7-计量管理 8-临床助手 9-知识文档 10-绩效分析 11-质控评审
     */
    private Integer category;

    /**
     * 唯一性，有含义的值
     */
    private String key;

    /**
     * 值类型 1-text 2-number 3-boolean
     * 类型为text时，如果需要做格式校验，需要在 validation 字段中定义正则表达式
     */
    private Integer valueType;

    /**
     * 合法性验证-正则表达式
     */
    private String validation;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否超管修改
     * 是-该配置仅在APM平台管理下的配置项管理菜单下修改
     * 否-该配置仅在系统配置下的配置项管理菜单下修改
     */
    private Boolean superAdminEdit;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 使用上级配置
     */
    private Boolean userParentConfig;

    /**
     * 实际值
     */
    private String actualValue;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getValueType() {
        return valueType;
    }

    public void setValueType(Integer valueType) {
        this.valueType = valueType;
    }

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getSuperAdminEdit() {
        return superAdminEdit;
    }

    public void setSuperAdminEdit(Boolean superAdminEdit) {
        this.superAdminEdit = superAdminEdit;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean getUserParentConfig() {
        return userParentConfig;
    }

    public void setUserParentConfig(Boolean userParentConfig) {
        this.userParentConfig = userParentConfig;
    }

    public String getActualValue() {
        return actualValue;
    }

    public void setActualValue(String actualValue) {
        this.actualValue = actualValue;
    }

    public ConfigItemVO(String uid, String name, Integer category, String key, Integer valueType, String validation, String description, Boolean superAdminEdit, String defaultValue, Boolean userParentConfig, String actualValue) {
        this.uid = uid;
        this.name = name;
        this.category = category;
        this.key = key;
        this.valueType = valueType;
        this.validation = validation;
        this.description = description;
        this.superAdminEdit = superAdminEdit;
        this.defaultValue = defaultValue;
        this.userParentConfig = userParentConfig;
        this.actualValue = actualValue;
    }

    public ConfigItemVO() {
    }
}
