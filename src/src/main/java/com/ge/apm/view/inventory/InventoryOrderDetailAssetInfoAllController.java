package com.ge.apm.view.inventory;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.InventoryOrderMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.inventory.InventoryOrderService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.google.common.base.Strings;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class InventoryOrderDetailAssetInfoAllController extends GenericCRUDUUIDController<InventoryOrderDetail> implements Serializable, ServerEventInterface {

	private UserAccount currentUser;
	private InventoryOrderDetailAssetInfoRepository dao;
    private InventoryOrderService inventoryOrderService;
    private UserAccountRepository userAccountRepository;
    private InventoryOrderMapper inventoryOrderMapper;
    private UaaService uaaService;
	private List<String> listClinicalDeptName;
    private List<String> listAccessClinical;
    private String queryAssetUid;
	@Override
	protected void init() {
		dao = WebUtil.getBean(InventoryOrderDetailAssetInfoRepository.class);
        inventoryOrderService = WebUtil.getBean(InventoryOrderService.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        inventoryOrderMapper = WebUtil.getBean(InventoryOrderMapper.class);
		currentUser = UserContextService.getCurrentUserAccount();
        listClinicalDeptName= uaaService.getAllAccessibleClinicalDeptStringList();
        listAccessClinical=uaaService.findAccessClinicalUidByUserUid(currentUser.getUid());
        String enCodeParameters = WebUtil.getRequestParameter("str");
        if (!Strings.isNullOrEmpty(enCodeParameters)) {
            queryAssetUid = (String) UrlEncryptController
                    .getValueFromMap(enCodeParameters, "queryAssetUID");
        }
	}

	@Override
	protected GenericRepositoryUUID<InventoryOrderDetail> getDAO() {
		// TODO Auto-generated method stub
		return dao;
	}
    public String changePage(boolean flag){
	    if(flag){
            return "inventoryOrder?faces-redirect=true";
        }
        return "inventoryOrderDetailAssetInfoMainAll?faces-redirect=true";
    }

    @Override
    protected Page<InventoryOrderDetail> loadData(PageRequest pageRequest) {
        //before
        Map<String, Object> param = MapUtil.newHashMap();
        List<SearchFilter> searchFilters = this.searchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        List<String> orderIdList = inventoryOrderService.getInventoryOrderData(currentUser.getTenantUID(), listAccessClinical,false);
        List<String> orgUidList = uaaService.getOrgListByUser(currentUser.getUid()).stream().map(OrgInfo::getUid).collect(Collectors.toList());
        param.put("tenantUid", currentUser.getTenantUID());
        param.put("page", pageRequest.getPageSize());
        param.put("offset", pageRequest.getPageNumber() * pageRequest.getPageSize());
        if (ObjectUtil.isNotEmpty(orderIdList)) {
            param.put("orderIdList", orderIdList);
        }
        if (ObjectUtil.isNotEmpty(orgUidList)) {
            param.put("orgUidList", orgUidList);
        }
        if (ObjectUtil.isNotEmpty(queryAssetUid)) {
            param.put("queryAssetUid", queryAssetUid);
        }
        inventoryOrderService.covQueryCondition(searchFilters, param);

        List<Map<String, Object>> issueOrderList = inventoryOrderMapper.findIssueOrder(param);
        List<Map<String, Object>> detailOrderList = inventoryOrderMapper.findDetailOrder(param);
        List<Integer> issueAssetIdList = issueOrderList.stream().map(map -> Integer.parseInt(map.get("assetid").toString())).collect(Collectors.toList());
        List<Integer> detailAssetList = detailOrderList.stream().map(map -> Integer.parseInt(map.get("assetid").toString())).collect(Collectors.toList());
        if (ObjectUtil.isNotEmpty(issueAssetIdList)) {
            param.put("issueAssetIdList", issueAssetIdList);
        }
        if (ObjectUtil.isNotEmpty(detailAssetList)) {
            param.put("detailAssetList", detailAssetList);
        }
        List<Map<String, Object>> joinAssetList = inventoryOrderMapper.findJoinAssetList(param);
        Long count = inventoryOrderMapper.findJoinAssetCount(param);
        List<InventoryOrderDetail> resultList = joinAssetList.stream().map(map -> JSON.toJavaObject(JSON.parseObject(JSON.toJSONString(map)), InventoryOrderDetail.class)).collect(Collectors.toList());
        return new PageImpl<>(resultList, pageRequest, count);
    }

/*	@Override
	protected Page<InventoryOrderDetail> loadData(PageRequest pageRequest) {
        //before
        loadData1(pageRequest);
        List<SearchFilter> searchFilters = this.searchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        List<SearchFilter> searchFilterList = new ArrayList<>();
        List<SearchFilter> searchFilterNoLedgerList = new ArrayList<>();
        List<InventoryOrderDetail> inventoryOrderDetailList = new ArrayList<>();
        //全院视图
        List<String> orderIdList = inventoryOrderService.getInventoryOrderData(currentUser.getTenantUID(), listAccessClinical,false);
        if(!orderIdList.isEmpty()){
            searchFilterList.add(new SearchFilter("inventoryOrderId", SearchFilter.Operator.IN, orderIdList, "1*"));
        }
        //无台账
        boolean isSolvedBoolean = false;
        boolean lastModifiedByBoolean = false;
        boolean lastModifiedDateBoolean = false;
        UserAccount user = UserContextService.getCurrentUserAccount();
        String value = null;
        for (SearchFilter searchFilter :searchFilters) {
            if ("isSolved".equals(searchFilter.fieldName)) {
                isSolvedBoolean = true;
            }
            if ("lastModifiedBy".equals(searchFilter.fieldName)) {
                lastModifiedByBoolean = true;
                value = (String)searchFilter.value;
            }
            if ("lastModifiedDate".equals(searchFilter.fieldName)) {
                lastModifiedDateBoolean = true;
            }
        }
        if(lastModifiedByBoolean){
            searchFilters.removeIf(e -> e.fieldName.equals("lastModifiedBy"));
            List<UserAccount> userAccountList = userAccountRepository.findByTenantUIDAndNameList(user.getTenantUID(), value);
            List<String> loginNamList = new ArrayList<>();
            if(!userAccountList.isEmpty()){
                for (UserAccount userAccount:userAccountList) {
                    loginNamList.add(userAccount.getLoginName());
                }
            }
            searchFilters.add(new SearchFilter("lastModifiedBy", SearchFilter.Operator.IN, loginNamList,"1*"));
        }
        if(!isSolvedBoolean){
            searchFilterNoLedgerList.addAll(searchFilters);
            boolean isClinicalDeptName = searchFilterNoLedgerList.removeIf(e -> e.fieldName.equals("assetInfo.clinicalDeptName"));
            boolean isQrCode = searchFilterNoLedgerList.removeIf(e -> e.fieldName.equals("assetInfo.qrCode"));
            for (SearchFilter searchFilter :searchFilters) {
                if(isClinicalDeptName && "assetInfo.clinicalDeptName".equals(searchFilter.fieldName)){
                    searchFilterNoLedgerList.add(new SearchFilter("orgName", SearchFilter.Operator.EQ, searchFilter.value,"1*"));
                }
                if(isQrCode && "assetInfo.qrCode".equals(searchFilter.fieldName)){
                    searchFilterNoLedgerList.add(new SearchFilter("qrCode", SearchFilter.Operator.LIKE, searchFilter.value,"1*"));
                }
            }
            searchFilterNoLedgerList.add(new SearchFilter("status", SearchFilter.Operator.EQ, 2,"1*"));
            if(!orderIdList.isEmpty()){
                searchFilterNoLedgerList.add(new SearchFilter("inventoryOrderId", SearchFilter.Operator.IN, orderIdList, "1*"));
            }
            searchFilterNoLedgerList.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID(), "1*2+"));
            List<InventoryOrderDetail> inventoryOrderDetails = dao.findBySearchFilter(searchFilterNoLedgerList);
            if(!inventoryOrderDetails.isEmpty()){
                for ( InventoryOrderDetail inventoryOrderDetail: inventoryOrderDetails) {
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
        searchFilterList.add(new SearchFilter("assetInfo.isChecked", SearchFilter.Operator.EQ, true,"1*"));
        searchFilterList.add(new SearchFilter("assetInfo.isDeleted", SearchFilter.Operator.EQ, false,"1*"));
        searchFilterList.add(new SearchFilter("assetInfo.isValid", SearchFilter.Operator.EQ, true,"1*"));
        searchFilters.addAll(searchFilterList);

        //获取有问题的工单
        List<SearchFilter> searchFiltersListIssue = new ArrayList(searchFilters);

        boolean isStatus = true;
        for (SearchFilter searchFilter :searchFiltersListIssue) {
            if("status".equals(searchFilter.fieldName)){
                isStatus =false;
                if("3".equals(searchFilter.value)){
                    isStatus=true;
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
        if (!inventoryIssueList.isEmpty()){
            for (InventoryIssue inventoryIssue :inventoryIssueList) {
                InventoryOrderDetail inventoryOrderDetail = new InventoryOrderDetail();
                inventoryOrderDetail.setAssetInfo(inventoryIssue.getAssetInfo());
                inventoryOrderDetail.setComment(inventoryIssue.getComment());
                inventoryOrderDetail.setInventoryCheckListItems(inventoryIssue.getInventoryCheckListItems());
                inventoryOrderDetail.setIsSolved(inventoryIssue.getIsSolved() ? "是" : "否" );
                inventoryOrderDetail.setStatus(3);//有问题
                detailList.add(inventoryOrderDetail);
                AssetInfoIdList.add(inventoryIssue.getAssetInfo().getId());
            }
        }
        if (!lastModifiedByBoolean && !lastModifiedDateBoolean && isStatus){
            inventoryOrderDetailList.addAll(detailList);
        }
        //已盘点/未盘点 工单
        List<SearchFilter> searchFiltersInventoryOrderDetailList = new ArrayList(searchFilters);
        if(!isSolvedBoolean){
            if(!AssetInfoIdList.isEmpty()){
                searchFiltersInventoryOrderDetailList.add(new SearchFilter("assetInfo.id", SearchFilter.Operator.NotIN, AssetInfoIdList,"1*"));
            }
            if(lastModifiedByBoolean || lastModifiedDateBoolean){
                searchFiltersInventoryOrderDetailList.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1,"1*"));
            }
            List<InventoryOrderDetail> inventoryOrderDetails = dao.findBySearchFilter(searchFiltersInventoryOrderDetailList);
            if(!inventoryOrderDetails.isEmpty() && !lastModifiedByBoolean && !lastModifiedDateBoolean){
                for ( InventoryOrderDetail inventoryOrderDetail: inventoryOrderDetails) {
                    Integer status = inventoryOrderDetail.getStatus();
                    if(status == 0){
                        inventoryOrderDetail.setLastModifiedDate(null);
                        inventoryOrderDetail.setLastModifiedBy(null);
                    }
                }
            }
            inventoryOrderDetailList.addAll(inventoryOrderDetails);

        }
        boolean isAssetInfoStatus = true;
        for (SearchFilter searchFilter :searchFilters) {
            if("status".equals(searchFilter.fieldName)){
                isAssetInfoStatus =false;
                if("4".equals(searchFilter.value)){
                    isAssetInfoStatus=true;
                }
            }
        }

        // 获取无工单的设备
        if(!isSolvedBoolean && !lastModifiedByBoolean && !lastModifiedDateBoolean && isAssetInfoStatus){
            List<SearchFilter> searchFiltersListNew = new ArrayList();
            List<SearchFilter> InventoryOrderDetailSearchFilters = new ArrayList<>();
            InventoryOrderDetailSearchFilters.addAll(searchFilterList);
            List<InventoryOrderDetail> List = dao.findBySearchFilter(InventoryOrderDetailSearchFilters);
            List<Integer> idList = new ArrayList<>();
            if(!List.isEmpty()) {
                for (InventoryOrderDetail inventoryOrderDetail : List) {
                    idList.add(inventoryOrderDetail.getAssetInfo().getId());
                }
                if(!idList.isEmpty()){
                    searchFiltersListNew.add(new SearchFilter("id", SearchFilter.Operator.NotIN, idList,"1*"));
                }
            }
            this.addSearchFiltersListNew(searchFilters,searchFiltersListNew);
            searchFiltersListNew.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFiltersListNew.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFiltersListNew.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFiltersListNew.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFiltersListNew.add(new SearchFilter("assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFiltersListNew.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFiltersListNew.add(new SearchFilter("isChecked", SearchFilter.Operator.EQ, true,"1*"));
            searchFiltersListNew.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false,"1*"));
            searchFiltersListNew.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true,"1*"));
            List<AssetInfo> assetInfoList = assetInfoRepository.findBySearchFilter(searchFiltersListNew);
            if (!assetInfoList.isEmpty()){
                inventoryOrderDetailList = new ArrayList(inventoryOrderDetailList);
                for (AssetInfo assetInfo:assetInfoList) {
                    InventoryOrderDetail inventoryOrderDetail = new InventoryOrderDetail();
                    inventoryOrderDetail.setAssetInfo(assetInfo);
                    inventoryOrderDetail.setStatus(4);//无工单
                    inventoryOrderDetailList.add(inventoryOrderDetail);
                }
            }
        }
        if (!Strings.isNullOrEmpty(queryAssetUid) && inventoryOrderDetailList.size()>0) {
            List<InventoryOrderDetail> inventoryOrderDetails = new ArrayList<>();
            for(InventoryOrderDetail inventoryOrderDetail :inventoryOrderDetailList){
                if(queryAssetUid.equals(inventoryOrderDetail.getAssetInfo().getUid())){
                    inventoryOrderDetails.add(inventoryOrderDetail);
                }
            }
            Page<InventoryOrderDetail>  inventoryOrderDetailPage = pageUtil.pageNum(inventoryOrderDetails, pageRequest);
            return inventoryOrderDetailPage;
        }
        Page<InventoryOrderDetail>  inventoryOrderDetailPage = pageUtil.pageNum(inventoryOrderDetailList, pageRequest);
		return inventoryOrderDetailPage;
	}*/
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

	private void addSearchFiltersListNew(List<SearchFilter> searchFilters,List<SearchFilter> searchFiltersListNew){
        for (SearchFilter searchFilter :searchFilters) {
            if("assetInfo.name".equals(searchFilter.fieldName)){
                searchFiltersListNew.add(new SearchFilter("name", SearchFilter.Operator.LIKE, searchFilter.value,"1*"));
            }
            if("assetInfo.clinicalDeptName".equals(searchFilter.fieldName)){
                searchFiltersListNew.add(new SearchFilter("clinicalDeptName", SearchFilter.Operator.EQ, searchFilter.value,"1*"));
            }
            if("assetInfo.qrCode".equals(searchFilter.fieldName)){
                searchFiltersListNew.add(new SearchFilter("qrCode", SearchFilter.Operator.LIKE, searchFilter.value,"1*"));
            }
            if("assetInfo.departNum".equals(searchFilter.fieldName)){
                searchFiltersListNew.add(new SearchFilter("departNum", SearchFilter.Operator.LIKE, searchFilter.value,"1*"));
            }
            if("assetInfo.functionType".equals(searchFilter.fieldName)){
                searchFiltersListNew.add(new SearchFilter("functionType", SearchFilter.Operator.LIKE, searchFilter.value,"1*"));
            }
            if("assetInfo.financingNum".equals(searchFilter.fieldName)){
                searchFiltersListNew.add(new SearchFilter("financingNum", SearchFilter.Operator.LIKE, searchFilter.value,"1*"));
            }
            if("assetInfo.status".equals(searchFilter.fieldName)){
                searchFiltersListNew.add(new SearchFilter("status", SearchFilter.Operator.EQ, searchFilter.value,"1*"));
            }
            if("assetInfo.locationName".equals(searchFilter.fieldName)){
                searchFiltersListNew.add(new SearchFilter("locationName", SearchFilter.Operator.LIKE, searchFilter.value,"1*"));
            }
        }
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
