package com.ge.apm.view.simpleparts;

import com.ge.apm.dao.simpleparts.SimplePartsRequestLineItemRepository;
import com.ge.apm.dao.simpleparts.SimplePartsRequestRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.SimplePartsItemValue;
import com.ge.apm.domain.simpleparts.SimplePartsItem;
import com.ge.apm.domain.simpleparts.SimplePartsRequest;
import com.ge.apm.domain.simpleparts.SimplePartsRequestLineItem;
import com.ge.apm.domain.simpleparts.SimplePartsWarehouse;
import com.ge.apm.service.approval.MultiLevelApprovalService;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.simpleparts.SimplePartsCodeGenerationService;
import com.ge.apm.service.simpleparts.SimplePartsRequestLineItemService;
import com.ge.apm.service.simpleparts.SimplePartsRequestService;
import com.ge.apm.service.simpleparts.SimplePartsWarehouseService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.ge.apm.service.constant.SimplePartsConstant.PARTS_PURCHASE_FORM;
import static com.ge.apm.service.constant.SimplePartsConstant.PARTS_REQUEST_FORM;

/**
 * @author csh
 * @version 1.0
 * @date 2020/6/16
 * @descript 备件明细Controller
 * @package com.ge.apm.view.spareparts
 */
@ManagedBean
@ViewScoped
public class SimplePartsRequestEditController extends GenericCRUDUUIDController<SimplePartsRequest> implements ServerEventInterface {

    private static final long serialVersionUID = 7743041136165433030L;
    private static final Logger LOGGER = LoggerFactory.getLogger(SimplePartsRequestEditController.class);

    private static final DecimalFormat df = new DecimalFormat("######0.00");

    private SimplePartsRequestLineItemService partsRequestLineItemService;
    private SimplePartsRequestLineItemRepository partsRequestLineItemRepository;
    private SimplePartsRequestService partsRequestService;
    private MultiLevelApprovalService multiLevelApprovalService;
    private SimplePartsWarehouseService partsWarehouseService;
    private SimplePartsRequestRepository partsRequestRepository;
    private SimplePartsCodeGenerationService partsCodeGenerationService;

    @Getter
    @Setter
    private SimplePartsRequestLineItem newLineItem;

    @Setter
    private List<SimplePartsRequestLineItem> lineItems = new ArrayList<>();

    @Override
    protected void init() {
        partsRequestLineItemRepository = WebUtil.getBean(SimplePartsRequestLineItemRepository.class);
        partsRequestLineItemService = WebUtil.getBean(SimplePartsRequestLineItemService.class);
        partsRequestService = WebUtil.getBean(SimplePartsRequestService.class);
        multiLevelApprovalService = WebUtil.getBean(MultiLevelApprovalService.class);
        partsWarehouseService = WebUtil.getBean(SimplePartsWarehouseService.class);
        partsRequestRepository = WebUtil.getBean(SimplePartsRequestRepository.class);
        partsCodeGenerationService = WebUtil.getBean(SimplePartsCodeGenerationService.class);
    }

    public void createPartsRequest() {
        selected = new SimplePartsRequest();
        selected.setType(PARTS_REQUEST_FORM);

        String requestCode = partsCodeGenerationService.getNextRequestCode(selected.getType());
        selected.setRequestCode(requestCode);

        lineItems.clear();
    }

    public void createPartsPurchase() {
        selected = new SimplePartsRequest();
        selected.setType(PARTS_PURCHASE_FORM);

        String requestCode = partsCodeGenerationService.getNextRequestCode(selected.getType());
        selected.setRequestCode(requestCode);

        lineItems.clear();
    }

    public int getLineItemsSize() {
        return lineItems.size();
    }

    public boolean isLastEmptyRowInCreate(int rowIndex) {
        return rowIndex == lineItems.size();
    }

    public void onPartsItemChange(SelectEvent event) {
        SimplePartsItem selectedPartsItem = (SimplePartsItem) event.getObject();

        newLineItem = addSelectedPartsToLineItem(selectedPartsItem);

        if (PARTS_REQUEST_FORM == selected.getType()) {
            SimplePartsItemValue partsItemValue = partsRequestService.getInventoryNumAndAvgPriceByPartsItem(selectedPartsItem.getId(), selected.getPartsWarehouseId()).get();
            newLineItem.setUnitPrice(partsItemValue.getEstimatedUnitPrice());
            newLineItem.setPartsItemValue(partsItemValue);
        }
    }

