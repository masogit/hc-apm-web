package com.ge.apm.view;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.UserAgentRepository;
import com.ge.apm.domain.UserAgent;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class UserAgentController extends JpaCRUDController<UserAgent> {

	private static final long serialVersionUID = 1L;
	UserAgentRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(UserAgentRepository.class);
    }

    @Override
    protected UserAgentRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<UserAgent> loadData(PageRequest pageRequest) {
    	return dao.findAll(pageRequest);
//        if (this.searchFilters == null) {
//            return dao.findAll(pageRequest);
//        } else {
//            return dao.findBySearchFilter(this.searchFilters, pageRequest);
//        }
    }

    @Override
    public List<UserAgent> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

/*
    @Override
    public void onBeforeNewObject(UserAgent object) {
    }
    
    @Override
    public void onAfterNewObject(UserAgent object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(UserAgent object) {
    }
    
    @Override
    public void onAfterUpdateObject(UserAgent object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(UserAgent object) {
    }
    
    @Override
    public void onAfterDeleteObject(UserAgent object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(UserAgent object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}