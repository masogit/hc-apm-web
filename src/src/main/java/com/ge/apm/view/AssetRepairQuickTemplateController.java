package com.ge.apm.view;

import com.ge.apm.dao.AssetRepairQuickTemplateRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetRepairQuickTemplate;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.common.I18nMessageService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
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

@ManagedBean
@ViewScoped
public class AssetRepairQuickTemplateController extends GenericCRUDUUIDController<AssetRepairQuickTemplate> {

    AssetRepairQuickTemplateRepository dao = null;
    I18nMessageService i18nMessageService;
    OrgInfoRepository orgInfoRepository;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetRepairQuickTemplateRepository.class);
        i18nMessageService = WebUtil.getBean(I18nMessageService.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
    }

    @Override
    protected GenericRepositoryUUID<AssetRepairQuickTemplate> getDAO() {
        return null;
    }

    @Override
    protected Page<AssetRepairQuickTemplate> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID()));

        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    @Override
    public List<AssetRepairQuickTemplate> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

    public String findTemplateTypeStr(Integer templateType) {
       if(templateType == 1){
           return "故障原因";
       }else if(templateType == 2){
           return "解决方案";
       }else if(templateType == 3){
           return "交付检测";
       }else{
           return "";
       }
    }

    public String findHierOrgUid(String hierOrgUid) {
        if(StringUtils.isEmpty(hierOrgUid)){
            return "";
        }
        OrgInfo orgInfo = orgInfoRepository.getByUid(hierOrgUid);
        if(orgInfo == null){
            return "";
        }
        return orgInfo.getName();
    }

    public String findAssetGroup(Integer assetGroup) {
        if(assetGroup == null){
            return "";
        }
        I18nMessage foundMsg = i18nMessageService.getByMsgTypeAndMsgKey("assetGroup", String.valueOf(assetGroup));
        if(foundMsg == null){
            return "";
        }
        return foundMsg.getValueZh();
    }

    public void save() {
        UserAccount ua = UserContextService.getCurrentUserAccount();
        selected.setTenantUid(ua.getTenantUID());
        selected.setHierOrgUid(ua.getOrgInfoUID());
        if(StringUtils.isEmpty(selected.getId())){
            selected.setCreateDate(new Date());
            selected.setCreatedBy(ua.getLoginName());
        }
        selected.setModifyDate(new Date());
        selected.setModifyBy(ua.getLoginName());
        dao.save(selected);
        selected = null;
    }

    public void delete(){
        dao.delete(selected);
    }

}