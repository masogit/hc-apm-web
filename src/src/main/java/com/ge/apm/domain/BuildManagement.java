package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ge.apm.service.utils.ChineseName;
import com.ge.apm.service.utils.PinyinUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import webapp.framework.web.WebUtil;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "build_management")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BuildManagement implements Serializable, ChineseName {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column(columnDefinition = "CHAR(32)")
    private String uid;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    @Size(max = 32)
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Column(name = "parent_uid", columnDefinition = "CHAR(32)")
    private String parentUID;

    @Column(name = "parent_id")
    private Integer parentID;

    @Column(name = "org_type")
    private Integer buildType;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "name")
    private String name;
    @Size(max = 64)
    @Column(name = "name_en")
    private String nameEn;
    @Column(name = "name_py_code")
    private String namePyCode;

    @Column(name = "short_name")
    private String shortName;

    @Column(name = "short_name_py_code")
    private String shortNamePyCode;

    @JoinColumn(name = "parent_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    private BuildManagement parentBuild;

    @Column(name = "hospital_id")
    private Integer hospitalId;

    @Basic(optional = false)
    @NotNull
    @Column(name = "site_id")
    private Integer siteId;

    @Column(name = "building_num")
    private Integer buildingNum;

    @Column(name = "floor_num")
    private Integer floorNum;

    @Column(name = "room")
    private Integer room;

    @PrePersist
    public void initializeUid() {
        if (uid == null)
            uid = UUID.randomUUID().toString().replace("-", "");

        if(this.parentBuild!=null)
            this.parentID = parentBuild.getId();
    }

    @Column(name = "asset_owner_id")
    private Integer assetOwnerId;
    @Column(name = "asset_owner_id2")
    private Integer assetOwnerId2;

    @Column(name = "hospital_ucmid")
    private String hospitalUcmid;

    @Column(name = "status")
    private Integer status;

    @Transient
    private String orgUri;

    @Column(name = "address")
    private String address;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "contact_phone")
    private String contactPhone;

    public void setName(String name) {
        this.name = name;
        this.namePyCode = name == null ? null : PinyinUtil.getShortPinyin(name).toLowerCase().replaceAll("[^0-9a-zA-Z]", "");
    }

    public String getTypeName() {
        if (this.buildType == null) return "";

        if (this.buildType.equals(4)) return WebUtil.getMessage("楼栋");
        else if (this.buildType.equals(5)) return WebUtil.getMessage("楼层");
        else if (this.buildType.equals(6)) return WebUtil.getMessage("房间");
        else return "";
    }

    @JsonIgnore
    public BuildManagement getHospital() {
        BuildManagement org = this;
        while (org != null) {
            if (org.getParentBuild() != null)
                org = org.getParentBuild();
            else
                break;
        }

        return org;
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
        if (!(object instanceof BuildManagement)) {
            return false;
        }
        BuildManagement other = (BuildManagement) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public String getOrgFullName() {
        String name = this.name;

        BuildManagement org = this;
        while (org.getParentBuild() != null) {
            String oName = org.getName();
            org = org.getParentBuild();
            String pName = org.getName();

            if (!(pName.contains(oName) || oName.contains(pName) || name.contains(pName)))
                name = org.getName() + " " + name;
        }

        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String getPinYin() {
        return namePyCode;
    }
}
