package com.ge.apm.view.inventory;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.InventoryOrderDetailAssetInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.InventoryOrderDetail;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.inventory.InventoryOrderService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 503035627
 */
@ManagedBean
@ViewScoped
public class InventoryOrderDetailAssetInfoNoWorkOrderController extends JpaCRUDController<AssetInfo> {

	private UserAccount currentUser;
	private InventoryOrderDetailAssetInfoRepository dao;
    private UaaService uaaService;
    private AssetInfoRepository assetInfoRepository;
    private InventoryOrderService inventoryOrderService;
	private InventoryOrderService service;
	private static final String ORG_NAME = "orgName";
	private List<String> listClinicalDeptName;
    private List<String> listAccessClinical;

	@Override
	protected void init() {
		dao = WebUtil.getBean(InventoryOrderDetailAssetInfoRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        inventoryOrderService = WebUtil.getBean(InventoryOrderService.class);
		service=WebUtil.getBean(InventoryOrderService.class);
        uaaService = WebUtil.getBean(UaaService.class);
		currentUser = UserContextService.getCurrentUserAccount();
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        listClinicalDeptName= uaaService.getAllAccessibleClinicalDeptStringList();
        listAccessClinical=uaaService.findAccessClinicalUidByUserUid(currentUser.getUid());
	}


    @Override
    protected GenericRepository<AssetInfo> getDAO() {
        return assetInfoRepository;
    }

    @Override
	protected Page<AssetInfo> loadData(PageRequest pageRequest) {
		//before
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());

		List<SearchFilter> searchFilters = this.searchFilters;
		if (searchFilters == null) {
			searchFilters = new ArrayList<SearchFilter>();
		}
        List<SearchFilter> InventoryOrderDetailSearchFilters = new ArrayList<>();
        InventoryOrderDetailSearchFilters.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        InventoryOrderDetailSearchFilters.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        InventoryOrderDetailSearchFilters.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        InventoryOrderDetailSearchFilters.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        InventoryOrderDetailSearchFilters.add(new SearchFilter("assetInfo.assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        InventoryOrderDetailSearchFilters.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        InventoryOrderDetailSearchFilters.add(new SearchFilter("assetInfo.isChecked", SearchFilter.Operator.EQ, true,"1*"));
        InventoryOrderDetailSearchFilters.add(new SearchFilter("assetInfo.isDeleted", SearchFilter.Operator.EQ, false,"1*"));
        InventoryOrderDetailSearchFilters.add(new SearchFilter("assetInfo.isValid", SearchFilter.Operator.EQ, true,"1*"));
        //全院视图
        List<String> orderIdList = inventoryOrderService.getInventoryOrderData(currentUser.getTenantUID(), listAccessClinical,false);
        if(!orderIdList.isEmpty()){
            InventoryOrderDetailSearchFilters.add(new SearchFilter("inventoryOrderId", SearchFilter.Operator.IN, orderIdList, "1*2+"));
        }
        List<InventoryOrderDetail> inventoryOrderDetailList = dao.findBySearchFilter(InventoryOrderDetailSearchFilters);
        List<Integer> idList = new ArrayList<>();
        if(!inventoryOrderDetailList.isEmpty()){
            for ( InventoryOrderDetail inventoryOrderDetail :inventoryOrderDetailList) {
                idList.add(inventoryOrderDetail.getAssetInfo().getId());
            }
            searchFilters.add(new SearchFilter("id", SearchFilter.Operator.NotIN, idList,"1*"));
        }
        searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false,"1*"));
        searchFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false,"1*"));
        searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true,"1*"));
        Page<AssetInfo> assetInfoList = assetInfoRepository.findBySearchFilter(searchFilters,pageRequest);
		//after
		return assetInfoList;
	}

	@Override
	public void onServerEvent(String eventName, Object eventObject) {
	}

	@Override
	public void onBeforeNewObject(AssetInfo object) {
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
