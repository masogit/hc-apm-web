package com.ge.apm.view.tenant;

import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import org.apache.commons.codec.DecoderException;
import webapp.framework.dao.GenericRepository;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@ManagedBean
@ViewScoped
public class TenantPasswordController extends JpaCRUDController<TenantInfo> {

    TenantInfoRepository dao;
    UserAccountRepository userAccountDao;

    TenantPasswordService tenantPasswordService;

    List<TenantInfo> tenantInfos;

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();

    }

    @Override
    protected GenericRepository<TenantInfo> getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        dao = WebUtil.getBean(TenantInfoRepository.class);
        userAccountDao = WebUtil.getBean(UserAccountRepository.class);
        tenantPasswordService = WebUtil.getBean(TenantPasswordService.class);

        tenantInfos = dao.findTenantInfoByInitialPasswordIsNotNull();

    }

    public void updateUserAccountPasswordWithTenantPassword() {
        String tenantUid = this.selected.getUid();

        try {
            tenantPasswordService.updateUserAccountPasswordWithTenantPassword(tenantUid);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<UserAccount> listUserAccountsWithDefaultPassword() {
        String tenantUid = this.selected.getUid();
        List<UserAccount> resultList = null;

        try {
            resultList = tenantPasswordService.listUserAccountsWithDefaultPassword(tenantUid);
        } catch (DecoderException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public List<TenantInfo> getTenantInfos() {
        return tenantInfos;
    }

    public void setTenantInfos(List<TenantInfo> tenantInfos) {
        this.tenantInfos = tenantInfos;
    }

}