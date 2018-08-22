package com.ge.apm.view;

import com.ge.apm.domain.AttrDesc;
import com.ge.apm.domain.Attribute;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.misc.AttributeListService;
import com.ge.apm.service.uaa.UserAccountService;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.context.RequestContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@ManagedBean
@ViewScoped
public class AttributeListController extends GenericCRUDUUIDController<Attribute> {

    private Logger log = Logger.getLogger(AttributeListController.class.getName());

    private AttributeListService attributeListService;

    private UserAccountService userAccountService;

    @Override
    protected GenericRepositoryUUID<Attribute> getDAO() {
        return attributeListService.getAttributeRepository();
    }

    @Override
    protected void init() {
        attributeListService = WebUtil.getBean(AttributeListService.class);
        userAccountService = WebUtil.getBean(UserAccountService.class);
    }

    @Override
    protected Page<Attribute> loadData(PageRequest pageRequest) {
        if(super.searchFilters == null) {
            super.searchFilters = new ArrayList<>();
        }

        UserAccount currentUser = UserContextService.getCurrentUserAccount();
        super.updateSearchFilter(new SearchFilter("tenantUid", SearchFilter.Operator.EQ, currentUser.getTenantUID(), "1*5+"));

        Page<Attribute> attributeList;
        if(searchFilters == null) {
            attributeList = attributeListService.findAllAttribute(pageRequest);
        } else {
            attributeList = attributeListService.findAttrsBySearchFilters(searchFilters, pageRequest);
        }
        return attributeList;
    }



    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        Date now = new Date();
        UserAccount currentUser = UserContextService.getCurrentUserAccount();
        super.selected.setCreatedBy(currentUser.getUid());
        super.selected.setCreatedDate(now);
        super.selected.setLastModifiedBy(currentUser.getUid());
        super.selected.setLastModifiedDate(now);
        super.selected.setTenantUid(currentUser.getTenantUID());
        super.selected.setInstitutionUid(currentUser.getInstitutionUID());
        super.selected.setHospitalUid(currentUser.getHospitalUID());
        super.selected.setSiteUid(currentUser.getSiteUID());
    }

    public void prepareEdit() {
        super.prepareEdit();
        Date now = new Date();
        UserAccount currentUser = UserContextService.getCurrentUserAccount();
        super.selected.setLastModifiedBy(currentUser.getUid());
        super.selected.setLastModifiedDate(now);
    }

    public void checkAndSave() {
        if(attributeListService.isAttributeExisted(super.selected)) {
            log.info("Attribute already existed");
            WebUtil.addErrorMessage("属性已存在");
            return;
        } else {
            super.save();
        }
    }

    public void deleteSelectedAttribute() {
        attributeListService.deleteAttrDescByAttributeId(super.selected.getId());
        super.delete();
    }

    public void editAttrDesc(Attribute attribute) {
        String attributeName = "";
        try {
            attributeName = URLEncoder.encode(attribute.getName(), "GBK");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String pageName = "AttrDescList";
        StringBuilder parameters = new StringBuilder()
                .append("action=EditAttrDesc").append("&")
                .append("selectedId=").append(attribute.getId()).append("&")
                .append("attrName=").append(attributeName);
        String url = pageName + ".xhtml?query=" + UrlParamUtil.encodeUrlQueryString(parameters.toString());
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
        requestContext.addCallbackParam("pageName", pageName);
    }

    public void onSelectAttribute() {
        log.info("selected: " + super.selected.getId());
        List<AttrDesc> attrDescList = attributeListService.findAttrDescByAttributeId(super.selected.getId());
        attrDescList.stream().map(item -> item.getName()).forEach(System.out::println);
    }

    public String findUserNameByAccountUid(String uid) {
        return userAccountService.getLoginNameByUid(uid);
    }

}
