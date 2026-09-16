package com.ge.apm.service.sharedasset;

import com.ge.healthcare.cn.common.mq.MqContext;
import com.ge.healthcare.cn.common.cons.ModifySourceEnum;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.SharedAssetReqDetailRepository;
import com.ge.apm.dao.SharedAssetReqTypeRepository;
import com.ge.apm.dao.SharedAssetRequestRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.SharedAssetReqDetail;
import com.ge.apm.domain.SharedAssetRequest;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SharedAssetRequestService {

    protected static final Logger logger = Logger.getLogger(SharedAssetRequestService.class);


    @Autowired
    private SharedAssetRequestRepository sharedAssetRequestRepository;
    @Autowired
    private AssetInfoRepository assetInfoRepository;
    @Autowired
    private SharedAssetReqTypeRepository sharedAssetReqTypeRepository;
    @Autowired
    private SharedAssetService sharedAssetService;
    @Autowired
    private SharedAssetReqDetailRepository asdDao;

    @Autowired
    private MqUtils mqUtils;


    @Transactional(rollbackFor = Exception.class)
    public void save(SharedAssetRequest selected) {
        sharedAssetRequestRepository.save(selected);
        assetInfoRepository.save(selected.getInfoList());
        sendMq(selected.getInfoList());
        sharedAssetReqTypeRepository.save(selected.getSartList());
        String voucherNum = getVoucherNum();
        for (SharedAssetReqDetail sard : selected.getSardList()) {
            logger.info("voucherNum :" + voucherNum + ", for sard:" + sard.getId());
            sard.setVoucherNum(voucherNum);
            voucherNum = String.format("%06d", Integer.parseInt(voucherNum) + 1);
        }
        asdDao.save(selected.getSardList());

    }
    public String getVoucherNum(){
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        String latestSeqInNumber =
                Optional.of(currentUserAccount.getTenantUID()).map(asdDao::findLatestVoucherNum).orElse("0");
        return String.format("%06d", Integer.parseInt(latestSeqInNumber) + 1);
    }
    public void sendMq(List<AssetInfo> assetInfos) {
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        MqContext mqContext=new MqContext();
        mqContext.setMethodName("sendMq");
        mqContext.setClassName("SharedAssetRequestService");
        mqContext.setServiceName("hc-apm-web");
        mqContext.setDescribe("设备调剂借用/归还交付变更设备");
        mqContext.setModifySource(ModifySourceEnum.CENTRAL_ALLOCATION.getCode());
        for (AssetInfo assetInfo:assetInfos){
            mqUtils.sendAssetToMQ(assetInfo,currentUserAccount,mqContext);
        }
    }
}
