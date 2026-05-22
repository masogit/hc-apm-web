package com.ge.apm.view.inventory;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.InventoryOrderDetailAssetInfoRepository;
import com.ge.apm.domain.InventoryOrderDetail;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.inventory.InventoryOrderService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.SelectEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by 503035627
 */
@ManagedBean
@ViewScoped
public class InventoryOrderDetailAssetInfoController extends GenericCRUDUUIDController<InventoryOrderDetail> implements Serializable, ServerEventInterface {

	private UserAccount currentUser;
	private InventoryOrderDetailAssetInfoRepository dao;
	private InventoryOrderService service;
    private UaaService uaaService;
    private InventoryOrderService inventoryOrderService;
	private AssetInfoRepository assetInfoRepository;
	private ConfigCacheService cacheService;

	private String orderId;
	private Boolean isFinished = true;
	private List<String> listClinicalDeptName;
	private int inventoryStatus = 0;
    private List<String> listAccessClinical;
	private String encodeAssetStr;
	private OrgInfoTreeService orgInfoTreeService;
	@Getter @Setter
	private List<OrgInfo> deptList;
	@Getter @Setter
	private String filterClinicalDeptOrg;
	@Getter @Setter
	private Set<String> clinicalDeptOrgUidSet;
	@Getter @Setter
	private List<OrgInfo> clinicalDeptList;
	@Getter @Setter
	private Boolean inventoryDisplayRepairOwner = Boolean.FALSE;
	@Getter @Setter
	private Boolean inventoryDisplayAssetDept = Boolean.FALSE;
	@Override
	protected void init() {
		dao = WebUtil.getBean(InventoryOrderDetailAssetInfoRepository.class);
		service=WebUtil.getBean(InventoryOrderService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        inventoryOrderService = WebUtil.getBean(InventoryOrderService.class);
		assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
		orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
		cacheService = WebUtil.getBean(ConfigCacheService.class);
		currentUser = UserContextService.getCurrentUserAccount();
        listAccessClinical=uaaService.findAccessClinicalUidByUserUid(currentUser.getUid());
		inventoryDisplayRepairOwner = Optional.ofNullable(inventoryOrderService.getConfigValue(currentUser, "InventoryDisplayRepairOwner")).map(Boolean::parseBoolean).orElse(Boolean.FALSE);
		inventoryDisplayAssetDept = Optional.ofNullable(inventoryOrderService.getConfigValue(currentUser, "InventoryDisplayAssetDept")).map(Boolean::parseBoolean).orElse(Boolean.FALSE);
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		orderId = request.getParameter("orderId");
		isFinished = Boolean.parseBoolean(request.getParameter("isFinished"));
        if (null == orderId){
            listClinicalDeptName= uaaService.getAllAccessibleClinicalDeptStringList();
        }else {
            listClinicalDeptName=service.getInventoryClinicalDeptUID(orderId,true,0);
        }
		deptList = uaaService.getAllAccessibleClinicalDeptList().stream().filter(item -> item.getOrgType() == 4).filter(item -> listClinicalDeptName.contains(item.getName())).collect(Collectors.toList());
        listAccessClinical=uaaService.findAccessClinicalUidByUserUid(currentUser.getUid());
		loadClinicalDeptList();
	}

	@Override
	protected GenericRepositoryUUID<InventoryOrderDetail> getDAO() {
		// TODO Auto-generated method stub
		return dao;
	}

	List<SearchFilter> bathFilters;

	@Override
	protected Page<InventoryOrderDetail> loadData(PageRequest pageRequest) {
		//before
		if (currentUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(currentUser.getAssetTypeGroupList())) {
			return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
		}
		List<SearchFilter> searchFilters = this.searchFilters;
		if (searchFilters == null) {
			searchFilters = new ArrayList<>();
		}
        List<String> orgUidList = uaaService.getOrgListByUser(currentUser.getUid()).stream().map(OrgInfo::getUid).collect(Collectors.toList());
        searchFilters.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("assetInfo.assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("assetInfo.isChecked", SearchFilter.Operator.EQ, true,"1*"));
        searchFilters.add(new SearchFilter("assetInfo.isDeleted", SearchFilter.Operator.EQ, false,"1*"));
        searchFilters.add(new SearchFilter("assetInfo.isValid", SearchFilter.Operator.EQ, true,"1*"));
		if (ObjectUtil.isNotEmpty(clinicalDeptOrgUidSet)) {
			searchFilters.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, clinicalDeptOrgUidSet, "1*"));
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
		searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, inventoryStatus,"1*"));
		if(pageRequest.getSort().getOrderFor("id")!=null){
			pageRequest=new PageRequest(pageRequest.getPageNumber(),pageRequest.getPageSize(),new Sort(Sort.Direction.DESC,"assetInfo.installDate").and(new Sort(Sort.Direction.ASC,"assetInfo.namePyCode")));
		}
		bathFilters = searchFilters;
		return dao.findBySearchFilter(searchFilters,pageRequest);
	}

	public String getOrgName(String uid) {
		return cacheService.findOrgByUid(uid).getName();
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

	public Boolean getFinished() {
		return isFinished;
	}

	public void setFinished(Boolean finished) {
		isFinished = finished;
	}

	public String getEncodeAssetStr() {
		return encodeAssetStr;
	}

	public void setEncodeAssetStr(String encodeAssetStr) {
		this.encodeAssetStr = encodeAssetStr;
	}

	public void bathInventory() {
		List<InventoryOrderDetail> nonInventoryOrderDetails = dao.findBySearchFilter(bathFilters);
		Date date = new Date();
		nonInventoryOrderDetails.forEach(d -> {
			d.setStatus(1);
			d.setLastModifiedDate(date);
			d.setLastModifiedBy(currentUser.getLoginName());
			d.setInventoryMethod("scanCode");
		});
		dao.save(nonInventoryOrderDetails);
		// 更新资产盘点日期
		Set<Integer> assetIds = nonInventoryOrderDetails.stream().map(t -> t.getAssetInfo().getId()).collect(Collectors.toSet());
		assetInfoRepository.bathInventory(assetIds);
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

}
