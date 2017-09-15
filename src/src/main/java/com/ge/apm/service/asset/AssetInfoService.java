/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetContractRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetContract;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.primefaces.model.TreeNode;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class AssetInfoService {

    AssetInfoRepository assetInfoDao = WebUtil.getBean(AssetInfoRepository.class);
    AssetContractRepository contractDao = WebUtil.getBean(AssetContractRepository.class);
    OrgInfoRepository orgDao = WebUtil.getBean(OrgInfoRepository.class);

    UaaService uaaService = WebUtil.getBean(UaaService.class);

    public String getAssetInfoName(Integer id) {
        if (null == id || id == 0) {
            return "";
        } else {
            return Optional.ofNullable(assetInfoDao.findById(id)).map(t -> t.getName()).orElse("");
        }
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

    public List<AssetContract> getContractList(Integer assetId) {
        return contractDao.findByAssetId(assetId);
    }

    public List<OrgInfo> getHospitalList() {
        UserAccount user = UserContextService.getCurrentUserAccount();
        return uaaService.getUserAccessibleSiteList(user.getUid());
    }

    public List<OrgInfo> getOrgDeptList(String siteUid) {
        UserAccount user = UserContextService.getCurrentUserAccount();
        return uaaService.getOrgDeptList(siteUid,user.getUid());
    }
    
    public List<SelectItem> getAllAccessibleOrgDeptList(){
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<OrgInfo> sitesList = uaaService.getUserAccessibleSiteList(user.getUid());
        List<SelectItem> dropdownList = new ArrayList<SelectItem>();
        for(OrgInfo item : sitesList){
            SelectItemGroup group = new SelectItemGroup(item.getName());
            List<SelectItem> itemList = uaaService.getOrgDeptList(item.getUid(),user.getUid()).stream()
                    .map(itemDept -> new SelectItem(itemDept.getUid(),itemDept.getName())).collect(Collectors.toList());
            
            group.setSelectItems(itemList.toArray(new SelectItem[itemList.size()]));
            dropdownList.add(group);
        }
        return dropdownList;
    }

    public List<SelectItem> getAllAccessibleOrgDeptListNestedByOrgLevel(){
        UserAccount user = UserContextService.getCurrentUserAccount();
        TreeNode rootNode = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(user.getSiteUID(), user.getUid());

        List<SelectItem> dropdownList = new ArrayList<SelectItem>();
        traverse(rootNode, dropdownList);

        return dropdownList;
    }

    private void traverse(TreeNode node, List<SelectItem> itemList) {

        OrgInfo orgInfo = (OrgInfo)node.getData();

//        String name = padLeft(orgInfo.getName(), orgInfo.getOrgType());
        SelectItem selectItem = new SelectItem(orgInfo.getUid(), orgInfo.getName(), "", orgInfo.getOrgType() != 4);
        itemList.add(selectItem);

        if (!node.isLeaf()) {
            for (TreeNode nodeItem: node.getChildren()) {
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
