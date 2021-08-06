package com.ge.apm.view.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.ZkbContractAssetRepository;
import com.ge.apm.dao.ZkbContractRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetInstallationService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
@Data
public class SupplierAssetController extends JpaCRUDController<AssetInfo> {

    private AssetInfoRepository dao;
    private UaaService uuaService;
    private AssetInstallationService aiService;


    private AssetInfo selectedAsset;
    private String oneSearchStr;

    private ServerEventInterface callingController;
    private Boolean showDialog = true;
    private String updateViewIDs;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
        uuaService = WebUtil.getBean(UaaService.class);
        aiService = WebUtil.getBean(AssetInstallationService.class);
        this.filterBySite = false;
        this.filterByHospital = false;
    }

    @Override
    protected AssetInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        this.selected = null;
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        UserAccount user = UserContextService.getCurrentUserAccount();
        searchFilters.add(new SearchFilter("supplierNewUID", SearchFilter.Operator.EQ, user.getTenantUID()));
        searchFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));

        return super.loadData(pageRequest);
    }


    public void onSelectRow() {
        selectedAsset = this.selected;
    }


    public void prepareDialogCallback(ServerEventInterface callingController, String updateViewIDs) {
        showDialog = true;
        this.selected = null;
        this.selectedAsset = null;
        this.callingController = callingController;
        this.updateViewIDs = updateViewIDs;

    }

    public void confirmSelection() {
        callingController.onServerEvent("assetSelected", this.selectedAsset);
    }

    @Override
    public void setSearchFilter() {
        super.setSearchFilter();
        if (StringUtils.isNotEmpty(oneSearchStr)) {
            this.updateSearchFilter(new SearchFilter("departNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("financingNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("qrCode", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("serialNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
        }
    }

    public String getOneSearchStr() {
        return oneSearchStr;
    }

    public void setOneSearchStr(String oneSearchStr) {
        this.oneSearchStr = oneSearchStr;
    }
}
