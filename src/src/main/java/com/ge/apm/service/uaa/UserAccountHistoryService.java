package com.ge.apm.service.uaa;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ge.apm.config.MQConfig;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.UserTagsMapper;
import com.ge.apm.domain.*;
import com.ge.apm.mq.MqUtils;
import com.ge.healthcare.cn.common.mq.MqContext;
import com.ge.healthcare.cn.common.utils.bean.BeanChangeUtil;
import com.ge.healthcare.cn.common.utils.bean.HistoryInfo;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author lyh
 * @version 1.0
 * @date 2021/5/19
 */
@Slf4j
@Service
public class UserAccountHistoryService {
    private static final String USER_POSITION = "userPosition";

    @Autowired
    private UserAccountHistoryRepository historyRepository;

    @Autowired
    private UserAccountRepository accountRepository;

    @Autowired
    private OrgInfoRepository orgInfoRepository;

    @Autowired
    private I18nMessageRepository messageRepository;

    @Autowired
    private UserOrgInfoRepository userOrgInfoRepository;

    @Autowired
    private UserTagsMapper userTagsMapper;

    @Autowired
    private MqUtils mqUtils;

    /**
     * 保存用户修改记录
     *
     * @param historyAction 历史记录操作
     * @param userAccount   更新的用户信息
     */
    public void save(String historyAction, UserAccount userAccount) {
        try {
            UserAccountHistory history = new UserAccountHistory();
            history.setUserUid(userAccount.getUid());
            history.setCreatedBy(userAccount.getLastModifiedBy());
            history.setCreatedDate(userAccount.getLastModifiedDate());
            history.setOperationType(historyAction);
            history.setOrgName(orgInfoRepository.getDepName(userAccount.getOrgInfoId()));
            history.setLoginName(userAccount.getLoginName());
            history.setName(userAccount.getName());
            history.setEmail(userAccount.getEmail());
            history.setTelephone(userAccount.getTelephone());
            history.setIsActive(Boolean.toString(userAccount.getIsActive()));
            history.setIsLocked(Boolean.toString(userAccount.getIsLocked()));
            I18nMessage i18nMessage = messageRepository.getByMsgTypeAndMsgKey(USER_POSITION, userAccount.getPosition());
            history.setPosition(i18nMessage == null ? null : i18nMessage.getValueZh());
            history.setComment(userAccount.getComment());
            history.setHospitalUID(userAccount.getHospitalUID());
            history.setInstitutionUID(userAccount.getInstitutionUID());
            history.setSiteUID(userAccount.getSiteUID());
            history.setTenantUID(userAccount.getTenantUID());
            List<HistoryInfo> historyInfos = ListUtil.newArrayList();
            HistoryInfo.TypeAction typeAction = HistoryInfo.TypeAction.valueOf(historyAction);
            switch (typeAction) {
                case CREATE:
                case IMPORT_CREATE:
                    break;
                case UPDATE:
                    packUpdateUserHistory(userAccount, history, historyInfos);
                    break;
                case DELETE:
                    packDeleteHistory(userAccount, history, historyInfos);
                    break;
                case UPDATE_ROLE:
                    packUpdateRoleHistory(userAccount, historyInfos);
                    break;
                case UPDATE_ORG:
                    packUpdateOrgHistory(userAccount, historyInfos);
                    break;
                case IMPORT_UPDATE:
                    packImportUpdateHistory(userAccount, history, historyInfos);
                    break;
                case IMPORT_UPDATE_ROLE:
                    packImportUpdateRoleHistory(userAccount, historyInfos);
                    break;
                case IMPORT_UPDATE_ORG:
                    packImportUpdateOrgHistory(userAccount, historyInfos);
                    break;
                case IMPORT_CREATE_ROLE:
                    packImportCreateRoleHistory(userAccount, historyInfos);
                    break;
                case IMPORT_CREATE_ORG:
                    packImportCreateOrgHistory(userAccount, historyInfos);
                    break;
                default:
                    UserAccount oldUserAccount = accountRepository.getById(userAccount.getId());
                    packUpdateDefaultHistory(oldUserAccount, userAccount, historyInfos);
                    break;
            }

            history.setJsonData(JSON.toJSONString(historyInfos, SerializerFeature.WriteMapNullValue));
            history.setUserJson(JSON.toJSONString(userAccount));
            historyRepository.save(history);
            saveUserHistory(history);
        } catch (Exception e) {
            log.error("UserAccountHistoryService.save() running error,errorMessage：" + e.getMessage(), e);
        }
    }

    public void packImportUpdateHistory(UserAccount userAccount, UserAccountHistory history, List<HistoryInfo> historyInfos) {
        UserAccount oldUserAccount = accountRepository.getById(userAccount.getId());
        if (ObjectUtil.isEmpty(userAccount.getPassword())) {
            userAccount.setPassword(oldUserAccount.getPassword());
            userAccount.setPwdSalt(oldUserAccount.getPwdSalt());
        }
        packUpdateHistory(oldUserAccount, userAccount, history, historyInfos);
    }

