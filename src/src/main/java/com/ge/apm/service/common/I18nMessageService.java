package com.ge.apm.service.common;

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.web.service.DbMessageSource;

import java.util.*;
import java.util.stream.Collectors;

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

    public String getMsgLabelByMsgTypeAndMsgKey(String msgType, String msgKey) {
        UserAccount userAccount = UserContextService.getCurrentUserAccount();

        I18nMessage foundMsg = i18nMessageDao.getByMsgTypeAndMsgKeyAndSiteId(msgType, msgKey, userAccount.getSiteId());
        if (foundMsg == null) {
            foundMsg = i18nMessageDao.getByMsgTypeAndMsgKeyAndSiteId(msgType, msgKey, -1);
        }

        if (null == foundMsg) {
            return null;
        } else {
            return foundMsg.getValueZh();
        }

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

    public List<I18nMessage> getByMsgType(String msgType) {
        UserAccount userAccount = UserContextService.getCurrentUserAccount();

        List<I18nMessage> foundMsgList = i18nMessageDao.getByMsgTypeAndSiteId(msgType, userAccount.getSiteId());
        if (foundMsgList.size() == 0) {
            foundMsgList = i18nMessageDao.getByMsgTypeAndSiteId(msgType, -1);
        }

        return foundMsgList;
    }

    public void resetCustomFieldName(CustomConfigFields fields, Integer siteId){
        I18nMessage byMsgTypeAndSiteIdAndMsgKey = i18nMessageDao.findFirstByMsgKeyAndSiteId(fields.getMsgKey(), siteId);
        if (null!= byMsgTypeAndSiteIdAndMsgKey){
            i18nMessageDao.delete(byMsgTypeAndSiteIdAndMsgKey);
        }
    }

    public void changeCustomFieldName(CustomConfigFields fields, Integer siteId, String newName){
        I18nMessage byMsgTypeAndSiteIdAndMsgKey = i18nMessageDao.findFirstByMsgKeyAndSiteId(fields.getMsgKey(), siteId);

        if(null == byMsgTypeAndSiteIdAndMsgKey ){
            I18nMessage defaultField = i18nMessageDao.findFirstByMsgKeyAndSiteId(fields.getMsgKey(), -1);
            byMsgTypeAndSiteIdAndMsgKey = new I18nMessage();
            byMsgTypeAndSiteIdAndMsgKey.setMsgKey(defaultField.getMsgKey());
            byMsgTypeAndSiteIdAndMsgKey.setMsgType(defaultField.getMsgType());
            byMsgTypeAndSiteIdAndMsgKey.setSiteId(siteId);
        }
        byMsgTypeAndSiteIdAndMsgKey.setValueZh(newName);
        i18nMessageDao.save(byMsgTypeAndSiteIdAndMsgKey);

    }

    public String getSystemFieldName(CustomConfigFields fields){
        I18nMessage defaultMsg = i18nMessageDao.findFirstByMsgKeyAndSiteId(fields.getMsgKey(), -1);

        return Optional.ofNullable(defaultMsg).map(i-> i.getValueZh()).orElse(fields.getMsgKey());
    }

    public Set<I18nMessage> collectSpare01InExstingList(List<I18nMessage> i18nMessagesByName, List<I18nMessage> spare01List) {
        Set<I18nMessage> spare01Set = new HashSet<>();

        for (I18nMessage i18nMessage: i18nMessagesByName) {
            collectSpare01InExstingList(i18nMessage, spare01List, spare01Set);
        }

        return spare01Set;
    }



    public void collectSpare01InExstingList(I18nMessage i18nMessage, List<I18nMessage> spare01List, Set<I18nMessage> spare01SetOutput) {
        Set<I18nMessage> collect = spare01List.stream().filter(item -> item.getMsgKey().equals(i18nMessage.getMsgKey()) ).collect(Collectors.toSet());
        spare01SetOutput.addAll(collect);

    }
}
