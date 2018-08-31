package com.ge.apm.view.uaa;

import com.ge.apm.dao.OrgInfoRepository;

import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TenantInfo;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import webapp.framework.web.WebUtil;
import com.ge.apm.dao.TenantInfoRepository;

@ManagedBean
@ViewScoped
public class TenantInfoController extends JpaCRUDController<TenantInfo> {

    TenantInfoRepository dao = null;
    UserAccountRepository userAccountRepository;
    UserAccount loginUser;

    @Override
    protected void init() {
        filterBySite = false;
        loginUser = UserContextService.getCurrentUserAccount();
        dao = WebUtil.getBean(TenantInfoRepository.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
    }

    @Override
    protected TenantInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<TenantInfo> loadData(PageRequest pageRequest) {
        selected = null;

        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<TenantInfo> getItemList() {
        return dao.getAllOrderByName();
    }
    
    @Override
    public String getErrorMessageForDuplicateKey(TenantInfo site){
        return String.format(WebUtil.getMessage("DuplicateSiteName"), site.getName());
    }
    
    @Override
    public String getKeyFieldNameValue(TenantInfo site){
        return WebUtil.getMessage("name")+"="+site.getName();
    }

    @Override
    public void onAfterNewObject(TenantInfo tenant, boolean isOK) {
        if(!isOK) return;
/*
        // import field code type data for this site
        try{
            String sql = "insert into i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) select distinct msg_type, msg_key, value_zh, value_en, value_tw, %d as site_id from i18n_message where msg_type in (select msg_type from field_code_type)";
            NativeSqlUtil.execute(String.format(sql, site.getId()), null);
        }
        catch(Exception ex){
            logger.error(ex.getMessage(), ex);
        }
*/

        // and create an default org for this site
//        String orgUID = UUID.randomUUID().toString().replace("-", "");
        OrgInfo org = new OrgInfo();
        org.setSiteId(tenant.getId());
        
        //for new UUIDs
        org.setOrgLevel(0);
        org.setOrgType(0);
        org.setUid(tenant.getUid());
        org.setTenantUID(tenant.getUid());
        org.setInstitutionUID(tenant.getUid());
        org.setHospitalUID(tenant.getUid());
        org.setSiteUID(tenant.getUid());

        org.setShortName(tenant.getName());
        org.setName(tenant.getName());
        
        OrgInfoRepository orgDao = WebUtil.getBean(OrgInfoRepository.class);
        orgDao.save(org);
    }

    @Transactional
    public void onAfterUpdateObject(TenantInfo tenant, boolean isOK) {
        if(!isOK){
            return;
        }
        if(selected.getIsEnabled() != null && selected.getIsEnabled()){
            //如果超级管理员激活某个租户，则激活该租户下的所有用户
            userAccountRepository.updateMembersStatus(selected.getUid());
        }else{
            //如果超级管理员关闭某个租户，则关闭该租户下的所有用户
            userAccountRepository.deactiveTenantMembersStatus(selected.getUid());
        }
    }

    @Override
    public void onBeforeNewObject(TenantInfo tenant) {
        tenant.setIsEnabled(false);
        super.onBeforeNewObject(tenant);
    }

    @Override
    protected boolean onPersistException(Exception ex, TenantInfo site){
        if(ex.getClass().equals(DataIntegrityViolationException.class)){
            DataIntegrityViolationException exFkey = (DataIntegrityViolationException) ex;
            if(exFkey.getRootCause().getMessage().toLowerCase().contains("foreign key constraint ")){
                String errMessage = WebUtil.getMessage("ForeighKeyErrorWithSiteId");
                WebUtil.addErrorMessage(errMessage);

                return true;
            }
        }
                
        return false;
    }

    public void onBeforeSave(TenantInfo tenantInfo) {
        if(tenantInfo.getIsEnabled() && tenantInfo.getActiveTime() == null){
            tenantInfo.setActiveTime(new Date());
            tenantInfo.setActiveType(0);
            tenantInfo.setActiveUserId(loginUser.getId());
        }
        if(!tenantInfo.getIsEnabled() && tenantInfo.getActiveTime() != null){
            tenantInfo.setActiveTime(null);
        }

    }
    
}