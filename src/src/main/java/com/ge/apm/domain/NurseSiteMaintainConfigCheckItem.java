package com.ge.apm.domain;

import com.alibaba.fastjson.JSONArray;
import com.ge.apm.domain.pure.TableOption;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by 503035627 on 2018/3/7.
 */
public class NurseSiteMaintainConfigCheckItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long key;
	private String name;
	private String groupName;
	private String description;
	private String id;
	private String fieldType;
	private String defaultValue;
	private List<TableOption> options=new ArrayList<>();
	private Boolean checkValue=Boolean.TRUE;
	private Date dateValue;
	private Boolean required;
	private Boolean defaultPowerOn;
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (key != null ? key.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the key fields are
        // not set
        if (!(object instanceof NurseSiteMaintainConfigCheckItem)) {
            return false;
        }
        NurseSiteMaintainConfigCheckItem other = (NurseSiteMaintainConfigCheckItem) object;
        if ((this.key == null && other.key != null) || (this.key != null && !this.key.equals(other.key))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "NurseSiteMaintainConfigCheckItem [ key=" + key + " ]";
    }

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public List<TableOption> getOptions() {
		return options;
	}

	public void setOptions(List<TableOption> options) {
		this.options = options;
	}

	public Boolean getCheckValue() {
		return checkValue;
	}

	public void setCheckValue(Boolean checkValue) {
		this.checkValue = checkValue;
	}

	public Date getDateValue() {

		try {
			if("Date".equals(fieldType) &&StringUtils.isNotBlank(defaultValue)){
				Long aLong = Long.valueOf(defaultValue);
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
				this.defaultValue=String.valueOf(time);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String fieldTypeStr(){
		String fieldTypeStr="";
		if(StringUtils.isEmpty(fieldType)) return fieldTypeStr;
		switch (fieldType){
			case "Text":
				fieldTypeStr="文本输入";
				break;
			case "Number":
				fieldTypeStr="数字输入";
				break;
			case "Select":
				fieldTypeStr="下拉选择";
				break;
			case "Date":
				fieldTypeStr="日期输入";
				break;

		}
		return fieldTypeStr;
	}

	public String optionStr(){
		if(CollectionUtils.isEmpty(options)){
			return "";
		}else{
			Optional<TableOption> first = options.stream().filter(item -> item.getKey().equals(defaultValue)).findFirst();
			if(first.isPresent()){
				return first.get().getValue();
			}else{
				return "";
			}
		}
	}

	public Boolean getRequired() {
		if(required == null){
			required=Boolean.FALSE;
		}
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public Boolean getDefaultPowerOn() {
		if(defaultPowerOn == null){
			defaultPowerOn = Boolean.FALSE;
		}
		return defaultPowerOn;
	}

	public void setDefaultPowerOn(Boolean defaultPowerOn) {
		this.defaultPowerOn = defaultPowerOn;
	}
}
