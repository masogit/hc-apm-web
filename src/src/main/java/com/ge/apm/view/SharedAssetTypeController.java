/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SharedAssetTypeRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.asset.AssetInfoController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author caoyong
 */
@ManagedBean
@ViewScoped
public class SharedAssetTypeController extends GenericCRUDUUIDController<SharedAssetType> {

    private UaaService uuaService;
    SharedAssetTypeRepository dao = null;
    AssetInfoRepository assetInfoDao = null;
    OrgInfoRepository orgInfoDao = null;
    private String operation;
    private String createDateFormatTime = null;

    private String filterUnitPrice;
    private Double filterUnitPriceFrom;
    private Double filterUnitPriceTo;

    @Override
    protected void init() {
        uuaService = WebUtil.getBean(UaaService.class);
        assetInfoDao = WebUtil.getBean(AssetInfoRepository.class);
        dao = WebUtil.getBean(SharedAssetTypeRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String encodeStr = request.getParameter("str");
        String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "actionName");
        if ("Create".equalsIgnoreCase(actionName)) {
            try {
                prepareCreate();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(AssetInfoController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if ("View".equalsIgnoreCase(actionName)) {
            prepareView();
        } else if ("Edit".equalsIgnoreCase(actionName)) {

            setSelected(dao.findById((String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid")));
            prepareEdit();
        } else if ("Delete".equalsIgnoreCase(actionName)) {
            prepareDelete();
        }
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
    }

    @Override
    protected SharedAssetTypeRepository getDAO() {
        return dao;
    }

    public String getViewPage(String id, String actionName) {
        operation = "Edit.xhtml?actionName=Edit&selectedid=" + "1";
        return UrlEncryptController.encodeUrlParam(operation);
    }

    public void getViewPageA(String pageName, String actionName) {
        operation = pageName + "?actionName=" + actionName + "&selectedid=" + selected.getId();
    }

    public String getViewPageN(String pageName, String actionName) {
        operation = pageName + "?actionName=" + actionName + "&selectedid=" + selected.getId();
        return UrlEncryptController.encodeUrlParam(operation);
    }

    public void onSelectSwapType() throws IOException {
        String url = "actionName=Edit&selectedid=" + selected.getId();
        url = "Edit.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    @Override
    protected Page<SharedAssetType> loadData(PageRequest pageRequest) {
        setUserSearchFilters();

        this.removeFilterOnField("unitPrice");

        if (StringUtils.isNotEmpty(this.getFilterUnitPrice())) {
            String[] split = this.getFilterUnitPrice().split("~");
            searchFilters.add(new SearchFilter("unitPrice", SearchFilter.Operator.GTE, split[0]));
            if (split.length == 2) {
                if (!split[1].equals(""))
                    searchFilters.add(new SearchFilter("unitPrice", SearchFilter.Operator.LTE, split[1]));
            }
        }

        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public SharedAssetType getAccessoriesById(String uid) {
        return dao.findById(uid);
    }

    @Override
    public List<SharedAssetType> getItemList() {
        return dao.find();
    }

    @Override
    public void onBeforeSave(SharedAssetType object) {
        object.setAccessoryList(object.getAccessoryList());
    }

    @Override
    public void onBeforeNewObject(SharedAssetType object) {
        object.setId(UUID.randomUUID().toString().replace("-", ""));
        object.setTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());
        object.setHospitalUID(UserContextService.getCurrentUserAccount().getHospitalUID());
        object.setInstitutionUID(UserContextService.getCurrentUserAccount().getInstitutionUID());
        object.setSiteUID(UserContextService.getCurrentUserAccount().getSiteUID());
        object.setCreateBy(UserContextService.getCurrentUserAccount().getName());
        object.setCreateDate(new Date());
    }

    @Override
    public void delete() {
        if (dao.findAssetIsExist(this.selected.getId()).isEmpty()) {
            dao.delete(selected);
            WebUtil.addSuccessMessage("已成功删除: " + selected.getSwapName());
            selected = null;
        } else {
            WebUtil.addErrorMessage("此调剂设备已在使用，请核实！");
        }
    }

    //根据登录人信息配置权限
    private void setUserSearchFilters() {
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<String> orgUidList = uuaService.getOrgListByUser(user.getUid()).stream().map(OrgInfo::getUid).collect(Collectors.toList());
        searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
    }

    public List<SharedAssetType> getSwapNameList() {
        List<SharedAssetType> sharedAssetTypes = dao.findbySiteUid(UserContextService.getCurrentUserAccount().getSiteUID());
        return sharedAssetTypes == null ? new ArrayList<>() : sharedAssetTypes;
    }

    public String getSharedAssetType(String id) {
        if (id != null) {
            SharedAssetType object = dao.findById(id);
            if (object != null) {
                return object.getSwapName();
            } else {
                return null;
            }
        }
        return null;
    }

    public List<SharedAssetType> getSwapNameReturnList() {
        String orgUid = UserContextService.getCurrentUserAccount().getOrgInfoUID();
        OrgInfo userOrg = orgInfoDao.getByUid(orgUid);

        List<SharedAssetType> objectList;
        if (userOrg.getOrgType() == 4) {
            objectList = dao.findbyClinicalDeptUid(UserContextService.getCurrentUserAccount().getSiteUID(), userOrg.getUid());
        } else {
            objectList = dao.findbySiteUid(UserContextService.getCurrentUserAccount().getSiteUID());
        }
        return objectList == null ? new ArrayList<>() : objectList;
    }

    public int getSwapQty(String Uid, String clinicalDeptUid) {
        if (null == Uid || Uid.equalsIgnoreCase("")) {
            return 0;
        } else {
            List<AssetInfo> objectList = dao.findbyBorrowQty(clinicalDeptUid, Uid);
            return objectList.size();
        }
    }


    public void onChanged() {
        SharedAssetTypeController swapTypeController = WebUtil.getBean(SharedAssetTypeController.class);
        if (swapTypeController.getSelected() != null) {

        }

    }

    private SharedAssetTypeAccesssory selectedAccessory;

    public SharedAssetTypeAccesssory getSelectedAccessory() {
        return selectedAccessory;
    }

    public void setSelectedAccessory(SharedAssetTypeAccesssory selectedAccessory) {
        this.selectedAccessory = selectedAccessory;
    }

    public void saveAccessory() {
        List<SharedAssetTypeAccesssory> accessoryList = selected.getAccessoryList();
        if (accessoryList.size() > 0) {
            for (int i = 0; i < accessoryList.size(); i++) {
                SharedAssetTypeAccesssory sata = accessoryList.get(i);
                if (sata.getName().equals(selectedAccessory.getName())) {
                    accessoryList.remove(sata);
                    i--;
                }
            }
        }
        selected.getAccessoryList().add(selectedAccessory);
    }

    public void prepareCreateAccessory() {
        crudActionName = "Create";
        selectedAccessory = new SharedAssetTypeAccesssory();
    }

    public void prepareEditAccessory() {
        crudActionName = "Edit";
    }

    public void deleteAccessory() {
        if (selectedAccessory != null)
            selected.getAccessoryList().remove(selectedAccessory);
    }

    public String getCreateDateFormatTime() {
        return createDateFormatTime;
    }

    public void setCreateDateFormatTime(String createDateFormatTime) {
        this.createDateFormatTime = createDateFormatTime;
    }

    public String getFilterUnitPrice() {
        double from = filterUnitPriceFrom != null ? filterUnitPriceFrom : 0;
        String s = (filterUnitPriceTo != null && filterUnitPriceTo != 0) ? filterUnitPriceTo + "" : "";
        if (filterUnitPriceFrom == null && filterUnitPriceTo == null)
            return null;
        return from + "~" + s;
    }

    /**
     * @return true if chargingType is day or time.
     */
    public boolean disableStartingFields() {
        String chargingType = selected.getChargingType();
        return StringUtils.equals("1", chargingType) || StringUtils.equals("3", chargingType);
    }

    /**
     * Reset value in starting fields, if charging type is day or time
     */
    public void onChargingTypeChange() {
        if (disableStartingFields()) {
            selected.setStartingPrice(null);
            selected.setStartingDuration(null);
        }
    }

    public void setFilterUnitPrice(String filterUnitPrice) {
        this.filterUnitPrice = filterUnitPrice;
    }

    public Double getFilterUnitPriceFrom() {
        return filterUnitPriceFrom;
    }

    public void setFilterUnitPriceFrom(Double filterUnitPriceFrom) {
        this.filterUnitPriceFrom = filterUnitPriceFrom;
    }

    public Double getFilterUnitPriceTo() {
        return filterUnitPriceTo;
    }

    public void setFilterUnitPriceTo(Double filterUnitPriceTo) {
        this.filterUnitPriceTo = filterUnitPriceTo;
    }
}
