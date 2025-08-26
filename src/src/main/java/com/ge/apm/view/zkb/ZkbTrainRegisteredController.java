package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbTrainPublishRegisterRepository;
import com.ge.apm.dao.ZkbTrainPublishRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbTrainPublishInfo;
import com.ge.apm.domain.ZkbTrainPublishRegister;
import com.ge.apm.service.zkb.ZkbTrainPublishRegisterService;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.PageUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class ZkbTrainRegisteredController extends ZkbTrainRegisterMainController<ZkbTrainPublishRegister> {


    ZkbTrainPublishRegisterService zkbTrainPublishRegisterService;

    ZkbTrainPublishRegisterRepository dao;

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(ZkbTrainPublishRegisterRepository.class);
        zkbTrainPublishRegisterService = WebUtil.getBean(ZkbTrainPublishRegisterService.class);
    }

    @Override
    protected Page<ZkbTrainPublishRegister> loadData(PageRequest pageRequest) {
        if(this.searchFilters == null){
            this.searchFilters = new ArrayList<>();
        }
        searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ, currentUser.getTenantUID()));
        return dao.findBySearchFilter(searchFilters,pageRequest);
    }

   

    @Override
    protected GenericRepositoryUUID<ZkbTrainPublishRegister> getDAO() {
        return dao;
    }
}