    public void packUpdateUserHistory(UserAccount userAccount, UserAccountHistory history, List<HistoryInfo> historyInfos) {
        UserAccount oldUserAccount = accountRepository.getById(userAccount.getId());
        packUpdateHistory(oldUserAccount, userAccount, history, historyInfos);
    }

    List<String> updateFieldNames = ListUtil.newArrayList("loginName", "name", "email", "telephone", "isActive", "isLocked", "comment", "tenantUID", "institutionUID", "hospitalUID", "siteUID");

    public void packUpdateHistory(UserAccount oldUserAccount, UserAccount newUserAccount, UserAccountHistory history, List<HistoryInfo> historyInfos) {
        BeanChangeUtil.packHistoryObj(updateFieldNames, history, oldUserAccount, newUserAccount);
        if (!ObjectUtil.equal(oldUserAccount.getOrgInfoId(), newUserAccount.getOrgInfoId())) {
            history.setOrgName(orgInfoRepository.getDepName(oldUserAccount.getOrgInfoId()) + "->" + orgInfoRepository.getDepName(newUserAccount.getOrgInfoId()));
        }
        if (!ObjectUtil.equal(oldUserAccount.getPosition(), newUserAccount.getPosition())) {
            I18nMessage oldMessage = messageRepository.getByMsgTypeAndMsgKey(USER_POSITION, oldUserAccount.getPosition());
            I18nMessage newMessage = messageRepository.getByMsgTypeAndMsgKey(USER_POSITION, newUserAccount.getPosition());
            history.setPosition((oldMessage == null ? null : oldMessage.getValueZh()) + "->" + (newMessage == null ? null : newMessage.getValueZh()));
        }
        List<String> newTags = newUserAccount.getTags().stream().sorted().collect(Collectors.toList());
        List<String> oldTags = userTagsMapper.getTagsByUser(oldUserAccount.getUid()).stream().sorted().collect(Collectors.toList());
        if (!newTags.equals(oldTags)) {
            HistoryInfo tagHistory = new HistoryInfo();
            tagHistory.setFieldName("tags");
            tagHistory.setOldValue(oldTags);
            tagHistory.setNewValue(newTags);
            historyInfos.add(tagHistory);
        }
        packUpdateDefaultHistory(oldUserAccount, newUserAccount, historyInfos);
    }

    public void packImportCreateRoleHistory(UserAccount userAccount, List<HistoryInfo> historyInfos) {
        List<Integer> newRoleIds = userAccount.getUserRoleList().stream().map(UserRole::getRoleId).sorted().collect(Collectors.toList());
        HistoryInfo userRoleHistoryInfo = new HistoryInfo();
        userRoleHistoryInfo.setFieldName("userRoleList");
        userRoleHistoryInfo.setOldValue(null);
        userRoleHistoryInfo.setNewValue(newRoleIds);
        historyInfos.add(userRoleHistoryInfo);
    }

    public void packImportUpdateRoleHistory(UserAccount userAccount, List<HistoryInfo> historyInfos) {
        packRoleHistory(userAccount.getUserRoleList(), userAccount.getListImportUserRole(), historyInfos);
    }

    public void packUpdateRoleHistory(UserAccount userAccount, List<HistoryInfo> historyInfos) {
        UserAccount oldUserAccount = accountRepository.getById(userAccount.getId());
        packRoleHistory(oldUserAccount.getUserRoleList(), userAccount.getUserRoleList(), historyInfos);
    }

    public void packRoleHistory(List<UserRole> oldUserRoleList, List<UserRole> newUserRoleList, List<HistoryInfo> historyInfos) {
        // 比较用户角色是否修改
        List<Integer> oldRoleIds = oldUserRoleList.stream().map(UserRole::getRoleId).sorted().collect(Collectors.toList());
        List<Integer> newRoleIds = newUserRoleList.stream().map(UserRole::getRoleId).sorted().collect(Collectors.toList());
        if (!oldRoleIds.equals(newRoleIds)) {
            HistoryInfo userRoleHistoryInfo = new HistoryInfo();
            userRoleHistoryInfo.setFieldName("userRoleList");
            userRoleHistoryInfo.setOldValue(oldRoleIds);
            userRoleHistoryInfo.setNewValue(newRoleIds);
            historyInfos.add(userRoleHistoryInfo);
        }
    }

