package com.ge.apm.service.zkb;

import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.ZkbContactPersonRepository;
import com.ge.apm.dao.ZkbSalesTenantAssociateRepository;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.ZkbContactPerson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * @autor by lyj 503277717
 * @date 2022/8/25
*/

@Component
public class ZkbSalesPersonInfoService {

    private static final Logger logger = LoggerFactory.getLogger(ZkbSalesPersonInfoService.class);


    @Autowired
    private TenantInfoRepository tenantInfoDao;

    @Autowired
    private ZkbSalesTenantAssociateRepository zkbSalesTenantAssociateRepository;

    public List<TenantInfo> getSourceTenantInfoList(){
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("type", SearchFilter.Operator.EQ, 3));
        return tenantInfoDao.findBySearchFilter(searchFilters);
    }




}
