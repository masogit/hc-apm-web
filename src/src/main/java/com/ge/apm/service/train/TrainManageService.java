package com.ge.apm.service.train;


import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.OrgDto;
import com.ge.apm.domain.dto.TrainRoleDto;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.event.FileUploadEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.ge.apm.domain.V2_BlobObject.*;

@Service
public class TrainManageService extends MicroServiceInvoker {

    @Value("#{systemEnvironment['DOMAIN_URL'] ?: urlProperties.domainUrl}")
    private String baseURL;

    @Autowired
    private TrainInfoRepository trainInfoRepository;

    @Autowired
    private TrainDetailRepository trainDetailRepository;

    @Autowired
    private BlobObjectRepository blobObjectRepository;

    @Autowired
    private BlobFileService blobFileService;

    @Autowired
    private CustomRoleRepository customRoleRepository;

    @Autowired
    private SysRoleRepository sysRoleRepository;

    @Autowired
    private TrainDetailSignRepository trainDetailSignRepository;

    @Autowired
    TenantInfoRepository tenantInfoRepository;

    @Autowired
    private UaaService uaaService;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    AssetInfoRepository assetInfoDao;

    @Autowired
    AssetTypeGroupRepository assetTypeGroupRepository;

    @Autowired
    OrgInfoRepository orgInfoDao;

    public String getBaseURL(){
        return baseURL;
    }


    public List<OrgDto> onCreateInventory(){
        List<OrgDto> orgDtoList = new ArrayList<>();
        List<Map> maps = getOrgListForGrouping(UserContextService.getCurrentUserAccount());
        for(Map m : maps){
            OrgDto o = new OrgDto();
            o.setSelected(Boolean.parseBoolean(m.get("selected").toString()));
            o.setUid(m.get("uid").toString());
            o.setId(Integer.parseInt(m.get("id").toString()));
            o.setOrgName(m.get("orgName").toString());
            orgDtoList.add(o);
        }
        /*HttpEntity<JSONObject> requestEntity = new HttpEntity<>(createHttpHeaders(UserContextService.getAccessToken()));
        String url = getGatewayURL(GatewayURL.BEFORE_CREATE_INVENTORY_ORDER);
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.GET,requestEntity,LinkedHashMap.class);
        return JSONObject.parseObject(JSONObject.toJSONString(response.getBody().get("data")), new TypeReference<List<OrgDto>>() {});*/
        return orgDtoList;
    }

    public List<TrainRoleDto> queryTrainRole(UserAccount userAccount){
        TenantInfo ti = tenantInfoRepository.findByuid(userAccount.getTenantUID());
        List<TrainRoleDto> trainRoleDtoList = new ArrayList<>();
        List<SysRole> sysRoleList = sysRoleRepository.find();
        List<SysRole> newRoles = sysRoleList.stream()
                .filter(t -> {
                    if(UserContextService.isOnlyLocalITAdmin()){
                        if("ITAdmin".equals(t.getName())){
                            return false;
                        }
                    }
                    if(ti.getInsulate() == null || !ti.getInsulate()){
                        if("LocalITAdmin".equals(t.getName())){
                            return false;
                        }
                    }
                    //判断是否是智康宝用户，如果不是就移除掉智康宝的角色
                    if(t.getName().equals("zkbDealerSales") || t.getName().equals("zkbDealerFE")){
                        if(null != ti.getType() && ti.getType()!=3){
                            return false;
                        }
                    }
                    //如果是GE租户下放开产品超管和PM超管角色 别的租户看不到
                    if(!"GE".equals(ti.getName())){
                        if(t.getName().equals("ReviewSystemAdministrator")|| t.getName().equals("PMAdministrator")){
                            return false;
                        }
                    }
                    return true;
                }).collect(Collectors.toList());
        if (uaaService.isOpenNewMenuRoles(userAccount.getTenantUID())) {
            Set<String> tenantSysRoles = uaaService.getSysRoleListByTenantUid(userAccount.getTenantUID());
            Set<String> tenantCustomRoles = uaaService.getTenantCustomRoles(userAccount.getTenantUID()).stream().map(CustomRole::getName).collect(Collectors.toSet());
            newRoles = newRoles.stream().filter(role -> tenantSysRoles.contains(role.getName()) || tenantCustomRoles.contains(role.getName())).collect(Collectors.toList());
        }
        newRoles.stream().forEach(s -> {
            TrainRoleDto trainRoleDto = new TrainRoleDto();
            trainRoleDto.setId(s.getId().toString());
            trainRoleDto.setRoleName(s.getRoleDesc());
            trainRoleDtoList.add(trainRoleDto);
        });
        /*List<CustomRole> customRoles = customRoleRepository.findByTenantUID(userAccount.getTenantUID());
        .filter(customRole -> customRole.getFunctionList() != null)
        .filter(customRole -> customRole.getFunctionList().contains("TRAIN_MANAGE_VIEW"))*/
        return trainRoleDtoList;
    }

