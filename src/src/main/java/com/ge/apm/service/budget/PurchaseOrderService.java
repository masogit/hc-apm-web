package com.ge.apm.service.budget;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.BudgetApplicationMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.budget.BudgetApplication;
import com.ge.apm.domain.budget.BudgetApplicationAsset;
import com.ge.apm.domain.budget.BudgetAssetInfo;
import com.ge.apm.domain.budget.PurchaseOrderInfo;
import com.ge.apm.service.approval.MultiLevelApprovalService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.budget.BudgetConstant;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.core.WechatParam;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * LYJ
 * 2023.10.18
 */
@Service
public class PurchaseOrderService {
    private static Logger logger = LoggerFactory.getLogger(PurchaseOrderService.class);

    @Autowired
    private PurchaseOrderInfoRepository purchaseOrderInfoRepository;
    @Autowired
    private BudgetApplicationRepository budgetApplicationDao;
    @Autowired
    private BudgetApplicationAssetRepository budgetApplicationAssetDao;
    @Autowired
    private BudgetAssetInfoRepository budgetAssetInfoDao;
    @Autowired
    private UserAccountRepository userDao;
    @Autowired
    private MultiLevelApprovalRepository approvalDao;
    @Autowired
    private MultiLevelApprovalService approvalService;
    @Autowired
    private BudgetApplicationMapper budgetApplicationMapper;
    @Autowired
    private BlobFileService blobService;
    @Autowired
    private OrgInfoRepository orgDao;

    @Transactional(rollbackFor = Exception.class)
    public boolean savePurchase(PurchaseOrderInfo purchaseOrderInfo,List<BudgetApplicationAsset> budgetApplicationAssetList) {
        //先去掉之前的关联数据
        List<BudgetApplication> lastBudgetApplications = budgetApplicationDao.findByPurchaseIdAndIsDeleted(purchaseOrderInfo.getId(),0);
        if(CollectionUtils.isNotEmpty(lastBudgetApplications)){
            lastBudgetApplications.stream().forEach(budget -> {
                budget.setPurchaseId(null);
                budget.setProcureStatus(null);
            });
        }
        budgetApplicationDao.save(lastBudgetApplications);

        //保存采购单数据
        PurchaseOrderInfo purchaseOrderInfoPersistent = purchaseOrderInfoRepository.save(purchaseOrderInfo);

       //处理申购单数据
        List<String> budgetApplicationIds = budgetApplicationAssetList.stream().
                map(BudgetApplicationAsset::getApplicationId).collect(Collectors.toList());
        List<BudgetApplication> curBudgetApplications =  budgetApplicationDao.findByIdIn(budgetApplicationIds);
        if(CollectionUtils.isNotEmpty(curBudgetApplications)){
            curBudgetApplications.stream().forEach(budget -> {
                budget.setPurchaseId(purchaseOrderInfoPersistent.getId());
                budget.setProcureStatus(BudgetApplication.PROCURESTATUS.Purchased.toString());
            });
        }
        budgetApplicationDao.save(curBudgetApplications);
        return true;
    }


    public void handleCommonField(PurchaseOrderInfo purchaseOrderInfo, UserAccount account){
        purchaseOrderInfo.setLastModifiedBy(account.getLoginName());
        purchaseOrderInfo.setLastModifiedDate(new Date());

        purchaseOrderInfo.setTenantUID(account.getTenantUID());
        purchaseOrderInfo.setInstitutionUID(account.getInstitutionUID());
        purchaseOrderInfo.setHospitalUID(account.getHospitalUID());
        purchaseOrderInfo.setSiteUID(account.getSiteUID());

    }

    public void initCreateObject(PurchaseOrderInfo purchaseOrderInfo,UserAccount account){
        purchaseOrderInfo.setPurchaseNum(generateApplicationNum());
        purchaseOrderInfo.setPurchaseQuantity(0);
        purchaseOrderInfo.setTotalPrice(new BigDecimal(0));
        purchaseOrderInfo.setCreatedBy(account.getLoginName());
        purchaseOrderInfo.setCreatedDate(new Date());
        purchaseOrderInfo.setLastModifiedBy(account.getLoginName());
        purchaseOrderInfo.setLastModifiedDate(new Date());
    }

    private String generateApplicationNum() {
        LocalDateTime date = LocalDateTime.now(ZoneId.of("GMT+8"));
        return "CG".concat(date.format(DateTimeFormatter.ofPattern("yyMMddHHmmssSSS")));
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean deleteApplication(PurchaseOrderInfo purchaseOrderInfo) {
        purchaseOrderInfo.setIsDeleted(1);
        purchaseOrderInfoRepository.save(purchaseOrderInfo);
        List<BudgetApplication> budgetApplicationList =  budgetApplicationDao.findByPurchaseIdAndIsDeleted(purchaseOrderInfo.getId(),0);
        budgetApplicationList.stream().forEach(budget -> {
            budget.setPurchaseId(null);
            budget.setProcureStatus(null);
        });
        budgetApplicationDao.save(budgetApplicationList);
        return true;
    }

    /**
     * 回退列表
     *
     * @param
     * @return
     */
    public String backList() {
        return "List.xhtml?faces-redirect=true";
    }


}