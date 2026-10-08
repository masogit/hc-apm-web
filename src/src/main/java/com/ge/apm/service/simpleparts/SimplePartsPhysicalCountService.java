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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SimplePartsPhysicalCountService {

    @Autowired
    private SimplePartsInventoryRepository partsInventoryRepository;

    @Autowired
    private SimplePartsTransactionRepository partsTransactionRepository;

    @Autowired
    private SimplePartsItemRepository partsItemRepository;

    @Autowired
    private SimplePartsRequestService partsRequestService;

    @Transactional
    public void save(List<SimplePartsInventory> partsInventoryList) {
        List<SimplePartsInventory> updateInventoryList = new ArrayList<>();

        List<SimplePartsTransaction> outStockPartsTransactionList = new ArrayList<>();
        List<SimplePartsTransaction> inStockPartsTransactionList = new ArrayList<>();

        for(SimplePartsInventory partsInventory : partsInventoryList ){
            int isOutStock = partsInventory.getOnhandQty() - partsInventory.getPhysicalCount();

            SimplePartsItem partsItem = partsItemRepository.findById(partsInventory.getPartsItemId().trim());
            SimplePartsItemValue partsItemValue = partsRequestService.getInventoryNumAndAvgPriceByPartsItem(partsInventory.getPartsItemId(),partsInventory.getPartsWarehouseId()).get();
            BigDecimal estimatedUnitPrice = partsItemValue.getEstimatedUnitPrice();
            BigDecimal partsItemNum = new BigDecimal(Math.abs(isOutStock));
            BigDecimal totalPrice = partsItemNum.multiply(estimatedUnitPrice);

            if(isOutStock == 0){
                //no change, continue next
                continue;
            }

            if(isOutStock > 0){
                partsInventory.setOnhandQty(partsInventory.getPhysicalCount());

                SimplePartsTransaction partsTransactionOutStock = new SimplePartsTransaction();
                partsTransactionOutStock.setRequestType(SimplePartsConstant.PARTS_PHYSICAL_COUNT_FORM);
                partsTransactionOutStock.setPartsWarehouseId(partsInventory.getPartsWarehouseId());
                partsTransactionOutStock.setPartsLocatorId(partsInventory.getPartsLocatorId());
                partsTransactionOutStock.setTransactionType(SimplePartsConstant.TRANSACTION_TYPE_OUT_STOCK);
                partsTransactionOutStock.setTransactionQty(isOutStock);
                partsTransactionOutStock.setName(partsInventory.getName());
                partsTransactionOutStock.setCreatedDate(new Date());
                partsTransactionOutStock.setSourcSupplier(partsItem.getSupplier());
                partsTransactionOutStock.setSiteUID(partsInventory.getSiteUID());
                partsTransactionOutStock.setUnitPrice(partsItemValue.getEstimatedUnitPrice());
                partsTransactionOutStock.setTotalPrice(totalPrice);
                outStockPartsTransactionList.add(partsTransactionOutStock);

            }else if(isOutStock < 0){
                partsInventory.setOnhandQty(partsInventory.getPhysicalCount());

                SimplePartsTransaction partsTransactionInStock = new SimplePartsTransaction();
                partsTransactionInStock.setRequestType(SimplePartsConstant.PARTS_PHYSICAL_COUNT_FORM);
                partsTransactionInStock.setPartsWarehouseId(partsInventory.getPartsWarehouseId());
                partsTransactionInStock.setPartsLocatorId(partsInventory.getPartsLocatorId());
                partsTransactionInStock.setTransactionType(SimplePartsConstant.TRANSACTION_TYPE_IN_STOCK);
                partsTransactionInStock.setTransactionQty(Math.abs(isOutStock));
                partsTransactionInStock.setName(partsInventory.getName());
                partsTransactionInStock.setCreatedDate(new Date());
                partsTransactionInStock.setSourcSupplier(partsItem.getSupplier());
                partsTransactionInStock.setSiteUID(partsInventory.getSiteUID());
                partsTransactionInStock.setUnitPrice(partsItemValue.getEstimatedUnitPrice());
                partsTransactionInStock.setTotalPrice(totalPrice);
                inStockPartsTransactionList.add(partsTransactionInStock);
            }

            if (partsInventory.getPhysicalCount() == 0) {
                //if not in stock any more, need to delete the record.
                partsInventoryRepository.deleteByWarehouseIdAndLocatorIdAndPartsItemId(partsInventory.getPartsWarehouseId(), partsInventory.getPartsLocatorId(), partsInventory.getPartsItemId());
            } else {
                updateInventoryList.add(partsInventory);
            }
        }

        partsInventoryRepository.save(updateInventoryList);
        partsTransactionRepository.save(outStockPartsTransactionList);
        partsTransactionRepository.save(inStockPartsTransactionList);
    }
}
