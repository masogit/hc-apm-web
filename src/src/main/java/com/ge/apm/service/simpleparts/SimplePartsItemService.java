package com.ge.apm.service.simpleparts;

import com.ge.apm.dao.simpleparts.SimplePartsCategoryRepository;
import com.ge.apm.dao.simpleparts.SimplePartsItemRepository;
import com.ge.apm.domain.simpleparts.SimplePartsCategory;
import com.ge.apm.domain.simpleparts.SimplePartsInventory;
import com.ge.apm.domain.simpleparts.SimplePartsItem;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SimplePartsItemService {

    @Autowired
    private SimplePartsItemRepository partsItemRepository;

    @Autowired
    private SimplePartsCategoryRepository partsDao;

    @Autowired
    private SimplePartsInventoryService partsInventoryService;
    @Autowired
    private SimplePartsCategoryService partsCategoryService;

    @Transactional
    public void save(SimplePartsItem parts) {
        partsItemRepository.save(parts);
        List<SimplePartsInventory> list = partsInventoryService.findByPartsItemId(parts.getId());
        if(list.size() > 0){
            list.stream().forEach(item -> item.setName(parts.getName()));
            partsInventoryService.save(list);         
        }
    }

    @Transactional(readOnly = true)
    public TreeNode buildPartsCategoryTree(String tenantUid) {
        return partsCategoryService.buildPartsCategoryInTree(tenantUid);
    }

    @Transactional(readOnly = true)
    public List<SimplePartsItem> findAll() {
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        List<SimplePartsItem> partsItems = partsItemRepository.findByTenantUid(userAccount.getTenantUID());
        return partsItems;
    }

    @Transactional(readOnly = true)
    public List<SimplePartsItem> findByIds(Set<String> ids) {
        return partsItemRepository.findAll(ids);
    }

    @Transactional(readOnly = true)
    public List<SimplePartsItem> getPartsItemListInInventoryByPartsHouseId(String partsWarehouseId, String name) {
        List<SimplePartsInventory> partsInventoryList = partsInventoryService.findByWarehouseId(partsWarehouseId);
        List<SimplePartsItem> list = Lists.newArrayList();
        if(partsInventoryList.size() > 0){
            Set<String> uidSet = partsInventoryList.stream().filter(item -> item.getOnhandQty() > 0).map(item -> item.getPartsItemId()).collect(Collectors.toSet());
            if(StringUtils.isEmpty(name)){
                list = this.findByIds(uidSet);
            }else{
                list = partsItemRepository.findByUidAndName(uidSet, name);
            }
        }

        return list;
    }
    @Transactional(readOnly = true)
    public List<SimplePartsItem> getPartsItemList(String tenantUID) {
        List<SimplePartsItem> list = partsItemRepository.findByTenantUid(tenantUID);
        return list != null && list.size() > 0 ? list : Lists.newArrayList();
    }

    @Transactional
    public void saveSelectedPartsEdit(Set<String> partsItemIds,SimplePartsCategory partsCategory) {
        partsItemRepository.updatePartsItemPartsCategoryByIds(partsItemIds,partsCategory.getName(),partsCategory.getEncoded());
    }

    @Transactional(readOnly = true)
    public SimplePartsItem getPartsItem(String partsItemId,String parts) {
        if(partsItemId != null){
            return partsItemRepository.findById(partsItemId);
        }else {
            List<SimplePartsItem> list = partsItemRepository.findByName(parts);
            if(list != null && list.size()>0){
                return list.get(0);
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<SimplePartsItem> findAllByName(String name) {
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        List<SimplePartsItem> partsItems = partsItemRepository.findByTenantUidAndName(userAccount.getTenantUID(), name);
        return partsItems;
    }

}
