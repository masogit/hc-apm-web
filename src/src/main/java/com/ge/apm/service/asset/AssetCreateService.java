/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.utils.ConfigUtils;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.service.wechat.CoreService;
import com.ge.apm.view.sysutil.UserContextService;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 212579464
 */
@Component
public class AssetCreateService {

    @Autowired
    protected WxMpService wxMpService;
    @Autowired
    private TenantInfoRepository tenantDao;
    @Autowired
    protected BlobFileService blobService;
    @Autowired
    private OrgInfoRepository orgDao;
    @Autowired
    private QrCodeLibRepository qrLibDao;
    @Autowired
    private AssetInfoRepository assetDao;
    @Autowired
    private UserAccountRepository userDao;
    @Autowired
    AssetFileAttachmentRepository attachDao;
    @Autowired
    QrCodeAttachmentRepository qrCodeAttachDao;
    @Autowired
    BrandRepository brandDao;
    @Autowired
    CoreService coreService;
    @Autowired
    ConfigUtils configUtils;
    @Autowired
    UserCommonService ucService;

    @Autowired
    BlobObjectRepository blobDao;

//    AttachmentFileService fileService = WebUtil.getBean(AttachmentFileService.class);
    public String getTenantName(Integer siteId) {
        TenantInfo si = tenantDao.findById(siteId);
        if (si != null) {
            return si.getName();
        } else {
            return siteId.toString();
        }
    }

    public String getSiteName(String siteUid){
        OrgInfo org = orgDao.getByUid(siteUid);
        return getOrgFullName(org);
    }
    
    private String getOrgFullName(OrgInfo org) {
        if(null==org || org.getId()==-1){
            return null;
        }
        String name = org.getName();
        while (org.getParentOrg() != null) {
            org = org.getParentOrg();
            name = org.getName() + "/" + name;
        }

        return name;
    }
            
    public String getHospitalName(Integer hospitalId) {
        OrgInfo org = orgDao.findById(hospitalId);
        if (org != null) {
            return org.getName();
        } else {
            return hospitalId.toString();
        }
    }

    public QrCodeLib getCreateRequest(String qrCode) {
        return qrLibDao.findByQrCode(qrCode);
    }

    public List<OrgInfo> getClinicalDeptList(Integer hospitalId) {
        List<SearchFilter> OrgInfoFilters = new ArrayList<>();
        OrgInfoFilters.add(new SearchFilter("hospitalId", SearchFilter.Operator.EQ, hospitalId));
        OrgInfoFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 4));
        return orgDao.findBySearchFilter(OrgInfoFilters);
    }

    public Boolean CreateAeest(AssetInfo assetInfo) {
        return (null != assetDao.save(assetInfo));
    }

    public List<UserAccount> getAssetOnwers(int siteId, Integer hospitalId) {
        //get all users in this hospital and has 'MultiHospital' users in this site 
        List<UserAccount> users = userDao.getAssetOnwers(siteId, hospitalId);
        return users;
    }

    public void createAttachments(AssetInfo assetInfo, String[] selectedPictures) {
        List<V2_BlobObject> attachList = new ArrayList();
        for (String item : selectedPictures) {
            V2_BlobObject blobObject = blobDao.findByObjectStorageId(item);
            blobObject.setBoType(V2_BlobObject.BO_TYPE_AssetInfo);
            blobObject.setBoSubType("1");
            blobObject.setBoUuid(assetInfo.getUid());
            blobObject.setBoId(assetInfo.getId());
            attachList.add(blobObject);
        }
        blobDao.save(attachList);
    }

    public List<V2_BlobObject> getQrCodePictureList(String qrCodeLibUid) {
        List<SearchFilter> qrCodeFilters = new ArrayList<>();
        qrCodeFilters.add(new SearchFilter("boUuid", SearchFilter.Operator.EQ, qrCodeLibUid));
        qrCodeFilters.add(new SearchFilter("boSubType", SearchFilter.Operator.EQ, "1"));
        List<V2_BlobObject> picsList = blobDao.findBySearchFilter(qrCodeFilters);
        return picsList;
    }

    public void updateQrLib(String qrCode, int i) {
        QrCodeLib qrLibItem = qrLibDao.findByQrCode(qrCode);
        qrLibItem.setStatus(i);
        qrLibDao.save(qrLibItem);
    }

    public List<V2_BlobObject> getQrCodeAudioList(String qrCodeLibUid) {
        List<SearchFilter> qrCodeFilters = new ArrayList<>();
        qrCodeFilters.add(new SearchFilter("boUuid", SearchFilter.Operator.EQ, qrCodeLibUid));
        qrCodeFilters.add(new SearchFilter("boSubType", SearchFilter.Operator.EQ, "2"));
        List<V2_BlobObject> audioList = blobDao.findBySearchFilter(qrCodeFilters);
        return audioList;
    }

    public boolean rejectReturn(QrCodeLib request, String rejectText) {
        String _openId = request.getSubmitWechatId();
        String _templateId = configUtils.fetchProperties("asset_build_template_id");
        Map<String, Object> map = new HashMap();
        map.put("first", "建档失败");
//        map.put("_qrCode", request.getQrCode());
//        map.put("_requestTime", TimeUtils.getStrDate(request.getSubmitDate(), "yyyy-MM-dd HH:mm:ss"));
//        map.put("_detail", rejectText);
        map.put("keyword1", rejectText);
        map.put("keyword2", TimeUtils.getStrDate(request.getSubmitDate(), "yyyy-MM-dd HH:mm:ss"));
        map.put("linkUrl", "wechat/asset/createInfoDetail.xhtml?qrCode=".concat(request.getQrCode()));

        coreService.sendWxTemplateMessage(_openId, _templateId, map);

        rejectText = TimeUtils.getStrDate(new Date(), "[yyyy-MM-dd HH:mm:ss]").concat(rejectText);

        request.setFeedback(request.getFeedback() == null ? rejectText : request.getFeedback().concat("//").concat(rejectText));
        qrLibDao.save(request);
        return true;
    }

    public OrgInfo getOrgInfo(Integer clinicalDeptId) {
        return orgDao.findById(clinicalDeptId);
    }

    public List<OrgInfo> getOrgListByHospital(Integer hospitalId) {
        return orgDao.getByHospitalId(hospitalId);
    }

