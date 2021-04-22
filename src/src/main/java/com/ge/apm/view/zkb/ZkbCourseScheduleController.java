package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbContractCourseRepository;
import com.ge.apm.dao.ZkbCourseRepository;
import com.ge.apm.dao.ZkbCourseScheduleRepository;
import com.ge.apm.dao.ZkbCourseTrainRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.zkb.ZkbCourseScheduleService;
import com.ge.apm.service.zkb.ZkbCourseTrainService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.DateUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;


/**
 * 课程计划表Controller
 */
@ManagedBean
@ViewScoped
public class ZkbCourseScheduleController extends GenericCRUDUUIDController<ZkbContractCourseSchedule> {

    /**
     * 课程计划表service
     */
    private ZkbCourseScheduleService zkbCourseScheduleService;

    /**
     * 获取用户信息
     */
    private UserContextService userContextService;

    /**
     * 报名service
     */
    private ZkbCourseTrainService zkbCourseTrainService;

    /**
     * 课程计划表repository
     */
    private ZkbCourseScheduleRepository zkbCourseScheduleRepository;

    /**
     * 获取所有目录信息
     */
    private ZkbCourseRepository zkbCourseRepository;

    /**
     * 获取用户合同中课程信息
     */
    private ZkbContractCourseRepository zkbContractCourseRepository;

    /**
     * 获取流程记录
     */
    private ZkbCourseTrainRepository zkbCourseTrainRepository;

    /**
     * 当前登录用户信息service
     */
    private UserAccount currentUser;

    /**
     * 是否代理商
     */
    private Boolean tenant = false;

    /**
     * 当前用户tenantUID
     */
    private String tenantUid;

    /**
     * 日期格式
     */
    private static final String dateFormat="yyyy-MM-dd";

    /**
     * 日期格式
     */
    private static final String dateFormat1="yyyy-MM-dd HH:mm:ss";

    /**
     * yes
     */
    private static final String yes = "Y";

    /**
     * no
     */
    private static final String no = "N";

    /**
     * false
     */
    private static final String f = "f";

    /**
     * true
     */
    private static final String t = "f";

    /**
     * 初始化
     */
    @Override
    protected void init() {
        super.init();
        zkbCourseScheduleService = WebUtil.getBean(ZkbCourseScheduleService.class);
        zkbCourseScheduleRepository = WebUtil.getBean(ZkbCourseScheduleRepository.class);
        zkbContractCourseRepository = WebUtil.getBean(ZkbContractCourseRepository.class);
        zkbCourseTrainRepository = WebUtil.getBean(ZkbCourseTrainRepository.class);
        zkbCourseRepository=WebUtil.getBean(ZkbCourseRepository.class);
        zkbCourseTrainService=WebUtil.getBean(ZkbCourseTrainService.class);
        userContextService=WebUtil.getBean(UserContextService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        if(userContextService.isZkbUser())
        {
            tenant=true;
            tenantUid=currentUser.getTenantUID();
        }
    }

    /**
     * 数据查询
     * @param pageRequest
     * @return
     */
    @Override
    protected Page<ZkbContractCourseSchedule> loadData(PageRequest pageRequest) {
        searchFilters= this.getSearchFilter(this.searchFilters);
        return zkbCourseScheduleRepository.findBySearchFilter(searchFilters, pageRequest);
    }

    /**
     * 动态组装列表过滤条件
     * @param csearchFilters
     * @return
     */
    public List<SearchFilter> getSearchFilter(List<SearchFilter> csearchFilters) {
        Date date = new Date();
        List<SearchFilter> searchFilters = csearchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }

        if(this.tenant)
        {
            searchFilters.add(new SearchFilter("valid", SearchFilter.Operator.EQ,"t"));
            String dateStr = DateUtil.getFormatStrByDate(date,dateFormat);
            List<ZkbContractCourse> zkbContractCourseList = this.zkbContractCourseRepository.findByDateAndTenantUid(dateStr,tenantUid);
            if(CollectionUtils.isEmpty(zkbContractCourseList))
            {
                searchFilters.add(new SearchFilter("id", SearchFilter.Operator.EQ,"error"));
                return searchFilters;
            }
            Set<String> set = new HashSet();
            for(ZkbContractCourse item:zkbContractCourseList)
            {
                set.add(item.getCourseId());
            }
            dateStr=dateStr+" 00:00:00";
            Date date1 = DateUtil.getDateByFormatStr(dateStr,dateFormat1);
            searchFilters.add(new SearchFilter("zkbCourseId", SearchFilter.Operator.IN,set));
            searchFilters.add(new SearchFilter("startDate",SearchFilter.Operator.LTE, date1));
            searchFilters.add(new SearchFilter("endDate",SearchFilter.Operator.GTE, date1));
        }
        return searchFilters;
    }

