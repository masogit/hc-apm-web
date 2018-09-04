package com.ge.apm.view.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.*;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.pm.V2_PmOrderService;
import com.ge.apm.service.sharedAsset.SharedAssetService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.*;
import com.ge.apm.view.SupplierController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.view.wo.Map2XmlConverter;
import com.ge.apm.view.wo.WorkOrderReportExportController;
import com.ge.apm.web.GbForm;
import com.google.common.base.Strings;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.DefaultMapper;
import javaslang.Tuple2;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.joda.time.DateTime;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetInfoController extends JpaCRUDController<AssetInfo> {
	
	private Logger log = Logger.getLogger(AssetInfoController.class.getName());

    private String gbCodeName;
    private static final long serialVersionUID = -1L;

    AssetInfoRepository dao = null;
    AssetFileImportService importService;

    AssetInfoService assetInfoService;
    
    private SharedAssetService sharedAssetService;

    private UserAccountRepository userDao;

    private QrCodeLibRepository qrcodeDao;

    private BrandRepository brandDao;

    private V2_PmScheduleRepository pmSchDao;
    private ExWorkOrderRepository exWoDao;
    private V2_PmOrderRepository v2_pmOrderRepository;

    private ConfigService configService;

    private boolean resultStatus;

    private UserAccount clinicalOwner;

    private OrgInfo clinicalDept;

    private Supplier supplier;

    private Supplier maintainer;

    private Brand brand;

    private StreamedContent zipFile;

    private StreamedContent excelFile;

    private UserAccount owner;
    private UserAccount owner2;
    private UserAccount owner3;
    //    private List<UserAccount> ownerList;
    private List<OrgInfo> ownerOrgList;

    private List<V2_BlobObject> attachements;
    
    private AutoCompleteService autoCompleteService;

    private BlobFileService blobService;

    private UaaService uaaService;

    private OrgInfoRepository orgDao;

    private BlobObjectRepository blobDao;

    private String operation;

    private AssetDepreciationService assetDepreciationService;
    private V2_PmOrderService pmService;

    private QrCodeService qrCodeService;
    private UserCommonService ucService;
    private AssetCreateService acService;

    private String oneSearchStr;
    private String qrCode;
    Boolean terminate;
    private Integer assetId;

    private List<AssetInfo> parentAssetList;
    private List<I18nMessage> code68List;
    private I18nMessageRepository i18nDao;

    private AssetFinacingNumGenerateService assetFinacingNumGenerateService;

    private String selectedSiteUID;

    private String measuring;

    private Boolean isGeneratePmOrder;

    private List<UserAccount> ownerList;
    private GbCodeService gbCodeService;
    private GbCodeRepository gbDao;
    public List<GbForm> gbFormList = new ArrayList<GbForm>();

    private Map<String, Boolean> selectedMap;

    private List<AssetInfo> currentPageAssets;

    private Boolean isSelectedCurrentPage = false;

    private OrgInfo selectSite;

    private Boolean needShowAssetApproval;

    private OrgInfo fileterOrg;

    private UserContextService userContextService;

    private WorkflowConfigRepository wfCfigDao;

    private SharedAssetTypeRepository swapDao;

    private SharedAssetReqDetailRepository sharedAssetReqDetailRepository;

    List<String> orgUids;
    List<String> swapUids;

    private Integer queryIndex = -1;

    private String exportQrCodeTemplateId;

    private Sort lastPageSort; //download use

    WorkflowConfig config;

    SupplierController supplierController;

    List<Supplier> supplierList = Collections.emptyList();
    
    private List<OrgInfo> deptListCanLeaseAsset;

    private String filterOrgName;

    private StreamedContent exportPdf;

    public Integer getQueryIndex() {
        return queryIndex;
    }

    public void setQueryIndex(Integer queryIndex) {
        this.queryIndex = queryIndex;
    }

    protected void setSelectedByUrlParam(String encodeUrl, String paramName) {
        setSelected(Integer.parseInt((String) UrlEncryptController.getValueFromMap(encodeUrl, paramName)));

        owner = selected.getAssetOwnerId() == null ? null : userDao.findById(selected.getAssetOwnerId());
        owner2 = selected.getAssetOwnerId2() == null ? null : userDao.findById(selected.getAssetOwnerId2());
        owner3 = selected.getAssetOwnerId3() == null ? null : userDao.findById(selected.getAssetOwnerId3());

        if (null != selected.getClinicalOwnerId()) {
            clinicalOwner = userDao.findById(selected.getClinicalOwnerId());
        }

        if (null != selected.getClinicalDeptId()) {
            clinicalDept = orgDao.findById(selected.getClinicalDeptId());
        }

        if (null != selected.getSupplierId()) {
            SupplierRepository supplierDao = WebUtil.getBean(SupplierRepository.class);
            supplier = supplierDao.findById(selected.getSupplierId());
        }

        if (null != selected.getMaitananceId()) {
            SupplierRepository supplierDao = WebUtil.getBean(SupplierRepository.class);
            maintainer = supplierDao.findById(Integer.parseInt(selected.getMaitananceId()));
        }

        qrCode = selected.getQrCode();

        selectedOrg = orgDao.getByUid(selected.getSiteUID());

        setSelectedSiteUID(selected.getSiteUID());

        getOwnerList();
        getClinicalOwnerList();
    }

    private boolean showDialog = false;
    private ServerEventInterface callingController;
    private String updateViewIDs;
    private TreeNode gbCodeTree;

    public void prepareDialogCallback(ServerEventInterface callingController, String updateViewIDs) {
        showDialog = true;

        this.selected = null;
        this.selectedNode = null;
        // this.selectedAsset = null;

        this.callingController = callingController;
        this.updateViewIDs = updateViewIDs;
//        UaaService uaaService = WebUtil.getBean(UaaService.class);

    }

    @Override
    protected void init() {
        pmSchDao = WebUtil.getBean(V2_PmScheduleRepository.class);
        v2_pmOrderRepository = WebUtil.getBean(V2_PmOrderRepository.class);
        dao = WebUtil.getBean(AssetInfoRepository.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        qrcodeDao = WebUtil.getBean(QrCodeLibRepository.class);
        blobDao = WebUtil.getBean(BlobObjectRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        brandDao = WebUtil.getBean(BrandRepository.class);
        wfCfigDao = WebUtil.getBean(WorkflowConfigRepository.class);
        swapDao = WebUtil.getBean(SharedAssetTypeRepository.class);
        sharedAssetReqDetailRepository = WebUtil.getBean(SharedAssetReqDetailRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        qrCodeService = WebUtil.getBean(QrCodeService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        acService = WebUtil.getBean(AssetCreateService.class);
        sharedAssetService = WebUtil.getBean(SharedAssetService.class);
        assetDepreciationService = WebUtil.getBean(AssetDepreciationService.class);
        pmService = WebUtil.getBean(V2_PmOrderService.class);
        terminate = Boolean.valueOf(WebUtil.getRequestParameter("terminate"));
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        ucService = WebUtil.getBean(UserCommonService.class);
        assetFinacingNumGenerateService = WebUtil.getBean(AssetFinacingNumGenerateService.class);
        gbCodeService = WebUtil.getBean(GbCodeService.class);
        gbDao = WebUtil.getBean(GbCodeRepository.class);
        exWoDao = WebUtil.getBean(ExWorkOrderRepository.class);
        configService = WebUtil.getBean(ConfigService.class);
        autoCompleteService=WebUtil.getBean(AutoCompleteService.class);
        importService = WebUtil.getBean(AssetFileImportService.class);
        assetInfoService = WebUtil.getBean(AssetInfoService.class);
        supplierController = WebUtil.getBean(SupplierController.class);

        this.filterBySite = false;
        this.selectedMap = new HashMap();

        UserAccount user = UserContextService.getCurrentUserAccount();
        orgUids = wfCfigDao.findDeptForSharedAsset(user.getSiteUID());
        swapUids = swapDao.findAllUid();

        String encodeStr = WebUtil.getRequestParameter("str");
        this.measuring = WebUtil.getRequestParameter("measuring");
        String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "actionName");
        // String actionName = WebUtil.getRequestParameter("actionName");
        if ("Create".equalsIgnoreCase(actionName)) {
            try {
                prepareCreate();
                selectedOrg = orgDao.getByUid(UserContextService.getCurrentUserAccount().getSiteUID());
                updateSelectedOrg(selectedOrg);
                config = configService.findConfigBySite(selected.getSiteUID());

                getOwnerList();

            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(AssetInfoController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if ("View".equalsIgnoreCase(actionName)) {
            // setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
            String terminateStr = (String) UrlEncryptController.getValueFromMap(encodeStr, "terminate");
            if (terminateStr != null) {
                terminate = Boolean.parseBoolean(terminateStr);
            }
            setSelectedByUrlParam(encodeStr, "selectedid");
            prepareView();
        } else if ("Edit".equalsIgnoreCase(actionName)) {
            //setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
            String terminateStr = (String) UrlEncryptController.getValueFromMap(encodeStr, "terminate");
            if (terminateStr != null) {
                terminate = Boolean.parseBoolean(terminateStr);
            }
            setSelectedByUrlParam(encodeStr, "selectedid");
            if (!StringUtils.isEmpty(selected.getNationalStandardCode())) {
                selectedGb = gbDao.getGbByCode(selected.getNationalStandardCode());
                if (null != selectedGb) {
                    gbCodeName = selectedGb.getCode() + "/" + selectedGb.getName();
                }
            }
            prepareEdit();
            config = configService.findConfigBySite(selected.getSiteUID());
        }
//        else if ("Delete".equalsIgnoreCase(actionName)) {
//            //setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
//            setSelectedByUrlParam(encodeStr, "selectedid");
//            prepareDelete();
//        }

        ownerOrgList = uaaService.getOrgListByHospitalId(UserContextService.getCurrentUserAccount().getHospitalId());
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        if (this.selected != null) {
            initOrgTree();
        }
        initGbTree();

        if (selectedOrg != null) {
            updateParentAssetList();
        }

        supplierList = supplierController.getSupplierList();

    }

    public Boolean getTerminate() {
        return terminate;
    }

    public void setTerminate(Boolean terminate) {
        this.terminate = terminate;
    }

    @Override
    protected AssetInfoRepository getDAO() {
        return dao;
    }

    private void setUserSearchFilters() {
        this.updateSearchFilter(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        if (terminate) {
            this.updateSearchFilter(new SearchFilter("isValid", SearchFilter.Operator.EQ, false));
        } else {
            this.updateSearchFilter(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
        }

        if (!this.hasFilterOnField("siteUID")) {
            UserAccount user = UserContextService.getCurrentUserAccount();
            List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            onSelectSiteTreeNode(null);
        }

        if (oneSearchStr != null && !oneSearchStr.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("name", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("departNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("financingNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("qrCode", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("serialNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("manufacture", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("clinicalDeptName", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
        }

        if (null != fileterOrg) {
            this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, fileterOrg.getUid(), "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "clinicalDeptUID".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }

        if (null != selectSite) {
            this.updateSearchFilter(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> item.conditionPath.equals("1*5+")).collect(Collectors.toList()));
        }

        if ("true".equals(this.measuring)) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(new Date());
            calendar.add(calendar.YEAR, -1);
            calendar.add(calendar.DATE, 60);
            Date remindDate = calendar.getTime();
            searchFilters.add(new SearchFilter("lastMeteringDate", SearchFilter.Operator.NE, null));
            searchFilters.add(new SearchFilter("lastMeteringDate", SearchFilter.Operator.GTE, remindDate));
        } else {
            this.removeFilterOnField("lastMeteringDate");
        }

    }

    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        lastPageSort = pageRequest.getSort(); //download use

        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        if (assetId == null) {
            setUserSearchFilters();
        }

        selected = null;
        List<Integer> stus = new ArrayList<>();
        stus.add(1);
        stus.add(3);

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String p=request.getServletPath();
        if(p.equalsIgnoreCase("/portal/BorrowApplyV2/StockOfAsset.xhtml") || p.equalsIgnoreCase("/portal/BorrowApplyV2/ListForQuery.xhtml")){
            this.updateSearchFilter(new SearchFilter("sharedAssetTypeUid", SearchFilter.Operator.IN, swapUids));
        }
        if(p.equalsIgnoreCase("/portal/BorrowApplyV2/ListForQuery.xhtml")){
            searchFilters.add(new SearchFilter("assetDeptUID", SearchFilter.Operator.NotIN, orgUids));
        }
        
        //1st. if asset belongs to any distribution center
        searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUids));
        //2nd, check if configuration regarding to shared asset has been created
        searchFilters.add(new SearchFilter("sharedAssetTypeUid", SearchFilter.Operator.IsNotNull, null));

        if (queryIndex == 1) {
            //正常可借用
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.IN,stus));
            this.updateSearchFilter(new SearchFilter("assetDeptUID", SearchFilter.Operator.IN, orgUids));
        } else if(queryIndex == 2){
            //正常已借出
            //JIRA#2898
            //searchFilters.add(new SearchFilter("status", SearchFilter.Operator.IN, stus));
            this.updateSearchFilter(new SearchFilter("assetDeptUID", SearchFilter.Operator.NotIN, orgUids));
        }else if(queryIndex == 3){
            //故障不可借用
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.NotIN, stus));
        }


        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            Page<AssetInfo> res = dao.findBySearchFilter(this.searchFilters, pageRequest);
            currentPageAssets = res.getContent();
            for(AssetInfo info : currentPageAssets) {
                //Since can't use fetch mode in Lazy, so load mapped objects by standalone search
                SharedAssetType sharedAssetType = findSharedAssetType(info);
                info.setSharedAssetType(sharedAssetType);
            	SharedAssetReqDetail reqDetail = selectProperReqDetail(info);
            	info.setSharedAssetReqDetail(reqDetail);
            }
            return res;
        }
    }

    @Override
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
        this.selectedMap.clear();
    }

    public void onSelectSiteTreeNode(NodeSelectEvent event) {

        if (null != event) {
            if (null != event.getTreeNode() && null != event.getTreeNode().getData()) {
                selectSite = (OrgInfo) event.getTreeNode().getData();
            } else {
                selectSite = null;
            }
            onClinicalDeptFilter(null);
        }

    }

    public void exportQrCode() throws Exception {
        List<AssetInfo> exportList = getExportList();
        zipFile = qrCodeService.exportQrCodeListByAssets(exportList.stream().filter(item -> !Strings.isNullOrEmpty(item.getQrCode())).collect(Collectors.toList()), exportQrCodeTemplateId);
    }

    public void onSelectAllOfPage() {
        for (AssetInfo item : currentPageAssets) {
            selectedMap.put(item.getUid(), this.isSelectedCurrentPage);
        }
    }

    public void onItemSelected() {
//        isSelectedCurrentPage = !currentPageAssets.stream().anyMatch(item -> !selectedMap.containsKey(item.getUid()) || !selectedMap.get(item.getUid()));
    }

    public List<AssetInfo> getExportList() {
        List<String> assetUids = selectedMap.entrySet().stream().filter(item -> item.getValue()).map(item -> item.getKey()).collect(Collectors.toList());
        return assetUids.isEmpty() ? dao.findBySearchFilter(searchFilters) : dao.findByUidIn(assetUids);
    }

    public void onSelectAsset() throws IOException {
        String url = "actionName=View&selectedid="
                + selected.getId().toString()
                + "&asset_name=" + selected.getName()
                + "&terminate=" + this.terminate;
        url = "Detail.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        //FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public String getViewPage(String pageName, String actionName) {
        operation = pageName + "?actionName=" + actionName + "&selectedid=" + selected.getId();
        return operation;
    }

    public String getDetailPage() {
        operation = "Detail.xhtml?actionName=View&selectedid=" + selected.getId()
                + "&asset_name=" + selected.getName();
        return operation;
    }

    public String getDetailPage(String assetId, String assetName) {
        operation = "Detail.xhtml?actionName=View&selectedid=" + assetId
                + "&asset_name=" + assetName
                + "&terminate=" + this.terminate;
        return UrlEncryptController.encodeUrlParam(operation);
    }

    public String getEditPage(String assetId, String assetName) {
        operation = "Create.xhtml?actionName=Edit&selectedid=" + assetId
                + "&asset_name=" + assetName
                + "&terminate=" + this.terminate;
        return UrlEncryptController.encodeUrlParam(operation);
    }

    public void assembleMaintainData(String assetUid) {
        operation = "/portal/woMgr/workOrderList?faces-redirect=true&assetUID=" + assetUid;
    }

    public void assembleMaintainList() {
        operation = "/portal/woMgr/workOrderList?faces-redirect=true";
        // operation = "/portal/woMgr/woMgrList?faces-redirect=true";
    }

    public String getContractListLink() {
        return "/portal/asset/contract/List?assetId=" + selected.getId();
    }

    public String getContractAddLink() {
        return "/portal/asset/contract/List?assetId=" + selected.getId() + "&actionName=Create";
    }

    @Override
    public void onBeforeNewObject(AssetInfo object) {

        assignUserOrg(object);

        object.setIsValid(true);
        object.setStatus(1);
        object.setDeviceStatus(0);
        object.setIsDeleted(false);

    }

    private void assignUserOrg(AssetInfo assetInfo) {
        assetInfo.setSiteId(UserContextService.getSiteId());
        assetInfo.setHospitalId(UserContextService.getCurrentUserAccount().getHospitalId());

        assetInfo.setTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());
        assetInfo.setHospitalUID(UserContextService.getCurrentUserAccount().getHospitalUID());
        assetInfo.setInstitutionUID(UserContextService.getCurrentUserAccount().getInstitutionUID());
        assetInfo.setSiteUID(UserContextService.getCurrentUserAccount().getSiteUID());
    }

    @Override
    public void onBeforeSave(AssetInfo object) {
        if (null != object.getClinicalDeptId()) {
            OrgInfo org = orgDao.findById(object.getClinicalDeptId());
            object.setClinicalDeptName(org.getName());

//            object.setTenantUID(org.getTenantUID());
//            object.setHospitalUID(org.getHospitalUID());
//            object.setInstitutionUID(org.getInstitutionUID());
//            object.setSiteUID(org.getSiteUID());
            object.setClinicalDeptUID(org.getUid());

        } else {
            //clear uid and its name
            object.setClinicalDeptUID(null);
            object.setClinicalDeptName(null);
        }
        DateTime temp;
        if (null != object.getManufactDate()) {
            temp = new DateTime(object.getManufactDate());
            object.setManufactDate(temp.plusHours(12).toDate());
        }
        if (null != object.getPurchaseDate()) {
            temp = new DateTime(object.getPurchaseDate());
            object.setPurchaseDate(temp.plusHours(12).toDate());
        }
        if (null != object.getArriveDate()) {
            temp = new DateTime(object.getArriveDate());
            object.setArriveDate(temp.plusHours(12).toDate());
        }
        if (null != object.getInstallDate()) {
            temp = new DateTime(object.getInstallDate());
            object.setInstallDate(temp.plusHours(12).toDate());
        }
        if (null != object.getWarrantyDate()) {
            temp = new DateTime(object.getWarrantyDate());
            object.setWarrantyDate(temp.plusHours(12).toDate());
        }
        if (null != object.getTerminateDate()) {
            temp = new DateTime(object.getTerminateDate());
            object.setTerminateDate(temp.plusHours(12).toDate());
        }

        Date now = new Date();

        if (object.getId() == null) {
            object.setCreatedBy(UserContextService.getCurrentUserAccount().getLoginName());
            object.setCreatedDate(now);
            object.setLastModifiedBy(UserContextService.getCurrentUserAccount().getLoginName());
            object.setLastModifiedDate(now);
        } else {
            object.setLastModifiedBy(UserContextService.getCurrentUserAccount().getLoginName());
            object.setLastModifiedDate(now);
        }
    }

    public Boolean needAssetApproval(String siteUid) {
        return acService.needAssetApproval(siteUid);
    }

    public Boolean needShowAssetApprovalByUser() {
        return acService.needShowAssetApprovalByUser(UserContextService.getCurrentUserAccount().getUid());
    }

    @Override
    public void onAfterUpdateObject(AssetInfo object, boolean isOK) {
        resultStatus = isOK;
    }

    @Override
    public void onAfterNewObject(AssetInfo object, boolean isOK) {
        resultStatus = isOK;
        if (isOK) {
            attachements.forEach(item -> {
                if (item.getBoId() == null) {
                    item.setBoId(object.getId());
                    item.setBoUuid(object.getUid());
                    blobDao.save(item);
                }
            });
        }
    }

    @Override
    public void onAfterDeleteObject(AssetInfo object, boolean isOK) {
        resultStatus = isOK;
    }

    public void renewQrcode() {
        QrCodeLib qrcodeLib = qrCodeService.createOneQrCode(selected.getTenantUID(), selected.getSiteUID(), 1);
        qrCode = qrcodeLib.getQrCode();
//        selected.setQrCode(qrcodeLib.getQrCode());
        onDepartNumChange("qrCode");
    }

    private boolean updateQrCode() {
        QrCodeLib qrCodeLib = qrcodeDao.findByQrCode(qrCode, selected.getSiteUID());
        if (qrCodeLib == null) {
            WebUtil.addErrorMessage(WebUtil.getMessage("InvalidQRCode"));
            return false;
        }
        if (!qrCodeLib.getSiteUID().equals(selected.getSiteUID())) {
            WebUtil.addErrorMessage(WebUtil.getMessage("WrongHospitalQRCode"));
            return false;
        }
        if (qrCodeLib.getStatus() != 1) {
            WebUtil.addErrorMessage(WebUtil.getMessage("AlreadyUsingQrCode"));
            return false;
        }

        QrCodeLib oldQRCodeLib = qrcodeDao.findByQrCode(selected.getQrCode(), selected.getSiteUID());
        if (null != oldQRCodeLib) {
            qrCodeService.discardQrCode(qrCode, selected.getSiteUID());
        }
        qrCodeLib.setStatus(3);
        qrcodeDao.save(qrCodeLib);
        selected.setQrCode(qrCode);
        return true;
    }

    public String submitApproval() {
        selected.setUsageStatus(2);
        return applyChange();
    }

    @Transactional
    public String applyChange() {
        if (!isTimeValidate()) {
            return "";
        }
        if (selected.getUsageStatus() == null) {
            selected.setUsageStatus(3);
        }
        if (Strings.isNullOrEmpty(qrCode) || "".equals(qrCode.trim())) {
            selected.setQrCode(null);
        } else {
            if (!qrCode.equals(selected.getQrCode())) {
                if ("DFQ".equals(getAssetCodeConfig())) {
                    QrCodeLib qrCodeLib = qrcodeDao.findByQrCode(qrCode, selected.getSiteUID());
                    if (qrCodeLib == null) {
                        if (!qrCodeService.createSpecialQrCode(qrCode, selected.getSiteUID())) {
                            WebUtil.addErrorMessage("二维码不正确");
                            return "";
                        };
                    }
                    qrCodeService.forceRemoveQrcode(selected.getQrCode(), selected.getSiteUID());
                    selected.setQrCode(null);
                }
                if (!updateQrCode()) {
                    qrCode = "";
                    return "";
                }
            }

        }
        if (null != gbCodeName) {
            GbCode gbCode = gbDao.getGbCode(gbCodeName.split("/")[0]);
            if (null == gbCode) {
                WebUtil.addErrorMessage(WebUtil.getMessage("无效的国标码"));
                return "";
            }

            selected.setNationalStandardCode(selectedGb.getCode());
        }

        Boolean isNew = selected.hashCode() == 0;
        this.save();
        assetDepreciationService.saveAssetDerpeciation(selected);
        if (isGeneratePmOrder != null && isGeneratePmOrder && isNew) {
            if (Strings.isNullOrEmpty(selected.getClinicalDeptUID())) {
                WebUtil.putGrowlMsg("使用科室未填，不能生成保养工单");
            } else {
                pmService.createPMrequest(selected.getUid(), null);
            }
        }
        if (resultStatus) {
            return getListPageLink();
        } else {
            return "";
        }
    }

    public void showMsg() {
        String pageMsg = WebUtil.getGrowlMsg();
        if (!Strings.isNullOrEmpty(pageMsg)) {
            WebUtil.addErrorMessage(pageMsg);
        }
    }

    public String getListPageLink() {
        return "List?faces-redirect=true" + (this.isTerminate() ? "&terminate=true" : "");
    }

    public String setValidButton() {
        if (terminate) {
            this.selected.setIsValid(true);
            this.selected.setTerminateDate(null);
        } else {
            int workOrdersNum = dao.fetchCurrentWorkOrders(selected.getId());
            if (workOrdersNum > 0) {
                WebUtil.addErrorMessage("当前设备存在未完成工单，不能报废！");
                return null;
            } else {
                this.selected.setIsValid(false);
                this.selected.setTerminateDate(new Date());
            }
        }
        update();
        return "List?faces-redirect=true" + (terminate ? "&terminate=true" : "");
    }

    public UserAccount getOwner() {
        return owner;
    }

    public List<OrgInfo> getOwnerOrgList() {
        return ownerOrgList;
    }

    public void setOwner(UserAccount owner) {
        this.owner = owner;
    }

    public void onOwnerChange(SelectEvent event) {

        if (null != owner) {
            selected.setAssetOwnerId(owner.getId());
            selected.setAssetOwnerName(owner.getName());
            selected.setAssetOwnerTel(owner.getTelephone());
        } else {
            selected.setAssetOwnerId(null);
            selected.setAssetOwnerName(null);
            selected.setAssetOwnerTel(null);
        }

        if (null != owner2) {
            selected.setAssetOwnerId2(owner2.getId());
            selected.setAssetOwnerName2(owner2.getName());
            selected.setAssetOwnerTel2(owner2.getTelephone());
        } else {
            selected.setAssetOwnerId2(null);
            selected.setAssetOwnerName2(null);
            selected.setAssetOwnerTel2(null);
        }

        if (null != owner3) {
            selected.setAssetOwnerId3(owner3.getId());
            selected.setAssetOwnerName3(owner3.getName());
            selected.setAssetOwnerTel3(owner3.getTelephone());
        } else {
            selected.setAssetOwnerId3(null);
            selected.setAssetOwnerName3(null);
            selected.setAssetOwnerTel3(null);
        }

    }

    public void onClinicalDeptChange() {

        if (clinicalDept == null) {
            selected.setClinicalDeptId(null);
        } else {
            selected.setClinicalDeptId(clinicalDept.getId());
        }

        this.clinicalOwner = null;
        selected.setClinicalOwnerId(null);
        selected.setClinicalOwnerName(null);
        selected.setClinicalOwnerTel(null);

        if (null != selected.getClinicalDeptId()) {
            OrgInfo org = orgDao.findById(selected.getClinicalDeptId());
            selected.setClinicalDeptName(org.getName());
            selected.setClinicalDeptUID(org.getUid());
        } else {
            selected.setClinicalDeptName(null);
            selected.setClinicalDeptUID(null);
        }

        updateParentAssetList();

        getClinicalOwnerList();
    }

    public void onClinicalOwnerChange() {
        if (null != clinicalOwner) {
            selected.setClinicalOwnerId(clinicalOwner.getId());
            selected.setClinicalOwnerName(clinicalOwner.getName());
            selected.setClinicalOwnerTel(clinicalOwner.getTelephone());
        } else {
            selected.setClinicalOwnerId(null);
            selected.setClinicalOwnerName(null);
            selected.setClinicalOwnerTel(null);
        }
    }
    
    public List<Supplier> autoCompleteSupplier(String query) {
        return autoCompleteService.autoCompleteVendor(query, supplierList);
    }

    public List<Supplier> autoCompleteMaintainer(String query) {
        return autoCompleteService.autoCompleteMaintainer(query, supplierList);
    }

    public void onSupplierChange() {

        if (null != supplier) {
            selected.setSupplierId(supplier.getId());
            selected.setVendor(supplier.getName());
        }
        else{
        	selected.setSupplierId(null);
            selected.setVendor(null);
        }
    }

    public void onMaintainerChange() {
        if (null != maintainer) {
            selected.setMaitananceId(maintainer.getId()+"");
            selected.setMaitanance(maintainer.getName());
        }
        else{
        	selected.setMaitananceId(null);
            selected.setMaitanance(null);
        }
    }

    public void onBrandChange() {
        if (null != brand) {
            selected.setBrand(brandDao.findById(brand.getId()));
        }
    }

    public void handleFileUpload(FileUploadEvent event) {

        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            String type = event.getComponent().getId();
            type = type.substring(type.indexOf("type") + 4);
            attachements.add(blobObject);
            blobObject.setBoId(selected.getId());
            blobObject.setBoType(V2_BlobObject.BO_TYPE_AssetInfo);
            blobObject.setBoSubType(type);
            blobObject.setBoUuid(selected.getUid());
            blobDao.save(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }

    }

    public List<V2_BlobObject> getAttachList(String assetuid, String type) {
        if (attachements == null || attachements.isEmpty()) {
            List<SearchFilter> sfs = new ArrayList();
            //sfs.addAll(this.searchFilters);
            sfs.add(new SearchFilter("boUuid", SearchFilter.Operator.EQ, assetuid));
            attachements = blobDao.findBySearchFilter(sfs);
        }
        return attachements.stream().filter(item -> item.getBoSubType() != null && item.getBoSubType().equals(type)).collect(Collectors.toList());
    }

    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        attachements = attachements.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toList());
    }

    public String getHospitalName(String siteUid) {
        OrgInfo org = orgDao.getByUid(siteUid);
        return org == null ? "" : org.getName();
//        return acService.getSiteName(siteUid);
    }

    public void onHospitalChange() {
        OrgInfo org = orgDao.getByUid(selected.getSiteUID());
        selected.setInstitutionUID(org.getInstitutionUID());
        selected.setHospitalUID(org.getHospitalUID());
        this.owner = null;
    }

    public List<UserAccount> getOwnerList() {
        if (ownerList == null) {
            ownerList = ucService.getWorkerList(selected.getId(),"");
        }
        return ownerList;
    }

    public List<UserAccount> completeOwnerList(String query) {
        return autoCompleteService.completeOwnerList(query, ownerList);
    }

    public List<UserAccount> completeClinicalOwnerList(String query) {

        if (clinicalOwnerList == null) return Collections.emptyList();;

        List<UserAccount> res = this.clinicalOwnerList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());

        res.sort(new PinYinComparatorByName());
        return res;
    }

    private List<UserAccount> getEmptyAccountList() {
        List<UserAccount> accountList = new ArrayList();
        UserAccount emptyValue = new UserAccount();
        emptyValue.setName(WebUtil.getMessage("selectOneMessage"));
        accountList.add(emptyValue);
        return accountList;
    }

    private List<UserAccount> clinicalOwnerList;

    public void getClinicalOwnerList() {
        if (null != selected.getClinicalDeptUID()) {
            clinicalOwnerList = userDao.findUsersByOrgIdExceptTemporaryAccount(selected.getClinicalDeptUID());
        }
    }

    public String getQrCodeImageBase64() {
        return qrCodeService.getQrCodeImageBase64(selected);
    }

    public boolean isTimeValidate() {
        String message = WebUtil.getMessage("shouldEarly"); //"{0} must before {1}!";
        AssetInfo input = this.selected;
        boolean isError = false;

        if (null != input.getManufactDate() && null != input.getPurchaseDate() && input.getManufactDate().after(input.getPurchaseDate())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("manufactDate"), WebUtil.getMessage("purchaseDate")));
        }

        if (null != input.getArriveDate() && null != input.getPurchaseDate() && input.getPurchaseDate().after(input.getArriveDate())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("purchaseDate"), WebUtil.getMessage("arriveDate")));
        }

        if (null != input.getArriveDate() && null != input.getInstallDate() && input.getArriveDate().after(input.getInstallDate())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("arriveDate"), WebUtil.getMessage("installDate")));
        }
        if (null != input.getWarrantyDate() && null != input.getInstallDate() && input.getWarrantyDate().before(input.getInstallDate())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("installDate"), WebUtil.getMessage("warrantyDate")));
        }
        if (null != input.getTerminateDate() && null != input.getInstallDate() && input.getTerminateDate().before(input.getInstallDate())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("installDate"), WebUtil.getMessage("terminateDate")));
        }
        if (null != input.getLastPmDate() && null != input.getInstallDate() && input.getLastPmDate().before(input.getInstallDate())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("installDate"), WebUtil.getMessage("lastPmDate")));
        }
        if (null != input.getLastMeteringDate() && null != input.getInstallDate() && input.getLastMeteringDate().before(input.getInstallDate())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("installDate"), WebUtil.getMessage("lastMeteringDate")));
        }
        if (null != input.getLastQaDate() && null != input.getInstallDate() && input.getLastQaDate().before(input.getInstallDate())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("installDate"), WebUtil.getMessage("lastQaDate")));
        }

        return !isError;
    }

    private String filterAssetStatus = null;

    public String getFilterAssetStatus() {
        return filterAssetStatus;
    }

    public void setFilterAssetStatus(String filterAssetStatus) {
        this.filterAssetStatus = filterAssetStatus;
    }

    public void setAssetStatusFilter() {
        if (filterAssetStatus == null && filterWarrantyDate == null) {
            return;
        }
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        if (filterAssetStatus != null) {
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, filterAssetStatus));
        }
        /*if (assetId != null) {
            searchFilters.add(new SearchFilter("id", SearchFilter.Operator.EQ, assetId));
        }*/
        if (filterWarrantyDate != null && "1".equals(filterWarrantyDate)) {
            Date warrantyDate = new Date();
            //2个月内，则是保修到期时间在2个月后时间之前所有
            Calendar c = Calendar.getInstance();
            c.setTime(warrantyDate);
            c.add(Calendar.MONTH, 2);
            varWarrantyDateTo = c.getTime();
            searchFilters.add(new SearchFilter("warrantyDate", SearchFilter.Operator.LTE, varWarrantyDateTo));
            searchFilters.add(new SearchFilter("isWarranty", SearchFilter.Operator.EQ, true));
            searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
            searchFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getSiteId()));
            searchFilters.add(new SearchFilter("hospitalId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getHospitalId()));
            //filterWarrantyDate = null;
        }
    }

    public void deleteAsset(AssetInfo asset) {
        ServiceRequestRepository srDao = WebUtil.getBean(ServiceRequestRepository.class);
        List<V2_ServiceRequest> srlist = srDao.findByAssetId(asset.getId());
        if (srlist != null && !srlist.isEmpty()) {
            WebUtil.addErrorMessage("该设备有工单存在,不能删除");
            return;
        }
        V2_PmOrderRepository pmDao = WebUtil.getBean(V2_PmOrderRepository.class);
        List<SearchFilter> pmFilter = Arrays.asList(new SearchFilter("assetUid", SearchFilter.Operator.EQ, asset.getUid()),
                new SearchFilter("isFinished", SearchFilter.Operator.EQ, false));
        List<V2_PmOrder> pmList = pmDao.findBySearchFilter(pmFilter);
        if (pmList != null && !pmList.isEmpty()) {
            WebUtil.addErrorMessage("该设备有保养工单存在,不能删除");
            return;
        }

        if (asset.getQrCode() != null) {
            qrCodeService.releaseQrCode(asset.getQrCode(), asset.getSiteUID());
        }
        asset.setIsDeleted(true);
        asset.setIsValid(false);
        asset.setQrCode(null);
        dao.save(asset);
    }

    private String filterWarrantyDate = null;

    public String getFilterWarrantyDate() {
        return filterWarrantyDate;
    }

    public void setFilterWarrantyDate(String filterWarrantyDate) {
        this.filterWarrantyDate = filterWarrantyDate;
    }

    private Date varWarrantyDateTo = null;
    private String warrantyFormatTime = null;

    public Date getVarWarrantyDateTo() {
        return varWarrantyDateTo;
    }

    public void setVarWarrantyDateTo(Date varWarrantyDateTo) {
        this.varWarrantyDateTo = varWarrantyDateTo;
    }

    public String getWarrantyFormatTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        if (varWarrantyDateTo == null) {
            return warrantyFormatTime;
        } else {
            return "~" + sdf.format(varWarrantyDateTo);
        }
    }

    public void setWarrantyFormatTime(String warrantyFormatTime) {
        this.warrantyFormatTime = warrantyFormatTime;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Boolean isTerminate() {
        if (null != selected) {
            return !selected.getIsValid();
        }
        return terminate;
    }

    public UserAccount getClinicalOwner() {
        return clinicalOwner;
    }

    public void setClinicalOwner(UserAccount clinicalOwner) {
        this.clinicalOwner = clinicalOwner;
    }

    public OrgInfo getClinicalDept() {
        return clinicalDept;
    }

    public void setClinicalDept(OrgInfo clinicalDept) {
        this.clinicalDept = clinicalDept;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Supplier getMaintainer() {
        return maintainer;
    }

    public void setMaintainer(Supplier maintainer) {
        this.maintainer = maintainer;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public UserAccount getOwner2() {
        return owner2;
    }

    public void setOwner2(UserAccount owner2) {
        this.owner2 = owner2;
    }

    public UserAccount getOwner3() {
        return owner3;
    }

    public void setOwner3(UserAccount owner3) {
        this.owner3 = owner3;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getOrgName(Integer orgId) {
        OrgInfo org = orgDao.findById(orgId);
        if (org != null) {
            return org.getName();
        }
        return null;
    }

    public String getSiteOrgName(String siteUID) {
        OrgInfo org = orgDao.getByUid(siteUID);
        if (org != null) {
            return org.getName();
        }
        return null;
    }

    public String getAssetDetailTitle() {
        return terminate ? WebUtil.getMessage("TerminateAssetInfoDetail") : WebUtil.getMessage("AssetInfoDetail");
    }

    public String getAssetEditTitle() {
        return terminate ? WebUtil.getMessage("TerminateAssetInfo") : WebUtil.getMessage("AssetInfo");
    }

    public String getAssetListTitle() {
        return terminate ? WebUtil.getMessage("TerminateAssetInfo") : WebUtil.getMessage("AssetInfo");
    }

    public String getButtonName() {
        return terminate ? WebUtil.getMessage("ActiveAsset") : WebUtil.getMessage("InactiveAsset");
    }

    public void updateParentAssetList() {

        List<SearchFilter> assetFilters = new ArrayList<>();

        assetFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        assetFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, this.selected.getSiteId()));

        if (this.selected.getClinicalDeptUID() != null) {
            assetFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, this.selected.getClinicalDeptUID()));
        }

        if (this.selected.getSiteUID() != null) {
            assetFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, this.selected.getSiteUID()));
        }

        if (this.selected.getUid() != null) {
            assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.NE, this.selected.getUid()));
        }

        parentAssetList = dao.findBySearchFilter(assetFilters);

    }

    public void onFunctionGroupChange() {
        code68List = new ArrayList<>();
        this.selected.setAssetCode68(null);

        this.changeFunctionGroup();
    }

    public void changeFunctionGroup() {
        Integer functionGroupId = this.selected.getFunctionGroup();
        if (functionGroupId != null) {
            I18nMessage functionGroup = i18nDao.getByMsgTypeAndMsgKey("assetFunctionType", functionGroupId.toString());
            if (functionGroup != null) {
                code68List = i18nDao.getByMsgType(functionGroup.getMsgKey());
            }
        }
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();

        changeFunctionGroup();
    }

    public List<AssetInfo> getParentAssetList() {
        return parentAssetList;
    }

    public void setParentAssetList(List<AssetInfo> parentAssetList) {
        this.parentAssetList = parentAssetList;
    }

    public String getParentAssetName() {
        if (this.selected.getParentAssetId() != null) {
            AssetInfo assetInfo = this.dao.getById(this.selected.getParentAssetId());
            if (assetInfo != null) {
                if (StringUtils.isEmpty(assetInfo.getFunctionType())) {
                    return assetInfo.getName();
                } else {
                    return assetInfo.getName() + "(" + assetInfo.getFunctionType() + ")";
                }
            }
        }
        return "";
    }

    public String getParentAssetNameByAssetId(int assetId) {
        AssetInfo ai = dao.findById(assetId);
        if (ai.getParentAssetId() != null) {
            AssetInfo assetInfo = dao.getById(ai.getParentAssetId());
            if (assetInfo != null) {
                if (StringUtils.isEmpty(assetInfo.getFunctionType())) {
                    return assetInfo.getName();
                } else {
                    return assetInfo.getName() + "(" + assetInfo.getFunctionType() + ")";
                }
            }
        }
        return "";
    }

    public List<I18nMessage> getCode68List() {
        return code68List;
    }

    public void setCode68List(List<I18nMessage> code68List) {
        this.code68List = code68List;
    }

    public StreamedContent getZipFile() {
        return zipFile;
    }

    public void setZipFile(StreamedContent zipFile) {
        this.zipFile = zipFile;
    }

    public String getOneSearchStr() {
        return oneSearchStr;
    }

    public void setOneSearchStr(String oneSearchStr) {
        this.oneSearchStr = oneSearchStr;
        if (oneSearchStr.isEmpty()) {
            this.removeFilterOnField("name");
            this.removeFilterOnField("departNum");
            this.removeFilterOnField("financingNum");
            this.removeFilterOnField("qrCode");
            this.removeFilterOnField("serialNum");
            this.removeFilterOnField("manufacture");
            this.removeFilterOnField("clinicalDeptName");
        }
    }

    public List<Brand> getBrandList() {
        return brandDao.find();
    }

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    private TreeNode orgTree;
    private TreeNode gbTree;

    private TreeNode selectedNode;
    private TreeNode selectedGbNode;

    private GbCode selectedGb;
    private OrgInfo selectedOrg;

    List<OrgInfo> clinicalDeptList;

    public TreeNode getSelectedGbNode() {
        return selectedGbNode;
    }

    public void setSelectedGbNode(TreeNode selectedGbNode) {
        this.selectedGbNode = selectedGbNode;
    }

    public TreeNode getGbTree() {
        return gbTree;
    }

    public void setGbTree(TreeNode gbTree) {
        this.gbTree = gbTree;
    }

    public TreeNode getOrgTree() {
        return orgTree;
    }

    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();

        updateSelectedOrg(orgInfo);

        updateParentAssetList();

        clinicalDeptList = assetInfoService.getClinicalDeptList(this.selectedSiteUID);

    }

    public void onSelectGbTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        GbCode gbCode = (GbCode) node.getData();
        updateSelectedOrgGb(gbCode);

