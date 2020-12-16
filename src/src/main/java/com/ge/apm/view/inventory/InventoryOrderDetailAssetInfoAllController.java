package com.ge.apm.view.inventory;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.InventoryIssueRepository;
import com.ge.apm.dao.InventoryOrderDetailAssetInfoRepository;
import com.ge.apm.dao.InventoryOrderRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.InventoryIssue;
import com.ge.apm.domain.InventoryOrderDetail;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.inventory.InventoryOrderService;
import com.ge.apm.view.sysutil.UserContextService;
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
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@ManagedBean
@ViewScoped
public class InventoryOrderDetailAssetInfoAllController extends GenericCRUDUUIDController<InventoryOrderDetail> implements Serializable, ServerEventInterface {

	private UserAccount currentUser;
	private InventoryOrderDetailAssetInfoRepository dao;
    private InventoryIssueRepository inventoryIssueRepository;
	private InventoryOrderRepository inventoryOrderRepository;
	private AssetInfoRepository assetInfoRepository;
	private InventoryOrderService service;
	private String orderId;
	private List<String> listClinicalDeptName;

	@Override
	protected void init() {
		dao = WebUtil.getBean(InventoryOrderDetailAssetInfoRepository.class);
        inventoryIssueRepository = WebUtil.getBean(InventoryIssueRepository.class);
		inventoryOrderRepository = WebUtil.getBean(InventoryOrderRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
		service=WebUtil.getBean(InventoryOrderService.class);
		currentUser = UserContextService.getCurrentUserAccount();
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		orderId = request.getParameter("orderId");
        if (null == orderId){
            listClinicalDeptName=service.getInventoryClinicalDeptUIDAll(true,0);
        }else {
            listClinicalDeptName=service.getInventoryClinicalDeptUID(orderId,true,0);
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
        List<SearchFilter> searchFilterList = new ArrayList<>();
        searchFilterList.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID(), "1*2+"));
        searchFilterList.add(new SearchFilter("assetInfo.isChecked", SearchFilter.Operator.EQ, true,"1*"));
        searchFilterList.add(new SearchFilter("assetInfo.isDeleted", SearchFilter.Operator.EQ, false,"1*"));
        searchFilterList.add(new SearchFilter("assetInfo.isValid", SearchFilter.Operator.EQ, true,"1*"));

        //before
		List<SearchFilter> searchFilters = this.searchFilters;
		if (searchFilters == null) {
			searchFilters = new ArrayList<SearchFilter>();
		}
        searchFilters.addAll(searchFilterList);

        List<InventoryOrderDetail> inventoryOrderDetailList = new ArrayList<>();
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
        //已盘点/未盘点/无台账 工单
        List<SearchFilter> searchFiltersInventoryOrderDetailList = new ArrayList(searchFilters);
        boolean isSolvedBoolean = searchFiltersInventoryOrderDetailList.removeIf(e -> e.fieldName.equals("isSolved"));
        if(!isSolvedBoolean){
            if(!AssetInfoIdList.isEmpty()){
                searchFiltersInventoryOrderDetailList.add(new SearchFilter("assetInfo.id", SearchFilter.Operator.NotIN, AssetInfoIdList,"1*"));
            }
            List<InventoryOrderDetail> inventoryOrderDetails = dao.findBySearchFilter(searchFiltersInventoryOrderDetailList);
            if(!inventoryOrderDetails.isEmpty()){
                for ( InventoryOrderDetail inventoryOrderDetail: inventoryOrderDetails) {
                    Integer status = inventoryOrderDetail.getStatus();
                    if(status == 2 || status == 0){
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
            searchFiltersListNew.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID(), "1*2+"));
            this.addSearchFiltersListNew(searchFilters,searchFiltersListNew);
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
                searchFiltersListNew.add(new SearchFilter("isChecked", SearchFilter.Operator.EQ, true,"1*"));
                searchFiltersListNew.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false,"1*"));
                searchFiltersListNew.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true,"1*"));
            }
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

        Page<InventoryOrderDetail>  inventoryOrderDetailPage = new PageImpl<InventoryOrderDetail>(inventoryOrderDetailList, pageRequest, inventoryOrderDetailList.size());
        //after
		return inventoryOrderDetailPage;
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

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

}
