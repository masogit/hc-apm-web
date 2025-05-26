package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "i18n_type_manage_tenant")
public class I18nTypeManageTenant {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id",columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "type_manage_id")
    private String  typeManageId;

    @Column(name = "type")
    private Integer type;


    @Column(name = "admin_edit",columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean adminEdit = Boolean.FALSE;

    @Column(name = "tenant_id")
    private Integer tenantId;


    @Column(name = "tenant_uid")
    private String tenantUid;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "type_manage_id", referencedColumnName = "id", insertable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private I18nTypeManage i18nTypeManage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeManageId() {
        return typeManageId;
    }

    public void setTypeManageId(String typeManageId) {
        this.typeManageId = typeManageId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Boolean getAdminEdit() {
        return adminEdit;
    }

    public void setAdminEdit(Boolean adminEdit) {
        this.adminEdit = adminEdit;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantUid() {
        return tenantUid;
    }

    public void setTenantUid(String tenantUid) {
        this.tenantUid = tenantUid;
    }

    public I18nTypeManage getI18nTypeManage() {
        return i18nTypeManage;
    }

    public void setI18nTypeManage(I18nTypeManage i18nTypeManage) {
        this.i18nTypeManage = i18nTypeManage;
    }
}
