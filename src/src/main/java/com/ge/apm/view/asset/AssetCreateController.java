/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import com.ge.apm.dao.BrandRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetCreateService;
import com.ge.apm.service.asset.AttachmentFileService;
import com.ge.apm.service.utils.TimeUtils;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class AssetCreateController {

    private AssetInfo assetInfo;
    private QrCodeLib createRequest;
    private AssetCreateService acServie;
    private AttachmentFileService attachFileService;
    private BrandRepository brandDao;

    private String[] selectedPictures;

    private UserAccount owner;
    private Brand brand;
    private String rejectText;
    private List<String> rejectTextHistory;
    private List<String> commentsList;

    List<V2_BlobObject> pictureList;
    List<V2_BlobObject> audioList;

    private UserAccount clinicalOwner;

    @PostConstruct
    protected void init() {
        String qrCode = WebUtil.getRequestParameter("qrCode");
        acServie = WebUtil.getBean(AssetCreateService.class);
        attachFileService = WebUtil.getBean(AttachmentFileService.class);
        brandDao = WebUtil.getBean(BrandRepository.class);
        createRequest = acServie.getCreateRequest(qrCode);

        assetInfo = createAssetInfo();

        pictureList = acServie.getQrCodePictureList(createRequest.getUid());
        audioList = acServie.getQrCodeAudioList(createRequest.getUid());

        //默认图片全选
        selectedPictures = new String[pictureList.size()];
        for (int i = 0; i < pictureList.size(); i++) {
            selectedPictures[i] = pictureList.get(i).getObjectStorageId();
        }

        rejectTextHistory = new ArrayList();
        if (null != createRequest.getFeedback()) {
            Collections.addAll(rejectTextHistory, createRequest.getFeedback().split("//"));
        }
        commentsList = new ArrayList();
        if (null != createRequest.getComment()) {
            Collections.addAll(commentsList, createRequest.getComment().split("\\|"));
        }

    }

    public void onBrandChange() {
        if (null != brand) {
            assetInfo.setBrand(brandDao.findById(brand.getId()));
        }
    }

    private AssetInfo createAssetInfo() {
        AssetInfo newAsset = new AssetInfo();
        newAsset.setSiteId(createRequest.getSiteId());
        newAsset.setHospitalId(createRequest.getHospitalId());
        newAsset.setQrCode(createRequest.getQrCode());
        newAsset.setIsValid(true);
        newAsset.setStatus(1);
        newAsset.setDeviceStatus(0);
        newAsset.setIsDeleted(Boolean.FALSE);
        newAsset.setName(createRequest.getAssetName());
        newAsset.setAssetGroup(createRequest.getAssetGroup());
        if(null!=createRequest.getOrgId()){
            OrgInfo org = acServie.getOrgInfo(createRequest.getOrgId());
            newAsset.setClinicalDeptId(createRequest.getOrgId());
            newAsset.setClinicalDeptUID(org.getUid());
        }
        newAsset.setClinicalOwnerId(createRequest.getUserId());

        newAsset.setTenantUID(createRequest.getTenantUID());
        newAsset.setHospitalUID(createRequest.getHospitalUID());
        newAsset.setInstitutionUID(createRequest.getInstitutionUID());
        newAsset.setSiteUID(createRequest.getSiteUID());

        if (null != createRequest.getUserId()) {
            clinicalOwner = acServie.getUserInfo(createRequest.getUserId());
            newAsset.setClinicalOwnerName(clinicalOwner.getName());
            newAsset.setClinicalOwnerTel(clinicalOwner.getTelephone());
        }
        return newAsset;
    }

    public List<OrgInfo> getClinicalDeptList() {
        return acServie.getClinicalDeptList(assetInfo.getHospitalId());
    }

    public String getHospitalName() {
        return acServie.getHospitalName(assetInfo.getHospitalId());
    }

    @Transactional
    public String applyChange() {
        if (owner != null) {
            assetInfo.setAssetOwnerId(owner.getId());
            assetInfo.setAssetOwnerName(owner.getName());
            assetInfo.setAssetOwnerTel(owner.getTelephone());
        }
        if (null != assetInfo.getClinicalDeptId()) {
            OrgInfo clinicalDept = acServie.getOrgInfo(assetInfo.getClinicalDeptId());
            assetInfo.setClinicalDeptName(clinicalDept.getName());
            assetInfo.setClinicalDeptUID(clinicalDept.getUid());
        }
        Boolean res = acServie.CreateAeest(assetInfo);
        if (res) {
            acServie.createAttachments(assetInfo, selectedPictures);
            acServie.updateQrLib(assetInfo.getQrCode(), 3);
            WebUtil.addSuccessMessage("创建成功");
            return "RequestsList?faces-redirect=true";
        } else {
            WebUtil.addErrorMessage("创建失败");
            return "";
        }
    }

    public String getAudioBase64(Integer fileId) {
        String audio = attachFileService.getBase64File(fileId);
        return audio;
    }

    public void returnBack() {
        acServie.rejectReturn(createRequest, rejectText);
        this.rejectTextHistory.add(TimeUtils.getStrDate(new Date(), "[yyyy-MM-dd HH:mm:ss]").concat(rejectText));
        rejectText = "";
        WebUtil.addSuccessMessage("消息已发送");
    }

    public List<UserAccount> getOwnerList() {
        List<UserAccount> list =  acServie.getAssetOwnerList(Optional.ofNullable(assetInfo.getClinicalDeptUID()).orElse(assetInfo.getSiteUID()));
        return list;
    }

    public void onClinicalDeptChange() {
        if(null!=assetInfo.getClinicalDeptId()){
            OrgInfo clinicalDept = acServie.getOrgInfo(assetInfo.getClinicalDeptId());
            assetInfo.setClinicalDeptName(clinicalDept.getName());
            assetInfo.setClinicalDeptUID(clinicalDept.getUid());
        }
        this.owner = null;
        this.clinicalOwner = null;
        assetInfo.setClinicalOwnerId(null);
        assetInfo.setClinicalOwnerName(null);
        assetInfo.setClinicalOwnerTel(null);
    }

    public void onClinicalOwnerChange() {
        if (null != clinicalOwner) {
            assetInfo.setClinicalOwnerId(clinicalOwner.getId());
            assetInfo.setClinicalOwnerName(clinicalOwner.getName());
            assetInfo.setClinicalOwnerTel(clinicalOwner.getTelephone());
        }
    }

    public List<UserAccount> getClinicalOwnerList() {

        return acServie.getClinicalOwnerList(assetInfo.getClinicalDeptUID());

    }

    public List<Brand> getBrandList() {
        return acServie.getBrandList();
    }

    //getter and setter
    public QrCodeLib getCreateRequest() {
        return createRequest;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public String[] getSelectedPictures() {
        return selectedPictures;
    }

    public void setSelectedPictures(String[] selectedPictures) {
        this.selectedPictures = selectedPictures;
    }

    public UserAccount getOwner() {
        return owner;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public void setOwner(UserAccount owner) {
        this.owner = owner;
    }

    public List<V2_BlobObject> getPictureList() {
        return pictureList;
    }

    public List<V2_BlobObject> getAudioList() {
        return audioList;
    }

    public String getRejectText() {
        return rejectText;
    }

    public void setRejectText(String rejectText) {
        this.rejectText = rejectText;
    }

    public List<String> getRejectTextHistory() {
        return rejectTextHistory;
    }

    public List<String> getCommentsList() {
        return commentsList;
    }

    public UserAccount getClinicalOwner() {
        return clinicalOwner;
    }

    public void setClinicalOwner(UserAccount clinicalOwner) {
        this.clinicalOwner = clinicalOwner;
    }

}
