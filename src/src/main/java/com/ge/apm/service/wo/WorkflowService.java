package com.ge.apm.service.wo;

import java.util.*;
import java.util.stream.Collectors;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.WorkflowConfigRepository;
import com.ge.apm.domain.*;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.dao.mapper.WechatMessageLogMapper;
import com.ge.apm.dao.mapper.WorkOrderMapper;
import com.ge.apm.service.utils.ConfigUtils;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.service.utils.WeiXinUtils;
import com.ge.apm.service.wechat.CoreService;

@Service
public class WorkflowService {
	Logger logger = LoggerFactory.getLogger(getClass());
	public static final int SPECIAL_DISPATCHER = 1;// 专人派工
													// work_order_dispather角色
	public static final int GRAB_DISPATCHER = 2;// 抢单 asset_staff 角色
	public static final int AUTO_DISPATCHER = 3;// 资产owner 1 2
	public static final int WORK_ORDER_STATUS_ACCEPT = 3;
	public static final int WORK_ORDER_STATUS_CLOSED = 5;

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
	CoreService coreService;

	@Autowired
	ConfigUtils configUtils;

	@Transactional
	public void execute() {
		if (logger.isDebugEnabled()) {
			logger.debug("begin check timeout and reopen……");
		}
		List<WorkOrder> orders = workOrderMapper.fetchUnFinishedWorkList();
		if (CollectionUtils.isEmpty(orders)) {
			if (logger.isInfoEnabled()) {
				logger.info("no open workOrder");
			}
			return;
		}
		for (WorkOrder workOrder : orders) {
			WorkflowConfig woc = workOrderMapper.fetchWorkflowConfig(workOrder.getSiteId(), workOrder.getHospitalId());
			if (woc == null) {
				logger.error("fetch WorkflowConfig error,siteId is {},hospitalId is {}", workOrder.getSiteId(),
						workOrder.getHospitalId());
				continue;
			}
			try {
				isTimeout(workOrder, woc);
				// isReopen(workOrder,woc); // 暂不开放 2017.3.30
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
			}

		}
		if (logger.isDebugEnabled()) {
			logger.debug("end timeout and reopen……");
		}
	}

	@SuppressWarnings("unused")
	private void isReopen(WorkOrder workOrder, WorkflowConfig woc) {
		WorkOrder parent = workOrderMapper.fetchParentWorkOrder(workOrder.getParentWoId());
		if (parent == null) {
			return;
		}
		DateTime parentStartTime = new DateTime(parent.getRequestTime());
		DateTime childStartTime = new DateTime(workOrder.getRequestTime());
		List<Integer> users = null;
		if (Minutes.minutesBetween(childStartTime, parentStartTime).getMinutes() > woc.getOrderReopenTimeframe()) {
			ReopenPushModel model = new ReopenPushModel();
			model.set_assetName(workOrder.getAssetName());
			model.set_parentRequestPerson(parent.getRequestorName());
			model.set_parentRequestTime(TimeUtils.getStrDate(parent.getRequestTime(), "yyyy-MM-dd hh:mm:ss"));
			model.set_requestPerson(workOrder.getRequestorName());
			model.set_requestTime(TimeUtils.getStrDate(workOrder.getRequestTime(), "yyyy-MM-dd hh:mm:ss"));
			model.set_linkUrl(coreService.getWoDetailUrl(workOrder.getId()));
			users = new ArrayList<Integer>(5);
			users.add(workOrder.getRequestorId());
			users.add(workOrder.getCurrentPersonId());
			users.add(parent.getRequestorId());
			users.add(parent.getCurrentPersonId());
			buildReopenTemplateMsg(model, users);
		}
	}

	private void buildReopenTemplateMsg(ReopenPushModel model, List<Integer> users) {
		List<UserAccount> accounts = userAccountMapper.getUserWechatId(users);
		for (UserAccount ua : accounts) {
			coreService.sendWxTemplateMessage(ua.getWeChatId(), configUtils.fetchProperties("reopen_template_id"),
					WeiXinUtils.object2Map(model));
		}
	}

