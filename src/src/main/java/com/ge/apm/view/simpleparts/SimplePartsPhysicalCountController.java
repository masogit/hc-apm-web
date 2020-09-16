package com.ge.apm.view.simpleparts;

import com.ge.apm.dao.simpleparts.SimplePartsWarehouseLocatorRepository;
import com.ge.apm.dao.simpleparts.SimplePartsWarehouseRepository;
import com.ge.apm.dao.mapper.simpleparts.SimplePartsInventoryMapper;
import com.ge.apm.domain.simpleparts.SimplePartsInventory;
import com.ge.apm.domain.simpleparts.SimplePartsWarehouse;
import com.ge.apm.service.constant.SimplePartsConstant;
import com.ge.apm.service.simpleparts.SimplePartsPhysicalCountService;
import com.ge.apm.service.simpleparts.SimplePartsWarehouseService;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.SelectEvent;
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
public class SimplePartsPhysicalCountController extends GenericCRUDUUIDController<SimplePartsInventory> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimplePartsPhysicalCountController.class);
    private static final long serialVersionUID = -483251039974336475L;

    private SimplePartsWarehouseService partsWarehouseService;
    private SimplePartsInventoryMapper partsInventoryMapper;
    private SimplePartsWarehouseRepository partsWarehouseRepository;
    private SimplePartsWarehouseLocatorRepository partsWarehouseLocatorRepository;
    private SimplePartsPhysicalCountService partsPhysicalCountService;

    @Getter @Setter
    private String partsWarehouseId;

    @Getter @Setter
    private List<SimplePartsWarehouse> warehouseList;

    @Getter @Setter
    private List<SimplePartsInventory> partsInventoryList;

    @Override
    protected void init() {
        partsWarehouseService = WebUtil.getBean(SimplePartsWarehouseService.class);
        partsInventoryMapper = WebUtil.getBean(SimplePartsInventoryMapper.class);
        partsWarehouseRepository = WebUtil.getBean(SimplePartsWarehouseRepository.class);
        partsWarehouseLocatorRepository = WebUtil.getBean(SimplePartsWarehouseLocatorRepository.class);
        partsPhysicalCountService = WebUtil.getBean(SimplePartsPhysicalCountService.class);
    }

    public void onWarehouseChange(){

    }

    public List<SimplePartsWarehouse> getWarehouseList() {
        List<SimplePartsWarehouse> warehouseList = partsWarehouseService.findAll();
        return warehouseList;
    }

    public List<SimplePartsInventory> getPartsInventoryList() {
        if(null == partsWarehouseId){
            return Collections.EMPTY_LIST;
        }else {
            partsInventoryList = partsInventoryMapper.findPartsInventory(partsWarehouseId);
            return partsInventoryList;
        }
    }

    public String getWarehouseName(String partsWarehouseId){
        return partsWarehouseRepository.getById(partsWarehouseId).getName();
    }

    public String getWarehouseLocatorName(String partsLocatorId){
        if(partsLocatorId.equals(SimplePartsConstant.VIRTUAL_LOCATOR_ID)){
            return "";
        }else {
            String partsWarehouseLocatorName = partsWarehouseLocatorRepository.findById(partsLocatorId) == null ? "" : partsWarehouseLocatorRepository.findById(partsLocatorId).getName();
            return  partsWarehouseLocatorName == null ? "" : partsWarehouseLocatorName;
        }
    }

    public void onRowSelect(SelectEvent event) {

    }

    public void save(){
        try {
            partsPhysicalCountService.save(partsInventoryList);
        }catch (Exception e){
            WebUtil.addErrorMessage("保存失败");
            return;
        }
        WebUtil.addSuccessMessage("保存成功");
    }

    @Override
    protected GenericRepositoryUUID<SimplePartsInventory> getDAO() {
        return null;
    }
}
