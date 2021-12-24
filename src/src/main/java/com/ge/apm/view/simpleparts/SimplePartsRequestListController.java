package com.ge.apm.view.simpleparts;

import com.ge.apm.dao.*;
import com.ge.apm.dao.simpleparts.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.simpleparts.*;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.constant.SimplePartsConstant;
import com.ge.apm.service.simpleparts.*;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author csh
 * @version 1.0
 * @date 2020/6/8
 * @descript 申领申购单总计Controller
 */
@ManagedBean
@ViewScoped
@SuppressWarnings("all")
public class SimplePartsRequestListController extends GenericCRUDUUIDController<SimplePartsRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimplePartsRequestListController.class);
    private static final long serialVersionUID = -483251039974336475L;

    public static final String VIEW_PARTS_REQUEST = "VIEW_PARTS_REQUEST";
    public static final String EDIT_PARTS_REQUEST = "EDIT_PARTS_REQUEST";

    private SimplePartsRequestService partsRequestService;
    private SimplePartsRequestRepository partsRequestRepository;
    private SimplePartsRequestLineItemService partsRequestLineItemService;
    private SimplePartsRequestLineItemRepository partsRequestLineItemRepository;
    private SimplePartsTransactionService partsTransactionService;
    private SimplePartsInventoryService partsInventoryService;
    private SimplePartsWarehouseService partsWarehouseService;
    private SimplePartsWarehouseRepository partsWarehouseRepository;
    private SimplePartsWarehouseLocatorService partsWarehouseLocatorService;
    private SimplePartsWarehouseLocatorRepository partsWarehouseLocatorRepository;
    private UaaService uuaService;
    private UserContextService userContextService;
    private UserAccountRepository userAccountRepository;
    private SimplePartsConfigService partsConfigService;
    private SimplePartsRequestEditController partsRequestEditController;
    private SimplePartsInventoryRepository simplePartsInventoryRepository;
    @Getter
    @Setter
    private String oneSearchStr;

    @Getter
    @Setter
    private int requestType;

    @Getter
    @Setter
    private Integer requestStatus;

    @Getter
    @Setter
    private List<SimplePartsRequestLineItem> partsRequestLineItems = Collections.emptyList();

    @Override
    protected void init() {
        partsRequestRepository = WebUtil.getBean(SimplePartsRequestRepository.class);
        partsRequestService = WebUtil.getBean(SimplePartsRequestService.class);
        partsRequestLineItemService = WebUtil.getBean(SimplePartsRequestLineItemService.class);
        partsTransactionService = WebUtil.getBean(SimplePartsTransactionService.class);
        partsInventoryService = WebUtil.getBean(SimplePartsInventoryService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        uuaService = WebUtil.getBean(UaaService.class);
        partsWarehouseService = WebUtil.getBean(SimplePartsWarehouseService.class);
        partsWarehouseRepository = WebUtil.getBean(SimplePartsWarehouseRepository.class);
        partsWarehouseLocatorService = WebUtil.getBean(SimplePartsWarehouseLocatorService.class);
        partsWarehouseLocatorRepository = WebUtil.getBean(SimplePartsWarehouseLocatorRepository.class);
        partsConfigService = WebUtil.getBean(SimplePartsConfigService.class);
        partsRequestLineItemRepository = WebUtil.getBean(SimplePartsRequestLineItemRepository.class);
        partsRequestEditController = WebUtil.getBean(SimplePartsRequestEditController.class);
        simplePartsInventoryRepository = WebUtil.getBean(SimplePartsInventoryRepository.class);
    }

    @Override
    protected GenericRepositoryUUID<SimplePartsRequest> getDAO() {
        return partsRequestRepository;
    }

    public void preparePartsRequest() {
        requestType = SimplePartsConstant.PARTS_REQUEST_FORM;
    }

    public void preparePartsPurchase() {
        requestType = SimplePartsConstant.PARTS_PURCHASE_FORM;
    }

    public void prepareStockIn() {
        requestType = SimplePartsConstant.PARTS_PURCHASE_FORM;
        requestStatus = SimplePartsConstant.PARTS_NOT_TRANSACTIONED;
    }

    public void prepareStockOut() {
        requestType = SimplePartsConstant.PARTS_REQUEST_FORM;
        requestStatus = SimplePartsConstant.PARTS_NOT_TRANSACTIONED;
    }

    @Override
    protected Page<SimplePartsRequest> loadData(PageRequest pageRequest) {
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();

        searchFilters = new ArrayList<>();

        List<String> orgUidList = uuaService.getOrgListByUser(currentUserAccount.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
        List<Integer> proposerIds = Lists.newArrayList();
        proposerIds.add(currentUserAccount.getId());
        searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("proposerId", SearchFilter.Operator.IN, proposerIds, "1*2+"));

        searchFilters.add(new SearchFilter("type", SearchFilter.Operator.EQ, requestType));
         if (null != requestStatus) {
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, requestStatus));
        }

        if (oneSearchStr != null && !oneSearchStr.isEmpty()) {
//            this.updateSearchFilter(new SearchFilter("createdBy", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("requestCode", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
        }

        pageRequest = addRequiredSortByRequestCode(pageRequest);
        Page<SimplePartsRequest> partsRequestList = partsRequestRepository.findBySearchFilter(searchFilters, pageRequest);
        return partsRequestList;


    }

    private PageRequest addRequiredSortByRequestCode(PageRequest pageRequest) {
        Iterator<Sort.Order> orders = pageRequest.getSort().iterator();
        boolean exists = false;
        while(orders.hasNext()){
            if(orders.next().getProperty().equals("requestCode")){
                exists = true;
                break;
            }
        }
        if (exists) {
            return pageRequest;
        } else {
            Sort sortWithRequestCode = new Sort(Sort.Direction.ASC, "requestCode");
            pageRequest= new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSort().and(sortWithRequestCode));
        }
        return pageRequest;
    }

    public void clearSearchCondition() {
        oneSearchStr = null;
    }

    public void onRowSelect(SelectEvent event) {

        partsRequestLineItems = partsRequestLineItemRepository.findByRequestCode(selected.getRequestCode());

        partsRequestLineItems.stream().forEach(item -> {
//            部分出入库使用
            item.setNotTransactionQty(item.getRequestQty() - item.getTransactionQty());

            if(StringUtils.isNotEmpty(selected.getPartsWarehouseId())){
                if (!partsConfigService.isEnableWarehouseLocator()) {
                    item.setPartsLocatorId(SimplePartsConstant.VIRTUAL_LOCATOR_ID);
                }

                if(selected.getType() == SimplePartsConstant.PARTS_REQUEST_FORM){
                    List<SimplePartsInventory> list = partsInventoryService.findByPartsItemIdAndPartsWarehouseId(item.getPartsItemId() ,selected.getPartsWarehouseId());
                    if(list.size() > 0){
                        item.setPartsWarehouseId(selected.getPartsWarehouseId());
                    }
                }else if(selected.getType() == SimplePartsConstant.PARTS_PURCHASE_FORM){
                    item.setPartsWarehouseId(selected.getPartsWarehouseId());
                }
            }
        });
        partsRequestEditController.onServerEvent(EDIT_PARTS_REQUEST, selected);
    }

    public void editPartsRequest() {
        partsRequestEditController.onServerEvent(EDIT_PARTS_REQUEST, selected);
    }

    public List<SimplePartsWarehouse> getStockInWarehouseList(SimplePartsRequestLineItem lineItem) {
        List<SimplePartsWarehouse> warehouseList = partsWarehouseService.findAllBySiteUidAndUserId(lineItem.getSiteUID());

        return warehouseList;
    }

    public List<SimplePartsWarehouse> getStockOutWarehouseList(SimplePartsRequestLineItem lineItem) {

        List<SimplePartsWarehouse> warehouseList = partsWarehouseService.findAllByPartsItemId(lineItem.getPartsItemId(),UserContextService.getCurrentUserAccount().getSiteUID());

        return warehouseList;
    }

    public void onWarehouseChange(SimplePartsRequestLineItem partsRequestLineItem) {

        if (!partsConfigService.isEnableWarehouseLocator()) {
            partsRequestLineItem.setPartsLocatorId(SimplePartsConstant.VIRTUAL_LOCATOR_ID);
        }

    }

    public List<SimplePartsWareHouseLocator> getLocatorListByWarehouse(SimplePartsRequestLineItem lineItem) {

        if (null == lineItem.getPartsWarehouseId()) {
            return Collections.emptyList();
        }

        List<SimplePartsWareHouseLocator> resultList;

        if (SimplePartsConstant.PARTS_PURCHASE_FORM == requestType) {
            resultList = partsWarehouseLocatorRepository.findAllByPartsWarehouseId(lineItem.getPartsWarehouseId());
        } else {
            UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
            List<SimplePartsWarehouse> warehouseList = partsWarehouseService.findAllBySiteUidAndUserId(currentUserAccount.getSiteUID());
            if (warehouseList.size() == 0) {
                return Collections.emptyList();
            }

            if (null == lineItem.getPartsWarehouseId()) {
                return Collections.emptyList();
            }

            Set<String> warehouseIds = warehouseList.stream().map(item -> item.getId()).collect(Collectors.toSet());
            if(!warehouseIds.contains(lineItem.getPartsWarehouseId())){
                return Collections.emptyList();
            }

            resultList = partsWarehouseLocatorService.findByPartsWarehouseIdAndPartsItemId(lineItem.getPartsWarehouseId(),lineItem.getPartsItemId());
        }

        return resultList;
    }

    public void onWarehouseLocatorChange(SimplePartsRequestLineItem partsRequestLineItem) {

        if (!partsConfigService.isEnableWarehouseLocator()) {
            partsRequestLineItem.setPartsLocatorId(SimplePartsConstant.VIRTUAL_LOCATOR_ID);
        }

    }

    public boolean currentPartsRequestApproved() {

        if (null == selected) {
            return false;
        }

        if (ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE != selected.getProposerStatus() && SimplePartsConstant.PARTS_TRANSACTIONED != selected.getStatus()) {
            return true;
        }

        return false;
    }

    public boolean currentPartsRequestStockApproved() {

        if (null == selected) {
            return false;
        }

        SimplePartsRequest partsRequest = partsRequestRepository.findById(selected.getId());

        if (ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE != partsRequest.getProposerStatus() || SimplePartsConstant.PARTS_NOT_TRANSACTIONED != partsRequest.getStatus()) {
            return false;
        }

        return true;
    }

    public boolean isCurrentPartsRequestEditable() {
        if (null == selected) {
            return false;
        }

        if (ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_DIS_AGREE == selected.getProposerStatus()
                && SimplePartsConstant.PARTS_NOT_TRANSACTIONED == selected.getStatus()) {
            return true;
        }

        return false;
    }

    public String stockIn() {

        SimplePartsRequest partsRequest = partsRequestRepository.findById(selected.getId());

        if (partsRequest.getProposerStatus() != ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE) {
            WebUtil.addWarnMessage("申购单审批未通过，无法入库");
            return null;
        }

        if(partsRequestLineItems.size() == 0){
            WebUtil.addWarnMessage("申购单备件信息为空");
            return null;
        }

//        部分出入库使用
        List<SimplePartsRequestLineItem> filterPartsRequestLineItems = partsRequestLineItems.stream().filter(item -> item.getStatus() != SimplePartsConstant.PARTS_TRANSACTIONED).collect(Collectors.toList());

        boolean enableWarehouseLocator = partsConfigService.isEnableWarehouseLocator();
        for (SimplePartsRequestLineItem partsRequestLineItem : filterPartsRequestLineItems) {

            if(StringUtils.isEmpty(partsRequestLineItem.getPartsWarehouseId())){
                WebUtil.addWarnMessage("申购单备件信息,须指定仓库");
                return null;
            }

            if(enableWarehouseLocator && StringUtils.isEmpty(partsRequestLineItem.getPartsLocatorId())){
                WebUtil.addWarnMessage("申购单备件信息,须指定仓位");
                return null;
            }
            if(userContextService.isCountyArea()){
                if(StringUtils.isEmpty(partsRequestLineItem.getStorageType())){
                    WebUtil.addWarnMessage("申购单备件信息,须指定入库类型");
                    return null;
                }
                if(StringUtils.isEmpty(partsRequestLineItem.getComments())){
                    WebUtil.addWarnMessage("申购单备件信息,须填写备注");
                    return null;
                }
            }
        }

        partsRequestService.stockInRequest(selected, filterPartsRequestLineItems);

        partsRequestLineItems.clear();

        return getListPageLink();
    }

    public String stockOut() {
        if (selected.getProposerStatus() != ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE) {
            WebUtil.addWarnMessage("申领单审批未通过，无法出库");
            return null;
        }

        if(partsRequestLineItems.size() == 0){
            WebUtil.addWarnMessage("申领单备件信息为空");
            return null;
        }

        //        部分出入库使用
        List<SimplePartsRequestLineItem> filterPartsRequestLineItems = partsRequestLineItems.stream().filter(item -> item.getStatus() != SimplePartsConstant.PARTS_TRANSACTIONED).collect(Collectors.toList());

        boolean enableWarehouseLocator = partsConfigService.isEnableWarehouseLocator();
        for (SimplePartsRequestLineItem partsRequestLineItem : filterPartsRequestLineItems) {

            if(StringUtils.isEmpty(partsRequestLineItem.getPartsWarehouseId())){
                WebUtil.addWarnMessage("申领单备件信息,须指定仓库");
                return null;
            }

            if(enableWarehouseLocator && StringUtils.isEmpty(partsRequestLineItem.getPartsLocatorId())){
                WebUtil.addWarnMessage("申领单备件信息,须指定仓位");
                return null;
            }
            if(userContextService.isCountyArea()) {
                if (StringUtils.isEmpty(partsRequestLineItem.getTransportCompany())) {
                    WebUtil.addWarnMessage("申领单备件信息,须指定运输公司");
                    return null;
                }
                if (StringUtils.isEmpty(partsRequestLineItem.getTransportNo())) {
                    WebUtil.addWarnMessage("申领单备件信息,须指定运单号");
                    return null;
                }
            }
        }

        try {
            partsRequestService.stockoutRequest(selected, filterPartsRequestLineItems);

            partsRequestLineItems.clear();

            return getListPageLink();

        } catch (SimplePartsInventoryNotEnoughException e) {
            WebUtil.addWarnMessage("库存不足");
            return null;
        }

    }

    private String getListPageLink() {
        if (SimplePartsConstant.PARTS_REQUEST_FORM == requestType) {
            return "PartsOutStock.xhtml?faces-redirect=true";
        } else {
            return "PartsInStock.xhtml?faces-redirect=true";
        }
    }

    public boolean isRenderStockInRequest(SimplePartsRequest partsRequest) {
        if (null == selected) {
            return false;
        }

        if (partsRequest.getRequestCode().equals(selected.getRequestCode()) && partsRequest.getProposerStatus() == ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE) {
            return true;
        } else {
            return false;
        }

    }

    public void onRowEdit(RowEditEvent event) {
        SimplePartsRequestLineItem partsRequestLineItem = (SimplePartsRequestLineItem ) event.getObject();
        SimplePartsRequestLineItem query = partsRequestLineItemRepository.findById(partsRequestLineItem.getId());

        if(StringUtils.isEmpty(partsRequestLineItem.getPartsWarehouseId())){
            if(partsRequestLineItem.getNotTransactionQty() == null){
                partsRequestLineItem.setNotTransactionQty(query.getRequestQty() - query.getTransactionQty());
            }
            WebUtil.addWarnMessage("仓库未选择");
            return;
        }

        boolean enableWarehouseLocator = partsConfigService.isEnableWarehouseLocator();
        if(enableWarehouseLocator && StringUtils.isEmpty(partsRequestLineItem.getPartsLocatorId())){
            if(partsRequestLineItem.getNotTransactionQty() == null){
                partsRequestLineItem.setNotTransactionQty(query.getRequestQty() - query.getTransactionQty());
            }
            WebUtil.addWarnMessage("须指定仓位");
            return;
        }else if(!enableWarehouseLocator){
            partsRequestLineItem.setPartsLocatorId(SimplePartsConstant.VIRTUAL_LOCATOR_ID);
        }

        SimplePartsRequest partsRequest = partsRequestRepository.findById(partsRequestLineItem.getPartsRequestId());
        if(partsRequest.getType() == 1){
            if(userContextService.isCountyArea()) {
                if (StringUtils.isEmpty(partsRequestLineItem.getTransportNo())) {
                    WebUtil.addWarnMessage("请填写运单号");
                    return;
                }
                if (StringUtils.isEmpty(partsRequestLineItem.getTransportCompany())) {
                    WebUtil.addWarnMessage("请填写运输公司");
                    return;
                }
            }
            if (partsRequestLineItem.getNotTransactionQty() == null || partsRequestLineItem.getNotTransactionQty() == 0){
                partsRequestLineItem.setNotTransactionQty(query.getRequestQty() - query.getTransactionQty());
                WebUtil.addWarnMessage("备件申购单明细出库数量请填写有意义数值");
                return;
            }

            Optional<SimplePartsInventory> partsInventoryOptional = simplePartsInventoryRepository.findByWarehouseIdAndLocatorIdAndPartsItemId(partsRequestLineItem.getPartsWarehouseId(), partsRequestLineItem.getPartsLocatorId(), partsRequestLineItem.getPartsItemId());
            if (partsInventoryOptional.isPresent()) {

                SimplePartsInventory partsInventory = partsInventoryOptional.get();
                // 更新
                int sumNum = partsInventory.getOnhandQty() - partsRequestLineItem.getNotTransactionQty() ;
                // 更新 总的库存
                if (sumNum < 0) {
                    partsRequestLineItem.setNotTransactionQty(query.getRequestQty() - query.getTransactionQty());
                    WebUtil.addWarnMessage("备件库存不足");
                    return;
                }
            }else{
                WebUtil.addWarnMessage("备件库存不足");
                return;
            }
        }else{
            if(userContextService.isCountyArea()) {
                if (StringUtils.isEmpty(partsRequestLineItem.getStorageType())) {
                    WebUtil.addWarnMessage("入库类型未选择");
                    return;
                }
                if (StringUtils.isEmpty(partsRequestLineItem.getComments())) {
                    WebUtil.addWarnMessage("请填写备注");
                    return;
                }
            }
            if (partsRequestLineItem.getNotTransactionQty() == null || partsRequestLineItem.getNotTransactionQty() == 0){
                partsRequestLineItem.setNotTransactionQty(query.getRequestQty() - query.getTransactionQty());
                WebUtil.addWarnMessage("备件申购单明细入库数量请填写有意义数值");
                return;
            }
        }

        if(query.getRequestQty() - query.getTransactionQty() >= partsRequestLineItem.getNotTransactionQty()){
            //备件出入库
            try {
                partsRequestLineItemService.stockInOrOutParts(partsRequestLineItem);
                List<SimplePartsRequestLineItem> partsRequestLineItemTemps = partsRequestLineItemRepository.findByRequestCode(partsRequest.getRequestCode());
                partsRequestLineItemTemps = partsRequestLineItemTemps.stream().filter(item -> item.getStatus() != SimplePartsConstant.PARTS_TRANSACTIONED).collect(Collectors.toList());
                if(partsRequestLineItemTemps.size() == 0){
                    RequestContext.getCurrentInstance().execute("window.location.reload()");
                }
            }catch (SimplePartsInventoryNotEnoughException e){
                logger.error("仓库库存不足或故障!",e);
                WebUtil.addWarnMessage("仓库库存不足或故障");
                return;
            }
        }else{
            partsRequestLineItem.setNotTransactionQty(query.getRequestQty() - query.getTransactionQty());
            partsRequestLineItem.setTransactionQty(query.getTransactionQty());
            WebUtil.addWarnMessage("当前申购单明细入库/出库数量不能大于初始需入库/出库的数量");
            return;
        }
    }

    public void onRowCancel(RowEditEvent event) {
    }

    public boolean renderPartsDetails(Integer status, Integer proposerStatus) {

        if (null == status || null == proposerStatus) {
            return false;
        }

        if (proposerStatus == ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE && status == SimplePartsConstant.PARTS_NOT_TRANSACTIONED) {
            return true;
        }

        return false;
    }

}
