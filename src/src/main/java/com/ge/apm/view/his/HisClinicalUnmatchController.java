
package com.ge.apm.view.his;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.HisAdministrativeOfficesMatchRepository;
import com.ge.apm.domain.his.HisAdministrativeOfficesMatch;
import com.ge.apm.service.his.HisClinicalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;

/**
 * @author
 */
@ManagedBean
@ViewScoped
public class HisClinicalUnmatchController extends GenericCRUDUUIDController<HisAdministrativeOfficesMatch> {
    private HisAdministrativeOfficesMatchRepository dao;
    private HisClinicalService hisClinicalService;
    private HisClinicalMatchController matchController;
    private HisOrgComponentController orgComponentController;

    @Override
    protected void init() {
        dao = WebUtil.getBean(HisAdministrativeOfficesMatchRepository.class);
        hisClinicalService = WebUtil.getBean(HisClinicalService.class);
        matchController = WebUtil.getBean(HisClinicalMatchController.class);
        orgComponentController = WebUtil.getBean(HisOrgComponentController.class);
    }

    @Override
    protected Page<HisAdministrativeOfficesMatch> loadData(PageRequest pageRequest) {
        if(this.searchFilters == null){
            searchFilters = new ArrayList<>();
        }

        removeFilterOnField("hosId");
        removeFilterOnField("siteUid");

        if (orgComponentController.getSelectedOrg() != null) {
            searchFilters.add(new SearchFilter("hosId", SearchFilter.Operator.EQ, orgComponentController.getSelectedOrg().getOrgCode()));
            searchFilters.add(new SearchFilter("siteUid", SearchFilter.Operator.EQ, orgComponentController.getSelectedOrg().getSiteUID()));
        }

        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    @Override
    protected HisAdministrativeOfficesMatchRepository getDAO() {
        return dao;
    }

    public void cancelMatch(String id) {
        logger.info(JSON.toJSONString(id));

        hisClinicalService.unmatch(id);

        matchController.refreshAfterOrgChange();
    }
}


