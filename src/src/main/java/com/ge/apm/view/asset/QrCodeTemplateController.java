package com.ge.apm.view.asset;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.QrCodeTemplateElementRepository;
import com.ge.apm.dao.QrCodeTemplateRepository;
import com.ge.apm.domain.OrgInfo;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.ge.apm.domain.QrCodeTemplate;
import com.ge.apm.domain.QrCodeTemplateElement;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.QrCodeTemplateService;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;

@ManagedBean
@ViewScoped
public class QrCodeTemplateController extends GenericCRUDUUIDController<QrCodeTemplate> {

    QrCodeTemplateRepository dao = null;
    QrCodeTemplateService templateService;
    QrCodeTemplateElementRepository elementDao;
    OrgInfoRepository orgDao;

    @Override
    protected void init() {
        dao = WebUtil.getBean(QrCodeTemplateRepository.class);
        templateService = WebUtil.getBean(QrCodeTemplateService.class);
        elementDao = WebUtil.getBean(QrCodeTemplateElementRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
    }

    @Override
    protected QrCodeTemplateRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<QrCodeTemplate> loadData(PageRequest pageRequest) {

        this.setSiteFilter();

        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    public String getEditLink() {
        return "CreateTemplate.xhtml?faces-redirect=true&action=Edit&templateId=" + selected.getId();
    }

    public String getPreViewBase64() {
        return selected == null ? "" : templateService.getPreViewBase64(selected.getId());
    }

    @Transactional
    public void deleteTemplate() {
        if (selected.isIsUsing()) {
            WebUtil.addErrorMessage("不能删除当前使用模版");
        } else {
            List<QrCodeTemplateElement> elements = templateService.getElementsByTemplate(selected.getId());
            elementDao.delete(elements);
            dao.delete(selected);
        }
    }

    @Transactional
    public void setToUsing(String templateId) {
        QrCodeTemplate usingTemplate = dao.findById(templateId);
        List<SearchFilter> templateFilters = new ArrayList();
        templateFilters.add(new SearchFilter("isUsing", SearchFilter.Operator.EQ, true));
        templateFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, usingTemplate.getSiteUID()));
        List<QrCodeTemplate> usingList = dao.findBySearchFilter(templateFilters);
        usingList.forEach(item -> item.setIsUsing(false));
        dao.save(usingList);
        usingTemplate.setIsUsing(true);
        dao.save(usingTemplate);
    }
    
    public List<OrgInfo> getHospitalFileterList(){
        UserAccount user = UserContextService.getCurrentUserAccount();
        return  orgDao.findByTenantUIDAndOrgType(user.getTenantUID(), user.getOrgLevel());
    }
    
    public String getHospitalName(String orgUid) {
        return orgDao.getByUid(orgUid).getName();
    }

}
