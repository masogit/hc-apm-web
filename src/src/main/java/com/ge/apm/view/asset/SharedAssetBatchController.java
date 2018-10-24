package com.ge.apm.view.asset;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.SharedAssetReqDetail;
import com.ge.apm.service.sharedasset.SharedAssetBatchService;
import com.ge.apm.service.sharedasset.SharedAssetService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.utils.RequestType;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.primefaces.component.tabview.Tab;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.MyCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Nick Liu
 * @since 2018.10.2 release
 */
@ManagedBean
@ViewScoped
public class SharedAssetBatchController extends MyCRUDController<AssetInfo> {

    private static final Logger logger = LoggerFactory.getLogger(SharedAssetBatchController.class);

    @Getter
    @Setter
    private String searchCriteria;

    @Getter
    @Setter
    private String currentTabId;

    @Getter
    @Setter
    private String requestType = RequestType.REQ_FOR_RECORD.getIndex();

    @Getter
    @Setter
    private String remark;

    @Getter
    @Setter
    private Date batchChargingBeginDate;

    @Getter
    @Setter
    private Date batchChargingEndDate;

    @Getter
    @Setter
    private Boolean willBeDelivered = Boolean.FALSE;

    @Getter
    @Setter
    private String leaseDeptName;

    private List<OrgInfo> leaseDeptList;

    @Getter
    @Setter
    private AssetInfo preAddAsset;

    /*
     * key   -> OrgInfo.uid
     * value -> OrgInfo
     */
    private Map<String, OrgInfo> orgInfoMap;

    private List<AssetInfo> leaseAssets = new LinkedList<>();

    private List<AssetInfo> returnAssets = new LinkedList<>();

    private List<AssetInfo> submittedAssets = new LinkedList<>();

    private SharedAssetBatchService sharedAssetBatchService;

    private SharedAssetService sharedAssetService;

    @Override
    protected BaseMapper getDAO() {
        return null;
    }

    @Override
    protected void init() {
        logger.info("Start to initialize component");
        sharedAssetBatchService = WebUtil.getBean(SharedAssetBatchService.class);
        sharedAssetService = WebUtil.getBean(SharedAssetService.class);
        leaseDeptList = sharedAssetBatchService.getOrgDeptList();
        orgInfoMap = leaseDeptList.stream().collect(Collectors.toMap(OrgInfo::getUid, dept -> dept));
        batchChargingBeginDate = Date.from(Instant.now());
        batchChargingEndDate = Date.from(Instant.now().plus(2, ChronoUnit.HOURS));
    }

    @Override
    protected List<AssetInfo> loadData(Pagination pageRequest, String sortField, boolean isAscending) {
        List<AssetInfo> result;
        if(submittedAssets.size() > 0) {
            result = submittedAssets;
        } else {
            if("batchReturn".equals(currentTabId)) {
                result = returnAssets;
            } else {
                result = leaseAssets;
            }
        }
        lazyModel.setRowCount(result.size());
        return result;
    }

    public void onTabChange(TabChangeEvent event) {
        Tab currentTab = event.getTab();

        if(currentTab == null) {
            currentTabId = "batchLease";
        } else {
            currentTabId = currentTab.getId();
        }

        logger.info("currentTabId: " + currentTabId);
    }

    public void findAndSelectAsset2Lease() {
        logger.info("default value of willBeDelivered: {}", this.willBeDelivered);
        if(StringUtils.isNotEmpty(this.searchCriteria)) {
            Boolean hasAdded = leaseAssets.stream().anyMatch(asset -> this.searchCriteria.equals(asset.getDepartNum())
                                                           || this.searchCriteria.equals(asset.getFinancingNum())
                                                           || this.searchCriteria.equals(asset.getQrCode()));
            if(hasAdded) {
                logger.info("Asset had been added");
                WebUtil.addErrorMessage("设备已添加");
            } else {
                Wrapper ew = new EntityWrapper();
                sharedAssetBatchService.buildDefaultFilters(ew);
                AssetInfo assetInfo = sharedAssetBatchService.findSharedAsset2Lease(ew, this.searchCriteria);
                if(assetInfo != null && StringUtils.isNotEmpty(assetInfo.getUid())) {
                    SharedAssetReqDetail reqDetail = assetInfo.getSharedAssetReqDetail();
                    if(reqDetail == null) {
                        reqDetail = new SharedAssetReqDetail();
                        reqDetail.setChargingBeginTime(this.batchChargingBeginDate);
                        //记录借用科室到req_detail
                        Optional<OrgInfo> leaseDept = leaseDeptList.stream()
                                                .filter(dept -> leaseDeptName.equals(dept.getName()))
                                                .findFirst();
                        leaseDept.map(dept -> dept.getUid()).ifPresent(reqDetail::setClinicalDeptUid);
                        leaseDept.map(dept -> dept.getName()).ifPresent(reqDetail::setClinicalDeptName);
                        //记录下送到req_detail
                        reqDetail.setWillBeDelivered(this.willBeDelivered);
                        assetInfo.setSharedAssetReqDetail(reqDetail);
                    }

                    leaseAssets.add(assetInfo);
                } else {
                    //TODO 未找到匹配设备
                }
            }
        }
    }

