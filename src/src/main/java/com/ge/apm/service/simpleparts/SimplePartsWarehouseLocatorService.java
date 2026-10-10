package com.ge.apm.service.simpleparts;

import com.ge.apm.dao.simpleparts.SimplePartsInventoryRepository;
import com.ge.apm.dao.simpleparts.SimplePartsWarehouseLocatorRepository;
import com.ge.apm.domain.simpleparts.SimplePartsWareHouseLocator;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author: kwg
 * @date: 2020/7/13 13:51
 * @version: 1.0
 */
@Service
public class SimplePartsWarehouseLocatorService {

    @Autowired
    private SimplePartsWarehouseLocatorRepository partsWarehouseLocatorRepository;

    @Autowired
    private SimplePartsInventoryRepository partsInventoryRepository;

    @Transactional(readOnly = true)
    public List<SimplePartsWareHouseLocator> findAllByPartsWarehouseId(String id) {
        return partsWarehouseLocatorRepository.findAllByPartsWarehouseId(id);
    }

    /**
     * @param partsWarehouseId
     * @return void
     * @date 2020/7/27 17:06
     * 根据仓库id查询该仓库的仓位
     */
    @Transactional(readOnly = true)
    public List<SimplePartsWareHouseLocator> findByPartsWarehouseId(String partsWarehouseId) {
        return partsWarehouseLocatorRepository.findByPartsWarehouseId(partsWarehouseId);
    }

    @Transactional(readOnly = true)
    public List<SimplePartsWareHouseLocator> findByPartsWarehouseIdAndPartsItemId(String partsWarehouseId, String partsItemId) {
        List<SimplePartsWareHouseLocator> partsWareHouseLocators = findByPartsWarehouseId(partsWarehouseId);
        List<SimplePartsWareHouseLocator> result = Lists.newArrayList();
        if(partsItemId != null && partsWareHouseLocators.size()>0) {
            partsWareHouseLocators.forEach(partsWareHouseLocator -> {
                int size = partsInventoryRepository.findByPartsItemIdAndLocatorId(partsItemId,partsWareHouseLocator.getId()).size();
                if(size>0){
                    result.add(partsWareHouseLocator);
                }
            });
        } else {
            return partsWareHouseLocators;
        }
        return result;
    }

}
