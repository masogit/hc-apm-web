package com.ge.apm.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
@Entity
@Table(name = "v2_device_type_group")
public class V2_DeviceTypeGroup implements Serializable{

	private static final long serialVersionUID = -1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
	private Integer id;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "device_type_id")
	private Integer deviceTypeId;//设备类型
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "asset_group")
	private Integer assetGroup;//资产类型
   
	@Column(name="tenant_uid",columnDefinition = "CHAR(32)")
    private String tenantUID;
    
    @Column(name="institution_uid",columnDefinition = "CHAR(32)")
    private String institutionUID;
    
    @Column(name="hospital_uid",columnDefinition = "CHAR(32)")
    private String hospitalUID;
    
    @Column(name="site_uid",columnDefinition = "CHAR(32)")
    private String siteUID;
    
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
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getDeviceTypeId() {
		return deviceTypeId;
	}
	public void setDeviceTypeId(Integer deviceTypeId) {
		this.deviceTypeId = deviceTypeId;
	}
	public Integer getAssetGroup() {
		return assetGroup;
	}
	public void setAssetGroup(Integer assetGroup) {
		this.assetGroup = assetGroup;
	}
	
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof V2_DeviceTypeGroup)) {
            return false;
        }
        V2_DeviceTypeGroup other = (V2_DeviceTypeGroup) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
	
}
