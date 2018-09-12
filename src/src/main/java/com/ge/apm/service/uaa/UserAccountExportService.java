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
public class UserAccountExportService {
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


	public List<Map<String, Object>> parseUserAccountToMap(List<UserAccount> listUserAccount) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Map<String, Object>> listMapResult = new ArrayList<Map<String, Object>>();
		if (listUserAccount != null && listUserAccount.size() > 0) {
			for (int i = 0; i < listUserAccount.size(); ++i) {
				UserAccount tempUser = listUserAccount.get(i);
				Map<String, Object> tempMap = new HashMap<String, Object>();
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
				if(StringUtils.isNotEmpty(tempUser.getDepartName())){
					tempMap.put(HEADER_DEPARTMENT, tempUser.getDepartName());
				}
				if(StringUtils.isNotEmpty(tempUser.getDepartName2())){
					tempMap.put(HEADER_DEPARTMENT2, tempUser.getDepartName2());
				}
				if(StringUtils.isNotEmpty(tempUser.getDepartName3())){
					tempMap.put(HEADER_DEPARTMENT3, tempUser.getDepartName3());
				}
				tempMap.put(HEADER_LOGINNAME, tempUser.getLoginName());
				tempMap.put(HEADER_NAME, tempUser.getName());
				tempMap.put(HEADER_EMAIL, tempUser.getEmail());
				tempMap.put(HEADER_TELEPHONE, tempUser.getTelephone());
				tempMap.put(HEADER_ISACTIVE, tempUser.getIsActive()?"是":"否");
				tempMap.put(HEADER_ISLOCKED, tempUser.getIsLocked()?"是":"否");
				tempMap.put(HEADER_ROLENAME, tempUser.getUserRoleNames());
				tempMap.put(HEADER_ACCESSSCOPE, getOrgInfoName(tempUser.getListOrgInfo()));
				tempMap.put(HEADER_UUID, tempUser.getUid());
				listMapResult.add(tempMap);
			}
		}
		return listMapResult;
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
