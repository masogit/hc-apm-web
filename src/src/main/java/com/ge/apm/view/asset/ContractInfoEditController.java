package com.ge.apm.view.asset;

import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetInfoService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.ContractInfoService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.SupplierController;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.SystemEventAuditEnum;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import javaslang.Tuple2;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@ManagedBean
@ViewScoped
public class ContractInfoEditController extends GenericCRUDUUIDController<ContractInfo> implements ServerEventInterface {

    private ContractInfoRepository dao = null;
    private AssetInfoRepository assetDao = null;
    private ContractInfoItemRepository contractItemDao = null;
    private SupplierRepository supplierDao = null;
    private OrgInfoRepository orgDao = null;
    private UserAccountRepository userDao = null;
    private TenantInfoRepository tenantInfoDao;
    private UaaService uaaService;
    private BlobFileService blobService;
    private AssetInfoService assetInfoService;
    private ContractInfoService contractService;
    private SupplierController supplierController;
    private ArrivalListRepository arrivalListRepository;
    private ConfigItemValueRepository configItemValueRepository;
    private HttpServletRequest request;

    private List<V2_BlobObject> attachements;
    private List<Supplier> supplierList = Collections.emptyList();
    private List<Supplier> manufactureList = Collections.emptyList();
    private Map<String, Object> selectedObjectMap = new HashMap();

    private TreeNode orgTree;

    private List<OrgInfo> clinicalDeptList;
    private List<String> removedAttachments = new ArrayList();

    private List<ContractInfoItem> allItemList;
    private List<ContractInfoItem> removedItemList = new ArrayList();
    private ContractInfoItem selectedContractItem;

    private List<V2_BlobObject> uploadedFileObjects = new ArrayList();
    private List<V2_BlobObject> uploadedInvoiceFileObjects = new ArrayList();
    private List<V2_BlobObject> invoiceAttachments = new ArrayList<>();
    private String attachmentType;

    private List<Tuple2<AssetInfo, V2_WorkOrder_Detail>> parts = new ArrayList();
    private List<AssetInfo> listSelectedAssetInfo = new ArrayList<AssetInfo>();

    private ContractInfoItem paymentInfo;

    private ContractInfoItem invoiceInfo;

    private Boolean canBeEdit;

    private Boolean terminate;
    @Getter
    @Setter
    private Boolean displayMaintenanceField;
    private UserAccount currentUser;
    @Getter
    @Setter
    private Boolean sortFlag = Boolean.TRUE;
    @Getter
    @Setter
    private Supplier newSupplier;

    @Getter
    @Setter
    private String newSupplierType;

    private List<I18nMessage> contractAttachmentTypeList;

    @Getter
    @Setter
    private List<String> selectedBlobIdList;
    @Getter
    @Setter
    private Map<String, Boolean> blobObjectSelectedMap;
    @Getter
    @Setter
    private StreamedContent attachListFile;

