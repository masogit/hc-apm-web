package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbContractLogRepository;
import com.ge.apm.domain.ZkbContractLog;
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
 * @date : 2023/5/11
 * @description :
 */
@ManagedBean
@ViewScoped
public class ZkbContractLogController extends GenericCRUDUUIDController<ZkbContractLog> {


    private ZkbContractLogRepository zkbContractLogRepository;
    private Page<ZkbContractLog> zkbContractLogPage;


    @Override
    protected void init() {
        super.init();
        zkbContractLogRepository = WebUtil.getBean(ZkbContractLogRepository.class);
    }

    @Override
    protected Page<ZkbContractLog> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }

        zkbContractLogPage = zkbContractLogRepository.findBySearchFilter(searchFilters, pageRequest);
        return zkbContractLogPage;
    }

    @Override
    protected GenericRepositoryUUID<ZkbContractLog> getDAO() {
        return zkbContractLogRepository;
    }
}
