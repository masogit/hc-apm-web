package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbUnrealRepository;
import com.ge.apm.domain.ZkbUnreal;
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
public class ZkbUnrealController extends GenericCRUDUUIDController<ZkbUnreal> {

    private ZkbUnrealRepository zkbUnrealRepository;

    @Override
    protected void init() {
        super.init();
        zkbUnrealRepository = WebUtil.getBean(ZkbUnrealRepository.class);
    }

    @Override
    protected GenericRepositoryUUID<ZkbUnreal> getDAO() {
        return zkbUnrealRepository;
    }

    @Override
    protected Page<ZkbUnreal> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }

        return zkbUnrealRepository.findBySearchFilter(searchFilters, pageRequest);
    }



}
