package com.ge.apm.service.sharedasset;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.ge.apm.dao.WorkflowConfigRepository;
import com.ge.apm.dao.mapper.SharedAssetMapper;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.SharedAssetReqDetail;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.sysutil.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Nick Liu
 * @since 2018.10.2 release
 */
@Service
public class SharedAssetBatchService {

    private static final Logger logger = LoggerFactory.getLogger(SharedAssetBatchService.class);

    @Autowired
    private SharedAssetMapper sharedAssetMapper;

    @Autowired
    private UaaService uaaService;

    @Autowired
    private WorkflowConfigRepository wfCfigDao;

    @Autowired
    private SharedAssetService sharedAssetService;

    public void buildDefaultFilters(Wrapper<AssetInfo> ew) {
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
        ew.andNew().in("ai.tenant_uid", orgUidList)
            .or().in("ai.institution_uid", orgUidList)
            .or().in("ai.hospital_uid", orgUidList)
            .or().in("ai.site_uid", orgUidList)
            .or().in("clinical_dept_uid", orgUidList);
        ew.andNew()
            .eq("ai.is_deleted", false)
            .and().eq("ai.is_valid", true);
    }

    public AssetInfo findSharedAsset2Lease(Wrapper<AssetInfo> ew, String searchCriteria) {
        AssetInfo assetInfo;
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<String> allocationCenterUids = wfCfigDao.findDeptForSharedAsset(user.getSiteUID());

        ew.andNew().eq("ai.depart_num", searchCriteria)
            .or().eq("ai.financing_num", searchCriteria)
            .or().eq("ai.qr_code", searchCriteria);

        ew.andNew().in("ai.asset_dept_uid", allocationCenterUids);

        logger.info("ew: " + ew.getSqlSegment());
        List<AssetInfo> assetInfoList = sharedAssetMapper.findSharedAsset(ew);
        //目前根据业务规则，如根据输入能查询到的设备数量为0或1个
        assetInfo = assetInfoList.stream().findFirst().get();
        logger.info("ai: " + assetInfo);
        return assetInfo;
    }

    public AssetInfo findSharedAsset2Return(Wrapper<AssetInfo> ew, String searchCriteria) {
        AssetInfo assetInfo;
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<String> allocationCenterUids = wfCfigDao.findDeptForSharedAsset(user.getSiteUID());

        ew.andNew().eq("ai.depart_num", searchCriteria)
            .or().eq("ai.financing_num", searchCriteria)
            .or().eq("ai.qr_code", searchCriteria);

        ew.andNew().notIn("ai.asset_dept_uid", allocationCenterUids);

        logger.info("ew: " + ew.getSqlSegment());
        List<AssetInfo> assetInfoList = sharedAssetMapper.findSharedAsset(ew);
        //目前根据业务规则，如根据输入能查询到的设备数量为0或1个
        assetInfo = assetInfoList.stream().findFirst().get();
        logger.info("ai: " + assetInfo);
        return assetInfo;
    }

    public List<OrgInfo> getOrgDeptList() {
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<OrgInfo> orgInfos = uaaService.getOrgDeptList(user.getSiteUID(), user.getUid());
        orgInfos.sort(TreeNodeUtil.orgInfoComparator);
        return orgInfos;
    }

    public SharedAssetReqDetail findReqDetailByAsset(AssetInfo assetInfo) {
        SharedAssetReqDetail sharedAssetReqDetail;
        Wrapper ew = new EntityWrapper();
        ew.andNew().eq("sard.asset_info_id", assetInfo.getUid());
        ew.orderBy("sard.charging_begin_time", true);
        List<SharedAssetReqDetail> reqDetailList = sharedAssetMapper.findReqDetailByAsset(ew);
        //正常情况下，即数据没有问题的情况下，一个设备仅对应一个未结束req_detail
        sharedAssetReqDetail = reqDetailList.stream().findFirst().get();
        logger.info("Retrieve req_detail from db: " + sharedAssetReqDetail);
        return sharedAssetReqDetail;
    }

    public ResponseEntity batchLeaseAssets(String requestType, String clinicalDeptUID, List<AssetInfo> leaseAssets) {
        Map<String, Object> reqData = new HashMap<>();
        reqData.put("clinicalDeptUID", clinicalDeptUID);
        reqData.put("type", requestType);
        Map<String, String> reqDetails = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyymmddHHmmss");
        for(AssetInfo assetInfo : leaseAssets) {
            reqDetails.put("assetInfoUID", assetInfo.getUid());
            reqDetails.put("chargingBeginTime", sdf.format(assetInfo.getSharedAssetReqDetail().getChargingBeginTime()));
            reqDetails.put("willBeDelivered", assetInfo.getSharedAssetReqDetail().getWillBeDelivered().toString());
        }
        reqData.put("details", reqDetails);
        return sharedAssetService.batchLeaseAssets(reqData);
    }

    public ResponseEntity batchReturnAssets(String requestType, String clinicalDeptUID, List<AssetInfo> returnAssets) {
        Map<String, Object> reqData = new HashMap<>();
        reqData.put("clinicalDeptUID", clinicalDeptUID);
        reqData.put("type", requestType);
        Map<String, String> reqDetails = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyymmddHHmmss");
        for(AssetInfo assetInfo : returnAssets) {
            reqDetails.put("assetInfoUID", assetInfo.getUid());
            reqDetails.put("chargingBeginTime", sdf.format(assetInfo.getSharedAssetReqDetail().getChargingBeginTime()));
            reqDetails.put("chargingEndTime", sdf.format(assetInfo.getSharedAssetReqDetail().getChargingEndTime()));
            reqDetails.put("isIntact", assetInfo.getSharedAssetReqDetail().getIntact().toString());
            reqDetails.put("compensationAmount", assetInfo.getSharedAssetReqDetail().getCompensationAmount().toString());
            reqDetails.put("reqComment", assetInfo.getSharedAssetReqDetail().getReqComment());
        }

        return sharedAssetService.batchReturnAssets(reqData);
    }

}
