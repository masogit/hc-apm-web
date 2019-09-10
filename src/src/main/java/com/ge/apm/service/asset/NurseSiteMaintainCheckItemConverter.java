/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import java.util.HashSet;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.ge.apm.domain.NurseSiteMaintainCheckItem;

/**
 *
 * @author 503035627
 */
@FacesConverter("nurseSiteMaintainCheckItemConverter")
public class NurseSiteMaintainCheckItemConverter implements Converter {
	Set<NurseSiteMaintainCheckItem> setCol=new HashSet<NurseSiteMaintainCheckItem>();
	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		if (value != null && value.trim().length() > 0) {
			try {
				String arr[]=value.split(",");
				NurseSiteMaintainCheckItem item=new NurseSiteMaintainCheckItem();
				item.setId(arr[0]);
				item.setSiteUID(arr[1]);
				item.setDescription(arr[2]);
				item.setName(arr[3]);
				for(NurseSiteMaintainCheckItem t:setCol){
					if(t.equals(item)){
						return t;
					}
				}
				return item;
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
			String name=item.getName();
			String description=item.getDescription();
			String siteUid=item.getSiteUID();
			if(id==null){
				id="";
			}
			if(name==null){
				name="";
			}
			if(description==null){
				description="";
			}
			if(siteUid==null){
				siteUid="";
			}
			setCol.add(item);
			return id+","+siteUid+","+description+","+name+",";
		}
		return "";
	}

}