	private void isTimeout(WorkOrder workOrder, WorkflowConfig woc) {
		List<WorkFlow> workFlows = workOrderMapper.fetchWorkFlowList(workOrder);
		if (CollectionUtils.isEmpty(workFlows)) {
			logger.error("cannot find work steps , workOrder id is ", workOrder.getId());
			return;
		}
		List<Integer> users = new ArrayList<Integer>(workFlows.size() * 10);
		TimeoutPushModel model = null;
		WorkFlow lastOne = workFlows.get(0);
		String stepName = lastOne.getStepName();
		Integer woId = workOrder.getId();
		Integer currentStepId = lastOne.getCurrentStepId();
		Integer ownerId = lastOne.getCurrentPersonId();
		Integer times = woc.getMaxMessageCount();
		TimeOutRule tor = isTimeout(lastOne, woc);
		if (tor.getIsTimeout()) {
			if (currentStepId == WORK_ORDER_STATUS_ACCEPT) {
				if (ownerId == -1) {// 抢单
					List<Integer> dispatchers = userAccountMapper.fetchDispaterUser(workOrder.getRequestorId(), 3);
					users.addAll(dispatchers);
				} else if (ownerId > 0) {// 自动派工
					users.add(ownerId);

					AssetInfo asset = assetInfoMapper.fetchAssetInfoById(workOrder.getAssetId());
					if (asset != null) {
						users.add(asset.getAssetOwnerId());
						users.add(asset.getAssetOwnerId2());
					}
				}
			} else if (currentStepId == WORK_ORDER_STATUS_CLOSED) {
				users.add(lastOne.getCurrentPersonId());
			} else {
				logger.error("current step need not remind,current stepId is {},workOrderId is {}", currentStepId,
						woId);
				return;
			}
			model = new TimeoutPushModel();
			model.setFirst(stepName + "超时");
			model.setKeyword1(workOrder.getAssetName());
			model.setKeyword2(TimeUtils.getStrDate(workOrder.getRequestTime(), "yyyy-MM-dd HH:mm:ss"));
			model.setKeyword3(CasePriorityNum.getName(workOrder.getCasePriority()));
			model.setKeyword4(stepName);// Constans.getName(workOrder.getCurrentStepId())
			model.setKeyword5(workOrder.getCurrentPersonName());
			model.setLinkUrl(coreService.getWoDetailUrl(workOrder.getId()));

			users.add(workOrder.getRequestorId());// 报修人

			List<Integer> subscribers = userAccountMapper.getAssetSubscriber(workOrder.getAssetId());
			users.addAll(subscribers);
			logger.info("before filter ,users is {}", users);
			List<Integer> filterUser = users.stream().filter(id -> id != null).filter(id -> id > 0).distinct()
					.collect(Collectors.toList());
			logger.info("after filter ,users is {}", users);
			if (CollectionUtils.isEmpty(filterUser)) {
				logger.error("1workOrder timeout but no users find,orderId is {}", workOrder.getId());
				return;
			}
			Iterator<Integer> it = filterUser.iterator();
			while (it.hasNext()) {
				Integer needPushUser = it.next();
				if (!isMatchRule(needPushUser, woId, currentStepId, times, tor.getTime())) {
					logger.info("userId {} is removed because of rule!", needPushUser);
					it.remove();
				}
			}
			if (CollectionUtils.isEmpty(filterUser)) {
				logger.warn("2workOrder timeout but no users find,orderId is {}", workOrder.getId());
				return;
			}
			buildTimeoutTemplateMsg(model, filterUser);
		} else {
			logger.info(" no workorder is timeout ....");
		}

	}

