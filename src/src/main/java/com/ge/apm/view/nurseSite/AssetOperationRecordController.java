package com.ge.apm.view.nurseSite;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.mapper.AssetOperationRecordMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.nurseSite.AssetOperationRecordService;
import com.ge.apm.view.SearchController;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.SelectEvent;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.MyCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.HashSet;
import java.util.List;

/**
 * @author Liu
 */
@ManagedBean
@ViewScoped
public class AssetOperationRecordController extends MyCRUDController<AssetOperationRecord> {

    private UserAccount currentUser;
    private AssetOperationRecordMapper dao;
    private String assetGroupFilter;
    private OrgInfo selectOrgInfo;
    private List<String> setUserUuids;
    private UserAuth customRoleAuth;
    private boolean returnFlag = false;

    @Override
    protected void init() {
        currentUser = UserContextService.getCurrentUserAccount();
        dao = WebUtil.getBean(AssetOperationRecordMapper.class);
        SearchController searchController = WebUtil.getBean(SearchController.class);
        searchController.setCallingController(this);
        searchController.setUpdateViewIDs(":optRecordListForm:datalist");
        searchController.setBlnOrgTreeUpdate(true);
        searchController.setType(5);
        searchController.updateData();
        AssetOperationRecordService service = WebUtil.getBean(AssetOperationRecordService.class);
        setUserUuids = service.findAccessClinicalUidListByUserUid(currentUser.getUid());
        this.selectOrgInfo = service.fetchOrgInfoByUid(currentUser.getSiteUID());
        customRoleAuth = UserContextService.getCustomRoleAuth(currentUser.getId(), CustomRoleAuthConstant.NURSE_SITE_OPTRECORD);
    }

    @Override
    protected List<AssetOperationRecord> loadData(Pagination pageRequest, String sortField, boolean isAscending) {
        if (this.searchFilters == null) {
            this.searchFilters = new HashSet<>();
        }
        // need list or set
        searchFilters.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, setUserUuids, "1*"));
        if (selectOrgInfo.getOrgType() == 0) {
            searchFilters.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.EQ, selectOrgInfo.getUid(), "1*2+"));
        } else if (selectOrgInfo.getOrgType() == 1) {
            searchFilters.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.EQ, selectOrgInfo.getUid(), "1*2+"));
        } else if (selectOrgInfo.getOrgType() == 2) {
            searchFilters.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.EQ, selectOrgInfo.getUid(), "1*2+"));
        } else if (selectOrgInfo.getOrgType() == 3) {
            searchFilters.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.EQ, selectOrgInfo.getUid(), "1*2+"));
        }
        boolean assetFlag = !this.hasFilterOnField("assetInfoUid");
        if (returnFlag == assetFlag) {
            pageRequest = new Pagination(1, pageRequest.getSize());
        }
        returnFlag = !assetFlag;
        this.lazyModel.setRowCount(dao.getAllCount(searchFilters, assetFlag));
        return dao.getAll(searchFilters, "createDate", isAscending, pageRequest.getLimit(), pageRequest.getOffset(), assetFlag);
    }

    public void onRowSelect(SelectEvent event) {
        AssetOperationRecord operationRecord = (AssetOperationRecord) event.getObject();
        if (this.searchFilters == null) {
            this.searchFilters = new HashSet<>();
        }
        // need list or set
        searchFilters.add(new SearchFilter("assetInfoUid", SearchFilter.Operator.EQ, operationRecord.getAssetInfoUid(), "1*"));
    }

    public void returnPreviousLevel() {
        this.removeFilterOnField("assetInfoUid");
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        try {
            OrgInfo orgInfo = (OrgInfo) eventObject;
            if (orgInfo != null) {
                selectOrgInfo = orgInfo;
                searchFilters.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBeforeNewObject(AssetOperationRecord object) {
        super.onBeforeNewObject(object);
        object.setTenantUID(currentUser.getTenantUID());
        object.setInstitutionUID(currentUser.getInstitutionUID());
        object.setHospitalUID(currentUser.getHospitalUID());
        object.setSiteUID(currentUser.getSiteUID());
    }

    @Override
    public void save() {
        this.selected = null;
        WebUtil.addSuccessMessage("保存成功");
    }

    @Override
    public void update() {
        save();
    }

    @Override
    public void delete() {
        this.selected = null;
    }

    @Override
    protected String getActionMessage(String actionName) {
        return "开关机记录" + " " + WebUtil.getMessage(actionName);
    }

    @Override
    protected BaseMapper<AssetOperationRecord> getDAO() {
        return dao;
    }

    public String getAssetGroupFilter() {
        return assetGroupFilter;
    }

    public void setAssetGroupFilter(String assetGroupFilter) {
        this.assetGroupFilter = assetGroupFilter;
    }

    public OrgInfo getSelectOrgInfo() {
        return selectOrgInfo;
    }

    public void setSelectOrgInfo(OrgInfo selectOrgInfo) {
        this.selectOrgInfo = selectOrgInfo;
    }

    public UserAuth getCustomRoleAuth() {
        return customRoleAuth;
    }

    public void setCustomRoleAuth(UserAuth customRoleAuth) {
        this.customRoleAuth = customRoleAuth;
    }

    public boolean getReturnFlag() {
        return returnFlag;
    }

    public void setReturnFlag(boolean returnFlag) {
        this.returnFlag = returnFlag;
    }
}
