/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.ServiceRequestRepository;
import com.ge.apm.dao.V2_PmOrderRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.V2_ServiceRequest;
import com.ge.apm.domain.V2_PmOrder;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@Component
public class AssetInfoMgrService {
    
    @Autowired
    private AssetInfoRepository assetDao;
    @Autowired
    private ServiceRequestRepository srDao;
    @Autowired
    private V2_PmOrderRepository pmDao;
    @Autowired
    private QrCodeService qrCodeService;
    
    public void deleteAsset(AssetInfo asset) {
        List<V2_ServiceRequest> srlist = srDao.findByAssetId(asset.getId());
        if (srlist != null && !srlist.isEmpty()) {
            WebUtil.addErrorMessage("该设备有工单存在,不能删除");
            return;
        }
        List<SearchFilter> pmFilter = Arrays.asList(new SearchFilter("assetUid", SearchFilter.Operator.EQ, asset.getUid()),
                new SearchFilter("isFinished", SearchFilter.Operator.EQ, false));
        List<V2_PmOrder> pmList = pmDao.findBySearchFilter(pmFilter);
        if (pmList != null && !pmList.isEmpty()) {
            WebUtil.addErrorMessage("该设备有保养工单存在,不能删除");
            return;
        }

        if (asset.getQrCode() != null) {
            qrCodeService.updateQrCodeStatus(asset.getQrCode(), asset.getSiteUID(), 1);
        }
        asset.setIsDeleted(true);
        asset.setIsValid(false);
        asset.setQrCode(null);
        assetDao.save(asset);
    }
}
