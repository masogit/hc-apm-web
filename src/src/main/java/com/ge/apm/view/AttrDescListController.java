package com.ge.apm.view;

import com.ge.apm.domain.AttrDesc;
import com.ge.apm.domain.Attribute;
import com.ge.apm.service.misc.AttributeListService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.logging.Logger;

@ManagedBean
@ViewScoped
public class AttrDescListController extends GenericCRUDUUIDController<AttrDesc> {

    private Logger log = Logger.getLogger(AttributeListController.class.getName());

    private AttributeListService attributeListService;

    private Attribute selectedAttribute;

    @Override
    protected GenericRepositoryUUID<AttrDesc> getDAO() {
        return attributeListService.getAttrDescRepository();
    }

    public void init() {
        attributeListService = WebUtil.getBean(AttributeListService.class);
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String encodeStr = WebUtil.getRequestParameter("query");

        selectedAttribute = new Attribute();
        String attributeId = (String)UrlEncryptController.getValueFromMap(encodeStr, "selectedId");
        selectedAttribute.setId(attributeId);
        String attributeName = (String)UrlEncryptController.getValueFromMap(encodeStr, "attrName");
        try {
            attributeName = URLDecoder.decode(attributeName, "GBK");
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        selectedAttribute.setName(attributeName);

    }

    @Override
    protected Page<AttrDesc> loadData(PageRequest pageRequest) {
        Page<AttrDesc> result = null;
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }

        if(StringUtils.isNotEmpty(this.selectedAttribute.getId())) {
            searchFilters.add(new SearchFilter("attributeId", SearchFilter.Operator.EQ, this.selectedAttribute.getId()));
            result = attributeListService.findAttrDescsBySearchFilters(searchFilters, pageRequest);
        }
        return result;
    }

    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        super.selected.setAttributeId(this.selectedAttribute.getId());
        super.selected.setRequired(false);
    }

    public void prepareEdit() {
        super.prepareEdit();
    }

    public void onBeforeSave(AttrDesc attrDesc) {
        if(attributeListService.isAttrDescExistByName(attrDesc.getName())) {
            log.info("AttrDesc already existed");
        }
    }

    public void deleteSelectedAttrDesc() {
        super.delete();
    }

    public Attribute getSelectedAttribute() {
        return this.selectedAttribute;
    }

}
