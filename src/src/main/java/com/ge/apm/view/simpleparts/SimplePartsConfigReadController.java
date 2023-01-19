package com.ge.apm.view.simpleparts;

import com.ge.apm.dao.simpleparts.SimplePartsItemRepository;
import com.ge.apm.domain.simpleparts.SimplePartsItem;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.simpleparts.SimplePartsConfigService;
import com.ge.apm.service.simpleparts.SimplePartsItemService;
import com.ge.apm.service.simpleparts.SimplePartsWarehouseService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
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

    private String partsWarehouseId;

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

    public List<SimplePartsItem> getPartsItemListInInventory(String query) {
        return partsItemService.getPartsItemListInInventoryByPartsHouseId(this.partsWarehouseId, query);
    }

    public List<SimplePartsItem> getAllPartsItemList(String query) {
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        List<SimplePartsItem> partsItemList = null;
        if(StringUtils.isEmpty(query)){
            partsItemList = partsItemRepository.findByTenantUid(userAccount.getTenantUID());
        }else{
            partsItemList = partsItemRepository.findByTenantUidAndName(userAccount.getTenantUID(), query);
        }
        return partsItemList;
    }

    public String getPartsWarehouseId() {
        return partsWarehouseId;
    }

    public void setPartsWarehouseId(String partsWarehouseId) {
        this.partsWarehouseId = partsWarehouseId;
    }
}
