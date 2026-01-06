package com.ge.apm.view;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.difyHttp.DifyStreamService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.web.difyChat.DiFyChatRequest;
import com.ge.apm.web.difyChat.DiFyChatResponse;
import org.springframework.util.StringUtils;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
public class AIKnowledgeController implements Serializable {
    
    private List<LogEntry> logEntries;
    private String newLogMessage;
    private String logLevel = "INFO";
    private boolean autoScroll = true;
    private UserAccount userAccount;
    private DifyStreamService difyStreamService;
    private boolean askEnd = true;

    @PostConstruct
    protected void init() {
        difyStreamService = WebUtil.getBean(DifyStreamService.class);
        userAccount = UserContextService.getCurrentUserAccount();
    }
    public AIKnowledgeController() {
        logEntries = new ArrayList<>();
        addLogEntry("INFO", "apm小助手准备就绪啦！", "我来啦！");
    }
    
    public void addLogMessage() {
        if (!StringUtils.hasText(newLogMessage)) {
            return;
        }
        askEnd = false;
        if (newLogMessage != null && !newLogMessage.trim().isEmpty()) {
            try {
                DiFyChatResponse result = difyStreamService.processDifyStream(new DiFyChatRequest());
                addLogEntry(logLevel, result.getResult(), newLogMessage);
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new javax.faces.application.FacesMessage(
                                javax.faces.application.FacesMessage.SEVERITY_INFO,
                                e.getMessage(),
                                null
                        ));
            }
        }
        newLogMessage = "";
        askEnd = true;
    }
    
    public void clearLogs() {
        logEntries.clear();
    }
    
    private void addLogEntry(String level, String message, String userPutMessage) {
        LogEntry entry = new LogEntry();
        entry.setTimestamp(new Date());
        entry.setLevel(level);
        entry.setMessage(message);
        entry.setUserPutMessage(userPutMessage);
        logEntries.add(entry);
    }
    
    // Getter 和 Setter 方法
    public List<LogEntry> getLogEntries() { return logEntries; }
    public void setLogEntries(List<LogEntry> logEntries) { this.logEntries = logEntries; }
    
    public String getNewLogMessage() { return newLogMessage; }
    public void setNewLogMessage(String newLogMessage) { this.newLogMessage = newLogMessage; }
    
    public String getLogLevel() { return logLevel; }
    public void setLogLevel(String logLevel) { this.logLevel = logLevel; }
    
    public boolean isAutoScroll() { return autoScroll; }
    public void setAutoScroll(boolean autoScroll) { this.autoScroll = autoScroll; }

    public static class LogEntry implements Serializable {
        private Date timestamp;
        private String level;
        private String message;
        private String userPutMessage;

        public String getLevelColor() {
            return "";
        }
        
        public String getLevelIcon() {
            return "";
        }
        
        // Getter 和 Setter
        public Date getTimestamp() { return timestamp; }
        public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
        
        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getUserPutMessage() {
            return userPutMessage;
        }

        public void setUserPutMessage(String userPutMessage) {
            this.userPutMessage = userPutMessage;
        }
    }
}
