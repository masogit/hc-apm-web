package com.ge.apm.domain;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "nurse_site_maintain_data")
public class NurseSiteMaintainData {
	private static final Logger LOGGER = LoggerFactory.getLogger(NurseSiteMaintainData.class);
	private static final long serialVersionUID = 1L;
	
	
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(columnDefinition = "CHAR(32)")
	private String id;
	
	@Column(name="parent_id", insertable = false, updatable = false)
	private String parentId;
	
	@Column(name="check_item")
	private String checkItem;
	
	@Column(name="check_value")
	private Boolean checkValue;
	
	@JoinColumn(name = "parent_id", referencedColumnName = "id",foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
	@org.hibernate.annotations.ForeignKey(name = "none")
    private NurseSiteMaintainItem item;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	public String getCheckItem() {
		return checkItem;
	}

	public void setCheckItem(String checkItem) {
		this.checkItem = checkItem;
	}

	public Boolean getCheckValue() {
		return checkValue;
	}

	public void setCheckValue(Boolean checkValue) {
		this.checkValue = checkValue;
	}

	public NurseSiteMaintainItem getItem() {
		return item;
	}

	public void setItem(NurseSiteMaintainItem item) {
		this.item = item;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof NurseSiteMaintainData)) {
			return false;
		}
		NurseSiteMaintainData other = (NurseSiteMaintainData) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)) || (this.id==null && other.id==null)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "NurseSiteMaintainData [ id=" + id + " ]";
	}
}
