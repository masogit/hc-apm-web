/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import com.ge.apm.dao.ArrivalListItemRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.QrCodeLibRepository;
import com.ge.apm.domain.ArrivalListItem;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.QrCodeLib;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.AssetDepreciationService;
import com.ge.apm.service.asset.AssetFinacingNumGenerateService;
import com.ge.apm.service.asset.AssetInstallationService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.QrCodeService;
import com.ge.apm.service.pm.V2_PmOrderService;
import com.ge.apm.service.uaa.UaaService;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class AssetInstallationController {

    private ArrivalListItem arrivalListItem;
    private ArrivalListItemRepository itemDao;
    private AssetInstallationService aiService;
    private AssetInfoRepository assetDao;
    private QrCodeLibRepository qrcodeDao;
    private UaaService uaaService;
    private BlobFileService blobService;
    private QrCodeService qrCodeService;
    private AssetFinacingNumGenerateService assetFinacingNumGenerateService;
    private AssetDepreciationService assetDepreciationService;
    private V2_PmOrderService pmService;

    private OrgInfo selectedOrg;
    private TreeNode orgTree;
    private TreeNode selectedNode;
    private List<V2_BlobObject> picList;

    private List<I18nMessage> code68List;
    private I18nMessageRepository i18nDao;
    private Boolean isGeneratePmOrder;

    private String currentQrCode;

    @PostConstruct
    protected void init() {
        aiService = WebUtil.getBean(AssetInstallationService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        qrCodeService = WebUtil.getBean(QrCodeService.class);
        qrcodeDao = WebUtil.getBean(QrCodeLibRepository.class);
        assetFinacingNumGenerateService = WebUtil.getBean(AssetFinacingNumGenerateService.class);
        assetDepreciationService = WebUtil.getBean(AssetDepreciationService.class);
        pmService = WebUtil.getBean(V2_PmOrderService.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        assetDao = WebUtil.getBean(AssetInfoRepository.class);
        itemDao = WebUtil.getBean(ArrivalListItemRepository.class);
        String itemId = WebUtil.getRequestParameter("itemId");
        arrivalListItem = itemDao.findById(itemId);
        currentQrCode = arrivalListItem.getQrCode();
        changeFunctionGroup();

        orgTree = uaaService.getOrgTreeBySite(arrivalListItem.getArrivalList().getSiteUID());
        setTreeNodeUnselectable(orgTree);
        orgTree.setExpanded(true);
        if (arrivalListItem.getClinicalDeptUID() != null) {
            selectedOrg = aiService.getOrgInfoByUid(arrivalListItem.getClinicalDeptUID());
        } else {
            selectedOrg = null;
        }

        picList = aiService.getPicList(arrivalListItem.getId());
    }

    private void setTreeNodeUnselectable(TreeNode node) {
        OrgInfo data = (OrgInfo) node.getData();
        if (data != null && data.getOrgType() != 4) {
            node.setSelectable(false);
        }
        if (node.getChildren() != null) {
            node.getChildren().forEach(item -> setTreeNodeUnselectable(item));
        }
    }

    public String rejectInstall() {
        if (arrivalListItem.getInstallStatus().equals(ArrivalListItem.INSTALL_STATUS_NEW)) {
            saveOrUpdateItem(arrivalListItem);
            if (aiService.submitInstallItem(arrivalListItem.getId(), false)) {
                WebUtil.addSuccessMessage("提交成功");
                return "arrivalItemsList?faces-redirect=true";
            } else {
                WebUtil.addErrorMessage("提交失败");
            }

        }
        return "";
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
        if (!qrCodeLib.getSiteUID().equals(arrivalListItem.getArrivalList().getSiteUID())) {
            WebUtil.addErrorMessage("错误:" + WebUtil.getMessage("WrongHospitalQRCode"));
            return false;
        }
        if (qrCodeLib.getStatus() != 1) {
            WebUtil.addErrorMessage("错误:" + WebUtil.getMessage("AlreadyUsingQrCode"));
            return false;
        }

        return true;
    }

    public String finishInstall() {
        Boolean isAckFinished = true;
        if (!arrivalListItem.getBusinessAckStatus().equals(ArrivalListItem.ACK_STATUS_PASS)) {
            WebUtil.addErrorMessage("商务验收未通过");
            isAckFinished = false;
        }
        if (!arrivalListItem.getClinicalAckStatus().equals(ArrivalListItem.ACK_STATUS_PASS)) {
            WebUtil.addErrorMessage("临床验收未通过");
            isAckFinished = false;
        }
        if (!arrivalListItem.getTechnicalAckStatus().equals(ArrivalListItem.ACK_STATUS_PASS)) {
            WebUtil.addErrorMessage("技术验收未通过");
            isAckFinished = false;
        }
        if (!currentQrCode.equals(arrivalListItem.getQrCode())) {
            if (!isAvailableQrcode(arrivalListItem.getQrCode())) {
                isAckFinished = false;
            }
        }

        if (!isAckFinished) {
            return "";
        }
        if (arrivalListItem.getInstallStatus().equals(ArrivalListItem.INSTALL_STATUS_NEW)) {
            saveOrUpdateItem(arrivalListItem);
            if (aiService.submitInstallItem(arrivalListItem.getId(), true)) {
                ArrivalListItem temp = itemDao.findById(arrivalListItem.getId());
                AssetInfo newAsset = assetDao.getByUid(temp.getAssetUID());
                assetDepreciationService.saveAssetDerpeciation(newAsset);
                if (isGeneratePmOrder) {
                    pmService.createPMrequest(newAsset.getUid(), null);
                }
                WebUtil.addSuccessMessage("提交成功");
            } else {
                WebUtil.addErrorMessage("提交失败");
            }

        }
        return "arrivalItemsList?faces-redirect=true";
    }

    public void removeAttachment(String storageId) {
        blobService.deleteBlobFileByStorageId(storageId);
        picList.removeAll(picList.stream().filter(item -> item.getObjectStorageId().equals(storageId)).collect(Collectors.toList()));
    }

    public List<V2_BlobObject> getAckPicList(String type) {
        return picList == null ? null : picList.stream().filter(item -> type.equals(item.getBoSubType())).collect(Collectors.toList());
    }

    public void onSelectOrgFromTree(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        selectedOrg = (OrgInfo) node.getData();
        if (selectedOrg.getOrgType() != 4) {
            WebUtil.addErrorMessage("选择的部门不是科室");
        }
        arrivalListItem.setClinicalDeptUID(selectedOrg.getUid());
    }

    private void saveOrUpdateItem(ArrivalListItem arrivalListItem) {
        arrivalListItem.setLastModifiedDate(new Date());
        itemDao.save(arrivalListItem);
    }

    public void updateItem() {
        if (Strings.isNullOrEmpty(arrivalListItem.getClinicalDeptUID())) {
            WebUtil.addErrorMessage(WebUtil.getMessage("clinicalDeptName") + WebUtil.getMessage("ValidationRequire"));
        } else if (arrivalListItem.getInstallStatus().equals(ArrivalListItem.INSTALL_STATUS_NEW)) {
            saveOrUpdateItem(arrivalListItem);
            WebUtil.addSuccessMessage("修改成功");
        } else {
            WebUtil.addErrorMessage("不可修改");
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        String typeName = event.getComponent().getClientId();
        typeName = typeName.contains("business") ? "business" : typeName;
        typeName = typeName.contains("clinical") ? "clinical" : typeName;
        typeName = typeName.contains("technical") ? "technical" : typeName;
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            picList.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_TYPE_ArrivalListItem);
            blobObject.setBoUuid(arrivalListItem.getId());
            blobObject.setBoSubType(typeName);
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public void onFunctionGroupChange() {
        code68List = new ArrayList<>();
        this.arrivalListItem.setAssetCode68(null);

        this.changeFunctionGroup();
    }

    public void changeFunctionGroup() {
        Integer functionGroupId = this.arrivalListItem.getFunctionGroup();
        if (functionGroupId != null) {
            I18nMessage functionGroup = i18nDao.getByMsgTypeAndMsgKey("assetFunctionType", functionGroupId.toString());
            if (functionGroup != null) {
                code68List = i18nDao.getByMsgType(functionGroup.getMsgKey());
            }
        }
    }

    public List<UserAccount> getClinicalOwnerList() {
        return aiService.getClinicalOwnerList(selectedOrg);
    }

    public List<AssetInfo> getParentAssetList() {
        List<SearchFilter> assetFilters = new ArrayList<>();
        assetFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, this.arrivalListItem.getArrivalList().getTenantUID()));
        assetFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, arrivalListItem.getArrivalList().getSiteUID()));
        assetFilters.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, arrivalListItem.getArrivalList().getAssetGroup()));
        if (!Strings.isNullOrEmpty(arrivalListItem.getClinicalDeptUID())) {
            assetFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, arrivalListItem.getClinicalDeptUID()));
        }
        return assetDao.findBySearchFilter(assetFilters);
    }

    public void updateFinancingNumByNationalStandardCode() {
        String financingNum = assetFinacingNumGenerateService.generateFinancingNum(
                null,
                this.arrivalListItem.getNationalStandardCode(),
                this.arrivalListItem.getArrivalList().getSiteUID()
        );
        if (financingNum != null && StringUtils.isEmpty(this.arrivalListItem.getFinancingNum())) {
            this.arrivalListItem.setFinancingNum(financingNum);
        }
    }

    public void renewQrcode() {
        QrCodeLib qrcodeLib = qrCodeService.createOneQrCode(arrivalListItem.getArrivalList().getTenantUID(), arrivalListItem.getArrivalList().getSiteUID(), 3);
        arrivalListItem.setQrCode(qrcodeLib.getQrCode());
    }

    public List<UserAccount> getOwnerList() {
        return aiService.getWorkerList();
    }

    public UserAccount getUserAccountByUid(String uid) {
        return aiService.getUserAccountByUid(uid);
    }

    public ArrivalListItem getArrivalListItem() {
        return arrivalListItem;
    }

    public void setArrivalListItem(ArrivalListItem arrivalListItem) {
        this.arrivalListItem = arrivalListItem;
    }

    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public void setSelectedOrg(OrgInfo selectedOrg) {
        this.selectedOrg = selectedOrg;
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

    public List<V2_BlobObject> getPicList() {
        return picList;
    }

    public void setPicList(List<V2_BlobObject> picList) {
        this.picList = picList;
    }

    public List<I18nMessage> getCode68List() {
        return code68List;
    }

    public void setCode68List(List<I18nMessage> code68List) {
        this.code68List = code68List;
    }

    public Boolean getIsGeneratePmOrder() {
        return isGeneratePmOrder;
    }

    public void setIsGeneratePmOrder(Boolean isGeneratePmOrder) {
        this.isGeneratePmOrder = isGeneratePmOrder;
    }

}
