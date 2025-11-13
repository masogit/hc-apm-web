package com.ge.apm.view;

import com.ge.apm.dao.AssetProbationRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetProbation;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetProbationEditController extends GenericCRUDUUIDController<AssetProbation> {
    private AssetProbationRepository dao;
    private UaaService uaaService;
    private BlobFileService blobService;
    private OrgInfoRepository orgInfoRepository;

    private UserAccount loginUser;
    @Getter @Setter
    private OrgInfo assetDept;
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
    private List<UserAccount> detectionUserList;
    @Getter @Setter
    private List<UserAccount> probationConfirmorList;
    @Getter @Setter
    private List<V2_BlobObject> attachments;
    @Getter @Setter
    private Integer action;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetProbationRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        loginUser = UserContextService.getCurrentUserAccount();

        assetDeptList = uaaService.getClinicalDeptListBySiteUID(loginUser.getSiteUID());
        String encodeStr = WebUtil.getRequestParameter("str");
        action = Integer.parseInt(UrlEncryptController.getValueFromMap(encodeStr, "action").toString());
        if (-1 == action) {
            try {
                prepareCreate();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            prepareEdit();
            String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedId");
            this.selected = dao.findById(selectedId);
            detectionUserList = uaaService.getUsersByDeptUidAndRole(this.selected.getAssetDeptUid(), "AssetStaff");
            probationConfirmorList = uaaService.getUsersByDeptUidAndRole(this.selected.getAssetDeptUid(), "DeptHead");
            this.assetDept = orgInfoRepository.getByUid(this.selected.getAssetDeptUid());
            this.assetDept = orgInfoRepository.getByUid(this.selected.getAssetDeptUid());
            this.specificationList = Arrays.asList(this.selected.getSpecification().split(","));
            this.informationList = Arrays.asList(this.selected.getInformation().split(","));
        }
    }

    @Override
    protected GenericRepositoryUUID<AssetProbation> getDAO() {
        return dao;
    }

    public void tempSave(boolean flag) {
        Date now = DateUtil.now();
        if (this.selected.getId() == null) {
            this.selected.setStatus(0);
            this.selected.setApplicantUserUid(loginUser.getUid());
            this.selected.setApplicantUserName(loginUser.getName());
            this.selected.setApplicationTime(now);
            this.selected.setTenantUid(loginUser.getTenantUID());
            this.selected.setInstitutionUid(loginUser.getInstitutionUID());
            this.selected.setHospitalUid(loginUser.getHospitalUID());
            this.selected.setSiteUid(loginUser.getSiteUID());
            this.selected.setCreatedBy(loginUser.getName());
            this.selected.setCreatedDate(now);
        }
        if (this.selected.getStatus() == 0) {
            this.selected.setSpecification(String.join(",", specificationList));
            this.selected.setInformation(String.join(",", informationList));
            this.selected.setAssetDeptUid(assetDept.getUid());
            this.selected.setAssetDeptName(assetDept.getName());
        } else if(this.selected.getStatus() == 1) {
            this.selected.setDetectionTime(now);
            this.selected.setProbationStartUserUid(probationConfirmor.getUid());
            this.selected.setProbationStartUserName(probationConfirmor.getName());
        }
        this.selected.setLastModifiedBy(loginUser.getName());
        this.selected.setLastModifiedDate(now);
        if (flag) {
            dao.save(this.selected);
            WebUtil.addSuccessMessage(WebUtil.getMessage("tempSave") + WebUtil.getMessage("Success"));
        }
    }

    public String submit() {
        tempSave(false);
        if (this.selected.getStatus() == 0) {
            this.selected.setStatus(1);
            this.selected.setDetectionUserUid(detectionUser.getUid());
            this.selected.setDetectionUserName(detectionUser.getName());
        } else if(this.selected.getStatus() == 1) {
            this.selected.setStatus(2);
        }
        dao.save(this.selected);
        WebUtil.addSuccessMessage(WebUtil.getMessage("Submit") + WebUtil.getMessage("Success"));
        return "externalAssetList?faces-redirect=true";
    }

    public List<OrgInfo> autoCompleteDept(String query) {
        if (assetDeptList == null) {
            return Collections.emptyList();
        }

        return this.assetDeptList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() != null && item.getNamePyCode().contains(query.toLowerCase()))).sorted(new PinYinComparatorByName()).collect(Collectors.toList());
    }

    public void onAssetDeptChange(String idx) {
        detectionUserList = uaaService.getUsersByDeptUidAndRole(this.assetDept.getUid(), "AssetStaff");
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


    public List<V2_BlobObject> getAttachList(String subType) {
        if (attachments == null) {
            attachments = blobService.findBlobObjectsByBoUid(this.selected.getId());
        }
        return attachments.stream().filter(item -> item.getBoSubType() != null && item.getBoSubType().equals(subType)).collect(Collectors.toList());
    }

    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        attachments = attachments.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toList());
    }

    public void handleFileUpload(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            String type = event.getComponent().getId();
            attachments.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_SUB_TYPE_ASSET_PROBATION);
            blobObject.setBoSubType(type);
            blobObject.setBoUuid(selected.getId());
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }
}
