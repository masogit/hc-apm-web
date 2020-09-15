package com.ge.apm.view.simpleparts;

import com.ge.apm.dao.simpleparts.SimplePartsConfigRepository;
import com.ge.apm.domain.simpleparts.SimplePartsConfig;
import com.ge.apm.view.sysutil.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Optional;

@ManagedBean
@ViewScoped
public class SimplePartsConfigController extends GenericCRUDUUIDController<SimplePartsConfig> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimplePartsConfigController.class);
    private static final long serialVersionUID = -483251039974336475L;

    private UserContextService userContextService;

    private SimplePartsConfigRepository partsConfigRepository;


    @Override
    protected GenericRepositoryUUID<SimplePartsConfig> getDAO() {
        return partsConfigRepository;
    }

    @Override
    protected void init()  {
        partsConfigRepository = WebUtil.getBean(SimplePartsConfigRepository.class);

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
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void save() {

        if (null != this.selected.getId()) {
            SimplePartsConfig existingPartsConfig = partsConfigRepository.findById(this.selected.getId());
            if (existingPartsConfig.isEnableWarehouseLocator() != this.selected.isEnableWarehouseLocator()) {
                if (!existingPartsConfig.isEnableWarehouseLocator()) {
                    //changed to enable
                    WebUtil.addWarnMessage("启用仓位后如果有库存在虚拟仓位上可能需要调拨至真实仓位上！");
                } else {
                    //changed to disable
                    WebUtil.addWarnMessage("取消仓位后如果有库存在真实仓位上可能需要调拨至虚拟仓位上！");
                }

            }
        }

        partsConfigRepository.save(this.selected);

        WebUtil.addSuccessMessage("保存成功!");
    }

}
