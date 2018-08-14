package com.ge.apm.view;

import com.ge.apm.domain.Attribute;
import com.ge.apm.service.misc.AttributeListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepository;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;
import java.util.List;

@ManagedBean
@ViewScoped
public class AttributeListController extends JpaCRUDController<Attribute> {

    private AttributeListService attributeListService;

    @Override
    protected GenericRepository<Attribute> getDAO() {
        return null;
    }

    @Override
    protected void init() {
        attributeListService = WebUtil.getServiceBean(AttributeListService.class);
    }

    @Override
    protected Page<Attribute> loadData(PageRequest pageRequest) {
        Page<Attribute> attributeList = attributeListService.findAll(pageRequest);
        return attributeList;
    }

}
