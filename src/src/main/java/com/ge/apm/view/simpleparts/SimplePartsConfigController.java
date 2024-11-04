package com.ge.apm.view.simpleparts;

import com.ge.apm.dao.simpleparts.SimplePartsConfigRepository;
import com.ge.apm.dao.simpleparts.SimplePartsInventoryRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.simpleparts.SimplePartsConfig;
import com.ge.apm.domain.simpleparts.SimplePartsInventory;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;
import java.util.Optional;

@ManagedBean
@ViewScoped
public class SimplePartsConfigController extends GenericCRUDUUIDController<SimplePartsConfig> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimplePartsConfigController.class);
    private static final long serialVersionUID = -483251039974336475L;

    private UserContextService userContextService;

    private SimplePartsConfigRepository partsConfigRepository;
    private SimplePartsInventoryRepository partsInventoryRepository;

    @Override
    protected GenericRepositoryUUID<SimplePartsConfig> getDAO() {
        return partsConfigRepository;
    }

    @Override
    protected void init()  {
        partsConfigRepository = WebUtil.getBean(SimplePartsConfigRepository.class);
        partsInventoryRepository = WebUtil.getBean(SimplePartsInventoryRepository.class);
        Optional<SimplePartsConfig> partsConfigOptional = partsConfigRepository.findByTenantUid(UserContextService.getCurrentUserAccount().getTenantUID());
        if (partsConfigOptional.isPresent()) {
            this.selected = partsConfigOptional.get();
            return;
        }

        try {
            super.prepareCreate();

            selected.setTenantUid(UserContextService.getCurrentUserAccount().getTenantUID());
            selected.setEnableWarehouseLocator(false);
            selected.setEnableWarehouseSelectionInRequest(false);
            selected.setEnableIntegrationWithMultiLevelAuditing(false);
            selected.setEnableIntegrationWithWorkOrder(false);
            selected.setEnableTenantLocal(false);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void save() {

        if (null != this.selected.getId()) {
            SimplePartsConfig existingPartsConfig = partsConfigRepository.findById(this.selected.getId());
            if (existingPartsConfig.isEnableWarehouseLocator() != this.selected.isEnableWarehouseLocator()) {
                UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
                List<SimplePartsInventory> all = Lists.newArrayList();
                String msg = "";
                if (!existingPartsConfig.isEnableWarehouseLocator()) {
                    all = partsInventoryRepository.findIsVirtualLoctorByTenantUid(currentUserAccount.getTenantUID());
                    //changed to enable
                    msg = "启用仓位失败,有库存在虚拟仓位上,需要调拨至真实仓位上！";
                } else {
                    all = partsInventoryRepository.findNotIsVirtualLoctorByTenantUid(currentUserAccount.getTenantUID());
                    //changed to disable
                    msg = "取消仓位失败,有库存在真实仓位上,需要调拨至虚拟仓位上！";
                }

                if(all.size() > 0 && all.stream().mapToInt(p -> p.getOnhandQty()).sum() > 0){
                    selected = existingPartsConfig;
                    WebUtil.addWarnMessage(msg);
                    return;
                }
            }
        }

        partsConfigRepository.save(this.selected);

        WebUtil.addSuccessMessage("保存成功!");
    }

}
