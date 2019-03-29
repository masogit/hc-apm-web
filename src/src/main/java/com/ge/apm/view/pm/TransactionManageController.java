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
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ManagedBean
@ViewScoped
public class TransactionManageController extends JpaCRUDController<DeptTransactionLog> implements Serializable {
    private TransactionService transactionService;
    private DeptTransactionLogRepository dao=null;
    private DeptTransactionCategoryRepository deptTransactionCategoryRepository;
    private UserContextService userContextService;
    @Getter @Setter
    private Integer tmCategory;
    @Getter@Setter
    private String remark;

    @Getter @Setter
    private DeptTransactionCategory deptTransactionCategory;
    private UserAccount user;
    @Override
    protected DeptTransactionLogRepository getDAO() {
        return dao;
    }
    @Override
    protected Page<DeptTransactionLog> loadData(PageRequest pageRequest){
        this.setSearchFilter();
        List<SearchFilter> searchFilters = new ArrayList();
        //searchFilters.remove()
        if(CollectionUtils.isNotEmpty(this.searchFilters)){
            searchFilters.addAll(this.searchFilters);
            // searchFilters.get(0).value= getKey(cateHm,searchFilters.get(0).value.toString());
        }
        if (userContextService.hasRole("AssetStaff")) {
            searchFilters.add(new SearchFilter("authorId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId()));
            searchFilters.add(new SearchFilter("authorId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*2+"));
        }
        Sort sort = new Sort(Sort.Direction.DESC, "startTime");
        pageRequest = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
        Page<DeptTransactionLog> bySearchFilter = dao.findBySearchFilter(searchFilters, pageRequest);
        return bySearchFilter;
    }
    public void onSelectTM(){
        System.out.println(selected);
    }
    public String getTitleName(){
        if(selected !=null && selected.getId() != null ){
            return "修改";
        }else{
            return "新增";
        }
    }
    HashMap<Integer,String> cateHm;
    public List<DeptTransactionCategory> getTranManList(){
        return  deptTransactionCategoryRepository.find();
    }
    public void init(){
        filterBySite = false;
        cateHm= new HashMap<>();
        cateHm.put(1,"带教");
        cateHm.put(2,"接待");
        cateHm.put(3,"会议");
        cateHm.put(4,"培训");
        cateHm.put(5,"巡查回访");
        cateHm.put(6,"计量");
        cateHm.put(7,"论证");
        cateHm.put(8,"其它");
        dao=WebUtil.getBean(DeptTransactionLogRepository.class);
        deptTransactionCategoryRepository =WebUtil.getBean(DeptTransactionCategoryRepository.class);
        transactionService= WebUtil.getBean(TransactionService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        user = UserContextService.getCurrentUserAccount();
    }
    public String getCateHmByNum(Integer num){
        return cateHm.get(num);
    }

    public void saveTranMan(){
       /* selected.setCategory(tmCategory);
       // selected.setName(deptTransactionCategory.getName());
        selected.setTenantUID(user.getTenantUID());
        selected.setHospritalUID(user.getHospitalUID());
        selected.setInstitutionUID(user.getInstitutionUID());
        selected.setSiteUID(user.getSiteUID());
        transactionService.createTm(selected);*/
        TransactionForm tf = new TransactionForm();
        tf.setStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(selected.getStartTime()));
        tf.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(selected.getEndTime()));
        tf.setCategory(tmCategory);
        tf.setRemark(remark);
        tf.setAuthorUID(UserContextService.getCurrentUserAccount().getUid());
        if(selected.getId()==null) {//新增
            transactionService.createTmByLink(tf, UserContextService.getAccessToken());
            remark = null;
        }else{
            tf.setId(selected.getId());
            transactionService.updateTransaction(tf, UserContextService.getAccessToken());
        }
    }
    @Override
    public void prepareCreate()throws InstantiationException, IllegalAccessException{
        deptTransactionCategory = new DeptTransactionCategory();
        remark=null;
        tmCategory=null;
        super.prepareCreate();

    }
    public void prepareEdit(){
        System.out.print(selected);
        remark=selected.getRemark();
        tmCategory=selected.getCategory();


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

    public  Integer getKey(HashMap<Integer,String> map,String value){
        Integer key = null;
        for(Integer getKey: map.keySet()){
            if(map.get(getKey).equals(value)){
                key = getKey;
            }
        }
        return key;
    }

}
