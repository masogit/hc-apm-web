package com.ge.apm.view.asset;


import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.SpecialAssetInfoService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.AssetTypeGroupService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.nurseSite.AssetSelectController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class SpecialAssetPermitInfoController  extends GenericCRUDUUIDController<PermitInfo> implements Serializable, ServerEventInterface {
    private static final Logger logger = LoggerFactory.getLogger(SpecialAssetController.class);


    private PermitInfoRepository permitInfoRepository;

    private UserAccount user;
    private UaaService uaaService;

    private UserContextService userContextService;

    private AssetSelectController assetSelectController;


    //选定的院区
    private OrgInfo selectSite;
    @Override
    protected void init() {
        permitInfoRepository = WebUtil.getBean(PermitInfoRepository.class);
        user = UserContextService.getCurrentUserAccount();
        uaaService = WebUtil.getBean(UaaService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        assetSelectController = WebUtil.getBean(AssetSelectController.class);
        assetSelectController.setCallingController(this);
    }

    @Override
    protected Page<PermitInfo> loadData(PageRequest pageRequest) {
        long l = System.currentTimeMillis();
        logger.info("SpecialAssetPermitInfoController.loadData start time:{}",l);


        if (searchFilters == null) {
            searchFilters = new ArrayList();
        }
        this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID(), "1*"));

        if (!UserContextService.isMVSRole()) {
            if (!this.hasFilterOnField("siteUID")) {
                List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
                searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            }
        }

        if (null != selectSite) {
            this.updateSearchFilter(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> item.conditionPath.equals("1*5+")).collect(Collectors.toList()));
        }

        Page<PermitInfo> page = permitInfoRepository.findBySearchFilter(this.searchFilters, pageRequest);
        List<PermitInfo> content = page.getContent();

        content.forEach(item -> item.setWarrantyExpireAlertDays(30));
        return page;
    }


    @Override
    protected PermitInfoRepository getDAO() {
        return permitInfoRepository;
    }

    public void onSelectSiteTreeNode(NodeSelectEvent event) {

        this.searchFilters.clear();

        if (null != event.getTreeNode() && null != event.getTreeNode().getData()) {
            selectSite = (OrgInfo) event.getTreeNode().getData();
        } else {
            selectSite = null;
        }

        //如果是mvs role,选择了所有租户选项，则清空所选机构
        if (userContextService.getIsMVSRole() && selectSite != null && selectSite.getId() == -1) {
            selectSite = null;
        }

    }

    public void onSelectAsset() throws IOException {
        if (null == selected) {
            return;
        }

//        String url = "actionName=View&selectedid="
//                + selected.getAssetInfo().getUid()
//                + "&asset_name=" + selected.getAssetInfo().getName()
//                + "&terminate=" + this.terminate;
//        url = "AssetDetail.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);
//
//        RequestContext requestContext = RequestContext.getCurrentInstance();
//        requestContext.addCallbackParam("url", url);
    }


    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        // TODO Auto-generated method stub
        super.prepareCreate();
        assetSelectController.loadData4Create();
    }

    public String getEditPage(String permitUid, String certificateName) {
        String operation = "SpecialAssetPermitCreate.xhtml?actionName=Edit&selectedid=" + permitUid
                + "&name=" + certificateName;
        return UrlEncryptController.encodeUrlParam(operation);
    }


    public void deleteAssetPermit(PermitInfo permitInfo) {
        permitInfoRepository.delete(permitInfo);
    }

    public OrgInfo getSelectSite() {
        return selectSite;
    }

    public void setSelectSite(OrgInfo selectSite) {
        this.selectSite = selectSite;
    }
}
