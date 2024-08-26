package com.ge.apm.view.his;


import com.ge.apm.dao.HisAssetGroupClinicalMatchRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.his.HisAssetGroupClinicalMatch;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.JpaCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class HisAssetGroupClinicalMatchController extends GenericCRUDUUIDController<HisAssetGroupClinicalMatch> implements ServerEventInterface {


    private HisAssetGroupClinicalMatchRepository hisAssetGroupClinicalMatchRepository;

    private HisOrgComponentController orgComponentController;

    private  OrgInfoRepository orgInfoRepository;

    @Getter
    @Setter
    private Integer filterAssetGroup;

    @Getter
    @Setter
    private String groupType;
    @Override
    protected void init() {
        hisAssetGroupClinicalMatchRepository = WebUtil.getBean(HisAssetGroupClinicalMatchRepository.class);
        orgComponentController = WebUtil.getBean(HisOrgComponentController.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);

    }


    @Override
    protected GenericRepositoryUUID<HisAssetGroupClinicalMatch> getDAO() {
        return null;
    }

    @Override
    protected Page<HisAssetGroupClinicalMatch> loadData(PageRequest pageRequest) {

        if(searchFilters==null){
            searchFilters  = new ArrayList<>();

        }

        removeFilterOnField("hisAssetGroupAssetMatch.orgInfoUid");
        removeFilterOnField("tenantUid");

        if (orgComponentController.getSelectedOrg() != null) {
            List<String> orgInfoUids = orgInfoRepository.getSiteOrgUidsByParentUid(orgComponentController.getSelectedOrg().getUid());
            searchFilters.add(new SearchFilter("orgInfoUid", SearchFilter.Operator.IN, orgInfoUids));
        }

        searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID()));


        Page<HisAssetGroupClinicalMatch> hisAssetGroupClinicalMatches = hisAssetGroupClinicalMatchRepository.findBySearchFilter(this.searchFilters, pageRequest);
        return hisAssetGroupClinicalMatches;
    }
}
