package com.ge.apm.view.report;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class JmReportController extends GenericCRUDUUIDController<JmReport> {

    private JmReportRepository dao;
    private TenantInfoRepository siteDao;
    private OrgInfoRepository orgDao;
    private UaaService uaaService;
    private UserContextService userContextService;
    private CustomRoleRepository customRoleRepository;

    @Getter @Setter
    private TenantInfo selectedTenant;
    @Getter @Setter
    private TreeNode orgTree;
    @Getter @Setter
    private UserAccount loginUser;
    @Getter @Setter
    private OrgInfo currentOrg;
    @Getter @Setter
    private int tenantId;
    @Setter
    private List<String> assignedRoles;
    @Setter
    private List<SysRoleVO> roleListVO;

    @Override
    protected void init() {
        userContextService = WebUtil.getBean(UserContextService.class);
        dao = WebUtil.getBean(JmReportRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        loginUser = UserContextService.getCurrentUserAccount();
        siteDao =  WebUtil.getBean(TenantInfoRepository.class);
        customRoleRepository = WebUtil.getBean(CustomRoleRepository.class);
        currentOrg = orgDao.findByUid(UserContextService.getCurrentUserAccount().getSiteUID());
        selectedTenant = siteDao.findByUid(loginUser.getTenantUID());
        this.filterBySite = false;
        this.filterByHospital = false;
        initRole();
    }

    @Override
    protected JmReportRepository getDAO() {
        return dao;
    }

    private void initRole() {
        roleListVO = uaaService.getSysRoleVO(loginUser.getTenantUID());
    }

    private void setFilter() {
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, loginUser.getTenantUID(), "1*2+"));
        searchFilters.add(new SearchFilter("assignTenant", SearchFilter.Operator.EQ, loginUser.getTenantUID(), "1*2+"));

    }

    @Override
    protected Page<JmReport> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        setFilter();
        Page<JmReport> pageResult = dao.findBySearchFilter(this.searchFilters, pageRequest);
        if (!userContextService.hasAnyRole("SuperAdmin,ITAdmin")) {
            pageResult.getContent().forEach(val -> {
                if (!userContextService.hasAnyRole(val.getAssignRole())) {
                    pageResult.getContent().remove(val);
                }
            });
        }
        return pageResult;
    }

    @Override
    public void onFilter(FilterEvent event) {
        selected = null;
        super.onFilter(event);
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
    }

    public void save() {
        if (crudActionName.equals("Create")) {
            selected.setTenantUID(loginUser.getTenantUID());
        }
        if (crudActionName.equals("Edit")) {
            if (tenantId != 0 && tenantId != selectedTenant.getId()) {
                TenantInfo tenantInfo = siteDao.findById(tenantId);
                selected.setAssignTenantName(tenantInfo.getName());
                selected.setAssignTenant(tenantInfo.getUid());
            }
        }
        dao.save(selected);
    }

    public void assignRole(ValueChangeEvent e) {
        List<String> newValues = (List<String>) e.getNewValue();
        List<String> oldValues = (List<String>) e.getOldValue();
        List<String> newlyCheckedRoles = (List<String>) CollectionUtils.subtract(newValues, oldValues);
        assignedRoles.clear();
        newlyCheckedRoles.forEach(val -> assignedRoles.add(val));
    }

    public void saveAssignRole() {
        StringBuilder role = new StringBuilder();
        assignedRoles.forEach(val -> role.append(val + ","));
        String assignRole = role.toString();
        selected.setAssignRole(assignRole.substring(0, assignRole.length() - 1));
        dao.save(selected);
    }

    public List<String> getAssignedRoles() {
        if (CollectionUtils.isEmpty(assignedRoles)) {
            if (selected != null && StringUtils.isNotEmpty(selected.getAssignRole())) {
                assignedRoles = Arrays.stream(selected.getAssignRole().split(",")).collect(Collectors.toList());
            } else {
                assignedRoles = new ArrayList<>();
            }
        }
        return assignedRoles;
    }

    public List<SysRoleVO> getRoleListVO() {
        return roleListVO;
    }

    public void cancel() {
        selected = null;
    }

    public void delete() {
        dao.delete(selected);
    }

}
