package com.ge.apm.service.common;

import com.ge.apm.dao.ConfigItemRepository;
import com.ge.apm.dao.ConfigItemValueRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.acquisition.ConfigItemForm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import webapp.framework.dao.SearchFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ConfigItemService {

    @Autowired
    private ConfigItemValueRepository configItemValueRepository;

    @Autowired
    ConfigCacheService configCacheService;

    /**
     * return boolean type
     * */
    public Boolean getConfigItemByKey(String key, UserAccount loginUser) {
        Boolean flag = null;
        String value = getConfigItemByKeyToString(key,loginUser);
        flag = Boolean.valueOf(value);
        return flag;
    }

    public String getConfigItemByKeyToString(String key, UserAccount loginUser) {
        return configItemValueRepository.findByKeyAndSiteForUseParent(loginUser.getSiteUID(), loginUser.getHospitalUID(), loginUser.getInstitutionUID(), loginUser.getTenantUID(), key);
    }


    public String getConfigItemByKeyToString(String key, String hospitalUID,String tenantUid) {
        return configItemValueRepository.findByKeyAndSiteForUseParent(hospitalUID, tenantUid, key);
    }

    public String getConfigByKeyAndSite(ConfigItemForm configItemForm) {
        if (StringUtils.isBlank(configItemForm.getTenantUid())
                || StringUtils.isBlank(configItemForm.getInstitutionUid())
                || StringUtils.isBlank(configItemForm.getHospitalUid())) {
            OrgInfo orgInfo = configCacheService.findOrgInfoByUid(configItemForm.getSiteUid());
            if (orgInfo != null) {
                configItemForm.setTenantUid(orgInfo.getTenantUID());
                configItemForm.setInstitutionUid(orgInfo.getInstitutionUID());
                configItemForm.setHospitalUid(orgInfo.getHospitalUID());
            }
        }
        return configItemValueRepository.findByKeyAndSiteForUseParent(configItemForm.getSiteUid(), configItemForm.getHospitalUid(), configItemForm.getInstitutionUid(), configItemForm.getTenantUid(), configItemForm.getKey());
    }

}
