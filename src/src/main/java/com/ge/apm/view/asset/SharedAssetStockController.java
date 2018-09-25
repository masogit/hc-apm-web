package com.ge.apm.view.asset;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.SharedAssetReqDetail;
import com.ge.apm.domain.SharedAssetRequest;
import com.ge.apm.service.sharedAsset.SharedAssetStockService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.utils.UrlParamUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.MyCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class SharedAssetStockController extends MyCRUDController<AssetInfo> {

    private static final Logger logger = LoggerFactory.getLogger(SharedAssetStockController.class);

    @Getter
    @Setter
    private String searchCriteria;

    @Getter
    @Setter
    private int queryType = -1;

    @Getter
    @Setter
    private String leaseDeptName;

    private SharedAssetStockService sharedAssetStockService;

    @Override
    protected BaseMapper<AssetInfo> getDAO() {
        return null;
    }

    protected void init() {
        logger.info("Start to initialize component");
        sharedAssetStockService = WebUtil.getBean(SharedAssetStockService.class);
    }

    protected List<AssetInfo> loadData(Pagination pageRequest, String sortField, boolean isAscending) {
        logger.info("thread: {}, searchCriteria: {}", Thread.currentThread().getId(), this.searchCriteria);
        Wrapper ew = new EntityWrapper();

        sharedAssetStockService.buildDefaultFilters(ew);
        sharedAssetStockService.addFiltersByQueryType(ew, this.queryType);
        if(StringUtils.isNotBlank(this.searchCriteria)) {
            sharedAssetStockService.addSearchCriteria(ew, this.searchCriteria.trim());
        }
        ew.andNew().isNotNull("shared_asset_type");
        wrapSearchFilter(ew);
        logger.info("SQL: " + ew.originalSql());
        lazyModel.setRowCount(sharedAssetStockService.getTotalRowCount(ew));
        ew.orderBy(sortField, isAscending);
        List<AssetInfo> assetList = sharedAssetStockService.loadAllSharedAssets(pageRequest, ew);
        return assetList;
    }

    public void onSelectAsset() throws IOException {
        String url = "actionName=View&selectedid="
            + selected.getId().toString()
            + "&asset_name=" + selected.getName()
            + "&terminate=" + false;
        url = "AssetDetail.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public List<SelectItem> getAllAccessibleOrgDeptListNestedByOrgLevel() {
        return sharedAssetStockService.getAllAccessibleOrgDeptListNestedByOrgLevel();
    }

    public void prepareForceLeaseAsset(AssetInfo asset) {
        SharedAssetReqDetail reqDetail = asset.getSharedAssetReqDetail();
        if(reqDetail == null) {
            reqDetail = new SharedAssetReqDetail();
            reqDetail.setChargingBeginTime(new Date());//默认计费开始时间为系统当前时间
            SharedAssetRequest leaseRequest = new SharedAssetRequest();
            leaseRequest.setType("2");//默认请求类型:补登记
            reqDetail.setLeaseRequest(leaseRequest);
        }
        asset.setSharedAssetReqDetail(reqDetail);
        super.setSelected(asset);
    }

    public void prepareForceReturnAsset(AssetInfo asset) {
        SharedAssetReqDetail reqDetail = asset.getSharedAssetReqDetail();
        SharedAssetRequest leaseRequest = sharedAssetStockService.findRequestById(asset.getSharedAssetReqDetail().getBorrowId());
        if(reqDetail.getReturnRequest() == null) {
            SharedAssetRequest returnRequest = new SharedAssetRequest();
            if(leaseRequest.getType() == null) {//默认请求类型和强制调拨类型相同
                returnRequest.setType("2");//默认请求类型:补登记
            } else {
                returnRequest.setType(leaseRequest.getType());
            }
            reqDetail.setReturnRequest(returnRequest);
        }
        reqDetail.setChargingEndTime(new Date());
        asset.setSharedAssetReqDetail(reqDetail);
        super.setSelected(asset);
    }

    public void forceLeaseAsset() {
        if(this.selected != null) {
            List<OrgInfo> deptList = sharedAssetStockService.getOrgDeptList(super.selected.getSiteUID());
            OrgInfo assetDept = deptList.stream().filter(item -> item.getName().equals(leaseDeptName))
                                                 .findFirst()
                                                 .orElse(null);
            try {
                if(assetDept != null) {
                    selected.setAssetDeptUID(assetDept.getUid());
                    ResponseEntity<LinkedHashMap> borrowResponse = sharedAssetStockService.forceLeaseAsset(selected);
                    LinkedHashMap<String, Object> rspBody = borrowResponse.getBody();
                    logger.info("****** force lease response ******");
                    rspBody.forEach((k, v) -> logger.info("key: " + k + ", value: " + v));
                }
            } catch (Exception ex) {
                logger.error("error: {}" + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public void forceReturnAsset() {
        if(this.selected != null) {
            try {
                ResponseEntity<LinkedHashMap> returnResponse = sharedAssetStockService.forceReturnAsset(selected);
                LinkedHashMap<String, Object> rspBody = returnResponse.getBody();
                logger.info("****** force return response ******");
                rspBody.forEach((k, v) -> logger.info("key: " + k + ", value: " + v));
            } catch (Exception ex) {
                logger.error("error: {}" + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public void onLeaseDeptNameChange(AjaxBehaviorEvent event) {
        if (event instanceof SelectEvent) {
            leaseDeptName = (String) ((SelectEvent) event).getObject();
        } else {
            List<OrgInfo> deptList = sharedAssetStockService.getOrgDeptList(super.selected.getSiteUID());
            if (!Strings.isNullOrEmpty(leaseDeptName) && !deptList.stream().anyMatch(item -> leaseDeptName.equals(item.getName()))) {
                leaseDeptName = "";
            }
        }
    }

    public List<String> autoCompleteDeptName(String query) {
        List<OrgInfo> deptList = sharedAssetStockService.getOrgDeptList(super.selected.getSiteUID());
        List<OrgInfo> res = deptList.stream().filter(item -> Strings.isNullOrEmpty(query)
                                                    || item.getName().contains(query)
                                                    || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase())))
                                             .collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName())
                            .distinct()
                            .collect(Collectors.toList());
    }

}
