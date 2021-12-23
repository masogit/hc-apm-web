package com.ge.apm.service.simpleparts;

import com.ge.apm.dao.simpleparts.SimplePartsInventoryRepository;
import com.ge.apm.dao.simpleparts.SimplePartsRequestLineItemRepository;
import com.ge.apm.dao.simpleparts.SimplePartsRequestRepository;
import com.ge.apm.dao.simpleparts.SimplePartsTransactionRepository;
import com.ge.apm.dao.mapper.simpleparts.SimplePartsInventoryMapper;
import com.ge.apm.dao.mapper.simpleparts.SimplePartsTransactionMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.SimplePartsItemValue;
import com.ge.apm.domain.simpleparts.*;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.constant.SimplePartsConstant;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author csh
 * @version 1.0
 * @date 2020/6/8
 * @descript 收据业务
 * @package com.ge.apm.service.spareParts
 */
@Service
@Transactional
public class SimplePartsRequestService {
    private static final Logger log = LoggerFactory.getLogger(SimplePartsRequestService.class);

    @Autowired
    private SimplePartsRequestRepository partsRequestRepository;

    @Autowired
    private SimplePartsRequestLineItemRepository partsRequestLineItemRepository;

    @Autowired
    private SimplePartsWarehouseService partsWarehouseService;

    @Autowired
    private SimplePartsTransactionMapper partsTransactionMapper;

    @Autowired
    private SimplePartsInventoryMapper partsInventoryMapper;

    @Autowired
    private SimplePartsInventoryRepository partsInventoryRepository;

    @Autowired
    private SimplePartsTransactionRepository partsTransactionRepository;

    @Autowired
    private SimplePartsRequestLineItemService partsRequestLineItemService;

    @Autowired
    private SimplePartsCodeGenerationService partsCodeGenerationService;

    /**
     * @return void
     * @throws
     * @params [partsRequest]
     * @author csh
     * @createTime 2020/6/8 15:56
     * @description 保存数据
     */
    public void save(SimplePartsRequest partsRequest) {
        partsRequestRepository.save(partsRequest);
    }

    /**
     * @return void
     * @params [partsRequest]
     * @author csh
     * @createTime 2020/6/12 10:39
     * @description 批量保存数据
     */
    public void save(List<SimplePartsRequest> partsRequest) {
        partsRequestRepository.save(partsRequest);
    }

    /**
     * @param type   申购表单
     * @param status 状态未入库
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsRequest>
     * @params [type, status]
     * @author csh0
     * @createTime 2020/6/15 14:51
     * @description 申购中的备件
     */
    public List<SimplePartsRequest> findByTypeAndStatus(Integer type, Integer status) {
        return partsRequestRepository.findByTypeAndStatus(type, status);
    }

    /**
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsRequest>
     * @params [requestCode]
     * @author csh
     * @createTime 2020/6/17 17:49
     * @description 根据订单编号查询到未审批的订单
     */
    public List<SimplePartsRequest> findByRequestCode(String requestCode) {
        return partsRequestRepository.findByRequestCode(requestCode);
    }

    /**
     * get remaining count and avg price for the parts item based on the current inveontry
     * @param partsItemId
     * @return
     */
    public Optional<SimplePartsItemValue> getInventoryNumAndAvgPriceByPartsItem(String partsItemId, String partsWarehouseId) {

        List<String> warehouseIds = Lists.newArrayList();
        List<SimplePartsWarehouse> partsWarehouseList = partsWarehouseService.findWarehouseListInRequest();

        if (partsWarehouseList.size() == 0) {
            log.info("查询备件交易记录和备件库存时,无可用仓库");
            return Optional.of(new SimplePartsItemValue(0,new BigDecimal("0").setScale(2,   BigDecimal.ROUND_HALF_UP)));
        }
        if(StringUtils.isNotEmpty(partsWarehouseId)){
            warehouseIds.add(partsWarehouseId);
        } else {
            warehouseIds.addAll(partsWarehouseList.stream().map(item -> item.getId()).collect(Collectors.toList()));
        }
        BigDecimal avgPrice = partsTransactionMapper.getAvgPriceByPartsItem(partsItemId, warehouseIds);
        Integer inventoryNum = partsInventoryMapper.getInventoryNumByPartsItem(partsItemId, warehouseIds);
        SimplePartsItemValue itemValue = new SimplePartsItemValue(inventoryNum == null ? 0:inventoryNum , avgPrice== null ? new BigDecimal("0").setScale(2,   BigDecimal.ROUND_HALF_UP):avgPrice.setScale(2,   BigDecimal.ROUND_HALF_UP));
        return Optional.of(itemValue);
    }

