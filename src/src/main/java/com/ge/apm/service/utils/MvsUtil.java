/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.utils;

import com.alibaba.druid.util.StringUtils;
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
            createNewMvsAssetInfo(assetInfo, updatedMvsAssetInfo);
        }

    }

    public void copyToMvsTableIfMvsEquip(AssetInfo assetInfo) {

        if (assetInfo == null) return;

        if (assetInfo.getMvsEquip() == null || assetInfo.getMvsEquip() != 1) return;

        MvsAssetInfo foundExisting = mvsAssetInfoDao.findById(assetInfo.getUid());

        if (foundExisting == null) {
            createNewMvsAssetInfo(assetInfo, null);
        }

    }

    private void createNewMvsAssetInfo(AssetInfo assetInfo, MvsAssetInfo mvsAssetInfo) {
        //below is 17 fields copied
        if (mvsAssetInfo == null) {
            mvsAssetInfo = new MvsAssetInfo();
        }

        if (StringUtils.isEmpty(mvsAssetInfo.getName())) {
            mvsAssetInfo.setName(assetInfo.getName());
        }

        if (StringUtils.isEmpty(mvsAssetInfo.getAliasName())) {
            mvsAssetInfo.setAliasName(assetInfo.getAliasName());
        }

        //site org
        if (StringUtils.isEmpty(mvsAssetInfo.getSiteName()) && !StringUtils.isEmpty(assetInfo.getSiteUID())) {
            OrgInfo siteOrg = orgInfoDao.findByUid(assetInfo.getSiteUID());
            if (siteOrg != null) {
                mvsAssetInfo.setSiteName(siteOrg.getName());
            }
        }

        //asset dept org
        if (StringUtils.isEmpty(mvsAssetInfo.getClinicalDeptName()) && !StringUtils.isEmpty(assetInfo.getClinicalDeptUID())) {
            OrgInfo clinicalDeptOrg = orgInfoDao.findByUid(assetInfo.getClinicalDeptUID());
            if (clinicalDeptOrg != null) {
                mvsAssetInfo.setClinicalDeptName(clinicalDeptOrg.getName());
            }
        }

        if (StringUtils.isEmpty(mvsAssetInfo.getManufacture())) {
            mvsAssetInfo.setManufacture(assetInfo.getManufacture());
        }
        if (StringUtils.isEmpty(mvsAssetInfo.getMakerShortName())) {
            mvsAssetInfo.setMakerShortName(assetInfo.getMakerShortName());
        }

        I18nMessage foundMsg;

        if (StringUtils.isEmpty(mvsAssetInfo.getAssetGroup()) && assetInfo.getAssetGroup() != null) {
            foundMsg = i18nMessageService.getByMsgTypeAndMsgKey("assetGroup", String.valueOf(assetInfo.getAssetGroup()));

            if (foundMsg != null) {
                mvsAssetInfo.setAssetGroup(foundMsg.getValueZh());
            }
        }

        if (StringUtils.isEmpty(mvsAssetInfo.getRepairForm()) && assetInfo.getRepairForm() != null) {
            foundMsg = i18nMessageService.getByMsgTypeAndMsgKey("repairForm", String.valueOf(assetInfo.getRepairForm()));

            if (foundMsg != null) {
                mvsAssetInfo.setRepairForm(foundMsg.getValueZh());
            }
        }

        if (StringUtils.isEmpty(mvsAssetInfo.getFunctionType())) {
            mvsAssetInfo.setFunctionType(assetInfo.getFunctionType());
        }

        if (StringUtils.isEmpty(mvsAssetInfo.getSerialNum())) {
            mvsAssetInfo.setSerialNum(assetInfo.getSerialNum());
        }
        if (StringUtils.isEmpty(mvsAssetInfo.getSystemId())) {
            mvsAssetInfo.setSystemId(assetInfo.getSystemId());
        }
        if (StringUtils.isEmpty(mvsAssetInfo.getFinancingNum())) {
            mvsAssetInfo.setFinancingNum(assetInfo.getFinancingNum());
        }
        if (mvsAssetInfo.getFactoryWarrantyDate() == null) {
            mvsAssetInfo.setFactoryWarrantyDate(assetInfo.getFactoryWarrantyDate());
        }
        if (mvsAssetInfo.getWarrantyDate() == null) {
            mvsAssetInfo.setWarrantyDate(assetInfo.getWarrantyDate());
        }
        if (StringUtils.isEmpty(mvsAssetInfo.getQrCode())) {
            mvsAssetInfo.setQrCode(assetInfo.getQrCode());
        }
        if (StringUtils.isEmpty(mvsAssetInfo.getComments())) {
            mvsAssetInfo.setComments(assetInfo.getComments());
        }
        if (StringUtils.isEmpty(mvsAssetInfo.getId())) {
            mvsAssetInfo.setId(assetInfo.getUid());
        }

        mvsAssetInfoDao.save(mvsAssetInfo);
    }

}
