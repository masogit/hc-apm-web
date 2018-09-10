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
public class UserAccountService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static final String SHEET_USER="datalist";
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

	@Autowired
	UserOrgInfoRepository userOrgDao;
	
	@Autowired
	UserOrgInfoRepository userOrgInfoRepository;
	
	@Autowired
	SysRoleRepository sysRoleRepository;
	
	@Autowired
	UserRoleRepository userRoleRepository;

	EmailValidator emailValidator = EmailValidator.getInstance();

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
	private boolean supplementUserRoleWithUserId(UserAccount user){
		List<UserRole> listUserRole=user.getUserRoleList();
		if(listUserRole!=null && listUserRole.size()>0){
			for(int i=0;i<listUserRole.size();++i){
				listUserRole.get(i).setUserId(user.getId());
			}
		}
		return true;
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
			userRoleRepository.delete(listUserRole);
		}
	}
	
	private void saveUserRole(UserAccount user,boolean create){
		if(!create){
			deleteUserRole(user);
		}
		if(create){
			supplementUserRoleWithUserId(user);
		}
		List<UserRole> listUserRole=user.getUserRoleList();
		if(listUserRole!=null && listUserRole.size()>0){
			userRoleRepository.save(listUserRole);
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
			deleteUserOrgInfo(user);
		}
		List<UserOrgInfo> listUserOrgInfo=user.getListUserOrgInfo();
		if(listUserOrgInfo!=null && listUserOrgInfo.size()>0){
			userOrgInfoRepository.save(listUserOrgInfo);
		}
	}
	
	private void updateUser(UserAccount user) {
		//TODO nickName
		try {
			user.setLastModifiedBy(currentUser.getLoginName());
			user.setLastModifiedDate(new Date());
			userAccountMapper.updateUserAccount(user);
			saveUserRole(user,false);
			saveUserOrgInfo(user,false);
		} catch (Exception e) {
			logger.error("update user error : {}", e.getMessage());
		}
	}

	public String getLoginNameByUid(String uid) {
		return Optional.ofNullable(userAccountRepository.getByUid(uid).getName()).orElse("");
	}

	@Transactional
	public void updateUserOrg(String userUid, Set<OrgInfo> selectedOrgList) {

		userOrgDao.deleteByUserUid(userUid);

		List<UserOrgInfo> userOrgList = new ArrayList();
		for (OrgInfo selectedOrg : selectedOrgList) {
			UserOrgInfo userOrg = new UserOrgInfo();
			userOrg.setOrgType(selectedOrg.getOrgType());
			userOrg.setOrgUid(selectedOrg.getUid());
			userOrg.setUserUid(userUid);
			userOrgList.add(userOrg);
		}

		userOrgDao.save(userOrgList);
	}
	
	public void supplementUserAccount(List<UserAccount> listUserAccount){
		if(listUserAccount!=null && listUserAccount.size()>0){
			Set<String> setUserUid=new HashSet<String>();
			Set<String> setUserOrgUid=new HashSet<String>();
			
			List<UserOrgInfo> listUserOrgInfo=null;
			List<OrgInfo> listSelfOrgInfo=null;
			
			for(int i=0;i<listUserAccount.size();++i){
				UserAccount tempUser=listUserAccount.get(i);
				setUserUid.add(tempUser.getUid());
				setUserOrgUid.add(tempUser.getOrgInfoUID());
			}
			if(setUserUid.size()>0){
				listUserOrgInfo=userOrgInfoRepository.findOrgUidListByUserUid(setUserUid);
			}
			if(setUserOrgUid.size()>0){
				listSelfOrgInfo=orgInfoRepository.findByUids(setUserOrgUid);
			}
			if(listSelfOrgInfo!=null){
				for(int j=0;j<listSelfOrgInfo.size();++j){
					for(int i=0;i<listUserAccount.size();++i){
						UserAccount tempUser=listUserAccount.get(i);
						OrgInfo tempOrgInfo=listSelfOrgInfo.get(j);
						String tempOrgUid=tempUser.getOrgInfoUID();
						if(tempOrgUid.equals(tempOrgInfo.getUid())){
							tempUser.setOrgInfo(tempOrgInfo);
						}
					}
				}
			}
		}
	}
	
	public Boolean exportData(ExcelDocument doc, List<Map<String, Object>> data) {

		Integer keyRow = 1;
		Integer dataRow = 2;

		for (Map<String, Object> item : data) {
			doc.writeRowData(SHEET_USER, keyRow, dataRow++, item);
		}

		return Boolean.TRUE;
	}
	
	public List<Map<String, Object>> parseUserAccountToMap(List<UserAccount> listUserAccount) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Map<String, Object>> listMapResult = new ArrayList<Map<String, Object>>();
		if (listUserAccount != null && listUserAccount.size() > 0) {
			for (int i = 0; i < listUserAccount.size(); ++i) {
				UserAccount tempUser = listUserAccount.get(i);
				Map<String, Object> tempMap = new HashMap<String, Object>();
				String orgFullName=tempUser.getOrgFullName();
				String orgDepartName=tempUser.getDepartName();
				if(StringUtils.isNotEmpty(tempUser.getInstitutionName())){
					tempMap.put("所属集团", tempUser.getInstitutionName());
				}
				if(StringUtils.isNotEmpty(tempUser.getHospitalName())){
					tempMap.put("所属医院*", tempUser.getHospitalName());
				}
				if(StringUtils.isNotEmpty(tempUser.getSiteName())){
					tempMap.put("所属院区", tempUser.getSiteName());
				}
				if(StringUtils.isNotEmpty(orgDepartName)){
					String[] departNameArr=orgDepartName.split("-");
					if(departNameArr.length>0){
						tempMap.put("科室1", departNameArr[0]);
					}
					if(departNameArr.length>1){
						tempMap.put("科室2", departNameArr[1]);
					}
					if(departNameArr.length>2){
						tempMap.put("科室3", departNameArr[2]);
					}
				}
				tempMap.put("登录用户名*", tempUser.getLoginName());
				tempMap.put("姓名*", tempUser.getName());
				tempMap.put("Email", tempUser.getEmail());
				tempMap.put("电话", tempUser.getTelephone());
				tempMap.put("激活?*", tempUser.getIsActive()?"是":"否");
				tempMap.put("锁定?*", tempUser.getIsLocked()?"是":"否");
				tempMap.put("角色*", tempUser.getUserRoleNames());
				tempMap.put("可见范围（科室）", getOrgInfoName(tempUser.getListOrgInfo()));
				tempMap.put("UUID", tempUser.getUid());
				listMapResult.add(tempMap);
			}
		}
		return listMapResult;
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
