package com.ge.apm.service.uaa;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.UserTagsMapper;
import com.ge.apm.domain.*;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.service.utils.ExcelParser;
import com.ge.apm.service.utils.PasswordUtil;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.util.CollectionUtils;
import webapp.framework.web.WebUtil;

@Service
public class UserAccountImportService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    protected Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();
    public List<I18nMessage> userPositionList = FieldValueMessageController.getFieldValueList("userPosition", siteId);

    private static final String HEADER_INSTITUTION = "所属集团";
    private static final String HEADER_HOSPITAL = "所属医院";
    private static final String HEADER_SITE = "所属院区";
    private static final String HEADER_DEPARTMENT = "科室1";
    private static final String HEADER_DEPARTMENT2 = "科室2";
    private static final String HEADER_DEPARTMENT3 = "科室3";
    private static final String HEADER_LOGINNAME = "登录用户名*";
    private static final String HEADER_NAME = "姓名*";
    private static final String HEADER_EMAIL = "Email";
    private static final String HEADER_TELEPHONE = "电话";
    private static final String HEADER_ISACTIVE = "激活?*";
    private static final String HEADER_ISLOCKED = "锁定?*";
    private static final String HEADER_ISDISABLE = "停用?*";
    private static final String HEADER_ROLENAME = "角色*";
    private static final String HEADER_ACCESSSCOPE = "可见范围（科室）";
    private static final String HEADER_POSITION = "职务";
    private static final String HEADER_TAGS = "标签";
    private static final String HEADER_PASSWORD = getEncryptedPass();
    private static final String HEADER_UUID = "UUID";
    private static final String HEADER_ASSET_CLASSIFICATION="资产分类";

    private static final String ROLE_GUEST = "仅报修权限账号";
    private static Map<String, SysRole> sysRoleMap;
    private static Map<String, CustomRole> customRoleMap;
    private Map<String, OrgInfo> mapOrgInfo = new HashMap<>();
    private Map<String, OrgInfo> mapUidOrgInfo = new HashMap<>();
    private Map<String, TenantInfo> mapUidTenantInfo = new HashMap<>();
    private OrgInfo orgInfoTemp;
    private int successCount;
    private int failedCount;
    boolean isInsulate = false;
    boolean isCreate = true;
    UserAccount currentUser;
    private List<OrgInfo> affiliatedDepartments;
    private List<String> affiliatedDepartmentsUid;

    @Autowired
    UserAccountMapper userAccountMapper;

    @Autowired
    UserAccountRepository userAccountRepository;

    @Autowired
    AssetInfoMapper assetInfoMapper;

    @Autowired
    TenantInfoRepository tenantInfoRepository;

    @Autowired
    OrgInfoRepository orgInfoRepository;

    @Autowired
    UserOrgInfoRepository userOrgDao;

    @Autowired
    UserOrgInfoRepository userOrgInfoRepository;

    @Autowired
    SysRoleRepository sysRoleRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    UserTagsMapper userTagsMapper;

    @Autowired
    UserTagsRepository userTagsDao;

    @Autowired
    UserAccountHistoryService userAccountHistoryService;

    @Autowired
    AssetTypeGroupRepository assetTypeGroupDao;

    @Autowired
    UserAssetGroupRepository userAssetGroupDao;

    @Autowired
    CustomRoleRepository customRoleRepository;

    EmailValidator emailValidator = EmailValidator.getInstance();

    private static String getEncryptedPass() {
        return "初始密码";
    }

    private boolean isExistsByLoginName(String loginName) {
        return userAccountMapper.getUserByLoginName(loginName) != null ? true : false;
    }

    private boolean checkData(UserAccount newUser) {
        return !StringUtils.isNotEmpty(newUser.getStatus());
    }

    private boolean handleNewUser(UserAccount newUser, String institution, String hospital, String site, String department, String department2, String department3, TenantInfo tenantInfo,String assetClassification) throws NoSuchAlgorithmException {
        //get all user
        checkNewUser(newUser, tenantInfo);
        if (!supplementUserRole(newUser)) {
            return false;
        }
        checkAssetClassification(newUser,assetClassification);


        if (checkData(newUser)) {
            setUIDs(newUser, institution, hospital, site, department, department2, department3);
            supplementAccessScope(newUser);
            if (checkData(newUser)) {
                createUser(newUser);
                return true;
            }
        }
        return false;
    }

    private String getPositionKey(String positionLabel) {
        if (null == positionLabel) {
            return null;
        }
        for (I18nMessage message : userPositionList) {
            if (message.getValue().equals(positionLabel)) {
                return message.getMsgKey();
            }
        }

        return null;
    }

    private UserAccount checkNewUser(UserAccount newUser, TenantInfo tenantInfo) throws NoSuchAlgorithmException {
        String loginName = newUser.getLoginName();
        String telephone = newUser.getTelephone();
        String uuid = newUser.getUid();
        if (StringUtils.isEmpty(uuid)) {
            //create
            isCreate = true;
            if (isExistsByLoginName(loginName)) {
                newUser.setStatusDisplay(UploadFileErrorTypeEnum.LOGINNAME_EXISTS, "用户名已存在");
                return newUser;
            } else {
                newUser.setUid(UUID.randomUUID().toString().replace("-", ""));
            }
            //create finally
            if (telephoneValid(newUser)) {
                newUser.setStatusDisplay(UploadFileErrorTypeEnum.DUPLICATE_TELEPHONE, "电话号码已存在");
            }

            if (StringUtils.isEmpty(newUser.getPassword())) {
                if (StringUtils.isEmpty(tenantInfo.getInitialPassword())) {
                    newUser.setPlainPassword(PasswordUtil.DEFAULT_PWD);
                    newUser.entryptPassword();
                } else {
                    newUser.setPassword(tenantInfo.getInitialPassword());
                    newUser.setPwdSalt(tenantInfo.getInitialPasswordSalt());
                }
            }
        } else {
            //update
            isCreate = false;
            UserAccount user = userAccountRepository.getByUid(uuid);
            if (user == null) {
                newUser.setStatusDisplay(UploadFileErrorTypeEnum.LOGINNAME_NOT_EXISTS, "用户不存在;");
                return newUser;
            } else {
                if (!user.getLoginName().equals(loginName)) {
                    if (isExistsByLoginName(loginName)) {
                        newUser.setStatusDisplay(UploadFileErrorTypeEnum.LOGINNAME_EXISTS, "用户名已存在;");
                        return newUser;
                    }
                }
                if (ObjectUtil.isNotEmpty(telephone) && !telephone.equals(user.getTelephone()) && telephoneValid(newUser)) {
                    newUser.setStatusDisplay(UploadFileErrorTypeEnum.DUPLICATE_TELEPHONE, "电话号码已存在");
                }
            }
            //update finally
            newUser.setUid(user.getUid());
            newUser.setId(user.getId());
            newUser.setPasswordErrorCount(user.getPasswordErrorCount());
            newUser.setUserRoleList(user.getUserRoleList());
            if (StringUtils.isEmpty(newUser.getEmail())) {
                newUser.setEmail(user.getEmail());
            }
            return newUser;
        }
        return newUser;
    }


    private boolean telephoneValid(UserAccount newUser) {
        String telephone = newUser.getTelephone();
        if (StringUtils.isNotEmpty(telephone)) {
            EntityWrapper<UserAccount> wrapper = new EntityWrapper<>();
            wrapper.eq("telephone", telephone);
            wrapper.eq(telephone.length() < 11, "tenant_uid", newUser.getTenantUID());
            wrapper.andNew().eq("is_deleted", false).or().isNull("is_deleted");
            return userAccountMapper.selectCount(wrapper) > 0;
        }
        return false;
    }

    private OrgInfo getOrgInfoByMappingUid(String uid) {
        OrgInfo tempOrgInfo = null;
        if (mapUidOrgInfo.containsKey(uid)) {
            tempOrgInfo = mapUidOrgInfo.get(uid);
        }
        if (tempOrgInfo == null) {
            tempOrgInfo = orgInfoRepository.getByUid(uid);
            if (tempOrgInfo != null) {
                mapUidOrgInfo.put(uid, tempOrgInfo);
            }
        }
        return tempOrgInfo;
    }

    private TenantInfo getTenantInfoByMappingUid(String uid) {
        TenantInfo temp = null;
        if (mapUidTenantInfo.containsKey(uid)) {
            temp = mapUidTenantInfo.get(uid);
        }
        if (temp == null) {
            temp = tenantInfoRepository.findByuid(uid);
            if (temp != null) {
                mapUidTenantInfo.put(uid, temp);
            }
        }
        return temp;
    }

    private OrgInfo getOrgInfoByInstitutionKey(String name, String uid) {
        OrgInfo temp = null;
        String key = name + uid;
        if (mapOrgInfo.containsKey(key)) {
            temp = mapOrgInfo.get(key);
        }
        if (temp == null) {
            temp = orgInfoRepository.getInstitutionByName(name, uid);
            if (temp != null) {
                mapOrgInfo.put(key, temp);
            }
        }
        return temp;
    }

    private OrgInfo getOrgInfoByHospitalKey(String name, String uid) {
        OrgInfo temp = null;
        String key = name + uid;
        if (mapOrgInfo.containsKey(key)) {
            temp = mapOrgInfo.get(key);
        }
        if (temp == null) {
            temp = orgInfoRepository.getHospitalByName(name, uid);
            if (temp != null) {
                mapOrgInfo.put(key, temp);
            }
        }
        return temp;
    }

    private OrgInfo getOrgInfoBySiteKey(String name, String uid) {
        OrgInfo temp = null;
        String key = name + uid;
        if (mapOrgInfo.containsKey(key)) {
            temp = mapOrgInfo.get(key);
        }
        if (temp == null) {
            temp = orgInfoRepository.getSiteByName(name, uid);
            if (temp != null) {
                mapOrgInfo.put(key, temp);
            }
        }
        return temp;
    }

    private OrgInfo getOrgInfoByDepartmentKey(String name, String uid) {
        OrgInfo temp = null;
        String key = name + uid;
        if (mapOrgInfo.containsKey(key)) {
            temp = mapOrgInfo.get(key);
        }
        if (temp == null) {
            temp = orgInfoRepository.getDepartmentByName(name, uid);
            if (temp != null) {
                mapOrgInfo.put(key, temp);
            }
        }
        return temp;
    }

    private OrgInfo getOrgInfoByDepartmentKeyParent(String name, String uid) {
        OrgInfo temp = null;
        String key = name + uid;
        if (mapOrgInfo.containsKey(key)) {
            temp = mapOrgInfo.get(key);
        }
        if (temp == null) {
            temp = orgInfoRepository.getDepartmentByNameParent(name, uid);
            if (temp != null) {
                mapOrgInfo.put(key, temp);
            }
        }
        return temp;
    }

    private void setUIDs(UserAccount newUser, String institution, String hospital, String site, String department, String department2, String department3) {
        orgInfoTemp = getOrgInfoByMappingUid(currentUser.getTenantUID());
        TenantInfo tenantInfo = getTenantInfoByMappingUid(currentUser.getTenantUID());
        if (orgInfoTemp == null || tenantInfo == null) {
            newUser.setStatusDisplay(UploadFileErrorTypeEnum.INSTITUTION, "集团不存在;");
            return;
        }
        newUser.setSiteId(tenantInfo.getId());
        if (getInstitutionID(newUser, institution, isInsulate)) {
            if (getHospitalID(newUser, hospital, isInsulate)) {
                if (getSiteID(newUser, site, isInsulate)) {
                    if (StringUtils.isNotEmpty(department)) {
                        if (getDepartmentID(newUser, department, isInsulate)) {
                            if (StringUtils.isNotEmpty(department2)) {
                                String tempDepartment = department + "-" + department2;
                                if (getDepartmentIDParent(newUser, tempDepartment, isInsulate)) {
                                    if (StringUtils.isNotEmpty(department3)) {
                                        tempDepartment = department + "-" + department2 + "-" + department3;
                                        if (getDepartmentIDParent(newUser, tempDepartment, isInsulate)) {
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        newUser.setOrgInfoUID(orgInfoTemp.getUid());
        newUser.setOrgInfoId(orgInfoTemp.getId());
        checkDepartment(newUser);
    }

    private boolean hasControllerOrgInfo(OrgInfo tempOrgInfo, String userOrgInfoUid) {
        while (tempOrgInfo != null) {
            if (tempOrgInfo.getUid().equals(userOrgInfoUid)) {
                return true;
            }
            tempOrgInfo = tempOrgInfo.getParentOrg();
        }
        return false;
    }

    private boolean getInstitutionID(UserAccount newUser, String institution, boolean isInsulate) {
        OrgInfo temp = getOrgInfoByInstitutionKey(institution, orgInfoTemp.getUid());
        if (temp == null) {
            if (StringUtils.isNotEmpty(institution)) {
                newUser.setStatusDisplay(UploadFileErrorTypeEnum.INSTITUTION_ERROR, "集团错误;");
                return false;
            }
        } else {
            orgInfoTemp = temp;
        }
        if (isInsulate) {
            if (!hasControllerOrgInfo(orgInfoTemp, currentUser.getInstitutionUID())) {
                newUser.setStatusDisplay(UploadFileErrorTypeEnum.INSTITUTION_ERROR, "集团错误;");
                return false;
            }
        }
        newUser.setInstitutionUID(orgInfoTemp.getUid());
        newUser.setOrgLevel(orgInfoTemp.getOrgLevel());
        newUser.setOrgType(orgInfoTemp.getOrgType());
        return true;
    }

    private boolean getHospitalID(UserAccount newUser, String hospital, boolean isInsulate) {
        OrgInfo temp = getOrgInfoByHospitalKey(hospital, orgInfoTemp.getUid());
        if (temp == null) {
            if (StringUtils.isNotEmpty(hospital)) {
                newUser.setStatusDisplay(UploadFileErrorTypeEnum.HOSPITAL_ERROR, "医院错误;");
                return false;
            }
        } else {
            orgInfoTemp = temp;
        }
        if (isInsulate) {
            if (!hasControllerOrgInfo(orgInfoTemp, currentUser.getHospitalUID())) {
                newUser.setStatusDisplay(UploadFileErrorTypeEnum.HOSPITAL_ERROR, "医院错误;");
                return false;
            }
        }
        newUser.setHospitalUID(orgInfoTemp.getUid());
        newUser.setOrgLevel(orgInfoTemp.getOrgLevel());
        newUser.setOrgType(orgInfoTemp.getOrgType());
        return true;
    }

    private boolean getSiteID(UserAccount newUser, String site, boolean isInsulate) {
        OrgInfo temp = getOrgInfoBySiteKey(site, orgInfoTemp.getUid());
        if (temp == null) {
            if (StringUtils.isNotEmpty(site)) {
                newUser.setStatusDisplay(UploadFileErrorTypeEnum.SITE_ERROR, "院区错误;");
                return false;
            }
        } else {
            orgInfoTemp = temp;
        }
        if (isInsulate) {
            if (!hasControllerOrgInfo(orgInfoTemp, currentUser.getSiteUID())) {
                newUser.setStatusDisplay(UploadFileErrorTypeEnum.SITE_ERROR, "院区错误;");
                return false;
            }
        }
        newUser.setSiteUID(orgInfoTemp.getUid());
        newUser.setHospitalId(orgInfoTemp.getId());
        newUser.setOrgLevel(orgInfoTemp.getOrgLevel());
        newUser.setOrgType(orgInfoTemp.getOrgType());
        return true;
    }
    private void checkDepartment(UserAccount newUser){
        boolean flag;
        if(ObjectUtil.isNotEmpty(newUser)){
            flag=affiliatedDepartmentsUid.stream().anyMatch(item->item.equals(newUser.getOrgInfoUID()));
           if(!flag){
               newUser.setStatusDisplay(UploadFileErrorTypeEnum.DEPARTMENT_ERROR, "科室错误;");
           }
         }
    }
    private boolean getDepartmentID(UserAccount newUser, String department, boolean isInsulate) {
        OrgInfo temp = getOrgInfoByDepartmentKey(department, orgInfoTemp.getUid());
        if (temp == null) {
            if (StringUtils.isNotEmpty(department)) {
                newUser.setStatusDisplay(UploadFileErrorTypeEnum.DEPARTMENT_ERROR, "科室错误;");
                return false;
            }
        } else {
            orgInfoTemp = temp;
        }
        newUser.setOrgLevel(orgInfoTemp.getOrgLevel());
        newUser.setOrgType(orgInfoTemp.getOrgType());
        return true;
    }

    private boolean getDepartmentIDParent(UserAccount newUser, String department, boolean isInsulate) {
        OrgInfo temp = getOrgInfoByDepartmentKeyParent(department, orgInfoTemp.getUid());
        if (temp == null) {
            if (StringUtils.isNotEmpty(department)) {
                newUser.setStatusDisplay(UploadFileErrorTypeEnum.DEPARTMENT_ERROR, "科室错误;");
                return false;
            }
        } else {
            orgInfoTemp = temp;
        }
        newUser.setOrgLevel(orgInfoTemp.getOrgLevel());
        newUser.setOrgType(orgInfoTemp.getOrgType());
        return true;
    }

    private boolean supplementUserRole(UserAccount user) {
        String roleName = user.getImportRoleNames();
        List<UserRole> listRole = new ArrayList<>();
        if (StringUtils.isEmpty(roleName)) {
            user.setStatusDisplay(UploadFileErrorTypeEnum.ROLE, "角色不能为空;");
            return false;
        } else {
            String[] roleNames = roleName.split("[,， ]");
            for (int i = 0; i < roleNames.length; ++i) {
                String tempRoleName = roleNames[i];
                if (StringUtils.isEmpty(tempRoleName)) {
                    continue;
                }
                if (ROLE_GUEST.equals(tempRoleName)) {
                    roleNames = new String[]{tempRoleName};
                    listRole.clear();
                }
                if (sysRoleMap.containsKey(tempRoleName)) {
                    SysRole sysRole = sysRoleMap.get(tempRoleName);
                    UserRole newRole = new UserRole();
                    newRole.setRoleId(sysRole.getId());
                    newRole.setUserId(user.getId());
                    newRole.setRole(sysRole);
                    listRole.add(newRole);
                } else if(customRoleMap.containsKey(tempRoleName)){
                    CustomRole customRole = customRoleMap.get(tempRoleName);
                    UserRole newRole = new UserRole();
                    newRole.setRoleId(customRole.getId());
                    newRole.setUserId(user.getId());
                    newRole.setRoleType(1);
                    listRole.add(newRole);
                } else {
                    user.setStatusDisplay(UploadFileErrorTypeEnum.ROLE_NOT_EXISTS, "角色不存在;");
                    return false;
                }
            }
        }
        user.setListImportUserRole(listRole);
        return true;
    }

    private boolean supplementUserRoleWithUserId(UserAccount user) {
        List<UserRole> listUserRole = user.getListImportUserRole();
        if (listUserRole != null && !listUserRole.isEmpty()) {
            for (UserRole userRole : listUserRole) {
                userRole.setUserId(user.getId());
            }
        }
        return true;
    }

    private void supplementOriginUserOrgInfo(UserAccount user) {
        List<UserOrgInfo> listUserOrgInfo = user.getListUserOrgInfo();
        if (listUserOrgInfo == null || listUserOrgInfo.isEmpty()) {
            listUserOrgInfo = userOrgInfoRepository.findByUserUid(user.getUid());
            user.setListUserOrgInfo(listUserOrgInfo);
        }
    }

    private void supplementOriginUserRole(UserAccount user) {
        List<UserRole> listUserRole = user.getUserRoleList();
        if (listUserRole == null || listUserRole.isEmpty()) {
            listUserRole = userRoleRepository.findByUserId(user.getId());
            user.setUserRoleList(listUserRole);
        }
    }

    private Map<String, SysRole> getRoleNameMap() {
        if (sysRoleMap != null) {
            return sysRoleMap;
        }
        Iterable<SysRole> listSysRole = sysRoleRepository.findAll();
        if (listSysRole != null) {
            sysRoleMap = new HashMap<>();
            for (SysRole tempSysRole : listSysRole) {
                sysRoleMap.put(tempSysRole.getRoleDesc(), tempSysRole);
            }
        }
        return sysRoleMap;
    }
    private Map<String, CustomRole> getCustomRoleNameMap() {
        customRoleMap = new HashMap<>();
        List<CustomRole> customRoles = customRoleRepository.findByTenantUID(currentUser.getTenantUID());
        if (CollectionUtils.isEmpty(customRoles)) return customRoleMap;
        customRoleMap = customRoles.stream().collect(Collectors.toMap(CustomRole::getName,item->item));
        return customRoleMap;
    }

    private boolean addUserAccessScope(UserAccount user, OrgInfo tempOrgInfo, boolean showError) {
        if (tempOrgInfo == null) {
            if (showError) {
                user.setStatusDisplay(UploadFileErrorTypeEnum.ACCESSSCOPE_NOT_EXISTS, "可见范围不存在;");
            }
            return false;
        } else {
            List<UserOrgInfo> listUserOrgInfo = user.getListImportUserOrgInfo();
            if (listUserOrgInfo == null) {
                listUserOrgInfo = new ArrayList<>();
                user.setListImportUserOrgInfo(listUserOrgInfo);
            }
            Integer orgType = tempOrgInfo.getOrgType();
            UserOrgInfo tempUserOrgInfo = new UserOrgInfo();
            tempUserOrgInfo.setOrgType(orgType);
            tempUserOrgInfo.setOrgUid(tempOrgInfo.getUid());
            tempUserOrgInfo.setUserUid(user.getUid());
            listUserOrgInfo.add(tempUserOrgInfo);
            if (orgType == 4) {
                addClinicalUserOrg(listUserOrgInfo, tempOrgInfo, user.getUid());
            }
        }
        return true;
    }

    private void addClinicalUserOrg(List<UserOrgInfo> listUserOrgInfo, OrgInfo tempOrgInfo, String userUid) {
        List<OrgInfo> listOrgInfo = orgInfoRepository.getHospitalChildrenByUids(Arrays.asList(tempOrgInfo.getUid()));
        if (!CollectionUtils.isEmpty(listOrgInfo)) {
            for (OrgInfo tempOrg : listOrgInfo) {
                String uid = tempOrg.getUid();
                boolean b = listUserOrgInfo.stream().anyMatch(item -> item.getOrgUid().equals(uid));
                if(!b){
                    UserOrgInfo tempUserOrgInfo = new UserOrgInfo();
                    tempUserOrgInfo.setOrgType(tempOrg.getOrgType());
                    tempUserOrgInfo.setOrgUid(uid);
                    tempUserOrgInfo.setUserUid(userUid);
                    listUserOrgInfo.add(tempUserOrgInfo);
                }
            }
        }
    }

    private boolean supplementAccessScope(UserAccount user) {
        if (ObjectUtil.isAllEmpty(user.getDepartName(), user.getDepartName2(), user.getDepartName3(), user.getAccessScope())) {
            OrgInfo tempOrgInfo = null;
            String key = "";
            if (StringUtils.isNotEmpty(user.getSiteName())) {
                key = user.getSiteName() + user.getHospitalUID();
                if (mapOrgInfo.containsKey(key)) {
                    tempOrgInfo = mapOrgInfo.get(key);
                } else {
                    tempOrgInfo = getOrgInfoBySiteKey(user.getSiteName(), user.getHospitalUID());
                }
                return addUserAccessScope(user, tempOrgInfo, true);
            } else if (StringUtils.isNotEmpty(user.getHospitalName())) {
                key = user.getHospitalName() + user.getInstitutionUID();
                if (mapOrgInfo.containsKey(key)) {
                    tempOrgInfo = mapOrgInfo.get(key);
                } else {
                    tempOrgInfo = getOrgInfoByHospitalKey(user.getHospitalName(), user.getInstitutionUID());
                }
                return addUserAccessScope(user, tempOrgInfo, true);
            } else if (StringUtils.isNotEmpty(user.getInstitutionName())) {
                key = user.getInstitutionName() + user.getTenantUID();
                if (mapOrgInfo.containsKey(key)) {
                    tempOrgInfo = mapOrgInfo.get(key);
                } else {
                    tempOrgInfo = getOrgInfoByHospitalKey(user.getInstitutionName(), user.getTenantUID());
                }
                return addUserAccessScope(user, tempOrgInfo, true);
            } else {
                return addUserAccessScope(user, orgInfoRepository.getOrgInfoUIDByUid(user.getTenantUID()), true);
            }
        } else {
            String accessScope = user.getAccessScope();
            if (ObjectUtil.isEmpty(accessScope)) {
                if (!isCreate) {
                    return true;
                }
                accessScope = "";
            }
            String defaultName = "";
            if (StringUtils.isNotEmpty(user.getDepartName())) {
                defaultName += user.getDepartName();
                if (StringUtils.isNotEmpty(user.getDepartName2())) {
                    defaultName += "-" + user.getDepartName2();
                    if (StringUtils.isNotEmpty(user.getDepartName3())) {
                        defaultName += "-" + user.getDepartName3();
                    }
                }
            }
            accessScope += "," + defaultName;
            String[] arrUserOrgInfo = accessScope.split("[,， ]");
            Set<String> setAccessScope = new HashSet<>();
            for (String s : arrUserOrgInfo) {
                if (StringUtils.isNotEmpty(s)) {
                    setAccessScope.add(s);
                }
            }
            for (String tempStr : setAccessScope) {
                int level;
                String key = tempStr + user.getSiteUID();
                if (StringUtils.isNotEmpty(user.getSiteName())) {
                    // 院区不为空
                    level = 3;
                    key = tempStr + user.getSiteUID();
                    if (!mapOrgInfo.containsKey(key)) {
                        getOrgInfoByDepartmentKey(setAccessScope, new OrgInfo(user.getSiteUID(), user.getSiteName(), null, mapOrgInfo.get(user.getSiteName() + user.getHospitalUID()).getParentUID()));
                    }
                } else if (StringUtils.isNotEmpty(user.getHospitalName())) {
                    // 医院不为空
                    level = 2;
                    if (!mapOrgInfo.containsKey(key)) {
                        getOrgInfoBySiteKey(setAccessScope, new OrgInfo(user.getHospitalUID(), user.getHospitalName(), null, mapOrgInfo.get(user.getHospitalName() + user.getInstitutionUID()).getParentUID()));
                    }
                } else if (StringUtils.isNotEmpty(user.getInstitutionName())) {
                    // 集团不为空
                    level = 1;
                    if (!mapOrgInfo.containsKey(key)) {
                        getOrgInfoByHospitalKey(setAccessScope, new OrgInfo(user.getInstitutionUID(), user.getInstitutionName(), null, mapOrgInfo.get(user.getInstitutionName() + user.getTenantUID()).getParentUID()));
                    }
                } else {
                    // 都为空 租户层级
                    level = 0;
                    if (!mapOrgInfo.containsKey(key)) {
                        getOrgInstitutionKey(setAccessScope, new OrgInfo(user.getInstitutionUID(), user.getInstitutionName(), null, null));
                    }
                }
                OrgInfo tempOrgInfo =  mapOrgInfo.computeIfAbsent(key,key1->{
                    return level == 3 ? mapOrgInfo.get(tempStr + user.getHospitalUID()):(level == 2 ? mapOrgInfo.get(tempStr + user.getHospitalUID()):(level == 1 ? mapOrgInfo.get(tempStr + user.getInstitutionUID()):(level == 0 ? mapOrgInfo.get(tempStr + user.getTenantUID()):null)));
                });
                if (key.equals(defaultName + user.getSiteUID())) {
                    if (!addUserAccessScope(user, tempOrgInfo, false)) {
                        return false;
                    }
                } else {
                    if (!addUserAccessScope(user, tempOrgInfo, true)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void getOrgInfoByDepartmentKey(Set<String> departmentNames, OrgInfo siteOrgInfo) {
        List<OrgInfo> listOrgInfo = orgInfoRepository.getDepartmentByName(departmentNames, siteOrgInfo.getUid());
        if (listOrgInfo != null) {
            for (OrgInfo tempOrgInfo : listOrgInfo) {
                String key = tempOrgInfo.getName() + siteOrgInfo.getUid();
                mapOrgInfo.put(key, tempOrgInfo);
                String parentKey = siteOrgInfo.getName() + "/" + tempOrgInfo.getName() + siteOrgInfo.getParentUID();
                mapOrgInfo.put(parentKey, tempOrgInfo);
            }
        }
    }

    private void getOrgInfoBySiteKey(Set<String> names, OrgInfo hospitalOrgInfo) {
        Set<String> siteNames = new HashSet<>();
        Map<String, Set<String>> departmentMap = new HashMap<>();
        for (String name : names) {
            List<String> accessScopeNames = Arrays.asList(name.split("/"));
            if (accessScopeNames.size() == 2) {
                Set<String> departmentNames = departmentMap.get(accessScopeNames.get(0));
                if (ObjectUtil.isEmpty(departmentNames)) {
                    departmentNames = new HashSet<>();
                }
                departmentNames.add(accessScopeNames.get(1));
                departmentMap.put(accessScopeNames.get(0), departmentNames);
            }
            siteNames.add(accessScopeNames.get(0));
        }

        List<OrgInfo> listOrgInfos = new ArrayList<>();
        List<OrgInfo> siteOrgInfos = orgInfoRepository.getSiteByName(siteNames, hospitalOrgInfo.getUid());
        for (OrgInfo siteOrgInfo : siteOrgInfos) {
            Set<String> orgDepartmentNames = departmentMap.get(siteOrgInfo.getName());
            if (ObjectUtil.isNotEmpty(orgDepartmentNames)) {
                getOrgInfoByDepartmentKey(orgDepartmentNames, siteOrgInfo);
                for (String orgDepartmentName : orgDepartmentNames) {
                    String departmentOrgKey = siteOrgInfo.getName() + "/" + orgDepartmentName + hospitalOrgInfo.getUid();
                    OrgInfo departmentOrg = mapOrgInfo.get(departmentOrgKey);
                    if (ObjectUtil.isNotEmpty(departmentOrg)) {
                        mapOrgInfo.put(hospitalOrgInfo.getName() + "/" + siteOrgInfo.getName() + "/" + orgDepartmentName + hospitalOrgInfo.getParentUID(), departmentOrg);
                    }
                }
            } else {
                listOrgInfos.add(siteOrgInfo);
                mapOrgInfo.put(hospitalOrgInfo.getName() + "/" + siteOrgInfo.getName() + hospitalOrgInfo.getParentUID(), siteOrgInfo);
            }
        }
        for (OrgInfo tempOrgInfo : listOrgInfos) {
            String key = tempOrgInfo.getName() + hospitalOrgInfo.getUid();
            mapOrgInfo.put(key, tempOrgInfo);
        }
    }

    private void getOrgInfoByHospitalKey(Set<String> names, OrgInfo institutionOrgInfo) {
        Set<String> hospitalNames = new HashSet<>();
        Map<String, Set<String>> siteMap = new HashMap<>();
        for (String name : names) {
            List<String> accessScopeNames = Arrays.asList(name.split("/"));
            if (accessScopeNames.size() == 3) {
                Set<String> siteNames = siteMap.get(accessScopeNames.get(0));
                if (ObjectUtil.isEmpty(siteNames)) {
                    siteNames = new HashSet<>();
                }
                siteNames.add(accessScopeNames.get(1) + "/" + accessScopeNames.get(2));
                siteMap.put(accessScopeNames.get(0), siteNames);
            } else if (accessScopeNames.size() == 2) {
                Set<String> siteNames = siteMap.get(accessScopeNames.get(1));
                if (ObjectUtil.isEmpty(siteNames)) {
                    siteNames = new HashSet<>();
                }
                siteNames.add(accessScopeNames.get(1));
                siteMap.put(accessScopeNames.get(0), siteNames);
            }
            hospitalNames.add(accessScopeNames.get(0));
        }

        List<OrgInfo> listOrgInfos = new ArrayList<>();
        List<OrgInfo> hospitalOrgInfos = orgInfoRepository.getHospitalByName(hospitalNames, institutionOrgInfo.getUid());
        for (OrgInfo hospitalOrgInfo : hospitalOrgInfos) {
            Set<String> siteNames = siteMap.get(hospitalOrgInfo.getName());
            if (ObjectUtil.isNotEmpty(siteNames)) {
                getOrgInfoBySiteKey(siteNames, hospitalOrgInfo);
                for (String siteName : siteNames) {
                    String siteOrgKey = hospitalOrgInfo.getName() + "/" + siteName + institutionOrgInfo.getUid();
                    OrgInfo siteOrg = mapOrgInfo.get(siteOrgKey);
                    if (ObjectUtil.isNotEmpty(siteOrg)) {
                        mapOrgInfo.put(institutionOrgInfo.getName() + "/" + hospitalOrgInfo.getName() + "/" + siteName + institutionOrgInfo.getParentUID(), siteOrg);
                    }
                }
            } else {
                listOrgInfos.add(hospitalOrgInfo);
                mapOrgInfo.put(institutionOrgInfo.getName() + "/" + hospitalOrgInfo.getName() + institutionOrgInfo.getParentUID(), hospitalOrgInfo);
            }
        }
        for (OrgInfo tempOrgInfo : listOrgInfos) {
            String key = tempOrgInfo.getName() + institutionOrgInfo.getUid();
            mapOrgInfo.put(key, tempOrgInfo);
        }
    }

    private void getOrgInstitutionKey(Set<String> names, OrgInfo tenantOrgInfo) {
        Set<String> institutionNames = new HashSet<>();
        Map<String, Set<String>> siteMap = new HashMap<>();
        for (String name : names) {
            List<String> accessScopeNames = Arrays.asList(name.split("/"));
            if (accessScopeNames.size() == 4) {
                Set<String> siteNames = siteMap.get(accessScopeNames.get(0));
                if (ObjectUtil.isEmpty(siteNames)) {
                    siteNames = new HashSet<>();
                }
                siteNames.add(accessScopeNames.get(1) + "/" + accessScopeNames.get(2) + "/" + accessScopeNames.get(3));
                siteMap.put(accessScopeNames.get(0), siteNames);
            } else if (accessScopeNames.size() == 3) {
                Set<String> siteNames = siteMap.get(accessScopeNames.get(0));
                if (ObjectUtil.isEmpty(siteNames)) {
                    siteNames = new HashSet<>();
                }
                siteNames.add(accessScopeNames.get(1) + "/" + accessScopeNames.get(2));
                siteMap.put(accessScopeNames.get(0), siteNames);
            } else if (accessScopeNames.size() == 2) {
                Set<String> siteNames = siteMap.get(accessScopeNames.get(0));
                if (ObjectUtil.isEmpty(siteNames)) {
                    siteNames = new HashSet<>();
                }
                siteNames.add(accessScopeNames.get(1));
                siteMap.put(accessScopeNames.get(0), siteNames);
            }
            institutionNames.add(accessScopeNames.get(0));
        }

        List<OrgInfo> listOrgInfos = new ArrayList<>();
        List<OrgInfo> institutionOrgInfos = orgInfoRepository.getInstitutionByName(institutionNames, tenantOrgInfo.getUid());
        for (OrgInfo institutionOrgInfo : institutionOrgInfos) {
            Set<String> siteNames = siteMap.get(institutionOrgInfo.getName());
            if (ObjectUtil.isNotEmpty(siteNames)) {
                getOrgInfoByHospitalKey(siteNames, institutionOrgInfo);
            } else {
                listOrgInfos.add(institutionOrgInfo);
            }
        }
        for (OrgInfo tempOrgInfo : listOrgInfos) {
            String key = tempOrgInfo.getName() + tenantOrgInfo.getUid();
            mapOrgInfo.put(key, tempOrgInfo);
        }
    }

    private void initParam() {
        mapOrgInfo.clear();
        mapUidOrgInfo.clear();
        mapUidTenantInfo.clear();
    }

    public List<UserAccount> handleExcel(UploadedFile file, UserAccount user) throws IOException {
        initParam();
        getRoleNameMap();
        currentUser = user;
        getCustomRoleNameMap();
        affiliatedDepartments=orgInfoRepository.getHospitalChildrenByUids(Collections.singletonList(currentUser.getOrgInfoUID()));
        affiliatedDepartmentsUid=affiliatedDepartments.stream().map(OrgInfo::getUid).distinct().collect(Collectors.toList());
        TenantInfo tenantInfo = tenantInfoRepository.findByuid(user.getTenantUID());
        if (tenantInfo != null && tenantInfo.getInsulate() != null) {
            isInsulate = tenantInfo.getInsulate();
            if (isInsulate && !UserContextService.isOnlyLocalITAdmin()) {
                isInsulate = false;
            }
        }
        List<UserAccount> importFailedDataList = new ArrayList<>();

        Workbook workbook = null;
        if (file.getFileName().toLowerCase().endsWith(".xls")) {
            workbook = new HSSFWorkbook(file.getInputstream());
        } else if (file.getFileName().toLowerCase().endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(file.getInputstream());
        }

        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            return Collections.emptyList();
        }
        Map<String, Integer> header = ExcelParser.findFirstRowHeader(sheet);
        if (header.isEmpty()) {
            return Collections.emptyList();
        }
        String institution;
        String hospital;
        String site;
        String department;
        String department2;
        String department3;
        String loginName;
        String name;
        String email;
        String telephone;
        boolean isActive;
        boolean isLocked;
        boolean isDisable;
        String roleName;
        String accessScope;
        String position;
        String tagsJoined;
        String password;
        String UUID;
        String assetClassification;


        int institutionIndex = header.get(HEADER_INSTITUTION);
        int hospitalIndex = header.get(HEADER_HOSPITAL);
        int siteIndex = header.get(HEADER_SITE);
        int departmentIndex = header.get(HEADER_DEPARTMENT);
        int departmentIndex2 = header.get(HEADER_DEPARTMENT2);
        int departmentIndex3 = header.get(HEADER_DEPARTMENT3);
        int loginnameIndex = header.get(HEADER_LOGINNAME);
        int nameIndex = header.get(HEADER_NAME);
        int emailIndex = header.get(HEADER_EMAIL);
        int telephoneIndex = header.get(HEADER_TELEPHONE);
        int isaCtiveIndex = header.get(HEADER_ISACTIVE);
        int isLockedIndex = header.get(HEADER_ISLOCKED);
        int isDisableIndex = header.get(HEADER_ISDISABLE);
        int roleNameIndex = header.get(HEADER_ROLENAME);
        int accessScopeIndex = header.get(HEADER_ACCESSSCOPE);
        int positionIndex = header.get(HEADER_POSITION);
        int tagsIndex = header.get(HEADER_TAGS);
        Integer passwordIndex = header.get(HEADER_PASSWORD);
        Integer uuidIndex = header.get(HEADER_UUID);
        int assetClassificationIndex=header.get(HEADER_ASSET_CLASSIFICATION);


        List<String> telephoneList = new ArrayList<>();
        List<String> loginNameList = new ArrayList<>();
        for (int j = 1; j <= sheet.getLastRowNum(); j++) {
            Row row = sheet.getRow(j);
            if (row == null) {
                continue;
            }
            UserAccount newUser = new UserAccount();
            try {
                newUser.setExcelIdex(j + 1);
                newUser.setTenantUID(user.getTenantUID());
                institution = ExcelParser.getCellStringValue(row, institutionIndex);
                hospital = ExcelParser.getCellStringValue(row, hospitalIndex);
                site = ExcelParser.getCellStringValue(row, siteIndex);
                department = ExcelParser.getCellStringValue(row, departmentIndex);
                department2 = ExcelParser.getCellStringValue(row, departmentIndex2);
                department3 = ExcelParser.getCellStringValue(row, departmentIndex3);
                loginName = ExcelParser.getCellStringValue(row, loginnameIndex);
                assetClassification=ExcelParser.getCellStringValue(row, assetClassificationIndex);

                if (loginName != null && loginName.length() > 0) {
                    boolean canParseDouble;
                    try {
                        Double.parseDouble(loginName);
                        canParseDouble = true;
                    } catch (Exception e) {
                        canParseDouble = false;
                    }
                    if (canParseDouble) {
                        if (loginName.indexOf(".") > 0) {
                            loginName = loginName.substring(0, loginName.indexOf("."));
                        }
                    }
                }
                name = ExcelParser.getCellStringValue(row, nameIndex);
                email = ExcelParser.getCellStringValue(row, emailIndex);
                telephone = ExcelParser.getCellStringValue(row, telephoneIndex);
                String strActive = ExcelParser.getCellStringValue(row, isaCtiveIndex);
                isActive = StringUtils.isNotEmpty(strActive) && "是".equals(strActive);
                String strLocked = ExcelParser.getCellStringValue(row, isLockedIndex);
                isLocked = !StringUtils.isNotEmpty(strLocked) || !"否".equals(strLocked);
                String strDisable = ExcelParser.getCellStringValue(row, isDisableIndex);
                isDisable = StringUtils.isNotEmpty(strDisable) && "是".equals(strDisable);
                roleName = ExcelParser.getCellStringValue(row, roleNameIndex);
                accessScope = ExcelParser.getCellStringValue(row, accessScopeIndex);

                position = ExcelParser.getCellStringValue(row, positionIndex);

                tagsJoined = ExcelParser.getCellStringValue(row, tagsIndex);

                password = ObjectUtil.isNotEmpty(passwordIndex) ? ExcelParser.getCellStringValue(row, passwordIndex) : "";

                UUID = ObjectUtil.isNotEmpty(uuidIndex) ? ExcelParser.getCellStringValue(row, uuidIndex) : "";

                newUser.setLoginName(loginName);
                newUser.setName(name);
                newUser.setEmail(email);
                if (StringUtils.isNotEmpty(telephone)) {
                    if (StringUtils.contains(telephone, "E")) {
                        BigDecimal bd = new BigDecimal(telephone);
                        telephone = bd.toPlainString();
                    }
                    if (telephoneList.contains(telephone)) {
                        newUser.setStatusDisplay(UploadFileErrorTypeEnum.DUPLICATE_TELEPHONE, "Excel 表中电话号码重复");
                    } else {
                        telephoneList.add(telephone);
                    }
                }
                newUser.setTelephone(telephone);
                newUser.setIsActive(isActive);
                if (isActive) {
                    newUser.setActiveTime(new Date());
                }
                newUser.setIsLocked(isLocked);
                newUser.setIsDisable(isDisable);
                newUser.setUid(UUID);
                newUser.setInstitutionName(institution);
                newUser.setHospitalName(hospital);
                newUser.setSiteName(site);
                newUser.setDepartName(department);
                newUser.setDepartName2(department2);
                newUser.setDepartName3(department3);
                newUser.setAccessScope(accessScope);
                newUser.setImportRoleNames(roleName);
                newUser.setPosition(getPositionKey(position));
                newUser.setTags(separateTags(tagsJoined));

                //handle password
                if (StringUtils.isNotEmpty(password) && StringUtils.isNotEmpty(password.trim())) {
                    String checkResult = PasswordUtil.checkPwd(password.trim());
                    if (!StringUtils.isEmpty(checkResult)) {
                        newUser.setStatusDisplay(UploadFileErrorTypeEnum.PASSWORD, checkResult);
                    } else {
                        newUser.setPlainPassword(password.trim());
                        newUser.entryptPassword();
                    }
                }

                if (StringUtils.isEmpty(loginName)) {
                    newUser.setStatusDisplay(UploadFileErrorTypeEnum.LOGINNAME, "登录名不能为空;");
                } else {
                    if (loginNameList.contains(loginName)) {
                        newUser.setStatusDisplay(UploadFileErrorTypeEnum.LOGINNAME, "Excel 表中登录名重复;");
                    } else {
                        loginNameList.add(loginName);
                    }
                }
                if (StringUtils.isEmpty(name)) {
                    newUser.setStatusDisplay(UploadFileErrorTypeEnum.NAME, "姓名不能为空;");
                }
                if (StringUtils.isEmpty(strActive)) {
                    newUser.setStatusDisplay(UploadFileErrorTypeEnum.IS_ACTIVED, "激活值不能为空;");
                }
                if (StringUtils.isEmpty(strLocked)) {
                    newUser.setStatusDisplay(UploadFileErrorTypeEnum.IS_LOCKED, "锁定值不能为空;");
                }
                if (StringUtils.isNotEmpty(department3) && StringUtils.isEmpty(department2)) {
                    newUser.setStatusDisplay(UploadFileErrorTypeEnum.DEPARTMENT_ERROR, "科室错误;");
                }
                if (StringUtils.isNotEmpty(department2) && StringUtils.isEmpty(department)) {
                    newUser.setStatusDisplay(UploadFileErrorTypeEnum.DEPARTMENT_ERROR, "科室错误;");
                }
                if (StringUtils.isNotEmpty(email) && !emailValidator.isValid(email)) {
                    newUser.setEmail(null);
                }
                if (StringUtils.isEmpty(roleName)) {
                    newUser.setStatusDisplay(UploadFileErrorTypeEnum.ROLE, "角色不能为空;");
                }
//                if(StringUtils.isEmpty(assetClassification)){
//                    newUser.setStatusDisplay(UploadFileErrorTypeEnum.LOGINNAME,"资产分类不能为空;");
//                }

                if (!handleNewUser(newUser, institution, hospital, site, department, department2, department3, tenantInfo,assetClassification)) {
                    importFailedDataList.add(newUser);
                    failedCount++;
                } else {
                    successCount++;
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                newUser.setStatusDisplay(UploadFileErrorTypeEnum.FORMAT_ERROR, "格式错误;");
                importFailedDataList.add(newUser);
                failedCount++;
            }
        }
        addWarnMessage();
        return importFailedDataList;
    }

    private List<String> separateTags(String tagsJoined) {
        if (StringUtils.isEmpty(tagsJoined)) {
            return Collections.emptyList();
        }

        // use space or comma as delimiter
        return Arrays.stream(tagsJoined.split("[, ]"))
                .filter(item -> !StringUtils.isEmpty(item))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    private void addWarnMessage() {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "处理完毕!", "总共" + (successCount + failedCount) + "条记录，" + successCount + "条处理成功，" + failedCount + "条失败！");
        FacesContext.getCurrentInstance().addMessage(null, message);
        successCount = 0;
        failedCount = 0;
    }

    @Transactional
    public void createUser(UserAccount user) {
        if (user.getId() != null) {
            updateUser(user);
            return;
        }
        try {
            user.setCreatedBy(currentUser.getLoginName());
            user.setCreatedDate(new Date());
            user.setLastModifiedBy(user.getCreatedBy());
            user.setLastModifiedDate(user.getCreatedDate());
            user.setPasswordErrorCount(0);

            userAccountHistoryService.save(UserAccount.HistoryAction.IMPORT_CREATE.name(), user);
            userAccountRepository.save(user);
            saveUserRole(user, true);
            saveUserOrgInfo(user, true);

            // need user uid to save
            saveOrUpdateTags(user);
            saveUserAuthGroup(assetClassificationUIds,user,true);


        } catch (Exception e) {
            logger.error("create user error : {}", e.getMessage());
        }
    }

    private void saveOrUpdateTags(UserAccount user) {
        List<String> existingTags = userTagsMapper.getTagsByUser(user.getUid());
        List<String> currentTags = user.getTags();

        List<String> newTags = new ArrayList<>(currentTags);
        newTags.removeAll(existingTags);

        List<String> removeTags = new ArrayList<>(existingTags);
        removeTags.removeAll(currentTags);

        //add new tags
        newTags.stream().distinct().forEach(item -> {
            UserTags tags = new UserTags();
            tags.setUserUid(user.getUid());
            tags.setTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());
            tags.setTag(item);
            userTagsDao.save(tags);
        });

        //remove useless tags
        removeTags.stream().distinct().forEach(item -> userTagsMapper.removeTag(user.getUid(), item));

    }

    private void deleteUserRole(UserAccount user) {
        List<UserRole> listUserRole = user.getUserRoleList();
        if (listUserRole != null && !listUserRole.isEmpty() && user.getListImportUserRole() != null && !user.getListImportUserRole().isEmpty()) {
            for (UserRole userRole : listUserRole) {
                userAccountMapper.deleteUserRole(userRole);
            }
        }
    }

    private void saveUserRole(UserAccount user, boolean create) {
        if (!create) {
            //delete before save
            supplementOriginUserRole(user);
            userAccountHistoryService.save(UserAccount.HistoryAction.IMPORT_UPDATE_ROLE.name(), user);
            deleteUserRole(user);
        }
        supplementUserRoleWithUserId(user);
        List<UserRole> listUserRole = user.getListImportUserRole();
        if (listUserRole != null && !listUserRole.isEmpty()) {
            for (UserRole userRole : listUserRole) {
                userAccountMapper.saveUserRole(userRole);
            }
        }
    }

    private void deleteUserOrgInfo(UserAccount user) {
        List<UserOrgInfo> listUserOrgInfo = user.getListUserOrgInfo();
        if (listUserOrgInfo != null && !listUserOrgInfo.isEmpty() && user.getListImportUserOrgInfo() != null && !user.getListImportUserOrgInfo().isEmpty()) {
            userOrgInfoRepository.delete(listUserOrgInfo);
        }
    }

    private void saveUserOrgInfo(UserAccount user, boolean create) {
        if (!create) {
            supplementOriginUserOrgInfo(user);
            userAccountHistoryService.save(UserAccount.HistoryAction.IMPORT_UPDATE_ORG.name(), user);
            deleteUserOrgInfo(user);
        } else {
            userAccountHistoryService.save(UserAccount.HistoryAction.IMPORT_CREATE_ORG.name(), user);
        }
        List<UserOrgInfo> listUserOrgInfo = user.getListImportUserOrgInfo();
        if (listUserOrgInfo != null && !listUserOrgInfo.isEmpty()) {
            userOrgInfoRepository.save(listUserOrgInfo);
        }
    }

    private void updateUser(UserAccount user) {
        try {
            //save
            user.setLastModifiedBy(currentUser.getLoginName());
            user.setLastModifiedDate(new Date());
            userAccountHistoryService.save(UserAccount.HistoryAction.IMPORT_UPDATE.name(), user);
            userAccountMapper.updateUserAccount(user);
            saveUserRole(user, false);
            saveUserOrgInfo(user, false);

            // need user uid to save tags
            saveOrUpdateTags(user);
            saveUserAuthGroup(assetClassificationUIds,user,false);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("update user error : {}", e.getMessage());
        }
    }


    private void saveUserAuthGroup(List<String> assetClassificationUIds,UserAccount user,boolean isCreate) {
        if(!isCreate){
            //update
            userAssetGroupDao.deleteByUserUid(user.getUid());
        }
        if(assetClassificationUIds!=null && !assetClassificationUIds.isEmpty()){
            for(int i=0;i<assetClassificationUIds.size();++i){
                String authGroupUid = assetClassificationUIds.get(i);
                AssetTypeGroup sysAuthGroup = assetTypeGroupDao.findByUid(authGroupUid);
                UserAssetGroup userAuthGroup = new UserAssetGroup();
                userAuthGroup.setUserUid(user.getUid());
                userAuthGroup.setAssetGroupUid(sysAuthGroup.getUid());
                userAssetGroupDao.save(userAuthGroup);
            }
        }

        if (UserContextService.getCurrentUserAccount().getLoginName().equals(user.getLoginName())) {
            // 如果修改的用户为当前登录用户，重新设置当前登录用户的资产分类
            UserContextService userContextService = WebUtil.getBean(UserContextService.class);
            userContextService.setAssetTypeGroup();
        }
    }
    private List<String> assetClassificationUIds = new ArrayList<>();

    private UserAccount checkAssetClassification(UserAccount newUser,String assetClassification) {
        String[] checkAssetClassificationUIds = assetClassification.trim().split(",");
        List<AssetTypeGroup> assetTypeGroups = assetTypeGroupDao.findByClassificationCodeAndTenantUid(Arrays.asList(checkAssetClassificationUIds),newUser.getTenantUID());
        if(assetTypeGroups!=null && !assetTypeGroups.isEmpty()){
            assetClassificationUIds = assetTypeGroups.stream().map(AssetTypeGroup::getUid).collect(Collectors.toList());
        }
        return null;
    }

}
