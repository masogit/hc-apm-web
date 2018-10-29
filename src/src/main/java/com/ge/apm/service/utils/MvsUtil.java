/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.utils;

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.MvsAssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.I18nMessageService;
import com.ge.apm.view.sysutil.UserContextService;
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

    @Autowired
    I18nMessageRepository i18nMessageDao;

    @Autowired
    I18nMessageService i18nMessageService;

    public void copyToMvsTableIfMvsEquip(AssetInfo assetInfo, MvsAssetInfo updatedMvsAssetInfo) {

        if (assetInfo == null) return;

        if (assetInfo.getMvsEquip() == null || assetInfo.getMvsEquip() != 1) return;

        MvsAssetInfo foundExisting = mvsAssetInfoDao.findById(assetInfo.getUid());

        if (foundExisting != null) {

            if (UserContextService.isMVSAdmin() && updatedMvsAssetInfo.getId() != null) {
                //only mvs admin can update
                mvsAssetInfoDao.save(updatedMvsAssetInfo);
            }

        } else {
            createNewMvsAssetInfo(assetInfo);
        }

    }

    private void createNewMvsAssetInfo(AssetInfo assetInfo) {
        //below is 17 fields copied
        MvsAssetInfo mvsAssetInfo = new MvsAssetInfo();

        mvsAssetInfo.setName(assetInfo.getName());
        mvsAssetInfo.setAliasName(assetInfo.getAliasName());

        //site org
        OrgInfo siteOrg = orgInfoDao.findByUid(assetInfo.getSiteUID());
        if (siteOrg == null) {
            logger.error("site org not found using site uid, {}", assetInfo.getSiteUID());
        } else {
            mvsAssetInfo.setSiteName(siteOrg.getName());
        }


        //asset dept org
        OrgInfo assetDeptOrg = orgInfoDao.findByUid(assetInfo.getAssetDeptUID());
        if (assetDeptOrg == null) {
            logger.error("asset dept org not found using asset dept uid, {}", assetInfo.getAssetDeptUID());
        } else {
            mvsAssetInfo.setAssetDeptName(assetDeptOrg.getName());
        }

        mvsAssetInfo.setManufacture(assetInfo.getManufacture());
        mvsAssetInfo.setMakerShortName(assetInfo.getMakerShortName());

        I18nMessage foundMsg;
        if (assetInfo.getAssetGroup() != null) {
            foundMsg = i18nMessageService.getByMsgTypeAndMsgKey("assetGroup", String.valueOf(assetInfo.getAssetGroup()));

            if (foundMsg != null) {
                mvsAssetInfo.setAssetGroup(foundMsg.getValueZh());
            }
        }

        if (assetInfo.getRepairForm() != null) {
            foundMsg = i18nMessageService.getByMsgTypeAndMsgKey("repairForm", String.valueOf(assetInfo.getRepairForm()));

            if (foundMsg != null) {
                mvsAssetInfo.setRepairForm(foundMsg.getValueZh());
            }
        }

        mvsAssetInfo.setFunctionType(assetInfo.getFunctionType());
        mvsAssetInfo.setSerialNum(assetInfo.getSerialNum());
        mvsAssetInfo.setSystemId(assetInfo.getSystemId());
        mvsAssetInfo.setFinancingNum(assetInfo.getFinancingNum());
        mvsAssetInfo.setFactoryWarrantyDate(assetInfo.getFactoryWarrantyDate());
        mvsAssetInfo.setWarrantyDate(assetInfo.getWarrantyDate());
        mvsAssetInfo.setQrCode(assetInfo.getQrCode());
        mvsAssetInfo.setComments(assetInfo.getComments());
        mvsAssetInfo.setId(assetInfo.getUid());

        mvsAssetInfoDao.save(mvsAssetInfo);
    }

}
