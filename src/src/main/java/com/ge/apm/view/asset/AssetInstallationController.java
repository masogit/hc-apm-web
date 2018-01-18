/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.*;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
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
    private QrCodeLibRepository qrcodeDao;
    private AssetInfoRepository assetDao;
    private BlobFileService blobService;
    private QrCodeService qrCodeService;
    private AssetFinacingNumGenerateService assetFinacingNumGenerateService;

    private List<V2_BlobObject> picList;

    private List<I18nMessage> code68List;
    private I18nMessageRepository i18nDao;
    private Boolean isGeneratePmOrder = false;
    private GbCodeService gbCodeService;
    private GbCodeRepository gbDao;
    AssetInfoRepository dao = null;
    private TreeNode selectedNode;
    private TreeNode selectedGbNode;
    private String gbCodeName;
    private GbCode selectedGb;
    private TreeNode gbTree;
//    private String currentQrCode;

    @PostConstruct
    protected void init() {
        aiService = WebUtil.getBean(AssetInstallationService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        qrCodeService = WebUtil.getBean(QrCodeService.class);
        qrcodeDao = WebUtil.getBean(QrCodeLibRepository.class);
        assetFinacingNumGenerateService = WebUtil.getBean(AssetFinacingNumGenerateService.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        assetDao = WebUtil.getBean(AssetInfoRepository.class);
        itemDao = WebUtil.getBean(ArrivalListItemRepository.class);
        String itemId = WebUtil.getRequestParameter("itemId");
        arrivalListItem = itemDao.findById(itemId);
//        currentQrCode = arrivalListItem.getQrCode();
        if (Strings.isNullOrEmpty(arrivalListItem.getAssetOwnerUID())) {
            arrivalListItem.setAssetOwnerUID(UserContextService.getCurrentUserAccount().getUid());
        }
        changeFunctionGroup();
        dao = WebUtil.getBean(AssetInfoRepository.class);
        gbCodeService = WebUtil.getBean(GbCodeService.class);
        gbDao = WebUtil.getBean(GbCodeRepository.class);

        picList = aiService.getPicList(arrivalListItem.getId());
        initGbTree();
    }

    public String rejectInstall() {
        if (arrivalListItem.getInstallStatus().equals(ArrivalListItem.INSTALL_STATUS_NEW)) {
            aiService.saveOrUpdateItem(arrivalListItem);
            if (aiService.submitInstallItem(arrivalListItem.getId(), false, null)) {
                WebUtil.addSuccessMessage("提交成功");
                return "arrivalItemsList?faces-redirect=true";
            } else {
                WebUtil.addErrorMessage("提交失败");
            }
        }
        return "";
    }

    public void initGbTree() {
        TreeNode treeNode = gbCodeService.buildGbTree();
        gbTree = new DefaultTreeNode("root", null, null);
        gbTree.getChildren().add(treeNode);
        treeNode.setExpanded(true);
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
        if (qrCodeLib.getStatus() != 1 && qrCodeLib.getStatus() != 2) {
            WebUtil.addErrorMessage("错误:" + WebUtil.getMessage("AlreadyUsingQrCode"));
            return false;
        }

        return true;
    }

    public String finishInstall() {
        Boolean isAckFinished = true;
        if (null != gbCodeName) {
            GbCode gbCode = gbDao.getGbCode(gbCodeName.split("/")[0]);
            if (null == gbCode) {
                WebUtil.addErrorMessage(WebUtil.getMessage("无效的国标码"));
                isAckFinished = false;
            }
            arrivalListItem.setNationalStandardCode(selectedGb.getCode());
            //更新资产国标码
            assetDao.updateStandardCode(selectedGb.getCode(), arrivalListItem.getAssetUID());
        }
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
        if (!Strings.isNullOrEmpty(arrivalListItem.getQrCode())) {
            if (!isAvailableQrcode(arrivalListItem.getQrCode())) {
                isAckFinished = false;
            }
        }

        if (!isAckFinished) {
            return "";
        }

        if (arrivalListItem.getWarrantyYear() != null) {
            Integer monthLong = Double.valueOf(arrivalListItem.getWarrantyYear() * 12).intValue();
            Calendar calender = Calendar.getInstance();
            calender.setTime(new Date());
            calender.add(Calendar.MONTH, monthLong);
            arrivalListItem.setFactoryWarrantyDate(calender.getTime());
        }
        if (arrivalListItem.getInstallStatus().equals(ArrivalListItem.INSTALL_STATUS_NEW)) {
            aiService.saveOrUpdateItem(arrivalListItem);
            if (aiService.submitInstallItem(arrivalListItem.getId(), true, isGeneratePmOrder)) {
                WebUtil.addSuccessMessage("提交成功");
            } else {
                WebUtil.addErrorMessage("提交失败");
            }

        }
        return "arrivalItemsList?faces-redirect=true";
    }

    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        picList.removeAll(picList.stream().filter(item -> item.getId().equals(blobId)).collect(Collectors.toList()));
    }

    public List<V2_BlobObject> getAckPicList(String type) {
        return picList == null ? null : picList.stream().filter(item -> type.equals(item.getBoSubType())).collect(Collectors.toList());
    }

    public void updateItem() {
        Boolean valid = true;
        if (null != gbCodeName) {
            GbCode gbCode = gbDao.getGbCode(gbCodeName.split("/")[0]);
            if (null == gbCode) {
                WebUtil.addErrorMessage(WebUtil.getMessage("无效的国标码"));
                return;
            }
            if (null != gbCodeName) {
                arrivalListItem.setNationalStandardCode(gbCodeName.split("/")[0]);
            }
        }

        if (Strings.isNullOrEmpty(arrivalListItem.getClinicalDeptUID())) {
            WebUtil.addErrorMessage(WebUtil.getMessage("clinicalDeptName") + WebUtil.getMessage("ValidationRequire"));
            valid = false;
        }
        if (!isAvailableQrcode(arrivalListItem.getQrCode())) {
            valid = false;
            arrivalListItem.setQrCode(null);
        }
        if (!valid) {
            return;
        }

        if (arrivalListItem.getInstallStatus().equals(ArrivalListItem.INSTALL_STATUS_NEW)) {
            aiService.saveOrUpdateItem(arrivalListItem);
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
        typeName = typeName.contains("asset") ? "asset" : typeName;
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
        return aiService.getClinicalOwnerList(arrivalListItem.getClinicalDeptUID());
    }

    public List<AssetInfo> getParentAssetList() {
        List<SearchFilter> assetFilters = new ArrayList<>();
        assetFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, this.arrivalListItem.getArrivalList().getTenantUID()));
        assetFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, arrivalListItem.getArrivalList().getSiteUID()));
