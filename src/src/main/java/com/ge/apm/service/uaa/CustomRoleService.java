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
			if(key.contains("nurseSite.")||key.equals("nurseSite")){
				listNew.add(i18n);
			}
			else if(key.equals("asset.inventory") || key.equals("asset")){
				listNew.add(i18n);
			}
		}
		return listNew;
	}
	public List<I18nMessage> getCardsByTenant(String tenantUid){
		List<I18nMessage> list18 = i18nRepository.findI18nByType(MSG_TYPE_CARD);
		return list18;
	}
	
	public JSONArray getAuth(){
		List<I18nMessage> list18 = i18nRepository.findI18nByType(MSG_TYPE_AUTH);
		JSONArray arr=new JSONArray();
		if(list18!=null && list18.size()>0){
			for(int i=0;i<list18.size();++i){
				JSONObject auth=new JSONObject();
				auth.put("k", list18.get(i).getMsgKey());
				auth.put("v", list18.get(i).getValue());
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
}
