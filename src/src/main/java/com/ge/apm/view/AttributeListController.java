package com.ge.apm.view;

import com.ge.apm.domain.AttrDesc;
import com.ge.apm.domain.Attribute;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.misc.AttributeListService;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@ManagedBean
@ViewScoped
public class AttributeListController extends GenericCRUDUUIDController<Attribute> {

    private Logger log = Logger.getLogger(AttributeListController.class.getName());

    private AttributeListService attributeListService;

    @Override
    protected GenericRepositoryUUID<Attribute> getDAO() {
        return attributeListService.getAttributeRepository();
    }

    @Override
    protected void init() {
        attributeListService = WebUtil.getBean(AttributeListService.class);
    }

    @Override
    protected Page<Attribute> loadData(PageRequest pageRequest) {
        Page<Attribute> attributeList = attributeListService.findAllAttribute(pageRequest);
        return attributeList;
    }

    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        Date now = new Date();
        UserAccount currentUser = UserContextService.getCurrentUserAccount();
        super.selected.setCreateBy(currentUser.getUid());
        super.selected.setCreateDate(now);
        super.selected.setLastUpdateBy(currentUser.getUid());
        super.selected.setLastUpdateDate(now);
        super.selected.setTenantUid(currentUser.getTenantUID());
        super.selected.setInstitutionUid(currentUser.getInstitutionUID());
        super.selected.setHospitalUid(currentUser.getHospitalUID());
        super.selected.setSiteUid(currentUser.getSiteUID());
    }

    public void prepareEdit() {
        super.prepareEdit();
        Date now = new Date();
        UserAccount currentUser = UserContextService.getCurrentUserAccount();
        super.selected.setLastUpdateBy(currentUser.getUid());
        super.selected.setLastUpdateDate(now);
    }

    public void onBeforeSave(Attribute attribute) {
        if(attributeListService.isAttributeExistByName(attribute.getName())) {
            log.info("Attribute already existed");
            return;
        }
    }

    public void deleteSelectedAttribute() {
        super.delete();
    }

    public void onSelectAttribute() {
        log.info("selected: " + super.selected.getId());
        List<AttrDesc> attrDescList = attributeListService.findAttrDescByAttributeId(super.selected.getId());
        attrDescList.stream().map(item -> item.getName()).forEach(System.out::println);
    }

}
