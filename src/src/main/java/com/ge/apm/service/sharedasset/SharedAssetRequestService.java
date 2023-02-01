package com.ge.apm.service.sharedasset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.SharedAssetReqDetailRepository;
import com.ge.apm.dao.SharedAssetReqTypeRepository;
import com.ge.apm.dao.SharedAssetRequestRepository;
import com.ge.apm.domain.SharedAssetReqDetail;
import com.ge.apm.domain.SharedAssetRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    @Transactional(rollbackFor = Exception.class)
    public void save(SharedAssetRequest selected) {
        sharedAssetRequestRepository.save(selected);
        assetInfoRepository.save(selected.getInfoList());

        sharedAssetReqTypeRepository.save(selected.getSartList());

        String voucherNum = sharedAssetService.getVoucherNum();
        for (SharedAssetReqDetail sard : selected.getSardList()) {
            logger.info("voucherNum :" + voucherNum + ", for sard:" + sard.getId());
            sard.setVoucherNum(voucherNum);
            voucherNum = String.format("%06d", Integer.parseInt(voucherNum) + 1);
        }
        asdDao.save(selected.getSardList());

    }
}
