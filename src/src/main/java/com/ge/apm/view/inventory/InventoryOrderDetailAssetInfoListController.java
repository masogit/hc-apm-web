package com.ge.apm.view.inventory;

import com.ge.apm.dao.InventoryIssueRepository;
import com.ge.apm.dao.InventoryOrderDetailAssetInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.InventoryIssue;
import com.ge.apm.domain.InventoryOrderDetail;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.inventory.InventoryOrderService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class InventoryOrderDetailAssetInfoListController extends GenericCRUDUUIDController<InventoryOrderDetail> implements Serializable, ServerEventInterface {

	private UserAccount currentUser;
	private InventoryOrderDetailAssetInfoRepository dao;
    private InventoryIssueRepository inventoryIssueRepository;
    private UserAccountRepository userAccountRepository;
    private UaaService uaaService;
	private InventoryOrderService service;
	private List<String> listClinicalDeptName;
    @Getter
    @Setter
    private String orderId;

	@Override
	protected void init() {
		dao = WebUtil.getBean(InventoryOrderDetailAssetInfoRepository.class);
        inventoryIssueRepository = WebUtil.getBean(InventoryIssueRepository.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
		service=WebUtil.getBean(InventoryOrderService.class);
		currentUser = UserContextService.getCurrentUserAccount();
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        orderId = request.getParameter("orderId");
        listClinicalDeptName=service.getInventoryClinicalDeptUID(orderId,true,0);
	}


	@Override
	protected GenericRepositoryUUID<InventoryOrderDetail> getDAO() {
		// TODO Auto-generated method stub
		return dao;
	}
	@Override
	protected Page<InventoryOrderDetail> loadData(PageRequest pageRequest) {
        List<InventoryOrderDetail> inventoryOrderDetailList = new ArrayList<>();
        if(UserContextService.hasCurrentTenantName("")) {
            //before
            List<SearchFilter> searchFilters = this.searchFilters;
            if (searchFilters == null) {
                searchFilters = new ArrayList<SearchFilter>();
            }
            List<SearchFilter> searchFilterList = new ArrayList<>();
            List<SearchFilter> searchFilterNoLedgerList = new ArrayList<>();

            //全院视图
            searchFilters.add(new SearchFilter("inventoryOrderId", SearchFilter.Operator.EQ, orderId, "1*"));

            //无台账
            boolean isSolvedBoolean = false;
            boolean lastModifiedByBoolean = false;
            boolean lastModifiedDateBoolean = false;
            UserAccount user = UserContextService.getCurrentUserAccount();
            String value = null;
            for (SearchFilter searchFilter : searchFilters) {
                if ("isSolved".equals(searchFilter.fieldName)) {
                    isSolvedBoolean = true;
                }
                if ("lastModifiedBy".equals(searchFilter.fieldName)) {
                    lastModifiedByBoolean = true;
                    value = (String) searchFilter.value;
                }
                if ("lastModifiedDate".equals(searchFilter.fieldName)) {
                    lastModifiedDateBoolean = true;
                }
            }
            if (lastModifiedByBoolean) {
                searchFilters.removeIf(e -> e.fieldName.equals("lastModifiedBy"));
                List<UserAccount> userAccountList = userAccountRepository.findByTenantUIDAndNameList(user.getTenantUID(), value);
                List<String> loginNamList = new ArrayList<>();
                if (!userAccountList.isEmpty()) {
                    for (UserAccount userAccount : userAccountList) {
                        loginNamList.add(userAccount.getLoginName());
                    }
                }
                searchFilters.add(new SearchFilter("lastModifiedBy", SearchFilter.Operator.IN, loginNamList, "1*"));
            }
            if (!isSolvedBoolean) {
                searchFilterNoLedgerList.addAll(searchFilters);
                boolean isClinicalDeptName = searchFilterNoLedgerList.removeIf(e -> e.fieldName.equals("assetInfo.clinicalDeptName"));
                boolean isQrCode = searchFilterNoLedgerList.removeIf(e -> e.fieldName.equals("assetInfo.qrCode"));
                for (SearchFilter searchFilter : searchFilters) {
                    if (isClinicalDeptName && "assetInfo.clinicalDeptName".equals(searchFilter.fieldName)) {
                        searchFilterNoLedgerList.add(new SearchFilter("orgName", SearchFilter.Operator.EQ, searchFilter.value, "1*"));
                    }
                    if (isQrCode && "assetInfo.qrCode".equals(searchFilter.fieldName)) {
                        searchFilterNoLedgerList.add(new SearchFilter("qrCode", SearchFilter.Operator.LIKE, searchFilter.value, "1*"));
                    }
                }
                searchFilterNoLedgerList.add(new SearchFilter("status", SearchFilter.Operator.EQ, 2, "1*"));
                searchFilterNoLedgerList.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID(), "1*2+"));
                List<InventoryOrderDetail> inventoryOrderDetails = dao.findBySearchFilter(searchFilterNoLedgerList);
                if (!inventoryOrderDetails.isEmpty()) {
                    for (InventoryOrderDetail inventoryOrderDetail : inventoryOrderDetails) {
                        AssetInfo assetInfo = new AssetInfo();
                        assetInfo.setQrCode(inventoryOrderDetail.getQrCode());
                        assetInfo.setClinicalDeptName(inventoryOrderDetail.getOrgName());
                        inventoryOrderDetail.setAssetInfo(assetInfo);
                    }
                }
                inventoryOrderDetailList.addAll(inventoryOrderDetails);
            }
            List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilterList.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilterList.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilterList.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilterList.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilterList.add(new SearchFilter("assetInfo.assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilterList.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilterList.add(new SearchFilter("assetInfo.isChecked", SearchFilter.Operator.EQ, true, "1*"));
            searchFilterList.add(new SearchFilter("assetInfo.isDeleted", SearchFilter.Operator.EQ, false, "1*"));
            searchFilterList.add(new SearchFilter("assetInfo.isValid", SearchFilter.Operator.EQ, true, "1*"));
            searchFilters.addAll(searchFilterList);

            //获取有问题的工单
            List<SearchFilter> searchFiltersListIssue = new ArrayList(searchFilters);

            boolean isStatus = true;
            for (SearchFilter searchFilter : searchFiltersListIssue) {
                if ("status".equals(searchFilter.fieldName)) {
                    isStatus = false;
                    if ("3".equals(searchFilter.value)) {
                        isStatus = true;
                    }
                }
            }
            searchFiltersListIssue.removeIf(e -> e.fieldName.equals("status"));
            searchFiltersListIssue.removeIf(e -> e.fieldName.equals("lastModifiedBy"));
            searchFiltersListIssue.removeIf(e -> e.fieldName.equals("lastModifiedDate"));
            List<InventoryIssue> inventoryIssueList = inventoryIssueRepository.findBySearchFilter(searchFiltersListIssue);
            List<Integer> AssetInfoIdList = new ArrayList<>();
            List<InventoryOrderDetail> detailList = new ArrayList<>();
            inventoryOrderDetailList = new ArrayList(inventoryOrderDetailList);
            if (!inventoryIssueList.isEmpty()) {
                for (InventoryIssue inventoryIssue : inventoryIssueList) {
                    InventoryOrderDetail inventoryOrderDetail = new InventoryOrderDetail();
                    inventoryOrderDetail.setAssetInfo(inventoryIssue.getAssetInfo());
                    inventoryOrderDetail.setComment(inventoryIssue.getComment());
                    inventoryOrderDetail.setInventoryCheckListItems(inventoryIssue.getInventoryCheckListItems());
                    inventoryOrderDetail.setIsSolved(inventoryIssue.getIsSolved() ? "是" : "否");
                    inventoryOrderDetail.setStatus(3);//有问题
                    inventoryOrderDetail.setClinicalDeptUID(inventoryIssue.getClinicalDept().getUid());
                    inventoryOrderDetail.setClinicalDeptName(inventoryIssue.getClinicalDept().getName());
                    detailList.add(inventoryOrderDetail);
                    AssetInfoIdList.add(inventoryIssue.getAssetInfo().getId());
                }
            }
            if (!lastModifiedByBoolean && !lastModifiedDateBoolean && isStatus) {
                inventoryOrderDetailList.addAll(detailList);
            }
            //已盘点/未盘点 工单
            List<SearchFilter> searchFiltersInventoryOrderDetailList = new ArrayList(searchFilters);
            if (!isSolvedBoolean) {
                if (!AssetInfoIdList.isEmpty()) {
                    searchFiltersInventoryOrderDetailList.add(new SearchFilter("assetInfo.id", SearchFilter.Operator.NotIN, AssetInfoIdList, "1*"));
                }
                if (lastModifiedByBoolean || lastModifiedDateBoolean) {
                    searchFiltersInventoryOrderDetailList.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1, "1*"));
                }
                List<InventoryOrderDetail> inventoryOrderDetails = dao.findBySearchFilter(searchFiltersInventoryOrderDetailList);
                if (!inventoryOrderDetails.isEmpty() && !lastModifiedByBoolean && !lastModifiedDateBoolean) {
                    for (InventoryOrderDetail inventoryOrderDetail : inventoryOrderDetails) {
                        Integer status = inventoryOrderDetail.getStatus();
                        if (status == 0) {
                            inventoryOrderDetail.setLastModifiedDate(null);
                            inventoryOrderDetail.setLastModifiedBy(null);
                        }
                    }
                }
                inventoryOrderDetailList.addAll(inventoryOrderDetails);

            }
        }
        int size = inventoryOrderDetailList.size();
        inventoryOrderDetailList= this.pageNum(inventoryOrderDetailList, pageRequest);
//        if(size>0){
//            for (InventoryOrderDetail inventoryOrderDetail :inventoryOrderDetailList){
//                if(inventoryOrderDetail.getClinicalDeptName()==null){
//                    inventoryOrderDetail.setClinicalDeptName(inventoryOrderDetail.getAssetInfo().getClinicalDeptName());
//                    inventoryOrderDetail.setClinicalDeptUID(inventoryOrderDetail.getClinicalDeptUID());
//                }
//            }
//        }
        Page<InventoryOrderDetail>  inventoryOrderDetailPage = new PageImpl<InventoryOrderDetail>(inventoryOrderDetailList, pageRequest, size);
        //after
		return inventoryOrderDetailPage;
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
	private List<InventoryOrderDetail> pageNum(List<InventoryOrderDetail> inventoryOrderDetailList,PageRequest pageRequest){
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize() - 1;
        int numStart = pageNumber * pageSize;
        numStart = numStart + pageNumber;
        int numEnd = numStart + pageSize + 1;
        int size = inventoryOrderDetailList.size();
        if(size > numEnd){
            inventoryOrderDetailList = inventoryOrderDetailList.subList(numStart ,numEnd);
        }else {
            inventoryOrderDetailList = inventoryOrderDetailList.subList(numStart ,size);
        }
        return inventoryOrderDetailList;
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


}
