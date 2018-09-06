package com.ge.apm.service.asset;

import com.ge.apm.dao.SupplierRepository;
import com.ge.apm.domain.Supplier;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.utils.PinYinComparator;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.dao.SearchFilter;

import java.util.ArrayList;
import java.util.List;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierDao;

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
}
