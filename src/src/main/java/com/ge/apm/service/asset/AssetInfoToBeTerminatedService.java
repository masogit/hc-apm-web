package com.ge.apm.service.asset;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.ModifySourceEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.web.WebUtil;
import java.util.*;

@Service
public class AssetInfoToBeTerminatedService{

    private static final Logger logger = LoggerFactory.getLogger(AssetInfoToBeTerminatedService.class);

    private static final long serialVersionUID = -1L;
    @Autowired
    private AssetInfoRepository dao;
    @Autowired
    private AssetInfoMgrService aiMgrService;

    @Autowired
    private AssetHistoryService assetHistoryService;

    @Autowired
    private AssetLifecyleService assetLifecyleService;


    public void terminateAsset(AssetInfo assetInfo,UserAccount userAccount) {
        if (assetInfo == null) {
            return;
        }

        logger.info("Terminate: " + assetInfo.getUid());
//        if (validateTermination(assetInfo)) return;
        int originStatus = assetInfo.getStatus();
        AssetInfo oldAssetInfo = assetHistoryService.depCopy(assetInfo);
        assetInfo.setStatus(4);
        assetInfo.setIsValid(false);
        assetInfo.setTerminateDate(new Date());
        assetHistoryService.saveAssetHistoryIfUpdated(oldAssetInfo,assetInfo,ModifySourceEnum.ASSET_SCRAP.getCode());
        dao.save(assetInfo);
//        assetLifecyleService.saveLifecycle(originStatus,assetInfo,userAccount);
        aiMgrService.updateNurseSiteMaintain(assetInfo.getUid());
    }

    public boolean validateTermination(AssetInfo assetInfo) {
        if (assetInfo.getStatus() != 4) {
            WebUtil.addErrorMessage(String.format(WebUtil.getMessage("EquipmentCannotBeTerminated"), assetInfo.getName()));
            return true;
        }

        int workOrdersNum = dao.fetchCurrentWorkOrders(assetInfo.getId());
        if (workOrdersNum > 0) {
            WebUtil.addErrorMessage(String.format(WebUtil.getMessage("UnfinishedWorkorderCandBeTerminated"), assetInfo.getName()));
            return true;
        }

        if (aiMgrService.isInDispenceCenter(assetInfo)) {
            WebUtil.addErrorMessageWithParams(String.format(WebUtil.getMessage("SwappedInRentPleaseReturnItFirst"), assetInfo.getName(), assetInfo.getDepartNum()));
            return true;
        }
        return false;
    }

}
