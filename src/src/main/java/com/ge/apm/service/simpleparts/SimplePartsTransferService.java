package com.ge.apm.service.simpleparts;

import com.ge.apm.dao.simpleparts.SimplePartsInventoryRepository;
import com.ge.apm.dao.simpleparts.SimplePartsItemRepository;
import com.ge.apm.dao.simpleparts.SimplePartsTransactionRepository;
import com.ge.apm.domain.simpleparts.SimplePartsInventory;
import com.ge.apm.domain.simpleparts.SimplePartsItem;
import com.ge.apm.domain.simpleparts.SimplePartsTransaction;
import com.ge.apm.domain.pure.SimplePartsItemValue;
import com.ge.apm.service.constant.SimplePartsConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

import static com.ge.apm.service.constant.SimplePartsConstant.TRANSACTION_TYPE_IN_STOCK;
import static com.ge.apm.service.constant.SimplePartsConstant.TRANSACTION_TYPE_OUT_STOCK;

@Service
public class SimplePartsTransferService {

    @Autowired
    private SimplePartsInventoryRepository partsInventoryRepository;

    @Autowired
    private SimplePartsTransactionRepository partsTransactionRepository;

    @Autowired
    private SimplePartsItemRepository partsItemRepository;

    @Autowired
    private SimplePartsRequestService partsRequestService;

    private SimplePartsItemValue partsItemValue;

    private BigDecimal totalPrice;

    @Transactional
    public void save(String sourcePartsWarehouseId, String sourcePartsWarehouseLocatorId, String targetPartsWarehouseId, String targetPartsWarehouseLocatorId, String sourcePartsItemId, int sourcePartsItemNum){

        SimplePartsInventory inventory = partsInventoryRepository.findByWarehouseIdAndLocatorIdAndPartsItemIdAndPartsItemId(sourcePartsWarehouseId, sourcePartsWarehouseLocatorId, sourcePartsItemId);
        int remainder = inventory.getOnhandQty() - sourcePartsItemNum;
        if (remainder == 0) {
            partsInventoryRepository.deleteByWarehouseIdAndLocatorIdAndPartsItemId(inventory.getPartsWarehouseId(), inventory.getPartsLocatorId(), inventory.getPartsItemId());
        } else {
            inventory.setOnhandQty(inventory.getOnhandQty() - sourcePartsItemNum );
            partsInventoryRepository.save(inventory);
        }

        SimplePartsInventory targetPartsInventory = partsInventoryRepository.findByPartsItemIdAndPartsWarehouseIdAndPartsLocatorId(sourcePartsItemId, targetPartsWarehouseId, targetPartsWarehouseLocatorId);
        if(targetPartsInventory != null){
            targetPartsInventory.setOnhandQty(targetPartsInventory.getOnhandQty() + sourcePartsItemNum);
            partsInventoryRepository.save(targetPartsInventory);
        }else {
            SimplePartsInventory targetInventory = new SimplePartsInventory();
            targetInventory.setOnhandQty(sourcePartsItemNum);
            targetInventory.setPartsWarehouseId(targetPartsWarehouseId);
            targetInventory.setPartsLocatorId(targetPartsWarehouseLocatorId);
            targetInventory.setName(inventory.getName());
            targetInventory.setPartsItemId(inventory.getPartsItemId());
            targetInventory.setTenantUID(inventory.getTenantUID());
            targetInventory.setInstitutionUID(inventory.getInstitutionUID());
            targetInventory.setSiteUID(inventory.getSiteUID());
            targetInventory.setHospitalUID(inventory.getHospitalUID());
            partsInventoryRepository.save(targetInventory);
        }


        SimplePartsItem partsItem = partsItemRepository.findById(sourcePartsItemId);
        partsItemValue = partsRequestService.getInventoryNumAndAvgPriceByPartsItem(sourcePartsItemId,sourcePartsWarehouseId).get();
        BigDecimal estimatedUnitPrice = partsItemValue.getEstimatedUnitPrice();
        BigDecimal partsItemNum = new BigDecimal(sourcePartsItemNum);
        totalPrice = partsItemNum.multiply(estimatedUnitPrice);

        SimplePartsTransaction partsTransactionOutStock = new SimplePartsTransaction();
        partsTransactionOutStock.setRequestType(SimplePartsConstant.PARTS_TRANSFER_FORM);
        partsTransactionOutStock.setPartsWarehouseId(sourcePartsWarehouseId);
        partsTransactionOutStock.setPartsLocatorId(sourcePartsWarehouseLocatorId);
        partsTransactionOutStock.setTransactionType(TRANSACTION_TYPE_OUT_STOCK);
        partsTransactionOutStock.setTransactionQty(sourcePartsItemNum);
        partsTransactionOutStock.setName(inventory.getName());
        partsTransactionOutStock.setCreatedDate(new Date());
        partsTransactionOutStock.setSourcSupplier(partsItem.getSupplier());
        partsTransactionOutStock.setSiteUID(inventory.getSiteUID());
        partsTransactionOutStock.setUnitPrice(partsItemValue.getEstimatedUnitPrice());
        partsTransactionOutStock.setTotalPrice(totalPrice);
        partsTransactionRepository.save(partsTransactionOutStock);

        SimplePartsTransaction partsTransactionInStock = new SimplePartsTransaction();
        partsTransactionInStock.setRequestType(SimplePartsConstant.PARTS_TRANSFER_FORM);
        partsTransactionInStock.setPartsWarehouseId(targetPartsWarehouseId);
        partsTransactionInStock.setPartsLocatorId(targetPartsWarehouseLocatorId);
        partsTransactionInStock.setTransactionType(TRANSACTION_TYPE_IN_STOCK);
        partsTransactionInStock.setName(inventory.getName());
        partsTransactionInStock.setTransactionQty(sourcePartsItemNum);
        partsTransactionInStock.setCreatedDate(new Date());
        partsTransactionInStock.setSourcSupplier(partsItem.getSupplier());
        partsTransactionInStock.setSiteUID(inventory.getSiteUID());
        partsTransactionInStock.setUnitPrice(partsItemValue.getEstimatedUnitPrice());
        partsTransactionInStock.setTotalPrice(totalPrice);
        partsTransactionRepository.save(partsTransactionInStock);
    }
}
