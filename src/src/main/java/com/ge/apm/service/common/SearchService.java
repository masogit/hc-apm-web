package com.ge.apm.service.common;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.UserOrgInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;

import webapp.framework.dao.SearchFilter;

/**
 * Created by 212605082 on 2018/5/8.
 */
@Service
public class SearchService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);
	private static final String SHEET_Maintain = "Maintain List";
	
	@Autowired
	AssetInfoRepository assetInfoRepository;

	@Autowired
	OrgInfoRepository orgInfoRepository;

	@Autowired
	UserOrgInfoRepository userOrgInfoRepository;

	@Autowired
	UserAccountRepository userAccountRepository;


	@Autowired
	private UaaService uaaService;


	public AssetInfo getAssetInfoByUid(String uid) {
		return assetInfoRepository.getByUid(uid);
	}

	public List<AssetInfo> getListAsset(String siteUid, String userUid) {
		return assetInfoRepository.findBySelfScope(siteUid, userUid);
	}

	public List<AssetInfo> getListAssetBySelfClinicalScope(String siteUid, String userUid, String clinicalDeptUid) {
		return assetInfoRepository.findBySelfClinicalScope(siteUid, userUid, clinicalDeptUid);
	}

	public List<AssetInfo> getListAssetInfoByUid(List<String> assetUids) {
		return assetInfoRepository.findByUidIn(assetUids);
	}

	public List<AssetInfo> getListAssetInfoByMaintainConfigParentId(String parentId) {
		return assetInfoRepository.findAssetByMaintainConfigParentId(parentId);
	}
	
	public List<AssetInfo> getListAssetInfoBySearchFilter(List<SearchFilter> listFilter,Sort sort){
		return assetInfoRepository.findBySearchFilter(listFilter,sort);
	}

	public List<OrgInfo> getListClinicalByUid(List<String> listUid) {
		return orgInfoRepository.findClinicalByUids(listUid);
	}

	public List<OrgInfo> getListClinicalByUid(Set<String> listUid) {
		return orgInfoRepository.findClinicalByUids(listUid);
	}
	
	public List<OrgInfo> getListClinicalByUidAndParent(Set<String> listUid,String parentUid) {
		return orgInfoRepository.findClinicalByUidsAndParent(listUid,parentUid);
	}
	
	public List<OrgInfo> getListParentSiteByUid(Set<String> listUid) {
		return orgInfoRepository.findParentSiteByUids(listUid);
	}
	
	public List<OrgInfo> getListSiteByUid(Set<String> listUid) {
		return orgInfoRepository.findSiteByUids(listUid);
	}

	public OrgInfo findOrgInfoByUid(String uid) {
		return orgInfoRepository.getByUid(uid);
	}

	public UserAccount getUserAccountByUid(String userUid) {
		return userAccountRepository.getByUid(userUid);
	}
	public List<String> findUserUidListByOrgUids(List<String> orgUids) {
		return userAccountRepository.findUserUidListByOrgUids(orgUids);
	}
	public OrgInfo getOrgInfoByUid(String uid){
		return orgInfoRepository.findByUid(uid);
	}
	public List<OrgInfo> getSiteListByHospitalUid(String hospitalUid){
		return orgInfoRepository.getSiteListByHospitalUid(hospitalUid);
	}
	
	public List<OrgInfo> getDeptListBySiteUid(String siteUid){
		return orgInfoRepository.getDeptListBySiteUid(siteUid);
	}
	
	public List<AssetInfo> getListAssetInfo(List<SearchFilter> listSearchFilter){
		return assetInfoRepository.findBySearchFilter(listSearchFilter);
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
}
