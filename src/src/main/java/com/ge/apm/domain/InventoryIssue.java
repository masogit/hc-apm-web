package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by 212605082 on 2017/8/2.
 */
@Entity
@Table(name = "inventory_issue")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable(true)
public class InventoryIssue implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Basic(optional = false)
    @NotNull
    @Column(name = "inventory_order_id")
    private String inventoryOrderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "clinical_dept_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), nullable = true)
    private OrgInfo clinicalDept;

    @ManyToOne
    @JoinColumn(name = "asset_id", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private AssetInfo assetInfo;

    @Basic(optional = false)
    @NotNull
    @Column(name = "is_solved")
    private boolean isSolved;

    @Column(name = "is_last")
    private Boolean isLast;

    @Column(name = "comment")
    private String comment;

    @Column(name = "inventory_method")
    private String inventoryMethod;
    
    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    @Size(max = 32)
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private Date lastModifiedDate ;

    @ManyToMany(targetEntity = InventoryCheckList.class, cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(name = "inventory_issue_detail",
            joinColumns = @JoinColumn(name = "inventory_issue_id"),
            inverseJoinColumns = @JoinColumn(name = "inventory_check_list_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT)))
    private Set<InventoryCheckList> inventoryCheckLists = new HashSet();
    
    @Transient
    private String inventoryCheckListItems;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInventoryOrderId() {
        return inventoryOrderId;
    }

    public void setInventoryOrderId(String inventoryOrderId) {
        this.inventoryOrderId = inventoryOrderId;
    }

    public boolean getIsSolved() {
        return this.isSolved;
    }

    public void setIsSolved(boolean isSolved) {
        this.isSolved = isSolved;
    }

    public Boolean getIsLast() {
        return isLast;
    }

    public void setIsLast(Boolean isLast) {
        this.isLast = isLast;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getInventoryMethod() {
        return inventoryMethod;
    }

    public void setInventoryMethod(String inventoryMethod) {
        this.inventoryMethod = inventoryMethod;
    }

    public Set<InventoryCheckList> getInventoryCheckLists() {
        return inventoryCheckLists;
    }

    public void setInventoryCheckLists(Set<InventoryCheckList> inventoryCheckLists) {
        this.inventoryCheckLists = inventoryCheckLists;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public OrgInfo getClinicalDept() {
        return clinicalDept;
    }

    public void setClinicalDept(OrgInfo clinicalDept) {
        this.clinicalDept = clinicalDept;
    }
    
    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
    }

    public String getInstitutionUID() {
        return institutionUID;
    }

    public void setInstitutionUID(String institutionUID) {
        this.institutionUID = institutionUID;
    }

    public String getHospitalUID() {
        return hospitalUID;
    }

    public void setHospitalUID(String hospitalUID) {
        this.hospitalUID = hospitalUID;
    }

    public String getSiteUID() {
        return siteUID;
    }

    public void setSiteUID(String siteUID) {
        this.siteUID = siteUID;
    }

	public String getInventoryCheckListItems() {
		String temp="";
		inventoryCheckListItems="";
		if(inventoryCheckLists!=null && inventoryCheckLists.size()>0){
			for(InventoryCheckList obj: inventoryCheckLists){
				temp=obj.getItem();
				if(StringUtils.isNotEmpty(temp)){
					inventoryCheckListItems+=temp+",";
				}
			}
			if(inventoryCheckListItems.length()>0){
				inventoryCheckListItems=inventoryCheckListItems.substring(0, inventoryCheckListItems.length()-1);
			}
		}
		return inventoryCheckListItems;
	}

	public void setInventoryCheckListItems(String inventoryCheckListItems) {
		this.inventoryCheckListItems = inventoryCheckListItems;
	}

    public String getLastModifiedBy() { return lastModifiedBy; }

    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }

    public Date getLastModifiedDate() { return lastModifiedDate; }

    public void setLastModifiedDate(Date lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof InventoryIssue)) {
            return false;
        }
        InventoryIssue other = (InventoryIssue) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }
}
