/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.ArrivalListRepository;
import com.ge.apm.dao.ContractInfoItemRepository;
import com.ge.apm.dao.ContractInfoRepository;
import com.ge.apm.domain.ArrivalList;
import com.ge.apm.domain.ArrivalListItem;
import com.ge.apm.domain.ContractInfo;
import com.ge.apm.domain.ContractInfoItem;
import com.ge.apm.domain.V2_BlobObject;
import java.util.List;
import java.util.stream.Collectors;
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

    public List<ContractInfoItem> getContractItems(String id) {
        return contractItemDao.findByContractId(id);
    }

    public List<ContractInfo> getContractListByTenant(String tenantUID) {
        return contractDao.findEquipmentContractByTenant(tenantUID);
    }

    public void contractItemArrival(List<ArrivalListItem> arrivalItemList) {
        if(null==arrivalItemList || arrivalItemList.isEmpty()){
            return ;
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
        if(null==contract){
            return false;
        }
        List<ContractInfoItem>  contractItems = getContractItems(contract.getId());
        List<ArrivalList> arrivalList =  arrivalDao.findByContractItemIdIn(contractItems.stream().map(ContractInfoItem::getId).collect(Collectors.toList()));
        if(arrivalList!=null && !arrivalList.isEmpty()){
            return false;
        }
        List<V2_BlobObject> blobList =  blobService.findBlobObjectsByBoUid(contract.getId());
        blobList.stream().forEach(item -> blobService.deleteBlobFile(item));
        
        contractItemDao.delete(contractItems);
        contractDao.delete(contract);
        
        return true;
    }

}
