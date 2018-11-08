package com.ge.apm.domain;


public class MsgConfigVO {
    private Integer id;
    private String msgNameEn;
    private String msgName;
    private Boolean isDisable = false;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }



    public Boolean getDisable() {
        return isDisable;
    }

    public String getMsgNameEn() {
        return msgNameEn;
    }

    public void setMsgNameEn(String msgNameEn) {
        this.msgNameEn = msgNameEn;
    }

    public String getMsgName() {
        return msgName;
    }

    public void setMsgName(String msgName) {
        this.msgName = msgName;
    }

    public void setDisable(Boolean disable) {
        isDisable = disable;
    }
}
