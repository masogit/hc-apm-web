/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.ArrivalListRepository;
import com.ge.apm.dao.ContractInfoItemRepository;
import com.ge.apm.dao.ContractInfoRepository;
import com.ge.apm.dao.V2_PmOrderRepository;
import com.ge.apm.dao.V2_WorkOrderDetailRepository;
import com.ge.apm.dao.V2_WorkOrderRepository;
import com.ge.apm.domain.ArrivalList;
import com.ge.apm.domain.ArrivalListItem;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.ContractInfo;
import com.ge.apm.domain.ContractInfoItem;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.V2_PmOrder;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.domain.V2_WorkOrder_Detail;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javaslang.Tuple2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author 212579464
 */
@Component
public class ContractInfoService {

    @Autowired
    private ContractInfoItemRepository contractItemDao;
    @Autowired
    private ContractInfoRepository contractDao;
    @Autowired
    private ArrivalListRepository arrivalDao;
    @Autowired
    private BlobFileService blobService;
    @Autowired
    private V2_WorkOrderDetailRepository partsDao;
    @Autowired
    private V2_WorkOrderRepository woDao;
    @Autowired
    private V2_PmOrderRepository pmDao;

    public List<ContractInfoItem> getContractItems(String id) {
        return contractItemDao.findByContractId(id);
    }

    public List<ContractInfo> getContractListByTenant(String tenantUID) {
        return contractDao.findEquipmentContractByTenant(tenantUID);
    }

    public void contractItemArrival(List<ArrivalListItem> arrivalItemList) {
        if (null == arrivalItemList || arrivalItemList.isEmpty()) {
            return;
        }
        String contractItemId = arrivalItemList.stream().findAny().get().getArrivalList().getContractItemId();
        ContractInfoItem currentContractItem = contractItemDao.findById(contractItemId);
        List<ContractInfoItem> itemList = arrivalItemList.stream().map(item -> {
            ContractInfoItem contractItem = new ContractInfoItem();
            contractItem.setArrivalItemId(item.getId());
            contractItem.setContractId(currentContractItem.getContractId());
            contractItem.setItemType(ContractInfoItem.ContractInfoItemType.ArrivalledItem.toString());
            contractItem.setPurchaseItemId(currentContractItem.getId());
            return contractItem;
        }).collect(Collectors.toList());
        contractItemDao.save(itemList);
    }

    public ContractInfoItem getContractItem(String contractItemId) {
        return contractItemDao.findById(contractItemId);
    }

    @Transactional
    public boolean deleteContract(ContractInfo contract) {
        if (null == contract) {
            return false;
        }
        List<ContractInfoItem> contractItems = getContractItems(contract.getId());
        List<ArrivalList> arrivalList = arrivalDao.findByContractItemIdIn(contractItems.stream().map(ContractInfoItem::getId).collect(Collectors.toList()));
        if (arrivalList != null && !arrivalList.isEmpty()) {
            return false;
        }
        List<V2_BlobObject> blobList = blobService.findBlobObjectsByBoUid(contract.getId());
        blobList.stream().forEach(item -> blobService.deleteBlobFile(item));

        contractItemDao.delete(contractItems);
        contractDao.delete(contract);

        return true;
    }

//    public List<V2_WorkOrder_Detail> getPartsInfo(String contractNum, String tenantUid) {
//
//        return partsDao.findByContractNum(contractNum, tenantUid);
//    }
//
//    public List<V2_WorkOrder_Detail> findByAssetUidAndContractNum(AssetInfo asset, String contractNum, String tenantUid) {
//
//        return partsDao.findByAssetUidAndContractNum(asset.getId(), asset.getUid(), contractNum, tenantUid);
//    }
    public List<ContractInfo> getContractListByAsset(AssetInfo asset) {
        List<V2_WorkOrder> woList = woDao.findByAssetId(asset.getId());
        List<V2_PmOrder> pmList = pmDao.getByAssetUid(asset.getUid());
        List<String> partsContractsNumList = woList.stream().flatMap(item -> item.getV2_workOrder_detail_list().stream()
                .map(detailItem -> detailItem.getContractNum())).distinct().collect(Collectors.toList());
        partsContractsNumList.addAll(pmList.stream().flatMap(item -> item.getDetails().stream()
                .map(detailItem -> detailItem.getContractNum())).distinct().collect(Collectors.toList()));
        List<ContractInfo> resList = contractDao.findByContractNumIn(partsContractsNumList, asset.getTenantUID());
        resList.addAll(contractItemDao.getByAssetUidAndContractType(asset.getUid()).stream().map(item -> item.getContractInfo()).distinct().collect(Collectors.toList()));
        return resList;
    }

    public List<Tuple2<AssetInfo, V2_WorkOrder_Detail>> getAssetPartsInfo(String contractNum, String tenantUid) {
        List<V2_WorkOrder> woList = partsDao.findWorkOrderByContractNum(contractNum, tenantUid);
        List<V2_PmOrder> pmList = partsDao.findPmOrderByContractNum(contractNum, tenantUid);

        List<Tuple2<AssetInfo, V2_WorkOrder_Detail>> res = new ArrayList();
        woList.stream().forEach(woItem -> {
            woItem.getV2_workOrder_detail_list().stream().filter(detailItem -> Objects.equals(detailItem.getContractNum(), contractNum))
                    .forEach(detailItem -> {
                        res.add(new Tuple2<AssetInfo, V2_WorkOrder_Detail>(woItem.getAssetInfo(), detailItem));
                    });
        });
        pmList.stream().forEach(pmItem -> {
            pmItem.getDetails().stream().filter(detailItem -> Objects.equals(detailItem.getContractNum(), contractNum))
                    .forEach(detailItem -> {
                        res.add(new Tuple2<AssetInfo, V2_WorkOrder_Detail>(pmItem.getAssetInfo(), detailItem));
                    });
        });

        return res;
    }

}
