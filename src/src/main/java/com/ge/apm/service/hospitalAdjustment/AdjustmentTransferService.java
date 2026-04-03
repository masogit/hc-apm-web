package com.ge.apm.service.hospitalAdjustment;

import com.ge.apm.dao.AdjustmentTransferApplicationRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.IdleEquipmentStockRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.*;
import com.ge.healthcare.cn.common.cons.ApmFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;

import java.util.Date;

@Service
public class AdjustmentTransferService {

    @Autowired
    private IdleEquipmentStockRepository stockRepository;
    @Autowired
    private AdjustmentTransferApplicationRepository dao;

    @Autowired
    private AssetInfoRepository assetInfoRepository;
    @Autowired
    private OrgInfoRepository orgInfoRepository;

    @Transactional
    public void cancel(AdjustmentTransferApplication selected, UserAccount currentUserAccount){
//        AdjustmentTransferApplication byUid = dao.findByUid(selected.getUid());
//        if(byUid.getStatus()!=1 && !selected.getApplicationUid().equals(currentUserAccount.getUid())){
//            WebUtil.addWarnMessage("无权操作,请检查订单状态");
//            return;
//        }
        selected.setStatus(3);
        selected.setUpdateTime(new Date());
        selected.setUpdatedBy(currentUserAccount.getUid());
        IdleEquipmentStock idleEquipmentStock = stockRepository.findByUid(selected.getStockUid());
        idleEquipmentStock.setBorrowHospitalUid(null);
        idleEquipmentStock.setBorrowSiteUid(null);
        idleEquipmentStock.setBorrowDeptUid(null);
        idleEquipmentStock.setStatus(1);
        idleEquipmentStock.setLastModifiedDate(new Date());
        idleEquipmentStock.setLastModifiedBy(currentUserAccount.getUid());
        stockRepository.save(idleEquipmentStock);
        dao.save(selected);
    }

    @Transactional
    public void   confirmReceipt(AdjustmentTransferApplication selected,UserAccount currentUserAccount) {
//        AdjustmentTransferApplication byUid = dao.findByUid(selected.getUid());
//        if(byUid.getStatus()!=4  && !selected.getApplicationUid().equals(currentUserAccount.getUid())){
//            WebUtil.addWarnMessage("无权操作,请检查订单状态");
//            return;
//        }

        selected.setStatus(5);
        selected.setUpdateTime(new Date());
        selected.setUpdatedBy(currentUserAccount.getUid());
        selected.setPendingDeliveryUid(currentUserAccount.getUid());
        selected.setPendingDeliveryDate(new Date());
        IdleEquipmentStock idleEquipmentStock = stockRepository.findByUid(selected.getStockUid());
        OrgInfo orgInfo = orgInfoRepository.getByUid(selected.getBorrowDeptUid());

        String assetUid = idleEquipmentStock.getAssetUid();
        AssetInfo assetInfo = assetInfoRepository.getByUid(assetUid);
        assetInfo.setClinicalDeptUID(selected.getBorrowDeptUid());
        assetInfo.setAssetDeptId(orgInfo.getId());
        assetInfo.setAssetDeptUID(orgInfo.getUid());
        assetInfo.setSiteUID(selected.getBorrowSiteUid());
        assetInfo.setHospitalUID(orgInfo.getHospitalUID());

        idleEquipmentStock.setStatus(3);
        idleEquipmentStock.setLastModifiedDate(new Date());
        idleEquipmentStock.setLastModifiedBy(currentUserAccount.getUid());
        stockRepository.save(idleEquipmentStock);
        assetInfoRepository.save(assetInfo);
        dao.save(selected);
    }


    @Transactional
    public void   returnPass(AdjustmentTransferApplication selected,UserAccount currentUserAccount) {
//        AdjustmentTransferApplication byUid = dao.findByUid(selected.getUid());
//
//        if(byUid.getStatus()!=7  ||  !userContextService.accessContainsFunctionMenu(ApmFunction.HOSPITAL_ADJUSTMENT_IDLE_EQUIPMENT_ADJUSTMENT_ACCEPTANCE.name())  ){
//            WebUtil.addWarnMessage("无权操作,请检查订单状态");
//            return;
//        }

        selected.setStatus(8);
        selected.setReturnApprovalDate(new Date());
        selected.setReturnApprovalUid(currentUserAccount.getUid());
        selected.setReturnApprovalResult(1);
        IdleEquipmentStock idleEquipmentStock = stockRepository.findByUid(selected.getStockUid());
        idleEquipmentStock.setStatus(4);
        idleEquipmentStock.setBorrowHospitalUid(null);
        idleEquipmentStock.setBorrowDeptUid(null);
        idleEquipmentStock.setBorrowSiteUid(null);
        idleEquipmentStock.setLastModifiedBy(currentUserAccount.getUid());
        idleEquipmentStock.setLastModifiedDate(new Date());
        OrgInfo orgInfo = orgInfoRepository.getByUid(selected.getStockDeptUid());
        AssetInfo assetInfo = assetInfoRepository.getByUid(idleEquipmentStock.getAssetUid());
        assetInfo.setClinicalDeptUID(selected.getStockDeptUid());
        assetInfo.setAssetDeptUID(selected.getStockDeptUid());
        assetInfo.setAssetDeptId(orgInfo.getId());
        assetInfo.setSiteUID(selected.getStockSiteUid());
        assetInfo.setHospitalUID(orgInfo.getHospitalUID());
        assetInfoRepository.save(assetInfo);
        stockRepository.save(idleEquipmentStock);
        dao.save(selected);
    }

    @Transactional

    public void   returnRefuse(AdjustmentTransferApplication selected,UserAccount currentUserAccount) {
//        AdjustmentTransferApplication byUid = dao.findByUid(selected.getUid());
//
//        if(byUid.getStatus()!=7  ||  !userContextService.accessContainsFunctionMenu(ApmFunction.HOSPITAL_ADJUSTMENT_IDLE_EQUIPMENT_ADJUSTMENT_ACCEPTANCE.name())  ){
//            WebUtil.addWarnMessage("无权操作,请检查订单状态");
//            return;
//        }
        selected.setStatus(9);
        selected.setReturnDate(new Date());
        selected.setReturnUid(currentUserAccount.getUid());
        selected.setReturnApprovalDate(new Date());
        selected.setReturnApprovalUid(currentUserAccount.getUid());
        selected.setReturnApprovalResult(2);
        IdleEquipmentStock idleEquipmentStock = stockRepository.findByUid(selected.getStockUid());
        idleEquipmentStock.setStatus(1);
        idleEquipmentStock.setBorrowHospitalUid(null);
        idleEquipmentStock.setBorrowDeptUid(null);
        idleEquipmentStock.setBorrowSiteUid(null);
        idleEquipmentStock.setLastModifiedBy(currentUserAccount.getUid());
        idleEquipmentStock.setLastModifiedDate(new Date());
        OrgInfo orgInfo = orgInfoRepository.getByUid(selected.getStockDeptUid());
        AssetInfo assetInfo = assetInfoRepository.getByUid(idleEquipmentStock.getAssetUid());
        assetInfo.setClinicalDeptUID(selected.getStockDeptUid());
        assetInfo.setAssetDeptUID(selected.getStockDeptUid());
        assetInfo.setAssetDeptId(orgInfo.getId());
        assetInfo.setSiteUID(selected.getStockSiteUid());
        assetInfo.setHospitalUID(orgInfo.getHospitalUID());
        assetInfoRepository.save(assetInfo);
        stockRepository.save(idleEquipmentStock);
        dao.save(selected);
    }

}
