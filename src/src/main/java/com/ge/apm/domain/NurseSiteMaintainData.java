package com.ge.apm.domain;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

@Entity
@Cacheable(false)
@Cache(usage=CacheConcurrencyStrategy.NONE)
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
	
	@Column(name="index")
	private Long index;
	
	@JoinColumn(name = "parent_id", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JsonIgnore
    private NurseSiteMaintainItem item;

	@Column(name="variable_text",columnDefinition = "TEXT")
	private String variableText;
	// 是否必填
	@Column(name="required")
	private Boolean required;
	//0:null,1:true,2:false
	@Transient
	private Integer checkValueWeb;
	@Transient
	private Date dateValue;
	@Column(name = "error_text",columnDefinition = "text")
	private String errorText;
	@Transient
	private String description;
	// 是否忽略该检查项（dailyCheckIgnorable 配置启用时可用）
	@Column(name = "is_skip")
	private Boolean isSkip;
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

	public Long getIndex() {
		return index;
	}

	public void setIndex(Long index) {
		this.index = index;
	}

	public NurseSiteMaintainItem getItem() {
		return item;
	}

	public void setItem(NurseSiteMaintainItem item) {
		this.item = item;
	}
	
	public String getStrValue(){
		if(this.checkValue==null){
			return "未检查";
		}
		else if(this.checkValue){
			return "通过";
		}
		else if(!this.checkValue){
			return "有问题";
		}
		return "未检查";
	}

	public Integer getCheckValueWeb() {
		if(this.checkValue==null){
			this.checkValueWeb=0;
		}
		else if(this.checkValue){
			this.checkValueWeb=1;
		}
		else if(!this.checkValue){
			this.checkValueWeb=2;
		}
		return checkValueWeb;
	}

	//0:null,1:true,2:false
	public void setCheckValueWeb(Integer checkValueWeb) {
		if(checkValueWeb!=null){
			if(checkValueWeb.equals(0)){
				this.checkValue=null;
			}
			else if(checkValueWeb.equals(1)){
				this.checkValue=true;
			}
			else if(checkValueWeb.equals(2)){
				this.checkValue=false;
			}
		}
		else{
			this.checkValue=null;
		}
		this.checkValueWeb = checkValueWeb;
	}

	public Boolean getRequired() {
		if(required == null){
			required = Boolean.FALSE;
		}
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
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

	public String getVariableText() {
		return variableText;
	}

	public void setVariableText(String variableText) {
		this.variableText = variableText;
	}

	public Date getDateValue() {

		try {
			if(StringUtils.isNotBlank(variableText)){
				Long aLong = Long.valueOf(variableText);
				return new Date(aLong);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateValue;
	}

	public void setDateValue(Date dateValue) {
		try {
			if(dateValue != null){
				long time = dateValue.getTime();
				this.variableText=String.valueOf(time);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getErrorText() {
		return errorText;
	}

	public void setErrorText(String errorText) {
		this.errorText = errorText;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIsSkip() {
		if (isSkip == null) {
			isSkip = Boolean.FALSE;
		}
		return isSkip;
	}

	public void setIsSkip(Boolean isSkip) {
		this.isSkip = isSkip;
	}
}
