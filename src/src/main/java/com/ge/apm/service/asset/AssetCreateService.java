/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.common.WechatSendMsgService;
import com.ge.apm.service.pm.V2_PmOrderService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ApmConstant;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.ApmFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
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
    CustomRoleRepository customeRoleDao;
    @Autowired
    UserRoleRepository userRoleDao;
    @Autowired
    I18nMessageRepository i18nDao;
    @Autowired
    private ConfigCacheService configCacheService;
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
    ConfigService configService;
    
    @Autowired
    private AssetDepreciationService assetDepreciationService;
    @Autowired
    private V2_PmOrderService pmService;

    public String getTenantName(String uid) {
        TenantInfo si = configCacheService.getTenantByUid(uid);
        if (si != null) {
            return si.getName();
        } else {
            return uid.toString();
        }
    }

    public String getSiteName(String siteUid) {
        OrgInfo org = configCacheService.findOrgByUid(siteUid);
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
        if (qrLibItem != null){
            qrLibItem.setStatus(i);
            qrLibDao.save(qrLibItem);
        }
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
        OrgInfo dept = configCacheService.findOrgByUid(deptUid);
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
            OrgInfo org = configCacheService.findOrgByUid(orgUid);
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
        OrgInfo siteOrg = configCacheService.findOrgByUid(siteUid);
        List<String> siteUidList = Arrays.asList(siteOrg.getTenantUID(),siteOrg.getInstitutionUID(),siteOrg.getHospitalUID(),siteOrg.getSiteUID());
        List<CustomRole> customRoles = customeRoleDao.findCustomRolesBySiteUIdsAndFunction(siteUidList, ApmFunction.ASSET_AUDIT_APPROVE.name());
        //List<WorkflowConfig> wcList = wcDao.getBySiteUIDList(siteUidList.stream().distinct().collect(Collectors.toList()));
        if (!CollectionUtils.isEmpty(customRoles)) {
            List<UserAccount> userAccounts = userDao.findCustomRoleUser(customRoles.stream().map(CustomRole::getId).collect(Collectors.toList()));
            return !CollectionUtils.isEmpty(userAccounts);
        }
        return false;
    }

    public List<UserAccount> getAssetApprovalUser(String siteUid){
        OrgInfo siteOrg = configCacheService.findOrgByUid(siteUid);
        List<String> siteUidList = Arrays.asList(siteOrg.getTenantUID(),siteOrg.getInstitutionUID(),siteOrg.getHospitalUID(),siteOrg.getSiteUID());
        List<CustomRole> customRoles = customeRoleDao.findCustomRolesBySiteUIdsAndFunction(siteUidList, ApmFunction.ASSET_AUDIT_APPROVE.name());
        //List<WorkflowConfig> wcList = wcDao.getBySiteUIDList(siteUidList.stream().distinct().collect(Collectors.toList()));
        if (!CollectionUtils.isEmpty(customRoles)) {
            List<UserAccount> userAccounts = userDao.findCustomRoleUser(customRoles.stream().map(CustomRole::getId).collect(Collectors.toList()));
            return userAccounts;
        }
        return new ArrayList<>();
    }
    
    public Boolean needShowAssetApprovalByUser(String userUid){
        UserAccount user = userDao.getByUid(userUid);
        List<String> orgList = new ArrayList<String>();
        orgList.add(user.getTenantUID());
        orgList.add(user.getInstitutionUID());
        orgList.add(user.getHospitalUID());
        orgList.add(user.getSiteUID());
//        List<WorkflowConfig> wcList =getWWorkflowConfig(orgList,user);
//        return wcList.stream().anyMatch(item-> !Strings.isNullOrEmpty(item.getAssetApprover()));
        List<CustomRole> customRoles = customeRoleDao.findCustomRolesBySiteUIdsAndFunction(orgList, ApmFunction.ASSET_AUDIT_APPROVE.name());
        //List<WorkflowConfig> wcList = wcDao.getBySiteUIDList(siteUidList.stream().distinct().collect(Collectors.toList()));
        if (!CollectionUtils.isEmpty(customRoles)) {
            List<UserAccount> userAccounts = userDao.findCustomRoleUser(customRoles.stream().map(CustomRole::getId).collect(Collectors.toList()));
            return !CollectionUtils.isEmpty(userAccounts);
        }
        return false;
    }
    private List<WorkflowConfig> getWWorkflowConfig(List<String> orgList,UserAccount user){
        List<WorkflowConfig> bySiteUIDList = wcDao.getBySiteUIDList(orgList);
        if(!CollectionUtils.isEmpty(bySiteUIDList)){
            return bySiteUIDList;
        }
        List<OrgInfo> byUser = orgDao.getByUser(user.getUid());
        if(CollectionUtils.isEmpty(byUser)){
            return new ArrayList<>();
        }
        List<String> collect = byUser.stream().filter(item -> item != null && !Objects.equals(4,item.getOrgType())).map(OrgInfo::getUid).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(collect)){
            return wcDao.findWorkflowConfig(collect);
        }
        List<String> collectType4 = byUser.stream().filter(item -> item != null && Objects.equals(4,item.getOrgType())).map(OrgInfo::getUid).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(collectType4)){
            return  wcDao.findWorkflowConfig(collectType4);
        }
        return new ArrayList<>();
    }
    public UserAccount getUserByWechatId(String wechatId){
        return userDao.getByWeChatId(wechatId);
    }

    //判断待报废按钮是否禁用false
    public boolean hasScrappedAuthority() {
        if(configService.needScrappedControl()){
            List<UserRole> userRoleList = userRoleDao.findByUserIdAndRoleType(UserContextService.getCurrentUserAccount().getId(), ApmConstant.CUSTOME_ROLE_TYPE);
            for (UserRole userRole : userRoleList) {
                String webAuthorities = customeRoleDao.findById(userRole.getRoleId()).getWebAuthorities();
                if(null != webAuthorities){
                    JSONArray authJSONArr= JSONArray.parseArray(webAuthorities);
                    for (int i = 0; i < authJSONArr.size(); i++) {
                        JSONObject ob = authJSONArr.getJSONObject(i);
                        if("nurseSiteAssetScrapped".equals(ob.get("k")) && (Boolean) ob.get("a"))
                            return true;
                    }
                }
            }
            return false;
        }else{
            return true;
        }
        // anyMatch(userRole -> customeRoleDao.findById(userRole.getRoleId()).getWebAuthorities().contains(""))
    }

    public List<I18nMessage> getAssetStatusList(){
        List<I18nMessage> assetStatusList = i18nDao.getByMsgTypeAndSiteId("assetStatus", ApmConstant.DEFAULT_SITE_ID);
        if(assetStatusList.size() >0 ){
            Iterator<I18nMessage> ite = assetStatusList.iterator();
            while(ite.hasNext()){
                I18nMessage next = ite.next();
                if( next.getMsgKey().equals("4") && !hasScrappedAuthority()){
                    ite.remove();
                }
            }
        }
        return assetStatusList;
    }
}
