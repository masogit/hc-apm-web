package com.ge.apm.view;

import com.ge.apm.domain.AttrDesc;
import com.ge.apm.service.misc.AttributeListService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;
import java.util.logging.Logger;

@ManagedBean
@ViewScoped
public class AttrDescListController extends GenericCRUDUUIDController<AttrDesc> {

    private Logger log = Logger.getLogger(AttributeListController.class.getName());

    private AttributeListService attributeListService;

    @Override
    protected GenericRepositoryUUID<AttrDesc> getDAO() {
        return attributeListService.getAttrDescRepository();
    }

    public void init() {
        attributeListService = WebUtil.getBean(AttributeListService.class);
    }

    @Override
    protected Page<AttrDesc> loadData(PageRequest pageRequest) {
        return null;
    }

    public void prepareCreate(String attributeId) throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        super.selected.setAttributeId(attributeId);
        super.selected.setRequired(false);
    }

    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        super.selected.setRequired(false);
    }

    public void prepareEdit() {
        super.prepareEdit();
    }

    public void onBeforeSave(AttrDesc attrDesc) {
        if(attributeListService.isAttrDescExistByName(attrDesc.getName())) {
            log.info("AttrDesc already existed");
            WebUtil.addErrorMessage("属性已存在");
            return;
        }
    }

    public void deleteSelectedAttrDesc() {
        super.delete();
    }

    public void onSelectAttrDesc() {
        if(super.selected != null) {
            log.info("selected: " + super.selected.getName());
        }
    }

}
