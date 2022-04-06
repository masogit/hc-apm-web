package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author : 503189362
 * @date : 2020/5/18
 * @description :
 */
@Entity
@Table(name = "zkb_contract_asset")
public class ZkbContractAsset {

    private static final long serialVersionUID = 1L;

    public enum AssetTypeEnum{
        highMachine,lowMachine,empty,depot
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "contract_id")
    @Size(max = 32)
    private String contractId;

    @Column(name = "asset_id")
    @Size(max = 32)
    private String assetId;

    /**
     * 高低端机
     */
    @Column(name = "high_low_machine")
    private String highLowMachine;

    @Column(name = "sub_region")
    private String subRegion;

    @Column(name = "modality")
    private String modality;

    /**
     * 责任工程师
     */
    @Column(name = "responsibl_engineer")
    private String responsiblEngineer;

    @Column(name = "ka")
    private String ka;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(insertable=false, updatable=false, name = "asset_id", referencedColumnName = "uid")
    private AssetInfo assetInfo;

    /**
     * highMachine  超声高端机
     * lowMachine   超声低端机
     * empty   N/A
     */
    @Column(name = "asset_type")
    private String assetType;

    @Column(name = "apply_num")
    private Integer applyNum = 0;

    /**
     * empty t f
     */
    @Column(name = "train_reason_flag")
    private String trainReasonFlag;

    /**
     * t f
     */
    @Column(name = "support_maintain_flag")
    private String supportMaintainFlag;

    @Column(name = "maintain_start_date")
    private Date maintainStartDate;

    @Column(name = "maintain_end_date")
    private Date maintainEndDate;

    @Column(name = "last_support_maintain_date")
    private Date lastSupportMaintainDate;

    public Date getMaintainStartDate() {
        return maintainStartDate;
    }

    public void setMaintainStartDate(Date maintainStartDate) {
        this.maintainStartDate = maintainStartDate;
    }

    public Date getMaintainEndDate() {
        return maintainEndDate;
    }

    public void setMaintainEndDate(Date maintainEndDate) {
        this.maintainEndDate = maintainEndDate;
    }

    public Date getLastSupportMaintainDate() {
        return lastSupportMaintainDate;
    }

    public void setLastSupportMaintainDate(Date lastSupportMaintainDate) {
        this.lastSupportMaintainDate = lastSupportMaintainDate;
    }

    public String getSupportMaintainFlag() {
        return supportMaintainFlag;
    }

    public void setSupportMaintainFlag(String supportMaintainFlag) {
        this.supportMaintainFlag = supportMaintainFlag;
    }

    public String getTrainReasonFlag() {
        return trainReasonFlag;
    }

    public void setTrainReasonFlag(String trainReasonFlag) {
        this.trainReasonFlag = trainReasonFlag;
    }

    public Integer getApplyNum() {
        return applyNum;
    }

    public void setApplyNum(Integer applyNum) {
        this.applyNum = applyNum;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getKa() {
        return ka;
    }

    public void setKa(String ka) {
        this.ka = ka;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public String getHighLowMachine() {
        return highLowMachine;
    }

    public void setHighLowMachine(String highLowMachine) {
        this.highLowMachine = highLowMachine;
    }

    public String getSubRegion() {
        return subRegion;
    }

    public void setSubRegion(String subRegion) {
        this.subRegion = subRegion;
    }

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    public String getResponsiblEngineer() {
        return responsiblEngineer;
    }

    public void setResponsiblEngineer(String responsiblEngineer) {
        this.responsiblEngineer = responsiblEngineer;
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
        if (!(object instanceof ZkbContractAsset)) {
            return false;
        }
        if(this==object){
            return true;
        }
        ZkbContractAsset other = (ZkbContractAsset) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.ZkbContractAsset[ id=" + id + " ]";
    }
}
