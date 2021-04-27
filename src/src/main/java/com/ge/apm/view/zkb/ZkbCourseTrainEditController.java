package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbCourseTrainRepository;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.ZkbCourseTrain;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.zkb.ZkbCourseTrainService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : 503189362
 * @date : 2021/4/9
 * @description :
 */
@ManagedBean
@ViewScoped
public class ZkbCourseTrainEditController extends GenericCRUDUUIDController<ZkbCourseTrain> {
    private UserAccount currentUser;
    private ZkbCourseTrainRepository zkbCourseTrainRepository;
    private UserContextService userContextService;
    private ZkbCourseTrainService zkbCourseTrainService;
    private List<V2_BlobObject> v2_blobObjectList;
    private BlobFileService blobFileService;
    private boolean showLetterFlag = false;
    private boolean showApplyFlag = false;
    private boolean showAuditFlag = false;
    private boolean showReasonFlag = false;
    private boolean showReasonAuditFlag = false;
    private boolean canEditFlag = false;
    private String showBtn = "";
    private String reason;

    @Override
    protected void init() {
        super.init();
        zkbCourseTrainRepository =  WebUtil.getBean(ZkbCourseTrainRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        zkbCourseTrainService = WebUtil.getBean(ZkbCourseTrainService.class);
        blobFileService = WebUtil.getBean(BlobFileService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        String encodeStr = WebUtil.getRequestParameter("str");
        String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedId");
        if(StringUtils.isNotEmpty(selectedId)) {
            selected = zkbCourseTrainRepository.findById(selectedId);
            v2_blobObjectList = zkbCourseTrainService.queryBlobObjectByPid(selectedId);
            initFlag();
        }
    }
    public void initFlag(){
        if(ZkbCourseTrain.FlowStepEnum.LetterPending.toString().equals(selected.getCurrentStep()) || ZkbCourseTrain.FlowStepEnum.ApplyPending.toString().equals(selected.getCurrentStep())){
            if(currentUser.getTenantUID().equals(selected.getTenantUID())){
                canEditFlag = true;
            }
        }else {
            if(zkbCourseTrainService.isZkbAdmin(currentUser.getTenantUID())){
                canEditFlag = true;
            }
        }

        if(ZkbCourseTrain.FlowStatusEnum.ScheduleNoJoin.toString().equals(selected.getCurrentStatus())){
            showLetterFlag = false;
        }else if(ZkbCourseTrain.FlowStatusEnum.ScheduleJoin.toString().equals(selected.getCurrentStatus())){
            showLetterFlag = true;
            if(canEditFlag){
                showBtn = "0";
            }
        }else if(ZkbCourseTrain.FlowStatusEnum.LetterSubmit.toString().equals(selected.getCurrentStatus())){
            showLetterFlag = true;
            showApplyFlag = true;
            if(canEditFlag){
                showBtn = "0";
            }
        }else if(ZkbCourseTrain.FlowStatusEnum.LetterCancelJoin.toString().equals(selected.getCurrentStatus())){
            showLetterFlag = true;
            showReasonFlag = true;
        }else if(ZkbCourseTrain.FlowStatusEnum.ApplySubmit.toString().equals(selected.getCurrentStatus())){
            showLetterFlag = true;
            showApplyFlag = true;
            showAuditFlag = true;
            if(canEditFlag){
                showBtn = "1";
            }
        }else if(ZkbCourseTrain.FlowStatusEnum.ApplyCancelJoin.toString().equals(selected.getCurrentStatus())){
            showLetterFlag = true;
            showReasonFlag = true;
            if(canEditFlag){
                showBtn = "1";
            }
        }else if(ZkbCourseTrain.FlowStatusEnum.ApplyCancelAccepted.toString().equals(selected.getCurrentStatus())){
            showLetterFlag = true;
            showReasonAuditFlag = true;
        }else if(ZkbCourseTrain.FlowStatusEnum.ApplyCancelRejected.toString().equals(selected.getCurrentStatus())){
            showLetterFlag = true;
            showReasonAuditFlag = true;
        }else if(ZkbCourseTrain.FlowStatusEnum.AuditAccepted.toString().equals(selected.getCurrentStatus())){
            showLetterFlag = true;
            showApplyFlag = true;
            showAuditFlag = true;
        }else if(ZkbCourseTrain.FlowStatusEnum.AuditRejected.toString().equals(selected.getCurrentStatus())){
            showLetterFlag = true;
            showApplyFlag = true;
            showAuditFlag = true;
        }
    }


    public void handleFileUpload(FileUploadEvent event){
        V2_BlobObject v2_blobObject = blobFileService.uploadFile(event);
        if (null != v2_blobObject) {
            v2_blobObject.setBoType(V2_BlobObject.BO_TYPE_ZKB_COURSE);
            v2_blobObject.setBoSubType(V2_BlobObject.BO_TYPE_ZKB_COURSE_TRAIN);
            v2_blobObject.setBoUuid(selected.getId());
            v2_blobObjectList.add(v2_blobObject);
            WebUtil.addSuccessMessage(v2_blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public void removeFile(V2_BlobObject v2_blobObject) {
        v2_blobObjectList = v2_blobObjectList.stream().filter(item -> !item.getId().equals(v2_blobObject.getId())).collect(Collectors.toList());
    }

    public void submitData(){
        zkbCourseTrainService.submitNext(selected);
    }

    public void noJoinNext(){

    }

    public void acceptNext(){

    }

    public void rejectNext(){

    }
    @Override
    protected GenericRepositoryUUID<ZkbCourseTrain> getDAO() {
        return zkbCourseTrainRepository;
    }

    public List<V2_BlobObject> getV2_blobObjectList() {
        return v2_blobObjectList;
    }

    public void setV2_blobObjectList(List<V2_BlobObject> v2_blobObjectList) {
        this.v2_blobObjectList = v2_blobObjectList;
    }

    public boolean isShowLetterFlag() {
        return showLetterFlag;
    }

    public void setShowLetterFlag(boolean showLetterFlag) {
        this.showLetterFlag = showLetterFlag;
    }

    public boolean isShowApplyFlag() {
        return showApplyFlag;
    }

    public void setShowApplyFlag(boolean showApplyFlag) {
        this.showApplyFlag = showApplyFlag;
    }

    public boolean isShowAuditFlag() {
        return showAuditFlag;
    }

    public void setShowAuditFlag(boolean showAuditFlag) {
        this.showAuditFlag = showAuditFlag;
    }

    public boolean isShowReasonFlag() {
        return showReasonFlag;
    }

    public void setShowReasonFlag(boolean showReasonFlag) {
        this.showReasonFlag = showReasonFlag;
    }

    public boolean isShowReasonAuditFlag() {
        return showReasonAuditFlag;
    }

    public void setShowReasonAuditFlag(boolean showReasonAuditFlag) {
        this.showReasonAuditFlag = showReasonAuditFlag;
    }

    public boolean isCanEditFlag() {
        return canEditFlag;
    }

    public void setCanEditFlag(boolean canEditFlag) {
        this.canEditFlag = canEditFlag;
    }

    public String getShowBtn() {
        return showBtn;
    }

    public void setShowBtn(String showBtn) {
        this.showBtn = showBtn;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
