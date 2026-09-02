package com.ge.apm.view.pm;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.FieldColumnDto;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.SupplierService;
import com.ge.apm.service.common.ConfigItemService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.pm.PmOrderPreCreateService;
import com.ge.apm.service.systemTempate.SystemTemplateService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.service.wo.PmOrderReportTemplateService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.context.LocaleHolder;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
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
    private BlobObjectRepository blobObjectRepository;
    private List<Integer> ids = new ArrayList<>();
    private String filterPmRuleStatus;
    private String filterPmRuleLevel;
    private TableDefinitionRepository templateDao;
    private List<PmOrderRuleCheckList> checkItemLists;
    private PmOrderRuleCheckList pmRuleCheckItem;
    private ConfigItemService configItemService;
    private ConfigItemValueRepository configItemValueRepository;

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
    private ConfigService configService;
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
    private List<String> deletedCheckListIds = new ArrayList<>();
    private UserContextService userContextService;
    private String ruleLevelTip;
    private Map<String, Boolean> selectedMap;
    private List<PmOrderRule> currentPageAssets;

    private SystemTemplateService systemTemplateService;
    private PmOrderPreCreateService pmOrderPreCreateService;
    private Integer periodOld;//保养周期
    private String periodUnitOld;//保养周期单位
    @Setter@Getter
    private String modifyScore = "";

    @Setter@Getter
    private String pmRuleShowScoreFlag;

    private Date minDate;

    @Override
    protected void init() {
        filterBySite = false;
        dao = WebUtil.getBean(PmOrderRuleRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        supplierDao = WebUtil.getBean(SupplierRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        user = UserContextService.getCurrentUserAccount();
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        pmOrderRuleItemRepository = WebUtil.getBean(PmOrderRuleItemRepository.class);
        pmOrderRuleCheckListRepository = WebUtil.getBean(PmOrderRuleCheckListRepository.class);
        pmOrderReportTemplateService = WebUtil.getBean(PmOrderReportTemplateService.class);
        configService = WebUtil.getBean(ConfigService.class);
        configItemService = WebUtil.getBean(ConfigItemService.class);
        configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);
        templateDao = WebUtil.getBean(TableDefinitionRepository.class);
        userCommonService = WebUtil.getBean(UserCommonService.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        relRuleAssetRepository = WebUtil.getBean(RelRuleAssetRepository.class);
        supplierService = WebUtil.getBean(SupplierService.class);
        blobObjectRepository = WebUtil.getBean(BlobObjectRepository.class);
        systemTemplateService = WebUtil.getBean(SystemTemplateService.class);
        pmOrderPreCreateService = WebUtil.getBean(PmOrderPreCreateService.class);
        isInsulate = UserContextService.isInsulate();
        loginUserOrgType = orgInfoRepository.findByUid(user.getSiteUID()).getOrgType();
        selectedUserOrg = orgInfoRepository.getByUid(user.getSiteUID());
        this.selectedMap = new HashMap<>();
        this.currentPageAssets = new ArrayList<>();
        initOrgTree();
        pmRuleShowScoreFlag = configItemService.getConfigItemByKeyToString("pmRuleShowScoreFlag", user);
    }

    public boolean getPmRule(){
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        WorkflowConfig workflowConfig = configService.findConfigBySite(userAccount.getSiteUID());
        return workflowConfig != null && workflowConfig.getPmRuleLevelFour();
    }

    public void initOrgTree() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();
        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(loginUser.getSiteUID(), loginUser.getUid());
        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        orgTree = new DefaultTreeNode("root", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        if (null != treeNode) {
            rootNode.getChildren().add(treeNode);
        }
        rootNode.setExpanded(true);
    }

/*    public void setTenantUid(String tenantUid) {
        //searchFilters.clear();
        //this.tenantUid = tenantUid;
        TenantInfo tempTenantInfo = tenantInfoRepository.findByuid(user.getTenantUID());
        //判断登录用户是否是LocalITAdmin(本地后台运营管理)
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
/*        while (org.getParentOrg() != null) {
            org = org.getParentOrg();
            name = org.getName() + "/" + name;
        }*/

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
        if(this.searchFilters == null){
            searchFilters =  new ArrayList<>();
        }
        List<SearchFilter> searchFilters = new ArrayList();
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
        if(CollectionUtils.isNotEmpty(this.searchFilters)){
            searchFilters.addAll(this.searchFilters);
        }
        setSearchFilter(searchFilters);

        Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
        pageRequest = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
        Page<PmOrderRule> pmOrderRulePage = dao.findBySearchFilter(searchFilters, pageRequest);
        currentPageAssets = pmOrderRulePage.getContent();
        return pmOrderRulePage;
    }

    public void setSearchFilter(List<SearchFilter> searchFilters) {
        if (hasFilterOnField("safetyReport")) {
            String value = searchFilters.stream().filter(item -> item.fieldName.equals("safetyReport")).map(item -> (String) item.value).findAny().orElse("");
            searchFilters.removeAll(searchFilters.stream().filter(item -> "safetyReport".equals(item.fieldName)).collect(Collectors.toList()));
            if (StrUtil.isNotEmpty(value)) {
                if ("Y".equals(value)) {
                    searchFilters.add(new SearchFilter("safetyReport", SearchFilter.Operator.IsNotNull, ""));
                } else if ("N".equals(value)) {
                    searchFilters.add(new SearchFilter("safetyReport", SearchFilter.Operator.IsNull, ""));
                }
            }
        }
        if (hasFilterOnField("performanceReport")) {
            String value = searchFilters.stream().filter(item -> item.fieldName.equals("performanceReport")).map(item -> (String) item.value).findAny().orElse("");
            searchFilters.removeAll(searchFilters.stream().filter(item -> "performanceReport".equals(item.fieldName)).collect(Collectors.toList()));
            if (StrUtil.isNotEmpty(value)) {
                if ("Y".equals(value)) {
                    searchFilters.add(new SearchFilter("performanceReport", SearchFilter.Operator.IsNotNull, ""));
                } else if ("N".equals(value)) {
                    searchFilters.add(new SearchFilter("performanceReport", SearchFilter.Operator.IsNull, ""));
                }
            }
        }
        if (hasFilterOnField("isScheduleType")) {
            String value = searchFilters.stream().filter(item -> item.fieldName.equals("isScheduleType")).map(item -> (String) item.value).findAny().orElse("");
            searchFilters.removeAll(searchFilters.stream().filter(item -> "isScheduleType".equals(item.fieldName)).collect(Collectors.toList()));
            if (StrUtil.isNotEmpty(value)) {
                if ("0".equals(value)) {
                    searchFilters.add(new SearchFilter("isScheduleType", SearchFilter.Operator.IsNull, "","1*2+"));
                    searchFilters.add(new SearchFilter("isScheduleType", SearchFilter.Operator.EQ, 0,"1*2+"));
                } else  {
                    searchFilters.add(new SearchFilter("isScheduleType", SearchFilter.Operator.EQ, 1));
                }
            }
        }
    }

    public List<UserAccount> getOwnerList(){
        return userAccountRepository.getAssetHeadAndStaffByTenantUID(user.getTenantUID());
    }

    public String getPeriodUnit(String periodUnit){
        if (StringUtils.isNotEmpty(periodUnit)) {
            switch (periodUnit) {
                case "y":
                    return "年";
                case "m":
                    return "月";
                case "w":
                    return "周";
                case "d":
                    return "日";
                default:
                    break;
            }
        }
        return null;
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
        tempFilter.add(new SearchFilter("templateType", SearchFilter.Operator.EQ, TableDefinition.TableTemplateType.Maintain.name()));
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
        tempFilter.add(new SearchFilter("templateType", SearchFilter.Operator.EQ, TableDefinition.TableTemplateType.Maintain.name()));
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



    @Transactional
    public void onBeforeSave(String ruleId,List<String> deletedCheckListIds) {
        if(CollectionUtils.isNotEmpty(deletedCheckListIds)){
            pmOrderRuleCheckListRepository.updateStatus(deletedCheckListIds);
        }
        pmOrderRuleCheckListRepository.deleteExistsRecord(ruleId);
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
        por.setPeriod(1);
        por.setPeriodUnit("m");//默认一个月
        por.setIsScheduleType(0);//默认非定时周期
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
        selected.setPeriod(getPeriod());
        // 校验定时周期配置
        if (!validateScheduleConfig()) {
            return;
        }
        if (selected.getId() != null) {
            onBeforeSave(selected.getUid(),deletedCheckListIds);
        }
        getName();
        if(selectedUserOrg != null){
            selected.setInstitutionUID(selectedUserOrg.getInstitutionUID());
            selected.setHospitalUID(selectedUserOrg.getHospitalUID());
            selected.setSiteUID(selectedUserOrg.getSiteUID());
        }
        dao.save(selected);
        // add blob
        if (ObjectUtil.isNotEmpty(uploadedFileObject)) {
            uploadedFileObject.setBoType(V2_BlobObject.BO_TYPE_PM_RULE);
            uploadedFileObject.setBoId(selected.getId());
            uploadedFileObject.setBoUuid(selected.getUid());
            uploadedFileObject.setTenantUid(selected.getTenantUID());
            blobObjectRepository.save(uploadedFileObject);
        }
        if (CollectionUtils.isNotEmpty(checkItemLists)) {
            checkItemLists.removeAll(checkItemLists.stream().filter(item -> item.getUid().equals("000")).collect(Collectors.toList()));
            for (PmOrderRuleCheckList item : checkItemLists) {
                item.setId(null);
                item.setRuleUID(selected.getUid());
            }
            pmOrderRuleCheckListRepository.save(checkItemLists);
        }
        checkItemLists.clear();
        if(!Objects.equals(selected.getPeriod(), periodOld) || !selected.getPeriodUnit().equals(periodUnitOld)){
            pmOrderPreCreateService.deleteByPmOrderRuleId(selected.getUid());
        }
        WebUtil.addSuccessMessage(WebUtil.getMessage("SaveSuccess"));
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
        deleteRule(selected.getUid());
    }
    public void deleteRule(String uid) {
        pmOrderRuleCheckListRepository.deleteExistsRecord(uid);
        List<RelRuleAsset> relRuleAssets = relRuleAssetRepository.findRelRuleAssets(uid);
        List<RelRuleAsset> updateList = new ArrayList<>(relRuleAssets.size());
        List<RelRuleAsset> deleteList = new ArrayList<>(relRuleAssets.size());
        if(CollectionUtils.isNotEmpty(relRuleAssets)){
            for (RelRuleAsset rra :relRuleAssets) {
                if(StringUtils.isNotEmpty(rra.getRuleUID()) && rra.getRuleUID().equals(uid)){
                    if(StringUtils.isEmpty(rra.getRuleUID2())
                            && StringUtils.isEmpty(rra.getRuleUID4()) && StringUtils.isEmpty(rra.getRuleUIDInspection())){
                        deleteList.add(rra);
                    }else{
                        rra.setRuleUID(null);
                        updateList.add(rra);
                    }
                }else if(StringUtils.isNotEmpty(rra.getRuleUID2()) && rra.getRuleUID2().equals(uid)){
                    if(StringUtils.isEmpty(rra.getRuleUID())
                            && StringUtils.isEmpty(rra.getRuleUID4()) && StringUtils.isEmpty(rra.getRuleUIDInspection())){
                        deleteList.add(rra);
                    }else{
                        rra.setRuleUID2(null);
                        updateList.add(rra);
                    }
                }else if(StringUtils.isNotEmpty(rra.getRuleUIDInspection()) && rra.getRuleUIDInspection().equals(uid)){
                    if(StringUtils.isEmpty(rra.getRuleUID())
                            && StringUtils.isEmpty(rra.getRuleUID2()) && StringUtils.isEmpty(rra.getRuleUID4())){
                        deleteList.add(rra);
                    }else{
                        rra.setRuleUIDInspection(null);
                        updateList.add(rra);
                    }
                }else if(StringUtils.isNotEmpty(rra.getRuleUID4()) && rra.getRuleUID4().contains(uid)){
                    if(StringUtils.isEmpty(rra.getRuleUID())
                            && StringUtils.isEmpty(rra.getRuleUID2()) && StringUtils.isEmpty(rra.getRuleUIDInspection())){
                        String[] split = StringUtils.split(uid, "|");
                        List<String> ruldUid4s = Arrays.asList(split);
                        if(ruldUid4s.size() == 1) {
                            deleteList.add(rra);
                        }else {
                            ruldUid4s.remove(uid);
                            String ruleUidsStr = StringUtils.join(ruldUid4s, "|");
                            rra.setRuleUID4(ruleUidsStr);
                            updateList.add(rra);
                        }
                    }else{
                        rra.setRuleUID4(null);
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
        //selected.setId(null);
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
            List<Integer> itemIds = new ArrayList<>();
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
                period =  itemIds.get(0);
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
        prepareEdit();
        selected.setId(null);
        selected.setPeriod(null);
        /*selectedUserOrg = orgInfoRepository.findByUid(selected.getSiteUID());
        initCheckItemLists();*/
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
            this.uploadedFile = null;
            super.prepareCreate();
            initCheckItemLists();
            ruleLevelTip ="";
            selectedUserOrg = orgInfoRepository.findByUid(selected.getSiteUID());
            // Set default work hours from pmEntryTypeSet config if not already set
            if (selected.getDefaultWorkHours() == null) {
                String value = configItemValueRepository.findByKeyAndSiteForUseParent(
                        selected.getSiteUID(), selected.getHospitalUID(),
                        selected.getInstitutionUID(), selected.getTenantUID(), "pmEntryTypeSet");
                if (StrUtil.isNotEmpty(value)) {
                    FieldColumnDto fieldColumnDto = JSON.parseObject(value, FieldColumnDto.class);
                    // entryType=1(工时段) or entryType=3(仅工时段), do not assign value
                    if (fieldColumnDto != null && fieldColumnDto.getEntryType() != null
                            && fieldColumnDto.getEntryType() != 1 && fieldColumnDto.getEntryType() != 3
                            && fieldColumnDto.getHours() != null) {
                        selected.setDefaultWorkHours(fieldColumnDto.getHours());
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    public void prepareEdit() {
        super.prepareEdit();
        selectedUserOrg = orgInfoRepository.findByUid(selected.getSiteUID());
        initCheckItemLists();
        if(selected!=null){
            periodOld=selected.getPeriod();
            periodUnitOld=selected.getPeriodUnit();

            if (selected.getDefaultWorkHours() == null) {
                String value = configItemValueRepository.findByKeyAndSiteForUseParent(
                        selected.getSiteUID(), selected.getHospitalUID(),
                        selected.getInstitutionUID(), selected.getTenantUID(), "pmEntryTypeSet");
                if (StrUtil.isNotEmpty(value)) {
                    FieldColumnDto fieldColumnDto = JSON.parseObject(value, FieldColumnDto.class);
                    // entryType=1(工时段) or entryType=3(仅工时段), do not assign value
                    if (fieldColumnDto != null && fieldColumnDto.getEntryType() != null
                            && fieldColumnDto.getEntryType() != 1 && fieldColumnDto.getEntryType() != 3
                            && fieldColumnDto.getHours() != null) {
                        selected.setDefaultWorkHours(fieldColumnDto.getHours());
                    }
                }
            }
        }
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
            pmRuleCheckItem.setStatus(1);
        } else {
            if (checkItemLists.stream().filter(item -> item.getUid().equals(itemId)).findAny().isPresent()){
                pmRuleCheckItem = checkItemLists.stream().filter(item -> item.getUid().equals(itemId)).findAny().get();
            }
        }
    }

    public void deleteItemDetail(String itemId) {
        if(!deletedCheckListIds.contains(itemId)){
            deletedCheckListIds.add(itemId);
        }
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

    public boolean isLocale(){
        Locale locale = LocaleHolder.getLocale();
        //System.out.println("locale  is "+locale);
        if(locale.getLanguage().equalsIgnoreCase("zh")){
            return true;
        } else {
            return false;
        }
    }

    public void generateReportTemplateFile() throws IOException{
//        String fileName = "maintenance-template.docx";
//        if (isLocale()){
//            fileName = "maintenance-template_zh.docx";
//        } else {
//            fileName = "maintenance-template_en.docx";
//
//        }
//        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("report/" + fileName );
        InputStream stream = systemTemplateService.getTemplateInputStream(SystemTemplateEnum.PM_ORDER_REPORT, user);


        try {
            InputStream doc = pmOrderReportTemplateService.generateReportTemplateByRule(stream, selected);
            this.reportTemplateFile = new DefaultStreamedContent(doc, "application/msword", "maintenance-report-template.docx");
        } catch (Exception e)  {
            Logger.getLogger(PmOrderRuleController.class.getName()).log(Level.WARNING, "Error happens while generate word template", e);
            WebUtil.addErrorMessage(WebUtil.getMessage("FailedGenerateWordErrMsg"));
        }finally {
            stream.close();
        }
    }

    /**
     * Remote Report template from master-data.
     */
    public void removeReportTemplate() {
        if (selected != null) {
            //不再删除附件，防止同一个附件被多个保养规则引用
            //blobService.deleteBlobFileById(selected.getReportStorageId());
            selected.setReportStorageId(null);
            selected.setReportTemplateName(null);
            //this.dao.save(selected);
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
//            this.dao.save(selected);
        }
    }

    public List<String> getSupplier(String name){
       return supplierService.getSupplier(name);
    }

    public void setNothing(){

    }

    public void cancelSave(){
        if(crudActionName.equalsIgnoreCase("Create")){
            prepareCreate();
            removeReportTemplate();
        }
        if(crudActionName.equalsIgnoreCase("Edit")){
            prepareEdit();
        }

    }


    public String changeruleLevelTip(){
        if(selected.getLevel() != null){
        if(selected.getLevel() == 2){
            ruleLevelTip = WebUtil.getMessage("SecondaryMaintenanceTip");
        }
        if(selected.getLevel() == 3){
            ruleLevelTip = WebUtil.getMessage("ThreelevelMaintenanceTip");
        }
        }
        return ruleLevelTip;
    }

    public boolean isDisable(){
        if(selected == null){
            return false;
        }
        boolean isAssetHead = UserContextService.checkRole("AssetHead");
        if(isAssetHead){
            return false;
        }else{
            return selected.getStatus() == 1 ? true : false;
        }
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = currentPageAssets.stream().noneMatch(item -> !selectedMap.containsKey(item.getUid()) || !selectedMap.get(item.getUid()));
        for (PmOrderRule item : currentPageAssets) {
            selectedMap.put(item.getUid(), !isSelectedAll);
        }
    }

    public void batchDelete(){
        Map<String, Boolean> map = this.selectedMap;
        List<String> deleteUids = Lists.newArrayList();
        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            if (Boolean.FALSE.equals(entry.getValue())) {
                continue;
            }
            deleteRule(entry.getKey());
            deleteUids.add(entry.getKey());
        }
        dao.deleteById(deleteUids);
        this.selectedMap.clear();
    }

    public void batchModify(){
        Map<String, Boolean> map = this.selectedMap;
        List<String> deleteUids = Lists.newArrayList();
        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            if (Boolean.FALSE.equals(entry.getValue())) {
                continue;
            }
            PmOrderRule rule = dao.findByUid(entry.getKey());
            rule.setRuleScore(modifyScore);
            dao.save(rule);
        }
    }


    public boolean isChecked() {
        return selectedMap.containsValue(true);
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

    public List<String> getDeletedCheckListIds() {
        return deletedCheckListIds;
    }

    public void setDeletedCheckListIds(List<String> deletedCheckListIds) {
        this.deletedCheckListIds = deletedCheckListIds;
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

    public String getRuleLevelTip() {
        return ruleLevelTip;
    }

    public void setRuleLevelTip(String ruleLevelTip) {
        this.ruleLevelTip = ruleLevelTip;
    }

    public Map<String, Boolean> getSelectedMap() {
        return selectedMap;
    }

    public void setSelectedMap(Map<String, Boolean> selectedMap) {
        this.selectedMap = selectedMap;
    }

    /**
     * 获取1-31天的列表
     */
    public List<Integer> getMonthDays() {
        List<Integer> days = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            days.add(i);
        }
        return days;
    }

    /**
     * 获取1-12月的列表
     */
    public List<Integer> getMonths() {
        List<Integer> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add(i);
        }
        return months;
    }

    /**
     * 校验定时周期配置
     */
    public boolean validateScheduleConfig() {
        if (selected == null) {
            return true;
        }
        // 如果选择了定时周期
        if (selected.getIsScheduleType() != null && selected.getIsScheduleType() == 1) {
            // 校验首次生成工单日期
            if (selected.getFirstGenerateDate() == null) {
                WebUtil.addErrorMessage("首次生成工单日期不能为空");
                return false;
            }
            // 校验首次生成工单日期 >= 当前日期
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date today = cal.getTime();
//            if (selected.getFirstGenerateDate().before(today)) {
//                WebUtil.addErrorMessage("首次生成工单日期必须大于等于当前日期");
//                return false;
//            }
            // 根据周期单位校验
            String periodUnit = selected.getPeriodUnit();
            if ("w".equals(periodUnit)) {
                if (selected.getScheduleDay() == null) {
                    WebUtil.addErrorMessage("周几不能为空");
                    return false;
                }
            } else if ("m".equals(periodUnit)) {
                if (selected.getScheduleDay() == null) {
                    WebUtil.addErrorMessage("每月几号不能为空");
                    return false;
                }
            } else if ("y".equals(periodUnit)) {
                if (selected.getScheduleMonth() == null) {
                    WebUtil.addErrorMessage("每年几月不能为空");
                    return false;
                }
                if (selected.getScheduleDay() == null) {
                    WebUtil.addErrorMessage("每月几号不能为空");
                    return false;
                }
            }
//            else if ("d".equals(periodUnit)) {
//                WebUtil.addErrorMessage("日周期暂不支持定时周期配置");
//                return false;
//            }
            // 校验预计完成天数
            if (selected.getCompletionDays() == null || selected.getCompletionDays() <= 0) {
                WebUtil.addErrorMessage("预计完成天数必须大于0");
                return false;
            }
        }
        return true;
    }

    public Date getMinDate() {
        LocalDate now = LocalDate.now();
        minDate = Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return minDate;
    }

    public void setMinDate(Date mindate) {
        this.minDate = mindate;
    }
}
