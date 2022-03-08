package com.ge.apm.view.audit;

import com.ge.apm.dao.AuditConfigItemRepository;
import com.ge.apm.dao.AuditEntryItemRelRepository;
import com.ge.apm.dao.AuditSystemCaseRepository;
import com.ge.apm.dao.AuditSystemEntryRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.uaa.UaaService;
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
    private UaaService uaaService;

    private String retrieval;
    private String entryName;
    private String entryMark;
    private Integer entryType;
    private String entryMarkName;
    private String caseUid;
    private String entryUid;
    private String configItemUid;
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
    private final AuditConfigItem auditConfigItem = new AuditConfigItem();
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
        uaaService = WebUtil.getBean(UaaService.class);
        if(selected==null){
            selected=new AuditSystemCase();
        }
        String uid = WebUtil.getRequestParameter("uid");
        if (uid!=null){
            selected = dao.findByUid(uid);
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
        AuditSystemEntry auditSystemEntry = new AuditSystemEntry();
        auditSystemEntry.setName(entryName);
        auditSystemEntry.setMark(entryMark);
        auditSystemEntry.setCaseUid(caseUid);
        if (caseLevel!=0){
            ++caseLevel;
            if(caseLevel>3){
                WebUtil.addErrorMessage("错误: 指标没有子节点,请重新配置");
                return;
            }
            auditSystemEntry.setLevel(caseLevel);
            if (entryType==null){
               entryType = caseLevel;
            }
            auditSystemEntry.setType(entryType);
            auditSystemEntry.setOrderBy(caseLevel);
        }else {
            auditSystemEntry.setLevel(1);
            auditSystemEntry.setOrderBy(1);
            auditSystemEntry.setType(1);
            entryUid="1";
        }
        auditSystemEntry.setParentUid(entryUid);
        auditSystemEntryRepository.save(auditSystemEntry);
        buildOrdTree();
    }
    public void saveAuditConfigItem() {
        AuditConfigItem auditConfigItem = auditConfigItemRepository.findByUid(configItemUid);
        if (caseLevel==1){
            auditConfigItemStripList.add(auditConfigItem);
        }else if(caseLevel==2){
            auditConfigItemPaymentList.add(auditConfigItem);
        }else {
            auditConfigItemTargetList.add(auditConfigItem);
        }
        AuditEntryItemRel auditEntryItemRel = new AuditEntryItemRel();
        auditEntryItemRel.setItemUid(auditConfigItem.getUid());
        auditEntryItemRel.setSystemEntryUid(auditSystemEntry.getUid());
        auditEntryItemRel.setOrderBy(auditConfigItem.getContentType());
        auditEntryItemRelRepository.save(auditEntryItemRel);
    }
    public void delNode(AuditConfigItem auditConfigItem) {
         auditConfigItemRepository.delete(auditConfigItem);
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
        TreeNode node = event.getTreeNode();
        auditSystemEntry = (AuditSystemEntry) node.getData();
        entryUid = auditSystemEntry.getUid();
        caseLevel = auditSystemEntry.getLevel();
        entryOrderBy = auditSystemEntry.getOrderBy();
        if (caseLevel==1){
            currentTabId = STRIP;
        }else if(caseLevel==2){
            currentTabId = PAYMENT;
        }else {
            currentTabId = TARGET;
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
    public List<AuditConfigItem> auditConfigItemList(){
        Integer contentTyp =1;
        if (entryType!=null){
            contentTyp =entryType;
        }
        auditConfigItems = auditConfigItemRepository.findByContentType(contentTyp);
        return auditConfigItems;
    }
    public List<AuditConfigItem> getStripList() {
        auditConfigItems(auditConfigItemStripList);
       return auditConfigItemStripList;
    }
    public List<AuditConfigItem> getPaymentList() {
        auditConfigItems(auditConfigItemPaymentList);
       return auditConfigItemPaymentList;
    }
    public List<AuditConfigItem> getTargetList() {
        auditConfigItems(auditConfigItemTargetList);
       return auditConfigItemTargetList;
    }
    private void auditConfigItems(List<AuditConfigItem> auditConfigItems){
        if (auditSystemEntry!=null){
        List<AuditConfigItem> auditConfigItemList = auditConfigItemRepository.entryAndContentList(auditSystemEntry.getUid());
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
        if(selected.getUid()==null){
            WebUtil.addErrorMessage("请选择数据!");
            return ;
        }
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
    public String getStatusName(int status){
        switch (status){
            case 2 : return "发布";
            default: return "草稿";
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

    public void setNothing(){}

    public List<AuditConfigItem> getAuditConfigItems() {
        return auditConfigItems;
    }

    public void setAuditConfigItems(List<AuditConfigItem> auditConfigItems) {
        this.auditConfigItems = auditConfigItems;
    }
}
