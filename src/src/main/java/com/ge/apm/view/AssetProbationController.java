package com.ge.apm.view;

import com.ge.apm.dao.AssetProbationRepository;
import com.ge.apm.domain.AssetProbation;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
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
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, queryIndex, "1*"));
        }
        Set<String> orgUidSet = uaaService.getOrgListByUser(loginUser.getUid()).stream().map(OrgInfo::getSiteUID).collect(Collectors.toSet());
        searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.IN, orgUidSet, "1*2+"));
        searchFilters.add(new SearchFilter("institutionUid", SearchFilter.Operator.IN, orgUidSet, "1*2+"));
        searchFilters.add(new SearchFilter("hospitalUid", SearchFilter.Operator.IN, orgUidSet, "1*2+"));
        searchFilters.add(new SearchFilter("siteUid", SearchFilter.Operator.IN, orgUidSet, "1*2+"));
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    public String createAction() {
        return UrlEncryptController.encodeUrlParam("externalAssetEdit?action=-1");
    }
    public String editAction(Integer action) {
        return UrlEncryptController.encodeUrlParam("externalAssetEdit?action=" + action + "&selectedId=".concat(selected.getId()));
    }

    public void cancelOrder() {
        this.selected.setStatus(7);
        this.selected.setCancelUserUid(loginUser.getUid());
        this.selected.setCancelUserName(loginUser.getName());
        this.selected.setCancelTime(DateUtil.now());
        dao.save(this.selected);
        WebUtil.addSuccessMessage(WebUtil.getMessage("SubmissionSuccessfullyCanceled"));
    }
}
