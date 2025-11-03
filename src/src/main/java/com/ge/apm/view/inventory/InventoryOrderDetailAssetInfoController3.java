package com.ge.apm.view.inventory;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;

import com.ge.apm.dao.ConfigItemValueRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.InventoryOrder;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.SelectEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.ge.apm.dao.InventoryOrderDetailAssetInfoRepository;
import com.ge.apm.dao.InventoryOrderRepository;
import com.ge.apm.domain.InventoryOrderDetail;
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
public class InventoryOrderDetailAssetInfoController3 extends GenericCRUDUUIDController<InventoryOrderDetail> implements Serializable, ServerEventInterface {

	private UserAccount currentUser;
	private InventoryOrderDetailAssetInfoRepository dao;
	private InventoryOrderRepository inventoryOrderRepository;
    private InventoryOrderService inventoryOrderService;
    private UserAccountRepository userAccountRepository;
    private UaaService uaaService;
	private InventoryOrderService service;
	private String orderId;
	private InventoryOrder inventoryOrder;
	private List<String> listClinicalDeptName;
	private static final String ORG_NAME = "orgName";
	private int inventoryStatus = 2;
    private List<String> listAccessClinical;
	private OrgInfoTreeService orgInfoTreeService;
	@Getter @Setter
	private String filterClinicalDeptOrg;
	@Getter @Setter
	private Set<String> clinicalDeptOrgUidSet;
	@Getter @Setter
	private List<OrgInfo> clinicalDeptList;
	@Getter @Setter
	private Boolean enableInventoryMethod = Boolean.FALSE;
	private List<InventoryOrderDetail> selectedItems;

