package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbTrainCourseRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbCourse;
import com.ge.apm.domain.ZkbTrainCourse;
import com.ge.apm.service.zkb.ZkbTrainCourseService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ManagedBean
@ViewScoped
public class ZkbTrainCourseController  extends GenericCRUDUUIDController<ZkbTrainCourse> {

    ZkbTrainCourseService zkbTrainCourseService;

    ZkbTrainCourseRepository dao;

    @Getter @Setter
    private UploadedFile file;

    private String actionName;

    private UserAccount currentUser;

    @Getter @Setter
    private List<ZkbTrainCourse> importFailedDataList;

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(ZkbTrainCourseRepository.class);
        zkbTrainCourseService = WebUtil.getBean(ZkbTrainCourseService.class);
        currentUser = UserContextService.getCurrentUserAccount();
    }

    @Override
    protected Page<ZkbTrainCourse> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }

        return dao.findBySearchFilter(searchFilters, pageRequest);
    }

    @Override
    public void prepareCreate(){
        this.actionName = "Create";
        selected = new ZkbTrainCourse();
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        this.actionName = "Edit";

    }

    public void saveCourse(){
        if(!zkbTrainCourseService.checkCourse(selected)){
            WebUtil.addSuccessMessage("课程已存在！");
        }
        zkbTrainCourseService.saveCourse(selected);
        selected = null;
    }


    public void handleFileUpload(FileUploadEvent event) throws IOException {

        this.file = event.getFile();
        importFailedDataList = zkbTrainCourseService.handleExcel(file, currentUser);
        if(importFailedDataList != null && importFailedDataList.size()==0){
            WebUtil.addSuccessMessage("导入完成！");
        }
        else{
            WebUtil.addErrorMessage("导入失败！请修改错误数据");
        }

    }

    public void delete(){
        if(!zkbTrainCourseService.checkDelete(selected)){
            WebUtil.addErrorMessage("删除失败，该课程已被使用");
            return;
        }
        super.delete();
    }

    public List<HashMap<String,String>> queryNormalCourse(){
        return zkbTrainCourseService.queryNormalCourse();
    }

    @Override
    protected GenericRepositoryUUID<ZkbTrainCourse> getDAO() {
        return dao;
    }
}
