package com.ge.apm.service.asset;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.SupplierRepository;
import com.ge.apm.dao.SupplierUserRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.WorkflowConfigRepository;
import com.ge.apm.domain.Supplier;
import com.ge.apm.domain.SupplierUser;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.WorkflowConfig;
import com.ge.apm.service.utils.PinYinComparator;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierDao;
    @Autowired
    private SupplierUserRepository supplierUserDao;
    @Autowired
    private UserAccountRepository userAccountDao;
    @Autowired
    private SupplierApiService supplierApiService;
    @Autowired
    private WorkflowConfigRepository workflowConfigDao;

    public List<String> getSupplier(String name) {
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<SearchFilter> filter = new ArrayList<>();

        filter.add(new SearchFilter("manufacturerT", SearchFilter.Operator.EQ, true,"1*"));
        filter.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID(),"1*"));
        if(StringUtils.isNotEmpty(name)){
            filter.add(new SearchFilter("name", SearchFilter.Operator.LIKE, name,"1*2+"));
            filter.add(new SearchFilter("namePyCode", SearchFilter.Operator.LIKE, name.toLowerCase(),"1*2+"));
        }
        //return supplierDao.findByManufacturerTAndTenantUID(true,user.getTenantUID());
        List<Supplier> suppliers = supplierDao.findBySearchFilter(filter);
        List<String> names = new ArrayList<>(suppliers.size());
        suppliers.stream().forEach( s -> names.add(s.getName()));
        names.sort(new PinYinComparator());
        return names;
    }

    public List<UserAccount> getTempStaffList(String tenantUID){
        return userAccountDao.findByTenantUIDAndTempStaffRole(tenantUID);
    }

    public List<SupplierUser> getSupplierUserList(Integer supplierId, String status, String queryStr){

        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();

        List<SearchFilter> searchFilterList = new ArrayList<>();

        searchFilterList.add(new SearchFilter("supplier.tenantUID", SearchFilter.Operator.EQ, currentUserAccount.getTenantUID(),"1*"));

        if(supplierId != null){
            searchFilterList.add(new SearchFilter("supplierID", SearchFilter.Operator.EQ, supplierId,"1*"));
        }

        if(StringUtils.isNotEmpty(status)){
            searchFilterList.add(new SearchFilter("status", SearchFilter.Operator.EQ, status,"1*"));
        }

        if(StringUtils.isNotEmpty(queryStr)){
            searchFilterList.add(new SearchFilter("supplier.name", SearchFilter.Operator.LIKE, queryStr,"1*2+"));
            searchFilterList.add(new SearchFilter("userAccount.name", SearchFilter.Operator.LIKE, queryStr,"1*2+"));
            searchFilterList.add(new SearchFilter("userAccount.telephone", SearchFilter.Operator.LIKE, queryStr,"1*2+"));
        }

        return supplierUserDao.findBySearchFilter(searchFilterList);
    }

    public List<UserAccount> getApprovedUserList(Integer supplierId){
        List<SupplierUser> supplierUserList = supplierUserDao.findBySupplierIDAndStatus(supplierId, SupplierUser.Status.Approved.name());

        List<UserAccount> approvedUserList = new ArrayList<>();
        for (SupplierUser supplierUser : supplierUserList) {
            approvedUserList.add(supplierUser.getUserAccount());
        }

        return approvedUserList;
    }

    public void approve(SupplierUser supplierUser){
        supplierUser.setStatus(SupplierUser.Status.Approved.name());
        supplierUserDao.save(supplierUser);
    }

    public void approveByID(String id){
        SupplierUser supplierUser = supplierUserDao.findById(id);
        supplierUser.setStatus(SupplierUser.Status.Approved.name());
        supplierUserDao.save(supplierUser);
    }

    public void deleteByID(String id){
        supplierUserDao.delete(id);
    }

    @Transactional
    public void refuseByID(String id){
        SupplierUser supplierUser = supplierUserDao.findById(id);
        supplierUserDao.delete(id);
        supplierUserDao.deleteUserWxApp(supplierUser.getUserAccount().getId());
        userAccountDao.delete(supplierUser.getUserAccount());
    }

    @Transactional
    public void updateSupplierUserList(Supplier supplier, List<UserAccount> approvedUserList){

        supplierUserDao.deleteBySupplierIDAndStatus(supplier.getId(), SupplierUser.Status.Approved.name());

        List<Integer> userIDList = new ArrayList<>();
        for (UserAccount userAccount : approvedUserList) {
            SupplierUser supplierUser = new SupplierUser();
            supplierUser.setSupplierID(supplier.getId());
            supplierUser.setSupplierUID(supplier.getUid());
            supplierUser.setUserID(userAccount.getId());
            supplierUser.setUserUID(userAccount.getUid());
            supplierUser.setStatus(SupplierUser.Status.Approved.name());
            supplierUserDao.save(supplierUser);

            userIDList.add(userAccount.getId());
        }

        supplierUserDao.deleteBySupplierIDAndStatusAndUserIDIn(supplier.getId(), SupplierUser.Status.PendingApproval.name(), userIDList);

    }

    public byte[] getWxaCodeImage(){

        String token = UserContextService.getAccessToken();
        UserAccount user = UserContextService.getCurrentUserAccount();
        String path = "pages/index/index?tenantName="+ user.getTenantName() +"&tenantUID=" + user.getTenantUID();
        String appName ="hc-apm-supplier";

        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("path", path);
        payloadMap.put("appName", appName);

        JSONObject payload = new JSONObject(payloadMap);

        return supplierApiService.getWxaCodeImage(token ,payload);

    }

    public byte[] getWeChatCodeImage(){

        String token = UserContextService.getAccessToken();

        return supplierApiService.getWeChatQrCodeImage(token);

    }

    public boolean activeSupplier(){

        UserAccount user = UserContextService.getCurrentUserAccount();

        List<WorkflowConfig> workflowConfigList = workflowConfigDao.findBySiteUID(user.getSiteUID());
        if(workflowConfigList.isEmpty()){
            workflowConfigList = workflowConfigDao.findBySiteUID(user.getHospitalUID());
        }
        if(workflowConfigList.isEmpty()){
            workflowConfigList = workflowConfigDao.findBySiteUID(user.getInstitutionUID());
        }
        if(workflowConfigList.isEmpty()){
            workflowConfigList = workflowConfigDao.findBySiteUID(user.getTenantUID());
        }

        if(!workflowConfigList.isEmpty()){
            if (workflowConfigList.get(0).getActiveSupplier() != null && workflowConfigList.get(0).getActiveSupplier()){
                return true;
            }
        }

        return false;

    }

}
