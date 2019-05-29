package com.ge.apm.view.measuring;

import com.ge.apm.dao.MeasureOrderRuleCheckListRepository;
import com.ge.apm.dao.PmOrderRuleCheckListRepository;
import com.ge.apm.domain.MeasureOrderRuleCheckList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

@ManagedBean
@ViewScoped
public class MeasureRuleCheckListController extends JpaCRUDController<MeasureOrderRuleCheckList> {

	private static final long serialVersionUID = -1L;
    MeasureOrderRuleCheckListRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(MeasureOrderRuleCheckListRepository.class);
    }

    @Override
    protected MeasureOrderRuleCheckListRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<MeasureOrderRuleCheckList> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<MeasureOrderRuleCheckList> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(MeasureOrderRuleCheckList object) {
    }
    
    @Override
    public void onAfterNewObject(MeasureOrderRuleCheckList object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(MeasureOrderRuleCheckList object) {
    }
    
    @Override
    public void onAfterUpdateObject(MeasureOrderRuleCheckList object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(MeasureOrderRuleCheckList object) {
    }
    
    @Override
    public void onAfterDeleteObject(MeasureOrderRuleCheckList object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(MeasureOrderRuleCheckList object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}