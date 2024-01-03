package com.ge.apm.view.budget;

import com.ge.apm.dao.BudgetPurchaseTemplateManageRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.budget.BudgetPurchaseTemplateManage;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author : 503189362
 * @date : 2023/11/2
 * @description :
 */
@ViewScoped
@ManagedBean(name = "budgetPurchaseTemplateManageController")
public class BudgetPurchaseTemplateManageController extends GenericCRUDUUIDController<BudgetPurchaseTemplateManage> {

    public static final String CREATE = "Create";
    public static final String EDIT = "Edit";

    private UserAccount account = UserContextService.getCurrentUserAccount();

    private BudgetPurchaseTemplateManageRepository budgetPurchaseTemplateManageRepository;
    private BlobFileService blobService;
    @Getter@Setter
    List<V2_BlobObject> blobObjectList = new ArrayList<>();


    @Override
    protected void init() {
        budgetPurchaseTemplateManageRepository = WebUtil.getBean(BudgetPurchaseTemplateManageRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
    }

    @Override
    protected Page<BudgetPurchaseTemplateManage> loadData(PageRequest pageRequest) {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, account.getTenantUID()));
        return budgetPurchaseTemplateManageRepository.findBySearchFilter(this.searchFilters, pageRequest);
    }


    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        crudActionName = CREATE;
        selected = new BudgetPurchaseTemplateManage();
        blobObjectList = new ArrayList<>();
    }

    @Override
    public void prepareEdit() {
        crudActionName = EDIT;
        blobObjectList = blobService.findBlobObjectsByBoUid(selected.getId());
    }

    public void handleProveFileUpload(FileUploadEvent event){
        V2_BlobObject v2_blobObject = blobService.uploadFile(event);
        blobObjectList.add(v2_blobObject);
    }

    List<V2_BlobObject> removeFile = new ArrayList<>();
    public void removeFile(V2_BlobObject v2_blobObject){
        blobObjectList.remove(v2_blobObject);
        removeFile.add(v2_blobObject);
    }

    public String saveData(){
        if(blobObjectList == null || blobObjectList.size() == 0){
            WebUtil.addErrorMessage("附件不能为空！");
            return "";
        }
        if(CREATE.equals(crudActionName)){
            selected.setTemplateType(BudgetPurchaseTemplateManage.TemplateTypeEnum.PurchaseTemplate.toString());
            selected.setCreatedBy(UserContextService.getCurrentUserAccount().getName());
            selected.setCreatedDate(new Date());
            selected.setTenantUID(account.getTenantUID());
            selected.setInstitutionUID(account.getTenantUID());
            selected.setHospitalUID(account.getTenantUID());
            selected.setSiteUID(account.getTenantUID());
            budgetPurchaseTemplateManageRepository.save(selected);
            for(V2_BlobObject b : blobObjectList) {
                b.setBoUuid(selected.getId());
                b.setBoType(V2_BlobObject.BO_TYPE_BUDGET_PURCHASE_TEMPLATE);
            }
            blobService.saveBlobObject(blobObjectList);
        }else{
            selected.setLastModifiedBy(UserContextService.getCurrentUserAccount().getName());
            selected.setLastModifiedDate(new Date());
            budgetPurchaseTemplateManageRepository.save(selected);
            for(V2_BlobObject b : blobObjectList) {
                if(StringUtils.isNotEmpty(b.getBoUuid())){
                    continue;
                }
                b.setBoUuid(selected.getId());
                b.setBoType(V2_BlobObject.BO_TYPE_BUDGET_PURCHASE_TEMPLATE);
            }
            blobService.saveBlobObject(blobObjectList);
        }
        if(removeFile != null && removeFile.size() > 0){
            for(V2_BlobObject v : removeFile){
                blobService.deleteBlobFile(v);
            }
        }
        return "List?faces-redirect=true";

    }

    public void deleteData(){

        //blobService.deleteBlobFile(selected.getBlobObject());
        budgetPurchaseTemplateManageRepository.delete(selected);
    }

    @Override
    protected GenericRepositoryUUID<BudgetPurchaseTemplateManage> getDAO() {
        return budgetPurchaseTemplateManageRepository;
    }
}
