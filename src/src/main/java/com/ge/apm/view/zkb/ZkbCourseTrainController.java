package com.ge.apm.view.zkb;

 import com.ge.apm.dao.ZkbCourseTrainRepository;
 import com.ge.apm.domain.TenantInfo;
 import com.ge.apm.domain.UserAccount;
 import com.ge.apm.domain.ZkbCourseTrain;
 import com.ge.apm.domain.ZkbCourseTrainUserConfig;
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
public class ZkbCourseTrainController extends GenericCRUDUUIDController<ZkbCourseTrain> {

    ZkbCourseTrainRepository zkbCourseTrainRepository;
    private UserAccount currentUser;
    private Page<ZkbCourseTrain> zkbCourseTrainPage;
    private List<TenantInfo> tenantInfoList;
    private String selectionTenantInfo;
    private ZkbCourseTrainService zkbCourseTrainService;
    private boolean showBtn = false;
    private UserContextService userContextService;


    @Override
    protected void init() {
        super.init();
        zkbCourseTrainRepository =  WebUtil.getBean(ZkbCourseTrainRepository.class);
        zkbCourseTrainService =  WebUtil.getBean(ZkbCourseTrainService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        if(userContextService.isZkbUser()) {
            this.selectionTenantInfo = currentUser.getTenantUID();
        }
        tenantInfoList = zkbCourseTrainService.getSourceTenantInfoList();
        initBtn();
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
    public void initBtn(){
        if(!zkbCourseTrainService.isZkbAdmin(currentUser.getTenantUID())){
            showBtn = true;
        }else{
            showBtn = false;
        }
    }

    public String redirectLSign(){
        return "Sign.xhtml?faces-redirect=true";
    }

    public String createAction(){
        return UrlEncryptController.encodeUrlParam("Edit?action=Create");
    }

    public String editAction() {
        return UrlEncryptController.encodeUrlParam("Edit?action=Edit&selectedId=".concat(selected.getId()));
    }

    public TenantInfo getTenantInfo(String tenantInfoUID){
        return zkbCourseTrainService.getTenantInfo(tenantInfoUID);
    }


    @Override
    public void delete() {
        if(!ZkbCourseTrain.FlowStepEnum.LetterPending.toString().equals(selected.getCurrentStep())){
            WebUtil.addErrorMessage("数据不能删除");
            return;
        }
        zkbCourseTrainService.delete(selected);
    }

    @Override
    protected GenericRepositoryUUID<ZkbCourseTrain> getDAO() {
        return zkbCourseTrainRepository;
    }

    public boolean isShowBtn() {
        return showBtn;
    }

    public void setShowBtn(boolean showBtn) {
        this.showBtn = showBtn;
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
