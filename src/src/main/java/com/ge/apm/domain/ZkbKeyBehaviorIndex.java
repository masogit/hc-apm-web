package com.ge.apm.domain;

import com.ge.apm.service.zkb.ZkbKeyBehaviorIndexService;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author 503202755 2020/9/23
 */
@Entity
@Table(name = "zkb_key_behavior_index")
public class ZkbKeyBehaviorIndex {

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
     * 时间
     */
    @Column(name = "kbi_date")
    private Date kbiDate;

    /**
     * 备件归还权重
     */
    @Column(name = "part_return_full_score")
    private Integer partReturnFullScore;

    /**
     * 备件归还得分
     */
    @Column(name = "part_return_score")
    private Integer partReturnScore;

    /**
     * 二维码粘贴率权重
     */
    @Column(name = "paste_quick_response_rate_full_score")
    private Integer pasteQuickResponseRateFullScore;

    /**
     * 二维码粘贴率得分
     */
    @Column(name = "paste_quick_response_rate_score")
    private Integer pasteQuickResponseRateScore;

    /**
     * APM开单率权重
     */
    @Column(name = "apm_billing_rate_full_score")
    private Integer apmBillingRateFullScore;

    /**
     * APM开单率得分
     */
    @Column(name = "apm_billing_rate_score")
    private Integer apmBillingRateScore;

    /**
     * 满意度回访权重
     */
    @Column(name = "satisfactory_degree_full_score")
    private Integer satisfactoryDegreeFullScore;

    /**
     * 满意度回访得分
     */
    @Column(name = "satisfactory_degree_score")
    private Integer satisfactoryDegreeScore;

    /**
     * 合规及健康安全权重
     */
    @Column(name = "compliance_full_score")
    private Integer complianceFullScore;

    /**
     * 合规及健康安全得分
     */
    @Column(name = "compliance_score")
    private Integer complianceScore;

    /**
     * 工程师配置权重
     */
    @Column(name = "engineer_full_score")
    private Integer engineerFullScore;

    /**
     * 工程师配置得分
     */
    @Column(name = "engineer_score")
    private Integer engineerScore;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * ccsb备注
     */
    @Column(name = "remark_Ccsb")
    @Size(min = 0, max = 256)
    private String remarkCcsb;

    /**
     * 租户名称
     */
    @Transient
    private String tenantName;

    /**
     * 是否生效
     */
    @Column(name = "effect")
    private String effect = ZkbKeyBehaviorIndexService.no;

    /**
     * 总得分
     */
    @Transient
    private Integer totalScore;

    /**
     * 权重总分
     */
    @Transient
    private Integer totalFullScore;


    /**
     * ccsb类型合同重新计算kpi标识
     */
    @Transient
    private boolean recalculate;

