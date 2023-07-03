package com.ge.apm.view.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : 503189362
 * @date : 2023/6/30
 * @description :
 */
@ManagedBean
@ViewScoped
public class ZkbCrossUserController extends GenericCRUDUUIDController<ZkbIbCrossUser> {

    private ZkbIbCrossUserRepository zkbIbCrossUserRepository;
    private TenantInfoRepository tenantInfoDao;

    @Override
    protected void init() {
        super.init();
        zkbIbCrossUserRepository = WebUtil.getBean(ZkbIbCrossUserRepository.class);
        tenantInfoDao = WebUtil.getBean(TenantInfoRepository.class);

    }


    @Override
    protected Page<ZkbIbCrossUser> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }


        return zkbIbCrossUserRepository.findBySearchFilter(searchFilters, pageRequest);
    }


    @Override
    protected GenericRepositoryUUID<ZkbIbCrossUser> getDAO() {
        return zkbIbCrossUserRepository;
    }

    public TenantInfo getTenantInfo(String tenantInfoUID){
        return tenantInfoDao.findByuid(tenantInfoUID);
    }

}
