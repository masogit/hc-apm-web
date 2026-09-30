/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ApmConstant;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import webapp.framework.web.WebUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 *
 * @author 212579464
 */
@Service
@Scope("prototype")
public class AssetFileImportUseCacheService extends AssetFileImportBaseService {
    private static final Logger logger = LoggerFactory.getLogger(AssetFileImportUseCacheService.class);

    @Autowired
    protected AssetFileImportSupportingService supportingService;

    @Transactional
    public void importAssetData(boolean crossSite, OrgInfo selectedOrg, List<Map<String, Object>> importAssetMap,
                                Boolean autoGenerateQrcode, Map<String, OrgInfo> orgNamesUriCache, AssetFileImportCache assetFileImportCache) {
        LocalDateTime startTime = LocalDateTime.now();

        int row = 0;

        try {
            if(Objects.equals(UserContextService.getCurrentUserAccount().getSiteId(), ApmConstant.IB_TENANT_ID)){
                //locate parent assets first for later use
                parentAssetsMap = supportingService.getParentAssets(importAssetMap);
                //first iteration: import parent asset
                for (Map<String, Object> item : importAssetMap) {
                    AssetInfo assetInfo = (AssetInfo)item.get("asset");
                    if(supportingService.isParentAssetForIBTenant(assetInfo)) {
                        importOneAsset(crossSite, selectedOrg, item, autoGenerateQrcode, orgNamesUriCache, importAssetMap, assetFileImportCache);
                    }
                }
                //second iteration: import child asset and other asset
                for(Map<String, Object> item : importAssetMap){
                    AssetInfo assetInfo = (AssetInfo)item.get("asset");
                    if(supportingService.isParentAssetForIBTenant(assetInfo)) {
                        continue;
                    }
                    if(supportingService.isChildAssetForIBTenant(assetInfo, parentAssetsMap)) {
                        supportingService.setParentAsset(assetInfo, parentAssetsMap);
                    }
                    importOneAsset(crossSite, selectedOrg, item, autoGenerateQrcode, orgNamesUriCache, importAssetMap, assetFileImportCache);
                }

            } else {
                for (Map<String, Object> item: importAssetMap) {
                    if (row % 100 == 0) {
                        logger.info("imported {} rows", row);
                    }

                    ++row;
                    importOneAsset(crossSite,selectedOrg, item, autoGenerateQrcode, orgNamesUriCache, importAssetMap, assetFileImportCache);
                }
            }

        } catch (Exception e) {
            logger.info("error occurred when importing the {} row", row);
            throw e;
        }

        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for importData: {} seconds", duration.getSeconds());
    }

