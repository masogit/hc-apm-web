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
import com.ge.apm.domain.*;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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
		covSearchFilters(searchFilters, orgUidList);
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
	}

	public void transferInventorized(InventoryIssue inventoryIssue) {
		AssetInfo assetInfo = inventoryIssue.getAssetInfo();
		List<SearchFilter> searchFilters = ListUtil.newArrayList(new SearchFilter("assetInfo.id", SearchFilter.Operator.EQ, assetInfo.getId(), "1*"));
		searchFilters.add(new SearchFilter("inventoryOrderId", SearchFilter.Operator.EQ, inventoryIssue.getInventoryOrderId(), "1*"));
		UserAccount user = UserContextService.getCurrentUserAccount();
		List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(OrgInfo::getUid).collect(Collectors.toList());
		covSearchFilters(searchFilters, orgUidList);
		List<InventoryIssue> assetInventoryIssues = dao.findBySearchFilter(searchFilters);
		List<InventoryOrderDetail> assetInventoryOrderDetails = inventoryOrderDetailDao.findBySearchFilter(searchFilters);
		InventoryOrderDetail inventoryOrderDetail = assetInventoryOrderDetails.get(0);
		inventoryOrderDetail.setLastModifiedBy(user.getLoginName());
		inventoryOrderDetail.setLastModifiedDate(DateUtil.now());
		inventoryOrderDetailDao.save(inventoryOrderDetail);
		dao.delete(assetInventoryIssues);
	}

	static void covSearchFilters(List<SearchFilter> searchFilters, List<String> orgUidList) {
		searchFilters.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
		searchFilters.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
		searchFilters.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
		searchFilters.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
		searchFilters.add(new SearchFilter("assetInfo.assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
		searchFilters.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
		searchFilters.add(new SearchFilter("assetInfo.isChecked", SearchFilter.Operator.EQ, true,"1*"));
		searchFilters.add(new SearchFilter("assetInfo.isDeleted", SearchFilter.Operator.EQ, false,"1*"));
		searchFilters.add(new SearchFilter("assetInfo.isValid", SearchFilter.Operator.EQ, true,"1*"));
	}
}
