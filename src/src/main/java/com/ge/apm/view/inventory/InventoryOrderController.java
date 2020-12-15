package com.ge.apm.view.inventory;

import java.io.Serializable;
import java.util.*;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.apm.domain.OrgInfo;
import com.ge.apm.service.inventory.InventoryOrderApiService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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

/**
 * Created by 503035627
 */
@ManagedBean
@ViewScoped
public class InventoryOrderController extends GenericCRUDUUIDController<InventoryOrder> implements Serializable{

	private UserAccount currentUser;
	private InventoryOrderRepository dao;
	private List<String> listAccessClinical;
    private UaaService uaaService;
    private InventoryOrderService service;
    private InventoryOrderApiService apiService;
    @Getter @Setter
    List<OrgInfo> orgsForSelected = new ArrayList<>(); //待选科室
    @Setter @Getter
    List<OrgInfo> selectedOrgs; //勾选盘点科室

	@Override
    protected void init() {
        dao = WebUtil.getBean(InventoryOrderRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        uaaService=WebUtil.getBean(UaaService.class);
        listAccessClinical=uaaService.findAccessClinicalUidByUserUid(currentUser.getUid());
        apiService = WebUtil.getBean(InventoryOrderApiService.class);
        service=WebUtil.getBean(InventoryOrderService.class);
        service.init();
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

    //创建盘点工单 保存
    public void save(){
        //时间校验
        if(selected.getPlanStartTime().after(selected.getPlanEndTime())){
            WebUtil.addErrorMessage("结束时间不得早于开始时间");
            return;
        }
	    try{
            apiService.doCreate(selected, selectedOrgs);
            selectedOrgs.clear();
        }catch(Exception e){
            WebUtil.addErrorMessage("盘点工单新建失败");
        }
        WebUtil.addSuccessMessage("盘点工单新建成功");
    }

    public void onCreate() throws IllegalAccessException, InstantiationException {
        super.prepareCreate();
        orgsForSelected = apiService.onCreateInventory();
    }

    public String getNameWithParent(OrgInfo org){
	    return apiService.getNameWithParent(org);
    }

    @Override
    public void onBeforeNewObject(InventoryOrder object) {
        super.onBeforeNewObject(object);
        object.setTenantUID(currentUser.getTenantUID());
        object.setInstitutionUID(currentUser.getInstitutionUID());
        object.setHospitalUID(currentUser.getHospitalUID());
        object.setSiteUID(currentUser.getSiteUID());
    }


    @Override
    protected GenericRepositoryUUID<InventoryOrder> getDAO() {
        return dao;
    }
	
}