	private void isTimeoutBak(WorkOrder workOrder, WorkflowConfig woc) {
		List<WorkFlow> workFlows = workOrderMapper.fetchWorkFlowList(workOrder);
		if (CollectionUtils.isEmpty(workFlows)) {
			logger.error("cannot find work steps , workOrder id is ", workOrder.getId());
			return;
		}
		List<Integer> users = new ArrayList<Integer>(workFlows.size() * 10);
		TimeoutPushModel model = null;
		WorkFlow lastOne = workFlows.get(0);
		String stepName = lastOne.getStepName();
		Integer woId = workOrder.getId();
		Integer currentStepId = lastOne.getCurrentStepId();
		Integer times = woc.getMaxMessageCount();
		if (isTimeoutBak(lastOne, woc)) {
			model = new TimeoutPushModel();
			model.setFirst(stepName + "超时");
			model.setKeyword1(workOrder.getAssetName());
			model.setKeyword2(TimeUtils.getStrDate(workOrder.getRequestTime(), "yyyy-MM-dd HH:mm:ss"));
			model.setKeyword3(CasePriorityNum.getName(workOrder.getCasePriority()));
			model.setKeyword4(stepName);// Constans.getName(workOrder.getCurrentStepId())
			model.setKeyword5(workOrder.getCurrentPersonName());
			model.setLinkUrl(coreService.getWoDetailUrl(workOrder.getId()));
			for (WorkFlow workFlow : workFlows) {
				users.add(workFlow.getCurrentPersonId());
			}
			users.add(workOrder.getRequestorId());// 报修人

			// sysRole
			if (woc.getDispatchMode() == SPECIAL_DISPATCHER) {
				List<Integer> dispatchers = userAccountMapper.fetchDispaterUser(workOrder.getRequestorId(), 8);
				users.addAll(dispatchers);
			} else if (woc.getDispatchMode() == GRAB_DISPATCHER) {
				List<Integer> dispatchers = userAccountMapper.fetchDispaterUser(workOrder.getRequestorId(), 3);
				users.addAll(dispatchers);
			} else if (woc.getDispatchMode() == AUTO_DISPATCHER) {
				AssetInfo asset = assetInfoMapper.fetchAssetInfoById(workOrder.getAssetId());
				if (asset != null) {
					users.add(asset.getAssetOwnerId());
					users.add(asset.getAssetOwnerId2());
				}
			}
			List<Integer> subscribers = userAccountMapper.getAssetSubscriber(workOrder.getAssetId());
			users.addAll(subscribers);
			logger.info("before filter ,users is {}", users);
			List<Integer> filterUser = users.stream().filter(id -> id != null).filter(id -> id > 0).distinct()
					.collect(Collectors.toList());
			logger.info("after filter ,users is {}", users);
			if (CollectionUtils.isEmpty(filterUser)) {
				logger.error("1workOrder timeout but no users find,orderId is {}", workOrder.getId());
				return;
			}
			Iterator<Integer> it = filterUser.iterator();
			while (it.hasNext()) {
				Integer needPushUser = it.next();
				if (!isMatchRuleBak(needPushUser, woId, currentStepId, times)) {
					logger.info("userId {} is removed because of rule!", needPushUser);
					it.remove();
				}
			}
			if (CollectionUtils.isEmpty(filterUser)) {
				logger.warn("2workOrder timeout but no users find,orderId is {}", workOrder.getId());
				return;
			}
			buildTimeoutTemplateMsg(model, filterUser);
		} else {
			logger.info(" no workorder is timeout ....");
		}

	}

	private void buildTimeoutTemplateMsg(TimeoutPushModel model, List<Integer> users) {
		logger.info("begin to push timeout msg");
		List<UserAccount> accounts = userAccountMapper.getUserWechatId(users);
		for (UserAccount ua : accounts) {
			coreService.sendWxTemplateMessage(ua.getWeChatId(), configUtils.fetchProperties("timeout_template_id"),
					WeiXinUtils.object2Map(model));
		}
		logger.info("push timeout over,to users is {}", users);
	}

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
        Map<String,WorkflowConfig> siteMap = new HashMap<String,WorkflowConfig>();
        Map<String,WorkflowConfig> hospitalMap = new HashMap<String,WorkflowConfig>();
        Map<String,WorkflowConfig> institutionMap = new HashMap<String,WorkflowConfig>();
        Map<String,WorkflowConfig> tenantMap = new HashMap<String,WorkflowConfig>();
        for (WorkflowConfig config : configs) {
            int level = getLevel(config);
            if(level == 1) {//租户级别
                tenantMap.put(config.getTenantUID(), config);
            }else if(level == 2) {//集团级别
                institutionMap.put(config.getInstitutionUID(), config);
            }else if(level == 3) {//医院级别
                hospitalMap.put(config.getHospitalUID(), config);
            }else if(level == 4) {//院区级别
                siteMap.put(config.getSiteUID(), config);
            }
        }
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
        if(org.getOrgType() == 0) {
            return 1;
        }else if(org.getOrgType() == 1) {
            return 2;
        }else if(org.getOrgType() == 2) {
            return 3;
        }else if(org.getOrgType() == 3) {
            return 4;
        }
        return -1;
    }
}
