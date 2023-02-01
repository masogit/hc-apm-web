package com.ge.apm.service.utils;

import webapp.framework.web.service.MessageBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class MessageUtil {

    private static MessageBundle i18ns = new MessageBundle();

    public static String getReturnMessage(String keyName) {
        try {
            return i18ns.getString(keyName);
        } catch (Exception e) {
            return keyName;
        }
    }
    public static void outPutReturnFacesMessage(String keyName, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage();
        msg.setSeverity(severity);
        try {
            msg.setSummary(i18ns.getString(keyName));
        } catch (Exception e) {
            msg.setSummary(keyName);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public static void outPutReturnFacesMessage(String keyName, FacesMessage.Severity severity, String detail) {
        FacesMessage msg = new FacesMessage();
        msg.setSeverity(severity);
        try {
            msg.setSummary(i18ns.getString(keyName));
            msg.setDetail(detail);
        } catch (Exception e) {
            msg.setSummary(keyName);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
