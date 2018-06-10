package com.ge.apm.view.asset;

import com.ge.apm.dao.ContractInfoItemRepository;
import com.ge.apm.dao.ContractInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SupplierRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.ContractInfo;
import com.ge.apm.domain.ContractInfoItem;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.Supplier;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.AssetInfoMgrService;
import com.ge.apm.service.asset.AssetInfoService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.ContractInfoService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Arrays;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import webapp.framework.web.mvc.ServerEventInterface;

@ManagedBean
@ViewScoped
public class ContractInfoEditController extends GenericCRUDUUIDController<ContractInfo> implements ServerEventInterface {

    private ContractInfoRepository dao = null;
    private ContractInfoItemRepository contractItemDao = null;
    private SupplierRepository supplierDao = null;
    private OrgInfoRepository orgDao = null;
    private UserAccountRepository userDao = null;
    private UaaService uaaService;
    private AssetInfoMgrService aimService;
    private BlobFileService blobService;
    private AssetInfoService assetInfoService;
    private ContractInfoService contractService;

    private List<V2_BlobObject> attachements;
    private List<Supplier> supplierList = Collections.emptyList();
    private Map<String, Object> selectedObjectMap = new HashMap();

    private TreeNode orgTree;

    private List<String> contractType;
    private List<OrgInfo> clinicalDeptList;

    private List<ContractInfoItem> contractItemsList;
    private ContractInfoItem selectedContractItem;

    private List<V2_BlobObject> uploadedFileObjects = new ArrayList();
    private String attachmentType;