    public void packImportCreateOrgHistory(UserAccount userAccount, List<HistoryInfo> historyInfos) {
        List<UserOrgInfo> newUserOrgInfos = userAccount.getListUserOrgInfo();
        List<String> newOrgUids = newUserOrgInfos.stream().map(UserOrgInfo::getOrgUid).sorted().collect(Collectors.toList());
        if (ObjectUtil.isNotEmpty(newOrgUids)) {
            HistoryInfo userRoleHistoryInfo = new HistoryInfo();
            userRoleHistoryInfo.setFieldName("userOrgList");
            userRoleHistoryInfo.setOldValue(null);
            userRoleHistoryInfo.setNewValue(newOrgUids);
            historyInfos.add(userRoleHistoryInfo);
        }
    }

    public void packImportUpdateOrgHistory(UserAccount userAccount, List<HistoryInfo> historyInfos) {
        List<UserOrgInfo> oldUserOrgInfos = userAccount.getListUserOrgInfo();
        List<UserOrgInfo> newUserOrgInfos = userAccount.getListImportUserOrgInfo();
        if (ObjectUtil.isNotEmpty(newUserOrgInfos)) {
            packOrgHistory(oldUserOrgInfos, newUserOrgInfos, historyInfos);
        }
    }

    public void packUpdateOrgHistory(UserAccount userAccount, List<HistoryInfo> historyInfos) {
        List<UserOrgInfo> oldUserOrgInfos = userOrgInfoRepository.findOrgUidListByUserUid(userAccount.getUid());
        List<UserOrgInfo> newUserOrgInfos = userAccount.getListUserOrgInfo();
        packOrgHistory(oldUserOrgInfos, newUserOrgInfos, historyInfos);
    }

    public void packOrgHistory(List<UserOrgInfo> oldUserOrgInfos, List<UserOrgInfo> newUserOrgInfos, List<HistoryInfo> historyInfos) {
        // 比较访问权限是否修改
        List<String> oldOrgUids = oldUserOrgInfos.stream().map(UserOrgInfo::getOrgUid).sorted().collect(Collectors.toList());
        List<String> newOrgUids = newUserOrgInfos.stream().map(UserOrgInfo::getOrgUid).sorted().collect(Collectors.toList());
        if (!oldOrgUids.equals(newOrgUids)) {
            HistoryInfo userRoleHistoryInfo = new HistoryInfo();
            userRoleHistoryInfo.setFieldName("userOrgList");
            userRoleHistoryInfo.setOldValue(oldOrgUids);
            userRoleHistoryInfo.setNewValue(newOrgUids);
            historyInfos.add(userRoleHistoryInfo);
        }
    }

    private void packDeleteHistory(UserAccount userAccount, UserAccountHistory history, List<HistoryInfo> historyInfos) {
        String loginName = history.getLoginName();
        history.setLoginName(loginName.substring(0, loginName.length() - 6) + "->" + loginName);
        String telephone = userAccount.getTelephone();
        HistoryInfo deleteHistory = new HistoryInfo();
        deleteHistory.setFieldName("isDelete");
        deleteHistory.setOldValue(false);
        deleteHistory.setNewValue(true);
        historyInfos.add(deleteHistory);
        if (ObjectUtil.isNotEmpty(telephone)) {
            history.setTelephone(telephone + "->" + "null");
            HistoryInfo telephoneHistory = new HistoryInfo();
            telephoneHistory.setFieldName("telephone");
            telephoneHistory.setOldValue(telephone);
            telephoneHistory.setNewValue(null);
            historyInfos.add(telephoneHistory);
        }
        String weChatId = userAccount.getWeChatId();
        if (ObjectUtil.isNotEmpty(weChatId)) {
            HistoryInfo telephoneHistory = new HistoryInfo();
            telephoneHistory.setFieldName("weChatId");
            telephoneHistory.setOldValue(weChatId);
            telephoneHistory.setNewValue(null);
            historyInfos.add(telephoneHistory);
        }
    }

    private void packUpdateDefaultHistory(UserAccount oldUserAccount, UserAccount newUserAccount, List<HistoryInfo> historyInfos) {
        historyInfos.addAll(BeanChangeUtil.contrastObj(oldUserAccount, newUserAccount));
    }

    public void saveUserHistory(UserAccountHistory history) {
        Locale locale = LocaleContextHolder.getLocale();
        MqContext mqContext = new MqContext();
        mqContext.setTime(DateUtil.now());
        mqContext.setType(MqContext.Type.USER_HISTORY.name());
        mqContext.setExChange(MQConfig.USER_CHANGE);
        mqContext.setClassName(this.getClass().getSimpleName());
        mqContext.setServiceName("hc-apm-web");
        mqContext.setMethodName("saveUserAccount");
        mqContext.setDescribe("保存用户信息");
        mqContext.setLocale(locale.getLanguage());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userAccountHistory", history);
        mqContext.setJsonObject(jsonObject);
        mqUtils.sendToMq(mqContext);
    }

    public void updateNewUser(String userUid, String loginName) {
        historyRepository.updateNewUser(userUid, loginName);
    }
}