    @Override
    protected void init() {
        dao = WebUtil.getBean(ContractInfoRepository.class);
        contractItemDao = WebUtil.getBean(ContractInfoItemRepository.class);
        supplierDao = WebUtil.getBean(SupplierRepository.class);
        assetDao = WebUtil.getBean(AssetInfoRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        tenantInfoDao = WebUtil.getBean(TenantInfoRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        assetInfoService = WebUtil.getBean(AssetInfoService.class);
        contractService = WebUtil.getBean(ContractInfoService.class);
        supplierController = WebUtil.getBean(SupplierController.class);
        arrivalListRepository = WebUtil.getBean(ArrivalListRepository.class);
        configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);
        String encodeStr = WebUtil.getRequestParameter("str");
        String terminateStr = (String) UrlEncryptController.getValueFromMap(encodeStr, "terminate");
        terminate = terminateStr == null ? false : Boolean.parseBoolean(terminateStr);
        String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "action");
        if (ObjectUtil.isEmpty(actionName)) {
            request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            actionName = request.getParameter("action");
        }
        currentUser = UserContextService.getCurrentUserAccount();
        if ("Create".equalsIgnoreCase(actionName)) {
            try {
                prepareCreate();
                selectedObjectMap.put("selectedOrg", orgDao.findByUid(currentUser.getSiteUID()));
                changeContractOrg((OrgInfo) selectedObjectMap.get("selectedOrg"));
                attachements = new ArrayList();
                allItemList = new ArrayList();
            } catch (InstantiationException ex) {
                Logger.getLogger(ContractInfoEditController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ContractInfoEditController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if ("Edit".equalsIgnoreCase(actionName)) {
            String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
            if (ObjectUtil.isEmpty(selectedId)) {
                String contractNum = request.getParameter("contractNum");
                this.selected = dao.findEquipmentByContractNum(contractNum, currentUser.getTenantUID());
            } else {
                this.selected = dao.findById(selectedId);
            }
            prepareEdit();
            attachements = blobService.findBlobObjectsByBoUid(selected.getId());
            allItemList = contractService.getContractItems(selected.getId());
            if (allItemList != null) {
                for (int i = 0; i < allItemList.size(); ++i) {
                    AssetInfo tempAsset = allItemList.get(i).getAssetInfo();
                    if (tempAsset != null) {
                        listSelectedAssetInfo.add(tempAsset);
                    }
                }
            }
            selectedObjectMap.put("supplier", selected.getSupplierUid() == null ? null : supplierDao.findByUid(selected.getSupplierUid()));
            selectedObjectMap.put("bidWinner", selected.getBidWinner() == null ? null : supplierDao.findByUid(selected.getBidWinner()));
            selectedObjectMap.put("selectedOrg", selected.getSiteUID() == null ? null : orgDao.findByUid(selected.getSiteUID()));

            if (ContractInfo.ContractType.Parts.toString().equals(selected.getContractType())) {
                parts = contractService.getAssetPartsInfo(selected.getContractNum(), selected.getTenantUID());
            }
        }
        displayMaintenanceField = Boolean.valueOf(configItemValueRepository.findByKeyAndSiteForUseParent(currentUser.getSiteUID(),
                currentUser.getHospitalUID(), currentUser.getInstitutionUID(), currentUser.getTenantUID(), "displayMaintenanceField"));
        if (selected != null) clinicalDeptList = assetInfoService.getClinicalDeptList(selected.getSiteUID());
        // supplierList =
        // aimService.getSuplierListBySiteUid(selected.getTenantUID());
        supplierList = supplierDao.findBySupplierTAndTenantUID(Boolean.TRUE, currentUser.getTenantUID());
        manufactureList = supplierDao.findByManufacturerTAndTenantUID(Boolean.TRUE, currentUser.getTenantUID());
        initOrgTree();
        List<I18nMessage> contractAttachmentType = FieldValueMessageController.getFieldValueList("contractAttachmentType");
        contractAttachmentTypeList = contractAttachmentType.stream().sorted(Comparator.comparingInt(I18nMessage::getId)).collect(Collectors.toList());
        blobObjectSelectedMap = new HashMap();
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
                if (bidWinner != null) {
                    selected.setBidWinner(bidWinner.getUid());
                    selected.setBidWinnerPerson(bidWinner.getContactor());
                    selected.setBidWinnerTelphone(bidWinner.getTel());
                }
                break;
            case "applyDept":
                OrgInfo clinicalDept = (OrgInfo) selectedObjectMap.get(idx);
                selectedContractItem.setApplyDept(clinicalDept == null ? null : clinicalDept.getUid());
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

    public void uploadInvoiceDataFile(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event.getFile());
        blobObject.setBoType(V2_BlobObject.BO_TYPE_AssetContract);
        blobObject.setBoSubType("payment");
        blobObject.setBoUuid(invoiceInfo.getId());
        uploadedInvoiceFileObjects.add(blobObject);
        invoiceAttachments.add(blobObject);
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
        UserAccount loginUser = currentUser;
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
        List<Supplier> res = supplierList.stream().filter(item -> Arrays.asList("1", "3").contains(item.getSupplierType())).filter(item -> Strings.isNullOrEmpty(query)
                || item.getName().contains(query) || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    public List<Supplier> autoCompleteSupplierByPY(String query) {
        List<Supplier> res = supplierList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());

        res.sort(new PinYinComparatorByName());
        return res;
    }

    public List<Supplier> autoCompleteManufactureByPY(String query) {
        List<Supplier> res = manufactureList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());

        res.sort(new PinYinComparatorByName());
        return res;
    }

    public void removeAttachmentById(String blobId) {
        removedAttachments.add(blobId);
        // blobService.deleteBlobFileById(blobId);
        attachements = attachements.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toList());
    }

    public void approveAttachmentById(String blobId) {
        blobService.approveBlob(blobId, "1");
    }

    public Boolean canApprove(String blobId) {
        return getCanBeEdit() && !blobService.approved(blobId);
    }

    public void removeUploadedFileObjects(String blobId) {
        blobService.deleteBlobFileById(blobId);
        uploadedFileObjects = uploadedFileObjects.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toList());
    }

    public void cancelUpload() {
        uploadedFileObjects.stream().forEach(item -> {
            blobService.deleteBlobFile(item);
        });
        uploadedFileObjects.clear();
    }

    @Transactional
    public void saveContract() {
        if (!Strings.isNullOrEmpty(selected.getContractNum())) {
            List<ContractInfo> contractList = dao.findByContractNumAndTenantUID(selected.getContractNum(), selected.getTenantUID());
            if (null != contractList && !contractList.isEmpty()) {
                if (contractList.stream().anyMatch(item -> !Objects.equals(item.getId(), selected.getId()))) {
                    WebUtil.addErrorMessage(WebUtil.getMessage("ContractNumberCannotBeReused"));
                    return;
                }
            }
        }
        boolean isEdit = selected.getId() == null ? false : true;
        if (!isEdit) {
            this.selected.setCreatedBy(currentUser.getUid());
            this.selected.setCreatedDate(DateUtil.now());
        }
        selected = dao.save(this.selected);
        attachements = attachements.stream().filter(item -> item.getBoUuid() == null).collect(Collectors.toList());
        attachements.stream().forEach(item -> {
            item.setBoUuid(selected.getId());
        });
        /*allItemList.stream().filter(item -> Strings.isNullOrEmpty(item.getContractId())).forEach(item -> {
            item.setContractId(selected.getId());
        });
        contractItemDao.save(allItemList);
        contractItemDao.delete(removedItemList);*/
        blobService.saveBlobObject(attachements);
        blobService.saveBlobObject(uploadedInvoiceFileObjects);

        removedAttachments.stream().forEach(item -> {
            blobService.deleteBlobFileById(item);
        });
        if (!ContractInfo.ContractType.Equipment.toString().equals(selected.getContractType())) {
            // 质保合同
            allItemList.stream().filter(item -> null != item.getAssetInfo()).forEach(item -> udpateAssetInfoWarranty(item.getAssetInfo()));
        }
        allItemList.stream().forEach(item -> item.setContractId(selected.getId()));
        contractItemDao.save(allItemList);
        for (ContractInfoItem item : allItemList) {
            if (ContractInfoItem.ContractInfoItemType.ExistAsset.toString().equals(item.getItemType())) {
                contractService.sendItemMq(item, SystemEventAuditEnum.CONTRACT_ASSET_INFO_ADD);
            }
        }
        allItemList = contractService.getContractItems(selected.getId());
        if (isEdit && CollectionUtils.isNotEmpty(allItemList)) {
            List<String> itemIds = new ArrayList<>();
            for (ContractInfoItem item : allItemList) {
                itemIds.add(item.getId());
            }
            List<ArrivalList> arrivalLists = arrivalListRepository.findByContractItemIdIn(itemIds);
            if (CollectionUtils.isNotEmpty(arrivalLists)) {
                for (ArrivalList arrivalList : arrivalLists) {
                    arrivalList.setContractNum(selected.getContractNum());
                }
                arrivalListRepository.save(arrivalLists);
            }
        }
        WebUtil.addSuccessMessage(WebUtil.getMessage("DataSavedOK"));
    }

    public String getCreatedBy(String createdBy) {
        return Optional.ofNullable(createdBy).map(userDao::findByUid).map(UserAccount::getName).orElse(createdBy);
    }

    private void udpateAssetInfoWarranty(AssetInfo asset) {
        if (null == asset.getWarrantyDate()) {
            asset.setWarrantyDate(selected.getEndDate());
            assetDao.save(asset);
        } else if (selected.getEndDate().after(asset.getWarrantyDate())) {
            asset.setWarrantyDate(selected.getEndDate());
            assetDao.save(asset);
        }
    }

    public void onEstimatedPriceChange() {
        if (null == selectedContractItem.getEstimatedPrice()) {
            selectedContractItem.setPriceType(null);
        } else if (selectedContractItem.getEstimatedPrice() < 1) {
            selectedContractItem.setPriceType(ContractInfo.PriceType.LT1.toString());
        } else if (selectedContractItem.getEstimatedPrice() < 5) {
            selectedContractItem.setPriceType(ContractInfo.PriceType.BT1_5.toString());
        } else if (selectedContractItem.getEstimatedPrice() < 50) {
            selectedContractItem.setPriceType(ContractInfo.PriceType.BT5_50.toString());
        } else {
            selectedContractItem.setPriceType(ContractInfo.PriceType.GT50.toString());
        }
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        if (eventName.equals("assetListSelected")) {
            List<AssetInfo> listAsset = (List<AssetInfo>) eventObject;
            if (listAsset != null) {
                for (int i = 0; i < listAsset.size(); ++i) {
                    AssetInfo tempAsset = listAsset.get(i);
                    if (listSelectedAssetInfo.contains(tempAsset)) {

                    } else {
                        listSelectedAssetInfo.add(tempAsset);
                        ContractInfoItem newItem = new ContractInfoItem();
                        newItem.setAssetUid(tempAsset.getUid());
                        newItem.setAssetInfo(tempAsset);
                        newItem.setContractId(selected.getId());
                        newItem.setItemType(ContractInfoItem.ContractInfoItemType.ExistAsset.toString());
                        allItemList.add(newItem);
                        contractItemDao.save(newItem);
                        contractService.sendItemMq(newItem, SystemEventAuditEnum.CONTRACT_ASSET_INFO_ADD);
                    }
                }
            }
        } else {
            AssetInfo asset = (AssetInfo) eventObject;
            if (asset == null) {
                return;
            }
            if (!asset.getSiteUID().equals(selected.getSiteUID())) {
                WebUtil.addErrorMessage(WebUtil.getMessage("CanOnlyAddEquipmentInTheSiteWhereTheContractIsLocated"));
            } else if (allItemList.stream().anyMatch(item -> Objects.equals(item.getAssetUid(), asset.getUid()))) {
                WebUtil.addErrorMessage(WebUtil.getMessage("ThisEquipmentExists"));
            } else {
                ContractInfoItem newItem = new ContractInfoItem();
                newItem.setAssetUid(asset.getUid());
                newItem.setAssetInfo(asset);
                newItem.setContractId(selected.getId());
                newItem.setItemType(ContractInfoItem.ContractInfoItemType.ExistAsset.toString());
                allItemList.add(newItem);
                contractItemDao.save(newItem);
            }
        }
    }

    public void remomveContractItem(AssetInfo assetInfo) {
        // ContractInfoItem removeItem = this.contractItemDao.findById(itemId);
        listSelectedAssetInfo.remove(assetInfo);
        List<ContractInfoItem> removeList = allItemList.stream().filter(item -> Objects.equals(item.getAssetUid(), assetInfo.getUid())).collect(Collectors.toList());
        allItemList.removeAll(removeList);
        removedItemList.addAll(removeList.stream().filter(item -> StringUtils.isNotEmpty(item.getId())).collect(Collectors.toList()));

    }

    public void remomveContractItem(ContractInfoItem item) {
        // ContractInfoItem removeItem = this.contractItemDao.findById(itemId);
        allItemList.remove(item);
        if (!Strings.isNullOrEmpty(item.getId())) {
            contractService.removeContractInfoItem(item);
            if (ContractInfoItem.ContractInfoItemType.ExistAsset.toString().equals(item.getItemType())) {
                contractService.sendItemMq(item, SystemEventAuditEnum.CONTRACT_ASSET_INFO_DELETE);
            }
            //contractItemDao.delete(item);
            //removedItemList.add(item);
        }
    }

    public boolean canRemovePucheseItem(ContractInfoItem contractItem) {
        return Strings.isNullOrEmpty(contractItem.getId()) ? true : !allItemList.stream().anyMatch(item -> Objects.equals(contractItem.getId(), item.getPurchaseItemId()));
    }

    public String getUserNameByUid(String orgUid) {
        UserAccount user = userDao.getByUid(orgUid);
        return user == null ? orgUid : user.getName();
    }

    public String getSupplierName(String supplierUid) {
        Supplier supplier = supplierDao.findByUid(supplierUid);
        return supplier == null ? null : supplier.getName();
    }

    public String getParentName(String parentId) {
        ContractInfoItem contractInfoItem = contractItemDao.findOne(parentId);
        return contractInfoItem == null ? null : contractInfoItem.getContractAssetName();
    }

    public String getDetailPageLinkByAsset(AssetInfo item) {
        String url = "";
        if (null != item) {
            AssetInfo asset = item;
            url = "actionName=View&selectedid=" + asset.getUid().toString() + "&asset_name=" + asset.getName() + "&terminate=" + !asset.getIsValid();
            url = "/portal/asset/info/AssetDetail.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        }
        return url;
    }

    //==============================================
    public String getDetailPageLinkByUid(String uid) {
        AssetInfo viewAsset = assetDao.findByUid(uid);
        String url = "actionName=View&selectedid="
                + viewAsset.getUid()
                + "&asset_name=" + viewAsset.getName()
                + "&terminate=" + this.terminate;
        url = "/portal/asset/info/AssetDetail.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);
        logger.info(url);
        return url;
    }

    public Boolean getTerminate() {
        return terminate;
    }

    public void viewContractItemPage(ContractInfoItem item) {
        String url = getDetailPageLinkByAsset(item.getAssetInfo());
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public void onPlannedArrivalDateChange() {
        if (null != selectedContractItem.getDeliveryDays() && null != selectedContractItem.getNoticeDate()) {
            selectedContractItem.setPlannedArriveDate(new Date(selectedContractItem.getNoticeDate().getTime() + 86400000 * selectedContractItem.getDeliveryDays()));
        } else {
            selectedContractItem.setPlannedArriveDate(null);
        }
    }

    public String isOnTimeArrival(ContractInfoItem item) {
        if (item.getPlannedArriveDate() == null || item.getArrivalListItem() == null || item.getArrivalListItem().getArrivalList().getArrivalDate() == null) {
            return "";
        }
        return item.getArrivalListItem().getArrivalList().getArrivalDate().before(item.getPlannedArriveDate()) ? WebUtil.getMessage("True2Yes") : WebUtil.getMessage("False2No");
    }

    public void savePurchaseItem() {
        selectedContractItem.setContractId(selected.getId());
        contractItemDao.save(selectedContractItem);
        allItemList = contractService.getContractItems(selected.getId());
    }

    public void cancelPurchaseItem() {
        selectedContractItem = null;
    }

    public void prepareAddPurchaseAsset() {
        selectedContractItem = new ContractInfoItem();
        selectedContractItem.setId(UUID.randomUUID().toString().replace("-", ""));
        selectedContractItem.setItemType(ContractInfoItem.ContractInfoItemType.Purchase.toString());
        selectedContractItem.setContractId(selected.getId());
        selectedObjectMap.put("manufacture", null);
        selectedObjectMap.put("applyDept", null);
    }

    public void prepareEditPurchaseAsset(ContractInfoItem item) {
        selectedContractItem = item;
        selectedObjectMap.put("manufacture", selectedContractItem.getManufacturer() == null ? null : supplierDao.findByUid(selectedContractItem.getManufacturer()));
        selectedObjectMap.put("applyDept", selectedContractItem.getApplyDept() == null ? null : orgDao.findByUid(selectedContractItem.getApplyDept()));
    }

    public List<ContractInfoItem> getPurchaseItemsList() {
        List<ContractInfoItem> tempContractInfoItemList = new ArrayList<>();
        tempContractInfoItemList = contractService.getContractItems(selected.getId());
        return tempContractInfoItemList.stream().filter(item -> ContractInfoItem.ContractInfoItemType.Purchase.toString().equals(item.getItemType())).collect(Collectors.toList());
    }

    public List<ContractInfoItem> getContractItemsList() {
        List<ContractInfoItem> tempContractInfoItemList = new ArrayList<>();
        tempContractInfoItemList = contractService.getContractItems(selected.getId());
        return allItemList.stream().filter(item -> ContractInfoItem.ContractInfoItemType.ExistAsset.toString().equals(item.getItemType())).collect(Collectors.toList());
        /*return allItemList.stream().filter(item -> ContractInfoItem.ContractInfoItemType.ExistAsset.toString().equals(item.getItemType())
                || ContractInfoItem.ContractInfoItemType.ArrivalledItem.toString().equals(item.getItemType())).collect(Collectors.toList());*/
    }

    public List<ContractInfoItem> getContractArrivalItemsList() {
        List<ContractInfoItem> tempContractInfoItemList = new ArrayList<>();
        tempContractInfoItemList = contractService.getContractItems(selected.getId());
        return allItemList.stream().filter(item -> ContractInfoItem.ContractInfoItemType.ArrivalledItem.toString().equals(item.getItemType())).collect(Collectors.toList());
    }

    public List<ContractInfoItem> getContractInvoiceList() {
        return allItemList.stream().filter(item -> ContractInfoItem.ContractInfoItemType.Invoice.toString().equals(item.getItemType())).collect(Collectors.toList());
    }

    public List<V2_BlobObject> getContractInvoiceAttachList(String id) {
        if (ObjectUtil.isEmpty(invoiceAttachments)) {
            invoiceAttachments = blobService.fetchBlobObject(id, V2_BlobObject.BO_TYPE_AssetContract, "payment");
        }
        return invoiceAttachments;
    }

    public void removeContractInvoiceAttachmentById(String blobId) {
        invoiceAttachments = invoiceAttachments.stream().filter(t -> !t.getId().equals(blobId)).collect(Collectors.toList());
        blobService.deleteBlobFileById(blobId);
    }

    public List<Tuple2<AssetInfo, V2_WorkOrder_Detail>> getPartsItemsList() {
        if (selected != null && Objects.equals(selected.getContractType(), ContractInfo.ContractType.Parts.toString())) {
            return contractService.getAssetPartsInfo(selected.getContractNum(), selected.getTenantUID());
        } else {
            return null;
        }
    }

    public void onChangeEndDate() {
        if (this.selected.getStartDate() != null && this.selected.getEndMonths() != null) {
            Instant startDateInstant = new Date(this.selected.getStartDate().getTime()).toInstant();
            ZoneId zone = ZoneId.systemDefault();
            LocalDateTime startLocalDateTime = LocalDateTime.ofInstant(startDateInstant, zone);
            LocalDate startLocalDate = startLocalDateTime.toLocalDate();

            Instant scheduleDateInstant = startLocalDate.plus(this.selected.getEndMonths(), ChronoUnit.MONTHS).plus(-1, ChronoUnit.DAYS).atStartOfDay().atZone(zone).toInstant();

            this.selected.setEndDate(Date.from(scheduleDateInstant));
        }
    }

    public void onChangeEndMonths() {
        this.selected.setEndMonths(null);
    }

    public void onContractTypeChange() {
        if (ContractInfo.ContractType.Equipment.toString().equals(selected.getContractType())) {
            selected.setInvoiceNum(null);
        }
    }

    public List<Object[]> getPartsList() {
        return parts.stream().map(item -> new Object[]{item._1(), item._2()}).collect(Collectors.toList());
    }

    public List<ContractInfoItem> getPaymentList() {
        return allItemList.stream().filter(item -> Objects.equals(item.getItemType(), ContractInfoItem.ContractInfoItemType.paymentRecord.toString()))
                .collect(Collectors.toList());
    }

    public void prepareAddPaymentInfo() {
        this.paymentInfo = new ContractInfoItem();
        paymentInfo.setItemType(ContractInfoItem.ContractInfoItemType.paymentRecord.toString());
        paymentInfo.setContractId(selected.getId());
        paymentInfo.setPaymentDate(new Date());
        paymentInfo.setId(UUID.randomUUID().toString().replaceAll("-", ""));
    }

    public void prepareAddInvoiceInfo() {
        this.invoiceInfo = new ContractInfoItem();
        invoiceInfo.setItemType(ContractInfoItem.ContractInfoItemType.Invoice.toString());
        invoiceInfo.setContractId(selected.getId());
        invoiceInfo.setPaymentDate(new Date());
        invoiceInfo.setId(UUID.randomUUID().toString().replaceAll("-", ""));
    }

    public void prepareEditPaymentInfo(ContractInfoItem item) {
        paymentInfo = item;
    }

    public void prepareEditInvoiceInfo(ContractInfoItem item) {
        invoiceInfo = item;
    }

    public void savePaymentItem() {
        if (!allItemList.contains(paymentInfo)) {
            allItemList.add(paymentInfo);
        }
        double sum = allItemList.stream().filter(t -> "paymentRecord".equals(t.getItemType())).mapToDouble(ContractInfoItem::getPaymentPrice).sum();
        Double amount = selected.getAmount() == null ? 0.0 : selected.getAmount();
        Double supplementaryAmount = selected.getSupplementaryAmount() == null ? 0.0 : selected.getSupplementaryAmount();
        if (sum > (amount + supplementaryAmount)) {
            allItemList.remove(paymentInfo);
            WebUtil.addErrorMessage("付款金额大于总额！");
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
        }
    }

    public void saveInvoiceItem() {
        if (!allItemList.contains(invoiceInfo)) {
            allItemList.add(invoiceInfo);
        }
    }

    public Boolean getCanBeEdit() {
        if (null == canBeEdit) {
            canBeEdit = uaaService.getOrgListByUser(currentUser.getUid()).stream()
                    .map(item -> item.getSiteUID()).distinct().anyMatch(item -> Objects.equals(item, selected.getTenantUID()) ||
                            Objects.equals(item, selected.getInstitutionUID()) || Objects.equals(item, selected.getHospitalUID()) ||
                            Objects.equals(item, selected.getSiteUID()));
        }
        return canBeEdit;
    }

    public List<ContractInfoItem> getParentAssetList() {
        return contractItemDao.findParentByPurchaseAndContractId(selected.getId(), selectedContractItem.getId());
    }

    public Boolean isYunNanFuWai() {
        TenantInfo tenantInfo = tenantInfoDao.findByuid(currentUser.getTenantUID());
        if ("云南省阜外心血管病医院".equals(tenantInfo.getName().trim())) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }


    public void prepareCreateSupplier(String type) {
        newSupplier = new Supplier();
        newSupplierType = type;
        if ("manufacturer".equals(type)) {
            newSupplier.setManufacturerT(true);
        }
        if ("supplier".equals(type)) {
            newSupplier.setSupplierT(true);
        }
        if ("maintainer".equals(type)) {
            newSupplier.setMaintainerT(true);
        }

    }

    public void prepareApproval(ContractInfo contractInfo) {
        this.selected = contractInfo;
        this.attachements = blobService.findBlobObjectsByBoUid(selected.getId());
    }

    public void approval() {
        this.selected.setApprovalTime(new Date());
        this.selected.setAuditor(currentUser.getName());
        if (Boolean.TRUE.equals(this.selected.getApprovalStatus())) {
            attachements.forEach(f->{
                f.setApproveStatus("1");
            });
            this.selected.setStatus(4);
        } else {
            this.selected.setStatus(3);
        }
        dao.save(this.selected);
        blobService.saveBlobObject(attachements);
    }

    public void onBlur() {
        UserAccount loginUser = currentUser;
        Supplier supplier = supplierDao.getBySiteIdAndName(loginUser.getSiteId(), this.newSupplier.getName());
        if (!ObjectUtils.isEmpty(supplier)) {
            this.newSupplier = supplier;
        } else {
            String name = newSupplier.getName();
            boolean ManufacturerT = newSupplier.getManufacturerT();
            boolean SupplierT = newSupplier.getSupplierT();
            boolean MaintainerT = newSupplier.getMaintainerT();
            prepareCreateSupplier("manufacturer");
            newSupplier.setName(name);
            newSupplier.setSupplierT(SupplierT);
            newSupplier.setMaintainerT(MaintainerT);
            newSupplier.setManufacturerT(ManufacturerT);
        }
    }

    public void saveNewSupplier() {
        UserAccount loginUser = currentUser;
        this.newSupplier.setSiteId(loginUser.getSiteId());
        this.newSupplier.setHospitalId(loginUser.getHospitalId());
        this.newSupplier.setTenantUID(loginUser.getTenantUID());
        this.newSupplier.setInstitutionUID(loginUser.getInstitutionUID());
        this.newSupplier.setHospitalUID(loginUser.getHospitalUID());
        this.newSupplier.setSiteUID(loginUser.getSiteUID());
        this.newSupplier.setCreatedDate(new Date());
        this.newSupplier.setCreatedBy(loginUser.getName());
        supplierController.setSelected(newSupplier);
        supplierController.saveSubmit();

        if ("manufacturer".equals(newSupplierType)) {
            if (ObjectUtil.isNotEmpty(newSupplier.getId())) {
                manufactureList.removeIf(t -> t.getId().equals(newSupplier.getId()));
            }
            manufactureList.add(newSupplier);
        } else if ("supplier".equals(newSupplierType)) {
            if (ObjectUtil.isNotEmpty(newSupplier.getId())) {
                supplierList.removeIf(t -> t.getId().equals(newSupplier.getId()));
            }
            supplierList.add(newSupplier);
        }
    }

    public boolean acceptTip(ContractInfo contractInfo) {
        boolean disable = false;
        if ("到货完成 验收完成".equals(contractInfo.getCompletion())) {
            disable = true;
        }
        return disable;
    }

    public void pick(String itemId) {
        selectedBlobIdList = new ArrayList<>();
        blobObjectSelectedMap.forEach((k, v) -> {
            if (v)
                selectedBlobIdList.add(k);
        });
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = attachements.stream().noneMatch(item -> !blobObjectSelectedMap.containsKey(item.getId()) || !blobObjectSelectedMap.get(item.getId()));
        for (V2_BlobObject item : attachements) {
            blobObjectSelectedMap.put(item.getId(), !isSelectedAll);
        }
        selectedBlobIdList = new ArrayList<>();
        blobObjectSelectedMap.forEach((k, v) -> {
            if (v) {
                selectedBlobIdList.add(k);
            } else {
                selectedBlobIdList.remove(k);
            }
        });
    }

    public boolean isChecked() {
        Map<String, Boolean> map = this.blobObjectSelectedMap;
        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            if (entry.getValue()) {
                return true;
            }
        }
        return false;
    }

    public void bathDeleteAttach() {
        for (String blobId : selectedBlobIdList) {
            blobObjectSelectedMap.remove(blobId);
            removeAttachmentById(blobId);
        }
    }

    public void bathDownloadAttach() {
        String zipName = Optional.ofNullable(selected.getContractNum()).orElse("") + selected.getName() + ".zip";
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream(); ZipOutputStream zipOut = new ZipOutputStream(bout)) {
            Map<String, V2_BlobObject> blobObjectMap = attachements.stream().collect(Collectors.toMap(V2_BlobObject::getId, t -> t));
            for (String blobId : selectedBlobIdList) {
                V2_BlobObject blobObject = blobObjectMap.get(blobId);
                String name = FieldValueMessageController.fieldValue("contractAttachmentType", blobObject.getBoSubType()).replaceAll("/", "或") + "_" + blobObject.getObjectName();
                zipOut.putNextEntry(new ZipEntry(name));
                byte[] bytess = IOUtils.toByteArray(blobService.getBlobObjectByStorageId(blobObject.getObjectStorageId()).getStream());
                IOUtils.write(bytess, zipOut);
                zipOut.closeEntry();
            }
            zipOut.close();
            attachListFile = new DefaultStreamedContent(new ByteArrayInputStream(bout.toByteArray()), "application/zip", zipName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void attachSort() {
        sortFlag = !sortFlag;
    }

    public BigDecimal getBigDecimal(BigDecimal amount) {
//        return BigDecimal.valueOf(amount);
        return amount;
    }

    public Map<String, Object> getSelectedObjectMap() {
        return selectedObjectMap;
    }

    public void setSelectedObjectMap(Map<String, Object> selectedObjectMap) {
        this.selectedObjectMap = selectedObjectMap;
    }

    public List<V2_BlobObject> getAttachements() {
        if(CollectionUtils.isEmpty(attachements)) return Lists.newArrayList();
        for (V2_BlobObject att : attachements) {
            att.setBoSubTypeValue(WebUtil.getFieldValueMessage("contractAttachmentType", att.getBoSubType()));
        }
        if (sortFlag) {
            return attachements.stream().sorted(Comparator.comparing(V2_BlobObject::getBoSubTypeValue)).collect(Collectors.toList());
        } else {
            return attachements.stream().sorted(Comparator.comparing(V2_BlobObject::getBoSubTypeValue).reversed()).collect(Collectors.toList());
        }
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

    public ContractInfoItem getSelectedContractItem() {
        return selectedContractItem;
    }

    public void setSelectedContractItem(ContractInfoItem selectedContractItem) {
        this.selectedContractItem = selectedContractItem;
    }

    public ContractInfoItem getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(ContractInfoItem paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public ContractInfoItem getInvoiceInfo() {
        return invoiceInfo;
    }

    public void setInvoiceInfo(ContractInfoItem invoiceInfo) {
        this.invoiceInfo = invoiceInfo;
    }


    public List<I18nMessage> getContractAttachmentTypeList() {
        return contractAttachmentTypeList;
    }

    public void setContractAttachmentTypeList(List<I18nMessage> contractAttachmentTypeList) {
        this.contractAttachmentTypeList = contractAttachmentTypeList;
    }
}
