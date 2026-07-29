package com.ge.apm.domain.acquisition;

public class ConfigItemForm {

    private static final long serialVersionUID = 1L;

    private String key;

    private Integer category;

    private String tenantUid;

    private String institutionUid;

    private String hospitalUid;

    private String siteUid;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getTenantUid() {
        return tenantUid;
    }

    public void setTenantUid(String tenantUid) {
        this.tenantUid = tenantUid;
    }

    public String getInstitutionUid() {
        return institutionUid;
    }

    public void setInstitutionUid(String institutionUid) {
        this.institutionUid = institutionUid;
    }

    public String getHospitalUid() {
        return hospitalUid;
    }

    public void setHospitalUid(String hospitalUid) {
        this.hospitalUid = hospitalUid;
    }

    public String getSiteUid() {
        return siteUid;
    }

    public void setSiteUid(String siteUid) {
        this.siteUid = siteUid;
    }
}

