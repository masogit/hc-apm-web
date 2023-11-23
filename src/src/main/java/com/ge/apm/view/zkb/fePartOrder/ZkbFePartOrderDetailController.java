package com.ge.apm.view.zkb.fePartOrder;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.ZkbContractRepository;
import com.ge.apm.dao.ZkbFePartOrderRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.zkb.CopyByInvok;
import com.ge.apm.service.zkb.ZkbContractAssetService;
import com.ge.apm.service.zkb.ZkbFePartOrderService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.util.DateUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import java.util.*;

/**
 * @author 503202755 2020/9/23
 */
@ManagedBean
@ViewScoped
public class ZkbFePartOrderDetailController extends GenericCRUDUUIDController<ZkbFePartOrder> {

    /**
     * 智康宝liucheng service
     */
    private ZkbFePartOrderService zkbFePartOrderService;

    /**
     * 智康宝人工申请 reporsitory
     */
    private ZkbFePartOrderRepository zkbFePartOrderRepository;

    /**
     * 合同信息
     */
    private ZkbContractRepository zkbContractRepository;

    /**
     * 设备信息
     */
    private AssetInfoRepository assetInfoRepository;


    /**
     * 合同列表
     */
    private List<ZkbContract> contractList;

    /**
     * 设备列表
     */
    private List<AssetInfo> zkbAssetInfoList;

    /**
     *  当前设备
     */
    private AssetInfo zkbAssetInfo;


    /**
     * 当前登录用户信息service
     */
    private UserAccount currentUser;

    /**
     * 当前用户称
     */
    private String userName;

    /**
     * 当前用户UID
     */
    private String userUid;

    /**
     * 当前用户tenantUid
     */
    private String tenantUID = "";

    /**
     * 当前用户登录名
     */
    private String loginName = "";

    /**
     * 默认合同错误
     */
    private Boolean contractError = false;

    /**
     * 是否扣除名额是否可用pack
     * YHAAO-16438  ZKB-GEFE人工申请中，外修的超声低端机保养申请选否时，是否扣除名额应默认为否且不可编辑
     */
    private Boolean subtractTotalStatusDisabled = false;

    /**
     * 主键
     */
    private String id;

    /**
     * 查询状态
     */
    private String queryStatus;

    /**
     * 当前用户在config中的配置权限
     */
    private Map<String, String> configTypeMap = new HashMap<>();

    private Map<String, String> i18Map;

    private ZkbContractAssetService zkbContractAssetService;

    /**
     *  提示信息confirm message
     */
    private String confirmMessage;

    /**
     *  代理商确认本次扣除信息
     */
    private String thisDeductionMessage;

    private String encodeStr;

    private String notGeAssetBox;