    public boolean saveCheck(TrainInfo trainInfo, List<TrainDetail> trainDetailList){
        boolean rs = true;
        if(CollectionUtils.isEmpty(trainDetailList)){
            //WebUtil.addErrorMessage("培训课程不能为空！");
        }

        return rs;
    }

    /**
     * 保存方法
     * @param trainInfo
    b * @param userAccount
     */
    @Transactional
    public void save(TrainInfo trainInfo, UserAccount userAccount, List<String> selectedOrgUid, List<String> selectedRoleId){
        if(StrUtil.isEmpty(trainInfo.getId())){
            trainInfo.setCreatedDate(new Date());
            trainInfo.setCreatedUid(userAccount.getUid());
            trainInfo.setCreatedBy(userAccount.getName());
            trainInfo.setCourseNo(generateCourseNo());
            trainInfo.setTenantUID(userAccount.getTenantUID());
            trainInfo.setInstitutionUID(userAccount.getInstitutionUID());
            trainInfo.setHospitalUID(userAccount.getHospitalUID());
            trainInfo.setOrgUid(userAccount.getOrgInfoUID());
            trainInfo.setSiteUID(userAccount.getSiteUID());
            trainInfo.setStatus(TrainDetail.Status.Planned.name());
        }
        if(CollectionUtils.isNotEmpty(selectedOrgUid)){
            trainInfo.setTrainingDeptUid(selectedOrgUid.stream().collect(Collectors.joining(",")));
            trainInfo.setTrainingDeptCount(selectedOrgUid.size());
        }
        if(CollectionUtils.isNotEmpty(selectedRoleId)){
            trainInfo.setTrainingRoleUid(selectedRoleId.stream().collect(Collectors.joining(",")));
        }
        trainInfo.setLastModifiedDate(new Date());
        trainInfo.setLastModifiedBy(userAccount.getName());
        trainInfoRepository.save(trainInfo);
        /*for(TrainDetail trainDetail : trainDetailList){
            if(StrUtil.isEmpty(trainDetail.getTrainId())){
                trainDetail.setTrainId(trainInfo.getId());
                trainDetail.setId(null);
                trainDetailRepository.save(trainDetail);
                for(V2_BlobObject blobObject : trainDetail.getBlobObjectList()){
                    blobObject.setBoUuid(trainDetail.getId());
                }
                blobObjectRepository.save(trainDetail.getBlobObjectList());
            }else{
                if(!TrainDetail.DataStatus.Original.name().equals(trainDetail.getDataStatus())){
                    //如果是新增的数据，把临时的id去除
                    if(TrainDetail.DataStatus.Create.name().equals(trainDetail.getDataStatus())){
                        trainDetail.setId(null);
                    }
                    trainDetail.setTrainId(trainInfo.getId());
                    trainDetailRepository.save(trainDetail);
                    for(V2_BlobObject blobObject : trainDetail.getBlobObjectList()){
                        if(StrUtil.isEmpty(blobObject.getBoUuid())){
                            blobObject.setBoUuid(trainDetail.getId());
                            blobObjectRepository.save(blobObject);
                        }
                    }
                }
            }
        }*/
    }

