package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 505389829
 */
@Entity
@Table(name = "multi_level_approval_config")
@Data
public class MultiLevelApprovalConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;

    /**
     * 审批流名称
     */
    @Column(name = "approval_name")
    private String approvalName;

    /**
     * 审批流编号
     */
    @Column(name = "approval_num")
    private String approvalNum;

    /**
     * 业务类型
     */
    @Column(name = "business_type")
    private String businessType;

    /**
     * 是否启用配置
     */
    @Column(name = "enable")
    private boolean enable;

    /**
     * 所选业务单元
     */
    @Column(name = "org_uid")
    private String orgUid;

    /**
     * 审批流配置信息
     */
    @Column(name = "configration")
    private String configration;

    /**
     * 节点审批人
     */
    @Column(name = "approval_item", columnDefinition = "VARCHAR(1024)")
    private String approvalItem;

    /**
     * 最小金额
     */
    @Transient
    private Integer minAmount;

    /**
     * 最大金额
     */
    @Transient
    private Integer maxAmount;

    /**
     * 设备类型
     */
    @Transient
    private String equipType;

    /**
     * 创建日期
     */
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    /**
     * 创建人
     */
    @CreatedBy
    @Column(name = "created_by", columnDefinition = "VARCHAR(64)")
    private String createdBy;

    /**
     * 租户
     */
    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    /**
     * 集团
     */
    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    /**
     * 医院
     */
    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    /**
     * 院区
     */
    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    public boolean getEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof MultiLevelApprovalConfig)) {
            return false;
        }
        MultiLevelApprovalConfig other = (MultiLevelApprovalConfig) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "MultiLevelApprovalConfig{" +
                "id='" + id + '\'' +
                ", approvalName='" + approvalName + '\'' +
                ", approvalNum='" + approvalNum + '\'' +
                ", businessType='" + businessType + '\'' +
                ", enable=" + enable +
                ", orgUid='" + orgUid + '\'' +
                ", configration='" + configration + '\'' +
                ", approvalItem='" + approvalItem + '\'' +
                ", minAmount=" + minAmount +
                ", maxAmount=" + maxAmount +
                ", equipType='" + equipType + '\'' +
                ", createdDate=" + createdDate +
                ", createdBy='" + createdBy + '\'' +
                ", tenantUID='" + tenantUID + '\'' +
                ", institutionUID='" + institutionUID + '\'' +
                ", hospitalUID='" + hospitalUID + '\'' +
                ", siteUID='" + siteUID + '\'' +
                '}';
    }
}
