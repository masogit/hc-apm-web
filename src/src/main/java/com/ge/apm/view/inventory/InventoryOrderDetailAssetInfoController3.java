package com.ge.apm.view.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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
 * Created by 212605082 on 2017/10/11.
 */
@ManagedBean
@ViewScoped
public class InventoryOrderDetailAssetInfoController3 extends GenericCRUDUUIDController<InventoryOrderDetail> implements Serializable, ServerEventInterface {

	private UserAccount currentUser;
	private InventoryOrderDetailAssetInfoRepository dao;
	private InventoryOrderRepository inventoryOrderRepository;
	private InventoryOrderService service;
	private String orderId;
	private List<String> listClinicalDeptName;
	private static final String ORG_NAME = "orgName";
	private int inventoryStatus = 2;
	private List<String> orgUidList;
    private UaaService uaaService;

	@Override
	protected void init() {
		dao = WebUtil.getBean(InventoryOrderDetailAssetInfoRepository.class);
		inventoryOrderRepository = WebUtil.getBean(InventoryOrderRepository.class);
		currentUser = UserContextService.getCurrentUserAccount();
		service=WebUtil.getBean(InventoryOrderService.class);
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		orderId = request.getParameter("orderId");
		listClinicalDeptName=service.getInventoryClinicalDeptUID(orderId,true,0);
		uaaService=WebUtil.getBean(UaaService.class);
	    orgUidList = uaaService.getOrgListByUser(currentUser.getUid()).stream().map(item -> item.getSiteUID()).collect(Collectors.toList());
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
			searchFilters = new ArrayList<SearchFilter>();
		}
		searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("inventoryOrderId", SearchFilter.Operator.EQ, orderId,"1*"));
        searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, inventoryStatus,"1*"));
        searchFilters.add(new SearchFilter("assetInfo.isChecked", SearchFilter.Operator.EQ, true,"1*"));
        Sort sort=pageRequest.getSort();
		sort.and(new Sort(Sort.Direction.DESC,"assetInfo.installDate").and(new Sort(Sort.Direction.ASC,"assetInfo.namePyCode")));
		Page<InventoryOrderDetail> pageAssetInfo = dao.findBySearchFilter(searchFilters, pageRequest);
		
		//after
		return pageAssetInfo;
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

}
