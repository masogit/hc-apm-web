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
}
