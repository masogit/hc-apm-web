package com.ge.apm.service.budget;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.budget.*;
import org.apache.commons.collections4.CollectionUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

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
    private PurchaseTemplateRepository purchaseTemplateRepository;
    @Autowired
    private BudgetApplicationExportService budgetApplicationExportService;
    @Value("#{systemEnvironment['GATEWAY_URL'] ?: urlProperties.defaultGateway}")
    private String baseURL;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");


    public String getBaseURL(){
        return baseURL;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean savePurchase(PurchaseOrderInfo purchaseOrderInfo,List<BudgetApplicationAsset> budgetApplicationAssetList,List<V2_BlobObject> attachList,List<String> templateIds) {
        //先去掉之前的关联数据
        if (!Optional.ofNullable(purchaseOrderInfo.getId()).isPresent()) { // 新增  数据状态和步骤为空
            this.generatePurchaseNum(purchaseOrderInfo);
        }else{
            //编辑才需要处理之前数据
            List<BudgetApplication> lastBudgetApplications = budgetApplicationDao.findByPurchaseIdAndIsDeletedAndOrderTypeIsNotNull(purchaseOrderInfo.getId(),0);
            if(CollectionUtils.isNotEmpty(lastBudgetApplications)){
                lastBudgetApplications.stream().forEach(budget -> {
                    setPurchaseInfoNull(budget);
                });
            }
            budgetApplicationDao.save(lastBudgetApplications);
        }

        //保存采购单数据
        purchaseOrderInfo.setApplicationAssetName(null);
        if(CollectionUtils.isNotEmpty(budgetApplicationAssetList)){
            purchaseOrderInfo.setApplicationAssetName(budgetApplicationAssetList.get(0).getApplicationAssetName());
        }
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

        //处理关联模板信息附件信息保存
        List<PurchaseTemplate> purchaseTemplateList = purchaseTemplateRepository.findByPurchaseId(purchaseOrderInfoPersistent.getId());
        purchaseTemplateRepository.delete(purchaseTemplateList);
        if(CollectionUtils.isNotEmpty(templateIds)){
            templateIds.stream().forEach(tempId -> {
                PurchaseTemplate purchaseTemplate = new PurchaseTemplate();
                purchaseTemplate.setTemplateId(tempId);
                purchaseTemplate.setPurchaseId(purchaseOrderInfoPersistent.getId());
                purchaseTemplateRepository.save(purchaseTemplate);
            });
        }
        return true;
    }

    public List<SearchFilter> initSearchFilter(List<SearchFilter> searchFilters) {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        } else {
            searchFilters.clear();
        }
        return searchFilters;
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
        purchaseOrderInfo.setOperator(account);
        purchaseOrderInfo.setOperatorId(account.getId());
        purchaseOrderInfo.setPurchaseQuantity(0);
        purchaseOrderInfo.setTotalPrice(new BigDecimal(0));
        purchaseOrderInfo.setCreatedBy(account.getLoginName());
        purchaseOrderInfo.setCreatedDate(new Date());
        purchaseOrderInfo.setLastModifiedBy(account.getLoginName());
        purchaseOrderInfo.setLastModifiedDate(new Date());
        purchaseOrderInfo.setStatus("NotEnabled");
    }

    /**
     * 给申请单添加单号和索引
     *
     * @param
     * @return
     */
    private PurchaseOrderInfo generatePurchaseNum(PurchaseOrderInfo purchaseOrderInfo) {
        Object o = purchaseOrderInfoRepository.getMaxIndex();
        int index = o == null ? 0 : Integer.parseInt(o.toString());
        index++;
        String purchaseNum = "PUR" + simpleDateFormat.format(new Date()) + String.format("%04d", index);
        purchaseOrderInfo.setPurchaseNum(purchaseNum);
        purchaseOrderInfo.setPurchaseNumIndex(index);
        return purchaseOrderInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteApplication(PurchaseOrderInfo purchaseOrderInfo) {
        purchaseOrderInfoRepository.delete(purchaseOrderInfo);
        List<BudgetApplication> budgetApplicationList =  budgetApplicationDao.findByPurchaseIdAndIsDeletedAndOrderTypeIsNotNull(purchaseOrderInfo.getId(),0);
        budgetApplicationList.stream().forEach(budget -> {
            setPurchaseInfoNull(budget);
        });
        budgetApplicationDao.save(budgetApplicationList);
        return true;
    }

    public void setPurchaseInfoNull(BudgetApplication budgetApplication){
        budgetApplication.setPurchaseId(null);
        budgetApplication.setProcureStatus(null);
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

    public boolean checkDistinct(List<BudgetApplicationAsset> curApplicationBudgetAssets,List<BudgetApplicationAsset> selectApplicationBudgetAssets){
        boolean arg = false;
        List<String> assetName = new ArrayList<String>();
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(curApplicationBudgetAssets)){
            List<String>  assetNameFirest = curApplicationBudgetAssets.stream().map(BudgetApplicationAsset::getApplicationAssetName).collect(Collectors.toList());
            assetName.addAll(assetNameFirest);
        }
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(selectApplicationBudgetAssets)){
            List<String>  assetNameSec = selectApplicationBudgetAssets.stream().map(BudgetApplicationAsset::getApplicationAssetName).collect(Collectors.toList());
            assetName.addAll(assetNameSec);
        }
        long num = assetName.stream().distinct().count();
        if (num != 1) {
            arg = true;
        }
        return arg;
    }

    public StreamedContent exportPurchaseZip(PurchaseOrderInfo purchaseOrderInfo) throws IOException {
        String base = "采购单" + purchaseOrderInfo.getPurchaseNum();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ZipOutputStream zout = new ZipOutputStream(bout);

        budgetApplicationExportService.generatePurchaseFile(zout, purchaseOrderInfo);
        budgetApplicationExportService.generateBudgetFiles(zout, purchaseOrderInfo);
        budgetApplicationExportService.generateSupplierFiles(zout, purchaseOrderInfo);

        zout.close();
        StreamedContent streamedContent = new DefaultStreamedContent((new ByteArrayInputStream(bout.toByteArray())), "application/zip", base+".zip");
        return streamedContent;
    }


}