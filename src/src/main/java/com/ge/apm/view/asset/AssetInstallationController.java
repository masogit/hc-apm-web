/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.*;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparator;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javaslang.Tuple2;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
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

    private List<V2_BlobObject> attachList;

    private List<I18nMessage> code68List;
    private I18nMessageRepository i18nDao;
//    private Boolean isGeneratePmOrder;
    private GbCodeService gbCodeService;
    private GbCodeRepository gbDao;
    private TreeNode selectedNode;
    private TreeNode selectedGbNode;
    private String gbCodeName;
    private GbCode selectedGb;
    private TreeNode gbTree;
    private String src = "";
//    private String currentQrCode;

    private ConfigService configService;

    private String exportQrCodeTemplateId;

    private String deptName;

    private List<OrgInfo> deptInfoList;
    private UaaService uaaService;
    private WorkflowConfig config;

    @PostConstruct
    protected void init() {
        aiService = WebUtil.getBean(AssetInstallationService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        qrCodeService = WebUtil.getBean(QrCodeService.class);
        configService = WebUtil.getBean(ConfigService.class);
        qrcodeDao = WebUtil.getBean(QrCodeLibRepository.class);
        assetFinacingNumGenerateService = WebUtil.getBean(AssetFinacingNumGenerateService.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        assetDao = WebUtil.getBean(AssetInfoRepository.class);
        itemDao = WebUtil.getBean(ArrivalListItemRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);

        String encodeStr = WebUtil.getRequestParameter("str");
        String itemId = (String) UrlEncryptController.getValueFromMap(encodeStr, "itemId");
        src = (String) UrlEncryptController.getValueFromMap(encodeStr, "src");
//        String itemId = WebUtil.getRequestParameter("itemId");
        arrivalListItem = itemDao.findById(itemId);
        config = configService.findConfigBySite(arrivalListItem.getArrivalList().getSiteUID());
        if (Strings.isNullOrEmpty(arrivalListItem.getAssetOwnerUID())) {
            arrivalListItem.setAssetOwnerUID(UserContextService.getCurrentUserAccount().getUid());
            onAssetOwnerNameChange();
        }
        if (!Strings.isNullOrEmpty(arrivalListItem.getClinicalDeptUID())) {
            OrgInfo org = aiService.getOrgInfoByUid(arrivalListItem.getClinicalDeptUID());
            deptName = org == null ? null : org.getName();
        }
        changeFunctionGroup();
        gbCodeService = WebUtil.getBean(GbCodeService.class);
        gbDao = WebUtil.getBean(GbCodeRepository.class);

        deptInfoList = uaaService.getClinicalDeptListBySiteUID(arrivalListItem.getArrivalList().getSiteUID());
        attachList = aiService.getPicList(arrivalListItem.getArrivalList().getId());
        attachList.addAll(aiService.getPicList(arrivalListItem.getId()));
        initGbTree();
    }

    public String rejectInstall() {
        if (arrivalListItem.getInstallStatus().equals(ArrivalListItem.INSTALL_STATUS_NEW)) {
            aiService.saveOrUpdateItem(arrivalListItem);
            if (aiService.submitInstallItem(arrivalListItem.getId(), false, null)) {
                WebUtil.addSuccessMessage("提交成功");
                return returnAction();
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
        String qrCodeStatus = qrCodeService.isAvailableQrcode(qrcode, arrivalListItem.getArrivalList().getSiteUID());
        if ("Normal".equals(qrCodeStatus)) {
            return true;
        } else {
            WebUtil.addErrorMessage("错误:" + WebUtil.getMessage(qrCodeStatus));
            return false;
        }
    }

    public String finishInstall() {
        Boolean isValidate = true;
        if (null != gbCodeName) {
            GbCode gbCode = gbDao.getGbCode(gbCodeName.split("/")[0]);
            if (null == gbCode) {
                WebUtil.addErrorMessage(WebUtil.getMessage("无效的国标码"));
                isValidate = false;
            }
            arrivalListItem.setNationalStandardCode(selectedGb.getCode());
            //更新资产国标码
            assetDao.updateStandardCode(selectedGb.getCode(), arrivalListItem.getAssetUID());
        }
        if (Strings.isNullOrEmpty(arrivalListItem.getClinicalDeptUID())) {
            WebUtil.addErrorMessage(WebUtil.getMessage("clinicalDeptName") + WebUtil.getMessage("ValidationRequire"));
            RequestContext.getCurrentInstance().addCallbackParam("ClinicalDeptName", true);
            isValidate = false;
        }
//        if (null == arrivalListItem.getIsGeneratePmOrder()) {
//            WebUtil.addErrorMessage("同时生成保养工单" + WebUtil.getMessage("ValidationRequire"));
//            RequestContext.getCurrentInstance().addCallbackParam("IsGeneratePmOrder", true);
//            isValidate = false;
//        }
        if (arrivalListItem.getWorkHours() == null) {
            WebUtil.addErrorMessage("安装工时" + WebUtil.getMessage("ValidationRequire"));
            RequestContext.getCurrentInstance().addCallbackParam("workHours", true);
            isValidate = false;
        }
        if (Strings.isNullOrEmpty(arrivalListItem.getSerialNum())) {
            WebUtil.addErrorMessage(WebUtil.getMessage("serialNum") + WebUtil.getMessage("ValidationRequire"));
            RequestContext.getCurrentInstance().addCallbackParam("serialNum", true);
            isValidate = false;
        }
        if (arrivalListItem.getArrivalList().getProcessType().equals(ArrivalList.ProcessType.Normal.toString())) {
            if (!arrivalListItem.getBusinessAckStatus().equals(ArrivalListItem.ACK_STATUS_PASS)) {
                WebUtil.addErrorMessage("商务验收未通过");
                isValidate = false;
            }
            if (!arrivalListItem.getClinicalAckStatus().equals(ArrivalListItem.ACK_STATUS_PASS)) {
                WebUtil.addErrorMessage("临床验收未通过");
                isValidate = false;
            }
            if (!arrivalListItem.getTechnicalAckStatus().equals(ArrivalListItem.ACK_STATUS_PASS)) {
                WebUtil.addErrorMessage("技术验收未通过");
                isValidate = false;
            }
        }

        if (!Strings.isNullOrEmpty(arrivalListItem.getQrCode())) {
            if (!isAvailableQrcode(arrivalListItem.getQrCode())) {
                arrivalListItem.setQrCode(null);
                isValidate = false;
            }
        }

        Tuple2<Boolean, String> isvalidDeptNum = aiService.isValidDeptNum(arrivalListItem);
        if (!isvalidDeptNum._1) {
            isValidate = false;
            WebUtil.addErrorMessage(isvalidDeptNum._2);
        }

        if (!isValidate) {
            return "";
        }

        if (arrivalListItem.getWarrantyYear() != null) {
            Integer monthLong = Double.valueOf(arrivalListItem.getWarrantyYear() * 12).intValue();
            Calendar calender = Calendar.getInstance();
            calender.setTime(new Date());
            calender.add(Calendar.MONTH, monthLong);
            arrivalListItem.setFactoryWarrantyDate(calender.getTime());
        }
        
        if(configService.isNeedAssetApprover(arrivalListItem.getArrivalList().getSiteUID())){
            //待审核
            arrivalListItem.setUsageStatus(2);
        }
        
        if (arrivalListItem.getInstallStatus().equals(ArrivalListItem.INSTALL_STATUS_NEW)) {
            aiService.saveOrUpdateItem(arrivalListItem);
            if (aiService.submitInstallItem(arrivalListItem.getId(), true, arrivalListItem.getIsGeneratePmOrder() == null ? false : arrivalListItem.getIsGeneratePmOrder())) {
                WebUtil.addSuccessMessage("提交成功");
            } else {
                WebUtil.addErrorMessage("提交失败");
            }

        }
        return returnAction();
    }

    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        attachList.removeAll(attachList.stream().filter(item -> item.getId().equals(blobId)).collect(Collectors.toList()));
    }

    public List<V2_BlobObject> getAttachList(String type) {
        return attachList == null ? null : attachList.stream().filter(item -> type.equals(item.getBoSubType()))
                .collect(Collectors.toList());
    }

    public String returnAction() {
        if ("List".equals(src)) {
            return "arrivalItemsList?faces-redirect=true";
        } else {
            return UrlEncryptController.encodeUrlParam("arrivalListEdit?action=Edit&listId=" + arrivalListItem.getArrivalListId());
        }
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
        typeName = typeName.substring(typeName.indexOf("type") + 4);
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            attachList.add(blobObject);
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
        assetFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        assetFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, this.arrivalListItem.getArrivalList().getTenantUID()));
        assetFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, arrivalListItem.getArrivalList().getSiteUID()));
        if (!Strings.isNullOrEmpty(arrivalListItem.getClinicalDeptUID())) {
            assetFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, arrivalListItem.getClinicalDeptUID()));
        }
        if (config != null && config.getGenerateMethod() != null && config.getGenerateMethod() == 2) {
            assetFilters.add(new SearchFilter("parentAssetId", SearchFilter.Operator.IsNull, ""));
        }
        return assetDao.findBySearchFilter(assetFilters);
    }

    public void onSelectGbTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        GbCode gbCode = (GbCode) node.getData();
        updateSelectedOrgGb(gbCode);
        //联动更新
        if (null != gbCodeName) {
            this.arrivalListItem.setNationalStandardCode(gbCodeName);
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
        onDepartNumChange("qrCode");
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
                arrivalListItem.getNationalStandardCode() == null ? null : arrivalListItem.getNationalStandardCode().split("/")[0],
                Strings.isNullOrEmpty(arrivalListItem.getClinicalDeptUID()) ? arrivalListItem.getArrivalList().getSiteUID() : arrivalListItem.getClinicalDeptUID(),
                arrivalListItem.getParentAssetId(), prifix);
        if (null != financingNum && financingNum.startsWith("error:")) {
            WebUtil.addErrorMessage(financingNum.replace("error:", ""));
            financingNum = "";
        }
        if (financingNum != null && (StringUtils.isEmpty(this.arrivalListItem.getFinancingNum()) || this.arrivalListItem.getFinancingNum().length() <= 4)) {
            this.arrivalListItem.setFinancingNum(financingNum);
        }
        onDepartNumChange("financingNum");
    }

    public String getQrCodeImageForAsset() {
        AssetInfo asset = new AssetInfo();
        asset.setQrCode(arrivalListItem.getQrCode());
        asset.setName(arrivalListItem.getArrivalList().getAssetName());
        asset.setAssetGroup(arrivalListItem.getArrivalList().getAssetGroup());
        asset.setSiteUID(arrivalListItem.getArrivalList().getSiteUID());
        asset.setFunctionType(arrivalListItem.getArrivalList().getFunctionType());
        asset.setClinicalDeptUID(arrivalListItem.getClinicalDeptUID());
        asset.setSerialNum(arrivalListItem.getSerialNum());
        asset.setDepartNum(arrivalListItem.getDepartNum());
        asset.setFinancingNum(arrivalListItem.getFinancingNum());
        asset.setModalityId(arrivalListItem.getModalityId());
        asset.setSystemId(arrivalListItem.getSystemId());
        return qrCodeService.getQrCodeImageBase64WithTemplate(asset, exportQrCodeTemplateId, arrivalListItem.getArrivalList().getSiteUID());
    }

    public void onAssetOwnerNameChange() {
        UserAccount owner1 = aiService.getUserAccountByUid(arrivalListItem.getAssetOwnerUID());
        List<Object[]> ownerComposition = assetDao.findAssetOwnerComposition(owner1.getId(), arrivalListItem.getArrivalList().getSiteUID());
        if (ownerComposition != null && !ownerComposition.isEmpty()) {
            Object[] composition = ownerComposition.get(0);
            Integer assetOwnerId2 = (Integer) composition[2];
            if (assetOwnerId2 != null) {
                UserAccount owner2 = aiService.getUserAccountById(assetOwnerId2);
                arrivalListItem.setAssetOwnerUID2(owner2 == null ? null : owner2.getUid());
            }
            Integer assetOwnerId3 = (Integer) composition[3];
            if (assetOwnerId3 != null) {
                UserAccount owner3 = aiService.getUserAccountById(assetOwnerId3);
                arrivalListItem.setAssetOwnerUID3(owner3 == null ? null : owner3.getUid());
            }
        }
    }

    public void onDepartNumChange(String fieldName) {
        if (config == null) {
            return;
        }
        if (!Strings.isNullOrEmpty(config.getAssetCodeConfig())) {
            String num = null;
            switch (config.getAssetCodeConfig()) {
                case "DFQ":
                    switch (fieldName) {
                        case "financingNum":
                            num = arrivalListItem.getFinancingNum();
                            if (!Strings.isNullOrEmpty(arrivalListItem.getQrCode())) {
                                if (!qrCodeService.deleteQrcode(arrivalListItem.getQrCode(), arrivalListItem.getArrivalList().getSiteUID())) {
//                                    qrCodeService.discardQrCode(arrivalListItem.getQrCode(), arrivalListItem.getArrivalList().getSiteUID());
                                    WebUtil.addErrorMessage("二维码不正确");
                                    arrivalListItem.setQrCode(null);
                                } else {
                                    arrivalListItem.setQrCode(num);
                                }
                            } else {
                                arrivalListItem.setQrCode(num);
                            }
                            break;
                        case "departNum":
                            num = arrivalListItem.getDepartNum();
                            if (!Strings.isNullOrEmpty(arrivalListItem.getQrCode())) {
                                if (!qrCodeService.deleteQrcode(arrivalListItem.getQrCode(), arrivalListItem.getArrivalList().getSiteUID())) {
//                                    qrCodeService.discardQrCode(arrivalListItem.getQrCode(), arrivalListItem.getArrivalList().getSiteUID());
                                    WebUtil.addErrorMessage("二维码不正确");
                                    arrivalListItem.setQrCode(null);
                                } else {
                                    arrivalListItem.setQrCode(num);
                                }
                            } else {
                                arrivalListItem.setQrCode(num);
                            }
                            break;
                        case "qrCode":
                            num = arrivalListItem.getQrCode();
                            arrivalListItem.setQrCode(num);
                            break;
                        default:
                    }

                    if (!Strings.isNullOrEmpty(num) && num.length() > 16) {
                        num = num.substring(0, 16);
                        if (null != arrivalListItem.getQrCode()) {
                            arrivalListItem.setQrCode(num);
                            WebUtil.addErrorMessage("二维码超长,最大16位");
                        }
                    }
                    arrivalListItem.setFinancingNum(num);
                    arrivalListItem.setDepartNum(num);

                    break;
                case "DF":
                    switch (fieldName) {
                        case "financingNum":
                            num = arrivalListItem.getFinancingNum();
                            arrivalListItem.setFinancingNum(num);
                            arrivalListItem.setDepartNum(num);
                            break;
                        case "departNum":
                            num = arrivalListItem.getDepartNum();
                            arrivalListItem.setFinancingNum(num);
                            arrivalListItem.setDepartNum(num);
                            break;
                    }
                    break;
                default:
            }
        }
    }

    public void onAckChange(String ackType) {
        switch (ackType) {
            case "technical":
                if (ArrivalListItem.ACK_STATUS_PASS.equals(arrivalListItem.getTechnicalAckStatus())) {
                    arrivalListItem.setTechnicalAckDate(new Date());
                } else {
                    arrivalListItem.setTechnicalAckDate(null);
                }
            case "clinical":
                if (ArrivalListItem.ACK_STATUS_PASS.equals(arrivalListItem.getClinicalAckStatus())) {
                    arrivalListItem.setClinicalAckDate(new Date());
                } else {
                    arrivalListItem.setClinicalAckDate(null);
                }
            case "business":
                if (ArrivalListItem.ACK_STATUS_PASS.equals(arrivalListItem.getBusinessAckStatus())) {
                    arrivalListItem.setBusinessAckDate(new Date());
                } else {
                    arrivalListItem.setBusinessAckDate(null);
                }
        }
    }

    public List<String> autoCompleteDept(String query) {
        List<String> res = deptInfoList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).map(OrgInfo::getName).collect(Collectors.toList());
        res.sort(new PinYinComparator());
        return res;
    }

    public void onClinicalDeptChange(SelectEvent event) {
        OrgInfo dept = deptInfoList.stream().filter(item -> item.getName().equals(deptName)).findFirst().orElse(null);
        if (dept != null) {
            arrivalListItem.setClinicalDeptUID(dept.getUid());
        }
    }

    public List<QrCodeTemplate> getQrCodeTemplates() {
        QrCodeTemplateService qtService = WebUtil.getBean(QrCodeTemplateService.class
        );
        return qtService.getTemplateByUser(UserContextService.getCurrentUserAccount());
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

    public List<I18nMessage> getCode68List() {
        return code68List;
    }

    public void setCode68List(List<I18nMessage> code68List) {
        this.code68List = code68List;
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

    public String getExportQrCodeTemplateId() {
        return exportQrCodeTemplateId;
    }

    public void setExportQrCodeTemplateId(String exportQrCodeTemplateId) {
        this.exportQrCodeTemplateId = exportQrCodeTemplateId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

}
