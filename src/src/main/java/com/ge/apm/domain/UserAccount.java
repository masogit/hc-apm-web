/*
 */
package com.ge.apm.domain;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ge.apm.service.utils.ChineseName;
import com.ge.apm.service.utils.Digests;
import com.ge.apm.service.utils.PinyinUtil;

/**
 *
 * @author 212547631
 */
@Entity
@Table(name = "user_account")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable(true)
@NamedQueries({ @NamedQuery(name = "UserAccount.findAll", query = "SELECT u FROM UserAccount u") })
public class UserAccount implements ChineseName, Serializable {

	private static final long serialVersionUID = 1L;

	public static final String HASH_ALGORITHM = "SHA-1";
	public static final int HASH_INTERATIONS = 1024;
	public static final int SALT_SIZE = 8;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id")
	private Integer id;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 64)
	@Column(name = "login_name")
	private String loginName;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 32)
	@Column(name = "name")
	private String name;
	@Column(name = "name_py_code")
	private String namePyCode;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "password")
	private String password;
	@Pattern(regexp = "^$|^[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?", message = "{javax.validation.constraints.Pattern.email.message}")
	// if the field contains email address consider  using this annotation to enforce field validation
	@Column(name = "email")
	@Size(min = 0, max = 32)
	private String email;
	@Size(max = 16)
	@Column(name = "telephone")
	private String telephone;
	@Basic(optional = false)
	@NotNull
	@Column(name = "is_super_admin")
	private boolean isSuperAdmin;
	@Basic(optional = false)
	@NotNull
	@Column(name = "is_site_admin")
	private boolean isSiteAdmin;
	@Basic(optional = false)
	@NotNull
	@Column(name = "is_local_admin")
	private boolean isLocalAdmin;
	@Basic(optional = false)
	@NotNull
	@Column(name = "is_active")
	private boolean isActive;

	@Column(name = "org_level")
	private Integer orgLevel;

	@Column(name = "is_online")
	private Boolean isOnline;

	// siteId==tenantuid.id
	@Column(name = "site_id")
	private Integer siteId;

	// hospitalId==siteuid.id
	@Column(name = "hospital_id")
	private Integer hospitalId;
	@Column(name = "last_login_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastLoginTime;

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 64)
	@Column(name = "pwd_salt")
	private String pwdSalt;

	@Column(name = "wechat_id")
	private String weChatId;

	@Column(name = "leader_user_id")
	private Integer leaderUserId;

	@Column(name = "password_update_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date passwordUpdateDate;

	@Column(name = "password_error_count")
	private Integer passwordErrorCount;

	@Column(name = "is_locked")
	private Boolean isLocked;

	@Column(name = "lock_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lockTime;

	@Column(name = "wechat_nickname")
	private String wechatNickname;

	@CreatedBy
	@Column(name = "created_by", nullable = true, length = 50, updatable = false)
	@JsonIgnore
	private String createdBy = "system";

	@CreatedDate
	@Column(name = "created_date", nullable = true)
	@Temporal(TemporalType.DATE)
	@JsonIgnore
	private Date createdDate = new Date();

	@LastModifiedBy
	@Column(name = "last_modified_by", length = 50)
	@JsonIgnore
	private String lastModifiedBy;

	@LastModifiedDate
	@Column(name = "last_modified_date")
	@Temporal(TemporalType.DATE)
	@JsonIgnore
	private Date lastModifiedDate = new Date();

	@Transient
	private String plainPassword;

	@Transient
	private boolean isUnbunding;

	@Transient
	private OrgInfo orgInfo;

	@Transient
	private String tenantName;

	@Transient
	private String institutionName;

	@Transient
	private String hospitalName;

	@Transient
	private String siteName;

	@Transient
	private String departName;

	@Transient
	private String orgFullName;

	@Transient
	private Integer orgType;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "userAccount", fetch = FetchType.EAGER)
	private List<UserRole> userRoleList;

	@Column(name = "org_id")
	@Basic(optional = false)
	@NotNull
	private Integer orgInfoId;

	@Column(name = "org_uid", columnDefinition = "CHAR(32)")
	@Size(max = 32)
	private String orgInfoUID;

	@Column(name = "uid", columnDefinition = "CHAR(32)")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
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

	@Column(name = "active_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date activeTime;

	@Transient
	private Integer excelIdex;

	@Transient
	private String statusDisplay;

	@Transient
	private String otherErrorMessage;

	@Transient
	private String status;

	@PrePersist
	public void initializeUid() {
		if (uid == null)
			uid = UUID.randomUUID().toString().replace("-", "");
	}

	public UserAccount() {
		isSuperAdmin = false;
		isSiteAdmin = false;
		isLocalAdmin = false;
		isActive = false;
		isOnline = false;
	}

	public String getOrgInfoUID() {
		return orgInfoUID;
	}

	public void setOrgInfoUID(String orgInfoUID) {
		this.orgInfoUID = orgInfoUID;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
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

	public Integer getOrgLevel() {
		return orgLevel;
	}

	public void setOrgLevel(Integer orgLevel) {
		this.orgLevel = orgLevel;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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
		this.namePyCode = PinyinUtil.getShortPinyin(name).toLowerCase().replaceAll("[^0-9a-zA-Z]", "");
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public boolean getIsSuperAdmin() {
		return isSuperAdmin;
	}

	public void setIsSuperAdmin(boolean isSuperAdmin) {
		this.isSuperAdmin = isSuperAdmin;
	}

	public boolean getIsSiteAdmin() {
		return isSiteAdmin;
	}

	public void setIsSiteAdmin(boolean isSiteAdmin) {
		this.isSiteAdmin = isSiteAdmin;
	}

	public boolean getIsLocalAdmin() {
		return isLocalAdmin;
	}

	public void setIsLocalAdmin(boolean isLocalAdmin) {
		this.isLocalAdmin = isLocalAdmin;
	}

	public Integer getOrgInfoId() {
		return orgInfoId;
	}

	public void setOrgInfoId(Integer orgInfoId) {
		this.orgInfoId = orgInfoId;
	}

	public boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsOnline() {
		return isOnline;
	}

	public void setIsOnline(Boolean isOnline) {
		this.isOnline = isOnline;
	}

	public Integer getSiteId() {
		return siteId;
	}

	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public List<UserRole> getUserRoleList() {
		return userRoleList;
	}

	public String getUserRoleNames() {
		String roleNames = "";
		if (CollectionUtils.isEmpty(userRoleList)) {
			return roleNames;
		}
		for (UserRole userRole : userRoleList) {
			roleNames = roleNames + userRole.getRole().getRoleDesc() + " ";
		}
		return roleNames;
	}

	public void setUserRoleList(List<UserRole> userRoleList) {
		this.userRoleList = userRoleList;
	}

	public Date getPasswordUpdateDate() {
		return passwordUpdateDate;
	}

	public void setPasswordUpdateDate(Date passwordUpdateDate) {
		this.passwordUpdateDate = passwordUpdateDate;
	}

	public Integer getPasswordErrorCount() {
		return passwordErrorCount;
	}

	public void setPasswordErrorCount(Integer passwordErrorCount) {
		this.passwordErrorCount = passwordErrorCount;
	}

	public Boolean getIsLocked() {
		return isLocked;
	}

	public void setIsLocked(Boolean isLocked) {
		this.isLocked = isLocked;
	}

	public String getNamePyCode() {
		return namePyCode;
	}

	public void setNamePyCode(String namePyCode) {
		this.namePyCode = namePyCode;
	}

	@Transient
	private List<String> userRoleNames;

	@JsonIgnore
	public List<String> getRoleNames() {
		if (userRoleNames == null) {
			userRoleNames = new ArrayList<String>();
			for (UserRole userRole : userRoleList) {
				userRoleNames.add(userRole.getRole().getName());
			}

			if (this.isSuperAdmin)
				userRoleNames.add("SuperAdmin");
			if (this.isSiteAdmin)
				userRoleNames.add("SiteAdmin");
			if (this.isLocalAdmin)
				userRoleNames.add("LocalAdmin");
		}

		return userRoleNames;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getWeChatId() {
		return weChatId;
	}

	public void setWeChatId(String weChatId) {
		this.weChatId = weChatId;
	}

	public Integer getLeaderUserId() {
		return leaderUserId;
	}

	public void setLeaderUserId(Integer leaderUserId) {
		this.leaderUserId = leaderUserId;
	}

	public String getPwdSalt() {
		return pwdSalt;
	}

	public void setPwdSalt(String pwdSalt) {
		this.pwdSalt = pwdSalt;
	}

	public String getPlainPassword() {
		return plainPassword;
	}

	public void setPlainPassword(String plainPassword) {
		this.plainPassword = plainPassword;
	}

	public String getWechatNickname() {
		return wechatNickname;
	}

	public void setWechatNickname(String wechatNickname) {
		this.wechatNickname = wechatNickname;
	}

	public OrgInfo getOrgInfo() {
		return orgInfo;
	}

	public void setOrgInfo(OrgInfo orgInfo) {
		this.orgInfo = orgInfo;
	}

	public void entryptPassword() throws NoSuchAlgorithmException {
		byte[] salt = Digests.generateSalt(SALT_SIZE);
		setPwdSalt(Digests.encodeHex(salt));

		byte[] hashPassword = Digests.sha1(getPlainPassword().getBytes(), salt, HASH_INTERATIONS);
		setPassword(Digests.encodeHex(hashPassword));
	}

	public Date getLockTime() {
		return lockTime;
	}

	public void setLockTime(Date lockTime) {
		this.lockTime = lockTime;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof UserAccount)) {
			return false;
		}
		UserAccount other = (UserAccount) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "com.ge.apm.domain.UserAccount[ id=" + id + " ]";
	}

	public boolean getIsUnbunding() {
		return isUnbunding;
	}

	public void setIsUnbunding(boolean isUnbunding) {
		this.isUnbunding = isUnbunding;
	}

	public Integer getExcelIdex() {
		return excelIdex;
	}

	public void setExcelIdex(Integer excelIdex) {
		this.excelIdex = excelIdex;
	}

	public String getOtherErrorMessage() {
		return otherErrorMessage;
	}

	private void setOtherErrorMessage(String otherErrorMessage) {
		this.otherErrorMessage = otherErrorMessage;
	}

	public String getStatus() {
		return this.status;
	}

	private void setStatus(String status) {
		this.status = status;
	}

	public String getStatusDisplay() {
		if (this.status != null) {
			String result = "";
			if (this.status.contains(UploadFileErrorTypeEnum.FORMAT_ERROR + ",")) {
				result += "格式错误；";
			} else if (this.status.contains(UploadFileErrorTypeEnum.RATE_WRONG + ",")) {
				result += "占比总和错误；";
			} else if (this.status.contains(UploadFileErrorTypeEnum.ASSET_NOT_EXISTS + ",")) {
				result += "设备名称不存在；";
			} else if (this.status.contains(UploadFileErrorTypeEnum.OTHER_ERROR + ",")) {
				result += this.otherErrorMessage;
			} else {
				result += this.status;
			}
			return result;
		}
		return statusDisplay;
	}

	public void setStatusDisplay(UploadFileErrorTypeEnum type, boolean pass) {
		if (pass) {
			if (this.status != null) {
				this.status = this.status.replaceAll(type + ",", "");
			}
		} else {
			if (this.status == null) {
				this.status = type + ",";
			} else if (this.status.indexOf(type + ",") < 0) {
				this.status += type + ",";
			}
		}
	}

	public void setStatusDisplay(UploadFileErrorTypeEnum type, String otherErrorMessage) {
		if (this.status == null) {
			this.status = type + ",";
		} else if (this.status.indexOf(type + ",") < 0) {
			this.status += type + ",";
		}
		this.otherErrorMessage = otherErrorMessage;
	}

	public String getOrgFullName() {
		if (this.orgInfo == null)
			return "";
		setOrgFullName();
		return this.orgFullName;
	}

	public void setOrgFullName(String orgFullName) {
		this.orgFullName = orgFullName;
	}

	private void setOrgFullName() {
		if (StringUtils.isNotEmpty(this.orgFullName)) {
			return;
		}
		OrgInfo org = this.orgInfo;
		if (this.orgInfo == null) {
			return;
		}
		OrgInfo parentOrg = org;
		String name="";
		while (parentOrg != null) {
			if (parentOrg.getOrgType() == 0) {
				setTenantName(parentOrg.getName());
			} else if (parentOrg.getOrgType() == 1) {
				setInstitutionName(parentOrg.getName());
			} else if (parentOrg.getOrgType() == 2) {
				setHospitalName(parentOrg.getName());
			} else if (parentOrg.getOrgType() == 3) {
				setSiteName(parentOrg.getName());
			} else if (parentOrg.getOrgType() == 4) {
				setDepartName(parentOrg.getName());
			}
			if(StringUtils.isEmpty(name)){
				name=parentOrg.getName();
			}
			else{
				name=parentOrg.getName()+"/"+name;
			}
			parentOrg = parentOrg.getParentOrg();
		}
		this.orgFullName=name;
	}

	public String getTenantName() {
		setOrgFullName();
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public String getInstitutionName() {
		setOrgFullName();
		return institutionName;
	}

	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}

	public String getHospitalName() {
		setOrgFullName();
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	public String getSiteName() {
		setOrgFullName();
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getDepartName() {
		setOrgFullName();
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

	public String loadOrgTableColumn() {
		switch (this.getOrgLevel() == null ? 3 : this.getOrgLevel()) {
		case 1:
			return "INSTITUTION_UID";
		case 2:
			return "HOSPITAL_UID";
		case 3:
			return "SITE_UID";
		default:
			return "SITE_UID";
		}
	}

	public String loadOrgColumnName() {
		switch (this.getOrgLevel() == null ? 3 : this.getOrgLevel()) {
		case 0:
			return "tenantUID";
		case 1:
			return "institutionUID";
		case 2:
			return "hospitalUID";
		case 3:
			return "siteUID";
		default:
			return "siteUID";
		}
	}

	public String loadOrgUid() {
		switch (this.getOrgLevel() == null ? 3 : this.getOrgLevel()) {
		case 0:
			return this.getTenantUID();
		case 1:
			return this.getInstitutionUID();
		case 2:
			return this.getHospitalUID();
		case 3:
			return this.getSiteUID();
		default:
			return this.getSiteUID();
		}
	}

	public Date getActiveTime() {
		return activeTime;
	}

	public void setActiveTime(Date activeTime) {
		this.activeTime = activeTime;
	}

	public Integer getOrgType() {
		return orgType;
	}

	public void setOrgType(Integer orgType) {
		this.orgType = orgType;
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

	public String getPinYin() {
		return getNamePyCode();
	}
	
	public UserAccount(String name,String uid){
		this.name=name;
		this.uid=uid;
	}
}
