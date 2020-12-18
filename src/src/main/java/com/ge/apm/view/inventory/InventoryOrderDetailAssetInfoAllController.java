package com.ge.apm.view.inventory;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.InventoryIssueRepository;
import com.ge.apm.dao.InventoryOrderDetailAssetInfoRepository;
import com.ge.apm.dao.InventoryOrderRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.inventory.InventoryOrderService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
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
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class InventoryOrderDetailAssetInfoAllController extends GenericCRUDUUIDController<InventoryOrderDetail> implements Serializable, ServerEventInterface {

	private UserAccount currentUser;
	private InventoryOrderDetailAssetInfoRepository dao;
    private InventoryIssueRepository inventoryIssueRepository;
	private InventoryOrderRepository inventoryOrderRepository;
    private InventoryOrderService inventoryOrderService;
	private AssetInfoRepository assetInfoRepository;
    private UaaService uaaService;
	private InventoryOrderService service;
	private List<String> listClinicalDeptName;
    private List<String> listAccessClinical;
	@Override
	protected void init() {
		dao = WebUtil.getBean(InventoryOrderDetailAssetInfoRepository.class);
        inventoryIssueRepository = WebUtil.getBean(InventoryIssueRepository.class);
		inventoryOrderRepository = WebUtil.getBean(InventoryOrderRepository.class);
        inventoryOrderService = WebUtil.getBean(InventoryOrderService.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
		service=WebUtil.getBean(InventoryOrderService.class);
		currentUser = UserContextService.getCurrentUserAccount();
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        listClinicalDeptName= uaaService.getAllAccessibleClinicalDeptStringList();
        listAccessClinical=uaaService.findAccessClinicalUidByUserUid(currentUser.getUid());
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
        boolean isSolvedBoolean = searchFilters.removeIf(e -> e.fieldName.equals("isSolved"));
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
        searchFilters.addAll(searchFilterList);

        //获取有问题的工单
        List<SearchFilter> searchFiltersListIssue = new ArrayList(searchFilters);
        boolean lastModifiedByBoolean = searchFiltersListIssue.removeIf(e -> e.fieldName.equals("lastModifiedBy"));
        boolean lastModifiedDateBoolean = searchFiltersListIssue.removeIf(e -> e.fieldName.equals("lastModifiedDate"));
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
            List<InventoryOrderDetail> inventoryOrderDetails = dao.findBySearchFilter(searchFiltersInventoryOrderDetailList);
            if(!inventoryOrderDetails.isEmpty()){
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
        int size = inventoryOrderDetailList.size();
        inventoryOrderDetailList= this.pageNum(inventoryOrderDetailList, pageRequest);
        Page<InventoryOrderDetail>  inventoryOrderDetailPage = new PageImpl<InventoryOrderDetail>(inventoryOrderDetailList, pageRequest, size);
        //after
		return inventoryOrderDetailPage;
	}

	private List<InventoryOrderDetail> pageNum(List<InventoryOrderDetail> inventoryOrderDetailList,PageRequest pageRequest){
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize() - 1;
        int numStart = pageNumber * pageSize;
        numStart = numStart + pageNumber;
        int numEnd = numStart + pageSize;
        int size = inventoryOrderDetailList.size();
        if(size > numEnd){
            inventoryOrderDetailList = inventoryOrderDetailList.subList(numStart ,numEnd);
        }else {
            inventoryOrderDetailList = inventoryOrderDetailList.subList(numStart ,size);
        }
        return inventoryOrderDetailList;
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
