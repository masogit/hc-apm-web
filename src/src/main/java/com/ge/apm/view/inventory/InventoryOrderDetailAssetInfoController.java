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
import com.ge.apm.dao.InventoryOrderDetailAssetInfoRepository;
import com.ge.apm.dao.InventoryOrderRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.InventoryOrder;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.inventory.InventoryOrderService;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

/**
 * Created by 212605082 on 2017/10/11.
 */
@ManagedBean
@ViewScoped
public class InventoryOrderDetailAssetInfoController extends GenericCRUDController<AssetInfo> implements Serializable, ServerEventInterface {

	private InventoryOrderService inventoryOrderService;
	private UserAccount currentUser;
	private InventoryOrderDetailAssetInfoRepository dao;
	private InventoryOrderRepository inventoryOrderRepository;
	private static final String UID = "uid";
	private String orderId;
	private List<String> listClinicalDeptUID;

	@Override
	protected void init() {
		inventoryOrderService = WebUtil.getBean(InventoryOrderService.class);
		dao = WebUtil.getBean(InventoryOrderDetailAssetInfoRepository.class);
		inventoryOrderRepository = WebUtil.getBean(InventoryOrderRepository.class);
		currentUser = UserContextService.getCurrentUserAccount();
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		orderId = request.getParameter("orderId");
		getInventoryClinicalDeptUID(orderId);
	}

	@Override
	protected GenericRepository<AssetInfo> getDAO() {
		// TODO Auto-generated method stub
		return dao;
	}

	@Override
	protected Page<AssetInfo> loadData(PageRequest pageRequest) {
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
		searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, listClinicalDeptUID));
		searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
		searchFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
		System.out.println("xxx");
		System.out.println(pageRequest.getPageNumber());
		System.out.println(pageRequest.getPageSize());
		Page<AssetInfo> pageAssetInfo = dao.findBySearchFilter(searchFilters, pageRequest);
		if (pageAssetInfo == null) {
			System.out.println("xxx pageAssetInfo is null");
		} else {
			System.out.println("xxx pageAssetInfo size=" + pageAssetInfo.getSize());
		}
		return pageAssetInfo;
	}

	@Override
	public void onServerEvent(String eventName, Object eventObject) {

	}

	@Override
	public void onBeforeNewObject(AssetInfo object) {
		super.onBeforeNewObject(object);
		object.setTenantUID(currentUser.getTenantUID());
		object.setInstitutionUID(currentUser.getInstitutionUID());
		object.setHospitalUID(currentUser.getHospitalUID());
		object.setSiteUID(currentUser.getSiteUID());
	}

	// private List<AssetInfo> getInventoryAsset(String orderId){
	// List<AssetInfo> listResult=new ArrayList<AssetInfo>();
	// if(StringUtils.isEmpty(orderId)){
	// return listResult;
	// }
	// InventoryOrder order=inventoryOrderRepository.findById(orderId);
	// String assignee_org=order.getAssigneeOrg();
	// JSONArray array=new JSONArray(assignee_org);
	// JSONObject obj=null;
	// String clinicalDeptUID="";
	// List<AssetInfo> listAssetInfo;
	// for(int i=0;i<array.length();++i){
	// obj=array.getJSONObject(i);
	// clinicalDeptUID=obj.getString(UID);
	// listAssetInfo=inventoryOrderService.getInventoryAssetList(orderId,
	// clinicalDeptUID);
	// if(listAssetInfo!=null && listAssetInfo.size()>0){
	// listResult.addAll(listAssetInfo);
	// }
	// }
	// return listResult;
	// }

	private void getInventoryClinicalDeptUID(String orderId) {
		listClinicalDeptUID = new ArrayList<String>();
		if (StringUtils.isEmpty(orderId)) {
			return;
		}
		InventoryOrder order = inventoryOrderRepository.findById(orderId);
		String assignee_org = order.getAssigneeOrg();
		JSONArray array = new JSONArray(assignee_org);
		JSONObject obj = null;
		String clinicalDeptUID = "";
		List<AssetInfo> listAssetInfo;
		for (int i = 0; i < array.length(); ++i) {
			obj = array.getJSONObject(i);
			clinicalDeptUID = obj.getString(UID);
			listClinicalDeptUID.add(clinicalDeptUID);
		}
	}

}
