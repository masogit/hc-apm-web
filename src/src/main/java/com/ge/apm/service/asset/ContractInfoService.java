/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.ge.apm.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.domain.ArrivalList;
import com.ge.apm.domain.ArrivalListItem;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.ContractInfo;
import com.ge.apm.domain.ContractInfoItem;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.V2_PmOrder;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.domain.V2_WorkOrder_Detail;
import com.ge.apm.service.uaa.UserAccountService;
import com.google.common.base.Strings;

import javaslang.Tuple2;

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
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private AssetInfoRepository assetInfoDao;

    public List<ContractInfoItem> getContractItems(String id) {
        return contractItemDao.findByContractId(id);
    }
    
    public ContractInfo getContractInfo(String id) {
        return contractDao.findById(id);
    }

    public List<ContractInfo> getContractListByTenant(String tenantUID) {
        return contractDao.findEquipmentContractByTenant(tenantUID);
    }
    
    public List<ContractInfo> getContractListByUser(UserAccount user){
    	if(userAccountService.isSiteUser(user)){
    		return contractDao.findEquipmentContractBySite(user.getSiteUID());
    	}
    	else if(userAccountService.isHospitalUser(user)){
    		return contractDao.findEquipmentContractByHospital(user.getHospitalUID());
    	}
    	else if(userAccountService.isInstitutionUser(user)){
    		return contractDao.findEquipmentContractByInstitution(user.getInstitutionUID());
    	}
    	else{
    		return contractDao.findEquipmentContractByTenant(user.getTenantUID());
    	}
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
    public List<ContractInfo> getContractListByAssetUid(String assetUid) {
        AssetInfo asset = assetInfoDao.findByUid(assetUid);

        return getContractListByAsset(asset);

    }

    public List<ContractInfo> getContractListByAsset(AssetInfo asset) {
        List<V2_WorkOrder> woList = woDao.findByAssetId(asset.getId());
        List<V2_PmOrder> pmList = pmDao.getByAssetUid(asset.getUid());
        List<String> partsContractsNumList = woList.stream().flatMap(item -> item.getV2_workOrder_detail_list().stream()
                .filter(detailItem -> !Strings.isNullOrEmpty(detailItem.getContractNum()))
                .map(detailItem -> detailItem.getContractNum())).distinct().collect(Collectors.toList());
        partsContractsNumList.addAll(pmList.stream().flatMap(item -> item.getDetails().stream()
                .filter(detailItem -> !Strings.isNullOrEmpty(detailItem.getContractNum()))
                .map(detailItem -> detailItem.getContractNum())).distinct().collect(Collectors.toList()));
        List<ContractInfo> resList = new ArrayList();
        if(!partsContractsNumList.isEmpty()){
            resList.addAll(contractDao.findByContractNumIn(partsContractsNumList, asset.getTenantUID()));
        }
        resList.addAll(contractItemDao.getByAssetUidAndContractType(asset.getUid()).stream().map(item -> item.getContractInfo()).distinct().collect(Collectors.toList()));
        return resList;
    }

    public List<Tuple2<AssetInfo, V2_WorkOrder_Detail>> getAssetPartsInfo(String contractNum, String tenantUid) {
        
        if(Strings.isNullOrEmpty(contractNum)){
            return new ArrayList(); 
        }
        List<V2_WorkOrder> woList = partsDao.findWorkOrderByContractNum(contractNum, tenantUid);
        List<V2_PmOrder> pmList = partsDao.findPmOrderByContractNum(contractNum, tenantUid);

        List<Tuple2<AssetInfo, V2_WorkOrder_Detail>> res = new ArrayList();
        woList.stream().distinct().forEach(woItem -> {
            woItem.getV2_workOrder_detail_list().stream().filter(detailItem -> Objects.equals(detailItem.getContractNum(), contractNum))
                    .forEach(detailItem -> {
                        res.add(new Tuple2<AssetInfo, V2_WorkOrder_Detail>(woItem.getAssetInfo(), detailItem));
                    });
        });
        pmList.stream().distinct().forEach(pmItem -> {
            pmItem.getDetails().stream().filter(detailItem -> Objects.equals(detailItem.getContractNum(), contractNum))
                    .forEach(detailItem -> {
                        res.add(new Tuple2<AssetInfo, V2_WorkOrder_Detail>(pmItem.getAssetInfo(), detailItem));
                    });
        });

        return res;
    }

    @Transactional
    public void updateContractStatus(String id, String status) {
        int result = contractDao.updateContractStatus(id, status);
    }

}
