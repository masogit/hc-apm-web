package com.ge.apm.domain;

import com.ge.apm.service.utils.ChineseName;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.*;

/**
 * @author : 503189362
 * @date : 2020/4/16
 * @description : 合同实体类
 */
@Entity
@Table(name = "zkb_contract")
public class ZkbContract implements ChineseName, Serializable {
    private static final long serialVersionUID = 1L;

    public enum ContractState {
        effective, noactive, expired
    }

    public enum ContractRegion {
        North,South,East,NW,SW
    }

    public enum ContractType {
        PCB,Biomed,CCS,DI
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
     *备件名额
     */
    @Column(name = "spare_quota")
    private Integer spareQuota = 0;

    /**
     *探头名额
     */
    @Column(name = "probe_quota")
    private Integer probeQuota = 0;

    /**
     *GE人工支持次数
     */
    @Column(name = "ge_support_times")
    private Integer geArtificialSupportTimes = 0;

    /**
     * 保养名额
     */
    @Column(name = "maintenance_quota")
    private Integer maintenanceQuota = 0;

    /**
     *备件/人工/保养
     */
    @Column(name = "spare_artificai")
    private Integer spareArtificail = 0;

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
    @Column(name = "adminAddress")
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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(insertable=false, updatable=false,name = "contract_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    List<ZkbContractCourse> zkbContractCourses = new ArrayList<>();

    public List<ZkbContractCourse> getZkbContractCourses() {
        return zkbContractCourses;
    }

    public void setZkbContractCourses(List<ZkbContractCourse> zkbContractCourses) {
        this.zkbContractCourses = zkbContractCourses;
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
}
