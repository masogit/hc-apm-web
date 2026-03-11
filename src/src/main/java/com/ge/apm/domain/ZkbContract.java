package com.ge.apm.domain;

import com.ge.apm.service.utils.ChineseName;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author : 503189362
 * @date : 2020/4/16
 * @description : 合同实体类。
 */
@Entity
@Table(name = "zkb_contract")
public class ZkbContract implements ChineseName, Serializable {
    private static final long serialVersionUID = 1L;

    public enum ContractState {
        effective, noactive, expired
    }

    public enum ContractRegion {
        E1, E2, E3, N3, W1, W2, N1, N2, S1, S2
    }

    public enum ContractType {
        PCB,Biomed,CCS,DI,CCSB,ULS_OEC
    }

    public enum ModifyTypeEnum{
        OC,NextYearBalance,Others
    }

    public enum QuotaDeductTypeEnum{
        New,Old,Null
    }
    public enum ContractAmount{
        oneMillionMore,oneMillionLess
    }

    public enum YOrNEnum{
        Y,N
    }

    public enum AssetQuotaTypeEnum{
        t,f
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    /**
     * 合同号
     */
    @Column(name = "contract_no")
    private String contractNo;

    /**
     * 区域 North/South/East/NW/SW
     */
    @Column(name = "region")
    private String region;



    /**
     * 合同类型	 PCB/Biomed/CCS/DI
     */
    @Column(name = "contract_type")
    private String contractType;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    /**
     * 合同状态  有效、未生效、已过期
     */
    @Column(name = "contract_state")
    private String contractState;

    /**
     * IB数量
     */
    @Column(name = "ib_number")
    private Integer ibNumber = 0;

    /**
     *备件剩余名额
     */
    @Column(name = "spare_quota")
    private Integer spareQuota = 0;

    /**
     *备件总额
     */
    @Column(name = "spare_quota_total")
    private Integer spareQuotaTotal = 0;

    /**
     *备件申请名额
     */
    @Column(name = "spare_quota_apply")
    private Integer spareQuotaApply = 0;
    /**
     *备件已使用名额
     */
    @Column(name = "spare_quota_use")
    private Integer spareQuotaUse = 0;

    /**
     *探头剩余名额 -- 任意名额
     */
    @Column(name = "probe_quota")
    private Integer probeQuota = 0;
    /**
     *探头名额
     */
    @Column(name = "probe_quota_total")
    private Integer probeQuotaTotal = 0;

    /**
     *探头申请名额
     */
    @Column(name = "probe_quota_apply")
    private Integer probeQuotaApply = 0;

    /**
     *探头已使用名额
     */
    @Column(name = "probe_quota_use")
    private Integer probeQuotaUse = 0;


    /**
     * 合同金额	100W/年以下oneMillionLess  ；100W/年以上   oneMillionMore
     */
    @Column(name = "contract_amount")
    private String contractAmount;

    @Column(name = "probe_quota_normal")
    private Integer probeQuotaNormal = 0;
    @Column(name = "probe_quota_normal_total")
    private Integer probeQuotaNormalTotal = 0;
    @Column(name = "probe_quota_normal_use")
    private Integer probeQuotaNormalUse = 0;
    @Column(name = "probe_quota_normal_apply")
    private Integer probeQuotaNormalApply = 0;


    @Column(name = "probe_quota_special")
    private Integer probeQuotaSpecial = 0;
    @Column(name = "probe_quota_special_total")
    private Integer probeQuotaSpecialTotal = 0;
    @Column(name = "probe_quota_special_use")
    private Integer probeQuotaSpecialUse = 0;
    @Column(name = "probe_quota_special_apply")
    private Integer probeQuotaSpecialApply = 0;

    @Column(name = "probe_quota_high")
    private Integer probeQuotaHigh = 0;
    @Column(name = "probe_quota_high_use")
    private Integer probeQuotaHighUse = 0;
    @Column(name = "probe_quota_high_apply")
    private Integer probeQuotaHighApply = 0;
    @Column(name = "probe_quota_high_total")
    private Integer probeQuotaHighTotal = 0;