	@Override
	protected void init() {
		dao = WebUtil.getBean(InventoryOrderDetailAssetInfoRepository.class);
		inventoryOrderRepository = WebUtil.getBean(InventoryOrderRepository.class);
        inventoryOrderService = WebUtil.getBean(InventoryOrderService.class);
		currentUser = UserContextService.getCurrentUserAccount();
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
		service=WebUtil.getBean(InventoryOrderService.class);
		orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
		ConfigItemValueRepository configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);
		String configValue = configItemValueRepository.findByKeyAndSiteForUseParent(currentUser.getSiteUID(), currentUser.getHospitalUID(), currentUser.getInstitutionUID(), currentUser.getTenantUID(), "enableInventoryMethod");
		enableInventoryMethod = Optional.ofNullable(configValue).map(Boolean::parseBoolean).orElse(Boolean.FALSE);
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		orderId = request.getParameter("orderId");
        if (null == orderId){
            listClinicalDeptName= uaaService.getAllAccessibleClinicalDeptStringList();
        }else {
            listClinicalDeptName=service.getInventoryClinicalDeptUID(orderId,true,0);
			inventoryOrder = inventoryOrderRepository.findById(orderId);
        }
        listAccessClinical=uaaService.findAccessClinicalUidByUserUid(currentUser.getUid());
		loadClinicalDeptList();
	}

	@Override
	protected GenericRepositoryUUID<InventoryOrderDetail> getDAO() {
		// TODO Auto-generated method stub
		return dao;
	}

	@Override
	protected Page<InventoryOrderDetail> loadData(PageRequest pageRequest) {
		//before
		List<SearchFilter> searchFilters = this.searchFilters;
		if (searchFilters == null) {
			searchFilters = new ArrayList<>();
		}
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID(), "1*2+"));
        //全院视图
        if(null == orderId){
			List<String> orderIdList = inventoryOrderService.getInventoryOrderData(currentUser.getTenantUID(), listAccessClinical,false);
			if(orderIdList.isEmpty()){
				orderIdList = ListUtil.newArrayList("-1");
			}
			searchFilters.add(new SearchFilter("inventoryOrderId", SearchFilter.Operator.IN, orderIdList, "1*"));
        }else {
            searchFilters.add(new SearchFilter("inventoryOrderId", SearchFilter.Operator.EQ, orderId,"1*"));
        }
		if (ObjectUtil.isNotEmpty(filterClinicalDeptOrg)) {
			searchFilters.add(new SearchFilter("orgName", SearchFilter.Operator.EQ, filterClinicalDeptOrg,"1*"));
		}
        String value = null;
        for (SearchFilter searchFilter :searchFilters) {
            if ("lastModifiedBy".equals(searchFilter.fieldName)) {
                value = (String)searchFilter.value;
            }
        }
        if(searchFilters.removeIf(e -> e.fieldName.equals("lastModifiedBy"))){
            UserAccount user = UserContextService.getCurrentUserAccount();
            List<UserAccount> userAccountList = userAccountRepository.findByTenantUIDAndNameList(user.getTenantUID(), value);
            List<String> loginNamList = new ArrayList<>();
            if(!userAccountList.isEmpty()){
                for (UserAccount userAccount:userAccountList) {
                    loginNamList.add(userAccount.getLoginName());
                }
            }
            searchFilters.add(new SearchFilter("lastModifiedBy", SearchFilter.Operator.IN, loginNamList,"1*"));
        }
        searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, inventoryStatus,"1*"));
        Page<InventoryOrderDetail> pageAssetInfo = dao.findBySearchFilter(searchFilters,pageRequest);
		//after
		return pageAssetInfo;
	}
    public String  getUserName(String loginName){
        if( null == loginName){
            return null;
        }
        UserAccount userAccount = userAccountRepository.getByLoginName(loginName);
        if (userAccount != null) {
            return userAccount.getName();
        }
        return null;
    }
	@Override
	public void onServerEvent(String eventName, Object eventObject) {
	}

	@Override
	public void onBeforeNewObject(InventoryOrderDetail object) {
		super.onBeforeNewObject(object);
		object.setTenantUID(currentUser.getTenantUID());
		object.setInstitutionUID(currentUser.getInstitutionUID());
		object.setHospitalUID(currentUser.getHospitalUID());
		object.setSiteUID(currentUser.getSiteUID());
	}

	private void loadClinicalDeptList() {
		clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList().stream().filter(item -> item.getOrgType() == 4).collect(Collectors.toList());
	}

	public List<String> autoCompleteDeptName(String query) {
		if (clinicalDeptList == null) {
			return Collections.emptyList();
		}

		List<OrgInfo> res = this.clinicalDeptList.stream()
				.filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
						|| (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase())))
				.collect(Collectors.toList());
		res.sort(new PinYinComparatorByName());
		return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
	}

	public void onClinicalDeptNameFilterChange(AjaxBehaviorEvent event) {
		if (event instanceof SelectEvent) {
			filterClinicalDeptOrg = (String) ((SelectEvent) event).getObject();
		}
		if (!Strings.isNullOrEmpty(filterClinicalDeptOrg) && !clinicalDeptList.stream().anyMatch(item -> filterClinicalDeptOrg.equals(item.getName()))) {
			filterClinicalDeptOrg = "-1";
		}
	}

	public void delete(InventoryOrderDetail inventoryOrderDetail) {
		dao.delete(inventoryOrderDetail);
		WebUtil.addSuccessMessage(WebUtil.getMessage("DeleteSuccess"));
	}

	public void bathDelete() {
		if (ObjectUtil.isNotEmpty(selectedItems)) {
			dao.delete(selectedItems);
		}
		WebUtil.addSuccessMessage(WebUtil.getMessage("DeleteSuccess"));
	}

	public List<InventoryOrderDetail> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(List<InventoryOrderDetail> selectedItems) {
		this.selectedItems = selectedItems;
	}

	public List<String> getListClinicalDeptName() {
		return listClinicalDeptName;
	}

	public void setListClinicalDeptName(List<String> listClinicalDeptName) {
		this.listClinicalDeptName = listClinicalDeptName;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public InventoryOrder getInventoryOrder() {
		return inventoryOrder;
	}

	public void setInventoryOrder(InventoryOrder inventoryOrder) {
		this.inventoryOrder = inventoryOrder;
	}
}
