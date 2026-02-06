package com.ge.apm.view.his;


import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.HisAssetSfxmMatchRepository;
import com.ge.apm.dao.HisEquipmentSfxmRepository;
import com.ge.apm.dao.OrgInfoRepository;

import com.ge.apm.domain.his.HisAdjustBusinessJob;
import com.ge.apm.domain.his.HisAssetSfxmMatch;

import com.ge.apm.service.his.HisAssetSfxmService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;


@ManagedBean
@ViewScoped
public class HisAssetSfxmUnMatchController extends GenericCRUDUUIDController<HisAssetSfxmMatch> implements ServerEventInterface {


    private HisOrgComponentController orgComponentController;


    private OrgInfoRepository orgInfoDao;
    private AssetInfoRepository assetInfoRepository;
    private HisEquipmentSfxmRepository hisEquipmentSfxmRepository;
    private HisAssetSfxmMatchRepository hisAssetSfxmMatchRepository;

    private HisAssetSfxmService hisAssetSfxmService;

    @Getter @Setter
    private HisAdjustBusinessJob hisAdjustBusinessJob;


    @Override
    protected void init() {
        orgComponentController = WebUtil.getBean(HisOrgComponentController.class);
        orgComponentController.registerController(this);


        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        hisEquipmentSfxmRepository = WebUtil.getBean(HisEquipmentSfxmRepository.class);
        hisAssetSfxmMatchRepository = WebUtil.getBean(HisAssetSfxmMatchRepository.class);
        hisAssetSfxmService = WebUtil.getBean(HisAssetSfxmService.class);

    }

    @Override
    protected Page<HisAssetSfxmMatch> loadData(PageRequest pageRequest) {
        if (null == searchFilters) {
            searchFilters = new ArrayList<>();
        }

        removeFilterOnField("orgCode");
        removeFilterOnField("siteUid");
        removeFilterOnField("tenantUid");
        searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID()));
        if (orgComponentController.getSelectedOrg() != null) {
            searchFilters.add(new SearchFilter("orgCode", SearchFilter.Operator.EQ, orgComponentController.getSelectedOrg().getOrgCode()));
            searchFilters.add(new SearchFilter("siteUid", SearchFilter.Operator.EQ, orgComponentController.getSelectedOrg().getSiteUID()));
        }

        return hisAssetSfxmMatchRepository.findBySearchFilter(this.searchFilters, pageRequest);
    }

    public void initDate(String id){
        hisAdjustBusinessJob = new HisAdjustBusinessJob();
        hisAdjustBusinessJob.setEquipmentMatchId(id);
    }


    public void onSelectCurrent(String id) {
        HisAssetSfxmMatch  hisAssetSfxmMatch = hisAssetSfxmMatchRepository.findById(id);
        if(hisAdjustBusinessJob.getDataStartDate() == null || hisAdjustBusinessJob.getDataEndDate() == null || hisAdjustBusinessJob.getDataEndDate().compareTo(hisAdjustBusinessJob.getDataStartDate()) <= 0){
            WebUtil.addSuccessMessage("取消设备联机匹配填写开始时间或结束时间错误");
            return;
        }

        hisAssetSfxmService.unmatch(hisAdjustBusinessJob, hisAssetSfxmMatch);

    }



    @Override
    protected GenericRepositoryUUID<HisAssetSfxmMatch> getDAO() {
        return hisAssetSfxmMatchRepository;
    }






}
