package com.ge.apm.service.hospitalAdjustment;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.AdjustmentTransferApplicationRepository;
import com.ge.apm.dao.CustomRoleRepository;
import com.ge.apm.dao.IdleEquipmentStockRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AdjustmentTransferApplication;
import com.ge.apm.domain.CustomRole;
import com.ge.apm.domain.IdleEquipmentStock;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.remote.WorkFlowService;
import com.ge.healthcare.cn.common.cons.ApmFunction;
import com.ge.healthcare.cn.common.cons.OrderNumberKeys;
import com.ge.healthcare.cn.common.utils.date.DatePattern;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IdleEquipmentStockService {
    public static final Logger log = LoggerFactory.getLogger(IdleEquipmentStockService.class);

    @Autowired
    private WorkFlowService workFlowService;

    @Autowired
    private AdjustmentTransferApplicationRepository adjustmentTransferApplicationRepository;


    @Autowired
    private IdleEquipmentStockRepository dao;

    @Autowired
    private CustomRoleRepository customRoleRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

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

        List<UserAccount> approvalUser = getApprovalUser(currentUserAccount.getTenantUID(),Arrays.asList(ApmFunction.HOSPITAL_ADJUSTMENT_IDLE_EQUIPMENT_ADJUSTMENT_ACCEPTANCE));
        List<String> uids = approvalUser.stream().map(UserAccount::getUid).collect(Collectors.toList());
        String join = String.join(",", uids);
        application.setApplicationPendingApprovalUid(join);
        application.setApplicationPendingApprovalDate(new Date());
        String hospitalAdjustmentApp = workFlowService.getOrderNumber("ADJUSTMENT_BORROW_APP"+currentUserAccount.getTenantUID());
        if(hospitalAdjustmentApp==null){
            hospitalAdjustmentApp = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN);
        }
        application.setApplicationNumber(OrderNumberKeys.AL.name()+hospitalAdjustmentApp);
        dao.save(selected);
        adjustmentTransferApplicationRepository.save(application);

    }

    public List<UserAccount>  getApprovalUser(String tenantUid,List<ApmFunction> functions){
        List<CustomRole> customRoles = customRoleRepository.findByTenantUidAndFunctionListContains(tenantUid);
        List<Integer> tenantRoleIds = new ArrayList<>();

        for (CustomRole customRole : customRoles) {
            List<String> strings = JSON.parseArray(customRole.getFunctionList(), String.class);
            for (ApmFunction function : functions) {
                if(CollectionUtils.isNotEmpty(strings) && strings.contains(function.name())){
                    tenantRoleIds.add(customRole.getId());
                }
            }
        }
        if(CollectionUtils.isEmpty(tenantRoleIds)){
            log.info("租户没有配置自定义调剂角色tenantUid:{}",tenantUid);
            return new ArrayList<>();
        }
        List<Integer> userIds = userAccountRepository.findByCustomRoleId(tenantRoleIds);

        return userAccountRepository.getByIds(new HashSet<>(userIds));
    }
}
