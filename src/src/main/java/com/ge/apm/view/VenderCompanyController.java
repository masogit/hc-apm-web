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
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;

@ManagedBean
@ViewScoped
public class VenderCompanyController extends GenericCRUDUUIDController<VenderCompany> {

    private VenderCompanyRepository dao;
    private SupplierRepository supplierRepository;
    private SupplierController supplierController;
    @Getter @Setter
    private String supplierType;

    @Override
    protected void init() {
        dao = WebUtil.getBean(VenderCompanyRepository.class);
        supplierRepository = WebUtil.getBean(SupplierRepository.class);
        supplierController = WebUtil.getBean(SupplierController.class);
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
        /*List<Supplier> hasSuppliers = supplierRepository.findByTenantUIDAndVenderCompanyIdIsNotNull(UserContextService.getCurrentUserAccount().getTenantUID());
        if (ObjectUtil.isNotEmpty(hasSuppliers)) {
            searchFilters.add(new SearchFilter("id", SearchFilter.Operator.NotIN, hasSuppliers.stream().map(Supplier::getVenderCompanyId).collect(Collectors.toList())));
        }*/
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    public void relation(){
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        Supplier temp = supplierRepository.findByTenantUIDAndVenderCompanyId(userAccount.getTenantUID(), selected.getId());
        if (ObjectUtil.isNotEmpty(temp)) {
            WebUtil.addErrorMessage("当前租户已添加该供应商");
            return;
        }
        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(selected, supplier);
        supplier.setId(null);
        supplier.setName(supplier.getName());
        supplier.setVenderCompanyId(selected.getId());
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
        supplierController.setSelected(supplier);
        supplierController.saveSubmit();
    }

    public String getType(VenderCompany supplier){
        if(supplier==null){
            return "";
        }
        String name =new String();
        if(supplier.getMaintainerT() != null && supplier.getMaintainerT()){
            name= name.concat(WebUtil.getMessage("maitanance")+" ");
        }
        if(supplier.getManufacturerT() != null && supplier.getManufacturerT()){
            name= name.concat(WebUtil.getMessage("manufacture")+" ");
        }
        if(supplier.getSupplierT() != null && supplier.getSupplierT()){
            name= name.concat(WebUtil.getMessage("vendor")+" ");
        }

        name= name.replace(" ","/");
        if(name.length()>0 && name.charAt(name.length()-1)=='/'){
            name= name.substring(0,name.length()-1);
        }
        return name;
    }
}