    @Transactional
    public List<TrainDetail> saveDetail(TrainDetail trainDetail,String detailActionName, List<V2_BlobObject> blobObjects, TrainInfo trainInfo, List<V2_BlobObject> resultBlobObjectList, List<TrainDetailSign> trainDetailSignList, List<TrainDetailSign> deleteSignList){
        if("Create".equals(detailActionName)){
            trainDetail.setCreatedDate(new Date());
        }else{
            trainDetail.setLastModifiedDate(new Date());
        }
        trainDetail.setStatus(TrainDetail.Status.Planned.name());
        trainDetailRepository.save(trainDetail);
        for(V2_BlobObject blobObject : blobObjects){
            if(StrUtil.isEmpty(blobObject.getBoUuid())){
                blobObject.setBoUuid(trainDetail.getId());
                blobObjectRepository.save(blobObject);
            }
        }
        for(V2_BlobObject blobObject : resultBlobObjectList){
            if(StrUtil.isEmpty(blobObject.getBoUuid())){
                blobObject.setBoUuid(trainDetail.getId());
                blobObjectRepository.save(blobObject);
            }
        }
        //签到信息
       if(CollectionUtils.isNotEmpty(trainDetailSignList)){
            for (TrainDetailSign trainDetailSign : trainDetailSignList){
                // 检查 TrainDetailSign 对象的 id 是否为空
                if(StrUtil.isEmpty(trainDetailSign.getDetailId())){
                    trainDetailSign.setId(null);
                    trainDetailSign.setSignOrder(null);
                    trainDetailSign.setDetailId(trainDetail.getId());
                    initAndSaveTrainDetailSign(trainDetailSign);
                }
            }
        }
        if(CollectionUtils.isNotEmpty(deleteSignList)){
            for(TrainDetailSign trainDetailSign : deleteSignList){
                trainDetailSignRepository.delete(trainDetailSign);
            }
        }
        List<TrainDetail> trainDetailList = trainDetailRepository.findByTrainId(trainDetail.getTrainId());
        if("Create".equals(detailActionName)){
            trainInfo.setStatus(TrainDetail.Status.Planned.name());//已计划
            trainInfo.setTrainQty(trainDetailList.size());
            trainInfoRepository.save(trainInfo);
        }
        return trainDetailList;
    }

    public List<TrainDetail> completeDetail(TrainDetail trainDetail,String detailActionName, List<V2_BlobObject> blobObjects, TrainInfo trainInfo, UserAccount userAccount, List<V2_BlobObject> resultBlobObjectList, List<TrainDetailSign> trainDetailSignList){
        if("Create".equals(detailActionName)){
            trainDetail.setCreatedDate(new Date());
        }else{
            trainDetail.setLastModifiedDate(new Date());
        }
        trainDetail.setStatus(TrainDetail.Status.Completed.name());
        trainDetail.setLastModifiedDate(new Date());
        trainDetailRepository.save(trainDetail);
        for(V2_BlobObject blobObject : blobObjects){
            if(StrUtil.isEmpty(blobObject.getBoUuid())){
                blobObject.setBoUuid(trainDetail.getId());
                blobObjectRepository.save(blobObjects);
            }
        }
        for(V2_BlobObject blobObject : resultBlobObjectList){
            if(StrUtil.isEmpty(blobObject.getBoUuid())){
                blobObject.setBoUuid(trainDetail.getId());
                blobObjectRepository.save(blobObjects);
            }
        }
        //签到信息
        if(CollectionUtils.isNotEmpty(trainDetailSignList)){
            for (TrainDetailSign trainDetailSign : trainDetailSignList){
                // 检查 TrainDetailSign 对象的 id 是否为空
                if(StrUtil.isEmpty(trainDetailSign.getDetailId())){
                    trainDetailSign.setId(null);
                    trainDetailSign.setDetailId(trainDetail.getId());
                    initAndSaveTrainDetailSign(trainDetailSign);
                }
            }
        }
        List<TrainDetail> trainDetailList = trainDetailRepository.findByTrainId(trainDetail.getTrainId());
        boolean allComplete = true;
        for(TrainDetail td : trainDetailList){
            if (!TrainDetail.Status.Completed.name().equals(td.getStatus())) {
                allComplete = false;
            }
        }
        if(allComplete){
            trainInfo.setStatus(TrainDetail.Status.Completed.name());
            trainInfo.setLastModifiedDate(new Date());
            trainInfo.setLastModifiedBy(userAccount.getName());
            trainInfoRepository.save(trainInfo);
        }
        return trainDetailList;
    }

