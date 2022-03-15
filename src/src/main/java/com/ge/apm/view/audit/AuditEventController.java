package com.ge.apm.view.audit;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.AuditEntryTemplateRelRepository;
import com.ge.apm.dao.AuditEventRepository;
import com.ge.apm.dao.AuditTemplateRepository;
import com.ge.apm.domain.AuditEntryTemplateRel;
import com.ge.apm.domain.AuditEvent;
import com.ge.apm.domain.AuditTemplate;
import com.ge.apm.domain.V2_BlobObject;
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

    @Override
    protected void init() {
        this.filterBySite = false;
        dao = WebUtil.getBean(AuditEventRepository.class);
    }

    @Override
    protected GenericRepositoryUUID<AuditEvent> getDAO() {
        return dao;
    }

    @Override
    protected Page<AuditEvent> loadData(PageRequest pageRequest) {
        Sort sort = new Sort(Sort.Direction.DESC, "eventTime");
        PageRequest page = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);

        List<SearchFilter> searchFilters = this.searchFilters;
        Page<AuditEvent> list = dao.findBySearchFilter(searchFilters, page);
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
}