    public String getContractAmount() {
        return contractAmount;
    }

    public void setContractAmount(String contractAmount) {
        this.contractAmount = contractAmount;
    }

    /**
     *GE人工支持剩余次数
     */
    @Column(name = "ge_support_times")
    private Integer geArtificialSupportTimes = 0;

    /**
     *GE人工支持次数
     */
    @Column(name = "ge_support_times_total")
    private Integer geArtificialSupportTimesTotal = 0;

    /**
     *GE人工支持申请次数
     */
    @Column(name = "ge_support_times_apply")
    private Integer geArtificialSupportTimesApply = 0;

    /**
     *GE人工支持使用次数
     */
    @Column(name = "ge_support_times_use")
    private Integer geArtificialSupportTimesUse = 0;

    /**
     * 保养名额
     */
    @Column(name = "maintenance_quota")
    private Integer maintenanceQuota = 0;

    /**
     *备件/人工/保养剩余名额
     */
    @Column(name = "spare_artificail")
    private Integer spareArtificail = 0;

    /**
     *备件/人工/保养总额
     */
    @Column(name = "spare_artificail_total")
    private Integer spareArtificailTotal = 0;

    /**
     *备件/人工/保养申请名额
     */
    @Column(name = "spare_artificail_apply")
    private Integer spareArtificailApply = 0;

    /**
     *备件/人工/保养已使用名额
     */
    @Column(name = "spare_artificail_use")
    private Integer spareArtificailUse = 0;


    /**
     *备件高值名额
     */
    @Column(name = "part_high_quota")
    private Integer partHighQuota = 0;
    @Column(name = "part_high_quota_use")
    private Integer partHighQuotaUse = 0;
    @Column(name = "part_high_quota_apply")
    private Integer partHighQuotaApply = 0;
    @Column(name = "part_high_quota_total")
    private Integer partHighQuotaTotal = 0;

    /**
     * CCSB保养名额
     */
    @Column(name = "ccsb_maintenance_quota")
    private Integer ccsbMaintenanceQuota = 0;
    @Column(name = "ccsb_maintenance_quota_use")
    private Integer ccsbMaintenanceQuotaUse = 0;
    @Column(name = "ccsb_maintenance_quota_apply")
    private Integer ccsbMaintenanceQuotaApply = 0;
    @Column(name = "ccsb_maintenance_quota_total")
    private Integer ccsbMaintenanceQuotaTotal = 0;

    //耗材名额
    @Column(name = "consumable_quota")
    private Integer consumableQuota = 0;
    @Column(name = "consumable_quota_use")
    private Integer consumableQuotaUse = 0;
    @Column(name = "consumable_quota_apply")
    private Integer consumableQuotaApply = 0;
    @Column(name = "consumable_quota_total")
    private Integer consumableQuotaTotal = 0;
    @Column(name = "consume_growth")
    private Integer consumeGrowth = 0;
    @Column(name = "consume_growth_date")
    private Date consumeGrowthDate;

    /**
     * 负责人
     */
    @Column(name = "principal")
    private String principal;

    /**
     * 电话
     */
    @Column(name = "principal_phone")
    private String principalPhone;

    /**
     * 邮箱
     */
    @Column(name = "principal_email")
    private String principalEmail;

    /**
     * 管理员
     */
    @Column(name = "admin")
    private String admin;

    /**
     * 管理员电话
     */
    @Column(name = "admin_phone")
    private String adminPhone;

    /**
     * 管理员邮箱
     */
    @Column(name = "admin_emial")
    private String adminEmail;

    /**
     * 地址
     */
    @Column(name = "adminaddress")
    private String adminAddress;

    @Column(name = "remark")
    @Size(max = 2000)
    private String remark;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    /**
     * 租户
     */
    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    @Size(max = 32)
    private String tenantUID;

    @Transient
    private String agentName;

