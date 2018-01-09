package com.ge.apm.service.asset;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.AssetSeriaNumConfigRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.AssetSeriaNumConfig;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.pm.PmOrderRequestApiService;

@Service
public class AssetFinacingNumGenerateService {

	public static final String FORMAT_STR = "00000";
	private static final Logger LOGGER = LoggerFactory.getLogger(AssetFinacingNumGenerateService.class);

	@Autowired
	private AssetInfoRepository assetInfoRepository;

	@Autowired
	private AssetSeriaNumConfigRepository assetSeriaNumConfigRepository;

	@Autowired
	private OrgInfoRepository orgInfoRepository;

/*	@Autowired
	private UserAccountRepository userAccountRepository;*/
	
	@Autowired
	private PmOrderRequestApiService pmOrderRequestApiService;
	
	public String autoGenerateFinacingNum(Integer assetId, String nationalStardCode, String uid,Integer majorAssetId,String prefix) {
		JSONObject param = new JSONObject();
		param.put("assetId", assetId);
		param.put("nationalStardCode", nationalStardCode);
		param.put("uid", uid);
		param.put("majorAssetId", majorAssetId);
		param.put("prefix", prefix);
		try {
			String result = pmOrderRequestApiService.autoGenerateFinacingNum(param.toJSONString());
			return result;
		}catch(Exception e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/***
	 * 该方法是新增资产或更新资产(必须是更新该字段)时调用，1是更新，2是新增
	 * 
	 * @param 1、assetId
	 *            如果有assetId，后面的三个字段可以为null,nationalStardCode
	 * @param 2、nationalStardCode
	 *            国标码
	 * @param clinicalId
	 *            科室id
	 * @param user
	 *            当前登陆者，如果科室id为空，则user必输
	 * @return
	 */
	public String generateFinacingNum(Integer assetId, String nationalStardCode, Integer clinicalId, UserAccount user) {
		LOGGER.info("generate finacing num begin ,assetId is {},code is {},clinicalId is {},user is {}", assetId,
				nationalStardCode, clinicalId, user);
		if (assetId != null) {
			AssetInfo asset = assetInfoRepository.findById(assetId);
			if (asset == null) {
				LOGGER.error("can't find asset by {}", assetId);
				return null;
			}
			if(StringUtils.isEmpty(nationalStardCode)){
				LOGGER.warn("nationalStardCode shouldn't be null !");
				return null;
			}
			OrgInfoLevel level = fetchConfig(asset.getSiteUID(), asset.getHospitalUID(), asset.getInstitutionUID(),asset.getTenantUID());
			if (level == null) {
				return null;
			} else {
				return getFinacingNum(nationalStardCode, level);
			}
		} else if (clinicalId != null) {
			OrgInfo orgInfo = orgInfoRepository.findById(clinicalId);
			if (orgInfo == null) {
				return null;
			}
			if(StringUtils.isEmpty(nationalStardCode)){
				LOGGER.warn("can't find nationStandardCode !");
				return null;
			}
			OrgInfoLevel level = fetchConfig(orgInfo.getSiteUID(), orgInfo.getHospitalUID(),
					orgInfo.getInstitutionUID(), orgInfo.getTenantUID());
			if (level == null) {
				return null;
			} else {
				return getFinacingNum(nationalStardCode, level);
			}
		} else if (user != null) {
			if(StringUtils.isEmpty(nationalStardCode)){
				LOGGER.warn("can't find nationStandardCode !");
				return null;
			}
			OrgInfoLevel level = fetchConfig(user.getSiteUID(), user.getHospitalUID(), user.getInstitutionUID(),
					user.getTenantUID());
			if (level == null) {
				return null;
			} else {
				return getFinacingNum(nationalStardCode, level);
			}
		}
		return null;
	}
	
	/***
	 *  1、
	 * @param assetId
	 * @param nationalStardCode
	 * @param uid
	 * @return
	 */
	public String generateFinancingNum(Integer assetId, String nationalStardCode,String uid){
		LOGGER.info("generate finacing num begin ,assetId is {},code is {},uid is {}", assetId,nationalStardCode,uid);
		if (assetId != null) {
			if(StringUtils.isEmpty(nationalStardCode)){
				LOGGER.warn("nationalStardCode shouldn't be null !");
				return null;
			}
			AssetInfo asset = assetInfoRepository.findById(assetId);
			if (asset == null) {
				LOGGER.error("can't find asset by {}", assetId);
				return null;
			}
			OrgInfoLevel level = fetchConfig(asset.getSiteUID(), asset.getHospitalUID(), asset.getInstitutionUID(),asset.getTenantUID());
			if (level == null) {
				return null;
			} else {
				return getFinacingNum(nationalStardCode, level);
			}
		} else if (uid != null) {
			if(StringUtils.isEmpty(nationalStardCode)){
				LOGGER.warn("can't find nationStandardCode !");
				return null;
			}
			OrgInfo orgInfo = orgInfoRepository.getByUid(uid);
			if (orgInfo == null) {
				return null;
			}
			OrgInfoLevel level = fetchConfig(orgInfo.getSiteUID(), orgInfo.getHospitalUID(),orgInfo.getInstitutionUID(), orgInfo.getTenantUID());
			if (level == null) {
				return null;
			} else {
				return getFinacingNum(nationalStardCode, level);
			}
		}
		return null;
	
	}

	private OrgInfoLevel fetchConfig(String siteUID, String hospitalUID, String institutionUID, String tenantUID) {
		AssetSeriaNumConfig config = null;
		config = assetSeriaNumConfigRepository.findBySiteUIDAndLevel(siteUID,1);
		if (config != null) {
			if(config.getGenerateMethod() != null && config.getGenerateMethod() == 1){
				return new OrgInfoLevel(1, siteUID);// 院区配置
			}
			return null;
		}
		config = assetSeriaNumConfigRepository.findBySiteUIDAndLevel(hospitalUID,2);
		if (config != null) {
			if(config.getGenerateMethod() != null && config.getGenerateMethod() == 1){
				return new OrgInfoLevel(2, hospitalUID);// 医院配置
			}else{
				return null;
			}
		} else {
			config = assetSeriaNumConfigRepository.findBySiteUIDAndLevel(institutionUID,3);
			if (config != null) {
				if(config.getGenerateMethod() != null && config.getGenerateMethod() == 1){
					return new OrgInfoLevel(3, institutionUID);// 集团配置
				}
				return null;
			} else {
				config = assetSeriaNumConfigRepository.findBySiteUIDAndLevel(tenantUID,4);
				if (config != null) {
					if(config.getGenerateMethod() != null && config.getGenerateMethod() == 1){
						return new OrgInfoLevel(4, tenantUID);// 租户配置
					}
					return null;
				} else {
					return null;// 未配置
				}
			}
		}
	}

	public String getFinacingNum(String nationalStardCode, OrgInfoLevel level) {
		DecimalFormat df = new DecimalFormat(FORMAT_STR);
		if (level.getLevel() == 1) {
			String maxFinacingNum = assetInfoRepository.fetchMaxAssetFinacingNumWithSiteUID(level.getUid(),nationalStardCode);
			if (StringUtils.isEmpty(maxFinacingNum)) {
				return nationalStardCode + "00001";
			}
			int lenth = maxFinacingNum.length();
			Integer finacingNum = Integer.parseInt(maxFinacingNum.substring(lenth-5, lenth));
			finacingNum++;
			return nationalStardCode + df.format(finacingNum);
		} else if (level.getLevel() == 2) {
			String maxFinacingNum = assetInfoRepository.fetchMaxAssetFinacingNumWithHospitalUID(level.getUid(),nationalStardCode);
			if (StringUtils.isEmpty(maxFinacingNum)) {
				return nationalStardCode + "00001";
			}
			int lenth = maxFinacingNum.length();
			Integer finacingNum = Integer.parseInt(maxFinacingNum.substring(lenth-5, lenth));
			finacingNum++;
			return nationalStardCode + df.format(finacingNum);
		} else if (level.getLevel() == 3) {
			String maxFinacingNum = assetInfoRepository.fetchMaxAssetFinacingNumWithInstitutionUID(level.getUid(),nationalStardCode);
			if (StringUtils.isEmpty(maxFinacingNum)) {
				return nationalStardCode + "00001";
			}
			int lenth = maxFinacingNum.length();
			Integer finacingNum = Integer.parseInt(maxFinacingNum.substring(lenth-5, lenth));
			finacingNum++;
			return nationalStardCode + df.format(finacingNum);
		} else if (level.getLevel() == 4) {
			String maxFinacingNum = assetInfoRepository.fetchMaxAssetFinacingNumWithTenantUID(level.getUid(),nationalStardCode);
			if (StringUtils.isEmpty(maxFinacingNum)) {
				return nationalStardCode + "00001";
			}
			int lenth = maxFinacingNum.length();
			Integer finacingNum = Integer.parseInt(maxFinacingNum.substring(lenth-5, lenth));
			finacingNum++;
			return nationalStardCode + df.format(finacingNum);
		}
		return null;
	}

	class OrgInfoLevel {
		private Integer level;
		private String uid;

		public Integer getLevel() {
			return level;
		}

		public void setLevel(Integer level) {
			this.level = level;
		}

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

		public OrgInfoLevel(Integer level, String uid) {
			super();
			this.level = level;
			this.uid = uid;
		}

	}
}
