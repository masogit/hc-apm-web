package com.ge.apm.service.common;

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class I18nMessageService {

    @Autowired
    I18nMessageRepository i18nMessageDao;

    public I18nMessage getByMsgTypeAndMsgKey(String msgType, String msgKey) {
        UserAccount userAccount = UserContextService.getCurrentUserAccount();

        I18nMessage foundMsg = i18nMessageDao.getByMsgTypeAndMsgKeyAndSiteId(msgType, msgKey, userAccount.getSiteId());
        if (foundMsg == null) {
            foundMsg = i18nMessageDao.getByMsgTypeAndMsgKeyAndSiteId(msgType, msgKey, -1);
        }

        return foundMsg;
    }
}
