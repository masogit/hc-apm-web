package com.ge.apm.view.zkb;

 import com.ge.apm.dao.ZkbCourseTrainRepository;
 import com.ge.apm.domain.TenantInfo;
 import com.ge.apm.domain.UserAccount;
 import com.ge.apm.domain.ZkbCourseTrain;
 import com.ge.apm.view.sysutil.UrlEncryptController;
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
public class ZkbCourseTrainController extends GenericCRUDUUIDController<ZkbCourseTrain> {

    ZkbCourseTrainRepository zkbCourseTrainRepository;
    private UserAccount currentUser;
    private Page<ZkbCourseTrain> zkbCourseTrainPage;
    private List<TenantInfo> tenantInfoList;
    private String selectionTenantInfo;

    @Override
    protected void init() {
        super.init();
        zkbCourseTrainRepository =  WebUtil.getBean(ZkbCourseTrainRepository.class);

    }

    @Override
    protected Page<ZkbCourseTrain> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }

        if(this.selectionTenantInfo != null){
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectionTenantInfo));
        }

        zkbCourseTrainPage = zkbCourseTrainRepository.findBySearchFilter(searchFilters, pageRequest);
        return zkbCourseTrainPage;
    }

    public String editAction() {
        return UrlEncryptController.encodeUrlParam("Edit?action=Edit&selectedId=".concat(selected.getId()));
    }

    @Override
    protected GenericRepositoryUUID<ZkbCourseTrain> getDAO() {
        return zkbCourseTrainRepository;
    }

    public List<TenantInfo> getTenantInfoList() {
        return tenantInfoList;
    }

    public void setTenantInfoList(List<TenantInfo> tenantInfoList) {
        this.tenantInfoList = tenantInfoList;
    }

    public String getSelectionTenantInfo() {
        return selectionTenantInfo;
    }

    public void setSelectionTenantInfo(String selectionTenantInfo) {
        this.selectionTenantInfo = selectionTenantInfo;
    }

    public Page<ZkbCourseTrain> getZkbCourseTrainPage() {
        return zkbCourseTrainPage;
    }

    public void setZkbCourseTrainPage(Page<ZkbCourseTrain> zkbCourseTrainPage) {
        this.zkbCourseTrainPage = zkbCourseTrainPage;
    }
}
