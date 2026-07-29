package com.ge.apm.view.surgeryAnesthesia;

import com.ge.apm.dao.SurgeryAnesthesiaEquipmentMatchRepository;
import com.ge.apm.domain.his.HisAdjustBusinessJob;
import com.ge.apm.domain.surgeryAnesthesia.SurgeryAnesthesiaEquipmentMatch;
import com.ge.apm.service.surgeryAnesthesia.SurgeryAnesthesiaAssetService;
import com.ge.apm.view.his.HisOrgComponentController;
import com.ge.apm.view.sysutil.UserContextService;
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
 * 手术室绩效管理 - 取消匹配Controller
 */
@ManagedBean
@ViewScoped
public class SurgeryAnesthesiaAssetUnmatchController extends GenericCRUDUUIDController<SurgeryAnesthesiaEquipmentMatch> implements ServerEventInterface {

    private SurgeryAnesthesiaEquipmentMatchRepository dao;
    private SurgeryAnesthesiaAssetService surgeryAnesthesiaAssetService;

    private SurgeryAnesthesiaAssetMatchController matchController;
    private HisOrgComponentController orgComponentController;

    @Getter
    @Setter
    private HisAdjustBusinessJob hisAdjustBusinessJob;

    @Override
    protected SurgeryAnesthesiaEquipmentMatchRepository getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        dao = WebUtil.getBean(SurgeryAnesthesiaEquipmentMatchRepository.class);
        surgeryAnesthesiaAssetService = WebUtil.getBean(SurgeryAnesthesiaAssetService.class);

        matchController = WebUtil.getBean(SurgeryAnesthesiaAssetMatchController.class);
        orgComponentController = WebUtil.getBean(HisOrgComponentController.class);

        orgComponentController.registerController(this);
    }

    @Override
    protected Page<SurgeryAnesthesiaEquipmentMatch> loadData(PageRequest pageRequest) {
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

        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    public void initDate(String id) {
        hisAdjustBusinessJob = new HisAdjustBusinessJob();
        hisAdjustBusinessJob.setEquipmentMatchId(id);
    }

    public void onSelectCurrent(String id) {
        SurgeryAnesthesiaEquipmentMatch equipmentMatch = dao.findById(id);
        if (hisAdjustBusinessJob.getDataStartDate() == null || hisAdjustBusinessJob.getDataEndDate() == null || hisAdjustBusinessJob.getDataEndDate().compareTo(hisAdjustBusinessJob.getDataStartDate()) <= 0) {
            WebUtil.addSuccessMessage("取消设备匹配填写开始时间或结束时间错误");
            return;
        }

        surgeryAnesthesiaAssetService.unmatch(hisAdjustBusinessJob, equipmentMatch);

        matchController.refreshAfterOrgChange();
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        logger.info(this);
    }
}