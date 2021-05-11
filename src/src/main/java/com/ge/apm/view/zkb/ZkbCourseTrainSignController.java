package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbCourseTrainRepository;
import com.ge.apm.dao.mapper.ZknContractCourseScheduleMapper;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbContractCourseSchedule;
import com.ge.apm.domain.ZkbCourseTrain;
import com.ge.apm.service.zkb.ZkbCourseTrainService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
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

/**
 * @author : 503189362
 * @date : 2021/4/8
 * @description :
 */
@ManagedBean
@ViewScoped
public class ZkbCourseTrainSignController extends GenericCRUDUUIDController<ZkbCourseTrain> {

    ZkbCourseTrainRepository zkbCourseTrainRepository;
    private UserAccount currentUser;
    private ZkbCourseTrainService zkbCourseTrainService;
    private List<ZkbContractCourseSchedule> zkbContractCourseSchedules = new ArrayList<>();
    private ZknContractCourseScheduleMapper zknContractCourseScheduleMapper;
    private String reason;
    private ZkbContractCourseSchedule selectedSchedule = new ZkbContractCourseSchedule();

    @Override
    protected void init() {
        super.init();
        zknContractCourseScheduleMapper =  WebUtil.getBean(ZknContractCourseScheduleMapper.class);
        zkbCourseTrainRepository =  WebUtil.getBean(ZkbCourseTrainRepository.class);
        zkbCourseTrainService =  WebUtil.getBean(ZkbCourseTrainService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        zkbContractCourseSchedules = zknContractCourseScheduleMapper.fetchByTenantUid(currentUser.getTenantUID());
    }


    public String applyAction(){
        return UrlEncryptController.encodeUrlParam("List");
    }


    public void joinNext(String courseScheduleId,String zkbContractCourseId){
        ZkbContractCourseSchedule schedule = null;
        for(ZkbContractCourseSchedule s : zkbContractCourseSchedules){
            if(courseScheduleId.equals(s.getId()) && zkbContractCourseId.equals(s.getZkbContractCourseId())){
                schedule = s;
                break;
            }
        }
        if(schedule != null){
            ZkbCourseTrain zkbCourseTrain = new ZkbCourseTrain();
            zkbCourseTrain.setPrePersonCount(schedule.getPrePersonCount());
            zkbCourseTrain.setParticipateStatus(ZkbCourseTrain.ParticipateStatusEnum.Y.toString());
            zkbCourseTrainService.save(schedule,zkbCourseTrain,currentUser);
            zkbContractCourseSchedules = zknContractCourseScheduleMapper.fetchByTenantUid(currentUser.getTenantUID());
        }
    }

    public void getSelectedSchedule(String courseScheduleId,String zkbContractCourseId){
        for(ZkbContractCourseSchedule s : zkbContractCourseSchedules){
            if(courseScheduleId.equals(s.getId()) && zkbContractCourseId.equals(s.getZkbContractCourseId())){
                selectedSchedule = s;
                break;
            }
        }
    }

    public void noJoinNext(){
        if(selectedSchedule == null) {
           return;
        }
        ZkbCourseTrain zkbCourseTrain = new ZkbCourseTrain();
        zkbCourseTrain.setReason(reason);
        zkbCourseTrain.setParticipateStatus(ZkbCourseTrain.ParticipateStatusEnum.N.toString());
        zkbCourseTrainService.save(selectedSchedule,zkbCourseTrain,currentUser);
        zkbContractCourseSchedules = zknContractCourseScheduleMapper.fetchByTenantUid(currentUser.getTenantUID());
    }
    public String redirectList(){
        return "List.xhtml?faces-redirect=true";
    }

    @Override
    protected GenericRepositoryUUID<ZkbCourseTrain> getDAO() {
        return zkbCourseTrainRepository;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<ZkbContractCourseSchedule> getZkbContractCourseSchedules() {
        return zkbContractCourseSchedules;
    }

    public void setZkbContractCourseSchedules(List<ZkbContractCourseSchedule> zkbContractCourseSchedules) {
        this.zkbContractCourseSchedules = zkbContractCourseSchedules;
    }

    public ZknContractCourseScheduleMapper getZknContractCourseScheduleMapper() {
        return zknContractCourseScheduleMapper;
    }

    public void setZknContractCourseScheduleMapper(ZknContractCourseScheduleMapper zknContractCourseScheduleMapper) {
        this.zknContractCourseScheduleMapper = zknContractCourseScheduleMapper;
    }
}
