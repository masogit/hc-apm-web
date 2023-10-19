package com.ge.apm.view.report;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.util.PageUtil;
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

    private PageUtil<JmReport> pageUtil = new PageUtil();

    private JmReportRepository dao;
    private TenantInfoRepository siteDao;
    private OrgInfoRepository orgDao;
    private UaaService uaaService;
    private UserContextService userContextService;
    private JmReportAssignTenantRepository assignTenantDao;
    private MicroServiceUtil microServiceUtil;

    @Getter @Setter
    private TenantInfo selectedTenant;
    @Getter @Setter
    private TreeNode orgTree;
    @Getter @Setter
    private UserAccount loginUser;
    @Getter @Setter
    private OrgInfo currentOrg;
    @Getter @Setter
    private Integer tenantId;
    @Setter
    private List<String> assignedRoles;
    @Setter
    private List<String> assignedTenants;
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
        currentOrg = orgDao.findByUid(UserContextService.getCurrentUserAccount().getSiteUID());
        assignTenantDao = WebUtil.getBean(JmReportAssignTenantRepository.class);
        microServiceUtil = WebUtil.getBean(MicroServiceUtil.class);

        this.filterBySite = false;
        this.filterByHospital = false;

        initRole();

        String reportId = WebUtil.getRequestParameter("reportId");
        if (StringUtils.isNotEmpty(reportId)) {
            selected = dao.findById(reportId);
        }
    }

    @Override
    protected JmReportRepository getDAO() {
        return dao;
    }

    private void initRole() {
        roleListVO = uaaService.getSysRoleVO(loginUser.getTenantUID());
    }

    private List<JmReport> setFilter(List<JmReport> reports) {
        if (CollectionUtils.isNotEmpty(this.searchFilters)) {
            String name = (String) searchFilters.get(0).value;
            return reports.stream().filter(val -> val.getName().contains(name)).collect(Collectors.toList());
        }
        return reports;
    }

    @Override
    protected Page<JmReport> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        List<JmReport> reports = new ArrayList<>();
        if (userContextService.hasRole("SuperAdmin")) {
            reports = dao.findAll();
        } else {
            reports = dao.findByTenantUid(loginUser.getTenantUID());
        }
        List<JmReport> reportIds = new ArrayList();
        if (!userContextService.hasAnyRole("SuperAdmin,ITAdmin")) {
            reports.forEach(val -> {
                List<JmReportAssignTenant> list = val.getJmReportAssignTenants().stream().filter(tenants -> tenants.getAssignTenant()
                        .equals(loginUser.getTenantUID()) && tenants.getJmReportId().equals(val.getId())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(list)) {
                    JmReportAssignTenant tenant = list.get(0);
                    if (!userContextService.hasAnyRole(tenant.getAssignRole())) {
                        reportIds.add(val);
                    }
                } else {
                    if (!userContextService.hasAnyRole("ITAdmin")) {
                        reportIds.add(val);
                    }
                }

            });
        }
        reports.removeAll(reportIds);
        reports = setFilter(reports);
        Page<JmReport> pages = pageUtil.pageNum(reports, pageRequest);
        return pages;
    }

    @Override
    public void onFilter(FilterEvent event) {
        selected = null;
        super.onFilter(event);
    }

    public void onSelect() {
        assignedRoles = new ArrayList<>();
        assignedTenants = new ArrayList<>();
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        if (StringUtils.isNotEmpty(selected.getAssignTenant())) {
            selectedTenant = siteDao.findByUid(selected.getAssignTenant());
        } else {
            selectedTenant = siteDao.findByUid(selected.getTenantUID());
        }
    }

    public void save() {
        if (crudActionName.equals("Create")) {
            JmReportAssignTenant assignTenant = new JmReportAssignTenant();
            assignTenant.setAssignTenant(loginUser.getTenantUID());
            assignTenant.setAssignRole("ITAdmin");
            selected.setJmReportAssignTenants(Arrays.asList(assignTenant));
            if (StringUtils.isEmpty(selected.getVisibleRange())) {
                selected.setVisibleRange("0");
            }
        }
        dao.save(selected);
    }

    public void assignTenant(ValueChangeEvent e) {
        assignList(assignedTenants, e);
    }

    public List<String> getAssignedTenants() {
        if (CollectionUtils.isEmpty(assignedTenants)) {
            assignedTenants = new ArrayList<>();
            if (selected != null && selected.getJmReportAssignTenants() != null) {
                selected.getJmReportAssignTenants().forEach(val -> assignedTenants.add(val.getAssignTenant()));
            }
        }
        return assignedTenants;
    }

    public void saveAssignTenant() {
        List<JmReportAssignTenant> assignTenants = Optional.ofNullable(selected.getJmReportAssignTenants()).orElse(new ArrayList<>());
        assignTenants.clear();
        JmReportAssignTenant jmReportAssignTenant;
        for (String item : assignedTenants) {
            jmReportAssignTenant = new JmReportAssignTenant();
            jmReportAssignTenant.setJmReportId(selected.getId());
            jmReportAssignTenant.setAssignTenant(item);
            jmReportAssignTenant.setAssignRole("ITAdmin");
            assignTenants.add(jmReportAssignTenant);
        }
        selected.setJmReportAssignTenants(assignTenants);
        dao.save(selected);
    }

    public void assignRole(ValueChangeEvent e) {
        assignList(assignedRoles, e);
    }

    public void assignList(List<String> list, ValueChangeEvent e) {
        List<String> newValues = (List<String>) e.getNewValue();
        List<String> oldValues = (List<String>) e.getOldValue();
        List<String> newlyCheckedRoles = (List<String>) CollectionUtils.subtract(newValues, oldValues);
        list.clear();
        newlyCheckedRoles.forEach(val -> list.add(val));
    }

    public void saveAssignRole() {
        StringBuilder role = new StringBuilder();
        assignedRoles.forEach(val -> role.append(val + ","));
        String assignRole = role.toString();
        JmReportAssignTenant assignTenant = assignTenantDao.findByJmReportIdAndTenantUid(selected.getId(), loginUser.getTenantUID());
        assignTenant.setAssignRole(assignRole.substring(0, assignRole.length() - 1));
        assignTenantDao.save(assignTenant);
    }

    public List<String> getAssignedRoles() {
        if (CollectionUtils.isEmpty(assignedRoles)) {
            assignedRoles = new ArrayList<>();
            if (selected != null) {
                JmReportAssignTenant assignTenant = assignTenantDao.findByJmReportIdAndTenantUid(selected.getId(), loginUser.getTenantUID());
                if (assignTenant == null) {
                    assignTenant = new JmReportAssignTenant();
                    assignTenant.setJmReportId(selected.getId());
                    assignTenant.setAssignTenant(loginUser.getTenantUID());
                    assignTenant.setAssignRole("ITAdmin");
                    assignTenantDao.save(assignTenant);
                }
                assignedRoles = Arrays.stream(assignTenant.getAssignRole().split(",")).collect(Collectors.toList());
            }
        }
        return assignedRoles;
    }

    public List<SysRoleVO> getRoleListVO() {
        return roleListVO;
    }

    public void cancel() {
        selected = null;
        assignedTenants = null;
        assignedRoles = null;
    }

    public void delete() {
        dao.delete(selected);
    }

    public String getType(Integer type) {
        String typeName = "";
        if (type.equals(1)) {
            typeName = "积木";
        } else if (type.equals(2)) {
            typeName = "达芬奇";
        }
        return typeName;
    }

    public void getViewUrl(JmReport jmReport) {
        this.selected = jmReport;
    }

    public String openNewWindowView(String reportId) {
        if (StringUtils.isEmpty(reportId)) {
            return "";
        }
        return "/portal/jmReport/jmReportView.xhtml?faces-redirect=true&amp;reportId=" +  reportId ;
    }

    public String view(int type) {
        String linkUrl = "";
        if (selected == null) {
            return linkUrl;
        }
        if (selected.getType().equals(1)) {
            switch (type) {
                case 1:
                    linkUrl = selected.getReportViewUrl() + "?token=" + UserContextService.getAccessToken().replace("Bearer ", "");
                    break;
                case 2:
                    linkUrl = selected.getReportEditUrl() + "?token=" + UserContextService.getAccessToken().replace("Bearer ", "");
                    break;
                default:
                    linkUrl = "";
                    break;
            }
        } else if (selected.getType().equals(2)) {
            linkUrl = microServiceUtil.getDaVinciShareLink(UserContextService.getAccessToken(), selected.getReportViewUrl());
        }
        return linkUrl;
    }

}
