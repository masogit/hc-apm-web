package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 *
 * @author 503202755 2020/10/20
 */
@Entity
@Table(name = "zkb_key_behavior_index_config")
public class ZkbKeyBehaviorIndexConfig {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    /**
     * 租户
     */
    @Column(name = "tenant_uid")
    private String tenantUID;

    /**
     * 备件归还权重
     */
    @Column(name = "part_return_full_score")
    private Integer partReturnFullScore;

    /**
     * 备件好还率权重
     */
    @Column(name = "intact_part_return_rate_full_score")
    private Integer intactPartReturnRateFullScore;

    /**
     * 二维码粘贴率权重
     */
    @Column(name = "paste_quick_response_rate_full_score")
    private Integer pasteQuickResponseRateFullScore;

    /**
     * APM开单率权重
     */
    @Column(name = "apm_billing_rate_full_score")
    private Integer apmBillingRateFullScore;

    /**
     * 满意度回访权重
     */
    @Column(name = "satisfactory_degree_full_score")
    private Integer satisfactoryDegreeFullScore;

    /**
     * 合规及健康安全权重
     */
    @Column(name = "compliance_full_score")
    private Integer complianceFullScore;

    /**
     * 工程师配置权重
     */
    @Column(name = "engineer_full_score")
    private Integer engineerFullScore;

    /**
     * 是否默认配置 1是 0否
     */
    @Column(name = "default_config")
    private Integer defaultConfig;

    /**
     *权重总分
     */
    @Transient
    private Integer totalFullStore;


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ZkbKeyBehaviorIndexConfig)) {
            return false;
        }
        ZkbKeyBehaviorIndexConfig other = (ZkbKeyBehaviorIndexConfig) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.RequireData[ id=" + id + " ]";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
    }

    public Integer getPartReturnFullScore() {
        return partReturnFullScore;
    }

    public void setPartReturnFullScore(Integer partReturnFullScore) {
        this.partReturnFullScore = partReturnFullScore;
    }

    public Integer getIntactPartReturnRateFullScore() {
        return intactPartReturnRateFullScore;
    }

    public void setIntactPartReturnRateFullScore(Integer intactPartReturnRateFullScore) {
        this.intactPartReturnRateFullScore = intactPartReturnRateFullScore;
    }

    public Integer getPasteQuickResponseRateFullScore() {
        return pasteQuickResponseRateFullScore;
    }

    public void setPasteQuickResponseRateFullScore(Integer pasteQuickResponseRateFullScore) {
        this.pasteQuickResponseRateFullScore = pasteQuickResponseRateFullScore;
    }

    public Integer getApmBillingRateFullScore() {
        return apmBillingRateFullScore;
    }

    public void setApmBillingRateFullScore(Integer apmBillingRateFullScore) {
        this.apmBillingRateFullScore = apmBillingRateFullScore;
    }

    public Integer getSatisfactoryDegreeFullScore() {
        return satisfactoryDegreeFullScore;
    }

    public void setSatisfactoryDegreeFullScore(Integer satisfactoryDegreeFullScore) {
        this.satisfactoryDegreeFullScore = satisfactoryDegreeFullScore;
    }

    public Integer getComplianceFullScore() {
        return complianceFullScore;
    }

    public void setComplianceFullScore(Integer complianceFullScore) {
        this.complianceFullScore = complianceFullScore;
    }

    public Integer getEngineerFullScore() {
        return engineerFullScore;
    }

    public void setEngineerFullScore(Integer engineerFullScore) {
        this.engineerFullScore = engineerFullScore;
    }

    public Integer getDefaultConfig() {
        return defaultConfig;
    }

    public void setDefaultConfig(Integer defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    public Integer getTotalFullStore() {
        Integer totalScore = new Integer(0);
        if(this.getPartReturnFullScore()!=null)
        {
            totalScore=totalScore+this.getPartReturnFullScore();
        }
        if(this.getIntactPartReturnRateFullScore()!=null)
        {
            totalScore=totalScore+this.getIntactPartReturnRateFullScore();
        }
        if(this.getPasteQuickResponseRateFullScore()!=null)
        {
            totalScore=totalScore+this.getPasteQuickResponseRateFullScore();
        }
        if(this.getApmBillingRateFullScore()!=null)
        {
            totalScore=totalScore+this.getApmBillingRateFullScore();
        }
        if(this.getSatisfactoryDegreeFullScore()!=null)
        {
            totalScore=totalScore+this.getSatisfactoryDegreeFullScore();
        }
        if(this.getComplianceFullScore()!=null)
        {
            totalScore=totalScore+this.getComplianceFullScore();
        }
        if(this.getEngineerFullScore()!=null)
        {
            totalScore=totalScore+this.getEngineerFullScore();
        }
        return totalScore;
    }

    public void setTotalFullStore(Integer totalFullStore) {
        this.totalFullStore = totalFullStore;
    }
}
