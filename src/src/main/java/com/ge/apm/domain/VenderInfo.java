package com.ge.apm.domain;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author lyj
 */
@Entity
@Table(name = "vender_info")
public class VenderInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(32)")
    private String id;



    public enum BidType {
        Vender,Company,Supplier
    }
    //投标人类型预供应商和招标公司
    @Column(name = "bid_Type")
    private String bidType;

    //供应商名称
    @Column(name = "vender_name")
    private String venderName;


    //联系方式
    @Column(name = "vender_phone")
    private String venderPhone;

    //代理人//代理商
    @Column(name = "vender_contacts")
    private String venderContacts;

    //生产企业规模
    @Column(name = "vender_production_size")
    private String venderProductionSize;

    //招标公司名称
    @Column(name = "company_name")
    private String companyName;

    //标的名称
    @Column(name = "company_target")
    private String companyTarget;

    //联系方式
    @Column(name = "company_phone")
    private String companyPhone;

    //负责人
    @Column(name = "company_contacts")
    private String companyContacts;

    //是否为管理员
    @Column(name = "is_admin")
    private Boolean isAdmin;

    @Column(name = "vender_company_id")
    private String venderCompanyId;

    @ManyToOne
    @JoinColumn(name = "vender_company_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable = false, updatable = false)
    private VenderCompany venderCompany;

    @Transient
    private String promptText;

    public String getPromptText() {
        return promptText;
    }

    public void setPromptText(String promptText) {
        this.promptText = promptText;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyTarget() {
        return companyTarget;
    }

    public void setCompanyTarget(String companyTarget) {
        this.companyTarget = companyTarget;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getCompanyContacts() {
        return companyContacts;
    }

    public void setCompanyContacts(String companyContacts) {
        this.companyContacts = companyContacts;
    }

    @Column(name = "opend_id")
    private String openId;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdDate;

    @Column(name = "last_modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastModifiedDate;

    public String getBidType() {
        return bidType;
    }

    public void setBidType(String bidType) {
        this.bidType = bidType;
    }

    public String getVenderProductionSize() {
        return venderProductionSize;
    }

    public void setVenderProductionSize(String venderProductionSize) {
        this.venderProductionSize = venderProductionSize;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVenderName() {
        return venderName;
    }

    public void setVenderName(String venderName) {
        this.venderName = venderName;
    }

    public String getVenderPhone() {
        return venderPhone;
    }

    public void setVenderPhone(String venderPhone) {
        this.venderPhone = venderPhone;
    }

    public String getVenderContacts() {
        return venderContacts;
    }

    public void setVenderContacts(String venderContacts) {
        this.venderContacts = venderContacts;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getVenderCompanyId() {
        return venderCompanyId;
    }

    public void setVenderCompanyId(String venderCompanyId) {
        this.venderCompanyId = venderCompanyId;
    }

    public VenderCompany getVenderCompany() {
        return venderCompany;
    }

    public void setVenderCompany(VenderCompany venderCompany) {
        this.venderCompany = venderCompany;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof VenderInfo)) {
            return false;
        }
        VenderInfo other = (VenderInfo) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }
}