    public Boolean showJoinButton()
    {
        if(!this.tenant)
        {
            return false;
        }
        ZkbCourseTrain zkbCourseTrain = this.zkbCourseTrainRepository.findByUidAndSheduleId(this.tenantUid,this.selected.getId());
        if(zkbCourseTrain==null)
        {
            return true;
        }
            return false;
    }

    public Boolean showCancelButton()
    {
        if(!this.tenant)
        {
            return false;
        }
        ZkbCourseTrain zkbCourseTrain = this.zkbCourseTrainRepository.findByUidAndSheduleId(this.tenantUid,this.selected.getId());
        if(zkbCourseTrain!=null&&ZkbCourseTrain.ParticipateStatusEnum.Y.toString().equals(zkbCourseTrain.getParticipateStatus()))
        {
            return true;
        }
        return false;
    }

    /**
     * 是否有编辑权限
     * @return
     */
    public Boolean hasEditRight()
    {
        if(this.tenant)  return false;
        if(this.selected!=null&&(f.equals(this.selected.getValid())||StringUtils.isBlank(this.selected.getValid()))) return true;
        return false;
    }


    /**
     * 预创建
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();

    }

    /**
     * 预编辑
     */
    @Override
    public void prepareEdit() {
        super.prepareEdit();
    }

    /**
     * 删除
     */
    @Override
    public void delete() {
        super.delete();
        this.cancel();
    }

    /**
     * 取消
     */
    public void cancel() {
        this.selected = null;
    }

    @Override
    public void save() {
        super.save();
        this.selected = null;
    }


    /**
     * 报名
     * @param joinFlag 报名标识
     */
    public void join(String joinFlag)
        {
        if(yes.equals(joinFlag))
        {
            if(this.selected.getJoinCount()==null||this.selected.getJoinCount().intValue()==0)
            {
                WebUtil.addErrorMessage(WebUtil.getMessage("zkbContractCourseSchedul_joinCountCanotBeNull"));
                return;
            }
        }else if(no.equals(joinFlag))
        {
            if(StringUtils.isBlank(this.selected.getReason()))
            {
                WebUtil.addErrorMessage(WebUtil.getMessage("zkbContractCourseSchedul_reasonCanotBeNull"));
                return;
            }
        }
        try {
            this.zkbCourseTrainService.generateTrain(this.selected,joinFlag,this.currentUser,this.selected.getReason(),this.selected.getJoinCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.selected = null;
    }

    /**
     * 获取dao
     * @return
     */
    @Override
    protected GenericRepositoryUUID<ZkbContractCourseSchedule> getDAO() {
        return zkbCourseScheduleRepository;
    }

    public List<ZkbCourse> getAllZkbCourse()
    {
        return this.zkbCourseRepository.find();
    }

    public void changeCid()
    {
       ZkbCourse zkbCourse = this.zkbCourseRepository.findOne(this.selected.getZkbCourseId());
        this.selected.setCid(zkbCourse.getCid());
        this.selected.setCname(zkbCourse.getCname());
        this.selected.setModality(zkbCourse.getModality());
    }

    public Boolean getTenant() {
        return tenant;
    }

    public void setTenant(Boolean tenant) {
        this.tenant = tenant;
    }
}