    public void findAndSelectAsset2Return() {
        if(StringUtils.isNotEmpty(this.searchCriteria)) {
            Boolean hasAdded = returnAssets.stream().anyMatch(asset -> this.searchCriteria.equals(asset.getDepartNum())
                                                            || this.searchCriteria.equals(asset.getFinancingNum())
                                                            || this.searchCriteria.equals(asset.getQrCode()));
            if(hasAdded) {
                logger.info("Asset had been added");
                WebUtil.addErrorMessage("设备已添加");
            } else {
                Wrapper ew = new EntityWrapper();
                sharedAssetBatchService.buildDefaultFilters(ew);
                AssetInfo assetInfo = sharedAssetBatchService.findSharedAsset2Return(ew, this.searchCriteria);
                if(assetInfo != null && StringUtils.isNotEmpty(assetInfo.getUid())) {
                    SharedAssetReqDetail reqDetail = sharedAssetBatchService.findReqDetailByAsset(assetInfo);
                    if(reqDetail != null) {
                        reqDetail.setChargingEndTime(this.batchChargingEndDate);
                        assetInfo.setSharedAssetReqDetail(reqDetail);
                        OrgInfo assetDept = orgInfoMap.get(assetInfo.getAssetDeptUID());
                        assetInfo.setAssetDeptOrg(assetDept);
                    }
                } else {
                    //TODO 未找到匹配设备
                }

                preAddAsset = assetInfo;

                RequestContext context = RequestContext.getCurrentInstance();
                if(this.validateReturnAsset(preAddAsset)) {
                    logger.info("Asset {} pass validation", preAddAsset.getName());
                    context.addCallbackParam("isValidReqDetail", Boolean.TRUE);
                    returnAssets.add(assetInfo);
                } else {
                    logger.info("Asset {} fail validation", preAddAsset.getName());
                    context.addCallbackParam("isValidReqDetail", Boolean.FALSE);
                }
            }
        }
    }

    public void batchLeaseAssets() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String chargingBeginTimeStr, chargingEndTimeStr;
        for(AssetInfo asset : leaseAssets) {
            logger.info("信息: " + asset.getDepartNum());
            Date chargingBeginTime = asset.getSharedAssetReqDetail().getChargingBeginTime();
            Date chargingEndTime = asset.getSharedAssetReqDetail().getChargingEndTime();

            chargingBeginTimeStr = (chargingBeginTime == null) ? "" : sdf.format(chargingBeginTime);
            chargingEndTimeStr = (chargingEndTime == null) ? "" : sdf.format(chargingEndTime);
            ResponseEntity<LinkedHashMap> borrowResponse = sharedAssetService.borrowByShortcut(asset.getClinicalDeptUID(),
                                                                                                asset.getAssetDeptUID(),
                                                                                                chargingBeginTimeStr,
                                                                                                chargingEndTimeStr,
                                                                                                this.requestType);
            logger.info("Response: " + borrowResponse);
        }
    }

    public void batchReturnAssets() {
        for(AssetInfo asset : returnAssets) {
            logger.info("信息: " + asset.getDepartNum());
        }
    }

    public void onLeaseDeptNameChange(AjaxBehaviorEvent event) {
        if (event instanceof SelectEvent) {
            leaseDeptName = (String) ((SelectEvent) event).getObject();
        } else {
            if (!Strings.isNullOrEmpty(leaseDeptName) && !leaseDeptList.stream().anyMatch(item -> leaseDeptName.equals(item.getName()))) {
                leaseDeptName = "";
            }
        }
    }

    public List<String> autoCompleteDeptName(String query) {
        List<OrgInfo> res = leaseDeptList.stream().filter(item -> Strings.isNullOrEmpty(query)
                                            || item.getName().contains(query)
                                            || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase())))
                                            .collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName())
                            .distinct()
                            .collect(Collectors.toList());
    }

    public void removeLeaseReqDetail() {
        this.leaseAssets.remove(this.selected);
    }

    public void removeReturnReqDetail() {
        this.returnAssets.remove(this.selected);
    }

    private Boolean validateReturnAsset(AssetInfo assetInfo) {
        Boolean result = Boolean.FALSE;
        SharedAssetReqDetail reqDetail = assetInfo.getSharedAssetReqDetail();
        if(reqDetail != null) {
            result = reqDetail.validateChargingBeginTime() && reqDetail.validateChargingEndTime();
        }
        return result;
    }

}
