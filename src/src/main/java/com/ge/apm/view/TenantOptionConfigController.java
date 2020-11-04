package com.ge.apm.view;

import com.ge.apm.dao.TenantOptionConfigRepository;
import com.ge.apm.domain.TenantOptionConfig;
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
public class TenantOptionConfigController extends GenericCRUDUUIDController<TenantOptionConfig> {

    private static final Logger logger = LoggerFactory.getLogger(TenantOptionConfigController.class);
    private static final long serialVersionUID = -1L;

    private TenantOptionConfigRepository tenantOptionConfigRepository;

    @Override
    protected GenericRepositoryUUID<TenantOptionConfig> getDAO() {
        return tenantOptionConfigRepository;
    }

    @Override
    protected void init() {
        tenantOptionConfigRepository = WebUtil.getBean(TenantOptionConfigRepository.class);

        Optional<TenantOptionConfig> tenantOptionConfigOptional = tenantOptionConfigRepository.findByTenantUid(UserContextService.getCurrentUserAccount().getTenantUID());
        if (tenantOptionConfigOptional.isPresent()) {
            this.selected = tenantOptionConfigOptional.get();
            return;
        }
        try {
            super.prepareCreate();

            selected.setTenantUid(UserContextService.getCurrentUserAccount().getTenantUID());
            selected.setOrderByCreateTimeByDefault(false);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    @Override
    public void save() {
         tenantOptionConfigRepository.save(this.selected);
        WebUtil.addSuccessMessage("保存成功!");
    }
}