    @Transactional
    public void cancelDetail(TrainDetail trainDetail, TrainInfo trainInfo, UserAccount userAccount){
        trainDetail.setStatus(TrainDetail.Status.Canceled.name());
        trainDetail.setLastModifiedDate(new Date());
        trainDetailRepository.save(trainDetail);

        List<TrainDetail> trainDetailList = trainDetailRepository.findByTrainId(trainDetail.getTrainId());
        boolean allCancel = true;
        for(TrainDetail td : trainDetailList){
            if (!TrainDetail.Status.Canceled.name().equals(td.getStatus())) {
                allCancel = false;
            }
        }
        if(allCancel){
            trainInfo.setStatus(TrainDetail.Status.Canceled.name());
            trainInfo.setLastModifiedDate(new Date());
            trainInfo.setLastModifiedBy(userAccount.getName());
            trainInfoRepository.save(trainInfo);
        }
    }

    public void deleteItem(TrainDetail trainDetail,  TrainInfo trainInfo){
        trainDetailRepository.delete(trainDetail);
        int count = trainInfo.getTrainQty() == null ? 0 : trainInfo.getTrainQty();
        trainInfo.setTrainQty(count - 1);
        trainInfoRepository.save(trainInfo);
    }

    public String generateCourseNo(){
        StringBuilder sb = new StringBuilder();
        String pre = DateUtil.format(new Date(), "yyyyMMdd");
        sb.append(pre);
        Object object = trainInfoRepository.queryMaxCourseNo(pre+"%");
        if(object == null){
            sb.append("001");
        }else{
            String current = object.toString().replace(pre, "");
            if(StrUtil.isNotEmpty(current)){
                int currentIndex = Integer.parseInt(current);
                currentIndex++;
                sb.append(String.format("%03d",currentIndex));
            }else{
                sb.append("001");
            }
        }

        return sb.toString();
    }

    public List<V2_BlobObject> queryBlobByDetailId(String detailId){
        return blobObjectRepository.fetchBlobObjectByUuidAndType(detailId, BO_TYPE_TRAIN_DETAIL);
    }

    public List<V2_BlobObject> queryResultBlobByDetailId(String detailId){
        return blobObjectRepository.fetchBlobObjectByUuidAndType(detailId, BO_TYPE_TRAIN_SIGN_FILE);
    }

    public void removeBlob(V2_BlobObject v2BlobObject){
        blobFileService.deleteBlobFile(v2BlobObject);
    }

