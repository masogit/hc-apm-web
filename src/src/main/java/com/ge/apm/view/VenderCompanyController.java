package com.ge.apm.view;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.view.preview.PreViewerBean;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class VenderCompanyController extends GenericCRUDUUIDController<VenderCompany> {

    private VenderCompanyRepository dao;
    private BlobFileService blobService;
    private SupplierRepository supplierRepository;
    private ConfigItemValueRepository configItemValueRepository;
    private VenderNoticeRepository venderNoticeRepository;
    private SupplierContactPersonRepository supplierContactPersonRepository;
    private SupplierController supplierController;
    @Getter @Setter
    private String noticeContent;
    @Getter @Setter
    private String supplierType;
    @Getter @Setter
    private Boolean relation;
    @Getter @Setter
    private Map<String, List<V2_BlobObject>> attachmentMap = MapUtil.newHashMap();
    @Getter @Setter
    private String venderCompanyId;
    @Getter @Setter
    private List<SupplierContactPerson> contactPersonList = ListUtil.newArrayList();
    private UserAccount currentUser;

    private PreViewerBean preViewerBean;
    @Override
    protected void init() {
        dao = WebUtil.getBean(VenderCompanyRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        supplierRepository = WebUtil.getBean(SupplierRepository.class);
        configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);
        supplierContactPersonRepository = WebUtil.getBean(SupplierContactPersonRepository.class);
        supplierController = WebUtil.getBean(SupplierController.class);
        preViewerBean = WebUtil.getBean(PreViewerBean.class);
        venderNoticeRepository = WebUtil.getBean(VenderNoticeRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        this.filterBySite = false;
        String encodeStr = WebUtil.getRequestParameter("str");
        venderCompanyId = (String) UrlEncryptController.getValueFromMap(encodeStr, "venderCompanyId");
        if (ObjectUtil.isNotEmpty(venderCompanyId)) {
            this.selected = dao.findById(venderCompanyId);
            contactPersonList = supplierContactPersonRepository.findByVenderCompanyId(this.selected.getId());
        }
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
        List<Supplier> hasSuppliers = supplierRepository.findByTenantUIDAndVenderCompanyIdIsNotNull(UserContextService.getCurrentUserAccount().getTenantUID());
        List<String> venderCompanyIds = ListUtil.newArrayList("-1");
        venderCompanyIds.addAll(hasSuppliers.stream().map(Supplier::getVenderCompanyId).collect(Collectors.toList()));
        if (this.hasFilterOnField("relation")) {
            if (relation) {
                searchFilters.add(new SearchFilter("id", SearchFilter.Operator.IN, venderCompanyIds));
            } else {
                searchFilters.add(new SearchFilter("id", SearchFilter.Operator.NotIN, venderCompanyIds));
            }
            this.removeFilterOnField("relation");
        }
        if(this.hasFilterOnField("supplierType")){
            switch (supplierType){
                case "1": searchFilters.add(new SearchFilter("supplierT",SearchFilter.Operator.EQ,true));break;
                case "2": searchFilters.add(new SearchFilter("maintainerT",SearchFilter.Operator.EQ,true));break;
                case "3": searchFilters.add(new SearchFilter("manufacturerT",SearchFilter.Operator.EQ,true));break;
            }
            this.removeFilterOnField("supplierType");
        }
        Page<VenderCompany> page = dao.findBySearchFilter(this.searchFilters, pageRequest);
        for (VenderCompany venderCompany : page.getContent()) {
            venderCompany.setRelation(venderCompanyIds.contains(venderCompany.getId()));
        }
        return page;
    }

    public void relation(){
        Supplier temp = supplierRepository.findByTenantUIDAndVenderCompanyId(currentUser.getTenantUID(), selected.getId());
        if (ObjectUtil.isNotEmpty(temp)) {
            WebUtil.addErrorMessage("当前租户已添加该供应商");
            return;
        }
        boolean mark = this.selected.getManufacturerT() || this.selected.getSupplierT() || this.selected.getMaintainerT();
        if (!mark) {
            //表示供应商类型一个都没有选
            WebUtil.addErrorMessage(WebUtil.getMessage("TypeErrTip"));
            RequestContext.getCurrentInstance().addCallbackParam("spType", true);
            return;
        }
        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(selected, supplier);
        supplier.setId(null);
        supplier.setName(supplier.getName());
        supplier.setVenderCompanyId(selected.getId());
        supplier.setTenantUID(currentUser.getTenantUID());
        supplier.setInstitutionUID(currentUser.getInstitutionUID());
        supplier.setHospitalUID(currentUser.getHospitalUID());
        supplier.setSiteUID(currentUser.getSiteUID());
        supplier.setSiteId(UserContextService.getSiteId());
        supplier.setCreatedBy(currentUser.getLoginName());
        supplier.setCreatedDate(new Date());
        supplier.setLastModifiedBy(currentUser.getLoginName());
        supplier.setLastModifiedDate(new Date());
        supplier.setSource("VenderCompany");
        supplier.setContactor(selected.getProjectDockerName());
        supplier.setTel(selected.getProjectDockerTel());
        supplierController.setSelected(supplier);
        supplierController.saveSubmit();
        WebUtil.addErrorMessage("添加成功！");
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

    public void onRowSelect(SelectEvent event) {
        if (null == event.getObject()) {
            return;
        }
        contactPersonList = supplierContactPersonRepository.findByVenderCompanyId(this.selected.getId());
    }

    public List<SupplierContactPerson> getPersonList() {
        return supplierContactPersonRepository.findByVenderCompanyId(this.selected.getId());
    }

    public List<V2_BlobObject> getAttachList(String id, String type) {
        List<V2_BlobObject> attachmentList = attachmentMap.get(id);
        if (ObjectUtil.isEmpty(attachmentList)) {
            attachmentList = blobService.findBlobObjectsByBoUid(id);
            attachmentMap.put(id, attachmentList);
        }
        return attachmentList.stream().filter(item -> item.getBoSubType() != null && item.getBoSubType().equals(type)).collect(Collectors.toList());
    }

    public boolean relationDisabled(){
        if (ObjectUtil.isEmpty(selected)) {
            return true;
        }
        Supplier temp = supplierRepository.findByTenantUIDAndVenderCompanyId(currentUser.getTenantUID(), selected.getId());
        return ObjectUtil.isNotEmpty(temp);
    }

    public void sendNotice() {
        VenderNotice venderNotice = new VenderNotice();
        Date now = DateUtil.now();
        venderNotice.setStatus(0);
        venderNotice.setVenderCompanyId(selected.getId());
        venderNotice.setNoticeContent(noticeContent);
        venderNotice.setTenantUid(currentUser.getTenantUID());
        venderNotice.setNoticeDate(now);
        venderNotice.setNoticeUser(currentUser.getName());
        venderNotice.setLastModifiedBy(currentUser.getName());
        venderNotice.setLastModifiedDate(now);
        venderNoticeRepository.save(venderNotice);
        noticeContent = null;
        WebUtil.addSuccessMessage("发送成功！");
    }

    public String getVenderCompanyLoginUrl() {
        return configItemValueRepository.findByKeyAndSiteForUseParent(currentUser.getSiteUID(), currentUser.getHospitalUID(), currentUser.getInstitutionUID(), currentUser.getTenantUID(), "vendorLoginUrl");
    }

    public void preViewed(String objectId){
        preViewerBean.onServerEvent("",objectId);
    }
}