/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetContractRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.ContractInfoItemRepository;
import com.ge.apm.domain.AssetContract;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.ContractInfoItem;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.model.TreeNode;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class AssetInfoService {

    AssetInfoRepository assetInfoDao = WebUtil.getBean(AssetInfoRepository.class);
    AssetContractRepository contractDao = WebUtil.getBean(AssetContractRepository.class);
    ContractInfoItemRepository contractItemDao = WebUtil.getBean(ContractInfoItemRepository.class);
    OrgInfoRepository orgDao = WebUtil.getBean(OrgInfoRepository.class);

    UaaService uaaService = WebUtil.getBean(UaaService.class);
    V2_WorkOrderService woService = WebUtil.getBean(V2_WorkOrderService.class);

    public String getAssetInfoName(Integer id) {
        if (null == id || id == 0) {
            return "";
        } else {
            return Optional.ofNullable(assetInfoDao.findById(id)).map(t -> t.getName()).orElse("");
        }
    }

    public List<UserAccount> getWorkerList(Integer assetId) {
        if (null == assetId) {
            assetId = 0;
        }
        return woService.getWorkerList(assetId);
    }

    public AssetInfo getAssetInfo(int id) {
        return assetInfoDao.findById(id);
    }

    public List<AssetInfo> getAssetList() {
        UserAccount currentUser = UserContextService.getCurrentUserAccount();
        List<SearchFilter> assetInfoFilters = new ArrayList<>();
        assetInfoFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, currentUser.getSiteId()));
        List<AssetInfo> assetList = assetInfoDao.findBySearchFilter(assetInfoFilters);
        return assetList;
    }

    public List<ContractInfoItem> getContractList(String assetUid) {
//        return contractDao.findByAssetId(assetId);
            //return contractItemDao.findByAssetUid(assetUid);
        return contractItemDao.getByAssetUidAndContractType(assetUid);
    }

    public List<OrgInfo> getHospitalList() {
        UserAccount user = UserContextService.getCurrentUserAccount();
        return uaaService.getUserAccessibleSiteList(user.getUid());
    }

    public List<OrgInfo> getOrgDeptList(String siteUid) {
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<OrgInfo> orgInfos = uaaService.getOrgDeptList(siteUid, user.getUid());
        orgInfos.sort(TreeNodeUtil.orgInfoComparator);
        return orgInfos;
    }

    public List<OrgInfo> getClinicalDeptList(String siteUid) {
        List<OrgInfo> orgInfos = orgDao.findBySiteUIDAndOrgTypeOrderByNameAsc(siteUid, 4);
        orgInfos.sort(TreeNodeUtil.orgInfoComparator);

        return orgInfos;
    }

    public List<OrgInfo> getHierachyClinicalDeptList(String siteUid) {
        List<OrgInfo> orgInfos = orgDao.findBySiteUIDAndOrgTypeIn(siteUid, new Integer[] {4});
        orgInfos.sort(TreeNodeUtil.orgInfoComparator);

        return orgInfos;
    }

    public List<OrgInfo> getClinicalDeptListWithEmptyValue(String siteUid) {
        List<OrgInfo> orgInfos = orgDao.findBySiteUIDAndOrgTypeOrderByNameAsc(siteUid, 4);
        orgInfos.sort(TreeNodeUtil.orgInfoComparator);

        OrgInfo emptyValue = new OrgInfo();
        emptyValue.setName(WebUtil.getMessage("selectOneMessage"));
        orgInfos.add(0, emptyValue);

        return orgInfos;
    }

    public List<SelectItem> getAllAccessibleOrgDeptList() {
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<OrgInfo> sitesList = uaaService.getUserAccessibleSiteList(user.getUid());
        List<SelectItem> dropdownList = new ArrayList<SelectItem>();
        for (OrgInfo item : sitesList) {
            SelectItemGroup group = new SelectItemGroup(item.getName());
            List<SelectItem> itemList = uaaService.getOrgDeptList(item.getUid(), user.getUid()).stream()
                    .map(itemDept -> new SelectItem(itemDept.getUid(), itemDept.getName())).collect(Collectors.toList());

            group.setSelectItems(itemList.toArray(new SelectItem[itemList.size()]));
            dropdownList.add(group);
        }
        return dropdownList;
    }

    public List<SelectItem> getAllAccessibleOrgDeptListNestedByOrgLevel() {
        UserAccount user = UserContextService.getCurrentUserAccount();
        TreeNode rootNode = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(user.getSiteUID(), user.getUid());

        TreeNodeUtil.sortTreeNodeByName(rootNode);

        List<SelectItem> dropdownList = new ArrayList<>();
        if (rootNode != null) {
            traverse(rootNode, dropdownList);
        }

        return dropdownList;
    }
    public List<SelectItem> getAllAccessibleOrgDeptListNestedByOrgLevelForReturn() {
        UserAccount user = UserContextService.getCurrentUserAccount();
        TreeNode rootNode = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelectedForReturn(user.getSiteUID(), user.getUid());

        TreeNodeUtil.sortTreeNodeByName(rootNode);

        List<SelectItem> dropdownList = new ArrayList<>();
        if (rootNode != null) {
            traverse(rootNode, dropdownList);
        }

        return dropdownList;
    }

    private void traverse(TreeNode node, List<SelectItem> itemList) {

        OrgInfo orgInfo = (OrgInfo) node.getData();

//        String name = padLeft(orgInfo.getName(), orgInfo.getOrgType());
        SelectItem selectItem = new SelectItem(orgInfo.getUid(), orgInfo.getName(), "", orgInfo.getOrgType() != 4);
        itemList.add(selectItem);

        if (!node.isLeaf()) {
            for (TreeNode nodeItem : node.getChildren()) {
                traverse(nodeItem, itemList);
            }
        }

    }

    private String padLeft(String name, int level) {

        StringBuilder sb = new StringBuilder();
        for (int index = 0; index < level; index++) {
            sb.append(" ");
        }
        return sb.toString() + name;
    }

}
