
package com.ge.apm.view.his;

import com.ge.apm.dao.HisEquipmentMatchRepository;
import com.ge.apm.domain.his.HisAdjustBusinessJob;
import com.ge.apm.domain.his.HisEquipmentMatch;
import com.ge.apm.service.his.HisAssetService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;

/**
 * @author
 */
@ManagedBean
@ViewScoped
public class HisAssetUnmatchController extends GenericCRUDUUIDController<HisEquipmentMatch> implements ServerEventInterface {

    private HisEquipmentMatchRepository dao;
    private HisAssetService hisAssetService;

    private HisAssetMatchController matchController;
    private HisOrgComponentController orgComponentController;

    @Getter @Setter
    private HisAdjustBusinessJob hisAdjustBusinessJob;

    @Override
    protected HisEquipmentMatchRepository getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        dao = WebUtil.getBean(HisEquipmentMatchRepository.class);
        hisAssetService = WebUtil.getBean(HisAssetService.class);

        matchController = WebUtil.getBean(HisAssetMatchController.class);
        orgComponentController = WebUtil.getBean(HisOrgComponentController.class);

        orgComponentController.registerController(this);
    }

    @Override
    protected Page<HisEquipmentMatch> loadData(PageRequest pageRequest) {
        if (null == searchFilters) {
            searchFilters = new ArrayList<>();
        }

        removeFilterOnField("eqHosid");
        removeFilterOnField("siteUid");

        if (orgComponentController.getSelectedOrg() != null) {
            searchFilters.add(new SearchFilter("eqHosid", SearchFilter.Operator.EQ, orgComponentController.getSelectedOrg().getOrgCode()));
            searchFilters.add(new SearchFilter("siteUid", SearchFilter.Operator.EQ, orgComponentController.getSelectedOrg().getSiteUID()));
        }

        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    public void initDate(String id){
        hisAdjustBusinessJob = new HisAdjustBusinessJob();
        hisAdjustBusinessJob.setEquipmentMatchId(id);
    }

    public void onSelectCurrent(String id) {
        HisEquipmentMatch equipmentMatch = dao.findById(id);
        if(hisAdjustBusinessJob.getDataStartDate() == null || hisAdjustBusinessJob.getDataEndDate() == null || hisAdjustBusinessJob.getDataEndDate().compareTo(hisAdjustBusinessJob.getDataStartDate()) <= 0){
            WebUtil.addSuccessMessage("取消设备联机匹配填写开始时间或结束时间错误");
            return;
        }

        hisAssetService.unmatch(hisAdjustBusinessJob, equipmentMatch);

        matchController.refreshAfterOrgChange();
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        logger.info(this);

    }
}


