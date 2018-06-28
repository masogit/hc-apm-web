package com.ge.apm.service.nurseSite;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.NurseSiteMaintainConfigRepository;
import com.ge.apm.dao.NurseSiteMaintainRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.NurseSiteMaintain;
import com.ge.apm.domain.NurseSiteMaintainConfig;
import com.ge.apm.service.uaa.UaaService;

import webapp.framework.dao.SearchFilter;

/**
 * Created by 212605082 on 2018/5/8.
 */
@Component
public class NurseSiteMaintainService{

	@Autowired
	NurseSiteMaintainRepository nurseSiteMaintainRepository;
	
	@Autowired
	NurseSiteMaintainConfigRepository nurseSiteMaintainConfigRepository;
	
	@Autowired
	AssetInfoRepository assetInfoRepository;
	
	@Autowired
    private UaaService uaaService;
	
	public NurseSiteMaintainConfig getMaintainConfigBySiteUid(String siteUid){
		return nurseSiteMaintainConfigRepository.findBySiteUid(siteUid);
	}
	
	public NurseSiteMaintainConfig getMaintainConfigByUid(String id){
		return nurseSiteMaintainConfigRepository.findByUid(id);
	}
	
	
	public List<NurseSiteMaintain> getMaintainData(Date startDate,Date endDate,String siteUid,String userUid){
		Calendar ca=Calendar.getInstance();
		ca.setTime(endDate);
		ca.set(Calendar.DATE, ca.get(Calendar.DATE)+1);
		return nurseSiteMaintainRepository.findBySiteUidAndDate(startDate,ca.getTime(),siteUid,userUid);
	}
	
	public List<NurseSiteMaintain> findBySearchFilter(List<SearchFilter> searchFilters){
		return nurseSiteMaintainRepository.findBySearchFilter(searchFilters);
	}
	
	public List<AssetInfo> getListAsset(String siteUid,String userUid){
		return assetInfoRepository.findByAccessScope(siteUid,userUid);
	}
	
	public AssetInfo getAssetInfoByUid(String uid){
		return assetInfoRepository.getByUid(uid);
	}
	
	public boolean canAddMaintainData(Date scheduleDate,String siteUid,String userUid){
		return nurseSiteMaintainRepository.findBySiteUidAndScheduleDate(scheduleDate, siteUid, userUid)==null?true:false;
	}
	
	public void save(NurseSiteMaintain obj){
		nurseSiteMaintainRepository.save(obj);
	}
	
	public void save(List<NurseSiteMaintain> obj){
		nurseSiteMaintainRepository.save(obj);
	}
	
	public void save(NurseSiteMaintainConfig obj){
		nurseSiteMaintainConfigRepository.save(obj);
	}
	
	public void delete(NurseSiteMaintain obj){
		nurseSiteMaintainRepository.delete(obj);
	}
	
	public void delete(NurseSiteMaintainConfig obj){
		nurseSiteMaintainConfigRepository.delete(obj);
	}
	
	public List<String> getOrgListByUser(String userUid){
		return uaaService.getOrgListByUser(userUid).stream().map(item -> item.getSiteUID()).collect(Collectors.toList());
	}
}