    @Override
    protected void init() {
        dao = WebUtil.getBean(ContractInfoRepository.class);
        contractItemDao = WebUtil.getBean(ContractInfoItemRepository.class);
        supplierDao = WebUtil.getBean(SupplierRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        aimService = WebUtil.getBean(AssetInfoMgrService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        assetInfoService = WebUtil.getBean(AssetInfoService.class);
        contractService = WebUtil.getBean(ContractInfoService.class);

        String encodeStr = WebUtil.getRequestParameter("str");
        String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "action");

        if ("Create".equalsIgnoreCase(actionName)) {
            try {
                prepareCreate();
                selectedObjectMap.put("selectedOrg", orgDao.findByUid(UserContextService.getCurrentUserAccount().getSiteUID()));
                changeContractOrg((OrgInfo) selectedObjectMap.get("selectedOrg"));
                attachements = new ArrayList();
                contractItemsList = new ArrayList();
            } catch (InstantiationException ex) {
                Logger.getLogger(ContractInfoEditController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ContractInfoEditController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if ("Edit".equalsIgnoreCase(actionName)) {
            String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
            this.selected = dao.findById(selectedId);
            prepareEdit();
            attachements = blobService.findBlobObjectsByBoUid(selected.getId());
            contractItemsList = contractService.getContractItems(selected.getId());
            selectedObjectMap.put("supplier", selected.getSupplierUid() == null ? null : supplierDao.findByUid(selected.getSupplierUid()));
            selectedObjectMap.put("bidWinner", selected.getBidWinner() == null ? null : supplierDao.findByUid(selected.getBidWinner()));
            selectedObjectMap.put("selectedOrg", selected.getSiteUID() == null ? null : orgDao.findByUid(selected.getSiteUID()));
            selectedObjectMap.put("applyDept", selected.getApplyDept() == null ? null : orgDao.findByUid(selected.getApplyDept()));

        }

        clinicalDeptList = assetInfoService.getClinicalDeptList(selected.getSiteUID());
        supplierList = aimService.getSuplierListBySiteUid(selected.getTenantUID(), selected.getSiteUID());
        contractType = Arrays.stream(ContractInfo.ContractType.values()).map((Enum<ContractInfo.ContractType> item) -> item.toString()).collect(Collectors.toList());
        initOrgTree();
    }

    @Override
    protected ContractInfoRepository getDAO() {
        return dao;
    }

    private void changeContractOrg(OrgInfo orgInfo) {
        selected.setTenantUID(orgInfo.getTenantUID());
        selected.setHospitalUID(orgInfo.getHospitalUID());
        selected.setInstitutionUID(orgInfo.getInstitutionUID());
        selected.setSiteUID(orgInfo.getSiteUID());
        clinicalDeptList = assetInfoService.getClinicalDeptList(selected.getSiteUID());
        selectedObjectMap.put("applyDept", null);
    }

    public void onSelectedObjectChange(String idx) {
        switch (idx) {
            case "supplier":
                Supplier supplier = (Supplier) selectedObjectMap.get(idx);
                selected.setSupplierUid(supplier == null ? null : supplier.getUid());
                break;
            case "manufacture":
                Supplier manufacturer = (Supplier) selectedObjectMap.get(idx);
                selectedContractItem.setManufacturer(manufacturer == null ? null : manufacturer.getUid());
                break;
            case "bidWinner":
                Supplier bidWinner = (Supplier) selectedObjectMap.get(idx);
                selected.setBidWinner(bidWinner == null ? null : bidWinner.getUid());
                selected.setBidWinnerPerson(bidWinner.getContactor());
                selected.setBidWinnerTelphone(bidWinner.getTel());
                break;
            case "applyDept":
                OrgInfo clinicalDept = (OrgInfo) selectedObjectMap.get(idx);
                selected.setApplyDept(clinicalDept == null ? null : clinicalDept.getUid());
                break;
        }
    }

    public List<OrgInfo> autoCompleteDept(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }
        List<OrgInfo> res = this.clinicalDeptList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    public void uploadDataFile(FileUploadEvent event) {
        uploadedFileObjects.add(blobService.uploadFile(event.getFile()));
    }

    public void finishUpload() {
        uploadedFileObjects.stream().forEach(item -> {
            item.setBoSubType(Strings.isNullOrEmpty(attachmentType) ? ContractInfo.AttachmentType.Other.toString() : attachmentType);
            item.setBoType(V2_BlobObject.BO_TYPE_AssetContract);
        });
        attachements.addAll(uploadedFileObjects);
        uploadedFileObjects.clear();
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        changeContractOrg(orgInfo);
        selectedObjectMap.put("selectedOrg", orgInfo);
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

    public List<Supplier> autoCompleteSupplier(String query) {
        List<Supplier> res = supplierList.stream().filter(item -> Arrays.asList("1", "3").contains(item.getSupplierType())).filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        attachements = attachements.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toList());
    }

    public void removeUploadedFileObjects(String blobId) {
        blobService.deleteBlobFileById(blobId);
        uploadedFileObjects = uploadedFileObjects.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toList());
    }

    public List<I18nMessage> getContractTypeList() {
        return FieldValueMessageController.doGetFieldValueList("contractInfoType", -1).stream()
                .filter(item -> contractType.contains(item.getMsgKey())).collect(Collectors.toList());
    }

    public void cancelUpload() {
        uploadedFileObjects.stream().forEach(item -> {
            blobService.deleteBlobFile(item);
        });
        uploadedFileObjects.clear();
    }

    public void saveContract() {
        selected = dao.save(this.selected);
        attachements.stream().forEach(item -> {
            item.setBoUuid(selected.getId());
        });
        contractItemsList.stream().filter(item -> Strings.isNullOrEmpty(item.getContractId())).forEach(item -> {
            item.setContractId(selected.getId());
        });
        contractItemDao.save(contractItemsList);
        blobService.saveBlobObject(attachements);
        WebUtil.addSuccessMessage(WebUtil.getMessage("DataSavedOK"));
    }

    public void onEstimatedPriceChange() {
        if (null == selected.getEstimatedPrice()) {
            selected.setPriceType(null);
        } else if (selected.getEstimatedPrice() < 1) {
            selected.setPriceType(ContractInfo.PriceType.LT1.toString());
        } else if (selected.getEstimatedPrice() < 5) {
            selected.setPriceType(ContractInfo.PriceType.BT1_5.toString());
        } else if (selected.getEstimatedPrice() < 50) {
            selected.setPriceType(ContractInfo.PriceType.BT5_50.toString());
        } else {
            selected.setPriceType(ContractInfo.PriceType.GT50.toString());
        }
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        AssetInfo asset = (AssetInfo) eventObject;
        if (asset == null) {
            return;
        }
        if (contractItemsList.stream().anyMatch(item -> Objects.equals(item.getAssetUid(), asset.getUid()))) {
            WebUtil.addErrorMessage("该设备已经存在");
        } else {
            ContractInfoItem newItem = new ContractInfoItem();
            newItem.setAssetUid(asset.getUid());
            newItem.setAssetInfo(asset);
            newItem.setContractId(selected.getId());
            newItem.setItemType(ContractInfoItem.ContractInfoItemType.ExistAsset.toString());
            contractItemsList.add(newItem);
        }
    }

    public void remomveContractItem(ContractInfoItem item) {
//        ContractInfoItem removeItem = this.contractItemDao.findById(itemId);
        contractItemsList.remove(item);
        if(!Strings.isNullOrEmpty(item.getId())){
            contractItemDao.delete(item);
        }
    }

    public String getUserNameByUid(String orgUid) {
        UserAccount user = userDao.getByUid(orgUid);
        return user == null ? null : user.getName();
    }

    public void viewContractItemPage(ContractInfoItem item) {
        AssetInfo asset = item.getAssetInfo();
        String url = "actionName=View&selectedid="
                + asset.getId().toString()
                + "&asset_name=" + asset.getName()
                + "&terminate=" + !asset.getIsValid();
        url = "../info/AssetDetail.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }
    
    public void savePurchaseItem(){
        contractItemsList.add(selectedContractItem);
    }
    
    public void cancelPurchaseItem(){
        selectedContractItem = null;
    }
    public void prepareAddPurchaseAsset(){
        selectedContractItem = new ContractInfoItem();
        selectedContractItem.setItemType(ContractInfoItem.ContractInfoItemType.Purchase.toString());
    }
    
    public void prepareEditPurchaseAsset(ContractInfoItem item){
        selectedContractItem = item;
        selectedObjectMap.put("manufacture", selectedContractItem.getManufacturer() == null ? null : supplierDao.findByUid(selectedContractItem.getManufacturer()));
    }

    //getter and setter
    public Map<String, Object> getSelectedObjectMap() {
        return selectedObjectMap;
    }

    public void setSelectedObjectMap(Map<String, Object> selectedObjectMap) {
        this.selectedObjectMap = selectedObjectMap;
    }

    public List<V2_BlobObject> getAttachements() {
        return attachements;
    }

    public List<Supplier> getSupplierList() {
        return supplierList;
    }

    public TreeNode getOrgTree() {
        return orgTree;
    }

    public void setOrgTree(TreeNode orgTree) {
        this.orgTree = orgTree;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public List<V2_BlobObject> getUploadedFileObjects() {
        return uploadedFileObjects;
    }

    public List<ContractInfoItem> getContractItemsList() {
        return contractItemsList;
    }

    public void setContractItemsList(List<ContractInfoItem> contractItemsList) {
        this.contractItemsList = contractItemsList;
    }

    public ContractInfoItem getSelectedContractItem() {
        return selectedContractItem;
    }

    public void setSelectedContractItem(ContractInfoItem selectedContractItem) {
        this.selectedContractItem = selectedContractItem;
    }

}
