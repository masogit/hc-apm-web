package com.ge.apm.view.his;

import com.ge.apm.dao.HisAssetGroupClinicalMatchRepository;
import com.ge.apm.dao.HisAssetGroupRepository;
import com.ge.apm.domain.his.HisAssetGroup;
import com.ge.apm.domain.his.HisAssetGroupClinicalMatch;
import webapp.framework.dao.GenericRepositoryUUID;
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

    @Override
    protected void init() {
        hisAssetGroupDao = WebUtil.getBean(HisAssetGroupRepository.class);
        hisAssetGroupClinicalMatchDao = WebUtil.getBean(HisAssetGroupClinicalMatchRepository.class);
    }

    public HisAssetGroup onSelectHisAssetGroup() {
        return selected;
    }

    public List<HisAssetGroup> getAllHisAssetGroup() {
        return hisAssetGroupDao.findAll();
    }

    @Override
    protected GenericRepositoryUUID<HisAssetGroup> getDAO() {
        return null;
    }

    public void saveHisList(){
        boolean hasName = checkName(selected.getName());
        if(hasName){
            return;
        }
        hisAssetGroupDao.save(selected);

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

    private boolean checkName(String name) {
        HisAssetGroup hisAssetGroup = hisAssetGroupDao.findByName(name);
        return hisAssetGroup != null ? Boolean.TRUE : Boolean.FALSE;
    }

    public Integer getAssetCountsByGroupUid(String assetGroupUid){
        return hisAssetGroupClinicalMatchDao.countByGroupUid(assetGroupUid);
    }
}