//        AssetSeriaNumConfig config = assetFinacingNumGenerateService.findConfigByUid(this.selected.getTenantUID(), this.selected.getSiteUID());
//
//        if (config == null || config.getGenerateMethod() == null || config.getGenerateMethod().intValue() != 1) {
//            //not configured to generate by national standard code
//            return;
//        }
////联动更新
//        if (null != selectedGb) {
//
//            String financingNum = assetFinacingNumGenerateService.autoGenerateFinacingNum(
//                    this.selected.getId(),
//                    this.selectedGb.getCode(),
//                    this.selected.getSiteUID(),
//                    this.selected.getParentAssetId(),
//                    this.selected.getFinancingNum()
//            );
//            if (financingNum != null && !financingNum.startsWith("error:")) {
//                this.selected.setFinancingNum(financingNum);
//            }
//
//        }
    }

    public void generateFinancialNum() {

        //check whether generation policy is configured.
//        AssetSeriaNumConfig config = assetFinacingNumGenerateService.findConfigByUid(this.selected.getTenantUID(), this.selected.getSiteUID());
//
//        if (config == null || config.getGenerateMethod() == null) {
//            return;
//        }
        String code = this.selectedGb == null ? null : this.selectedGb.getCode();

        String financingNum = assetFinacingNumGenerateService.autoGenerateFinacingNum(
                this.selected.getId(),
                code,
                this.selected.getSiteUID(),
                this.selected.getParentAssetId(),
                this.selected.getFinancingNum()
        );
        if (financingNum != null) {
            if (financingNum.startsWith("error:")) {
                WebUtil.addErrorMessage(financingNum.replace("error:", ""));
            } else {
                this.selected.setFinancingNum(financingNum);
                onDepartNumChange("financingNum");
            }

        }

    }

    public void onParentAssetChange() {

        WorkflowConfig config = configService.findConfigBySite(this.selected.getSiteUID());

        if (config == null || config.getGenerateMethod() == null || config.getGenerateMethod().intValue() != 2) {
            //not configured to generate by parent asset
            return;
        }

        String code = this.selectedGb == null ? null : this.selectedGb.getCode();

        if (this.selected.getParentAssetId() != null) {
            String financingNum = assetFinacingNumGenerateService.autoGenerateFinacingNum(
                    this.selected.getId(),
                    code,
                    this.selected.getSiteUID(),
                    this.selected.getParentAssetId(),
                    this.selected.getFinancingNum()
            );
            if (financingNum != null && !financingNum.startsWith("error:")) {
                this.selected.setFinancingNum(financingNum);
            }

        }

    }

    private void updateSelectedOrg(OrgInfo orgInfo) {

        if (orgInfo == null || orgInfo.getId() == -1) {

            //revert to current user's org if org is not selected by the user
            assignUserOrg(selected);

            selectedOrg = null;
            setSelectedSiteUID(null);
        } else {
            selectedOrg = orgInfo;

            selected.setTenantUID(selectedOrg.getTenantUID());
            selected.setInstitutionUID(selectedOrg.getInstitutionUID());
            selected.setHospitalUID(selectedOrg.getHospitalUID());
            selected.setSiteUID(selectedOrg.getSiteUID());
            selected.setHospitalId(selectedOrg.getId());

            setSelectedSiteUID(selectedOrg.getUid());
            config = configService.findConfigBySite(selected.getSiteUID());
        }

    }

    private void updateSelectedOrgGb(GbCode gbCode) {
        if (gbCode == null || gbCode.getId() == -1) {
            selectedGb = null;
        } else {
            selectedGb = gbCode;
            gbCodeName = gbCode.getCode() + "/" + gbCode.getName();
        }

    }

    public void initGbTree() {
        TreeNode treeNode = gbCodeService.buildGbTree();
        gbTree = new DefaultTreeNode("root", null, null);
        gbTree.getChildren().add(treeNode);
        treeNode.setExpanded(true);
    }

    public void initOrgTree() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();

        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(loginUser.getSiteUID(), loginUser.getUid());
