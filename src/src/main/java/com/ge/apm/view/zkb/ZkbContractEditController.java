package com.ge.apm.view.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetTagService;
import com.ge.apm.service.zkb.ZkbConstantUtil;
import com.ge.apm.service.zkb.ZkbContractService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : 503189362
 * @date : 2020/5/14
 * @description :
 */
@ManagedBean
@ViewScoped
public class ZkbContractEditController extends GenericCRUDUUIDController<ZkbContract> implements ServerEventInterface {
    private ZkbContractRepository dao;

    private ZkbContractCourse zkbContractCourseSelected;
    private ZkbContractAsset zkbContractAssetSelected;
    private String actionDetailName;
    private String actionAssetName;
    private String actionCatName;
    private ZkbContractService zkbContractService;
    private UserContextService userContextService;
    private AssetTagService assetTagService;

    private UserAccount currentUser;
    private String actionName;
    private String selectionTenantInfo;
    private AssetInfo queryAsset;

    private List<ZkbContractCourse> contractCourseList ;
    private List<ZkbContractAsset> zkbContractAssetList;
    private List<ZkbContractCat> zkbContractCatList;
    private String reason;
    private boolean operateFlag = false;
    private ZkbPartOrderUserConfigRepository zkbPartOrderUserConfigRepository;
    private ZkbContractCat zkbContractCatSelected;
    private List<I18nMessage> quotaDeductTypeList = new ArrayList<>();
    I18nMessageRepository i18nMessageRepository;
    private boolean fourInOneFlag = false;
    private ZkbContractCatRepository zkbContractCatRepository;
    private TenantInfo tenantInfo;

    private ZkbZoneRepository zkbZoneRepository;

    @Override
    protected GenericRepositoryUUID<ZkbContract> getDAO() {
        return dao;
    }

