package com.ge.apm.service.uaa;

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.UserAccountHistoryRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserAccountHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.web.WebUtil;

/**
 * @author lyh
 * @version 1.0
 * @date 2021/5/19
 */
@Slf4j
@Service
public class UserAccountHistoryService {

    @Autowired
    private UserAccountHistoryRepository historyRepository;

    @Autowired
    private UserAccountRepository accountRepository;

    @Autowired
    private OrgInfoRepository orgInfoRepository;

    @Autowired
    private I18nMessageRepository messageRepository;

    /**
     * 保存用户修改记录
     *
     * @param persistAction
     * @param userAccount
     */
    public void save(WebUtil.PersistAction persistAction, UserAccount userAccount) {
        try {
            UserAccountHistory history = new UserAccountHistory();
            history.setCreatedBy(userAccount.getLastModifiedBy());
            history.setCreatedDate(userAccount.getLastModifiedDate());
            String operationType = "";
            if (persistAction == WebUtil.PersistAction.CREATE) {
                operationType = "新增";
            } else if (persistAction == WebUtil.PersistAction.DELETE) {
                operationType = "删除";
            } else if (persistAction == WebUtil.PersistAction.UPDATE) {
                operationType = "修改";
            }
            history.setOperationType(operationType);
            history.setOrgName(orgInfoRepository.getDepName(userAccount.getOrgInfoId()));
            history.setLoginName(userAccount.getLoginName());
            history.setName(userAccount.getName());
            history.setEmail(userAccount.getEmail());
            history.setTelephone(userAccount.getTelephone());
            history.setIsActive(userAccount.getIsActive() ? "是" : "否");
            history.setIsLocked(userAccount.getIsLocked() ? "是" : "否");
            I18nMessage i18nMessage = messageRepository.getByMsgTypeAndMsgKey("userPosition", userAccount.getPosition());
            history.setPosition(i18nMessage == null ? null : i18nMessage.getValueZh());
            history.setComment(userAccount.getComment());
            history.setHospitalUID(userAccount.getHospitalUID());
            history.setInstitutionUID(userAccount.getInstitutionUID());
            history.setSiteUID(userAccount.getSiteUID());
            history.setTenantUID(userAccount.getTenantUID());
            if (persistAction == WebUtil.PersistAction.UPDATE) {
                UserAccount oldUserAccount = accountRepository.getById(userAccount.getId());
                if (!isEqual(oldUserAccount.getOrgInfoId(), userAccount.getOrgInfoId())) {
                    history.setOrgName(orgInfoRepository.getDepName(oldUserAccount.getOrgInfoId()) + "->" + orgInfoRepository.getDepName(userAccount.getOrgInfoId()));
                }
                if (!isEqual(oldUserAccount.getLoginName(), userAccount.getLoginName())) {
                    history.setLoginName(oldUserAccount.getLoginName() + "->" + userAccount.getLoginName());
                }
                if (!isEqual(oldUserAccount.getName(), userAccount.getName())) {
                    history.setName(oldUserAccount.getName() + "->" + userAccount.getName());
                }
                if (!isEqual(oldUserAccount.getEmail(), userAccount.getEmail())) {
                    history.setEmail(oldUserAccount.getEmail() + "->" + userAccount.getEmail());
                }
                if (!isEqual(oldUserAccount.getTelephone(), userAccount.getTelephone())) {
                    history.setTelephone(oldUserAccount.getTelephone() + "->" + userAccount.getTelephone());
                }
                if (oldUserAccount.getIsActive() != userAccount.getIsActive()) {
                    history.setIsActive((oldUserAccount.getIsActive() ? "是" : "否") + "->" + (userAccount.getIsActive() ? "是" : "否"));
                }
                if (oldUserAccount.getIsLocked() != userAccount.getIsLocked()) {
                    history.setIsLocked((oldUserAccount.getIsLocked() ? "是" : "否") + "->" + (userAccount.getIsLocked() ? "是" : "否"));
                }
                if (!isEqual(oldUserAccount.getPosition(), userAccount.getPosition())) {
                    I18nMessage oldMessage = messageRepository.getByMsgTypeAndMsgKey("userPosition", oldUserAccount.getPosition());
                    I18nMessage newMessage = messageRepository.getByMsgTypeAndMsgKey("userPosition", userAccount.getPosition());
                    history.setPosition((oldMessage == null ? null : oldMessage.getValueZh()) + "->" + (newMessage == null ? null : newMessage.getValueZh()));
                }
                if (!isEqual(oldUserAccount.getComment(), userAccount.getComment())) {
                    history.setComment(oldUserAccount.getComment() + "->" + userAccount.getComment());
                }
                if (!isEqual(oldUserAccount.getHospitalUID(), userAccount.getHospitalUID())) {
                    history.setHospitalUID(oldUserAccount.getHospitalUID() + "->" + userAccount.getHospitalUID());
                }
                if (!isEqual(oldUserAccount.getInstitutionUID(), userAccount.getInstitutionUID())) {
                    history.setInstitutionUID(oldUserAccount.getInstitutionUID() + "->" + userAccount.getInstitutionUID());
                }
                if (!isEqual(oldUserAccount.getSiteUID(), userAccount.getSiteUID())) {
                    history.setSiteUID(oldUserAccount.getSiteUID() + "->" + userAccount.getSiteUID());
                }
                if (!isEqual(oldUserAccount.getTenantUID(), userAccount.getTenantUID())) {
                    history.setTenantUID(oldUserAccount.getTenantUID() + "->" + userAccount.getTenantUID());
                }
            }
            historyRepository.save(history);
        } catch (Exception e) {
            log.error("保存用户修改记录出错,错误信息：" + e.getMessage(),e);
        }
    }

    private boolean isEqual(Object oldValue, Object newValue) {
        if (oldValue == null && newValue == null) {
            return true;
        }
        if (oldValue == null && newValue != null) {
            return false;
        }
        return oldValue.equals(newValue);
    }
}