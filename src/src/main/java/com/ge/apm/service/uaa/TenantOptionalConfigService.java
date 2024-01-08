package com.ge.apm.service.uaa;

import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.TenantOptionConfigRepository;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.TenantOptionConfig;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TenantOptionalConfigService  {

    @Autowired
    private TenantOptionConfigRepository tenantOptionConfigDao;
    @Autowired
    private TenantInfoRepository tenantInfoDao;

    @Autowired
    private ConfigCacheService configCacheService;

    @Transactional
    public void save(TenantOptionConfig config, TenantInfo selectedTenant){
        //保存config
        tenantOptionConfigDao.save(config);
        configCacheService.cleanTenantOptionConfigByUid(selectedTenant.getUid());
        //保存qrCodeEditable字段到tenantInfo表

        boolean qrCodeEditable = selectedTenant.getQrCodeEditable();

        TenantInfo tenantInfo = tenantInfoDao.findByUid(selectedTenant.getUid());
        tenantInfo.setQrCodeEditable(qrCodeEditable);

        tenantInfoDao.save(tenantInfo);
        //configCacheService.cleanTenantCacheByUid(selectedTenant.getUid());

    }

}
