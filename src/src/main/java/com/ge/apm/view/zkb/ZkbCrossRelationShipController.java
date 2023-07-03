package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbIbCrossRelationShipLogRepository;
import com.ge.apm.dao.ZkbIbCrossRelationShipRepository;
import com.ge.apm.dao.ZkbIbCrossUserRepository;
import com.ge.apm.domain.ZkbIbCrossRelationShip;
import com.ge.apm.domain.ZkbIbCrossUser;
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
public class ZkbCrossRelationShipController extends GenericCRUDUUIDController<ZkbIbCrossRelationShip> {

    private ZkbIbCrossRelationShipRepository zkbIbCrossRelationShipRepository;

    @Override
    protected void init() {
        super.init();
        zkbIbCrossRelationShipRepository = WebUtil.getBean(ZkbIbCrossRelationShipRepository.class);

    }


    @Override
    protected Page<ZkbIbCrossRelationShip> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }


        return zkbIbCrossRelationShipRepository.findBySearchFilter(searchFilters, pageRequest);
    }


    @Override
    protected GenericRepositoryUUID<ZkbIbCrossRelationShip> getDAO() {
        return zkbIbCrossRelationShipRepository;
    }


}
