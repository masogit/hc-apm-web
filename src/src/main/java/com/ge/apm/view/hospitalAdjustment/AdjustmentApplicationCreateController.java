package com.ge.apm.view.hospitalAdjustment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.constant.AssetConstant;
import com.ge.apm.service.uaa.UaaService;

import com.ge.apm.service.remote.WorkFlowService;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.OrderNumberKeys;
import com.ge.healthcare.cn.common.utils.date.DatePattern;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@ManagedBean
@ViewScoped
public class AdjustmentApplicationCreateController extends GenericCRUDUUIDController<IdleEquipmentApplication>  implements Serializable, ServerEventInterface {
    private IdleEquipmentApplicationRepository dao;

    private UserAccount currentUserAccount;


    private AssetInfoRepository assetInfoRepository;
    private AssetInfo selectedAsset;

    private UaaService uaaService;

    private UserAccountRepository userDao;

    private OrgInfoRepository orgInfoRepository;

    @Getter
    @Setter
    private List<String> selectedBlobIdList;
    @Getter
    @Setter
    private List<I18nMessage> contractAttachmentTypeList;

    private WorkFlowService workFlowService;
    @Getter
    @Setter
    private List<V2_BlobObject> uploadedFileObjects = new ArrayList();
    @Getter
    @Setter
    private List<String> attachments=new ArrayList<>();
    @Override
    protected GenericRepositoryUUID<IdleEquipmentApplication> getDAO() {
        return dao;
    }


