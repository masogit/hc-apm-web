package com.ge.apm.service.uaa;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.*;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
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
import com.ge.apm.service.utils.ExcelParser;
import com.ge.apm.service.utils.PasswordUtil;
import com.ge.apm.view.sysutil.UserContextService;

@Component
public class UserAccountService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static final String HEADER_INSTITUTION="集团";
	private static final String HEADER_HOSPITAL="医院";
	private static final String HEADER_SITE="院区";
	private static final String HEADER_DEPARTMENT="科室";
	private static final String HEADER_LOGINNAME="登录用户名";
	private static final String HEADER_NAME="姓名";
	private static final String HEADER_EMAIL="Email";
	private static final String HEADER_TELEPHONE="电话";
	private static final String HEADER_ISACTIVE="激活?";
	private static final String HEADER_ISLOCKED="锁定?";
	private static final String HEADER_ROLENAME="角色";
	private static final String HEADER_UUID="UUID";
	private OrgInfo orgInfoTemp;
	private int successCount;
	private int failedCount;
	private int existsCount;
	private int recordCount;
	boolean isInsulate=false;
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

	public List<UserAccount> getUserAccount() {
		return userAccountMapper.getAllUser();
	};

	public UserAccount getUserById(Integer id) {
		return userAccountMapper.getUserById(id);
	}

	public UserModel getUserAccoutByUserId(Integer userId) {
		UserModel user = userAccountMapper.getUserAccoutByUserId(userId);
		if (user == null) {
			return null;
		}
		List<SysRole> roles = userAccountMapper.getUserRoles(user.getUserId());
		if (CollectionUtils.isEmpty(roles)) {
			logger.error("{} has none role,user id is {}", user.getLoginName(), user.getUserId());
			return user;
		}
		StringBuilder sb = new StringBuilder();
		for (SysRole sysRole : roles) {
			sb.append(sysRole.getRoleDesc() + ",");
		}
		String role = sb.toString();
		if (role.endsWith(",")) {
			role = role.substring(0, role.length() - 1);
		}
		user.setUserRole(role);
		return user;
	}

	public boolean isExists(String openId) {
		return userAccountMapper.getUserByWechatId(openId) != null ? true : false;
	}
	public boolean isExistsByLoginName(String loginName) {
		return userAccountMapper.getUserByLoginName(loginName) != null ? true : false;
	}
	private boolean checkData(UserAccount newUser){
		boolean pass=true;
		if(StringUtils.isNotEmpty(newUser.getStatus())){
			pass=false;
		}
		return pass;
	}
	private boolean handleNewUser(UserAccount newUser,String institution,String hospital,String site,String department){
		checkLoginName(newUser);
		if(checkData(newUser)){
			setUIDs(newUser, institution, hospital, site, department);
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
			if(isExistsByLoginName(loginName)){
				existsCount++;
				newUser.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR,"用户名已存在");
				return newUser;
			}
			//create finally
		}
		else{
			//update
			UserAccount user=userAccountRepository.getByUid(uuid);
			if(user==null){
				newUser.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR,"用户不存在;");
				return newUser;
			}
			else{
				if(!user.getLoginName().equals(loginName)){
					if(isExistsByLoginName(loginName)){
						newUser.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR,"用户名已存在;");
						return newUser;
					}
				}
			}
			//update finally
			newUser.setUid(user.getUid());
			newUser.setId(user.getId());
			newUser.setPasswordErrorCount(user.getPasswordErrorCount());
			return newUser;
		}
		return newUser;
	}
	private void setUIDs(UserAccount newUser,String institution,String hospital,String site,String department){
		orgInfoTemp=orgInfoRepository.getByUid(currentUser.getTenantUID());
		TenantInfo tenantInfo=tenantInfoRepository.findByuid(currentUser.getTenantUID());
		if(orgInfoTemp==null || tenantInfo==null){
			newUser.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR,"集团不存在;");
			return;
		}
		newUser.setSiteId(tenantInfo.getId());
		if(isInsulate){
			newUser.setInstitutionUID(currentUser.getInstitutionUID());
			newUser.setHospitalUID(currentUser.getHospitalUID());
			newUser.setSiteUID(currentUser.getSiteUID());
			orgInfoTemp=orgInfoRepository.getByUid(currentUser.getHospitalUID());
			orgInfoTemp=orgInfoRepository.getByUid(currentUser.getSiteUID());
			newUser.setOrgLevel(orgInfoTemp.getOrgLevel());
			newUser.setOrgType(orgInfoTemp.getOrgType());
			if(getDepartmentID(newUser, department,isInsulate)){
			}
		}
		else{
			if(getInstitutionID(newUser, institution)){
				if(getHospitalID(newUser, hospital)){
					if(getSiteID(newUser, site)){
						if(getDepartmentID(newUser, department,isInsulate)){
						}
					}
				}
			}
		}
		newUser.setOrgInfoUID(orgInfoTemp.getUid());
		newUser.setOrgInfoId(orgInfoTemp.getId());
		
	}
	
	private boolean getInstitutionID(UserAccount newUser,String institution){
		OrgInfo temp=orgInfoRepository.getInstitutionByName(institution,orgInfoTemp.getUid());
		if(temp==null){
			if(StringUtils.isNotEmpty(institution)){
				newUser.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR,"集团错误;");
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
		OrgInfo temp=orgInfoRepository.getHospitalByName(hospital,orgInfoTemp.getUid());
		if(temp==null){
			if(StringUtils.isNotEmpty(hospital)){
				newUser.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR,"医院错误;");
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
		OrgInfo temp=orgInfoRepository.getSiteByName(site,orgInfoTemp.getUid());
		if(temp==null){
			if(StringUtils.isNotEmpty(site)){
				newUser.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR,"院区错误;");
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
		
		OrgInfo temp=orgInfoRepository.getDepartmentByName(department,orgInfoTemp.getUid());
		if(temp==null){
			if(StringUtils.isNotEmpty(department)){
				newUser.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR,"科室错误;");
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
	
	public List<UserAccount> handleExcel(UploadedFile file, UserAccount user) throws IOException, ParseException {
//		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
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
		headers.add(HEADER_LOGINNAME);
		headers.add(HEADER_NAME);
		headers.add(HEADER_EMAIL);
		headers.add(HEADER_TELEPHONE);
		headers.add(HEADER_ISACTIVE);
		headers.add(HEADER_ISLOCKED);
		headers.add(HEADER_ROLENAME);
		headers.add(HEADER_UUID);
		Map<String, Integer> header = ExcelParser.findHeader(sheet,headers);
		if (header.isEmpty()) {
			return null;
		}
		String institution;
		String hospital;
		String site;
		String department;
		String loginName;
		String name;
		String email;
		String telephone;
		boolean isActive;
		boolean isLocked;
		String roleName;
		String UUID;

		int institutionIndex=header.get(HEADER_INSTITUTION);
		int hospitalIndex=header.get(HEADER_HOSPITAL);
		int siteIndex=header.get(HEADER_SITE);
		int departmentIndex=header.get(HEADER_DEPARTMENT);
		int loginnameIndex=header.get(HEADER_LOGINNAME);
		int nameIndex=header.get(HEADER_NAME);
		int emailIndex=header.get(HEADER_EMAIL);
		int telephoneIndex=header.get(HEADER_TELEPHONE);
		int isactiveIndex=header.get(HEADER_ISACTIVE);
		int islockedIndex=header.get(HEADER_ISLOCKED);
		int rolenameIndex=header.get(HEADER_ROLENAME);
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
				
				if(StringUtils.isEmpty(loginName)){
					newUser.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR,"登录名不能为空;");
					pass=false;
				}
				if(StringUtils.isEmpty(name)){
					newUser.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR,"姓名不能为空;");
					pass=false;
				}
				if(StringUtils.isEmpty(strActive)){
					newUser.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR,"激活值不能为空;");
					pass=false;
				}
				if(StringUtils.isEmpty(strLocked)){
					newUser.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR,"锁定值不能为空;");
					pass=false;
				}
				if(handleNewUser(newUser, institution, hospital, site, department)==false){
					importFailedDataList.add(newUser);
					failedCount++;
				}
				else{
					successCount++;
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				newUser.setStatusDisplay(UploadFileErrorTypeEnum.FORMAT_ERROR,false);
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
	public UserAccount createUser(String name, String mobile, Integer assetId, String wechatId, String telephone,String nickName) {
		logger.info("create user ,name is {},mobile is {},assetId is {},wechatId is {},telephone is {}", name, mobile,assetId, wechatId, telephone);
		AssetInfo asset = assetInfoMapper.fetchAssetInfoById(assetId);
		if (asset != null) {
			UserAccount user = new UserAccount();
			user.setWeChatId(wechatId);
			user.setName(name);
			user.setLoginName(wechatId);
			user.setTelephone(telephone);
			user.setSiteId(asset.getSiteId());
			user.setHospitalId(asset.getHospitalId());
			user.setOrgInfoId(asset.getClinicalDeptId());
			user.setIsActive(true);
			user.setPlainPassword(PasswordUtil.DEFAULT_PWD);
			//TODO nickName
			
			try {
				user.entryptPassword();
				userAccountMapper.saveUserAccount(user);
				UserRole userRole=new UserRole();
				userRole.setRoleId(9);
				userRole.setUserId(user.getId());
				userAccountMapper.saveUserRole(userRole);
				user.setPlainPassword(null);
				return user;
			} catch (NoSuchAlgorithmException e) {
				logger.error("cannot find asset by id : {}", assetId);
			}
		}
		return null;
	}
	
	@Transactional
	public void createUser(UserAccount user) {
		if(StringUtils.isNotEmpty(user.getUid())){
			updateUser(user);
			return;
		}
		user.setUid(UUID.randomUUID().toString().replace("-", ""));
		user.setPlainPassword(PasswordUtil.DEFAULT_PWD);
		//TODO nickName
		try {
			user.setCreatedBy(currentUser.getLoginName());
			user.setCreatedDate(new Date());
			user.setLastModifiedBy(user.getCreatedBy());
			user.setLastModifiedDate(user.getCreatedDate());
			user.setPasswordErrorCount(0);
			user.entryptPassword();
			userAccountMapper.saveUserAccount(user);
			UserRole userRole=new UserRole();
			userRole.setRoleId(7);
			userRole.setUserId(user.getId());
			userAccountMapper.saveUserRole(userRole);
			UserOrgInfo userOrgInfo=new UserOrgInfo();
			userOrgInfo.setOrgType(user.getOrgType());
			userOrgInfo.setOrgUid(user.getOrgInfoUID());
			userOrgInfo.setUserUid(user.getUid());
			if(userOrgInfo.getId()==null){
				userOrgInfo.setId(UUID.randomUUID().toString().replace("-", ""));
			}
			userAccountMapper.saveUserOrgInfo(userOrgInfo);
			user.setPlainPassword(null);
		} catch (NoSuchAlgorithmException e) {
			logger.error("create user error : {}", e.getMessage());
		}
	}
	
	private void updateUser(UserAccount user) {
		//TODO nickName
		try {
			user.setLastModifiedBy(currentUser.getLoginName());
			user.setLastModifiedDate(new Date());
			userAccountMapper.updateUserAccount(user);
		} catch (Exception e) {
			logger.error("update user error : {}", e.getMessage());
		}
	}

	public String getLoginNameByUid(String uid) {
		return Optional.ofNullable(userAccountRepository.getByUid(uid).getName()).orElse("");
	}
}
