package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "table_jm_report")
public class JmReport implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    //报表名称
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "name")
    private String name;

    //浏览地址
    @Column(name = "report_view_url")
    private String reportViewUrl;

    //编辑地址
    @Column(name = "report_edit_url")
    private String reportEditUrl;

    //描述
    @Column(name = "description")
    private String description;

    @CreatedBy
    @Column(name = "created_by", nullable = true, length = 50)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_date", nullable = true)
    private Date createdDate;

    @LastModifiedBy
    @Column(name = "last_modified_by", nullable = true, length = 50)
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_date", nullable = true)
    private Date lastModifiedDate;

    @Column(name = "status")
    private String status;

    @Column(name = "type")
    private Integer type;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Column(name = "assign_tenant", columnDefinition = "CHAR(32)")
    private String assignTenant;

    @Column(name = "assign_tenant_name")
    private String assignTenantName;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "jm_report_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private List<JmReportAssignTenant> jmReportAssignTenants;

    @Column(name = "assign_role")
    private String assignRole;

    @Column(name = "visible_range")//可见范围 -1所有租户可见  0超管可见
    private String visibleRange;

    public String getVisibleRange() {
        return visibleRange;
    }

    public void setVisibleRange(String visibleRange) {
        this.visibleRange = visibleRange;
    }

    public List<JmReportAssignTenant> getJmReportAssignTenants() {
        return jmReportAssignTenants;
    }

    public void setJmReportAssignTenants(List<JmReportAssignTenant> jmReportAssignTenants) {
        this.jmReportAssignTenants = jmReportAssignTenants;
    }

    public String getAssignRole() {
        return assignRole;
    }

    public void setAssignRole(String assignRole) {
        this.assignRole = assignRole;
    }

    public String getAssignTenantName() {
        return assignTenantName;
    }

    public void setAssignTenantName(String assignTenantName) {
        this.assignTenantName = assignTenantName;
    }

    public String getAssignTenant() {
        return assignTenant;
    }

    public void setAssignTenant(String assignTenant) {
        this.assignTenant = assignTenant;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getReportViewUrl() {
        return reportViewUrl;
    }

    public void setReportViewUrl(String reportUrl) {
        this.reportViewUrl = reportUrl;
    }

    public String getReportEditUrl() {
        return reportEditUrl;
    }

    public void setReportEditUrl(String reportEditUrl) {
        this.reportEditUrl = reportEditUrl;
    }

    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof JmReport)) {
            return false;
        }
        JmReport other = (JmReport) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