    /**
     * 排名
     */
    @Transient
    private Integer rank;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ZkbKeyBehaviorIndex)) {
            return false;
        }
        ZkbKeyBehaviorIndex other = (ZkbKeyBehaviorIndex) object;
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

    public Date getKbiDate() {
        return kbiDate;
    }

    public void setKbiDate(Date kbiDate) {
        this.kbiDate = kbiDate;
    }

    public String getKbiDateFormat()
    {
        if(this.kbiDate==null) return "";
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM");
        return sdf1.format(this.kbiDate);
    }



    /**
     * 计算总分数
     * @return
     */
    public Integer getTotalScore()
    {
        Integer totalScore = new Integer(0);
        if(this.getPartReturnScore()!=null)
        {
            totalScore=totalScore+this.getPartReturnScore();//备件归还得分
        }
        if(this.getPasteQuickResponseRateScore()!=null)
        {
            totalScore=totalScore+this.getPasteQuickResponseRateScore();//二维码粘贴率得分
        }
        if(this.getApmBillingRateScore()!=null)
        {
            totalScore=totalScore+this.getApmBillingRateScore();//APM开单率得分
        }
        if(this.getSatisfactoryDegreeScore()!=null)
        {
            totalScore=totalScore+this.getSatisfactoryDegreeScore();//满意度回访得分
        }
        if(this.getComplianceScore()!=null)
        {
            totalScore=totalScore+this.getComplianceScore();//合规及健康安全得分
        }
        if(this.getEngineerScore()!=null)
        {
            totalScore=totalScore+this.getEngineerScore();//工程师配置得分
        }
        return totalScore;
    }

    public Integer getTotalFullScore()
    {
        Integer totalFullScore = new Integer(0);
        if(this.getPartReturnFullScore()!=null)
        {
            totalFullScore=totalFullScore+this.getPartReturnFullScore();
        }
        if(this.getPasteQuickResponseRateFullScore()!=null)
        {
            totalFullScore=totalFullScore+this.getPasteQuickResponseRateFullScore();
        }
        if(this.getApmBillingRateFullScore()!=null)
        {
            totalFullScore=totalFullScore+this.getApmBillingRateFullScore();
        }
        if(this.getSatisfactoryDegreeFullScore()!=null)
        {
            totalFullScore=totalFullScore+this.getSatisfactoryDegreeFullScore();
        }
        if(this.getComplianceFullScore()!=null)
        {
            totalFullScore=totalFullScore+this.getComplianceFullScore();
        }
        if(this.getEngineerFullScore()!=null)
        {
            totalFullScore=totalFullScore+this.getEngineerFullScore();
        }
        return totalFullScore;
    }

    public Integer getPartReturnFullScore() {
        if(partReturnFullScore==null){
            return 0;
        }
        return partReturnFullScore;
    }

    public void setPartReturnFullScore(Integer partReturnFullScore) {
        this.partReturnFullScore = partReturnFullScore;
    }

    public Integer getPartReturnScore() {
        if(partReturnScore==null){
            return 0;
        }
        return partReturnScore;
    }

    public void setPartReturnScore(Integer partReturnScore) {
        this.partReturnScore = partReturnScore;
    }

    public Integer getPasteQuickResponseRateFullScore() {
        if(pasteQuickResponseRateFullScore==null){
            return 0;
        }
        return pasteQuickResponseRateFullScore;
    }

    public void setPasteQuickResponseRateFullScore(Integer pasteQuickResponseRateFullScore) {
        this.pasteQuickResponseRateFullScore = pasteQuickResponseRateFullScore;
    }

    public Integer getPasteQuickResponseRateScore() {
        if(pasteQuickResponseRateScore==null){
            return 0;
        }
        return pasteQuickResponseRateScore;
    }

    public void setPasteQuickResponseRateScore(Integer pasteQuickResponseRateScore) {
        this.pasteQuickResponseRateScore = pasteQuickResponseRateScore;
    }

    public Integer getApmBillingRateFullScore() {
        if(apmBillingRateFullScore==null){
            return 0;
        }
        return apmBillingRateFullScore;
    }

    public void setApmBillingRateFullScore(Integer apmBillingRateFullScore) {
        this.apmBillingRateFullScore = apmBillingRateFullScore;
    }

    public Integer getApmBillingRateScore() {
        if(apmBillingRateScore==null){
            return 0;
        }
        return apmBillingRateScore;
    }

    public void setApmBillingRateScore(Integer apmBillingRateScore) {
        this.apmBillingRateScore = apmBillingRateScore;
    }

    public Integer getSatisfactoryDegreeFullScore() {
        if(satisfactoryDegreeFullScore==null){
            return 0;
        }
        return satisfactoryDegreeFullScore;
    }

    public void setSatisfactoryDegreeFullScore(Integer satisfactoryDegreeFullScore) {
        this.satisfactoryDegreeFullScore = satisfactoryDegreeFullScore;
    }

    public Integer getSatisfactoryDegreeScore() {
        if(satisfactoryDegreeScore==null){
            return 0;
        }
        return satisfactoryDegreeScore;
    }

    public void setSatisfactoryDegreeScore(Integer satisfactoryDegreeScore) {
        this.satisfactoryDegreeScore = satisfactoryDegreeScore;
    }

    public Integer getComplianceFullScore() {
        if(complianceFullScore==null){
            return 0;
        }
        return complianceFullScore;
    }

    public void setComplianceFullScore(Integer complianceFullScore) {
        this.complianceFullScore = complianceFullScore;
    }

    public Integer getComplianceScore() {
        if(complianceScore==null){
            return 0;
        }
        return complianceScore;
    }

    public void setComplianceScore(Integer complianceScore) {
        this.complianceScore = complianceScore;
    }

    public Integer getEngineerFullScore() {
        if(engineerFullScore==null){
            return 0;
        }
        return engineerFullScore;
    }

    public void setEngineerFullScore(Integer engineerFullScore) {
        this.engineerFullScore = engineerFullScore;
    }

    public Integer getEngineerScore() {
        if(engineerScore==null){
            return 0;
        }
        return engineerScore;
    }

    public void setEngineerScore(Integer engineerScore) {
        this.engineerScore = engineerScore;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public void setTotalFullScore(Integer totalFullScore) {
        this.totalFullScore = totalFullScore;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }



    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public boolean isRecalculate() {
        return recalculate;
    }

    public void setRecalculate(boolean recalculate) {
        this.recalculate = recalculate;
    }

    public String getRemarkCcsb() {
        return remarkCcsb;
    }

    public void setRemarkCcsb(String remarkCcsb) {
        this.remarkCcsb = remarkCcsb;
    }
}
