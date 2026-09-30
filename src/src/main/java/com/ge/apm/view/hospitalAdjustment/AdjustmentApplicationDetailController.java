package com.ge.apm.view.hospitalAdjustment;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.IdleEquipmentApplicationRepository;
import com.ge.apm.dao.IdleEquipmentStockRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.hospitalAdjustment.IdleEquipmentApplicationService;
import com.ge.apm.service.remote.MasterDataService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.util.CollectionUtils;
import sun.misc.BASE64Decoder;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionListener;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@ManagedBean
@ViewScoped
public class AdjustmentApplicationDetailController extends GenericCRUDUUIDController<IdleEquipmentApplication> implements Serializable, ServerEventInterface {
    private IdleEquipmentApplicationRepository dao;
    private IdleEquipmentStockRepository stockRepository;

    private UserAccount currentUserAccount;


    private AssetInfoRepository assetInfoRepository;

    private MasterDataService masterDataService;
    private AssetInfo selectedAsset;


    private UserAccountRepository userDao;


    private IdleEquipmentApplicationService idleEquipmentApplicationService;

    @Getter
    @Setter
    private List<String> selectedBlobIdList;
    @Getter
    @Setter
    private List<I18nMessage> contractAttachmentTypeList;
    @Getter
    @Setter
    private List<V2_BlobObject> uploadedFileObjects = new ArrayList();
    // 签名相关
    @Getter
    @Setter
    private String signatureContent;
    @Getter
    private StreamedContent signatureContentStream;
    @Getter
    private String signatureId;
    @Getter
    @Setter
    private List<String> attachments=new ArrayList<>();
    private StreamedContent exportFile;
    @Getter
    @Setter
    private String fieldsetName;
    @Override
    protected GenericRepositoryUUID<IdleEquipmentApplication> getDAO() {
        return dao;
    }


    @Override
    protected void init() {
        dao = WebUtil.getBean(IdleEquipmentApplicationRepository.class);
        stockRepository = WebUtil.getBean(IdleEquipmentStockRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        masterDataService = WebUtil.getBean(MasterDataService.class);
        idleEquipmentApplicationService = WebUtil.getBean(IdleEquipmentApplicationService.class);
        currentUserAccount = UserContextService.getCurrentUserAccount();
        String encodeStr = WebUtil.getRequestParameter("str");
        String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "actionName");

        String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
        if(selectedId==null){
            return;
        }
        setSelected(dao.findByUid(selectedId));
        if(selected==null){
            return;
        }
        String assetUid = selected.getAssetUid();
        if (assetUid != null) {
            selectedAsset = assetInfoRepository.findByUid(assetUid);
        }
        if("Approval".equals(actionName)){
            selected.setAuditDate(new Date());
        }

        attachements = blobService.findBlobObjectsByBoUid(selected.getUid());
        if(selected.getAttachments()!=null){
            attachments= JSONObject.parseArray(selected.getAttachments(),String.class);

        }
        List<I18nMessage> contractAttachmentType = FieldValueMessageController.getFieldValueList("HospitalAdjustmentAttachmentType");
        contractAttachmentTypeList = contractAttachmentType.stream().sorted(Comparator.comparingInt(I18nMessage::getId)).collect(Collectors.toList());
        blobObjectSelectedMap = new HashMap();
    }