//    public String pushVoiceToWechat(Integer fileId) throws Exception {
//        StreamedContent sc = fileService.getFile(fileId);
//        return coreService.uploadMediaToWechat(sc.getStream());
//    }
//    public String pushImageToWechat(Integer fileId) throws Exception {
//        StreamedContent sc = fileService.getFile(fileId);
//        return coreService.uploadImageToWechat(sc.getStream());
//    }
    public UserAccount getUserInfo(Integer userId) {
        return userDao.getById(userId);
    }
    
    public List<UserAccount> getWorkerList(){
        return ucService.getWorkerList(0);
    }

    public List<UserAccount> getAssetOwnerList(String deptUid) {
        OrgInfo dept = orgDao.getByUid(deptUid);
        List<String> deptUids = Arrays.asList(deptUid, dept.getTenantUID(), dept.getInstitutionUID(), dept.getHospitalUID(), dept.getSiteUID());
        return userDao.findByAssetAccess(deptUids);
    }

    public List<UserAccount> getClinicalOwnerList(String clinicalDeptUid, UserAccount clinicalOwner) {
        List<SearchFilter> usersFilters = new ArrayList<>();
        if (null != clinicalDeptUid) {
            usersFilters.add(new SearchFilter("orgInfoUID", SearchFilter.Operator.EQ, clinicalDeptUid));
            return userDao.findBySearchFilter(usersFilters);
        } else {
            List<UserAccount> ownerList = new ArrayList();
            if(clinicalOwner != null) {
                ownerList.add(clinicalOwner);
            }
            return ownerList;
        }
    }

    @Transactional
    public void removeAttachment(Integer id, String fileUrl) {
        List<SearchFilter> attachFilters = new ArrayList<>();
        attachFilters.add(new SearchFilter("assetId", SearchFilter.Operator.EQ, id));
        attachFilters.add(new SearchFilter("fileUrl", SearchFilter.Operator.EQ, fileUrl));
        List<AssetFileAttachment> attList = attachDao.findBySearchFilter(attachFilters);
        attList.forEach((item) -> {
//            fileService.deleteAttachment(item.getFileId());
            String openId = UserContextService.getCurrentUserAccount().getWeChatId();
            if (blobService.deleteBlobFileByStorageId(openId, fileUrl)) {
                attachDao.delete(item);
            }
        });
    }

    public void addWechatPicAttachment(AssetInfo assetInfo, String serverId) {
        File file = null;
        try {
            file = wxMpService.getMaterialService().mediaDownload(serverId);
        } catch (WxErrorException ex) {
            Logger.getLogger(AssetCreateService.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (file == null) {
            return;
        }
//        Integer uploadFileId = fileService.uploadFile(file);
        String openId = UserContextService.getCurrentUserAccount().getWeChatId();
        V2_BlobObject blobObject = blobService.uploadWxFile(openId, file);
        if (null != blobObject) {
            blobObject.setBoId(assetInfo.getId());
            blobObject.setBoSubType("1");
            blobObject.setBoType(V2_BlobObject.BO_TYPE_AssetInfo);
            blobObject.setBoUuid(assetInfo.getUid());
            blobDao.save(blobObject);

            AssetFileAttachment attach = new AssetFileAttachment();
            attach.setAssetId(assetInfo.getId());
            attach.setFileUrl(blobObject.getObjectStorageId());
            attach.setFileType("1");
            attach.setHospitalId(assetInfo.getHospitalId());
            attach.setSiteId(assetInfo.getSiteId());
            attach.setName(blobObject.getObjectName());
            attachDao.save(attach);
        }
        file.delete();
    }

    public String getOrgName(String orgUid) {
        if (orgUid != null) {
            OrgInfo org = orgDao.getByUid(orgUid);
            return org == null ? null : org.getName();
        }
        return null;
    }

    public String getOrgName(Integer orgId) {
        if (orgId != null) {
            OrgInfo org = orgDao.findById(orgId);
            return org == null ? null : org.getName();
        }
        return null;
    }

    public List<Brand> getBrandList() {
        return brandDao.find();
    }

}
