package com.ge.apm.view.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.stream.Collectors;
import org.primefaces.event.NodeSelectEvent;

@ManagedBean
@ViewScoped
public class AssetInfoApprovalController extends JpaCRUDController<AssetInfo> {

    private AssetInfoRepository dao;
    private UaaService uaaService;
    private OrgInfoRepository orgDao;
    private BlobObjectRepository blobDao;
    private List<V2_BlobObject> picList;
    private OrgInfo fileterOrg;
    private UserAccount loginUser;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        blobDao = WebUtil.getBean(BlobObjectRepository.class);
        loginUser = UserContextService.getCurrentUserAccount();

        String encodeStr = WebUtil.getRequestParameter("str");
        if (!Strings.isNullOrEmpty(encodeStr)) {
            String assetUid = (String) UrlEncryptController.getValueFromMap(encodeStr, "assetUid");
            this.selected = dao.findByUid(assetUid);
        }
    }

    @Override
    protected AssetInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        if (loginUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(loginUser.getAssetTypeGroupList())) {
            return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
        }
        selected = null;
        if (this.searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        if (!this.hasFilterOnField("siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(OrgInfo::getUid).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
            searchFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
            searchFilters.add(new SearchFilter("usageStatus", SearchFilter.Operator.EQ, 2));
        }

        // 资产分类权限
        if (loginUser.isEnableAssetTypeGroup()) {
            searchFilters.add(new SearchFilter("assetType", SearchFilter.Operator.IN, loginUser.getAssetTypeGroupMap().keySet(), "1*"));
        }
        
        if (null != fileterOrg) {
            this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, fileterOrg.getUid(), "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "clinicalDeptUID".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
        return dao.findBySearchFilter(this.searchFilters, pageRequest);

    }

    public String checkItem() {
        String operation = "ApprovalItemDetail.xhtml?actionName=Edit&selectedid=" + selected.getUid()
                + "&asset_name=" + selected.getName()
                + "&terminate=" + false
                + "&approval=" + true;
        return UrlEncryptController.encodeUrlParam(operation);
    }

    public List<V2_BlobObject> getPicList() {
        if (selected == null) {
            return Collections.emptyList();
        }
        if (picList == null) {
            picList = blobDao.findByBoUuid(selected.getUid());
        }
        return picList;
    }

    public void rejectCreate() {
        this.selected.setUsageStatus(1);
        dao.save(selected);
    }

    public void approveCreate() {
        this.selected.setUsageStatus(3);
        dao.save(selected);
    }

    public void onClinicalDeptFilter(NodeSelectEvent event) {
        if (null != event && null != event.getTreeNode() && null != event.getTreeNode().getData()) {
            OrgInfo deptOrg = (OrgInfo) event.getTreeNode().getData();
            if (Objects.equals(deptOrg.getOrgType(), 4)) {
                fileterOrg = deptOrg;
                return;
            }
        }
        fileterOrg = null;
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

    public OrgInfo getFileterOrg() {
        return fileterOrg;
    }

    public void setFileterOrg(OrgInfo fileterOrg) {
        this.fileterOrg = fileterOrg;
    }
    
}