    /**
     * stock in single parts
     * @param partsRequestLineItem
     * @param partsRequest
     */
    public void stockInParts(SimplePartsRequestLineItem partsRequestLineItem, SimplePartsRequest partsRequest) {

        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();

        SimplePartsTransaction partsTransaction = new SimplePartsTransaction();
        partsTransaction.setSourcSupplier(partsRequestLineItem.getPartsSupplier());
        partsTransaction.setRequestCode(partsRequestLineItem.getRequestCode());
        partsTransaction.setPartsRequestId(partsRequestLineItem.getPartsRequestId());
        partsTransaction.setPartsRequestLineItemId(partsRequestLineItem.getId());
        partsTransaction.setCreatedBy(partsRequestLineItem.getCreatedBy());
        partsTransaction.setCreatedDate(new Date());
        partsTransaction.setTransactionQty(partsRequestLineItem.getRequestQty());
        partsTransaction.setPartsItemId(partsRequestLineItem.getPartsItemId());
        partsTransaction.setName(partsRequestLineItem.getName());
        partsTransaction.setUnitPrice(partsRequestLineItem.getUnitPrice());
        partsTransaction.setTotalPrice(partsRequestLineItem.getTotalPrice());
        partsTransaction.setPartsWarehouseId(partsRequestLineItem.getPartsWarehouseId());
        partsTransaction.setPartsLocatorId(partsRequestLineItem.getPartsLocatorId());

        partsTransaction.setRequestType(partsRequest.getType());
        partsTransaction.setTransactionType(SimplePartsConstant.TRANSACTION_TYPE_IN_STOCK);

        // 增加相对应的权限，例如租户、院区、等
        SimplePartsTransactionService.addCurrentOrgInfo(partsTransaction);

        partsTransactionRepository.save(partsTransaction);
    }

    /**
     * stock in the whole request
     * @param partsRequest
     */
    @Transactional
    public synchronized void stockInRequest(SimplePartsRequest partsRequest, List<SimplePartsRequestLineItem> partsRequestLineItemList) {

        Date createDate = new Date();
        // 增加相对应的权限，例如租户、院区、等
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        partsRequestLineItemList.stream().forEach(item -> {
            SimplePartsTransaction partsTransaction = new SimplePartsTransaction();
            partsTransaction.setSourcSupplier(item.getPartsSupplier());
            partsTransaction.setRequestCode(item.getRequestCode());
            partsTransaction.setPartsRequestId(partsRequest.getId());
            partsTransaction.setPartsRequestLineItemId(item.getId());
            partsTransaction.setCreatedBy(currentUserAccount.getLoginName());
            partsTransaction.setCreatedDate(createDate);
            partsTransaction.setTransactionQty(item.getRequestQty() - item.getTransactionQty());
            partsTransaction.setPartsItemId(item.getPartsItemId());
            partsTransaction.setName(item.getName());
            partsTransaction.setUnitPrice(item.getUnitPrice());
            int qty = item.getRequestQty() - item.getTransactionQty();
            partsTransaction.setTotalPrice(item.getUnitPrice().multiply(new BigDecimal(qty)));
            partsTransaction.setPartsWarehouseId(item.getPartsWarehouseId());
            partsTransaction.setPartsLocatorId(item.getPartsLocatorId());

            partsTransaction.setRequestType(partsRequest.getType());
            partsTransaction.setTransactionType(SimplePartsConstant.TRANSACTION_TYPE_IN_STOCK);

            partsTransaction.setComments(item.getComments());//备注
            partsTransaction.setStorageType(item.getStorageType());//入库类型
            partsTransaction.setAddressInfo(item.getAddressInfo());//地址信息

            SimplePartsTransactionService.addCurrentOrgInfo(partsTransaction);

            partsTransactionRepository.save(partsTransaction);

            item.setStatus(SimplePartsConstant.PARTS_TRANSACTIONED);
            partsRequestLineItemRepository.save(item);

            // 将数据拷贝到总库存中
            SimplePartsInventory partsInventory;

            Optional<SimplePartsInventory> partsInventoryOptional = partsInventoryRepository.findByWarehouseIdAndLocatorIdAndPartsItemId(item.getPartsWarehouseId(), item.getPartsLocatorId(), item.getPartsItemId());
            if (partsInventoryOptional.isPresent()) {
                partsInventory = partsInventoryOptional.get();
                // 更新
                int sumNum = partsInventory.getOnhandQty() + (item.getRequestQty() - item.getTransactionQty());
                partsInventory.setOnhandQty(sumNum);
            } else {
                // 新增
                partsInventory = new SimplePartsInventory();
                partsInventory.setPartsItemId(item.getPartsItemId());
                partsInventory.setPartsLocatorId(item.getPartsLocatorId());
                partsInventory.setPartsWarehouseId(item.getPartsWarehouseId());
                partsInventory.setName(item.getName());
                partsInventory.setOnhandQty(item.getRequestQty() - item.getTransactionQty());

                SimplePartsInventoryService.addCurrentOrgInfo(partsInventory);

            }

            partsInventoryRepository.save(partsInventory);
        });

        partsRequest.setStatus(SimplePartsConstant.PARTS_TRANSACTIONED);
        partsRequestRepository.save(partsRequest);
        if(partsRequest.getMaintenanceWorkOrderNumber() != null){
            partsRequest.setId(null);
            partsRequest.setCreatedDate(new Date());
            List<SimplePartsRequest> rc = this.findByRequestCode(partsRequest.getRequestCode());
            if (rc.size() > 0) {
                String requestCode = partsCodeGenerationService.getNextRequestCode(SimplePartsConstant.PARTS_REQUEST_FORM);
                partsRequest.setRequestCode(requestCode);
            }
            partsRequestLineItemList.stream().forEach(item -> {
                item.setId(null);
            });
            this.saveNewPartsRequest(partsRequest,partsRequestLineItemList,false);
        }
    }

