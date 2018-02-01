package com.ge.apm.view.asset;

import com.ge.apm.dao.ArrivalListItemRepository;
import com.ge.apm.dao.ArrivalListRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.QrCodeLibRepository;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import com.ge.apm.domain.ArrivalList;
import com.ge.apm.domain.ArrivalListItem;
import com.ge.apm.domain.GbCode;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.QrCodeLib;
import com.ge.apm.domain.Supplier;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AssetFinacingNumGenerateService;
import com.ge.apm.service.asset.AssetInstallationService;
import com.ge.apm.service.asset.GbCodeService;
import com.ge.apm.service.orginfo.OrgInfoService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class AssetArrivedListEditController extends GenericCRUDUUIDController<ArrivalList> {

    private ArrivalListRepository dao = null;
    private ArrivalListItemRepository itemDao = null;

    private OrgInfo selectedOrg;

    private QrCodeLibRepository qrcodeDao;
    private List<ArrivalListItem> arrivalItemList;

    private AssetInstallationService aiService;
    private UaaService uaaService;
    private TreeNode orgTree;

    private Supplier supplier;

    private TreeNode selectedNode;

    private ArrivalListItem easyInstallItem;

    private OrgInfoService orgService;

    private List<I18nMessage> code68List;

    private I18nMessageRepository i18nDao;
    private Integer installCount;
    private Map<String, Boolean> generateMap;
    private Boolean generatePmorder;

    private String nationalStandardCode;
    private AssetFinacingNumGenerateService assetFinacingNumGenerateService;
    private GbCodeService gbCodeService;
    private TreeNode gbTree;

    @Override
    protected void init() {

        dao = WebUtil.getBean(ArrivalListRepository.class);
        itemDao = WebUtil.getBean(ArrivalListItemRepository.class);
        aiService = WebUtil.getBean(AssetInstallationService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgService = WebUtil.getBean(OrgInfoService.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        qrcodeDao = WebUtil.getBean(QrCodeLibRepository.class);
        assetFinacingNumGenerateService = WebUtil.getBean(AssetFinacingNumGenerateService.class);
        gbCodeService = WebUtil.getBean(GbCodeService.class);
        TreeNode subTree = uaaService.getOrgTreeByParentIdWithSelectedChildren(UserContextService.getCurrentUserAccount().getSiteUID(), UserContextService.getCurrentUserAccount().getUid());
        orgTree = new DefaultTreeNode("org", null, null);
        orgTree.getChildren().add(subTree);

        String encodeStr = WebUtil.getRequestParameter("str");
        String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "action");
        if ("Create".equalsIgnoreCase(actionName)) {
            try {
                prepareCreate();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(AssetInfoController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if ("Edit".equalsIgnoreCase(actionName)) {
            String listId = (String) UrlEncryptController.getValueFromMap(encodeStr, "listId");
            this.selected = dao.findById(listId);
            arrivalItemList = itemDao.findByArrivalListId(selected.getId());
            selectedOrg = aiService.getOrgInfoByUid(selected.getSiteUID());
            if (selected.getSupplierId() != null) {
                supplier = aiService.getSupplierById(selected.getSupplierId());
            }
            if (ArrivalList.ProcessType.Easy.toString().equals(selected.getProcessType())) {
                initEasyInstallItem();
                this.changeFunctionGroup();

            }
            prepareEdit();
        }
    }

    @Override
    protected ArrivalListRepository getDAO() {
        return dao;
    }

    @Override
    public void onBeforeNewObject(ArrivalList object) {
        UserAccount user = UserContextService.getCurrentUserAccount();
        object.setCreatorUID(user.getUid());
        object.setStatus(1);
        object.setProcessType(ArrivalList.ProcessType.Normal.toString());
        selectedOrg = aiService.getOrgInfoByUid(user.getSiteUID());
    }

    public void applyChange() {
        this.save();
    }

    @Override
    public void onBeforeSave(ArrivalList object) {
        object.setTenantUID(selectedOrg.getTenantUID());
        object.setInstitutionUID(selectedOrg.getInstitutionUID());
        object.setHospitalUID(selectedOrg.getHospitalUID());
        object.setSiteUID(selectedOrg.getSiteUID());
    }

    public void cancel() {
        this.selected = null;
    }

    @Override
    public void delete() {
        if (selected.getStatus() == 1) {
            dao.delete(selected);
            this.selected = null;
        } else {
            WebUtil.addErrorMessage("到货清单已经提交,不能删除");
        }
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        this.arrivalItemList = null;
    }

    public void submitArrivalList() {
        onBeforeSave(selected);
        dao.save(selected);
        if (aiService.submitArrivalList(selected.getId())) {
            WebUtil.addSuccessMessage("到货清单已经提交");
        } else {
            WebUtil.addErrorMessage("到货清单提交失败");
        };
        arrivalItemList = itemDao.findByArrivalListId(selected.getId());
        initEasyInstallItem();
        selected = dao.findById(selected.getId());
    }

    public void onSelectOrgFromTree(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        selectedOrg = (OrgInfo) node.getData();
    }

    public void onSupplierChange() {
        if (null != supplier) {
            selected.setSupplierId(supplier.getId());
            selected.setVendor(supplier.getName());
        }
    }

    public UserAccount getUserAccountByUid(String uid) {
        return aiService.getUserAccountByUid(uid);
    }

    public void prepareEasyInstall() {
        arrivalItemList = itemDao.findByArrivalListId(selected.getId());
        initEasyInstallItem();
        installCount = 1;
        generatePmorder = null;

        if (generateMap == null) {
            generateMap = new HashMap();
        } else {
            generateMap.clear();
        }
        saveEasyItem();
    }

    private Boolean isAvailableQrcode(String qrcode) {
        if (Strings.isNullOrEmpty(qrcode)) {
            return true;
        }

        QrCodeLib qrCodeLib = qrcodeDao.findByQrCode(qrcode);
        if (qrCodeLib == null) {
            WebUtil.addErrorMessage("错误:" + WebUtil.getMessage("InvalidQRCode") + qrcode);
            return false;
        }
        if (!qrCodeLib.getSiteUID().equals(selected.getSiteUID())) {
            WebUtil.addErrorMessage("错误:" + WebUtil.getMessage("WrongHospitalQRCode"));
            return false;
        }
        if (qrCodeLib.getStatus() != 1 && qrCodeLib.getStatus() != 2) {
            WebUtil.addErrorMessage("错误:" + WebUtil.getMessage("AlreadyUsingQrCode"));
            return false;
        }

        return true;
    }

    public void submitEasyInstall() {
        if (installCount == 1 && !Strings.isNullOrEmpty(easyInstallItem.getQrCode())) {
            if (!isAvailableQrcode(easyInstallItem.getQrCode())) {
                return;
            }
        }

        StringBuilder financingNumErrorMsg = new StringBuilder();

        String prefix = easyInstallItem.getFinancingNum();
        List<ArrivalListItem> submitItem = getNewInstallAssetList().subList(0, installCount);
        for (ArrivalListItem item : submitItem) {
            item.setClinicalDeptUID(easyInstallItem.getClinicalDeptUID());
            item.setAliasName(easyInstallItem.getAliasName());
            item.setAssetOwnerUID(easyInstallItem.getAssetOwnerUID());
            item.setAssetOwnerUID2(easyInstallItem.getAssetOwnerUID2());
            item.setAssetOwnerUID3(easyInstallItem.getAssetOwnerUID3());
            item.setClinicalOwnerUid(easyInstallItem.getClinicalOwnerUid());
            item.setLocationCode(easyInstallItem.getLocationCode());
            item.setLocationName(easyInstallItem.getLocationName());
            item.setComments(easyInstallItem.getComments());
            item.setFinancingNum(easyInstallItem.getFinancingNum());
            item.setSerialNum(easyInstallItem.getSerialNum());
            item.setDepartNum(easyInstallItem.getDepartNum());

            if (installCount == 1) {
                item.setQrCode(easyInstallItem.getQrCode());
                item.setFinancingNum(easyInstallItem.getFinancingNum());
            } else {
                if (generateMap.get("generateQrCode")) {
                    item.setQrCode(aiService.getNewQrcode(easyInstallItem.getArrivalList().getTenantUID(), easyInstallItem.getArrivalList().getSiteUID()).getQrCode());
                }
                if (generateMap.get("generateFinancingNum")) {
                    String financingNum = gennerateFinancingNum(easyInstallItem, prefix);
                    if (null != financingNum && financingNum.startsWith("error:")) {
                        String errorMsg = financingNum.replace("error:", "");
                        if (financingNumErrorMsg.lastIndexOf(errorMsg) < 0) {
                            financingNumErrorMsg.append(errorMsg).append("\r\n");
                        }
                        financingNum = "";
                    }
                    item.setFinancingNum(financingNum);
                }
            }

            aiService.saveOrUpdateItem(item);
            if (!aiService.submitInstallItem(item.getId(), true, generatePmorder)) {
                WebUtil.addErrorMessage("提交失败");
                break;
            }
        }
        if (financingNumErrorMsg.length() > 0) {
            WebUtil.addErrorMessage(financingNumErrorMsg.toString());
        }
        WebUtil.addSuccessMessage("提交成功");
        arrivalItemList = itemDao.findByArrivalListId(selected.getId());
        initEasyInstallItem();
    }

    public String gennerateFinancingNum(ArrivalListItem item, String prifix) {
        if (prifix != null) {
            if (prifix.length() > 3) {
                prifix = prifix.substring(0, 3);
            }
        } else {
            prifix = "";
        }

        String financingNum = assetFinacingNumGenerateService.autoGenerateFinacingNum(null,
                item.getNationalStandardCode().split("/")[0], item.getClinicalDeptUID() == null ? item.getArrivalList().getSiteUID() : item.getClinicalDeptUID(), item.getParentAssetId(), prifix);
        return financingNum;
    }

    public void generateFinacialNum() {
        String financingNum = gennerateFinancingNum(easyInstallItem, easyInstallItem.getFinancingNum());
        if (null != financingNum && financingNum.startsWith("error:")) {
            WebUtil.addErrorMessage(financingNum.replace("error:", ""));
            financingNum = "";
        }
        easyInstallItem.setFinancingNum(financingNum);
    }

    public void renewQrcode() {
        if (null != easyInstallItem) {
            easyInstallItem.setQrCode(aiService.getNewQrcode(easyInstallItem.getArrivalList().getTenantUID(), easyInstallItem.getArrivalList().getSiteUID()).getQrCode());
        }
    }

    public void cancelSaveEasyInstall() {
        initEasyInstallItem();
    }

    private void initEasyInstallItem() {
        easyInstallItem = getNewInstallAssetList().stream().findAny().orElse(new ArrivalListItem());
        nationalStandardCode = easyInstallItem.getNationalStandardCode();
    }

    public List<UserAccount> getOwnerList() {
        return aiService.getWorkerList();
    }

    public List<UserAccount> getClinicalOwnerList() {
        if (null != easyInstallItem) {
            return aiService.getClinicalOwnerList(easyInstallItem.getClinicalDeptUID());
        }
        return null;
    }

    public void onFunctionGroupChange() {
        code68List = new ArrayList<>();
        this.easyInstallItem.setAssetCode68(null);

        this.changeFunctionGroup();
    }

    public void changeFunctionGroup() {
        Integer functionGroupId = this.easyInstallItem.getFunctionGroup();
        if (functionGroupId != null) {
            I18nMessage functionGroup = i18nDao.getByMsgTypeAndMsgKey("assetFunctionType", functionGroupId.toString());
            if (functionGroup != null) {
                code68List = i18nDao.getByMsgType(functionGroup.getMsgKey());
            }
        }
    }

    public void saveEasyItem() {
        easyInstallItem.setNationalStandardCode(nationalStandardCode == null ? "" : nationalStandardCode.split("/")[0]);
        List<ArrivalListItem> saveList = getNewInstallAssetList();
        saveList.forEach(item -> {
            item.setNationalStandardCode(easyInstallItem.getNationalStandardCode());
            item.setMaitanance(easyInstallItem.getMaitanance());
            item.setFunctionGroup(easyInstallItem.getFunctionGroup());
            item.setAssetCode68(easyInstallItem.getAssetCode68());
            item.setManufactDate(easyInstallItem.getManufactDate());
            item.setPurchaseDate(easyInstallItem.getPurchaseDate());
            item.setWarrantyYear(easyInstallItem.getWarrantyYear());
            item.setWarrantyDate(easyInstallItem.getWarrantyDate());
            item.setTerminateDate(easyInstallItem.getTerminateDate());
            item.setPurchasePrice(easyInstallItem.getPurchasePrice());
            item.setSalvageValue(easyInstallItem.getSalvageValue());
            item.setLifecycleInYear(easyInstallItem.getLifecycleInYear());
            item.setDepreciationMethod(easyInstallItem.getDepreciationMethod());
            item.setAssetUsage(easyInstallItem.getAssetUsage());
            item.setCapitalSource(easyInstallItem.getCapitalSource());
            item.setMeasureGrade(easyInstallItem.getMeasureGrade());
            item.setRiskGrade(easyInstallItem.getRiskGrade());
            item.setEmergencyEquipment(easyInstallItem.getEmergencyEquipment());
            item.setImportEquipment(easyInstallItem.getImportEquipment());
            item.setMeasuringItemCategory(easyInstallItem.getMeasuringItemCategory());
            item.setAccountingCategory(easyInstallItem.getAccountingCategory());
        });
        itemDao.save(saveList);
    }

    public List<ArrivalListItem> getNewInstallAssetList() {
        if (null != arrivalItemList) {
            return arrivalItemList.stream().filter(item -> item.getInstallStatus().equals(ArrivalListItem.INSTALL_STATUS_NEW)).collect(Collectors.toList());
        } else {
            return new ArrayList();
        }
    }

    public void onSelectGbTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        GbCode gbCode = (GbCode) node.getData();
        if (gbCode == null || gbCode.getId() == -1) {
            nationalStandardCode = "";
        } else {
            nationalStandardCode = gbCode.getCode() + "/" + gbCode.getName();
        }
    }

    public List<String> completeGbName(String prefix) {
        return gbCodeService.completeGbName(prefix);
    }

    public OrgInfo getOrgInfoByUid(String orgUid) {
        return aiService.getOrgInfoByUid(orgUid);
    }

    //geter and setter
    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public void setSelectedOrg(OrgInfo selectedOrg) {
        this.selectedOrg = selectedOrg;
    }

    public List<ArrivalListItem> getArrivalItemList() {
        return arrivalItemList;
    }

    public void setArrivalItemList(List<ArrivalListItem> arrivalItemList) {
        this.arrivalItemList = arrivalItemList;
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

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public ArrivalListItem getEasyInstallItem() {
        return easyInstallItem;
    }

    public void setEasyInstallItem(ArrivalListItem easyInstallItem) {
        this.easyInstallItem = easyInstallItem;
    }

    public List<I18nMessage> getCode68List() {
        return code68List;
    }

    public void setCode68List(List<I18nMessage> code68List) {
        this.code68List = code68List;
    }

    public Integer getInstallCount() {
        return installCount;
    }

    public void setInstallCount(Integer installCount) {
        this.installCount = installCount;
    }

    public Map<String, Boolean> getGenerateMap() {
        return generateMap;
    }

    public void setGenerateMap(Map<String, Boolean> generateMap) {
        this.generateMap = generateMap;
    }

    public String getNationalStandardCode() {
        return nationalStandardCode;
    }

    public void setNationalStandardCode(String nationalStandardCode) {
        this.nationalStandardCode = nationalStandardCode;
    }

    public TreeNode getGbTree() {
        if (gbTree == null) {
            TreeNode treeNode = gbCodeService.buildGbTree();
            gbTree = new DefaultTreeNode("root", null, null);
            gbTree.getChildren().add(treeNode);
            treeNode.setExpanded(true);
        }
        return gbTree;
    }

    public Boolean getGeneratePmorder() {
        return generatePmorder;
    }

    public void setGeneratePmorder(Boolean generatePmorder) {
        this.generatePmorder = generatePmorder;
    }
    
    

}
