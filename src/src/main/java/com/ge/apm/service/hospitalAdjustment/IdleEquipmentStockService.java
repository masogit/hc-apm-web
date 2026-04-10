package com.ge.apm.service.hospitalAdjustment;

import com.ge.apm.dao.AdjustmentTransferApplicationRepository;
import com.ge.apm.dao.IdleEquipmentStockRepository;
import com.ge.apm.domain.AdjustmentTransferApplication;
import com.ge.apm.domain.IdleEquipmentStock;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.remote.WorkFlowService;
import com.ge.healthcare.cn.common.cons.OrderNumberKeys;
import com.ge.healthcare.cn.common.utils.date.DatePattern;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class IdleEquipmentStockService {

    @Autowired
    private WorkFlowService workFlowService;

    @Autowired
    private AdjustmentTransferApplicationRepository adjustmentTransferApplicationRepository;


    @Autowired
    private IdleEquipmentStockRepository dao;

    @Transactional
    public void applyForBorrowing(IdleEquipmentStock selected, UserAccount currentUserAccount, AdjustmentTransferApplication application){
        selected.setLastModifiedBy(currentUserAccount.getUid());
        selected.setLastModifiedDate(new Date());
        selected.setBorrowHospitalUid(currentUserAccount.getHospitalUID());
        selected.setBorrowSiteUid(currentUserAccount.getSiteUID());
        selected.setStatus(2);
        application.setStockUid(selected.getUid());
        application.setCreatedBy(currentUserAccount.getUid());
        application.setCreateTime(new Date());
        application.setUpdatedBy(currentUserAccount.getUid());
        application.setUpdateTime(new Date());
        application.setApplicationUid(currentUserAccount.getUid());
        application.setTenantUid(currentUserAccount.getTenantUID());
        application.setStockDeptUid(selected.getStockDeptUid());
        application.setStockSiteUid(selected.getStockSiteUid());
        application.setStockHospitalUid(selected.getStockHospitalUid());
        application.setBorrowDeptUid(selected.getBorrowDeptUid());
        application.setBorrowSiteUid(currentUserAccount.getSiteUID());
        application.setBorrowHospitalUid(currentUserAccount.getHospitalUID());
        application.setStatus(1);

        String hospitalAdjustmentApp = workFlowService.getOrderNumber("ADJUSTMENT_BORROW_APP"+currentUserAccount.getTenantUID());
        if(hospitalAdjustmentApp==null){
            hospitalAdjustmentApp = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN);
        }
        application.setApplicationNumber(OrderNumberKeys.AL.name()+hospitalAdjustmentApp);
        dao.save(selected);
        adjustmentTransferApplicationRepository.save(application);

    }
}
