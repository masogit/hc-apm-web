package com.ge.apm.view.simpleparts;

import com.ge.apm.dao.simpleparts.SimplePartsWarehouseLocatorRepository;
import com.ge.apm.dao.simpleparts.SimplePartsWarehouseRepository;
import com.ge.apm.domain.simpleparts.SimplePartsInventory;
import com.ge.apm.domain.simpleparts.SimplePartsWareHouseLocator;
import com.ge.apm.domain.simpleparts.SimplePartsWarehouse;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.simpleparts.SimplePartsInventoryService;
import com.ge.apm.service.simpleparts.SimplePartsWarehouseLocatorService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: csh
 * @date: 2020/7/13 13:55
 * @version: 1.0
 */
@ManagedBean
@ViewScoped
@SuppressWarnings("all")
public class SimplePartsWarehouseLocatorController extends GenericCRUDUUIDController<SimplePartsWarehouse>  implements ServerEventInterface {
    private static final long serialVersionUID = 1886024138737071702L;

    private SimplePartsWarehouseRepository partsWarehouseRepository;
    private SimplePartsWarehouseLocatorService partsWarehouseLocatorService;
    private SimplePartsWarehouseLocatorRepository partsWarehouseLocatorRepository;
    private SimplePartsInventoryService partsInventoryService;

    @Getter
    @Setter
    public SimplePartsWareHouseLocator partsWareHouseLocator;

    @Setter
    @Getter
    public List<SimplePartsWareHouseLocator> partsWareHouseLocatorList = new ArrayList<>();;

    @Override
    protected void init() {
        partsWarehouseRepository = WebUtil.getBean(SimplePartsWarehouseRepository.class);
        partsWarehouseLocatorService = WebUtil.getBean(SimplePartsWarehouseLocatorService.class);
        partsWarehouseLocatorRepository = WebUtil.getBean(SimplePartsWarehouseLocatorRepository.class);
        partsInventoryService = WebUtil.getBean(SimplePartsInventoryService.class);
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        selected = (SimplePartsWarehouse) eventObject;
    }

    @Override
    protected GenericRepositoryUUID<SimplePartsWarehouse> getDAO() {
        return partsWarehouseRepository;
    }

    public void cancelPartsWarehouseLocator() {
        partsWareHouseLocator = null;
    }

//    public List<PartsWareHouseLocator> getPartsWareHouseLocatorList() {
//        if (partsWareHouseLocatorList == null) {
//            partsWareHouseLocatorList = new ArrayList<PartsWareHouseLocator>();
//        }
//        PartsWareHouseLocator partsWareHouseLocator = new PartsWareHouseLocator();
//        partsWareHouseLocator.setId(PartsConstant.PARTS_LOCAL_ORG_ID);
//        boolean flag = false;
//        for (PartsWareHouseLocator info : partsWareHouseLocatorList) {
//            String uid = info.getId();
//            if (PartsConstant.PARTS_LOCAL_ORG_ID.equals(uid)) {
//                flag = true;
//                break;
//            }
//        }
//        if (flag) {
//            partsWareHouseLocatorList = partsWareHouseLocatorList.stream()
//                    .filter(item -> !PartsConstant.PARTS_LOCAL_ORG_ID.equals(item.getId()))
//                    .collect(Collectors.toList());
//            partsWareHouseLocatorList.add(partsWareHouseLocator);
//        } else {
//            partsWareHouseLocatorList.add(partsWareHouseLocator);
//        }
//        return partsWareHouseLocatorList;
//    }

    public List<SimplePartsWareHouseLocator> getLineLocatorsWithOptionalActionRow() {

        List<SimplePartsWareHouseLocator> resultList = new ArrayList<>(partsWareHouseLocatorList);
        if (!isEditPartsWareHouse()) {
            SimplePartsWareHouseLocator placeHolderRow = new SimplePartsWareHouseLocator();
            resultList.add(placeHolderRow);
        }

        return resultList;
    }

    private boolean isEditPartsWareHouse() {
        if (null == selected) {
            return true;
        }

        if (null == selected.getId()) {
            return true;
        }

        return false;
    }

    /**
     * 初始化仓位配置页面
     */
    public void initPartsWarehouseLocator(String uid) {
        SimplePartsWarehouse partsWarehouse = partsWarehouseRepository.getById(uid);
        if (partsWarehouse == null) {
            WebUtil.addSuccessMessage("仓库不存在");
            return;
        }

        selected = partsWarehouse;
        partsWareHouseLocatorList = partsWarehouseLocatorService.findAllByPartsWarehouseId(partsWarehouse.getId());
        if(partsWareHouseLocatorList.size()>0){
            partsWareHouseLocatorList = partsWareHouseLocatorList.stream().sorted((d1, d2) -> d1.getCreatedDate().compareTo(d2.getCreatedDate())).collect(Collectors.toList());
        }
    }

    /**
     * 保存仓位
     */
    public void savePartsWarehouseLocator() {
        if (selected == null) {
            WebUtil.addSuccessMessage("仓库不存在");
            return;
        }

        partsWareHouseLocator.setPartsWarehouseId(selected.getId());
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        partsWareHouseLocator.setCreatedBy(userAccount.getName());
        partsWareHouseLocator.setCreatedDate(new Date());
        partsWarehouseLocatorRepository.save(partsWareHouseLocator);
        partsWareHouseLocatorList = partsWarehouseLocatorRepository.findAllByPartsWarehouseId(selected.getId());
        cancelPartsWarehouseLocator();
    }

    public void deletPartsWarehouseLocator(String itemId) {
        SimplePartsWareHouseLocator locator = partsWarehouseLocatorRepository.findById(itemId);
        List<SimplePartsInventory> list = partsInventoryService.findByWarehouseIdAndLocatorId(locator.getPartsWarehouseId() , itemId);
        if(list.size() > 0 && list.stream().mapToInt(p -> p.getOnhandQty()).sum() > 0){
            WebUtil.addSuccessMessage("该仓位下存在可用备件");
            return;
        }
        partsWareHouseLocatorList.removeAll(partsWareHouseLocatorList.stream().filter(item -> item.getId().equals(itemId)).collect(Collectors.toList()));
        partsWarehouseLocatorRepository.delete(locator);
    }

    public void preparePartsWarehouseLocatorAdd() {
        partsWareHouseLocator = new SimplePartsWareHouseLocator();
    }

    public void preparePartsWarehouseLocatorEdit(String itemId) {
        partsWareHouseLocator = partsWareHouseLocatorList.stream().filter(item -> item.getId().equals(itemId)).findAny().get();
    }

    public boolean isLastEmptyRowInCreate(int rowIndex) {
        return rowIndex == partsWareHouseLocatorList.size();
    }

}
