package com.ge.apm.view.sysutil;

import java.io.Serializable;
import java.util.Date;
import java.util.Enumeration;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequestWrapper;

import org.hibernate.Cache;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.SqlConfigurableChartController;
import webapp.framework.web.mvc.SqlConfigurableDataController;
import webapp.framework.web.service.DbMessageSource;

@ManagedBean(name="appContextService")
@ApplicationScoped
public class AppContextService implements Serializable{
    private static final long serialVersionUID = 1L;

    private EntityManager em;
    private static String sqlPagingTemplate;

    @PostConstruct
    public void init() {
        em = WebUtil.getBean(EntityManager.class);
    }

    public static String getSqlPagingTemplate() {
        return sqlPagingTemplate;  // MySQL: " limit :#_pageStart,:#_pageSize"  PostgreSql: " limit :#_pageSize offset :#_pageStart"
    }
    public static void setSqlPagingTemplate(String sqlPagingTemplate) {
        AppContextService.sqlPagingTemplate = sqlPagingTemplate;
    }

    private static String fileUploadFolder;

    public static String getFileUploadFolder() {
        return fileUploadFolder;
    }

    public static void setFileUploadFolder(String fileUploadFolder) {
        AppContextService.fileUploadFolder = fileUploadFolder;
    }
    
    public String getQueryStringWithLocal(HttpServletRequestWrapper request, String locale){
        String queryStr = "locale="+locale;
        Enumeration paramNames = request.getParameterNames();
        while(paramNames.hasMoreElements()){
            String paramName = paramNames.nextElement().toString();
            if("locale".equals(paramName)) continue;
            
            String paramValue = request.getParameter(paramName);
            queryStr = String.format("%s&%s=%s", queryStr, paramName, paramValue);
        }

        return queryStr;
    }
    
    public void reloadI18nMessage(){
        DbMessageSource.reLoadMessages();
    }

    public void reloadChartConfig(){
        SqlConfigurableChartController.reLoadChartConfig();
    }

    public void reloadDataTableConfig(){
        SqlConfigurableDataController.reLoadDataTableConfig();
    }

    public void clearJPACache(){
//        CacheManager.clearAll();

        Session session = (Session)em.getDelegate();

        if (session != null && session.isOpen()) {
            session.clear();
        }

        SessionFactory sessionFactory = session.getSessionFactory();
        Cache cache = sessionFactory.getCache();
        if (cache != null) {
            cache.evictEntityRegions();
            cache.evictQueryRegions();
        }

    }
    
     public String getStringHeader(String str, int length) {
        if (null == str) {
            return null;
        }
        else if (str.length() <= length) {
            return str;
        }else {
            return str.substring(0,length)+"...";
        }
    }
     
     public Date getCurentDate(){
         return TimeUtil.now();
     }
}
