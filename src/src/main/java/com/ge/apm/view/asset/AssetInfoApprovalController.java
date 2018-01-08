package com.ge.apm.view.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetInfoApprovalController extends JpaCRUDController<AssetInfo> {

    private AssetInfoRepository dao;
    private UaaService uaaService;
    private OrgInfoRepository orgDao;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
    }

    @Override
    protected AssetInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        selected = null;
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        if (!this.hasFilterOnField("siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
            searchFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
            searchFilters.add(new SearchFilter("usageStatus", SearchFilter.Operator.EQ, 2));
        }
        return dao.findBySearchFilter(this.searchFilters, pageRequest);

    }

    public void rejectCreate() {
        this.selected.setUsageStatus(1);
        dao.save(selected);
    }

    public void approveCreate() {
        this.selected.setUsageStatus(3);
        dao.save(selected);
    }

    public void cancel() {
        this.selected = null;
    }

    public String getHospitalName(String siteUid) {
        OrgInfo org = orgDao.getByUid(siteUid);
        return org == null ? "" : org.getName();
    }

    public String getParentAssetName() {
        if (this.selected.getParentAssetId() != null) {
            AssetInfo assetInfo = this.dao.getById(this.selected.getParentAssetId());
            if (assetInfo != null) {
                return assetInfo.getName() + "(" + assetInfo.getFunctionType() + ")";
            }
        }
        return "";
    }

}
