package com.ge.apm.view.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.ge.apm.dao.*;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.InventoryIssue;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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
public class InventoryOrderDetailAssetInfoController2 extends GenericCRUDUUIDController<InventoryOrderDetail> implements Serializable, ServerEventInterface {

	private UserAccount currentUser;
	private InventoryOrderDetailAssetInfoRepository dao;
    private InventoryIssueRepository inventoryIssueRepository;
    private UserAccountRepository userAccountRepository;
    private UaaService uaaService;
    private InventoryOrderService inventoryOrderService;
    private AssetInfoRepository assetInfoRepository;
	private String orderId;
    private Boolean isFinished = true;
	private List<String> listClinicalDeptName;
	private int inventoryStatus = 1;
    private List<String> listAccessClinical;
    private ConfigItemValueRepository configItemValueRepository;

	@Override
	protected void init() {
		dao = WebUtil.getBean(InventoryOrderDetailAssetInfoRepository.class);
        inventoryIssueRepository = WebUtil.getBean(InventoryIssueRepository.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        inventoryOrderService = WebUtil.getBean(InventoryOrderService.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);

        currentUser = UserContextService.getCurrentUserAccount();
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		orderId = request.getParameter("orderId");
        isFinished = Boolean.parseBoolean(request.getParameter("isFinished"));
        if (null == orderId){
            listClinicalDeptName= uaaService.getAllAccessibleClinicalDeptStringList();
        }else {
            listClinicalDeptName=inventoryOrderService.getInventoryClinicalDeptUID(orderId,true,0);
        }
        listAccessClinical=uaaService.findAccessClinicalUidByUserUid(currentUser.getUid());

    }

	@Override
	protected GenericRepositoryUUID<InventoryOrderDetail> getDAO() {
		// TODO Auto-generated method stub
		return dao;
	}

	@Override
	protected Page<InventoryOrderDetail> loadData(PageRequest pageRequest) {
		//before
        if (currentUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(currentUser.getAssetTypeGroupList())) {
            return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
        }
		List<SearchFilter> searchFilters = this.searchFilters;
		if (searchFilters == null) {
			searchFilters = new ArrayList<SearchFilter>();
		}
        List<SearchFilter> searchFilterList = new ArrayList<>();
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
        searchFilterList.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilterList.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilterList.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilterList.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilterList.add(new SearchFilter("assetInfo.assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilterList.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilterList.add(new SearchFilter("assetInfo.isChecked", SearchFilter.Operator.EQ, true,"1*"));
        searchFilterList.add(new SearchFilter("assetInfo.isDeleted", SearchFilter.Operator.EQ, false,"1*"));
        searchFilterList.add(new SearchFilter("assetInfo.isValid", SearchFilter.Operator.EQ, true,"1*"));
        //全院视图
        if(null == orderId){
            List<String> orderIdList = inventoryOrderService.getInventoryOrderData(currentUser.getTenantUID(), listAccessClinical,false);
            if(orderIdList.isEmpty()){
                orderIdList = ListUtil.newArrayList("-1");
            }
            searchFilters.add(new SearchFilter("inventoryOrderId", SearchFilter.Operator.IN, orderIdList, "1*"));
            if (user.isEnableAssetTypeGroup()) {
                searchFilters.add(new SearchFilter("assetInfo.assetType", SearchFilter.Operator.IN, user.getAssetTypeGroupMap().keySet(), "1*"));
            }
        }else {
            searchFilters.add(new SearchFilter("inventoryOrderId", SearchFilter.Operator.EQ, orderId,"1*"));
        }
        if(pageRequest.getSort().getOrderFor("id")!=null){
			pageRequest=new PageRequest(pageRequest.getPageNumber(),pageRequest.getPageSize(),new Sort(Sort.Direction.DESC,"assetInfo.installDate").and(new Sort(Sort.Direction.ASC,"assetInfo.namePyCode")));
		}
        if(UserContextService.hasCurrentTenantName("")){}
        String configValue = configItemValueRepository.findByKeyAndSiteForUseParent(user.getSiteUID(),user.getHospitalUID(),user.getInstitutionUID(),user.getTenantUID(),"enableHasIssueShownInTheInventory");
        if(Boolean.TRUE.toString().equalsIgnoreCase(configValue)){
            List<SearchFilter> inventoryIssueSearchFilters = new ArrayList<>();
            inventoryIssueSearchFilters.addAll(searchFilterList);
            inventoryIssueSearchFilters.add(new SearchFilter("inventoryOrderId", SearchFilter.Operator.EQ, orderId,"1*"));
            List<InventoryIssue> inventoryIssueList = inventoryIssueRepository.findBySearchFilter(inventoryIssueSearchFilters);
            if(!inventoryIssueList.isEmpty()) {
                List<Integer> idList = new ArrayList<>();
                for (InventoryIssue inventoryIssue : inventoryIssueList) {
                    idList.add(inventoryIssue.getAssetInfo().getId());
                }
                if(!idList.isEmpty()){
                    searchFilters.add(new SearchFilter("assetInfo.id", SearchFilter.Operator.NotIN, idList,"1*"));
                }
            }
        }
        String value = null;
        for (SearchFilter searchFilter :searchFilters) {
            if ("lastModifiedBy".equals(searchFilter.fieldName)) {
                value = (String)searchFilter.value;
            }
        }
        if(searchFilters.removeIf(e -> e.fieldName.equals("lastModifiedBy"))){
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
        searchFilters.addAll(searchFilterList);
        Page<InventoryOrderDetail> pageAssetInfo = dao.findBySearchFilter(searchFilters,pageRequest);
//        if(!pageAssetInfo.getContent().isEmpty()){
//            for (InventoryOrderDetail inventoryOrderDetail :pageAssetInfo.getContent()){
//                if(inventoryOrderDetail.getClinicalDeptName()==null){
//                    inventoryOrderDetail.setClinicalDeptName(inventoryOrderDetail.getAssetInfo().getClinicalDeptName());
//                    inventoryOrderDetail.setClinicalDeptUID(inventoryOrderDetail.getClinicalDeptUID());
//                }
//            }
//        }
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

    public void transferNonInventory(InventoryOrderDetail inventoryOrderDetail) {
        InventoryIssue inventoryIssue = inventoryIssueRepository.findProjectionByAssetIdAndInventoryOrderId(inventoryOrderDetail.getAssetInfo().getId(), inventoryOrderDetail.getInventoryOrderId());
        if (ObjectUtil.isNotEmpty(inventoryIssue)) {
            WebUtil.addErrorMessage("当前资产有问题未解决，请检查！");
            return;
        }
        inventoryOrderDetail.setStatus(0);
        dao.save(inventoryOrderDetail);
        Integer assetId = inventoryOrderDetail.getAssetInfo().getId();
        Date lastInventoryDate = dao.findLastInventoryDateByAssetId(assetId);
        AssetInfo assetInfo = assetInfoRepository.findById(assetId);
        assetInfo.setLastInventoryDate(lastInventoryDate);
        assetInfoRepository.save(assetInfo);
    }

}
