package com.ge.apm.view.pm;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.PmOrderRuleCheckListRepository;
import com.ge.apm.domain.PmOrderRuleCheckList;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class PmOrderRuleCheckListController extends JpaCRUDController<PmOrderRuleCheckList> {

	private static final long serialVersionUID = -1L;
	PmOrderRuleCheckListRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(PmOrderRuleCheckListRepository.class);
    }

    @Override
    protected PmOrderRuleCheckListRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<PmOrderRuleCheckList> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<PmOrderRuleCheckList> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(PmOrderRuleCheckList object) {
    }
    
    @Override
    public void onAfterNewObject(PmOrderRuleCheckList object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(PmOrderRuleCheckList object) {
    }
    
    @Override
    public void onAfterUpdateObject(PmOrderRuleCheckList object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(PmOrderRuleCheckList object) {
    }
    
    @Override
    public void onAfterDeleteObject(PmOrderRuleCheckList object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(PmOrderRuleCheckList object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}