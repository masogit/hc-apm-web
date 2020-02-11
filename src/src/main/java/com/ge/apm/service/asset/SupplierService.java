package com.ge.apm.service.asset;

import com.ge.apm.dao.SupplierRepository;
import com.ge.apm.dao.SupplierUserRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.Supplier;
import com.ge.apm.domain.SupplierUser;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.utils.PinYinComparator;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;

import java.util.ArrayList;
import java.util.List;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierDao;
    @Autowired
    private SupplierUserRepository supplierUserDao;
    @Autowired
    private UserAccountRepository userAccountDao;

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

    public List<SupplierUser> getSupplierUserList(Integer supplierId, String status){
        return supplierUserDao.findBySupplierIDAndStatus(supplierId, status);
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

    public void refuse(SupplierUser supplierUser){
        supplierUserDao.delete(supplierUser.getId());
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
}
