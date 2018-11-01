package com.ge.apm.service.common;

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.web.service.DbMessageSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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


    public static List<I18nMessage> getMessageUsingDefaultConfigAsLowestPriority(String msgType, int siteId) {

        List<I18nMessage> msgList = getMessagesByType(msgType, siteId);

        if(msgList.isEmpty() && siteId != -1){
            msgList = getMessagesByType(msgType, -1);
        }

        return msgList;
    }

    public static List<I18nMessage> getMessagesByType(String msgType, int siteId){


        Map<String, I18nMessage> cache = DbMessageSource.getMessageCache(siteId);
        if(cache==null){

            return Collections.EMPTY_LIST;
        }

//        msgType = msgType.toLowerCase();
        List<I18nMessage> msgList = new ArrayList<I18nMessage>();
        for(Map.Entry<String, I18nMessage> item: cache.entrySet()){
            if(item.getValue().getMsgType().equals(msgType)){
                msgList.add(item.getValue());
            }
        }

        Collections.sort(msgList);

        return msgList;
    }

    /**
     * except label, field_name,  msg will use msg type and msg key as its map key
     * @param msgType
     * @param msgKey
     * @param siteId
     * @return
     */
    public static I18nMessage getMessageUsingDefaultConfigAsLowestPriority(String msgType, String msgKey, int siteId) {
        String propertyKey = String.format("%s-%s", msgType, msgKey);
        propertyKey = propertyKey.toLowerCase();

        Map<String, I18nMessage> siteCache = DbMessageSource.getMessageCache(siteId);

        I18nMessage msg = siteCache.get(propertyKey);
        if (msg != null) {
            return msg;
        }

        Map<String, I18nMessage> rootCache = DbMessageSource.getMessageCache(-1);
        msg = rootCache.get(propertyKey);

        return msg;
    }

    /**
     * label, field_name msg type i18n message only use msg_key as its map key
     * @param msgKey
     * @param siteId
     * @return
     */
    public static I18nMessage getMessageByMsgKeyUsingDefaultConfigAsLowestPriority(String msgKey, int siteId) {
        String propertyKey = msgKey.toLowerCase();

        Map<String, I18nMessage> siteCache = DbMessageSource.getMessageCache(siteId);

        I18nMessage msg = siteCache.get(propertyKey);
        if (msg != null) {
            return msg;
        }

        Map<String, I18nMessage> rootCache = DbMessageSource.getMessageCache(-1);
        msg = rootCache.get(propertyKey);

        return msg;
    }
}
