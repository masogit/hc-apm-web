package com.ge.apm.service.simpleparts;

import com.ge.apm.dao.simpleparts.SimplePartsInventoryRepository;
import com.ge.apm.dao.simpleparts.SimplePartsRequestLineItemRepository;
import com.ge.apm.dao.simpleparts.SimplePartsRequestRepository;
import com.ge.apm.dao.simpleparts.SimplePartsTransactionRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.simpleparts.SimplePartsInventory;
import com.ge.apm.domain.simpleparts.SimplePartsRequest;
import com.ge.apm.domain.simpleparts.SimplePartsRequestLineItem;
import com.ge.apm.domain.simpleparts.SimplePartsTransaction;
import com.ge.apm.service.constant.SimplePartsConstant;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author csh
 * @version 1.0
 * @date 2020/6/16
 * @descript 备件明细Service
 * @package com.ge.apm.service.spareparts
 */
@Service
public class SimplePartsRequestLineItemService {

    @Autowired
    private SimplePartsRequestLineItemRepository partsRequestLineItemRepository;

    @Autowired
    private SimplePartsInventoryRepository partsInventoryRepository;

    @Autowired
    private SimplePartsInventoryService partsInventoryService;

    @Autowired
    private SimplePartsTransactionRepository partsTransactionRepository;

    @Autowired
    private SimplePartsTransactionService partsTransactionService;

    @Autowired
    private SimplePartsRequestRepository partsRequestRepository;

    @Autowired
    EntityManager entityManager;

    /**
     * @return void
     * @params [partsRequestLineItemLists]
     * @author csh
     * @createTime 2020/6/16 8:54
     * @description 批量保存数据
     */
    @Transactional
    public void save(List<SimplePartsRequestLineItem> partsRequestLineItemLists) {
        partsRequestLineItemRepository.save(partsRequestLineItemLists);
    }

    /**
     * @return void
     * @params [partsRequestLineItem]
     * @author csh
     * @createTime 2020/6/16 10:42
     * @description 单个更新或保存
     */
    @Transactional
    public SimplePartsRequestLineItem save(SimplePartsRequestLineItem partsRequestLineItem) {
       return partsRequestLineItemRepository.save(partsRequestLineItem);
    }

    /**
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsRequestLineItem>
     * @params [requestCode]
     * @author csh
     * @createTime 2020/6/16 9:23
     * @description 根据申购单的编号查询
     */
    @Transactional(readOnly = true)
    public List<SimplePartsRequestLineItem> findLineItemsByRequestCode(String requestCode) {
        return partsRequestLineItemRepository.findPartsRequestLineItemsByRequestCode(requestCode);
    }

    public static void addCurrentOrgInfo(SimplePartsRequestLineItem lineItem) {
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();

        lineItem.setTenantUID(currentUserAccount.getTenantUID());
        lineItem.setInstitutionUID(currentUserAccount.getInstitutionUID());
        lineItem.setHospitalUID(currentUserAccount.getHospitalUID());
        lineItem.setSiteUID(currentUserAccount.getSiteUID());
        lineItem.setClinicalDeptUid(currentUserAccount.getClinicalDeptUID());
    }

    public static void addCurrentOrgInfo(SimplePartsRequestLineItem lineItem, UserAccount userAccountFromWorkOrder) {

        lineItem.setTenantUID(userAccountFromWorkOrder.getTenantUID());
        lineItem.setInstitutionUID(userAccountFromWorkOrder.getInstitutionUID());
        lineItem.setHospitalUID(userAccountFromWorkOrder.getHospitalUID());
        lineItem.setSiteUID(userAccountFromWorkOrder.getSiteUID());
        lineItem.setClinicalDeptUid(userAccountFromWorkOrder.getClinicalDeptUID());
    }

