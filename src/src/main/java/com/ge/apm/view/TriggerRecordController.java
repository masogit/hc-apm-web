package com.ge.apm.view;

import com.ge.apm.dao.TriggerRecordRepository;
import com.ge.apm.domain.TriggerRecord;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;

@ManagedBean
@ViewScoped
public class TriggerRecordController extends GenericCRUDUUIDController<TriggerRecord> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TriggerRecordController.class);

    private UserAccount currentUser;
    private TriggerRecordRepository dao;




    @Override
    protected void init() {
        dao = WebUtil.getBean(TriggerRecordRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        this.searchFilters = new ArrayList<>();
    }

    @Override
    protected GenericRepositoryUUID<TriggerRecord> getDAO() {
        return dao;
    }

    @Override
    protected Page<TriggerRecord> loadData(PageRequest pageRequest) {
        return super.loadData(pageRequest);
    }
}