    public void initDteail(String selectedId) {

        setSelected(dao.findByUid(selectedId));
        String assetUid = selected.getAssetUid();
        if (assetUid != null) {
            selectedAsset = assetInfoRepository.findByUid(assetUid);
        }

        attachements = blobService.findBlobObjectsByBoUid(selected.getUid());
        if(selected.getAttachments()!=null){
            attachments= JSONObject.parseArray(selected.getAttachments(),String.class);

        }
        List<I18nMessage> contractAttachmentType = FieldValueMessageController.getFieldValueList("HospitalAdjustmentAttachmentType");
        contractAttachmentTypeList = contractAttachmentType.stream().sorted(Comparator.comparingInt(I18nMessage::getId)).collect(Collectors.toList());
        blobObjectSelectedMap = new HashMap();

    }


    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        AssetInfo asset = (AssetInfo) eventObject;
        if (asset == null) {
            return;
        }
        onSelectedAsset(asset);
        this.selectedAsset = asset;

    }


    private void onSelectedAsset(AssetInfo asset) {

        selectedAsset = asset;
    }


    @Getter
    @Setter
    private Map<String, Boolean> blobObjectSelectedMap;
    private List<String> removedAttachments = new ArrayList();

    @Getter
    @Setter
    private List<V2_BlobObject> attachements;

    @Getter
    @Setter
    private StreamedContent attachListFile;

    private BlobFileService blobService;

    @Getter
    @Setter
    private Boolean sortFlag = Boolean.TRUE;

    @Getter
    @Setter
    private Boolean canBeEdit;

    @Getter
    @Setter
    private String attachmentType;


    public void bathDeleteAttach() {
        for (String blobId : selectedBlobIdList) {
            blobObjectSelectedMap.remove(blobId);
            removeAttachmentById(blobId);
        }
    }


    public void removeAttachmentById(String blobId) {
        removedAttachments.add(blobId);
        // blobService.deleteBlobFileById(blobId);
        attachements = attachements.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toList());
    }


    public boolean isChecked() {
        Map<String, Boolean> map = this.blobObjectSelectedMap;
        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            if (entry.getValue()) {
                return true;
            }
        }
        return false;
    }

    public void bathDownloadAttach() {
        String zipName = Optional.ofNullable(selected.getApplicationNumber()).orElse("") + selected.getAssetName() + ".zip";
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream(); ZipOutputStream zipOut = new ZipOutputStream(bout)) {
            Map<String, V2_BlobObject> blobObjectMap = attachements.stream().collect(Collectors.toMap(V2_BlobObject::getId, t -> t));
            for (String blobId : selectedBlobIdList) {
                V2_BlobObject blobObject = blobObjectMap.get(blobId);
                String name = FieldValueMessageController.fieldValue("HospitalAdjustmentAttachmentType", blobObject.getBoSubType()).replaceAll("/", "或") + "_" + blobObject.getObjectName();
                zipOut.putNextEntry(new ZipEntry(name));
                byte[] bytess = IOUtils.toByteArray(blobService.getBlobObjectByStorageId(blobObject.getObjectStorageId()).getStream());
                IOUtils.write(bytess, zipOut);
                zipOut.closeEntry();
            }
            zipOut.close();
            attachListFile = new DefaultStreamedContent(new ByteArrayInputStream(bout.toByteArray()), "application/zip", zipName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public List<V2_BlobObject> getAttachements() {
        if (com.baomidou.mybatisplus.toolkit.CollectionUtils.isEmpty(attachements)) return Lists.newArrayList();
        for (V2_BlobObject att : attachements) {
            att.setBoSubTypeValue(WebUtil.getFieldValueMessage("HospitalAdjustmentAttachmentType", att.getBoSubType()));
        }
        if (sortFlag) {
            return attachements.stream().sorted(Comparator.comparing(V2_BlobObject::getBoSubTypeValue)).collect(Collectors.toList());
        } else {
            return attachements.stream().sorted(Comparator.comparing(V2_BlobObject::getBoSubTypeValue).reversed()).collect(Collectors.toList());
        }
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = attachements.stream().noneMatch(item -> !blobObjectSelectedMap.containsKey(item.getId()) || !blobObjectSelectedMap.get(item.getId()));
        for (V2_BlobObject item : attachements) {
            blobObjectSelectedMap.put(item.getId(), !isSelectedAll);
        }
        selectedBlobIdList = new ArrayList<>();
        blobObjectSelectedMap.forEach((k, v) -> {
            if (v) {
                selectedBlobIdList.add(k);
            } else {
                selectedBlobIdList.remove(k);
            }
        });
    }

    public void removeUploadedFileObjects(String blobId) {
        blobService.deleteBlobFileById(blobId);
        uploadedFileObjects = uploadedFileObjects.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toList());
    }

    public void uploadDataFile(FileUploadEvent event) {
        uploadedFileObjects.add(blobService.uploadFile(event.getFile()));
    }

    public void finishUpload() {
        uploadedFileObjects.stream().forEach(item -> {
            item.setBoSubType(Strings.isNullOrEmpty(attachmentType) ? ContractInfo.AttachmentType.Other.toString() : attachmentType);
            item.setBoType(V2_BlobObject.BO_TYPE_HospitalAdjustmentContract);
        });
        attachements.addAll(uploadedFileObjects);
        uploadedFileObjects.clear();
    }

    public void attachSort() {
        sortFlag = !sortFlag;
    }

    public String getUserNameByUid(String orgUid) {
        UserAccount user = userDao.getByUid(orgUid);
        return user == null ? orgUid : user.getName();
    }

    public Boolean getCanBeEdit() {
        if (null == canBeEdit) {
//            canBeEdit = uaaService.getOrgListByUser(currentUser.getUid()).stream()
//                    .map(item -> item.getSiteUID()).distinct().anyMatch(item -> Objects.equals(item, selected.getTenantUID()) ||
//                            Objects.equals(item, selected.getInstitutionUID()) || Objects.equals(item, selected.getHospitalUID()) ||
//                            Objects.equals(item, selected.getSiteUID()));
        }
        return canBeEdit;
    }

    public void pick(String itemId) {
        selectedBlobIdList = new ArrayList<>();
        blobObjectSelectedMap.forEach((k, v) -> {
            if (v)
                selectedBlobIdList.add(k);
        });
    }


    @Getter
    private boolean newSignature = true;

    public void getSignature() {


        List<Map<String, String>> signatureList = masterDataService.getUserSignature();

        if (!CollectionUtils.isEmpty(signatureList)) {
            String lastSignatureId = signatureList.get(signatureList.size() - 1).get("signatureUid");
            this.signatureContentStream = blobService.getBlobObjectByStorageId(lastSignatureId);
            this.signatureId = lastSignatureId;
            this.newSignature = false;
        }else{
            this.newSignature = true;
        }
    }



    public void saveSignatureAndSubmitApprove() throws Exception {
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletResponse response = (HttpServletResponse) ctx.getResponse();

        if(StringUtils.isEmpty(this.signatureId) && (this.newSignature && StringUtils.isEmpty(this.signatureContent))){
            WebUtil.addErrorMessage("签名不可为空！");
            response.setStatus(206);
            return;
        }

        if(this.newSignature && StringUtils.isNotEmpty(signatureContent)){
            try {
                String base64 = signatureContent.substring(signatureContent.indexOf(",") + 1);
                BASE64Decoder decoder = new BASE64Decoder();
                byte[] decode = decoder.decodeBuffer(base64);

                V2_BlobObject blobObject = blobService.uploadFile(new ByteArrayInputStream(decode), "signature.png");
                if (null != blobObject) {
                    this.signatureId = blobObject.getObjectStorageId();
                    masterDataService.addUserSignature(this.signatureId);
                }else{
                    WebUtil.addErrorMessage("签名保存失败！");
                    response.setStatus(206);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                WebUtil.addErrorMessage("签名保存失败！");
                response.setStatus(206);
                return;
            }
        }

    }

    public void resetSignature() {
        this.signatureId = null;
        this.signatureContent = null;
        this.signatureContentStream = null;
        this.newSignature = true;
    }
    public void cancel() {

        IdleEquipmentApplication equipmentApplication = dao.findByUid(selected.getUid());
        if (!Objects.equals(equipmentApplication.getStatus(), 2)) {
            WebUtil.addErrorMessage("审批状态异常,不支持取消");
            return;
        }
        if(!equipmentApplication.getApplicationUserUid().equals(currentUserAccount.getUid())){
            WebUtil.addErrorMessage("申请人才能取消");
            return;
        }
        selected.setStatus(5);
        equipmentApplication.setStatus(5);
        dao.save(equipmentApplication);
        WebUtil.addSuccessMessage("取消成功");
    }
    public void approval() {

        IdleEquipmentApplication equipmentApplication = dao.findByUid(selected.getUid());
        if(equipmentApplication.getAuditResult()!=null){
            WebUtil.addErrorMessage("申请已经处理过了..请稍后查看详情！");
            return;
        }

        if(signatureId==null){
            WebUtil.addErrorMessage("签名不可为空！");
            return;
        }
        if(selected.getAuditResult()==null){
            WebUtil.addErrorMessage("审核结果必填！");
            return;
        }
        if(!Objects.equals(selected.getStatus(), 2)){
            WebUtil.addErrorMessage("审批状态不合法");
            return;
        }
        selected.setAuditSignature(signatureId);
        selected.setAuditUserUid(currentUserAccount.getUid());
        selected.setLastModifiedBy(currentUserAccount.getUid());
        selected.setLastModifiedDate(new Date());
        selected.setStatus(3);
        try {
            idleEquipmentApplicationService.idleEquipmentApproval(selected,currentUserAccount);
            WebUtil.addErrorMessage("审批完成");
        }catch (Exception e){
            logger.error("idle Equipment approval error :", e);
            WebUtil.addErrorMessage("审批异常，稍后重试");

        }

    }

    public void refuse(){
        IdleEquipmentApplication equipmentApplication = dao.findByUid(selected.getUid());
        if(equipmentApplication.getAuditResult()!=null){
            WebUtil.addErrorMessage("申请已经处理过了..请稍后查看详情！");
            return;
        }

        if(signatureId==null){
            WebUtil.addErrorMessage("签名不可为空！");
            return;
        }
        if(selected.getAuditResult()==null){
            WebUtil.addErrorMessage("审核结果必填！");
            return;
        }
        if(!Objects.equals(selected.getStatus(), 2)){
            WebUtil.addErrorMessage("审批状态不合法");
            return;
        }
        selected.setAuditSignature(signatureId);
        selected.setAuditUserUid(currentUserAccount.getUid());
        selected.setLastModifiedBy(currentUserAccount.getUid());
        selected.setLastModifiedDate(new Date());
        selected.setStatus(4);
        dao.save(selected);
        WebUtil.addErrorMessage("已退回");
    }

    public void download()   {
        try {
            byte[] download = idleEquipmentApplicationService.download(selected);
            exportFile= new DefaultStreamedContent(new ByteArrayInputStream(download), "application/msword", "闲置设备申请表.doc");

        }catch (Exception e){
            WebUtil.addErrorMessage("调剂申请表下载失败");
            logger.error("调剂申请表下载失败 error: ",e);
        }

    }


    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }


    public void approveAttachmentById(String blobId) {
        blobService.approveBlob(blobId, "1");
    }

    public Boolean canApprove(String blobId) {
        return getCanBeEdit() && !blobService.approved(blobId);
    }

    public UserAccount getCurrentUserAccount() {
        return currentUserAccount;
    }

    public void setCurrentUserAccount(UserAccount currentUserAccount) {
        this.currentUserAccount = currentUserAccount;
    }

    public AssetInfo getSelectedAsset() {
        return selectedAsset;
    }

    public void setSelectedAsset(AssetInfo selectedAsset) {
        this.selectedAsset = selectedAsset;
    }



}
