package com.ge.apm.view.audit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.audit.AuditTemplateService;
import com.ge.apm.service.audit.AuditUserCaseService;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
    private AuditEventRepository auditEventRepository;
    private AuditCaseRepository auditCaseRepository;

    private BlobFileService blobService;
    private AuditTemplateService auditTemplateService;
    private AuditUserCaseService auditUserCaseService;
    // 列表页面用
    private TenantInfo selectedTenant;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AuditCaseRepository.class);
        relDao = WebUtil.getBean(AuditEntryTemplateRelRepository.class);
        caseDao = WebUtil.getBean(AuditSystemCaseRepository.class);
        targetDao = WebUtil.getBean(AuditTargetRepository.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        auditSystemEntryRepository = WebUtil.getBean(AuditSystemEntryRepository.class);
        auditEventRepository = WebUtil.getBean(AuditEventRepository.class);
        auditCaseRepository = WebUtil.getBean(AuditCaseRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        auditTemplateService = WebUtil.getBean(AuditTemplateService.class);
        auditUserCaseService = WebUtil.getBean(AuditUserCaseService.class);
        selectedTenant = new TenantInfo();
        selectedTenant.setName("请选择租户");
    }

    @Override
    protected GenericRepositoryUUID<AuditCase> getDAO() {
        return dao;
    }

    @Override
    protected Page<AuditCase> loadData(PageRequest pageRequest) {
        if(CollectionUtils.isEmpty(this.searchFilters)){
            this.searchFilters = new ArrayList<>();
        }
        List<SearchFilter> searchFilters = this.searchFilters;
        Page<AuditCase> pageList = dao.findBySearchFilter(searchFilters, pageRequest);

        List<AuditCase> list = pageList.getContent();
        if(CollectionUtils.isEmpty(list)){
            return pageList;
        }

        for(AuditCase auditCase : list){
            AuditSystemCase auditSystemCase = auditCase.getAuditSystemCase();

            AuditTarget target = targetDao.findBySystemCaseUidAndAndTarget(auditSystemCase.getUid(), auditCase.getTarget());

            if(target != null){
                auditCase.setTargetId(target.getTarget());
                auditCase.setTargetScore(target.getScore());
            }

            Integer stripNum=0;//条总数
            Integer paymentNum=0;//款总数
            Integer targetNum=0;//指标总数
            Double scoreNum=0D;//总分值
            List<AuditSystemEntry> auditSystemEntries = auditSystemEntryRepository.findByCaseUid(auditSystemCase.getUid());
            if (auditSystemEntries!=null && !auditSystemEntries.isEmpty()){
                for (AuditSystemEntry auditSystemEntry : auditSystemEntries){
                    if (Objects.equals(auditSystemEntry.getType(),1)){
                        stripNum++;
                    }else if(Objects.equals(auditSystemEntry.getType(),2)){
                        BigDecimal score = new BigDecimal(scoreNum);
                        BigDecimal scoreStandard = new BigDecimal(auditSystemEntry.getScoreStandard()==null ? 0D :auditSystemEntry.getScoreStandard());
                        scoreNum=score.add(scoreStandard).doubleValue();
                        paymentNum++;
                    }else {
                        String parentUid = auditSystemEntry.getParentUid();
                        AuditSystemEntry data = auditSystemEntryRepository.findByUid(parentUid);
                        if(data!=null && Objects.equals(data.getType(),1)){
                            BigDecimal score = new BigDecimal(scoreNum);
                            BigDecimal scoreStandard = new BigDecimal(auditSystemEntry.getScoreStandard()==null ? 0D :auditSystemEntry.getScoreStandard());
                            scoreNum=score.add(scoreStandard).doubleValue();
                        }
                        targetNum++;
                    }
                }
            }
            auditSystemCase.setStripNum(stripNum);
            auditSystemCase.setPaymentNum(paymentNum);
            auditSystemCase.setTargetNum(targetNum);
            auditSystemCase.setScoreNum(scoreNum);
        }

        return pageList;
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        this.selected.setAllowDownload(true);
    }

    public void setSelected(String uid) {
        AuditCase auditCase = dao.findById(uid);
        super.setSelected(auditCase);
    }

    public String saveAuditCase(){
        try {
            auditUserCaseService.saveUserCase(this.selected);
            createEvent("ADD_CASE", this.selected, "success");
            this.selected=null;
            WebUtil.addSuccessMessage(WebUtil.getMessage("Operation") + WebUtil.getMessage("Success"));
        }catch (Exception e){
            createEvent("ADD_CASE", this.selected, "fail");
            WebUtil.addErrorMessage(WebUtil.getMessage("Operation") + WebUtil.getMessage("Failed") + "！" + e.getMessage());
            return "";
        }

        return "auditUserCaseList?faces-redirect=true";
    }

    /**
     * 用户版本回退
     */
    public void initialize(String caseUid){
        AuditCase auditCase = auditCaseRepository.findById(caseUid);
        try {
            auditUserCaseService.initialize(caseUid);
            createEvent("INIT_TENANT_AUDIT", auditCase, "success");
            WebUtil.addSuccessMessage(WebUtil.getMessage("Initialize") + WebUtil.getMessage("Success"));
        }catch (Exception e){
            createEvent("INIT_TENANT_AUDIT", auditCase, "fail");
            WebUtil.addErrorMessage(WebUtil.getMessage("Initialize") + WebUtil.getMessage("Failed"));
        }
    }

    /**
     * 用户版本过期
     */
    public void expired(String caseUid){
        AuditCase auditCase = auditCaseRepository.findById(caseUid);
        try {
            auditCaseRepository.expiredCase(caseUid);
            createEvent("EXPIRED_TENANT_AUDIT", auditCase, "success");
            WebUtil.addSuccessMessage(WebUtil.getMessage("expired") + WebUtil.getMessage("Success"));
        }catch (Exception e){
            createEvent("EXPIRED_TENANT_AUDIT", auditCase, "fail");
            WebUtil.addErrorMessage(WebUtil.getMessage("expired") + WebUtil.getMessage("Failed"));
        }
    }

    public void changeAllowDownload(String caseUid, Boolean allowDownload){
        try {
            AuditCase auditCase = auditCaseRepository.findById(caseUid);
            auditCase.setAllowDownload(allowDownload);
            auditCase.setUpdater(UserContextService.getCurrentUserAccount().getName());
            auditCase.setUpdateTime(new Date());
            auditCaseRepository.save(auditCase);
            WebUtil.addSuccessMessage(WebUtil.getMessage("Operation") + WebUtil.getMessage("Success"));
        } catch (Exception e){
            WebUtil.addErrorMessage(WebUtil.getMessage("Operation") + WebUtil.getMessage("Failed") + "！" + e.getMessage());
        }
    }

    public List<TenantInfo> getAllTenant(){
        List<TenantInfo> list = tenantInfoRepository.getAllOrderByName();
        List<TenantInfo> tenantInfoList = list.stream().filter(item -> {
            if (StringUtils.isNotEmpty(item.getFeatureList())) {
                Set<String> apmFeatures = ((List<String>)JSON.parse(item.getFeatureList())).stream().collect(Collectors.toSet());
                if (apmFeatures.contains("AUDIT")) {
                    return true;
                }
            }
            String tenantConfig = item.getTenantConfig();
            JSONObject jsonObject = JSONObject.parseObject(tenantConfig);
            if(jsonObject != null){
                JSONArray jsonArray = jsonObject.getJSONArray("menuList");
                List<String> menuList = jsonArray != null ? jsonArray.toJavaList(String.class) : null;
                if (menuList != null && menuList.size() > 0 && (menuList.contains("audit"))) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
        return tenantInfoList;
    }

    public List<OrgInfo> getSiteByTenant(){
        if(this.selected == null || StringUtils.isEmpty(this.selected.getTenantUid())){
            return new ArrayList<>();
        }
        return orgInfoRepository.findAllSiteByTenantUid(this.selected.getTenantUid());
    }

    public List<AuditSystemCase> getAllSystemCase(){
        Iterable<AuditSystemCase> systemCases = caseDao.findByStatus(2);
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

    /**
     * 列表页面的院区查询
     * @return
     */
    public List<OrgInfo> getSiteByTenantForListPage(){
        if(this.selectedTenant == null || StringUtils.isEmpty(this.selectedTenant.getUid())){
            return new ArrayList<>();
        }
        List<OrgInfo> list = orgInfoRepository.findAllSiteByTenantUid(this.selectedTenant.getUid());
        return list;
    }

    public String getTenantName(String tenantUID) {
        return tenantInfoRepository.findByuid(tenantUID).getName();
    }

    public String getSiteNameByUId(String uid) {
        OrgInfo oi = orgInfoRepository.getByUid(uid);
        if (oi == null) {
            return null;
        }
        if (Objects.equals(oi.getOrgLevel(), 3)) {
            return oi.getName();
        }
        return null;
    }

    public TenantInfo getSelectedTenant() {
        return selectedTenant;
    }

    public void setSelectedTenant(TenantInfo selectedTenant) {
        this.selectedTenant = selectedTenant;
    }

    private void createEvent(String eventType, AuditCase auditCase, String result){
        AuditEvent event = new AuditEvent();
        event.setEventType(eventType);
        event.setOperator(UserContextService.getCurrentUserAccount().getName());
        event.setContent(JSON.toJSONString(auditCase));
        event.setEventObjectUid(auditCase.getUid());
        event.setResult(result);
        event.setEventTime(new Date());
        event.setSiteUid(auditCase.getSiteUid());
        auditEventRepository.save(event);
    }
}
