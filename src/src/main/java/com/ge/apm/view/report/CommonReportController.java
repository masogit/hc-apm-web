package com.ge.apm.view.report;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TableDefinitionRepository;
import com.ge.apm.dao.UnstructuredDataCollectionRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TableDefinition;
import com.ge.apm.domain.UnstructuredDataCollection;
import com.ge.apm.service.pm.V2_PmOrderService;
import com.ge.apm.service.report.TableDefinitionService;

import webapp.framework.web.WebUtil;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class CommonReportController {

    private String reportBaseUid;

    private TableDefinitionRepository tableDao;

    private OrgInfoRepository orgDao;

    private UnstructuredDataCollectionRepository reportDao;

    private TableDefinitionService tableService;
    
    private V2_PmOrderService v2PmOrderService;

    @PostConstruct
    protected void init() {
        tableService = WebUtil.getBean(TableDefinitionService.class);
        tableDao = WebUtil.getBean(TableDefinitionRepository.class);
        reportDao = WebUtil.getBean(UnstructuredDataCollectionRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        v2PmOrderService = WebUtil.getBean(V2_PmOrderService.class);
    }

    public List<UnstructuredDataCollection> getReportList(String boUid) {
        return reportDao.findByBoUuid(boUid);
    }

    public List<TableDefinition> getReportTypeList(String siteUID) {
        OrgInfo site = orgDao.getByUid(siteUID);
        List<String> orgUids = Arrays.asList(site.getSiteUID(), site.getHospitalUID(), site.getInstitutionUID(), site.getTenantUID());
        return tableDao.getBySiteUIDIn(orgUids);
    }

    public void deleteReporter(String id) {
        if (null != id) {
            tableService.deleteReport(id);
        }
    }

    public void addReporter(String boUid) {
        TableDefinition table = tableDao.findByBaseId(reportBaseUid);
        UnstructuredDataCollection newReport = new UnstructuredDataCollection();
        newReport.setBoUuid(boUid);
        newReport.setSchemaId(table.getId());
        v2PmOrderService.saveReport(newReport);
    }

    public String getTemplateName(String templateId) {
        return Optional.ofNullable(tableDao.findById(templateId)).orElse(new TableDefinition()).getName();
    }

    public TableDefinition getTemplateById(String templateId) {
        return Optional.ofNullable(tableDao.findById(templateId)).orElse(new TableDefinition());
    }

    public String getEditLinkUrl(UnstructuredDataCollection report) {
        TableDefinition table = tableDao.findById(report.getSchemaId());
        if(table==null)
                return "";
        if (null != table.getExcelStorageId()) {
            return "/portal/reporter/EditExcelReport.xhtml?faces-redirect=true&reportId=" + report.getId();
        }else{
            return "/portal/reporter/EditReport.xhtml?faces-redirect=true&reportId=" + report.getId();
        }
    }

    public String getViewLinkUrl(UnstructuredDataCollection report) {
        TableDefinition table = tableDao.findById(report.getSchemaId());
        if(table==null)
            return "";
         if (null != table.getExcelStorageId()) {
            return "/portal/reporter/ViewExcelReport.xhtml?faces-redirect=true&reportId=" + report.getId();
        }else{
            return "/portal/reporter/ViewReport.xhtml?faces-redirect=true&reportId=" + report.getId();
        }
    }

    public String getReportBaseUid() {
        return reportBaseUid;
    }

    public void setReportBaseUid(String reportBaseUid) {
        this.reportBaseUid = reportBaseUid;
    }


}
