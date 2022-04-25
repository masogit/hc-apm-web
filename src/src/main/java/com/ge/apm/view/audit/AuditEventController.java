package com.ge.apm.view.audit;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.AuditEntryTemplateRelRepository;
import com.ge.apm.dao.AuditEventRepository;
import com.ge.apm.dao.AuditTemplateRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.audit.AuditTemplateService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 503266962
 */
@ManagedBean
@ViewScoped
public class AuditEventController extends GenericCRUDUUIDController<AuditEvent> {


    private AuditEventRepository dao;

    private I18nMessageRepository i18nDao;

    private List<I18nMessage> eventType;
    private List<I18nMessage> eventResult;

    @Override
    protected void init() {
        this.filterBySite = false;
        dao = WebUtil.getBean(AuditEventRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        eventType = i18nDao.getByMsgType("auditEvent");
        eventResult = i18nDao.getByMsgType("auditEventResult");
    }

    @Override
    protected GenericRepositoryUUID<AuditEvent> getDAO() {
        return dao;
    }

    @Override
    protected Page<AuditEvent> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;
        Page<AuditEvent> list = dao.findBySearchFilter(searchFilters, pageRequest);
        return list;
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
    }

    public void setSelected(String uid) {
        AuditEvent auditEvent = dao.findById(uid);
        super.setSelected(auditEvent);
    }

    public List<I18nMessage> getEventType() {
        return eventType;
    }

    public void setEventType(List<I18nMessage> eventType) {
        this.eventType = eventType;
    }

    public List<I18nMessage> getEventResult() {
        return eventResult;
    }

    public void setEventResult(List<I18nMessage> eventResult) {
        this.eventResult = eventResult;
    }
}
