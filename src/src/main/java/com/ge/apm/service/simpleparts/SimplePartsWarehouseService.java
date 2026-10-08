package com.ge.apm.service.simpleparts;

import com.ge.apm.dao.*;
import com.ge.apm.dao.simpleparts.SimplePartsInventoryRepository;
import com.ge.apm.dao.simpleparts.SimplePartsWarehouseOrgRepository;
import com.ge.apm.dao.simpleparts.SimplePartsWarehouseRepository;
import com.ge.apm.dao.simpleparts.SimplePartsWarehouseLocatorRepository;
import com.ge.apm.dao.simpleparts.SimplePartsWarehouseUserRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.simpleparts.*;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: csh
 * @date: 2020/7/13 13:51
 * @version: 1.0
 */
@Service
public class SimplePartsWarehouseService {

    @Autowired
    private SimplePartsWarehouseRepository partsWarehouseRepository;

    @Autowired
    private OrgInfoRepository orgInfoRepository;

    @Autowired
    private SimplePartsWarehouseOrgRepository partsWarehouseOrgRepository;

    @Autowired
    private SimplePartsWarehouseUserRepository partsWarehouseUserRepository;

    @Autowired
    private SimplePartsInventoryRepository partsInventoryRepository;

    private static final Integer START_USING = 1;
    private static final Integer STOP_USING = 0;

    @Autowired
    private SimplePartsWarehouseLocatorRepository simplePartsWarehouseLocatorRepository;

    /**
     * @param
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsWarehouse>
     * @date 2020/7/13 13:54
     */
    @Transactional(readOnly = true)
    public List<SimplePartsWarehouse> findAll() {
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        List<SimplePartsWarehouse> list =  partsWarehouseRepository.findAllWarehouseList(currentUserAccount.getTenantUID());

        return list;
    }

    /**
     * @date 2020/7/13 18:18
     * 保存数据
     */
    @Transactional
    public SimplePartsWarehouse save(SimplePartsWarehouse sparePartsWarehouse) {
        return partsWarehouseRepository.save(sparePartsWarehouse);
    }

    @Transactional(readOnly = true)
    public TreeNode buildSparePartsInTree(String tenantUID) {
        List<OrgInfo> resultList = orgInfoRepository.getOrgHierarchyExceptClinicalDeptForTenant(tenantUID);
        Map<String, TreeNode> treeNodeMap = new HashMap<>();
        for (OrgInfo orgInfo : resultList) {
            TreeNode node = new DefaultTreeNode("org", orgInfo, null);
            node.setRowKey(orgInfo.getUid());
            treeNodeMap.put(orgInfo.getUid(), node);
        }
        TreeNode root = new DefaultTreeNode("root", null, null);
        for (Map.Entry<String, TreeNode> entry : treeNodeMap.entrySet()) {
            TreeNode currentNode = entry.getValue();
            OrgInfo currentData = (OrgInfo) currentNode.getData();

            if (null == currentData.getParentUID()) {
                root.getChildren().add(currentNode);
            } else {
                TreeNode parentNode = treeNodeMap.get(currentData.getParentUID());
                if (null != parentNode) {
                    parentNode.getChildren().add(currentNode);
                }
            }
        }
        sortSysAuthGroupNaturally(root);
        return root;
    }

    private void sortSysAuthGroupNaturally(TreeNode root) {
        root.getChildren().sort((TreeNode o1, TreeNode o2) -> {
                    OrgInfo sag1 = (OrgInfo) o1.getData();
                    OrgInfo sag2 = (OrgInfo) o2.getData();
                    return sag1.getName().compareTo(sag2.getName());
                }
        );
        for (TreeNode node : root.getChildren()) {
            sortSysAuthGroupNaturally(node);
        }
    }

    /**
     * @param id
     * @return com.ge.apm.domain.simpleparts.PartsWarehouse
     * @date 2020/7/22 15:21
     * 根据id查询
     */
    @Transactional(readOnly = true)
    public SimplePartsWarehouse findWarehouseNameById(String id) {
        return partsWarehouseRepository.getById(id);
    }

