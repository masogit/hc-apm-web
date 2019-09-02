/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.ge.apm.domain.NurseSiteMaintainCheckItem;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;

import webapp.framework.web.WebUtil;

/**
 *
 * @author 503035627
 */
@FacesConverter("nurseSiteMaintainCheckItemConverter")
public class NurseSiteMaintainCheckItemConverter implements Converter {
	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		if (value != null && value.trim().length() > 0) {
			try {
				String arr[]=value.split(",");
				if(arr.length==1){
					NurseSiteMaintainService nurseSiteMaintainService = (NurseSiteMaintainService) WebUtil.getBean(NurseSiteMaintainService.class);
					return nurseSiteMaintainService.getMaintainCheckItemById(value);
				}
				else{
					NurseSiteMaintainCheckItem item=new NurseSiteMaintainCheckItem();
					item.setName(arr[0]);
					item.setDescription(arr[1]);
					item.setKey(Long.valueOf(arr[2]));
					return item;
				}
			} catch (Exception e) {
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public String getAsString(FacesContext fc, UIComponent uic, Object object) {
		if (object == null) {
			return "";
		}
		if (object instanceof NurseSiteMaintainCheckItem) {
			NurseSiteMaintainCheckItem item = (NurseSiteMaintainCheckItem) object;
			String id=item.getId();
			if(StringUtils.isNotEmpty(id)){
				return id;
			}
			else{
				String name=item.getName();
				String description=item.getDescription();
				Long key=item.getKey();
				if(name==null){
					name="";
				}
				if(description==null){
					description="";
				}
				if(key==null){
					key=new Date().getTime();
				}
				return name+","+description+","+key;
			}
		}
		return "";
	}

}
