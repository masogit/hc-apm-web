package com.ge.apm.view.simpleparts;

import com.ge.apm.dao.simpleparts.SimplePartsItemRepository;
import com.ge.apm.domain.simpleparts.SimplePartsItem;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.simpleparts.SimplePartsConfigService;
import com.ge.apm.service.simpleparts.SimplePartsItemService;
import com.ge.apm.service.simpleparts.SimplePartsWarehouseService;
import com.ge.apm.view.sysutil.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

@ManagedBean
@ViewScoped
public class SimplePartsConfigReadController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimplePartsConfigReadController.class);
    private static final long serialVersionUID = -483251039974336475L;

    private SimplePartsConfigService partsConfigService;
    private SimplePartsWarehouseService partsWarehouseService;
    private SimplePartsItemService partsItemService;
    private SimplePartsItemRepository partsItemRepository;

    @PostConstruct
    protected void init() {
        partsConfigService = WebUtil.getBean(SimplePartsConfigService.class);
        partsWarehouseService = WebUtil.getBean(SimplePartsWarehouseService.class);
        partsItemService = WebUtil.getBean(SimplePartsItemService.class);
        partsItemRepository = WebUtil.getBean(SimplePartsItemRepository.class);
    }

    public boolean isEnableWarehouseLocator() {
        return partsConfigService.isEnableWarehouseLocator();
    }

    public boolean isEnableWarehouseSelectionInRequest() {
        return partsConfigService.isEnableWarehouseSelectionInRequest();
    }

    public List<SimplePartsItem> getPartsItemListInInventory(String partsWarehouseId) {

        return partsItemService.getPartsItemListInInventoryByPartsHouseId(partsWarehouseId);
    }

    public List<SimplePartsItem> getAllPartsItemList() {
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        List<SimplePartsItem> partsItemList = partsItemRepository.findByTenantUid(userAccount.getTenantUID());
        return partsItemList;
    }

}
