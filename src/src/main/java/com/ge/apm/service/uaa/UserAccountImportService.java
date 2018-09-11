package com.ge.apm.service.uaa;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.collections.CollectionUtils;
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

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SysRoleRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.UserOrgInfoRepository;
import com.ge.apm.dao.UserRoleRepository;
import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.SysRole;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UploadFileErrorTypeEnum;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserModel;
import com.ge.apm.domain.UserOrgInfo;
import com.ge.apm.domain.UserRole;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.ExcelParser;
import com.ge.apm.service.utils.PasswordUtil;
import com.ge.apm.view.sysutil.UserContextService;

@Service
public class UserAccountImportService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static final String HEADER_INSTITUTION="所属集团*";
	private static final String HEADER_HOSPITAL="所属医院";
	private static final String HEADER_SITE="所属院区";
	private static final String HEADER_DEPARTMENT="科室1";
	private static final String HEADER_DEPARTMENT2="科室2";
	private static final String HEADER_DEPARTMENT3="科室3";
	private static final String HEADER_LOGINNAME="登录用户名*";
	private static final String HEADER_NAME="姓名*";
	private static final String HEADER_EMAIL="Email";
	private static final String HEADER_TELEPHONE="电话";
	private static final String HEADER_ISACTIVE="激活?*";
	private static final String HEADER_ISLOCKED="锁定?*";
	private static final String HEADER_ROLENAME="角色*";
	private static final String HEADER_ACCESSSCOPE="可见范围（科室）";
	private static final String HEADER_UUID="UUID";
	private static final String SHEET_USER="datalist";
	private static Map<String,Integer> sysRoleMap;
	private Map<String,OrgInfo> mapOrgInfo=new HashMap<String,OrgInfo>();
	private Map<String,OrgInfo> mapUidOrgInfo=new HashMap<String,OrgInfo>();
	private Map<String,TenantInfo> mapUidTenantInfo=new HashMap<String,TenantInfo>();
	private OrgInfo orgInfoTemp;
	private int successCount;
	private int failedCount;
	private int existsCount;
	private int recordCount;
	boolean isInsulate=false;
	boolean isCreate=true;
	UserAccount currentUser;
	
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

	EmailValidator emailValidator = EmailValidator.getInstance();


	private boolean isExistsByLoginName(String loginName) {
		return userAccountMapper.getUserByLoginName(loginName) != null ? true : false;
	}
	private boolean checkData(UserAccount newUser){
		boolean pass=true;
		if(StringUtils.isNotEmpty(newUser.getStatus())){
			pass=false;
		}
		return pass;
	}
	private boolean handleNewUser(UserAccount newUser,String institution,String hospital,String site,String department,String department2,String department3){
		//get all user
		checkLoginName(newUser);
		if(!supplementUserRole(newUser)){
			return false;
		}
		if(checkData(newUser)){
			setUIDs(newUser, institution, hospital, site, department,department2,department3);
			supplementAccessScope(newUser);
			if(checkData(newUser)){
				createUser(newUser);
				return true;
			}
		}
		return false;
	}
	
	private UserAccount checkLoginName(UserAccount newUser){
		String loginName=newUser.getLoginName();
		String uuid=newUser.getUid();
		if(StringUtils.isEmpty(uuid)){
			//create
			isCreate=true;
			if(isExistsByLoginName(loginName)){
				existsCount++;
				newUser.setStatusDisplay(UploadFileErrorTypeEnum.LOGINNAME_EXISTS,"用户名已存在");
				return newUser;
			}
			else{
				newUser.setUid(UUID.randomUUID().toString().replace("-", ""));
			}
			//create finally
		}
		else{
			//update
			isCreate=false;
			UserAccount user=userAccountRepository.getByUid(uuid);
			if(user==null){
				newUser.setStatusDisplay(UploadFileErrorTypeEnum.LOGINNAME_NOT_EXISTS,"用户不存在;");
				return newUser;
			}
			else{
				if(!user.getLoginName().equals(loginName)){
					if(isExistsByLoginName(loginName)){
						newUser.setStatusDisplay(UploadFileErrorTypeEnum.LOGINNAME_EXISTS,"用户名已存在;");
						return newUser;
					}
				}
			}
			//update finally
			newUser.setUid(user.getUid());
			newUser.setId(user.getId());
			newUser.setPasswordErrorCount(user.getPasswordErrorCount());
			newUser.setUserRoleList(user.getUserRoleList());
			if(StringUtils.isEmpty(newUser.getEmail())){
				newUser.setEmail(user.getEmail());
			}
			return newUser;
		}
		return newUser;
	}
	private OrgInfo getOrgInfoByMappingUid(String uid){
		OrgInfo tempOrgInfo=null;
		if(mapUidOrgInfo.containsKey(uid)){
			tempOrgInfo=mapUidOrgInfo.get(uid);
		}
		if(tempOrgInfo==null){
			tempOrgInfo=orgInfoRepository.getByUid(uid);
			if(tempOrgInfo!=null){
				mapUidOrgInfo.put(uid, tempOrgInfo);
			}
		}
		return tempOrgInfo;
	}
	private TenantInfo getTenantInfoByMappingUid(String uid){
		TenantInfo temp=null;
		if(mapUidTenantInfo.containsKey(uid)){
			temp=mapUidTenantInfo.get(uid);
		}
		if(temp==null){
			temp=tenantInfoRepository.findByuid(uid);
			if(temp!=null){
				mapUidTenantInfo.put(uid, temp);
			}
		}
		return temp;
	}
	private OrgInfo getOrgInfoByInstitutionKey(String name,String uid){
		OrgInfo temp=null;
		String key=name+uid;
		if(mapOrgInfo.containsKey(key)){
			temp=mapOrgInfo.get(key);
		}
		if(temp==null){
			temp=orgInfoRepository.getInstitutionByName(name, uid);
			if(temp!=null){
				mapOrgInfo.put(key, temp);
			}
		}
		return temp;
	}
	private OrgInfo getOrgInfoByHospitalKey(String name,String uid){
		OrgInfo temp=null;
		String key=name+uid;
		if(mapOrgInfo.containsKey(key)){
			temp=mapOrgInfo.get(key);
		}
		if(temp==null){
			temp=orgInfoRepository.getHospitalByName(name, uid);
			if(temp!=null){
				mapOrgInfo.put(key, temp);
			}
		}
		return temp;
	}
	private OrgInfo getOrgInfoBySiteKey(String name,String uid){
		OrgInfo temp=null;
		String key=name+uid;
		if(mapOrgInfo.containsKey(key)){
			temp=mapOrgInfo.get(key);
		}
		if(temp==null){
			temp=orgInfoRepository.getSiteByName(name, uid);
			if(temp!=null){
				mapOrgInfo.put(key, temp);
			}
		}
		return temp;
	}
	private OrgInfo getOrgInfoByDepartmentKey(String name,String uid){
		OrgInfo temp=null;
		String key=name+uid;
		if(mapOrgInfo.containsKey(key)){
			temp=mapOrgInfo.get(key);
		}
		if(temp==null){
			temp=orgInfoRepository.getDepartmentByName(name, uid);
			if(temp!=null){
				mapOrgInfo.put(key, temp);
			}
		}
		return temp;
	}
	
	private OrgInfo getOrgInfoByDepartmentKeyParent(String name,String uid){
		OrgInfo temp=null;
		String key=name+uid;
		if(mapOrgInfo.containsKey(key)){
			temp=mapOrgInfo.get(key);
		}
		if(temp==null){
			temp=orgInfoRepository.getDepartmentByNameParent(name, uid);
			if(temp!=null){
				mapOrgInfo.put(key, temp);
			}
		}
		return temp;
	}
	private void getOrgInfoByDepartmentKey(Set<String> names,String uid){
		List<OrgInfo> listOrgInfo=orgInfoRepository.getDepartmentByName(names,uid);
		if(listOrgInfo!=null){
			for(int i=0;i<listOrgInfo.size();++i){
				OrgInfo tempOrgInfo=listOrgInfo.get(i);
				String key=tempOrgInfo.getName()+uid;
				mapOrgInfo.put(key, tempOrgInfo);
			}
		}
	}
	private void setUIDs(UserAccount newUser,String institution,String hospital,String site,String department,String department2,String department3){
		orgInfoTemp=getOrgInfoByMappingUid(currentUser.getTenantUID());
		TenantInfo tenantInfo=getTenantInfoByMappingUid(currentUser.getTenantUID());
		if(orgInfoTemp==null || tenantInfo==null){
			newUser.setStatusDisplay(UploadFileErrorTypeEnum.INSTITUTION,"集团不存在;");
			return;
		}
		newUser.setSiteId(tenantInfo.getId());
		if(isInsulate){
//			newUser.setInstitutionUID(currentUser.getInstitutionUID());
//			newUser.setHospitalUID(currentUser.getHospitalUID());
//			orgInfoTemp=getOrgInfoByMappingUid(currentUser.getHospitalUID());
////			newUser.setSiteUID(currentUser.getSiteUID());
////			orgInfoTemp=orgInfoRepository.getByUid(currentUser.getSiteUID());
//			newUser.setOrgLevel(orgInfoTemp.getOrgLevel());
//			newUser.setOrgType(orgInfoTemp.getOrgType());
//			if(getSiteID(newUser, site)){
//				if(getDepartmentID(newUser, department,isInsulate)){
//					if(getDepartmentIDParent(newUser, department+"-"+department2,isInsulate)){
//						if(getDepartmentIDParent(newUser,department+"-"+department2+"-"+department3,isInsulate)){
//						}
//					}
//				}
//			}
			if(getInstitutionID(newUser, institution)){
				if(!orgInfoTemp.getUid().equals(currentUser.getInstitutionUID())){
					newUser.setStatusDisplay(UploadFileErrorTypeEnum.INSTITUTION_ERROR,"集团错误;");
					return;
				}
				if(getHospitalID(newUser, hospital)){
					if(!orgInfoTemp.getUid().equals(currentUser.getHospitalUID())){
						newUser.setStatusDisplay(UploadFileErrorTypeEnum.HOSPITAL_ERROR,"医院错误;");
						return;
					}
					if(getSiteID(newUser, site)){
						if(!orgInfoTemp.getUid().equals(currentUser.getSiteUID())){
							newUser.setStatusDisplay(UploadFileErrorTypeEnum.SITE_ERROR,"院区错误;");
							return;
						}
						if(StringUtils.isNotEmpty(department)){
							if(getDepartmentID(newUser, department,isInsulate)){
								if(StringUtils.isNotEmpty(department2)){
									String tempDepartment=department+"-"+department2;
									if(getDepartmentIDParent(newUser, tempDepartment,isInsulate)){
										if(StringUtils.isNotEmpty(department3)){
											tempDepartment=department+"-"+department2+"-"+department3;
											if(getDepartmentIDParent(newUser, tempDepartment,isInsulate)){
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		else{
			//always
			if(getInstitutionID(newUser, institution)){
				if(getHospitalID(newUser, hospital)){
					if(getSiteID(newUser, site)){
						if(StringUtils.isNotEmpty(department)){
							if(getDepartmentID(newUser, department,isInsulate)){
								if(StringUtils.isNotEmpty(department2)){
									String tempDepartment=department+"-"+department2;
									if(getDepartmentIDParent(newUser, tempDepartment,isInsulate)){
										if(StringUtils.isNotEmpty(department3)){
											tempDepartment=department+"-"+department2+"-"+department3;
											if(getDepartmentIDParent(newUser, tempDepartment,isInsulate)){
											}
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
		
	}
	
	private boolean getInstitutionID(UserAccount newUser,String institution){
		OrgInfo temp=getOrgInfoByInstitutionKey(institution, orgInfoTemp.getUid());
		if(temp==null){
			if(StringUtils.isNotEmpty(institution)){
				newUser.setStatusDisplay(UploadFileErrorTypeEnum.INSTITUTION_ERROR,"集团错误;");
				return false;
			}
		}
		else{
			orgInfoTemp=temp;
		}
		newUser.setInstitutionUID(orgInfoTemp.getUid());
		newUser.setOrgLevel(orgInfoTemp.getOrgLevel());
		newUser.setOrgType(orgInfoTemp.getOrgType());
		return true;
	}
	private boolean getHospitalID(UserAccount newUser,String hospital){
		OrgInfo temp=getOrgInfoByHospitalKey(hospital, orgInfoTemp.getUid());
		if(temp==null){
			if(StringUtils.isNotEmpty(hospital)){
				newUser.setStatusDisplay(UploadFileErrorTypeEnum.HOSPITAL_ERROR,"医院错误;");
				return false;
			}
		}
		else{
			orgInfoTemp=temp;
		}
		newUser.setHospitalUID(orgInfoTemp.getUid());
		newUser.setOrgLevel(orgInfoTemp.getOrgLevel());
		newUser.setOrgType(orgInfoTemp.getOrgType());
		return true;
	}
	private boolean getSiteID(UserAccount newUser,String site){
		OrgInfo temp=getOrgInfoBySiteKey(site, orgInfoTemp.getUid());
		if(temp==null){
			if(StringUtils.isNotEmpty(site)){
				newUser.setStatusDisplay(UploadFileErrorTypeEnum.SITE_ERROR,"院区错误;");
				return false;
			}
		}
		else{
			orgInfoTemp=temp;
		}
		newUser.setSiteUID(orgInfoTemp.getUid());
		newUser.setHospitalId(orgInfoTemp.getId());
		newUser.setOrgLevel(orgInfoTemp.getOrgLevel());
		newUser.setOrgType(orgInfoTemp.getOrgType());
		return true;
	}
	private boolean getDepartmentID(UserAccount newUser,String department,boolean isInsulate){
		OrgInfo temp=getOrgInfoByDepartmentKey(department, orgInfoTemp.getUid());
		if(temp==null){
			if(StringUtils.isNotEmpty(department)){
				newUser.setStatusDisplay(UploadFileErrorTypeEnum.DEPARTMENT_ERROR,"科室错误;");
				return false;
			}
		}
		else{
			orgInfoTemp=temp;
		}
		newUser.setOrgLevel(orgInfoTemp.getOrgLevel());
		newUser.setOrgType(orgInfoTemp.getOrgType());
		return true;
	}
	
	private boolean getDepartmentIDParent(UserAccount newUser,String department,boolean isInsulate){
		OrgInfo temp=getOrgInfoByDepartmentKeyParent(department, orgInfoTemp.getUid());
		if(temp==null){
			if(StringUtils.isNotEmpty(department)){
				newUser.setStatusDisplay(UploadFileErrorTypeEnum.DEPARTMENT_ERROR,"科室错误;");
				return false;
			}
		}
		else{
			orgInfoTemp=temp;
		}
		newUser.setOrgLevel(orgInfoTemp.getOrgLevel());
		newUser.setOrgType(orgInfoTemp.getOrgType());
		return true;
	}
	
	private boolean supplementUserRole(UserAccount user){
		String roleName=user.getImportRoleNames();
		List<UserRole> listRole=new ArrayList<UserRole>();
		if(StringUtils.isEmpty(roleName)){
			user.setStatusDisplay(UploadFileErrorTypeEnum.ROLE,"角色不能为空;");
			return false;
		}
		else{
			String roleNames[]=roleName.split("[,， ]");
			for(int i=0;i<roleNames.length;++i){
				String tempRoleName=roleNames[i];
				if(StringUtils.isEmpty(tempRoleName)){
					continue;
				}
				if(sysRoleMap.containsKey(tempRoleName)){
					UserRole newRole=new UserRole();
					newRole.setRoleId(sysRoleMap.get(tempRoleName));
					newRole.setUserId(user.getId());
					listRole.add(newRole);
				}
				else{
					user.setStatusDisplay(UploadFileErrorTypeEnum.ROLE_NOT_EXISTS,"角色不存在;");
					return false;
				}
			}
		}
		user.setListImportUserRole(listRole);
		return true;
	}
	
	private boolean supplementUserRoleWithUserId(UserAccount user){
		List<UserRole> listUserRole=user.getListImportUserRole();
		if(listUserRole!=null && listUserRole.size()>0){
			for(int i=0;i<listUserRole.size();++i){
				listUserRole.get(i).setUserId(user.getId());
			}
		}
		return true;
	}
	
	private void supplementOriginUserOrgInfo(UserAccount user){
		List<UserOrgInfo> listUserOrgInfo=user.getListUserOrgInfo();
		if(listUserOrgInfo==null||listUserOrgInfo.size()==0){
			listUserOrgInfo=userOrgInfoRepository.findByUserUid(user.getUid());
			user.setListUserOrgInfo(listUserOrgInfo);
		}
	}
	
	private void supplementOriginUserRole(UserAccount user){
		List<UserRole> listUserRole=user.getUserRoleList();
		if(listUserRole==null||listUserRole.size()==0){
			listUserRole=userRoleRepository.findByUserId(user.getId());
			user.setUserRoleList(listUserRole);
		}
	}
	
	private Map<String,Integer>getRoleNameMap(){
		if(sysRoleMap!=null){
			return sysRoleMap;
		}
		Iterable<SysRole> listSysRole=sysRoleRepository.findAll();
		if(listSysRole!=null){
			sysRoleMap=new HashMap<String,Integer>();
			Iterator<SysRole> itSysRole=listSysRole.iterator();
			while(itSysRole.hasNext()){
				SysRole tempSysRole=itSysRole.next();
				sysRoleMap.put(tempSysRole.getRoleDesc(), tempSysRole.getId());
			}
		}
		return sysRoleMap;
	}
	
	private boolean addUserAccessScope(UserAccount user,OrgInfo tempOrgInfo,boolean showError){
		if(tempOrgInfo==null){
			if(showError){
				user.setStatusDisplay(UploadFileErrorTypeEnum.ACCESSSCOPE_NOT_EXISTS,"可见范围不存在;");
			}
			return false;
		}
		else{
			List<UserOrgInfo> listUserOrgInfo=user.getListImportUserOrgInfo();
			if(listUserOrgInfo==null){
				listUserOrgInfo=new ArrayList<UserOrgInfo>();
				user.setListImportUserOrgInfo(listUserOrgInfo);
			}
			UserOrgInfo tempUserOrgInfo=new UserOrgInfo();
			tempUserOrgInfo.setOrgType(4);
			tempUserOrgInfo.setOrgUid(tempOrgInfo.getUid());
			tempUserOrgInfo.setUserUid(user.getUid());
			listUserOrgInfo.add(tempUserOrgInfo);
		}
		return true;
	}
	
	private boolean supplementAccessScope(UserAccount user){
		if(StringUtils.isEmpty(user.getDepartName()) && StringUtils.isEmpty(user.getDepartName2()) && StringUtils.isEmpty(user.getDepartName3())){
			OrgInfo tempOrgInfo=null;
			String key="";
			if(StringUtils.isNotEmpty(user.getSiteName())){
				key=user.getSiteName()+user.getHospitalUID();
				if(mapOrgInfo.containsKey(key)){
					tempOrgInfo=mapOrgInfo.get(key);
				}
				else{
					tempOrgInfo=getOrgInfoBySiteKey(user.getSiteName(), user.getHospitalUID());
				}
				if(addUserAccessScope(user, tempOrgInfo,true)==false){
					return false;
				}
			}
			else if(StringUtils.isNotEmpty(user.getHospitalName())){
				key=user.getHospitalName()+user.getInstitutionUID();
				if(mapOrgInfo.containsKey(key)){
					tempOrgInfo=mapOrgInfo.get(key);
				}
				else{
					tempOrgInfo=getOrgInfoByHospitalKey(user.getHospitalName(), user.getInstitutionUID());
				}
				if(addUserAccessScope(user, tempOrgInfo,true)==false){
					return false;
				}
			}
			else if(StringUtils.isNotEmpty(user.getInstitutionName())){
				key=user.getInstitutionName()+user.getTenantUID();
				if(mapOrgInfo.containsKey(key)){
					tempOrgInfo=mapOrgInfo.get(key);
				}
				else{
					tempOrgInfo=getOrgInfoByHospitalKey(user.getInstitutionName(), user.getTenantUID());
				}
				if(addUserAccessScope(user, tempOrgInfo,true)==false){
					return false;
				}
			}
		}
		else{
			String accessScope=user.getAccessScope();
			if(StringUtils.isEmpty(accessScope)){
				if(!isCreate){
					return true;
				}
				accessScope="";
			}
			String defaultName="";
			if(StringUtils.isNotEmpty(user.getDepartName())){
				defaultName+=user.getDepartName();
				if(StringUtils.isNotEmpty(user.getDepartName2())){
					defaultName+="-"+user.getDepartName2();
					if(StringUtils.isNotEmpty(user.getDepartName3())){
						defaultName+="-"+user.getDepartName3();
					}
				}
			}
			accessScope+=","+defaultName;
			String[] arrUserOrgInfo=accessScope.split("[,， ]");
			Set<String> setAccessScope=new HashSet<String>();
			for(int i=0;i<arrUserOrgInfo.length;++i){
				if(StringUtils.isEmpty(arrUserOrgInfo[i])){
					continue;
				}
				setAccessScope.add(arrUserOrgInfo[i]);
			}
			Iterator<String> tempIt=setAccessScope.iterator();
			while(tempIt.hasNext()){
				String tempStr=tempIt.next();
				String key=tempStr+user.getSiteUID();
				OrgInfo tempOrgInfo=null;
				if(mapOrgInfo.containsKey(key)){
					tempOrgInfo=mapOrgInfo.get(key);
				}
				else{
					getOrgInfoByDepartmentKey(setAccessScope, user.getSiteUID());
					tempOrgInfo=mapOrgInfo.get(key);
				}
				if(key.equals(defaultName)){
					if(addUserAccessScope(user,tempOrgInfo,false)==false){
						return false;
					}
				}
				else{
					if(addUserAccessScope(user,tempOrgInfo,true)==false){
						return false;
					}
				}
			}
		}
		return true;
	}
	
	private void initParam(){
		mapOrgInfo.clear();
		mapUidOrgInfo.clear();
		mapUidTenantInfo.clear();
	}
	
	public List<UserAccount> handleExcel(UploadedFile file, UserAccount user) throws IOException, ParseException {
//		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		initParam();
		getRoleNameMap();
		currentUser=user;
		TenantInfo tenantInfo=tenantInfoRepository.findByuid(user.getTenantUID());
		if(tenantInfo!=null && tenantInfo.getInsulate()!=null){
			isInsulate=tenantInfo.getInsulate();
			if(isInsulate){
				if(!UserContextService.isOnlyLocalITAdmin()){
					isInsulate=false;
				}
			}
		}
		List<UserAccount> importFailedDataList = new ArrayList<UserAccount>();
		
		Workbook workbook = null;
		if(file.getFileName().toLowerCase().endsWith(".xls")){
			workbook=new HSSFWorkbook(file.getInputstream());
		}
		else if(file.getFileName().toLowerCase().endsWith(".xlsx")){
			workbook=new XSSFWorkbook(file.getInputstream());
		}
		
		Sheet sheet = workbook.getSheetAt(0);
		if (sheet == null) {
			return null;
		}
		List<String> headers = new ArrayList<>();
		headers.add(HEADER_INSTITUTION);
		headers.add(HEADER_HOSPITAL);
		headers.add(HEADER_SITE);
		headers.add(HEADER_DEPARTMENT);
		headers.add(HEADER_DEPARTMENT2);
		headers.add(HEADER_DEPARTMENT3);
		headers.add(HEADER_LOGINNAME);
		headers.add(HEADER_NAME);
		headers.add(HEADER_EMAIL);
		headers.add(HEADER_TELEPHONE);
		headers.add(HEADER_ISACTIVE);
		headers.add(HEADER_ISLOCKED);
		headers.add(HEADER_ROLENAME);
		headers.add(HEADER_ACCESSSCOPE);
		headers.add(HEADER_UUID);
		Map<String, Integer> header = ExcelParser.findHeader(sheet,headers);
		if (header.isEmpty()) {
			return null;
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
		String roleName;
		String accessScope;
		String UUID;

		int institutionIndex=header.get(HEADER_INSTITUTION);
		int hospitalIndex=header.get(HEADER_HOSPITAL);
		int siteIndex=header.get(HEADER_SITE);
		int departmentIndex=header.get(HEADER_DEPARTMENT);
		int departmentIndex2=header.get(HEADER_DEPARTMENT2);
		int departmentIndex3=header.get(HEADER_DEPARTMENT3);
		int loginnameIndex=header.get(HEADER_LOGINNAME);
		int nameIndex=header.get(HEADER_NAME);
		int emailIndex=header.get(HEADER_EMAIL);
		int telephoneIndex=header.get(HEADER_TELEPHONE);
		int isactiveIndex=header.get(HEADER_ISACTIVE);
		int islockedIndex=header.get(HEADER_ISLOCKED);
		int rolenameIndex=header.get(HEADER_ROLENAME);
		int accessScopeIndex=header.get(HEADER_ACCESSSCOPE);
		int uuidIndex=header.get(HEADER_UUID);

		for (int j = header.get("HEADER_ROW") + 1; j <= sheet.getLastRowNum(); j++) {
			Row row = sheet.getRow(j);
			if (row == null) {
				continue;
			}
			boolean pass=true;
			UserAccount newUser = new UserAccount();			
			try {
				newUser.setExcelIdex(j + 1);
				newUser.setTenantUID(user.getTenantUID());
				institution=ExcelParser.getCellStringValue(row, institutionIndex);
				hospital=ExcelParser.getCellStringValue(row, hospitalIndex);
				site=ExcelParser.getCellStringValue(row, siteIndex);
				department=ExcelParser.getCellStringValue(row, departmentIndex);
				department2=ExcelParser.getCellStringValue(row, departmentIndex2);
				department3=ExcelParser.getCellStringValue(row, departmentIndex3);
				loginName=ExcelParser.getCellStringValue(row, loginnameIndex);
				if(loginName!=null && loginName.length()>0){
					boolean canParseDouble=false;
					try {
						Double.parseDouble(loginName);
						canParseDouble=true;
					} catch (Exception e) {
						// TODO: handle exception
						canParseDouble=false;
					}
					if(canParseDouble){
						if(loginName.indexOf(".")>0){
							loginName=loginName.substring(0,loginName.indexOf("."));
						}
					}
				}
				name=ExcelParser.getCellStringValue(row, nameIndex);
				email=ExcelParser.getCellStringValue(row, emailIndex);
				telephone=ExcelParser.getCellStringValue(row, telephoneIndex);
				String strActive=ExcelParser.getCellStringValue(row, isactiveIndex);
				if(StringUtils.isNotEmpty(strActive)&& strActive.equals("是")){
					isActive=true;
				}
				else{
					isActive=false;
				}
				String strLocked=ExcelParser.getCellStringValue(row, islockedIndex);
				if(StringUtils.isNotEmpty(strLocked)&& strLocked.equals("否")){
					isLocked=false;
				}
				else{
					isLocked=true;
				}
				roleName=ExcelParser.getCellStringValue(row, rolenameIndex);
				accessScope=ExcelParser.getCellStringValue(row, accessScopeIndex);
				UUID=ExcelParser.getCellStringValue(row, uuidIndex);
				
				if(!pass){
					importFailedDataList.add(newUser);
					failedCount++;
					continue;
				}
				newUser.setLoginName(loginName);
				newUser.setName(name);
				newUser.setEmail(email);
				if(StringUtils.isNotEmpty(telephone)){
					if(StringUtils.contains(telephone, "E")){
						BigDecimal bd = new BigDecimal(telephone);
				        telephone=bd.toPlainString();
					}
				}
				newUser.setTelephone(telephone);
				newUser.setIsActive(isActive);
				if(isActive){
					newUser.setActiveTime(new Date());
				}
				newUser.setIsLocked(isLocked);
//				newUser.setUserRoleList(userRoleList);
				newUser.setUid(UUID);
				newUser.setInstitutionName(institution);
				newUser.setHospitalName(hospital);
				newUser.setSiteName(site);
				newUser.setDepartName(department);
				newUser.setDepartName2(department2);
				newUser.setDepartName3(department3);
				newUser.setAccessScope(accessScope);
				newUser.setImportRoleNames(roleName);
				
				if(StringUtils.isEmpty(institution)){
					newUser.setStatusDisplay(UploadFileErrorTypeEnum.INSTITUTION,"所属集团不能为空;");
					pass=false;
				}
				
				if(StringUtils.isEmpty(loginName)){
					newUser.setStatusDisplay(UploadFileErrorTypeEnum.LOGINNAME,"登录名不能为空;");
					pass=false;
				}
				if(StringUtils.isEmpty(name)){
					newUser.setStatusDisplay(UploadFileErrorTypeEnum.NAME,"姓名不能为空;");
					pass=false;
				}
				if(StringUtils.isEmpty(strActive)){
					newUser.setStatusDisplay(UploadFileErrorTypeEnum.IS_ACTIVED,"激活值不能为空;");
					pass=false;
				}
				if(StringUtils.isEmpty(strLocked)){
					newUser.setStatusDisplay(UploadFileErrorTypeEnum.IS_LOCKED,"锁定值不能为空;");
					pass=false;
				}
				if (StringUtils.isNotEmpty(email) && !emailValidator.isValid(email)) {
					newUser.setEmail(null);
//                    newUser.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR,"邮件地址无效");
                }
				if(StringUtils.isEmpty(roleName)){
					newUser.setStatusDisplay(UploadFileErrorTypeEnum.ROLE,"角色不能为空;");
					pass=false;
				}
				
				if(handleNewUser(newUser, institution, hospital, site, department,department2,department3)==false){
					importFailedDataList.add(newUser);
					failedCount++;
				}
				else{
					successCount++;
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				newUser.setStatusDisplay(UploadFileErrorTypeEnum.FORMAT_ERROR,"格式错误;");
				importFailedDataList.add(newUser);
				failedCount++;
			}
		}
		addWarnMessage();
		return importFailedDataList;
	}
	
	private void addWarnMessage(){
		FacesMessage message=null;
		message = new FacesMessage(FacesMessage.SEVERITY_WARN, "处理完毕!", "总共"+(successCount+failedCount)+"条记录，"+successCount+"条处理成功，"+failedCount+"条失败！");
        FacesContext.getCurrentInstance().addMessage(null, message);
        successCount=0;
        failedCount=0;
        existsCount=0;
	}
	
	@Transactional
	public void createUser(UserAccount user) {
		if(user.getId()!=null){
			updateUser(user);
			return;
		}
		user.setPlainPassword(PasswordUtil.DEFAULT_PWD);
		//TODO nickName
		try {
			user.setCreatedBy(currentUser.getLoginName());
			user.setCreatedDate(new Date());
			user.setLastModifiedBy(user.getCreatedBy());
			user.setLastModifiedDate(user.getCreatedDate());
			user.setPasswordErrorCount(0);
			user.entryptPassword();
			user.setPlainPassword(null);
			userAccountRepository.save(user);
			saveUserRole(user,true);
			saveUserOrgInfo(user, true);
		} catch (NoSuchAlgorithmException e) {
			logger.error("create user error : {}", e.getMessage());
		}
	}
	
	private void deleteUserRole(UserAccount user){
		List<UserRole> listUserRole=user.getUserRoleList();
		if(listUserRole!=null && listUserRole.size()>0){
			for(int i=0;i<listUserRole.size();++i){
				userAccountMapper.deleteUserRole(listUserRole.get(i));
			}
		}
	}
	
	private void saveUserRole(UserAccount user,boolean create){
		if(!create){
			//delete before save
			supplementOriginUserRole(user);
			deleteUserRole(user);
		}
		supplementUserRoleWithUserId(user);
		List<UserRole> listUserRole=user.getListImportUserRole();
		if(listUserRole!=null && listUserRole.size()>0){
			for(int i=0;i<listUserRole.size();++i){
				userAccountMapper.saveUserRole(listUserRole.get(i));
			}
		}
	}
	
	private void deleteUserOrgInfo(UserAccount user){
		List<UserOrgInfo> listUserOrgInfo=user.getListUserOrgInfo();
		if(listUserOrgInfo!=null && listUserOrgInfo.size()>0){
			userOrgInfoRepository.delete(listUserOrgInfo);
		}
	}
	
	private void saveUserOrgInfo(UserAccount user,boolean create){
		if(!create){
			supplementOriginUserOrgInfo(user);
			deleteUserOrgInfo(user);
		}
		List<UserOrgInfo> listUserOrgInfo=user.getListImportUserOrgInfo();
		if(listUserOrgInfo!=null && listUserOrgInfo.size()>0){
			userOrgInfoRepository.save(listUserOrgInfo);
		}
	}
	
	private void updateUser(UserAccount user) {
		//TODO nickName
		try {
			//save
			user.setLastModifiedBy(currentUser.getLoginName());
			user.setLastModifiedDate(new Date());
			userAccountMapper.updateUserAccount(user);
			saveUserRole(user,false);
			saveUserOrgInfo(user,false);
		} catch (Exception e) {
			logger.error("update user error : {}", e.getMessage());
		}
	}
	//not remove
	private void supplementAccessScope(List<UserAccount> listUserAccount,List<UserOrgInfo> listUserOrgInfo){
		Set<String> setAccessOrgUid=new HashSet<String>();
		Map<String,List<OrgInfo>> mapUserAccessScope=new HashMap<String,List<OrgInfo>>();
		Map<String,List<UserOrgInfo>> mapUserOrgInfo=new HashMap<String,List<UserOrgInfo>>();
		Map<String,Set<String>> mapUserAccessScopeUid=new HashMap<String,Set<String>>();
		List<OrgInfo> listOrgInfo=null;
		if(listUserOrgInfo!=null){
			for(int i=0;i<listUserOrgInfo.size();++i){
				UserOrgInfo tempUserOrg=listUserOrgInfo.get(i);
				String tempUserUid=tempUserOrg.getUserUid();
				if(mapUserOrgInfo.containsKey(tempUserUid)){
					mapUserOrgInfo.get(tempUserUid).add(tempUserOrg);
				}
				else{
					List<UserOrgInfo> tempListUserOrg=new ArrayList<UserOrgInfo>();
					tempListUserOrg.add(tempUserOrg);
					mapUserOrgInfo.put(tempUserUid, tempListUserOrg);
				}
			}
		}
		if(mapUserOrgInfo.size()>0){
			mapUserAccessScopeUid=supplementAccessOrgUid(mapUserOrgInfo);
			if(mapUserAccessScopeUid.size()>0){
				for(Set<String> tempSet: mapUserAccessScopeUid.values()){
					setAccessOrgUid.addAll(tempSet);
				}
			}
		}
		if(setAccessOrgUid.size()>0){
			listOrgInfo=orgInfoRepository.findByUids(setAccessOrgUid);
		}
		if(listOrgInfo!=null){
			Iterator<String> tempIt=mapUserAccessScopeUid.keySet().iterator();
			while(tempIt.hasNext()){
				String key=tempIt.next();
				Set<String> setValue=mapUserAccessScopeUid.get(key);
				if(setValue!=null){
					Iterator<String> tempInnerIt=setValue.iterator();
					List<OrgInfo> listTempOrgInfo=new ArrayList<OrgInfo>();
					while(tempInnerIt.hasNext()){
						String tempAccessUid=tempInnerIt.next();
						if(StringUtils.isNotEmpty(tempAccessUid)){
							for(int i=0;i<listOrgInfo.size();++i){
								OrgInfo tempOrgInfo=listOrgInfo.get(i);
								String tempOrgUid=tempOrgInfo.getUid();
								if(tempAccessUid.equals(tempOrgUid)){
									listTempOrgInfo.add(tempOrgInfo);
								}
							}
						}
					}
					mapUserAccessScope.put(key, listTempOrgInfo);
				}
			}
		}
		if(mapUserAccessScope.size()>0){
			for(int i=0;i<listUserAccount.size();++i){
				UserAccount tempUser=listUserAccount.get(i);
				String tempUserUid=tempUser.getUid();
				Iterator<String> tempIt=mapUserAccessScope.keySet().iterator();
				while(tempIt.hasNext()){
					String tempKey=tempIt.next();
					if(tempUserUid.equals(tempKey)){
						tempUser.setListOrgInfo(mapUserAccessScope.get(tempKey));
					}
				}
			}
		}
	}
	
	//don't remove
	private Map<String,Set<String>> supplementAccessOrgUid(Map<String,List<UserOrgInfo>> mapUserOrgInfo){
		Map<String,Set<String>> mapResult=new HashMap<String,Set<String>>();
		if(mapUserOrgInfo.size()>0){
			Iterator<String> it=mapUserOrgInfo.keySet().iterator();
			while(it.hasNext()){
				String key=it.next();
				List<UserOrgInfo> value=mapUserOrgInfo.get(key);
				if(value!=null){
					//remove
//					if(value.size()==1){
//						UserOrgInfo tempUserOrg=value.get(0);
//						if(tempUserOrg.getOrgType()!=4){
//							Set<String> deepOrgUid=orgInfoRepository.findClinicalDeptOrgUidListByOrgUid(tempUserOrg.getOrgUid());
//							if(deepOrgUid!=null){
//								mapResult.put(key, deepOrgUid);
//							}
//							//all access
////							else{
////								deepOrgUid=new HashSet<String>();
////								mapResult.put(key, deepOrgUid);
////							}
////							deepOrgUid.add(tempUserOrg.getOrgUid());
//						}
//						else{
//							mapResult.put(key, convertUserOrgToUid(value));
//						}
//					}
//					else{
//						mapResult.put(key, convertUserOrgToUid(value));
//					}
					mapResult.put(key, convertUserOrgToUid(value));
				}
			}
		}
		return mapResult;
	}
	
	private Set<String> convertUserOrgToUid(List<UserOrgInfo> listUserOrg){
		Set<String> result=new HashSet<String>();
		if(listUserOrg!=null){
			for(int i=0;i<listUserOrg.size();++i){
				result.add(listUserOrg.get(i).getOrgUid());
			}
		}
		return result;
	}
	
	private String getOrgInfoName(List<OrgInfo> listOrgInfo){
		String result="";
		if(listOrgInfo!=null){
			for(int i=0;i<listOrgInfo.size();++i){
				String name=listOrgInfo.get(i).getName();
				if(StringUtils.isNotEmpty(name)){
					result+=name+",";
				}
			}
		}
		if(result.length()>0){
			result=result.substring(0, result.length()-1);
		}
		return result;
	}
}
