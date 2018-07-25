/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.BrandRepository;
import com.ge.apm.dao.GbCodeRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetCreateService;
import com.ge.apm.service.asset.AssetFinacingNumGenerateService;
import com.ge.apm.service.asset.AssetInfoMgrService;
import com.ge.apm.service.asset.GbCodeService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.utils.TimeUtils;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class AssetCreateController {

    private AssetInfo assetInfo;
    private QrCodeLib createRequest;
    private AssetCreateService acService;
    private BrandRepository brandDao;
    private OrgInfoRepository orgDao;

    private GbCodeRepository gbDao;
    private GbCodeService gbCodeService;

    private AssetInfoMgrService assetMgrService;
    private AssetInfoMgrService aimService;

    private String[] selectedPictures;

    private UserAccount owner;

    private OrgInfo clinicalDept;

    private Brand brand;
    private String rejectText;
    private List<String> rejectTextHistory;
    private List<String> commentsList;

    List<V2_BlobObject> pictureList;
    List<V2_BlobObject> audioList;

    private UserAccount clinicalOwner;

    private Boolean isGeneratePmOrder;

    private List<I18nMessage> code68List;
    private I18nMessageRepository i18nDao;
    private GbCode selectedGb;
    private String gbCodeName;

    private TreeNode gbTree;
    private TreeNode selectedGbNode;
    private AssetFinacingNumGenerateService assetFinacingNumGenerateService;

    private List<Supplier> supplierList = Collections.emptyList();
    private Map<String, Object> selectedObjectMap = new HashMap();

    @PostConstruct
    protected void init() {
        String qrCode = WebUtil.getRequestParameter("qrCode");
        String siteUid = WebUtil.getRequestParameter("siteUid");
        acService = WebUtil.getBean(AssetCreateService.class);
        brandDao = WebUtil.getBean(BrandRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        gbDao = WebUtil.getBean(GbCodeRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        gbCodeService = WebUtil.getBean(GbCodeService.class);
        assetMgrService = WebUtil.getBean(AssetInfoMgrService.class);
        aimService = WebUtil.getBean(AssetInfoMgrService.class);
        assetFinacingNumGenerateService = WebUtil.getBean(AssetFinacingNumGenerateService.class);

        createRequest = acService.getCreateRequest(qrCode, siteUid);
        assetInfo = createAssetInfo();

        pictureList = acService.getQrCodePictureList(createRequest.getUid());
        audioList = acService.getQrCodeAudioList(createRequest.getUid());

        //默认图片全选
        selectedPictures = new String[pictureList.size()];
        for (int i = 0; i < pictureList.size(); i++) {
            selectedPictures[i] = pictureList.get(i).getObjectStorageId();
        }

        rejectTextHistory = new ArrayList();
        if (null != createRequest.getFeedback()) {
            Collections.addAll(rejectTextHistory, createRequest.getFeedback().split("//"));
        }
        commentsList = new ArrayList();
        if (null != createRequest.getComment()) {
            Collections.addAll(commentsList, createRequest.getComment().split("\\|"));
        }

        if (null != createRequest.getOrgId()) {
            clinicalDept = orgDao.findById(createRequest.getOrgId());
        }

        supplierList = aimService.getSuplierListBySiteUid(createRequest.getTenantUID());
        initGbTree();

    }

    public void onBrandChange() {
        if (null != brand) {
            assetInfo.setBrand(brandDao.findById(brand.getId()));
        }
    }

    public void onSupplierChange(String idx) {
        Supplier supplier = (Supplier) selectedObjectMap.get(idx);
        switch (idx) {
            case "maintainer":
                assetInfo.setMaitananceId(supplier == null ? null : supplier.getId().toString());
                assetInfo.setMaitanance(supplier == null ? null : supplier.getName());
                assetInfo.setMaitananceTel(supplier == null ? null : supplier.getTel());
                break;
            case "manufacture":
                assetInfo.setManufactureId(supplier == null ? null : supplier.getId().toString());
                assetInfo.setManufacture(supplier == null ? null : supplier.getName());
                break;
        }
    }

    public List<Supplier> autoCompleteManufacture(String query) {
        List<Supplier> res = supplierList.stream().filter(item -> item.getManufacturerT() != null && item.getManufacturerT()).filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    public List<Supplier> autoCompleteMaintainer(String query) {
        List<Supplier> res = supplierList.stream().filter(item -> item.getMaintainerT() != null && item.getMaintainerT()).filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    private AssetInfo createAssetInfo() {
        AssetInfo newAsset = new AssetInfo();
        newAsset.setQrCode(createRequest.getQrCode());
        newAsset.setSerialNum(createRequest.getSerialNum());
        newAsset.setFunctionType(createRequest.getModelNum());
        newAsset.setManufactDate(createRequest.getManufactureDate());
        newAsset.setIsValid(true);
        newAsset.setStatus(1);
        newAsset.setDeviceStatus(0);
        newAsset.setIsDeleted(Boolean.FALSE);
        newAsset.setName(createRequest.getAssetName());
        newAsset.setAssetGroup(createRequest.getAssetGroup());
        if (null != createRequest.getOrgId() && createRequest.getOrgId() > 0) {
            OrgInfo org = acService.getOrgInfo(createRequest.getOrgId());
            newAsset.setClinicalDeptId(createRequest.getOrgId());
            newAsset.setClinicalDeptUID(org.getUid());
        }
        OrgInfo siteOrg = orgDao.findByUid(createRequest.getSiteUID());
        newAsset.setHospitalId(siteOrg.getId());
        newAsset.setSiteId(siteOrg.getSiteId());
        newAsset.setTenantUID(createRequest.getTenantUID());
        newAsset.setHospitalUID(createRequest.getHospitalUID());
        newAsset.setInstitutionUID(createRequest.getInstitutionUID());
        newAsset.setSiteUID(createRequest.getSiteUID());
        newAsset.setUsageStatus(acService.needAssetApproval(createRequest.getSiteUID()) ? 2 : 3);

        if (null != createRequest.getUserId()) {
            owner = acService.getUserInfo(createRequest.getUserId());
            newAsset.setAssetOwnerId(owner.getId());
            newAsset.setAssetOwnerName(owner.getName());
            newAsset.setAssetOwnerTel(owner.getTelephone());
        }

        if (!Strings.isNullOrEmpty(createRequest.getSubmitWechatId())) {
            UserAccount submitter = acService.getUserByWechatId(createRequest.getSubmitWechatId());
            if (null != submitter) {
                newAsset.setCreatedBy(submitter.getUid());
            }
        }
        newAsset.setCreatedDate(createRequest.getSubmitDate());
        return newAsset;
    }

    public List<OrgInfo> getClinicalDeptList() {
        return acService.getClinicalDeptList(assetInfo.getSiteUID());
    }

    public List<OrgInfo> getClinicalDeptListWithEmptyValue() {
        List<OrgInfo> orgInfos = acService.getClinicalDeptList(assetInfo.getSiteUID());

        OrgInfo emptyValue = new OrgInfo();
        emptyValue.setName(WebUtil.getMessage("selectOneMessage"));
        orgInfos.add(0, emptyValue);

        return orgInfos;
    }

    public String getHospitalName() {
        return acService.getSiteName(assetInfo.getSiteUID());
    }

    @Transactional
    public String applyChange() {
        if (owner != null) {
            assetInfo.setAssetOwnerId(owner.getId());
            assetInfo.setAssetOwnerName(owner.getName());
            assetInfo.setAssetOwnerTel(owner.getTelephone());
        }
        if (null != assetInfo.getClinicalDeptId()) {
            OrgInfo clinicalDept = acService.getOrgInfo(assetInfo.getClinicalDeptId());
            assetInfo.setClinicalDeptName(clinicalDept.getName());
            assetInfo.setClinicalDeptUID(clinicalDept.getUid());
        }
        Boolean res = acService.createAsset(assetInfo, isGeneratePmOrder);
        if (null != isGeneratePmOrder && isGeneratePmOrder && Strings.isNullOrEmpty(assetInfo.getClinicalDeptUID())) {
            WebUtil.putGrowlMsg("使用科室未填，不能生成保养工单");
        }
        if (res) {
            acService.createAttachments(assetInfo, selectedPictures);
            acService.updateQrLib(assetInfo.getQrCode(), 3, createRequest.getSiteUID());
            WebUtil.addSuccessMessage("创建成功");
            return "RequestsList?faces-redirect=true";
        } else {
            WebUtil.addErrorMessage("创建失败");
            return "";
        }
    }

    public void returnBack() {
        acService.rejectReturn(createRequest, rejectText);
        this.rejectTextHistory.add(TimeUtils.getStrDate(new Date(), "[yyyy-MM-dd HH:mm:ss]").concat(rejectText));
        rejectText = "";
        WebUtil.addSuccessMessage("消息已发送");
    }

    public List<UserAccount> getOwnerList() {
        List<UserAccount> accountList = getEmptyAccountList();
        accountList.addAll(acService.getWorkerList());
//        accountList.addAll(acServie.getAssetOwnerList(Optional.ofNullable(assetInfo.getClinicalDeptUID()).orElse(assetInfo.getSiteUID())));
        return accountList;
    }

    public void onClinicalDeptChange() {
        if (this.clinicalDept == null) {
            assetInfo.setClinicalDeptId(null);
        } else {
            assetInfo.setClinicalDeptId(this.clinicalDept.getId());
        }

        if (null != assetInfo.getClinicalDeptId()) {
            OrgInfo clinicalDept = acService.getOrgInfo(assetInfo.getClinicalDeptId());
            assetInfo.setClinicalDeptName(clinicalDept.getName());
            assetInfo.setClinicalDeptUID(clinicalDept.getUid());
        } else {
            assetInfo.setClinicalDeptName(null);
            assetInfo.setClinicalDeptUID(null);
        }
        this.owner = null;
        this.clinicalOwner = null;
        assetInfo.setClinicalOwnerId(null);
        assetInfo.setClinicalOwnerName(null);
        assetInfo.setClinicalOwnerTel(null);
    }

    public void onClinicalOwnerChange() {
        if (null != clinicalOwner) {
            assetInfo.setClinicalOwnerId(clinicalOwner.getId());
            assetInfo.setClinicalOwnerName(clinicalOwner.getName());
            assetInfo.setClinicalOwnerTel(clinicalOwner.getTelephone());
        }
    }

    public List<UserAccount> getClinicalOwnerList() {
        List<UserAccount> accountList = getEmptyAccountList();
        accountList.addAll(acService.getClinicalOwnerList(assetInfo.getClinicalDeptUID(), this.clinicalOwner)
                .stream().filter(item -> item.getUserRoleList() != null && !item.getUserRoleList().stream()
                .anyMatch(roleItem -> roleItem.getRole().getName().equals("Guest"))).collect(Collectors.toList()));
        return accountList;
    }

    private List<UserAccount> getEmptyAccountList() {
        List<UserAccount> accountList = new ArrayList();
        UserAccount emptyValue = new UserAccount();
        emptyValue.setName(WebUtil.getMessage("selectOneMessage"));
        accountList.add(emptyValue);
        return accountList;
    }

    public List<Brand> getBrandList() {
        return acService.getBrandList();
    }

    public void onFunctionGroupChange() {
        code68List = new ArrayList<>();
        this.assetInfo.setAssetCode68(null);

        this.changeFunctionGroup();
    }

    public void changeFunctionGroup() {
        Integer functionGroupId = this.assetInfo.getFunctionGroup();
        if (functionGroupId != null) {
            I18nMessage functionGroup = i18nDao.getByMsgTypeAndMsgKey("assetFunctionType", functionGroupId.toString());
            if (functionGroup != null) {
                code68List = i18nDao.getByMsgType(functionGroup.getMsgKey());
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

    private void initGbTree() {
        TreeNode treeNode = gbCodeService.buildGbTree();
        gbTree = new DefaultTreeNode("root", null, null);
        gbTree.getChildren().add(treeNode);
        treeNode.setExpanded(true);
    }

    public void onSelectGbTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        GbCode gbCode = (GbCode) node.getData();
        updateSelectedOrgGb(gbCode);
        //联动更新
        if (null != gbCodeName) {
            this.assetInfo.setNationalStandardCode(gbCodeName);

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

    public void gennerateFinancingNum() {
        String prifix = assetInfo.getFinancingNum() == null ? "" : assetInfo.getFinancingNum();
        String financingNum = assetFinacingNumGenerateService.autoGenerateFinacingNum(null,
                assetInfo.getNationalStandardCode() == null ? null : assetInfo.getNationalStandardCode().split("/")[0],
                Strings.isNullOrEmpty(assetInfo.getClinicalDeptUID()) ? assetInfo.getSiteUID() : assetInfo.getClinicalDeptUID(), assetInfo.getParentAssetId(), prifix);
        if (null != financingNum && financingNum.startsWith("error:")) {
            WebUtil.addErrorMessage(financingNum.replace("error:", ""));
            financingNum = "";
        }
        if (financingNum != null && (StringUtils.isEmpty(this.assetInfo.getFinancingNum()) || this.assetInfo.getFinancingNum().length() <= 4)) {
            this.assetInfo.setFinancingNum(financingNum);
        }
    }

    public List<AssetInfo> getParentAssetList() {
        return assetMgrService.getParentsAssetsList(assetInfo);
    }

    //getter and setter
    public QrCodeLib getCreateRequest() {
        return createRequest;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public String[] getSelectedPictures() {
        return selectedPictures;
    }

    public void setSelectedPictures(String[] selectedPictures) {
        this.selectedPictures = selectedPictures;
    }

    public UserAccount getOwner() {
        return owner;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public void setOwner(UserAccount owner) {
        this.owner = owner;
    }

    public List<V2_BlobObject> getPictureList() {
        return pictureList;
    }

    public List<V2_BlobObject> getAudioList() {
        return audioList;
    }

    public String getRejectText() {
        return rejectText;
    }

    public void setRejectText(String rejectText) {
        this.rejectText = rejectText;
    }

    public List<String> getRejectTextHistory() {
        return rejectTextHistory;
    }

    public List<String> getCommentsList() {
        return commentsList;
    }

    public UserAccount getClinicalOwner() {
        return clinicalOwner;
    }

    public void setClinicalOwner(UserAccount clinicalOwner) {
        this.clinicalOwner = clinicalOwner;
    }

    public Boolean getIsGeneratePmOrder() {
        return isGeneratePmOrder;
    }

    public void setIsGeneratePmOrder(Boolean isGeneratePmOrder) {
        this.isGeneratePmOrder = isGeneratePmOrder;
    }

    public List<I18nMessage> getCode68List() {
        return code68List;
    }

    public TreeNode getGbTree() {
        return gbTree;
    }

    public void setGbTree(TreeNode gbTree) {
        this.gbTree = gbTree;
    }

    public TreeNode getSelectedGbNode() {
        return selectedGbNode;
    }

    public void setSelectedGbNode(TreeNode selectedGbNode) {
        this.selectedGbNode = selectedGbNode;
    }

    public OrgInfo getClinicalDept() {
        return clinicalDept;
    }

    public void setClinicalDept(OrgInfo clinicalDept) {
        this.clinicalDept = clinicalDept;
    }

    public Map<String, Object> getSelectedObjectMap() {
        return selectedObjectMap;
    }

    public void setSelectedObjectMap(Map<String, Object> selectedObjectMap) {
        this.selectedObjectMap = selectedObjectMap;
    }

}