    public List<SimplePartsWarehouse> getWarehouseListInRequest() {
        List<SimplePartsWarehouse> warehouseList = partsWarehouseService.findWarehouseListInRequest();
        return warehouseList;
    }

    public void onWarehouseChange(SelectEvent event) {

    }

    public SimplePartsRequestLineItem addSelectedPartsToLineItem(SimplePartsItem selectedPartsItem) {
        SimplePartsRequestLineItem partsRequestLineItem = new SimplePartsRequestLineItem();

        //parts info
        partsRequestLineItem.setName(selectedPartsItem.getName());
        partsRequestLineItem.setPartsItemId(selectedPartsItem.getId());
        partsRequestLineItem.setPartsModel(selectedPartsItem.getModel());
        partsRequestLineItem.setPartsSupplier(selectedPartsItem.getSupplier());
        partsRequestLineItem.setPartsBrand(selectedPartsItem.getBrand());

        //record timestamp
        partsRequestLineItem.setCreatedDate(new Date());
        partsRequestLineItem.setCreatedBy(UserContextService.getCurrentUserAccount().getName());

        partsRequestLineItem.setPartsItem(selectedPartsItem);

        return partsRequestLineItem;
    }

    public void addSelectedPartsToRequest() {
        //根据备件名称去查询申购单中的信息

        if (newLineItem.getRequestQty() == 0) {
            WebUtil.addWarnMessage("申请的备件数量不能为0");
            return;
        }

        if (newLineItem.getUnitPrice() == null) {
            WebUtil.addWarnMessage("申请的备件价格为空");
            return;
        }

        newLineItem.setTotalPrice(new BigDecimal(df.format(SimplePartsRequest.calculateTotalPrice(newLineItem.getRequestQty(), newLineItem.getUnitPrice()))));
        newLineItem.setCreatedBy(UserContextService.getCurrentUserAccount().getName());

        this.lineItems.add(newLineItem);

    }

    public boolean isRequestSubmitable() {
        return lineItems.size() == 0 ? false : true;
    }

    public BigDecimal getTotalPrice() {
        BigDecimal totalPrice = new BigDecimal(0);

        for (SimplePartsRequestLineItem lineItem : lineItems) {
            totalPrice = totalPrice.add(lineItem.getTotalPrice());
        }

        return totalPrice;
    }

    public int getTotalCount() {
        int totalCount = 0;

        for (SimplePartsRequestLineItem lineItem : lineItems) {
            totalCount = totalCount + lineItem.getRequestQty();
        }

        return totalCount;
    }

    public List<SimplePartsRequestLineItem> getLineItemsWithOptionalActionRow() {

        List<SimplePartsRequestLineItem> resultList = new ArrayList<>(lineItems);
        if (!isEditPartsRequest()) {
            SimplePartsRequestLineItem placeHolderRow = new SimplePartsRequestLineItem();
            resultList.add(placeHolderRow);
        }

        return resultList;
    }

    private boolean isEditPartsRequest() {
        if (null == selected) {
            return false;
        }

        if (null == selected.getId()) {
            return false;
        }

        return true;
    }

    /**
     * @return void
     * @throws
     * @params []
     * @author csh
     * @createTime 2020/6/9 15:04
     * @description 保存备件申购单据
     */
    public String savePartsRequest() {
        try {
            selected.setTotalPrice(getTotalPrice());
            selected.setRequestQty(getTotalCount());
            selected.setCreatedDate(new Date());
            selected.setCreatedBy(UserContextService.getCurrentUserAccount().getName());
            // 保存单据总计
            if (isEditPartsRequest()) {
                selected.setProposerStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_ING);
            } else {

                List<SimplePartsRequest> rc = partsRequestService.findByRequestCode(selected.getRequestCode());
                if (rc.size() > 0) { //如果申请单号已经被保存，再生成一个申请单号
                    String requestCode = partsCodeGenerationService.getNextRequestCode(selected.getType());
                    selected.setRequestCode(requestCode);
                }
            }

            partsRequestService.savePartsRequestWithProvidedData(selected, lineItems);

            // 从配置的审批多级审批流程中获取到审批人
            MultiLevelApproval multiLevelApprovalPersionAndSubmit = multiLevelApprovalService.getMultiLevelApprovalPersonAndSubmit(selected, null, false);
            if (multiLevelApprovalPersionAndSubmit == null) {
                //沒有找到合适的审批直接入库
                selected.setProposerStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE);
                partsRequestService.save(selected);
                WebUtil.addWarnMessage("可以直接进行库存操作");
            } else {
                WebUtil.addWarnMessage("审批流发起成功");
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return getListPageLink();
    }