    @Override
    protected void init() {
        dao = WebUtil.getBean(IdleEquipmentApplicationRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        workFlowService = WebUtil.getBean(WorkFlowService.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        currentUserAccount = UserContextService.getCurrentUserAccount();
        String encodeStr = WebUtil.getRequestParameter("str");
        String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "actionName");
        if ("Create".equalsIgnoreCase(actionName)) {
            try {
                prepareCreate();
            }catch (Exception e){
                logger.error("医院院间调剂申请异常 error: ",e);
            }
            attachements=new ArrayList<>();
            attachments.addAll(Arrays.asList("ContractInvoice","InspectionReport","PhysicalPhoto"));
            selected.setApplicationDate(new Date());

            OrgInfo orgInfo = orgInfoRepository.findByUid(currentUserAccount.getSiteUID());
            selected.setCompanyName(orgInfo.getName());
            selected.setContactNumber(currentUserAccount.getTelephone());
            selected.setContactPerson(currentUserAccount.getName());
        } else if ("Edit".equalsIgnoreCase(actionName)) {
            String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
            setSelected(dao.findByUid(selectedId));
            String assetUid = selected.getAssetUid();
            if(assetUid!=null){
                selectedAsset = assetInfoRepository.findByUid(assetUid);
            }
            attachements = blobService.findBlobObjectsByBoUid(selected.getUid());
            if(selected.getAttachments()!=null){
                attachments= JSONObject.parseArray(selected.getAttachments(),String.class);

            }
        }else if("Copy".equalsIgnoreCase(actionName)){
            String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
            setSelected(dao.findByUid(selectedId));
            String assetUid = selected.getAssetUid();
            if(assetUid!=null){
                selectedAsset = assetInfoRepository.findByUid(assetUid);
            }

            attachements = blobService.findBlobObjectsByBoUid(selected.getUid());
            if(selected.getAttachments()!=null){
                attachments= JSONObject.parseArray(selected.getAttachments(),String.class);

            }
            selected.setUid(null);
            for (V2_BlobObject attachement : attachements) {
                attachement.setBoUuid(null);
            }
            selected.setStatus(null);
            selected.setAuditUserUid(null);
            selected.setAuditDate(null);
            selected.setAuditSignature(null);
            selected.setAuditResult(null);
            selected.setAuditResultReason(null);
            selected.setApplicationDate(new Date());
            selected.setContactNumber(currentUserAccount.getTelephone());
            selected.setContactPerson(currentUserAccount.getName());
        }

        if (selectedAsset == null) {
            selectedAsset = new AssetInfo();
        }

        blobObjectSelectedMap = new HashMap();
    }


    public void saveApplication(){

        selected.setAssetUid(selectedAsset.getUid());
        selected.setAssetName(selectedAsset.getName());
        selected.setStockHospitalUid(selectedAsset.getHospitalUID());
        selected.setStockSiteUid(selectedAsset.getSiteUID());
        selected.setStockDeptUid(selectedAsset.getClinicalDeptUID());
        selected.setApplicationUserUid(currentUserAccount.getUid());
        selected.setCreatedDate(new Date());
        selected.setLastModifiedBy(currentUserAccount.getUid());
        selected.setLastModifiedDate(new Date());
        selected.setTenantUid(currentUserAccount.getTenantUID());
        selected.setAttachments(JSON.toJSONString(attachments));
        if(CollectionUtils.isEmpty(attachments) || !attachments.contains("other")){
            selected.setAttachmentsOtherDescription(null);
        }
        if(selected.getStatus()==null){
            selected.setStatus(1);
        }
        dao.save(selected);

        if(CollectionUtils.isNotEmpty(attachements)  && attachements.stream().anyMatch(ite->ite.getBoUuid()==null) ){
            attachements.stream().forEach(item -> {
                item.setBoUuid(selected.getUid());
            });
            blobService.saveBlobObject(attachements);
        }
        if(CollectionUtils.isNotEmpty(removedAttachments)){
            removedAttachments.stream().forEach(item -> {
                blobService.deleteBlobFileById(item);
            });
        }

    }

    public boolean ckeck(){
        if(selectedAsset==null || selectedAsset.getId()==null){
            WebUtil.addWarnMessage("请选择设备");
            return  false;
        }
        List<IdleEquipmentApplication> byAssetUidAndStatusIn = dao.findByAssetUidAndStatusIn(selectedAsset.getUid(), Arrays.asList(1, 2, 3));
        if(CollectionUtils.isNotEmpty(byAssetUidAndStatusIn)){
            boolean b = byAssetUidAndStatusIn.stream().anyMatch(i -> Objects.equals(i.getStatus(), 3));
            if(b){
                WebUtil.addWarnMessage("该设备已经审批通过入库");
                return  false;
            }
            boolean b1 = byAssetUidAndStatusIn.stream().anyMatch(i -> i.getUid().equals(selected.getUid()));
            if(!b1){
                WebUtil.addWarnMessage("该设备已经提交申请或者已暂存");
                return  false;
            }
        }
        return true;
    }
    public String  temporarily (){
        if(!ckeck()){
            return "";
        }
        selected.setStatus(1);
        WebUtil.addWarnMessage("暂存完成");
        saveApplication();
        return "/portal/hospitalAdjustment/IdleEquipmentApplication?faces-redirect=true";

    }

    public String  submit (){
        if(!ckeck()){
            return "";
        }

        if(CollectionUtils.isNotEmpty(attachments)){
            for (String attachment : attachments) {
                boolean b = attachements.stream().anyMatch(ite -> ite.getBoSubType().equals(attachment));
                if(!b){
                    WebUtil.addWarnMessage("附件材料清单："+WebUtil.getFieldValueMessage("HospitalAdjustmentAttachmentType", attachment)+"没上传");
                    return "";
                }
            }
        }
        selected.setStatus(2);
        String hospitalAdjustmentApp = workFlowService.getOrderNumber("HOSPITAL_ADJUSTMENT_APP"+currentUserAccount.getTenantUID());
        if(hospitalAdjustmentApp==null){
             hospitalAdjustmentApp = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN);
        }
        selected.setApplicationNumber(OrderNumberKeys.IN.name()+hospitalAdjustmentApp);
        WebUtil.addWarnMessage("申请完成");
        saveApplication();
        return "/portal/hospitalAdjustment/IdleEquipmentApplication?faces-redirect=true";

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

        selectedAsset=asset;
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
        String zipName = Optional.ofNullable(selected.getApplicationNumber()).orElse("")  +"闲置设备申请表附件"+ ".zip";
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
        if(com.baomidou.mybatisplus.toolkit.CollectionUtils.isEmpty(attachements)) return Lists.newArrayList();
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
        if(StringUtils.isBlank(attachmentType)){
            WebUtil.addErrorMessage("请选择附件类型");
            return;
        }
        uploadedFileObjects.stream().forEach(item -> {
            item.setBoSubType(attachmentType);
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


    public List<I18nMessage>  attachmentsList(){
        List<I18nMessage> contractAttachmentType = FieldValueMessageController.getFieldValueList("HospitalAdjustmentAttachmentType");
        List<I18nMessage>     contractAttachmentTypeList = contractAttachmentType.stream().sorted(Comparator.comparingInt(I18nMessage::getId)).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(attachments)){
            return new ArrayList<>();
        }
        return contractAttachmentTypeList.stream().filter(ite -> attachments.contains(ite.getMsgKey())).collect(Collectors.toList());

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