    /**
     * stock in single parts
     * @param partsRequestLineItem
     */
    @Transactional
    public synchronized void stockInOrOutParts(SimplePartsRequestLineItem partsRequestLineItem) throws SimplePartsInventoryNotEnoughException {

        SimplePartsRequest partsRequest = partsRequestRepository.findById(partsRequestLineItem.getPartsRequestId());
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        //处理交易记录
        SimplePartsTransaction partsTransaction = new SimplePartsTransaction();
        partsTransaction.setSourcSupplier(partsRequestLineItem.getPartsSupplier());
        partsTransaction.setRequestCode(partsRequestLineItem.getRequestCode());
        partsTransaction.setPartsRequestId(partsRequestLineItem.getPartsRequestId());
        partsTransaction.setPartsRequestLineItemId(partsRequestLineItem.getId());
        partsTransaction.setCreatedBy(currentUserAccount.getLoginName());
        partsTransaction.setCreatedDate(new Date());
        partsTransaction.setTransactionQty(partsRequestLineItem.getNotTransactionQty());
        partsTransaction.setPartsItemId(partsRequestLineItem.getPartsItemId());
        partsTransaction.setName(partsRequestLineItem.getName());
        partsTransaction.setUnitPrice(partsRequestLineItem.getUnitPrice());
        partsTransaction.setTotalPrice(partsRequestLineItem.getUnitPrice().multiply(new BigDecimal(partsRequestLineItem.getNotTransactionQty())));
        partsTransaction.setPartsWarehouseId(partsRequestLineItem.getPartsWarehouseId());
        partsTransaction.setPartsLocatorId(partsRequestLineItem.getPartsLocatorId());

        partsTransaction.setTransportNo(partsRequestLineItem.getTransportNo());//运单号
        partsTransaction.setTransportCompany(partsRequestLineItem.getTransportCompany());//运输公司
        partsTransaction.setComments(partsRequestLineItem.getComments());//备注
        partsTransaction.setStorageType(partsRequestLineItem.getStorageType());//入库类型
        partsTransaction.setAddressInfo(partsRequestLineItem.getAddressInfo());//地址信息
        partsTransaction.setPartsDescription(partsRequestLineItem.getPartsDescription());//备件描述




        partsTransaction.setRequestType(partsRequest.getType());
        partsTransaction.setTransactionType(partsRequest.getType() == 1? SimplePartsConstant.TRANSACTION_TYPE_OUT_STOCK : SimplePartsConstant.TRANSACTION_TYPE_IN_STOCK);

        partsTransactionService.addCurrentOrgInfo(partsTransaction);
        partsTransactionRepository.save(partsTransaction);

        // 处理库存,将数据拷贝到总库存中
        SimplePartsInventory partsInventory;
        Optional<SimplePartsInventory> partsInventoryOptional = partsInventoryRepository.findByWarehouseIdAndLocatorIdAndPartsItemId(partsRequestLineItem.getPartsWarehouseId(), partsRequestLineItem.getPartsLocatorId(), partsRequestLineItem.getPartsItemId());

        if(partsRequest.getType() == 1){
            if (partsInventoryOptional.isPresent()) {
                partsInventory = partsInventoryOptional.get();
                // 更新
                int sumNum = partsInventory.getOnhandQty() - partsRequestLineItem.getNotTransactionQty();
                if( sumNum < 0){
                    // 库存不足
                    throw new SimplePartsInventoryNotEnoughException("no inventory");
                }

                if(sumNum == 0){
                    partsInventoryRepository.deleteByWarehouseIdAndLocatorIdAndPartsItemId(partsRequestLineItem.getPartsWarehouseId(), partsRequestLineItem.getPartsLocatorId(), partsRequestLineItem.getPartsItemId());
                    entityManager.detach(partsInventory);
                }else{
                    partsInventory.setOnhandQty(sumNum);
                    partsInventoryRepository.save(partsInventory);
                }
            } else{
                // 库存不足
                throw new SimplePartsInventoryNotEnoughException("no inventory");
            }
        }else{
            if (partsInventoryOptional.isPresent()) {
                partsInventory = partsInventoryOptional.get();
                // 更新
                int sumNum = partsInventory.getOnhandQty() + partsRequestLineItem.getNotTransactionQty();
                partsInventory.setOnhandQty(sumNum);
            } else{
                // 新增
                partsInventory = new SimplePartsInventory();
                partsInventory.setPartsItemId(partsRequestLineItem.getPartsItemId());
                partsInventory.setPartsLocatorId(partsRequestLineItem.getPartsLocatorId());
                partsInventory.setPartsWarehouseId(partsRequestLineItem.getPartsWarehouseId());
                partsInventory.setName(partsRequestLineItem.getName());
                partsInventory.setOnhandQty(partsRequestLineItem.getNotTransactionQty());
                partsInventoryService.addCurrentOrgInfo(partsInventory);
            }
            partsInventoryRepository.save(partsInventory);
        }

        //处理备件明细
        if(String.valueOf(partsRequestLineItem.getRequestQty()).equals(String.valueOf(partsRequestLineItem.getTransactionQty() + partsRequestLineItem.getNotTransactionQty()))){
            partsRequestLineItem.setStatus(1);
        }
        partsRequestLineItem.setTransactionQty(partsRequestLineItem.getTransactionQty() + partsRequestLineItem.getNotTransactionQty());
        partsRequestLineItem.setNotTransactionQty(partsRequestLineItem.getRequestQty() - partsRequestLineItem.getTransactionQty());
        partsRequestLineItemRepository.save(partsRequestLineItem);

        //处理申请单状态
        List<SimplePartsRequestLineItem> partsRequestLineItems = partsRequestLineItemRepository.findByRequestCode(partsRequest.getRequestCode());
        partsRequestLineItems = partsRequestLineItems.stream().filter(item -> item.getStatus() != SimplePartsConstant.PARTS_TRANSACTIONED).collect(Collectors.toList());
        if(partsRequestLineItems.size() == 0){
            partsRequest.setStatus(SimplePartsConstant.PARTS_TRANSACTIONED);
            partsRequestRepository.save(partsRequest);
        }
    }

}
