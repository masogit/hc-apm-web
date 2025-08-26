package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbTrainPublishRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbTrainCourse;
import com.ge.apm.domain.ZkbTrainPublishInfo;
import com.ge.apm.service.zkb.ZkbTrainCourseQuotaService;
import com.ge.apm.service.zkb.ZkbTrainPublishService;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
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
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class ZkbTrainPublishController extends GenericCRUDUUIDController<ZkbTrainPublishInfo> {
    private ZkbTrainPublishRepository dao;
    private String actionName;

    private ZkbTrainCourseQuotaService zkbTrainCourseQuotaService;
    private ZkbTrainPublishService zkbTrainPublishService;
    private UserAccount currentUser;

    @Getter
    @Setter
    private ZkbTrainCourse selectedCourse;

    @Getter@Setter
    private List<ZkbTrainCourse> zkbTrainCourseList = new ArrayList<>();

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(ZkbTrainPublishRepository.class);
        zkbTrainCourseQuotaService = WebUtil.getBean(ZkbTrainCourseQuotaService.class);
        zkbTrainPublishService = WebUtil.getBean(ZkbTrainPublishService.class);
        currentUser = UserContextService.getCurrentUserAccount();

    }

    @Override
    protected Page<ZkbTrainPublishInfo> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }

        return dao.findBySearchFilter(searchFilters, pageRequest);
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        this.actionName = "Create";
        selected = new ZkbTrainPublishInfo();
        zkbTrainCourseList = zkbTrainCourseQuotaService.queryTrainCourse();
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        this.actionName = "Edit";
        zkbTrainCourseList = zkbTrainCourseQuotaService.queryTrainCourse();
        selectedCourse = selected.getZkbTrainCourse();

    }

    public List<ZkbTrainCourse> autoCompleteCourse(String query){
        List<ZkbTrainCourse> res = zkbTrainCourseList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)).collect(Collectors.toList());

        return res;
    }


    public void courseSelect(){
        selected.setZkbTrainCourse(selectedCourse);
    }

    public void saveData(){
        zkbTrainPublishService.save(selected, currentUser,selectedCourse);
    }

    @Override
    protected GenericRepositoryUUID<ZkbTrainPublishInfo> getDAO() {
        return dao;
    }
}
