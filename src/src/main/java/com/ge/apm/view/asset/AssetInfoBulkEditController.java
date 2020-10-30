package com.ge.apm.view.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.domain.pure.KeyLabel;
import com.ge.apm.service.asset.*;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.orginfo.OrgInfoClinicalDeptService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparator;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetInfoBulkEditController extends JpaCRUDController<AssetInfo>  {

    static PageRequest pageRequest = new PageRequest(0, 100);

    private AssetInfoRepository dao;
    private AssetInfoService assetInfoService;
    private UaaService uaaService;
    private UserCommonService ucService;
    private I18nMessageRepository i18nDao;
    private OrgInfoRepository orgDao;
    private ConfigService configService;
    private OrgInfoClinicalDeptService clinicalDeptService;
    private UserAccountRepository userDao;
    private AssetInfoMgrService aimService;
    private AutoCompleteService autoCompleteService;
    private GbCodeRepository gbDao;
    private AssetFinacingNumGenerateService assetFinacingNumGenerateService;
    private GbCodeService gbCodeService;
    private QrCodeService qrCodeService;
    private CustomConfigService ccService;
    private AssetInfoBulkEditService assetInfoBulkEditService;
    private AssetInfoBulkEditCheckService assetInfoBulkEditCheckService;
    private OrgInfoRepository orgInfoDao;
    private NurseSiteMaintainItemRepository nurseSiteMaintainItemRepository;
    private NurseSiteMaintainDataRepository nurseSiteMaintainDataRepository;
    private V2_WorkOrderRepository v2WorkerOrderRepository;
    private V2_WorkOrderDetailRepository v2WorkerOrderDetailRepository;
    private V2_WorkOrderStepRepository v2_WorkOrderStepRepository;
    private V2ServiceRequestRepository v2ServiceRequestRepository;

    //******* filter start

    @Getter @Setter
    private DualListModel<AssetInfo> assets = new DualListModel<>(null, new ArrayList<>());
    @Getter @Setter
    private TreeNode orgTree;

    @Getter @Setter
    private AssetInfo filter = new AssetInfo();

    private List<OrgInfo> clinicalDeptListAsFilter = Collections.emptyList();

    @Getter @Setter
    private String oneSearchStr;

    //******* filter end


    //****** new value start
    private List<OrgInfo> clinicalDeptList = Collections.emptyList();
    //****** new value end

    @Getter
    private List<KeyLabel> candidateColumns = new ArrayList<>();

    @Getter @Setter
    private List<String> selectedColumns = new ArrayList<>();
    @Getter @Setter
    private boolean assetGroupSecondarySupport = false;
    @Getter @Setter
    private List<I18nMessage> assetGroupSecondaryList;
    @Getter @Setter
    private List<UserAccount> ownerList;
    @Getter @Setter
    private List<AssetInfo> parentAssetList;
    @Getter@Setter
    private Map<String, Object> selectedObjectMap = new HashMap();
    private WorkflowConfig config;
    private List<UserAccount> clinicalOwnerList;
    @Getter
    @Setter
    private List<Supplier> manuList = Collections.emptyList();
    @Getter
    @Setter
    private List<Supplier> supplierList = Collections.emptyList();
    @Getter
    @Setter
    private List<Supplier> mantainList = Collections.emptyList();
    @Getter
    @Setter
    private Supplier newSupplier;
    private String createTpye = null;
    @Getter
    @Setter
    private String gbCodeName;
    @Getter
    @Setter
    private TreeNode gbTree;
    @Getter
    @Setter
    private GbCode selectedGb;
    @Getter
    @Setter
    private String qrCode;
    @Getter
    @Setter
    private Date purchaseDate;
    @Getter
    @Setter
    private List<I18nMessage> code68List;
    private List<String> readableFieldsList;
    @Getter@Setter
    private CustomConfigObject customConfig;
    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
        assetInfoService = WebUtil.getBean(AssetInfoService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        assetInfoBulkEditService = WebUtil.getBean(AssetInfoBulkEditService.class);
        assetInfoBulkEditCheckService = WebUtil.getBean(AssetInfoBulkEditCheckService.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        nurseSiteMaintainItemRepository = WebUtil.getBean(NurseSiteMaintainItemRepository.class);
        nurseSiteMaintainDataRepository = WebUtil.getBean(NurseSiteMaintainDataRepository.class);
        v2WorkerOrderRepository = WebUtil.getBean(V2_WorkOrderRepository.class);
        v2WorkerOrderDetailRepository= WebUtil.getBean(V2_WorkOrderDetailRepository.class);
        v2_WorkOrderStepRepository= WebUtil.getBean(V2_WorkOrderStepRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        ucService = WebUtil.getBean(UserCommonService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        configService = WebUtil.getBean(ConfigService.class);
        clinicalDeptService = WebUtil.getBean(OrgInfoClinicalDeptService.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        aimService = WebUtil.getBean(AssetInfoMgrService.class);
        autoCompleteService = WebUtil.getBean(AutoCompleteService.class);
        gbDao = WebUtil.getBean(GbCodeRepository.class);
        assetFinacingNumGenerateService = WebUtil.getBean(AssetFinacingNumGenerateService.class);
        gbCodeService = WebUtil.getBean(GbCodeService.class);
        qrCodeService = WebUtil.getBean(QrCodeService.class);
        ccService = WebUtil.getBean(CustomConfigService.class);
        selected = new AssetInfo();
        customConfig = ccService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());
        readableFieldsList = ccService.getReadAssetFieldsByRoles(customConfig, UserContextService.getRoles());
        initCandidateKeyLabels();
        UserAccount loginUser = UserContextService.getCurrentUserAccount();
        assetGroupSecondarySupport = i18nDao.countBySiteIdAndMsgTypeStartsWith(loginUser.getSiteId(), "assetGroupSecondary") > 0 ? true : false;
        if (assetGroupSecondarySupport) {
            onAssetGroupChange();
        }
        ownerList = ucService.getWorkerList(selected.getId(), "");
        selectedObjectMap.put("selectedOrg", orgDao.findByUid(UserContextService.getCurrentUserAccount().getSiteUID()));
        changeAssetOrg((OrgInfo) selectedObjectMap.get("selectedOrg"));
        manuList = aimService.getOnlyManuListBySiteUid(selected.getTenantUID());
        supplierList = aimService.getOnlySuplierListBySiteUid(selected.getTenantUID());
        mantainList = aimService.getOnlyMaintainListBySiteUid(selected.getTenantUID());
        initGbTree();
        qrCode = WebUtil.getRequestParameter("qrCode");
    }

    private void initCandidateKeyLabels() {
        KeyLabel keyLabel;
//        if(readableFieldsList.contains("serialNum")){
//            keyLabel = new KeyLabel("serialNum", WebUtil.getMessage("serialNum"));
//            candidateColumns.add(keyLabel);
//        }
        if(readableFieldsList.contains("name")){
            keyLabel = new KeyLabel("name", WebUtil.getMessage("name"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("aliasName")){
            keyLabel = new KeyLabel("aliasName", WebUtil.getMessage("aliasName"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("assetGroup")){
            keyLabel = new KeyLabel("assetGroup", WebUtil.getMessage("assetGroup"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("assetOwnerName")){
            keyLabel = new KeyLabel("assetOwnerName", WebUtil.getMessage("assetOwnerName"));
            candidateColumns.add(keyLabel);
        }
//        if(readableFieldsList.contains("assetOwnerTel")){
//            keyLabel = new KeyLabel("assetOwnerTel", WebUtil.getMessage("assetOwnerTel"));
//            candidateColumns.add(keyLabel);
//        }
        if(readableFieldsList.contains("assetOwnerName2")){
            keyLabel = new KeyLabel("assetOwnerName2", WebUtil.getMessage("assetOwnerName2"));
            candidateColumns.add(keyLabel);
        }
//        if(readableFieldsList.contains("assetOwnerTel2")){
//            keyLabel = new KeyLabel("assetOwnerTel2", WebUtil.getMessage("assetOwnerTel2"));
//            candidateColumns.add(keyLabel);
//        }
        if(readableFieldsList.contains("assetOwnerName3")){
            keyLabel = new KeyLabel("assetOwnerName3", WebUtil.getMessage("assetOwnerName3"));
            candidateColumns.add(keyLabel);
        }
//        if(readableFieldsList.contains("assetOwnerTel3")){
//            keyLabel = new KeyLabel("assetOwnerTel3", WebUtil.getMessage("assetOwnerTel3"));
//            candidateColumns.add(keyLabel);
//        }
        if(readableFieldsList.contains("pmOwnerName")){
            keyLabel = new KeyLabel("pmOwnerName", WebUtil.getMessage("pmOwnerName"));
            candidateColumns.add(keyLabel);
        }
//        if(readableFieldsList.contains("pmOwnerTel")){
//            keyLabel = new KeyLabel("pmOwnerTel", WebUtil.getMessage("pmOwnerTel"));
//            candidateColumns.add(keyLabel);
//        }
        if(readableFieldsList.contains("clinicalOwnerName")){
            keyLabel = new KeyLabel("clinicalOwnerName", WebUtil.getMessage("clinicalOwnerName"));
            candidateColumns.add(keyLabel);
        }
//        if(readableFieldsList.contains("clinicalOwnerTel")){
//            keyLabel = new KeyLabel("clinicalOwnerTel", WebUtil.getMessage("clinicalOwnerTel"));
//            candidateColumns.add(keyLabel);
//        }
        if(readableFieldsList.contains("manufacture")){
            keyLabel = new KeyLabel("manufacture", WebUtil.getMessage("manufacture"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("makerShortName")){
            keyLabel = new KeyLabel("makerShortName", WebUtil.getMessage("makerShortName"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("vendor")){
            keyLabel = new KeyLabel("vendor", WebUtil.getMessage("vendor"));
            candidateColumns.add(keyLabel);
        }
//        if(readableFieldsList.contains("vendorTel")){
//            keyLabel = new KeyLabel("vendorTel", WebUtil.getMessage("vendorTel"));
//            candidateColumns.add(keyLabel);
//        }
        if(readableFieldsList.contains("maitanance")){
            keyLabel = new KeyLabel("maitanance", WebUtil.getMessage("maitanance"));
            candidateColumns.add(keyLabel);
        }
//        if(readableFieldsList.contains("maitananceTel")){
//            keyLabel = new KeyLabel("maitananceTel", WebUtil.getMessage("maitananceTel"));
//            candidateColumns.add(keyLabel);
//        }
        if(readableFieldsList.contains("nationalStandardCode")){
            keyLabel = new KeyLabel("nationalStandardCode", WebUtil.getMessage("nationalStandardCode"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("assetDeptName")){
            keyLabel = new KeyLabel("assetDeptName", WebUtil.getMessage("assetDeptName"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("assetValueCategory")){
            keyLabel = new KeyLabel("assetValueCategory", WebUtil.getMessage("assetValueCategory"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("capitalSource2")){
            keyLabel = new KeyLabel("capitalSource2", WebUtil.getMessage("capitalSource2"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("parentAsset")){
            keyLabel = new KeyLabel("parentAsset", WebUtil.getMessage("parentAsset"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("deviceName")){
            keyLabel = new KeyLabel("deviceName", WebUtil.getMessage("deviceName"));
            candidateColumns.add(keyLabel);
        }
//        if(readableFieldsList.contains("systemId")){
//            keyLabel = new KeyLabel("systemId", WebUtil.getMessage("systemId"));
//            candidateColumns.add(keyLabel);
//        }
//        if(readableFieldsList.contains("financingNum")){
//            keyLabel = new KeyLabel("financingNum", WebUtil.getMessage("financingNum"));
//            candidateColumns.add(keyLabel);
//        }
//        if(readableFieldsList.contains("departNum")){
//            keyLabel = new KeyLabel("departNum", WebUtil.getMessage("departNum"));
//            candidateColumns.add(keyLabel);
//        }
//        if(readableFieldsList.contains("barcode")){
//            keyLabel = new KeyLabel("barcode", WebUtil.getMessage("barcode"));
//            candidateColumns.add(keyLabel);
//        }
//        if(readableFieldsList.contains("registrationNo")){
//            keyLabel = new KeyLabel("registrationNo", WebUtil.getMessage("registrationNo"));
//            candidateColumns.add(keyLabel);
//        }
//        if(readableFieldsList.contains("modalityId")){
//            keyLabel = new KeyLabel("modalityId", WebUtil.getMessage("modalityId"));
//            candidateColumns.add(keyLabel);
//        }
//        if(readableFieldsList.contains("qrCode")){
//            keyLabel = new KeyLabel("qrCode", WebUtil.getMessage("qrCode"));
//            candidateColumns.add(keyLabel);
//        }
        if(readableFieldsList.contains("functionType")){
            keyLabel = new KeyLabel("functionType", WebUtil.getMessage("functionType"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("functionGroup")){
            keyLabel = new KeyLabel("functionGroup", WebUtil.getMessage("functionGroup"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("assetCode68")){
            keyLabel = new KeyLabel("assetCode68", WebUtil.getMessage("assetCode68"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("functionGrade")){
            keyLabel = new KeyLabel("functionGrade", WebUtil.getMessage("functionGrade"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("repairForm")){
            keyLabel = new KeyLabel("repairForm", WebUtil.getMessage("repairForm"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("locationCode")){
            keyLabel = new KeyLabel("locationCode", WebUtil.getMessage("locationCode"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("locationName")){
            keyLabel = new KeyLabel("locationName", WebUtil.getMessage("locationName"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("status")){
            keyLabel = new KeyLabel("status", WebUtil.getMessage("status"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("deviceStatus")){
            keyLabel = new KeyLabel("deviceStatus", WebUtil.getMessage("deviceStatus"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("isAssetPMed")){
            keyLabel = new KeyLabel("isAssetPMed", WebUtil.getMessage("isAssetPMed"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("isAssetChecked")){
            keyLabel = new KeyLabel("isAssetChecked", WebUtil.getMessage("isAssetChecked"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("comments")){
            keyLabel = new KeyLabel("comments", WebUtil.getMessage("comments"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("assetUsage")){
            keyLabel = new KeyLabel("assetUsage", WebUtil.getMessage("assetUsage"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("capitalSource")){
            keyLabel = new KeyLabel("capitalSource", WebUtil.getMessage("capitalSource"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("measureGrade")){
            keyLabel = new KeyLabel("measureGrade", WebUtil.getMessage("measureGrade"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("riskGrade")){
            keyLabel = new KeyLabel("riskGrade", WebUtil.getMessage("riskGrade"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("emergencyEquipment")){
            keyLabel = new KeyLabel("emergencyEquipment", WebUtil.getMessage("emergencyEquipment"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("importEquipment")){
            keyLabel = new KeyLabel("importEquipment", WebUtil.getMessage("importEquipment"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("measuringItemCategory")){
            keyLabel = new KeyLabel("measuringItemCategory", WebUtil.getMessage("measuringItemCategory"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("accountingCategory")){
            keyLabel = new KeyLabel("accountingCategory", WebUtil.getMessage("accountingCategory"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("manufactDate")){
            keyLabel = new KeyLabel("manufactDate", WebUtil.getMessage("manufactDate"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("purchaseDate")){
            keyLabel = new KeyLabel("purchaseDate", WebUtil.getMessage("purchaseDate"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("arriveDate")){
            keyLabel = new KeyLabel("arriveDate", WebUtil.getMessage("arriveDate"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("installDate")){
            keyLabel = new KeyLabel("installDate", WebUtil.getMessage("installDate"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("factoryWarrantyDate")){
            keyLabel = new KeyLabel("factoryWarrantyDate", WebUtil.getMessage("factoryWarrantyDate"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("warrantyDate")){
            keyLabel = new KeyLabel("warrantyDate", WebUtil.getMessage("warrantyDate"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("terminateDate")){
            keyLabel = new KeyLabel("terminateDate", WebUtil.getMessage("terminateDate"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("isWarrantyNotice")){
            keyLabel = new KeyLabel("isWarrantyNotice", WebUtil.getMessage("isWarrantyNotice"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("purchasePrice")){
            keyLabel = new KeyLabel("purchasePrice", WebUtil.getMessage("purchasePrice"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("salvageValue")){
            keyLabel = new KeyLabel("salvageValue", WebUtil.getMessage("salvageValue"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("lifecycle")){
            keyLabel = new KeyLabel("lifecycle", WebUtil.getMessage("lifecycle"));
            candidateColumns.add(keyLabel);
        }
        if(readableFieldsList.contains("depreciationMethod")){
            keyLabel = new KeyLabel("depreciationMethod", WebUtil.getMessage("depreciationMethod"));
            candidateColumns.add(keyLabel);
        }
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
                code68List = i18nDao.getByMsgTypeOrderByMsgKey(functionGroup.getMsgKey());
            }
        }
    }
    public void handleDateSelect(SelectEvent event) {
        Object object = event.getObject();
        purchaseDate= (Date) object;
    }
    public void renewQrcode() {
        QrCodeLib qrcodeLib = qrCodeService.createOneQrCode(selected.getTenantUID(), selected.getSiteUID(), 1);
        qrCode = qrcodeLib.getQrCode();
        onDepartNumChange("qrCode");
    }
    public void generateFinancialNum() {
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
                    if (!Strings.isNullOrEmpty(num) && num.length() > 64) {
                        qrCode = num.substring(0, 64);
                        selected.setFinancingNum(qrCode);
                        selected.setDepartNum(qrCode);
                        WebUtil.addErrorMessage("二维码超长,最大64位");
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

    public void  onParentAssetChange() {
        AssetInfo selectedAsset = (AssetInfo) selectedObjectMap.get("parentAsset");
        selected.setParentAssetId(null == selectedAsset ? null : selectedAsset.getId());
    }

    public List<AssetInfo> completeParentAssetId(String query) {
        parentAssetList = aimService.getParentsAssetsList(this.selected);
        return autoCompleteService.completeAssetInfoList(query, parentAssetList);
    }
    private void updateSelectedOrgGb(GbCode gbCode) {
        if (gbCode == null || gbCode.getId() == -1) {
            selectedGb = null;
        } else {
            selectedGb = gbCode;
            gbCodeName = gbCode.getCode() + "/" + gbCode.getName();
        }
    }

    public void onSelectGbTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        GbCode gbCode = (GbCode) node.getData();
        updateSelectedOrgGb(gbCode);
    }

    public void initGbTree() {
        TreeNode treeNode = gbCodeService.buildGbTree();
        gbTree = new DefaultTreeNode("root", null, null);
        gbTree.getChildren().add(treeNode);
        treeNode.setExpanded(true);
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

    public List<String> completeGbName(String prefix) {
        List<String> names = null;
        if (!StringUtils.isEmpty(prefix)) {
            names = gbDao.findGbCodeByPrefix(prefix);
        }
        return names;
    }

    public List<Supplier> autoCompleteMaintainer(String query) {
        mantainList = aimService.getOnlyMaintainListBySiteUid(selected.getTenantUID());
        return autoCompleteService.autoCompleteMaintainer(query, mantainList);
    }
    public List<Supplier> autoCompleteSupplier(String query) {
        //重新获取一下供应商
        supplierList = aimService.getOnlySuplierListBySiteUid(selected.getTenantUID());
        return autoCompleteService.autoCompleteVendor(query, supplierList);
    }

    public void prepareCreateSupplier(String type) {
        newSupplier = new Supplier();
        if ("manufacturer".equals(type)) {
            createTpye = type;
            newSupplier.setManufacturerT(true);
        }
        if ("supplier".equals(type)) {
            createTpye = type;
            newSupplier.setSupplierT(true);
        }
        if ("maintainer".equals(type)) {
            createTpye = type;
            newSupplier.setMaintainerT(true);
        }

    }

    public void onSupplierChange(String idx) {
        changeSelect(idx);
    }
    private void changeSelect(String idx) {
        Supplier supplier = (Supplier) selectedObjectMap.get(idx);
        switch (idx) {
            case "supplier":
                selected.setSupplierId(supplier == null ? null : supplier.getId());
                selected.setVendor(supplier == null ? null : supplier.getName());
                selected.setVendorTel(supplier == null ? null : supplier.getTel());
                break;
            case "maintainer":
                selected.setMaitananceId(supplier == null ? null : supplier.getId() == null ? null : String.valueOf(supplier.getId()));
                selected.setMaitanance(supplier == null ? null : supplier.getName());
                selected.setMaitananceTel(supplier == null ? null : supplier.getTel());
                break;
            case "manufacturer":
                selected.setManufactureId(supplier == null ? null : supplier.getId() == null ? null : String.valueOf(supplier.getId()));
                selected.setManufacture(supplier == null ? null : supplier.getName());
                break;
        }
    }
    public List<Supplier> autoCompleteManufacture(String query) {
        manuList = aimService.getOnlyManuListBySiteUid(selected.getTenantUID());
        return autoCompleteService.autoCompleteManufacture(query, manuList);
    }
    public List<UserAccount> completeClinicalOwnerList(String query) {
        if (clinicalOwnerList == null) {
            return Collections.emptyList();
        }
        List<UserAccount> res = this.clinicalOwnerList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }
    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();

        selectedObjectMap.put("selectedOrg", orgInfo);

        changeAssetOrg(orgInfo);
    }
    private void updateClinicalDeptOnSiteChange() {

        clinicalDeptList = clinicalDeptService.getUserAccessibleClinicalDeptsBySelectedSite(selected.getSiteUID());
    }
    public void getClinicalOwnerList() {
        if (null != selected.getClinicalDeptUID()) {
            clinicalOwnerList = userDao.findUsersByOrgIdExceptTemporaryAccount(selected.getClinicalDeptUID());
        } else {
            clinicalOwnerList = Collections.emptyList();
        }
    }
    public void onAssetDeptChange(String idx) {
        OrgInfo clinicalDept = null;
        switch (idx) {
            case "clinicalDept":
                clinicalDept = (OrgInfo) selectedObjectMap.get("clinicalDept");
                selected.setClinicalDeptId(clinicalDept == null ? null : clinicalDept.getId());
                selected.setClinicalDeptName(clinicalDept == null ? null : clinicalDept.getName());
                selected.setClinicalDeptUID(clinicalDept == null ? null : clinicalDept.getUid());
                selectedObjectMap.put("clinicalOwner", null);
                onOwnerChange("clinicalOwner");
                getClinicalOwnerList();
                break;
            case "assetDept":
                clinicalDept = (OrgInfo) selectedObjectMap.get("assetDept");
                selected.setAssetDeptId(clinicalDept == null ? null : clinicalDept.getId());
                selected.setAssetDeptUID(clinicalDept == null ? null : clinicalDept.getUid());
                break;
        }

    }
    private void changeAssetOrg(OrgInfo orgInfo) {
        selected.setSiteId(orgInfo.getSiteId());
        selected.setHospitalId(orgInfo.getId());

        selected.setTenantUID(orgInfo.getTenantUID());
        selected.setHospitalUID(orgInfo.getHospitalUID());
        selected.setInstitutionUID(orgInfo.getInstitutionUID());
        selected.setSiteUID(orgInfo.getSiteUID());
        config = configService.findConfigBySite(selected.getSiteUID());

        updateClinicalDeptOnSiteChange();

        selectedObjectMap.put("clinicalDept", null);
        onAssetDeptChange("clinicalDept");
        selectedObjectMap.put("assetDept", null);
        onAssetDeptChange("assetDept");
        if (parentAssetList != null) {
            parentAssetList.clear();
        }
    }
    public void onOwnerChange(String idx) {
        UserAccount selectedUser = (UserAccount) selectedObjectMap.get(idx);
        switch (idx) {
            case "assetOwner":
                selected.setAssetOwnerId(null == selectedUser ? null : selectedUser.getId());
                selected.setAssetOwnerName(null == selectedUser ? null : selectedUser.getName());
                selected.setAssetOwnerTel(null == selectedUser ? null : selectedUser.getTelephone());
                break;
            case "assetOwner2":
                selected.setAssetOwnerId2(null == selectedUser ? null : selectedUser.getId());
                selected.setAssetOwnerName2(null == selectedUser ? null : selectedUser.getName());
                selected.setAssetOwnerTel2(null == selectedUser ? null : selectedUser.getTelephone());
                break;
            case "assetOwner3":
                selected.setAssetOwnerId3(null == selectedUser ? null : selectedUser.getId());
                selected.setAssetOwnerName3(null == selectedUser ? null : selectedUser.getName());
                selected.setAssetOwnerTel3(null == selectedUser ? null : selectedUser.getTelephone());
                break;
            case "clinicalOwner":
                selected.setClinicalOwnerId(null == selectedUser ? null : selectedUser.getId());
                selected.setClinicalOwnerName(null == selectedUser ? null : selectedUser.getName());
                selected.setClinicalOwnerTel(null == selectedUser ? null : selectedUser.getTelephone());
                break;
            case "measuringOwner":
                selected.setMeasuringOwnerId(null == selectedUser ? null : selectedUser.getId());
                selected.setMeasuringOwnerName(null == selectedUser ? null : selectedUser.getName());
                selected.setMeasuringOwnerTel(null == selectedUser ? null : selectedUser.getTelephone());
                break;
            case "pmOwner":
                selected.setPmOwnerId(null == selectedUser ? null : selectedUser.getId());
                selected.setPmOwnerName(null == selectedUser ? null : selectedUser.getName());
                selected.setPmOwnerTel(null == selectedUser ? null : selectedUser.getTelephone());
        }


    }
    public List<UserAccount> completeOwnerList(String query) {
        if (ownerList == null) {
            return Collections.emptyList();
        }
        List<UserAccount> res = this.ownerList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        return res;
    }
    public void onAssetGroupChange() {

        if (!assetGroupSecondarySupport) return;

        if (this.selected.getAssetGroup() == null) {
            this.assetGroupSecondaryList = Collections.emptyList();
        } else {
            String msgType = "assetGroupSecondary_" + this.selected.getAssetGroup();
            this.assetGroupSecondaryList = i18nDao.getByMsgTypeAndSiteId(msgType, UserContextService.getCurrentUserAccount().getSiteId());
        }

    }
    @Override
    protected GenericRepository<AssetInfo> getDAO() {
        return null;
    }

    public void initOrgTree() {

        UserAccount loginUser = UserContextService.getCurrentUserAccount();
        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(loginUser.getSiteUID(), loginUser.getUid());
        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        orgTree = new DefaultTreeNode("root", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        rootNode.setSelectable(false);
        if (null != treeNode) {
            rootNode.getChildren().add(treeNode);
        }
        rootNode.setExpanded(true);
    }

    public void onSelectTreeNodeAsFilter(NodeSelectEvent event) {
        OrgInfo filterSiteOrg = null;
        if (null == event || null == event.getTreeNode() || null == event.getTreeNode().getData()) {
            filterSiteOrg = null;
        } else {
            filterSiteOrg = (OrgInfo) event.getTreeNode().getData();
        }
        filter.setSiteOrg(filterSiteOrg);
        filter.setClinicalDeptOrg(null);

        if (null == filterSiteOrg) {
            clinicalDeptListAsFilter = Collections.emptyList();
        } else {
            clinicalDeptListAsFilter = assetInfoService.getHierachyClinicalDeptList(filter.getSiteOrg().getSiteUID());
        }
    }

    public List<OrgInfo> autoCompleteDeptAsFilter(String query) {
        if (clinicalDeptListAsFilter.size() == 0) {
            return Collections.emptyList();
        }

        List<OrgInfo> res = clinicalDeptListAsFilter.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    public void onClinicalDeptChangeAsFilter() {

    }

    public DualListModel<AssetInfo> getAssets() {

        prepareFilters();

        Page<AssetInfo> page = dao.findBySearchFilter(this.searchFilters, pageRequest);
        List<AssetInfo> source = new ArrayList<>(page.getContent());
        source.removeAll(assets.getTarget());
        assets.setSource(source);

        return assets;
    }

    private void prepareFilters() {
        searchFilters = new ArrayList<>();
        SearchFilter searchFilter;

        searchFilter = new SearchFilter("tenantUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID());
        this.searchFilters.add(searchFilter);

        if (null != filter.getSiteOrg()) {
            List<String> orgUidList = orgInfoDao.getOrgInfoUidsByparentUid(filter.getSiteOrg().getUid());
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }

        if (null != filter.getClinicalDeptOrg()) {
            searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, filter.getClinicalDeptOrg().getUid()));
        }

        if (null != this.filter.getAssetGroup()) {
            searchFilter = new SearchFilter("assetGroup", SearchFilter.Operator.EQ, this.filter.getAssetGroup());
            this.searchFilters.add(searchFilter);
        }

        if (oneSearchStr != null && !oneSearchStr.isEmpty()) {
            searchFilters.add(new SearchFilter("name", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            searchFilters.add(new SearchFilter("departNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            searchFilters.add(new SearchFilter("financingNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            searchFilters.add(new SearchFilter("qrCode", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
        }
    }

    public void onTransfer(TransferEvent event) {

    }

    public void onTabChange(TabChangeEvent event) {
        if ("选择资产".equals(event.getTab().getTitle())) {
            return;
        }

        setDefaultValueForSelectedAssets();
    }

    private void setDefaultValueForSelectedAssets() {
        List<AssetInfo> assetInfos = assets.getTarget();

        if (assetInfos.size() == 0) {
            return;
        }

        AssetInfo firstAsset = assetInfos.get(0);

        Object obj;
        obj = getSameValue(assets.getTarget(), "siteUID");
        if (null != obj) {
            OrgInfo foundOrgInfo = orgInfoDao.findByUid((String)obj);
            selected.setSiteOrg(foundOrgInfo);
        }

        obj = getSameValue(assets.getTarget(), "clinicalDeptUID");
        if (null != obj) {
            OrgInfo foundOrgInfo = orgInfoDao.findByUid((String)obj);
            selected.setClinicalDeptOrg(foundOrgInfo);
        }

        obj = getSameValue(assets.getTarget(), "assetGroup");
        if (null != obj) {
            selected.setAssetGroup((Integer)obj);
        }


    }

    private Object getSameValue(List<AssetInfo> assetInfos, String columnName) {
        try {
            Field field = AssetInfo.class.getDeclaredField(columnName);
            field.setAccessible(true);

            if (assetInfos.size() == 0) {
                return null;
            }

            if (assetInfos.size() == 1) {
                Object obj = field.get(assetInfos.get(0));
                return obj;
            }

            Object prevObj = field.get(assetInfos.get(0));

            for (int i = 1; i < assetInfos.size(); i++) {
                AssetInfo assetInfo = assetInfos.get(i);
                Object obj = field.get(assetInfo);

                if (!Objects.equals(obj, prevObj)) {
                    return null;
                }

                prevObj = obj;
            }

            return prevObj;

        } catch (NoSuchFieldException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    public void onSelectTreeNodeAsNewValue(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo siteOrg = (OrgInfo) node.getData();
        selected.setSiteOrg(siteOrg);

        selected.setClinicalDeptOrg(null);

        if (null == siteOrg) {
            clinicalDeptList = Collections.emptyList();
        } else {
            clinicalDeptList = assetInfoService.getHierachyClinicalDeptList(selected.getSiteOrg().getSiteUID());
        }

    }

    public List<OrgInfo> autoCompleteDept(String query) {
        if (clinicalDeptList.size() == 0) {
            return Collections.emptyList();
        }

        List<OrgInfo> res = clinicalDeptList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    public boolean displayControl(String name) {
        for (String key: selectedColumns) {
            if (key.equals(name)) {
                return true;
            }
        }

        return false;
    }

    public void commitBulkEditSite() {

        if (!checkIsValidInput()) {
            return;
        }

        //commit changes after validated
        List<AssetInfo> selectedAssets = assets.getTarget();

        //验证
        if(!assetInfoBulkEditCheckService.verify(selectedAssets)){
            WebUtil.addErrorMessage("有设备正在调剂中，不能更换院区!");
            return;
        }

        if(!assetInfoBulkEditCheckService.verifyForMaintain(selectedAssets)){
            WebUtil.addErrorMessage("有设备正在保养中，不能更换院区！");
            return;
        }

        if(!assetInfoBulkEditCheckService.verifyForInventory(selectedAssets)){
            WebUtil.addErrorMessage("有设备正在盘点中，不能更换院区!");
            return;
        }

        for(AssetInfo as:selectedAssets){
            boolean assetFlag =true;
            List<V2_ServiceRequest> serviceRequestList =v2ServiceRequestRepository.findByAssetId(as.getId());
                 if(CollectionUtils.isNotEmpty(serviceRequestList)){
                        for(V2_ServiceRequest service: serviceRequestList){
                            if(service.getCloseTime()==null){
                                assetFlag=false;
                            }
                        }
                        if(assetFlag){
                            continue;
                        }else{
                            WebUtil.addErrorMessage("有设备正在维修中，不能更换院区!");
                            return;
                        }
                 }

//            List<V2_WorkOrder> pmOrderList = v2WorkerOrderRepository.findByAssetId(as.getId());
//            if(CollectionUtils.isNotEmpty(pmOrderList)){
//                for(V2_WorkOrder workerOder : pmOrderList){
////                    List<String> ids=new ArrayList<>();
////                    ids.add(workerOder.getSrId());
////                    List<V2_ServiceRequest> serviceRequestList = v2ServiceRequestRepository.findByIdIn(ids);
//                        if("已取消".equals(workerOder.getCurrentStepName()) || "反馈".equals(workerOder.getCurrentStepName()) || "3分".equals(workerOder.getCurrentStepName())){
//                            continue;
//                        }else{
//                            WebUtil.addErrorMessage("有设备正在维修中，不能更换院区!");
//                            return;
//                        }
//                    }
//            }
        }

        //临床助手检查进行中
        Optional<String> optionalResult = assetInfoBulkEditCheckService.verifyForNurseSite(selectedAssets);
        if (optionalResult.isPresent()) {
            WebUtil.addErrorMessage(optionalResult.get());
            return;
        }

        try {
            assetInfoBulkEditService.commitBulkEditSite(selectedAssets, selected, selectedColumns);

            assets.setSource(new ArrayList<>());
            assets.setTarget(new ArrayList<>());

            WebUtil.addSuccessMessage("批量修改资产院区成功!");
        } catch(Exception e) {
            WebUtil.addErrorMessage(e.getMessage());
        }

    }

    public void commitBulkEdit() {

//        if (!checkIsValidInput()) {
//            return;
//        }

        //commit changes after validated
        List<AssetInfo> selectedAssets = assets.getTarget();
        if (null != gbCodeName) {
            GbCode gbCode = gbDao.getGbCode(gbCodeName.split("/")[0]);
            if (null == gbCode) {
                WebUtil.addErrorMessage(WebUtil.getMessage("无效的国标码"));
                return ;
            }
            selected.setNationalStandardCode(selectedGb.getCode());
        }
        selected.setQrCode(qrCode);
        assetInfoBulkEditService.commitBulkEdit(selectedAssets, selected, selectedColumns);

        assets.setSource(new ArrayList<>());
        assets.setTarget(new ArrayList<>());
        selected = new AssetInfo();
        WebUtil.addSuccessMessage("批量修改资产属性成功!");

    }

    private boolean checkIsValidInput() {
        if (assets.getTarget().size() == 0) {
            WebUtil.addErrorMessage("请选择相应资产!");
            return false;
        }

        if (null == this.selected.getSiteOrg()) {
            WebUtil.addErrorMessage("请选择院区!");
            return false;
        }

        if (null == this.selected.getClinicalDeptOrg()) {
            WebUtil.addErrorMessage("请选择科室!");
            return false;
        }

        return true;
    }

    public boolean disableBulkEditSiteButton() {
        if (assets.getTarget().size() == 0) {
            return true;
        }

        return false;
    }

    public boolean disableBulkEditButton() {
        if (assets.getTarget().size() == 0) {
            return true;
        }

        if (selectedColumns.size() == 0) {
            return true;
        }

        return false;
    }

}
