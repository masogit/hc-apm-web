package com.ge.apm.view.inventory;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.InventoryOrderDetailAssetInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.InventoryOrderDetail;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.inventory.InventoryOrderService;
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

/**
 * Created by 503035627
 */
@ManagedBean
@ViewScoped
public class InventoryOrderDetailAssetInfoNoWorkOrderController extends JpaCRUDController<AssetInfo> {

	private UserAccount currentUser;
	private InventoryOrderDetailAssetInfoRepository dao;
    private AssetInfoRepository assetInfoRepository;
	private InventoryOrderService service;
	private static final String ORG_NAME = "orgName";
	private String orderId;
	private List<String> listClinicalDeptName;

	@Override
	protected void init() {
		dao = WebUtil.getBean(InventoryOrderDetailAssetInfoRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
		service=WebUtil.getBean(InventoryOrderService.class);
		currentUser = UserContextService.getCurrentUserAccount();
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		orderId = request.getParameter("orderId");
        if (null == orderId){
            listClinicalDeptName=service.getInventoryClinicalDeptUIDAll(true,0);
        }else {
            listClinicalDeptName=service.getInventoryClinicalDeptUID(orderId,true,0);
        }	}


    @Override
    protected GenericRepository<AssetInfo> getDAO() {
        return assetInfoRepository;
    }

    @Override
	protected Page<AssetInfo> loadData(PageRequest pageRequest) {
		//before
		List<SearchFilter> searchFilters = this.searchFilters;
		if (searchFilters == null) {
			searchFilters = new ArrayList<SearchFilter>();
		}
		searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID(), "1*2+"));
        List<SearchFilter> InventoryOrderDetailSearchFilters = new ArrayList<>();
        if(null != orderId){
            InventoryOrderDetailSearchFilters.add(new SearchFilter("inventoryOrderId", SearchFilter.Operator.EQ, orderId,"1*"));
        }
        InventoryOrderDetailSearchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID(), "1*2+"));
        InventoryOrderDetailSearchFilters.add(new SearchFilter("assetInfo.isChecked", SearchFilter.Operator.EQ, true,"1*"));
        List<InventoryOrderDetail> inventoryOrderDetailList = dao.findBySearchFilter(InventoryOrderDetailSearchFilters);
        List<Integer> idList = new ArrayList<>();
        if(!inventoryOrderDetailList.isEmpty()){
            for ( InventoryOrderDetail inventoryOrderDetail :inventoryOrderDetailList) {
                idList.add(inventoryOrderDetail.getAssetInfo().getId());
            }
            searchFilters.add(new SearchFilter("id", SearchFilter.Operator.NotIN, idList,"1*"));
            searchFilters.add(new SearchFilter("isChecked", SearchFilter.Operator.EQ, true,"1*"));
        }
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

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

}
