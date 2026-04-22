package com.ge.apm.view.hospitalAdjustment;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.hospitalAdjustment.IdleEquipmentStockService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.remote.MasterDataService;
import com.ge.apm.service.remote.WorkFlowService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.view.uaa.OrgTreeController;
import com.ge.apm.view.wo.WorkOrderMgrController;
import com.ge.healthcare.cn.common.cons.ApmSystemRole;
import com.ge.healthcare.cn.common.cons.OrderNumberKeys;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.date.DatePattern;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionListener;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@ManagedBean
@ViewScoped
public class IdleEquipmentStockDetailController extends GenericCRUDUUIDController<IdleEquipmentStock> {

    protected static final Logger log = LoggerFactory.getLogger(WorkOrderMgrController.class);

    private IdleEquipmentStockRepository dao;
    private AssetInfoRepository assetInfoRepository;
    private OrgInfoRepository orgInfoRepository;

    private UserAccountRepository userDao;

    private IdleEquipmentApplication idleEquipmentApplication;


    private UserAccount currentUserAccount;

    private AdjustmentApplicationDetailController adjustmentApplicationDetailController;

    private AdjustmentTransferApplicationRepository adjustmentTransferApplicationRepository;

    @Getter
    @Setter
    private AdjustmentTransferApplication application;
    @Getter
    @Setter
    private AssetInfo assetInfo;

    @Getter @Setter
    private OrgInfo filterDeptOrg;


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
    private WorkFlowService workFlowService;

    private IdleEquipmentStockService idleEquipmentStockService;

    @Getter
    @Setter
    private List<String> attachments=new ArrayList<>();
    @Override
    protected void init() {
        dao = WebUtil.getBean(IdleEquipmentStockRepository.class);
        adjustmentApplicationDetailController = WebUtil.getBean(AdjustmentApplicationDetailController.class);
        adjustmentTransferApplicationRepository = WebUtil.getBean(AdjustmentTransferApplicationRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        workFlowService = WebUtil.getBean(WorkFlowService.class);
        currentUserAccount = UserContextService.getCurrentUserAccount();
        userDao = WebUtil.getBean(UserAccountRepository.class);
        idleEquipmentStockService = WebUtil.getBean(IdleEquipmentStockService.class);

        blobService = WebUtil.getBean(BlobFileService.class);
        blobObjectSelectedMap = new HashMap();
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
        assetInfo = assetInfoRepository.findByUid(assetUid);
        attachements=new ArrayList<>();


    }

    public void initStock(){
        adjustmentApplicationDetailController.initDteail(selected.getApplicationUid());
    }


    @Override
    protected GenericRepositoryUUID<IdleEquipmentStock> getDAO() {
        return dao;
    }

    public List<OrgInfo> autoCompleteDept(String query) {

        List<OrgInfo>  clinicalDeptList = orgInfoRepository.getUserAccessibleClinicalDeptsBySelectedSite(currentUserAccount.getOrgInfoUID(), UserContextService.getCurrentUserAccount().getUid());

        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }
        List<OrgInfo> res = clinicalDeptList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? Boolean.FALSE : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }
    private void loadClinicalDept(){


    }

    public void applyForBorrowing() {
        IdleEquipmentStock idleEquipmentStock = dao.findByUid(selected.getUid());
        Integer status = idleEquipmentStock.getStatus();
        if(1!=status){
            WebUtil.addWarnMessage("调剂设备不可借用,状态异常");
            return;
        }
        if(filterDeptOrg==null){
            WebUtil.addWarnMessage("借用科室不能为空");
            return;
        }
        selected.setBorrowDeptUid(filterDeptOrg.getUid());
        application.setAttachments(JSON.toJSONString(attachments));
        if(CollectionUtils.isEmpty(attachments) || !attachments.contains("other")){
            application.setAttachmentsOtherDescription(null);
        }
        try {
            idleEquipmentStockService.applyForBorrowing(selected,currentUserAccount,application);



            if(CollectionUtils.isNotEmpty(attachements)  && attachements.stream().anyMatch(ite->ite.getBoUuid()==null) ){
                attachements.stream().forEach(item -> {
                    item.setBoUuid(application.getUid());
                });
                blobService.saveBlobObject(attachements);
            }
            if(CollectionUtils.isNotEmpty(removedAttachments)){
                removedAttachments.stream().forEach(item -> {
                    blobService.deleteBlobFileById(item);
                });
            }
            WebUtil.addSuccessMessage("调剂设备借用申请完成");
        }catch (Exception e){
            log.error("调剂设备借用申请异常 e:",e);
            WebUtil.addSuccessMessage("调剂设备借用申请异常");

        }


    }

