package com.ge.apm.web.difyChat;

public class DiFyChatRequest {

    private String paramStr;

    private String userName;

    private String selectStorage;

    private String conversationId;

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getParamStr() {
        return paramStr;
    }

    public void setParamStr(String paramStr) {
        this.paramStr = paramStr;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSelectStorage() {
        return selectStorage;
    }

    public void setSelectStorage(String selectStorage) {
        this.selectStorage = selectStorage;
    }
}
