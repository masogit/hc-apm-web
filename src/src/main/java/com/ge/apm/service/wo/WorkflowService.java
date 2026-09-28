package com.ge.apm.service.wo;

import java.util.*;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.WorkflowConfigRepository;
import com.ge.apm.domain.*;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.dao.mapper.WechatMessageLogMapper;
import com.ge.apm.dao.mapper.WorkOrderMapper;
import com.ge.apm.service.utils.ConfigUtils;

@Service
public class WorkflowService {
	Logger logger = LoggerFactory.getLogger(getClass());


	@Autowired
	WorkOrderMapper workOrderMapper;

	@Autowired
	UserAccountMapper userAccountMapper;

	@Autowired
	AssetInfoMapper assetInfoMapper;

	@Autowired
	WechatMessageLogMapper wechatMessageLogMapper;
    @Autowired
    AssetInfoRepository assetInfoRepository;
    @Autowired
    OrgInfoRepository orgInfoRepository;
    @Autowired
    TenantInfoRepository tenantInfoRepository;
    @Autowired
    WorkflowConfigRepository workflowConfigRepository;

	@Autowired
	ConfigUtils configUtils;


	private boolean isMatchRule(Integer userId, Integer woId, Integer currentStepId, Integer times,
			Integer timeOutConfig) {
		WechatMessageLog wml = new WechatMessageLog();
		UserAccount user = userAccountMapper.getUserById(userId);
		String weChatId = user.getWeChatId();
		if (StringUtils.isEmpty(weChatId)) {
			logger.error("user {} does not bind wx!", user.getLoginName());
			return false;
		}
		wml.setWechatid(weChatId);
		wml.setWoId(woId);
		wml.setWoStepId(currentStepId);
		WechatMessageLog wmlFromDB = wechatMessageLogMapper.fetchByProperties(wml);
		if (wmlFromDB == null) {
			wechatMessageLogMapper.insertMessageLogMapper(wml);
			return true;
		}
		DateTime lastPushTime = new DateTime(wmlFromDB.getLastModifiedDate());
		DateTime now = new DateTime();
		// 如果当前时间和上次发送时间的间隔不足配置的时间间隔，则不发送
		if (Minutes.minutesBetween(lastPushTime, now).getMinutes() < timeOutConfig) {
			logger.info("timeout interval is not enough!,interval is {}", timeOutConfig);
			return false;
		}
		// -1 stands for no limit
		if (times <= -1) {
			wechatMessageLogMapper.updateMessageLogLastModifyTime(wmlFromDB);
			return true;
		}

		if (wmlFromDB.getMessageCount() >= times) {
			return false;
		}
		wechatMessageLogMapper.updateMessageLogMapper(wmlFromDB);
		return true;
	}

	private boolean isMatchRuleBak(Integer userId, Integer woId, Integer currentStepId, Integer times) {
		// -1 stands for no limit
		if (times == -1) {
			return true;
		}
		WechatMessageLog wml = new WechatMessageLog();
		UserAccount user = userAccountMapper.getUserById(userId);
		String weChatId = user.getWeChatId();
		if (StringUtils.isEmpty(weChatId)) {
			logger.error("user {} does not bind wx!", user.getLoginName());
			return false;
		}
		wml.setWechatid(weChatId);
		wml.setWoId(woId);
		wml.setWoStepId(currentStepId);
		WechatMessageLog wmlFromDB = wechatMessageLogMapper.fetchByProperties(wml);
		if (wmlFromDB == null) {
			wechatMessageLogMapper.insertMessageLogMapper(wml);
			return true;
		}
		if (wmlFromDB.getMessageCount() >= times) {
			return false;
		}
		wechatMessageLogMapper.updateMessageLogMapper(wmlFromDB);
		return true;
	}

	public boolean isTimeoutBak(WorkFlow workFlow, WorkflowConfig woc) {
		DateTime now = new DateTime(new Date());
		DateTime startTime = new DateTime(workFlow.getStartTime());
		Integer timeout = 0;
		// if (workFlow.getCurrentStepId() == Constans.DISPATCH.getIndex()) {
		// timeout = woc.getTimeoutDispatch();
		// } else
		if (workFlow.getCurrentStepId() == Constans.ACCEPT.getIndex()) {
			timeout = woc.getTimeoutAccept();
		} else if (workFlow.getCurrentStepId() == Constans.CLOSED.getIndex()) {
			timeout = woc.getTimeoutClose();
		}
		if (timeout > 0) {
			return Minutes.minutesBetween(startTime, now).getMinutes() > timeout;
		}
		// else if(workFlow.getCurrentStepId() == Constans.REPAIR.getIndex()){
		// //repair need not to notice
		// timeout =woc.getTimeoutRepair();
		// }
		return false;
	}

