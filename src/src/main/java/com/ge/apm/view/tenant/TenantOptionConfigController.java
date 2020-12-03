package com.ge.apm.view.tenant;

import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.TenantOptionConfigRepository;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.TenantOptionConfig;
import com.ge.apm.service.uaa.TenantOptionalConfigService;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class TenantOptionConfigController extends GenericCRUDUUIDController<TenantOptionConfig> {

    private static final Logger logger = LoggerFactory.getLogger(TenantOptionConfigController.class);
    private static final long serialVersionUID = -1L;

    private TenantOptionConfigRepository tenantOptionConfigRepository;
    private TenantInfoRepository tenantInfoDao;
    private TenantOptionalConfigService tenantOptionalConfigService;

    @Getter @Setter
    private TenantInfo selectedTenant;

    private List<TenantInfo> tenantInfoList;

    @Override
    protected GenericRepositoryUUID<TenantOptionConfig> getDAO() {
        return tenantOptionConfigRepository;
    }

    @Override
    protected void init() {
        tenantOptionConfigRepository = WebUtil.getBean(TenantOptionConfigRepository.class);
        tenantInfoDao = WebUtil.getBean(TenantInfoRepository.class);
        tenantOptionalConfigService = WebUtil.getBean(TenantOptionalConfigService.class);

        tenantInfoList = Lists.newArrayList(tenantInfoDao.findAll().iterator());

        selectedTenant = tenantInfoDao.findByUid(UserContextService.getCurrentUserAccount().getTenantUID());

        Optional<TenantOptionConfig> tenantOptionConfigOptional = tenantOptionConfigRepository.findByTenantUid(UserContextService.getCurrentUserAccount().getTenantUID());
        if (tenantOptionConfigOptional.isPresent()) {
            this.selected = tenantOptionConfigOptional.get();
            return;
        }

        try {
            super.prepareCreate();

            selected.setTenantUid(selectedTenant.getUid());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    public List<TenantInfo> autoCompleteTenant(String query) {
        if (tenantInfoList == null) {
            return Collections.emptyList();
        }

        List<TenantInfo> res = this.tenantInfoList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query))
                .collect(Collectors.toList());

        return res;
    }

    public void onTenantChange() {
        Optional<TenantOptionConfig> tenantOptionConfigOptional = tenantOptionConfigRepository.findByTenantUid(selectedTenant.getUid());
        if (tenantOptionConfigOptional.isPresent()) {
            this.selected = tenantOptionConfigOptional.get();
        } else {
            this.selected = new TenantOptionConfig();
            this.selected.setTenantUid(selectedTenant.getUid());
        }

    }

    @Override
    @Transactional
    public void save() {
        if (null == selectedTenant) {
            WebUtil.addErrorMessage("请先选择租户!");
            return;
        }

        tenantOptionalConfigService.save(this.selected,selectedTenant);
        WebUtil.addSuccessMessage("保存成功!");
    }
}
