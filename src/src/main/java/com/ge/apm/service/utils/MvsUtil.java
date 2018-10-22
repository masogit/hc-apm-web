/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.utils;

import com.ge.apm.dao.MvsAssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.MvsAssetInfo;
import com.ge.apm.domain.OrgInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Quan Tang
 */
@Component
public class MvsUtil {
    private static final Logger logger = LoggerFactory.getLogger(MvsUtil.class);

    @Autowired
    MvsAssetInfoRepository mvsAssetInfoDao;

    @Autowired
    OrgInfoRepository orgInfoDao;

    public void copyToMvsTable(AssetInfo assetInfo) {

        MvsAssetInfo foundExisting = mvsAssetInfoDao.findByAssetUid(assetInfo.getUid());

        if (foundExisting != null) return;

        MvsAssetInfo mvsAssetInfo = new MvsAssetInfo();

        //below is 17 fields copied
        mvsAssetInfo.setName(assetInfo.getName());
        mvsAssetInfo.setAliasName(assetInfo.getAliasName());

        //site org
        mvsAssetInfo.setSiteUID(assetInfo.getSiteUID());

        OrgInfo siteOrg = orgInfoDao.findByUid(assetInfo.getSiteUID());
        if (siteOrg == null) {
            logger.error("site org not found using site uid, {}", assetInfo.getSiteUID());
        } else {
            mvsAssetInfo.setSiteName(siteOrg.getName());
        }

        //asset dept org
        mvsAssetInfo.setAssetDeptUID(assetInfo.getAssetDeptUID());

        OrgInfo assetDeptOrg = orgInfoDao.findByUid(assetInfo.getAssetDeptUID());
        if (assetDeptOrg == null) {
            logger.error("asset dept org not found using asset dept uid, {}", assetInfo.getAssetDeptUID());
        } else {
            mvsAssetInfo.setAssetDeptName(assetDeptOrg.getName());
        }

        mvsAssetInfo.setManufacture(assetInfo.getManufacture());
        mvsAssetInfo.setMakerShortName(assetInfo.getMakerShortName());
        mvsAssetInfo.setAssetGroup(assetInfo.getAssetGroup());
        mvsAssetInfo.setRepairForm(assetInfo.getRepairForm());
        mvsAssetInfo.setFunctionType(assetInfo.getFunctionType());
        mvsAssetInfo.setSerialNum(assetInfo.getSerialNum());
        mvsAssetInfo.setSystemId(assetInfo.getSystemId());
        mvsAssetInfo.setFinancingNum(assetInfo.getFinancingNum());
        mvsAssetInfo.setFactoryWarrantyDate(assetInfo.getFactoryWarrantyDate());
        mvsAssetInfo.setWarrantyDate(assetInfo.getWarrantyDate());
        mvsAssetInfo.setQrCode(assetInfo.getQrCode());
        mvsAssetInfo.setComments(assetInfo.getComments());
        mvsAssetInfo.setAssetUid(assetInfo.getUid());

        mvsAssetInfoDao.save(mvsAssetInfo);
    }

}
