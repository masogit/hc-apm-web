package com.ge.apm.view.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.alibaba.druid.util.StringUtils;
import com.ge.apm.dao.InventoryIssueRepository;
import com.ge.apm.dao.InventoryOrderRepository;
import com.ge.apm.domain.InventoryIssue;
import com.ge.apm.domain.InventoryOrder;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.inventory.InventoryOrderService;
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
public class InventoryOrderDetailAssetInfoIssueController extends GenericCRUDUUIDController<InventoryIssue> implements Serializable, ServerEventInterface {

	private UserAccount currentUser;
	private InventoryIssueRepository dao;
	private InventoryOrderRepository inventoryOrderRepository;
	private InventoryOrderService service;
	private static final String ORG_NAME = "orgName";
	private String orderId;
	private List<String> listClinicalDeptName;

	@Override
	protected void init() {
		dao = WebUtil.getBean(InventoryIssueRepository.class);
		service=WebUtil.getBean(InventoryOrderService.class);
		inventoryOrderRepository = WebUtil.getBean(InventoryOrderRepository.class);
		currentUser = UserContextService.getCurrentUserAccount();
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		orderId = request.getParameter("orderId");
		listClinicalDeptName=service.getInventoryClinicalDeptUID(orderId,true,0);
	}

	@Override
	protected GenericRepositoryUUID<InventoryIssue> getDAO() {
		// TODO Auto-generated method stub
		return dao;
	}

	@Override
	protected Page<InventoryIssue> loadData(PageRequest pageRequest) {
		List<SearchFilter> searchFilters = this.searchFilters;
		if (searchFilters == null) {
			searchFilters = new ArrayList<>();
		}
		searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID()));
		if (currentUser.getOrgLevel() != 0) {
			if (currentUser.getOrgLevel() == 1) {
				searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, currentUser.getInstitutionUID()));
			} else if (currentUser.getOrgLevel() == 2) {
				searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, currentUser.getHospitalUID()));
			} else {
				searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, currentUser.getSiteUID()));
			}
		}
		searchFilters.add(new SearchFilter("inventoryOrderId", SearchFilter.Operator.EQ, orderId));
		Page<InventoryIssue> pageAssetInfo = dao.findBySearchFilter(searchFilters, pageRequest);
		return pageAssetInfo;
	}

	@Override
	public void onServerEvent(String eventName, Object eventObject) {

	}

	@Override
	public void onBeforeNewObject(InventoryIssue object) {
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

}
