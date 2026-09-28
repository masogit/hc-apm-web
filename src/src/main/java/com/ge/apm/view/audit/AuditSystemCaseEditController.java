package com.ge.apm.view.audit;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.audit.AuditItemConfigService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.ReorderEvent;
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

    private Integer targetType;
    private Double targetScore;
    private String entryMarkName;  //检索
    private String caseUid;
    private String configItemUid;
    private String templateUid;
    private TreeNode orgTree;
    private TreeNode selectedNode;
    private AuditSystemEntry auditSystemEntry;  //选中的Entry
    private List<AuditConfigItem> auditConfigItemListByEntry=new ArrayList<>();  //选中的Entry 对应的itemList
    private List<AuditConfigItem> auditConfigItems;  //下拉选择
    private List<AuditTemplate> auditTemplateListByEntry =new ArrayList<>(); //选中的Entry 对应的templateList
    private  AuditConfigItem auditConfigItemSelected ;
    private AuditTemplate auditTemplateSelected = new AuditTemplate();
    private  AuditSystemEntry auditSystemEntrySelected;    // 被创建的新节点
    private  AuditTarget auditTargetSelected = new AuditTarget();
    private  AuditConfigItem auditConfigItem = new AuditConfigItem();
    private List<V2_BlobObject> attachements;
    private BlobFileService blobService;
    protected String currentTabId = null;
    private String selectType = "1";
    private boolean entryFlag = false;
    private  String editChoiceConfigItemUid;  //被编辑item Uid
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
                    if (Objects.equals(auditSystemEntry.getType(),1)){
                        stripNum++;
                    }else if(Objects.equals(auditSystemEntry.getType(),2)){
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

    private  void addNode(TreeNode pnode,AuditSystemEntry nodeData,  List<AuditSystemEntry> auditSystemEntryList){

        TreeNode node = new DefaultTreeNode("org", nodeData, pnode);
        node.setExpanded(true);
        node.setRowKey("org_" + nodeData.getUid());
        node.setSelected(null!= selectedNode && nodeData.getUid().equals(((AuditSystemEntry)selectedNode.getData()).getUid()));
        auditSystemEntryList.stream().filter(i-> Objects.equals(i.getParentUid(),nodeData.getUid()))
                .sorted(Comparator.comparing(AuditSystemEntry::getOrderBy))
                .forEach(i->addNode(node,i,auditSystemEntryList));
        pnode.getChildren().add(node);
    }

    public TreeNode buildEntryTreeNodes(List<AuditSystemEntry> auditSystemEntryList) {
        orgTree = new DefaultTreeNode("root", null, null);

        auditSystemEntryList.stream().filter(i-> Strings.isNullOrEmpty(i.getParentUid()) || "1".equals(i.getParentUid()))
                .sorted(Comparator.comparing(AuditSystemEntry::getOrderBy))
                .forEach(i-> addNode(orgTree,i,auditSystemEntryList));

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
        if(Objects.equals(auditSystemEntry.getLevel(),1)){
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

        AuditSystemEntry newEntry = Optional.ofNullable(auditSystemEntrySelected).orElse(auditSystemEntry);
        if(null == newEntry){
            WebUtil.addErrorMessage("错误: 无有效数据");
            return;
        }

        String eventType = "EDIT_SYSTEM_ENTRY" ;

        if( Strings.isNullOrEmpty(newEntry.getUid())){
            eventType = "ADD_SYSTEM_ENTRY";
            newEntry.setCreator(UserContextService.getCurrentUserAccount().getName());
            newEntry.setCreateTime(new Date());
            newEntry.setCaseUid(caseUid);
            newEntry.setScoreInit(0D);
        }


        try {
            auditSystemEntryRepository.save(newEntry);
            buildOrdTree();
            auditSystemEntrySelected = null;
            createEventSysytemEntry(eventType, newEntry, "success");
        } catch (Exception e){
            createEventSysytemEntry(eventType, newEntry, "fail");
            WebUtil.addErrorMessage(WebUtil.getMessage("save") + WebUtil.getMessage("FAILED"));
        }
    }
    public void saveAuditConfigItem(AuditConfigItem data) {
        if(data==null){ //页面保存
            if(Optional.ofNullable(auditConfigItemSelected)
                    .map(i-> Strings.isNullOrEmpty(i.getUid())).orElse(true)){
                WebUtil.addErrorMessage("请选择数据!");
                return;
            }
            data = auditConfigItemRepository.findByUid(auditConfigItemSelected.getUid());

            List<AuditEntryItemRel> list = auditEntryItemRelRepository.findByItemUidAndSystemEntryUid(data.getUid(), auditSystemEntry.getUid());
            if(!CollectionUtils.isEmpty(list)){
                WebUtil.addErrorMessage("重复添加!");
                return;
            }
        }
        AuditEntryItemRel auditEntryItemRel = new AuditEntryItemRel();
        auditEntryItemRel.setItemUid(data.getUid());
        auditEntryItemRel.setSystemEntryUid(auditSystemEntry.getUid());
        auditEntryItemRel.setOrderBy(Optional.ofNullable(auditConfigItemListByEntry).map(List::size).orElse(0));
        if(editChoiceConfigItemUid !=null){
            List<AuditEntryItemRel> list = auditEntryItemRelRepository.findByItemUidAndSystemEntryUid(editChoiceConfigItemUid, auditSystemEntry.getUid());
            if (list!=null && !list.isEmpty()){
                auditEntryItemRelRepository.delete(list);
            }
            editChoiceConfigItemUid = null;
        }
        auditEntryItemRelRepository.save(auditEntryItemRel);
        auditConfigItemSelected=null;
        loadAuditConfigItemListByEntry();
    }
    public void saveAuditTemplate() {
        AuditTemplate auditTemplate = auditTemplateRepository.findByUid(templateUid);
        AuditEntryTemplateRel auditEntryTemplateRel = new AuditEntryTemplateRel();
        auditEntryTemplateRel.setEntryUid(auditSystemEntry.getUid());
        auditEntryTemplateRel.setTemplateUid(auditTemplate.getUid());
        auditEntryTemplateRel.setOrderBy(auditTemplateListByEntry.size());
        auditEntryTemplateRelRepository.save(auditEntryTemplateRel);
        if(auditTemplateSelected!=null){
            List<AuditEntryTemplateRel> list = auditEntryTemplateRelRepository.findByTemplateUidAndEntryUidAndOrderBy(auditTemplateSelected.getUid(), auditSystemEntry.getUid(), auditSystemEntry.getOrderBy());
            if (list!=null && !list.isEmpty()){
                auditEntryTemplateRelRepository.delete(list);
            }
        }
        auditTemplateSelected=new AuditTemplate();
        loadAuditTemplateList();
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
    public void editAuditConfigItem(AuditConfigItem configItem){
        auditConfigItem = null;
        if(configItem!=null){
            auditConfigItemSelected=configItem ;
            editChoiceConfigItemUid = configItem.getUid();
        }
    }
    public void editAuditSystementry(){
        if(auditSystemEntry!=null&&auditSystemEntry.getUid()!=null){
            auditSystemEntrySelected=auditSystemEntry;
            entryFlag = false;
            if (Objects.equals(auditSystemEntry.getLevel(),1)){
                entryFlag=true;
            }
        }
    }
    public void deleteTemplate(AuditTemplate auditTemplate){
        auditEntryTemplateRelRepository.deleteByTemplateUidAndEntryUid(auditTemplate.getUid(),auditSystemEntry.getUid());
        loadAuditTemplateList();
    }

    public void deleteNode(AuditConfigItem auditConfigItem) {
        auditEntryItemRelRepository.deleteByItemUidAndSystemEntryUid(auditConfigItem.getUid(),auditSystemEntry.getUid());
        loadAuditConfigItemListByEntry();
    }

    private void loadAuditConfigItemListByEntry(){
        auditConfigItemListByEntry = auditConfigItemRepository.findConfigItemListByEntryUID(auditSystemEntry.getUid());
        AuditConfigItem emptyAuditConfigItem = new AuditConfigItem();
        emptyAuditConfigItem.setUid("000");
        auditConfigItemListByEntry.add(emptyAuditConfigItem);
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        auditSystemEntry = (AuditSystemEntry) node.getData();

        loadAuditConfigItemListByEntry();
        loadAuditTemplateList();

    }
    public void preDelete() {
         if (auditSystemEntry==null){
             WebUtil.addErrorMessage("请选择数据!");
             return;
         }
        try {
            auditSystemEntryRepository.delete(auditSystemEntry);
            deleteAuditSystemEntryList(auditSystemEntry.getUid());
            buildOrdTree();
            createEventSysytemEntry("DELETE_SYSTEM_ENTRY", auditSystemEntry, "success");
            auditSystemEntry = null;
            auditConfigItemListByEntry.clear();
        } catch (Exception e){
            createEventSysytemEntry("DELETE_SYSTEM_ENTRY", auditSystemEntry, "fail");
            WebUtil.addErrorMessage(WebUtil.getMessage("delete") + WebUtil.getMessage("FAILED"));
        }
    }

    public  void deleteAuditSystemEntryList(String parentUid){
        List<AuditSystemEntry> list = auditSystemEntryRepository.findByParentUid(parentUid);
        if (list!=null && !list.isEmpty()){
            for ( AuditSystemEntry entry:list ) {
                auditSystemEntryRepository.delete(entry);
                deleteAuditSystemEntryList(entry.getUid());
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
        auditSystemEntrySelected=new AuditSystemEntry();
        auditSystemEntrySelected.setLevel(Optional.ofNullable(auditSystemEntry).map(i-> i.getType()+1).orElse(1));
        auditSystemEntrySelected.setType(auditSystemEntrySelected.getLevel());
        auditSystemEntrySelected.setParentUid(Optional.ofNullable(auditSystemEntry).map(AuditSystemEntry::getUid).orElse("1"));
        auditSystemEntrySelected.setOrderBy(Optional.ofNullable(selectedNode).map(TreeNode::getChildren).map(n-> n.size()+1).orElse(1));
        entryFlag = false;
        if (Objects.equals(auditSystemEntrySelected.getLevel(),2)){
            entryFlag=true;
        }
    }

    public void preCreateStrip()  {
        if (selected!=null){
            caseUid = selected.getUid();
        }
        auditSystemEntrySelected=new AuditSystemEntry();
        auditSystemEntrySelected.setLevel(1);
        auditSystemEntrySelected.setType(1);
        auditSystemEntrySelected.setParentUid("1");
        auditSystemEntrySelected.setOrderBy(Optional.ofNullable(orgTree).map(TreeNode::getChildren).map(n->n.size()+1)
                .orElse(1));
    }

    public List<AuditTemplate> allAuditTemplates(){
        return auditTemplateRepository.findByType(1);
    }

    public void onContentTypeChange(){
        if(Optional.ofNullable(auditConfigItemSelected).map(i-> null == i.getContentType()).orElse(true)){
            auditConfigItems = null;
        }else{
            auditConfigItems = auditConfigItemRepository.findByContentType(auditConfigItemSelected.getContentType());
        }
    }

    public void onEntryTypeChecking(){
        if (auditSystemEntrySelected!=null){
            auditSystemEntrySelected.setType(auditSystemEntrySelected.getLevel());
        }
    }
    public List<AuditConfigItem> getConfigItemList(){
        return auditConfigItemRepository.findByContentType(1);
    }
    public List<AuditTarget> getTargetTypeList() {
        AuditTarget auditTarget = new AuditTarget();
        List<AuditTarget> auditTargets = new ArrayList<>();
        if(caseUid!=null){
            auditTargets = auditTargetRepository.findBySystemCaseUid(caseUid);
            if (auditTargets.contains(auditTarget)){
                auditTargets.remove(auditTarget);
            }
        }
        auditTarget.setUid("000");
        auditTargets.add(auditTarget);
        return auditTargets;
    }

    public List<AuditTemplate> loadAuditTemplateList() {
        auditTemplateListByEntry = auditTemplateRepository.findAuditTemplateListByEntryUID(auditSystemEntry.getUid());
        AuditTemplate template = new AuditTemplate();
        template.setUid("000");
        auditTemplateListByEntry.add(template);
        return auditTemplateListByEntry;
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
    public void onContent(String type) {

        switch (type){
            case "entryItem":
                auditConfigItemSelected=new AuditConfigItem();
                if(Objects.equals(selected.getType(), 2)){ //质控
                    auditConfigItemSelected.setContentType(3);
                    this.onContentTypeChange();
                }
                auditConfigItem = null;
                editChoiceConfigItemUid = null;
                break;
            case "entryTemplate":
                auditTemplateSelected=new AuditTemplate();
                break;
        }
    }
    public void onAuditTarget() {
        targetScore = null;
        targetType = null;
    }

    public void createNewItem(){
        auditConfigItem = new AuditConfigItem();
    }

    public void onTemplateRowReorder(ReorderEvent event) {
        List<String> idOrderList = auditTemplateListByEntry.stream().filter(i -> !Strings.isNullOrEmpty(i.getUid()))
                .map(AuditTemplate::getUid).collect(Collectors.toList());
        List<AuditEntryTemplateRel> itemRelList = auditEntryTemplateRelRepository.findByEntryUid(auditSystemEntry.getUid());
        List<AuditEntryTemplateRel> changedItemRelList = itemRelList.stream().filter(i -> idOrderList.contains(i.getTemplateUid()))
                .filter(i -> Optional.ofNullable(i.getOrderBy()).map(o-> o!=idOrderList.indexOf(i.getTemplateUid())).orElse(true))
                .peek(i -> i.setOrderBy(idOrderList.indexOf(i.getTemplateUid()))).collect(Collectors.toList());
        auditEntryTemplateRelRepository.save(changedItemRelList);
        loadAuditTemplateList();
    }

    public void onItemRowReorder(ReorderEvent event) {
        List<String> idOrderList = auditConfigItemListByEntry.stream().filter(i -> !Strings.isNullOrEmpty(i.getUid()))
                .map(AuditConfigItem::getUid).collect(Collectors.toList());
        List<AuditEntryItemRel> ItemRelList = auditEntryItemRelRepository.findBySystemEntryUid(auditSystemEntry.getUid());
        List<AuditEntryItemRel> changedItemRelList = ItemRelList.stream().filter(i -> idOrderList.contains(i.getItemUid()))
                .filter(i -> Optional.ofNullable(i.getOrderBy()).map(o-> o!=idOrderList.indexOf(i.getItemUid())).orElse(true))
                .peek(i -> i.setOrderBy(idOrderList.indexOf(i.getItemUid()))).collect(Collectors.toList());
        auditEntryItemRelRepository.save(changedItemRelList);
        loadAuditConfigItemListByEntry();
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

        if(Objects.equals(auditConfigItem.getType(), 1) && StringUtils.isEmpty(auditConfigItem.getContent())){
            WebUtil.addErrorMessage("请填写内容！");
            return ;
        }
        if(Objects.equals(auditConfigItem.getType(), 2) && CollectionUtils.isEmpty(this.attachements)){
            WebUtil.addErrorMessage("请上传图片！");
            return ;
        }
        if(selected.getType()!=null && Objects.equals(selected.getType(), 2)){
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
        } catch (Exception e){
            createEvent(eventType, auditConfigItem, "fail");
            WebUtil.addErrorMessage(WebUtil.getMessage("Save") + WebUtil.getMessage("FAILED"));
        }
    }
    public boolean typeChecking(){
        if (Objects.equals(selected.getType(),2)){
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

    public  void nodeMoveDownAction(){
        AuditSystemEntry selectedEntry = (AuditSystemEntry)selectedNode.getData();
        List<TreeNode> bros = selectedNode.getParent().getChildren();
        if(bros.indexOf(selectedNode)==bros.size()-1){
            return;
        }
        AuditSystemEntry downNode = (AuditSystemEntry)bros.get(bros.indexOf(selectedNode) + 1).getData();
        selectedEntry.setOrderBy(bros.indexOf(selectedNode)+1);
        downNode.setOrderBy(selectedEntry.getOrderBy()-1);
        auditSystemEntryRepository.save(Arrays.asList(selectedEntry, downNode));
        buildOrdTree();
    }
    public  void nodeMoveUpAction(){
        AuditSystemEntry selectedEntry = (AuditSystemEntry)selectedNode.getData();
        List<TreeNode> bros = selectedNode.getParent().getChildren();
        if(bros.indexOf(selectedNode)==0){
            return;
        }
        AuditSystemEntry upNode = (AuditSystemEntry)bros.get(bros.indexOf(selectedNode) - 1).getData();
        selectedEntry.setOrderBy(bros.indexOf(selectedNode));
        upNode.setOrderBy(selectedEntry.getOrderBy()+1);
        auditSystemEntryRepository.save(Arrays.asList(selectedEntry, upNode));
        buildOrdTree();
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
            UserAccount userAccount = UserContextService.getCurrentUserAccount();
            selected.setCreateTime(new Date());
            selected.setCreator(userAccount.getName());
        }
        try {
            selected.setStatus(1);
            dao.save(selected);
            caseUid=selected.getUid();
            buildOrdTree();
            createEventSysytemCase(eventType, selected, "success");
        } catch (Exception e){
            createEventSysytemCase(eventType, selected, "fail");
            WebUtil.addErrorMessage(WebUtil.getMessage("save") + WebUtil.getMessage("FAILED"));
        }

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

    public List<AuditConfigItem> getAuditConfigItemListByEntry() {
        return auditConfigItemListByEntry;
    }

    public void setAuditConfigItemListByEntry(List<AuditConfigItem> auditConfigItemListByEntry) {
        this.auditConfigItemListByEntry = auditConfigItemListByEntry;
    }

    public AuditSystemEntry getAuditSystemEntrySelected() {
        return auditSystemEntrySelected;
    }

    public void setAuditSystemEntrySelected(AuditSystemEntry auditSystemEntrySelected) {
        this.auditSystemEntrySelected = auditSystemEntrySelected;
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

    public boolean isEntryFlag() {
        return entryFlag;
    }

    public void setEntryFlag(boolean entryFlag) {
        this.entryFlag = entryFlag;
    }

    public void setNothing(){
    }
    public void setContentNothing(){
        if(!Strings.isNullOrEmpty(auditConfigItemSelected.getUid())){
            auditConfigItemSelected = auditConfigItemRepository.findByUid(auditConfigItemSelected.getUid());
        }else{
            auditConfigItemSelected.setContent(null);
            auditConfigItemSelected.setMark(null);
        }
        auditConfigItem=null;
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

    public List<AuditTemplate> getAuditTemplateListByEntry() {
        return auditTemplateListByEntry;
    }

    public void setAuditTemplateListByEntry(List<AuditTemplate> auditTemplateListByEntry) {
        this.auditTemplateListByEntry = auditTemplateListByEntry;
    }
}
