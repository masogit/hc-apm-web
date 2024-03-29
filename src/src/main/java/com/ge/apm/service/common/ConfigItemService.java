package com.ge.apm.service.common;

import com.ge.apm.dao.ConfigItemRepository;
import com.ge.apm.dao.ConfigItemValueRepository;
import com.ge.apm.domain.ConfigItem;
import com.ge.apm.domain.ConfigItemValue;
import com.ge.apm.domain.UserAccount;
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
    private ConfigItemRepository configItemRepository;
    @Autowired
    private ConfigItemValueRepository configItemValueRepository;

    /**
     * return boolean type
     * */
    public Boolean getConfigItemByKey(String key, UserAccount loginUser) {
        Boolean flag = null;
        ConfigItem byDefaultValueForKey = configItemRepository.findByDefaultValueForKey(key);
        if (byDefaultValueForKey == null) return Boolean.FALSE;
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("key", SearchFilter.Operator.EQ, key));
        String siteUID = loginUser.getSiteUID();
        String hospitalUID = loginUser.getHospitalUID();
        String institutionUID = loginUser.getInstitutionUID();
        String tenantUID = loginUser.getTenantUID();
        List<String> value = Arrays.asList(siteUID, hospitalUID, institutionUID, tenantUID);
        searchFilters.add(new SearchFilter("siteUid", SearchFilter.Operator.IN, value));
        List<ConfigItemValue> configItemValues = configItemValueRepository.findBySearchFilter(searchFilters);
        if (!CollectionUtils.isEmpty(configItemValues)) {
            Map<String, Boolean> collect = configItemValues.stream().collect(Collectors.toMap(ConfigItemValue::getSiteUid, item -> Boolean.valueOf(item.getValue())));
            if ((flag = collect.get(siteUID)) == null
                    && (flag = collect.get(hospitalUID)) == null
                    && (flag = collect.get(institutionUID)) == null
                    && (flag = collect.get(tenantUID)) == null) {
                flag = Boolean.valueOf(byDefaultValueForKey.getDefaultValue());
            }
        } else {
            flag = Boolean.valueOf(byDefaultValueForKey.getDefaultValue());
        }
        return flag;
    }
}
