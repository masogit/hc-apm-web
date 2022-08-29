package com.ge.apm.service.uaa;

import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.dao.mapper.UserTagsMapper;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

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
	private static final String HEADER_ROLENAME="角色*";
	private static final String HEADER_ACCESSSCOPE="可见范围（科室）";
	private static final String HEADER_POSITION="职务";
	private static final String HEADER_TAGS="标签";
	private static final String HEADER_UUID="UUID";
	private static final String SHEET_USER="datalist";
	private static final String HEADER_STOPUSE="停用?*";
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
	UserTagsRepository userTagsDao;

	EmailValidator emailValidator = EmailValidator.getInstance();


	public List<Map<String, Object>> parseUserAccountToMap(List<UserAccount> listUserAccount) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Map<String, Object>> listMapResult = new ArrayList<Map<String, Object>>();
		if (listUserAccount != null && listUserAccount.size() > 0) {
			for (int i = 0; i < listUserAccount.size(); ++i) {
				UserAccount tempUser = listUserAccount.get(i);
				Map<String, Object> tempMap = new HashMap<String, Object>();
				if(StringUtils.isNotEmpty(tempUser.getDepartName2())){
					tempMap.put(HEADER_DEPARTMENT2, tempUser.getDepartName2());
				}
				if(StringUtils.isNotEmpty(tempUser.getDepartName3())){
					tempMap.put(HEADER_DEPARTMENT3, tempUser.getDepartName3());
				}
				if(StringUtils.isNotEmpty(tempUser.getDepartName())){
					tempMap.put(HEADER_DEPARTMENT, tempUser.getDepartNames());
				}
				String orgFullName=tempUser.getOrgFullName();
				if(StringUtils.isNotEmpty(tempUser.getInstitutionName())){
					tempMap.put(HEADER_INSTITUTION, tempUser.getInstitutionName());
				}
				if(StringUtils.isNotEmpty(tempUser.getHospitalName())){
					tempMap.put(HEADER_HOSPITAL, tempUser.getHospitalName());
				}
				if(StringUtils.isNotEmpty(tempUser.getSiteName())){
					tempMap.put(HEADER_SITE, tempUser.getSiteName());
				}
				tempMap.put(HEADER_LOGINNAME, tempUser.getLoginName());
				tempMap.put(HEADER_NAME, tempUser.getName());
				tempMap.put(HEADER_EMAIL, tempUser.getEmail());
				tempMap.put(HEADER_TELEPHONE, tempUser.getTelephone());
				tempMap.put(HEADER_ISACTIVE, tempUser.getIsActive()?"是":"否");
				tempMap.put(HEADER_ISLOCKED, tempUser.getIsLocked()?"是":"否");
				tempMap.put(HEADER_ROLENAME, tempUser.getUserRoleNames());
				tempMap.put(HEADER_POSITION, convertPositionIdToLabel(tempUser.getPosition()));
				tempMap.put(HEADER_TAGS, joinTags(tempUser));
				tempMap.put(HEADER_ACCESSSCOPE, getOrgInfoName(tempUser.getListOrgInfo()));
				tempMap.put(HEADER_UUID, tempUser.getUid());
				listMapResult.add(tempMap);
			}
		}
		return listMapResult;
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
}
