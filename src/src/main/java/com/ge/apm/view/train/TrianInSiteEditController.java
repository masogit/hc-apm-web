package com.ge.apm.view.train;

import com.ge.apm.dao.TrainInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.OrgDto;
import com.ge.apm.domain.dto.TrainRoleDto;
import com.ge.apm.service.asset.QrCodeDrawService;
import com.ge.apm.service.train.TrainManageService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.qrcode.QrCodeSupportUtil;
import com.ge.healthcare.cn.common.utils.date.DateUnit;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class TrianInSiteEditController  extends GenericCRUDUUIDController<TrainInfo> {

    private TrainInfoRepository dao;

    private UserAccount loginUser;

    private UaaService uaaService;
    private UserContextService userContextService;
    private TrainManageService trainManageService;
    private QrCodeDrawService qrCodeDrawService;

    @Getter @Setter
    private String actionName = "";

    @Getter @Setter
    private String detailActionName = "";
    @Setter @Getter
    List<String> selectedOrgUid;
    @Getter @Setter
    List<OrgDto> orgsForSelected = new ArrayList<>(); //待选科室
    @Getter @Setter
    List<String> selectedRoleId;
    @Getter @Setter
    List<TrainRoleDto> rolesForSelected = new ArrayList<>();

    @Getter@Setter
    List<V2_BlobObject> blobObjectList = new ArrayList<>();

    @Getter@Setter
    List<V2_BlobObject> resultBlobObjectList = new ArrayList<>();

    @Getter @Setter
    private boolean canEdit = true;
    @Getter@Setter
    private String filterClinicalDeptOrg; //选定的所属科室
    @Getter @Setter
    private List<OrgInfo> clinicalDeptList;

    @Getter @Setter
    private List<TrainDetail> trainDetailList = new ArrayList<>();

    @Getter@Setter
    private List<TrainDetailSign> trainDetailSignList = new ArrayList<>();//签到信息List

    @Getter @Setter
    private TrainDetail selectedTrainDetail;

    @Getter @Setter
    private TrainDetailSign selectedTrainDetailSign;

    @Getter@Setter
    V2_BlobObject selectedBlobObject;
    @Getter@Setter
    String fileName;
    @Getter@Setter
    String fileType;
    @Getter @Setter
    private OrgInfo selectSite; //选定的所属院区
    @Getter @Setter
    private String filterSigner;
    @Setter
    private List<UserAccount> clinicalOwnerList;
    @Getter @Setter
    private List<OrgInfo> orgInfoList;
    @Getter @Setter
    private Integer signTotal;

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(TrainInfoRepository.class);
        loginUser = UserContextService.getCurrentUserAccount();
        uaaService = WebUtil.getBean(UaaService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        trainManageService = WebUtil.getBean(TrainManageService.class);
        qrCodeDrawService = WebUtil.getBean(QrCodeDrawService.class);
        selectSite = userContextService.getLoginUserOrgInfo();
        String encodeStr = WebUtil.getRequestParameter("str");
        actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "actionName");

        if ("Create".equalsIgnoreCase(actionName)) {
            prepareCreate();
        }else if("Edit".equalsIgnoreCase(actionName)){
            String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
            selected = dao.findById(selectedId);
            prepareEdit();
        }

    }

    public void prepareCreate(){
        selected = new TrainInfo();
        selected.setCreatedDate(new Date());
        orgsForSelected = trainManageService.onCreateInventory();
        rolesForSelected = trainManageService.queryTrainRole(loginUser);
        canEdit = true;
    }

    public void prepareEdit(){
        if(CollectionUtils.isEmpty(orgsForSelected)){
            orgsForSelected = trainManageService.onCreateInventory();
        }
        if(StrUtil.isNotEmpty(selected.getTrainingDeptUid())){
            selectedOrgUid = Arrays.asList(selected.getTrainingDeptUid().split(","));
        }
        if(CollectionUtils.isEmpty(rolesForSelected)){
            rolesForSelected = trainManageService.queryTrainRole(loginUser);
        }
        if(StrUtil.isNotEmpty(selected.getTrainingRoleUid())){
            selectedRoleId = Arrays.asList(selected.getTrainingRoleUid().split(","));
        }
        trainDetailList = trainManageService.queryDetailByTrainId(selected.getId());
        UserAccount userAccount = uaaService.getUserByUid(selected.getCreatedUid());
        if(!userAccount.getOrgInfoUID().equals(loginUser.getOrgInfoUID())){
            canEdit = false;
        }else{
            canEdit = true;
        }
        if (selected.getStatus().equals(TrainDetail.Status.Completed.name())) {
            canEdit = false;
        }
    }

    public void roleChange(){
        if(CollectionUtils.isEmpty(selectedRoleId)){
            return;
        }
        if(CollectionUtils.isEmpty(selectedOrgUid)){
            return;
        }
        Integer i = trainManageService.queryUserCountByRoles(selectedRoleId, selectedOrgUid);
        selected.setTrainingPeopleCount(i);
    }

    /**
     * 保存主课程信息
     */
    public String saveData(){
        if(!trainManageService.saveCheck(selected, trainDetailList)){
            return "";
        }
        trainManageService.save(selected, loginUser, selectedOrgUid, selectedRoleId);
        WebUtil.addSuccessMessage("保存成功！");
        String url = UrlEncryptController.encodeUrlParam("trainEdit?faces-redirect=true&actionName=Edit".concat("&selectedid=").concat(selected.getId()));
        System.out.println(url);
        return url;
    }

    /**
     * 返回训练主页面的导航路径
     */
    public String backList(){
        return "trainMain?faces-redirect=true";
    }


    public void clearData(){
        selected = null;
        selectedOrgUid = new ArrayList<>();
        selectedRoleId = new ArrayList<>();
        selectedTrainDetail = null;
        trainDetailList = new ArrayList<>();
    }

    /**
     * 子课程新增前方法
     */
    public void prepareItemCreate(){
        detailActionName = "Create";
        selectedTrainDetail = new TrainDetail();
        selectedTrainDetail.setTrainId(selected.getId());
        selectedTrainDetail.setStatus(TrainDetail.Status.Planned.name());
        //selectedTrainDetail.setDataStatus(TrainDetail.DataStatus.Create.name());
        blobObjectList = new ArrayList<>();
        resultBlobObjectList = new ArrayList<>();
        trainDetailSignList = new ArrayList<>();
        //selectedTrainDetail.setCreatedDate(new Date());
    }
    /**
     * 子课程编辑前方法
     */
    public void prepareItemEdit(){
        detailActionName = "Edit";
        blobObjectList = trainManageService.queryBlobByDetailId(selectedTrainDetail.getId());
        trainDetailSignList = trainManageService.queryDetailSignByDetailId(selectedTrainDetail.getId());
        resultBlobObjectList = trainManageService.queryResultBlobByDetailId(selectedTrainDetail.getId());

        if(CollectionUtils.isNotEmpty(trainDetailSignList)){
            if(StrUtil.isEmpty(selectedTrainDetail.getCompleteRecorder())){
                selectedTrainDetail.setCompleteRecorder(loginUser.getName());
            }
            signTotal = trainDetailSignList.size();
        }
    }

    public String getDeleteConformation(){
        if(selectedTrainDetail == null){
            return "";
        }
        List<TrainDetailSign> trainDetailSigns = trainManageService.queryDetailSignByDetailId(selectedTrainDetail.getId());
        if(CollectionUtils.isNotEmpty(trainDetailSigns)){
            return "该子课程已产生签到记录，删除后所有关联数据将永久清除，且无法恢复。请确认是否继续删除？";
        }else{
            return "是否确认删除？";
        }
    }

    private void loadClinicalDeptList() {
        clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> item.getOrgType() == 4).collect(Collectors.toList());
        if (null != selectSite) {
            clinicalDeptList = clinicalDeptList.stream().filter(item -> item.getSiteUID().equals(selectSite.getSiteUID()) || item.getHospitalUID().equals(selectSite.getSiteUID())
                    || item.getInstitutionUID().equals(selectSite.getSiteUID()) || item.getTenantUID().equals(selectSite.getSiteUID())).collect(Collectors.toList());
        }
    }
    public void onClinicalDeptNameFilterChange(AjaxBehaviorEvent event) {
        if (event instanceof SelectEvent) {
            filterClinicalDeptOrg = (String) ((SelectEvent) event).getObject();
        }
        if (!Strings.isNullOrEmpty(filterClinicalDeptOrg) && !clinicalDeptList.stream().anyMatch(item -> filterClinicalDeptOrg.equals(item.getName()))) {
            filterClinicalDeptOrg = null;
        }else{
            OrgInfo orgInfo = clinicalDeptList.stream().filter(item -> filterClinicalDeptOrg.equals(item.getName())).findFirst().get();
            selectedTrainDetailSign.setSignDeptName(orgInfo.getName());
            selectedTrainDetailSign.setSignDeptUid(orgInfo.getUid());
        }
        if(StrUtil.isNotEmpty(filterClinicalDeptOrg)){
            getClinicalOwnerList();
        }
    }
    public List<String> autoCompleteDeptName(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }
        List<OrgInfo> res = this.clinicalDeptList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                        || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase())))
                .collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }

    public List<UserAccount> completeClinicalOwnerList(String query) {
        if (clinicalOwnerList == null) {
            return Collections.emptyList();
        }

        List<UserAccount> res = this.clinicalOwnerList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    public void getClinicalOwnerList() {
        if (StrUtil.isNotEmpty(selectedTrainDetailSign.getSignDeptUid())) {
            clinicalOwnerList = uaaService.getUsersByDeptUid(selectedTrainDetailSign.getSignDeptUid());
        } else {
            clinicalOwnerList = Collections.emptyList();
        }
    }
    public void onSignerFilterChange(AjaxBehaviorEvent event) {
        UserAccount userAccount = null;
        if (event instanceof SelectEvent) {
            userAccount = (UserAccount) ((SelectEvent) event).getObject();
        }
        if(userAccount == null){
            filterSigner = null;
        }else{
            selectedTrainDetailSign.setSignerUid(userAccount.getUid());
            selectedTrainDetailSign.setSignerName(userAccount.getName());
            filterSigner = userAccount.getUid();
        }
        /*if (!Strings.isNullOrEmpty(filterSigner) && !clinicalOwnerList.stream().anyMatch(item -> filterSigner.equals(item.getName()))) {
            filterSigner = null;
        }else{
            UserAccount us = clinicalOwnerList.stream().filter(item -> filterSigner.equals(item.getName())).findFirst().get();
            selectedTrainDetailSign.setSignerUid(us.getUid());
            selectedTrainDetailSign.setSignerName(us.getName());
        }*/
    }

    public void signDeptUidValueChage(){
        String uid = selectedTrainDetailSign.getSignDeptUid();
        if(StrUtil.isEmpty(uid)){
            return;
        }
        OrgInfo orgInfo = orgInfoList.stream().filter(t -> t.getUid().equals(uid)).findFirst().get();
        selectedTrainDetailSign.setSignDeptName(orgInfo.getName());
    }

    /**
     * 签到新增前方法
     */
    public void prepareSignCreate(){
        loadClinicalDeptList();
        selectedTrainDetailSign = new TrainDetailSign();
        selectedTrainDetailSign.setSignDate(new Date());
        selectedTrainDetailSign.setDetailId(selectedTrainDetail.getId());
        orgInfoList = trainManageService.queryOrgToSign(selected.getTrainingDeptUid());
    }

    /**
     * 删除签到信息
     */
    public void deleteSign(){
        trainManageService.deleteSign(selectedTrainDetailSign);
        trainDetailSignList = trainManageService.queryDetailSignByDetailId(selectedTrainDetailSign.getDetailId());
    }
    /**
     * 保存签到信息
     */
    public void saveSign(){
        selectedTrainDetailSign.setSignDate(new Date());
        selectedTrainDetailSign.setSignOrder(trainDetailSignList.size() + 1);
        trainDetailSignList.add(selectedTrainDetailSign);
        signTotal = trainDetailSignList.size();
        selectedTrainDetail.setActualSignTotal(trainDetailSignList.size());
        selectedTrainDetailSign = new TrainDetailSign();
    }

    /**
     * 生成签到二维码
     */
    public void generateQrCode(){
        trainManageService.generateQrCode(selectedTrainDetail);
    }

    public String qrCodeView() throws Exception {
        if(selectedTrainDetail != null && StrUtil.isNotEmpty(selectedTrainDetail.getQrCodeStr())){
            String url = trainManageService.getBaseURL().replace("gateway","") + "wx/training/signin";
            url = url + "?" + selectedTrainDetail.getQrCodeStr();
            return  qrCodeDrawService.getImageBase64(QrCodeSupportUtil.createQrCodeWithoutLogo(selectedTrainDetail.getQrCodeStr(), url, Color.WHITE));
        }else{
            return "";
        }
    }

    /**
     * 子课程完成操作
     */
    public void completeDetail(){
        /*if(CollectionUtils.isEmpty(trainDetailSignList)){
            WebUtil.addErrorMessage("签到信息不能为空！");
            return;
        }*/
        if(selectedTrainDetail.getActualTrainDate() == null){
            WebUtil.addErrorMessage("实际培训完成日期不能为空！");
            return;
        }
        trainManageService.completeDetail(selectedTrainDetail, blobObjectList, selected,loginUser, resultBlobObjectList);
        selectedTrainDetail = null;
        trainDetailSignList = new ArrayList<>();
    }

    public void cancelDetail(){
        trainManageService.cancelDetail(selectedTrainDetail, selected,loginUser);
        selectedTrainDetail = null;
    }

    public void backDetail(){
        selectedTrainDetail = null;
    }

    /**
     * 子课程保存操作
     */
    public void saveDetail(){
        // 调用服务层保存培训详情信息
        trainDetailList = trainManageService.saveDetail(selectedTrainDetail, detailActionName, blobObjectList, selected, resultBlobObjectList, trainDetailSignList);
        // 重置选中的培训详情对象和培训详情标识列表
        selectedTrainDetail = null;
        trainDetailSignList = new ArrayList<>();
    }


    public List<V2_BlobObject> queryFileByDetailId(String id ){
        return trainManageService.queryBlobByDetailId(id);
    }

    public void deleteItem(){
        if(selectedTrainDetail != null){
            for(TrainDetail t : trainDetailList){
                if(t.getId().equals(selectedTrainDetail.getId())){
                    trainDetailList.remove(t);
                    trainManageService.deleteItem(selectedTrainDetail, selected);
                    selectedTrainDetail = null;
                    break;
                }
            }
        }
    }

    public void handleFileUpload(FileUploadEvent event){
        V2_BlobObject v2BlobObject = trainManageService.handleFileUpload(event, V2_BlobObject.BO_TYPE_TRAIN_DETAIL, null);
        if(v2BlobObject != null){
            blobObjectList.add(v2BlobObject);
        }
    }
    public void handleResultFileUpload(FileUploadEvent event){
        V2_BlobObject v2BlobObject = trainManageService.handleFileUpload(event, V2_BlobObject.BO_TYPE_TRAIN_SIGN_FILE, V2_BlobObject.BO_TYPE_TRAIN_SIGN_RESULT);
        if(v2BlobObject != null){
            resultBlobObjectList.add(v2BlobObject);
        }
    }
    public void handlePhotoFileUpload(FileUploadEvent event){
        V2_BlobObject v2BlobObject = trainManageService.handleFileUpload(event, V2_BlobObject.BO_TYPE_TRAIN_SIGN_FILE, V2_BlobObject.BO_TYPE_TRAIN_SIGN_PHOTO);
        if(v2BlobObject != null){
            resultBlobObjectList.add(v2BlobObject);
        }
    }

    public List<V2_BlobObject> getResultBlobList(String type){
        if(CollectionUtils.isEmpty(resultBlobObjectList)){
            return new ArrayList<>();
        }
        return resultBlobObjectList.stream().filter(t -> t.getBoSubType().equals(type)).collect(Collectors.toList());
    }

    public void removeFile(V2_BlobObject v2BlobObject){
        trainManageService.removeBlob(v2BlobObject);
        resultBlobObjectList.remove(v2BlobObject);
    }

    public void removeResultFile(V2_BlobObject v2BlobObject){
        trainManageService.removeBlob(v2BlobObject);
        resultBlobObjectList.remove(v2BlobObject);
    }

    public void prepareEditFile(V2_BlobObject v2BlobObject){
        selectedBlobObject = v2BlobObject;

        int index = selectedBlobObject.getObjectName().lastIndexOf(".");
        fileName = selectedBlobObject.getObjectName().substring(0, index);
        fileType = selectedBlobObject.getObjectName().substring(index, selectedBlobObject.getObjectName().length());
    }
    public void saveModifyFileName(){
        String fn = fileName + fileType;
        selectedBlobObject.setObjectName(fn);
        trainManageService.saveModifyFileName(selectedBlobObject);
    }

    public String getPrintSignDate(){
        if(selectedTrainDetail == null){
            return "";
        }
        return DateUtil.format(selectedTrainDetail.getPlanTrainDate(), "yyyy-MM-dd") + " " + DateUtil.format(selectedTrainDetail.getStartTime(), "HH:mm");
    }

    public String getTrainInstructors(){
        if(selectedTrainDetail == null){
            return "";
        }
        String internalInstructors = StrUtil.isEmpty(selectedTrainDetail.getInternalInstructors()) ? "" : selectedTrainDetail.getInternalInstructors();
        String externalInstructors = StrUtil.isEmpty(selectedTrainDetail.getExternalInstructors()) ? "" : selectedTrainDetail.getExternalInstructors();
        String externalInstructorsCompany = StrUtil.isEmpty(selectedTrainDetail.getExternalInstructorsCompany()) ? "" : selectedTrainDetail.getExternalInstructorsCompany();
        return internalInstructors.concat(" ").concat(externalInstructors).concat(" ").concat(externalInstructorsCompany);
    }

    @Override
    protected GenericRepositoryUUID<TrainInfo> getDAO() {
        return dao;
    }
}
