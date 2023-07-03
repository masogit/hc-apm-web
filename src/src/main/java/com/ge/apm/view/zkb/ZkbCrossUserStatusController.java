package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbIbCrossUserRepository;
import com.ge.apm.dao.ZkbIbCrossUserStatusRepository;
import com.ge.apm.domain.ZkbIbCrossUser;
import com.ge.apm.domain.ZkbIbCrossUserStatus;
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
public class ZkbCrossUserStatusController extends GenericCRUDUUIDController<ZkbIbCrossUserStatus> {

    private ZkbIbCrossUserStatusRepository zkbIbCrossUserStatusRepository;

    @Override
    protected void init() {
        super.init();
        zkbIbCrossUserStatusRepository = WebUtil.getBean(ZkbIbCrossUserStatusRepository.class);

    }


    @Override
    protected Page<ZkbIbCrossUserStatus> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        return zkbIbCrossUserStatusRepository.findBySearchFilter(searchFilters, pageRequest);
    }


    @Override
    protected GenericRepositoryUUID<ZkbIbCrossUserStatus> getDAO() {
        return zkbIbCrossUserStatusRepository;
    }


}