    @Transactional(rollbackFor = SimplePartsInventoryNotEnoughException.class)
    public void stockoutRequest(SimplePartsRequest partsRequest, List<SimplePartsRequestLineItem> partsRequestLineItemList)
            throws SimplePartsInventoryNotEnoughException {
        // 增加相对应的权限，例如租户、院区、等
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();

        for (SimplePartsRequestLineItem item: partsRequestLineItemList) {
            // 更新 总的库存
            Optional<SimplePartsInventory> partsInventoryOptional = partsInventoryRepository.findByWarehouseIdAndLocatorIdAndPartsItemId(item.getPartsWarehouseId(), item.getPartsLocatorId(), item.getPartsItemId());

            if (!partsInventoryOptional.isPresent()) {
                throw new SimplePartsInventoryNotEnoughException("no inventory");
            }

            SimplePartsInventory partsInventory = partsInventoryOptional.get();
            int newInventoryNum = partsInventory.getOnhandQty() - (item.getRequestQty() - item.getTransactionQty());

            if (newInventoryNum < 0) {
                throw new SimplePartsInventoryNotEnoughException("no inventory");
            }

            if (newInventoryNum == 0) {
                partsInventoryRepository.deleteByWarehouseIdAndLocatorIdAndPartsItemId(item.getPartsWarehouseId(), item.getPartsLocatorId(), item.getPartsItemId());
            } else {
                partsInventory.setOnhandQty(partsInventory.getOnhandQty() -  (item.getRequestQty() - item.getTransactionQty()));
                partsInventoryRepository.save(partsInventory);
            }

            SimplePartsTransaction partsTransaction = new SimplePartsTransaction();
            partsTransaction.setSourcSupplier(item.getPartsSupplier());
            partsTransaction.setRequestCode(item.getRequestCode());
            partsTransaction.setPartsRequestId(partsRequest.getId());
            partsTransaction.setPartsRequestLineItemId(item.getId());
            partsTransaction.setCreatedBy(currentUserAccount.getLoginName());
            partsTransaction.setCreatedDate(new Date());
            partsTransaction.setTransactionQty(item.getRequestQty() - item.getTransactionQty());
            partsTransaction.setPartsItemId(item.getPartsItemId());
            partsTransaction.setName(item.getName());
            partsTransaction.setUnitPrice(item.getUnitPrice());
            int qty = item.getRequestQty() - item.getTransactionQty();
            partsTransaction.setTotalPrice(item.getUnitPrice().multiply(new BigDecimal(qty)));
            partsTransaction.setPartsWarehouseId(item.getPartsWarehouseId());
            partsTransaction.setPartsLocatorId(item.getPartsLocatorId());
            partsTransaction.setRequestType(partsRequest.getType());
            partsTransaction.setTransactionType(SimplePartsConstant.TRANSACTION_TYPE_OUT_STOCK);

            partsTransaction.setTransportNo(item.getTransportNo()); //运单号
            partsTransaction.setTransportCompany(item.getTransportCompany());   //运输公司
            partsTransaction.setAddressInfo(item.getAddressInfo());//地址信息

            SimplePartsTransactionService.addCurrentOrgInfo(partsTransaction);
            partsTransactionRepository.save(partsTransaction);

            item.setStatus(SimplePartsConstant.PARTS_TRANSACTIONED);
            partsRequestLineItemService.save(item);
        }

        partsRequest.setStatus(SimplePartsConstant.PARTS_TRANSACTIONED);
        partsRequestRepository.save(partsRequest);
    }

