package com.ge.apm.view.inventory;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.dto.OrgDto;
import com.ge.apm.service.inventory.InventoryOrderApiService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.ge.apm.dao.InventoryOrderRepository;
import com.ge.apm.domain.InventoryOrder;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.inventory.InventoryOrderService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

/**
 * Created by 503035627
 */
@ManagedBean
@ViewScoped
public class InventoryOrderController extends GenericCRUDUUIDController<InventoryOrder> implements Serializable{

	private UserAccount currentUser;
	private InventoryOrderRepository dao;
	private List<String> listAccessClinical;
    private UaaService uaaService;
    private InventoryOrderService service;
    private InventoryOrderApiService apiService;
    @Getter @Setter
    List<OrgDto> orgsForSelected = new ArrayList<>(); //待选科室
    @Setter @Getter
    List<String> selectedOrgUid; //勾选盘点科室
    @Setter @Getter
    private InventoryOrder inventoryOrder;
    @Getter @Setter
    private Boolean filterNoneAsset = true;
	@Override
    protected void init() {
        dao = WebUtil.getBean(InventoryOrderRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        uaaService=WebUtil.getBean(UaaService.class);
        listAccessClinical=uaaService.findAccessClinicalUidByUserUid(currentUser.getUid());
        apiService = WebUtil.getBean(InventoryOrderApiService.class);
        service=WebUtil.getBean(InventoryOrderService.class);
        service.init();
    }

    @Override
    protected Page<InventoryOrder> loadData(PageRequest pageRequest) {
    	List<SearchFilter> searchFilters = this.searchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        Page<InventoryOrder> pageRe=service.getPageData(pageRequest, currentUser, searchFilters,listAccessClinical);
        return pageRe;
    }

    //创建盘点工单 保存
    public String saveCreate(){
        //时间校验
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String s = sdf.format(TimeUtil.now());
        Date now = new Date();
        try {
            now =  sdf.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (selected.getPlanStartTime().before(now)) {
            WebUtil.addErrorMessage("计划开始时间不得早于当前日期");
            return "";
        }
        if (selected.getPlanStartTime().after(selected.getPlanEndTime())) {
            WebUtil.addErrorMessage("计划结束时间不得早于开始时间");
            return "";
        }
        if (ObjectUtil.isEmpty(selectedOrgUid)) {
            WebUtil.addErrorMessage("盘点科室不能为空");
            return "";
        }
	    try{
            List<OrgDto> selectedOrgs = ListUtil.newArrayList();
            for (OrgDto orgDto : orgsForSelected) {
                for (String string : selectedOrgUid) {
                    if (string.contains(orgDto.getUid()) && string.contains("#assetType=" + orgDto.getAssetType())) {
                        selectedOrgs.add(orgDto);
                    }
                }
            }
            apiService.doCreate(selected, selectedOrgs);
            selectedOrgUid.clear();
        }catch(Exception e){
            WebUtil.addErrorMessage("盘点工单新建失败");
            return "";
        }
        WebUtil.addSuccessMessage("盘点工单新建成功");
        return "inventoryOrder?faces-redirect=true";
    }

    public String finishOrder() {
        //时间校验
        if(inventoryOrder.getStartTime().after(inventoryOrder.getEndTime())){
            WebUtil.addErrorMessage("实际结束时间不得早于开始时间");
            return "";
        }
	    try{
            apiService.doFinishOrder(inventoryOrder);
        }catch(Exception e){
            WebUtil.addErrorMessage("盘点工单关闭失败");
            return "";
        }
        WebUtil.addSuccessMessage("盘点工单关闭成功");
        return "inventoryOrder?faces-redirect=true";
    }

    public void onCreate() throws IllegalAccessException, InstantiationException {
        super.prepareCreate();
        orgsForSelected = apiService.onCreateInventory();
        if (filterNoneAsset) {
            List<OrgInfo> orgInfos = uaaService.filterNoneAsset(orgsForSelected.stream().map(OrgDto::getUid).collect(Collectors.toList()));
            List<Integer> orgInfoIds = orgInfos.stream().map(OrgInfo::getId).collect(Collectors.toList());
            orgsForSelected = orgsForSelected.stream().filter(t -> orgInfoIds.contains(t.getId())).collect(Collectors.toList());
        }
    }

    public void onFinishOrder(String id){
        inventoryOrder = new InventoryOrder();
        inventoryOrder.setId(id);
    }

    public String getNameWithParent(OrgInfo org){
	    return apiService.getNameWithParent(org);
    }

    @Override
    public void onBeforeNewObject(InventoryOrder object) {
        super.onBeforeNewObject(object);
        object.setTenantUID(currentUser.getTenantUID());
        object.setInstitutionUID(currentUser.getInstitutionUID());
        object.setHospitalUID(currentUser.getHospitalUID());
        object.setSiteUID(currentUser.getSiteUID());
    }

    @Override
    protected GenericRepositoryUUID<InventoryOrder> getDAO() {
        return dao;
    }
	
}
