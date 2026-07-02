package com.ge.apm.view.nurseSite;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.mapper.AssetOperationRecordMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.nurseSite.AssetOperationRecordService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.SearchController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.joda.time.DateTime;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.MyCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private UaaService uaaService;
    private OrgInfoRepository orgInfoDao;
    private UserAuth customRoleAuth;
    private boolean returnFlag = false;
    private boolean operationFlag = false;
    private String assetInfoUid;
    private List<String> orgUidList;

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
        uaaService = WebUtil.getBean(UaaService.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        orgUidList = uaaService.getOrgListByUser(currentUser.getUid()).stream().map(OrgInfo::getUid).collect(Collectors.toList());
        this.selectOrgInfo = service.fetchOrgInfoByUid(currentUser.getSiteUID());
        customRoleAuth = UserContextService.getCustomRoleAuth(currentUser.getId(), CustomRoleAuthConstant.NURSE_SITE_OPTRECORD);
        String encodeStr = WebUtil.getRequestParameter("str");
        assetInfoUid = (String) UrlEncryptController.getValueFromMap(encodeStr, "assetInfoUid");
        if (assetInfoUid != null) {
            operationFlag = true;
            if (this.searchFilters == null) {
                this.searchFilters = new HashSet<>();
            }
            searchFilters.add(new SearchFilter("assetInfoUid", SearchFilter.Operator.EQ, assetInfoUid, "1*"));
        }
    }

    @Override
    protected List<AssetOperationRecord> loadData(Pagination pageRequest, String sortField, boolean isAscending) {
        if (currentUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(currentUser.getAssetTypeGroupList())){
            return ListUtil.empty();
        }
        if (this.searchFilters == null) {
            this.searchFilters = new HashSet<>();
        }
        searchFilters.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID(), "1*"));
        if (selectOrgInfo.getOrgType() == 1) {
            searchFilters.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.EQ, selectOrgInfo.getUid(), "1*"));
        } else if (selectOrgInfo.getOrgType() == 2) {
            searchFilters.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.EQ, selectOrgInfo.getUid(), "1*"));
        } else if (selectOrgInfo.getOrgType() == 3) {
            searchFilters.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.EQ, selectOrgInfo.getUid(), "1*"));
        }
        searchFilters.add(new SearchFilter("assetInfo.orgUidList", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        boolean assetFlag = !this.hasFilterOnField("assetInfoUid");
        if (currentUser.isEnableAssetTypeGroup()) {
            searchFilters.add(new SearchFilter("assetInfo.assetType", SearchFilter.Operator.IN, currentUser.getAssetTypeGroupMap().keySet(), "1*"));
        }
        this.lazyModel.setRowCount(dao.getAllCount(searchFilters, assetFlag, false));

        return dao.getAll(searchFilters, "createDate", isAscending, pageRequest.getLimit(), pageRequest.getOffset(), assetFlag, false);
    }

    public void onRowSelect(SelectEvent event) {
        AssetOperationRecord operationRecord = (AssetOperationRecord) event.getObject();
        if (this.searchFilters == null) {
            this.searchFilters = new HashSet<>();
        }
        // need list or set
        searchFilters.add(new SearchFilter("assetInfoUid", SearchFilter.Operator.EQ, operationRecord.getAssetInfoUid(), "1*"));
    }

    public void onSelectAsset(SelectEvent event) {
        AssetOperationRecord operationRecord = (AssetOperationRecord) event.getObject();
        String url = "actionName=View&assetInfoUid=" + operationRecord.getAssetInfoUid();
        url = "assetOperationRecordList.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public void returnPreviousLevel() {
        this.removeFilterOnField("assetInfoUid");
    }

    @Override
    public void onFilter(FilterEvent event) {
        if (searchFilters == null) {
            searchFilters = new HashSet<>();
        }
        searchFilters.clear();

        if (operationFlag) {
            searchFilters.add(new SearchFilter("assetInfoUid", SearchFilter.Operator.EQ, assetInfoUid, "1*"));
        }
        getFilterMatchModes((DataTable) event.getComponent());
        for (Map.Entry<String, Object> filter : event.getFilters().entrySet()) {
            String fieldName = filter.getKey();
            Object filterValue = filter.getValue();
            if (filterValue == null) {
                continue;
            }
            if (isDangerValue(filterValue)) {
                filterValue = "";
            }
            if (isDateTimeField(fieldName)) {
                // support lte & gte for datetime field
                String[] values = filterValue.toString().split("~");
                if (values.length > 0) {
                    filterValue = TimeUtil.fromString(values[0]);

                    if (filterValue != null) {
                        searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.GTE, ((DateTime) filterValue).toDate()));
                    }
                }

                if (values.length > 1) {
                    filterValue = TimeUtil.fromString(values[1]);
                    if (filterValue != null) {
                        searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.LTE, ((DateTime) filterValue).toDate()));
                    }
                }
                continue;
            }

            String matchMode = filterMatchModes.get(fieldName);
            addFilter(fieldName, matchMode, filterValue);
        }
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

    public boolean getOperationFlag() {
        return operationFlag;
    }

    public void setOperationFlag(boolean operationFlag) {
        this.operationFlag = operationFlag;
    }

    public List<AssetOperationRecord> getAssetOperationRecordList(String assetUid){
        searchFilters = new HashSet<>();
        searchFilters.add(new SearchFilter("assetInfoUid", SearchFilter.Operator.EQ, assetUid, "1*2+"));
        List<AssetOperationRecord> list = dao.getAll(searchFilters, "createDate", false, 1000, 0, false, true);
        searchFilters.clear();
        return list;
    }

    public OrgInfo getOrgInfoByUid(String uid) {
        return orgInfoDao.getByUid(uid);
    }

}
