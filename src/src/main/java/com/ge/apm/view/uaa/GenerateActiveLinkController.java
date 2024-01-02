package com.ge.apm.view.uaa;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.sysutil.UrlEncryptController;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import webapp.framework.web.WebUtil;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class GenerateActiveLinkController {

    TenantInfoRepository dao;

    private ConfigCacheService configCacheService;

    private OrgInfoRepository orgDao;

    @Getter
    @Setter
    private TenantInfo tenantInfo;

    @Getter
    @Setter
    private OrgInfo hospital;

    @Getter
    @Setter
    private String link;

    @PostConstruct
    protected void init() {
        configCacheService = WebUtil.getBean(ConfigCacheService.class);
        dao = WebUtil.getBean(TenantInfoRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
    }

    public List<OrgInfo> orgList() {
        if (ObjectUtils.isEmpty(tenantInfo)) {
            return new ArrayList<>();
        }
        return orgDao.findByTenantUIDAndOrgType(tenantInfo.getUid(), 2);
    }

    public void generateActiveLink() throws IOException {
        String hospitalUID = hospital.getHospitalUID();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        // 获取 HttpServletRequest 对象
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        // 获取当前项目的请求地址
        String requestURL = request.getRequestURL().toString().replace("/admin/adminActiveLinkGenerate.xhtml", "");

        link = requestURL + "/ActiveFeature.xhtml?str=" + UrlParamUtil.encodeUrlQueryString("hospitalUID=" + hospitalUID);

    }
}
