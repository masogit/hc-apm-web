package com.ge.apm.view.his;

import com.ge.apm.dao.HisAssetGroupAssetMatchRepository;
import com.ge.apm.dao.HisAssetGroupClinicalMatchRepository;
import com.ge.apm.dao.HisAssetGroupRepository;
import com.ge.apm.domain.his.HisAssetGroup;
import com.ge.apm.domain.his.HisAssetGroupAssetMatch;
import com.ge.apm.domain.his.HisAssetGroupClinicalMatch;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.context.RequestContext;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

@ManagedBean
@ViewScoped
public class HisGlobalAssetGroupController extends GenericCRUDUUIDController<HisAssetGroup> {

    private HisAssetGroupRepository hisAssetGroupDao;
    private HisAssetGroupClinicalMatchRepository hisAssetGroupClinicalMatchDao;
    private HisAssetGroupAssetMatchRepository hisAssetGroupAssetMatchRepository;

    @Override
    protected void init() {
        hisAssetGroupDao = WebUtil.getBean(HisAssetGroupRepository.class);
        hisAssetGroupClinicalMatchDao = WebUtil.getBean(HisAssetGroupClinicalMatchRepository.class);
        hisAssetGroupAssetMatchRepository = WebUtil.getBean(HisAssetGroupAssetMatchRepository.class);
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
