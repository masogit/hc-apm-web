package com.ge.apm.view.acquisition;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbContract;
import com.ge.apm.domain.ZkbContractAsset;
import com.ge.apm.domain.acquisition.AcquisitionPlan;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AcquisitionSelectAssetController  extends JpaCRUDController<AssetInfo> {
    AssetInfoRepository dao = null;
    @Getter @Setter
    private String oneSearchStr;
    private UserContextService userContextService;
    private UserAccount user;
    private UaaService uuaService;
    @Getter @Setter
    private boolean showDialog = false;
    @Getter @Setter
    private Map<String, Boolean> selectedMap = new HashMap<>();
    @Setter @Getter
    private String applicationStatus;
    @Getter @Setter
    List<AssetInfo> assetInfos;

    Page<AssetInfo> assetInfoPage;
    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
        uuaService = WebUtil.getBean(UaaService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        user = UserContextService.getCurrentUserAccount();
        selectedMap = new HashMap<>();
    }
    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        if(StringUtils.isEmpty(applicationStatus) || AcquisitionPlan.ApplicationStatusEnum.TempSave.name().equals(applicationStatus)){
            this.selected = null;
            if (searchFilters == null) {
                searchFilters = new ArrayList<SearchFilter>();
            }
            this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID(), "1*"));
            if (user.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(user.getAssetTypeGroupList())) {
                return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
            }
            if (!this.hasFilterOnField("siteUID")) {

                searchFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
                List<String> orgUidList = uuaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
                searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                searchFilters.add(new SearchFilter("assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
            }
            if (user.isEnableAssetTypeGroup()) {
                searchFilters.add(new SearchFilter("assetType", SearchFilter.Operator.IN, user.getAssetTypeGroupMap().keySet(), "1*"));
            }
            assetInfoPage = dao.findBySearchFilter(searchFilters, pageRequest);
            return assetInfoPage;
        }else {
            return new PageImpl<>(getAssetInfos(pageRequest), pageRequest, assetInfos.size());
        }
    }

    public List<AssetInfo> getAssetInfos(PageRequest pageRequest) {
        if (assetInfos == null || assetInfos.isEmpty()) {
            return new ArrayList<>();
        }
        int start = (int) pageRequest.getOffset();
        if (start < 0 || start >= assetInfos.size()) {
            return new ArrayList<>();
        }
        int end = Math.min(start + pageRequest.getPageSize(), assetInfos.size());
        return new ArrayList<>(assetInfos.subList(start, end));
    }



    @Override
    public void setSearchFilter() {
        // TODO Auto-generated method stub
        super.setSearchFilter();
        if (StringUtils.isNotEmpty(oneSearchStr)) {
            this.updateSearchFilter(new SearchFilter("departNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("financingNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("qrCode", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("serialNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            if(userContextService.isCountyArea()){
                this.updateSearchFilter(new SearchFilter("systemId", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            }
        }
    }

    private String updateViewIDs;

    public String getUpdateViewIDs() {
        return updateViewIDs;
    }

    private ServerEventInterface callingController;
    private List<AssetInfo> selectedAssetList = new ArrayList<>();

    public void onSelectAllOfPage() {
        List<AssetInfo> currentPageAssets = assetInfoPage.getContent();
        boolean isSelectedAll = !currentPageAssets.stream()
                .anyMatch(item -> !selectedMap.containsKey(item.getUid()) || !selectedMap.get(item.getUid()));
        for (AssetInfo item : currentPageAssets) {
            selectedMap.put(item.getUid(), !isSelectedAll);
        }
    }

    public void onSelectRow() {
        // single row select handler - checkbox column handles multi-select independently
    }

    public void confirmSelection(String maintainFlag) {
        selectedAssetList = new ArrayList<>();

        if (selectedMap != null && !selectedMap.isEmpty()) {
            for (Map.Entry<String, Boolean> entry : selectedMap.entrySet()) {
                if (Boolean.TRUE.equals(entry.getValue())) {
                    AssetInfo a = dao.findByUid(entry.getKey());
                    if (a != null) {
                        selectedAssetList.add(a);
                    }
                }
            }
        }

        if (selectedAssetList.isEmpty() && this.selected != null) {
            selectedAssetList.add(this.selected);
        }

        if (selectedAssetList.isEmpty()) {
            WebUtil.addErrorMessageKey("noAssetSelected");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }

        showDialog = false;
        if (callingController != null) {
            callingController.onServerEvent("onAssetSelected", this.selectedAssetList);
        }
    }

    public void prepareDialogCallback(ServerEventInterface callingController, String updateViewIDs,List<AssetInfo> selectedAssets, String status, String assetUids) {
        showDialog = true;

        this.selected = null;
        this.selectedAssetList = new ArrayList<>();
        this.selectedMap = new HashMap<>();
        applicationStatus = status;
        assetInfos = new ArrayList<>();
        this.callingController = callingController;
        this.updateViewIDs = updateViewIDs;

        List<String> uids = Arrays.stream(assetUids.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());


        if(StrUtil.isNotEmpty(status) && !status.equals(AcquisitionPlan.ApplicationStatusEnum.TempSave.name())){
            for (String uid : uids){
                AssetInfo assetInfo = dao.findByUid(uid);
                assetInfos.add(assetInfo);
            }
        }else {
            if(selectedAssets == null){
                selectedAssets = new ArrayList<>();
            }
            for (String uid : uids){
                AssetInfo assetInfo = dao.findByUid(uid);
                selectedAssets.add(assetInfo);
                selectedMap.put(uid, true);
            }

        }
    }

    @Override
    protected GenericRepository<AssetInfo> getDAO() {
        return dao;
    }
}
