package com.ge.apm.view.asset;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.domain.pure.KeyLabel;
import com.ge.apm.service.asset.*;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.common.CustomConfigConstant;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.orginfo.OrgInfoClinicalDeptService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ApmConstant;
import com.ge.apm.service.utils.PinYinComparator;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.zkb.ZkbFileDirectoryService;
import com.ge.apm.view.SupplierController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.*;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetInfoBulkEditController extends JpaCRUDController<AssetInfo>  {
    private static final Logger logger = LoggerFactory.getLogger(AssetInfoBulkEditController.class);

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
    private GbCodeService gbCodeService;
    private QrCodeService qrCodeService;
    private CustomConfigService ccService;
    private AssetInfoBulkEditService assetInfoBulkEditService;
    private AssetInfoBulkEditCheckService assetInfoBulkEditCheckService;
    private OrgInfoRepository orgInfoDao;
    private V2ServiceRequestRepository v2ServiceRequestRepository;
    private AssetFinacingNumGenerateService assetFinacingNumGenerateService;
    private AssetCreateService acService;
    private ZkbFileDirectoryService zkbFileDirectoryService;
    private BlobFileService blobService;
    private AssetHistoryService assetHistoryService;
    private SupplierController supplierController;
    private QrCodeLib createRequest;
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
    private List<String> editableFieldsList;
    @Getter@Setter
    private CustomConfigObject customConfig;
    //设备状态
    @Getter
    List<I18nMessage> assetStatusList = Collections.emptyList();
    @Setter
    private List<ZkbFileDirectory> subZkbFileDirectoryList;
    private List<V2_BlobObject> attachements;
    @Getter
    @Setter
    private List<V2_BlobObject> pictureList;
    @Setter
    @Getter
    private String[] selectedPictures;
    @Getter
    private List<JSONObject> changeHistoryList = new ArrayList<>();
    private String uidList = null;
    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
        assetInfoService = WebUtil.getBean(AssetInfoService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        assetInfoBulkEditService = WebUtil.getBean(AssetInfoBulkEditService.class);
        assetInfoBulkEditCheckService = WebUtil.getBean(AssetInfoBulkEditCheckService.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
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
        v2ServiceRequestRepository = WebUtil.getBean(V2ServiceRequestRepository.class);
        acService = WebUtil.getBean(AssetCreateService.class);
        zkbFileDirectoryService = WebUtil.getBean(ZkbFileDirectoryService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        assetHistoryService = WebUtil.getBean(AssetHistoryService.class);
        supplierController = WebUtil.getBean(SupplierController.class);

        selected = new AssetInfo();
        customConfig = ccService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());
        readableFieldsList = ccService.getReadAssetFieldsByRoles(customConfig, UserContextService.getRoles());
        editableFieldsList = ccService.getEditAssetFieldsByRoles(customConfig, UserContextService.getRoles());
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
        updateAssetStatusList();
        uidList = WebUtil.getRequestParameter("uidList");
    }
    public void saveNewSupplier() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();
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
        supplierList.add(newSupplier);
        if (StringUtils.isNotBlank(newSupplier.getUid()) && !supplierList.contains(newSupplier)) {
            supplierList.add(newSupplier);
        }
        changeSelect(createTpye);

    }
    public String getListPageLink() {
        return "AssetList.xhtml?faces-redirect=true";
    }
    public String applyChange() throws Exception {

        //check whether parent asset is valid when editing
        if (!isValidParentAsset()) {
            WebUtil.addErrorMessage("主设备不符合要求，请重新设置");
            return "";
        }

        //设置质保时间
        if (null == selected.getFactoryWarrantyDate()) {
            if (selected.getAcceptDate() != null && selected.getWarrantyMonth() != null) {
                selected.setFactoryWarrantyDate(TimeUtil.toJodaDate(selected.getAcceptDate()).plusMonths(selected.getWarrantyMonth()).toDate());
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
        if (UserContextService.isMVSRole() && this.selected.getMvsEquip() == null) {
            this.selected.setMvsEquip(1);
        }
        List<AssetInfo> assetInfoList = AssetInfoListController.resultAssetMap.get(uidList);
        List<AssetInfo> newList = new ArrayList<>();
        newList= depCopy(assetInfoList);
        for (AssetInfo assetInfo : newList ) {
            selected.setIsValid(assetInfo.getIsValid());
            selected.setIbDeviceStatus(assetInfo.getIbDeviceStatus());
            selected.setConnectToInsite(assetInfo.getConnectToInsite());
            if(selected.getAssetDeptId()==0){
                selected.setAssetDeptId(null);
            }
            selected.setSiteId(assetInfo.getSiteId());
            selected.setSiteUID(assetInfo.getSiteUID());
            selected.setHospitalId(assetInfo.getHospitalId());
            selected.setTenantUID(assetInfo.getTenantUID());
            selected.setHospitalUID(assetInfo.getHospitalUID());
            selected.setInstitutionUID(assetInfo.getInstitutionUID());
            copyPropertiesExclude(selected, assetInfo);
        }
        this.saveAssetHistory(assetInfoList,newList);
        for (AssetInfo assetInfo: newList) {
            dao.save(assetInfo);
            onAfterNewObject(assetInfo);
        }
        AssetInfoListController.resultAssetMap.clear();
        return "AssetList.xhtml?faces-redirect=true";
    }

    public void onAfterNewObject(AssetInfo object) {
        if(attachements !=null){
            attachements.forEach(item -> {
                if (item.getBoId() == null) {
                    item.setBoId(object.getId());
                    item.setBoUuid(object.getUid());
                    blobService.saveBlobObject(item);
                }
            });
        }

    }


    public static <T> List<T> depCopy(List<T> srcList) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try {
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(srcList);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream inStream = new ObjectInputStream(byteIn);
            List<T> destList = (List<T>) inStream.readObject();
            return destList;
        } catch (IOException e) {
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }







    /**
     * 复制对象属性：只复制需要的属性值，目标对象中原来的值不变
     * 只适用于2个相同对象
     * @param from 要复制的对象
     * @param to 目标对象
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static void copyPropertiesExclude(Object from, Object to) throws Exception {
        if(!from.getClass().equals(to.getClass())){
            throw new RuntimeException("对象不是同一类型，fromClass："+from.getClass()+",toClass："+to.getClass());
        }
        Method[] fromMethods = from.getClass().getDeclaredMethods();
        Method[] toMethods = to.getClass().getDeclaredMethods();
        Method fromMethod = null, toMethod = null;
        String fromMethodName = null, toMethodName = null;
        for (int i = 0; i < fromMethods.length; i++) {
            fromMethod = fromMethods[i];
            fromMethodName = fromMethod.getName();
            if (!fromMethodName.contains("get")) {
                continue;
            }
            toMethodName = "set" + fromMethodName.substring(3);
            toMethod = findMethodByName(toMethods, toMethodName);
            //如果方法为null，不处理
            if (toMethod == null) {
                continue;
            }
            Object value = fromMethod.invoke(from, new Object[0]);
            //如果值为空，不处理
            if(value == null) {
                continue;
            }
            //集合类判空处理
            if(value instanceof Collection) {
                Collection newValue = (Collection)value;
                if(newValue.size() <= 0)
                    continue;
            }
            toMethod.invoke(to, new Object[] {value});
        }
    }
    /**
     * 从方法数组中获取指定名称的方法
     *
     * @param methods
     * @param name
     * @return
     */
    public static Method findMethodByName(Method[] methods, String name) {
        for (int j = 0; j < methods.length; j++) {
            if (methods[j].getName().equals(name))
                return methods[j];
        }
        return null;
    }

    private void saveAssetHistory(List<AssetInfo> oldAssetInfoList,List<AssetInfo> assetInfoList) {
        for (AssetInfo oldAssetInfo:oldAssetInfoList ) {
            for (int i = 0; i < assetInfoList.size(); i++) {
                int oldId = oldAssetInfo.getId();
                int newId =assetInfoList.get(i).getId();
                if( oldId == newId ){
                    assetHistoryService.saveAssetHistoryIfUpdated(oldAssetInfo, assetInfoList.get(i), CustomConfigConstant.getAssetsImportantFieldsAll());
                }
            }
        }

    }
    private boolean updateQrCode() {
        QrCodeLib qrCodeLib = qrCodeService.findByQrCode(qrCode, selected.getSiteUID());
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

        QrCodeLib oldQRCodeLib = qrCodeService.findByQrCode(selected.getQrCode(), selected.getSiteUID());
        if (null != oldQRCodeLib) {
            qrCodeService.releaseQrCode(oldQRCodeLib.getQrCode(), selected.getSiteUID());
        }
        qrCodeService.updateQrCodeStatus(qrCode, selected.getSiteUID(), 3);
        selected.setQrCode(qrCode);
        return true;
    }
    public boolean isValidParentAsset() {

        if (selected.getParentAssetId() == null) {
            return true;
        }

        AssetInfo assetInfo = dao.findById(selected.getParentAssetId());

        if (null == assetInfo) {
            return false;
        }

        if (StringUtils.isEmpty(selected.getClinicalDeptUID())) {
            return false;
        }

        //parent asset and child asset will be in the same dept
        if (!selected.getClinicalDeptUID().equals(assetInfo.getClinicalDeptUID())) {
            return false;
        }

        return true;
    }

    public boolean needEdit(String fieldKey) {
        return editableFieldsList.contains(fieldKey);
    }
    public boolean needShow(String itemKey, String fieldKey) {
        return itemKey.equals(fieldKey) && readableFieldsList.contains(fieldKey);
    }
    public boolean needShow(String fieldKey) {

        if (customConfig.getAssetsEditpageBasicFields().contains(fieldKey)) return true;
        if (customConfig.getAssetsEditpageCatalogFields().contains(fieldKey)) return true;
        if (customConfig.getAssetsEditpageCodesFields().contains(fieldKey)) return true;
        if (customConfig.getAssetsEditpageDateFields().contains(fieldKey)) return true;
        if (customConfig.getAssetsEditpageFinicialFields().contains(fieldKey)) return true;
        if (customConfig.getAssetsEditpageStatusFields().contains(fieldKey)) return true;

        return false;
    }
    public boolean getIsSelect46Account() {
        if (UserContextService.getCurrentUserAccount().getSiteId()==46&&UserContextService.getCurrentUserAccount().getTenantUID().equals(UserContextService.getCurrentUserAccount().getSiteUID())) {
            return true;
        }
        return false;
    }
    //重新获取资产状态枚举
    public void updateAssetStatusList() {
        assetStatusList = i18nDao.getByMsgTypeAndSiteId("assetStatus", ApmConstant.DEFAULT_SITE_ID);
        assetStatusList.stream().forEach(status ->{
            if(status.getMsgKey().equals("4")){
                status.setEditable(hasScrappedAuthority());
            }else{
                status.setEditable(true);
            }
        });
    }
    //判断待报废按钮是否禁用
    public boolean hasScrappedAuthority() {
        return acService.hasScrappedAuthority();
    }
    public String selectCreatedBy(String loginName){
        UserAccount user = userDao.getByLoginName(loginName);
        if(user != null){
            return user.getName();
        }
        return null;
    }
    /**
     * 获取所有主目录信息
     * @return
     */
    public List<ZkbFileDirectory> findMainDirectory() {
        return zkbFileDirectoryService.findMainDirectory(null);
    }

    /**
     * 根据主目录值获取子目录信息
     * @return
     */
    public List<ZkbFileDirectory> getSubZkbFileDirectoryList() {
        if(this.selected==null||this.selected.getMainDirectory()==null)
        {
            return null;
        }
        List<ZkbFileDirectory> mainZkbFileDirectory = this.zkbFileDirectoryService.findByLevelAndName(
                ZkbFileDirectoryService.MAIN_DIRECTORY_LEVEL_VALUE,
                this.selected.getMainDirectory());
        if(CollectionUtils.isEmpty(mainZkbFileDirectory))
        {
            return null;
        }
        ZkbFileDirectory zkbFileDirectory = new ZkbFileDirectory();
        zkbFileDirectory=mainZkbFileDirectory.get(0);
        subZkbFileDirectoryList=this.zkbFileDirectoryService.findByZkbFileDirectory(zkbFileDirectory);
        return subZkbFileDirectoryList;
    }
    public List<V2_BlobObject> getAttachList(String assetuid, String type) {
        if (attachements == null) {
            attachements = blobService.findBlobObjectsByBoUid(assetuid);
        }
        return attachements.stream().filter(item -> item.getBoSubType() != null && item.getBoSubType().equals(type)).collect(Collectors.toList());
    }
    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        attachements = attachements.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toList());
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
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }
    public int getAssetChangeHistoryCount() {
        return changeHistoryList.size();
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
        this.updateSearchFilter(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        this.updateSearchFilter(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));

        if (null != filter.getSiteOrg()) {
            List<String> orgUidList = orgInfoDao.getOrgInfoUidsByparentUid(filter.getSiteOrg().getUid());
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilter = new SearchFilter("tenantUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID());
            this.searchFilters.add(searchFilter);
        }else {
            UserAccount user = UserContextService.getCurrentUserAccount();
            List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
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
            selected = new AssetInfo();
            WebUtil.addSuccessMessage("批量修改资产院区成功!");
        } catch(Exception e) {
            WebUtil.addErrorMessage(e.getMessage());
            logger.error(e.getMessage(), e);
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
