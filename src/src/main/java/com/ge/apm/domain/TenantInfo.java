/*
 */
package com.ge.apm.domain;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ge.apm.service.utils.Digests;
import com.ge.apm.service.utils.PasswordEncodeUtil;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.util.StringUtils;

/**
 *
 * @author 212547631
 */
@Entity
@Table(name = "tenant_info")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable(true)
public class TenantInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column(columnDefinition = "CHAR(32)")
    private String uid;
    
    @Basic(optional = false)
    @NotNull
    @Size(max = 64)
    @Column(name = "name")
    private String name;
    @Size(max = 64)
    @Column(name = "name_en")
    private String nameEn;
    @Size(max = 64)
    @Column(name = "alias_name")
    private String aliasName;
    @Size(max = 256)
    @Column(name = "site_description")
    private String description;
    @Size(max = 64)
    @Column(name = "contact_person")
    private String contactPerson;
    @Size(max = 64)
    @Column(name = "contact_phone")
    private String contactPhone;
    @Pattern(regexp="^$|^[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 64)
    @Column(name = "contact_email")
    private String contactEmail;
    @Size(max = 64)
    @Column(name = "location")
    private String location;
    @Size(max = 256)
    @Column(name = "location_en")
    private String locationEn;
    @Column(name = "time_zone")
    private Double timeZone = 8.0d;
    @Size(max = 16)
    @Column(name = "default_lang")
    private String defaultLang;
    @Column(name = "is_enabled")
    private Boolean isEnabled = false;

    @Basic(optional = false)
    @NotNull
    @Column(name = "manhour_price")
    private Double manhourPrice;
    
    @Column(name = "wf_auto_step2")
    private Boolean wfAutoStep2;
    @Column(name = "wf_auto_step3")
    private Boolean wfAutoStep3;
    @Column(name = "wf_auto_step4")
    private Boolean wfAutoStep4;
    @Column(name = "wf_auto_step5")
    private Boolean wfAutoStep5;
    @Column(name = "wf_auto_step6")
    private Boolean wfAutoStep6;

    @Column(name = "password_lifetime")
    private Integer passwordLifeTime; //密码有效天数
    
    @Column(name = "type")
    private Integer type = 0;//0、普通租户，1、代理商租户 2、临时租户

    @Column(name = "is_insulate")
    private Boolean isInsulate;

    @Column(name = "active_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date activeTime;


    @Column(name="contract_no")
    private String contractNo;

    @Column(name = "active_user_id")
    private Integer activeUserId;

    @Column(name = "active_type")
    private Integer activeType;//0 means super admin ,1 means by tenant user
    
    @Column(name="tenant_config",columnDefinition="text")
    private String tenantConfig;

    @Size(max = 255)
    @Column(name = "initial_password")
    private String initialPassword;

    @Size(max = 64)
    @Column(name = "initial_password_salt")
    private String initialPasswordSalt;

    @Column(name = "test_status")
    private Integer testStatus;// 0:正常租户  -1：运营商租户（包含测试租户）

    @Temporal(TemporalType.DATE)
    @Column(name = "created_date", nullable = true)
    private Date createdDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "last_modified_date", nullable = true)
    private Date lastModifiedDate;

    @Transient
    private String plainPassword;

    @Column(name = "currency")
    private Integer currency = 1;//1、cny 2、dollar

    @Column(name = "is_deleted")
    private Boolean isDeleted;//是否删除

    @Transient
    private String cpmURL;

    public Integer getCurrency() {
        return currency;
    }

    public void setCurrency(Integer currency) {
        this.currency = currency;
    }

    public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}


    public TenantInfo() {
        isInsulate = false;
        isEnabled = false;
        wfAutoStep2 = false;
        wfAutoStep3 = false;
        wfAutoStep4 = false;
        wfAutoStep5 = false;
        wfAutoStep6 = false;
        manhourPrice = 1000.0;
    }

    @PrePersist
    public void initializeUid() {
        if(uid==null)
            uid = UUID.randomUUID().toString().replace("-", "");
    }
    
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationEn() {
        return locationEn;
    }

    public void setLocationEn(String locationEn) {
        this.locationEn = locationEn;
    }

    public Double getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(Double timeZone) {
        this.timeZone = timeZone;
    }

    public String getDefaultLang() {
        return defaultLang;
    }

    public void setDefaultLang(String defaultLang) {
        this.defaultLang = defaultLang;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Boolean getWfAutoStep2() {
        return wfAutoStep2;
    }

    public void setWfAutoStep2(Boolean wfAutoStep2) {
        this.wfAutoStep2 = wfAutoStep2;
    }

    public Boolean getWfAutoStep3() {
        return wfAutoStep3;
    }

    public void setWfAutoStep3(Boolean wfAutoStep3) {
        this.wfAutoStep3 = wfAutoStep3;
    }

    public Boolean getWfAutoStep4() {
        return wfAutoStep4;
    }

    public void setWfAutoStep4(Boolean wfAutoStep4) {
        this.wfAutoStep4 = wfAutoStep4;
    }

    public Boolean getWfAutoStep5() {
        return wfAutoStep5;
    }

    public void setWfAutoStep5(Boolean wfAutoStep5) {
        this.wfAutoStep5 = wfAutoStep5;
    }

    public Boolean getWfAutoStep6() {
        return wfAutoStep6;
    }

    public void setWfAutoStep6(Boolean wfAutoStep6) {
        this.wfAutoStep6 = wfAutoStep6;
    }

    public Double getManhourPrice() {
        return manhourPrice;
    }

    public void setManhourPrice(Double manhourPrice) {
        this.manhourPrice = manhourPrice;
    }

    public Integer getPasswordLifeTime() {
        return passwordLifeTime;
    }

    public void setPasswordLifeTime(Integer passwordLifeTime) {
        this.passwordLifeTime = passwordLifeTime;
    }

    public Boolean getInsulate() {
        return isInsulate;
    }

    public void setInsulate(Boolean insulate) {
        isInsulate = insulate;
    }

    public Boolean getIsInsulate() {
        return isInsulate;
    }

    public void setIsInsulate(Boolean insulate) {
        isInsulate = insulate;
    }
    
    public String getTenantConfig() {
		return tenantConfig;
	}

	public void setTenantConfig(String tenantConfig) {
		this.tenantConfig = tenantConfig;
	}

	public String getCpmURL() {
		return cpmURL;
	}

	public void setCpmURL(String cpmURL) {
		this.cpmURL = cpmURL;
	}

    public String getInitialPassword() {
        return initialPassword;
    }

    public void setInitialPassword(String initialPassword) {
        this.initialPassword = initialPassword;
    }

    public String getInitialPasswordSalt() {
        return initialPasswordSalt;
    }

    public void setInitialPasswordSalt(String initialPasswordSalt) {
        this.initialPasswordSalt = initialPasswordSalt;
    }

    public String getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;

        encryptPassword();
    }

    private void encryptPassword() {

        if (StringUtils.isEmpty(getPlainPassword())) {
            setInitialPassword(null);
            setInitialPasswordSalt(null);
            return;
        }

        byte[] salt = Digests.generateSalt(UserAccount.SALT_SIZE);
        setInitialPasswordSalt(Digests.encodeHex(salt));

        byte[] hashPassword = new byte[0];
        try {
            hashPassword = Digests.sha1(getPlainPassword().getBytes(), salt, UserAccount.HASH_INTERATIONS);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        setInitialPassword(Digests.encodeHex(hashPassword));
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TenantInfo)) {
            return false;
        }
        TenantInfo other = (TenantInfo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.SiteInfo[ id=" + id + " ]";
    }

    public Date getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(Date activeTime) {
        this.activeTime = activeTime;
    }

    public Integer getActiveUserId() {
        return activeUserId;
    }

    public void setActiveUserId(Integer activeUserId) {
        this.activeUserId = activeUserId;
    }

    public Integer getActiveType() {
        return activeType;
    }

    public void setActiveType(Integer activeType) {
        this.activeType = activeType;
    }

    public Integer getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(Integer testStatus) {
        this.testStatus = testStatus;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
