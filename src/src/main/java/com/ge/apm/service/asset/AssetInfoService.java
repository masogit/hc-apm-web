/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import cn.hutool.core.util.ObjectUtil;
import com.ge.apm.dao.*;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.SysAuthGroup;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.*;
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
    SysAuthGroupRepository authGroupDao = WebUtil.getBean(SysAuthGroupRepository.class);

    UaaService uaaService = WebUtil.getBean(UaaService.class);
    V2_WorkOrderService woService = WebUtil.getBean(V2_WorkOrderService.class);
    JdbcTemplate jdbcTemplate = WebUtil.getBean(JdbcTemplate.class);

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

    public List<OrgInfo> getClinicalDeptList(String siteUid,Integer id) {
        if(id==0){
            List<OrgInfo> orgInfos = orgDao.findBySiteUIDAndOrgTypeOrderByNameAsc1(siteUid);
            orgInfos.sort(TreeNodeUtil.orgInfoComparator);
            return orgInfos;
        }
        if (id==1){
            List<OrgInfo> orgInfos = orgDao.findBySiteUIDAndOrgTypeOrderByNameAsc2(siteUid);
            orgInfos.sort(TreeNodeUtil.orgInfoComparator);
            return orgInfos;
        }
        if(id==2){
            List<OrgInfo> orgInfos = orgDao.findBySiteUIDAndOrgTypeOrderByNameAsc3(siteUid);
            orgInfos.sort(TreeNodeUtil.orgInfoComparator);
            return orgInfos;
        }
        if(id==3){
            List<OrgInfo> orgInfos = orgDao.findBySiteUIDAndOrgTypeOrderByNameAsc4(siteUid);
            orgInfos.sort(TreeNodeUtil.orgInfoComparator);
            return orgInfos;
        }
        if(id==4){
            List<OrgInfo> orgInfos = orgDao.findBySiteUIDAndOrgTypeOrderByNameAsc5(siteUid);
            orgInfos.sort(TreeNodeUtil.orgInfoComparator);
            return orgInfos;
        }
        return null;
    }

    public List<OrgInfo> getClinicalDeptList(String siteUid) {
            List<OrgInfo> orgInfos = orgDao.findBySiteUIDAndOrgTypeOrderByNameAsc(siteUid,4);
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

    public TreeNode buildSysAuthGroupInTree() {

        List<SysAuthGroup> groupList = authGroupDao.listSysAuthGroup();

        TreeNode tempNode = new DefaultTreeNode("sysAuthGroup", null, null);
        Map<String, TreeNode> treeMap = groupList.stream().collect(Collectors.toMap(SysAuthGroup::getUid, item -> new DefaultTreeNode("sysAuthGroup", item, null)));
        for (SysAuthGroup org : groupList) {
            if (treeMap.containsKey(org.getParentUid())) {
                treeMap.get(org.getParentUid()).getChildren().add(treeMap.get(org.getUid()));
            } else {
                tempNode.getChildren().add(treeMap.get(org.getUid()));
            }
        }
        sortSysAuthGroupNaturally(tempNode);
        TreeNode rootNode = new DefaultTreeNode("sysAuthGroup", null, null);
        List<TreeNode> children = rootNode.getChildren();
        children.add(new DefaultTreeNode("sysAuthGroup", new SysAuthGroup("请选择..."),null));
        children.addAll(tempNode.getChildren());
        rootNode.setExpanded(true);
        return rootNode;
    }
    public AssetInfo selectAssetInfos(String uid,String name, String departNum,Integer assetGroup,Set<String> clinicalDeptOrgUidSet,String nationalStandardCode,
                                      String makerShortName,String manufacture,String qrCode,String startUsingDate,String purchaseDate){
        StringBuffer sqlStringBuffer = new StringBuffer();
        sqlStringBuffer.append(" select a.* from asset_info a where  a.uid = '"+ uid+"'");
        if(name!=null) sqlStringBuffer.append(" and a.name like '%"+name+"%'");
        if(departNum!=null) sqlStringBuffer.append(" and a.depart_num like '%"+departNum+"%'");
        if(assetGroup!=null) sqlStringBuffer.append(" and a.asset_group = "+ assetGroup);
        if(nationalStandardCode!=null) sqlStringBuffer.append(" and a.national_standard_code like '%"+nationalStandardCode+"%'");
        if(makerShortName!=null) sqlStringBuffer.append(" and a.make_short_name like '%"+makerShortName+"%'");
        if(manufacture!=null) sqlStringBuffer.append(" and a.manufacture like '%"+manufacture+"%'");
        if(qrCode!=null) sqlStringBuffer.append(" and a.qr_code like '%"+qrCode+"%'");
        if(CollectionUtils.isNotEmpty(clinicalDeptOrgUidSet)) {
            StringBuffer clinicalDeptUidStr=new StringBuffer();
            clinicalDeptUidStr.append("( ");
            for ( String clinicalDeptUid :clinicalDeptOrgUidSet) {
                clinicalDeptUidStr.append("'"+clinicalDeptUid+"' ,");
            }
            clinicalDeptUidStr.deleteCharAt(clinicalDeptUidStr .length() - 1);
            clinicalDeptUidStr.append(" )");
            sqlStringBuffer.append(" and a.clinical_dept_uid in "+clinicalDeptUidStr);
        }
        if (StringUtils.isNotEmpty(startUsingDate)){
            String startUsingDateStart = startUsingDate.substring(0, 10);
            String startUsingDateEnd = startUsingDate.substring(11, startUsingDate.length());
            sqlStringBuffer.append(" and a.install_date >=   '"+startUsingDateStart.replace("/","-")+"'");
            sqlStringBuffer.append(" and a.install_date <= '"+startUsingDateEnd.replace("/","-")+"'");
        }
        if (StringUtils.isNotEmpty(purchaseDate)){
            String purchaseDateStart = purchaseDate.substring(0, 10);
            String purchaseDateEnd = purchaseDate.substring(11, purchaseDate.length());
            sqlStringBuffer.append(" and a.purchase_date >=   '"+purchaseDateStart.replace("/","-")+"'");
            sqlStringBuffer.append(" and a.purchase_date <=  '"+purchaseDateEnd.replace("/","-")+"'");
        }
        List<AssetInfo> assetInfos=jdbcTemplate.query(sqlStringBuffer.toString(), new BeanPropertyRowMapper<AssetInfo>(AssetInfo.class));
        if(CollectionUtils.isNotEmpty(assetInfos)){
            return assetInfos.get(0);
        }
        return null;
    }


    public void sortSysAuthGroupNaturally(TreeNode root) {
        root.getChildren().sort((TreeNode o1, TreeNode o2) -> {
                    SysAuthGroup sag1 = (SysAuthGroup) o1.getData();
                    SysAuthGroup sag2 = (SysAuthGroup) o2.getData();
                    return sag1.getClassificationCode().compareTo(sag2.getClassificationCode());
                }
        );
        for (TreeNode node: root.getChildren()) {
            sortSysAuthGroupNaturally(node);
        }

    }

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    public static String getAssetUsedLifecycle(Date installDate, Integer lifecycle) {
        if (ObjectUtil.isEmpty(installDate) || ObjectUtil.isEmpty(lifecycle)) {
            return null;
        }
        Temporal temporal1 = LocalDate.parse(SDF.format(installDate));
        Temporal temporal2 = LocalDate.parse(SDF.format(System.currentTimeMillis()));
        return String.format("%.0f", Math.floor(Double.parseDouble(String.valueOf(ChronoUnit.MONTHS.between(temporal1, temporal2))) / 12));
    }
}
