package com.ge.apm.view.audit;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;

/**
 * @author 503189829
 * @create 2022/2/24
 * @since 1.0.0
 */
@ManagedBean
@ViewScoped
public class AuditSystemCaseEditController extends GenericCRUDUUIDController<AuditSystemCase> {

    private AuditSystemCaseRepository dao;
    private AuditSystemEntryRepository auditSystemEntryRepository;
    private AuditConfigItemRepository auditConfigItemRepository;
    private AuditEntryItemRelRepository auditEntryItemRelRepository;
    private AuditTargetRepository auditTargetRepository;
    private AuditTemplateRepository auditTemplateRepository;
    private AuditEntryTemplateRelRepository auditEntryTemplateRelRepository;

    private String retrieval;
    private String entryName;
    private String entryMark;
    private Integer entryType;
    private Integer targetType;
    private Double targetScore;
    private String entryMarkName;
    private String caseUid;
    private String entryUid;
    private String configItemUid;
    private String templateUid;
    private Double paymentScoreStandard;
    private Double targetScoreStandard;
    private boolean onSelectFlag = true;
    private boolean entryFlag = false;
    private int caseLevel=0;
    private Integer entryOrderBy =0;
    private TreeNode orgTree;
    private TreeNode selectedNode;
    private AuditSystemEntry auditSystemEntry;
    private List<AuditConfigItem> auditConfigItemStripList =new ArrayList<>();
    private List<AuditConfigItem> auditConfigItemPaymentList=new ArrayList<>();
    private List<AuditConfigItem> auditConfigItemTargetList=new ArrayList<>();
    private List<AuditConfigItem> auditConfigItems=new ArrayList<>();
    private List<AuditTarget> auditTargets=new ArrayList<>();
    private List<AuditTemplate> auditTemplates=new ArrayList<>();
    private final AuditConfigItem auditConfigItem = new AuditConfigItem();
    private final AuditTarget auditTarget = new AuditTarget();
    private final AuditTemplate auditTemplate = new AuditTemplate();
    public static final String STRIP = "STRIP";//条
    public static final String PAYMENT = "PAYMENT";//款
    public static final String TARGET = "TARGET";//指标
    protected String currentTabId = null;
    @Override
    protected void init() {
        dao = WebUtil.getBean(AuditSystemCaseRepository.class);
        auditSystemEntryRepository = WebUtil.getBean(AuditSystemEntryRepository.class);
        auditConfigItemRepository = WebUtil.getBean(AuditConfigItemRepository.class);
        auditEntryItemRelRepository = WebUtil.getBean(AuditEntryItemRelRepository.class);
        auditTargetRepository = WebUtil.getBean(AuditTargetRepository.class);
        auditTemplateRepository = WebUtil.getBean(AuditTemplateRepository.class);
        auditEntryTemplateRelRepository = WebUtil.getBean(AuditEntryTemplateRelRepository.class);
        String uid = WebUtil.getRequestParameter("uid");
        if (uid!=null){
            selected = dao.findByUid(uid);
            caseUid = selected.getUid();
        }
        if (selected==null){
            selected = new AuditSystemCase();
        }
        buildOrdTree();

    }

