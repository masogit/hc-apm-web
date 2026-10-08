package com.ge.apm.view.his;

import com.ge.apm.dao.HisAdjustBusinessJobRepository;
import com.ge.apm.domain.dto.SelectItemDto;
import com.ge.apm.domain.his.HisAdjustBusinessJob;
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
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
public class HisAdjustBusinessJobController extends GenericCRUDUUIDController<HisAdjustBusinessJob> implements ServerEventInterface {

    private HisAdjustBusinessJobRepository dao;
    private HisOrgComponentController orgComponentController;

    @Getter @Setter
    private HisAdjustBusinessJob selectedJob;

    @Getter @Setter
    private String filterTaskStatus;

    @Getter @Setter
    private List<SelectItemDto> taskStatusList;

    @Getter @Setter
    private HisAdjustBusinessJob selectedTaskForReset;

    @Getter @Setter
    private Integer taskType = 2;

    @Override
    protected HisAdjustBusinessJobRepository getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        dao = WebUtil.getBean(HisAdjustBusinessJobRepository.class);
        orgComponentController = WebUtil.getBean(HisOrgComponentController.class);
        orgComponentController.registerController(this);
        loadTaskStatusList();
    }

    @Override
    protected Page<HisAdjustBusinessJob> loadData(PageRequest pageRequest) {
        if (null == searchFilters) {
            searchFilters = new ArrayList<>();
        }

        removeFilterOnField("orgCode");
        removeFilterOnField("siteUid");
        removeFilterOnField("tenantUid");
        removeFilterOnField("taskType");
        searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID()));
        searchFilters.add(new SearchFilter("taskType", SearchFilter.Operator.EQ, taskType));
        if (orgComponentController.getSelectedOrg() != null) {
            searchFilters.add(new SearchFilter("orgCode", SearchFilter.Operator.EQ, orgComponentController.getSelectedOrg().getOrgCode()));
            searchFilters.add(new SearchFilter("siteUid", SearchFilter.Operator.EQ, orgComponentController.getSelectedOrg().getSiteUID()));
        }

        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    private void loadTaskStatusList() {
        taskStatusList = new ArrayList<>();
        taskStatusList.add(new SelectItemDto("0", "待执行"));
        taskStatusList.add(new SelectItemDto("1", "已执行"));
        taskStatusList.add(new SelectItemDto("2", "执行失败"));
    }

    public void initResetTask(HisAdjustBusinessJob job) {
        selectedTaskForReset = job;
    }

    public void resetFailedTask() {
        if (selectedTaskForReset == null || selectedTaskForReset.getTaskStatus() == null || selectedTaskForReset.getTaskStatus() != 2) {
            WebUtil.addErrorMessage("只能重置执行失败的任务");
            return;
        }
        selectedTaskForReset.setTaskStatus(0);
        selectedTaskForReset.setComment(null);
        selectedTaskForReset.setUpdateDate(new Date());
        dao.save(selectedTaskForReset);
        selectedTaskForReset = null;
        WebUtil.addSuccessMessage("任务状态已重置为待执行");
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        logger.info(this);
    }
}
