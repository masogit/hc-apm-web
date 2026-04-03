package com.ge.apm.service.hospitalAdjustment;

import com.ge.apm.dao.IdleEquipmentApplicationRepository;
import com.ge.apm.dao.IdleEquipmentStockRepository;
import com.ge.apm.domain.IdleEquipmentApplication;
import com.ge.apm.domain.IdleEquipmentStock;
import com.ge.apm.domain.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class IdleEquipmentApplicationService {
    @Autowired
    private IdleEquipmentApplicationRepository dao;
    @Autowired
    private IdleEquipmentStockRepository stockRepository;

    @Transactional
    public void idleEquipmentApproval(IdleEquipmentApplication selected, UserAccount currentUserAccount){

        IdleEquipmentStock idleEquipmentStock = new IdleEquipmentStock();
        idleEquipmentStock.setStatus(1);
        idleEquipmentStock.setCreatedDate(new Date());
        idleEquipmentStock.setCreatedBy(currentUserAccount.getUid());
        idleEquipmentStock.setApplicationNumber(selected.getApplicationNumber());
        idleEquipmentStock.setApplicationUid(selected.getUid());
        idleEquipmentStock.setAssetUid(selected.getAssetUid());
        idleEquipmentStock.setStockSiteUid(selected.getStockSiteUid());
        idleEquipmentStock.setStockDeptUid(selected.getStockDeptUid());
        idleEquipmentStock.setStockHospitalUid(selected.getStockHospitalUid());
        idleEquipmentStock.setTenantUid(currentUserAccount.getTenantUID());
        stockRepository.save(idleEquipmentStock);
        dao.save(selected);
    }
}
