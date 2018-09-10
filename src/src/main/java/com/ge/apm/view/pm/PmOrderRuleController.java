package com.ge.apm.view.pm;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.SupplierService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparator;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.service.wo.PmOrderReportTemplateService;
import com.ge.apm.view.sysutil.UserContextService;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.google.common.base.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.*;
import org.primefaces.event.NodeSelectEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.StringUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class PmOrderRuleController extends JpaCRUDController<PmOrderRule> {
    private static final long serialVersionUID = -1L;
    private PmOrderRuleRepository dao = null;
    private UserAccount user;
    private UserAccountRepository userAccountRepository;
    private PmOrderRuleItemRepository pmOrderRuleItemRepository;
    private PmOrderRuleCheckListRepository pmOrderRuleCheckListRepository;
    private PmOrderReportTemplateService pmOrderReportTemplateService;
    private List<Integer> ids = new ArrayList<>();
    private String filterPmRuleStatus;
    private String filterPmRuleLevel;
    private TableDefinitionRepository templateDao;
    private List<PmOrderRuleCheckList> checkItemLists;
    private PmOrderRuleCheckList pmRuleCheckItem;

    // for report template
    private UploadedFile uploadedFile;
    private BlobFileService blobService;
    private V2_BlobObject uploadedFileObject;

    private StreamedContent reportTemplateFile;

    private OrgInfoRepository orgInfoRepository;
    private Boolean isInsulate;
    private Integer loginUserOrgType;
    private UserCommonService userCommonService;
    private UaaService uaaService;

    private OrgInfo selectedUserOrg;
    private TenantInfo selectedTenant;
    private TreeNode orgTree;
    private TreeNode selectedNode;
    private TenantInfoRepository tenantInfoRepository;
    private OrgInfo selectedOrg;
    private SupplierRepository supplierDao;
    private Integer manufactureId;
    private AssetInfoRepository assetInfoRepository;
    private RelRuleAssetRepository relRuleAssetRepository;
    private SupplierService supplierService;

    @Override
    protected void init() {
        filterBySite = false;
        dao = WebUtil.getBean(PmOrderRuleRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        supplierDao = WebUtil.getBean(SupplierRepository.class);
        user = UserContextService.getCurrentUserAccount();
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        pmOrderRuleItemRepository = WebUtil.getBean(PmOrderRuleItemRepository.class);
        pmOrderRuleCheckListRepository = WebUtil.getBean(PmOrderRuleCheckListRepository.class);
        pmOrderReportTemplateService = WebUtil.getBean(PmOrderReportTemplateService.class);
        templateDao = WebUtil.getBean(TableDefinitionRepository.class);
        userCommonService = WebUtil.getBean(UserCommonService.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        relRuleAssetRepository = WebUtil.getBean(RelRuleAssetRepository.class);
        supplierService = WebUtil.getBean(SupplierService.class);
        isInsulate = UserContextService.isInsulate();
        loginUserOrgType = orgInfoRepository.findByUid(user.getSiteUID()).getOrgType();
        selectedUserOrg = orgInfoRepository.getByUid(user.getOrgInfoUID());
        initTree(user.getTenantUID());
    }

/*    public void setTenantUid(String tenantUid) {
        //searchFilters.clear();
        //this.tenantUid = tenantUid;
        TenantInfo tempTenantInfo = tenantInfoRepository.findByuid(user.getTenantUID());
        //判断登陆用户是否是LocalITAdmin(本地后台运营管理)
        if(UserContextService.isInsulate()){
            selectedTenant = tempTenantInfo;
            orgTree = uaaService.getOrgTreeBySiteWithoutClinical(user.getSiteUID());
        }else{
            selectedTenant = tenantInfoRepository.findByuid(tenantUid);
            OrgInfo rootOrg = new OrgInfo();
            rootOrg.setId(-1);
            rootOrg.setName(WebUtil.getMessage("AllHospitals"));
            orgTree = new DefaultTreeNode("root", null, null);
            TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
            rootNode.getChildren().addAll(uaaService.getOrgFullTreeByTenantWithoutClinical(tenantUid).getChildren());
        }

        TreeNode selectedUserOrg = TreeNodeUtil.findNodeFromTree(orgTree, selectedOrg);
        if (null != selectedUserOrg) {
            selectedUserOrg.setSelected(true);
            TreeNodeUtil.expandParentsNodes(selectedUserOrg);
        }
    }*/

    private void initTree(String tenantUid) {
        TenantInfo tempTenantInfo = tenantInfoRepository.findByuid(user.getTenantUID());
        selectedTenant = tempTenantInfo;
        orgTree = uaaService.getOrgTreeBySiteWithoutClinical(user.getSiteUID());
/*        if(UserContextService.isInsulate()){
            //隔离租户
            selectedTenant = tempTenantInfo;
            orgTree = uaaService.getOrgTreeBySiteWithoutClinical(user.getSiteUID());
        }else{
            //非隔离租户
            selectedTenant = tenantInfoRepository.findByuid(tenantUid);
            OrgInfo rootOrg = new OrgInfo();
            rootOrg.setId(-1);
            rootOrg.setName(WebUtil.getMessage("AllHospitals"));
            orgTree = new DefaultTreeNode("root", null, null);
            TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
            rootNode.getChildren().addAll(uaaService.getOrgFullTreeByTenantWithoutClinical(tenantUid).getChildren());
        }*/

        TreeNode selectedUserOrg = TreeNodeUtil.findNodeFromTree(orgTree, selectedOrg);
        if (null != selectedUserOrg) {
            selectedUserOrg.setSelected(true);
            TreeNodeUtil.expandParentsNodes(selectedUserOrg);
        }
    }

    public String getOrgFullName(OrgInfo org) {
        if (null == org || org.getId() == -1) {
            return null;
        }
        String name = org.getName();
        while (org.getParentOrg() != null) {
            org = org.getParentOrg();
            name = org.getName() + "/" + name;
        }

        return name;
    }

    public void onSelectOrgForUser(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        selectedUserOrg = (OrgInfo) node.getData();
        selected.setSiteUID(selectedUserOrg.getSiteUID());
    }

    @Override
    protected PmOrderRuleRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<PmOrderRule> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = new ArrayList();
        
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
        if(CollectionUtils.isNotEmpty(this.searchFilters)){
            searchFilters.addAll(this.searchFilters);
        }
        Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
        pageRequest = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
        return dao.findBySearchFilter(searchFilters, pageRequest);
    }

    public List<UserAccount> getOwnerList(){
        return userAccountRepository.getAssetHeadAndStaffByTenantUID(user.getTenantUID());
    }


    /***
     * 获取勾选项
     * @return
     */
    public List<Integer> getSelectedIds() {
        if (selected != null) {
            ids = pmOrderRuleCheckListRepository.findByRuleUID(selected.getUid());
        }
        return ids;
    }

    /***
     * 获取租户下面所有Items
     * @return
     */
    public List<PmOrderRuleItem> getAllItems() {
        List<PmOrderRuleItem> items = pmOrderRuleItemRepository.findByTenantUID(user.getTenantUID());
        if (CollectionUtils.isEmpty(items)) {
            items = new ArrayList<PmOrderRuleItem>();
        }
        return items;
    }

    public void onSelect() {
        this.selected = null;
        this.checkItemLists.clear();
    }

    /**
     * 电气安全检测报告，不关联设备分类
     * @return
     */
    public List<TableDefinition> getTableDefinitionForES() {
        List<SearchFilter> tempFilter = new ArrayList<SearchFilter>();
/*        if(selectedUserOrg != null){
            tempFilter.addAll(userCommonService.getLoginUserFilters(isInsulate,selectedUserOrg.getOrgType()));
        }else{
            tempFilter.addAll(userCommonService.getLoginUserFilters(isInsulate,loginUserOrgType));
        }*/
        //tempFilter.addAll(userCommonService.getLoginUserFilters(isInsulate,loginUserOrgType));
        tempFilter.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
        tempFilter.add(new SearchFilter("status", SearchFilter.Operator.EQ, "Released"));
        tempFilter.add(new SearchFilter("assetGroup", SearchFilter.Operator.IsNull, null));
        return templateDao.findBySearchFilter(tempFilter);
    }

    /***
     * 性能检测报告，关联设备分类
     * @return
     */
    public List<TableDefinition> getTableDefinition() {
        List<SearchFilter> tempFilter = new ArrayList<>();
        //tempFilter.addAll(userCommonService.getLoginUserFilters(isInsulate,loginUserOrgType));
        tempFilter.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
        tempFilter.add(new SearchFilter("status", SearchFilter.Operator.EQ, "Released"));
        if (this.selected != null && this.selected.getAssetGroup() != null) {
            tempFilter.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, selected.getAssetGroup()));
        }
/*        else{
            tempFilter.add(new SearchFilter("assetGroup", SearchFilter.Operator.IsNotNull, null));
        }*/
        return templateDao.findBySearchFilter(tempFilter);
    }



    @Override
    @Transactional
    public void onBeforeSave(PmOrderRule por) {
        pmOrderRuleCheckListRepository.deleteExistsRecord(por.getUid());
    }

    public List<UserAccount> getApproveUserList() {
        return userAccountRepository.getAssetHeadList(user.getTenantUID());
    }

    public UserAccount getUser() {
        return user;
    }

    public void setUser(UserAccount user) {
        this.user = user;
    }

    @Override
    public void onBeforeNewObject(PmOrderRule por) {
        por.setTenantUID(selectedUserOrg.getTenantUID());
        por.setInstitutionUID(selectedUserOrg.getInstitutionUID());
        por.setHospitalUID(selectedUserOrg.getHospitalUID());
        por.setSiteUID(selectedUserOrg.getSiteUID());
        por.setStatus(0);
        por.setCreatedDate(new Date());
        por.setOwnerId(user.getId());
        por.setPeriod(1);//默认一个月
    }

    public void resetNewObject() {
        if (selected.getId() == null) {
            selected = null;
            if (checkItemLists != null) {
                checkItemLists.clear();
            }
        }


    }

    public String getUserName(Integer userId) {
        if (userId == null) {
            return null;
        }
        if (userId <= 0) {
            return null;
        }
        UserAccount user = userAccountRepository.findById(userId);
        if (user == null) {
            return null;
        }

        return user.getName();
    }


    @Transactional
    public void applyChange() {
        {
            selected.setPeriod(getPeriod());
            if (selected.getId() != null) {
                onBeforeSave(selected);
            }
            getName();
            if(selectedUserOrg != null){
                selected.setInstitutionUID(selectedUserOrg.getInstitutionUID());
                selected.setHospitalUID(selectedUserOrg.getHospitalUID());
                selected.setSiteUID(selectedUserOrg.getSiteUID());
            }
            dao.save(selected);
            if (CollectionUtils.isNotEmpty(checkItemLists)) {
                checkItemLists.removeAll(checkItemLists.stream().filter(item -> item.getUid().equals("000")).collect(Collectors.toList()));
                for (PmOrderRuleCheckList item : checkItemLists) {
                    item.setId(null);
                    item.setRuleUID(selected.getUid());
                }
                pmOrderRuleCheckListRepository.save(checkItemLists);
            }
            checkItemLists.clear();
            WebUtil.addSuccessMessage(WebUtil.getMessage("保存成功！"));
        }
    }

    public void getName() {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(selected.getAssetName())) {
            sb.append(selected.getAssetName() + "|");
        }
        if (StringUtils.isNotEmpty(selected.getManufacture())) {
            sb.append(selected.getManufacture() + "|");
        }
        if (StringUtils.isNotEmpty(selected.getFunctionType())) {
            sb.append(selected.getFunctionType() + "|");
        }
        if (StringUtils.isNotEmpty(selected.getRemark())) {
            sb.append(selected.getRemark());
        }
        String name = sb.toString();
        if (name.endsWith("|")) {
            name = name.substring(0, name.lastIndexOf("|"));
        }
        selected.setName(name);
    }

    @Transactional
    public void approve() {
        applyChange();
/*        if(selectedUserOrg != null){
            selected.setInstitutionUID(selectedUserOrg.getInstitutionUID());
            selected.setHospitalUID(selectedUserOrg.getHospitalUID());
            selected.setSiteUID(selectedUserOrg.getSiteUID());
        }*/
        selected.setStatus(1);
        selected.setApproveUserId(user.getId());
        dao.save(selected);
        WebUtil.addSuccessMessage(WebUtil.getMessage("approveSuccess"));
    }

    public void setAssetStatusFilter() {
        if (filterPmRuleStatus == null) {
            return;
        }
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        if (filterPmRuleStatus != null) {
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, Integer.parseInt(filterPmRuleStatus)));
        }
        if (filterPmRuleLevel != null) {
            searchFilters.add(new SearchFilter("level", SearchFilter.Operator.EQ, Integer.parseInt(filterPmRuleLevel)));
        }
    }

    public String getFilterPmRuleStatus() {
        return filterPmRuleStatus;
    }

    public void setFilterPmRuleStatus(String filterPmRuleStatus) {
        this.filterPmRuleStatus = filterPmRuleStatus;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

    public void onPriview() {

    }

    @Override
    @Transactional
    public void delete() {
        String uid = selected.getUid();
        //int level = selected.getLevel();
        pmOrderRuleCheckListRepository.deleteExistsRecord(uid);
        List<RelRuleAsset> relRuleAssets = relRuleAssetRepository.findRelRuleAssets(uid);
        List<RelRuleAsset> updateList = new ArrayList<>(relRuleAssets.size());
        List<RelRuleAsset> deleteList = new ArrayList<>(relRuleAssets.size());
        if(CollectionUtils.isNotEmpty(relRuleAssets)){
            for (RelRuleAsset rra :relRuleAssets) {
                if(StringUtils.isNotEmpty(rra.getRuleUID()) && rra.getRuleUID().equals(uid)){
                    if(StringUtils.isEmpty(rra.getRuleUID2())){
                        deleteList.add(rra);
                    }else{
                        rra.setRuleUID(null);
                        updateList.add(rra);
                    }
                }else if(StringUtils.isNotEmpty(rra.getRuleUID2()) && rra.getRuleUID2().equals(uid)){
                    if(StringUtils.isEmpty(rra.getRuleUID())){
                        deleteList.add(rra);
                    }else{
                        rra.setRuleUID2(null);
                        updateList.add(rra);
                    }
                }
            }
        }
        relRuleAssetRepository.save(updateList);
        relRuleAssetRepository.delete(deleteList);
        super.delete();
    }

    @Transactional
    public void saveAs() {
/*        if(selectedUserOrg != null){
            selected.setInstitutionUID(selectedUserOrg.getInstitutionUID());
            selected.setHospitalUID(selectedUserOrg.getHospitalUID());
            selected.setSiteUID(selectedUserOrg.getSiteUID());
        }*/
        selected.setId(null);
        selected.setStatus(0);
        selected.setCreatedDate(new Date());
        selected.setUid(UUID.randomUUID().toString().replace("-", ""));
        selected.setOwnerId(user.getId());
        selected.setApproveUserId(null);
        applyChange();
    }

    public String getDefalutReport() {
        return dao.getDefalutReport(1);
    }

    public Integer getPeriod() {
        if (selected.getPeriod() != null && selected.getPeriod() > 0) {
            return selected.getPeriod();
        } else {
            List<Integer> itemIds = new ArrayList<Integer>();
            checkItemLists.removeAll(checkItemLists.stream().filter(item -> item.getUid().equals("000")).collect(Collectors.toList()));
            if (!checkItemLists.isEmpty()) {
                Iterator it = checkItemLists.iterator();
                while (it.hasNext()) {
                    PmOrderRuleCheckList o = (PmOrderRuleCheckList)it.next();
                    itemIds.add(o.getPeriod());
                }
            }
            Integer period = 1;
            if (itemIds.size() > 0) {
                Collections.sort(itemIds);
                period =  itemIds.get(0);//pmOrderRuleItemRepository.fetchMinPeriod(user.getTenantUID(), itemIds);
            }
            if (period <= 0) {
                period = 1;
            }
            return period;
        }
    }

    public String getJsonValueById(String baseId) {
        if (!Strings.isNullOrEmpty(baseId)) {
            TableDefinition schema = templateDao.findByBaseId(baseId);
            return schema == null ? null : schema.getSchema();
        } else {
            return null;
        }
    }


    public void initSaveAs() {
        Integer newId = selected.getId();
        selected = null;
        selected = dao.findById(newId);
        selected.setPeriod(null);
        initCheckItemLists();
    }

    public String getFilterPmRuleLevel() {
        return filterPmRuleLevel;
    }

    public void setFilterPmRuleLevel(String filterPmRuleLevel) {
        this.filterPmRuleLevel = filterPmRuleLevel;
    }

    /* since 2018-03-20 **/

    public void initCheckItemLists() {
        if (selected != null && selected.getUid() != null) {
            checkItemLists = pmOrderRuleCheckListRepository.findAllByRuleUID(selected.getUid());
        }
        if (CollectionUtils.isEmpty(checkItemLists)) {
            checkItemLists = new ArrayList<>();
        }
    }

    public void prepareCreate() {
        try {
            this.selected = null;
            this.checkItemLists = null;
            super.prepareCreate();
            initCheckItemLists();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    public void prepareEdit() {
        super.prepareEdit();
        Integer newId = selected.getId();
        selected = null;
        selected = dao.findById(newId);
        initCheckItemLists();
    }

    public void prepareEditItemDetail(String itemId) {
        if ("new".equals(itemId)) {
            pmRuleCheckItem = new PmOrderRuleCheckList();
            pmRuleCheckItem.setUid(UUID.randomUUID().toString().replace("-", ""));
            pmRuleCheckItem.setRuleUID(selected.getUid());
            pmRuleCheckItem.setTenantUID(user.getTenantUID());
            pmRuleCheckItem.setInstitutionUID(selectedUserOrg.getInstitutionUID());
            pmRuleCheckItem.setHospitalUID(selectedUserOrg.getHospitalUID());
            pmRuleCheckItem.setSiteUID(selectedUserOrg.getSiteUID());
        } else {
            pmRuleCheckItem = checkItemLists.stream().filter(item -> item.getUid().equals(itemId)).findAny().get();
        }
    }

    public void deleteItemDetail(String itemId) {
        checkItemLists.removeAll(checkItemLists.stream().filter(item -> item.getUid().equals(itemId)).collect(Collectors.toList()));
    }

    public void addCheckListItem() {
        if (!checkItemLists.contains(pmRuleCheckItem)) {
            checkItemLists.add(pmRuleCheckItem);
        }
        //checkItemLists.removeAll(checkItemLists.stream().filter(item -> item.getUid().equals("000")).collect(Collectors.toList()));
        cancelCheckListItem();
    }

    public void cancelCheckListItem() {
        pmRuleCheckItem = null;
    }

    public PmOrderRuleCheckList getPmRuleCheckItem() {
        return pmRuleCheckItem;
    }

    public void setPmRuleCheckItem(PmOrderRuleCheckList pmRuleCheckItem) {
        this.pmRuleCheckItem = pmRuleCheckItem;
    }

    public List<PmOrderRuleCheckList> getCheckItemLists() {
        if(checkItemLists == null){
            return null;
        }
        PmOrderRuleCheckList itemDetail = new PmOrderRuleCheckList();
        itemDetail.setUid("000");
        if (!checkItemLists.contains(itemDetail)) {
            checkItemLists.add(itemDetail);
        }else{
            checkItemLists.remove(itemDetail);
            checkItemLists.add(itemDetail);
        }
        return checkItemLists;
    }

    public void setCheckItemLists(List<PmOrderRuleCheckList> checkItemLists) {
        this.checkItemLists = checkItemLists;
    }

    public void generateReportTemplateFile() {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("report/maintenance-template.docx");

        try {
            InputStream doc = pmOrderReportTemplateService.generateReportTemplateByRule(stream, selected);
            this.reportTemplateFile = new DefaultStreamedContent(doc, "application/msword", "maintenance-report-template.docx");
        } catch (Exception e)  {
            Logger.getLogger(PmOrderRuleController.class.getName()).log(Level.WARNING, "Error happens while generate word template", e);
            WebUtil.addErrorMessage("Word模版生成失败");
        }
    }

    /**
     * Remote Report template from master-data.
     */
    public void removeReportTemplate() {
        if (selected != null) {
            blobService.deleteBlobFileById(selected.getReportStorageId());
            selected.setReportStorageId(null);
            selected.setReportTemplateName(null);
            this.dao.save(selected);
            this.uploadedFile = null;
        }
    }


    /**
     * Save uploaded file to master-data service.
     *
     * @param event {@link FileUploadEvent} object.
     */
    public void uploadDataFile(FileUploadEvent event) {
        this.uploadedFile = event.getFile();
        uploadedFileObject = blobService.uploadFile(uploadedFile);
        this.selected.setReportTemplateName(uploadedFile.getFileName());
    }

    /**
     * Cancel upload.
     */
    public void cancelUpload() {
        this.uploadedFile = null;
        if (null != uploadedFileObject) {
            uploadedFileObject = null;
        }
    }

    /**
     * Update report storage id after finish upload.
     */
    public void finishUpload() {
        if (uploadedFileObject != null) {
            String storageId = uploadedFileObject.getObjectStorageId();
            this.selected.setReportStorageId(storageId);
            this.dao.save(selected);
        }
    }

    public List<String> getSupplier(String name){
       return supplierService.getSupplier(name);
    }

    public void setNothing(){

    }

    public Integer getManufactureId() {
        return manufactureId;
    }

    public void setManufactureId(Integer manufactureId) {
        this.manufactureId = manufactureId;
    }

    /**
     * Getter for uploaded file.
     *
     * @return {@link UploadedFile} object.
     */
    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    /**
     * Setter for {@code uploadedFile}.
     *
     * @param uploadedFile {@link UploadedFile} object.
     */
    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    /**
     * Getter for report template file.
     *
     * @return {@link StreamedContent} object.
     */
    public StreamedContent getReportTemplateFile() {
        return reportTemplateFile;
    }

    /**
     * Setter for {@code reportTemplateFile}.
     *
     * @param reportTemplateFile {@link StreamedContent} object.
     */
    public void setReportTemplateFile(StreamedContent reportTemplateFile) {
        this.reportTemplateFile = reportTemplateFile;
    }

    public Boolean getInsulate() {
        return isInsulate;
    }

    public void setInsulate(Boolean insulate) {
        isInsulate = insulate;
    }

    public Integer getLoginUserOrgType() {
        return loginUserOrgType;
    }

    public void setLoginUserOrgType(Integer loginUserOrgType) {
        this.loginUserOrgType = loginUserOrgType;
    }

    public OrgInfo getSelectedUserOrg() {
        return selectedUserOrg;
    }

    public void setSelectedUserOrg(OrgInfo selectedUserOrg) {
        this.selectedUserOrg = selectedUserOrg;
    }

    public TenantInfo getSelectedTenant() {
        return selectedTenant;
    }

    public void setSelectedTenant(TenantInfo selectedTenant) {
        this.selectedTenant = selectedTenant;
    }

    public TreeNode getOrgTree() {
        return orgTree;
    }

    public void setOrgTree(TreeNode orgTree) {
        this.orgTree = orgTree;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public void setSelectedOrg(OrgInfo selectedOrg) {
        this.selectedOrg = selectedOrg;
    }
}
