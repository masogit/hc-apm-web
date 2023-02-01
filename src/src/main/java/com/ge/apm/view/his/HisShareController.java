package com.ge.apm.view.his;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.view.sysutil.UserContextService;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class HisShareController {

    private OrgInfoRepository orgInfoDao;

    private List<OrgInfo> orgInfoList;

    @PostConstruct
    protected void init() {
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
    }

    public List<OrgInfo> getOrgInfoList() {

        if (null == orgInfoList) {
            List<SearchFilter> orgInfoFilters = new ArrayList<>();
            orgInfoFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID()));
            orgInfoFilters.add(new SearchFilter("orgCode", SearchFilter.Operator.IsNotNull,null));
            orgInfoList = orgInfoDao.findBySearchFilter(orgInfoFilters);
        }

        return orgInfoList;
    }


}
