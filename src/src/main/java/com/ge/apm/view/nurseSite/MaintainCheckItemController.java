package com.ge.apm.view.nurseSite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.TransferEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.DualListModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.NurseSiteMaintainCheckItemRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.MaintainDataPair;
import com.ge.apm.domain.NurseSiteMaintainCheckItem;
import com.ge.apm.domain.NurseSiteMaintainConfig;
import com.ge.apm.domain.NurseSiteMaintainConfigCheckItem;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserAuth;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;
import com.ge.apm.view.SearchController;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

@ManagedBean
@ViewScoped
public class MaintainCheckItemController extends GenericCRUDUUIDController<NurseSiteMaintainCheckItem> implements Serializable{
	private static final Logger LOGGER = LoggerFactory.getLogger(MaintainCheckItemController.class);
    private I18nMessageRepository i18nDao;
    private UserAccount currentUser;
    private NurseSiteMaintainService nurseSiteMaintainService;
    private NurseSiteMaintainCheckItemRepository dao;
  	

    @Override
    protected void init() {
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        dao= WebUtil.getBean(NurseSiteMaintainCheckItemRepository.class);
        nurseSiteMaintainService = WebUtil.getBean(NurseSiteMaintainService.class);
        nurseSiteMaintainService.init();
        //test
    }

    @Override
    protected GenericRepositoryUUID<NurseSiteMaintainCheckItem> getDAO(){
        return dao;
    }

    @Override
    protected Page<NurseSiteMaintainCheckItem> loadData(PageRequest pageRequest) {
    	List<SearchFilter> searchFilters = this.searchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        //need list or set
        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, currentUser.getSiteUID(), "1*"));
        Page<NurseSiteMaintainCheckItem> pageResult=dao.findBySearchFilter(searchFilters, pageRequest);
        return pageResult;
    }

    @Override
    public void save(){
    	boolean exists=dao.findExistsBySiteUid(currentUser.getSiteUID(), this.selected.getName());
    	if(!exists){
    		Date date=new Date();
        	this.selected.setModifyByUid(currentUser.getUid());
        	this.selected.setModifyDate(date);
        	if(this.selected.getKey()==null){
        		this.selected.setKey(date.getTime());
        	}
        	dao.save(this.selected);
            WebUtil.addSuccessMessage("保存成功");
    	}
    	else{
    		WebUtil.addSuccessMessage("请勿重复添加检查项");
    	}
    	this.selected=null;
    }
	
	@Override
	protected String getActionMessage(String actionName) {
		// TODO Auto-generated method stub
		return "日常维护检查项"+" "+WebUtil.getMessage(actionName);
	}
	
	@Override
	public void prepareCreate() throws InstantiationException, IllegalAccessException {
		// TODO Auto-generated method stub
		super.prepareCreate();
		this.selected.setTenantUID(currentUser.getTenantUID());
		this.selected.setInstitutionUID(currentUser.getInstitutionUID());
		this.selected.setHospitalUID(currentUser.getHospitalUID());
		this.selected.setSiteUID(currentUser.getSiteUID());
		this.selected.setCreateByUid(currentUser.getUid());
		this.selected.setCreateDate(new Date());
	}
	
	
	
	private void addMessage(String str) {
		FacesMessage message = new FacesMessage(str,null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
}