    public void saveNewPartsRequest(SimplePartsRequest partsRequest, List<SimplePartsRequestLineItem> partsRequestLineItemList, boolean isApproval) {
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();

        // 申领单类型
        partsRequest.setType(SimplePartsConstant.PARTS_REQUEST_FORM);
        // 未出库
        partsRequest.setStatus(SimplePartsConstant.PARTS_NOT_TRANSACTIONED);
        // 未审批
        partsRequest.setProposerId(currentUserAccount.getId());
        if(isApproval){
            partsRequest.setProposerStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_ING);
        }else {
            partsRequest.setProposerStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE);
        }

        addCurrentOrgInfo(partsRequest);

        partsRequestRepository.save(partsRequest);

        // 备件明细保存
        partsRequestLineItemList.stream().forEach(item -> {
            item.setPartsRequestId(partsRequest.getId());
            item.setRequestCode(partsRequest.getRequestCode());
            item.setCreatedBy(currentUserAccount.getLoginName());

            SimplePartsRequestLineItemService.addCurrentOrgInfo(item);

            // 未出库
            item.setStatus(SimplePartsConstant.PARTS_NOT_TRANSACTIONED);
        });

        partsRequestLineItemService.save(partsRequestLineItemList);

    }

    public void savePartsRequestWithProvidedData(SimplePartsRequest partsRequest, List<SimplePartsRequestLineItem> partsRequestLineItemList) {
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();

        //未出入库
        partsRequest.setStatus(SimplePartsConstant.PARTS_NOT_TRANSACTIONED);

        // 未审批
        partsRequest.setCreatedBy(currentUserAccount.getLoginName());
        partsRequest.setCreatedDate(new Date());

        partsRequest.setProposerId(currentUserAccount.getId());
        partsRequest.setProposerDate(new Date());
        partsRequest.setProposerStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_ING);

        addCurrentOrgInfo(partsRequest);

        partsRequestRepository.save(partsRequest);

        // 单个备件保存
        partsRequestLineItemList.stream().forEach(item -> {
            item.setPartsRequestId(partsRequest.getId());
            item.setRequestCode(partsRequest.getRequestCode());
            item.setProposerDate(new Date());

            item.setCreatedBy(currentUserAccount.getLoginName());
            item.setCreatedDate(new Date());

            SimplePartsRequestLineItemService.addCurrentOrgInfo(item);
            // 未出入库
            item.setStatus(SimplePartsConstant.PARTS_NOT_TRANSACTIONED);

        });

        partsRequestLineItemRepository.save(partsRequestLineItemList);
    }

    public static void addCurrentOrgInfo(SimplePartsRequest partsRequest) {
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();

        partsRequest.setTenantUID(currentUserAccount.getTenantUID());
        partsRequest.setInstitutionUID(currentUserAccount.getInstitutionUID());
        partsRequest.setHospitalUID(currentUserAccount.getHospitalUID());
        partsRequest.setSiteUID(currentUserAccount.getSiteUID());
        partsRequest.setClinicalDeptUID(currentUserAccount.getClinicalDeptUID());
    }

    public static void addCurrentOrgInfo(SimplePartsRequest partsRequest, UserAccount userAccountFromWorkOrder) {

        partsRequest.setTenantUID(userAccountFromWorkOrder.getTenantUID());
        partsRequest.setInstitutionUID(userAccountFromWorkOrder.getInstitutionUID());
        partsRequest.setHospitalUID(userAccountFromWorkOrder.getHospitalUID());
        partsRequest.setSiteUID(userAccountFromWorkOrder.getSiteUID());
        partsRequest.setClinicalDeptUID(userAccountFromWorkOrder.getClinicalDeptUID());
    }
}
