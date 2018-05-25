package com.ge.apm.view.asset;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.ge.apm.dao.ArrivalListItemRepository;
import com.ge.apm.dao.ArrivalListRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.QrCodeLibRepository;
import com.ge.apm.dao.SupplierRepository;
import com.ge.apm.domain.ArrivalList;
import com.ge.apm.domain.ArrivalListItem;
import com.ge.apm.domain.GbCode;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.Supplier;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.AssetFinacingNumGenerateService;
import com.ge.apm.service.asset.AssetInstallationService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.GbCodeService;
import com.ge.apm.service.asset.AutoCompleteService;
import com.ge.apm.service.orginfo.OrgInfoService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparator;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;

import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

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
    private Supplier maitanance;

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

    private List<V2_BlobObject> attachList;

    private BlobFileService blobService;

    private String supplierName;
    SupplierRepository supplierDao = null;
    List<Supplier> suppliersList;
    private boolean needInstorageCheck;
    private AutoCompleteService autoCompleteService;

    @Override
    protected void init() {

        dao = WebUtil.getBean(ArrivalListRepository.class);
        itemDao = WebUtil.getBean(ArrivalListItemRepository.class);
        aiService = WebUtil.getBean(AssetInstallationService.class);
        autoCompleteService = WebUtil.getBean(AutoCompleteService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgService = WebUtil.getBean(OrgInfoService.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        qrcodeDao = WebUtil.getBean(QrCodeLibRepository.class);
        supplierDao = WebUtil.getBean(SupplierRepository.class);
        assetFinacingNumGenerateService = WebUtil.getBean(AssetFinacingNumGenerateService.class);
        gbCodeService = WebUtil.getBean(GbCodeService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        TreeNode subTree = uaaService.getOrgTreeByParentIdWithSelectedChildren(UserContextService.getCurrentUserAccount().getSiteUID(), UserContextService.getCurrentUserAccount().getUid());
        orgTree = new DefaultTreeNode("org", null, null);
        orgTree.getChildren().add(subTree);
        attachList = new ArrayList();

        String encodeStr = WebUtil.getRequestParameter("str");
        String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "action");
        
        if ("Create".equalsIgnoreCase(actionName)) {
            try {
                String copyFrom = (String) UrlEncryptController.getValueFromMap(encodeStr, "copyFrom");
                prepareCreate();
                if (!Strings.isNullOrEmpty(copyFrom)) {
                    copyFiledsValueById(copyFrom);
                }

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
                supplierName = supplier==null? null:supplier.getName();
            }
            initEasyInstallItem();
            this.changeFunctionGroup();
            attachList = aiService.getPicList(selected.getId());
//            if (ArrivalList.ProcessType.Easy.toString().equals(selected.getProcessType())) {
//                initEasyInstallItem();
//                this.changeFunctionGroup();
//
//            }
            prepareEdit();
        }

        suppliersList = aiService.getSupplierListBySiteUid(selected.getSiteUID());
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        needInstorageCheck=userContextService.needInstorageCheck();
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
        //remove default value
//        object.setProcessType(ArrivalList.ProcessType.Normal.toString());
        object.setAgent(user.getName());
        object.setArrivalDate(new Date());
        selectedOrg = aiService.getOrgInfoByUid(user.getSiteUID());

    }

    public void applyChange() {
        onSupplierChange();
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
        saveArrivalList();
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
        if (selected.getContractPrice() != null) {
            easyInstallItem.setPurchasePrice(selected.getContractPrice() / selected.getQuantity());
            easyInstallItem.setIsPm(true);
            easyInstallItem.setIsChecked(true);
        }
        String maitananceName=easyInstallItem.getMaitanance();
        if(maitananceName!=null){
        	this.maitanance=new Supplier();
        	maitanance.setName(maitananceName);
        }
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
        if (easyInstallItem.getWarrantyYear() != null) {
            Integer monthLong = Double.valueOf(easyInstallItem.getWarrantyYear() * 12).intValue();
            Calendar calender = Calendar.getInstance();
            calender.setTime(new Date());
            calender.add(Calendar.MONTH, monthLong);
            easyInstallItem.setFactoryWarrantyDate(calender.getTime());
        }
        saveList.forEach(item -> {
            item.setNationalStandardCode(easyInstallItem.getNationalStandardCode());
            item.setFunctionGroup(easyInstallItem.getFunctionGroup());
            item.setAssetCode68(easyInstallItem.getAssetCode68());
            item.setRegistrationNo(easyInstallItem.getRegistrationNo());
            item.setMaitanance(easyInstallItem.getMaitanance());
            item.setPurchaseDate(easyInstallItem.getPurchaseDate());
            item.setWarrantyDate(easyInstallItem.getWarrantyDate());
            item.setPurchasePrice(easyInstallItem.getPurchasePrice());
            item.setAssetUsage(easyInstallItem.getAssetUsage());
            item.setCapitalSource(easyInstallItem.getCapitalSource());
            item.setMeasureGrade(easyInstallItem.getMeasureGrade());
            item.setRiskGrade(easyInstallItem.getRiskGrade());
            item.setEmergencyEquipment(easyInstallItem.getEmergencyEquipment());
            item.setImportEquipment(easyInstallItem.getImportEquipment());
            item.setMeasuringItemCategory(easyInstallItem.getMeasuringItemCategory());
            item.setAccountingCategory(easyInstallItem.getAccountingCategory());
            item.setSalvageValue(easyInstallItem.getSalvageValue());
            item.setLifecycleInYear(easyInstallItem.getLifecycleInYear());
            item.setDepreciationMethod(easyInstallItem.getDepreciationMethod());
            item.setIsPm(easyInstallItem.getIsPm());
            item.setIsChecked(easyInstallItem.getIsChecked());
        });
        itemDao.save(saveList);
        WebUtil.addSuccessMessage("已保存");
    }

    public List<ArrivalListItem> getNewInstallAssetList() {
        if (null != arrivalItemList) {
            return arrivalItemList.stream().filter(item -> item.getInstallStatus().equals(ArrivalListItem.INSTALL_STATUS_NEW)).collect(Collectors.toList());
        } else {
            return new ArrayList();
        }
    }

    public void takeWorkOrder(String itemId) {
        ArrivalListItem item = itemDao.findById(itemId);
        item.setCurrentPersonUID(UserContextService.getCurrentUserAccount().getUid());
        aiService.reAssignWorkOrder(item);
        arrivalItemList = itemDao.findByArrivalListId(selected.getId());
//        cancel();
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

    private void copyFiledsValueById(String copyFrom) {
        ArrivalList srcList = dao.findById(copyFrom);
        if (null != srcList) {
            selected.setHospitalUID(srcList.getHospitalUID());
            selected.setAssetName(srcList.getAssetName());
            selected.setAssetGroup(srcList.getAssetGroup());
            selected.setArrivalDate(srcList.getArrivalDate());
            selected.setFunctionType(srcList.getFunctionType());
            selected.setManufacture(srcList.getManufacture());
            selected.setOrigin(srcList.getOrigin());
            selected.setInvoiceNum(srcList.getInvoiceNum());
            selected.setContractNum(srcList.getContractNum());
            selected.setContractPrice(srcList.getContractPrice());
            selected.setVendor(srcList.getVendor());
            selected.setAgent(srcList.getAgent());
            selected.setTenantUID(srcList.getTenantUID());
            selected.setInstitutionUID(srcList.getInstitutionUID());
            selected.setHospitalUID(srcList.getHospitalUID());
            selected.setSiteUID(srcList.getSiteUID());
        }

    }

    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        attachList.removeAll(attachList.stream().filter(item -> item.getId().equals(blobId)).collect(Collectors.toList()));
    }

    public void handleFileUpload(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        String type = event.getComponent().getId();
        type = type.substring(type.indexOf("type") + 4);
        if (null != blobObject) {
            attachList.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_TYPE_ArrivalList);
            blobObject.setBoUuid(selected.getId());
            blobObject.setBoSubType(type);
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public List<V2_BlobObject> getAttachList(String type) {
        return attachList == null ? null : attachList.stream().filter(item -> item.getBoSubType() == null || item.getBoSubType().equals(type)).collect(Collectors.toList());
    }

    public List<String> completeGbName(String prefix) {
        return gbCodeService.completeGbName(prefix);
    }

    public OrgInfo getOrgInfoByUid(String orgUid) {
        return aiService.getOrgInfoByUid(orgUid);
    }

    public Integer getInstalledCount() {
        if (arrivalItemList != null) {
            return arrivalItemList.stream().filter(item -> ArrivalListItem.INSTALL_STATUS_FINISHED.equals(item.getInstallStatus())
                    || ArrivalListItem.INSTALL_STATUS_INSTORAGE.equals(item.getInstallStatus())).collect(Collectors.toList()).size();
        } else {
            return 0;
        }
    }

    public void saveArrivalList() {
        this.save();
    }

    public List<Supplier> autoCompleteVendor(String query) {
        return autoCompleteService.autoCompleteVendor(query, suppliersList);
    }
    
    public List<Supplier> autoCompleteMaintainer(String query) {
        return autoCompleteService.autoCompleteMaintainer(query, suppliersList);
    }

    public void onSupplierChange() {
    	if (null != supplier) {
            selected.setSupplierId(supplier.getId());
            selected.setVendor(supplier.getName());
        }
    }
    
    public void onMaintainerChange() {
    	if (null != maitanance) {
            this.easyInstallItem.setMaitanance(maitanance.getName());
        }
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

    public Supplier getMaitanance() {
		return maitanance;
	}

	public void setMaitanance(Supplier maitanance) {
		this.maitanance = maitanance;
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

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

	public boolean isNeedInstorageCheck() {
		return needInstorageCheck;
	}

	public void setNeedInstorageCheck(boolean needInstorageCheck) {
		this.needInstorageCheck = needInstorageCheck;
	}

}
