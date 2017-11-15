package com.ge.apm.view.pm;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.PmOrderRuleItemRepository;
import com.ge.apm.domain.PmOrderRuleItem;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class PmOrderRuleItemController extends JpaCRUDController<PmOrderRuleItem> {

	private static final long serialVersionUID = -1L;
	
    private PmOrderRuleItemRepository dao = null;
	private UserAccount user;
	
    @Override
    protected void init() {
        dao = WebUtil.getBean(PmOrderRuleItemRepository.class);
        user = UserContextService.getCurrentUserAccount();
    }

    @Override
    protected PmOrderRuleItemRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<PmOrderRuleItem> loadData(PageRequest pageRequest) {
    	List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();
    	 searchFilters.add(new SearchFilter("tenantUID",SearchFilter.Operator.EQ, user.getTenantUID()));
    	return dao.findBySearchFilter(searchFilters, pageRequest);
    }

    @Override
    public List<PmOrderRuleItem> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }
    
    public void onBeforeNewObject(PmOrderRuleItem object) {
    	object.setTenantUID(user.getTenantUID());
    }
    
/*
    @Override
    public void onBeforeNewObject(PmOrderRuleItem object) {
    }
    
    @Override
    public void onAfterNewObject(PmOrderRuleItem object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(PmOrderRuleItem object) {
    }
    
    @Override
    public void onAfterUpdateObject(PmOrderRuleItem object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(PmOrderRuleItem object) {
    }
    
    @Override
    public void onAfterDeleteObject(PmOrderRuleItem object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(PmOrderRuleItem object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}