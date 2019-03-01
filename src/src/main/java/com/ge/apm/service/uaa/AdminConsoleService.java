package com.ge.apm.service.uaa;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SysRoleRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.UserOrgInfoRepository;
import com.ge.apm.dao.UserRoleRepository;
import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.domain.TenantInfo;

@Service
public class AdminConsoleService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
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
	

	
	@Transactional
	public void addVersion(String version,String key) {
		Iterable<TenantInfo> listTenantInfo=tenantInfoRepository.findAll();
		if(listTenantInfo!=null){
			Iterator<TenantInfo> it=listTenantInfo.iterator();
			while(it.hasNext()){
				TenantInfo tempInfo=it.next();
				String strTenantInfo=tempInfo.getTenantConfig();
				if(StringUtils.isNotEmpty(strTenantInfo)){
					JSONObject jsonObj=null;
					try {
						jsonObj=JSONObject.parseObject(strTenantInfo);
					} catch (Exception e) {
						// TODO: handle exception
					}
					if(jsonObj!=null){
						String tempVersion=jsonObj.getString("edition");
						if(tempVersion.equals(version)){
							JSONArray tempArr=jsonObj.getJSONArray("menuList");
							if(tempArr!=null){
								if(!tempArr.contains(key)){
									tempArr.add(key);
									String result=jsonObj.toString();
									tempInfo.setTenantConfig(result);
									tenantInfoRepository.save(tempInfo);
								}
							}
						}
					}
				}
			}
		}
	}
	@Transactional
	public void deleteVersion(String version,String key) {
		Iterable<TenantInfo> listTenantInfo=tenantInfoRepository.findAll();
		if(listTenantInfo!=null){
			Iterator<TenantInfo> it=listTenantInfo.iterator();
			while(it.hasNext()){
				TenantInfo tempInfo=it.next();
				String strTenantInfo=tempInfo.getTenantConfig();
				if(StringUtils.isNotEmpty(strTenantInfo)){
					JSONObject jsonObj=null;
					try {
						jsonObj=JSONObject.parseObject(strTenantInfo);
					} catch (Exception e) {
						// TODO: handle exception
					}
					if(jsonObj!=null){
						String tempVersion=jsonObj.getString("edition");
						if(tempVersion.equals(version)){
							JSONArray tempArr=jsonObj.getJSONArray("menuList");
							if(tempArr!=null){
								if(tempArr.contains(key)){
									tempArr.remove(key);
									String result=jsonObj.toString();
									tempInfo.setTenantConfig(result);
									tenantInfoRepository.save(tempInfo);
								}
							}
						}
					}
				}
			}
		}
	}
}
