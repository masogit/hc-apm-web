package com.ge.apm.view.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.ge.apm.dao.*;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.InventoryOrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.ge.apm.domain.InventoryIssue;
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
public class InventoryOrderDetailAssetInfoIssueController extends GenericCRUDUUIDController<InventoryIssue> implements Serializable, ServerEventInterface {

	private UserAccount currentUser;
	private InventoryIssueRepository dao;
	private AssetInfoRepository assetInfoDao;
    private InventoryOrderService inventoryOrderService;
    private UserAccountRepository userAccountRepository;
	private InventoryOrderDetailAssetInfoRepository inventoryOrderDetailDao;
    private UaaService uaaService;
	private InventoryOrderService service;
	private static final String ORG_NAME = "orgName";
	private String orderId;
	private List<String> listClinicalDeptName;
    private List<String> listAccessClinical;
    private Boolean inventorized;

	@Override
	protected void init() {
		dao = WebUtil.getBean(InventoryIssueRepository.class);
		service=WebUtil.getBean(InventoryOrderService.class);
		assetInfoDao = WebUtil.getBean(AssetInfoRepository.class);
        inventoryOrderService = WebUtil.getBean(InventoryOrderService.class);
        uaaService = WebUtil.getBean(UaaService.class);
		userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
		currentUser = UserContextService.getCurrentUserAccount();
		inventoryOrderDetailDao = WebUtil.getBean(InventoryOrderDetailAssetInfoRepository.class);
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		orderId = request.getParameter("orderId");
		if (null == orderId){
            listClinicalDeptName= uaaService.getAllAccessibleClinicalDeptStringList();
        }else {
            listClinicalDeptName= uaaService.getAllAccessibleClinicalDeptStringList();
		}
        listAccessClinical=uaaService.findAccessClinicalUidByUserUid(currentUser.getUid());
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
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
        searchFilters.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("assetInfo.assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("assetInfo.isChecked", SearchFilter.Operator.EQ, true,"1*"));
        searchFilters.add(new SearchFilter("assetInfo.isDeleted", SearchFilter.Operator.EQ, false,"1*"));
        searchFilters.add(new SearchFilter("assetInfo.isValid", SearchFilter.Operator.EQ, true,"1*"));
       	boolean hasLastModifiedBy = false; String lastModifiedByFilter = null;
       	for (SearchFilter searchFilter :searchFilters) {
       		if ("lastModifiedBy".equals(searchFilter.fieldName)) {
       			hasLastModifiedBy = true;
				lastModifiedByFilter = (String)searchFilter.value;
       		}
		}
       	if(hasLastModifiedBy) {
			searchFilters.removeIf(e -> e.fieldName.equals("lastModifiedBy"));
			List<UserAccount> userAccountList = userAccountRepository.findByTenantUIDAndNameList(user.getTenantUID(), lastModifiedByFilter);
			List<String> loginNamList = new ArrayList<>();
			if(!userAccountList.isEmpty()){
				for (UserAccount userAccount:userAccountList) {
					loginNamList.add(userAccount.getLoginName());
				}
			}
			searchFilters.add(new SearchFilter("lastModifiedBy", SearchFilter.Operator.IN, loginNamList,"1*"));
		}
        //全院视图
        if(null == orderId){
            List<String> orderIdList = inventoryOrderService.getInventoryOrderData(currentUser.getTenantUID(), listAccessClinical,false);
            if(!orderIdList.isEmpty()){
                searchFilters.add(new SearchFilter("inventoryOrderId", SearchFilter.Operator.IN, orderIdList, "1*"));
            }
        }else {
            searchFilters.add(new SearchFilter("inventoryOrderId", SearchFilter.Operator.EQ, orderId,"1*"));
        }
        if(pageRequest.getSort().getOrderFor("id")!=null){
			pageRequest=new PageRequest(pageRequest.getPageNumber(),pageRequest.getPageSize(),new Sort(Sort.Direction.DESC,"assetInfo.installDate").and(new Sort(Sort.Direction.ASC,"assetInfo.namePyCode")));
		}
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

	@Override
	public void save() {
		AssetInfo assetInfo = selected.getAssetInfo();
		dao.save(selected);
		assetInfoDao.save(assetInfo);
		if (Boolean.TRUE.equals(inventorized)) {
			InventoryOrderDetail inventoryOrderDetail = new InventoryOrderDetail();
			inventoryOrderDetail.setStatus(1);
			inventoryOrderDetail.setTenantUID(selected.getTenantUID());
			inventoryOrderDetail.setInstitutionUID(selected.getInstitutionUID());
			inventoryOrderDetail.setHospitalUID(selected.getHospitalUID());
			inventoryOrderDetail.setSiteUID(selected.getSiteUID());
			inventoryOrderDetail.setInventoryOrderId(selected.getInventoryOrderId());
			inventoryOrderDetail.setLastModifiedBy(selected.getLastModifiedBy());
			inventoryOrderDetail.setLastModifiedDate(selected.getLastModifiedDate());
			inventoryOrderDetail.setClinicalDeptUID(selected.getClinicalDept().getUid());
			inventoryOrderDetail.setClinicalDeptName(selected.getClinicalDept().getName());
			inventoryOrderDetail.setAssetInfo(assetInfo);
			inventoryOrderDetailDao.save(inventoryOrderDetail);
			dao.delete(selected);
		}
	}

	public Boolean getInventorized() {
		return inventorized;
	}

	public void setInventorized(Boolean inventorized) {
		this.inventorized = inventorized;
	}
}
