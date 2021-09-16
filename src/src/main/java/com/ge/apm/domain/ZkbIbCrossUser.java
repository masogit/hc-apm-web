package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author : 503189362
 * @date : 2021/4/21
 * @description :
 */
@Entity
@Table(name = "zkb_ib_cross_user")
public class ZkbIbCrossUser  {


    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "uid")
    private String uid;

    @Column(name = "login_name")
    private String loginName;

    @Column(name = "name")
    private String name;
    @Column(name = "name_py_code")
    private String namePyCode;

    @JsonIgnore
    @Size(min = 1, max = 255)
    @Column(name = "password")
    private String password;

    @Size(min = 0, max = 32)
    @Column(name = "email")
    private String email;
    @Size(max = 16)
    @Column(name = "telephone")
    private String telephone;

    @Column(name = "is_super_admin")
    private boolean isSuperAdmin;

    @Column(name = "is_site_admin")
    private boolean isSiteAdmin;

    @Column(name = "is_local_admin")
    private boolean isLocalAdmin;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "is_audit_admin")
    private Boolean isAuditAdmin;

    @Column(name = "org_level")
    private Integer orgLevel;

    @Column(name = "is_online")
    private Boolean isOnline;
    @Column(name = "site_id")
    private Integer siteId;
    @Column(name = "hospital_id")
    private Integer hospitalId;
    @Column(name = "last_login_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginTime;
    @JsonIgnore

    @Size(min = 1, max = 64)
    @Column(name = "pwd_salt")
    private String pwdSalt;

    @Column(name = "wechat_id")
    private String weChatId;

    @Column(name = "ma_ib_open_id")
    private String wxMaIbOpenId;

    @Column(name = "wx_union_id")
    private String wxUnionId;

    @Column(name = "leader_user_id")
    private Integer leaderUserId;

    @Column(name = "password_update_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date passwordUpdateDate;

    @Column(name = "password_error_count")
    private Integer passwordErrorCount;

    @Column(name = "is_locked")
    private Boolean isLocked;

    @Column(name = "hospital_uid")
    private String hospitalUid;

    @Column(name = "institution_uid")
    private String institutionUid;

    @Column(name = "site_uid")
    private String siteUid;

    @Column(name = "tenant_uid")
    private String tenantUid;

    @Column(name = "wechat_nickname")
    private String wechatNickname;

    @Column(name = "admin_mode")
    private Boolean adminMode;

    @Column(name = "login_admin")
    private String loginAdmin;

    @Column(name = "org_id")
    private Integer orgInfoId;

    @Column(name = "org_uid")
    private String orgUid;

    @Column(name = "parent_uid")
    private String parentUid;

    @Column(name = "is_ge_op")
    private Boolean isGeOp;

    @Column(name = "is_ge_wechat")
    private Boolean isGeWechat;

    @Column(name = "region")
    private Integer region = 1;//地区

    @Column(name = "ge_sso")
    private String geSso;

    @Column(name = "ge_user_type")
    private Integer geUserType; //1 fe; 2 OLE; 3 Sales; 4 Mgr

    @Column(name = "mvs_role_type")
    private Integer mvsRoleType;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "position")
    private String position;//职务

    // 绑定APP的别名
    @Column(name = "app_alias", length = 40)
    private String appAlias;

    @Column(name = "first_visit_date")
    private Date firstVisitDate;

    //default_env 0-IB 1-ACCT
    @Column(name = "default_env")
    private Integer defaultEnv;

    @Column(name = "account_creation_type")
    private Integer accountCreationType;

    /**
     * 1.工程师 2.销售
     */
    @Column(name = "user_type")
    private String userType;

    public String getNamePyCode() {
        return namePyCode;
    }

    public void setNamePyCode(String namePyCode) {
        this.namePyCode = namePyCode;
    }

    public String getHospitalUid() {
        return hospitalUid;
    }

    public void setHospitalUid(String hospitalUid) {
        this.hospitalUid = hospitalUid;
    }

    public String getInstitutionUid() {
        return institutionUid;
    }

    public void setInstitutionUid(String institutionUid) {
        this.institutionUid = institutionUid;
    }

    public String getSiteUid() {
        return siteUid;
    }

    public void setSiteUid(String siteUid) {
        this.siteUid = siteUid;
    }

    public String getTenantUid() {
        return tenantUid;
    }

    public void setTenantUid(String tenantUid) {
        this.tenantUid = tenantUid;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public boolean isSuperAdmin() {
        return isSuperAdmin;
    }

    public void setSuperAdmin(boolean superAdmin) {
        isSuperAdmin = superAdmin;
    }

    public boolean isSiteAdmin() {
        return isSiteAdmin;
    }

    public void setSiteAdmin(boolean siteAdmin) {
        isSiteAdmin = siteAdmin;
    }

    public boolean isLocalAdmin() {
        return isLocalAdmin;
    }

    public void setLocalAdmin(boolean localAdmin) {
        isLocalAdmin = localAdmin;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Boolean getAuditAdmin() {
        return isAuditAdmin;
    }

    public void setAuditAdmin(Boolean auditAdmin) {
        isAuditAdmin = auditAdmin;
    }

    public Integer getOrgLevel() {
        return orgLevel;
    }

    public void setOrgLevel(Integer orgLevel) {
        this.orgLevel = orgLevel;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getPwdSalt() {
        return pwdSalt;
    }

    public void setPwdSalt(String pwdSalt) {
        this.pwdSalt = pwdSalt;
    }

    public String getWeChatId() {
        return weChatId;
    }

    public void setWeChatId(String weChatId) {
        this.weChatId = weChatId;
    }

    public String getWxMaIbOpenId() {
        return wxMaIbOpenId;
    }

    public void setWxMaIbOpenId(String wxMaIbOpenId) {
        this.wxMaIbOpenId = wxMaIbOpenId;
    }

    public String getWxUnionId() {
        return wxUnionId;
    }

    public void setWxUnionId(String wxUnionId) {
        this.wxUnionId = wxUnionId;
    }

    public Integer getLeaderUserId() {
        return leaderUserId;
    }

    public void setLeaderUserId(Integer leaderUserId) {
        this.leaderUserId = leaderUserId;
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

    public Boolean getLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public String getWechatNickname() {
        return wechatNickname;
    }

    public void setWechatNickname(String wechatNickname) {
        this.wechatNickname = wechatNickname;
    }

    public Boolean getAdminMode() {
        return adminMode;
    }

    public void setAdminMode(Boolean adminMode) {
        this.adminMode = adminMode;
    }

    public String getLoginAdmin() {
        return loginAdmin;
    }

    public void setLoginAdmin(String loginAdmin) {
        this.loginAdmin = loginAdmin;
    }

    public Integer getOrgInfoId() {
        return orgInfoId;
    }

    public void setOrgInfoId(Integer orgInfoId) {
        this.orgInfoId = orgInfoId;
    }

    public String getOrgUid() {
        return orgUid;
    }

    public void setOrgUid(String orgUid) {
        this.orgUid = orgUid;
    }

    public String getParentUid() {
        return parentUid;
    }

    public void setParentUid(String parentUid) {
        this.parentUid = parentUid;
    }

    public Boolean getGeOp() {
        return isGeOp;
    }

    public void setGeOp(Boolean geOp) {
        isGeOp = geOp;
    }

    public Boolean getGeWechat() {
        return isGeWechat;
    }

    public void setGeWechat(Boolean geWechat) {
        isGeWechat = geWechat;
    }

    public Integer getRegion() {
        return region;
    }

    public void setRegion(Integer region) {
        this.region = region;
    }

    public String getGeSso() {
        return geSso;
    }

    public void setGeSso(String geSso) {
        this.geSso = geSso;
    }

    public Integer getGeUserType() {
        return geUserType;
    }

    public void setGeUserType(Integer geUserType) {
        this.geUserType = geUserType;
    }

    public Integer getMvsRoleType() {
        return mvsRoleType;
    }

    public void setMvsRoleType(Integer mvsRoleType) {
        this.mvsRoleType = mvsRoleType;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getAppAlias() {
        return appAlias;
    }

    public void setAppAlias(String appAlias) {
        this.appAlias = appAlias;
    }

    public Date getFirstVisitDate() {
        return firstVisitDate;
    }

    public void setFirstVisitDate(Date firstVisitDate) {
        this.firstVisitDate = firstVisitDate;
    }

    public Integer getDefaultEnv() {
        return defaultEnv;
    }

    public void setDefaultEnv(Integer defaultEnv) {
        this.defaultEnv = defaultEnv;
    }

    public Integer getAccountCreationType() {
        return accountCreationType;
    }

    public void setAccountCreationType(Integer accountCreationType) {
        this.accountCreationType = accountCreationType;
    }
}
