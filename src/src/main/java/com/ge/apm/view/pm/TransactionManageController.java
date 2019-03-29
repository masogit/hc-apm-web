package com.ge.apm.view.pm;

import com.ge.apm.dao.DeptTransactionLogRepository;
import com.ge.apm.dao.DeptTransactionCategoryRepository;
import com.ge.apm.domain.DeptTransactionCategory;
import com.ge.apm.domain.DeptTransactionLog;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.transaction.TransactionService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.web.TransactionForm;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class TransactionManageController extends GenericCRUDUUIDController<DeptTransactionLog> implements Serializable {
    private TransactionService transactionService;
    private DeptTransactionLogRepository dao=null;
    private DeptTransactionCategoryRepository deptTransactionCategoryRepository;
private UserContextService userContextService;
    @Getter @Setter
    private DeptTransactionCategory deptTransactionCategory;
    private UserAccount user;
    @Override
    protected DeptTransactionLogRepository getDAO() {
        return dao;
    }
    @Override
    protected Page<DeptTransactionLog> loadData(PageRequest pageRequest){
        List<SearchFilter> searchFilters = new ArrayList();
        Sort sort = new Sort(Sort.Direction.DESC, "startTime");
        pageRequest = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
        return dao.findBySearchFilter(searchFilters, pageRequest);
    }
public void onSelectTM(){
    System.out.println("dd");
    System.out.println(selected);

}
public List<DeptTransactionCategory> getTranManList(){
    return  deptTransactionCategoryRepository.find();
}
    public void init(){
        dao=WebUtil.getBean(DeptTransactionLogRepository.class);
        deptTransactionCategoryRepository =WebUtil.getBean(DeptTransactionCategoryRepository.class);
        transactionService= WebUtil.getBean(TransactionService.class);
        user = UserContextService.getCurrentUserAccount();
   }

   public void saveTranMan(){
       /* selected.setTransactionUID(deptTransactionCategory.getId());
       // selected.setName(deptTransactionCategory.getName());
        selected.setTenantUID(user.getTenantUID());
        selected.setHospitalUID(user.getHospitalUID());
        selected.setInstitutionUID(user.getInstitutionUID());
        selected.setSiteUID(user.getSiteUID());
        transactionService.createTm(selected);*/
       TransactionForm tf = new TransactionForm();

       tf.setStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(selected.getStartTime()));
       tf.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(selected.getEndTime()));
       tf.setCategory(deptTransactionCategory.getId());
       tf.setAuthorUID(UserContextService.getCurrentUserAccount().getUid());
       transactionService.createTmByLink(tf,UserContextService.getAccessToken());
   }
   @Override
   public void prepareCreate()throws InstantiationException, IllegalAccessException{
       deptTransactionCategory = new DeptTransactionCategory();
       super.prepareCreate();

   }
   public void prepareEdit(){

   }
    @Override
   public void delete(){
        transactionService.deleteTm(selected);
        selected=null;
    }

    public DeptTransactionCategory getDeptTransactionCategory() {
        return deptTransactionCategory;
    }

    public void setDeptTransactionCategory(DeptTransactionCategory deptTransactionCategory) {
        this.deptTransactionCategory = deptTransactionCategory;
    }
}
