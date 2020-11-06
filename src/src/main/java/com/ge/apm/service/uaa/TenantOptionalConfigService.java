package com.ge.apm.service.uaa;

import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.TenantOptionConfigRepository;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.TenantOptionConfig;
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

    @Transactional
    public void save(TenantOptionConfig config, boolean qrCodeEditable){
        //保存config
        tenantOptionConfigDao.save(config);
        //保存qrCodeEditable字段到tenantInfo表
        TenantInfo tenantInfo = tenantInfoDao.findByUid(UserContextService.getCurrentUserAccount().getTenantUID());
        if(null != tenantInfoDao.findByUid(UserContextService.getCurrentUserAccount().getTenantUID())){
            tenantInfoDao.updateQrCodeEditable(qrCodeEditable,tenantInfo.getUid());
        }
    }

}
