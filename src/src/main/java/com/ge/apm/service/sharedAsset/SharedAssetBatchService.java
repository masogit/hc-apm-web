package com.ge.apm.service.sharedAsset;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.ge.apm.dao.WorkflowConfigRepository;
import com.ge.apm.dao.mapper.SharedAssetMapper;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SharedAssetBatchService {

    private static final Logger logger = LoggerFactory.getLogger(SharedAssetBatchService.class);

    @Autowired
    private SharedAssetMapper sharedAssetMapper;

    @Autowired
    private UaaService uaaService;

    @Autowired
    private WorkflowConfigRepository wfCfigDao;

    public void buildDefaultFilters(Wrapper<AssetInfo> ew) {
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
        ew.andNew().in("ai.tenant_uid", orgUidList)
            .or().in("ai.institution_uid", orgUidList)
            .or().in("ai.hospital_uid", orgUidList)
            .or().in("ai.site_uid", orgUidList)
            .or().in("clinical_dept_uid", orgUidList);
    }

    public AssetInfo findSharedAsset2Lease(Wrapper<AssetInfo> ew, String searchCriteria) {
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<String> allocationCenterUids = wfCfigDao.findDeptForSharedAsset(user.getSiteUID());

        ew.andNew().eq("ai.depart_num", searchCriteria)
            .or().eq("ai.financing_num", searchCriteria)
            .or().eq("ai.qr_code", searchCriteria);

        ew.andNew().in("ai.asset_dept_uid", allocationCenterUids);

        logger.info("ew: " + ew.getSqlSegment());
        AssetInfo ai = sharedAssetMapper.findSharedAssetByWrapper(ew);
        logger.info("ai: " + ai);
        return ai;
    }

    public AssetInfo findSharedAsset2Return(Wrapper<AssetInfo> ew, String searchCriteria) {
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<String> allocationCenterUids = wfCfigDao.findDeptForSharedAsset(user.getSiteUID());

        ew.andNew().eq("ai.depart_num", searchCriteria)
            .or().eq("ai.financing_num", searchCriteria)
            .or().eq("ai.qr_code", searchCriteria);

        ew.andNew().notIn("ai.asset_dept_uid", allocationCenterUids);

        logger.info("ew: " + ew.getSqlSegment());
        AssetInfo ai = sharedAssetMapper.findSharedAssetByWrapper(ew);
        logger.info("ai: " + ai);
        return ai;
    }

}
