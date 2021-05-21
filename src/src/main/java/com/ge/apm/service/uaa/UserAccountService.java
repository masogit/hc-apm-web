package com.ge.apm.service.uaa;

import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.dao.mapper.UserTagsMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.PasswordUtil;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;

import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class UserAccountService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static org.slf4j.Logger log = LoggerFactory.getLogger(UserAccountService.class);

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

	@Autowired
	UserTagsMapper userTagsMapper;

	@Autowired
	UserTagsRepository userTagsRepository;

	@Autowired
	UserAccountHistoryService userAccountHistoryService;

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
	public boolean isSiteUser(UserAccount user) {
		if (!user.getSiteUID().equals(user.getHospitalUID())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isHospitalUser(UserAccount user) {
		if (!user.getHospitalUID().equals(user.getInstitutionUID())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isInstitutionUser(UserAccount user) {
		if (!user.getInstitutionUID().equals(user.getTenantUID())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isTenantUser(UserAccount user) {
		return !isInstitutionUser(user);
	}

	@Transactional
	public void updateUserOrg(String userUid, Set<OrgInfo> selectedOrgList,List<String> treeDeleteUrgOidList,int SiteId,List<String> unselectInstitutions,List<String> unselectUidss,boolean selectAllButton,OrgInfo orgInfo46,List<OrgInfo> orgInfoTableData46) {
		if(46==SiteId){
			if(selectAllButton){//如果为全选状态
				userOrgDao.deleteByUserUid(userUid);//清空该用户所有的权限
				List<UserOrgInfo> userOrgList = new ArrayList();
				UserOrgInfo userOrg = new UserOrgInfo();
				userOrg.setOrgType(orgInfo46.getOrgType());
				userOrg.setOrgUid(orgInfo46.getUid());
				userOrg.setUserUid(userUid);
				userOrgList.add(userOrg);
				userOrgDao.save(userOrgList);
				return;
			}else{
				if(orgInfoTableData46.size()>0){
					userOrgDao.deleteByUserUid(userUid);
					List<UserOrgInfo> userOrgList = new ArrayList();
					for (OrgInfo selectedOrg : orgInfoTableData46) {
						UserOrgInfo userOrg = new UserOrgInfo();
						userOrg.setOrgType(selectedOrg.getOrgType());
						userOrg.setOrgUid(selectedOrg.getUid());
						userOrg.setUserUid(userUid);
						userOrgList.add(userOrg);
					}
					userOrgDao.save(userOrgList);
					return;
				}else{
					//userOrgDao.deleteByUserUid46(userUid ,treeDeleteUrgOidList);
					//查询列表中所有的集团id 并删除集团id下的所有
					Set<String> institutionUIDs = new HashSet<String>();
					Iterator it = selectedOrgList.iterator();
					while (it.hasNext()) {
						OrgInfo str = (OrgInfo) it.next();
						institutionUIDs.add(str.getInstitutionUID());
					}
					List<String> institutionUIDList = new ArrayList<String>(institutionUIDs);
					institutionUIDList.addAll(unselectInstitutions);
					userOrgDao.deleteByUserUid46(userUid ,institutionUIDList);
					if(unselectUidss.size()>0){
						userOrgDao.deleteByUserUids46(userUid ,unselectUidss);
					}
				}

			}
		}else{
			userOrgDao.deleteByUserUid(userUid);
		}

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

	public String getUserLanguageByLoginName(String loginName) {
		return userAccountMapper.getUserLanguageByLoginName(loginName);
	}
	public UserAccount getUserByTelephone(String telephone) {
		return userAccountRepository.findByTelephone(telephone);
	}

	@Transactional
    public void deleteUserAccount(UserAccount userAccount){
		String userUid = userAccount.getUid();
		userTagsRepository.deleteByUserUid(userUid);
		userOrgDao.deleteByUserUid(userUid);
        userRoleRepository.deleteByUserAccount(userAccount.getId());
        userAccountRepository.deleteByUid(userUid);
		log.info("delete userAccount , uid: {}",userAccount.getUid());

		UserAccount currentUser = UserContextService.getCurrentUserAccount();
		userAccount.setLastModifiedBy(currentUser.getLoginName());
		userAccount.setLastModifiedDate(new Date());
		userAccountHistoryService.save(WebUtil.PersistAction.DELETE, userAccount);
	}

	public boolean whetherTheCallIsRepeated(String telephone,String userTelephone,Integer userId){
		if(null == telephone||telephone.equals(userTelephone)){//如果跟登陆电话一样则直接放行
			return false;
		}
		List<UserAccount> userAccounts = userAccountMapper.getUserAccountByTelephone(telephone);
		for (int i = 0; i < userAccounts.size(); i++) {
			if(userId==userAccounts.get(i).getId()){
				userAccounts.remove(i);//移除掉自己
			}
		}
		if(userAccounts.size()>0){//如果还有大于1条的数据则说明该手机几号存在
			return true;
		}else{
			return false;
		}
	}

	public List<String> getLoginNameBySiteId(Integer siteId){
		return userAccountMapper.getLoginNameBySiteId(siteId);
	}

}
