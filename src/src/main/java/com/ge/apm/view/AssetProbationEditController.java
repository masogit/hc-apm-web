package com.ge.apm.view;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.AssetProbationRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetProbation;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.approval.WxMessageService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.preview.PreViewerBean;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.core.message.MSGEvent;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetProbationEditController extends GenericCRUDUUIDController<AssetProbation> {
    private AssetProbationRepository dao;
    private UaaService uaaService;
    private BlobFileService blobService;
    private OrgInfoRepository orgInfoRepository;
    private WxMessageService wxMessageService;
    private MicroServiceUtil microServiceUtil;
    private PreViewerBean preViewerBean;

    @Getter @Setter
    private UserAccount loginUser;
    @Getter @Setter
    private OrgInfo assetDept;
    @Getter @Setter
    private UserAccount deptContact;
    @Getter @Setter
    private UserAccount detectionUser;
    @Getter @Setter
    private UserAccount probationConfirmor;
    @Getter @Setter
    private List<String> informationList;
    @Getter @Setter
    private List<String> specificationList;
    @Getter @Setter
    private List<OrgInfo> assetDeptList;
    @Getter @Setter
    private List<UserAccount> deptContactList;
    @Getter @Setter
    private List<UserAccount> detectionUserList;
    @Getter @Setter
    private List<UserAccount> probationConfirmorList;
    @Getter @Setter
    private List<V2_BlobObject> attachments = ListUtil.newArrayList();
    @Getter @Setter
    private Integer action;
    @Getter @Setter
    private String actionName;
    @Getter @Setter
    private Boolean canEditBase;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetProbationRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        wxMessageService = WebUtil.getBean(WxMessageService.class);
        microServiceUtil = WebUtil.getBean(MicroServiceUtil.class);
        preViewerBean = WebUtil.getBean(PreViewerBean.class);
        loginUser = UserContextService.getCurrentUserAccount();

        assetDeptList = uaaService.getClinicalDeptListBySiteUID(loginUser.getSiteUID());
        String encodeStr = WebUtil.getRequestParameter("str");
        action = Integer.parseInt(UrlEncryptController.getValueFromMap(encodeStr, "action").toString());
        actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "actionName");
        if (-1 == action && ObjectUtil.isEmpty(actionName)) {
            try {
                prepareCreate();
                this.selected.setCurrentPersonId(loginUser.getUid());
                this.selected.setCurrentPersonName(loginUser.getName());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            if ("View".equals(actionName)) {
                prepareView();
            } else {
                prepareEdit();
            }
            String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedId");
            this.selected = dao.findById(selectedId);
            deptContactList = Optional.ofNullable(this.selected.getAssetDeptUid()).map(assetDeptUid -> uaaService.getUsersByDeptUidAndRole(assetDeptUid, "ClinicalStaff")).orElse(ListUtil.empty());
            detectionUserList = Optional.ofNullable(this.loginUser.getSiteUID()).map(siteUid -> uaaService.getUsersBySiteUidAndRole(siteUid, "AssetStaff")).orElse(ListUtil.empty());
            probationConfirmorList = Optional.ofNullable(this.selected.getAssetDeptUid()).map(assetDeptUid -> uaaService.getUsersByDeptUidAndRole(assetDeptUid, "ClinicalStaff")).orElse(ListUtil.empty());
            this.assetDept = Optional.ofNullable(this.selected.getAssetDeptUid()).map(orgInfoRepository::getByUid).orElse(new OrgInfo());
            this.deptContact = Optional.ofNullable(this.selected.getDeptContactUid()).map(uaaService::getUserByUid).orElse(new UserAccount());
            this.detectionUser = Optional.ofNullable(this.selected.getDetectionUserUid()).map(uaaService::getUserByUid).orElse(new UserAccount());
            this.probationConfirmor = Optional.ofNullable(this.selected.getProbationStartUserUid()).map(uaaService::getUserByUid).orElse(new UserAccount());
            this.specificationList = Optional.ofNullable(this.selected.getSpecification()).map(specification -> Arrays.asList(specification.split(","))).orElse(ListUtil.empty());
            this.informationList = Optional.ofNullable(this.selected.getInformation()).map(information -> Arrays.asList(information.split(","))).orElse(ListUtil.empty());
        }
        canEditBase = canEditBase();
    }

    @Override
    protected GenericRepositoryUUID<AssetProbation> getDAO() {
        return dao;
    }

    public void tempSave(boolean flag) {
        Date now = DateUtil.now();
        if (this.selected.getId() == null) {
            String serialNumber = querySerialNumber(dao.queryOrderNum(loginUser.getTenantUID()));
            String dateStr = DateUtil.format(new Date(), "yyyyMMdd");
            this.selected.setOrderNum(dateStr + serialNumber);
            this.selected.setStatus(0);
            this.selected.setApplicantUserUid(loginUser.getUid());
            this.selected.setApplicantUserName(loginUser.getName());
            this.selected.setTenantUid(loginUser.getTenantUID());
            this.selected.setInstitutionUid(loginUser.getInstitutionUID());
            this.selected.setHospitalUid(loginUser.getHospitalUID());
            this.selected.setSiteUid(loginUser.getSiteUID());
            this.selected.setCreatedBy(loginUser.getName());
            this.selected.setCreatedDate(now);
        }
        if (this.selected.getStatus() == 0 || action == -11) {
            if (ObjectUtil.isNotEmpty(deptContact)) {
                this.selected.setDeptContactUid(deptContact.getUid());
                this.selected.setDeptContactName(deptContact.getName());
            }
            if (ObjectUtil.isNotEmpty(specificationList)) {
                this.selected.setSpecification(String.join(",", specificationList));
            }
            if (ObjectUtil.isNotEmpty(informationList)) {
                this.selected.setInformation(String.join(",", informationList));
            }
            if (ObjectUtil.isNotEmpty(assetDept)) {
                this.selected.setAssetDeptUid(assetDept.getUid());
                this.selected.setAssetDeptName(assetDept.getName());
            }
        }
        if(this.selected.getStatus() == 1 && flag && ObjectUtil.isNotEmpty(probationConfirmor)) {
            this.selected.setProbationStartUserUid(probationConfirmor.getUid());
            this.selected.setProbationStartUserName(probationConfirmor.getName());
        }
        this.selected.setLastModifiedBy(loginUser.getName());
        this.selected.setLastModifiedDate(now);
        if (flag) {
            dao.save(this.selected);
            if (ObjectUtil.isNotEmpty(attachments) && "Create".equals(super.crudActionName)) {
                attachments.forEach(att -> att.setBoUuid(this.selected.getId()));
                blobService.saveBlobObject(attachments);
            }
            WebUtil.addSuccessMessage(WebUtil.getMessage("tempSave") + WebUtil.getMessage("Success"));
        }
    }

    public String submit() {
        Date now = DateUtil.now();
        tempSave(false);
        String sendMsgUserUid = null;
        Map<String, String> eventParams = MapUtil.newHashMap();
        boolean valid = true;
        switch (this.selected.getStatus()) {
            case 0:
                this.selected.setStatus(1);
                this.selected.setApplicationTime(now);
                this.selected.setCurrentPersonId(detectionUser.getUid());
                this.selected.setCurrentPersonName(detectionUser.getName());
                this.selected.setDetectionUserUid(detectionUser.getUid());
                this.selected.setDetectionUserName(detectionUser.getName());
                sendMsgUserUid = detectionUser.getUid();
                eventParams.put("currentPerson", detectionUser.getName());
                eventParams.put("content", "您有新的设备试用工单需要检测");
                break;
            case 1:
                if (ObjectUtil.isEmpty(this.selected.getDetectionSituation())) {
                    WebUtil.addErrorMessage("检测情况不能为空！");
                    RequestContext.getCurrentInstance().addCallbackParam("detectionSituation", true);
                    valid = false;
                }
                if (ObjectUtil.isEmpty(this.probationConfirmor)) {
                    WebUtil.addErrorMessage("试用确认人不能为空！");
                    RequestContext.getCurrentInstance().addCallbackParam("probationUser", true);
                    valid = false;
                }
                if (!valid) {
                    break;
                }
                this.selected.setStatus(2);
                this.selected.setCurrentPersonId(probationConfirmor.getUid());
                this.selected.setCurrentPersonName(probationConfirmor.getName());
                this.selected.setProbationStartUserUid(probationConfirmor.getUid());
                this.selected.setProbationStartUserName(probationConfirmor.getName());
                this.selected.setDetectionTime(now);
                sendMsgUserUid = probationConfirmor.getUid();
                eventParams.put("currentPerson", probationConfirmor.getName());
                eventParams.put("content", "您有新的设备试用工单需要确认试用");
                break;
            case 2:
                this.selected.setStatus(3);
                this.selected.setProbationStartTime(now);
                break;
            case 3:
                this.selected.setStatus(4);
                this.selected.setCurrentPersonId(this.selected.getDetectionUserUid());
                this.selected.setCurrentPersonName(this.selected.getDetectionUserName());
                this.selected.setProbationEndUserUid(loginUser.getUid());
                this.selected.setProbationEndUserName(loginUser.getName());
                this.selected.setProbationEndTime(now);
                sendMsgUserUid = this.selected.getDetectionUserUid();
                eventParams.put("currentPerson", this.selected.getDetectionUserName());
                eventParams.put("content", "您有新的设备试用工单需要确认撤离");
                break;
            case 4:
                this.selected.setStatus(5);
                this.selected.setCurrentPersonId(this.selected.getApplicantUserUid());
                this.selected.setCurrentPersonName(this.selected.getApplicantUserName());
                this.selected.setEvacuationUserUid(loginUser.getUid());
                this.selected.setEvacuationUserName(loginUser.getName());
                this.selected.setEvacuationEndTime(now);
                sendMsgUserUid = this.selected.getApplicantUserUid();
                eventParams.put("currentPerson", this.selected.getApplicantUserName());
                eventParams.put("content", "您有新的设备试用工单需要关单");
                break;
            case 5:
                this.selected.setStatus(6);
                this.selected.setCloseUserUid(loginUser.getUid());
                this.selected.setCloseUserName(loginUser.getName());
                this.selected.setCloseTime(now);
                break;
            default:
                break;
        }
        boolean submitOrder = submitOrder();
        RequestContext.getCurrentInstance().addCallbackParam("submitDetection", false);
        if (!valid || !submitOrder) {
            return "";
        }
        dao.save(this.selected);
        if (ObjectUtil.isNotEmpty(attachments) && "Create".equals(super.crudActionName)) {
            attachments.forEach(att -> att.setBoUuid(this.selected.getId()));
            blobService.saveBlobObject(attachments);
        }
        if (ObjectUtil.isNotEmpty(sendMsgUserUid)) {
            sendMessage(sendMsgUserUid, eventParams);
        }
        WebUtil.addSuccessMessage(WebUtil.getMessage("Submit") + WebUtil.getMessage("Success"));
        return "externalAssetList?faces-redirect=true";
    }

    private void sendMessage(String sendMsgUserUid, Map<String, String> eventParams) {
        eventParams.put("status", WebUtil.getFieldValueMessage("probationStatus", this.selected.getStatus().toString()));
        eventParams.put("orderNum", this.selected.getOrderNum());
        eventParams.put("assetName", this.selected.getName() + "," + this.selected.getFunctionType() + "," + this.selected.getAssetDeptName());
        String linkUrl = microServiceUtil.getWebContextUrl() + "/wx/trial-asset?id=" + this.selected.getId();
        logger.info("sendMessage : " + JSON.toJSONString(eventParams));
        wxMessageService.sendWxMsgToMq(eventParams, linkUrl, MSGEvent.ASSET_PROBATION_PROCESS, ListUtil.newArrayList(sendMsgUserUid), null);
    }

    public boolean submitOrder() {
        boolean valid = true;
        if (ObjectUtil.isEmpty(this.selected.getName())) {
            WebUtil.addErrorMessage("设备名称不能为空！");
            RequestContext.getCurrentInstance().addCallbackParam("equipmentName", true);
            valid = false;
        }
        if (ObjectUtil.isEmpty(this.selected.getFunctionType())) {
            WebUtil.addErrorMessage("规格型号不能为空！");
            RequestContext.getCurrentInstance().addCallbackParam("functionType", true);
            valid = false;
        }
        if (ObjectUtil.isEmpty(this.assetDept)) {
            WebUtil.addErrorMessage("使用科室不能为空！");
            RequestContext.getCurrentInstance().addCallbackParam("assetDept", true);
            valid = false;
        }
        if (ObjectUtil.isEmpty(this.deptContact)) {
            WebUtil.addErrorMessage("使用科室联系人不能为空！");
            RequestContext.getCurrentInstance().addCallbackParam("deptContact", true);
            valid = false;
        }
        if (ObjectUtil.isEmpty(this.selected.getDeptContactTel())) {
            WebUtil.addErrorMessage("科室联系电话不能为空！");
            RequestContext.getCurrentInstance().addCallbackParam("deptContactTel", true);
            valid = false;
        }
        if (ObjectUtil.isEmpty(this.selected.getCompany())) {
            WebUtil.addErrorMessage("公司名称不能为空！");
            RequestContext.getCurrentInstance().addCallbackParam("company", true);
            valid = false;
        }
        if (ObjectUtil.isEmpty(this.selected.getCompanyContactName())) {
            WebUtil.addErrorMessage("公司联系人不能为空！");
            RequestContext.getCurrentInstance().addCallbackParam("companyContactName", true);
            valid = false;
        }
        if (ObjectUtil.isEmpty(this.selected.getCompanyContactTel())) {
            WebUtil.addErrorMessage("公司联系电话不能为空！");
            RequestContext.getCurrentInstance().addCallbackParam("companyContactTel", true);
            valid = false;
        }
        if (ObjectUtil.isEmpty(this.selected.getApprovalCompletedDate())) {
            WebUtil.addErrorMessage("审批完成日期不能为空！");
            RequestContext.getCurrentInstance().addCallbackParam("approvalCompletedDate", true);
            valid = false;
        }
        if (ObjectUtil.isEmpty(this.selected.getPlanStartDate())) {
            WebUtil.addErrorMessage("计划开始时间不能为空！");
            RequestContext.getCurrentInstance().addCallbackParam("planStartDate", true);
            valid = false;
        }
        if (ObjectUtil.isEmpty(this.selected.getPlanEndDate())) {
            WebUtil.addErrorMessage("计划结束时间不能为空！");
            RequestContext.getCurrentInstance().addCallbackParam("planEndDate", true);
            valid = false;
        }
        if (ObjectUtil.isEmpty(this.specificationList)) {
            WebUtil.addErrorMessage("说明书不能为空！");
            RequestContext.getCurrentInstance().addCallbackParam("specification", true);
            valid = false;
        }
        if (ObjectUtil.isEmpty(this.informationList)) {
            WebUtil.addErrorMessage("三证资料不能为空！");
            RequestContext.getCurrentInstance().addCallbackParam("threeInformation", true);
            valid = false;
        }
        if (ObjectUtil.isNotEmpty(this.selected.getStatus())) {
            if (this.selected.getStatus() == 3 && ObjectUtil.isEmpty(this.selected.getDetectionSituation())) {
                WebUtil.addErrorMessage("检测情况不能为空！");
                RequestContext.getCurrentInstance().addCallbackParam("detectionSituationRead", true);
                valid = false;
            }
        }
        if (valid) {
            RequestContext.getCurrentInstance().addCallbackParam("submitDetection", true);
        }
        return valid;
    }

    public String updateUser(Integer status) {
        String sendMsgUserUid = null;
        Map<String, String> eventParams = MapUtil.newHashMap();
        if (status == 1) {
            tempSave(false);
            if (ObjectUtil.isNotEmpty(this.selected.getDetectionUserUid()) && !this.selected.getDetectionUserUid().equals(detectionUser.getUid())) {
                sendMsgUserUid = detectionUser.getUid();
                eventParams.put("currentPerson", detectionUser.getName());
                eventParams.put("content", "您有新的设备试用工单需要检测");
            }
            this.selected.setDetectionUserUid(detectionUser.getUid());
            this.selected.setDetectionUserName(detectionUser.getName());
            this.selected.setCurrentPersonId(detectionUser.getUid());
            this.selected.setCurrentPersonName(detectionUser.getName());
        } else {
            if (ObjectUtil.isNotEmpty(this.selected.getProbationStartUserUid()) && !this.selected.getProbationStartUserUid().equals(probationConfirmor.getUid())) {
                sendMsgUserUid = probationConfirmor.getUid();
                eventParams.put("currentPerson", probationConfirmor.getName());
                eventParams.put("content", "您有新的设备试用工单需要确认试用");
            }
            this.selected.setProbationStartUserUid(probationConfirmor.getUid());
            this.selected.setProbationEndUserName(probationConfirmor.getName());
            this.selected.setCurrentPersonId(probationConfirmor.getUid());
            this.selected.setCurrentPersonName(probationConfirmor.getName());
        }
        dao.save(this.selected);
        if (ObjectUtil.isNotEmpty(sendMsgUserUid)) {
            sendMessage(sendMsgUserUid, eventParams);
        }
        return "externalAssetList?faces-redirect=true";
    }

    public List<Map<String, Object>> getStepList() {
        List<Map<String, Object>> stepList = ListUtil.newArrayList();
        if (this.selected.getStatus() >= 0) {
            Map<String, Object> step = MapUtil.newHashMap();
            step.put("stepName", "已创建");
            step.put("ownerName", this.selected.getCreatedBy());
            step.put("startTime", DateUtil.formatDateTime(this.selected.getCreatedDate()));
            stepList.add(step);
        }
        if (this.selected.getStatus() >= 1) {
            Map<String, Object> step = MapUtil.newHashMap();
            step.put("stepName", "待检测");
            step.put("ownerName", this.selected.getDetectionUserName());
            step.put("startTime", DateUtil.formatDateTime(this.selected.getDetectionTime()));
            stepList.add(step);
        }
        if (this.selected.getStatus() >= 2) {
            Map<String, Object> step = MapUtil.newHashMap();
            step.put("stepName", "待试用");
            step.put("ownerName", this.selected.getProbationStartUserName());
            step.put("startTime", DateUtil.formatDateTime(this.selected.getProbationStartTime()));
            stepList.add(step);
        }
        if (this.selected.getStatus() >= 3) {
            Map<String, Object> step = MapUtil.newHashMap();
            step.put("stepName", "试用中");
            step.put("ownerName", this.selected.getProbationEndUserName());
            step.put("startTime", DateUtil.formatDateTime(this.selected.getProbationEndTime()));
            stepList.add(step);
        }
        if (this.selected.getStatus() >= 4) {
            Map<String, Object> step = MapUtil.newHashMap();
            step.put("stepName", "待撤离");
            step.put("ownerName", this.selected.getEvacuationUserName());
            step.put("startTime", DateUtil.formatDateTime(this.selected.getEvacuationEndTime()));
            stepList.add(step);
        }
        if (this.selected.getStatus() >= 5) {
            Map<String, Object> step = MapUtil.newHashMap();
            step.put("stepName", "待关单");
            step.put("ownerName", this.selected.getCloseUserName());
            step.put("startTime", DateUtil.formatDateTime(this.selected.getCloseTime()));
            stepList.add(step);
        }
        if (this.selected.getCancel()) {
            Map<String, Object> step = MapUtil.newHashMap();
            step.put("stepName", "已取消");
            step.put("ownerName", this.selected.getCancelUserName());
            step.put("startTime", DateUtil.formatDateTime(this.selected.getCancelTime()));
            stepList.add(step);
        }
        return stepList;
    }

    private String querySerialNumber(String serialNumber){
        if(StrUtil.isEmpty(serialNumber)){
            return "0001";
        }else{
            int index = Integer.parseInt(serialNumber);
            index++;
            return String.format("%04d",index);
        }
    }

    public List<OrgInfo> autoCompleteDept(String query) {
        if (assetDeptList == null) {
            return Collections.emptyList();
        }

        return this.assetDeptList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() != null && item.getNamePyCode().contains(query.toLowerCase()))).sorted(new PinYinComparatorByName()).collect(Collectors.toList());
    }

    public void onAssetDeptChange(String idx) {
        deptContactList = uaaService.getUsersByDeptUidAndRole(this.assetDept.getUid(), "ClinicalStaff");
    }

    public List<UserAccount> autoCompleteEngineer(String query) {
        if (detectionUserList == null) {
            return Collections.emptyList();
        }

        return this.detectionUserList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() != null && item.getNamePyCode().contains(query.toLowerCase()))).sorted(new PinYinComparatorByName()).collect(Collectors.toList());
    }

    public List<UserAccount> autoCompleteConfirmor(String query) {
        if (probationConfirmorList == null) {
            return Collections.emptyList();
        }

        return this.probationConfirmorList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() != null && item.getNamePyCode().contains(query.toLowerCase()))).sorted(new PinYinComparatorByName()).collect(Collectors.toList());
    }

    public List<UserAccount> autoCompleteDeptContact(String query) {
        if (deptContactList == null) {
            return Collections.emptyList();
        }

        return this.deptContactList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() != null && item.getNamePyCode().contains(query.toLowerCase()))).sorted(new PinYinComparatorByName()).collect(Collectors.toList());
    }


    public List<V2_BlobObject> getAttachList(String subType) {
        if (this.selected.getId() != null) {
            attachments = blobService.findBlobObjectsByBoUid(this.selected.getId());
        }
        return attachments.stream().filter(item -> item.getBoSubType() != null && item.getBoSubType().equals(subType)).collect(Collectors.toList());
    }

    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        attachments = attachments.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toList());
    }

    public void handleFileUpload(FileUploadEvent event) {
        String type = event.getComponent().getId();
        long count = attachments.stream().filter(item -> item.getBoSubType() != null && item.getBoSubType().equals(type)).count();
        if (count == 10) {
            WebUtil.addErrorMessage("附件已达限制！");
            return;
        }
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            attachments.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_SUB_TYPE_ASSET_PROBATION);
            blobObject.setBoSubType(type);
            blobObject.setBoUuid(this.selected.getId());
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public boolean canEditBase() {
        if (this.selected.getId() == null) {
            return true;
        }
        if ("View".equals(actionName)) {
            return false;
        }
        return loginUser.getUid().equals(this.selected.getApplicantUserUid()) && this.selected.getStatus() <= 1;
    }

    public boolean canEditDetection() {
        if ("View".equals(actionName)) {
            return false;
        }
        return loginUser.getUid().equals(this.selected.getDetectionUserUid()) && this.selected.getStatus() <= 2;
    }

    public boolean viewDetection() {
        if ("View".equals(actionName) && this.selected.getStatus() == 1) {
            return false;
        }
        return this.selected.getStatus() >= 1 && this.action != -11;
    }

    public boolean viewEvacuation() {
        if ("View".equals(actionName) && this.selected.getStatus() == 3) {
            return false;
        }
        return this.selected.getStatus() >= 3;
    }

    public void preViewed(String objectId){
        preViewerBean.onServerEvent("",objectId);
    }

    public Date getPlanMaxDate() {
        Date planStartDate = this.selected.getPlanStartDate();
        if (ObjectUtil.isNotEmpty(planStartDate)) {
            return DateUtil.offsetMonth(planStartDate, 3);
        }
        return null;
    }

    public Date getPlanMinDate() {
        Date planEndDate = this.selected.getPlanEndDate();
        if (ObjectUtil.isNotEmpty(planEndDate)) {
            return DateUtil.offsetMonth(planEndDate, -3);
        }
        return null;
    }
}
