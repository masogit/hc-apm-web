package com.ge.apm.view.pm;

import com.ge.apm.dao.TransactionManageRepository;
import com.ge.apm.domain.TransactionManage;
import com.ge.apm.domain.V2_PmOrder;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class TransactionManageController extends GenericCRUDUUIDController<TransactionManage> implements Serializable {
    private TransactionManageRepository dao;
    @Override
    protected GenericRepositoryUUID<TransactionManage> getDAO() {
        return null;
    }

   public void init(){
    dao= WebUtil.getBean(TransactionManageRepository.class);
   }
}
