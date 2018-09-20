package com.ge.apm.service.sharedAsset;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.WorkflowConfigRepository;
import com.ge.apm.dao.mapper.SharedAssetMapper;
import com.ge.apm.dao.mapper.SharedAssetRequestMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.SharedAssetInfoUtils;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.sysutil.UserContextService;
import javaslang.Tuple2;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.faces.model.SelectItem;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SharedAssetStockService {

    private static final Logger logger = LoggerFactory.getLogger(SharedAssetStockService.class);

    @Autowired
    private SharedAssetMapper sharedAssetMapper;

    @Autowired
    private SharedAssetRequestMapper sharedAssetRequestMapper;

    @Autowired
    private UaaService uaaService;

    @Autowired
    private SharedAssetService sharedAssetService;

    @Autowired
    private WorkflowConfigRepository wfCfigDao;

    public void buildDefaultFilters(Wrapper<AssetInfo> ew) {
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());

        ew.andNew()
            .in("asset_tenant_uid", orgUidList)
            .or().in("asset_institution_uid", orgUidList)
            .or().in("asset_hospital_uid", orgUidList)
            .or().in("asset_site_uid", orgUidList)
            .or().in("clinical_dept_uid", orgUidList);
    }

    public void addFiltersByQueryType(Wrapper<AssetInfo> ew, int queryType) {
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<String> allocationCenterUids = wfCfigDao.findDeptForSharedAsset(user.getSiteUID());
        switch (queryType) {
            case 1 :
                ew.andNew().eq("asset_status", 1).or().eq("asset_status", 3);
                ew.andNew().in("asset_dept_uid", allocationCenterUids);
                break;
            case 2:
                ew.andNew().notIn("asset_dept_uid", allocationCenterUids);
                break;
            case 3:
                ew.andNew().ne("asset_status", 1).andNew().ne("asset_status", 3);
                ew.andNew().in("asset_dept_uid", allocationCenterUids);
                break;
            default:
        }
    }

    public void addSearchCriteria(Wrapper<AssetInfo> ew, String searchCriteria) {
        ew.andNew()
            .like("asset_name", searchCriteria)
            .or().like("asset_num", searchCriteria)
            .or().like("financing_num", searchCriteria)
            .or().like("qr_code", searchCriteria)
            .or().like("serial_num", searchCriteria)
            .or().like("manufacture", searchCriteria)
            .or().like("lease_org_name", searchCriteria);
    }

    public Integer getTotalRowCount(Wrapper<AssetInfo> wrapper) {
        return sharedAssetMapper.getTotalRowCount(wrapper);
    }

    public List<AssetInfo> loadAllSharedAssets(Pagination pageRequest, Wrapper<AssetInfo> wrapper) {
        List<AssetInfo> assetList = sharedAssetMapper.loadSharedAssets(pageRequest, wrapper);
        calcRentTimeAndFee(assetList);
        return assetList;
    }

    public List<SelectItem> getAllAccessibleOrgDeptListNestedByOrgLevel() {
        UserAccount user = UserContextService.getCurrentUserAccount();
        TreeNode rootNode = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(user.getSiteUID(), user.getUid());

        TreeNodeUtil.sortTreeNodeByName(rootNode);

        List<SelectItem> dropdownList = new ArrayList<>();
        if (rootNode != null) {
            traverse(rootNode, dropdownList);
        }

        return dropdownList;
    }

    public SharedAssetRequest findRequestById(String borrowId) {
        if(logger.isInfoEnabled()) {
            logger.info("Borrow id: {}", borrowId);
        }
        SharedAssetRequest request = sharedAssetRequestMapper.findRequestById(borrowId);
        if(logger.isInfoEnabled()) {
            logger.info("SharedAssetRequest: {}", request);
        }
        return request;
    }

    public List<OrgInfo> getOrgDeptList(String siteUid) {
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<OrgInfo> orgInfos = uaaService.getOrgDeptList(siteUid, user.getUid());
        orgInfos.sort(TreeNodeUtil.orgInfoComparator);
        return orgInfos;
    }

    public ResponseEntity<LinkedHashMap> forceLeaseAsset(AssetInfo asset) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date chargingBeginTime = asset.getSharedAssetReqDetail().getChargingBeginTime();
        Date chargingEndTime = asset.getSharedAssetReqDetail().getChargingEndTime();
        String beginTimeStr = (chargingBeginTime == null) ? "" : formatter.format(chargingBeginTime);
        String endTimeStr = (chargingEndTime == null) ? "" : formatter.format(chargingEndTime);
        logger.info("assetDeptUID: {}, assetInfoUID: {}, chargingBeginTime: {}, chargingEndTime: {}, reqType: {}",
                        asset.getAssetDeptUID(), asset.getUid() , beginTimeStr , endTimeStr , asset.getSharedAssetReqDetail().getLeaseRequest().getType());
        ResponseEntity<LinkedHashMap> borrowResponse = sharedAssetService.borrowByShortcut(asset.getAssetDeptUID(),
                                                                                            asset.getUid(),
                                                                                            beginTimeStr,
                                                                                            endTimeStr,
                                                                                            asset.getSharedAssetReqDetail().getLeaseRequest().getType());
        return borrowResponse;
    }

    public ResponseEntity<LinkedHashMap> forceReturnAsset(AssetInfo asset) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        SharedAssetReqDetail reqDetail = asset.getSharedAssetReqDetail();
        Date chargingBeginTime = reqDetail.getChargingBeginTime();
        Date chargingEndTime = reqDetail.getChargingEndTime();
        String beginTimeStr = (chargingBeginTime == null) ? "" : formatter.format(chargingBeginTime);
        String endTimeStr = (chargingEndTime == null) ? "" : formatter.format(chargingEndTime);
        Boolean isIntact = reqDetail.getIntact();
        Double compensationAmount = reqDetail.getCompensationAmount();
        String reqComment = reqDetail.getReqComment();
        logger.info("assetDeptUID: {}, assetInfoUID: {}, chargingBeginTime: {}, chargingEndTime: {}, reqType: {}, intact: {}, compensationAmount: {}, reqComment: {}",
                        asset.getAssetDeptUID(), asset.getUid(), beginTimeStr, endTimeStr, asset.getSharedAssetReqDetail().getReturnRequest().getType(), isIntact.toString(), compensationAmount.toString(), reqComment);
        ResponseEntity<LinkedHashMap> returnResponse = sharedAssetService.returnByShortcut(asset.getAssetDeptUID(),
                                                                                            asset.getUid(),
                                                                                            beginTimeStr,
                                                                                            endTimeStr,
                                                                                            asset.getSharedAssetReqDetail().getReturnRequest().getType(),
                                                                                            isIntact.toString(),
                                                                                            compensationAmount.toString(),
                                                                                            reqComment);
        return returnResponse;
    }

    private void calcRentTimeAndFee(List<AssetInfo> assets) {
        for(AssetInfo asset : assets) {
            SharedAssetReqDetail reqDetail = asset.getSharedAssetReqDetail();
            if(reqDetail != null) {
                if(StringUtils.isBlank(reqDetail.getChargingType())) {
                    logger.info("charging_type is missed in request, so set charging_type from share_asset_type: " + asset.getUid() + ":" + asset.getSharedAssetType().getChargingType());
                    reqDetail.setChargingType(asset.getSharedAssetType().getChargingType());
                }
                Tuple2<BigDecimal, String> chargingUnitAndRentTime = SharedAssetInfoUtils.getChargingUnitAndTotalTime(reqDetail);
                BigDecimal fee = SharedAssetInfoUtils.getTotalAmount(chargingUnitAndRentTime._1, reqDetail);
                reqDetail.setRentalTime(chargingUnitAndRentTime._2);
                reqDetail.setTotalAmount(fee.doubleValue());
            }
        }
    }

    private void traverse(TreeNode node, List<SelectItem> itemList) {
        OrgInfo orgInfo = (OrgInfo) node.getData();

        SelectItem selectItem = new SelectItem(orgInfo.getUid(), orgInfo.getName(), "", orgInfo.getOrgType() != 4);
        itemList.add(selectItem);

        if (!node.isLeaf()) {
            for (TreeNode nodeItem : node.getChildren()) {
                traverse(nodeItem, itemList);
            }
        }
    }

}
