package com.ge.apm.view.schedule;

import com.ge.apm.dao.UserScheduleRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserSchedule;
import com.ge.apm.service.schedule.UserScheduleService;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.*;

/**
 * Created by 212605082 on 2018/1/12.
 */
@ManagedBean
@ViewScoped
public class UserVacationScheduleController extends GenericCRUDUUIDController<UserSchedule> implements Serializable, ServerEventInterface {

    //休假
    private final String SCHEDULE_TYPE_VACATION = "V";
    //排班
    private final String SCHEDULE_TYPE_WORK = "W";

    private UserScheduleRepository dao;

    private UserAccount currentUser;

    private UserScheduleService userScheduleService;

    private Date startDate;
    private Date endDate;

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(UserScheduleRepository.class);
        userScheduleService = WebUtil.getBean(UserScheduleService.class);
        currentUser = UserContextService.getCurrentUserAccount();
    }

    @Override
    protected GenericRepositoryUUID<UserSchedule> getDAO() {
        return dao;
    }

    @Override
    protected Page<UserSchedule> loadData(PageRequest pageRequest) {
        this.selected = null;

        if (this.searchFilters == null) {
            this.searchFilters = new ArrayList<>();
        }

        searchFilters.add(new SearchFilter("userUID", SearchFilter.Operator.EQ, currentUser.getUid()));
        searchFilters.add(new SearchFilter("type", SearchFilter.Operator.EQ, SCHEDULE_TYPE_VACATION));
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    public String saveVacation(){

        if(!isValideDate()){
            return "";
        }

        List<LinkedHashMap<String, Date>> resultList = userScheduleService.saveVacation(startDate, endDate);

        if(!resultList.isEmpty()){
            String msg = "";
            for (Map<String, Date> msgMap : resultList) {
                msg += "无效日期：" + msgMap.get("invalidDate") + ";";
            }
            WebUtil.addErrorMessage(msg);
            return "";
        }

        return "userVacationList?faces-redirect=true";
    }

    public boolean isValideDate(){
        Calendar calender = Calendar.getInstance();
        calender.setTime(new Date());
        calender.add(Calendar.DAY_OF_MONTH, -1);

        if (startDate.before(calender.getTime())) {
            WebUtil.addErrorMessage("开始日期不能早于当前日期");
            return false;
        }
        if (endDate.before(startDate)) {
            WebUtil.addErrorMessage("结束日期不能早于开始日期");
            return false;
        }

        return true;
    }

    @Override
    public void delete() {
        userScheduleService.deleteUserSchedule(this.selected);
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {}

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
