package com.ge.apm.view.audit;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.audit.AuditItemConfigService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 503189829
 * @create 2022/2/24
 * @since 1.0.0
 */
@ManagedBean
@ViewScoped
public class AuditSystemCaseEditController extends GenericCRUDUUIDController<AuditSystemCase> {

    private AuditSystemCaseRepository dao;
    private AuditCaseRepository auditCaseRepository;
    private AuditSystemEntryRepository auditSystemEntryRepository;
    private AuditConfigItemRepository auditConfigItemRepository;
    private AuditEntryItemRelRepository auditEntryItemRelRepository;
    private AuditTargetRepository auditTargetRepository;
    private AuditTemplateRepository auditTemplateRepository;
    private AuditEntryTemplateRelRepository auditEntryTemplateRelRepository;
    private AuditItemConfigService auditItemConfigService;
    private AuditEventRepository auditEventRepository;

    private String retrieval;
    private String entryName;
    private String entryMark;
    private String sourceText;
    private Double entryScoreStandard;
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
    private boolean entryScoreFlag = true;
    private boolean orderByFlag = true;
    private int caseLevel=0;
    private Integer entryOrderBy =0;
    private TreeNode orgTree;
    private TreeNode selectedNode;
    private AuditSystemEntry auditSystemEntry;
    private List<AuditConfigItem> auditConfigItemStripList =new ArrayList<>();
    private List<AuditConfigItem> auditConfigItemPaymentList=new ArrayList<>();
    private List<AuditConfigItem> auditConfigItemTargetList=new ArrayList<>();
    private List<AuditConfigItem> auditConfigItems=new ArrayList<>();
    private List<AuditTemplate> auditTemplates=new ArrayList<>();
    private  AuditConfigItem auditConfigItemSelected = new AuditConfigItem();
    private AuditTemplate auditTemplateSelected = new AuditTemplate();
    private  AuditSystemEntry auditSystemEntrySelected = new AuditSystemEntry();
    private  AuditTarget auditTargetSelected = new AuditTarget();
    private  AuditConfigItem auditConfigItem = new AuditConfigItem();
    private List<V2_BlobObject> attachements;
    private BlobFileService blobService;
    private final AuditTemplate auditTemplate = new AuditTemplate();
    public static final String STRIP = "STRIP";//条
    public static final String PAYMENT = "PAYMENT";//款
    public static final String TARGET = "TARGET";//指标
    protected String currentTabId = null;
    private String selectType = "1";
    private String entryContent = null;
    private boolean contentTypeFlag = false;
    @Override
    protected void init() {
        dao = WebUtil.getBean(AuditSystemCaseRepository.class);
        auditCaseRepository = WebUtil.getBean(AuditCaseRepository.class);
        auditSystemEntryRepository = WebUtil.getBean(AuditSystemEntryRepository.class);
        auditConfigItemRepository = WebUtil.getBean(AuditConfigItemRepository.class);
        auditEntryItemRelRepository = WebUtil.getBean(AuditEntryItemRelRepository.class);
        auditTargetRepository = WebUtil.getBean(AuditTargetRepository.class);
        auditTemplateRepository = WebUtil.getBean(AuditTemplateRepository.class);
        auditEntryTemplateRelRepository = WebUtil.getBean(AuditEntryTemplateRelRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        auditItemConfigService = WebUtil.getBean(AuditItemConfigService.class);
        auditEventRepository = WebUtil.getBean(AuditEventRepository.class);

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
        buildEntryTreeNodes(auditSystemEntryList);
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
    public TreeNode buildEntryTreeNodes(List<AuditSystemEntry> auditSystemEntryList) {
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
    public TreeNode getEntryTreeNodes(List<AuditSystemEntry> auditSystemEntryList) {
        orgTree = new DefaultTreeNode("root", null, null);
        Map<String, TreeNode> treeMap = new LinkedHashMap<>();
        Map<String, TreeNode> parentTreeMap = new LinkedHashMap<>();
        for (AuditSystemEntry auditSystemEntry : auditSystemEntryList) {
            TreeNode node = new DefaultTreeNode("org", auditSystemEntry, null);
            node.setRowKey("org_" + auditSystemEntry.getUid());
            editData(auditSystemEntry,node,treeMap,parentTreeMap);
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
    private void editData(AuditSystemEntry auditSystemEntry, TreeNode node ,Map<String, TreeNode> treeMap, Map<String, TreeNode> parentTreeMap ){
        treeMap.put(auditSystemEntry.getUid(), node);
        node.setExpanded(true);
        if(auditSystemEntry.getLevel()==1){
            parentTreeMap.put(auditSystemEntry.getUid(),node);
        }else {
            AuditSystemEntry entry = auditSystemEntryRepository.findByUid(auditSystemEntry.getParentUid());
            if(entry!=null) {
                TreeNode treeNode = new DefaultTreeNode("org", entry, null);
                editData(entry,treeNode,treeMap,parentTreeMap);
            }
        }
    }

    public void saveAuditSystemEntry() {
        AuditSystemEntry date = new AuditSystemEntry();
        String eventType = "ADD_SYSTEM_ENTRY";
        Integer type=0;
        if(auditSystemEntrySelected!=null&&auditSystemEntrySelected.getUid()!=null){
            date=auditSystemEntrySelected;
            eventType="EDIT_SYSTEM_ENTRY";
        }else {
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

                if (entryType==null){
                    entryType = caseLevel;
                }else if(entryType==3){
                    caseLevel=3;
                }
                date.setLevel(caseLevel);
                date.setType(entryType);
                date.setOrderBy(entryType);
            }else {
                date.setLevel(1);
                date.setOrderBy(1);
                date.setType(1);
                entryUid="1";
            }
            date.setParentUid(entryUid);
            date.setCaseUid(caseUid);
            date.setScoreInit(0D);
        }
        date.setName(entryName);
        date.setMark(entryMark);
        date.setScoreStandard(entryScoreStandard);
        date.setSourceText(sourceText);

        try {
            auditSystemEntryRepository.save(date);
            buildOrdTree();
            caseLevel=0;
            entryType=null;
            entryScoreStandard=null;
            sourceText=null;
            createEventSysytemEntry(eventType, date, "success");
            WebUtil.addSuccessMessage(WebUtil.getMessage("saveSuccess"));
        } catch (Exception e){
            createEventSysytemEntry(eventType, date, "fail");
            WebUtil.addErrorMessage(WebUtil.getMessage("save") + WebUtil.getMessage("FAILED"));
        }
    }
    public void saveAuditConfigItem(AuditConfigItem data) {
        if(data==null){
            data = auditConfigItemRepository.findByUid(configItemUid);
        }
        AuditEntryItemRel auditEntryItemRel = new AuditEntryItemRel();
        auditEntryItemRel.setItemUid(data.getUid());
        auditEntryItemRel.setSystemEntryUid(auditSystemEntry.getUid());
        Integer orderBy;
        if ( currentTabId == STRIP){
            orderBy=1;
        }else if(currentTabId == PAYMENT){
            orderBy=2;
            auditSystemEntry.setScoreStandard(paymentScoreStandard);
        }else {
            orderBy=3;
            auditSystemEntry.setScoreStandard(targetScoreStandard);
        }
        auditEntryItemRel.setOrderBy(orderBy);
        if(auditConfigItemSelected!=null){
            List<AuditEntryItemRel> list = auditEntryItemRelRepository.findByItemUidAndSystemEntryUidAndOrderBy(auditConfigItemSelected.getUid(), auditSystemEntry.getUid(), orderBy);
            if (list!=null && !list.isEmpty()){
                auditEntryItemRelRepository.delete(list);
            }
        }

        auditSystemEntryRepository.save(auditSystemEntry);
        auditEntryItemRelRepository.save(auditEntryItemRel);
        auditConfigItemSelected=new AuditConfigItem();
    }
    public void saveAuditTemplate() {
        AuditTemplate auditTemplate = auditTemplateRepository.findByUid(templateUid);
        AuditEntryTemplateRel auditEntryTemplateRel = new AuditEntryTemplateRel();
        auditEntryTemplateRel.setEntryUid(auditSystemEntry.getUid());
        auditEntryTemplateRel.setTemplateUid(auditTemplate.getUid());
        auditEntryTemplateRel.setOrderBy(auditSystemEntry.getOrderBy());
        auditEntryTemplateRelRepository.save(auditEntryTemplateRel);
        if(auditTemplateSelected!=null){
            List<AuditEntryTemplateRel> list = auditEntryTemplateRelRepository.findByTemplateUidAndEntryUidAndOrderBy(auditTemplateSelected.getUid(), auditSystemEntry.getUid(), auditSystemEntry.getOrderBy());
            if (list!=null && !list.isEmpty()){
                auditEntryTemplateRelRepository.delete(list);
            }
        }
        auditTemplateSelected=new AuditTemplate();
    }
    public void saveAuditTarget() {
        auditTargetSelected.setSystemCaseUid(caseUid);
        auditTargetRepository.save(auditTargetSelected);
        auditTargetSelected=new AuditTarget();
    }

    public void deleteAuditTarger(AuditTarget auditTarget){
        auditTargetRepository.delete(auditTarget);
    }
    public void editAuditTarger(AuditTarget auditTarget){
        if(auditTarget!=null){
            auditTargetSelected=auditTarget ;
        }
    }
    public void editAuditTemplate(AuditTemplate auditTemplate){
        if(auditTemplate!=null){
            auditTemplateSelected=auditTemplate ;
        }
    }
    public void editAuditConfigItem(AuditConfigItem auditConfigItem){
        contentTypeFlag =false;
        if(auditConfigItem!=null){
            auditConfigItemSelected=auditConfigItem ;
        }
    }
    public void editAuditSystementry(){
        if(auditSystemEntry!=null&&auditSystemEntry.getUid()!=null){
            entryType=auditSystemEntry.getType();
            entryMark=auditSystemEntry.getMark();
            entryName=auditSystemEntry.getName();
            entryScoreStandard=auditSystemEntry.getScoreStandard();
            sourceText=auditSystemEntry.getSourceText();
            auditSystemEntrySelected=auditSystemEntry;
            entryScoreFlag=true;
            if(auditSystemEntry.getLevel()==1){
                entryScoreFlag=false;
            }
        }
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
        orderByFlag=true;
        if (caseLevel==1){
            currentTabId = STRIP;
        }else if(caseLevel==2){
            currentTabId = PAYMENT;
            paymentScoreStandard=auditSystemEntry.getScoreStandard();
        }else {
            AuditSystemEntry entry = auditSystemEntryRepository.findByUid(auditSystemEntry.getParentUid());
            if(entry!=null){
                if(entry.getOrderBy()==1){
                    orderByFlag=false;
                }
            }
            currentTabId = TARGET;
            targetScoreStandard=auditSystemEntry.getScoreStandard();
        }
        entryScoreStandard=null;
        sourceText=null;
    }
    public void preDelete() {
        onSelectFlag=true;
         if (auditSystemEntry==null){
             WebUtil.addErrorMessage("请选择数据!");
             return;
         }
        try {
            auditSystemEntryRepository.delete(auditSystemEntry);
            deleteAuditSystemEntryList(auditSystemEntry.getUid());
            buildOrdTree();
            createEventSysytemEntry("DELETE_SYSTEM_ENTRY", auditSystemEntry, "success");
            WebUtil.addSuccessMessage(WebUtil.getMessage("deleteSuccess"));
        } catch (Exception e){
            createEventSysytemEntry("DELETE_SYSTEM_ENTRY", auditSystemEntry, "fail");
            WebUtil.addErrorMessage(WebUtil.getMessage("delete") + WebUtil.getMessage("FAILED"));
        }
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
            entryScoreFlag=true;
        }else{
            entryFlag =false;
            entryScoreFlag=true;
        }
        auditSystemEntrySelected=new AuditSystemEntry();
        entryScoreStandard=null;
        sourceText=null;
    }
    public List<AuditTemplate> auditTemplates(){
        return auditTemplateRepository.findByType(1);
    }
    public List<AuditConfigItem> auditConfigItemList(){
        Integer contentTyp =1;
        if (entryType!=null){
            contentTyp =entryType;
        }
        if(selected.getType()!=null && selected.getType()==2){
            contentTyp=3;//质控只有 解读
        }
        auditConfigItems = auditConfigItemRepository.findByContentType(contentTyp);
        return auditConfigItems;
    }
    public List<AuditTarget> getTargetTypeList() {
        AuditTarget auditTarget = new AuditTarget();
        List<AuditTarget> auditTargets = auditTargetRepository.findBySystemCaseUid(caseUid);
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
        AuditConfigItem auditConfigItem = new AuditConfigItem();
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
            String eventType = "EDIT_SYSTEM_CASE";
            if (status==2){
                eventType = "ENABLE_SYSTEM_CASE";
            }else if(status==1) {
                List<AuditCase> list = auditCaseRepository.findBySystemCaseUid(selected.getUid());
                if (list!=null && !list.isEmpty()){
                    WebUtil.addErrorMessage("数据被引用中,不能存为草稿!");
                    return null;
                }
            }
            try {
                selected.setStatus(status);
                dao.save(selected);
                createEventSysytemCase(eventType, selected, "success");
                WebUtil.addSuccessMessage(WebUtil.getMessage("saveSuccess"));
            } catch (Exception e){
                createEventSysytemCase(eventType, selected, "fail");
                WebUtil.addErrorMessage(WebUtil.getMessage("save") + WebUtil.getMessage("FAILED"));
            }

        }else{
            WebUtil.addErrorMessage("请先保存数据!");
            return null;
        }
        String operation= "AuditSystemCaseList.xhtml?actionName=Create";
       return UrlEncryptController.encodeUrlParam(operation);
    }

    public void deleteAction() {
        if(selected.getUid()==null){
            WebUtil.addErrorMessage("请选择数据!");
            return ;
        }
        dao.delete(selected);
    }
    public void onContent() {
        contentTypeFlag=false;
        auditConfigItemSelected=new AuditConfigItem();
        auditTemplateSelected=new AuditTemplate();
        entryType = null;
        configItemUid = null;
        templateUid=null;
    }
    public void onAuditTarget() {
        targetScore = null;
        targetType = null;
    }
    public void changeContentType(){
         contentTypeFlag =true;
    }
    public void changeType(){
        selectType = "1".equals(selectType) ? "2" : "1";
    }
    public List<V2_BlobObject> getAttachList(String itemUid, String type) {
        if (attachements == null) {
            attachements = blobService.findBlobObjectsByBoUid(itemUid);
        }
        List<V2_BlobObject> collect = attachements.stream().filter(item -> item.getBoSubType() != null && item.getBoSubType().equals(type)).collect(Collectors.toList());
        return collect;
    }
    public void handleFileUpload(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            attachements.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_TYPE_AUDIT_CONFIG_ITEM);
            blobObject.setBoSubType(V2_BlobObject.BO_SUB_TYPE_IMG);
            blobObject.setBoUuid(selected.getUid());
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }
    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        attachements = attachements.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toList());
    }
    public void saveNewAuditConfigItem(){

        if(1 ==auditConfigItem.getType() && StringUtils.isEmpty(auditConfigItem.getContent())){
            WebUtil.addErrorMessage("请填写内容！");
            return ;
        }
        if(2 ==auditConfigItem.getType() && CollectionUtils.isEmpty(this.attachements)){
            WebUtil.addErrorMessage("请上传图片！");
            return ;
        }
        if(selected.getType()!=null && selected.getType()==2){
            auditConfigItem.setContentType(3);
        }
        // 同类型下，mark唯一
        List<SearchFilter> searchFilterList = new ArrayList<>();
        searchFilterList.add(new SearchFilter("mark", SearchFilter.Operator.EQ, auditConfigItem.getMark()));
        searchFilterList.add(new SearchFilter("contentType", SearchFilter.Operator.EQ, auditConfigItem.getContentType()));

        if(StringUtils.isNotEmpty(auditConfigItem.getUid())){
            searchFilterList.add(new SearchFilter("uid", SearchFilter.Operator.NE, auditConfigItem.getUid()));
        }

        List<AuditConfigItem> auditConfigItemList = auditConfigItemRepository.findBySearchFilter(searchFilterList);

        if(CollectionUtils.isNotEmpty(auditConfigItemList)){
            WebUtil.addErrorMessage("同类型下，编号重复，无法提交！");
            return ;
        }
        String eventType = "EDIT_CONFIG_ITEM";
        if(StringUtils.isBlank(auditConfigItem.getUid())){
            auditConfigItem.setCreator(UserContextService.getCurrentUserAccount().getName());
            auditConfigItem.setCreateTime(new Date());
            eventType = "ADD_CONFIG_ITEM";
        }

        try {
            auditItemConfigService.saveAuditConfigItem(auditConfigItem, attachements);
            saveAuditConfigItem(auditConfigItem);
            createEvent(eventType, auditConfigItem, "success");
            auditConfigItem=null;
            WebUtil.addSuccessMessage(WebUtil.getMessage("SaveSuccess"));
        } catch (Exception e){
            createEvent(eventType, auditConfigItem, "fail");
            WebUtil.addErrorMessage(WebUtil.getMessage("Save") + WebUtil.getMessage("FAILED"));
        }
    }
    public boolean typeChecking(){
        if (selected.getType()==2){
            List<AuditSystemCase> data = dao.findByType(selected.getType());
            if(data!=null && !data.isEmpty()){
                WebUtil.addErrorMessage("已存在质控配置不能重复添加!");
                return true;
            }
        }
        return false;

    }
    private void createEvent(String eventType, AuditConfigItem item, String result){
        AuditEvent event = new AuditEvent();
        event.setEventType(eventType);
        event.setOperator(UserContextService.getCurrentUserAccount().getName());
        event.setContent(JSON.toJSONString(item));
        event.setEventObjectUid(item.getUid());
        event.setResult(result);
        event.setEventTime(new Date());
        auditEventRepository.save(event);
    }
    private void createEventSysytemCase(String eventType, AuditSystemCase auditSystemCase, String result){
        AuditEvent event = new AuditEvent();
        event.setEventType(eventType);
        event.setOperator(UserContextService.getCurrentUserAccount().getName());
        event.setContent(JSON.toJSONString(auditSystemCase));
        event.setEventObjectUid(auditSystemCase.getUid());
        event.setResult(result);
        event.setEventTime(new Date());
        auditEventRepository.save(event);
    }
    private void createEventSysytemEntry(String eventType, AuditSystemEntry data, String result){
        AuditEvent event = new AuditEvent();
        event.setEventType(eventType);
        event.setOperator(UserContextService.getCurrentUserAccount().getName());
        event.setContent(JSON.toJSONString(data));
        event.setEventObjectUid(data.getUid());
        event.setResult(result);
        event.setEventTime(new Date());
        auditEventRepository.save(event);
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
        if(entryMarkName==null || "".equals(entryMarkName)){
            this.buildOrdTree();
        }else {
            orgTree = getEntryTreeNodes(auditSystemEntryRepository.findListByName(entryMarkName,uid));
        }

    }
    public void saveSelectedOrg() {
        if( this.typeChecking()){
            return;
        }
        String eventType = "ADD_SYSTEM_CASE";
        if (selected.getUid()!=null){
            eventType = "EDIT_SYSTEM_CASE";
        }
        try {
            UserAccount userAccount = UserContextService.getCurrentUserAccount();
            selected.setStatus(1);
            selected.setCreateTime(new Date());
            selected.setCreator(userAccount.getName());
            dao.save(selected);
            buildOrdTree();
            createEventSysytemCase(eventType, selected, "success");
            WebUtil.addSuccessMessage(WebUtil.getMessage("saveSuccess"));
        } catch (Exception e){
            createEventSysytemCase(eventType, selected, "fail");
            WebUtil.addErrorMessage(WebUtil.getMessage("save") + WebUtil.getMessage("FAILED"));
        }

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

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public Double getEntryScoreStandard() {
        return entryScoreStandard;
    }

    public void setEntryScoreStandard(Double entryScoreStandard) {
        this.entryScoreStandard = entryScoreStandard;
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

    public boolean getContentTypeFlag() {
        return contentTypeFlag;
    }

    public void setContentTypeFlag(boolean contentTypeFlag) {
        this.contentTypeFlag = contentTypeFlag;
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


    public void setNothing(){
    }
    public void setContentNothing(){
        AuditConfigItem auditConfigItem = auditConfigItemRepository.findByUid(configItemUid);
        if (auditConfigItem!=null){
            auditConfigItemSelected=auditConfigItem;
        }
    }

    public String getEntryContent() {
        return entryContent;
    }

    public void setEntryContent(String entryContent) {
        this.entryContent = entryContent;
    }

    public AuditConfigItem getAuditConfigItem() {
        return auditConfigItem;
    }

    public void setAuditConfigItem(AuditConfigItem auditConfigItem) {
        this.auditConfigItem = auditConfigItem;
    }

    public List<AuditConfigItem> getAuditConfigItems() {
        return auditConfigItems;
    }

    public String getSelectType() {
        return selectType;
    }

    public void setSelectType(String selectType) {
        this.selectType = selectType;
    }
    public int getCaseLevel() {
        return caseLevel;
    }

    public void setCaseLevel(int caseLevel) {
        this.caseLevel = caseLevel;
    }

    public void setAuditConfigItems(List<AuditConfigItem> auditConfigItems) {
        this.auditConfigItems = auditConfigItems;
    }

    public AuditSystemEntry getAuditSystemEntry() {
        return auditSystemEntry;
    }

    public void setAuditSystemEntry(AuditSystemEntry auditSystemEntry) {
        this.auditSystemEntry = auditSystemEntry;
    }

    public AuditTarget getAuditTargetSelected() {
        return auditTargetSelected;
    }

    public AuditConfigItem getAuditConfigItemSelected() {
        return auditConfigItemSelected;
    }

    public void setAuditConfigItemSelected(AuditConfigItem auditConfigItemSelected) {
        this.auditConfigItemSelected = auditConfigItemSelected;
    }

    public AuditTemplate getAuditTemplateSelected() {
        return auditTemplateSelected;
    }

    public void setAuditTemplateSelected(AuditTemplate auditTemplateSelected) {
        this.auditTemplateSelected = auditTemplateSelected;
    }

    public boolean isEntryScoreFlag() {
        return entryScoreFlag;
    }

    public void setEntryScoreFlag(boolean entryScoreFlag) {
        this.entryScoreFlag = entryScoreFlag;
    }

    public boolean isOrderByFlag() {
        return orderByFlag;
    }

    public void setOrderByFlag(boolean orderByFlag) {
        this.orderByFlag = orderByFlag;
    }
}
