package com.ge.apm.service.uaa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.NurseSiteMaintainConfigItemRepository;
import com.ge.apm.dao.NurseSiteMaintainConfigRepository;
import com.ge.apm.dao.NurseSiteMaintainItemRepository;
import com.ge.apm.dao.NurseSiteMaintainRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SysRoleRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.UserOrgInfoRepository;
import com.ge.apm.dao.UserRoleRepository;
import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.NurseSiteMaintain;
import com.ge.apm.domain.NurseSiteMaintainConfig;
import com.ge.apm.domain.NurseSiteMaintainConfigItem;
import com.ge.apm.domain.NurseSiteMaintainItem;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;

@Service
public class CustomRoleService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	UserAccountMapper userAccountMapper;
	
	@Autowired
	UserAccountRepository userAccountRepository;

	@Autowired
	AssetInfoRepository assetInfoRepository;
	
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
	NurseSiteMaintainRepository nurseSiteMaintainRepository;
	
	@Autowired
	NurseSiteMaintainItemRepository nurseSiteMaintainItemRepository;
	
	@Autowired
	NurseSiteMaintainConfigRepository nurseSiteMaintainConfigRepository;
	
	@Autowired
	NurseSiteMaintainConfigItemRepository nurseSiteMaintainConfigItemRepository;
	
	@Autowired
	NurseSiteMaintainService nurseSiteMaintainService;
	
	@Autowired
	I18nMessageRepository i18nRepository;
	
	private static final String MSG_TYPE_VERSION = "tenantVersionMenu";
	private static final String MSG_TYPE_AUTH = "customRoleAuth";
	private static final String MSG_TYPE_CARD = "wechatCard";
	
	private List<I18nMessage> listI18Card=null;
	private List<I18nMessage> listI18Auth=null;
	public void init(){
		listI18Card=null;
		listI18Auth=null;
	}
	
	public List<I18nMessage> getMenusByTenant(String tenantUid){
		List<I18nMessage> list18 = i18nRepository.findI18nByType(MSG_TYPE_VERSION);
		TenantInfo tenant=tenantInfoRepository.findByuid(tenantUid);
		List<I18nMessage> listNew=new ArrayList<I18nMessage>();
		String tenantConfig=tenant.getTenantConfig();
		if(StringUtils.isEmpty(tenantConfig)){
			return list18;
		}
		if(list18!=null && list18.size()>0){
			for(int i=0;i<list18.size();++i){
				if(tenantConfig.contains("\""+list18.get(i).getMsgKey()+"\"")){
					listNew.add(list18.get(i));
				}
			}
		}
		//temp delete some menus
		return removeNoNeedI18nMenus(listNew);
	}
	//v5.1
	private List<I18nMessage> removeNoNeedI18nMenus(List<I18nMessage> list18){
		List<I18nMessage> listNew=new ArrayList<I18nMessage>();
		for(int i=0;i<list18.size();++i){
			I18nMessage i18n=list18.get(i);
			String key=i18n.getMsgKey();
			if(key.contains("nurseSite.")){
				listNew.add(i18n);
			}
			else if(key.equals("asset.inventory")){
				listNew.add(i18n);
			}
		}
		return listNew;
	}
	public List<I18nMessage> getCardI18n(){
		if(listI18Card==null){
			listI18Card = i18nRepository.findI18nByType(MSG_TYPE_CARD);
		}
		return listI18Card;
	}
	
	public List<I18nMessage> getAuthI18n(){
		if(listI18Auth==null){
			listI18Auth = i18nRepository.findI18nByType(MSG_TYPE_AUTH);
		}
		return listI18Auth;
	}
	
	public JSONArray getAuth(){
		getAuthI18n();
		JSONArray arr=new JSONArray();
		if(listI18Auth!=null && listI18Auth.size()>0){
			for(int i=0;i<listI18Auth.size();++i){
				JSONObject auth=new JSONObject();
				auth.put("k", listI18Auth.get(i).getMsgKey());
				auth.put("v", listI18Auth.get(i).getValue());
				auth.put("a", false);
				auth.put("m", false);
				auth.put("d", false);
				auth.put("s", false);
				auth.put("i", false);
				auth.put("o", false);
				arr.add(auth);
			}
		}
		return arr;
	}
	
	public String parseAuthArray(JSONArray authArr){
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<authArr.size();++i){
			JSONObject tempObj=authArr.getJSONObject(i);
			String key=tempObj.getString("k");
			List<I18nMessage> listI18=i18nRepository.findI18nByType(key);
			if(listI18!=null && listI18.size()>0){
				for(int j=0;j<listI18.size();++j){
					I18nMessage tempMessage=listI18.get(j);
					String tempKey=tempMessage.getMsgKey();
					if(tempObj.getBooleanValue(tempKey)){
						sb.append(tempMessage.getValue());
						sb.append(",");
					}
				}
			}
		}
		String str=sb.toString();
		str=str.substring(0, str.length()-1);
		return str;
	}
}
