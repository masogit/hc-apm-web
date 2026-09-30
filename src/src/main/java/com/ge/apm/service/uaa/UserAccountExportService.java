package com.ge.apm.service.uaa;

import com.alibaba.excel.util.ListUtils;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.dao.mapper.UserTagsMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.dao.SearchFilter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserAccountExportService {
	private Logger logger = LoggerFactory.getLogger(getClass());

	protected Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();
	public List<I18nMessage> userPositionList = FieldValueMessageController.getFieldValueList("userPosition", siteId);

	private static final String HEADER_INSTITUTION="所属集团";
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
	private static final String HEADER_ISDISABLE="停用?*";
	private static final String HEADER_ROLENAME="角色*";
	private static final String HEADER_ACCESSSCOPE="可见范围（科室）";
	private static final String HEADER_POSITION="职务";
	private static final String HEADER_TAGS="标签";
	private static final String HEADER_ASSET_CLASSIFICATION="资产分类";
	private static final String HEADER_UUID="UUID";
	private static final String SHEET_USER="datalist";

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
	private UserAssetGroupRepository userAssetGroupRepository;

	@Autowired
	private AssetTypeGroupRepository assetTypeGroupRepository;

	@Autowired
	private CustomRoleRepository customRoleRepository;

	EmailValidator emailValidator = EmailValidator.getInstance();


	public List<Map<String, Object>> parseUserAccountToMap(List<UserAccount> listUserAccount) {
		List<Map<String, Object>> listMapResult = new ArrayList<>();
		List<OrgInfo> orgInfoTenantAll = tenantOrgInfoAll();
		List<OrgInfo> orgInfos = tenantOrgInfoTypeOf4All(orgInfoTenantAll);
		List<SysRole> sysRoleList = getTenantSysRole();
		List<CustomRole> customRoleList = getTenantCustomRole();
		if (listUserAccount != null && !listUserAccount.isEmpty()) {
			listUserAccount=listUserAccount.stream().filter(item->!Boolean.TRUE.equals(item.getIsDeleted())).collect(Collectors.toList());
			setListOrgInfo(listUserAccount,orgInfoTenantAll);
			Map<Integer, List<UserRole>> userSysRoleList = getUserRoleList(listUserAccount, 0);
			Map<Integer, List<UserRole>> userCustomList = getUserRoleList(listUserAccount, 1);
			for (UserAccount tempUser : listUserAccount) {
				Map<String, Object> tempMap = new HashMap<>();
				setDepartNames(tempMap, tempUser,orgInfos);
				if (StringUtils.isNotEmpty(tempUser.getInstitutionName())) {
					tempMap.put(HEADER_INSTITUTION, tempUser.getInstitutionName());
				}
				if (StringUtils.isNotEmpty(tempUser.getHospitalName())) {
					tempMap.put(HEADER_HOSPITAL, tempUser.getHospitalName());
				}
				if (StringUtils.isNotEmpty(tempUser.getSiteName())) {
					tempMap.put(HEADER_SITE, tempUser.getSiteName());
				}
				tempMap.put(HEADER_LOGINNAME, tempUser.getLoginName());
				tempMap.put(HEADER_NAME, tempUser.getName());
				tempMap.put(HEADER_EMAIL, tempUser.getEmail());
				tempMap.put(HEADER_TELEPHONE, tempUser.getTelephone());
				tempMap.put(HEADER_ISACTIVE, Boolean.TRUE.equals(tempUser.getIsActive()) ? "是" : "否");
				tempMap.put(HEADER_ISLOCKED, Boolean.TRUE.equals(tempUser.getIsLocked()) ? "是" : "否");
				tempMap.put(HEADER_ISDISABLE, Boolean.TRUE.equals(tempUser.getIsDisable()) ? "是" : "否");
                String userSysRoleNames = getUserSysRoleNames(userSysRoleList, sysRoleList, tempUser);
                String userCustomRoleNames = getUserCustomRoleNames(userCustomList, customRoleList, tempUser);
                tempMap.put(HEADER_ROLENAME, (userSysRoleNames + " " + userCustomRoleNames).trim());
				tempMap.put(HEADER_POSITION, convertPositionIdToLabel(tempUser.getPosition()));
				tempMap.put(HEADER_TAGS, joinTags(tempUser));
				tempMap.put(HEADER_ACCESSSCOPE, getOrgInfoName(tempUser.getListOrgInfo()));
				List<String> assetGroupEncoded = userAssetGroupRepository.findByUserUid(tempUser.getUid());
				String str = org.apache.commons.lang.StringUtils.join(assetGroupEncoded, ",");
				tempMap.put(HEADER_ASSET_CLASSIFICATION,str);
				tempMap.put(HEADER_UUID, tempUser.getUid());
				listMapResult.add(tempMap);
			}
		}
		return listMapResult;
	}

    public String getUserSysRoleNames(Map<Integer, List<UserRole>> userSysRoleList, List<SysRole> sysRoleList, UserAccount account) {
        String userRoleNames = "";

        if(userSysRoleList == null){
            return userRoleNames;
        }
        List<UserRole> userRoleList = userSysRoleList.get(account.getId());
        if (CollectionUtils.isNotEmpty(userRoleList)) {
            List<Integer> collect = userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toList());
            List<String> sysRoleName = sysRoleList.stream().filter(item -> collect.contains(item.getId())).map(SysRole::getRoleDesc).collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(sysRoleName)) {
                userRoleNames += String.join(" ", sysRoleName);
            }
        }

        return userRoleNames;
    }

	private String getUserCustomRoleNames(Map<Integer, List<UserRole>> userCustomList, List<CustomRole> customRoleList, UserAccount tempUser){
		String userRoleNames = "";

		if(userCustomList == null){
			return userRoleNames;
		}
	    List<UserRole> userRoleList = userCustomList.get(tempUser.getId());
		if(CollectionUtils.isNotEmpty(userRoleList)){
			List<Integer> collect = userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toList());
			List<String> customName = customRoleList.stream().filter(item -> collect.contains(item.getId())).map(CustomRole::getName).collect(Collectors.toList());
			if(CollectionUtils.isNotEmpty(customName)){
				userRoleNames += String.join(" ", customName);
			}
		}
		return userRoleNames;
	}
	private Map<Integer, List<UserRole>> getUserRoleList(List<UserAccount> listUserAccount, Integer roleType){
		if(CollectionUtils.isEmpty(listUserAccount)) return null;
		List<Integer> ids = listUserAccount.stream().map(UserAccount::getId).collect(Collectors.toList());
		List<SearchFilter> searchFilters = new ArrayList<>();
		searchFilters.add(new SearchFilter("userId", SearchFilter.Operator.IN,ids));
		searchFilters.add(new SearchFilter("roleType", SearchFilter.Operator.EQ,roleType));
		List<UserRole> userRoleList = userRoleRepository.findBySearchFilter(searchFilters);
		if(CollectionUtils.isEmpty(userRoleList)) return null;
		Map<Integer, List<UserRole>> collect = userRoleList.stream().collect(Collectors.groupingBy(UserRole::getUserId));
		return collect;
	}
    private List<SysRole> getTenantSysRole(){
        return  sysRoleRepository.find();
    }
	private List<CustomRole> getTenantCustomRole(){
		String tenantUID = UserContextService.getCurrentUserAccount().getTenantUID();
		return  customRoleRepository.findByTenantUID(tenantUID);
	}
	private void setDepartNames(Map<String, Object> tempMap,UserAccount tempUser,List<OrgInfo> orgInfos){
		String siteUID = tempUser.getSiteUID();
		String orgInfoUID = tempUser.getOrgInfoUID();
		if(StringUtils.isBlank(siteUID) ) return;
		if(StringUtils.isBlank(orgInfoUID)) return;
		if(siteUID.equals(orgInfoUID))return;
		Optional<OrgInfoVO> first = orgInfos.stream().filter(item -> item.getUid().equals(orgInfoUID)).map(item -> {
			OrgInfoVO vo=new OrgInfoVO();
			String parentUID = item.getParentUID();
			vo.setParentUID(parentUID);
			vo.setName(item.getShortName());
			vo.setUid(item.getUid());
			vo.setParent(recursiveSearch(item, orgInfos));
			return vo;
		}).findFirst();
		if(first.isPresent()){
			OrgInfoVO orgInfoVO=first.get();
			List<String> org=new ArrayList<>();
			do {
				org.add(orgInfoVO.getName());

			}while ((orgInfoVO=orgInfoVO.getParent()) != null);
			Collections.reverse(org);
			for (int i = 0; i < org.size(); i++) {
				if(i==0){
					tempMap.put(HEADER_DEPARTMENT,org.get(i));
				}else if(i == 1){
					tempMap.put(HEADER_DEPARTMENT2,org.get(i));
				}else if(i ==2){
					tempMap.put(HEADER_DEPARTMENT3,org.get(i));
				}
			}
		}
	}
	private OrgInfoVO recursiveSearch(OrgInfo orgInfo, List<OrgInfo> orgInfos){
		Optional<OrgInfoVO> first = orgInfos.stream().filter(item -> {
			String parentUID = orgInfo.getParentUID();
			return item.getUid().equals(parentUID);
		}).filter(item->{
			return item != null;
		}).map(item -> {
			OrgInfoVO vo = new OrgInfoVO();
			String parentUID = item.getParentUID();
			vo.setParentUID(parentUID);
			vo.setName(item.getShortName());
			vo.setUid(item.getUid());
			vo.setParent(recursiveSearch(item, orgInfos));
			return vo;
		}).findFirst();
		return first.isPresent()?first.get():null;
	}
	private void setListOrgInfo(List<UserAccount> userAccounts,List<OrgInfo> orgInfos){
		if(CollectionUtils.isEmpty(userAccounts)) return;
		if(CollectionUtils.isEmpty(orgInfos)) return;
		Set<String> collect = userAccounts.stream().map(UserAccount::getUid).collect(Collectors.toSet());
		List<UserOrgInfo> userOrgList = userOrgDao.findOrgUidListByUserUid(collect);
		if(CollectionUtils.isEmpty(userOrgList)) return;
		Map<String, OrgInfo> ogInfoMap = orgInfos.stream().collect(Collectors.toMap(OrgInfo::getUid, item -> item));
		Map<String, List<OrgInfo>> orgInfoMap = userOrgList.stream().collect(Collectors.groupingBy(UserOrgInfo::getUserUid,
				Collectors.mapping(item -> ogInfoMap.get(item.getOrgUid()), Collectors.toCollection(ArrayList::new))));
		userAccounts.forEach(item->{
			String siteUID = item.getSiteUID();
			List<OrgInfo> listOrgInfo = orgInfoMap.get(item.getUid()); // 过滤跨院区
			if(CollectionUtils.isNotEmpty(listOrgInfo) && StringUtils.isNotEmpty(siteUID)){
				listOrgInfo=listOrgInfo.stream().filter(Objects::nonNull).filter(org->siteUID.equals(org.getSiteUID()) || siteUID.equals(org.getHospitalUID()) || siteUID.equals(org.getInstitutionUID()) ||  siteUID.equals(org.getTenantUID())).collect(Collectors.toList());
				item.setListOrgInfo(listOrgInfo);
			}
		});
	}
	private List<OrgInfo> tenantOrgInfoAll(){
		UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
		String tenantUID = currentUserAccount.getTenantUID();
		return orgInfoRepository.findByTenantUID(tenantUID);
	}
	private List<OrgInfo> tenantOrgInfoTypeOf4All(List<OrgInfo> orgInfos){
		if(CollectionUtils.isEmpty(orgInfos)) return ListUtils.newArrayList();
		return  orgInfos.stream().filter(item->item.getOrgType() != null && Objects.equals(item.getOrgType(), 4)).collect(Collectors.toList());
	}
	/**
	 * use space as delimiter to group tags to export
	 * @param tempUser
	 * @return
	 */
	private String joinTags(UserAccount tempUser) {
		List<String> existingTags = userTagsMapper.getTagsByUser(tempUser.getUid());

		return String.join(" ", existingTags);

	}

	public void supplementUserAccount(List<UserAccount> listUserAccount) {
		if (listUserAccount != null && listUserAccount.size() > 0) {
			Set<String> setUserOrgUid = new HashSet<>();

			for (UserAccount tempUser : listUserAccount) {
				if (StringUtils.isNotEmpty(tempUser.getOrgInfoUID())) {
					setUserOrgUid.add(tempUser.getOrgInfoUID());
				}
			}

			List<OrgInfo> listSelfOrgInfo = orgInfoRepository.findByUids(setUserOrgUid);

			for (OrgInfo orgInfo : listSelfOrgInfo) {
				for (UserAccount tempUser : listUserAccount) {
					String tempOrgUid = tempUser.getOrgInfoUID();
					if (StringUtils.equals(tempOrgUid, orgInfo.getUid())) {
						tempUser.setOrgInfo(orgInfo);
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

	private String convertPositionIdToLabel(String position) {
		if (StringUtils.isEmpty(position)) {
			return null;
		}

		for (I18nMessage message: userPositionList) {
			if (message.getMsgKey().equals(position)) {
				return message.getValue();
			}
		}

		return null;
	}
	public InputStream processorTemplateFile(InputStream inputStream)  {
		return getCustomRole(inputStream);
	}

	private InputStream getCustomRole(InputStream inputStream) {
		UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
		String tenantUID = currentUserAccount.getTenantUID();
		List<CustomRole> customRoles = customRoleRepository.findByTenantUID(tenantUID);
		if(org.springframework.util.CollectionUtils.isEmpty(customRoles)){
			return inputStream;
		}
		try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)){
//			String lastRow="资产分类非必填(租户开启资产分类值为资产分类的编码例如    ：01-01,01-02多项用,号分割 )";
			XSSFSheet explanation = workbook.getSheetAt(1);
			int lastRowNum = explanation.getLastRowNum();
			XSSFCellStyle cellStyle = explanation.getRow(lastRowNum-1).getCell(0).getCellStyle();
			for (int i = 0; i < customRoles.size(); i++) {
				XSSFRow row = explanation.createRow(lastRowNum + i);
				XSSFCell cell = row.createCell(0);
				cell.setCellValue(customRoles.get(i).getName());
				cell.setCellStyle(cellStyle);
			}
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
			return inputStream;
		}
	}
}
