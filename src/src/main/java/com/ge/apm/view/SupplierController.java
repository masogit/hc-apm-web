package com.ge.apm.view;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import webapp.framework.dao.SearchFilter;
import webapp.framework.util.StringUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.SupplierRepository;
import com.ge.apm.domain.Supplier;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class SupplierController extends JpaCRUDController<Supplier> {

    SupplierRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(SupplierRepository.class);
        this.filterBySite = true;
    }

    @Override
    protected SupplierRepository getDAO() {
        return dao;
    }

    public List<Supplier> getSupplierList() {
        return dao.getBySiteId(UserContextService.getSiteId());
    }
    
    public Supplier getSupplier(Integer id) {
        return dao.findById(id);
    }
    

    @Override
    public void onBeforeNewObject(Supplier supplier) {
        supplier.setSiteId(UserContextService.getSiteId());
        supplier.setTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());
    }
    
    @Override
    public String getKeyFieldNameValue(Supplier supplier){
        return WebUtil.getMessage("name")+"="+supplier.getName();
    }
    
    @Override
    protected Page<Supplier> loadData(PageRequest pageRequest) {
        this.setSiteFilter();
        this.selected = null;
        boolean nameFilter=false;
        Object nameValue="";
        for(SearchFilter filter: searchFilters){
            if(filter.fieldName.equals("name")){
                nameFilter=true;
                nameValue=filter.value;
                filter.conditionPath="1*2*3+";
            }
        }
        if(nameFilter){
            nameValue=((String)nameValue).toLowerCase();
            searchFilters.add(new SearchFilter("namePyCode",SearchFilter.Operator.LIKE,nameValue,"1*2*3+"));
        }

        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    public String getGongyingshangLeixing(Supplier supplier){
     if(supplier==null || supplier.getMaintainerT()==null|| supplier.getManufacturerT()==null||supplier.getSupplierT()==null){
         return "";
     }
        String name =new String();
        if(supplier.getMaintainerT()){
           name= name.concat("维修商 ");
        }
        if(supplier.getManufacturerT()){
            name= name.concat("制造商 ");
        }
        if(supplier.getSupplierT()){
            name= name.concat("供应商");
        }

       name= name.replace(" ","/");
        if(name.charAt(name.length()-1)=='/'){
            name= name.substring(0,name.length()-1);
        }
        return name;
    }
    
}