package com.ge.apm.view.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.*;
import com.ge.apm.service.nurseSite.WordReportExportService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.MessageUtil;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetAchievementsController extends JpaCRUDController<AssetAchievements> {
    private Logger log = Logger.getLogger(AssetAchievementsController.class.getName());

    /*接口类*/
    private AssetInfoRepository assetInfoRepository;
    private AssetAchievementsRepository assetAchievementsRepository;
    private OrgInfoRepository orgInfoRepository;
    private I18nMessageRepository i18nDao;
    private UserContextService userContextService;
    private OrgInfoTreeService orgInfoTreeService;
    private AssetInfoMgrService aiMgrService;
    private AssetLifecyleService assetLifecyleService;
    private UaaService uaaService;
    private CustomConfigService ccService;
    private WordReportExportService exportService;
    private SupplierService supplierService;
    private TableDefinitionRepository templateDao;
    private BlobFileService blobService;
    private UnstructuredDataCollectionRepository unstructuredDataCollectionRepository;

    /*属性类*/
    @Getter@Setter
    private UserAccount user;
    @Getter@Setter
    private List<AssetInfo> assetInfoList;
    @Getter@Setter
    private AssetInfo selectedAssetInfo;
    @Getter@Setter
    private AssetAchievements selected; //选定的所属院区
    @Getter@Setter
    private OrgInfo selectSite; //选定的所属院区
    @Getter@Setter
    private TreeNode orgTree;
    @Getter@Setter
    private TreeNode selectedNode;
    @Getter@Setter
    private UploadedFile uploadedFile;
    @Getter@Setter
    private V2_BlobObject uploadedFileObject;
    @Getter@Setter
    private StreamedContent reportTemplateFile;
    @Getter@Setter
    private Integer assetGroup;
    @Getter@Setter
    private List<I18nMessage> assetGroupSecondaryList;
    @Getter@Setter
    private Map<String, Boolean> selectedMap; //复选的设备
    @Getter@Setter
    private Sort lastPageSort; //export use
    @Getter@Setter
    private List<AssetAchievements> currentPageAssets;
    @Getter@Setter
    private StreamedContent wordFile;
    @Getter@Setter
    private StreamedContent zipFile;
    @Getter@Setter
    private CustomConfigObject customConfig;
    @Getter@Setter
    private String functionType; //设备型号
    @Getter@Setter
    private String assetName; //设备名称
    @Getter@Setter
    private List<SearchFilter> assetSearchFilters;
    @Getter@Setter
    private boolean isDisabel = false;


    @Override
    protected void init() {
        /*接口类*/
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        assetLifecyleService = WebUtil.getBean(AssetLifecyleService.class);
        aiMgrService = WebUtil.getBean(AssetInfoMgrService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        ccService = WebUtil.getBean(CustomConfigService.class);
        exportService = WebUtil.getBean(WordReportExportService.class);
        assetAchievementsRepository = WebUtil.getBean(AssetAchievementsRepository.class);
        supplierService = WebUtil.getBean(SupplierService.class);
        templateDao = WebUtil.getBean(TableDefinitionRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        unstructuredDataCollectionRepository = WebUtil.getBean(UnstructuredDataCollectionRepository.class);

        /*属性类*/
        this.selectedMap = new HashMap();
        user = UserContextService.getCurrentUserAccount();
        /*初始化方法*/
        initOrgTree();
    }

    public void selectEvent(){

    }

    public void setUserSearchFilters() {
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID()));
        if (!ObjectUtils.isEmpty(selectSite)) {
            String[] fields = new String[]{"siteUID", "hospitalUID", "institutionUID"};
            Arrays.asList(fields).forEach(field -> this.updateSearchFilter(new SearchFilter(field, SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+")));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> item.conditionPath.equals("1*5+")).collect(Collectors.toList()));
        }
        if (null != assetGroup) {
            this.updateSearchFilter(new SearchFilter("assetGroup", SearchFilter.Operator.EQ,  assetGroup, "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "assetGroup".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
        if (!StringUtils.isEmpty(functionType)) {
            this.updateSearchFilter(new SearchFilter("functionType", SearchFilter.Operator.LIKE,  functionType, "1*3+"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "functionType".equals(item.fieldName) && item.conditionPath.equals("1*3+")).collect(Collectors.toList()));
        }
        if (!StringUtils.isEmpty(assetName)) {
            this.updateSearchFilter(new SearchFilter("name", SearchFilter.Operator.LIKE,  assetName, "1*3+"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "name".equals(item.fieldName) && item.conditionPath.equals("1*3+")).collect(Collectors.toList()));
        }
    }

    @Override
    protected Page<AssetAchievements> loadData(PageRequest pageRequest) {
        selected = null;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        setUserSearchFilters();
        Page<AssetAchievements> res = assetAchievementsRepository.findBySearchFilter(this.searchFilters, pageRequest);
        lastPageSort = pageRequest.getSort(); //export excel use
        currentPageAssets = res.getContent();
        return res;
    }

    @Override
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
        this.selectedMap.clear();
    }

    public void onSchemaSelected(AssetAchievements assetAchievements) {

    }

    public void assetGroupChangeListener() {
        if (assetGroup == null) {
            assetGroupSecondaryList = Collections.emptyList();
        } else {
            assetGroupSecondaryList = i18nDao.getByMsgTypeAndSiteId("assetGroupSecondary_" + String.valueOf(assetGroup.intValue()), userContextService.getLoginUser().getSiteId());
        }
    }

    public void delete(AssetAchievements assetAchievements) {
        assetSearchFilters = new ArrayList<>();
        selected = assetAchievements;
        setAssetSearchFilters();
        List<AssetInfo> assetInfos = assetInfoRepository.findBySearchFilter(assetSearchFilters);
        assetInfos.forEach(assetInfo -> {
            assetInfo.setAssetAchievements(null);
            assetInfoRepository.save(assetInfos);
        });
        selected = null;
        assetAchievementsRepository.delete(assetAchievements);
    }

    @Transactional
    public void applyChange() {
        if (selectedAssetInfo == null) {
            WebUtil.addSuccessMessage(WebUtil.getMessage("EquipmentMustBeSelectedErrMsg"));
            return;
        }
        getName();
        if(selectSite != null){
            selected.setInstitutionUID(selectSite.getInstitutionUID());
            selected.setHospitalUID(selectSite.getHospitalUID());
            selected.setSiteUID(selectSite.getSiteUID());
            selected.setSiteId(selectSite.getSiteId());
            selected.setTenantUID(selectSite.getTenantUID());
        }
        setAssetToAchievements();
        assetAchievementsRepository.save(selected);
        unstructuredDataCollectionRepository.save(buildUdc(selected));
        selectedAssetInfo.setAssetAchievements(selected.getUid());
        assetInfoRepository.save(selectedAssetInfo);
        WebUtil.addSuccessMessage(WebUtil.getMessage("SaveSuccess"));
    }

    public UnstructuredDataCollection buildUdc(AssetAchievements assetAchievements) {
        UnstructuredDataCollection udc = unstructuredDataCollectionRepository.findCKByUIdAssetAchievements(assetAchievements.getUid());
        if (udc == null) {
            udc = new UnstructuredDataCollection();
            udc.setCreatedDate(new Date());
        }
        udc.setBoType("asset_achievements_report");
        udc.setBoUuid(assetAchievements.getUid());
        udc.setSchemaId(assetAchievements.getAchievementsReport());
        return udc;
    }

    public void setAssetToAchievements() {
        selected.setAssetGroup(selectedAssetInfo.getAssetGroup());
        selected.setAssetName(selectedAssetInfo.getName());
        selected.setFunctionType(selectedAssetInfo.getFunctionType());
        selected.setManufacture(selectedAssetInfo.getManufacture());
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

    public void getName() {
        StringBuilder sb = new StringBuilder();
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(selected.getAssetName())) {
            sb.append(selected.getAssetName() + "|");
        }
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(selected.getManufacture())) {
            sb.append(selected.getManufacture() + "|");
        }
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(selected.getFunctionType())) {
            sb.append(selected.getFunctionType() + "|");
        }
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(selected.getRemark())) {
            sb.append(selected.getRemark());
        }
        String name = sb.toString();
        if (name.endsWith("|")) {
            name = name.substring(0, name.lastIndexOf("|"));
        }
        selected.setName(name);
    }

    public void setAssetSearchFilters() {
        assetSearchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID()));
        if (!ObjectUtils.isEmpty(selectSite)) {
            assetSearchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectSite.getSiteUID(), "1*5+"));
            assetSearchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selectSite.getHospitalUID(), "1*5+"));
            assetSearchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selectSite.getInstitutionUID(), "1*5+"));
        } else {
            assetSearchFilters.removeAll(assetSearchFilters.stream().filter(item -> item.conditionPath.equals("1*5+")).collect(Collectors.toList()));
        }
        if (selected != null) {
            if (null != selected.getAssetGroup()) {
                assetSearchFilters.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ,  selected.getAssetGroup(), "1*"));
            } else {
                assetSearchFilters.removeAll(assetSearchFilters.stream().filter(item -> "assetGroup".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
            }
            if (!StringUtils.isEmpty(selected.getFunctionType())) {
                assetSearchFilters.add(new SearchFilter("functionType", SearchFilter.Operator.LIKE,  selected.getFunctionType(), "1*"));
            } else {
                assetSearchFilters.removeAll(assetSearchFilters.stream().filter(item -> "functionType".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
            }
            if (!StringUtils.isEmpty(selected.getAssetName())) {
                assetSearchFilters.add(new SearchFilter("name", SearchFilter.Operator.LIKE,  selected.getAssetName(), "1*"));
            } else {
                assetSearchFilters.removeAll(assetSearchFilters.stream().filter(item -> "name".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
            }
            if (!StringUtils.isEmpty(selected.getManufacture())) {
                assetSearchFilters.add(new SearchFilter("manufacture", SearchFilter.Operator.LIKE,  selected.getManufacture(), "1*"));
            } else {
                assetSearchFilters.removeAll(assetSearchFilters.stream().filter(item -> "manufacture".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
            }
            if (!StringUtils.isEmpty(selected.getUid())) {
                assetSearchFilters.add(new SearchFilter("assetAchievements", SearchFilter.Operator.EQ,  selected.getUid(), "1*"));
            } else {
                assetSearchFilters.removeAll(assetSearchFilters.stream().filter(item -> "assetAchievements".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
            }
        }
    }

    public void searchAsset() {
        selected.setAchievementsReport(null);
        try {
            assetSearchFilters = new ArrayList<>();
            setAssetSearchFilters();
            assetInfoList = assetInfoRepository.findBySearchFilter(assetSearchFilters);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void prepareCreate() {
        try {
            isDisabel = false;
            this.selected = null;
            this.uploadedFile = null;
            super.prepareCreate();
            selectSite = orgInfoRepository.findByUid(UserContextService.getCurrentUserAccount().getSiteUID());
            assetSearchFilters = new ArrayList<>();
            setAssetSearchFilters();
            assetInfoList = assetInfoRepository.findBySearchFilter(assetSearchFilters);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void prepareEdit(AssetAchievements assetAchievements) {
        isDisabel = true;
        selected = assetAchievements;
        super.prepareEdit();
        selectSite = orgInfoRepository.findByUid(selected.getSiteUID());
        assetSearchFilters = new ArrayList<>();
        setAssetSearchFilters();
        assetInfoList = assetInfoRepository.findBySearchFilter(assetSearchFilters);
        selectedAssetInfo = assetInfoList.get(0);
    }

    public String getOrgFullName(OrgInfo org) {
        if (null == org || org.getId() == -1) {
            return null;
        }
        String name = org.getName();
        return name;
    }

    public void initOrgTree() {
        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(user.getSiteUID(), user.getUid());
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

    public void onSelectOrgForUser(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        selectSite = (OrgInfo) node.getData();
        selected.setSiteUID(selectSite.getSiteUID());
    }

    public List<String> getSupplier(String name){
        return supplierService.getSupplier(name);
    }

    public List<TableDefinition> getTableDefinitionForES() {
        List<SearchFilter> tempFilter = new ArrayList<>();
        tempFilter.add(new SearchFilter("templateType", SearchFilter.Operator.EQ, TableDefinition.TableTemplateType.AssetAchievements.name()));
        tempFilter.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
        tempFilter.add(new SearchFilter("status", SearchFilter.Operator.EQ, "Released"));
        if (selected.getAssetGroup() != null) {
            tempFilter.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, selected.getAssetGroup()));
        }
        return templateDao.findBySearchFilter(tempFilter);
    }

    public String getJsonValueById(String baseId) {
        if (!Strings.isNullOrEmpty(baseId)) {
            TableDefinition schema = templateDao.findByBaseId(baseId);
            return schema == null ? null : schema.getSchema();
        } else {
            return null;
        }
    }

    public void setNull() {
        selected.setAchievementsReport(null);
    }

    public String getEditUrl(String achievementsUid) {
        UnstructuredDataCollection udc = unstructuredDataCollectionRepository.findCKByUIdAssetAchievements(achievementsUid);
        if (null != udc) {
            return "/portal/reporter/EditReport.xhtml?faces-redirect=true&amp;reportId=" + udc.getId() + "&amp;requestSource=assetAchievements";
        }
        return "";
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = !currentPageAssets.stream().anyMatch(item -> !selectedMap.containsKey(item.getUid()) || !selectedMap.get(item.getUid()));
        for (AssetAchievements item : currentPageAssets) {
            selectedMap.put(item.getUid(), !isSelectedAll);
        }
    }

    @Override
    protected GenericRepository<AssetAchievements>  getDAO() {
        return assetAchievementsRepository;
    }
}
