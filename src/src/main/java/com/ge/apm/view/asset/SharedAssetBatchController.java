package com.ge.apm.view.asset;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.SharedAssetReqDetail;
import com.ge.apm.service.sharedAsset.SharedAssetBatchService;
import com.ge.apm.service.sharedAsset.SharedAssetService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.primefaces.component.tabview.Tab;
import org.primefaces.event.TabChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.MyCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

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
    private String requestType = "2";//请求默认类型为补登记

    @Getter
    @Setter
    private Date createDate = new Date();

    @Getter
    @Setter
    private String remark;

    @Getter
    @Setter
    private Date batchChargingBeginDate = new Date();

    @Getter
    @Setter
    private Date batchChargingEndDate;

    private List<AssetInfo> leaseAssets = new LinkedList<>();

    private List<AssetInfo> returnAssets = new LinkedList<>();

    private List<AssetInfo> submittedAssets = new LinkedList<>();

    private SharedAssetBatchService sharedAssetBatchService;

    private SharedAssetService sharedAssetService;

    @Override
    protected BaseMapper getDAO() {
        return null;
    }

    protected void init() {
        logger.info("Start to initialize component");
        sharedAssetBatchService = WebUtil.getBean(SharedAssetBatchService.class);
        sharedAssetService = WebUtil.getBean(SharedAssetService.class);
    }

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

    public void findAndSelectAsset4Lease() {
        boolean hasAdded = Boolean.FALSE;
        if(StringUtils.isNotEmpty(this.searchCriteria)) {
            hasAdded = leaseAssets.stream().anyMatch(asset -> this.searchCriteria.equals(asset.getDepartNum())
                                                           || this.searchCriteria.equals(asset.getFinancingNum())
                                                           || this.searchCriteria.equals(asset.getQrCode()));
            if(hasAdded) {
                logger.info("Asset had been added");
            } else {
                Wrapper ew = new EntityWrapper();
                sharedAssetBatchService.buildDefaultFilters(ew);
                AssetInfo ai = sharedAssetBatchService.findSharedAsset2Lease(ew, this.searchCriteria);
                if(ai != null && StringUtils.isNotEmpty(ai.getUid())) {
                    SharedAssetReqDetail reqDetail = ai.getSharedAssetReqDetail();
                    if(reqDetail == null) {
                        ai.setSharedAssetReqDetail(new SharedAssetReqDetail());
                    }
                    ai.getSharedAssetReqDetail().setChargingBeginTime(this.batchChargingBeginDate);
                    leaseAssets.add(ai);
                }
            }
        }
    }

    public void findAndSelectAsset4Return() {
        boolean hasAdded = Boolean.FALSE;
        if(StringUtils.isNotEmpty(this.searchCriteria)) {
            hasAdded = returnAssets.stream().anyMatch(asset -> this.searchCriteria.equals(asset.getDepartNum())
                                                            || this.searchCriteria.equals(asset.getFinancingNum())
                                                            || this.searchCriteria.equals(asset.getQrCode()));
            if(hasAdded) {
                logger.info("Asset had been added");
            } else {
                Wrapper ew = new EntityWrapper();
                sharedAssetBatchService.buildDefaultFilters(ew);
                AssetInfo ai = sharedAssetBatchService.findSharedAsset2Return(ew, this.searchCriteria);
                if(ai != null && StringUtils.isNotEmpty(ai.getUid())) {
                    returnAssets.add(ai);
                }
            }
        }
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

}
