/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.AeMapper;
import com.ge.apm.dao.mapper.AssetTagsMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.*;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.utils.MvsUtil;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.onnx.RiskLevel;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.google.common.base.Strings;
import javaslang.Tuple2;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.TreeNode;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class AssetInfoDetailController {
    private Logger log = Logger.getLogger(AssetInfoController.class.getName());
    private AeMapper aeMapper;
    private AssetInfoRepository assetDao;
    private MvsAssetInfoRepository mvsAssetInfoDao;
    private MvsUtil mvsUtil;
    private ConfigCacheService configCacheService;
    private V2_PmScheduleRepository pmSchDao;
    private BlobObjectRepository blobDao;
    private SupplierRepository supplierDao;
    private CustomConfigService ccService;
    private AssetInfoMgrService aimService;
    private ConfigService configService;
    private ContractInfoService contractService;
    private ContractInfoItemRepository contractItemDao;
    private AssetTagsMapper assetTagsMapper;
    private UserAccountRepository userDao;
    private AssetHistoryService assetHistoryService;
    private SysAuthGroupRepository sysAuthGroupDao;
    private AssetTypeGroupRepository assetTypeGroupRepository;
    private UserAccountRepository userAccountRepository;
    private AssetInfoService assetInfoService;
    private BlobFileService blobService;
    private Boolean terminate;
    private AssetInfo selected;
    private List<V2_BlobObject> attachements;

    private CustomConfigObject customConfig;

    private List<String> readableFieldsList;
    private TreeNode parentsTree;

    private MvsAssetInfo mvsAssetInfo = new MvsAssetInfo();

    private String maintanceType = "internal";

    private List<AssetLifecycleEvent> assetEvents;
    private Boolean termOptions = Boolean.TRUE;//期限选项
    @Getter
    private List<JSONObject> changeHistoryList = null;
    private SysAuthGroup selectedAuthGroup;
    private AssetTypeGroup selectedAssetTypeGroup;
    @Getter
    private boolean countyArea = false;
    private UserAccount loginUser;

    private UserContextService userContextService;

    private ArrivalListItemRepository arrivalListItemRepository;

    private MultiLevelApprovalRepository multiLevelApprovalRepository;
    private SpecialAssetInfoService specialAssetInfoService;
    private V2_WorkOrderRepository workOrderRepository;


    private SpecialAssetInfo specialAssetInfo;
    @PostConstruct
    protected void init() {
        assetDao = WebUtil.getBean(AssetInfoRepository.class);
        workOrderRepository = WebUtil.getBean(V2_WorkOrderRepository.class);
        aeMapper = WebUtil.getBean(AeMapper.class);
        mvsAssetInfoDao = WebUtil.getBean(MvsAssetInfoRepository.class);
        mvsUtil = WebUtil.getBean(MvsUtil.class);
        pmSchDao = WebUtil.getBean(V2_PmScheduleRepository.class);
        blobDao = WebUtil.getBean(BlobObjectRepository.class);
        configCacheService = WebUtil.getBean(ConfigCacheService.class);
        supplierDao = WebUtil.getBean(SupplierRepository.class);
        ccService = WebUtil.getBean(CustomConfigService.class);
        configService = WebUtil.getBean(ConfigService.class);
        aimService = WebUtil.getBean(AssetInfoMgrService.class);
        contractItemDao = WebUtil.getBean(ContractInfoItemRepository.class);
        contractService = WebUtil.getBean(ContractInfoService.class);
        assetTagsMapper = WebUtil.getBean(AssetTagsMapper.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        assetHistoryService = WebUtil.getBean(AssetHistoryService.class);
        sysAuthGroupDao = WebUtil.getBean(SysAuthGroupRepository.class);
        assetTypeGroupRepository = WebUtil.getBean(AssetTypeGroupRepository.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        assetInfoService = WebUtil.getBean(AssetInfoService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        loginUser = UserContextService.getCurrentUserAccount();
        String encodeStr = WebUtil.getRequestParameter("str");
        String terminateStr = (String) UrlEncryptController.getValueFromMap(encodeStr, "terminate");
        terminate = Boolean.parseBoolean(terminateStr);
        String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
        if (selectedId != null) {
            selected = assetDao.findByUid(selectedId);
        } else {
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            terminate = Boolean.parseBoolean(request.getParameter("terminate"));
            String assetUid = (String) UrlEncryptController.getValueFromMap(request.getParameter("assetUid"), "assetUid");
            selected = assetDao.findByUid(assetUid);
        }
        selected.setAssetUsedLifecycle(AssetInfoService.getAssetUsedLifecycle(selected.getInstallDate(), selected.getLifecycle()));
        customConfig = ccService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());
        readableFieldsList = ccService.getReadAssetFieldsByRoles(customConfig, UserContextService.getRoles());
        userContextService = WebUtil.getBean(UserContextService.class);
        countyArea = userContextService.isCountyArea();
        arrivalListItemRepository = WebUtil.getBean(ArrivalListItemRepository.class);
        multiLevelApprovalRepository = WebUtil.getBean(MultiLevelApprovalRepository.class);
        specialAssetInfoService = WebUtil.getBean(SpecialAssetInfoService.class);
        assetInfoService.setSupplier(ListUtil.newArrayList(selected));
        maintanceType = aimService.getMaintanceType(selected);
        assetEvents  = aimService.getAssetEventsList(selected);

        if (null != selected.getMedicalApparatusClassification()) {
            selectedAuthGroup = sysAuthGroupDao.findByClassificationCode(selected.getMedicalApparatusClassification());
        }
        if (null != selected.getAssetType()) {
            selectedAssetTypeGroup = assetTypeGroupRepository.findByEncodedAndTenantUid(selected.getAssetType(), selected.getTenantUID());
        }

        //ge mvc data
        if (selected.getMvsEquip() != null && selected.getMvsEquip() == 1) {
            mvsUtil.copyToMvsTableIfMvsEquip(selected, mvsAssetInfo);
            MvsAssetInfo found = mvsAssetInfoDao.findById(selected.getUid());
            if (found != null) {
                mvsAssetInfo = found;
            }
        }

        List<String> existingTags = assetTagsMapper.getTagsByAsset(this.selected.getUid());
        this.selected.setTags(existingTags);

        if(Boolean.TRUE.equals(selected.isSpecialAsset())){
            specialAssetInfo=  specialAssetInfoService.findByAssetId(selected.getId());
        }
        assetInfoService.setWarrantyStatus(ListUtil.newArrayList(this.selected));
    }

    public OrgInfo getOrgInfoByUid(String uid) {
        if (Strings.isNullOrEmpty(uid)) {
            return null;
        }
        return configCacheService.findOrgByUid(uid);
    }

    public void getPerformanceByDay(){
        String str = aeMapper.selectAeMapperBean(String.valueOf(selected.getId()));
        if(str!=null && selected.getStudydate()!=null){
            List<HashMap> list = aeMapper.selectAeWorkloadBeanByDay(str,selected.getStudydate());
            if(list!= null && !list.isEmpty() && list.get(0)!=null){
                HashMap hashMap = list.get(0);
                Date startTime = (Date) hashMap.get("start_time");
                Date endTime = (Date) hashMap.get("end_time");
                Double studyexposuretime = (Double) hashMap.get("studyexposuretime");
                selected.setAet((String)hashMap.get("aet"));
                selected.setStudydate((Date)hashMap.get("studydate"));
                selected.setStartTime(startTime);
                selected.setEndTime(endTime);
                selected.setStudyexposuretime((double)Math.round(studyexposuretime/3600*100)/100);
                double count = studyexposuretime / ((endTime.getTime() - startTime.getTime())/1000);
                count = (double)Math.round(count*10000)/100;
                selected.setUsageRate(count+"%");
                Double monitorRate = selected.getMonitorRate();
                Double otherPower = selected.getOtherPower();
                Double unitPriceElectricity = selected.getUnitPriceElectricity()!=null ?selected.getUnitPriceElectricity() :0D;
                double energyConsumption = (monitorRate + otherPower) * unitPriceElectricity * studyexposuretime / 3600;
                selected.setEnergyConsumption((double)Math.round(energyConsumption*100)/100);
            }else {
                setCancel();
            }
        }
    }
    private void setCancel(){
        selected.setStartTime(null);
        selected.setEndTime(null);
        selected.setStudyexposuretime(null);
        selected.setUsageRate(null);
        selected.setEnergyConsumption(null);
    }

    public Boolean getTermOptions() {
        return termOptions;
    }

    public void setTermOptions(Boolean termOptions) {
        this.termOptions = termOptions;
        setCancel();
        selected.setStudydate(null);
    }

    public void getPerformanceByMonth(){
        String str = aeMapper.selectAeMapperBean(String.valueOf(selected.getId()));
        if(str!=null && selected.getStudydate()!=null){
            List<HashMap> list = aeMapper.selectAeWorkloadBeanByMonthMin(str,selected.getStudydate(),getLastDayOfMonth(selected.getStudydate()));
            if(list!= null && !list.isEmpty() && list.get(0)!=null){
                HashMap hashMap = list.get(0);
                Date startTime = (Date) hashMap.get("min");
                Date endTime = (Date) hashMap.get("max");
                Double studyexposuretime = 0D;
                List<HashMap> mapList = aeMapper.selectAeWorkloadBeanSumStudyexposuretime(str,selected.getStudydate(),getLastDayOfMonth(selected.getStudydate()));
                if(mapList!= null && !mapList.isEmpty()){
                    HashMap map = mapList.get(0);
                    studyexposuretime = (Double) map.get("sum");
                }
                selected.setStartTime(startTime);
                selected.setEndTime(endTime);
                selected.setStudyexposuretime((double)Math.round(studyexposuretime/3600*100)/100);
                double count = studyexposuretime / ((endTime.getTime() - startTime.getTime())/1000);
                count = (double)Math.round(count*10000)/100;
                selected.setUsageRate(count+"%");
                Double monitorRate = selected.getMonitorRate();
                Double otherPower = selected.getOtherPower();
                Double unitPriceElectricity = selected.getUnitPriceElectricity()!=null ?selected.getUnitPriceElectricity() :0D;
                double energyConsumption = (monitorRate + otherPower) * unitPriceElectricity * studyexposuretime / 3600;
                selected.setEnergyConsumption((double)Math.round(energyConsumption*100)/100);
            }else {
                setCancel();
            }
        }
    }
    public String getListDetail() throws UnknownHostException {
        String str = aeMapper.selectAeMapperBean(String.valueOf(selected.getId()));
        if(str!=null){
            List<String> list = aeMapper.selectAeWorkloadBeanByDashboardPage(str);
            if(list!=null && !list.isEmpty()){
                String dashboardPage = list.get(0);
                String dashboardPageStart = dashboardPage.substring(0,dashboardPage.indexOf("#"));
                String dashboardPageEnd = dashboardPage.substring(dashboardPage.indexOf("#"));
                InetAddress addr = InetAddress.getLocalHost();
                String hostAddress = addr.getHostAddress();
                String token = UserContextService.getAccessToken().substring(7);
                String url="http://"+hostAddress+ "/"+dashboardPageStart +"?"+"token="+token+dashboardPageEnd+"/device";
                log.info("url="+url);
                return url;
            }

        }
        return null;
    }
    public static Date getLastDayOfMonth(final Date date) {

        final Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        final int last = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        cal.set(Calendar.DAY_OF_MONTH, last);

        return cal.getTime();

    }
    public List<V2_BlobObject> getAttachList(String assetuid, String type) {
        if (attachements == null || attachements.isEmpty()) {
            List<SearchFilter> sfs = new ArrayList();
            sfs.add(new SearchFilter("boUuid", SearchFilter.Operator.EQ, assetuid));
            attachements = blobDao.findBySearchFilter(sfs);
        }
        return attachements.stream().filter(item -> item.getBoSubType() != null && item.getBoSubType().equals(type)).collect(Collectors.toList());
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            String fileName = event.getFile().getFileName();
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
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        attachements = attachements.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toList());
    }

    public boolean isLoginUser(String userUid) {
        return loginUser.getUid().equals(userUid);
    }

    public List<WorkflowConfig.AttachmentCategory> getAttachmentCategories() {
        return configService.getAttachmentCategories(configService.findConfigBySite(selected.getSiteUID()));
    }

    public List<PmScheduleData> getPmScheduleData(String type) {
        if (this.selected == null) {
            return null;
        }
        List<PmScheduleData> pmsList = pmSchDao.getPmScheduleList(this.selected.getUid(), type);
        return pmsList;
    }

    public List<AssetLifecycleEvent> getOrderList(Integer type){
        List<AssetLifecycleEvent> res =  assetEvents.stream().filter(item->item.getEventType() == type).sorted(new Comparator<AssetLifecycleEvent>(){
            @Override
            public int compare(AssetLifecycleEvent o1, AssetLifecycleEvent o2) {
                return o1.getCreateTime().compareTo(o2.getCreateTime());
            }
        }).collect(Collectors.toList());

        return res.size()>3? res.subList(0, 2):res;
    }

    public String getContractListLink() {
        return "/portal/asset/contract/ContractList?assetId=" + selected.getId();
    }

    public String getContractAddLink() {
        return "/portal/asset/contract/ContractEdit?assetId=" + selected.getId() + "&actionName=Create";
    }

    public String getListPageLink() {
        return "AssetList?faces-redirect=true" + (terminate ? "&terminate=true" : "");
    }

    public String getSpecialListPageLink() {
        return "specialAssetList?faces-redirect=true" + (terminate ? "&terminate=true" : "");
    }

    public String getParentAssetName() {
        if (this.selected.getParentAssetId() != null) {
            AssetInfo assetInfo = this.assetDao.getById(this.selected.getParentAssetId());
            if (assetInfo != null) {
                if (StringUtils.isEmpty(assetInfo.getFunctionType())) {
                    return assetInfo.getName();
                } else {
                    return assetInfo.getName() + "(" + assetInfo.getFunctionType() + ")";
                }
            }
        }
        return "";
    }
    public String selectCreatedBy(String loginName){
        UserAccount user = userDao.getByLoginName(loginName);
        if(user != null){
            return user.getName();
        }
        return null;
    }
    public String getCustomConfigString() {
        return JSON.toJSONString(customConfig);
    }

    public TreeNode getParentsRelationTree() {
        if (parentsTree == null) {
            parentsTree = aimService.getParentAssetTree(selected);
            TreeNodeUtil.expandTreeNode(parentsTree);
        }
        return parentsTree;
    }

    public boolean showParentsTree() {
        TreeNode node = getParentsRelationTree();
        if (node == null || node.getChildCount() == 0) {
            return false;
        }
        return node.getChildren().stream().anyMatch(item -> item.getChildCount() > 0);
    }

    public String getDetailPageLink(String uid) {
        AssetInfo viewAsset = assetDao.findByUid(uid);
        String url = "actionName=View&selectedid="
                + viewAsset.getUid().toString()
                + "&asset_name=" + viewAsset.getName()
                + "&terminate=" + this.terminate;
        url = "AssetDetail.xhtml?faces-redirect=true&str=" + UrlParamUtil.encodeUrlQueryString(url);
        return url;
    }

    public boolean needShow(String itemKey, String fieldKey) {
        return itemKey.equals(fieldKey) && readableFieldsList.contains(fieldKey);
    }

    public boolean show(String assetUid) {
        ArrivalListItem arrivalListItemByAssetUid = arrivalListItemRepository.getArrivalListItemByAssetUid(assetUid);
        if(arrivalListItemByAssetUid!=null){
            return true;
        }else{
            return false;
        }
    }
    public void onTabChange(TabChangeEvent event) {

    }

    public List<ContractInfo> getContractList(){
        return contractService.getContractListByAsset(selected);
    }

    public String getContractInvoice(ContractInfo contractInfo) {
        if (null == contractInfo) {
            return null;
        }
        switch (contractInfo.getContractType()) {
            case "Full":
            case "Other":
            case "Artificial":
                return contractInfo.getInvoiceNum();
            case "Equipment":
                List<ContractInfoItem> contractItemList =  contractItemDao.findByContractId(contractInfo.getId());
                StringBuilder sb= new StringBuilder();
                contractItemList.stream().filter(item -> item.getAssetUid()!=null && item.getAssetUid().equals(selected.getUid()))
                        .filter(item -> item.getArrivalListItem()!=null && null !=item.getArrivalListItem().getArrivalList().getInvoiceNum())
                        .map(item -> item.getArrivalListItem().getArrivalList().getInvoiceNum()).distinct()
                        .forEach(item-> sb.append(item).append(" "));
                return sb.toString();
            case "Parts":
                List<Tuple2<AssetInfo, V2_WorkOrder_Detail>>  parts = contractService.getAssetPartsInfo(contractInfo.getContractNum(),contractInfo.getTenantUID());
                StringBuilder partsSb= new StringBuilder();
                parts.stream().filter(item -> Objects.equals(item._1().getUid(), selected.getUid()))
                        .map(item -> item._2().getInvoiceNum()).distinct()
                        .forEach(item-> partsSb.append(item).append(" "));
                return partsSb.toString();
            default:
        }
        return null;
    }
    public String  getUserName(String loginName){
        if( null == loginName){
            return null;
        }
        UserAccount userAccount = userAccountRepository.getByLoginName(loginName);
        if (userAccount != null) {
            return userAccount.getName();
        }
        return null;
    }
    public String getSelectedAuthGroupName() {
        if (null == selectedAuthGroup) {
            return null;
        } else {
            return selectedAuthGroup.getClassificationCode() +"  "+ selectedAuthGroup.getName().replaceAll("\\d+","");
        }
    }
    public String getSelectedAssetTypeGroupName() {
        if (null == selectedAssetTypeGroup) {
            return null;
        } else {
            return selectedAssetTypeGroup.getDisplayName();
        }
    }

    //getter and setter
    public Boolean getTerminate() {
        return terminate;
    }

    public AssetInfo getSelected() {
        return selected;
    }

    public CustomConfigObject getCustomConfig() {
        return customConfig;
    }

    public MvsAssetInfo getMvsAssetInfo() {
        return mvsAssetInfo;
    }

    public void setMvsAssetInfo(MvsAssetInfo mvsAssetInfo) {
        this.mvsAssetInfo = mvsAssetInfo;
    }

    public String getMaintanceType() {
        return maintanceType;
    }

    public int getAssetChangeHistoryCount() {
        if(null==changeHistoryList  && null!=selected){
            changeHistoryList = assetHistoryService.getAssetChangeHistoryByJSON(selected.getUid());
        }
        return changeHistoryList.size();
    }

    public SpecialAssetInfo getSpecialAssetInfo() {
        return specialAssetInfo;
    }

    public void setSpecialAssetInfo(SpecialAssetInfo specialAssetInfo) {
        this.specialAssetInfo = specialAssetInfo;
    }

    //    @Getter
//    private List<MultiLevelApproval> assetApprovalProcess=new ArrayList();
//    public  int getAssetProcessRecordCount(){
//        if(CollectionUtils.isEmpty(assetApprovalProcess) && null!=selected){
//            assetApprovalProcess=multiLevelApprovalRepository.getAssetProcessRecord(selected.getTenantUID(),selected.getUid());
//        }
//        return assetApprovalProcess.size();
//    }
    public boolean verifyFileSuffixName(String fileName,String fileSuffixName){

        if(fileName.contains(fileSuffixName)){
            return true;
        }
        return false;
    }

    public String getWorkOrderId(String srId) {
        List<V2_WorkOrder> orders = workOrderRepository.findBySrId(srId);
        return orders.get(0).getId();
    }

    public String getQueryTabValue(Integer assetId) {
        boolean isYiMaiYangGuangTenant = userContextService.isYiMaiYangGuangTenant();
        boolean selectedIdMatches = selected.getId().equals(assetId);
        if (!isYiMaiYangGuangTenant || selectedIdMatches) {
            return "0";
        } else {
            return "1";
        }
    }

    public String getEstimatedRiskLevelName(Integer code) {
        if (code == null) return "";
        RiskLevel level = RiskLevel.getByCode(code);
        return level != null ? level.getName() : "";
    }


    public void buildDataLinkUrl() {
        String uid = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("uid");

        String type = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("type");

        Integer assetId = selected.getId();

        String url = "";
        String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();

        if("repair".equals(type)){
            String encodeUrlQueryString = "actionName=View&selectedid="
                    + uid
                    + "&queryIndex=12" ;
            encodeUrlQueryString = UrlParamUtil.encodeUrlQueryString(encodeUrlQueryString);
            url = contextPath + "/woMgr/workOrderList.xhtml?str=" + encodeUrlQueryString;
        }else if("maintenance".equals(type)){
            String encodeUrlQueryString = "actionName=View&orderId="
                    + uid
                    + "&query=100&queryTab=" + getQueryTabValue(assetId);
            encodeUrlQueryString = UrlParamUtil.encodeUrlQueryString(encodeUrlQueryString);
            url = contextPath + "/pm/pmOrderListAll.xhtml?str=" + encodeUrlQueryString;
        }else if("measuring".equals(type)){
            url = "actionName=View&selectedid="
                    + selected.getUid()
                    +"&matchType=1"+"&boUuid="+uid;
            url = "measuringResultList.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);
        }
        log.info("timeline_url:"+url);
        RequestContext.getCurrentInstance().addCallbackParam("url", url);

    }
}