    public V2_BlobObject handleFileUpload(FileUploadEvent event, String boType, String subBoType){
        V2_BlobObject v2_blobObject = blobFileService.uploadFile(event);
        if (null != v2_blobObject) {
            v2_blobObject.setBoType(boType);
            //v2_blobObject.setBoSubType(V2_BlobObject.BO_TYPE_TRAIN_DETAIL);
            v2_blobObject.setBoSubType(boType);
            v2_blobObject.setBoSubType(subBoType);
            v2_blobObject.setBoUuid(null);
            WebUtil.addSuccessMessage(v2_blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
        return v2_blobObject;
    }

    public void saveModifyFileName(V2_BlobObject v2_blobObject){
        blobFileService.saveBlobObject(v2_blobObject);
    }

    public List<TrainDetail> queryDetailByTrainId(String trainId){
        List<TrainDetail> trainDetailList = trainDetailRepository.findByTrainId(trainId);
        for(TrainDetail td :trainDetailList){
            List<V2_BlobObject> v2_blobObjectList = blobObjectRepository.findByBoUuid(td.getId());
            td.setBlobObjectList(v2_blobObjectList);
        }
        return trainDetailList;
    }

    public Integer queryUserCountByRoles(List<String> roles, List<String> deptUid){
        if(CollectionUtils.isEmpty(roles)){
            return 0;
        }
        List<Integer> ids = roles.stream().map(t -> Integer.parseInt(t)).collect(Collectors.toList());
        List<UserAccount> userAccountList = userAccountRepository.findBySiteUidAndRoleNames(ids, deptUid);
        return userAccountList.size();
    }

    public void saveSign(TrainDetailSign trainDetailSign){
        TrainDetailSign existSign = trainDetailSignRepository.findFirstByDetailIdAndSignerName(trainDetailSign.getDetailId(), trainDetailSign.getSignerName());
        if(existSign != null){
            return;
        }
       initAndSaveTrainDetailSign(trainDetailSign);
    }

    /**
     * 初始化并保存培训详情签到信息
     * @param trainDetailSign 需要初始化和保存的培训详情签到对象
     */
    private void initAndSaveTrainDetailSign(TrainDetailSign trainDetailSign) {

        // 保存 TrainDetailSign 对象到数据库
        trainDetailSignRepository.save(trainDetailSign);
    }


    public void deleteSign(TrainDetailSign trainDetailSign){
        trainDetailSignRepository.delete(trainDetailSign);
    }

    public List<TrainDetailSign> queryDetailSignByDetailId(String detailId){
        List<TrainDetailSign> trainDetailSignList = trainDetailSignRepository.findByDetailId(detailId);

        for(TrainDetailSign s : trainDetailSignList){
            if(StrUtil.isEmpty(s.getSignDeptName())){
                OrgInfo orgInfo = orgInfoDao.getByUid(s.getSignDeptUid());
                if(orgInfo != null){
                    s.setSignDeptName(orgInfo.getName());
                }
            }
        }

        if(CollectionUtils.isNotEmpty(trainDetailSignList)){
            for(int i = 0; i < trainDetailSignList.size(); i++){
                TrainDetailSign trainDetailSign = trainDetailSignList.get(i);
                trainDetailSign.setSignOrder(i+1);
            }
        }
        return trainDetailSignList;
    }

    public List<OrgInfo> queryOrgToSign(String orgUidStr){
        if(StrUtil.isEmpty(orgUidStr)){
            return new ArrayList<>();
        }
        List<OrgInfo> orgInfoList = new ArrayList<>();
        Arrays.asList(orgUidStr.split(",")).stream().forEach(t -> {
            OrgInfo orgInfo = orgInfoDao.getByUid(t);
            orgInfoList.add(orgInfo);
        });

        return orgInfoList;
    }


    public List getOrgListForGrouping(UserAccount user) {
        List<Map> userClinicalDeptList = findClinicalDeptMapByUser(user);
        List<String> clinicalDeptUIDs = ListUtil.newArrayList("-1");
        clinicalDeptUIDs.addAll(userClinicalDeptList.stream().map(t -> (String) t.get("uid")).collect(Collectors.toList()));
        if (user.isEnableAssetTypeGroup()) {
            List<AssetInfo> assetInfoList = Optional.
                    ofNullable(assetInfoDao.findAssetTypeToInventory(user.getTenantUID(), clinicalDeptUIDs, user.getAssetTypeGroupMap().keySet())).
                    map(assetInfoDao::findByUidIn).orElse(ListUtil.empty());
            Map<String, List<AssetInfo>> assetMap = assetInfoList.stream().collect(Collectors.groupingBy(AssetInfo::getClinicalDeptUID));
            List<AssetTypeGroup> assetTypeGroups = assetTypeGroupRepository.listParentAssetTypeGroupByEncodeds(user.getTenantUID(), user.getAssetTypeGroupMap().keySet());
            Map<String, String> assetTypeMap = assetTypeGroups.stream().collect(Collectors.toMap(AssetTypeGroup::getEncoded, AssetTypeGroup::getName));
            List<Map> userAssetTypeClinicalDeptList = ListUtil.newArrayList();
            for (Map userClinicalDept : userClinicalDeptList) {
                List<AssetInfo> clinicalAssets = assetMap.get((String) userClinicalDept.get("uid"));
                Set<String> assetTypeParent = clinicalAssets.stream().map(asset -> asset.getAssetType().split("-")[0]).collect(Collectors.toSet());
                for (String assetType : assetTypeParent) {
                    Map userAssetTypeClinicalDept = MapUtil.newHashMap();
                    userAssetTypeClinicalDept.putAll(userClinicalDept);
                    userAssetTypeClinicalDept.put("assetType", assetType);
                    userAssetTypeClinicalDept.put("orgName", assetTypeMap.get(assetType) + " / " + userClinicalDept.get("orgName"));
                    userAssetTypeClinicalDeptList.add(userAssetTypeClinicalDept);
                }
            }
            userClinicalDeptList = userAssetTypeClinicalDeptList;
        }
        userClinicalDeptList.sort(Comparator.comparing(o -> {
            try {
                return PinyinHelper.convertToPinyinString(String.valueOf(o.get("orgName")), "", PinyinFormat.WITHOUT_TONE);
            } catch (PinyinException e) {
                return String.valueOf(o.get("orgName"));
            }
        }));
        return userClinicalDeptList;
    }

    private List<Map> findClinicalDeptMapByUser(UserAccount user) {
        return findUserDeptScope(user).stream().map(orgInfo -> {
            Map map = new HashMap();
            map.put("id", orgInfo.getId());
            map.put("uid", orgInfo.getUid());
            map.put("orgName", orgInfo.getParentOrg() == null ? orgInfo.getName() : orgInfo.getParentOrg().getName() + " / " + orgInfo.getName());
            map.put("selected", true);
            return map;
        }).collect(Collectors.toList());
    }

    private List<OrgInfo> findUserDeptScope(UserAccount user) {
        List<String> orgUIDList = ListUtil.newArrayList("-1");
        if (user.isEnableAssetTypeGroup()) {
            if (ObjectUtil.isNotEmpty(user.getAssetTypeGroupList())) {
                orgUIDList.addAll(orgInfoDao.getUserDeptScopeByAssetType(user.getUid(), user.getAssetTypeGroupMap().keySet()));
            }
        } else {
            orgUIDList.addAll(orgInfoDao.getUserDeptScope(user.getUid()));
        }
        return orgInfoDao.findBySpecification((Root<OrgInfo> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(root.get("uid").in(orgUIDList));
            predicates.add(builder.equal(root.get("orgType"), 4));
            return builder.and(predicates.toArray(new Predicate[0]));
        });
    }


   public void generateQrCode(TrainDetail trainDetail){
       if(trainDetail.getQrCodeGenerateDate() != null){
           Date oldDate = trainDetail.getQrCodeGenerateDate();
           oldDate = DateUtil.offsetMinute(oldDate, 30);
           if(oldDate.compareTo(new Date()) > 0){
               return;
           }
       }

       trainDetail.setQrCodeGenerateDate(new Date());
       StringBuilder sb = new StringBuilder();
       sb.append("id=").append(trainDetail.getId());
       trainDetail.setQrCodeStr(sb.toString());
       trainDetailRepository.save(trainDetail);
   }
}
