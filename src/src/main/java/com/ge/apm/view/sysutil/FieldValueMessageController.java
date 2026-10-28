package com.ge.apm.view.sysutil;

import com.ge.apm.dao.AssetFaultTypeRepository;
import com.ge.apm.domain.AssetFaultType;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.RoleMsgRel;
import com.ge.apm.domain.UserAccount;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import webapp.framework.web.WebUtil;
import webapp.framework.web.service.DbMessageSource;
import webapp.framework.web.service.DbResourcesUtil;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static webapp.framework.web.WebUtil.getServiceBean;

@ManagedBean(name = "fieldMsg")
@ApplicationScoped
public class FieldValueMessageController implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(FieldValueMessageController.class);

    public String msgValueZh(String msgKey) {
        DbResourcesUtil resourcesUtil = (DbResourcesUtil) getServiceBean(DbResourcesUtil.class);
        return resourcesUtil.getMsgValue(msgKey, new Locale("zh", "CN"));
    }

    public static String doGetFieldValue(String fieldName, String msgKey) {
        String key = fieldName + "-" + msgKey;
        String value = WebUtil.getMessage(key);

        if (key.equals(value))
            return "";  // this means the message not found.
        else
            return value;
    }

    public static String fieldValue(String fieldName, String msgKey) {
//        if(fieldName.equals("riskGradeNew") && msgKey.equals("40")) {  //风险等级新旧规则映射
//            msgKey = "30";
//        }
        return doGetFieldValue(fieldName, msgKey);
    }

    public static List<I18nMessage> getFieldValueListByMsgType(String fieldName) {
        Integer instId = -1;
        try {
            instId = UserContextService.getCurrentUserAccount().getSiteId();
        } catch (Exception ex) {
        }

        return getFieldValueList(fieldName, instId);
    }

    public static List<I18nMessage> getMeasureGradeList(){
        List<I18nMessage> msgList = getFieldValueListByMsgType("measureGrade");
        I18nMessage tempI18m = null;
        for (I18nMessage i18m : msgList) {
            if("4".equals(i18m.getMsgKey())){
                tempI18m = i18m;
            }
        }
        msgList.remove(tempI18m);
        return msgList;
    }

    // 获取当前已启用的I18n列表
    public static List<I18nMessage> getEnabledFieldValueList(String fieldName) {
        return getFieldValueList(fieldName).stream().filter(I18nMessage::getEnabled).collect(Collectors.toList());
    }

    public static List<I18nMessage> getFieldValueList(String fieldName) {
        Integer instId = -1;
        try {
            instId = UserContextService.getCurrentUserAccount().getSiteId();
        } catch (Exception ex) {
        }
        return getFieldValueList(fieldName, instId);
    }

    @Autowired
    AssetFaultTypeRepository assetFaultTypeRepository;

    public List<AssetFaultType> getFieldValueList_bak(String fieldName) {
        Integer instId = -1;//glnote_java

        List<AssetFaultType> byAssetGroupId = assetFaultTypeRepository.getByAssetGroupId(1);
        // instId = UserContextService.getCurrentUserAccount().getSiteId();

        return byAssetGroupId;
    }

    public static List<I18nMessage> getFieldValueList(String fieldName, int institutionId) {
        List<I18nMessage> msgList = doGetFieldValueList(fieldName, institutionId);
        if (msgList.isEmpty() && institutionId != -1) {
            msgList = doGetFieldValueList(fieldName, -1);
        }
        //msgList = msgList.stream().sorted(Comparator.comparing(I18nMessage::getMsgKey)).collect(Collectors.toList());

        return msgList;
    }

    public static List<I18nMessage> doGetFieldValueList(String fieldName, int institutionId) {
        List<I18nMessage> msgList = new ArrayList<I18nMessage>();

        Map<String, I18nMessage> cache = DbMessageSource.getMessageCache(institutionId);
        if (cache == null) {
            logger.warn("message cache not found, institutionId=" + institutionId);
            return msgList;
        }

        fieldName = fieldName.toLowerCase();
        for (Map.Entry<String, I18nMessage> item : cache.entrySet()) {
            if (item.getValue().getMsgType().toLowerCase().equals(fieldName)) {
                msgList.add(item.getValue());
            }
        }
        msgList.sort(Comparator.comparing(I18nMessage::getOrderBy, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(I18nMessage::getMsgKey));

//        Collections.sort(msgList);

        return msgList;
    }

    public void reloadMessages() {
        DbMessageSource.reLoadMessages();
        WebUtil.addSuccessMessage("Messages reloaded!");
    }


    public static List<I18nMessage> getI18nByBatchFileType() {
        UserContextService userService = WebUtil.getBean(UserContextService.class);
        UserAccount ua = userService.getCurrentUserAccount();
        List<RoleMsgRel> roleMsgRels = userService.getRoleMsgInfos(ua.getId());

        boolean dataMainStaff = hasRole(roleMsgRels, "DataMaintainStaff");
        boolean geInternalStaff = hasRole(roleMsgRels, "GEInternalStaff");

        List<I18nMessage> allMsgList = new ArrayList<>();

        if (ua.getIsGeOp()) {
            List<I18nMessage> msgList1 = getI18nMsgList(userService, "GeOp");
            allMsgList.addAll(msgList1);
        }
        if (geInternalStaff) {
            List<I18nMessage> msgList2 = getI18nMsgList(userService, "GEInternalStaff");
            allMsgList.addAll(msgList2);
        }
        if (dataMainStaff) {
            List<I18nMessage> msgList3 = getI18nMsgList(userService, "DataMaintainStaff");
            allMsgList.addAll(msgList3);
        }
        return allMsgList;
    }


    public static List<I18nMessage> getI18nMsgList(UserContextService service, String roleName) {
        List<I18nMessage> msgList = null;
        List<RoleMsgRel> roleMsgs = service.getRoleMsgInfosByRoleName(roleName);
        if (CollectionUtils.isNotEmpty(roleMsgs)) {
            msgList = new ArrayList<>();
            for (RoleMsgRel r : roleMsgs) {
                I18nMessage msg = service.getByMsgTypeAndMsgKey(r.getMsgType(), r.getMsgKey());
                if (msg != null)
                    msgList.add(msg);
            }
        }
        return msgList;
    }

    public static boolean hasRole(List<RoleMsgRel> roleMsgRels, String roleName) {
        if (CollectionUtils.isNotEmpty(roleMsgRels)) {
            for (int i = 0; i < roleMsgRels.size(); i++) {
                String name = roleMsgRels.get(i).getRoleName();
                if (name.equalsIgnoreCase(roleName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String fieldValues(String fieldNames, String msgKey){
        String[] msgTypes = fieldNames.split(",");
        for (String msgType: msgTypes) {
            String msgKeyZh = fieldValue(msgType, msgKey);
            if(StringUtils.isNotBlank(msgKeyZh))
                return msgKeyZh;
        }
        return null;
    }

}