	public TimeOutRule isTimeout(WorkFlow workFlow, WorkflowConfig woc) {
		TimeOutRule tor = new TimeOutRule();
		Boolean isTimeOut = false;
		DateTime now = new DateTime(new Date());
		DateTime startTime = new DateTime(workFlow.getStartTime());
		Integer timeout = 0;
		// if (workFlow.getCurrentStepId() == Constans.DISPATCH.getIndex()) {
		// timeout = woc.getTimeoutDispatch();
		// } else
		if (workFlow.getCurrentStepId() == Constans.ACCEPT.getIndex()) {
			timeout = woc.getTimeoutAccept();
		} else if (workFlow.getCurrentStepId() == Constans.CLOSED.getIndex()) {
			timeout = woc.getTimeoutClose();
		}
		tor.setTime(timeout);

		if (timeout > 0) {
			isTimeOut = Minutes.minutesBetween(startTime, now).getMinutes() > timeout;
		}
		tor.setIsTimeout(isTimeOut);
		// else if(workFlow.getCurrentStepId() == Constans.REPAIR.getIndex()){
		// //repair need not to notice
		// timeout =woc.getTimeoutRepair();
		// }
		return tor;
	}
    public WorkflowConfig fetchWorkflowByAssetId(Integer assetId){
        if(assetId == null){
            logger.warn("assetId is null !");
            return null;
        }
        String siteUID = assetInfoRepository.findSiteUidByAssetId(assetId);
        if(siteUID == null ){
            logger.error("asset is deleted ! assetId is {}",assetId);
            return null;
        }
        return getWorkflowByOrg(siteUID);
    }
    public WorkflowConfig getWorkflowByOrg(String orgUid){
        OrgInfo orgInfo = orgInfoRepository.getOrgInfoUIDByUid(orgUid);
        if(orgInfo == null){
            logger.error("orgInfo has been deleted ! orgUid is {}",orgUid);
            return null;
        }
        TenantInfo tenantInfo = tenantInfoRepository.findByUid(orgInfo.getTenantUID());
        if(tenantInfo == null){
            logger.error("tenant has been deleted ! tenantUID is {}",orgInfo.getTenantUID());
            return null;
        }
        //如果是隔离租户，则只查询资产所属机构对应的工作流
        if(tenantInfo.getIsInsulate() != null && tenantInfo.getIsInsulate()){
            WorkflowConfig insulateConfig = workflowConfigRepository.findFirstBySiteUID(orgInfo.getSiteUID());
            if(insulateConfig == null){
                logger.warn("can't find workflowConfig in current level ! uid is {}",orgInfo.getSiteUID());
            }
            return insulateConfig;
        }
        List<WorkflowConfig> configs = workflowConfigRepository.findByTenantUID(orgInfo.getTenantUID());//fetchWorkflowConfig();
		Map<String,WorkflowConfig> siteMap = new HashMap<>();
		Map<String,WorkflowConfig> hospitalMap = new HashMap<>();
		Map<String,WorkflowConfig> institutionMap = new HashMap<>();
		Map<String,WorkflowConfig> tenantMap = new HashMap<>();
		configs.forEach(config -> {
			if (config.getSiteUID().equals(orgInfo.getSiteUID())) {
				siteMap.put(config.getSiteUID(), config);
			} else if (config.getHospitalUID().equals(orgInfo.getHospitalUID())) {
				hospitalMap.put(config.getHospitalUID(), config);
			} else if (config.getInstitutionUID().equals(orgInfo.getInstitutionUID())) {
				institutionMap.put(config.getInstitutionUID(), config);
			} else if (config.getTenantUID().equals(orgInfo.getTenantUID())) {
				tenantMap.put(config.getTenantUID(), config);
			}
		});
        if(siteMap.get(orgInfo.getSiteUID()) != null){
            return siteMap.get(orgInfo.getSiteUID());
        }else if(hospitalMap.get(orgInfo.getHospitalUID()) != null){
            return hospitalMap.get(orgInfo.getHospitalUID());
        }else if(institutionMap.get(orgInfo.getInstitutionUID()) != null){
            return institutionMap.get(orgInfo.getInstitutionUID());
        }else if(tenantMap.get(orgInfo.getTenantUID()) != null){
            return tenantMap.get(orgInfo.getTenantUID());
        }
        return null;
    }
    public int getLevel(WorkflowConfig config) {
        OrgInfo org = orgInfoRepository.getOrgInfoUIDByUid(config.getSiteUID());
        if(Objects.equals(org.getOrgType(), 0)) {
            return 1;
        }else if(Objects.equals(org.getOrgType(), 1)) {
            return 2;
        }else if(Objects.equals(org.getOrgType(), 2)) {
            return 3;
        }else if(Objects.equals(org.getOrgType(), 3)) {
            return 4;
        }
        return -1;
    }
}
