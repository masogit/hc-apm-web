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
	private Map<Integer,List<String>> mapAccess;
    private UaaService uaaService;

	@Override
    protected void init() {
        dao = WebUtil.getBean(InventoryOrderRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        uaaService=WebUtil.getBean(UaaService.class);
        mapAccess=uaaService.findAccessUidByUserUid(currentUser.getUid());
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
        if(mapAccess.get(0)!=null){
        	searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, mapAccess.get(0), "1*2+"));
        }
        if(mapAccess.get(1)!=null){
        	searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, mapAccess.get(1), "1*2+"));
        }
        if(mapAccess.get(2)!=null){
        	searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, mapAccess.get(2), "1*2+"));
        }
        if(mapAccess.get(3)!=null){
        	searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, mapAccess.get(3), "1*2+"));
        }
        Page<InventoryOrder> pageRe=dao.findBySearchFilter(searchFilters, pageRequest);
        if(mapAccess.get(4)!=null){
        	List<String> setClinical=mapAccess.get(4);
        	if(pageRe.hasContent()){
            	List<InventoryOrder> listRe=pageRe.getContent();
            	List<InventoryOrder> listNewRe=new ArrayList<InventoryOrder>();
            	if(listRe.size()>0){
            		for(int i=0;i<listRe.size();++i){
            			InventoryOrder tempOrder=listRe.get(i);
            			String assigneeOrg=tempOrder.getAssigneeOrg();
            			if(StringUtils.isNotEmpty(assigneeOrg)) {
                            for(String clinicalUid:setClinical){
                            	if(assigneeOrg.contains(clinicalUid)){
                            		listNewRe.add(tempOrder);
                            	}
                            }
                        }
            		}
            	}
            	Page<InventoryOrder> pageNewRe=new PageImpl<InventoryOrder>(listNewRe,pageRequest,pageRe.getTotalElements());
            	return pageNewRe;
            }
        }
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
