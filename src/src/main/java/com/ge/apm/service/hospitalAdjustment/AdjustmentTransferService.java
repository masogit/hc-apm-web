package com.ge.apm.service.hospitalAdjustment;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.ExportService;
import com.ge.apm.service.approval.WxMessageService;
import com.ge.healthcare.cn.common.cons.ApmFunction;
import com.ge.healthcare.cn.common.core.message.MSGEvent;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;

import java.util.*;

@Service
public class AdjustmentTransferService {
    public static final Logger log = LoggerFactory.getLogger(AdjustmentTransferService.class);

    @Autowired
    private IdleEquipmentStockRepository stockRepository;
    @Autowired
    private AdjustmentTransferApplicationRepository dao;

    @Autowired
    private AssetInfoRepository assetInfoRepository;
    @Autowired
    private OrgInfoRepository orgInfoRepository;

    @Autowired
    private CustomRoleRepository customRoleRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private WxMessageService wxMessageService;

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




    public void approvalMsg(AdjustmentTransferApplication adjustmentTransferApplication){
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("orderCode", adjustmentTransferApplication.getApplicationNumber());
        stringMap.put("content", "设备调剂申请已通过，请确认信息后线下交接");
        stringMap.put("date", DateUtil.formatDateTime(new Date()));

        List<CustomRole> customRoles = customRoleRepository.findByTenantUidAndFunctionListContains(adjustmentTransferApplication.getTenantUid());
        List<Integer> tenantRoleIds = new ArrayList<>();

        for (CustomRole customRole : customRoles) {
            List<String> strings = JSON.parseArray(customRole.getFunctionList(), String.class);
            if(CollectionUtils.isNotEmpty(strings) && strings.contains(ApmFunction.HOSPITAL_ADJUSTMENT_WORKBENCH.name())){
                tenantRoleIds.add(customRole.getId());
            }
        }
        if(CollectionUtils.isEmpty(tenantRoleIds)){
            log.info("租户没有配置自定义调剂角色tenantUid:{}",adjustmentTransferApplication.getTenantUid());
        }
        List<Integer> userIds = userAccountRepository.findByCustomRoleId(tenantRoleIds);

        String stockHospitalUid = adjustmentTransferApplication.getStockHospitalUid();
        String borrowHospitalUid = adjustmentTransferApplication.getBorrowHospitalUid();


        List<String> byIdsAndHospitalUID = userAccountRepository.getByIdsAndHospitalUID(userIds, Arrays.asList(stockHospitalUid, borrowHospitalUid));
        wxMessageService.sendWxMsgToMq(stringMap,null,MSGEvent.HOSPITAL_ADJUSTMENT_BORROW_PENDING_REVIEW,byIdsAndHospitalUID,null);
    }

}
