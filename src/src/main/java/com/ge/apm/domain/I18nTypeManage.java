package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "i18n_type_manage")
public class I18nTypeManage  {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id",columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "msg_type")
    private String msgType;

//    @Column(name = "type")
    @Transient
    private Integer type = -1;

    @Column(name = "description",columnDefinition = "text")
    private String description;
    // 是否支持管理员修改
//    @Column(name = "admin_edit",columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Transient
    private Boolean adminEdit = Boolean.FALSE;

    @OneToMany(mappedBy = "i18nTypeManage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private  List<I18nTypeManageTenant> i18nTypeManageTenants;

    @Transient
    private boolean alone;

    @Transient
    private boolean defaultValue;

    @Transient
    private List<I18nMessage> i18nMessageList = new ArrayList<>();

    @Transient
    private List<I18nMessage> defaultMsgList = new ArrayList<>();

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

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAdminEdit() {
        return adminEdit;
    }

    public void setAdminEdit(Boolean adminEdit) {
        this.adminEdit = adminEdit;
    }

    public boolean isAlone() {
        return alone;
    }

    public void setAlone(boolean alone) {
        this.alone = alone;
    }

    public boolean isDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    public List<I18nMessage> getI18nMessageList() {
        return i18nMessageList;
    }

    public void setI18nMessageList(List<I18nMessage> i18nMessageList) {
        this.i18nMessageList = i18nMessageList;
    }

    public List<I18nMessage> getDefaultMsgList() {
        return defaultMsgList;
    }

    public void setDefaultMsgList(List<I18nMessage> defaultMsgList) {
        this.defaultMsgList = defaultMsgList;
    }

    public List<I18nTypeManageTenant> getI18nTypeManageTenants() {
        return i18nTypeManageTenants;
    }

    public void setI18nTypeManageTenants(List<I18nTypeManageTenant> i18nTypeManageTenants) {
        this.i18nTypeManageTenants = i18nTypeManageTenants;
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
        if (!(object instanceof I18nTypeManage)) {
            return false;
        }
        I18nTypeManage other = (I18nTypeManage) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }
}
