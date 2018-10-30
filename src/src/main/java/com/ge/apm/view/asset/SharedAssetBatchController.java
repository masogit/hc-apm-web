package com.ge.apm.view.asset;

import com.alibaba.fastjson.JSON;
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

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
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

    public static final String BATCH_RETURN_TAB_ID = "batchReturn";
    public static final String SERVICE_RESPONSE_STATUS_OK = "OK";
    public static final String SERVICE_RESPONSE_BIZ_CODE = "bizStatusCode";
    public static final String SERVICE_RESPONSE_RENT_DETAILS = "rentDetails";

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

    @Getter
    @Setter
    private String assetDeptName;

    private List<OrgInfo> leaseDeptList;

    @Getter
    @Setter
    private AssetInfo preAddAsset;

    /**
     * key   -> OrgInfo.uid
     * value -> OrgInfo
     */
    private Map<String, OrgInfo> orgInfoMap;

    private List<AssetInfo> leaseAssets = new LinkedList<>();

    private List<AssetInfo> submittedLeaseAssets;

    private List<AssetInfo> returnAssets = new LinkedList<>();

    private List<AssetInfo> submittedReturnAssets;

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
        batchChargingEndDate = Date.from(Instant.now().plus(2, ChronoUnit.SECONDS));
    }

    @Override
    protected List<AssetInfo> loadData(Pagination pageRequest, String sortField, boolean isAscending) {
        List<AssetInfo> result;
        if(BATCH_RETURN_TAB_ID.equals(currentTabId)) {
            if(submittedReturnAssets == null || returnAssets.size() > 0) {
                result = returnAssets;
            } else {
                result = submittedReturnAssets;
            }
        } else {
            if(submittedLeaseAssets == null || leaseAssets.size() > 0) {
                result = leaseAssets;
            } else {
                result = submittedLeaseAssets;
            }
        }
        lazyModel.setRowCount(result.size());
        return result;
    }

    public void onTabChange(TabChangeEvent event) {
        Tab currentTab = event.getTab();
        if(event.getTab() == null) {
            currentTabId = "batchLease";
        } else {
            currentTabId = currentTab.getId();
        }
    }

    public void findAndSelectAsset2Lease() {
        logger.info("default value of willBeDelivered: {}", this.willBeDelivered);
        if(StringUtils.isNotEmpty(this.searchCriteria)) {
            Boolean hasAdded = leaseAssets.stream().anyMatch(asset -> this.searchCriteria.equals(asset.getDepartNum())
                                                           || this.searchCriteria.equals(asset.getFinancingNum())
                                                           || this.searchCriteria.equals(asset.getQrCode()));
            if(hasAdded) {
                logger.info("Asset had been added");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, null, "设备已加入待借用列表"));
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
                    //未找到匹配设备
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, null, "未找到匹配设备"));
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
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, null, "设备已加入待归还列表"));
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

                    preAddAsset = assetInfo;

                    if(this.validateAssetDept(assetInfo)) {
                        RequestContext context = RequestContext.getCurrentInstance();
                        if(this.validateAssetChargingTIme(preAddAsset)) {
                            logger.info("Asset {} pass validation", preAddAsset.getName());
                            context.addCallbackParam("isValidReqDetail", Boolean.TRUE);
                            addAsset2ReturnList(assetInfo);
                        } else {
                            logger.info("Asset {} fail validation", preAddAsset.getName());
                            context.addCallbackParam("isValidReqDetail", Boolean.FALSE);
                        }
                    } else {
                        //借用科室不匹配
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "借用科室不匹配"));
                    }
                } else {
                    // 未找到匹配设备
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "未找到满足条件设备"));
                }
            }
        }
    }

    public void batchLeaseAssets() {
        if(leaseAssets != null && leaseAssets.size() > 0) {
            String clinicalDeptUID = leaseDeptList.stream().filter(dept -> leaseDeptName.equals(dept.getName())).map(dept -> dept.getUid()).findFirst().get();
            ResponseEntity<LinkedHashMap> responseEntity = sharedAssetBatchService.batchLeaseAssets(requestType, clinicalDeptUID ,leaseAssets);
            Map responseBody = responseEntity.getBody();
            if(SERVICE_RESPONSE_STATUS_OK.equals(responseBody.get(SERVICE_RESPONSE_BIZ_CODE))) {
                submittedLeaseAssets = leaseAssets;
                leaseAssets = new LinkedList<>();

                Map<String, Object> approvedLeaseRequest = (Map<String, Object>) responseBody.get("data");
                List rentDetails = (List)approvedLeaseRequest.get(SERVICE_RESPONSE_RENT_DETAILS);
                for(Object rentDetail : rentDetails) {
                    SharedAssetReqDetail reqDetail = JSON.parseObject(JSON.toJSONString(rentDetail), SharedAssetReqDetail.class);
                    AssetInfo asset = submittedLeaseAssets.stream().filter(assetInfo -> assetInfo.getUid().equals(reqDetail.getAssetInfoId())).findFirst().get();
                    asset.setSharedAssetReqDetail(reqDetail);
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null, "批量借用成功"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "批量借用失败"));
            }
        } else {
            //空提交, 通过页面组件disable了提交按钮
        }
    }

    public void batchReturnAssets() {
        if(returnAssets != null && returnAssets.size() > 0) {
            String clinicalDeptUID = returnAssets.stream().findFirst().map(dept -> dept.getAssetDeptUID()).get();
            ResponseEntity<LinkedHashMap> responseEntity = sharedAssetBatchService.batchReturnAssets(requestType, clinicalDeptUID, returnAssets);
            Map responseBody = responseEntity.getBody();
            if(SERVICE_RESPONSE_STATUS_OK.equals(responseBody.get(SERVICE_RESPONSE_BIZ_CODE))) {
                submittedReturnAssets = returnAssets;
                returnAssets = new LinkedList<>();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null, "批量归还成功"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "批量归还成功"));
            }
        } else {
            //空提交, 通过页面组件disable了提交按钮
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
        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }

    public void removeLeaseReqDetail() {
        this.leaseAssets.remove(this.selected);
    }

    public void resetLeaseAssets() {
        super.selected = null;
        if(submittedLeaseAssets != null && submittedLeaseAssets.size() > 0) {
            submittedLeaseAssets.clear();
        }
        if(leaseAssets.size() > 0) {
            leaseAssets.clear();
        }
    }

    public void addAsset2ReturnList(AssetInfo assetInfo) {
        returnAssets.add(assetInfo);
        this.assetDeptName = orgInfoMap.get(assetInfo.getAssetDeptUID()).getName();
    }

    public void removeReturnReqDetail() {
        this.returnAssets.remove(this.selected);
        if(returnAssets.size() == 0) {
            this.assetDeptName = "";
        }
    }

    public void resetReturnAssets() {
        super.selected = null;
        this.assetDeptName = "";
        if(submittedReturnAssets != null && submittedReturnAssets.size() > 0) {
            submittedReturnAssets.clear();
        }
        if(returnAssets.size() > 0) {
            returnAssets.clear();
        }
    }

    public Boolean enableLeaseSubmitBtn() {
        Boolean result = Boolean.FALSE;
        if(leaseAssets.size() > 0) {
            result = Boolean.TRUE;
        }
        return result;
    }

    public Boolean enableReturnSubmitBtn() {
        Boolean result = Boolean.FALSE;
        if(returnAssets.size() > 0) {
            result = Boolean.TRUE;
        }
        return result;
    }

    private Boolean validateAssetChargingTIme(AssetInfo assetInfo) {
        Boolean result = Boolean.FALSE;
        SharedAssetReqDetail reqDetail = assetInfo.getSharedAssetReqDetail();
        if(reqDetail != null) {
            if(reqDetail.validateChargingBeginTime()) {
                //TODO
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "计费开始时间必须在当前时间之前"));
                return false;
            }
            if(reqDetail.validateChargingEndTime()) {
                //TODO
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "计费结束时间必须在计费开始时间之后，并在未来两小时内"));
                return false;
            }
            result = reqDetail.validateChargingBeginTime() && reqDetail.validateChargingEndTime();
        }
        return result;
    }

    private Boolean validateAssetDept(AssetInfo assetInfo) {
        Boolean result = Boolean.TRUE;
        //验证新加入设备的使用科室是否和之前加入到待归还列表中设备使用科室是否一致
        if(returnAssets.size() > 0) {
            AssetInfo assetInReturnList = returnAssets.stream().findFirst().get();
            result = assetInReturnList.getAssetDeptUID().equals(assetInfo.getAssetDeptUID());
        }
        return result;
    }

}
