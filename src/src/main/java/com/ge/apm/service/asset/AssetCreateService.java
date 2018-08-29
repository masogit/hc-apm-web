/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.common.WechatSendMsgService;
import com.ge.apm.service.pm.V2_PmOrderService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.TimeUtils;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author 212579464
 */
@Component
public class AssetCreateService {

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
    UserCommonService ucService;
    @Autowired
    WechatSendMsgService wsService;
    @Autowired
    WorkflowConfigRepository wcDao;

    @Autowired
    BlobObjectRepository blobDao;
    @Autowired
    private UaaService uuaService;
    
    @Autowired
    private AssetDepreciationService assetDepreciationService;
    @Autowired
    private V2_PmOrderService pmService;

    public String getTenantName(String uid) {
        TenantInfo si = tenantDao.findByuid(uid);
        if (si != null) {
            return si.getName();
        } else {
            return uid.toString();
        }
    }

    public String getSiteName(String siteUid) {
        OrgInfo org = orgDao.getByUid(siteUid);
        return getOrgFullName(org);
    }

    private String getOrgFullName(OrgInfo org) {
        if (null == org || org.getId() == -1) {
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

    public QrCodeLib getCreateRequest(String qrCode,String siteUid) {
        return qrLibDao.findByQrCode(qrCode,siteUid);
    }

    public List<OrgInfo> getClinicalDeptList(String siteUid) {
        List<SearchFilter> OrgInfoFilters = new ArrayList<>();
        OrgInfoFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, siteUid));
        OrgInfoFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 4));
        return orgDao.findBySearchFilter(OrgInfoFilters);
    }

    public List<OrgInfo> getClinicalDeptList(Integer hospitalId) {
        List<SearchFilter> OrgInfoFilters = new ArrayList<>();
        OrgInfoFilters.add(new SearchFilter("parentOrg.id", SearchFilter.Operator.EQ, hospitalId));
        OrgInfoFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 4));
        return orgDao.findBySearchFilter(OrgInfoFilters);
    }

    public Boolean createAsset(AssetInfo assetInfo, Boolean isGeneratePmOrder) {
        boolean isNew = assetInfo.getId()==null;
        if(null == assetDao.save(assetInfo)){
            return false;
        }
        assetDepreciationService.saveAssetDerpeciation(assetInfo);
//        if(isNew && isGeneratePmOrder && !Strings.isNullOrEmpty(assetInfo.getClinicalDeptUID())){
//            pmService.createPMrequest(assetInfo.getUid(), null);
//        }
        return true;
    }

    public List<UserAccount> getAssetOnwers(int siteId, Integer hospitalId) {
        //get all users in this hospital and has 'MultiHospital' users in this site 
        List<UserAccount> users = userDao.getAssetOnwers(siteId, hospitalId);
        return users;
    }

    public void createAttachments(AssetInfo assetInfo, String[] selectedPictures) {
        List<V2_BlobObject> attachList = new ArrayList();
        for (String item : selectedPictures) {
            V2_BlobObject blobObject = blobDao.findAllByObjectStorageId(item).stream().findFirst().get();
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

    public void updateQrLib(String qrCode, int i,String siteUid) {
        QrCodeLib qrLibItem = qrLibDao.findByQrCode(qrCode,siteUid);
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

        String openId = request.getSubmitWechatId();
        Map<String, Object> map = new HashMap();
        map.put("_openId", openId);
        map.put("first", "建档失败");
        map.put("keyword1", TimeUtils.getStrDate(request.getSubmitDate(), "yyyy-MM-dd HH:mm:ss") + "提交建档".concat(request.getQrCode()));
        map.put("keyword2", rejectText);
        map.put("linkUrl", "/wx/scanQrCode?qrCode=".concat(request.getQrCode()));
        if (wsService.sendCreateAssetRejectMsg(map)) {
            rejectText = TimeUtils.getStrDate(new Date(), "[yyyy-MM-dd HH:mm:ss]").concat(rejectText);
            request.setFeedback(request.getFeedback() == null ? rejectText : request.getFeedback().concat("//").concat(rejectText));
            qrLibDao.save(request);
            return true;
        }else{
            return false;
        }
    }

    public OrgInfo getOrgInfo(Integer clinicalDeptId) {
        return orgDao.findById(clinicalDeptId);
    }

    public List<OrgInfo> getOrgListByHospital(Integer hospitalId) {
        return orgDao.getByHospitalId(hospitalId);
    }

    public UserAccount getUserInfo(Integer userId) {
        return userDao.getById(userId);
    }

    public List<UserAccount> getWorkerList() {
        return ucService.getWorkerList(0,"");
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
            if (clinicalOwner != null) {
                ownerList.add(clinicalOwner);
            }
            return ownerList;
        }
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
    
    public Boolean needAssetApproval(String siteUid){
        OrgInfo siteOrg = orgDao.getByUid(siteUid);
        List<String> siteUidList = Arrays.asList(siteOrg.getTenantUID(),siteOrg.getInstitutionUID(),siteOrg.getHospitalUID(),siteOrg.getSiteUID());
        List<WorkflowConfig> wcList = wcDao.getBySiteUIDList(siteUidList.stream().distinct().collect(Collectors.toList()));
        return wcList.stream().anyMatch(item-> !Strings.isNullOrEmpty(item.getAssetApprover()));
    }
    
    public Boolean needShowAssetApprovalByUser(String userUid){
        UserAccount user = userDao.getByUid(userUid);
        List<String> siteUidList = uuaService.getUserAccessibleSiteList(userUid).stream().filter(item->item.getOrgType()<4).map(OrgInfo::getUid).distinct().collect(Collectors.toList());
        siteUidList.addAll(Arrays.asList(user.getTenantUID(),user.getInstitutionUID(),user.getHospitalUID(),user.getSiteUID()));
        List<WorkflowConfig> wcList = wcDao.getBySiteUIDList(siteUidList.stream().distinct().collect(Collectors.toList()));
        return wcList.stream().anyMatch(item-> !Strings.isNullOrEmpty(item.getAssetApprover()));
    }
    
    public UserAccount getUserByWechatId(String wechatId){
        return userDao.getByWeChatId(wechatId);
    }

}
