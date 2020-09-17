package com.ge.apm.view.uaa;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang.StringUtils;
import webapp.framework.dao.GenericRepository;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class OrgInfoPathController extends JpaCRUDController<OrgInfo> {

    private OrgInfoRepository orgDao;

    @Override
    protected void init() {
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
    }

    @Override
    protected GenericRepository<OrgInfo> getDAO() {
        return null;
    }

    public String getOrgFullPath(){
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        String orgInfoFullPath =orgDao.listOrgInfoNamePathInHierarchy(currentUserAccount.getOrgInfoUID());
        return orgInfoFullPath;
    }

    public String getOrgFullPathByOrgInfo(OrgInfo currentOrg){
        if (null == currentOrg) {
            return null;
        }
        String orgInfoFullPath = orgDao.listOrgInfoNamePathInHierarchy(currentOrg.getUid());
        return orgInfoFullPath;
    }

    public String getOrgFullPathByOrgUid(String orgUid){
        if (StringUtils.isEmpty(orgUid)) {
            return null;
        }
        String orgInfoFullPath = orgDao.listOrgInfoNamePathInHierarchyWithSlash(orgUid);
        return orgInfoFullPath;
    }
}