    public void buildOrdTree() {
        String uid=null;
        if (selected!=null){
            uid= selected.getUid();
        }
        List<AuditSystemEntry> auditSystemEntryList =auditSystemEntryRepository.entryList(uid);
        getOrgTreeNodes(auditSystemEntryList,"1");
    }
    @Override
    protected GenericRepositoryUUID<AuditSystemCase> getDAO() {
        return dao;
    }
    @Override
    protected Page<AuditSystemCase> loadData(PageRequest pageRequest) {
        Page<AuditSystemCase> searchFilter = dao.findBySearchFilter(searchFilters, pageRequest);
        if(searchFilter!=null){
            List<AuditSystemCase> list = searchFilter.getContent();
            for(AuditSystemCase auditSystemCase:list){
                Integer stripNum=0;//条总数
                Integer paymentNum=0;//款总数
                Integer targetNum=0;//指标总数
                List<AuditSystemEntry> auditSystemEntries = auditSystemEntryRepository.findByCaseUid(auditSystemCase.getUid());
                for (AuditSystemEntry auditSystemEntry : auditSystemEntries){
                    if (auditSystemEntry.getType()==1){
                        stripNum++;
                    }else if(auditSystemEntry.getType()==2){
                        paymentNum++;
                    }else {
                        targetNum++;
                    }
                }
                auditSystemCase.setStripNum(stripNum);
                auditSystemCase.setPaymentNum(paymentNum);
                auditSystemCase.setTargetNum(targetNum);
            }
        }

        return searchFilter;
    }
    public TreeNode getOrgTreeNodes(List<AuditSystemEntry> auditSystemEntryList,String siteUID) {
        orgTree = new DefaultTreeNode("root", null, null);
        Map<String, TreeNode> treeMap = new LinkedHashMap<>();
        Map<String, TreeNode> parentTreeMap = new LinkedHashMap<>();
        for (AuditSystemEntry auditSystemEntry : auditSystemEntryList) {
            TreeNode node = new DefaultTreeNode("org", auditSystemEntry, null);
            node.setRowKey("org_" + auditSystemEntry.getUid());
            if(auditSystemEntry.getLevel()==1){
                parentTreeMap.put(auditSystemEntry.getUid(),node);
            }
            treeMap.put(auditSystemEntry.getUid(), node);
        }
        for (Map.Entry<String, TreeNode> nodeItem : treeMap.entrySet()) {
            TreeNode node = nodeItem.getValue();
            AuditSystemEntry auditSystemEntry = (AuditSystemEntry) node.getData();
            if (auditSystemEntry.getParentUid() != null) {
                TreeNode parentNode = treeMap.get(auditSystemEntry.getParentUid());
                if (parentNode != null) {
                    parentNode.getChildren().add(node);
                }
            }
        }
        for (Map.Entry<String, TreeNode> nodeItem : parentTreeMap.entrySet()) {
            String parentUid = nodeItem.getKey();
            if (treeMap.get(parentUid) != null) {
                orgTree.getChildren().add(treeMap.get(parentUid));
            }
        }

        return orgTree;
    }
    public void saveAuditSystemEntry() {
        AuditSystemEntry date = new AuditSystemEntry();
        date.setName(entryName);
        date.setMark(entryMark);
        date.setCaseUid(caseUid);
        date.setScoreInit(0D);
        Integer type=0;
        if(auditSystemEntry!=null){
            type= auditSystemEntry.getType();
        }
        auditSystemEntry=null;
        if (caseLevel!=0){
            ++caseLevel;
            if(type==3){
                caseLevel=0;
                entryType=null;
                WebUtil.addErrorMessage("错误: 指标没有子节点,不能配置");
                return;
            }
            date.setLevel(caseLevel);
            if (entryType==null){
               entryType = caseLevel;
            }
            date.setType(entryType);
            date.setOrderBy(entryType);
        }else {
            date.setLevel(1);
            date.setOrderBy(1);
            date.setType(1);
            entryUid="1";
        }
        date.setParentUid(entryUid);
        auditSystemEntryRepository.save(date);
        buildOrdTree();
        caseLevel=0;
        entryType=null;
    }
    public void saveAuditConfigItem() {
        AuditConfigItem auditConfigItem = auditConfigItemRepository.findByUid(configItemUid);
        AuditEntryItemRel auditEntryItemRel = new AuditEntryItemRel();
        auditEntryItemRel.setItemUid(auditConfigItem.getUid());
        auditEntryItemRel.setSystemEntryUid(auditSystemEntry.getUid());
        if ( currentTabId == STRIP){
            auditEntryItemRel.setOrderBy(1);
        }else if(currentTabId == PAYMENT){
            auditEntryItemRel.setOrderBy(2);
            auditSystemEntry.setScoreStandard(paymentScoreStandard);
        }else {
            auditSystemEntry.setScoreStandard(targetScoreStandard);
            auditEntryItemRel.setOrderBy(3);
        }

        auditSystemEntryRepository.save(auditSystemEntry);
        auditEntryItemRelRepository.save(auditEntryItemRel);
    }
    public void saveAuditTemplate() {
        AuditTemplate auditTemplate = auditTemplateRepository.findByUid(templateUid);
        AuditEntryTemplateRel auditEntryTemplateRel = new AuditEntryTemplateRel();
        auditEntryTemplateRel.setEntryUid(auditSystemEntry.getUid());
        auditEntryTemplateRel.setTemplateUid(auditTemplate.getUid());
        auditEntryTemplateRel.setOrderBy(3);
        auditEntryTemplateRelRepository.save(auditEntryTemplateRel);
    }
    public void saveAuditTarget() {
        AuditTarget auditTarget = new AuditTarget();
        auditTarget.setScore(targetScore);
        auditTarget.setSystemCaseUid(caseUid);
        auditTarget.setTarget(targetType);
        auditTargetRepository.save(auditTarget);
    }

