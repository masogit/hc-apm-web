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
	private static final String ID = "id";
	private static final String ORG_NAME = "orgName";
	private String orderId;
	private List<Integer> listAssetId;
	private List<String> listClinicalDeptName;

	@Override
	protected void init() {
		dao = WebUtil.getBean(InventoryIssueRepository.class);
		inventoryOrderRepository = WebUtil.getBean(InventoryOrderRepository.class);
		currentUser = UserContextService.getCurrentUserAccount();
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		orderId = request.getParameter("orderId");
		getInventoryClinicalDeptUID(orderId);
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
		searchFilters.add(new SearchFilter("isSolved", SearchFilter.Operator.EQ, false));
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


	private void getInventoryClinicalDeptUID(String orderId) {
		listAssetId = new ArrayList<Integer>();
		listClinicalDeptName = new ArrayList<String>();
		if (StringUtils.isEmpty(orderId)) {
			return;
		}
		InventoryOrder order = inventoryOrderRepository.findById(orderId);
		String assignee_org = order.getAssigneeOrg();
		JSONArray array = new JSONArray(assignee_org);
		JSONObject obj = null;
		Integer assetId;
		String temp = "";
		for (int i = 0; i < array.length(); ++i) {
			obj = array.getJSONObject(i);
			assetId = obj.getInt(ID);
			temp = obj.getString(ORG_NAME);
			temp = temp.substring(temp.lastIndexOf(" ") + 1, temp.length());
			if (!listClinicalDeptName.contains(temp)) {
				listClinicalDeptName.add(temp);
			}
			listAssetId.add(assetId);
		}
	}

	public List<String> getListClinicalDeptName() {
		return listClinicalDeptName;
	}

	public void setListClinicalDeptName(List<String> listClinicalDeptName) {
		this.listClinicalDeptName = listClinicalDeptName;
	}

}