    @Transactional
    protected void importOneAsset(boolean crossSite, OrgInfo selectedOrg, Map<String, Object> item, boolean autoGenerateQrcode, Map<String, OrgInfo> orgNamesUriCache, List<Map<String, Object>> importAssetMap,
                                  AssetFileImportCache assetFileImportCache) {

        Map<String, OrgInfo> allCilinicalOfSiteUidCache;
        Map<String, AssetInfo> departNumInAssetInfoCache;
        //Map<String, UserAccount> orgClinicalCache;
        Multimap<String, UserAccount> userAccountOwnerNameInfoCahce;
        Multimap<String, UserAccount> userAccountOwnerName2InfoCahce;
        Multimap<String, UserAccount> userAccountOwnerName3InfoCahce;
        Multimap<String, UserAccount> userAccountClinicalOwnerInfoCache;
        Multimap<String, UserAccount> userAccountPmOwnerInfoCache;
        Map<String, Supplier> supplierCache;
        Map<String, Supplier> manufacturerCache;
        Map<String, Supplier> maintainancerCache;

        allCilinicalOfSiteUidCache = assetFileImportCache.allCilinicalOfSiteUid;
        departNumInAssetInfoCache = assetFileImportCache.departNumInAssetInfoCache;
        //orgClinicalCache = assetFileImportCache.orgClinicalCache;
        userAccountOwnerNameInfoCahce = assetFileImportCache.userAccountOwnerNameInfoCahce;
        userAccountOwnerName2InfoCahce = assetFileImportCache.userAccountOwnerName2InfoCahce;
        userAccountOwnerName3InfoCahce = assetFileImportCache.userAccountOwnerName3InfoCahce;
        userAccountClinicalOwnerInfoCache = assetFileImportCache.userAccountClinicalOwnerInfoCache;
        userAccountPmOwnerInfoCache = assetFileImportCache.userAccountPmOwnerInfoCache;
        supplierCache = assetFileImportCache.supplierCache;
        manufacturerCache = assetFileImportCache.manufacturerCache;
        maintainancerCache = assetFileImportCache.maintainancerCache;


        AssetInfo newAsset = (AssetInfo) item.get("asset");
        Map<String, ExcelDocument.ValueWrapper> rowMap = (Map<String, ExcelDocument.ValueWrapper>) item.get("rowData");

        AssetRow.clearErrMessage(item);

        item.put("status", StringUtils.isEmpty(newAsset.getUid()) ? ImportStatus.Created : ImportStatus.Exist);

        OrgInfo siteOrg = selectedOrg;
        if (crossSite) {
            siteOrg = supportingService.findSiteOrgInAssetExcelRow(rowMap, orgNamesUriCache).getSiteOrg();
        }
        //check depart num to avoid duplicate when update
        boolean isDuplicate = false;

        if( null != departNumInAssetInfoCache.get(newAsset.getDepartNum())){
            isDuplicate = supportingService.isAssetDepartNumDuplicate(siteOrg.getUid(), newAsset);
        }
        //boolean isDuplicate = supportingService.isAssetDepartNumDuplicate(siteOrg.getUid(), newAsset);

        if (isDuplicate) {
            item.put("status", ImportStatus.Failure);
            AssetRow.addErrMessage(item, "错误: 设备编号重复");
            return;
        }

        String clinicalDeptName = newAsset.getClinicalDeptName();
        if (StringUtils.isEmpty(clinicalDeptName)) {
            if (StringUtils.isEmpty(newAsset.getUid())) {
                item.put("status", ImportStatus.Failure);
                AssetRow.addErrMessage(item, "错误: 科室为空");
                return;
            }

        } else {
            //OrgInfo clinicalDept = supportingService.findOrgOnly(siteOrg, clinicalDeptName);
            OrgInfo clinicalDept = allCilinicalOfSiteUidCache.get(clinicalDeptName);
            if (clinicalDept != null) {
                //new child clinical will reach here
                newAsset.setClinicalDeptId(clinicalDept.getId());
                newAsset.setClinicalDeptUID(clinicalDept.getUid());
            }
        }

        String assetDeptName = newAsset.getAssetDeptName();
        if (!StringUtils.isEmpty(assetDeptName)) {
            //OrgInfo assetDept = supportingService.findOrgOnly(siteOrg, assetDeptName);
            OrgInfo assetDept = allCilinicalOfSiteUidCache.get(assetDeptName);
            if (assetDept != null) {
                newAsset.setAssetDeptId(assetDept.getId());
                newAsset.setAssetDeptUID(assetDept.getUid());
            } else {
                newAsset.setAssetDeptName(null);
            }

        }
//        supportingService.setAssetDeptId(newAsset, siteOrg, item);

        Collection<UserAccount> userAccounts = null;
        //for AssetOwner
        if (newAsset.getAssetOwnerName() == null) {
            AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("AssetOwnerName") + "未设置");
        } else {

            //userAccounts = userDao.findByTenantUIDAndName(tenantUID, newAsset.getAssetOwnerName());
            userAccounts =  userAccountOwnerNameInfoCahce.get(newAsset.getAssetOwnerName());

            if (userAccounts.size() != 1) {
                AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("AssetOwnerName") + newAsset.getAssetOwnerName() + "无此用户/无法关联");
                newAsset.setAssetOwnerName(null);
            } else {
                newAsset.setAssetOwnerId(userAccounts.iterator().next().getId());
                newAsset.setAssetOwnerTel(userAccounts.iterator().next().getTelephone());
            }
        }

        //for AssetOwner2
        if (newAsset.getAssetOwnerName2() == null) {
            AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("AssetOwnerName2") + "未设置");
        } else {
            //userAccounts = userDao.findByTenantUIDAndName(tenantUID, newAsset.getAssetOwnerName2());
            userAccounts = userAccountOwnerName2InfoCahce.get(newAsset.getAssetOwnerName2());

            if (userAccounts.size() != 1) {
                AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("AssetOwnerName2") + newAsset.getAssetOwnerName2() + "无此用户/无法关联");
                newAsset.setAssetOwnerName2(null);
            } else {
                newAsset.setAssetOwnerId2(userAccounts.iterator().next().getId());
                newAsset.setAssetOwnerTel2(userAccounts.iterator().next().getTelephone());
            }

        }

        //for AssetOwner3
        if (newAsset.getAssetOwnerName3() == null) {
            AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("assetOwnerName3") + "未设置");
        } else {
            //userAccounts = userDao.findByTenantUIDAndName(tenantUID, newAsset.getAssetOwnerName3());
            userAccounts = userAccountOwnerName3InfoCahce.get(newAsset.getAssetOwnerName3());

            if (userAccounts.size() != 1) {
                AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("assetOwnerName3") + newAsset.getAssetOwnerName3() + "无此用户/无法关联");
                newAsset.setAssetOwnerName3(null);
            } else {
                newAsset.setAssetOwnerId3(userAccounts.iterator().next().getId());
                newAsset.setAssetOwnerTel3(userAccounts.iterator().next().getTelephone());
            }

        }

        //for ClinicalOwner
        if (newAsset.getClinicalOwnerName() == null) {
            AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("ClinicalOwnerName") + "未设置");
        } else {
            //userAccounts = userDao.findByTenantUIDAndName(tenantUID, newAsset.getClinicalOwnerName());

            userAccounts = userAccountClinicalOwnerInfoCache.get(newAsset.getClinicalOwnerName());

            if (userAccounts.size() != 1) {
                AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("ClinicalOwnerName") + newAsset.getClinicalOwnerName() + "无此用户/无法关联");
                newAsset.setClinicalOwnerName(null);
            } else {
                newAsset.setClinicalOwnerId(userAccounts.iterator().next().getId());
                newAsset.setClinicalOwnerTel(userAccounts.iterator().next().getTelephone());
            }

        }

        //for Pm OwnerName
        if (newAsset.getPmOwnerName() == null) {
            AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("pmOwnerName") + "未设置");
        } else {
            //userAccounts = userDao.findByTenantUIDAndName(tenantUID, newAsset.getPmOwnerName());
            userAccounts = userAccountPmOwnerInfoCache.get(newAsset.getPmOwnerName());

            if (userAccounts.size() != 1) {
                AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("pmOwnerName") + newAsset.getPmOwnerName() + "无此用户/无法关联");
                newAsset.setPmOwnerName(null);
            } else {
                newAsset.setPmOwnerId(userAccounts.iterator().next().getId());
                newAsset.setPmOwnerTel(userAccounts.iterator().next().getTelephone());
            }

        }

        Supplier supplier = null;
        //导入供应商，并修正ID
        if (!StringUtils.isEmpty(newAsset.getVendor())) {
            if(null == supplierCache.get(newAsset.getVendor())){
                supplier = supportingService.importSupplierIfNeeded(siteOrg, newAsset.getVendor(), newAsset.getVendorTel());
            }else {
                supplier = supplierCache.get(newAsset.getVendor());
            }
            newAsset.setSupplierId(supplier.getId());
            //supplier = supportingService.importSupplierIfNeeded(siteOrg, newAsset.getVendor(), newAsset.getVendorTel());
        } else {
            newAsset.setSupplierId(null);
        }

        ////导入维修商，并修正ID
        if (!StringUtils.isEmpty(newAsset.getMaitanance())) {
            if(null == maintainancerCache.get(newAsset.getMaitanance())){
                supplier = supportingService.importMaintainancerIfNeeded(siteOrg, newAsset.getMaitanance(), newAsset.getMaitananceTel());
            }else{
                supplier = maintainancerCache.get(newAsset.getMaitanance());
            }
            //supplier = supportingService.importMaintainancerIfNeeded(siteOrg, newAsset.getMaitanance(), newAsset.getMaitananceTel());
            newAsset.setMaitananceId(supplier.getId().toString());
        } else {
            newAsset.setMaitananceId(null);
        }

        ////导入制造商，并修正ID
        if (!StringUtils.isEmpty(newAsset.getManufacture())) {
            if(null == manufacturerCache.get(newAsset.getManufacture())){
                supplier = supportingService.importManufacturerIfNeeded(siteOrg, newAsset.getManufacture());
            }else {
                supplier = manufacturerCache.get(newAsset.getManufacture());
            }
            //supplier = supportingService.importManufacturerIfNeeded(siteOrg, newAsset.getManufacture());
            newAsset.setManufactureId(supplier.getId().toString());
        } else {
            newAsset.setManufactureId(null);
        }

        //auto generate financing num
        String financingNum = newAsset.getFinancingNum();
        String nationalStandardCode = newAsset.getNationalStandardCode();

        if (supportingService.needGenerateFinancingNum(workflowConfig, newAsset)) {

            //修改的时候不需要生成资产编号
            financingNum = assetFinacingNumGenerateService.autoGenerateFinacingNum(
                    null,
                    nationalStandardCode,
                    newAsset.getSiteUID(),
                    null,
                    financingNum
            );

            if (financingNum == null) {
                item.put("status", ImportStatus.Failure);
                AssetRow.addErrMessage(item, "取资产编码失败！请检查相应微服务");
                return;
            }
            else if (financingNum.startsWith("error:")) {
                item.put("status", ImportStatus.Failure);
                AssetRow.addErrMessage(item, financingNum);
                return;
            } else {
                newAsset.setFinancingNum(financingNum);
            }

        }

        supportingService.releaseOldQRCodeInUse(siteOrg, item);

        //create qr code if needed, make new qrcode in used.
        if (StringUtils.isEmpty(newAsset.getQrCode())) {
            if (autoGenerateQrcode) {
                QrCodeLib newCode = qrCodeService.createOneQrCode(siteOrg, 3);
                newAsset.setQrCode(newCode.getQrCode());
            }
        } else {

            boolean changed = supportingService.isQrCodeNewOrChanged(newAsset, item);

            if (changed) {
                QrCodeLib qrCodeLib = qrcodeDao.findByQrCode(newAsset.getQrCode(),newAsset.getSiteUID());
                if (qrCodeLib == null) {
                    qrCodeService.createSpecifiedQrCode(newAsset.getQrCode(), siteOrg, 3);

                } else {
                    if (qrCodeLib.getStatus() == 1) {//已发行
                        qrCodeLib.setStatus(3);//已建档
                        qrcodeDao.save(qrCodeLib);
                    } else {
                        item.put("status", ImportStatus.Failure);
                        AssetRow.addErrMessage(item, "错误: 该二维码已使用！");
                        return;
                    }

                }
            }

        }

        supportingService.removeParentAssetIfNotExists(newAsset);

        //if import status failure, already returned, will not reach here.
        assetDao.save(newAsset);
        assetDepreciationService.saveAssetDerpeciation(newAsset);

        supportingService.saveOrUpdateAssetTags(newAsset, rowMap);

        if (UserContextService.isMVSRole()) {
            if (newAsset.getMvsEquip() == null) {
                newAsset.setMvsEquip(1);
                assetDao.save(newAsset);
            }

            mvsUtil.copyToMvsTableIfMvsEquip(newAsset);

        }
    }

}
