package com.ge.apm.view.assetClassificationManagement;

import com.ge.apm.dao.AssetClassificationModelRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetClassificationModel;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.remote.MasterDataService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.asset.AssetInfoListController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.AssetClassification32Enum;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
@Data
public class ModelTrainingController extends GenericCRUDUUIDController<AssetClassificationModel> implements Serializable, ServerEventInterface {
    private static final Logger logger = LoggerFactory.getLogger(ModelTrainingController.class);

    private AssetClassificationModelRepository dao;
    private UserAccount user;

    private UaaService uaaService;
    private OrgInfoRepository orgInfoRepository;

    private MasterDataService masterDataService;

    @Override
    protected void init() {


        dao = WebUtil.getBean(AssetClassificationModelRepository.class);


        user = UserContextService.getCurrentUserAccount();
        uaaService = WebUtil.getBean(UaaService.class);

        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        masterDataService = WebUtil.getBean(MasterDataService.class);

    }

    @Override
    protected Page<AssetClassificationModel> loadData(PageRequest pageRequest) {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        if(user.getIsSuperAdmin()){
            this.updateSearchFilter(new SearchFilter("tenantUid", SearchFilter.Operator.EQ, "0", "1*"));

        }else{
            this.updateSearchFilter(new SearchFilter("tenantUid", SearchFilter.Operator.EQ, user.getTenantUID(), "1*"));
        }
        selected = null;

        Page<AssetClassificationModel> res = dao.findBySearchFilter(this.searchFilters, pageRequest);

        return res;
    }
    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        this.selected = new AssetClassificationModel();
    }

    @Override
    public void prepareEdit()   {
//        systemTemplateValue= systemTemplateValueRepository.findById(selected.getValueId());
    }

    @Override
    protected GenericRepositoryUUID<AssetClassificationModel> getDAO() {
        return dao;
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {

    }
    public List<String> getAssetClassification32All(){

        return AssetClassification32Enum.getAllDisplayNameList();
    }

    public void addCategoryModel() {
        if(selected!=null){
            selected.setTenantUid(user.getTenantUID());
            Map<String, Object> stringObjectMap = masterDataService.saveAssetClassificationModel(selected);
            if(stringObjectMap!=null){
                if (stringObjectMap.containsKey("failMessage")) {
                    WebUtil.addErrorMessage(String.valueOf(stringObjectMap.get("failMessage")));
                    FacesContext.getCurrentInstance().validationFailed();
                    return;
                }
                WebUtil.addSuccessMessage("添加成功");
            }
        }
        selected=null;
    }

    public void deleteModel(AssetClassificationModel asset) {
        Map<String, Object> stringObjectMap = masterDataService.deleteAssetClassificationModel(asset);
        if(stringObjectMap!=null){
            WebUtil.addSuccessMessage("删除成功");
        }

    }

    public void edit(AssetClassificationModel  asset) {
        selected=asset;
    }
}