    public Boolean isBorrowing() {
        if(selected!=null && currentUserAccount!=null){
            return selected.getStockSiteUid().equals(currentUserAccount.getSiteUID());

        }

        return false;
    }

    public void initBorrowing() {
        application=new AdjustmentTransferApplication();

        IdleEquipmentStock idleEquipmentStock = dao.findByUid(selected.getUid());
        if(idleEquipmentStock.getStatus()!=1){
            WebUtil.addWarnMessage("设备在借用中或不可借");
            return;
        }

        OrgInfo orgInfo = orgInfoRepository.findByUid(currentUserAccount.getSiteUID());
        application.setUnitName(orgInfo.getName());
        application.setContactPerson(currentUserAccount.getName());
        application.setContactPhone(currentUserAccount.getTelephone());
        application.setApplicationDate(new Date());
        application.setSpecificDeviceName(selected.getAssetInfo().getName());
    }

    @Getter
    @Setter
    private List<String> selectedBlobIdList;
    @Getter
    @Setter
    private List<I18nMessage> contractAttachmentTypeList;
    @Getter
    @Setter
    private List<V2_BlobObject> uploadedFileObjects = new ArrayList();

    @Getter
    @Setter
    private Boolean sortFlag = Boolean.TRUE;

    @Getter
    @Setter
    private String attachmentType;

    public boolean isChecked() {
        Map<String, Boolean> map = this.blobObjectSelectedMap;
        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            if (entry.getValue()) {
                return true;
            }
        }
        return false;
    }
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

    public void pick(String itemId) {
        selectedBlobIdList = new ArrayList<>();
        blobObjectSelectedMap.forEach((k, v) -> {
            if (v)
                selectedBlobIdList.add(k);
        });
    }
    public void bathDownloadAttach() {
        String zipName = Optional.ofNullable(selected.getApplicationNumber()).orElse("") + "调剂借用申请表附件" + ".zip";
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream(); ZipOutputStream zipOut = new ZipOutputStream(bout)) {
            Map<String, V2_BlobObject> blobObjectMap = attachements.stream().collect(Collectors.toMap(V2_BlobObject::getId, t -> t));
            for (String blobId : selectedBlobIdList) {
                V2_BlobObject blobObject = blobObjectMap.get(blobId);
                String name = FieldValueMessageController.fieldValue("HospitalAdjustmentBorrowAttachmentType", blobObject.getBoSubType()).replaceAll("/", "或") + "_" + blobObject.getObjectName();
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
            att.setBoSubTypeValue(WebUtil.getFieldValueMessage("HospitalAdjustmentBorrowAttachmentType", att.getBoSubType()));
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
    public void attachSort() {
        sortFlag = !sortFlag;
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

    public void cancelUpload() {
        uploadedFileObjects.stream().forEach(item -> {
            blobService.deleteBlobFile(item);
        });
        uploadedFileObjects.clear();
    }
    public String getUserNameByUid(String orgUid) {
        UserAccount user = userDao.getByUid(orgUid);
        return user == null ? orgUid : user.getName();
    }

    public List<I18nMessage>  attachmentsList(){
        List<I18nMessage> contractAttachmentType = FieldValueMessageController.getFieldValueList("HospitalAdjustmentBorrowAttachmentType");
        List<I18nMessage>   contractAttachmentTypeList = contractAttachmentType.stream().sorted(Comparator.comparingInt(I18nMessage::getId)).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(attachments)){
            return new ArrayList<>();
        }
        return contractAttachmentTypeList.stream().filter(ite -> attachments.contains(ite.getMsgKey())).collect(Collectors.toList());

    }
}
