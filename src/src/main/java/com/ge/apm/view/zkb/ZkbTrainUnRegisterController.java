package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbTrainPublishRepository;
import com.ge.apm.domain.*;
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
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
public class ZkbTrainUnRegisterController extends GenericCRUDUUIDController<ZkbTrainPublishInfo> {
    ZkbTrainPublishRegisterService zkbTrainPublishRegisterService;

    private PageUtil<ZkbTrainPublishInfo> pageUtil = new PageUtil<>();

    ZkbTrainPublishRepository dao;

    UserAccount currentUser = UserContextService.getCurrentUserAccount();


    @Override
    protected void init() {
        super.init();
        zkbTrainPublishRegisterService = WebUtil.getBean(ZkbTrainPublishRegisterService.class);
        dao = WebUtil.getBean(ZkbTrainPublishRepository.class);
    }

    @Override
    protected Page<ZkbTrainPublishInfo> loadData(PageRequest pageRequest) {
        if(this.searchFilters == null){
            this.searchFilters = new ArrayList<>();
        }
        // 过滤：报名截止日期 >= 当前时间
        searchFilters.add(new SearchFilter("signDeadline", SearchFilter.Operator.GTE, new Date()));
        List<ZkbTrainPublishInfo> list = zkbTrainPublishRegisterService.loadUnRegisterDataList(this.searchFilters, currentUser.getTenantUID());
        return pageUtil.pageNum(list,pageRequest);
    }


    @Override
    protected GenericRepositoryUUID<ZkbTrainPublishInfo> getDAO() {
        return dao;
    }
}