    @Override
    protected void init() {
        super.init();
        zkbFePartOrderService = WebUtil.getBean(ZkbFePartOrderService.class);
        zkbFePartOrderRepository = WebUtil.getBean(ZkbFePartOrderRepository.class);
        zkbContractRepository = WebUtil.getBean(ZkbContractRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        this.loginName = currentUser.getLoginName();
        this.userName = currentUser.getName();
        this.userUid = currentUser.getUid();
        this.tenantUID = currentUser.getTenantUID();
        encodeStr = WebUtil.getRequestParameter("str");
        String action = (String) UrlEncryptController.getValueFromMap(encodeStr, "action");
        this.id = (String) UrlEncryptController.getValueFromMap(encodeStr, "id");
        this.queryStatus = (String) UrlEncryptController.getValueFromMap(encodeStr, "queryStatus");
        this.notGeAssetBox = (String) UrlEncryptController.getValueFromMap(encodeStr, "notGeAssetBox");
        try {
            if (ZkbFePartOrderController.CREATE_ACTION.equals(action)) {
                this.prepareCreate();
                this.selected.setNotGeAsset(WebUtil.getRequestParameter("notGeAsset"));
                //如果是人工申请，是否保养默认为否，同时页面该字段隐藏
                if(ZkbFePartOrder.OrderType.ManMade.toString().equals(selected.getOrderType())){
                    this.selected.setMaintainFlag(CopyByInvok.F);
                }
            } else if (ZkbFePartOrderController.EDIT_ACTION.equals(action)) {
                this.prepareEdit();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        configTypeMap = this.zkbFePartOrderService.getMapConfigByLoginName(this.loginName);
        this.i18Map = this.zkbFePartOrderService.getI18MessageMap(ZkbFePartOrderService.status);
        zkbContractAssetService = WebUtil.getBean(ZkbContractAssetService.class);
    }

    @Override
    protected GenericRepositoryUUID<ZkbFePartOrder> getDAO() {
        return this.zkbFePartOrderRepository;
    }

    public List<ZkbContract> getContractList() {
        return contractList;
    }

    public void setContractList(List<ZkbContract> contractList) {
        this.contractList = contractList;
    }

    public List<AssetInfo> getZkbAssetInfoList() {
        return zkbAssetInfoList;
    }

    public void setZkbAssetInfoList(List<AssetInfo> zkbAssetInfoList) {
        this.zkbAssetInfoList = zkbAssetInfoList;
    }

    /**
     * 添加新数据前，初始化
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        this.zkbFePartOrderService.initNewOrder(this.selected);
        this.selected.setReportUser(this.userName);
        this.selected.setOrderType(ZkbFePartOrder.OrderType.ManMade.toString());
        this.selected.setCurrentStep(ZkbFePartOrder.workFlowSteps.Requested.toString());
        this.zkbFePartOrderService.initTestStatus(this.loginName, this.selected);
    }




    public void notGeAssetChangeEditPage(ValueChangeEvent event){
        String url = "Edit.xhtml?str=" + encodeStr + "&notGeAsset=" + (event.getNewValue()==null?"":event.getNewValue().toString());
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }




    /**
     * 编辑数据前,初始化
     */
    @Override
    public void prepareEdit() {
        super.prepareEdit();
        this.selected = this.zkbFePartOrderRepository.findById(this.id);
        this.contractList = this.zkbFePartOrderService.loadZkbContractInfo(this.selected.getContractId());
        this.zkbAssetInfoList = this.zkbFePartOrderService.initAssetInfo(this.selected);
        this.zkbAssetInfo = (this.zkbAssetInfoList!=null&&this.zkbAssetInfoList.size()>0)?this.zkbAssetInfoList.get(0):new AssetInfo();
        this.zkbAssetInfoList.stream().forEach(e -> e.setAssetGroupName(WebUtil.getFieldValueMessage("assetGroup",  e.getAssetGroup()!=null? e.getAssetGroup().toString():"")));
        this.selected.setNotGeAsset("t".equals(this.selected.getNotGeAsset())?this.selected.getNotGeAsset():"f");
        this.selected.setManufacture(this.zkbAssetInfo.getManufacture());
        this.confirmMessage =this.genConfirmMessage();
    }


    public Boolean fourHundredStatus() {
        Boolean fourHundredStatus = true;
        if(ZkbFePartOrder.workFlowStatus.FourHundredsReject.toString().equals(this.selected.getStatus())){
            fourHundredStatus = false;
        }
        return fourHundredStatus;
    }
    /**
     * @autor by lyj 503277717
     * @date 2021/12/21
     * KB - ZKB FE 申请与GE FE审批需要弹窗显示剩余名额
     */
    public String genConfirmMessage(){
        String confirmMessage = new String();
        ZkbContract zct = this.contractList.get(0);
        ZkbFePartOrder zfpo = this.selected;
        if("ManMade".equals(this.selected.getOrderType())){//人工  新逻辑扣减     //  旧逻辑扣联合名额
            if("false".equals(this.selected.getSubtractTotalStatus())){
                confirmMessage += "本次申请不扣除名额！";
                thisDeductionMessage = "本次申请不扣除名额！";
            }else{
                if ("DI".equals(zct.getContractType())
                        || "PCB".equals(zct.getContractType())
                        || "PCB MVS".equals(zct.getContractType())) {
                    confirmMessage += "本次扣除1个备件+人工+保养名额；";
                    thisDeductionMessage = "本次扣除1个备件+人工+保养名额。";
                    String spareArtificail = zct.getSpareArtificail()!= null?zct.getSpareArtificail().toString():"0";
                    confirmMessage += "备件+人工+保养剩余名额为"+spareArtificail+"。";
                } else if ("CCS".equals(zct.getContractType())
                        || "Biomed".equals(zct.getContractType())) {
                    confirmMessage += "本次扣除1个人工支持名额；";
                    thisDeductionMessage = "本次扣除1个人工支持名额。";
                    String geArtificialSupportTimes = zct.getGeArtificialSupportTimes()!= null?zct.getGeArtificialSupportTimes().toString():"0";
                    confirmMessage += "人工支持剩余名额为"+geArtificialSupportTimes+"。";
                }
            }
        }
        return confirmMessage.toString();
    }

    /**
     * 选择设备
     */
    /*public void selectedWorkFlow(){
        this.contractList=this.zkbFePartOrderService.loadZkbContractInfo(this.selectZkbFePartOrder.getContractId());
        this.selected = this.zkbFePartOrderService.selectedWorkFlow(this.selected,this.selectZkbFePartOrder);
        ZkbContract zkbContract =  contractList.get(0);
        AssetInfo assetInfo = assetInfoRepository.getBySystemId(this.selected.getSystemId());
        Boolean fiveNineNine = this.zkbContractAssetService.checkFiveNineNineContract(zkbContract,selected.getReportDate(),selected.getArtificialType(),selected.getOrderType(),assetInfo.getUid());
        if(!fiveNineNine){
            this.selected.setFatherPartOrderNo(null);
        }
    }*/



    /**
     * 选择设备
     */
    public void selectedWorkFlow() {
        this.contractError = false;
        List<ZkbFePartOrder> list = this.zkbFePartOrderService.fullInfoBySystemIdBuildData(this.selected, this.zkbAssetInfo);
        if (list.size()>1) {
            WebUtil.addErrorMessage("当前设备存在多条有效期内的合同！");
            this.contractList = null;
            this.contractError = true;
            return;
        }
        this.contractList = this.zkbFePartOrderService.loadZkbContractInfoWithCheck(this.selected.getContractId(),this.selected.getSystemId());
        this.zkbAssetInfo = this.assetInfoRepository.getByUid(this.zkbAssetInfo.getUid());
        //未匹配到合同
        if (CollectionUtils.isEmpty(this.contractList)) {
            WebUtil.addErrorMessage("未查询出合同数据！","");
            this.contractError = true;
            return;
        }
        ZkbContract zkbContract =  this.contractList.stream().findFirst().get();
        if (ZkbContract.ContractType.CCSB.toString().equals(zkbContract.getContractType())&&ZkbFePartOrder.OrderType.ManMade.toString().equals(this.selected.getOrderType())) {
            WebUtil.addErrorMessage("合同类型为CCSB的设备只允许进行备件申请！","");
            this.contractError = true;
            return;
        }
        Boolean fiveNineNine = zkbContractAssetService.checkFiveNineNineContract(contractList.get(0),selected.getReportDate(),selected.getArtificialType(),selected.getOrderType(),null);
        if(!fiveNineNine){
            contractError = true;
        }
        if(2==zkbAssetInfo.getAssetGroup()//超声
                &&ZkbFePartOrderService.assetTypeLowMachineValue.equals(this.zkbFePartOrderService.getAssetType(this.contractList.get(0),this.selected))//低端机
                &&2 == zkbAssetInfo.getRepairForm()//外修
                && !"t".equals(this.selected.getMaintainFlag())){
            this.subtractTotalStatusDisabled = true;
        }
        if("t".equals(this.selected.getNotGeAsset())){
            if(!zkbFePartOrderService.checkMachineType(zkbContract,this.zkbAssetInfo)){
                WebUtil.addErrorMessage(WebUtil.getMessage("非GE设备只能申请高端机！"));
                return ;
            }
        }

    }

    /**
     * 根据systemId填充设备合同信息
     */
   /* public void fullInfoBySystemId(String systemId) {
        if (StringUtils.isBlank(systemId)) {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_systemIdError"));
            return;
        }
        this.zkbFePartOrderService.fullInfoBySystemId(this.selected, systemId);

        this.contractList = this.zkbFePartOrderService.loadZkbContractInfo(this.selected.getContractId());
        this.contractError = false;

//验证设备状态
        if (StringUtils.isNotBlank(zkbFePartOrderService.checkAsset(systemId))) {
            WebUtil.addErrorMessage(zkbFePartOrderService.checkAsset(systemId),"");
            this.contractList = null;
            this.contractError = true;
            return;
        }
        //未匹配到合同
        if (CollectionUtils.isEmpty(this.contractList)) {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_notFindContractError"));
            this.contractError = true;
            return;
        }
        Boolean fiveNineNine = zkbContractAssetService.checkFiveNineNineContract(contractList.get(0),selected.getReportDate(),selected.getArtificialType(),selected.getOrderType(),null);
        if(!fiveNineNine){
            contractError = true;
        }
    }*/

    /**
     * 根据systemId填充设备合同信息
     */
    public void fullInfoBySystemId(String systemId) {
        this.zkbAssetInfo = null;
        this.zkbAssetInfoList = null;
        this.selected.setSystemId(systemId);
        if (StringUtils.isBlank(this.selected.getNotGeAsset())) {
            WebUtil.addErrorMessage(WebUtil.getMessage("错误：请选择是否GE设备或者非GE设备"));
            return;
        }
        if (StringUtils.isBlank(this.selected.getMaintainFlag())) {
            WebUtil.addErrorMessage(WebUtil.getMessage("错误：请选择是否为保养申请"));
            return;
        }
        if (StringUtils.isBlank(systemId)) {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_systemIdError"));
            return;
        }
       this.zkbAssetInfoList = this.zkbFePartOrderService.fullInfoBySystemId(this.selected, systemId);
       this.zkbAssetInfoList.stream().forEach(e -> e.setAssetGroupName(WebUtil.getFieldValueMessage("assetGroup",  e.getAssetGroup()!=null? e.getAssetGroup().toString():"")));
       if (CollectionUtils.isEmpty(this.zkbAssetInfoList)) {
            WebUtil.addErrorMessage(WebUtil.getMessage("设备不存在!"));
            return;
       }
    }

    public String backList() {
        return this.zkbFePartOrderService.backList(this.queryStatus,this.notGeAssetBox);
    }

    public String saveAndToListPage() {
        if (StringUtils.isBlank(this.selected.getContractId())) {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_contractCannotBeNull"));
            return "";
        }
        //检查合同号和systemId是否一致
        /*Boolean consist = this.zkbFePartOrderService.checkSystemIdAndContract(this.selected);
        if (!consist) {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_contractSystemIdError"));
            return "";
        }*/
        ZkbContract zkbContract = this.zkbContractRepository.findById(this.selected.getContractId());
        if("t".equals(this.selected.getNotGeAsset())){
            if(!zkbFePartOrderService.checkContracts(this.selected.getTenantUid())){
                WebUtil.addErrorMessage(WebUtil.getMessage("该设备所在代理商没有biomed 和PCB MVS类型合同，不能进行非GE设备申请！"));
                return "";
            }
        }
        Boolean fiveNineNine = this.zkbContractAssetService.checkFiveNineNineContract(contractList.get(0),selected.getReportDate(),selected.getArtificialType(),selected.getOrderType(),null);
        if(!fiveNineNine){
            return "";
        }
        if ("true".equals(this.selected.getSubtractTotalStatus())) {//SubtractTotalStatus是否扣除名额字段
            if ("DI".equals(zkbContract.getContractType())
                    || "PCB".equals(zkbContract.getContractType())
                    || "PCB MVS".equals(zkbContract.getContractType())) {
                if (zkbContract.getSpareArtificail() == null || zkbContract.getSpareArtificail() <= 0) {
                    WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_contractSupportTimesCannotBeZero"));
                    return "";
                }
            } else if ("CCS".equals(zkbContract.getContractType())
                    || "Biomed".equals(zkbContract.getContractType())) {
                if (zkbContract.getGeArtificialSupportTimes() == null || zkbContract.getGeArtificialSupportTimes() <= 0) {
                    WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_contractSupportTimesCannotBeZero"));
                    return "";
                }
            }
            this.zkbFePartOrderService.moveRemianTimeOfContract(this.zkbAssetInfo,zkbContract, 1, this.selected, this.currentUser, "人工申请");
        }

        String url = this.zkbFePartOrderService.wolkFlowToComplete(this.selected, this.userUid, this.userName, this.tenantUID, this.queryStatus, ZkbFePartOrder.workFlowStatus.Requested.toString(),
                ZkbFePartOrder.workFlowSteps.WaitingAssignment.toString(), new Date(), null, GatewayURL.FE_PART_ORDER_SEND_MESSAGE,this.notGeAssetBox);
        this.zkbFePartOrderService.sendMessage(this.selected.getId(), GatewayURL.FE_PART_ORDER_SEND_WECHART);
        return url;
    }


    /**
     * 供应商同意
     *
     * @return
     */
    public String supplierAccept(String comment) {
        Date date = new Date();
        this.selected.setComment(comment);
        this.selected.setTenantAssignUser(this.userName);
        this.selected.setTenantAssignDate(new Date());
        //GE FE合同到期后400下单前增加Commercial角色进行审批
        ZkbContract zkbContract = zkbContractRepository.findById(this.selected.getContractId());
        String workFlowStep = ZkbFePartOrder.workFlowSteps.WaitingApm.toString();
        String format = "yyyy-MM-dd";
        String dateStr = DateUtil.getFormatStrByDate(this.selected.getReportDate(), format);
        Date reportDate = DateUtil.getDateByFormatStr(dateStr, format);
        //GE FE合同到期后400下单前增加Commercial角色进行审批  且不是599合同
        //人工叫报修时间  备件探头叫申请时间  申请时间如果小于开始时间，大于结束时间需要commercial
        if (((reportDate.compareTo(zkbContract.getStartDate()) < 0 || reportDate.compareTo(zkbContract.getEndDate()) > 0)&&!"Y".equals(zkbContract.getFiveNineNineFlag()))&&!"t".equals(this.selected.getNotGeAsset())){
            workFlowStep = ZkbFePartOrder.workFlowSteps.WaitingCommercial.toString();
        }
        String result = this.zkbFePartOrderService.wolkFlowToComplete(this.selected, this.userUid, this.userName, this.tenantUID, this.queryStatus, ZkbFePartOrder.workFlowStatus.TenantAccept.toString(),
                workFlowStep, date, null, GatewayURL.FE_PART_ORDER_SEND_EMAIL,this.notGeAssetBox);
        //注意：流程为WaitingCommercial审批时需要发微信、短信、邮件通知
        if(workFlowStep.equals(ZkbFePartOrder.workFlowSteps.WaitingCommercial.toString())&&!"Y".equals(zkbContract.getFiveNineNineFlag())){
            this.zkbFePartOrderService.sendMessage(this.selected.getId(), GatewayURL.FE_PART_ORDER_SEND_WECHART);
            this.zkbFePartOrderService.sendMessage(this.selected.getId(), GatewayURL.FE_PART_ORDER_SEND_MESSAGE);
        }
        return result;
    }

    public boolean checkContractDate() {
        return this.zkbFePartOrderService.checkContractDate(this.contractList);
    }

    /**
     * 供应商拒绝同意
     *
     * @return
     */
    public String supplierReject() {
        Date date = new Date();
        this.selected.setTenantAssignUser(this.userName);
        this.selected.setTenantAssignDate(new Date());
        ZkbContract zkbContract = this.zkbContractRepository.findById(this.selected.getContractId());
        this.zkbFePartOrderService.moveRemianTimeOfContract(this.zkbAssetInfo,zkbContract, 3, this.selected, this.currentUser, this.i18Map.get("TenantReject"));
        return this.zkbFePartOrderService.wolkFlowToComplete(this.selected, this.userUid, this.userName, this.tenantUID, this.queryStatus, ZkbFePartOrder.workFlowStatus.TenantReject.toString(),
                ZkbFePartOrder.workFlowSteps.Complete.toString(), date, date, GatewayURL.FE_PART_ORDER_SEND_EMAIL,this.notGeAssetBox);
    }

    /**
     * Commercial同意
     *
     * @return
     */
    public String commercialAccept(String comment) {
        Date date = new Date();
        this.selected.setCommercialComment(comment);
        this.selected.setCommercialAssignUser(this.userName);
        this.selected.setCommercialAssignDate(date);
        return this.zkbFePartOrderService.wolkFlowToComplete(this.selected, this.userUid, this.userName, this.tenantUID, this.queryStatus, ZkbFePartOrder.workFlowStatus.CommercialAccept.toString(),
                ZkbFePartOrder.workFlowSteps.WaitingApm.toString(), date, null, GatewayURL.FE_PART_ORDER_SEND_EMAIL,this.notGeAssetBox);
    }

    /**
     * Commercial拒绝
     *
     * @return
     */
    public String commercialReject() {
        Date date = new Date();
        this.selected.setCommercialAssignUser(this.userName);
        this.selected.setCommercialAssignDate(date);
        ZkbContract zkbContract = this.zkbContractRepository.findById(this.selected.getContractId());
        this.zkbFePartOrderService.moveRemianTimeOfContract(this.zkbAssetInfo,zkbContract, 3, this.selected, this.currentUser, this.i18Map.get("CommercialReject"));
        return this.zkbFePartOrderService.wolkFlowToComplete(this.selected, this.userUid, this.userName, this.tenantUID, this.queryStatus, ZkbFePartOrder.workFlowStatus.CommercialReject.toString(),
                ZkbFePartOrder.workFlowSteps.Complete.toString(), date, date, GatewayURL.FE_PART_ORDER_SEND_EMAIL,this.notGeAssetBox);
    }

    /**
     * 400团队同意
     *
     * @return
     */
    public String fullApmInfo() {
        this.selected.setFullApmInfoUser(this.userName);
        this.selected.setFullApmInfoDate(new Date());
        ZkbContract zkbContract = this.zkbContractRepository.findById(this.selected.getContractId());
        this.zkbFePartOrderService.moveRemianTimeOfContract(this.zkbAssetInfo,zkbContract, 2, this.selected, this.currentUser, this.i18Map.get("FourHundredsAccept"));
        return this.zkbFePartOrderService.wolkFlowToComplete(this.selected, this.userUid, this.userName, this.tenantUID, this.queryStatus, ZkbFePartOrder.workFlowStatus.FourHundredsAccept.toString(),
                ZkbFePartOrder.workFlowSteps.Complete.toString(), new Date(), new Date(), GatewayURL.FE_PART_ORDER_SEND_WECHART,this.notGeAssetBox);
    }

    /**
     * 400团队拒绝
     *
     * @return
     */
    public String rejectApmInfo() {
        this.selected.setFullApmInfoUser(this.userName);
        this.selected.setFullApmInfoDate(new Date());
        ZkbContract zkbContract = this.zkbContractRepository.findById(this.selected.getContractId());
        this.zkbFePartOrderService.moveRemianTimeOfContract(this.zkbAssetInfo,zkbContract, 4, this.selected, this.currentUser, this.i18Map.get("FourHundredsReject"));
        return this.zkbFePartOrderService.wolkFlowToComplete(this.selected, this.userUid, this.userName, this.tenantUID, this.queryStatus, ZkbFePartOrder.workFlowStatus.FourHundredsReject.toString(),
                ZkbFePartOrder.workFlowSteps.Complete.toString(), new Date(), new Date(), GatewayURL.FE_PART_ORDER_SEND_WECHART,this.notGeAssetBox);
    }

    /**
     * 判断当前用户在当前节点是否有编辑权限
     *
     * @return
     */
    public Boolean hasEditRight(String fieldsetType) {
        return this.zkbFePartOrderService.hasEditRight(fieldsetType, this.selected, this.configTypeMap, ZkbFePartOrder.manmadeStepAndRoles);
    }

    /**
     * 判断是否隐藏还是显示
     *
     * @param fieldsetType
     * @return
     */
    public Boolean checkRendered(String fieldsetType) {
        return this.zkbFePartOrderService.checkRendered(fieldsetType, this.selected, this.configTypeMap, ZkbFePartOrder.manmadeStepAndRoles);
    }

    public Boolean checkRendered() {
        if (StringUtils.isNotBlank(this.selected.getGeEngineerName()) && this.configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.GEAdmin.toString())) {
            return true;
        }
        return false;
    }

    public Boolean getContractError() {
        return contractError;
    }

    public void setContractError(Boolean contractError) {
        this.contractError = contractError;
    }

    public String getConfirmMessage() {
        return confirmMessage;
    }

    public void setConfirmMessage(String confirmMessage) {
        this.confirmMessage = confirmMessage;
    }

    public String getThisDeductionMessage() {
        return thisDeductionMessage;
    }

    public void setThisDeductionMessage(String thisDeductionMessage) {
        this.thisDeductionMessage = thisDeductionMessage;
    }

    public AssetInfo getZkbAssetInfo() {
        return zkbAssetInfo;
    }

    public void setZkbAssetInfo(AssetInfo zkbAssetInfo) {
        this.zkbAssetInfo = zkbAssetInfo;
    }

    public Boolean getSubtractTotalStatusDisabled() {
        subtractTotalStatusDisabled = false;
        if(zkbAssetInfo!=null&&this.selected!=null&&CollectionUtils.isNotEmpty(this.contractList)){
            if(2==zkbAssetInfo.getAssetGroup()//超声
                    &&ZkbFePartOrderService.assetTypeLowMachineValue.equals(this.zkbFePartOrderService.getAssetType(this.contractList.get(0),this.selected))//低端机
                    &&2 == zkbAssetInfo.getRepairForm()//外修
                    && !"t".equals(this.selected.getMaintainFlag())){
                this.subtractTotalStatusDisabled = true;
                this.selected.setSubtractTotalStatus("false");
            }
        }
        return subtractTotalStatusDisabled;
    }

    public void handSubtractStatus(ValueChangeEvent event){
        if("highMachine".equals(this.selected.getAssetType())&&"f".equals(this.selected.getNotGeAsset())){
            this.selected.setSubtractTotalStatus("false");
            if("t".equals(event.getNewValue())){
                this.selected.setSubtractTotalStatus("true");
            }
        }
    }

    public void setSubtractTotalStatusDisabled(Boolean subtractTotalStatusDisabled) {
        this.subtractTotalStatusDisabled = subtractTotalStatusDisabled;
    }

    public String getNotGeAssetBox() {
        return notGeAssetBox;
    }

    public void setNotGeAssetBox(String notGeAssetBox) {
        this.notGeAssetBox = notGeAssetBox;
    }
}
