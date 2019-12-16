package com.ge.apm.view.asset;

import com.ge.apm.dao.FileAttachmentOrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.FileAttachmentOrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 212605082 on 2019/12/9.
 */
@ManagedBean
@ViewScoped
public class ZkbUserFileAttachmentController extends GenericCRUDUUIDController<FileAttachmentOrgInfo> {


    private FileAttachmentOrgInfoRepository dao;
    private TenantInfoRepository tenantInfoDao;

    private UserAccount currentUser;

    @Override
    protected void init() {
        dao = WebUtil.getBean(FileAttachmentOrgInfoRepository.class);
        tenantInfoDao = WebUtil.getBean(TenantInfoRepository.class);

        currentUser = UserContextService.getCurrentUserAccount();
    }

    @Override
    protected FileAttachmentOrgInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<FileAttachmentOrgInfo> loadData(PageRequest pageRequest) {

        List<SearchFilter> searchFilters = this.searchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }

        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID()));

        Page<FileAttachmentOrgInfo> fileAttachmentOrgInfoPage = dao.findBySearchFilter(searchFilters, pageRequest);

        return fileAttachmentOrgInfoPage;

    }

}
