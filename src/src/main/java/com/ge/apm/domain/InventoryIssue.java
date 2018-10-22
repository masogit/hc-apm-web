package com.ge.apm.domain;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.io.Serializable;
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
    @JoinColumn(name = "clinical_dept_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), nullable = true)
    private OrgInfo clinicalDept;

    @ManyToOne
    @JoinColumn(name = "asset_id", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private AssetInfo assetInfo;

    @Basic(optional = false)
    @NotNull
    @Column(name = "is_solved")
    private boolean isSolved;

    @Column(name = "comment")
    private String comment;
    
    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    @Size(max = 32)
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
    
}
