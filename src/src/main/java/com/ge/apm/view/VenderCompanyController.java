package com.ge.apm.view;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class VenderCompanyController extends GenericCRUDUUIDController<VenderCompany> {

    private VenderCompanyRepository dao;
    private SupplierRepository supplierRepository;
    @Getter @Setter
    private Boolean supplierT = Boolean.FALSE;
    @Getter @Setter
    private Boolean maintainerT = Boolean.FALSE;
    @Getter @Setter
    private Boolean manufacturerT = Boolean.FALSE;

    @Override
    protected void init() {
        dao = WebUtil.getBean(VenderCompanyRepository.class);
        supplierRepository = WebUtil.getBean(SupplierRepository.class);
        this.filterBySite = false;
    }

    @Override
    protected VenderCompanyRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<VenderCompany> loadData(PageRequest pageRequest) {
        if(this.searchFilters == null){
            searchFilters =  new ArrayList<>();
        }
        List<Supplier> hasSuppliers = supplierRepository.findByTenantUIDAndVenderCompanyIdIsNotNull(UserContextService.getCurrentUserAccount().getTenantUID());
        if (ObjectUtil.isNotEmpty(hasSuppliers)) {
            searchFilters.add(new SearchFilter("id", SearchFilter.Operator.NotIN, hasSuppliers.stream().map(Supplier::getVenderCompanyId).collect(Collectors.toList())));
        }
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    public void relation(){
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(selected, supplier);
        supplier.setId(null);
        supplier.setVenderCompanyId(selected.getId());
        supplier.setSupplierT(supplierT);
        supplier.setMaintainerT(maintainerT);
        supplier.setManufacturerT(manufacturerT);
        supplier.setTenantUID(userAccount.getTenantUID());
        supplier.setInstitutionUID(userAccount.getInstitutionUID());
        supplier.setHospitalUID(userAccount.getHospitalUID());
        supplier.setSiteUID(userAccount.getSiteUID());
        supplier.setSiteId(UserContextService.getSiteId());
        supplier.setCreatedBy(userAccount.getLoginName());
        supplier.setCreatedDate(new Date());
        supplier.setLastModifiedBy(userAccount.getLoginName());
        supplier.setLastModifiedDate(new Date());
        supplier.setSource("VenderCompany");
        supplierRepository.save(supplier);
    }
}