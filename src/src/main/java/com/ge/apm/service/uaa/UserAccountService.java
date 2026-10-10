package com.ge.apm.service.uaa;

import com.ge.apm.service.simpleparts.SimplePartsWarehouseUserService;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.dao.mapper.UserTagsMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.schedule.UserScheduleService;
import com.ge.apm.service.utils.PasswordUtil;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

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
	@Autowired
	UserScheduleService userScheduleService;
	@Autowired
	MeasuringTypeConfigRepository measuringTypeConfigRepository;
	@Autowired
	MessageSubscriberRepository messageSubscriberRepository;
	@Autowired
	AssetTagMsgSubscriberRepository assetTagMsgSubscriberRepository;
	@Autowired
	OwnerConfigRepository ownerConfigRepository;
	@Autowired
	WorkflowConfigRepository workflowConfigRepository;


	@Autowired
	private SimplePartsWarehouseUserService simplePartsWarehouseUserService;

	@Autowired
	UserAssetGroupRepository userAssetGroupDao;

	@Autowired
	private SiteToSiteAllocationRepository siteToSiteAllocationRepository;

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
	public void updateUserOrg(UserAccount userAccount, Set<OrgInfo> selectedOrgList,List<String> treeDeleteUrgOidList,List<String> unselectInstitutions,List<String> unselectUidss,boolean selectAllButton,OrgInfo orgInfo46,List<OrgInfo> orgInfoTableData46) {
		String userUid = userAccount.getUid();
        List<UserOrgInfo> userOrgInfos = new ArrayList();
        for (OrgInfo selectedOrg : selectedOrgList) {
            UserOrgInfo userOrg = new UserOrgInfo();
            userOrg.setOrgType(selectedOrg.getOrgType());
            userOrg.setOrgUid(selectedOrg.getUid());
            userOrg.setUserUid(userUid);
            userOrgInfos.add(userOrg);
        }
		if(Objects.equals(userAccount.getSiteId(), 46)){
			if(selectAllButton){//如果为全选状态
				List<UserOrgInfo> userOrgList = new ArrayList();
				UserOrgInfo userOrg = new UserOrgInfo();
				userOrg.setOrgType(orgInfo46.getOrgType());
				userOrg.setOrgUid(orgInfo46.getUid());
				userOrg.setUserUid(userUid);
				userOrgList.add(userOrg);
				userAccount.setListUserOrgInfo(userOrgList);
				userAccountHistoryService.save(UserAccount.HistoryAction.UPDATE_ORG.name(), userAccount);
				userOrgDao.deleteByUserUid(userUid);//清空该用户所有的权限
				userOrgDao.save(userOrgList);
				return;
			}else{
				if(orgInfoTableData46.size()>0){
					List<UserOrgInfo> userOrgList = new ArrayList();
					for (OrgInfo selectedOrg : orgInfoTableData46) {
						UserOrgInfo userOrg = new UserOrgInfo();
						userOrg.setOrgType(selectedOrg.getOrgType());
						userOrg.setOrgUid(selectedOrg.getUid());
						userOrg.setUserUid(userUid);
						userOrgList.add(userOrg);
					}
					userAccount.setListUserOrgInfo(userOrgList);
					userAccountHistoryService.save(UserAccount.HistoryAction.UPDATE_ORG.name(), userAccount);
					userOrgDao.deleteByUserUid(userUid);
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
                    userAccount.setListUserOrgInfo(userOrgInfos);
                    userAccountHistoryService.save(UserAccount.HistoryAction.UPDATE_ORG.name(), userAccount);
					userOrgDao.deleteByUserUid46(userUid ,institutionUIDList);
					if(unselectUidss.size()>0){
						userOrgDao.deleteByUserUids46(userUid ,unselectUidss);
					}
				}

			}
		}else{
            userAccount.setListUserOrgInfo(userOrgInfos);
            userAccountHistoryService.save(UserAccount.HistoryAction.UPDATE_ORG.name(), userAccount);
			userOrgDao.deleteByUserUid(userUid);
		}

		userOrgDao.save(userOrgInfos);
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
		userAssetGroupDao.deleteByUserUid(userAccount.getUid());


		UserAccount currentUser = UserContextService.getCurrentUserAccount();
		userAccount.setLastModifiedBy(currentUser.getLoginName());
		userAccount.setLastModifiedDate(new Date());
		userAccountHistoryService.save(UserAccount.HistoryAction.DELETE.name(), userAccount);
	}

	public boolean whetherTheCallIsRepeated(String telephone,UserAccount userAccount){
		if (ObjectUtil.isEmpty(telephone)) {
			return false;
		}
		EntityWrapper<UserAccount> wrapper = new EntityWrapper<>();
		wrapper.eq("telephone", telephone);
		wrapper.ne(ObjectUtil.isNotEmpty(userAccount.getId()), "id", userAccount.getId());
		wrapper.eq(telephone.length() < 11, "tenant_uid", UserContextService.getCurrentUserAccount().getTenantUID());
		wrapper.andNew().eq("is_deleted",false).or().isNull("is_deleted");
		return userAccountMapper.selectCount(wrapper) > 0;
	}

	public List<String> getLoginNameBySiteId(Integer siteId){
		return userAccountMapper.getLoginNameBySiteId(siteId);
	}

	public List<String> getLoginNameBySiteUid(String siteUid) {
		return userAccountMapper.getBySiteUid(siteUid).stream().map(UserAccount::getLoginName).distinct().collect(Collectors.toList());
	}

    public String getDefaultLanguage(String loginName) {
		UserAccount userAccount = userAccountRepository.findByLoginName(loginName);
		if(userAccount == null){
			return null;
		}
		if(StringUtils.isNotEmpty(userAccount.getDefaultLanguage())){
			return userAccount.getDefaultLanguage();
		}
		return null;
    }

	public boolean checkUser(UserAccount userAccount) {
		// 判断手机号码是否重复
		if (whetherTheCallIsRepeated(userAccount.getTelephone(), userAccount)) {
			WebUtil.addSuccessMessage(WebUtil.getMessage("ErrorDuplicatedData")+":"+WebUtil.getMessage("telephone"));
			return false;
		}
		return true;
	}

	public List<String> userAssociationVerification(UserAccount selected) {
		List<String> result=new ArrayList<>();
		isSparePartsAdmin(selected,result);
		isUserSchedule(selected,result);
		isMeasurement(selected,result);
		existSubscribeMessages(selected,result);
		isPersonLiable(selected,result);
		existWorkflowConfigPersonLiable(selected,result);
		existSiteToSiteAllocation(selected,result);
		logger.info("该用户为设备类型负责人,result:{}", JSONObject.toJSONString(result));
		return result;
	}
	private void existSiteToSiteAllocation(UserAccount selected,List<String> result){
		String uid = selected.getUid();
		List<SearchFilter> searchFilters=new ArrayList<>();
		searchFilters.add(new SearchFilter("borrowerUid", SearchFilter.Operator.EQ,uid,"1*2+"));
		searchFilters.add(new SearchFilter("returnerUid", SearchFilter.Operator.EQ,uid,"1*2+"));
		List<SiteToSiteAllocation> siteToSiteAllocations = siteToSiteAllocationRepository.findBySearchFilter(searchFilters);
		if(CollectionUtils.isNotEmpty(siteToSiteAllocations)){
			boolean exist = siteToSiteAllocations.stream().anyMatch(item -> {
				if (item.getReturnAt() == null) {
					return Boolean.TRUE;
				} else {
					return (StringUtils.isEmpty(item.getReturnerAck()) || StringUtils.isEmpty(item.getReceiverSignatureStorageId()) || StringUtils.isEmpty(item.getReturnerSignatureStorageId()));
				}
			});
			if(exist){
				result.add("用户有未完成的院间借调工单，禁止删除！");
			}
		}

	}
	private void  existWorkflowConfigPersonLiable(UserAccount selected,List<String> result){
		// TODO
		String siteUID = selected.getSiteUID();
		String hospitalUID = selected.getHospitalUID();
		String institutionUID = selected.getInstitutionUID();
		String tenantUID = selected.getTenantUID();
		List<String> uidList = Arrays.asList(siteUID, hospitalUID, institutionUID, tenantUID);
		List<WorkflowConfig> workflowConfig = workflowConfigRepository.findWorkflowConfig(uidList);
		if(CollectionUtils.isNotEmpty(workflowConfig)){
			Set<String> set=new HashSet<>();
			for (WorkflowConfig config : workflowConfig) {
				checkoutWorkflowConfig(config,selected,set);
			}
			if(!set.isEmpty()){
				result.addAll(set);
			}
		}
	}
	private void checkoutWorkflowConfig(WorkflowConfig item,UserAccount selected,Set<String> result){
		logger.info("该用户为设备类型负责人,config:{},user:{}", JSONObject.toJSONString(item),selected);
		Integer userID = selected.getId();
		String userUID = selected.getUid();
		Integer measureDispatchUserId = item.getMeasureDispatchUserId();
		Integer measureDispatchUserId2 = item.getMeasureDispatchUserId2();
		Integer pmDispatchUserId = item.getPmDispatchUserId();
		Integer pmDispatchUserId2 = item.getPmDispatchUserId2();
		String assetApprover = item.getAssetApprover();
		if(userID.equals(measureDispatchUserId))  result.add("该用户为计量派工人，不能操作！");
		if(userID.equals(measureDispatchUserId2)) result.add("该用户为计量派工人2，不能操作！");
		if(userID.equals(pmDispatchUserId))  result.add("该用户为保养派工人，不能操作！");
		if(userID.equals(pmDispatchUserId2)) result.add("该用户为保养派工人2，不能操作！");
		if(userUID.equals(assetApprover)) result.add("该用户为资产审核人，不能操作！");
		// 检查用户是否属于维修派工角色
		if (item.getDispatchRoleId() != null) {
			List<UserRole> userCustomRoles = userRoleRepository.findByUserIdAndRoleType(userID, 1);
			boolean isInDispatchRole = userCustomRoles.stream()
					.anyMatch(ur -> item.getDispatchRoleId().equals(ur.getRoleId()));
			if (isInDispatchRole) result.add("该用户为维修派工角色成员，不能操作！");
		}
		logger.info("流程配置项中负责人校验,config:{},user:{},result:{}", JSONObject.toJSONString(item), JSONObject.toJSONString(selected),JSONObject.toJSONString(result));

	}
	private  void  isPersonLiable(UserAccount selected,List<String> result){
		String uid = selected.getUid();
		List<SearchFilter> searchFilters=new ArrayList<>();
		searchFilters.add(new SearchFilter("woOwnerUID", SearchFilter.Operator.EQ,uid,"1+"));
		searchFilters.add(new SearchFilter("woOwnerUID2", SearchFilter.Operator.EQ,uid,"1+"));
		searchFilters.add(new SearchFilter("woOwnerUID3", SearchFilter.Operator.EQ,uid,"1+"));
		searchFilters.add(new SearchFilter("pmOwnerUID", SearchFilter.Operator.EQ,uid,"1+"));
		List<OwnerConfig> bySearchFilter = ownerConfigRepository.findBySearchFilter(searchFilters);
		if(CollectionUtils.isNotEmpty(bySearchFilter)){
			Map<String, List<OwnerConfig>> collect = bySearchFilter.stream().collect(Collectors.groupingBy(OwnerConfig::getOwnerConfigType));
			if(collect !=null ){
				if(collect.containsKey(OwnerConfig.OwnerConfigType.AssetGroup.name())){
					// 设置为设备类型维修责任人1\2\3\类型保养责任人
					result.add("该用户为设备类型负责人，不能操作！");
				}
				if(collect.containsKey(OwnerConfig.OwnerConfigType.Department.name())){
					//设置为科室维修责任人1\2\3\科室保养责任人
					result.add("该用户为科室责任人，不能操作！");
				}
			}
			logger.info("该用户为设备类型负责人,{}", JSONObject.toJSONString(bySearchFilter));
		}

	}
	public void  existSubscribeMessages(UserAccount selected,List<String> result){
		// 订阅消息
		Integer id = selected.getId();
		List<SearchFilter> searchFilters=new ArrayList<>();
		searchFilters.add(new SearchFilter("subscribeUserId", SearchFilter.Operator.EQ,id));
		List<MessageSubscriber> bySearchFilter = messageSubscriberRepository.findBySearchFilter(searchFilters);
		if(CollectionUtils.isNotEmpty(bySearchFilter)){
			result.add("该用户有设备消息订阅，不能操作！");
			logger.info("该用户有设备消息订阅,{}", JSONObject.toJSONString(bySearchFilter));
		}
		List<AssetTagMsgSubscriber> assetTagMsgSubscriber = assetTagMsgSubscriberRepository.getAssetTagMsgSubscriber(id);
		if(CollectionUtils.isNotEmpty(assetTagMsgSubscriber)){
			result.add("该用户有标签消息订阅，不能操作！");
			logger.info("该用户有标签消息订阅,{}", JSONObject.toJSONString(assetTagMsgSubscriber));
		}
	}
	private void isMeasurement(UserAccount selected,List<String> result){
		// 计量类型责任人
		List<SearchFilter> searchFilters=new ArrayList<>();
		searchFilters.add(new SearchFilter("measureOwnerUid", SearchFilter.Operator.EQ,selected.getUid()));
		List<MeasuringTypeConfig> bySearchFilter = measuringTypeConfigRepository.findBySearchFilter(searchFilters);
		if(CollectionUtils.isNotEmpty(bySearchFilter)){
			result.add("该用户为计量类型责任人，不能操作！");
			logger.info("该用户为计量类型责任人,{}", JSONObject.toJSONString(bySearchFilter));
		}
	}
	private void isUserSchedule(UserAccount selected,List<String> result){
		//设置了排班记录的人员
		userScheduleService.existUserWorkList(selected,result, UserSchedule.Type.W);
		userScheduleService.existUserWorkList(selected,result, UserSchedule.Type.A);

	}
	private void  isSparePartsAdmin(UserAccount selected,List<String> result){
		//设置为备件仓库管理员
		List<UserRole> userRoleList = selected.getUserRoleList();
		if(CollectionUtils.isNotEmpty(userRoleList)){
			boolean anyMatch = userRoleList.stream().map(UserRole::getRole).anyMatch(item -> "SparePartsAdmin".equals(item.getName()));
			if(anyMatch) {
				List<String> simplePartsWarehouses = simplePartsWarehouseUserService.findAllByPartsWarehouseByUserUid(selected.getUid());
				if(CollectionUtils.isNotEmpty(simplePartsWarehouses)){
					result.add("该用户为备件仓库管理员，不能操作！");
				}
			}
		}
	}
}
