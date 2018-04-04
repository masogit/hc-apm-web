package com.ge.apm.view.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.TabChangeEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.ge.apm.dao.InventoryOrderDetailAssetInfoRepository;
import com.ge.apm.dao.InventoryOrderRepository;
import com.ge.apm.domain.InventoryOrderDetail;
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
public class InventoryOrderDetailAssetInfoController extends GenericCRUDUUIDController<InventoryOrderDetail> implements Serializable, ServerEventInterface {

	private UserAccount currentUser;
	private InventoryOrderDetailAssetInfoRepository dao;
	private String orderId;
	private List<String> listClinicalDeptName;
	private int inventoryStatus = 0;
	private boolean initBln=true;
	private int initIndex=0;
	private List<Integer> listStatus;

	@Override
	protected void init() {
		listStatus=new ArrayList<Integer>();
		listStatus.add(0);
		listStatus.add(1);
		listStatus.add(2);
		dao = WebUtil.getBean(InventoryOrderDetailAssetInfoRepository.class);
		currentUser = UserContextService.getCurrentUserAccount();
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		orderId = request.getParameter("orderId");
	}

	@Override
	protected GenericRepositoryUUID<InventoryOrderDetail> getDAO() {
		// TODO Auto-generated method stub
		return dao;
	}

	@Override
	protected Page<InventoryOrderDetail> loadData(PageRequest pageRequest) {
		//before
		if(initBln){
			inventoryStatus=listStatus.get(initIndex);
			initIndex++;
		}
		
		List<SearchFilter> searchFilters = this.searchFilters;
		if (searchFilters == null) {
			searchFilters = new ArrayList<SearchFilter>();
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
		searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, inventoryStatus));
		Page<InventoryOrderDetail> pageAssetInfo = dao.findBySearchFilter(searchFilters, pageRequest);
		getClinicalDeptNames(pageAssetInfo);
		//after
		if(initBln){
			if(initIndex==listStatus.size()){
				initBln=false;
				inventoryStatus=0;
			}
		}
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

	public void onTabChanged(TabChangeEvent event) {
		String tabName = event.getTab().getId();
		if ("tab0".equals(tabName))
			inventoryStatus = 0;
		else if ("tab1".equals(tabName)) {
			inventoryStatus = 1;
		} else if ("tab2".equals(tabName)) {
			inventoryStatus = 2;
		} else if ("tab3".equals(tabName))
			inventoryStatus = 3;
		else
			inventoryStatus = 0;
		
	}

//	private void getInventoryClinicalDeptUID(String orderId) {
//		listClinicalDeptUID = new ArrayList<String>();
//		listClinicalDeptName = new ArrayList<String>();
//		if (StringUtils.isEmpty(orderId)) {
//			return;
//		}
//		InventoryOrder order = inventoryOrderRepository.findById(orderId);
//		String assignee_org = order.getAssigneeOrg();
//		JSONArray array = new JSONArray(assignee_org);
//		JSONObject obj = null;
//		String clinicalDeptUID = "";
//		String temp = "";
//		for (int i = 0; i < array.length(); ++i) {
//			obj = array.getJSONObject(i);
//			clinicalDeptUID = obj.getString(UID);
//			temp = obj.getString(ORG_NAME);
//			temp = temp.substring(temp.lastIndexOf(" ") + 1, temp.length());
//			if (!listClinicalDeptName.contains(temp)) {
//				listClinicalDeptName.add(temp);
//			}
//			listClinicalDeptUID.add(clinicalDeptUID);
//		}
//	}
	
	private void getClinicalDeptNames(Page<InventoryOrderDetail> pageInventoryOrderDetail){
		
		if(listClinicalDeptName==null){
			listClinicalDeptName = new ArrayList<String>();
		}
		if(initBln){
			String name="";
			for(InventoryOrderDetail detail: pageInventoryOrderDetail){
				name=detail.getAssetInfo().getClinicalDeptName();
				if(StringUtils.isNotEmpty(name)){
					if(!listClinicalDeptName.contains(name)){
						listClinicalDeptName.add(name);
					}
				}
			}
		}
		
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
