package com.ge.apm.service.nurseSite;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AssetInstallationService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.dao.SearchFilter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author nick.liu
 * @create 2019/3/22 11:19
 */
@Service
public class AssetSelectService {

    private static final Logger log = LoggerFactory.getLogger(AssetSelectService.class);

    @Autowired
    private UaaService uaaService;

    @Autowired
    private AssetInstallationService aiService;

    @Autowired
    private AssetInfoRepository assetInfoRepository;

    public TreeNode loadOrgTree() {
        UserAccount currentUser = UserContextService.getCurrentUserAccount();
        TreeNode orgTreeRoot = new DefaultTreeNode("org", null, null);
        TreeNode subTree = uaaService.getOrgTreeByParentIdWithSelectedChildren(currentUser.getSiteUID());
        orgTreeRoot.getChildren().add(subTree);
        log.debug("load org tree complete: " + subTree.getChildCount());
        return orgTreeRoot;
    }

    public OrgInfo loadSelectedOrg() {
        UserAccount currentUser = UserContextService.getCurrentUserAccount();
        OrgInfo selectedOrg = aiService.getOrgInfoByUid(currentUser.getSiteUID());
        log.debug("selected org is: " + selectedOrg.getName());
        return selectedOrg;
    }

    public List<OrgInfo> loadDeptListByOrg(OrgInfo selectedOrg) {
        UserAccount currentUser = UserContextService.getCurrentUserAccount();
        List<OrgInfo> deptList = uaaService.findAccessClinicalUidByUserUid(currentUser.getUid(), selectedOrg.getSiteUID());
        List<OrgInfo> validDeptList = deptList.stream()
                .filter(t -> this.getDeptsWithAsset(selectedOrg).contains(t.getUid()))
                .collect(Collectors.toList());
        return validDeptList;
    }

    public List<OrgInfo> loadValidDeptList(OrgInfo selectedOrg) {
        List<OrgInfo> accessibleDeptList = uaaService.getAllAccessibleClinicalDeptList();
        List<OrgInfo> validDeptList = accessibleDeptList.stream()
                .filter(t -> this.getDeptsWithAsset(selectedOrg).contains(t.getUid()))
                .collect(Collectors.toList());
        return validDeptList;
    }

    public List<AssetInfo> loadAssetsBySearchFilter(List<SearchFilter> searchFilters) {
        List<AssetInfo> assetInfoList = assetInfoRepository.findBySearchFilter(searchFilters);
        return assetInfoList;
    }

    private List<String> getDeptsWithAsset(OrgInfo selectedOrg) {
        List<String> clinicalDeptsWithAsset = assetInfoRepository.findDistinctClinicalDeptUids(selectedOrg.getSiteUID());
        List<String> assetDeptsWithAsset = assetInfoRepository.findDistinctAssetDeptUids(selectedOrg.getSiteUID());
        clinicalDeptsWithAsset.addAll(assetDeptsWithAsset);
        List<String> validDeptUids = clinicalDeptsWithAsset.stream().distinct().collect(Collectors.toList());
        return validDeptUids;
    }

}
