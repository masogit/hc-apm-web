package com.ge.apm.service.zkb;

import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.TenantInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 212605082 on 2019/12/16.
 */
@Component
public class ZkbContactPersonService {

    @Autowired
    private TenantInfoRepository tenantInfoDao;

    public List<TenantInfo> getSourceTenantInfoList(){

        List<SearchFilter> searchFilters = new ArrayList<>();

        searchFilters.add(new SearchFilter("type", SearchFilter.Operator.EQ, 3));

        return tenantInfoDao.findBySearchFilter(searchFilters);
    }

    public TenantInfo getTenantInfo(String tenantUID){
        return tenantInfoDao.findByuid(tenantUID);
    }

}