    @Column(name = "modify_type")
    private String modifyType;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(insertable=false, updatable=false,name = "contract_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    List<ZkbContractCourse> zkbContractCourses = new ArrayList<>();


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(insertable=false, updatable=false,name = "contract_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    Set<ZkbContractAsset> zkbContractAssets = new HashSet<>();

    /**
     * New Old  N/A
     */
    @Column(name = "quota_deduct_type")
    private String quotaDeductType;

    /**
     * Y  N
     */
    @Column(name = "five_nine_nine_flag")
    private String fiveNineNineFlag;

    @Column(name = "year_count")
    private Integer yearCount;

    @Column(name = "contract_start_date")
    private Date contractStartDate;

    @Column(name = "contract_end_date")
    private Date contractEndDate;

    @Column(name = "renew_flag")
    private String renewFlag;

    @Column(name = "first_year_flag")
    private String firstYearFlag;

    @Column(name = "link_contract_no")
    private String linkContractNo;

    @Column(name = "company_code")
    private String companyCode;

    //Y  N
    @Column(name = "can_apply_toolbox")
    private String canApplyToolbox;

    /**
     * 2022年起探头高低端名额分类
     * t:是
     * f:否
     */
    @Column(name = "asset_quota_type")
    private String assetQuotaType;

    public String getCanApplyToolbox() {
        return canApplyToolbox;
    }

    public void setCanApplyToolbox(String canApplyToolbox) {
        this.canApplyToolbox = canApplyToolbox;
    }

    public Integer getCcsbMaintenanceQuota() {
        return ccsbMaintenanceQuota;
    }

    public void setCcsbMaintenanceQuota(Integer ccsbMaintenanceQuota) {
        this.ccsbMaintenanceQuota = ccsbMaintenanceQuota;
    }

    public Integer getCcsbMaintenanceQuotaUse() {
        return ccsbMaintenanceQuotaUse;
    }

    public void setCcsbMaintenanceQuotaUse(Integer ccsbMaintenanceQuotaUse) {
        this.ccsbMaintenanceQuotaUse = ccsbMaintenanceQuotaUse;
    }

    public Integer getCcsbMaintenanceQuotaApply() {
        return ccsbMaintenanceQuotaApply;
    }

    public void setCcsbMaintenanceQuotaApply(Integer ccsbMaintenanceQuotaApply) {
        this.ccsbMaintenanceQuotaApply = ccsbMaintenanceQuotaApply;
    }

    public Integer getCcsbMaintenanceQuotaTotal() {
        return ccsbMaintenanceQuotaTotal;
    }

    public void setCcsbMaintenanceQuotaTotal(Integer ccsbMaintenanceQuotaTotal) {
        this.ccsbMaintenanceQuotaTotal = ccsbMaintenanceQuotaTotal;
    }

    public Integer getPartHighQuota() {
        return partHighQuota;
    }

    public void setPartHighQuota(Integer partHighQuota) {
        this.partHighQuota = partHighQuota;
    }

    public Integer getPartHighQuotaUse() {
        return partHighQuotaUse;
    }

    public void setPartHighQuotaUse(Integer partHighQuotaUse) {
        this.partHighQuotaUse = partHighQuotaUse;
    }

    public Integer getPartHighQuotaApply() {
        return partHighQuotaApply;
    }

    public void setPartHighQuotaApply(Integer partHighQuotaApply) {
        this.partHighQuotaApply = partHighQuotaApply;
    }

    public Integer getPartHighQuotaTotal() {
        return partHighQuotaTotal;
    }

    public void setPartHighQuotaTotal(Integer partHighQuotaTotal) {
        this.partHighQuotaTotal = partHighQuotaTotal;
    }

    public String getAssetQuotaType() {
        return assetQuotaType;
    }

    public void setAssetQuotaType(String assetQuotaType) {
        this.assetQuotaType = assetQuotaType;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getFirstYearFlag() {
        return firstYearFlag;
    }

    public void setFirstYearFlag(String firstYearFlag) {
        this.firstYearFlag = firstYearFlag;
    }

    public String getLinkContractNo() {
        return linkContractNo;
    }

    public void setLinkContractNo(String linkContractNo) {
        this.linkContractNo = linkContractNo;
    }

    public Date getContractStartDate() {
        return contractStartDate;
    }

    public void setContractStartDate(Date contractStartDate) {
        this.contractStartDate = contractStartDate;
    }

    public Date getContractEndDate() {
        return contractEndDate;
    }

    public void setContractEndDate(Date contractEndDate) {
        this.contractEndDate = contractEndDate;
    }

    public String getRenewFlag() {
        return renewFlag;
    }

    public void setRenewFlag(String renewFlag) {
        this.renewFlag = renewFlag;
    }

    public String getFiveNineNineFlag() {
        return fiveNineNineFlag;
    }

    public void setFiveNineNineFlag(String fiveNineNineFlag) {
        this.fiveNineNineFlag = fiveNineNineFlag;
    }

    public Integer getYearCount() {
        return yearCount;
    }

    public void setYearCount(Integer yearCount) {
        this.yearCount = yearCount;
    }

    public String getQuotaDeductType() {
        return quotaDeductType;
    }

    public void setQuotaDeductType(String quotaDeductType) {
        this.quotaDeductType = quotaDeductType;
    }
    @Transient
    private String tenantName;

    public String getModifyType() {
        return modifyType;
    }

    public void setModifyType(String modifyType) {
        this.modifyType = modifyType;
    }

    public Integer getProbeQuotaHighUse() {
        return probeQuotaHighUse;
    }

    public void setProbeQuotaHighUse(Integer probeQuotaHighUse) {
        this.probeQuotaHighUse = probeQuotaHighUse;
    }

    public Integer getProbeQuotaHighApply() {
        return probeQuotaHighApply;
    }

    public void setProbeQuotaHighApply(Integer probeQuotaHighApply) {
        this.probeQuotaHighApply = probeQuotaHighApply;
    }

    public Integer getProbeQuotaNormalTotal() {
        return probeQuotaNormalTotal;
    }

    public void setProbeQuotaNormalTotal(Integer probeQuotaNormalTotal) {
        this.probeQuotaNormalTotal = probeQuotaNormalTotal;
    }

    public Integer getProbeQuotaNormalUse() {
        return probeQuotaNormalUse;
    }

    public void setProbeQuotaNormalUse(Integer probeQuotaNormalUse) {
        this.probeQuotaNormalUse = probeQuotaNormalUse;
    }

    public Integer getProbeQuotaNormalApply() {
        return probeQuotaNormalApply;
    }

    public void setProbeQuotaNormalApply(Integer probeQuotaNormalApply) {
        this.probeQuotaNormalApply = probeQuotaNormalApply;
    }

    public Integer getProbeQuotaSpecialTotal() {
        return probeQuotaSpecialTotal;
    }

    public void setProbeQuotaSpecialTotal(Integer probeQuotaSpecialTotal) {
        this.probeQuotaSpecialTotal = probeQuotaSpecialTotal;
    }

    public Integer getProbeQuotaSpecialUse() {
        return probeQuotaSpecialUse;
    }

    public void setProbeQuotaSpecialUse(Integer probeQuotaSpecialUse) {
        this.probeQuotaSpecialUse = probeQuotaSpecialUse;
    }

    public Integer getProbeQuotaSpecialApply() {
        return probeQuotaSpecialApply;
    }

    public void setProbeQuotaSpecialApply(Integer probeQuotaSpecialApply) {
        this.probeQuotaSpecialApply = probeQuotaSpecialApply;
    }

    public Integer getProbeQuotaHighTotal() {
        return probeQuotaHighTotal;
    }

    public void setProbeQuotaHighTotal(Integer probeQuotaHighTotal) {
        this.probeQuotaHighTotal = probeQuotaHighTotal;
    }

    public Integer getProbeQuotaNormal() {
        return probeQuotaNormal;
    }

    public void setProbeQuotaNormal(Integer probeQuotaNormal) {
        this.probeQuotaNormal = probeQuotaNormal;
    }

    public Integer getProbeQuotaSpecial() {
        return probeQuotaSpecial;
    }

    public void setProbeQuotaSpecial(Integer probeQuotaSpecial) {
        this.probeQuotaSpecial = probeQuotaSpecial;
    }

    public Integer getProbeQuotaHigh() {
        return probeQuotaHigh;
    }

    public void setProbeQuotaHigh(Integer probeQuotaHigh) {
        this.probeQuotaHigh = probeQuotaHigh;
    }

    public Set<ZkbContractAsset> getZkbContractAssets() {
        return zkbContractAssets;
    }

    public void setZkbContractAssets(Set<ZkbContractAsset> zkbContractAssets) {
        this.zkbContractAssets = zkbContractAssets;
    }

    public List<ZkbContractCourse> getZkbContractCourses() {
        return zkbContractCourses;
    }

    public void setZkbContractCourses(List<ZkbContractCourse> zkbContractCourses) {
        this.zkbContractCourses = zkbContractCourses;
    }

    public Integer getSpareQuotaTotal() {
        return spareQuotaTotal;
    }

    public void setSpareQuotaTotal(Integer spareQuotaTotal) {
        this.spareQuotaTotal = spareQuotaTotal;
    }

    public Integer getSpareQuotaApply() {
        return spareQuotaApply;
    }

    public void setSpareQuotaApply(Integer spareQuotaApply) {
        this.spareQuotaApply = spareQuotaApply;
    }

    public Integer getSpareQuotaUse() {
        return spareQuotaUse;
    }

    public void setSpareQuotaUse(Integer spareQuotaUse) {
        this.spareQuotaUse = spareQuotaUse;
    }

    public Integer getProbeQuotaTotal() {
        return probeQuotaTotal;
    }

    public void setProbeQuotaTotal(Integer probeQuotaTotal) {
        this.probeQuotaTotal = probeQuotaTotal;
    }

    public Integer getProbeQuotaApply() {
        return probeQuotaApply;
    }

    public void setProbeQuotaApply(Integer probeQuotaApply) {
        this.probeQuotaApply = probeQuotaApply;
    }

    public Integer getProbeQuotaUse() {
        return probeQuotaUse;
    }

    public void setProbeQuotaUse(Integer probeQuotaUse) {
        this.probeQuotaUse = probeQuotaUse;
    }

    public Integer getGeArtificialSupportTimesTotal() {
        return geArtificialSupportTimesTotal;
    }

    public void setGeArtificialSupportTimesTotal(Integer geArtificialSupportTimesTotal) {
        this.geArtificialSupportTimesTotal = geArtificialSupportTimesTotal;
    }

    public Integer getGeArtificialSupportTimesApply() {
        return geArtificialSupportTimesApply;
    }

    public void setGeArtificialSupportTimesApply(Integer geArtificialSupportTimesApply) {
        this.geArtificialSupportTimesApply = geArtificialSupportTimesApply;
    }

    public Integer getGeArtificialSupportTimesUse() {
        return geArtificialSupportTimesUse;
    }

    public void setGeArtificialSupportTimesUse(Integer geArtificialSupportTimesUse) {
        this.geArtificialSupportTimesUse = geArtificialSupportTimesUse;
    }

    public Integer getSpareArtificailTotal() {
        return spareArtificailTotal;
    }

    public void setSpareArtificailTotal(Integer spareArtificailTotal) {
        this.spareArtificailTotal = spareArtificailTotal;
    }

    public Integer getSpareArtificailApply() {
        return spareArtificailApply;
    }

    public void setSpareArtificailApply(Integer spareArtificailApply) {
        this.spareArtificailApply = spareArtificailApply;
    }

    public Integer getSpareArtificailUse() {
        return spareArtificailUse;
    }

    public void setSpareArtificailUse(Integer spareArtificailUse) {
        this.spareArtificailUse = spareArtificailUse;
    }

    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
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

    public Integer getMaintenanceQuota() {
        return maintenanceQuota;
    }

    public void setMaintenanceQuota(Integer maintenanceQuota) {
        this.maintenanceQuota = maintenanceQuota;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public Integer getSpareArtificail() {
        return spareArtificail;
    }

    public void setSpareArtificail(Integer spareArtificail) {
        this.spareArtificail = spareArtificail;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getContractState() {
        return contractState;
    }

    public void setContractState(String contractState) {
        this.contractState = contractState;
    }

    public Integer getIbNumber() {
        return ibNumber;
    }

    public void setIbNumber(Integer ibNumber) {
        this.ibNumber = ibNumber;
    }

    public Integer getSpareQuota() {
        return spareQuota;
    }

    public void setSpareQuota(Integer spareQuota) {
        this.spareQuota = spareQuota;
    }

    public Integer getProbeQuota() {
        return probeQuota;
    }

    public void setProbeQuota(Integer probeQuota) {
        this.probeQuota = probeQuota;
    }


    public Integer getGeArtificialSupportTimes() {
        return geArtificialSupportTimes;
    }

    public void setGeArtificialSupportTimes(Integer geArtificialSupportTimes) {
        this.geArtificialSupportTimes = geArtificialSupportTimes;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getPrincipalPhone() {
        return principalPhone;
    }

    public void setPrincipalPhone(String principalPhone) {
        this.principalPhone = principalPhone;
    }

    public String getPrincipalEmail() {
        return principalEmail;
    }

    public void setPrincipalEmail(String principalEmail) {
        this.principalEmail = principalEmail;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getAdminPhone() {
        return adminPhone;
    }

    public void setAdminPhone(String adminPhone) {
        this.adminPhone = adminPhone;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getAdminAddress() {
        return adminAddress;
    }

    public void setAdminAddress(String adminAddress) {
        this.adminAddress = adminAddress;
    }

    public Integer getConsumableQuota() {
        return consumableQuota;
    }

    public void setConsumableQuota(Integer consumableQuota) {
        this.consumableQuota = consumableQuota;
    }

    public Integer getConsumableQuotaUse() {
        return consumableQuotaUse;
    }

    public void setConsumableQuotaUse(Integer consumableQuotaUse) {
        this.consumableQuotaUse = consumableQuotaUse;
    }

    public Integer getConsumableQuotaApply() {
        return consumableQuotaApply;
    }

    public void setConsumableQuotaApply(Integer consumableQuotaApply) {
        this.consumableQuotaApply = consumableQuotaApply;
    }

    public Integer getConsumableQuotaTotal() {
        return consumableQuotaTotal;
    }

    public void setConsumableQuotaTotal(Integer consumableQuotaTotal) {
        this.consumableQuotaTotal = consumableQuotaTotal;
    }

    public Integer getConsumeGrowth() {
        return consumeGrowth;
    }

    public void setConsumeGrowth(Integer consumeGrowth) {
        this.consumeGrowth = consumeGrowth;
    }

    public Date getConsumeGrowthDate() {
        return consumeGrowthDate;
    }

    public void setConsumeGrowthDate(Date consumeGrowthDate) {
        this.consumeGrowthDate = consumeGrowthDate;
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
        if (!(object instanceof ZkbContract)) {
            return false;
        }
        ZkbContract other = (ZkbContract) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.ZkbContract[ id=" + id + " ]";
    }

    @Override
    public String getPinYin() {
        return null;
    }

    public Integer getRemainDaysContract()
    {
        Integer zeroDay = new Integer(0);
        if(this.endDate==null)
        {
            return zeroDay;
        }
        Date currentDate = new Date();
        LocalDate startLocaleData = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocaleData = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Integer days = (int)ChronoUnit.DAYS.between(startLocaleData,endLocaleData);
        if(days<0)
        {
            return 0;
        }
        return days;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
}
