package com.ge.apm.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "site_to_site_allocation")
public class SiteToSiteAllocation implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column
    private String id;
    @Column(name="asset_uid")
    private String assetUid;
    @Column(name="asset_name")
    private String assetName;//设备名称
    @Column(name="asset_dept_num")
    private String assetDeptNum; //设备编号
    @Column(name="asset_serial_num")
    private String assetSerialNum; //序列号
    @Column(name="asset_qr_code")
    private String assetQrCode; //二维码
    @Column(name="out_dept_uid")
    private String outDeptUid;
    @Column(name="out_dept_name")
    private String outDeptName; //借出科室
    @Column(name="in_dept_uid")
    private String inDeptUid;
    @Column(name="in_dept_name")
    private String inDeptName; //借用科室
    @Column(name="out_site_uid")
    private String outSiteUid;
    @Column(name="out_site_name")
    private String outSiteName;
    @Column(name="in_site_uid")
    private String inSiteUid;
    @Column(name="in_site_name")
    private String inSiteName;
    @Column(name="borrower_uid")
    private String borrowerUid;
    @Column(name="borrower_name")
    private String borrowerName; //借用人
    @Column(name="returner_uid")
    private String returnerUid;
    @Column(name="returner_name")
    private String returnerName; //归还人
    @Column(name="receiver_uid")
    private String receiverUid;
    @Column(name="receiver_name")
    private String receiverName; //归还受理人
    @Column(name="lender_uid")
    private String lenderUid;
    @Column(name="lender_name")
    private String lenderName; //借出人
    @Column(name="created_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name="borrowed_at")
    private Date borrowedAt; //借出时间

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name="return_at")
    private Date returnAt; //归还日期
    @Column(name="lend_remark")
    private String lendRemark; //借用备注
    @Column(name="return_remark")
    private String returnRemark; //归还备注
    @Column(name="borrower_ack")
    private String borrowerAck;
    @Column(name="returner_ack")
    private String returnerAck;
    @Column(name="borrower_ack_ame")
    private String borrowerAckName; //借用确认人
    @Column(name="returner_ack_name")
    private String returnerAckName;  //归还受理人
    @Column(name="request_num")
    private String requestNum;
    @Column(name="borrower_signature_storage_id")
    private String borrowerSignatureStorageId;
    @Column(name="lender_signature_storage_id")
    private String lenderSignatureStorageId;
    @Column(name="returner_signature_storage_id")
    private String returnerSignatureStorageId;
    @Column(name="receiver_signature_storage_id")
    private String receiverSignatureStorageId;
    @Transient
    @JsonProperty
    private String duration; //借用时长

    @Column(name = "borrower_signature_uid")
    private String borrowerSignatureUid;
    @Column(name = "lender_signature_uid")
    private String lenderSignatureUid;
    @Column(name = "returner_signature_uid")
    private String returnerSignatureUid;
    @Column(name = "receiver_signature_uid")
    private String receiverSignatureUid;
    @Column(name = "borrower_signature_date")
    private Date borrowerSignatureDate;
    @Column(name = "lender_signature_date")
    private Date lenderSignatureDate;
    @Column(name = "returner_signature_date")
    private Date returnerSignatureDate;
    @Column(name = "receiver_signature_date")
    private Date receiverSignatureDate;
    @Column(name = "borrower_ack_date")
    private Date borrowerAckDate;
    @Column(name = "receiver_date")
    private Date receiverDate;
    @Column(name = "returner_ack_date")
    private Date returnerAckDate;
    @Transient
    @JsonProperty
    private Boolean enableSimple;


    @Transient
    @JsonProperty
    private List <V2_BlobObject> attachments;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetDeptNum() {
        return assetDeptNum;
    }

    public void setAssetDeptNum(String assetDeptNum) {
        this.assetDeptNum = assetDeptNum;
    }

    public String getAssetSerialNum() {
        return assetSerialNum;
    }

    public void setAssetSerialNum(String assetSerialNum) {
        this.assetSerialNum = assetSerialNum;
    }

    public String getAssetQrCode() {
        return assetQrCode;
    }

    public void setAssetQrCode(String assetQrCode) {
        this.assetQrCode = assetQrCode;
    }

    public String getOutDeptUid() {
        return outDeptUid;
    }

    public void setOutDeptUid(String outDeptUid) {
        this.outDeptUid = outDeptUid;
    }

    public String getOutDeptName() {
        return outDeptName;
    }

    public void setOutDeptName(String outDeptName) {
        this.outDeptName = outDeptName;
    }

    public String getInDeptUid() {
        return inDeptUid;
    }

    public void setInDeptUid(String inDeptUid) {
        this.inDeptUid = inDeptUid;
    }

    public String getInDeptName() {
        return inDeptName;
    }

    public void setInDeptName(String inDeptName) {
        this.inDeptName = inDeptName;
    }

    public String getOutSiteUid() {
        return outSiteUid;
    }

    public void setOutSiteUid(String outSiteUid) {
        this.outSiteUid = outSiteUid;
    }

    public String getOutSiteName() {
        return outSiteName;
    }

    public void setOutSiteName(String outSiteName) {
        this.outSiteName = outSiteName;
    }

    public String getInSiteUid() {
        return inSiteUid;
    }

    public void setInSiteUid(String inSiteUid) {
        this.inSiteUid = inSiteUid;
    }

    public String getInSiteName() {
        return inSiteName;
    }

    public void setInSiteName(String inSiteName) {
        this.inSiteName = inSiteName;
    }

    public String getBorrowerUid() {
        return borrowerUid;
    }

    public void setBorrowerUid(String borrowerUid) {
        this.borrowerUid = borrowerUid;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public String getLenderUid() {
        return lenderUid;
    }

    public void setLenderUid(String lenderUid) {
        this.lenderUid = lenderUid;
    }

    public String getLenderName() {
        return lenderName;
    }

    public void setLenderName(String lenderName) {
        this.lenderName = lenderName;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getBorrowedAt() {
        return borrowedAt;
    }

    public void setBorrowedAt(Date borrowedAt) {
        this.borrowedAt = borrowedAt;
    }

    public Date getReturnAt() {
        return returnAt;
    }

    public void setReturnAt(Date returnAt) {
        this.returnAt = returnAt;
    }

    public String getLendRemark() {
        return lendRemark;
    }

    public void setLendRemark(String lendRemark) {
        this.lendRemark = lendRemark;
    }

    public String getBorrowerAck() {
        return borrowerAck;
    }

    public void setBorrowerAck(String borrowerAck) {
        this.borrowerAck = borrowerAck;
    }

    public String getReturnerAck() {
        return returnerAck;
    }

    public void setReturnerAck(String returnerAck) {
        this.returnerAck = returnerAck;
    }

    public String getRequestNum() {
        return requestNum;
    }

    public void setRequestNum(String requestNum) {
        this.requestNum = requestNum;
    }

    public String getBorrowerSignatureStorageId() {
        return borrowerSignatureStorageId;
    }

    public void setBorrowerSignatureStorageId(String borrowerSignatureStorageId) {
        this.borrowerSignatureStorageId = borrowerSignatureStorageId;
    }

    public String getLenderSignatureStorageId() {
        return lenderSignatureStorageId;
    }

    public void setLenderSignatureStorageId(String lenderSignatureStorageId) {
        this.lenderSignatureStorageId = lenderSignatureStorageId;
    }

    public String getReturnerSignatureStorageId() {
        return returnerSignatureStorageId;
    }

    public void setReturnerSignatureStorageId(String returnerSignatureStorageId) {
        this.returnerSignatureStorageId = returnerSignatureStorageId;
    }

    public String getReceiverSignatureStorageId() {
        return receiverSignatureStorageId;
    }

    public void setReceiverSignatureStorageId(String receiverSignatureStorageId) {
        this.receiverSignatureStorageId = receiverSignatureStorageId;
    }

    public String getReturnRemark() {
        return returnRemark;
    }

    public void setReturnRemark(String returnRemark) {
        this.returnRemark = returnRemark;
    }

    public String getBorrowerAckName() {
        return borrowerAckName;
    }

    public void setBorrowerAckName(String borrowerAckName) {
        this.borrowerAckName = borrowerAckName;
    }

    public String getReturnerAckName() {
        return returnerAckName;
    }

    public void setReturnerAckName(String returnerAckName) {
        this.returnerAckName = returnerAckName;
    }

    public String getReturnerUid() {
        return returnerUid;
    }

    public void setReturnerUid(String returnerUid) {
        this.returnerUid = returnerUid;
    }

    public String getReturnerName() {
        return returnerName;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public void setReturnerName(String returnerName) {
        this.returnerName = returnerName;
    }

    public String getDuration() {
        if (this.borrowedAt == null) {
            return "0.0小时";
        }
        Date startTime = this.borrowedAt;
        Date endTime = this.returnAt == null ? new Date() : this.returnAt;
        long minutes = Duration.between(startTime.toInstant(), endTime.toInstant()).toMinutes();
        double hours = (double) minutes / 60;
        BigDecimal bg = new BigDecimal(hours);
        return bg.setScale(1, RoundingMode.HALF_UP).doubleValue() + "小时";
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getBorrowerSignatureUid() {
        return borrowerSignatureUid;
    }

    public void setBorrowerSignatureUid(String borrowerSignatureUid) {
        this.borrowerSignatureUid = borrowerSignatureUid;
    }

    public String getLenderSignatureUid() {
        return lenderSignatureUid;
    }

    public void setLenderSignatureUid(String lenderSignatureUid) {
        this.lenderSignatureUid = lenderSignatureUid;
    }

    public String getReturnerSignatureUid() {
        return returnerSignatureUid;
    }

    public void setReturnerSignatureUid(String returnerSignatureUid) {
        this.returnerSignatureUid = returnerSignatureUid;
    }

    public String getReceiverSignatureUid() {
        return receiverSignatureUid;
    }

    public void setReceiverSignatureUid(String receiverSignatureUid) {
        this.receiverSignatureUid = receiverSignatureUid;
    }

    public Date getBorrowerSignatureDate() {
        return borrowerSignatureDate;
    }

    public void setBorrowerSignatureDate(Date borrowerSignatureDate) {
        this.borrowerSignatureDate = borrowerSignatureDate;
    }

    public Date getLenderSignatureDate() {
        return lenderSignatureDate;
    }

    public void setLenderSignatureDate(Date lenderSignatureDate) {
        this.lenderSignatureDate = lenderSignatureDate;
    }

    public Date getReturnerSignatureDate() {
        return returnerSignatureDate;
    }

    public void setReturnerSignatureDate(Date returnerSignatureDate) {
        this.returnerSignatureDate = returnerSignatureDate;
    }

    public Date getReceiverSignatureDate() {
        return receiverSignatureDate;
    }

    public void setReceiverSignatureDate(Date receiverSignatureDate) {
        this.receiverSignatureDate = receiverSignatureDate;
    }

    public Date getBorrowerAckDate() {
        return borrowerAckDate;
    }

    public void setBorrowerAckDate(Date borrowerAckDate) {
        this.borrowerAckDate = borrowerAckDate;
    }

    public Date getReceiverDate() {
        return receiverDate;
    }

    public void setReceiverDate(Date receiverDate) {
        this.receiverDate = receiverDate;
    }

    public Date getReturnerAckDate() {
        return returnerAckDate;
    }

    public void setReturnerAckDate(Date returnerAckDate) {
        this.returnerAckDate = returnerAckDate;
    }

    public Boolean getEnableSimple() {
        return enableSimple;
    }

    public void setEnableSimple(Boolean enableSimple) {
        this.enableSimple = enableSimple;
    }

    public List<V2_BlobObject> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<V2_BlobObject> attachments) {
        this.attachments = attachments;
    }
}
