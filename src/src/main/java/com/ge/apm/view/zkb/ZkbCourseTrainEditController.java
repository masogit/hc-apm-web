package com.ge.apm.view.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.ZknContractCourseScheduleMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.zkb.ZkbCourseTrainService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
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
    private ZkbCourseTrainUserConfigRepository zkbCourseTrainUserConfigRepository;
    private ZknContractCourseScheduleMapper zknContractCourseScheduleMapper;
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
    private List<ZkbCourseTrainDetail> zkbCourseTrainDetailList = new ArrayList<>();
    private List<ZkbCourseTrainDetail> selectedCourseTrainDetailList = new ArrayList<>();
    private List<ZkbContractCourseSchedule> zkbContractCourseSchedules = new ArrayList<>();
    private ZkbContractCourseSchedule selectedCourseSchedule = new ZkbContractCourseSchedule();

    @Override
    protected void init() {
        super.init();
        zkbCourseTrainRepository =  WebUtil.getBean(ZkbCourseTrainRepository.class);
        zknContractCourseScheduleMapper =  WebUtil.getBean(ZknContractCourseScheduleMapper.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        zkbCourseTrainService = WebUtil.getBean(ZkbCourseTrainService.class);
        zkbCourseTrainUserConfigRepository = WebUtil.getBean(ZkbCourseTrainUserConfigRepository.class);
        blobFileService = WebUtil.getBean(BlobFileService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        String encodeStr = WebUtil.getRequestParameter("str");
        String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedId");
        if(StringUtils.isNotEmpty(selectedId)) {
            selected = zkbCourseTrainRepository.findById(selectedId);
            v2_blobObjectList = zkbCourseTrainService.queryBlobObjectByPid(selectedId);
            initFlag();
            initApplyList();
        }else {
            zkbContractCourseSchedules = zknContractCourseScheduleMapper.fetchByTenantUid(currentUser.getTenantUID());
            selected = new ZkbCourseTrain();
        }
    }

    public List<V2_BlobObject> queryByDetailEid(String eid){

        return null;
    }
    public void initFlag(){
        if(ZkbCourseTrain.FlowStepEnum.LetterPending.toString().equals(selected.getCurrentStep()) ||
            ZkbCourseTrain.FlowStepEnum.ApplyPending.toString().equals(selected.getCurrentStep())){
            if(currentUser.getTenantUID().equals(selected.getTenantUID())){
                canEditFlag = true;
            }
        }else {
            if(zkbCourseTrainService.isZkbAdmin(currentUser.getTenantUID())){
                List<ZkbCourseTrainUserConfig> zkbCourseTrainUserConfigs = zkbCourseTrainUserConfigRepository.findByLoginName(currentUser.getLoginName());
                if(zkbCourseTrainUserConfigs.size() > 0){
                    canEditFlag = true;
                }
            }
        }

        if(ZkbCourseTrain.FlowStatusEnum.ScheduleNoJoin.toString().equals(selected.getCurrentStatus())){
            showReasonFlag = true;
        }else if(ZkbCourseTrain.FlowStatusEnum.ScheduleJoin.toString().equals(selected.getCurrentStatus())){
            if(canEditFlag){
                showLetterFlag = true;
                showBtn = "0";
            }
        }else if(ZkbCourseTrain.FlowStatusEnum.LetterSubmit.toString().equals(selected.getCurrentStatus())){
            showLetterFlag = true;
            if(canEditFlag){
                showApplyFlag = true;
                showBtn = "0";
            }
        }else if(ZkbCourseTrain.FlowStatusEnum.LetterCancelJoin.toString().equals(selected.getCurrentStatus())){
            showLetterFlag = true;
            showReasonFlag = true;
        }else if(ZkbCourseTrain.FlowStatusEnum.ApplySubmit.toString().equals(selected.getCurrentStatus())){
            showLetterFlag = true;
            showApplyFlag = true;
            if(canEditFlag){
                showAuditFlag = true;
                showBtn = "1";
            }
        }else if(ZkbCourseTrain.FlowStatusEnum.ApplyCancelJoin.toString().equals(selected.getCurrentStatus())){
            showLetterFlag = true;
            showReasonFlag = true;
            if(canEditFlag){
                showReasonAuditFlag = true;
                showBtn = "1";
            }
        }else if(ZkbCourseTrain.FlowStatusEnum.ApplyCancelAccepted.toString().equals(selected.getCurrentStatus())){
            showLetterFlag = true;
            showReasonAuditFlag = true;
            showReasonFlag = true;
        }else if(ZkbCourseTrain.FlowStatusEnum.ApplyCancelRejected.toString().equals(selected.getCurrentStatus())){
            showLetterFlag = true;
            showReasonAuditFlag = true;
            showReasonFlag = true;
        }else if(ZkbCourseTrain.FlowStatusEnum.AuditAccepted.toString().equals(selected.getCurrentStatus())){
            showLetterFlag = true;
            showApplyFlag = true;
            showAuditFlag = true;
        }else if(ZkbCourseTrain.FlowStatusEnum.AuditRejected.toString().equals(selected.getCurrentStatus())){
            showLetterFlag = true;
            showApplyFlag = true;
            showAuditFlag = true;
            if(ZkbCourseTrain.FlowStepEnum.ApplyPending.toString().equals(selected.getCurrentStep())){
                showBtn = "0";
            }
        }
    }

    public void initApplyList(){
        if(ZkbCourseTrain.FlowStepEnum.ApplyPending.toString().equals(selected.getCurrentStep())){
            zkbCourseTrainDetailList = zkbCourseTrainService.queryInitDetails(selected);
        }else if(ZkbCourseTrain.FlowStepEnum.ApplyAuditPending.toString().equals(selected.getCurrentStep()) ||
                ZkbCourseTrain.FlowStepEnum.Completed.toString().equals(selected.getCurrentStep())){
            zkbCourseTrainDetailList = zkbCourseTrainService.querySaveDetail(selected);
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

    public String saveData() {
        if(selectedCourseSchedule == null){
            return "";
        }
        List<ZkbCourseTrain> zkbCourseTrains = zkbCourseTrainRepository.findByCourseScheduleIdAndParticipateStatus(selectedCourseSchedule.getId(),ZkbCourseTrain.ParticipateStatusEnum.Y.toString());
        if(zkbCourseTrains != null && zkbCourseTrains.size() > 0){
            WebUtil.addErrorMessage("该课程已申请过培训");
            return "";
        }
        selected.setParticipateStatus(ZkbCourseTrain.ParticipateStatusEnum.Y.toString());
        zkbCourseTrainService.save(selectedCourseSchedule,selected,currentUser);
        return "List?faces-redirect=true";

    }

    public void removeFile(V2_BlobObject v2_blobObject) {
        v2_blobObjectList = v2_blobObjectList.stream().filter(item -> !item.getId().equals(v2_blobObject.getId())).collect(Collectors.toList());
    }

    public String submitData(){
        if(ZkbCourseTrain.FlowStepEnum.ApplyPending.toString().equals(selected.getCurrentStep())){
            if(selectedCourseTrainDetailList.size() == 0){
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "请选择参加的工程师！", "");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return "";
            }
        }else if(ZkbCourseTrain.FlowStepEnum.LetterPending.toString().equals(selected.getCurrentStep())){
            if(v2_blobObjectList ==null || v2_blobObjectList.size() == 0){
               /* FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "请上传确认函！", "");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return "";*/
            }
        }
        zkbCourseTrainService.submitNext(selected,selectedCourseTrainDetailList,v2_blobObjectList,currentUser);
        return "List?faces-redirect=true";
    }

    public String noJoinNext(){
        selected.setReason(reason);
        zkbCourseTrainService.noJoinNext(selected);
        selected = zkbCourseTrainRepository.findById(selected.getId());
        return "List?faces-redirect=true";
    }

    public String acceptNext(){
        zkbCourseTrainService.submitNext(selected,selectedCourseTrainDetailList,v2_blobObjectList,currentUser);
        return "List?faces-redirect=true";
    }

    public String rejectNext(){
        zkbCourseTrainService.rejectedNext(selected,currentUser);
        return "List?faces-redirect=true";
    }

    public String backList(){
        return "List?faces-redirect=true";
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

    public List<ZkbCourseTrainDetail> getZkbCourseTrainDetailList() {
        return zkbCourseTrainDetailList;
    }

    public void setZkbCourseTrainDetailList(List<ZkbCourseTrainDetail> zkbCourseTrainDetailList) {
        this.zkbCourseTrainDetailList = zkbCourseTrainDetailList;
    }

    public List<ZkbCourseTrainDetail> getSelectedCourseTrainDetailList() {
        return selectedCourseTrainDetailList;
    }

    public void setSelectedCourseTrainDetailList(List<ZkbCourseTrainDetail> selectedCourseTrainDetailList) {
        this.selectedCourseTrainDetailList = selectedCourseTrainDetailList;
    }

    public List<ZkbContractCourseSchedule> getZkbContractCourseSchedules() {
        return zkbContractCourseSchedules;
    }

    public void setZkbContractCourseSchedules(List<ZkbContractCourseSchedule> zkbContractCourseSchedules) {
        this.zkbContractCourseSchedules = zkbContractCourseSchedules;
    }

    public ZkbContractCourseSchedule getSelectedCourseSchedule() {
        return selectedCourseSchedule;
    }

    public void setSelectedCourseSchedule(ZkbContractCourseSchedule selectedCourseSchedule) {
        this.selectedCourseSchedule = selectedCourseSchedule;
    }
}