    public void deleteAuditTarger(AuditTarget auditTarget){
        auditTargetRepository.delete(auditTarget);
    }
    public void deleteTemplate(AuditTemplate auditTemplate){
        auditEntryTemplateRelRepository.deleteByTemplateUid(auditTemplate.getUid());
    }

    public void deleteNode(AuditConfigItem auditConfigItem) {
        auditEntryItemRelRepository.deleteByItemUid(auditConfigItem.getUid());
        if (caseLevel==1){
            auditConfigItemStripList.remove(auditConfigItem);
        }else if(caseLevel==2){
            auditConfigItemPaymentList.remove(auditConfigItem);
        }else {
            auditConfigItemTargetList.remove(auditConfigItem);
        }

    }
    public void onSelectTreeNode(NodeSelectEvent event) {
        caseLevel=0;
        entryUid = null;
        onSelectFlag=false;
        entryType=null;
        TreeNode node = event.getTreeNode();
        auditSystemEntry = (AuditSystemEntry) node.getData();
        entryUid = auditSystemEntry.getUid();
        caseLevel = auditSystemEntry.getLevel();
        entryOrderBy = auditSystemEntry.getOrderBy();
        if (caseLevel==1){
            currentTabId = STRIP;
        }else if(caseLevel==2){
            currentTabId = PAYMENT;
            paymentScoreStandard=auditSystemEntry.getScoreStandard();
        }else {
            currentTabId = TARGET;
            targetScoreStandard=auditSystemEntry.getScoreStandard();
        }
    }
    public void preDelete() {
        onSelectFlag=true;
         if (auditSystemEntry==null){
             WebUtil.addErrorMessage("请选择数据!");
             return;
         }
        auditSystemEntryRepository.delete(auditSystemEntry);
        deleteAuditSystemEntryList(auditSystemEntry.getUid());
        buildOrdTree();
    }

