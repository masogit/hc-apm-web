package com.ge.apm.view.his;

import com.ge.apm.dao.HisManualJobRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.HisRequestParamsDto;
import com.ge.apm.domain.his.HisManualJob;
import com.ge.apm.service.constant.HisConstant;
import com.ge.apm.service.his.HisManualJobService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.DateUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * @author 503209435
 */
@ManagedBean
@ViewScoped
public class HisManualJobController extends GenericCRUDUUIDController<HisManualJob> {

    private HisManualJobService hisManualJobService;
    private HisManualJobRepository hisManualJobRepository;

    @Getter
    @Setter
    private HisRequestParamsDto hisRequestParamsDto;

    @Getter
    @Setter
    private HisManualJob hisManualJob;

    @Getter
    @Setter
    private OrgInfo selectedOrg;

    @Getter
    @Setter
    private boolean openFlag;

    @Override
    protected GenericRepositoryUUID<HisManualJob> getDAO() {
        return hisManualJobRepository;
    }

    @Override
    protected void init() {
        hisManualJobService = WebUtil.getBean(HisManualJobService.class);
        hisManualJobRepository = WebUtil.getBean(HisManualJobRepository.class);
        hisManualJob = new HisManualJob();
        hisRequestParamsDto = new HisRequestParamsDto();
    }

    @Override
    protected Page<HisManualJob> loadData(PageRequest pageRequest) {
        searchFilters = new ArrayList<SearchFilter>();
        if (hisRequestParamsDto.getStartDate() != null) {
            searchFilters.add(new SearchFilter("dataStartDate", SearchFilter.Operator.GTE, hisRequestParamsDto.getStartDate()));
        }
        if (hisRequestParamsDto.getEndDate() != null) {
            searchFilters.add(new SearchFilter("dataEndDate", SearchFilter.Operator.LT, DateUtil.getDateAddOneDay(hisRequestParamsDto.getEndDate())));
        }
        if (hisRequestParamsDto.getTaskTypeName() != null) {
            searchFilters.add(new SearchFilter("taskTypeName", SearchFilter.Operator.LIKE, hisRequestParamsDto.getTaskTypeName()));
        }
        if (hisRequestParamsDto.getTaskResultStatus() != null) {
            searchFilters.add(new SearchFilter("taskResultStatus", SearchFilter.Operator.EQ, hisRequestParamsDto.getTaskResultStatus()));
        }

        return hisManualJobRepository.findBySearchFilter(this.searchFilters, pageRequest);
    }

    public List<String> getTaskTypeList(){
        return HisConstant.getTaskTypeList();
    }

    public List<String> getExecResultList(){
        return HisConstant.getExecResultList();
    }

    public void saveManualData() {
        if(HisConstant.getTaskTypeByTimeList().contains(hisManualJob.getTaskTypeName())){
            Date dataStartDate = hisManualJob.getDataStartDate();
            Date dataEndDate = hisManualJob.getDataEndDate();
            if(dataEndDate.getTime() < dataStartDate.getTime()) {
                WebUtil.addErrorMessage("任务开始时间应在结束时间之前！");
                return;
            }

            hisManualJob.setOrgCode(selectedOrg.getOrgCode());
            hisManualJob.setOrgName(selectedOrg.getName());

            if(hisManualJob.getTaskTypeName().equals("PACS系统收费明细") || hisManualJob.getTaskTypeName().equals("LIS系统收费明细")) {
                while (dataStartDate.before(dataEndDate) || dataStartDate.compareTo(dataEndDate) == 0) {
                    HisManualJob newHisManualJob = new HisManualJob();
                    BeanUtils.copyProperties(hisManualJob,newHisManualJob);
                    Calendar start = Calendar.getInstance();
                    start.setTime(dataStartDate);

                    newHisManualJob.setDataStartDate(dataStartDate);
                    start.add(Calendar.MONTH,1);
                    dataStartDate = start.getTime();
                    newHisManualJob.setDataEndDate(dataStartDate);
                    if(dataStartDate.after(dataEndDate) || dataStartDate.compareTo(dataEndDate) == 0){
                        newHisManualJob.setDataEndDate(dataEndDate);
                    }
                    hisManualJobService.saveManualData(newHisManualJob);
                }
            }

            if(hisManualJob.getTaskTypeName().equals("门诊收费明细")) {
                while (dataStartDate.before(dataEndDate) || dataStartDate.compareTo(dataEndDate) == 0) {
                    HisManualJob newHisManualJob = new HisManualJob();
                    BeanUtils.copyProperties(hisManualJob,newHisManualJob);
                    Calendar start = Calendar.getInstance();
                    start.setTime(dataStartDate);

                    newHisManualJob.setDataStartDate(dataStartDate);
                    start.add(Calendar.DAY_OF_MONTH,5);
                    dataStartDate = start.getTime();
                    newHisManualJob.setDataEndDate(dataStartDate);
                    if(dataStartDate.after(dataEndDate) || dataStartDate.compareTo(dataEndDate) == 0){
                        newHisManualJob.setDataEndDate(dataEndDate);
                    }
                    hisManualJobService.saveManualData(newHisManualJob);
                }
            }

        }else{
            hisManualJob.setOrgCode(selectedOrg.getOrgCode());
            hisManualJob.setOrgName(selectedOrg.getName());
            hisManualJobService.saveManualData(hisManualJob);
        }

        hisManualJob = new HisManualJob();

    }

    public void setToWaiting(String id) {
        selected = hisManualJobRepository.findById(id);
        if("done".equals(selected.getTaskStatus()) && "fail".equals(selected.getTaskResultStatus())){
            String taskType = selected.getTaskType();
            if(taskType.contains("-auto")){
                taskType = taskType.replaceAll("-auto", "");
                selected.setTaskType(taskType);
                selected.setTaskTypeName(selected.getTaskTypeName().replaceAll("-自动",""));
            }
        }
        selected.setTaskStatus(HisConstant.STATUS_WAIT);
        super.save();
    }

    public void setNotWaiting(String id) {
        selected = hisManualJobRepository.findById(id);
        selected.setTaskStatus(HisConstant.STATUS_INIT);
        super.save();
    }

    public void onTaskChange() {
        String taskTypeName = hisManualJob.getTaskTypeName();
        if(HisConstant.getTaskTypeByTimeList().contains(taskTypeName)){
            openFlag = true;
            return;
        }
        openFlag = false;
    }

    public String getStatusName(String str){
        switch (str){
            case HisConstant.STATUS_INIT:
                return HisConstant.STATUS_INIT_NAME;
            case HisConstant.STATUS_WAIT:
                return HisConstant.STATUS_WAIT_NAME;
            case HisConstant.STATUS_RUNING:
                return HisConstant.STATUS_RUNING_NAME;
            default :
                return HisConstant.STATUS_DONE_NAME;
        }
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        selectedOrg = new OrgInfo();
        hisManualJob = new HisManualJob();
        super.prepareCreate();

    }


}
