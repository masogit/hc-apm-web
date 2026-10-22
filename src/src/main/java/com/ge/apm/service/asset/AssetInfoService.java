/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.aop.SystemEventAuditAopAspect;
import com.ge.apm.dao.mapper.ContractInfoMapper;
import com.ge.apm.domain.*;
import com.ge.healthcare.cn.common.cons.AuditBoTypeEnum;
import com.ge.healthcare.cn.common.cons.SystemEventAuditEnum;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.apm.dao.*;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
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


    SystemEventAuditAopAspect systemEventAuditAopAspect = WebUtil.getBean(SystemEventAuditAopAspect.class);
    AssetInfoRepository assetInfoDao = WebUtil.getBean(AssetInfoRepository.class);
    OrgInfoRepository orgDao = WebUtil.getBean(OrgInfoRepository.class);
    SysAuthGroupRepository authGroupDao = WebUtil.getBean(SysAuthGroupRepository.class);
    SupplierRepository supplierDao = WebUtil.getBean(SupplierRepository.class);

    UaaService uaaService = WebUtil.getBean(UaaService.class);
    V2_WorkOrderService woService = WebUtil.getBean(V2_WorkOrderService.class);
    ContractInfoMapper contractInfoMapper = WebUtil.getBean(ContractInfoMapper.class);

    public String getAssetInfoName(Integer id) {
        if (null == id || id == 0) {
            return "";
        } else {
            return Optional.ofNullable(assetInfoDao.findById(id)).map(AssetInfo::getName).orElse("");
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
        return assetInfoDao.findBySearchFilter(assetInfoFilters);
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
        SelectItem selectItem = new SelectItem(orgInfo.getUid(), orgInfo.getName(), "", !Objects.equals(orgInfo.getOrgType(), 4));
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

    public static void sortSysAuthGroupNaturally(TreeNode root) {
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

    public void setSupplier(List<AssetInfo> assets){
        List<Integer> supplierIds = ListUtil.newArrayList(-1);
        for (AssetInfo asset : assets) {
            Optional.ofNullable(asset.getSupplierId()).map(supplierIds::add);
            Optional.ofNullable(asset.getManufactureId()).map(Integer::parseInt).map(supplierIds::add);
            Optional.ofNullable(asset.getMaitananceId()).map(Integer::parseInt).map(supplierIds::add);
        }
        List<Supplier> suppliers = supplierDao.findByIdIn(supplierIds);
        Map<Integer, String> supplierMap = suppliers.stream().collect(HashMap::new, (map, item) -> map.put(item.getId(), item.getTel()), HashMap::putAll);
        for (AssetInfo asset : assets) {
            asset.setVendorTel(supplierMap.get(asset.getSupplierId()));
            asset.setManufactureTel(supplierMap.get(Optional.ofNullable(asset.getManufactureId()).map(Integer::parseInt).orElse(null)));
            asset.setMaitananceTel(supplierMap.get(Optional.ofNullable(asset.getMaitananceId()).map(Integer::parseInt).orElse(null)));
        }
    }

    public void setWarrantyStatus(List<AssetInfo> assets){
        List<String> assetUidList = ObjectUtil.isEmpty(assets) ? ListUtil.newArrayList("-1") : assets.stream().map(AssetInfo::getUid).collect(Collectors.toList());
        List<Map<String, Object>> assetDateList = contractInfoMapper.findDateByAssetUidList(assetUidList);
        Map<String, List<Map<String, Object>>> assetDateMap = assetDateList.stream().collect(Collectors.groupingBy(map -> (String) map.get("assetuid")));
        Date now = DateUtil.now();
        for (AssetInfo asset : assets) {
            List<Map<String, Object>> dateList = assetDateMap.get(asset.getUid());
            if (ObjectUtil.isNotEmpty(dateList)) {
                long count = dateList.stream().filter(map -> ((Date) map.get("startdate")).before(now) && ((Date) map.get("enddate")).after(now)).count();
                if (count > 0) {
                    asset.setWarrantyStatus("在保");
                } else {
                    asset.setWarrantyStatus("出保");
                }
            } else {
                asset.setWarrantyStatus("不确定");
            }
        }
    }

    public void postProcessorExcel(){
        try{
            SystemEventAuditEnum eventAuditEnum = SystemEventAuditEnum.TABLE_DOWNLOAD_ASSET;
            SystemEventAudit systemEventAudit = systemEventAuditAopAspect.buildEventAudit(UserContextService.getCurrentUserAccount(), eventAuditEnum, null, Boolean.TRUE, "", AuditBoTypeEnum.TABLE_DOWNLOAD.getBoType(), "AssetInfo_export");
            systemEventAuditAopAspect.sendSystemAuditMessage(systemEventAudit);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
