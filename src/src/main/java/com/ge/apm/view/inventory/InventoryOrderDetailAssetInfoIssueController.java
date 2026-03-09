package com.ge.apm.view.inventory;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.event.SelectEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
	private Boolean isFinished = true;
	private List<String> listClinicalDeptName;
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
	@Getter @Setter
	private Boolean inventoryDisplayRepairOwner = Boolean.FALSE;

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
		orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
		enableInventoryMethod = Optional.ofNullable(inventoryOrderService.getConfigValue(currentUser, "enableInventoryMethod")).map(Boolean::parseBoolean).orElse(Boolean.FALSE);
		inventoryDisplayRepairOwner = Optional.ofNullable(inventoryOrderService.getConfigValue(currentUser, "InventoryDisplayRepairOwner")).map(Boolean::parseBoolean).orElse(Boolean.FALSE);
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		orderId = request.getParameter("orderId");
		isFinished = Boolean.parseBoolean(request.getParameter("isFinished"));
		if (null == orderId){
            listClinicalDeptName= uaaService.getAllAccessibleClinicalDeptStringList();
        }else {
			listClinicalDeptName=service.getInventoryClinicalDeptUID(orderId,true,0);
		}
        listAccessClinical=uaaService.findAccessClinicalUidByUserUid(currentUser.getUid());
		loadClinicalDeptList();
	}

	@Override
	protected GenericRepositoryUUID<InventoryIssue> getDAO() {
		// TODO Auto-generated method stub
		return dao;
	}

	@Override
	protected Page<InventoryIssue> loadData(PageRequest pageRequest) {
		if (currentUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(currentUser.getAssetTypeGroupList())) {
			return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
		}
		List<SearchFilter> searchFilters = this.searchFilters;
		if (searchFilters == null) {
			searchFilters = new ArrayList<>();
		}
        List<String> orgUidList = uaaService.getOrgListByUser(currentUser.getUid()).stream().map(OrgInfo::getUid).collect(Collectors.toList());
		covSearchFilters(searchFilters, orgUidList);
		if (ObjectUtil.isNotEmpty(clinicalDeptOrgUidSet)) {
			searchFilters.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, clinicalDeptOrgUidSet, "1*"));
		}
       	boolean hasLastModifiedBy = false; String lastModifiedByFilter = null;
       	for (SearchFilter searchFilter :searchFilters) {
       		if ("assetInfo.lastModifiedBy".equals(searchFilter.fieldName)) {
       			hasLastModifiedBy = true;
				lastModifiedByFilter = (String)searchFilter.value;
       		}
		}
       	if(hasLastModifiedBy) {
			searchFilters.removeIf(e -> e.fieldName.equals("assetInfo.lastModifiedBy"));
			List<UserAccount> userAccountList = userAccountRepository.findByTenantUIDAndNameList(currentUser.getTenantUID(), lastModifiedByFilter);
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
			if(orderIdList.isEmpty()){
				orderIdList = ListUtil.newArrayList("-1");
			}
			searchFilters.add(new SearchFilter("inventoryOrderId", SearchFilter.Operator.IN, orderIdList, "1*"));
			if (currentUser.isEnableAssetTypeGroup()) {
				searchFilters.add(new SearchFilter("assetInfo.assetType", SearchFilter.Operator.IN, currentUser.getAssetTypeGroupMap().keySet(), "1*"));
			}
        }else {
            searchFilters.add(new SearchFilter("inventoryOrderId", SearchFilter.Operator.EQ, orderId,"1*"));
        }
        if(pageRequest.getSort().getOrderFor("id")!=null){
			pageRequest=new PageRequest(pageRequest.getPageNumber(),pageRequest.getPageSize(),new Sort(Sort.Direction.DESC,"assetInfo.installDate").and(new Sort(Sort.Direction.ASC,"assetInfo.namePyCode")));
		}
		return dao.findBySearchFilter(searchFilters, pageRequest);
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
		if (ObjectUtil.isEmpty(assetInventoryOrderDetails)) {
			String clinicalDeptUID = assetInfo.getClinicalDeptUID();
			List<String> orderIdList = inventoryOrderService.getInventoryOrderData(currentUser.getTenantUID(), ListUtil.newArrayList(clinicalDeptUID),false);
			if(CollectionUtils.isNotEmpty(orderIdList)){
				searchFilters.removeIf(e -> e.fieldName.equals("inventoryOrderId"));
				searchFilters.add(new SearchFilter("inventoryOrderId", SearchFilter.Operator.IN, orderIdList,"1*"));
				if (user.isEnableAssetTypeGroup()) {
					searchFilters.add(new SearchFilter("assetType", SearchFilter.Operator.EQ, assetInfo.getAssetType(),"1*"));
				}
			}
			assetInventoryOrderDetails = inventoryOrderDetailDao.findBySearchFilter(searchFilters);
		}
		if (ObjectUtil.isEmpty(assetInventoryOrderDetails)) {
			WebUtil.addErrorMessage("当前设备无盘点工单，无法转入已盘点！");
			return;
		}
		InventoryOrderDetail inventoryOrderDetail = assetInventoryOrderDetails.get(0);
		inventoryOrderDetail.setLastModifiedBy(user.getLoginName());
		inventoryOrderDetail.setLastModifiedDate(DateUtil.now());
		inventoryOrderDetail.setStatus(1);
		inventoryOrderDetailDao.updateIsLastByAssetIds(ListUtil.newArrayList(assetInfo.getId()));
		inventoryOrderDetail.setIsLast(true);
		inventoryOrderDetailDao.save(inventoryOrderDetail);
		dao.delete(assetInventoryIssues);
	}

	public void delete(InventoryIssue inventoryIssue) {
		dao.delete(inventoryIssue);
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
			filterClinicalDeptOrg = null;
		}
		if (filterClinicalDeptOrg != null) {
			List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptList.stream().filter(item -> item.getName().equals(filterClinicalDeptOrg)).collect(Collectors.toList());

			Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptList);
			clinicalDeptOrgUidSet = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
		} else {
			clinicalDeptOrgUidSet = Collections.emptySet();
		}
	}

	public Boolean getFinished() {
		return isFinished;
	}

	public void setFinished(Boolean finished) {
		isFinished = finished;
	}
}
