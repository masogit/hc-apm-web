package com.ge.apm.service.simpleparts;

import com.ge.apm.dao.simpleparts.*;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.simpleparts.*;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author csh
 * @version 1.0
 * @date 2020/6/17
 * @descript 备件库存总Service
 * @package com.ge.apm.service.spareparts
 */
@Service
public class SimplePartsInventoryService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SimplePartsInventoryRepository partsInventoryRepository;

    @Autowired
    SimplePartsItemRepository partsItemRepository;

    @Autowired
    private SimplePartsWarehouseOrgRepository simplePartsWarehouseOrgRepository;

    @Autowired
    private SimplePartsWarehouseRepository simplePartsWarehouseRepository;

    @Autowired
    private SimplePartsWarehouseService simplePartsWarehouseService;
    @Autowired
    private SimplePartsWarehouseUserRepository partsWarehouseUserRepository;

    /**
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsInventory>
     * @throws
     * @params []
     * @author csh
     * @createTime 2020/6/8 14:53
     * @description 查找所有的备件库存信息
     */
    @Transactional(readOnly = true)
    public List<SimplePartsItem> findAll() {
        List<SimplePartsItem> list = Lists.newArrayList();
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        List<SimplePartsWareHouseOrg> simplePartsWareHouseOrgList = simplePartsWarehouseOrgRepository.findAllByOrgUid(currentUserAccount.getSiteUID());
        if(simplePartsWareHouseOrgList.size() == 0){
            logger.info("根据当前操作用户,未查询到关联的仓库组织. SiteUID : " +currentUserAccount.getOrgInfoUID());
            return list;
        }

        Set<String> partsWarehouseIds = simplePartsWareHouseOrgList.stream().map(p->p.getPartsWarehouseId()).collect(Collectors.toSet());
        List<SimplePartsWarehouse>  simplePartsWarehouseList = simplePartsWarehouseRepository.getByIds(currentUserAccount.getTenantUID(),partsWarehouseIds);

        if(simplePartsWarehouseList.size() == 0){
            logger.info("根据当前操作用户,未查询到关联的仓库. SiteUID : " +currentUserAccount.getOrgInfoUID());
            return list;
        }
        Set<String> partsWarehouseIds2 = simplePartsWarehouseList.stream().map(p->p.getId()).collect(Collectors.toSet());

//        List<SimplePartsInventory> all = partsInventoryRepository.findByTenantUid(currentUserAccount.getTenantUID());
        List<SimplePartsInventory> all = partsInventoryRepository.findByPartsWarehouseIds(partsWarehouseIds2);
        if(all.size() > 0){
            Set<String> uidSet = all.stream().filter(item -> item.getPartsItemId() !=null).map(item -> item.getPartsItemId()).collect(Collectors.toSet());
            list = this.findByIds(uidSet);
        }
        return list;
    }

    @Transactional(readOnly = true)
    public List<SimplePartsItem> findByIds(Set<String> ids) {
        return partsItemRepository.findAll(ids);
    }
    /**
     * @return void
     * @params [partsInventoryLists]
     * @author csh
     * @createTime 2020/6/16 9:41
     * @description 批量保存
     */
    @Transactional
    public void save(List<SimplePartsInventory> partsInventoryLists) {
        partsInventoryRepository.save(partsInventoryLists);
    }

    /**
     * @return void
     * @params [partsInventory]
     * @author csh
     * @createTime 2020/6/18 13:33
     * @description 保存单个数据
     */
    @Transactional
    public SimplePartsInventory save(SimplePartsInventory partsInventory) {
        return partsInventoryRepository.save(partsInventory);
    }

    /**
     * @param warehouseId 仓库编码
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsInventory>
     * @date 2020/7/21 18:39
     * 根据仓库id查询
     */
    @Transactional(readOnly = true)
    public List<SimplePartsInventory> findByWarehouseId(String warehouseId) {
        if(StringUtils.isEmpty(warehouseId)){
            //修改 : 原先是查询租户下所有库存,现在是查询仓库机构映射下以及仓库管理员下,所有仓库,再根据这些仓库去查询库存
            List<SimplePartsWarehouse> list = simplePartsWarehouseService.findWarehouseListInRequest();
            if(list == null || list.size() == 0){
                return Lists.newArrayList();
            }
            Set<String> warehouseIds = list.stream()
                    .filter(item -> item.getId() != null)
                    .map(item -> item.getId()).collect(Collectors.toSet());
            return partsInventoryRepository.findByPartsWarehouseIds(warehouseIds);
        }

        return partsInventoryRepository.findByPartsWarehouseId(warehouseId);
    }

    @Transactional(readOnly = true)
    public List<SimplePartsInventory> findByWarehouseIdAndLocatorId(String warehouseId, String locatorId) {
        return partsInventoryRepository.findByWarehouseIdAndLocatorId(warehouseId, locatorId);
    }

    public static void addCurrentOrgInfo(SimplePartsInventory partsInventory) {
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();

        partsInventory.setTenantUID(currentUserAccount.getTenantUID());
        partsInventory.setInstitutionUID(currentUserAccount.getInstitutionUID());
        partsInventory.setHospitalUID(currentUserAccount.getHospitalUID());
        partsInventory.setSiteUID(currentUserAccount.getSiteUID());
        partsInventory.setClinicalDeptUID(currentUserAccount.getClinicalDeptUID());
    }

    @Transactional(readOnly = true)
    public List<SimplePartsInventory> findByPartsItemIdAndPartsWarehouseId(String partsItemId, String partsWarehouseId) {
        return partsInventoryRepository.findByPartsItemIdAndPartsWarehouseId(partsItemId,partsWarehouseId);
    }

    @Transactional(readOnly = true)
    public List<SimplePartsInventory> findByPartsItemId(String partsItemId) {
        return partsInventoryRepository.findByPartsItemId(partsItemId);
    }

    @Transactional(readOnly = true)
    public boolean isVirtualLocatorExistsInInventory(String partsWarehouseId) {
        int count = partsInventoryRepository.countOfVirtualLocatorsByPartsWarehouseId(partsWarehouseId);
        if (count == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsInventory>
     * @throws
     * @params []
     * @author csh
     * @createTime 2020/6/8 14:53
     * @description 查找所有的备件库存信息
     */
    @Transactional(readOnly = true)
    public List<SimplePartsItem> findAllByName(String name) {
        List<SimplePartsItem> list = Lists.newArrayList();
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        List<SimplePartsWareHouseOrg> simplePartsWareHouseOrgList = simplePartsWarehouseOrgRepository.findAllByOrgUid(currentUserAccount.getSiteUID());
        Set<String> partsWarehouseIds = simplePartsWareHouseOrgList.stream().map(p->p.getPartsWarehouseId()).collect(Collectors.toSet());

        if (currentUserAccount.getRoleNames().contains("SparePartsAdmin")) {
            List<SimplePartsWareHouseUser> partsWareHouseUserList = partsWarehouseUserRepository.findAllByUserUid(currentUserAccount.getUid());
            Set<String> warehouseIdFromUser = partsWareHouseUserList.stream().
                    filter(item -> item.getPartsWarehouseId() != null)
                    .map(item -> item.getPartsWarehouseId()).collect(Collectors.toSet());
            partsWarehouseIds.addAll(warehouseIdFromUser);
        }
        List<SimplePartsWarehouse>  simplePartsWarehouseList = simplePartsWarehouseRepository.getByIds(currentUserAccount.getTenantUID(),partsWarehouseIds);

        if(simplePartsWarehouseList.size() == 0){
            logger.info("根据当前操作用户,未查询到关联的仓库. SiteUID : " +currentUserAccount.getOrgInfoUID());
            return list;
        }

        Set<String> partsWarehouseIds2 = simplePartsWarehouseList.stream().map(p->p.getId()).collect(Collectors.toSet());

        List<SimplePartsInventory> all = partsInventoryRepository.findByPartsWarehouseIdsAndName(partsWarehouseIds2, name);
        if(all.size() > 0){
            Set<String> uidSet = all.stream().filter(item -> item.getPartsItemId() !=null).map(item -> item.getPartsItemId()).collect(Collectors.toSet());
            list = this.findByIds(uidSet);
        }
        return list;
    }

}