    private static List<SimplePartsWarehouse> distinctByKey(List<SimplePartsWarehouse> list) {
        Set<SimplePartsWarehouse> set = new TreeSet<SimplePartsWarehouse>(new Comparator<SimplePartsWarehouse>() {
            @Override
            public int compare(SimplePartsWarehouse o1, SimplePartsWarehouse o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        set.addAll(list);
        return new ArrayList<SimplePartsWarehouse>(set);
    }

    /**
     * @param partsItemId
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsWarehouse>
     * @date 2020/7/13 13:54
     * 根据siteUid和userId 查找备件所属仓库
     */
    @Transactional(readOnly = true)
    public List<SimplePartsWarehouse> findAllByPartsItemId(String partsItemId, String siteUid) {
        List<SimplePartsWarehouse> result = Lists.newArrayList();
        Set<String> idSets = new HashSet<>();

        List<SimplePartsInventory> partsInventorys = partsInventoryRepository.findByPartsItemId(partsItemId);
        Set<String> warehouseIds1 = partsInventorys.stream().filter(item ->item.getPartsWarehouseId() != null).map(item -> item.getPartsWarehouseId()).collect(Collectors.toSet());

        List<SimplePartsWarehouse> warehouseList = this.findAllBySiteUidAndUserId(siteUid);
        Set<String> warehouseIds2 = warehouseList.stream().map(item -> item.getId()).collect(Collectors.toSet());

        idSets.addAll(warehouseIds1);
        idSets.retainAll(warehouseIds2);

        if(idSets.size() >0 ){
            UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
            result = partsWarehouseRepository.getByIds(currentUserAccount.getTenantUID(),idSets);
            result =  result.stream().filter(item -> (item.getStatus().equals(START_USING))).collect(
                    Collectors.collectingAndThen(
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(SimplePartsWarehouse::getId))), ArrayList::new)
            );
        }
        return result;
    }


    /**
     * @param siteUid
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsWarehouse>
     * @date 2020/7/13 13:54
     * 根据siteUid和userId 查找可用仓库
     */
    @Transactional(readOnly = true)
    public List<SimplePartsWarehouse> findAllBySiteUidAndUserId(String siteUid) {
        List<SimplePartsWarehouse> result = Lists.newArrayList();
        Set<String> idSets = Sets.newHashSet();

        List<SimplePartsWareHouseOrg> listOrg = partsWarehouseOrgRepository.findAllByOrgUid(siteUid);
        if(listOrg.size() > 0){
            idSets.addAll(listOrg.stream().filter(item -> item.getPartsWarehouseId() != null).map(item -> item.getPartsWarehouseId()).collect(Collectors.toSet()));
        }

        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        List<SimplePartsWareHouseUser> partsWareHouseUserList = partsWarehouseUserRepository.findAllByUserUid(currentUserAccount.getUid());
        if(partsWareHouseUserList.size() > 0){
            idSets.addAll(partsWareHouseUserList.stream().filter(item -> item.getPartsWarehouseId() != null).map(item -> item.getPartsWarehouseId()).collect(Collectors.toSet()));
        }

        if(idSets.size() > 0){
            result =  partsWarehouseRepository.getByIds(currentUserAccount.getTenantUID(),idSets);
            result =  result.stream().filter(item -> START_USING.equals(item.getStatus())).collect(
                    Collectors.collectingAndThen(
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(SimplePartsWarehouse::getId))), ArrayList::new)
            );
        }
        return result;
    }

    @Transactional(readOnly = true)
    public Optional<SimplePartsWarehouse> findDefaultWarehouseByAssetInfoForCurrentUser(AssetInfo assetInfo) {

        List<SimplePartsWarehouse> warehouseList = findWarehouseListMappedBySiteUid(assetInfo.getSiteUID());

        if (warehouseList.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(warehouseList.get(0));
        }
    }

    @Transactional(readOnly = true)
    public List<SimplePartsWarehouse> findWarehouseListMappedBySiteUid(String siteUid) {

        Set<String> idSets = Sets.newHashSet();

        List<SimplePartsWareHouseOrg> listOrg = partsWarehouseOrgRepository.findAllByOrgUid(siteUid);
        Set<String> warehouseIdFromOrgMapping = listOrg.stream()
                .filter(item -> item.getPartsWarehouseId() != null)
                .map(item -> item.getPartsWarehouseId()).collect(Collectors.toSet());

        idSets.addAll(warehouseIdFromOrgMapping);
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        if(idSets.size() > 0){
            List<SimplePartsWarehouse> resultList =  partsWarehouseRepository.getPartsWarehouseListInUseByIds(currentUserAccount.getTenantUID(),idSets);
            return resultList;
        } else {
            return Collections.emptyList();
        }

    }

    @Transactional(readOnly = true)
    public List<SimplePartsWarehouse> findWarehouseListInRequest() {

        Set<String> idSets = Sets.newHashSet();

        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();

        List<SimplePartsWareHouseOrg> listOrg = partsWarehouseOrgRepository.findAllByOrgUid(currentUserAccount.getSiteUID());
        Set<String> warehouseIdFromOrgMapping = listOrg.stream()
                .filter(item -> item.getPartsWarehouseId() != null)
                .map(item -> item.getPartsWarehouseId()).collect(Collectors.toSet());

        idSets.addAll(warehouseIdFromOrgMapping);

        if (UserContextService.isPartsAdmin()) {
            List<SimplePartsWareHouseUser> partsWareHouseUserList = partsWarehouseUserRepository.findAllByUserUid(currentUserAccount.getUid());
            Set<String> warehouseIdFromUser = partsWareHouseUserList.stream().
                    filter(item -> item.getPartsWarehouseId() != null)
                    .map(item -> item.getPartsWarehouseId()).collect(Collectors.toSet());
            idSets.addAll(warehouseIdFromUser);
        }

        if(idSets.size() > 0){
            List<SimplePartsWarehouse> resultList =  partsWarehouseRepository.getPartsWarehouseListInUseByIds(currentUserAccount.getTenantUID(),idSets);
            return resultList;
        } else {
            return Collections.emptyList();
        }

    }

    @Transactional(readOnly = true)
    public SimplePartsWareHouseLocator findWarehouseLocatorNameById(String id) {
        return simplePartsWarehouseLocatorRepository.findById(id);
    }
}
