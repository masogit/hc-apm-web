package com.ge.apm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
	private String check_item;
	
	@Column(name="check_value")
	private Boolean check_value;
	
	@JoinColumn(name = "parent_id", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
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

	public String getCheck_item() {
		return check_item;
	}

	public void setCheck_item(String check_item) {
		this.check_item = check_item;
	}

	public Boolean getCheck_value() {
		return check_value;
	}

	public void setCheck_value(Boolean check_value) {
		this.check_value = check_value;
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
