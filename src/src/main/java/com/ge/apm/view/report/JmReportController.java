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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
    private JmReportAssignTenantRepository assignTenantDao;

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

    private void setFilter() {
    }

    @Override
    protected Page<JmReport> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        setFilter();
        List<JmReport> reports = dao.findByTenantUid(loginUser.getTenantUID());
        List<JmReport> reportIds = new ArrayList();
        if (!userContextService.hasAnyRole("SuperAdmin,ITAdmin")) {
            reports.forEach(val -> {
                JmReportAssignTenant assignTenant = val.getJmReportAssignTenants().stream().filter(tenants -> tenants.getAssignTenant().equals(loginUser.getTenantUID()) && tenants.getJmReportId().equals(val.getId())).collect(Collectors.toList()).get(0);
                if (!userContextService.hasAnyRole(assignTenant.getAssignRole())) {
                    reportIds.add(val);
                }
            });
        }
        reports.removeAll(reportIds);
        int size = reports.size();
        return new PageImpl<>(pageNum(reports, pageRequest), pageRequest, size);
    }

    private List<JmReport> pageNum(List<JmReport> jmReportList, PageRequest pageRequest) {
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize() - 1;
        int numStart = pageNumber * pageSize;
        numStart = numStart + pageNumber;
        int numEnd = numStart + pageSize + 1;
        int size = jmReportList.size();
        if(size > numEnd){
            jmReportList = jmReportList.subList(numStart ,numEnd);
        }else {
            jmReportList = jmReportList.subList(numStart ,size);
        }
        return jmReportList;
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

    public void tenantChange() {
        if (tenantId != null && tenantId != selectedTenant.getId()) {
            selectedTenant = siteDao.findById(tenantId);
        }
    }

    public void assignTenant(ValueChangeEvent e) {
        List<String> newValues = (List<String>) e.getNewValue();
        List<String> oldValues = (List<String>) e.getOldValue();
        List<String> newlyCheckedRoles = (List<String>) CollectionUtils.subtract(newValues, oldValues);
        assignedTenants.clear();
        newlyCheckedRoles.forEach(val -> assignedTenants.add(val));
    }

    public List<String> getAssignedTenants() {
        if (CollectionUtils.isEmpty(assignedTenants)) {
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
        JmReportAssignTenant assignTenant = assignTenantDao.findByJmReportIdAndTenantUid(selected.getId(), loginUser.getTenantUID());
        assignTenant.setAssignRole(assignRole.substring(0, assignRole.length() - 1));
        assignTenantDao.save(assignTenant);
    }

    public List<String> getAssignedRoles() {
        if (CollectionUtils.isEmpty(assignedRoles)) {
            if (selected != null) {
                JmReportAssignTenant assignTenant = assignTenantDao.findByJmReportIdAndTenantUid(selected.getId(), loginUser.getTenantUID());
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

    public String getViewUrl(String reportId) {
        if (StringUtils.isNotEmpty(reportId)) {
            return "/portal/jmReport/jmReportView.xhtml?faces-redirect=true&amp;reportId=" +  reportId ;
        }
        return "";
    }

    public String getCheckUrl(String reportId) {
        if (StringUtils.isNotEmpty(reportId)) {
            return "/portal/jmReport/jmReportCheck.xhtml?faces-redirect=true&amp;reportId=" +  reportId ;
        }
        return "";
    }

    public String view() {
        if (selected != null) {
            return  selected.getReportViewUrl() + "?token=" + UserContextService.getAccessToken().replace("Bearer ", "");
        }
        return "";
    }

    public String check() {
        if (selected != null) {
            return  selected.getReportEditUrl() + "?token=" + UserContextService.getAccessToken().replace("Bearer ", "");
        }
        return "";
    }

}
