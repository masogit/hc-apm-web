package com.ge.apm.view.train;


import com.alibaba.fastjson.JSONPath;
import com.ge.apm.dao.EHomeTransactionWorkOrderRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.EHomeTransactionWorkOrder;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
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
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class TrainMvsController extends GenericCRUDUUIDController<EHomeTransactionWorkOrder> {

    EHomeTransactionWorkOrderRepository eHomeTransactionWorkOrderRepository;

    OrgInfoRepository orgInfoRepository;

    UserAccount loginUser;

    @Override
    protected void init() {
        super.init();
        eHomeTransactionWorkOrderRepository = WebUtil.getBean(EHomeTransactionWorkOrderRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        loginUser = UserContextService.getCurrentUserAccount();
    }

    @Override
    protected Page<EHomeTransactionWorkOrder> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        setSearchFilterSiteUid();
        searchFilters.add(new SearchFilter("parentKey", SearchFilter.Operator.EQ, "PX"));
        Page<EHomeTransactionWorkOrder> page = eHomeTransactionWorkOrderRepository.findBySearchFilter(this.searchFilters, pageRequest);
        for(EHomeTransactionWorkOrder e :page.getContent()){
            Object o = JSONPath.read(e.getSrContext(), "$.faultDesc").toString();
            if(o != null){
                e.setCourseName(o.toString());
            }
            Object o1 = JSONPath.read(e.getWoContext(), "$.engineer").toString();
            if(o != null){
                e.setEngineer(o1.toString());
            }
        }
        return page;
    }

    public void setSearchFilterSiteUid(){
        List<OrgInfo> list = orgInfoRepository.getOrgInfosByParentId(loginUser.getHospitalUID());
        List<String> strs = list.stream().filter(t -> StrUtil.isNotEmpty(t.getHospitalUcmid())).map(OrgInfo::getHospitalUcmid).collect(Collectors.toList());
        updateSearchFilter(new SearchFilter("ucmId", SearchFilter.Operator.IN, strs));
    }

    @Override
    protected GenericRepositoryUUID<EHomeTransactionWorkOrder> getDAO() {
        return eHomeTransactionWorkOrderRepository;
    }
}
