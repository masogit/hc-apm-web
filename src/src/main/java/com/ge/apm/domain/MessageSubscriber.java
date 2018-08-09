/*
 */
package com.ge.apm.domain;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author 212579464
 */
@Entity
@Table(name = "message_subscriber")
public class MessageSubscriber implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "site_id")
    private int siteId;
    @Basic(optional = false)
    @Column(name = "hospital_id")
    private int hospitalId;


    @Basic(optional = false)
    @Column(name = "asset_id")
    private int assetId;


    @JoinColumn(name = "asset_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private AssetInfo assetInfo;



    @Basic(optional = false)
    @Column(name = "subscribe_user_id")
    private int subscribeUserId;

    @JoinColumn(name = "subscribe_user_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private UserAccount subscriberUser;


    // receiveMsgMode: 0: no subscribe ; 1:all subscribe 2:partial subscribe
    @Basic(optional = false)
    @Column(name = "receive_msg_mode")
    private int receiveMsgMode;
    @Basic(optional = false)
    @Column(name = "is_receive_timeout_msg")
    private boolean isReceiveTimeoutMsg;
    @Basic(optional = false)
    @Column(name = "is_receive_chat_msg")
    private boolean isReceiveChatMsg;

    @Basic(optional = true)
    @Column(name = "level_0")
    private Boolean level0;

    @Basic(optional = true)
    @Column(name = "level_1")
    private Boolean level1;

    @Basic(optional = true)
    @Column(name = "level_2")
    private Boolean level2;

    //1 维修事件 2 保养事件 3 合同到期事件
    @Basic(optional = true)
    @Column(name = "select_type")
    private Integer selectType;


   /* @Basic(optional = true)
    @Column(name = "repair_event")
    private Boolean repairEvent;

    @Basic(optional = true)
    @Column(name = "maintain_event")
    private Boolean maintainEvent;
    @Basic(optional = true)
    @Column(name = "contract_due_event")
    private Boolean contractDueEvent;*/
    @Basic(optional = true)
    @Column(name = "quality_hit")
    private Boolean qualityHit;
    @Basic(optional = true)
    @Column(name = "main_rep_hit")
    private Boolean mainRepHit;

    @CreatedBy
    @Column(name = "created_by", nullable = true, length = 50)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_date", nullable = true)
    private Date createdDate;

    @LastModifiedBy
    @Column(name = "last_modified_by", nullable = true, length = 50)
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_date", nullable = true)
    private Date lastModifiedDate;

    public MessageSubscriber() {
    }

    public MessageSubscriber(Integer id) {
        this.id = id;
    }

    public MessageSubscriber(Integer id, int siteId, int hospitalId, int assetId, int subscribeUserId, int receiveMsgMode, boolean isReceiveTimeoutMsg, boolean isReceiveChatMsg) {
        this.id = id;
        this.siteId = siteId;
        this.hospitalId = hospitalId;
        this.assetId = assetId;
        this.subscribeUserId = subscribeUserId;
        this.receiveMsgMode = receiveMsgMode;
        this.isReceiveTimeoutMsg = isReceiveTimeoutMsg;
        this.isReceiveChatMsg = isReceiveChatMsg;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public int getAssetId() {
        return assetId;
    }

    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }

    public int getSubscribeUserId() {
        return subscribeUserId;
    }

    public void setSubscribeUserId(int subscribeUserId) {
        this.subscribeUserId = subscribeUserId;
    }

    public int getReceiveMsgMode() {
        return receiveMsgMode;
    }

    public void setReceiveMsgMode(int receiveMsgMode) {
        this.receiveMsgMode = receiveMsgMode;
    }

    public boolean getIsReceiveTimeoutMsg() {
        return isReceiveTimeoutMsg;
    }

    public void setIsReceiveTimeoutMsg(boolean isReceiveTimeoutMsg) {
        this.isReceiveTimeoutMsg = isReceiveTimeoutMsg;
    }

    public boolean getIsReceiveChatMsg() {
        return isReceiveChatMsg;
    }

    public void setIsReceiveChatMsg(boolean isReceiveChatMsg) {
        this.isReceiveChatMsg = isReceiveChatMsg;
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
        if (!(object instanceof MessageSubscriber)) {
            return false;
        }
        MessageSubscriber other = (MessageSubscriber) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MessageSubscriber[ id=" + id + " ]";
    }

    public Boolean getLevel0() {
        return level0;
    }

    public void setLevel0(Boolean level0) {
        this.level0 = level0;
    }

    public Boolean getLevel1() {
        return level1;
    }

    public void setLevel1(Boolean level1) {
        this.level1 = level1;
    }

    public Boolean getLevel2() {
        return level2;
    }

    public void setLevel2(Boolean level2) {
        this.level2 = level2;
    }

    public Integer getSelectType() {
        return selectType;
    }

    public void setSelectType(Integer selectType) {
        this.selectType = selectType;
    }

    /*public Boolean getRepairEvent() {
        return repairEvent;
    }

    public void setRepairEvent(Boolean repairEvent) {
        this.repairEvent = repairEvent;
    }

    public Boolean getMaintainEvent() {
        return maintainEvent;
    }

    public void setMaintainEvent(Boolean maintainEvent) {
        this.maintainEvent = maintainEvent;
    }

    public Boolean getContractDueEvent() {
        return contractDueEvent;
    }

    public void setContractDueEvent(Boolean contractDueEvent) {
        this.contractDueEvent = contractDueEvent;
    }*/

    public Boolean getQualityHit() {
        return qualityHit;
    }

    public void setQualityHit(Boolean qualityHit) {
        this.qualityHit = qualityHit;
    }

    public Boolean getMainRepHit() {
        return mainRepHit;
    }

    public void setMainRepHit(Boolean mainRepHit) {
        this.mainRepHit = mainRepHit;
    }

    public UserAccount getSubscriberUser() {
        return subscriberUser;
    }

    public void setSubscriberUser(UserAccount subscriberUser) {
        this.subscriberUser = subscriberUser;
    }

    public boolean isReceiveTimeoutMsg() {
        return isReceiveTimeoutMsg;
    }

    public void setReceiveTimeoutMsg(boolean receiveTimeoutMsg) {
        isReceiveTimeoutMsg = receiveTimeoutMsg;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
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
}