//        orgTree = new DefaultTreeNode("root",null,null);
//        orgTree.getChildren().add(treeNode);
//        orgTree.setExpanded(true);

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

    public void updateFinancingNumByNationalStandardCode() {
        if (null != selected && null != selectedGb) {
            String financingNum = assetFinacingNumGenerateService.autoGenerateFinacingNum(
                    this.selected.getId(),
                    this.selectedGb.getCode(),
                    this.selected.getSiteUID(),
                    null,
                    null
            );
            if (financingNum != null && StringUtils.isEmpty(this.selected.getFinancingNum())) {
                this.selected.setFinancingNum(financingNum);
            }
        }

    }

    public void onClinicalDeptFilter(NodeSelectEvent event) {

        this.selectedMap.clear();
        if (null != event && null != event.getTreeNode() && null != event.getTreeNode().getData()) {
            OrgInfo deptOrg = (OrgInfo) event.getTreeNode().getData();
            if (deptOrg.getOrgType() == 4) {
                fileterOrg = deptOrg;
                return;
            }
        }
        fileterOrg = null;
    }

    public void discard(AssetInfo asset) {
        if (asset != null && asset.getUsageStatus() == 3) {
            asset.setIsValid(false);
            asset.setTerminateDate(new Date());
            this.selected = dao.save(asset);
            WebUtil.addSuccessMessage("报废成功！");
        } else {
            WebUtil.addSuccessMessage("只能报废待报废状态的资产！");
        }
    }

    public void readyForDiscard(AssetInfo asset) {
        if (asset != null && asset.getUsageStatus() == 2) {
            asset.setStatus(4);
            this.selected = dao.save(asset);
            WebUtil.addSuccessMessage("已将状态修改为待报废！");
        } else {
            WebUtil.addSuccessMessage("只能待报废停用状态的资产！");
        }
    }

    public String getSelectedSiteUID() {
        return selectedSiteUID;
    }

    public void setSelectedSiteUID(String selectedSiteUID) {
        this.selectedSiteUID = selectedSiteUID;

        clinicalDeptList = assetInfoService.getClinicalDeptList(this.selectedSiteUID);
    }

    public List<String> completeAssetName(String prefix) {
        String tenantUID = UserContextService.getCurrentUserAccount().getTenantUID();
        List<String> names = null;
        if (StringUtils.isEmpty(prefix)) {
            names = dao.findDistinctAssetNames(tenantUID);
        } else {
            names = dao.findDistinctAssetNamesByPrefix(tenantUID, prefix);
        }

        names.sort(new PinYinComparator());

        return names;

    }

    public List<String> completeManufacture(String prefix) {
        String tenantUID = UserContextService.getCurrentUserAccount().getTenantUID();

        List<String> names = dao.findDistinctManufacturesByPrefix(tenantUID, prefix);

        names.sort(new PinYinComparator());

        return names;

    }

    public List<String> completeGbName(String prefix) {
        List<String> names = null;
        if (!StringUtils.isEmpty(prefix)) {
            names = gbDao.findGbCodeByPrefix(prefix);
        }
        return names;

    }

    public GbCode getSelectedGb() {
        return selectedGb;
    }

    public void setSelectedGb(GbCode selectedGb) {
        this.selectedGb = selectedGb;

    }

    public Boolean getIsGeneratePmOrder() {
        return isGeneratePmOrder;
    }

    public void setIsGeneratePmOrder(Boolean isGeneratePmOrder) {
        this.isGeneratePmOrder = isGeneratePmOrder;
    }

    public void cancleWarranty(Integer assetId) {
        AssetInfo ai = dao.findById(assetId);
        if (ai != null) {
            ai.setWarranty(false);
            dao.save(ai);
        }
    }

    public String getGbCodeName() {
        return gbCodeName;
    }

    public void setGbCodeName(String gbCodeName) {

        this.gbCodeName = gbCodeName;
    }

    public boolean isShowDialog() {
        return showDialog;
    }

    public void setShowDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }

    public Map<String, Boolean> getSelectedMap() {
        return selectedMap;
    }

    public void setSelectedMap(Map<String, Boolean> selectedMap) {
        this.selectedMap = selectedMap;
    }

    public Boolean getIsSelectedCurrentPage() {
        if (null == currentPageAssets) {
            return false;
        } else {
            this.isSelectedCurrentPage = !currentPageAssets.stream().anyMatch(item -> !selectedMap.containsKey(item.getUid()) || !selectedMap.get(item.getUid()));
            return isSelectedCurrentPage;
        }
    }

    public void setIsSelectedCurrentPage(Boolean isSelectedCurrentPage) {
        this.isSelectedCurrentPage = isSelectedCurrentPage;
    }

    public OrgInfo getSelectSite() {
        return selectSite;
    }

    public void setSelectSite(OrgInfo selectSite) {
        this.selectSite = selectSite;
    }

    public Boolean getNeedShowAssetApproval() {
        if (null == needShowAssetApproval) {
            needShowAssetApproval = acService.needShowAssetApprovalByUser(UserContextService.getCurrentUserAccount().getUid());
        }
        return needShowAssetApproval;
    }

    public void batchProcessToBeTerminated() {
        Map<String, Boolean> map = this.selectedMap;
        for (Entry<String, Boolean> entry : map.entrySet()) {
            if (!entry.getValue()) {
                continue;
            }

            String assetUid = entry.getKey();
            AssetInfo assetInfo = dao.getByUid(assetUid);
            if (assetInfo == null) {
                continue;
            }

            assetInfo.setStatus(4);

            dao.save(assetInfo);

        }

    }

    public void batchProcessToTerminated() {
        Map<String, Boolean> map = this.selectedMap;
        for (Entry<String, Boolean> entry : map.entrySet()) {
            if (!entry.getValue()) {
                continue;
            }

            String assetUid = entry.getKey();
            AssetInfo assetInfo = dao.getByUid(assetUid);
            if (assetInfo == null) {
                continue;
            }

            int workOrdersNum = dao.fetchCurrentWorkOrders(assetInfo.getId());
            if (workOrdersNum > 0) {
                WebUtil.addErrorMessage(String.format("设备名称[%s]存在未完成工单，不能报废！", assetInfo.getName()));
                continue;
            }

            assetInfo.setIsValid(false);
            assetInfo.setTerminateDate(new Date());
            dao.save(assetInfo);

        }

    }

    public void searchList(Integer buttonIndex) {
        queryIndex = buttonIndex;
        this.searchFilters = null;
        this.selected = null;
    }

    public boolean isChecked() {

        Map<String, Boolean> map = this.selectedMap;
        for (Entry<String, Boolean> entry : map.entrySet()) {
            if (entry.getValue()) {
                return true;
            }

        }
        return false;
    }

    public List<V2_PmOrder> getPmOrderList() {
        if (this.selected == null) {
            return null;
        }
        List<V2_PmOrder> pmOrdersForAsset = v2_pmOrderRepository.getPmOrdersForAsset(selected.getUid());
        return pmOrdersForAsset;
    }

    public List<PmScheduleData> getPmScheduleData(String type) {
        if (this.selected == null) {
            return null;
        }
        List<PmScheduleData> pmsList = pmSchDao.getPmScheduleList(this.selected.getUid(), type);
        return pmsList;
    }

    public List<QrCodeTemplate> getQrCodeTemplates() {
        QrCodeTemplateService qtService = WebUtil.getBean(QrCodeTemplateService.class);
        return qtService.getTemplateByUser(UserContextService.getCurrentUserAccount());
    }

    //获取外修工单
    public List<ExternalWorkOrder> getExWorkOrder() {
        if (this.selected == null) {
            return null;
        }
        List<ExternalWorkOrder> exWorkOrderList = exWoDao.getExWorkOrderList(this.selected.getUid());
        return exWorkOrderList;

    }

    public ExWorkOrderRepository getExWoDao() {
        return exWoDao;
    }

    public void setExWoDao(ExWorkOrderRepository exWoDao) {
        this.exWoDao = exWoDao;
    }

    public OrgInfo getFileterOrg() {
        return fileterOrg;
    }

    public void setFileterOrg(OrgInfo fileterOrg) {
        this.fileterOrg = fileterOrg;
    }

    public StreamedContent getExcelFile() {
        return excelFile;
    }

    public void setExcelFile(StreamedContent excelFile) {
        this.excelFile = excelFile;
    }

    @Deprecated
    public void exportExcel() throws IOException {

        //非设备科的人导出的时候，导出部分数据
        boolean allColumns = false;
        if (userContextService.hasRole("AssetHead") || userContextService.hasRole("AssetStaff")) {
            allColumns = true;
        }

        Pageable pageable = new PageRequest(0, 100000000, lastPageSort);

        List<AssetInfo> assetList = dao.findBySearchFilter(this.searchFilters, pageable).getContent();

        List<OrgInfo> orgList = orgDao.findByParentUID(UserContextService.getCurrentUserAccount().getSiteUID());
        orgList = orgList.stream().filter(orgInfo -> orgInfo.getOrgType() == 4).collect(Collectors.toList());

        List<OrgInfo> sites = orgDao.getSitesByTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());


        List<String> assetUids = selectedMap.entrySet().stream().filter(item -> item.getValue()).map(item -> item.getKey()).collect(Collectors.toList());
        if (!assetUids.isEmpty()) {
            assetList = assetList.stream().filter(assetInfo -> {
                Boolean exists = this.selectedMap.get(assetInfo.getUid());
                if (exists == null) {
                    return false;
                }
                return exists.booleanValue();
            }).collect(Collectors.toList());
        }

        //不可用
        ExcelDocument doc = importService.exportAsset(assetList, sites, orgList, Collections.EMPTY_LIST);

        DefaultStreamedContent streamedContent = new DefaultStreamedContent(doc.getStream());
        streamedContent.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        streamedContent.setName("AssetInfo_export.xlsx");

        excelFile = streamedContent;
    }

    //三码合一设置
    public String getAssetCodeConfig() {
        return config == null ? null : config.getAssetCodeConfig();
    }

    public void onDepartNumChange(String fieldName) {
        if (!Strings.isNullOrEmpty(getAssetCodeConfig())) {
            String num = null;
            switch (getAssetCodeConfig()) {
                case "DFQ":
                    switch (fieldName) {
                        case "financingNum":
                            num = selected.getFinancingNum();
                            break;
                        case "departNum":
                            num = selected.getDepartNum();
                            break;
                        case "qrCode":
                            num = qrCode;
                            break;
                        default:
                    }
                    if (!Strings.isNullOrEmpty(num) && num.length() > 16) {
                        qrCode = num.substring(0, 16);
                        selected.setFinancingNum(qrCode);
                        selected.setDepartNum(qrCode);
                        WebUtil.addErrorMessage("二维码超长,最大16位");
                    } else {
                        qrCode = num;
                        selected.setFinancingNum(num);
                        selected.setDepartNum(num);
                    }
                    break;
                case "DF":
                    switch (fieldName) {
                        case "financingNum":
                            num = selected.getFinancingNum();
                            selected.setFinancingNum(num);
                            selected.setDepartNum(num);
                            break;
                        case "departNum":
                            num = selected.getDepartNum();
                            selected.setFinancingNum(num);
                            selected.setDepartNum(num);
                            break;
                    }
                    break;
                default:
            }
        }
    }

    public String getExportQrCodeTemplateId() {
        return exportQrCodeTemplateId;
    }

    public void setExportQrCodeTemplateId(String exportQrCodeTemplateId) {
        this.exportQrCodeTemplateId = exportQrCodeTemplateId;
    }

    public List<OrgInfo> autoCompleteDept(String query) {
        if (clinicalDeptList == null) return Collections.emptyList();

        List<OrgInfo> res = this.clinicalDeptList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    public void setOwnerList(List<UserAccount> ownerList) {
        this.ownerList = ownerList;
    }

	public List<String> getOrgUids() {
		return orgUids;
	}

	public void prepareForceAllocateAsset(AssetInfo asset) {
        SharedAssetType sharedAssetType = findSharedAssetType(asset);
        asset.setSharedAssetType(sharedAssetType);
		SharedAssetReqDetail reqDetail = this.selectProperReqDetail(asset);
		reqDetail = (reqDetail == null) ? new SharedAssetReqDetail() : reqDetail;
		SharedAssetRequest assetRequest = reqDetail.getLeaseRequest();
		if(assetRequest == null) {
			assetRequest = new SharedAssetRequest();
			reqDetail.setLeaseRequest(assetRequest);
		}
		asset.setSharedAssetReqDetail(reqDetail);
		this.selected = asset;
	}
	
	public void prepareForceReturnAsset(AssetInfo asset) {
        SharedAssetType sharedAssetType = findSharedAssetType(asset);
        asset.setSharedAssetType(sharedAssetType);
		SharedAssetReqDetail reqDetail = this.selectProperReqDetail(asset);
		reqDetail = (reqDetail == null) ? new SharedAssetReqDetail() : reqDetail;
		SharedAssetRequest assetRequest = reqDetail.getReturnRequest();
		if(assetRequest == null) {
			assetRequest = new SharedAssetRequest();
			reqDetail.setReturnRequest(assetRequest);
		}
		asset.setSharedAssetReqDetail(reqDetail);
		this.selected = asset;
	}

	public void forceReturnAsset() {
		if(this.selected != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			SharedAssetReqDetail reqDetail = selected.getSharedAssetReqDetail();
			Date chargingBeginTime = reqDetail.getChargingBeginTime();
			Date chargingEndTime = reqDetail.getChargingEndTime();
			String beginTimeStr = (chargingBeginTime == null) ? "" : formatter.format(chargingBeginTime);
			String endTimeStr = (chargingEndTime == null) ? "" : formatter.format(chargingEndTime);
			try {
			    if(reqDetail.getClinicalDeptUid() != null) {
			        selected.setAssetDeptUID(reqDetail.getClinicalDeptUid());
                    log.info("assetDeptUID, assetInfoUID, chargingBeginTime, chargingEndTime, reqType： " + selected.getAssetDeptUID() + "," + selected.getUid() + ", " + beginTimeStr + ", " + endTimeStr + ", " + selected.getSharedAssetReqDetail().getReturnRequest().getType());
                    ResponseEntity<LinkedHashMap> returnResponse = sharedAssetService.returnByShortcut(selected.getAssetDeptUID(), selected.getUid(), beginTimeStr, endTimeStr, selected.getSharedAssetReqDetail().getReturnRequest().getType());
                    LinkedHashMap<String, Object> rspBody = returnResponse.getBody();
                    log.info("****** force return response ******");
                    rspBody.forEach((k, v) -> log.info("key: " + k + ", value: " + v));
                }
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void forceAllocateAsset() {
		if(this.selected != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			Date chargingBeginTime = selected.getSharedAssetReqDetail().getChargingBeginTime();
			Date chargingEndTime = selected.getSharedAssetReqDetail().getChargingEndTime();
			String beginTimeStr = (chargingBeginTime == null) ? "" : formatter.format(chargingBeginTime);
			String endTimeStr = (chargingEndTime == null) ? "" : formatter.format(chargingEndTime);
            OrgInfo assetDept = clinicalDeptList.stream().filter(item -> item.getName().equals(this.filterOrgName)).findFirst().orElse(null);
			try {
			    if(assetDept != null) {
                    selected.setAssetDeptUID(assetDept.getUid());
                    log.info("assetDeptUID, assetInfoUID, chargingBeginTime, chargingEndTime, reqType： " + selected.getAssetDeptUID() + "," + selected.getUid() + ", " + beginTimeStr + ", " + endTimeStr + ", " + selected.getSharedAssetReqDetail().getLeaseRequest().getType());
                    ResponseEntity<LinkedHashMap> borrowResponse = sharedAssetService.borrowByShortcut(selected.getAssetDeptUID(), selected.getUid(), beginTimeStr, endTimeStr, selected.getSharedAssetReqDetail().getLeaseRequest().getType());
                    LinkedHashMap<String, Object> rspBody = borrowResponse.getBody();
                    log.info("****** force allocate response ******");
                    rspBody.forEach((k, v) -> log.info("key: " + k + ", value: " + v));
                }
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private SharedAssetReqDetail selectProperReqDetail(AssetInfo item) {
		Date nowDate = new Date();
        List<SharedAssetReqDetail> sharedAssetReqDetailList = sharedAssetReqDetailRepository.findByAssetInfoUid(item.getUid());
        SharedAssetReqDetail sharedAssetReqDetail = Optional.ofNullable(sharedAssetReqDetailList).get().stream()
                                                            .filter(reqDetail -> reqDetail.getChargingEndTime() == null || reqDetail.getChargingEndTime().compareTo(nowDate) > 0)
                                                            .findFirst()
                                                            .orElse(null);

    	if(sharedAssetReqDetail != null) {
            calcChargingByAssetReqDetail(item, sharedAssetReqDetail);
    	}
    	return sharedAssetReqDetail;
	}

	private SharedAssetReqDetail findLatestReqDetail(AssetInfo item) {
        List<SharedAssetReqDetail> sharedAssetReqDetailList = sharedAssetReqDetailRepository.findByAssetInfoUid(item.getUid());
        SharedAssetReqDetail sharedAssetReqDetail = Optional.ofNullable(sharedAssetReqDetailList).get().stream()
                                                            .sorted((SharedAssetReqDetail o1, SharedAssetReqDetail o2) -> {
                                                                if(o1.getChargingEndTime() == null) {
                                                                        return -1;
                                                                    } else if(o2.getChargingEndTime() == null) {
                                                                        return 1;
                                                                    } else {
                                                                        if(o1.getChargingEndTime().before(o2.getChargingEndTime())) {
                                                                            return 1;
                                                                        } else {
                                                                            return -1;
                                                                        }
                                                                    }
                                                            })
                                                            .findFirst()
                                                            .orElse(null);
        if(sharedAssetReqDetail != null) {
            calcChargingByAssetReqDetail(item, sharedAssetReqDetail);
        }
        return sharedAssetReqDetail;
    }

	private SharedAssetReqDetail calcChargingByAssetReqDetail(AssetInfo item, SharedAssetReqDetail sharedAssetReqDetail) {
        Date beginTime = sharedAssetReqDetail.getChargingBeginTime();
        Date endTime = sharedAssetReqDetail.getChargingEndTime();

        String chargingType = item.getSharedAssetType().getChargingType();
        Tuple2<BigDecimal, String> unitAndTotalTime = SharedAssetInfoUtils.getChargingUnitAndTotalTime(sharedAssetReqDetail);
        BigDecimal unit = unitAndTotalTime._1;
        String totalTime = unitAndTotalTime._2;

        if(item.getStatus().intValue() == 1 || item.getStatus().intValue() == 3) {
            if(this.getOrgUids().contains(item.getAssetDeptUID())) {
            } else {
                SharedAssetRequest request = sharedAssetReqDetail.getLeaseRequest();
                if(request != null && "1".equals(request.getType())) {
                    unit = new BigDecimal(0);
                }
            }
        }
        sharedAssetReqDetail.setRentalTime(totalTime);

        BigDecimal totalAmount = SharedAssetInfoUtils.getTotalAmount(unit, sharedAssetReqDetail);
        sharedAssetReqDetail.setTotalAmount(totalAmount.doubleValue());
        return sharedAssetReqDetail;
    }

	private SharedAssetType findSharedAssetType(AssetInfo assetInfo) {
        SharedAssetType sharedAssetType = swapDao.findById(assetInfo.getSharedAssetTypeUid());
        return sharedAssetType;
    }

    private Date currentDate;

    public Date getCurrentDate() {
        return new Date();
    }

    public String getFilterOrgName() {
        return filterOrgName;
    }

    public void setFilterOrgName(String filterOrgName) {
        this.filterOrgName = filterOrgName;
    }

    public List<String> autoCompleteDeptName(String query) {
        if(clinicalDeptList == null) {
            clinicalDeptList = assetInfoService.getOrgDeptList(this.selected.getSiteUID());
        }

        List<OrgInfo> res = this.clinicalDeptList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
            || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }

    public void onClinicalDeptNameFilterChange(AjaxBehaviorEvent event) {
        if (event instanceof SelectEvent) {
            filterOrgName = (String) ((SelectEvent) event).getObject();
            return;
        } else {
            if (!Strings.isNullOrEmpty(filterOrgName) && !clinicalDeptList.stream().anyMatch(item -> filterOrgName.equals(item.getName()))) {
                filterOrgName = "";
            }
        }

    }

    //301医院则显示打印按钮
    public boolean validate301()
    {
        boolean isTargetHospital = false;
        List<OrgInfo> allocDeptList = orgDao.findByUids(new HashSet<>(orgUids));
        OrgInfo allocDept = allocDeptList.stream().findFirst().get();
        String hosName = allocDept.getOrgFullName();
        if(hosName.contains("中国人民解放军总医院"))
        {
            isTargetHospital = true;
        }

        return isTargetHospital;
    }

    //若当前状态为可用，则打印上次归还信息
    //若当前状态为借出，则打印当前借用信息
    public void generateVoucher(AssetInfo asset) {
        //当前状态已借出
        if(!asset.getAssetDeptUID().equals(asset.getClinicalDeptUID()))
        {
            List<OrgInfo> allocDeptList = orgDao.findByUids(new HashSet<>(orgUids));
            OrgInfo allocDept = allocDeptList.stream().findFirst().get();
            Map<String, Object> info = new HashedMap();

            info.put("抬头", allocDept.getParentOrg().getName() + allocDept.getName());
            info.put("经办人", userContextService.getUserName());

            getPdfContent(asset, info);
            doExportToPdf(info, "sharedAssetBorrowed.xsl", "SharedAssetBorrowInfo.pdf" );
        }
        else
        {
            Map<String, Object> returned = new HashedMap();
            getPdfContent(asset, returned);
            doExportToPdf(returned, "sharedAssetReturned.xsl", "SharedAssetReturnInfo.pdf" );
        }
    }

    private void getPdfContent(AssetInfo asset, Map<String, Object> info)
    {
        SharedAssetType sharedAssetType = findSharedAssetType(asset);
        asset.setSharedAssetType(sharedAssetType);
        SharedAssetReqDetail reqDetail = this.selectProperReqDetail(asset);
        reqDetail = (reqDetail == null) ? this.findLatestReqDetail(asset) : reqDetail;
        if(reqDetail != null) {
            asset.setSharedAssetReqDetail(reqDetail);
            this.selected = asset;
            SharedAssetInfoUtils.fillData2Voucher(asset, info);
        }
    }

    private void doExportToPdf(Map<String, Object> info, String xslName, String pdfName) {
        try {
            String woXml = getWoXml(info);

            File xsl = new File(WorkOrderReportExportController.class.getClassLoader().
                    getResource("../../portal/pdf/" + xslName).getPath());

            File conf = new File(WorkOrderReportExportController.class.getClassLoader().
                    getResource("../../portal/pdf/fop.xml").getPath());

            FopFactory fopFactory = FopFactory.newInstance(conf);
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            TransformerFactory factory = TransformerFactory.newInstance();
            try {
                Transformer transformer = factory.newTransformer(new StreamSource(xsl));
                Source src = new StreamSource(new StringReader(woXml));
                Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);
                Result res = new SAXResult(fop.getDefaultHandler());
                transformer.transform(src, res);
            } finally {
                exportPdf = (new DefaultStreamedContent((new ByteArrayInputStream(out.toByteArray())), MimeConstants.MIME_PDF, pdfName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getWoXml(Map<String, Object> data) {
        XStream xStream = new XStream(new DomDriver());
        xStream.registerConverter(new Map2XmlConverter(new DefaultMapper(XStream.class.getClassLoader())));
        xStream.alias("List", List.class);
        xStream.alias("map", HashedMap.class);

        return xStream.toXML(data);
    }

    public StreamedContent getExportPdf() {
        return exportPdf;
    }

    public void setExportPdf(StreamedContent exportPdf) {
        this.exportPdf = exportPdf;
    }
    
}