    public String getListPageLink() {
        if (PARTS_REQUEST_FORM == selected.getType()) {
            return "PartsRequest?faces-redirect=true";
        } else {
            return "PartsPurchase?faces-redirect=true";
        }
    }

    public void validateRequestQty(String partsItemId, Integer requestQty) {

        if (requestQty <= 0) {
            WebUtil.addWarnMessage("请输入正确的库存数量！！");
            return;
        }

        Optional<SimplePartsItemValue> partsItemValueOptional = partsRequestService.getInventoryNumAndAvgPriceByPartsItem(partsItemId,selected.getPartsWarehouseId());

        if (!partsItemValueOptional.isPresent()) {
            WebUtil.addWarnMessage("库存不足！！");
            return;
        }

        if (requestQty > partsItemValueOptional.get().getInStockNum()) {
            WebUtil.addWarnMessage("库存可能不足，请输入正确的库存数量！！");
            return;
        }

    }


    /**
     * @return void
     * @params []
     * @author csh
     * @createTime 2020/6/12 10:28
     * @description 清空备件列表
     */
    public void removeLineItem() {

        newLineItem = new SimplePartsRequestLineItem();
        newLineItem.setRequestQty(0);
        newLineItem.setUnitPrice(new BigDecimal(0));

    }

    /**
     * @return void
     * @params [sparePartsDocument]
     * @author csh
     * @createTime 2020/6/12 10:36
     * @description 删除集合中指定的元素 TODO
     */
    public void deleteCurrentPartsLineItem(SimplePartsRequestLineItem currentLineItem) {
        this.lineItems.remove(currentLineItem);
    }

    /**
     *
     */
    public void initPartsRequestLineItem() {
        newLineItem = new SimplePartsRequestLineItem();
    }

    public void onRowEdit(RowEditEvent event) {
        SimplePartsRequestLineItem lineItem = (SimplePartsRequestLineItem)event.getObject();

        SimplePartsRequestLineItem query = partsRequestLineItemRepository.findById(lineItem.getId());

        if(lineItem.getRequestQty() <= 0){
            lineItem.setRequestQty(query.getRequestQty());
            lineItem.setUnitPrice(query.getUnitPrice());
            WebUtil.addWarnMessage("备件申请数量要大于0");
            return;
        }

        if(lineItem.getUnitPrice() == null || lineItem.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0){
            lineItem.setRequestQty(query.getRequestQty());
            lineItem.setUnitPrice(query.getUnitPrice());
            WebUtil.addWarnMessage("请填写正确备件申请价格");
            return;
        }

        lineItem.setTotalPrice(SimplePartsRequest.calculateTotalPrice(lineItem.getRequestQty(),lineItem.getUnitPrice()));
    }

    public void onRowCancel(RowEditEvent event) {

    }

    public boolean isSelectedPartsInputValid() {
        if (null == newLineItem) {
            return false;
        }

        if (newLineItem.getRequestQty() <= 0) {
            return false;
        }

        if (null == newLineItem.getUnitPrice()) {
            return false;
        }

        return true;
    }

    @Override
    protected GenericRepositoryUUID<SimplePartsRequest> getDAO() {
        return partsRequestRepository;
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {

        if (SimplePartsRequestListController.EDIT_PARTS_REQUEST.equals(eventName)) {
            selected = (SimplePartsRequest) eventObject;
            lineItems = partsRequestLineItemService.findLineItemsByRequestCode(selected.getRequestCode());
        }

    }
}
