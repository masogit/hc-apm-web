package com.ge.apm.view;

import com.ge.apm.dao.AssetProbationRepository;
import com.ge.apm.domain.AssetProbation;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetProbationController extends GenericCRUDUUIDController<AssetProbation> {
    private AssetProbationRepository dao;
    private UaaService uaaService;

    @Getter @Setter
    private UserAccount loginUser;
    @Getter @Setter
    private Integer queryIndex = -1;
    @Getter @Setter
    private List<OrgInfo> assetDeptList;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetProbationRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        loginUser = UserContextService.getCurrentUserAccount();

        assetDeptList = uaaService.getClinicalDeptListBySiteUID(loginUser.getSiteUID());
        String query = WebUtil.getRequestParameter("query");
        if (ObjectUtil.isNotEmpty(query)) {
            this.queryIndex = Integer.parseInt(query);
        }
    }

    @Override
    protected GenericRepositoryUUID<AssetProbation> getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetProbation> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        if (queryIndex >= 0) {
            if (queryIndex == 7) {
                searchFilters.add(new SearchFilter("cancel", SearchFilter.Operator.EQ, true, "1*"));
            } else {
                searchFilters.add(new SearchFilter("cancel", SearchFilter.Operator.EQ, false, "1*"));
                searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, queryIndex, "1*"));
            }
        }
        if (super.hasFilterOnField("status")) {
            SearchFilter statusFilter = searchFilters.stream().filter(item -> "status".equals(item.fieldName)).collect(Collectors.toList()).get(0);
            super.removeFilterOnField("status");
            if ("7".equals(statusFilter.value.toString())) {
                searchFilters.add(new SearchFilter("cancel", SearchFilter.Operator.EQ, true, "1*"));
            } else {
                searchFilters.add(new SearchFilter("cancel", SearchFilter.Operator.EQ, false, "1*"));
                searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, statusFilter.value.toString(), "1*"));
            }
        }
        Set<String> functionList = UserContextService.getCurrentUserFunction();
        if (functionList.contains("EXTERNAL_ASSET_MANAGE_APPLICATION")) {
            Set<String> orgUidSet = uaaService.getOrgListByUser(loginUser.getUid()).stream().map(OrgInfo::getSiteUID).collect(Collectors.toSet());
            searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.IN, orgUidSet, "1*2+"));
            searchFilters.add(new SearchFilter("institutionUid", SearchFilter.Operator.IN, orgUidSet, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUid", SearchFilter.Operator.IN, orgUidSet, "1*2+"));
            searchFilters.add(new SearchFilter("siteUid", SearchFilter.Operator.IN, orgUidSet, "1*2+"));
        } else if (functionList.contains("EXTERNAL_ASSET_MANAGE_DETECTION")) {
            searchFilters.add(new SearchFilter("detectionUserUid", SearchFilter.Operator.EQ, loginUser.getUid(), "1*"));
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.GTE, 1, "1*"));
        } else if (functionList.contains("EXTERNAL_ASSET_MANAGE_PROBATION")) {
            searchFilters.add(new SearchFilter("probationStartUserUid", SearchFilter.Operator.EQ, loginUser.getUid(), "1*"));
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.GTE, 2, "1*"));
        } else {
            searchFilters.add(new SearchFilter("id", SearchFilter.Operator.EQ, "000", "1*"));
        }
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    public String createAction() {
        return UrlEncryptController.encodeUrlParam("externalAssetEdit?action=-1");
    }
    public String editAction(Integer action) {
        return UrlEncryptController.encodeUrlParam("externalAssetEdit?action=" + action + "&selectedId=".concat(selected.getId()));
    }

    public void cancelOrder() {
        this.selected.setCancel(true);
        this.selected.setCancelUserUid(loginUser.getUid());
        this.selected.setCancelUserName(loginUser.getName());
        this.selected.setCancelTime(DateUtil.now());
        this.selected.setLastModifiedBy(loginUser.getName());
        this.selected.setLastModifiedDate(DateUtil.now());
        dao.save(this.selected);
        WebUtil.addSuccessMessage(WebUtil.getMessage("SubmissionSuccessfullyCanceled"));
    }

    public void closeOrder(AssetProbation assetProbation) {
        assetProbation.setStatus(6);
        assetProbation.setCloseUserUid(loginUser.getUid());
        assetProbation.setCloseUserName(loginUser.getName());
        assetProbation.setCloseTime(DateUtil.now());
        assetProbation.setLastModifiedBy(loginUser.getName());
        assetProbation.setLastModifiedDate(DateUtil.now());
        dao.save(assetProbation);
        WebUtil.addSuccessMessage(WebUtil.getMessage("CloseSuccess"));
    }

    public void onSelectOrder(String selectedId) {
        String url = "actionName=View&selectedId=" + selected.getId() + "&action=" + queryIndex;
        url = "externalAssetEdit.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public boolean canOperation() {
        if (ListUtil.newArrayList(-1, 6, 7).contains(queryIndex)) {
            return false;
        }
        List<String> roleNames = loginUser.getRoleNames();
        Set<String> functionList = UserContextService.getCurrentUserFunction();
        switch (queryIndex) {
            case 0:
            case 5:
                if (functionList.contains("EXTERNAL_ASSET_MANAGE_APPLICATION")) {
                    return true;
                }
                break;
            case 1:
                if (functionList.contains("EXTERNAL_ASSET_MANAGE_DETECTION") || functionList.contains("EXTERNAL_ASSET_MANAGE_APPLICATION")) {
                    return true;
                }
                break;
            case 2:
                if (functionList.contains("EXTERNAL_ASSET_MANAGE_PROBATION") || functionList.contains("EXTERNAL_ASSET_MANAGE_DETECTION") || functionList.contains("EXTERNAL_ASSET_MANAGE_APPLICATION")) {
                    return true;
                }
                break;
            case 3:
                if (functionList.contains("EXTERNAL_ASSET_MANAGE_PROBATION")) {
                    return true;
                }
                break;
            case 4:
                if (functionList.contains("EXTERNAL_ASSET_MANAGE_DETECTION")) {
                    return true;
                }
                break;
        }
        return false;
    }
}
