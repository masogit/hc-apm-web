package com.ge.apm.view.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.InventoryOrderRepository;
import com.ge.apm.domain.InventoryOrder;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.inventory.InventoryOrderService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

/**
 * Created by 503035627
 */
@ManagedBean
@ViewScoped
public class InventoryOrderController extends GenericCRUDUUIDController<InventoryOrder> implements Serializable, ServerEventInterface{

	private UserAccount currentUser;
	private InventoryOrderRepository dao;
	private List<String> listAccessClinical;
    private UaaService uaaService;
    private InventoryOrderService service;

	@Override
    protected void init() {
        dao = WebUtil.getBean(InventoryOrderRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        uaaService=WebUtil.getBean(UaaService.class);
        listAccessClinical=uaaService.findAccessClinicalUidByUserUid(currentUser.getUid());
        service=WebUtil.getBean(InventoryOrderService.class);
        service.init();
    }

    @Override
    protected GenericRepositoryUUID<InventoryOrder> getDAO() {
        return dao;
    }

    @Override
    protected Page<InventoryOrder> loadData(PageRequest pageRequest) {
    	List<SearchFilter> searchFilters = this.searchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
//        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID(), "1*"));
//        if(listAccessClinical!=null){
//        	for(int i=0;i<listAccessClinical.size();++i){
//        		searchFilters.add(new SearchFilter("assigneeOrg", SearchFilter.Operator.LIKE, listAccessClinical.get(i), "1*2+"));
//        	}
//        }
//        Page<InventoryOrder> pageRe=dao.findBySearchFilter(searchFilters, pageRequest);
        Page<InventoryOrder> pageRe=service.getPageData(pageRequest, currentUser.getTenantUID(), searchFilters,listAccessClinical);
        return pageRe;
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {

    }

    @Override
    public void onBeforeNewObject(InventoryOrder object) {
        super.onBeforeNewObject(object);
        object.setTenantUID(currentUser.getTenantUID());
        object.setInstitutionUID(currentUser.getInstitutionUID());
        object.setHospitalUID(currentUser.getHospitalUID());
        object.setSiteUID(currentUser.getSiteUID());
    }
	
}
