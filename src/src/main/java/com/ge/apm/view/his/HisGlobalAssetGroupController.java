package com.ge.apm.view.his;

import com.ge.apm.dao.HisAssetGroupAssetMatchRepository;
import com.ge.apm.dao.HisAssetGroupClinicalMatchRepository;
import com.ge.apm.dao.HisAssetGroupRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.his.HisAssetGroup;
import com.ge.apm.domain.his.HisAssetGroupAssetMatch;
import com.ge.apm.domain.his.HisAssetGroupClinicalMatch;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
public class HisGlobalAssetGroupController extends GenericCRUDUUIDController<HisAssetGroup> {

    private HisAssetGroupRepository hisAssetGroupDao;
    private HisAssetGroupClinicalMatchRepository hisAssetGroupClinicalMatchDao;
    private HisAssetGroupAssetMatchRepository hisAssetGroupAssetMatchRepository;

    private UserAccount userAccount;

    @Getter
    @Setter
    private String groupType;

    @Override
    protected void init() {
        hisAssetGroupDao = WebUtil.getBean(HisAssetGroupRepository.class);
        hisAssetGroupClinicalMatchDao = WebUtil.getBean(HisAssetGroupClinicalMatchRepository.class);
        hisAssetGroupAssetMatchRepository = WebUtil.getBean(HisAssetGroupAssetMatchRepository.class);
        userAccount = UserContextService.getCurrentUserAccount();
    }

    @Override
    protected Page<HisAssetGroup> loadData(PageRequest pageRequest) {
        if(searchFilters==null){
            searchFilters  = new ArrayList<>();
        }
        removeFilterOnField("tenantUid");
        searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID()));

        Sort.Order orders = new Sort.Order(Sort.Direction.DESC, "lastModifiedDate", Sort.NullHandling.NULLS_LAST);
        Sort sort = new Sort(orders);
        pageRequest= new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
        Page<HisAssetGroup> hisAssetGroups = hisAssetGroupDao.findBySearchFilter(this.searchFilters, pageRequest);
        return hisAssetGroups;
    }

    public HisAssetGroup onSelectHisAssetGroup() {
        return selected;
    }

    public List<HisAssetGroup> getAllHisAssetGroup() {

        String tenantUID = UserContextService.getCurrentUserAccount().getTenantUID();


        return hisAssetGroupDao.findByTenantUid(tenantUID);
    }

    @Override
    protected GenericRepositoryUUID<HisAssetGroup> getDAO() {
        return null;
    }

    public void saveHisList(){
        String tenantUID = UserContextService.getCurrentUserAccount().getTenantUID();
        selected.setTenantUid(tenantUID);
        boolean hasName = checkNameType(selected.getName(),selected.getType(),tenantUID);
        if(hasName){
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            WebUtil.addErrorMessage("分组类型已存在");
            return;
        }
        if(selected.getUuid()==null){
            selected.setCreatedBy(userAccount.getUid());
            selected.setCreatedDate(new Date());
        }
        selected.setLastModifiedBy(userAccount.getUid());
        selected.setLastModifiedDate(new Date());
        hisAssetGroupDao.save(selected);
        hisAssetGroupAssetMatchRepository.updateAssetGroupNameByGroupUid(selected.getName(),selected.getUuid());
        selected = null;
        clearAssetGroupCache();
    }

    @Override
    public void delete() {
        List<HisAssetGroupClinicalMatch>  hisAssetGroupClinicalMatchList= hisAssetGroupClinicalMatchDao.findByAssetGroupUid(selected.getUuid());
        if(!hisAssetGroupClinicalMatchList.isEmpty()){
            WebUtil.addErrorMessage("该分组已与设备绑定，不能删除");
            return;
        }
        hisAssetGroupDao.delete(selected);

        selected = null;

        clearAssetGroupCache();
    }

    private void clearAssetGroupCache() {
        HisAssetGroupEditController editController = WebUtil.getBean(HisAssetGroupEditController.class);
        editController.clearAssetGroups();
    }

    private boolean checkName(String name, String tenantUID) {
        HisAssetGroup hisAssetGroup = hisAssetGroupDao.findByNameAndTenantUid(name,tenantUID);
        return hisAssetGroup != null ? Boolean.TRUE : Boolean.FALSE;
    }

    private boolean checkNameType(String name,String type, String tenantUID) {
        HisAssetGroup hisAssetGroup = hisAssetGroupDao.findByNameAndTypeAndTenantUid(name,type,tenantUID);
        return hisAssetGroup != null ? Boolean.TRUE : Boolean.FALSE;
    }

    public Integer getAssetCountsByGroupUid(String assetGroupUid){
        return hisAssetGroupClinicalMatchDao.countByGroupUid(assetGroupUid);
    }
}
