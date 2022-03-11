package com.ge.apm.view.audit;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.audit.AuditTemplateService;
import com.ge.apm.service.audit.AuditUserCaseService;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 503266962
 */
@ManagedBean
@ViewScoped
public class AuditUserCaseController extends GenericCRUDUUIDController<AuditCase> {


    private AuditCaseRepository dao;
    private AuditEntryTemplateRelRepository relDao;
    private AuditSystemCaseRepository caseDao;
    private AuditTargetRepository targetDao;
    private TenantInfoRepository tenantInfoRepository;
    private OrgInfoRepository orgInfoRepository;
    private AuditSystemEntryRepository auditSystemEntryRepository;

    private BlobFileService blobService;
    private AuditTemplateService auditTemplateService;
    private AuditUserCaseService auditUserCaseService;
    // 编辑页面用
    private String selectedTenantUid;
    // 列表页面用
    private TenantInfo selectedTenant;
    private OrgInfo selectedOrg;

    @Override
    protected void init() {
        this.filterBySite = false;
        dao = WebUtil.getBean(AuditCaseRepository.class);
        relDao = WebUtil.getBean(AuditEntryTemplateRelRepository.class);
        caseDao = WebUtil.getBean(AuditSystemCaseRepository.class);
        targetDao = WebUtil.getBean(AuditTargetRepository.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        auditSystemEntryRepository = WebUtil.getBean(AuditSystemEntryRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        auditTemplateService = WebUtil.getBean(AuditTemplateService.class);
        auditUserCaseService = WebUtil.getBean(AuditUserCaseService.class);

        selectedTenant = new TenantInfo();
        selectedTenant.setName("请选择租户");

        selectedOrg = new OrgInfo();
        selectedOrg.setUid("请选择院区");
        selectedOrg.setName("请选择院区");
    }

    @Override
    protected GenericRepositoryUUID<AuditCase> getDAO() {
        return dao;
    }

    @Override
    protected Page<AuditCase> loadData(PageRequest pageRequest) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        PageRequest page = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
        if(CollectionUtils.isEmpty(this.searchFilters)){
            this.searchFilters = new ArrayList<>();
        }
        removeFilterOnField("siteUid");
        this.searchFilters.add(new SearchFilter("siteUid", SearchFilter.Operator.EQ, this.selectedOrg.getUid()));
        List<SearchFilter> searchFilters = this.searchFilters;
        Page<AuditCase> pageList = dao.findBySearchFilter(searchFilters, page);

        List<AuditCase> list = pageList.getContent();
        if(CollectionUtils.isEmpty(list)){
            return pageList;
        }

        for(AuditCase auditCase : list){
            AuditSystemCase auditSystemCase = auditCase.getAuditSystemCase();
            Integer stripNum=0;//条总数
            Integer paymentNum=0;//款总数
            Integer targetNum=0;//指标总数
            List<AuditSystemEntry> auditSystemEntries = auditSystemEntryRepository.findByCaseUid(auditSystemCase.getUid());
            if (auditSystemEntries!=null && !auditSystemEntries.isEmpty()){
                for (AuditSystemEntry auditSystemEntry : auditSystemEntries){
                    if (auditSystemEntry.getType()==1){
                        stripNum++;
                    }else if(auditSystemEntry.getType()==2){
                        paymentNum++;
                    }else {
                        targetNum++;
                    }
                }
            }
            auditSystemCase.setStripNum(stripNum);
            auditSystemCase.setPaymentNum(paymentNum);
            auditSystemCase.setTargetNum(targetNum);
        }

        return pageList;
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        this.selectedTenantUid = null;
        super.prepareCreate();
    }

    public void setSelected(String uid) {
        AuditCase auditCase = dao.findById(uid);
        super.setSelected(auditCase);
    }

    public void saveAuditCase(){
        auditUserCaseService.saveUserCase(this.selected);
        this.selected=null;
    }

    /**
     * 用户版本回退
     */
    public void initialize(String caseUid){
        auditUserCaseService.initialize(caseUid);
    }

    public List<TenantInfo> getAllTenant(){
        if (UserContextService.isSuperAdminWithPartnerAccount()) {
            return tenantInfoRepository.findAllByTestStatusIsNullOrTestStatusNotOrderByName(-1);
        } else {
            return tenantInfoRepository.getAllOrderByName();
        }
    }

    public List<OrgInfo> getSiteByTenant(){
        if(StringUtils.isEmpty(this.selectedTenantUid)){
            return new ArrayList<>();
        }
        return orgInfoRepository.findAllSiteByTenantUid(this.selectedTenantUid);
    }

    public List<AuditSystemCase> getAllSystemCase(){
        Iterable<AuditSystemCase> systemCases = caseDao.findAll();
        if(systemCases != null){
            return Lists.newArrayList(systemCases);
        }
        return new ArrayList<>();
    }

    public List<AuditTarget> getTarget(){
        if(this.selected == null || StringUtils.isEmpty(this.selected.getSystemCaseUid())){
            return new ArrayList<>();
        }
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("systemCaseUid", SearchFilter.Operator.EQ, this.selected.getSystemCaseUid()));
        List<AuditTarget> targetList = targetDao.findBySearchFilter(searchFilters);
        return targetList;
    }

    public void setSelectTenantInfo(){
        if(StringUtils.isNotEmpty(this.selectedTenant.getUid())){
            this.selectedTenant = tenantInfoRepository.findByuid(this.selectedTenant.getUid());
        }
    }

    public void setSelectOrgInfo(){
        if(StringUtils.isNotEmpty(this.selectedOrg.getUid())){
            this.selectedOrg = orgInfoRepository.findByUid(this.selectedOrg.getUid());
        }
    }

    /**
     * 列表页面的院区查询
     * @return
     */
    public List<OrgInfo> getSiteByTenantForListPage(){
        if(StringUtils.isEmpty(this.selectedTenant.getUid())){
            return new ArrayList<>();
        }
        return orgInfoRepository.findAllSiteByTenantUid(this.selectedTenant.getUid());
    }

    public void setTenant(String tenantUid) {
        this.selectedTenantUid = tenantUid;
    }

    public String getSelectedTenantUid() {
        return selectedTenantUid;
    }

    public void setSelectedTenantUid(String selectedTenantUid) {
        this.selectedTenantUid = selectedTenantUid;
    }

    public TenantInfo getSelectedTenant() {
        return selectedTenant;
    }

    public void setSelectedTenant(TenantInfo selectedTenant) {
        this.selectedTenant = selectedTenant;
    }

    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public void setSelectedOrg(OrgInfo selectedOrg) {
        this.selectedOrg = selectedOrg;
    }
}