    public  void deleteAuditSystemEntryList(String parentUid){
        List<AuditSystemEntry> list = auditSystemEntryRepository.findByParentUid(parentUid);
        if (list!=null && !list.isEmpty()){
            for ( AuditSystemEntry auditSystemEntry:list ) {
                auditSystemEntryRepository.delete(auditSystemEntry);
                deleteAuditSystemEntryList(auditSystemEntry.getUid());
            }
        }
    }
    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        if(selected==null){
            selected=new AuditSystemCase();
        }
    }

    public void preCreate()  {
        if (selected!=null){
            caseUid = selected.getUid();
        }
        entryName=null;
        entryMark=null;
        if (onSelectFlag){
            caseLevel=0;
            entryUid = null;
        }
        if (caseLevel==1){
            entryFlag =true;
        }else{
            entryFlag =false;
        }
    }
    public List<AuditTemplate> auditTemplates(){
        return auditTemplateRepository.findByType(1);
    }
    public List<AuditConfigItem> auditConfigItemList(){
        Integer contentTyp =1;
        if (entryType!=null){
            contentTyp =entryType;
        }
        auditConfigItems = auditConfigItemRepository.findByContentType(contentTyp);
        return auditConfigItems;
    }
    public List<AuditTarget> getTargetTypeList() {
        auditTargets = auditTargetRepository.findBySystemCaseUid(caseUid);
        if (auditTargets.contains(auditTarget)){
            auditTargets.remove(auditTarget);
        }
        auditTarget.setUid("000");
        auditTargets.add(auditTarget);
        return auditTargets;
    }
    public List<AuditTemplate> getAuditTemplateList() {
        auditTemplates = auditEntryTemplateRelRepository.entryAndTemplateList(entryUid,3);
        if (auditTemplates.contains(auditTemplate)){
            auditTemplates.remove(auditTemplate);
        }
        auditTemplate.setUid("000");
        auditTemplates.add(auditTemplate);
        return auditTemplates;
    }
    public List<AuditConfigItem> getStripList() {
        auditConfigItemStripList= new ArrayList<>();
        buildaAuditConfigItems(auditConfigItemStripList,1);
       return auditConfigItemStripList;
    }
    public List<AuditConfigItem> getPaymentList() {
        auditConfigItemPaymentList= new ArrayList<>();
        buildaAuditConfigItems(auditConfigItemPaymentList,2);
       return auditConfigItemPaymentList;
    }
    public List<AuditConfigItem> getTargetList() {
        auditConfigItemTargetList= new ArrayList<>();
        buildaAuditConfigItems(auditConfigItemTargetList,3);
       return auditConfigItemTargetList;
    }
    private void buildaAuditConfigItems(List<AuditConfigItem> auditConfigItems,Integer orderBy){
        if (auditSystemEntry!=null){
            List<String> systemEntryUids = new ArrayList<>();
            systemEntryUids.add(auditSystemEntry.getUid());
            if(!"1".equals(auditSystemEntry.getParentUid())){
                systemEntryUids.add(auditSystemEntry.getParentUid());
                 AuditSystemEntry data = auditSystemEntryRepository.findByUid(auditSystemEntry.getParentUid());
                if (data!=null){
                    if(!"1".equals(data.getParentUid())){
                        systemEntryUids.add(data.getParentUid());
                    }
                }
            }
            List<AuditConfigItem> auditConfigItemList = auditConfigItemRepository.entryAndContentList(systemEntryUids,orderBy);
            if(auditConfigItemList!=null && !auditConfigItemList.isEmpty()){
            for (AuditConfigItem auditConfigItem :auditConfigItemList){
                if (!auditConfigItems.contains(auditConfigItem)){
                    auditConfigItems.add(auditConfigItem);
                }
            }
        }
    }

        if(auditConfigItems==null){
            auditConfigItems =new ArrayList<>();
        }
        if (auditConfigItems.contains(auditConfigItem)){
            auditConfigItems.remove(auditConfigItem);
        }
        auditConfigItem.setUid("000");
        auditConfigItems.add(auditConfigItem);
    }
    public void editAction() {
        String operation= "AuditSystemCaseEdit.xhtml?faces-redirect=true&uid=" + selected.getUid() ;
        WebUtil.navigateTo(operation);
    }
    public String listAction(Integer status) {
        if(selected.getUid()!=null){
            selected.setStatus(status);
            dao.save(selected);
        }
        String operation= "AuditSystemCaseList.xhtml?actionName=Create";
       return UrlEncryptController.encodeUrlParam(operation);
    }
    public AuditSystemCase onSelectAuditSystemCase() {
        return selected;
    }
    public String getStatusName(int status){
        switch (status){
            case 1 : return "草稿";
            case 2 : return "发布";
            default: return "过期";
        }
    }

    public void deleteAction() {
        if(selected.getUid()==null){
            WebUtil.addErrorMessage("请选择数据!");
            return ;
        }
        dao.delete(selected);
    }
    public void onContent() {
        entryType = null;
        configItemUid = null;
        templateUid=null;
    }
    public void onAuditTarget() {
        targetScore = null;
        targetType = null;
    }
    public void strip() {
        currentTabId = STRIP;
    }

    public void payment() {
        currentTabId = PAYMENT;
    }

    public void target() {
        currentTabId = TARGET;
    }
    public boolean getStrip() {
        return STRIP.equals(currentTabId);
    }

    public boolean getPayment() {
        return PAYMENT.equals(currentTabId);
    }

    public boolean getTarget() {
        return TARGET.equals(currentTabId);
    }
    public void filterNode() {
        String uid=null;
        if (selected!=null){
            uid= selected.getUid();
        }
        orgTree = getOrgTreeNodes(auditSystemEntryRepository.findListByName(entryMarkName,uid),"1");
    }
    public void saveSelectedOrg() {
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        selected.setStatus(1);
        selected.setCreateTime(new Date());
        selected.setCreator(userAccount.getName());
        dao.save(selected);
        buildOrdTree();
    }

    public Integer getEntryType() {
        return entryType;
    }

    public void setEntryType(Integer entryType) {
        this.entryType = entryType;
    }

    public Integer getTargetType() {
        return targetType;
    }

    public void setTargetType(Integer targetType) {
        this.targetType = targetType;
    }

    public Double getTargetScore() {
        return targetScore;
    }

    public void setTargetScore(Double targetScore) {
        this.targetScore = targetScore;
    }

    public Double getScore() {
        return targetScore;
    }

    public void setScore(Double targetScore) {
        this.targetScore = targetScore;
    }

    public String getEntryMarkName() {
        return entryMarkName;
    }

    public void setEntryMarkName(String entryMarkName) {
        this.entryMarkName = entryMarkName;
    }

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    public String getEntryMark() {
        return entryMark;
    }

    public void setEntryMark(String entryMark) {
        this.entryMark = entryMark;
    }

    public TreeNode getOrgTree() {
        return orgTree;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public String getRetrieval() {
        return retrieval;
    }

    public void setRetrieval(String retrieval) {
        this.retrieval = retrieval;
    }

    public boolean isEntryFlag() {
        return entryFlag;
    }

    public Integer getEntryOrderBy() {
        return entryOrderBy;
    }

    public void setEntryOrderBy(Integer entryOrderBy) {
        this.entryOrderBy = entryOrderBy;
    }

    public String getConfigItemUid() {
        return configItemUid;
    }

    public void setConfigItemUid(String configItemUid) {
        this.configItemUid = configItemUid;
    }

    public String getTemplateUid() {
        return templateUid;
    }

    public void setTemplateUid(String templateUid) {
        this.templateUid = templateUid;
    }

    public Double getPaymentScoreStandard() {
        return paymentScoreStandard;
    }

    public void setPaymentScoreStandard(Double paymentScoreStandard) {
        this.paymentScoreStandard = paymentScoreStandard;
    }

    public Double getTargetScoreStandard() {
        return targetScoreStandard;
    }

    public void setTargetScoreStandard(Double targetScoreStandard) {
        this.targetScoreStandard = targetScoreStandard;
    }


    public void setNothing(){}

    public List<AuditConfigItem> getAuditConfigItems() {
        return auditConfigItems;
    }

    public void setAuditConfigItems(List<AuditConfigItem> auditConfigItems) {
        this.auditConfigItems = auditConfigItems;
    }
}
