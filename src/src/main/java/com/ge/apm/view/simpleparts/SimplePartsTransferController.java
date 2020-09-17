package com.ge.apm.view.simpleparts;

import com.ge.apm.dao.simpleparts.SimplePartsInventoryRepository;
import com.ge.apm.dao.simpleparts.SimplePartsTransactionRepository;
import com.ge.apm.dao.simpleparts.SimplePartsWarehouseLocatorRepository;
import com.ge.apm.dao.mapper.simpleparts.SimplePartsInventoryMapper;
import com.ge.apm.domain.simpleparts.*;
import com.ge.apm.service.constant.SimplePartsConstant;
import com.ge.apm.service.simpleparts.SimplePartsConfigService;
import com.ge.apm.service.simpleparts.SimplePartsInventoryService;
import com.ge.apm.service.simpleparts.SimplePartsTransferService;
import com.ge.apm.service.simpleparts.SimplePartsWarehouseService;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Collections;
import java.util.List;

@ManagedBean
@ViewScoped
public class SimplePartsTransferController extends GenericCRUDUUIDController<SimplePartsRequest>  {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimplePartsTransferController.class);
    private static final long serialVersionUID = -483251039974336475L;

    private SimplePartsInventoryRepository partsInventoryRepository;
    private SimplePartsInventoryService partsInventoryService;
    private SimplePartsTransactionRepository partsTransactionRepository;
    private SimplePartsWarehouseLocatorRepository partsWarehouseLocatorRepository;
    private SimplePartsWarehouseService partsWarehouseService;
    private SimplePartsConfigReadController partsConfigReadController;
    private SimplePartsTransferService partsTransferService;
    private SimplePartsInventoryMapper partsInventoryMapper;
    private SimplePartsConfigService partsConfigService;

    @Getter @Setter
    private String sourcePartsWarehouseId;

    @Getter @Setter
    private String sourcePartsWarehouseLocatorId;

    @Getter @Setter
    private String sourcePartsItemId;

    @Getter @Setter
    private int sourcePartsItemNum;

    @Getter @Setter
    private String targetPartsWarehouseId;

    @Getter @Setter
    private String targetPartsWarehouseLocatorId;

    @Getter @Setter
    private List<SimplePartsWareHouseLocator> partsWareHouseLocatorList;

    @Getter @Setter
    private List<SimplePartsWareHouseLocator> targetPartsWareHouseLocatorList;

    @Getter @Setter
    private List<SimplePartsItem> sourcePartsItemsInInventoryList;

    private SimplePartsInventory partsInventory;


    @Override
    protected void init() {
        partsInventoryRepository = WebUtil.getBean(SimplePartsInventoryRepository.class);
        partsInventoryService = WebUtil.getBean(SimplePartsInventoryService.class);
        partsTransactionRepository = WebUtil.getBean(SimplePartsTransactionRepository.class);
        partsWarehouseLocatorRepository = WebUtil.getBean(SimplePartsWarehouseLocatorRepository.class);
        partsConfigReadController = WebUtil.getBean(SimplePartsConfigReadController.class);
        partsConfigService = WebUtil.getBean(SimplePartsConfigService.class);
        partsWarehouseService = WebUtil.getBean(SimplePartsWarehouseService.class);
        partsTransferService = WebUtil.getBean(SimplePartsTransferService.class);
        partsInventoryMapper = WebUtil.getBean(SimplePartsInventoryMapper.class);
    }

    @Override
    protected GenericRepositoryUUID<SimplePartsRequest> getDAO() {
        return null;
    }



    public List<SimplePartsWarehouse> getSourceWarehouseList() {
        List<SimplePartsWarehouse> warehouseList = partsWarehouseService.findWarehouseListInRequest();
        return warehouseList;
    }

    public List<SimplePartsWarehouse> getTargetWarehouseList() {
        List<SimplePartsWarehouse> targetWarehouseList = partsWarehouseService.findWarehouseListInRequest();
        return targetWarehouseList;
    }

    public void onSourceWarehouseChange(){
        partsWareHouseLocatorList = this.getSourcePartsWareHouseLocatorList();

        if (!partsConfigReadController.isEnableWarehouseLocator()) {
            sourcePartsWarehouseLocatorId = SimplePartsConstant.VIRTUAL_LOCATOR_ID;
        }
    }

    public void onSourceWarehouseLocatorChange(){
        sourcePartsItemsInInventoryList = this.getSourcePartsItemsInInventoryList();
    }

    public void onPartsItemChange(){
        if(null == sourcePartsItemId){
            return ;
        }else {
            partsInventory = partsInventoryRepository.findByWarehouseIdAndLocatorIdAndPartsItemIdAndPartsItemId(sourcePartsWarehouseId, sourcePartsWarehouseLocatorId, sourcePartsItemId);
            sourcePartsItemNum = partsInventory == null ? 0 : partsInventory.getOnhandQty();
        }
    }

    public void onTargetWarehouseChange(){
        targetPartsWareHouseLocatorList = this.getTargetPartsWareHouseLocatorList();
        if (!partsConfigReadController.isEnableWarehouseLocator()) {
            targetPartsWarehouseLocatorId = SimplePartsConstant.VIRTUAL_LOCATOR_ID;
        }
    }

    public List<SimplePartsWareHouseLocator> getSourcePartsWareHouseLocatorList() {
        if(null == sourcePartsWarehouseId){
            return Collections.EMPTY_LIST;
        }

        List<SimplePartsWareHouseLocator> partsWareHouseLocatorList = partsWarehouseLocatorRepository.findByPartsWarehouseId(sourcePartsWarehouseId);
        boolean virtualLocatorExists = partsInventoryService.isVirtualLocatorExistsInInventory(sourcePartsWarehouseId);
        if (virtualLocatorExists) {
            partsWareHouseLocatorList.add(SimplePartsWareHouseLocator.VIRTUAL_LOCATOR);
        }
        return partsWareHouseLocatorList;
    }

    public List<SimplePartsItem> getSourcePartsItemsInInventoryList() {
        if(null == sourcePartsWarehouseLocatorId && null == sourcePartsWarehouseId){
            return Collections.EMPTY_LIST;
        }
        List<SimplePartsItem> partsItemList;
        if(null == sourcePartsWarehouseLocatorId){
            partsItemList = partsInventoryMapper.findPartsItemsByWarehouseIdAndLocatorId(sourcePartsWarehouseId, SimplePartsConstant.VIRTUAL_LOCATOR_ID);
        }else {
            partsItemList = partsInventoryMapper.findPartsItemsByWarehouseIdAndLocatorId(sourcePartsWarehouseId, sourcePartsWarehouseLocatorId);
        }
        return partsItemList;
    }

    public List<SimplePartsWareHouseLocator> getTargetPartsWareHouseLocatorList() {
        if(null == targetPartsWarehouseId){
            return Collections.EMPTY_LIST;
        }

        boolean isEnableWarehouseLocator = partsConfigReadController.isEnableWarehouseLocator();
        List<SimplePartsWareHouseLocator> targetPartsWareHouseLocatorList = Lists.newArrayList();
        if (isEnableWarehouseLocator) {
            targetPartsWareHouseLocatorList = partsWarehouseLocatorRepository.findByPartsWarehouseId(targetPartsWarehouseId);
        }else{
            targetPartsWareHouseLocatorList.add(SimplePartsWareHouseLocator.VIRTUAL_LOCATOR);
        }
        return targetPartsWareHouseLocatorList;
    }

    public void save(){
        boolean isWareHouseLocatorOpen = partsConfigService.isEnableWarehouseLocator();
        int partsItemNum = partsInventory.getOnhandQty();
        if(partsItemNum<sourcePartsItemNum){
            WebUtil.addErrorMessage("保存失败，备件数量超过库存数量");
            return;
        }else if (sourcePartsItemNum==0){
            WebUtil.addErrorMessage("保存失败，备件数量至少为1个");
            return;
        }else if(sourcePartsWarehouseId.equals(targetPartsWarehouseId)){
            WebUtil.addErrorMessage("源仓库与目标仓库不能相同");
            return;
        }else if(isWareHouseLocatorOpen == true && (null == targetPartsWarehouseLocatorId || targetPartsWarehouseLocatorId.isEmpty()) ){
            WebUtil.addErrorMessage("仓位已开启，请选择仓位");
        }
        try {
            partsTransferService.save(sourcePartsWarehouseId, sourcePartsWarehouseLocatorId, targetPartsWarehouseId, targetPartsWarehouseLocatorId, sourcePartsItemId, sourcePartsItemNum);
        }catch (Exception e){
            WebUtil.addErrorMessage("保存失败");
            return;
        }
        sourcePartsWarehouseId = null;
        sourcePartsWarehouseLocatorId = null;
        targetPartsWarehouseId = null;
        targetPartsWarehouseLocatorId = null;
        sourcePartsItemId = null;
        sourcePartsItemNum = 0;
        WebUtil.addSuccessMessage("保存成功");
    }
}