    @Override
    protected void init()  {
        super.init();

        dao = WebUtil.getBean(ZkbContractRepository.class);
        zkbContractService = WebUtil.getBean(ZkbContractService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        assetTagService = WebUtil.getBean(AssetTagService.class);
        i18nMessageRepository = WebUtil.getBean(I18nMessageRepository.class);
        zkbPartOrderUserConfigRepository = WebUtil.getBean(ZkbPartOrderUserConfigRepository.class);
        zkbContractCatRepository = WebUtil.getBean(ZkbContractCatRepository.class);
        zkbZoneRepository = WebUtil.getBean(ZkbZoneRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        String encodeStr = WebUtil.getRequestParameter("str");
        actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "action");
        if ("Create".equalsIgnoreCase(actionName)) {
            try {
                selectionTenantInfo = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectionTenantInfo");
                tenantInfo =getTenantInfo(selectionTenantInfo);
                prepareCreate();
            }catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }else {
            String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
            this.selected = dao.findById(selectedId);
            tenantInfo =getTenantInfo(selected.getTenantUID());
            contractCourseList = zkbContractService.queryCourseByContractId(this.selected.getId());
            this.selected.setZkbContractCourses(contractCourseList);
            zkbContractAssetList = zkbContractService.queryAssetByContractId(this.selected.getId());
            zkbContractCatList = zkbContractService.queryCatByContractId(this.selected.getId());
            prepareEdit();
        }
        queryQuota();
        targetAssets = new ArrayList();
        queryAsset = new AssetInfo();
        this.getQueryAssetsList();
        if(!userContextService.isZkbUser()){
            List<String> userConfigs = new ArrayList<>();
            userConfigs.add(ZkbPartOrderStep.PartOrderStep.ClosePending.toString());
            userConfigs.add(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString());
         /*   userConfigs.add(ZkbPartOrderStep.PartOrderStep.OrderPending.toString());
            userConfigs.add(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString());*/
            List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByLoginNameAndConfigTypeIsIn(currentUser.getLoginName(),userConfigs);
            if(zkbPartOrderUserConfigs !=null && zkbPartOrderUserConfigs.size() > 0){
                operateFlag = true;
            }
        }
    }

    public Boolean qtyRender(String type){
        //if(userContextService.isZkbUser()) {
            if(ZkbContract.ContractType.DI.toString().equals(selected.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(selected.getContractType())){
                if(ZkbContractQuotaLog.OrderTypeEnum.Spare.toString().equals(type) ||
                    ZkbContractQuotaLog.OrderTypeEnum.CssbMaintenance.toString().equals(type) ||
                    ZkbContractQuotaLog.OrderTypeEnum.Artificial.toString().equals(type)
                ){
                    return Boolean.FALSE;
                }
            }else if(ZkbContract.ContractType.Biomed.toString().equals(selected.getContractType()) || ZkbContract.ContractType.CCS.toString().equals(selected.getContractType())){
                if(ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString().equals(type) ||
                    ZkbContractQuotaLog.OrderTypeEnum.PartsHigh.toString().equals(type)  ||
                    ZkbContractQuotaLog.OrderTypeEnum.CssbMaintenance.toString().equals(type)){
                    return Boolean.FALSE;
                }
            }else if(ZkbContract.ContractType.PCB.toString().equals(selected.getContractType())){
                if(ZkbContractQuotaLog.OrderTypeEnum.Spare.toString().equals(type) ||
                    ZkbContractQuotaLog.OrderTypeEnum.Artificial.toString().equals(type) ||
                    ZkbContractQuotaLog.OrderTypeEnum.CssbMaintenance.toString().equals(type)){
                    return Boolean.FALSE;
                }
            }else if("PCB MVS".equals(selected.getContractType())){
                if(ZkbContractQuotaLog.OrderTypeEnum.Spare.toString().equals(type) ||
                    ZkbContractQuotaLog.OrderTypeEnum.Artificial.toString().equals(type) ||
                    ZkbContractQuotaLog.OrderTypeEnum.CssbMaintenance.toString().equals(type) ||
                    ZkbContractQuotaLog.OrderTypeEnum.PartsHigh.toString().equals(type)){
                    return Boolean.FALSE;
                }
            }else if(ZkbContract.ContractType.CCSB.toString().equals(selected.getContractType())){
                if(ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString().equals(type) ||
                        ZkbContractQuotaLog.OrderTypeEnum.PartsHigh.toString().equals(type)){
                    return Boolean.FALSE;
                }
            }
        //}
        return Boolean.TRUE;
    }


    public TenantInfo getTenantInfo(String tenantInfoUID){
        return zkbContractService.getTenantInfo(tenantInfoUID);
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        this.zkbContractCourseSelected = null;
        this.selected.setTenantUID(this.selectionTenantInfo);
        this.selected.setCreatedBy(this.currentUser.getLoginName());
        this.selected.setCreatedDate(new Date());
        this.selected.setIbNumber(0);
        this.selected.setSpareQuota(0);
        this.selected.setGeArtificialSupportTimes(0);
        this.selected.setProbeQuota(0);
        this.selected.setMaintenanceQuota(0);
        this.selected.setCanApplyToolbox(ZkbConstantUtil.NORMAL_Y);
        this.initCourseList();
        contractCourseList = new ArrayList<>();
        zkbContractAssetList = new ArrayList<>();
    }


    public void fourInOneFlagChange(){
        if(fourInOneFlag){
            zkbContractCatSelected.setSnNo(ZkbConstantUtil.FOUR_TO_ONE);
        }else{
            zkbContractCatSelected.setSnNo("");
        }
    }

    public void queryQuota(){
        if(StringUtils.isEmpty(selected.getContractType())){
            quotaDeductTypeList = i18nMessageRepository.getByMsgType("zkbContractQuotaDeductType");
        }else if(ZkbContract.ContractType.Biomed.toString().equals(selected.getContractType()) || ZkbContract.ContractType.CCS.toString().equals(selected.getContractType())){
            quotaDeductTypeList = i18nMessageRepository.getByMsgType("zkbContractQuotaDeductType");
            for(I18nMessage i : quotaDeductTypeList){
                if(i.getMsgKey().equals(ZkbContract.QuotaDeductTypeEnum.Null.toString())){
                    quotaDeductTypeList.remove(i);
                    break;
                }
            }
        }else{
            quotaDeductTypeList = i18nMessageRepository.getByMsgType("zkbContractQuotaDeductType");
        }
    }

    public void contractTypeSelect(){
        queryQuota();
        if ("Create".equalsIgnoreCase(actionName)) {
            if(ZkbContract.ContractType.DI.toString().equals(selected.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(selected.getContractType()) || ZkbContract.ContractType.PCB.toString().equals(selected.getContractType())){
                selected.setQuotaDeductType(ZkbContract.QuotaDeductTypeEnum.Null.toString());
            }
        }else {
            if(ZkbContract.ContractType.DI.toString().equals(selected.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(selected.getContractType()) || ZkbContract.ContractType.PCB.toString().equals(selected.getContractType())){
                if(StringUtils.isEmpty(selected.getQuotaDeductType())){
                    selected.setQuotaDeductType(ZkbContract.QuotaDeductTypeEnum.Null.toString());
                }
            }
        }
    }

    /**
     * 合同编辑前操作
     */
    @Override
    public void prepareEdit() {
        super.prepareEdit();
        this.initCourseList();
    }

    /**
     * 新增保存，主要是保存完后需要重进加载一下详情页面
     * 同时将状态变更为编辑，并且更新selectedid值
     */
    public String createContract(){
        HashMap<String, Object> r = zkbContractService.checkContract(selected,actionName);
        boolean flag = (boolean)r.get("flag");
        if(flag){
            selected = zkbContractService.saveContract(this.selected,actionName,reason,currentUser);
            this.reason="";
            actionName = "Edit";
            WebUtil.addSuccessMessage(WebUtil.getMessage("DataSavedOK"));
            return UrlEncryptController.encodeUrlParam("Edit?action=Edit&selectedid=".concat(selected.getId()));
        }else{
            String errorLog = (String) r.get("errorLog");
            WebUtil.addErrorMessage(errorLog);
            return "";
        }
    }

    /**
     * 编辑保存
     */
    public void saveContract(){
        HashMap<String, Object> r = zkbContractService.checkContract(selected,actionName);
        boolean flag = (boolean)r.get("flag");
        if(flag) {
            selected = zkbContractService.saveContract(this.selected, actionName, reason, currentUser);
            this.reason = "";
            WebUtil.addSuccessMessage(WebUtil.getMessage("DataSavedOK"));
        }else{
            String errorLog = (String) r.get("errorLog");
            WebUtil.addErrorMessage(errorLog);
            //WebUtil.addErrorMessage(WebUtil.getMessage("ErrorContractNoIsExist"));
        }
    }

    //region 课程
    private List<ZkbCourse> courseList;

    public void initCourseList(){
        courseList = zkbContractService.selectAllCourse();
    }

    public List<ZkbCourse> autoCompleteCourse(String query){
        List<ZkbCourse> res = courseList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getCname().contains(query)).collect(Collectors.toList());
        return res;
    }


    public void prepareDetailCreate(){
        actionDetailName="Create";
        zkbContractCourseSelected = new ZkbContractCourse();
        zkbContractCourseSelected.setCreatedDate(new Date());
        this.reason = "";
    }

    public void prepareDetailEdit(){
        actionDetailName="Edit";
        this.reason = "";
    }

    public void saveDetail(){
        boolean flag=true;
        for(ZkbContractCourse zkbContractCourse : contractCourseList){
            if(zkbContractCourseSelected.getZkbCourse().getId().equals(zkbContractCourse.getCourseId())){
                if("Create".equals(actionDetailName)){
                    flag=false;
                    break;
                }else if("Edit".equals(actionDetailName)){
                    if(!zkbContractCourse.getId().equals(this.zkbContractCourseSelected.getId())){
                        flag=false;
                        break;
                    }
                }
            }
        }
        if (!flag){
            WebUtil.addErrorMessage("课程已存在");
            contractCourseList = zkbContractService.queryCourseByContractId(this.selected.getId());
            return;
        }

        double courseCountTotal = zkbContractCourseSelected.getMainCourseCountTotal() == null ? 0 : zkbContractCourseSelected.getMainCourseCountTotal();
        double courseCount = zkbContractCourseSelected.getMainCourseCount() == null ? 0 : zkbContractCourseSelected.getMainCourseCount();
        double courseCountApply = zkbContractCourseSelected.getMainCourseCountApply() == null ? 0 : zkbContractCourseSelected.getMainCourseCountApply();
        double courseCountUse = zkbContractCourseSelected.getMainCourseCountUse() == null ? 0 : zkbContractCourseSelected.getMainCourseCountUse();
        if(courseCountTotal != (courseCount + courseCountApply + courseCountUse)){
            contractCourseList = zkbContractService.queryCourseByContractId(this.selected.getId());
            WebUtil.addErrorMessage("课程总额不等于课程剩余名额、课程申请名额、课程已使用名额之和,");
            return;
        }
        if("Create".equals(actionDetailName)){
            zkbContractCourseSelected.setContractId(this.selected.getId());
        }
        zkbContractCourseSelected.setCourseId(zkbContractCourseSelected.getZkbCourse().getId());
        contractCourseList = zkbContractService.saveContractCourse(zkbContractCourseSelected,contractCourseList,this.selected,actionDetailName,reason,currentUser);
        this.zkbContractCourseSelected =null;
    }

    public void deleteDetail(){
        contractCourseList = zkbContractService.delContractCourse(zkbContractCourseSelected,contractCourseList,this.selected,currentUser,reason);
        this.zkbContractCourseSelected =null;
    }
    //endregion

    //region 设备
    public String getRepairForm(String value){
        if(StringUtils.isEmpty(value)){
            return "";
        }
        String v = WebUtil.getFieldValueMessage("repairForm",value);
        return v;
    }

    private String assetName;
    private List<AssetInfo> sourceAssets;
    private List<AssetInfo> targetAssets;
    private DualListModel<AssetInfo> availableAssets ;

    public void prepareAssetCreate(){
        targetAssets = new ArrayList();
        queryAsset = new AssetInfo();
        queryAsset.setTenantUID(this.selected.getTenantUID());
        availableAssets = new DualListModel<>();
        this.getQueryAssetsList();
    }

    public void prepareAssetEdit(){
        this.actionAssetName="Edit";
        this.reason = "";
    }

    public void saveAssetEdit(){
        zkbContractService.updateAsset(selected,zkbContractAssetSelected,reason,currentUser);
        zkbContractAssetList = zkbContractService.queryAssetByContractId(this.selected.getId());
        this.zkbContractAssetSelected = null;
        this.reason= "";
    }

   public void deleteAsset(){
        zkbContractService.deleteAsset(this.zkbContractAssetSelected,this.selected,currentUser,reason);
        this.zkbContractAssetList.remove(this.zkbContractAssetSelected);
        this.zkbContractAssetSelected = null;

       this.selected.setLastModifiedBy(currentUser.getLoginName());
       this.selected.setLastModifiedDate(new Date());
       this.selected.setZkbContractAssets(null);
       dao.save(this.selected);
   }

    public void getQueryAssetsList() {
        if(this.assetName != null){
            queryAsset.setName(this.assetName);
        }else{
            queryAsset.setName(null);
        }
        if (StringUtils.isNotEmpty(queryAsset.getTenantUID()) && (queryAsset.getAssetGroup() != null || queryAsset.getName() != null || queryAsset.getQrCode() != null || queryAsset.getFunctionType() != null)) {
            //if (StringUtils.isNotEmpty(queryAsset.getTenantUID())){
            sourceAssets = assetTagService.getAssetList(queryAsset);

            sourceAssets = new ArrayList(sourceAssets);
            sourceAssets.removeAll(targetAssets);
        } else {
            sourceAssets = new ArrayList<AssetInfo>();
            targetAssets = new ArrayList<AssetInfo>();
        }
        availableAssets = new DualListModel<>(sourceAssets, targetAssets);
    }

    public void onAssetsTransfer(TransferEvent event) {

        for (Object item : event.getItems()) {
            if (event.isAdd()) {
                targetAssets.add((AssetInfo) item);
            } else {
                targetAssets.remove((AssetInfo) item);
            }
        }
    }

    public List<ZkbZone> queryZoneByRegion(){
        return zkbZoneRepository.findByRegion(selected.getRegion());
    }

    public void saveAsset(){
        if(targetAssets.size() > 0){
            zkbContractService.saveAsset(targetAssets,this.selected,currentUser,zkbContractAssetList);
            zkbContractAssetList = zkbContractService.queryAssetByContractId(this.selected.getId());
        }
    }
    //endregion

    //region 探头
    public void prepareCatCreate(){
        actionCatName="Create";
        zkbContractCatSelected = new ZkbContractCat();
        this.reason = "";
        this.fourInOneFlag = false;
    }

    public void prepareCatEdit(){
        actionCatName="Edit";
        this.reason = "";
        zkbContractCatSelected = zkbContractCatRepository.findById(zkbContractCatSelected.getId());
        if(ZkbConstantUtil.FOUR_TO_ONE.equals(zkbContractCatSelected.getSnNo())){
            this.fourInOneFlag = true;
        }else{
            this.fourInOneFlag = false;
        }
    }

    public void saveCat(){
        boolean flag=true;
        /*for(ZkbContractCat c : zkbContractCatList){
            if(zkbContractCatSelected.getSnNo().equals(c.getSnNo())){
                if("Create".equals(actionCatName)){
                    flag=false;
                    break;
                }else if("Edit".equals(actionCatName)){
                    if(!zkbContractCatSelected.getId().equals(c.getId())){
                        flag=false;
                        break;
                    }
                }
            }
        }*/
        if (!flag){
            WebUtil.addErrorMessage("sn编号已存在");
            contractCourseList = zkbContractService.queryCourseByContractId(this.selected.getId());
            return;
        }

        if("Create".equals(actionCatName)) {
            zkbContractCatSelected.setContractId(this.selected.getId());
        }
        zkbContractService.saveCat(zkbContractCatSelected,actionCatName,reason,currentUser,selected);
        zkbContractCatList = zkbContractService.queryCatByContractId(selected.getId());
        fourInOneFlag = false;
        this.zkbContractCatSelected = null;
    }


    public void deleteCat(){
        zkbContractService.deleteCat(zkbContractCatSelected,selected,currentUser,reason);
        zkbContractCatList = zkbContractService.queryCatByContractId(selected.getId());
    }
    //endregion

    private String logType;
    public void preForLog(String _logType){
        this.logType = _logType;
        this.reason="";
    }

    public void operateByLogType(){
        if(StringUtils.isEmpty(logType)){
            return;
        }
        ZkbContractLog.LogType ly =ZkbContractLog.LogType.valueOf(logType);
        switch (ly) {
            case contractUpdate:
                saveContract();
                break;
            case courseUpdate:
                //System.out.println("None");
                break;
            case CourseDelete:
                deleteDetail();
                break;
            case assetDelete:
                deleteAsset();
                break;
            case catUpdate:
                break;
            case catDelete:
                deleteCat();
                break;
        }
        this.reason="";
    }

    public boolean isAssetDeleteCanOperate(){
        if(zkbContractAssetSelected == null || StringUtils.isEmpty(zkbContractAssetSelected.getId()) || userContextService.isZkbUser()){
            return true;
        }else{
            return false;
        }
    }

    public boolean isCourseCanOperate(){
        if(zkbContractCourseSelected == null || zkbContractCourseSelected.getZkbCourse() ==null || userContextService.isZkbUser()){
            return true;
        }else{
            return false;
        }
    }

    public boolean isCatCanOperate(){
        if(zkbContractCatSelected == null || zkbContractCatSelected.getId() ==null || userContextService.isZkbUser()){
            return true;
        }else{
            return false;
        }
    }

    public boolean isCreateHide(){
        if("Create".equals(actionName)){
            return operateFlag;
        }else {
            return false;
        }
    }

    public boolean isEditHide(){
        if("Edit".equals(actionName)){
            return operateFlag;
        }else{
            return false;
        }
    }

    public void resetZeroScore(){
        zkbContractService.resetZeroScore(selected, currentUser);
    }

    public String getActionAssetName() {
        return actionAssetName;
    }

    public void setActionAssetName(String actionAssetName) {
        this.actionAssetName = actionAssetName;
    }

    public DualListModel<AssetInfo> getAvailableAssets() {
        return availableAssets;
    }

    public void setAvailableAssets(DualListModel<AssetInfo> availableAssets) {
        this.availableAssets = availableAssets;
    }

    public List<AssetInfo> getSourceAssets() {
        return sourceAssets;
    }

    public void setSourceAssets(List<AssetInfo> sourceAssets) {
        this.sourceAssets = sourceAssets;
    }

    public List<AssetInfo> getTargetAssets() {
        return targetAssets;
    }

    public void setTargetAssets(List<AssetInfo> targetAssets) {
        this.targetAssets = targetAssets;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getActionDetailName() {
        return actionDetailName;
    }

    public void setActionDetailName(String actionDetailName) {
        this.actionDetailName = actionDetailName;
    }

    public ZkbContractCourse getZkbContractCourseSelected() {
        return zkbContractCourseSelected;
    }

    public void setZkbContractCourseSelected(ZkbContractCourse zkbContractCourseSelected) {
        this.zkbContractCourseSelected = zkbContractCourseSelected;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getSelectionTenantInfo() {
        return selectionTenantInfo;
    }

    public void setSelectionTenantInfo(String selectionTenantInfo) {
        this.selectionTenantInfo = selectionTenantInfo;
    }

    public List<ZkbCourse> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<ZkbCourse> courseList) {
        this.courseList = courseList;
    }

    public List<ZkbContractCourse> getContractCourseList() {
        return contractCourseList;
    }

    public void setContractCourseList(List<ZkbContractCourse> contractCourseList) {
        this.contractCourseList = contractCourseList;
    }

    public List<ZkbContractAsset> getZkbContractAssetList() {
        return zkbContractAssetList;
    }

    public void setZkbContractAssetList(List<ZkbContractAsset> zkbContractAssetList) {
        this.zkbContractAssetList = zkbContractAssetList;
    }

    public AssetInfo getQueryAsset() {
        return queryAsset;
    }

    public void setQueryAsset(AssetInfo queryAsset) {
        this.queryAsset = queryAsset;
    }

    public ZkbContractAsset getZkbContractAssetSelected() {
        return zkbContractAssetSelected;
    }

    public void setZkbContractAssetSelected(ZkbContractAsset zkbContractAssetSelected) {
        this.zkbContractAssetSelected = zkbContractAssetSelected;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isOperateFlag() {
        return operateFlag;
    }

    public void setOperateFlag(boolean operateFlag) {
        this.operateFlag = operateFlag;
    }

    public ZkbContractCat getZkbContractCatSelected() {
        return zkbContractCatSelected;
    }

    public void setZkbContractCatSelected(ZkbContractCat zkbContractCatSelected) {
        this.zkbContractCatSelected = zkbContractCatSelected;
    }

    public List<ZkbContractCat> getZkbContractCatList() {
        return zkbContractCatList;
    }

    public void setZkbContractCatList(List<ZkbContractCat> zkbContractCatList) {
        this.zkbContractCatList = zkbContractCatList;
    }


    public String getActionCatName() {
        return actionCatName;
    }

    public void setActionCatName(String actionCatName) {
        this.actionCatName = actionCatName;
    }

    public List<I18nMessage> getQuotaDeductTypeList() {
        return quotaDeductTypeList;
    }

    public void setQuotaDeductTypeList(List<I18nMessage> quotaDeductTypeList) {
        this.quotaDeductTypeList = quotaDeductTypeList;
    }

    public boolean isFourInOneFlag() {
        return fourInOneFlag;
    }

    public void setFourInOneFlag(boolean fourInOneFlag) {
        this.fourInOneFlag = fourInOneFlag;
    }

    public TenantInfo getTenantInfo() {
        return tenantInfo;
    }

    public void setTenantInfo(TenantInfo tenantInfo) {
        this.tenantInfo = tenantInfo;
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {

    }
}
