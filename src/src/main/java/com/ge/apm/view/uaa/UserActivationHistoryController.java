package com.ge.apm.view.uaa;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@ManagedBean(name = "userActivationHistoryController")
@ViewScoped
public class UserActivationHistoryController extends JpaCRUDController<UserActivationHistory> {
    public static final int ACTIVATION_ROLE = 10;

    OrgInfoRepository orgInfoRepository = null;

    UaaService uaaService;

    UserActivationHistoryRepository userActivationHistoryRepository;

    UserAccountRepository userAccountRepository;

    UserAccount loginUser;

    TenantInfoRepository tenantInfoRepository;

    private Boolean agree = false;

    private List<String> items;

    @Override
    protected void init() {
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        userActivationHistoryRepository = WebUtil.getBean(UserActivationHistoryRepository.class);
        loginUser = UserContextService.getCurrentUserAccount();
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
    }

    @Override
    protected UserActivationHistoryRepository getDAO() {
        return userActivationHistoryRepository;
    }

    @Override
    protected Page<UserActivationHistory> loadData(PageRequest pageRequest) {
        return userActivationHistoryRepository.findAll(pageRequest);
    }

    @Transactional
    public void confirmActivation(){
        if(loginUser.getIsActive()){
            System.out.println("already active !");
            WebUtil.addSuccessMessage("激活成功！");
            return;
        }
        loginUser.setIsActive(true);
        userAccountRepository.save(loginUser);
        UserActivationHistory history = new UserActivationHistory();
        Date now = new Date();
        history.setActiveTime(now);
        history.setCreateTime(now);
        history.setLastModifyTime(now);
        history.setUserId(loginUser.getId());
        history.setTenantUID(loginUser.getTenantUID());
        history.setInstitutionUID(loginUser.getInstitutionUID());
        history.setHospitalUID(loginUser.getHospitalUID());
        history.setSiteUID(loginUser.getSiteUID());
        userActivationHistoryRepository.save(history);
        List<UserRole> roles = loginUser.getUserRoleList();
        roles.stream().filter(role -> role.getRoleId() == ACTIVATION_ROLE);
        if(CollectionUtils.isNotEmpty(roles)){
            TenantInfo tenant = tenantInfoRepository.findByuid(loginUser.getTenantUID());
            if(tenant.getIsEnabled() != null && !tenant.getIsEnabled()){
                tenant.setIsEnabled(true);
                tenantInfoRepository.save(tenant);
                userAccountRepository.updateMembersStatus(loginUser.getTenantUID());
            }
        }
        WebUtil.addSuccessMessage("激活成功！");
    }

    public void logout(){
        /*String context = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        String logoutUrl = FacesContext().getExternalContext().getRequestContextPath()+ "/logout.xhtml?uid="+loginUser.getId();
        return logoutUrl;*/
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null){
            ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
            HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
            HttpServletResponse response = (HttpServletResponse) ctx.getResponse();
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        WebUtil.redirectTo("/login.xhtml");
    }

    public void updateAgree(){
        if(CollectionUtils.isNotEmpty(items)){
            String str = items.get(0);
            agree = Integer.parseInt(str) == 1 ? true : false;
        }else{
            agree = false;
        }
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public Boolean getAgree() {
        return agree;
    }

    public void setAgree(Boolean agree) {
        this.agree = agree;
    }
}