//        assetFilters.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, arrivalListItem.getArrivalList().getAssetGroup()));
        if (!Strings.isNullOrEmpty(arrivalListItem.getClinicalDeptUID())) {
            assetFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, arrivalListItem.getClinicalDeptUID()));
        }
        return assetDao.findBySearchFilter(assetFilters);
    }

//    public void updateFinancingNumByNationalStandardCode() {
//        if (null != gbCodeName) {
//            String financingNum = assetFinacingNumGenerateService.generateFinancingNum(
//                    null,
//                    gbCodeName.split("/")[0],
//                    this.arrivalListItem.getArrivalList().getSiteUID()
//            );
//            if (financingNum != null && StringUtils.isEmpty(this.arrivalListItem.getFinancingNum())) {
//                this.arrivalListItem.setFinancingNum(financingNum);
//            }
//        }
//    }
    public void onSelectGbTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        GbCode gbCode = (GbCode) node.getData();
        updateSelectedOrgGb(gbCode);
        //联动更新
        if (null != gbCodeName) {
//            String financingNum = assetFinacingNumGenerateService.generateFinancingNum(null,
//                    gbCode.getCode(),
//                    this.arrivalListItem.getArrivalList().getSiteUID()
//            );
            this.arrivalListItem.setNationalStandardCode(gbCodeName);
//            if (financingNum != null && StringUtils.isEmpty(this.arrivalListItem.getFinancingNum())) {
//                this.arrivalListItem.setFinancingNum(financingNum);
//            }

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

    public void renewQrcode() {
        arrivalListItem.setQrCode(aiService.getNewQrcode(arrivalListItem.getArrivalList().getTenantUID(), arrivalListItem.getArrivalList().getSiteUID()).getQrCode());
    }

    public List<String> completeGbName(String prefix) {
        List<String> names = null;
        if (!StringUtils.isEmpty(prefix)) {
            names = gbDao.findGbCodeByPrefix(prefix);
        }
        return names;

    }

    public void gennerateFinancingNum() {
        String prifix = arrivalListItem.getFinancingNum() == null ? "" : arrivalListItem.getFinancingNum();
        String financingNum = assetFinacingNumGenerateService.autoGenerateFinacingNum(null,
                arrivalListItem.getNationalStandardCode()==null?null:arrivalListItem.getNationalStandardCode().split("/")[0], this.arrivalListItem.getArrivalList().getSiteUID(), arrivalListItem.getParentAssetId(), prifix);
        if(null!= financingNum && financingNum.startsWith("error:")){
            WebUtil.addErrorMessage(financingNum.replace("error:", ""));
            financingNum = "";
        }
        if (financingNum != null && (StringUtils.isEmpty(this.arrivalListItem.getFinancingNum()) || this.arrivalListItem.getFinancingNum().length() <= 3)) {
            this.arrivalListItem.setFinancingNum(financingNum);
        }
    }

//    public void onParentAssetChange() {
//        if (Strings.isNullOrEmpty(arrivalListItem.getDepartNum()) && arrivalListItem.getParentAssetId() != null && arrivalListItem.getParentAssetId() > 0) {
//            AssetInfo parentAsset = assetDao.getById(arrivalListItem.getParentAssetId());
//            String finNum = assetFinacingNumGenerateService.generateFinancingNum(parentAsset.getId(), gbCodeName, arrivalListItem.getArrivalList().getSiteUID());
//            arrivalListItem.setFinancingNum(finNum);
//            if (!Strings.isNullOrEmpty(parentAsset.getDepartNum()) && parentAsset.getDepartNum().length() > 2 && parentAsset.getDepartNum().endsWith("00")) {
//                List<SearchFilter> parentAssetFilters = Arrays.asList(new SearchFilter("parentAssetId", SearchFilter.Operator.EQ, arrivalListItem.getParentAssetId()));
//                List<String> childrenNumList = assetDao.findBySearchFilter(parentAssetFilters).stream()
//                        .map(item -> item.getDepartNum()).collect(Collectors.toList());
//                childrenNumList.addAll(itemDao.findBySearchFilter(parentAssetFilters).stream()
//                        .filter(item -> "new".equals(item.getInstallStatus()))
//                        .map(item -> item.getDepartNum()).collect(Collectors.toList()));
//                String prefix = parentAsset.getDepartNum().substring(0, parentAsset.getDepartNum().length() - 2);
//                for (int i = 1; i < 100; i++) {
//                    String num = prefix.concat(i < 10 ? "0" : "").concat(String.valueOf(i));
//                    if (!childrenNumList.contains(num)) {
//                        arrivalListItem.setDepartNum(num);
//                        break;
//                    }
//                }
//            }
//        }
//    }

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

    public String getGbCodeName() {
        return gbCodeName;
    }

    public void setGbCodeName(String gbCodeName) {
        this.gbCodeName = gbCodeName;
    }

    public GbCode getSelectedGb() {
        return selectedGb;
    }

    public void setSelectedGb(GbCode selectedGb) {
        this.selectedGb = selectedGb;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

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
}
