/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.aop.SystemEventAuditAopAspect;
import com.ge.apm.domain.*;
import com.ge.healthcare.cn.common.cons.SystemEventAuditEnum;
import com.ge.healthcare.cn.common.mq.MqContext;
import com.ge.healthcare.cn.common.mq.RabbitConfig;
import com.ge.apm.dao.*;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.service.uaa.UserAccountService;
import com.google.common.base.Strings;

import javaslang.Tuple2;

/**
 *
 * @author 212579464
 */
@Component
public class ContractInfoService {
    public static final Logger log = LoggerFactory.getLogger(ContractInfoService.class);

    @Autowired
    private ContractInfoItemRepository contractItemDao;
    @Autowired
    private ContractInfoRepository contractDao;
    @Autowired
    private ArrivalListRepository arrivalDao;
    @Autowired
    private ArrivalListItemRepository arrivalListItemDao;
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
    @Autowired
    private SystemEventAuditAopAspect systemEventAuditAopAspect;

    @Autowired
    private MqUtils mqUtils;

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
        String contractItemId = "";
        if(arrivalItemList.stream().findAny().isPresent()){
            contractItemId = arrivalItemList.stream().findAny().get().getArrivalList().getContractItemId();
        }
        if (ObjectUtil.isEmpty(contractItemId)) {
            return;
        }
        ContractInfoItem currentContractItem = contractItemDao.findById(contractItemId);
        List<ContractInfoItem> itemList = arrivalItemList.stream().map(item -> {
            ContractInfoItem contractItem = new ContractInfoItem();
            contractItem.setArrivalItemId(item.getId());
            contractItem.setContractId(currentContractItem.getContractId());
            contractItem.setItemType(ContractInfoItem.ContractInfoItemType.ArrivalledItem.toString());
            contractItem.setPurchaseItemId(currentContractItem.getId());
            contractItem.setParentId(currentContractItem.getParentId());
            return contractItem;
        }).collect(Collectors.toList());
        contractItemDao.save(itemList);
        arrivalItemList.forEach(t -> t.setWarrantyDate(currentContractItem.getContractInfo().getWarrantyEndDate()));
        arrivalListItemDao.save(arrivalItemList);
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
        sendMq(contract,"contract_info");
        sendMq(contractItems,"contract_info_item");
        return true;
    }

    public void sendItemMq(ContractInfoItem item, SystemEventAuditEnum systemEventAuditEnum) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("assetUid", item.getAssetUid());
        jsonObject.put("contractId", item.getContractId());
        jsonObject.put("ItemType", ContractInfoItem.ContractInfoItemType.ExistAsset.toString());
        SystemEventAudit systemEventAudit = systemEventAuditAopAspect.buildEventAudit(UserContextService.getCurrentUserAccount(), systemEventAuditEnum, item.getId(), Boolean.TRUE, "","contract_info_item", JSONObject.toJSONString(jsonObject));
        systemEventAuditAopAspect.sendSystemAuditMessage(systemEventAudit);
    }

    private void sendMq(Object object,String objectType) {
        try {
            MqContext mqContext = new MqContext();
            mqContext.setClassName("ContractInfoController");
            mqContext.setServiceName("hc-apm-web");
            mqContext.setMethodName("deleteContract");
            mqContext.setDescribe("合同列表删除合同");

            mqContext.setTime(new Date());
            mqContext.setExChange(RabbitConfig.PHYSICAL_DATA_DEL_CHANGE);
            mqContext.setRoutingKey("");
            mqContext.setType(RabbitConfig.PHYSICALDATADELETE);
            UserAccount userAccount = UserContextService.getCurrentUserAccount();
            mqUtils.sendPhysicalDataDel(object,objectType,userAccount,mqContext);
        }catch (Exception e){
            log.error("contract delete send mq error");
        }
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

    @Transactional
    public void removeContractInfoItem(ContractInfoItem item){

        List<ContractInfoItem> tempContractInfoItemList = contractItemDao.findByParentIdAndContractId(item.getId(), item.getContractId());
        for (ContractInfoItem contractInfoItem : tempContractInfoItemList) {
            contractInfoItem.setParentId(null);
            contractItemDao.save(contractInfoItem);
        }

        contractItemDao.delete(item);
        sendMq(item,"contract_info_item");

    }

}
